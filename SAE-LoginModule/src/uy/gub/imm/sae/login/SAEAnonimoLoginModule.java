package uy.gub.imm.sae.login;

import java.security.Principal;
import java.security.acl.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

/**
 * Esta clase es utilizada para permitir la autenticación anónima de usuarios para la parte pública de la aplicación.
 * Básicamente, se trata de autenticar a un usuario "inventado" y temporal, de forma de poder registrarlo en una empresa
 * y así lograr que la aplicación pueda utilizar un EntityManager asociado al esquema de dicha empresa.
 * 
 * El código de usuario debe estar compuesto por un nombre y el identificador de la empresa separados por una barra.
 * Por ejemplo "usuario12345/3". El nombre debería ser único o al menos no tener probabilidad de colisionar con el nombre
 * de otro usuario. La contraseña debe ser el MD5 mismo código de usuario. No hay un problema de seguridad con esto ya
 * que lo único que se puede lograr es autenticar un usuario cuyo rol es "RA_AE_ANONIMO".
 * 
 * @author spio
 *
 */
public class SAEAnonimoLoginModule extends AbstractServerLoginModule {

	private String codigo = null;
	private Integer empresa = null;
	private String password = null;
	
	private Principal identity = null;
	
	public SAEAnonimoLoginModule() {
		
	}

	@Override
	public boolean login() throws LoginException {
		loginOk = false;
		if (callbackHandler == null) {
			throw new LoginException("No hay registrado un callback handler");
		}
		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("name:");
		callbacks[1] = new PasswordCallback("password:", false);
		try {
			callbackHandler.handle(callbacks);
		} catch (Exception e) {
			throw new LoginException("Error en la invocación al callback handler");
		}

		NameCallback nameCallback = (NameCallback) callbacks[0];
		PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];

		String username = nameCallback.getName();
		String partes[] = username.split("/", 2);
		
		//Separar el codigo de usuario en sus dos partes: usuario/empresa
		if(partes.length == 1) {
			codigo = username;
			empresa = null;
		}else {
			codigo = partes[0];
			empresa = Integer.valueOf(partes[1]);
		}
		
		password = new String(passwordCallback.getPassword());
		
		if(codigo==null || codigo.trim().isEmpty() || password==null || password.trim().isEmpty()) {
			loginOk = false;
		}else {
			//validar los datos
			String dsJndiName = (String) options.get("dsJndiName");
			Connection conn = null;
			try {
				String password0 = Utilidades.encriptarPassword(username); 
				String password1 = password;
				if(password0==null || password1==null || !password0.equals(password1)) {
					throw new FailedLoginException("Código de usuario o contraseña no válidos");
				}
				
				//La identidad se crea concatenando el codigo con un numero aleatorio 
				//Por si el mismo usuario se autentica desde dos navegadores diferentes
				String tenant = "default";
				if(empresa != null) {
					Context initContext = new InitialContext();
					DataSource ds = (DataSource)initContext.lookup(dsJndiName);
					conn = ds.getConnection();
					PreparedStatement st = conn.prepareStatement("select datasource from global.ae_empresas where id=?");
					st.setInt(1, empresa);
					st.executeQuery();
					ResultSet rs = st.getResultSet();
					if(rs.next()) {
						tenant = rs.getString(1);
					}
				}
				loginOk = true;
				identity = new SAEPrincipal(codigo, tenant, false);
			}catch(Exception ex) {
				loginOk = false;
			}finally {
				try {
					conn.close();
				}catch(Exception ex) {
					//
				}
			}
		}
		return loginOk;
	}

	@Override
	protected Principal getIdentity() {
		return identity;
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		//Es necesario crar un grupo llamado "Roles" donde se colocan todos los roles del usuario
		Group rolesGroup = new SimpleGroup("Roles");
		rolesGroup.addMember(new SimpleGroup("RA_AE_ANONIMO"));
		
		Group[] roles = new Group[1];
		roles[0] = rolesGroup;
		return roles;
	}

}
