/*
 * SAE - Sistema de Agenda Electronica
 * Copyright (C) 2009  IMM - Intendencia Municipal de Montevideo
 *
 * This file is part of SAE.

 * SAE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uy.gub.imm.sae.business.ejb.facade;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.internal.SessionImpl;
import org.picketbox.commons.cipher.Base64;

import uy.gub.imm.sae.business.dto.UsuarioEmpresaRoles;
import uy.gub.imm.sae.business.ejb.servicios.ServiciosTramitesBean;
import uy.gub.imm.sae.business.utilidades.MailUtiles;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Oficina;
import uy.gub.imm.sae.entity.global.Organismo;
import uy.gub.imm.sae.entity.global.Token;
import uy.gub.imm.sae.entity.global.Tramite;
import uy.gub.imm.sae.entity.global.UnidadEjecutora;
import uy.gub.imm.sae.entity.global.Usuario;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
public class UsuariosEmpresasBean implements UsuariosEmpresasLocal,  UsuariosEmpresasRemote{

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;
	
  @PersistenceContext(unitName = "SAE-EJB")
  private EntityManager entityManager;
	
  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionLocal")
	private Configuracion confBean;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendasBean!uy.gub.imm.sae.business.ejb.facade.AgendasLocal")
	private Agendas agendasEJB;
	
  @EJB
  private ServiciosTramitesBean tramitesBean;
	
	
	@Override
	public Empresa obtenerEmpresaPorId(Integer id) throws ApplicationException {
		if(id == null) {
			return null;
		}
		try{
			Empresa empresa = (Empresa)globalEntityManager.find(Empresa.class, id);
			return empresa;
		} catch(NoResultException nrEx) {
			return null;
		} catch(Exception ex) {
			throw new ApplicationException(ex);
		} 
	}
	
	@Override
	public Empresa obtenerEmpresaPorNombre(String nombre) throws ApplicationException {
		if(nombre == null) {
			return null;
		}
		try{
			Empresa empresa = (Empresa)globalEntityManager.createQuery("SELECT e from Empresa e WHERE e.fechaBaja IS NULL and e.nombre = :nombre")
				.setParameter("nombre", nombre)
				.getSingleResult();
			return empresa;
		} catch(NoResultException nrEx) {
			return null;
		} catch(Exception ex) {
			throw new ApplicationException(ex);
		}
	}
	
	@Override
	public Usuario obtenerUsuarioPorCodigo(String codigo) throws ApplicationException {
		if(codigo == null) {
			return null;
		}
		try{
			Usuario usuario = (Usuario)globalEntityManager.createQuery("SELECT u from Usuario u WHERE u.codigo = :codigo")
				.setParameter("codigo", codigo)
				.getSingleResult();
			return usuario;
		} catch(NoResultException nrEx) {
			return null;
		} catch(Exception ex) {
			throw new ApplicationException(ex);
		}
	}

	@Override
	public List<Empresa> consultarEmpresas() throws ApplicationException {
		try{

			//Connection connection = globalEntityManager.unwrap(Connection.class);  
			Connection connection = globalEntityManager.unwrap(SessionImpl.class).connection();
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet result = metaData.getSchemas();
			List<String> esquemas = new ArrayList<String>();
			while (result.next()) {
				esquemas.add(result.getString(1));
			}
			Query query = globalEntityManager.createQuery("SELECT e from Empresa e WHERE e.fechaBaja IS NULL ORDER BY e.nombre");
			@SuppressWarnings("unchecked")
			List<Empresa> empresas = (List<Empresa>) query.getResultList();
			//debo cargar las empresas que tienen correcto el esquema de base de datos
			List<Empresa> empresasCorrectas = new ArrayList<Empresa>();
			for (Empresa empresa : empresas) {
				if (esquemas.contains(empresa.getDatasource()))
				{
					empresasCorrectas.add(empresa);
				}
				
			}
			return empresasCorrectas;
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}
	
	@Override
	public List<Empresa> consultarTodasEmpresas() throws ApplicationException {
		try{
			Query query = globalEntityManager.createQuery("SELECT e from Empresa e WHERE e.fechaBaja IS NULL ORDER BY e.nombre");
			@SuppressWarnings("unchecked")
			List<Empresa> empresas = (List<Empresa>) query.getResultList();
			return empresas;
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}
	
	@Override
	public List<Empresa> consultarEmpresasPorUsuario(Usuario usu) throws ApplicationException {
		try{
			//Connection connection = globalEntityManager.unwrap(Connection.class);  
			Connection connection = globalEntityManager.unwrap(SessionImpl.class).connection();
			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet result = metaData.getSchemas();
			List<String> esquemas = new ArrayList<String>();
			while (result.next()) {
				esquemas.add(result.getString(1));
			}
			//debo cargar las empresas que tienen correcto el esquema de base de datos
			List<Empresa> empresasCorrectas = new ArrayList<Empresa>();
			for (Empresa empresa : usu.getEmpresas()) {
				if (esquemas.contains(empresa.getDatasource())) {
					empresasCorrectas.add(empresa);
				}
			}
			return empresasCorrectas;
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}

	
	
	@Override
	public Empresa guardarEmpresa(Empresa empresa) throws UserException {
		if(empresa == null) {
			return null;
		}
		//Verificar si ya está creado el esquema para la empresa (puede no contener tablas)
		Connection connection = globalEntityManager.unwrap(SessionImpl.class).connection();
		DatabaseMetaData metaData;
		boolean existeEsquema = false;
		try {
			metaData = connection.getMetaData();
			ResultSet result = metaData.getSchemas();
			while (result.next()) {
				if(empresa.getDatasource().equals(result.getString(1))) {
					existeEsquema = true;
				}
			}	
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if(!existeEsquema) {
			throw new UserException("no_se_puede_guardar_la_empresa_porque_no_existe_el_esquema");
		}
		if(empresa.getId() == null) {
			globalEntityManager.persist(empresa);
			globalEntityManager.flush();
			globalEntityManager.refresh(empresa);
		}else {
			empresa = globalEntityManager.merge(empresa);
		}
		return empresa;
	}
	
	public void eliminarEmpresa(Empresa empresa, TimeZone timezone, String codigoUsuario) throws ApplicationException, UserException {
		if(empresa==null || empresa.getId() == null) {
			return;
		}
		Connection connection = globalEntityManager.unwrap(SessionImpl.class).connection();
		DatabaseMetaData metaData;
		List<String> esquemas = null;
		try {
			metaData = connection.getMetaData();
			ResultSet result = metaData.getSchemas();
			esquemas = new ArrayList<String>();
			while (result.next()) {
				esquemas.add(result.getString(1));
			}	
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (esquemas.contains(empresa.getDatasource())) {
			List<Agenda> agendasEliminar = agendasEJB.consultarAgendas();
			for (Agenda agenda : agendasEliminar) {
				try {
					agendasEJB.eliminarAgenda(agenda, timezone, codigoUsuario);
				} catch (UserException e) {
					throw new UserException("no_se_puede_eliminar_la_empresa_porque_hay_reservas_vivas");
				}
			}
		}

		//Desasociar a todos los usuarios de esta empresa
		List<Usuario> usuarios = consultarUsuariosEmpresa(empresa.getId());
		for(Usuario usuario : usuarios) {
		  usuario.getEmpresas().remove(empresa);
		}
		
		//Marcar la empresa recibida como eliminada (por si el invocante la sigue usando)
		empresa.setFechaBaja(new Date());
		//Recuperar la empresa por si está detached
		empresa = globalEntityManager.find(Empresa.class, empresa.getId());
		//Eliminar la empresa
		empresa.setFechaBaja(new Date());
		globalEntityManager.persist(empresa);
		globalEntityManager.flush();
	}
	
	@Override
	public List<Usuario> consultarUsuarios() throws ApplicationException {
		try{
			Query query = globalEntityManager.createQuery("SELECT u from Usuario u WHERE u.fechaBaja IS NULL ORDER BY u.nombre");
			@SuppressWarnings("unchecked")
			List<Usuario> usuarios = (List<Usuario>) query.getResultList();
			return usuarios;
		} catch (Exception ex){
			throw new ApplicationException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> consultarUsuariosEmpresa(Integer empresaId) throws ApplicationException {
		try{
			Query query = globalEntityManager.createQuery("SELECT u from Usuario u, IN(u.empresas) e WHERE u.fechaBaja IS NULL "
					+ " AND e.id = :empresaId ORDER BY u.nombre");
			query.setParameter("empresaId", empresaId);
			List<Usuario> usuarios = (List<Usuario>) query.getResultList();
			return usuarios;
		} catch (Exception ex){
			throw new ApplicationException(ex);
		}
	}
	
	@Override
	public Usuario guardarUsuario(Usuario usuario) throws ApplicationException {
		if(usuario == null) {
			return null;
		}
		if(usuario.getId() == null) {
			globalEntityManager.persist(usuario);
			globalEntityManager.flush();
			globalEntityManager.refresh(usuario);
		}else {
			usuario = globalEntityManager.merge(usuario);
		}
		return usuario;
	}
	
	public void eliminarUsuarioEmpresa(Usuario usuario, Empresa emp) throws ApplicationException {
		if(usuario==null || usuario.getId() == null) {
			return;
		}
		if(emp==null || emp.getId() == null) {
			return;
		}
		if (usuario.getEmpresas().contains(emp)){
      //Eliminar los roles globales
			String schema = (String) globalEntityManager.getEntityManagerFactory().getProperties().get("hibernate.default_schema");
			if(schema == null) {
				schema = "public";
			}
			String sql = "DELETE FROM "+schema+".ae_rel_usuarios_roles r WHERE r.usuario_id=? AND r.empresa_id=?";
			Query query = globalEntityManager.createNativeQuery(sql);
			query.setParameter(1, usuario.getId());
			query.setParameter(2, emp.getId());
			query.executeUpdate();
			//Eliminar los roles por recurso
	    query = entityManager.createQuery("DELETE FROM RolesUsuarioRecurso r WHERE r.id.usuarioId=:usuarioId");
	    query = query.setParameter("usuarioId", usuario.getId());
	    query.executeUpdate();
			//Desasociar el usuario de la empresa
			usuario.getEmpresas().remove(emp);
			//Si no es superadministrador y no quedan empresas asociadas al usuario, se marca como eliminado
			if(!usuario.isSuperadmin() && usuario.getEmpresas().isEmpty()) {
			  usuario.setFechaBaja(new Date());
			}
		}else{
			return;
		}
		guardarUsuario(usuario);
	}

	@SuppressWarnings("unchecked")
	public List<String> obtenerRolesUsuarioEmpresa(Integer usuarioId, Integer empresaId) throws ApplicationException {
		if(usuarioId==null || empresaId==null) {
			return new ArrayList<String>();
		}
		
		try{
			String schema = (String) globalEntityManager.getEntityManagerFactory().getProperties().get("hibernate.default_schema");
			if(schema == null) {
				schema = "public";
			}
			String sql = "SELECT r.rol_nombre FROM "+schema+".ae_rel_usuarios_roles r WHERE r.usuario_id=? AND r.empresa_id=?";
			Query query = globalEntityManager.createNativeQuery(sql);
			query.setParameter(1, usuarioId);
			query.setParameter(2, empresaId);
			List<String> roles = (List<String>)query.getResultList();
			return roles;
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}
	
	public void guardarRolesUsuarioEmpresa(UsuarioEmpresaRoles roles) throws ApplicationException {
		try{
			String schema = (String) globalEntityManager.getEntityManagerFactory().getProperties().get("hibernate.default_schema");
			if(schema == null) {
				schema = "public";
			}
			
			String sql = "DELETE FROM "+schema+".ae_rel_usuarios_roles r WHERE r.usuario_id=? AND r.empresa_id=?";
			Query query = globalEntityManager.createNativeQuery(sql);
			query.setParameter(1, roles.getIdUsuario());
			query.setParameter(2, roles.getIdEmpresa());
			query.executeUpdate();
			
			sql = "INSERT INTO "+schema+".ae_rel_usuarios_roles(usuario_id, empresa_id, rol_nombre) values (?,?,?)";
			query = globalEntityManager.createNativeQuery(sql);
			query.setParameter(1, roles.getIdUsuario());
			query.setParameter(2, roles.getIdEmpresa());
			for(String rol : roles.getRoles()) {
				query.setParameter(3, rol);
				query.executeUpdate();
			}
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}


	@Override
	public Usuario generarYEnviarPassword(Usuario usuarioEditar) throws ApplicationException {
		if(usuarioEditar == null) {
			return null;
		}
		//Generar una contraseña nueva
		
		String password0 = RandomStringUtils.randomAscii(8);
		
		String password = encriptarPassword(password0);
		usuarioEditar.setPassword(password);
		//Guardar el usuario
		usuarioEditar = guardarUsuario(usuarioEditar);
		//Enviar por mail la nueva contraseña
		if(usuarioEditar.getCorreoe() == null) {
			throw new ApplicationException("no_se_puede_enviar_el_correo_porque_el_usuario_no_tiene_direccion_de_correo_electronico");
		}
		String content = "El usuario "+usuarioEditar.getCodigo()+" tendrá la nueva contraseña ["+password0+"] (sin los corchetes).";
		content = content+" Si la aplicación está configurada para utilizar CDA deberá utilizar su contraseña de CDA en lugar de esta contraseña";
		try {
			MailUtiles.enviarMail(usuarioEditar.getCorreoe(), "Nueva contraseña", content, MailUtiles.CONTENT_TYPE_HTML);
			
		}catch(MessagingException mEx) {
			throw new ApplicationException(mEx);
		}
		//Devolver el usuario con la nueva contraseña
		return usuarioEditar;
	}
	
	public static String encriptarPassword(String pwd) {
		if(pwd == null) {
			return null;
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd.getBytes());
			byte[] md5 = md.digest();
			String b64 = Base64.encodeBytes(md5);
			return b64;
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	//===========================================================================================
	
	@SuppressWarnings({"unchecked"})
	public List<Organismo> obtenerOrganismos(boolean actualizar) throws ApplicationException {
		if(actualizar) {
			try {
			  List<Organismo> organismos = tramitesBean.obtenerOrganismos();
        if(organismos != null) {
          //Se pudo invocar el servicio y parsear el resultado
          //Vaciar la tabla de organismos
          Query trunc = globalEntityManager.createQuery("DELETE FROM Organismo o");
          trunc.executeUpdate();
          for(Organismo organismo : organismos) {
            globalEntityManager.persist(organismo);
          }
        }
			}	catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		//Devolver la lista de organismos
		Query query = globalEntityManager.createQuery("SELECT o FROM Organismo o ORDER BY o.nombre");
		List<Organismo> organismos = (List<Organismo>)query.getResultList();
		return organismos;
	}

	@SuppressWarnings({"unchecked"})
	public List<UnidadEjecutora> obtenerUnidadesEjecutoras(boolean actualizar) throws ApplicationException {
		if(actualizar) {
			try {
			  List<UnidadEjecutora> unidadesEjecutoras = tramitesBean.obtenerUnidadesEjecutoras();
			  if(unidadesEjecutoras != null) {
	        //Se pudo invocar el servicio y parsear el resultado 
	        //Vaciar la tabla de organismos
	        Query trunc = globalEntityManager.createQuery("DELETE FROM UnidadEjecutora u");
	        trunc.executeUpdate();
	        //Insertar las unidades ejecutoras nuevas
	        for(UnidadEjecutora unidadEjecutora : unidadesEjecutoras) {
	          globalEntityManager.persist(unidadEjecutora);
	        }
			  }
			}	catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		//Devolver la lista de organismos
		Query query = globalEntityManager.createQuery("SELECT u FROM UnidadEjecutora u ORDER BY u.nombre");
		List<UnidadEjecutora> uEjecutoras = (List<UnidadEjecutora>)query.getResultList();
		return uEjecutoras;
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public List<Tramite> obtenerTramitesEmpresa(Integer empresaId, boolean actualizar) throws ApplicationException, UserException {
		if(actualizar) {
			Empresa empresa = obtenerEmpresaPorId(empresaId);
			if(empresa == null) {
				throw new UserException("no_se_encuentra_la_empresa_especificada");
			}
		  Integer organismoCod = null;
		  Integer unidadEjCod = null;
		  try {
		  	organismoCod = Integer.valueOf(empresa.getOrganismoCodigo());
		  	unidadEjCod = Integer.valueOf(empresa.getUnidadEjecutoraCodigo());
		  }catch(Exception ex) {
		  	//Nada para hacer
		  }
			if(organismoCod==null || organismoCod.intValue()<1 || unidadEjCod==null || unidadEjCod.intValue()<1) {
				throw new UserException("no_se_puede_obtener_los_tramites_porque_la_empresa_no_esta_asociada_a_ningun_organismo");
			}
			
			List<Tramite> tramites = tramitesBean.obtenerTramitesOrganismoYUnidadEjecutora(empresaId, organismoCod, unidadEjCod);
				
			if(tramites != null) {
				//Se procesaron todas las letras correctamente, se actualiza la base de datos
			  Query trunc = globalEntityManager.createQuery("DELETE FROM Tramite t WHERE t.empresaId=:empresaId");
			  trunc.setParameter("empresaId", empresaId);
			  trunc.executeUpdate();
			  for(Tramite tramite : tramites) {
			  	globalEntityManager.persist(tramite);
			  }
			}
		}
		//Devolver la lista de tramites
		Query query = globalEntityManager.createQuery("SELECT t FROM Tramite t WHERE t.empresaId=:empresaId ORDER BY t.nombre");
		query.setParameter("empresaId", empresaId);
		List<Tramite> tramites = (List<Tramite>)query.getResultList();
		
		return tramites;
	}

	@SuppressWarnings({"unchecked"})
	public List<Oficina> obtenerOficinasTramite(String tramiteEmpresaId, boolean actualizar) throws ApplicationException {
		if(tramiteEmpresaId == null) {
			return new ArrayList<Oficina>(0);
		}
		if(actualizar) {
			try {
				Tramite tramite = globalEntityManager.find(Tramite.class, tramiteEmpresaId);
				if(tramite != null) {
  				List<Oficina> oficinas = tramitesBean.obtenerOficinasTramite(tramite);
  				if(oficinas != null) {
  				  //Se pudo invocar el servicio y parsear el resultado
  					//Vaciar la tabla de oficinas para el tramite
  				  Query trunc = globalEntityManager.createQuery("DELETE FROM Oficina o WHERE o.tramite.id=:tramiteId");
  				  trunc.setParameter("tramiteId", tramiteEmpresaId);
  				  trunc.executeUpdate();
  				  for(Oficina oficina : oficinas) {
  				  	globalEntityManager.persist(oficina);
  				  }
  				}
				}
			}	catch (Exception ex) {
				ex.printStackTrace();
				//throw new ApplicationException("no_se_pudo_consultar_el_servicio_web", ex);
			}
		}
		//Devolver la lista de organismos
		Query query = globalEntityManager.createQuery("SELECT o FROM Oficina o WHERE o.tramite.id=:tramiteId ORDER BY o.nombre");
		query.setParameter("tramiteId", tramiteEmpresaId);
		List<Oficina> oficinas = (List<Oficina>)query.getResultList();
		return oficinas;
	}

	@Override
	public byte[] obtenerLogoEmpresaPorEmpresaId(Integer empId) {
		Query query = globalEntityManager.createQuery("SELECT e.logo from Empresa e WHERE e.id = :empresaId");
		query.setParameter("empresaId", empId);
		byte[] logo = (byte[])query.getSingleResult();
		return logo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Empresa> obtenerEmpresasPorDatasource(String dataSource) {
		Query query = globalEntityManager.createQuery("SELECT e from Empresa e WHERE e.datasource =:esquema");
		query.setParameter("esquema", dataSource);
		List<Empresa> listEmpresas = (List<Empresa>)query.getResultList();
		
		return listEmpresas;
	}
	
	@Override
	public boolean empresaEsquemaValido(Integer empresaId) {
		Query query = globalEntityManager.createQuery("SELECT e from Empresa e WHERE e.id =:empId");
		query.setParameter("empId", empresaId);
		Empresa e = (Empresa)query.getSingleResult();
		
		Connection connection = globalEntityManager.unwrap(SessionImpl.class).connection();
		DatabaseMetaData metaData;
		List<String> esquemas = new ArrayList<String>();
		try {
			metaData = connection.getMetaData();
			ResultSet result = metaData.getSchemas();
			while (result.next()) {
				esquemas.add(result.getString(1));
			}	
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return esquemas.contains(e.getDatasource());
	}
	
	public boolean existeEsquema(String esquema) {
		Connection connection = globalEntityManager.unwrap(SessionImpl.class).connection();
		DatabaseMetaData metaData;
		boolean existe = false;
		try {
			metaData = connection.getMetaData();
			ResultSet result = metaData.getSchemas();
			while (result.next()) {
				if(esquema.equals(result.getString(1))) {
					existe = true;
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return existe;
	}
	
	public List<String> obtenerIdiomasSoportados() {
		String idiomas = confBean.getString("IDIOMAS_SOPORTADOS");
		if(idiomas == null) {
			return null;
		}
		List<String> idiomasSoportados = new ArrayList<String>();
		for(String idioma : idiomas.split(",")) {
			if(!idioma.trim().isEmpty()) {
				idiomasSoportados.add(idioma.trim());
			}
		}
		return idiomasSoportados;		
		
	}
	
	public boolean hayOtroSuperadmin(Integer usuarioId) throws ApplicationException {
		try{
			globalEntityManager.createQuery("SELECT u FROM Usuario u WHERE u.superadmin=true AND u.id<>:usuarioId")
				.setParameter("usuarioId", usuarioId)
				.setMaxResults(1)
				.getSingleResult();
			return true;
		} catch(NoResultException nrEx) {
			return false;
		} catch(Exception ex) {
			throw new ApplicationException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
  public List<Token> consultarTokensEmpresa(Integer empresaId) throws ApplicationException {
		Query query = globalEntityManager.createQuery("SELECT t FROM Token t WHERE t.empresa.id=:empresaId ORDER BY t.nombre");
		query.setParameter("empresaId", empresaId);
		List<Token> tokens = (List<Token>)query.getResultList();
		return tokens;
	}
	
  public String crearToken(Integer empresaId, String nombre, String email) throws UserException {
  	if(empresaId==null || nombre==null || email==null) {
  		throw new UserException("parametros_incorrectos");
  	}
  	Empresa empresa = globalEntityManager.find(Empresa.class, empresaId);
  	if(empresa==null) {
  		throw new UserException("no_se_encuentra_la_empresa_especificada");
  	}

  	String sToken = null;
  	String query = "SELECT t FROM Token t WHERE t.token=:token";
  	while(sToken == null) {
  		sToken = RandomStringUtils.randomAlphanumeric(25);
  		//Verificar que no esté usado el token
  		boolean ok = globalEntityManager.createQuery(query).setParameter("token", sToken).getResultList().isEmpty();
  		if(!ok) {
  			//Ya está en uso, se genera otro
  			sToken = null;
  		}
  	}
  	
  	Token token = new Token();
  	token.setEmail(email);
  	token.setEmpresa(empresa);
  	token.setFecha(new Date());
  	token.setNombre(nombre);
  	token.setToken(sToken);
  	
  	globalEntityManager.persist(token);
  	
		return sToken;
	}
	
  public void eliminarToken(String sToken) {
  	Token token = globalEntityManager.find(Token.class, sToken);
  	if(token!=null) {
  		globalEntityManager.remove(token);
  	}
  }
  
}
