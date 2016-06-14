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
			//throw new FailedLoginException("Código de usuario o contraseña no válidos");
			return false;
		}else {
			//validar los datos
			String dsJndiName = (String) options.get("dsJndiName");
			Connection conn = null;
			try {
				String password0 = Utilidades.encriptarPassword(username); 
				String password1 = password;
				if(password0==null || password1==null) {
					//throw new FailedLoginException("Código de usuario o contraseña no válidos");
					return false;
				}
				loginOk = password0.equals(password1);
				
				//La identidad se crea concatenando el codigo con un numero aleatorio 
				//Por si el mismo usuario se autentica desde dos navegadores diferentes
				//identity = createIdentity(codigo);
				//String codigoAumentado = codigo + "-" + ((new Date()).getTime());
				//identity = createIdentity(codigoAumentado);
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
				//identity = new SAEPrincipal(codigoAumentado, tenant, false);
				identity = new SAEPrincipal(codigo, tenant, false);
			}catch(Exception ex) {
				ex.printStackTrace();
				throw new FailedLoginException("No se pudo validar las credenciales: "+ex.getMessage());
			}finally {
				try {
					conn.close();
				}catch(Exception ex) {
					//
				}
			}
			
			return loginOk;
		}

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
