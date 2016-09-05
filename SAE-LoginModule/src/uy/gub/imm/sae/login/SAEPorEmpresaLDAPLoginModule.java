package uy.gub.imm.sae.login;

import java.security.Principal;
import java.security.acl.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

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

import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SimpleBindRequest;

/**
 * Esta clase es utilizada para permitir la autenticación normal de usuarios para la parte privada de la aplicación, 
 * tanto en el login inicial (cuando no hay una empresa seleccioanda) como cuando el usuario logueado cambia de empresa.
 * 
 * El código de usuario debe estar compuesto por un nombre y el identificador de la empresa separados por una barra. 
 * Por ejemplo "usuario1/3". Si no se incluye el identificador de la empresa, y el usuario no es superadministrador,
 * no se cargará ningún rol. Si es superadministrador se cargan todos los roles incluso si no se especifica la empresa. 
 * Si no es superadministrador pero se especifica una empresa se cargan los roles del usuario en la empresa indicada. La 
 * contraseña debe corresponder con la que está almacenada en la base de datos (por ahora con Base64(MD5(x))).
 * 
 * Nota: este módulo puede ser deshabilitado si se decide eliminar la autenticación local y solo usar CDA.
 * 
 * @author spio
 *
 */
public class SAEPorEmpresaLDAPLoginModule extends AbstractServerLoginModule {

	private String codigo = null;
	private Integer empresa = null;
	private String password = null;
	private boolean superadmin = false;
	
	private Principal identity = null;
	
	public SAEPorEmpresaLDAPLoginModule() {
		
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
			String ldapHost = (String) options.get("ldapHost"); //Host del servidor LDAP
			String ldapPort = (String) options.get("ldapPort"); //Puerto del servidor LDAP (389)
			String ldapUser = (String) options.get("ldapUser"); //Usuario para conectarse
			String ldapPass = (String) options.get("ldapPass"); //Contraseña del usuario anterior
		  String ldapBase = (String) options.get("ldapBase"); //A partir de donde buscar usuarios
		  String ldapAttr = (String) options.get("ldapAttr"); //Debería ser cn o uid

		  LDAPConnection ldapConn = null;
		  Connection conn = null;
		  try {
			  
		  	//Intentar abrir la conexión con el servidor LDAP
				try {
					ldapConn = new LDAPConnection(ldapHost, Integer.valueOf(ldapPort).intValue(), ldapUser, ldapPass);
				} catch (LDAPException e) {
					throw new FailedLoginException("No se pudo establecer una conexión con el servidor LDAP.");
				}		
			  //Hacer la búsqueda del usuario
				List<SearchResultEntry> entries = null;
				try {
					//SearchResult searchResults = ldapConn.search(ldapBase, SearchScope.SUB, "(uid=" + codigo + ")", "*");
					SearchResult searchResults = ldapConn.search(ldapBase, SearchScope.SUB, "("+ldapAttr+"=" + codigo + ")", "*");
					entries = searchResults.getSearchEntries();
					if(entries==null || entries.isEmpty()) {
						throw new FailedLoginException("Código de usuario o contraseña no válidos");
					}
				} catch (LDAPException e) {
					throw new FailedLoginException("Error ejecutando consulta contra el servidor LDAP: "+e.getMessage());
				}	
				//Validar la contraseña del usuario
				try {
					Entry entry = entries.get(0);
					String dn = entry.getDN().toString();
			    BindRequest bindRequest = new SimpleBindRequest(dn, password);
			    BindResult bindResult = ldapConn.bind(bindRequest);
			    ResultCode bindResultCode = bindResult.getResultCode();
			    if(!bindResultCode.equals(ResultCode.SUCCESS)) {
			    	throw new FailedLoginException("Código de usuario o contraseña no válidos");
			    }
				} catch (LDAPException e) {
					throw new FailedLoginException("Error ejecutando consulta contra el servidor LDAP: "+e.getMessage());
				}
		    //Determinar si es superadmin
				Context initContext = new InitialContext();
				String dsJndiName = (String) options.get("dsJndiName");
				DataSource ds = (DataSource)initContext.lookup(dsJndiName);
				conn = ds.getConnection();
				String sql = "select superadmin from global.ae_usuarios where codigo=? and fecha_baja is null";
				PreparedStatement st = conn.prepareStatement(sql);
				st.setString(1, codigo);
				st.executeQuery();
				ResultSet rs = st.getResultSet();
				if(!rs.next()) {
					throw new FailedLoginException("No existe el usuario en la base de datos");
				}
				try {
					superadmin = rs.getBoolean(1);
				}catch(Exception ex) {
					superadmin = false;
				}
				//Determinar el esquema en el cual va a trabajar
				String tenant = "default";
				if(empresa != null) {
					st = conn.prepareStatement("select datasource from global.ae_empresas where id=?");
					st.setInt(1, empresa);
					st.executeQuery();
					rs = st.getResultSet();
					if(rs.next()) {
						tenant = rs.getString(1);
					}
					rs.close();
					st.close();
				}
				loginOk = true;
				identity = new SAEPrincipal(codigo, tenant, superadmin);
			}catch(Exception ex) {
				loginOk = false;
			}finally {
				try {
					//Cerrar la conexión con el servidor LDAP
			    ldapConn.close();
				}catch(Exception ex) {
					//
				}
				try {
					//Cerrar la conexión con la base de datos
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
		
		//Si es administrador, tiene todos los roles
		//Si no es administrador pero hay empresa, se obtienen los roles del usuario en esa empresa
		//Si no es administrador y tampoco hay empresa, no hay mas roles
		if(superadmin) {
			rolesGroup.addMember(new SimpleGroup("RA_AE_ADMINISTRADOR"));
			rolesGroup.addMember(new SimpleGroup("RA_AE_PLANIFICADOR"));
			rolesGroup.addMember(new SimpleGroup("RA_AE_FCALL_CENTER"));
			rolesGroup.addMember(new SimpleGroup("RA_AE_LLAMADOR"));
		}else if(empresa != null) {
		
			//Obtener los roles para el usuario en la empresa dada
			String dsJndiName = (String) options.get("dsJndiName");
			Connection conn = null;
			try {
	
				//Si es superadministrador tiene todos los roles
					Context initContext = new InitialContext();
					DataSource ds = (DataSource)initContext.lookup(dsJndiName);
					conn = ds.getConnection();
					PreparedStatement st = conn.prepareStatement("select rol_nombre from global.ae_rel_usuarios_roles ur "
							+ "join global.ae_usuarios u on u.id=ur.usuario_id join global.ae_empresas e on e.id=ur.empresa_id "
							+ "where u.codigo=? and e.id=?");
					st.setString(1, codigo);
					st.setInt(2, empresa);
					ResultSet rs = st.executeQuery();
					while(rs.next()) {
						String rolNombre = rs.getString(1);
			  		rolesGroup.addMember(new SimpleGroup(rolNombre));
					}
				}catch(Exception ex) {
					throw new FailedLoginException("No se pudo obtener los roles: "+ex.getMessage());
				}finally {
					try {
						conn.close();
					}catch(Exception ex) {
						//
					}
				}		
			}
		
			Group[] roles = new Group[1];
			roles[0] = rolesGroup;
			return roles;
		}
	
	}
