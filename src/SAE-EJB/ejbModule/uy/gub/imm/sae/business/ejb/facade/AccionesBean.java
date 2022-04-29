package uy.gub.imm.sae.business.ejb.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ParametroAccion;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Accion;
import uy.gub.imm.sae.entity.AccionPorDato;
import uy.gub.imm.sae.entity.AccionPorRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

/**
 * Session Bean implementation class AccionesBean
 */

@Stateless
@RolesAllowed("RA_AE_ADMINISTRADOR")
public class AccionesBean implements AccionesLocal, AccionesRemote {

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager em;

	static Logger logger = Logger.getLogger(AccionesBean.class);
	
	
	@SuppressWarnings("unchecked")
	public List<Accion> consultarAcciones() throws ApplicationException{
		try{
			List<Accion> accion = (List<Accion>) em
									.createQuery("SELECT v from Accion v " +
											"WHERE v.fechaBaja IS NULL " +
											" ORDER BY v.nombre")
									.getResultList();
			return accion;
			} catch (Exception e){
				throw new ApplicationException(e);
			}
	}
	/**
	 * Crea la accion <b>v</b> en el sistema.
	 * Controla la unicidad del nombre de la accion entre todas las acciones vivas (fechaBaja == null).
	 * Retorna la accion con su identificador interno.
	 * Roles permitidos: Administrador
	 * @throws UserException 
	 * @throws ApplicationException 
	 * @throws BusinessException 
	 */
	public Accion crearAccion(Accion v)throws UserException, BusinessException {

		if (v.getNombre() == null || v.getNombre().trim().isEmpty()) {
			throw new UserException("el_nombre_de_la_accion_es_obligatorio");
		} else if (v.getNombre().length() > 50) {
			throw new UserException("el_nombre_de_la_accion_es_demasiado_largo");
		}
    if (v.getDescripcion() == null || v.getDescripcion().trim().isEmpty()) {
      throw new UserException("la_descripcion_de_la_accion_es_obligatoria");
    }else if (v.getDescripcion().length() > 100) {
      throw new UserException("la_descripcion_de_la_accion_es_demasiado_larga");
    }
		if (v.getServicio() == null || v.getServicio().trim().isEmpty()) {
			throw new UserException("el_servicio_de_la_accion_es_obligatorio");
		}else if (v.getServicio().length() > 250) {
			throw new UserException("el_servicio_de_la_accion_es_demasiado_largo");
		}
		if (v.getHost() == null || v.getHost().trim().isEmpty()) {
			throw new UserException("el_host_de_la_accion_es_obligatorio");
		}else if (v.getHost().length() > 100) {
			throw new UserException("el_host_de_la_accion_es_demasiado_largo");
		}
		
		if (existeAccionPorNombre(v.getNombre()) ) {
			throw new UserException("ya_existe_una_accion_con_el_nombre_especificado");
		}

		Accion vNueva = new Accion(v);
		em.persist(vNueva);
		
	  modificarParametrosAccion(vNueva, v.getParametrosAccion());
		
		vNueva.getParametrosAccion().size(); //lazy
		
		return vNueva;
	}

	/**
	 * Se realiza la baja l�gica de la accion (se setea fechaBaja con la fecha actual del sistema).
	 * Controla que no existan datos vivos asociados a la accion , si es as� no se la da de baja.	 * 
	 * @throws UserException 
	 * @throws ApplicationException 
	 * @throws BusinessException 
	 */

	public void eliminarAccion(Accion v) throws UserException, BusinessException {

		Accion accion = (Accion) em.find(Accion.class, v.getId());
		
		if (accion == null) {
		  throw new UserException ("no_se_encuentra_la_accion_especificada");
		}
		
	  if (hayDatosVivos(accion)) {
			throw new UserException("la_accion_se_encuentra_asociada_a_un_recurso");
		}
		
		//Elimino todos los parametros
    modificarParametrosAccion(accion, new ArrayList<ParametroAccion>());

		accion.setFechaBaja(new Date());
	}

	
	/**
	 * Se realiza la modificación de la accion <b>v</b>.
	 * Controla la unicidad del nombre de la accion entre todas las acciones vivas (fechaBaja == null).
	 * Actualiza los parametros de acuerdo a los indicados en la accion de entrada.
	 * Roles permitidos: Administrador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public Accion modificarAccion(Accion v) throws UserException, BusinessException {

		Accion accionActual = (Accion) em.find(Accion.class, v.getId());
		
		if (accionActual == null) {
			throw new UserException ("no_se_encuentra_la_accion_especificada");
		}
		
    if (v.getNombre() == null || v.getNombre().trim().isEmpty()) {
      throw new UserException("el_nombre_de_la_accion_es_obligatorio");
    } else if (v.getNombre().length() > 50) {
      throw new UserException("el_nombre_de_la_accion_es_demasiado_largo");
    }
    if (v.getDescripcion() == null || v.getDescripcion().trim().isEmpty()) {
      throw new UserException("la_descripcion_de_la_accion_es_obligatoria");
    }else if (v.getDescripcion().length() > 100) {
      throw new UserException("la_descripcion_de_la_accion_es_demasiado_larga");
    }
    if (v.getServicio() == null || v.getServicio().trim().isEmpty()) {
      throw new UserException("el_servicio_de_la_accion_es_obligatorio");
    }else if (v.getServicio().length() > 250) {
      throw new UserException("el_servicio_de_la_accion_es_demasiado_largo");
    }
    if (v.getHost() == null || v.getHost().trim().isEmpty()) {
      throw new UserException("el_host_de_la_accion_es_obligatorio");
    }else if (v.getHost().length() > 100) {
      throw new UserException("el_host_de_la_accion_es_demasiado_largo");
    }
    
    if (existeAccionPorNombreSalvoEsta(v) ) {
      throw new UserException("ya_existe_una_accion_con_el_nombre_especificado");
    }

  	accionActual.setNombre(v.getNombre());
  	accionActual.setDescripcion(v.getDescripcion());
  	accionActual.setServicio(v.getServicio());
  	accionActual.setHost(v.getHost());
      
	  modificarParametrosAccion(accionActual, v.getParametrosAccion());
  	
  	accionActual.getParametrosAccion().size(); //lazy
  	return accionActual;
	}

	
	/*
	 * Modifica los parametros de la accion, realizando Alta/Baja/Modificacion
	 * hasta sincronizar la lista de parametros del objeto persistido con la lista de parametros de entrada 
	 */
	private void modificarParametrosAccion(Accion v, List<ParametroAccion> parametrosModificados) throws UserException {
		
		List<ParametroAccion> parametrosActuales = v.getParametrosAccion();
		
		Date ahora = new Date();
		
		//Parametros nuevos/modificados para accederlos por id.
		Map<Integer, ParametroAccion> mapModificados = new HashMap<Integer, ParametroAccion>();
		for (ParametroAccion parametroModificado : parametrosModificados) {
			if (parametroModificado.getId() != null) {
				mapModificados.put(parametroModificado.getId(), parametroModificado);
			}
		}
		
		//Recorro los parametros actuales:
		// 1- Dando de baja los que no esten en el mapa, pues significa que el usuario los quiere borrar.
		// 2- Modificando los que si esten en el mapa.
		for (ParametroAccion pActual : parametrosActuales) {
			chequeoNoUsoDelParametro(pActual);
			if (mapModificados.containsKey(pActual.getId())) {
				//Modificar el parámetro
				ParametroAccion pModificado = mapModificados.get(pActual.getId());
				if (pModificado.getNombre() == null || pModificado.getNombre().equals("")) throw new UserException("-1","Falta indicar el nombre de un parámetro");
				if (pModificado.getNombre().length() > 50) throw new UserException("-1","El nombre del parámetro es demasiado largo, máximo 50 caracteres.");
				pActual.setNombre(pModificado.getNombre());
			} else {
				//Eliminar el parámetro
				pActual.setFechaBaja(ahora);
			}
		}

		//Recorro los parametros modificados buscando parametros nuevos, es decir, los que no tengan id definido.
		for (ParametroAccion pModificado : parametrosModificados) {
			if (pModificado.getId() == null) {
				//Parametro nuevo
				ParametroAccion pNuevo = new ParametroAccion(pModificado);
				pNuevo.setFechaBaja(null); //Por las dudas.
				pNuevo.setAccion(v);
				if (pNuevo.getNombre() == null || pNuevo.getNombre().equals("")) throw new UserException("-1","Falta indicar el nombre de un parámetro");
				if (pNuevo.getNombre().length() > 50) throw new UserException("-1","El nombre del parámetro es demasiado largo, máximo 50 caracteres.");
				em.persist(pNuevo);
			}
		}
		
	}
	
	private void chequeoNoUsoDelParametro(ParametroAccion p) throws UserException {
		
		Long cant = (Long)em.createQuery(
				"select count(vxd) from AccionPorDato vxd " +
				"where vxd.nombreParametro = :nombre and " +
				"      vxd.accionPorRecurso.accion.id = :accion and " +
				"      vxd.fechaDesasociacion is null "
				).setParameter("nombre", p.getNombre()).setParameter("accion", p.getAccion().getId().intValue()).getSingleResult();
		
		if (cant > 0) {
			throw new UserException("no_se_puede_modificar_los_parametros_si_estan_en_uso");
		}
	}
	
	public Boolean existeAccionPorNombre(String nombreAccion) {

		Long cant = (Long) em
					.createQuery("SELECT count(v) from Accion v " +
							"WHERE upper(v.nombre) = :nombre " +
							"AND v.fechaBaja IS NULL")
					.setParameter("nombre", nombreAccion.toUpperCase())
					.getSingleResult();
		
		return (cant > 0);
	}
	
	
	
	
	
	/************************************************************
	 * ADMINISTRACION DE LAS VALIDACIONES DE UN RECURSO
	 * @throws ApplicationException 
	 ***********************************************************/
	
	
	@SuppressWarnings("unchecked")
	public List<AccionPorRecurso> obtenerAccionesDelRecurso(Recurso recurso) throws ApplicationException {
		
		List<AccionPorRecurso> acciones = null;
		
		try {
			acciones = em.createQuery(
					"SELECT   vxr from AccionPorRecurso vxr " +
					"WHERE    vxr.recurso = :recurso AND " +
					"         vxr.fechaBaja IS NULL " +
					"ORDER BY vxr.evento DESC, vxr.ordenEjecucion "
			).setParameter("recurso", recurso).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		return acciones;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<AccionPorDato> obtenerAsociacionesAccionPorDato(AccionPorRecurso vr) throws ApplicationException {

		List<AccionPorDato> asociaciones = null;
		
		try {
			asociaciones = em.createQuery(
					"SELECT   vxd from AccionPorDato vxd " +
					"WHERE    vxd.accionPorRecurso = :vxr AND " +
					"         vxd.fechaDesasociacion IS NULL " +
					"ORDER BY vxd.nombreParametro "
			).setParameter("vxr", vr).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		return asociaciones;
	}

	
	
	
	
	
	
	
	/**
	 * Crea una AccionPorDato, es decir, asocia la accion al dato.
	 * Controla que esta accion ya no este asociada al dato, 
	 * es decir que no exista otra AccionPorDato viva para el par accion,dato. 
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public AccionPorDato asociarAccionPorDato(DatoASolicitar d, AccionPorRecurso vr, AccionPorDato vd) throws UserException, ApplicationException {

		//Se controla que exista el Dato a solicitar y que no se encuentre dado de baja.
		DatoASolicitar datoActual = (DatoASolicitar) em.find(DatoASolicitar.class, d.getId());
		
		if (datoActual == null) {
			throw new UserException("AE10055","No existe el dato a Solicitar: " + d.getId().toString());
		}

		//No se puede modificar un dato con fecha de baja
		if (datoActual.getFechaBaja() != null) {
			throw new UserException("AE10062","No se puede utilizar un dato con fecha de baja");
		}
		
		//Se controla que exista la acción y que no se encuentre dada de baja.
		AccionPorRecurso valActual = (AccionPorRecurso) em.find(AccionPorRecurso.class, vr.getId());
		
		if (valActual == null) {
			throw new UserException("AE10032","No existe la acción que se quiere modificar: " + vr.getId().toString());
		}
		
		vd.setDatoASolicitar(d);
		vd.setAccionPorRecurso(vr);
		
		//No se puede modificar un dato con fecha de baja
		if (valActual.getFechaBaja() != null) {
			throw new UserException("AE10063","No se puede utilizar una acción con fecha de baja");
		}
		
		//Se controla que la acción no est� ya asociada al dato.
		if (existeAsocDatoAccion(vd)) {
			throw new UserException("AE10066","Ya existe esa accion por Dato");
		}
		
		if (vd.getFechaDesasociacion() != null){
			throw new UserException("AE10065","Al asociar un dato a una acción, la fecha de desasociacion debe ser nula");
		}
		
		em.persist(vd);
		return vd;
	}
	
	@SuppressWarnings("unchecked")
	public List<ParametroAccion> consultarParametrosDeLaAccion(Accion v) throws ApplicationException {
		
		
		List<ParametroAccion> parametros = null;
		
		try {
			parametros = em.createQuery(
					"SELECT   p from ParametroAccion p " +
					"WHERE    p.accion = :accion AND " +
					"         p.fechaBaja IS NULL " +
					"ORDER BY p.nombre "
			).setParameter("accion", v).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		parametros.size();
		return parametros;		
	}
		
	
	public AccionPorRecurso crearAccionPorRecurso(
			AccionPorRecurso vrDTO) throws BusinessException, UserException {

		if (vrDTO.getOrdenEjecucion() == null || vrDTO.getOrdenEjecucion() < 1){
			throw new UserException("-1", "Debe especificar el orden de ejecuci�n de la acción");
		}
		
		if (vrDTO.getAccion() == null || vrDTO.getAccion().getId() == null) {
			throw new BusinessException("-1", "Falta indicar la Accion que se quiere asignar al recurso");
		}
		
		Accion v = em.find(Accion.class, vrDTO.getAccion().getId());
		if (v == null || v.getFechaBaja() != null) {
			throw new BusinessException("-1", "No existe la acción que se quiere asignar al recurso");
		}
		
		if (vrDTO.getRecurso() == null || vrDTO.getRecurso().getId() == null) {
			throw new BusinessException("-1", "Falta indicar el recurso al que se le quiere asignar una accion"	);
		}
		
		Recurso r = em.find(Recurso.class, vrDTO.getRecurso().getId());
		if (r == null || r.getFechaBaja() != null) {
			throw new BusinessException("-1", "No existe el recurso al que se le quiere asignar una accion");
		}
		
		AccionPorRecurso vxr = new AccionPorRecurso();
		vxr.setOrdenEjecucion(vrDTO.getOrdenEjecucion());
		vxr.setEvento(vrDTO.getEvento());
		vxr.setAccion(v);
		vxr.setRecurso(r);
		
		em.persist(vxr);

		modificarAccionesPorDatoDeLaAccionPorRecurso(vxr, vrDTO.getAccionesPorDato());
		
		return vxr;		
	}
	public void eliminarAccionPorRecurso(AccionPorRecurso vr) throws BusinessException {

		//Se controla que exista la acción.
		AccionPorRecurso vrActual = (AccionPorRecurso) em.find(AccionPorRecurso.class, vr.getId());
		
		if (vrActual == null) {
			throw new BusinessException("-1","No existe la accion asociada al recurso: " + vr.getId().toString());
		}
		
		for (AccionPorDato vxd : vrActual.getAccionesPorDato()) {
			vxd.setFechaDesasociacion(new Date());
		}
		vrActual.setFechaBaja(new Date());

	}
	
	/**
	 * 
	 */
	public AccionPorRecurso modificarAccionPorRecurso(AccionPorRecurso vr) throws UserException, BusinessException {
		//Se controla que exista la acción.
		AccionPorRecurso vrActual = (AccionPorRecurso) em.find(AccionPorRecurso.class, vr.getId());
		
		if (vrActual == null) {
			throw new BusinessException("AE10067","No existe la accion asociada al recurso: " + vr.getId().toString());
		}

		if (vrActual.getFechaBaja() != null) {
			throw new BusinessException("AE10067","La accion asociada al recurso esta eliminada: " + vr.getId().toString());
		}

		if (vr.getOrdenEjecucion() == null || vr.getOrdenEjecucion() < 1){
			throw new UserException("AE10064","Debe especificar el orden de ejecuci�n de la acción");
		}
		
		vrActual.setOrdenEjecucion(vr.getOrdenEjecucion());
		vrActual.setEvento(vr.getEvento());
		
		//Controlo que no me cambie de accion mientras tenga accionesPorDato vivas.
		if (! vrActual.getAccion().getId().equals(vr.getAccion().getId()) &&
			vrActual.getAccionesPorDato().size() > 0) {
			throw new UserException ("-1", "Para cambiar de acción, primero debe eliminar todas las asociaciones de parámetros");
		}
		
		if (! vrActual.getAccion().getId().equals(vr.getAccion().getId())) {
			Accion v = em.find(Accion.class, vr.getAccion().getId());
			if (v == null || v.getFechaBaja() != null) {
				throw new BusinessException("-1", "No existe la acción que se quiere asignar al recurso");
			}
			vrActual.setAccion(v);
		}
		else {
			modificarAccionesPorDatoDeLaAccionPorRecurso(vrActual, vr.getAccionesPorDato());
		}
		
		return vrActual;
	}

	
	
	
	/*
	 * Modifica el mapeo: DatoASolicitar <--> ParametroAccion para una AccionPorRecurso, realizando Alta/Baja/Modificacion
	 * hasta sincronizar la lista de accionesPorDato del objeto persistido con la lista de accionesPorDato de entrada 
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void modificarAccionesPorDatoDeLaAccionPorRecurso(AccionPorRecurso vxr, List<AccionPorDato> accionesPorDatoModificados) throws UserException, BusinessException {
		
		List<AccionPorDato> accionesPorDatoActuales = em.createQuery("" +
				"from  AccionPorDato vxd " +
				"where vxd.accionPorRecurso = :vxr and vxd.fechaDesasociacion is null " +
				"")
				.setParameter("vxr", vxr).getResultList();
		
		Date ahora = new Date();
		
		//AccionPorDato modificados para accederlos por id.
		Map<Integer, AccionPorDato> mapModificados = new HashMap<Integer, AccionPorDato>();
		for (AccionPorDato vxdModificado : accionesPorDatoModificados) {
			if (vxdModificado.getId() != null) {
				mapModificados.put(vxdModificado.getId(), vxdModificado);
			}
		}
		
		//Recorro los AccionPorDato actuales:
		// 1- Dando de baja los que no esten en el mapa, pues significa que el usuario los quiere borrar.
		// 2- Modificando los que si esten en el mapa.
		for (AccionPorDato vxdActual : accionesPorDatoActuales) {

			if (mapModificados.containsKey(vxdActual.getId())) {
				//Modificar la accionPorDato
				AccionPorDato vxdModificado = mapModificados.get(vxdActual.getId());
				
				if (vxdModificado.getNombreParametro() == null) {
					throw new UserException("-1","Falta indicar el parámetro");
				}
				try {
					ParametroAccion param = (ParametroAccion)em.createQuery(
						"from ParametroAccion p " +
						"where p.nombre = :nombre and p.fechaBaja is null and p.accion = :v ")
						.setParameter("nombre", vxdModificado.getNombreParametro())
						.setParameter("v", vxr.getAccion())
						.getSingleResult();
				} catch (NoResultException e) {
					throw new BusinessException("-1", "Se intenta asociar un parametro que no existe.");
				}
				
				DatoASolicitar campoActual = em.find(DatoASolicitar.class, vxdModificado.getDatoASolicitar().getId());

				if (campoActual == null || campoActual.getFechaBaja() != null || ! campoActual.getRecurso().equals(vxr.getRecurso())) {
					throw new BusinessException("-1", "Se intenta asociar un DatoASolicitar que no existe o no corresponde al recurso en cuesti�n");
				}
				
				vxdActual.setNombreParametro(vxdModificado.getNombreParametro());
				vxdActual.setDatoASolicitar(campoActual);
			}
			else {
				//Eliminar la accionPorDato
				vxdActual.setFechaDesasociacion(ahora);
			}
		}

		//Recorro los AccionPorDato modificados buscando nuevos, es decir, los que no tengan id definido.
		for (AccionPorDato vxdModificado : accionesPorDatoModificados) {
			if (vxdModificado.getId() == null) {
				//AccionPorDato nueva
				AccionPorDato vxdNuevo = new AccionPorDato();

				if (vxdModificado.getNombreParametro() == null) {
					throw new UserException("-1","Falta indicar el parámetro");
				}
				ParametroAccion param = (ParametroAccion)em.createQuery(
						"from  ParametroAccion p " +
						"where p.nombre = :nombre and p.fechaBaja is null and p.accion = :v ")
						.setParameter("nombre", vxdModificado.getNombreParametro())
						.setParameter("v", vxr.getAccion())
						.getSingleResult();
				
				if (param == null) {
					throw new BusinessException("-1", "Se intenta asociar un parametro que no existe.");
				}
				
				DatoASolicitar campoActual = em.find(DatoASolicitar.class, vxdModificado.getDatoASolicitar().getId());

				if (campoActual == null || campoActual.getFechaBaja() != null || ! campoActual.getRecurso().equals(vxr.getRecurso())) {
					throw new BusinessException("-1", "Se intenta asociar un DatoASolicitar que no existe o no corresponde al recurso en cuesti�n");
				}
				
				vxdNuevo.setNombreParametro(vxdModificado.getNombreParametro());
				vxdNuevo.setDatoASolicitar(campoActual);
				vxdNuevo.setAccionPorRecurso(vxr);
				
				em.persist(vxdNuevo);
			}
		}
		
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Realiza una baja l�gica de la AccionPorDato (setea fechaBaja a la fecha actual del sistema).
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */
	public void desasociarAccionPorDato(AccionPorDato vd) throws UserException {

		//Se controla que no exista la acción.
		AccionPorDato vdActual = (AccionPorDato) em.find(AccionPorDato.class, vd.getId());
		
		if (vdActual == null) {
			throw new UserException("AE10067","No existe esa accion por Dato: " + vd.getId().toString());
		}	

		if (vdActual.getFechaDesasociacion() != null) {
			throw new UserException("AE10067","La accion por dato esta desasociada: " + vd.getId().toString());
		}
		
		
		vdActual.setFechaDesasociacion(new Date());
	}
	
	
	//Se controla que la acción no est� ya asociada al dato.
	private Boolean existeAsocDatoAccion(AccionPorDato vd) throws ApplicationException{
		try{
			Long cant = (Long) em
							.createQuery("SELECT count(vd) "+
									"FROM AccionPorDato vd " +
									"WHERE vd.datoASolicitar = :dato " +
									"AND vd.accion = :accion " +
									"AND (vd.id <> :id OR :id is null) " +
									"AND vd.fechaDesasociacion IS NULL")
							.setParameter("dato", vd.getDatoASolicitar())
							.setParameter("accion", vd.getAccionPorRecurso())
							.setParameter("id", vd.getId())
							.getSingleResult();
		
			return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}	
	
	private Boolean existeAccionPorNombreSalvoEsta(Accion v) {

		Long cant = (Long) em.createQuery(
					"SELECT count(v) from Accion v " +
					"WHERE upper(v.nombre) = :nombre AND " +
					"      v.fechaBaja IS NULL AND " +
					"      v.id != :id ")
					.setParameter("nombre", v.getNombre().toUpperCase())
					.setParameter("id", v.getId())
					.getSingleResult();
			
		return (cant > 0);
	}
	
	private Boolean hayDatosVivos(Accion v){
		Long cant = (Long) em
					.createQuery("SELECT count(vxr) FROM  AccionPorRecurso vxr " +
							"WHERE vxr.accion = :accion AND vxr.fechaBaja is null")
					.setParameter("accion", v)
					.getSingleResult();
		return (cant > 0);
	}
}

