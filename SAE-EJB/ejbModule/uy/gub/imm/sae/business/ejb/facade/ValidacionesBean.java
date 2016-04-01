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
import uy.gub.imm.sae.entity.ParametroValidacion;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Validacion;
import uy.gub.imm.sae.entity.ValidacionPorDato;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed("RA_AE_ADMINISTRADOR")
public class ValidacionesBean implements ValidacionesLocal, ValidacionesRemote {

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager em;

	static Logger logger = Logger.getLogger(ValidacionesBean.class);
	
	
	@SuppressWarnings("unchecked")
	public List<Validacion> consultarValidaciones() throws ApplicationException{
		try{
			List<Validacion> validacion = (List<Validacion>) em
									.createQuery("SELECT v from Validacion v " +
											"WHERE v.fechaBaja IS NULL " +
											" ORDER BY v.nombre")
									.getResultList();
			return validacion;
			} catch (Exception e){
				throw new ApplicationException(e);
			}
	}
	/**
	 * Crea la validacion <b>v</b> en el sistema.
	 * Controla la unicidad del nombre de la validacion entre todas las validaciones vivas (fechaBaja == null).
	 * Retorna la validacion con su identificador interno.
	 * Roles permitidos: Administrador
	 * @throws UserException 
	 * @throws ApplicationException 
	 * @throws BusinessException 
	 */
	public Validacion crearValidacion(Validacion v)throws UserException, BusinessException {

		if (v.getNombre() == null || v.getNombre().equals("")) {
			throw new UserException("-1", "El nombre es requerido");
		}
		if (v.getNombre().length() > 50) {
			throw new UserException("-1", "El nombre es demasiado largo, máximo 50 caracteres.");
		}
		
		if (v.getServicio() == null || v.getServicio().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del servicio que implementa la validación");
		}
		if (v.getServicio().length() > 250) {
			throw new UserException("-1", "El servicio es demasiado largo, máximo 250 caracteres.");
		}
		
		if (v.getHost() == null || v.getHost().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del host donde se encuentra la validación");
		}
		if (v.getHost().length() > 100) {
			throw new UserException("-1", "El host es demasiado largo, máximo 100 caracteres.");
		}
		
		if (v.getDescripcion() == null || v.getDescripcion().equals("")) {
			throw new UserException("-1", "Debe indicar la descripción del servicio");
		}
		if (v.getDescripcion().length() > 100) {
			throw new UserException("-1", "La descripción es demasiado larga, máximo 100 caracteres.");
		}
		
		//Se controla que no exista otra validacion con el mismo nombre
		if (existeValidacionPorNombre(v.getNombre()) ) {
				throw new UserException("AE10031","Ya existe una validacion con ese nombre "+ v.getNombre());
		}
		//Se controla que la validacion no tenga fecha de baja
		if (v.getFechaBaja() != null){
			throw new BusinessException("AE10028","No se puede dar de alta una validacion con fecha de baja: "+ v.getNombre());			
		}

		Validacion vNueva = new Validacion(v);
		em.persist(vNueva);
		
		logger.error("Cant parametros nuevos: "+v.getParametrosValidacion().size());
		modificarParametrosValidacion(vNueva, v.getParametrosValidacion());
		
		vNueva.getParametrosValidacion().size(); //lazy
		
		return vNueva;
	}

	/**
	 * Se realiza la baja lógica de la validacion (se setea fechaBaja con la fecha actual del sistema).
	 * Controla que no existan datos vivos asociados a la validacion , si es así no se la da de baja.	 * 
	 * @throws UserException 
	 * @throws ApplicationException 
	 * @throws BusinessException 
	 */

	public void eliminarValidacion(Validacion v) throws UserException, BusinessException {

		Validacion validacion = (Validacion) em.find(Validacion.class, v.getId());
		
		if (validacion == null) {
			throw new BusinessException("AE10029","No existe la validacion que se quiere eliminar: " + v.getId().toString());
		}
		
		//Se controla que no existan datos asociados vivos para la validacion.
		 if  (hayDatosVivos(validacion)) {
			throw new UserException("AE10030","La validación se encuentra asociada a algun recurso: " + v.getId().toString());
		}
		
		//Elimino todos los parametros
		modificarParametrosValidacion(validacion, new ArrayList<ParametroValidacion>());

		validacion.setFechaBaja(new Date());
	}

	
	/**
	 * Se realiza la modificación de la validacion <b>v</b>.
	 * Controla la unicidad del nombre de la validacion entre todas las validaciones vivas (fechaBaja == null).
	 * Actualiza los parametros de acuerdo a los indicados en la validacion de entrada.
	 * Roles permitidos: Administrador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public Validacion modificarValidacion(Validacion v) throws UserException, BusinessException {

		Validacion validacionActual = (Validacion) em.find(Validacion.class, v.getId());
		
		if (validacionActual == null) {
			throw new BusinessException ("AE10032","No existe la validacion que se quiere modificar: " + v.getId().toString());
		}
		
		if (v.getNombre() == null || v.getNombre().equals("")) {
			throw new UserException("-1", "El nombre es requerido");
		}
		if (v.getNombre().length() > 50) {
			throw new UserException("-1", "El nombre es demasiado largo, máximo 50 caracteres.");
		}
		
		if (v.getServicio() == null || v.getServicio().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del servicio que implementa la validación");
		}
		if (v.getServicio().length() > 250) {
			throw new UserException("-1", "El servicio es demasiado largo, máximo 250 caracteres.");
		}
		
		if (v.getHost() == null || v.getHost().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del host donde se encuentra la validación");
		}
		if (v.getHost().length() > 100) {
			throw new UserException("-1", "El host es demasiado largo, máximo 100 caracteres.");
		}
		
		if (v.getDescripcion() == null || v.getDescripcion().equals("")) {
			throw new UserException("-1", "Debe indicar la descripción del servicio");
		}
		if (v.getDescripcion().length() > 100) {
			throw new UserException("-1", "La descripción es demasiado larga, máximo 100 caracteres.");
		}
		
		//Se controla que no exista otra validacion viva con el mismo nombre salvo la propia.
		if (existeValidacionPorNombreSalvoEsta(v) ) {
			throw new UserException("AE10031","Ya existe una validacion con el nombre: "+ v.getNombre());
		}

		//Se controla que la validacion no tenga fecha de baja
		if (validacionActual.getFechaBaja() != null){
			throw new BusinessException("AE10028","No se puede modificar una validacion con fecha de baja: "+ v.getNombre());			
		}
		
    	validacionActual.setNombre(v.getNombre());
    	validacionActual.setDescripcion(v.getDescripcion());
    	validacionActual.setServicio(v.getServicio());
    	validacionActual.setHost(v.getHost());
        
    	modificarParametrosValidacion(validacionActual, v.getParametrosValidacion());
    	
    	validacionActual.getParametrosValidacion().size(); //lazy
    	return validacionActual;
	}

	
	/*
	 * Modifica los parametros de la validacion, realizando Alta/Baja/Modificacion
	 * hasta sincronizar la lista de parametros del objeto persistido con la lista de parametros de entrada 
	 */
	private void modificarParametrosValidacion(Validacion v, List<ParametroValidacion> parametrosModificados) throws UserException {
		
		List<ParametroValidacion> parametrosActuales = v.getParametrosValidacion();
		
		Date ahora = new Date();
		
		//Parametros nuevos/modificados para accederlos por id.
		Map<Integer, ParametroValidacion> mapModificados = new HashMap<Integer, ParametroValidacion>();
		for (ParametroValidacion parametroModificado : parametrosModificados) {
			if (parametroModificado.getId() != null) {
				mapModificados.put(parametroModificado.getId(), parametroModificado);
			}
		}
		
		//Recorro los parametros actuales:
		// 1- Dando de baja los que no esten en el mapa, pues significa que el usuario los quiere borrar.
		// 2- Modificando los que si esten en el mapa.
		for (ParametroValidacion pActual : parametrosActuales) {

			chequeoNoUsoDelParametro(pActual);

			if (mapModificados.containsKey(pActual.getId())) {
				//Modificar el parámetro
				ParametroValidacion pModificado = mapModificados.get(pActual.getId());
				if (pModificado.getNombre() == null || pModificado.getNombre().equals("")) throw new UserException("-1","Falta indicar el nombre de un parámetro");
				if (pModificado.getNombre().length() > 50) throw new UserException("-1","El nombre del parámetro es demasiado largo, máximo 50 caracteres.");
				if (pModificado.getTipo()   == null) throw new UserException("-1","Falta indicar el tipo de un parámetro");
				if (pModificado.getLargo()  == null) throw new UserException("-1","Falta indicar el largo de un parámetro");
				
				pActual.setNombre(pModificado.getNombre());
				pActual.setTipo(pModificado.getTipo());
				pActual.setLargo(pModificado.getLargo());
			}
			else {
				//Eliminar el parámetro
				pActual.setFechaBaja(ahora);
			}
		}

		//Recorro los parametros modificados buscando parametros nuevos, es decir, los que no tengan id definido.
		for (ParametroValidacion pModificado : parametrosModificados) {
			if (pModificado.getId() == null) {
				//Parametro nuevo
				ParametroValidacion pNuevo = new ParametroValidacion(pModificado);
				pNuevo.setFechaBaja(null); //Por las dudas.
				pNuevo.setValidacion(v);
				if (pNuevo.getNombre() == null || pNuevo.getNombre().equals("")) throw new UserException("-1","Falta indicar el nombre de un parámetro");
				if (pNuevo.getNombre().length() > 50) throw new UserException("-1","El nombre del parámetro es demasiado largo, máximo 50 caracteres.");
				if (pNuevo.getTipo() == null) throw new UserException("-1","Falta indicar el tipo de un parámetro");
				if (pNuevo.getLargo() == null) throw new UserException("-1","Falta indicar el largo de un parámetro");
				
				em.persist(pNuevo);
			}
		}
		
	}
	
	private void chequeoNoUsoDelParametro(ParametroValidacion p) throws UserException {
		
		Long cant = (Long)em.createQuery(
				"select count(vxd) from ValidacionPorDato vxd " +
				"where vxd.nombreParametro = :nombre and " +
				"      vxd.validacionPorRecurso.validacion.id = :validacion and " +
				"      vxd.fechaDesasociacion is null "
				).setParameter("nombre", p.getNombre()).setParameter("validacion", p.getValidacion().getId().intValue()).getSingleResult();
		
		if (cant > 0) {
			throw new UserException("-1", "No se puede modificar/eliminar un parámetro si está siendo utilizado en las validaciones de algún recurso");
		}
	}
	
	public Boolean existeValidacionPorNombre(String nombreValidacion) {

		Long cant = (Long) em
					.createQuery("SELECT count(v) from Validacion v " +
							"WHERE upper(v.nombre) = :nombre " +
							"AND v.fechaBaja IS NULL")
					.setParameter("nombre", nombreValidacion.toUpperCase())
					.getSingleResult();
		
		return (cant > 0);
	}
	
	
	
	
	
	/************************************************************
	 * ADMINISTRACION DE LAS VALIDACIONES DE UN RECURSO
	 * @throws ApplicationException 
	 ***********************************************************/
	
	
	@SuppressWarnings("unchecked")
	public List<ValidacionPorRecurso> obtenerValidacionesDelRecurso(Recurso recurso) throws ApplicationException {
		
		List<ValidacionPorRecurso> validaciones = null;
		
		try {
			validaciones = em.createQuery(
					"SELECT   vxr from ValidacionPorRecurso vxr " +
					"WHERE    vxr.recurso = :recurso AND " +
					"         vxr.fechaBaja IS NULL " +
					"ORDER BY vxr.ordenEjecucion "
			).setParameter("recurso", recurso).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		return validaciones;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<ValidacionPorDato> obtenerAsociacionesValidacionPorDato(ValidacionPorRecurso vr) throws ApplicationException {

		List<ValidacionPorDato> asociaciones = null;
		
		try {
			asociaciones = em.createQuery(
					"SELECT   vxd from ValidacionPorDato vxd " +
					"WHERE    vxd.validacionPorRecurso = :vxr AND " +
					"         vxd.fechaDesasociacion IS NULL " +
					"ORDER BY vxd.nombreParametro "
			).setParameter("vxr", vr).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		return asociaciones;
	}

	
	
	
	
	
	
	
	/**
	 * Crea una ValidacionPorDato, es decir, asocia la validacion al dato.
	 * Controla que esta validacion ya no este asociada al dato, 
	 * es decir que no exista otra ValidacionPorDato viva para el par validacion,dato. 
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public ValidacionPorDato asociarValidacionPorDato(DatoASolicitar d, ValidacionPorRecurso vr, ValidacionPorDato vd) throws UserException, ApplicationException {

		//Se controla que exista el Dato a solicitar y que no se encuentre dado de baja.
		DatoASolicitar datoActual = (DatoASolicitar) em.find(DatoASolicitar.class, d.getId());
		
		if (datoActual == null) {
			throw new UserException("AE10055","No existe el dato a Solicitar: " + d.getId().toString());
		}

		//No se puede modificar un dato con fecha de baja
		if (datoActual.getFechaBaja() != null) {
			throw new UserException("AE10062","No se puede utilizar un dato con fecha de baja");
		}
		
		//Se controla que exista la validación y que no se encuentre dada de baja.
		ValidacionPorRecurso valActual = (ValidacionPorRecurso) em.find(ValidacionPorRecurso.class, vr.getId());
		
		if (valActual == null) {
			throw new UserException("AE10032","No existe la validación que se quiere modificar: " + vr.getId().toString());
		}
		
		vd.setDatoASolicitar(d);
		vd.setValidacionPorRecurso(vr);
		
		//No se puede modificar un dato con fecha de baja
		if (valActual.getFechaBaja() != null) {
			throw new UserException("AE10063","No se puede utilizar una validación con fecha de baja");
		}
		
		//Se controla que la validación no está ya asociada al dato.
		if (existeAsocDatoValidacion(vd)) {
			throw new UserException("AE10066","Ya existe esa validacion por Dato");
		}
		
		if (vd.getFechaDesasociacion() != null){
			throw new UserException("AE10065","Al asociar un dato a una validación, la fecha de desasociacion debe ser nula");
		}
		
		em.persist(vd);
		return vd;
	}
	
	@SuppressWarnings("unchecked")
	public List<ParametroValidacion> consultarParametrosDeLaValidacion(Validacion v) throws ApplicationException {
		
		
		List<ParametroValidacion> parametros = null;
		
		try {
			parametros = em.createQuery(
					"SELECT   p from ParametroValidacion p " +
					"WHERE    p.validacion = :validacion AND " +
					"         p.fechaBaja IS NULL " +
					"ORDER BY p.nombre "
			).setParameter("validacion", v).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		parametros.size();
		return parametros;		
	}
		
	
	public ValidacionPorRecurso crearValidacionPorRecurso(
			ValidacionPorRecurso vrDTO) throws BusinessException, UserException {

		if (vrDTO.getOrdenEjecucion() == null || vrDTO.getOrdenEjecucion() < 1){
			throw new UserException("-1", "Debe especificar el orden de ejecución de la validación");
		}
		
		if (vrDTO.getValidacion() == null || vrDTO.getValidacion().getId() == null) {
			throw new BusinessException("-1", "Falta indicar la Validacion que se quiere asignar al recurso");
		}
		
		Validacion v = em.find(Validacion.class, vrDTO.getValidacion().getId());
		if (v == null || v.getFechaBaja() != null) {
			throw new BusinessException("-1", "No existe la validación que se quiere asignar al recurso");
		}
		
		if (vrDTO.getRecurso() == null || vrDTO.getRecurso().getId() == null) {
			throw new BusinessException("-1", "Falta indicar el recurso al que se le quiere asignar una validacion"	);
		}
		
		Recurso r = em.find(Recurso.class, vrDTO.getRecurso().getId());
		if (r == null || r.getFechaBaja() != null) {
			throw new BusinessException("-1", "No existe el recurso al que se le quiere asignar una validacion");
		}
		
		ValidacionPorRecurso vxr = new ValidacionPorRecurso();
		vxr.setOrdenEjecucion(vrDTO.getOrdenEjecucion());
		vxr.setValidacion(v);
		vxr.setRecurso(r);
		em.persist(vxr);

		modificarValidacionesPorDatoDeLaValidacionPorRecurso(vxr, vrDTO.getValidacionesPorDato());
		
		return vxr;		
	}
	public void eliminarValidacionPorRecurso(ValidacionPorRecurso vr) throws BusinessException {

		//Se controla que exista la validación.
		ValidacionPorRecurso vrActual = (ValidacionPorRecurso) em.find(ValidacionPorRecurso.class, vr.getId());
		
		if (vrActual == null) {
			throw new BusinessException("-1","No existe la validacion asociada al recurso: " + vr.getId().toString());
		}
		
		for (ValidacionPorDato vxd : vrActual.getValidacionesPorDato()) {
			vxd.setFechaDesasociacion(new Date());
		}
		vrActual.setFechaBaja(new Date());

	}
	
	/**
	 * 
	 */
	public ValidacionPorRecurso modificarValidacionPorRecurso(ValidacionPorRecurso vr) throws UserException, BusinessException {
		//Se controla que exista la validación.
		ValidacionPorRecurso vrActual = (ValidacionPorRecurso) em.find(ValidacionPorRecurso.class, vr.getId());
		
		if (vrActual == null) {
			throw new BusinessException("AE10067","No existe la validacion asociada al recurso: " + vr.getId().toString());
		}

		if (vrActual.getFechaBaja() != null) {
			throw new BusinessException("AE10067","La validacion asociada al recurso esta eliminada: " + vr.getId().toString());
		}

		if (vr.getOrdenEjecucion() == null || vr.getOrdenEjecucion() < 1){
			throw new UserException("AE10064","Debe especificar el orden de ejecución de la validación");
		}
		
		vrActual.setOrdenEjecucion(vr.getOrdenEjecucion());
		
		//Controlo que no me cambie de validacion mientras tenga validacionesPorDato vivas.
		if (! vrActual.getValidacion().getId().equals(vr.getValidacion().getId()) &&
			vrActual.getValidacionesPorDato().size() > 0) {
			throw new UserException ("-1", "Para cambiar de validación, primero debe eliminar todas las asociaciones de parámetros");
		}
		
		if (! vrActual.getValidacion().getId().equals(vr.getValidacion().getId())) {
			Validacion v = em.find(Validacion.class, vr.getValidacion().getId());
			if (v == null || v.getFechaBaja() != null) {
				throw new BusinessException("-1", "No existe la validación que se quiere asignar al recurso");
			}
			vrActual.setValidacion(v);
		}
		else {
			modificarValidacionesPorDatoDeLaValidacionPorRecurso(vrActual, vr.getValidacionesPorDato());
		}
		
		return vrActual;
	}

	
	
	
	/*
	 * Modifica el mapeo: DatoASolicitar <--> ParametroValidacion para una ValidacionPorRecurso, realizando Alta/Baja/Modificacion
	 * hasta sincronizar la lista de validacionesPorDato del objeto persistido con la lista de validacionesPorDato de entrada 
	 */
	@SuppressWarnings("unchecked")
	private void modificarValidacionesPorDatoDeLaValidacionPorRecurso(ValidacionPorRecurso vxr, List<ValidacionPorDato> validacionesPorDatoModificados) throws UserException, BusinessException {
		
		List<ValidacionPorDato> validacionesPorDatoActuales = em.createQuery("" +
				"from  ValidacionPorDato vxd " +
				"where vxd.validacionPorRecurso = :vxr and vxd.fechaDesasociacion is null " +
				"")
				.setParameter("vxr", vxr).getResultList();
		
		Date ahora = new Date();
		
		//ValidacionPorDato modificados para accederlos por id.
		Map<Integer, ValidacionPorDato> mapModificados = new HashMap<Integer, ValidacionPorDato>();
		for (ValidacionPorDato vxdModificado : validacionesPorDatoModificados) {
			if (vxdModificado.getId() != null) {
				mapModificados.put(vxdModificado.getId(), vxdModificado);
			}
		}
		
		//Recorro los ValidacionPorDato actuales:
		// 1- Dando de baja los que no esten en el mapa, pues significa que el usuario los quiere borrar.
		// 2- Modificando los que si esten en el mapa.
		for (ValidacionPorDato vxdActual : validacionesPorDatoActuales) {

			if (mapModificados.containsKey(vxdActual.getId())) {
				//Modificar la validacionPorDato
				ValidacionPorDato vxdModificado = mapModificados.get(vxdActual.getId());
				
				if (vxdModificado.getNombreParametro() == null) {
					throw new UserException("-1","Falta indicar el parámetro");
				}
				try {
					em.createQuery(
						"from ParametroValidacion p " +
						"where p.nombre = :nombre and p.fechaBaja is null and p.validacion = :v ")
						.setParameter("nombre", vxdModificado.getNombreParametro())
						.setParameter("v", vxr.getValidacion())
						.getSingleResult();
				} catch (NoResultException e) {
					throw new BusinessException("-1", "Se intenta asociar un parametro que no existe.");
				}
				
				DatoASolicitar campoActual = em.find(DatoASolicitar.class, vxdModificado.getDatoASolicitar().getId());

				if (campoActual == null || campoActual.getFechaBaja() != null || ! campoActual.getRecurso().equals(vxr.getRecurso())) {
					throw new BusinessException("-1", "Se intenta asociar un DatoASolicitar que no existe o no corresponde al recurso en cuestión");
				}
				
				vxdActual.setNombreParametro(vxdModificado.getNombreParametro());
				vxdActual.setDatoASolicitar(campoActual);
			}
			else {
				//Eliminar la validacionPorDato
				vxdActual.setFechaDesasociacion(ahora);
			}
		}

		//Recorro los ValidacionPorDato modificados buscando nuevos, es decir, los que no tengan id definido.
		for (ValidacionPorDato vxdModificado : validacionesPorDatoModificados) {
			if (vxdModificado.getId() == null) {
				//ValidacionPorDato nueva
				ValidacionPorDato vxdNuevo = new ValidacionPorDato();

				if (vxdModificado.getNombreParametro() == null) {
					throw new UserException("-1","Falta indicar el parámetro");
				}
				ParametroValidacion param = (ParametroValidacion)em.createQuery(
						"from  ParametroValidacion p " +
						"where p.nombre = :nombre and p.fechaBaja is null and p.validacion = :v ")
						.setParameter("nombre", vxdModificado.getNombreParametro())
						.setParameter("v", vxr.getValidacion())
						.getSingleResult();
				
				if (param == null) {
					throw new BusinessException("-1", "Se intenta asociar un parametro que no existe.");
				}
				
				DatoASolicitar campoActual = em.find(DatoASolicitar.class, vxdModificado.getDatoASolicitar().getId());

				if (campoActual == null || campoActual.getFechaBaja() != null || ! campoActual.getRecurso().equals(vxr.getRecurso())) {
					throw new BusinessException("-1", "Se intenta asociar un DatoASolicitar que no existe o no corresponde al recurso en cuestión");
				}
				
				vxdNuevo.setNombreParametro(vxdModificado.getNombreParametro());
				vxdNuevo.setDatoASolicitar(campoActual);
				vxdNuevo.setValidacionPorRecurso(vxr);
				
				em.persist(vxdNuevo);
			}
		}
		
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Realiza una baja lógica de la ValidacionPorDato (setea fechaBaja a la fecha actual del sistema).
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */
	public void desasociarValidacionPorDato(ValidacionPorDato vd) throws UserException {

		//Se controla que no exista la validación.
		ValidacionPorDato vdActual = (ValidacionPorDato) em.find(ValidacionPorDato.class, vd.getId());
		
		if (vdActual == null) {
			throw new UserException("AE10067","No existe esa validacion por Dato: " + vd.getId().toString());
		}	

		if (vdActual.getFechaDesasociacion() != null) {
			throw new UserException("AE10067","La validacion por dato esta desasociada: " + vd.getId().toString());
		}
		
		
		vdActual.setFechaDesasociacion(new Date());
	}
	
	
	//Se controla que la validación no está ya asociada al dato.
	private Boolean existeAsocDatoValidacion(ValidacionPorDato vd) throws ApplicationException{
		try{
			Long cant = (Long) em
							.createQuery("SELECT count(vd) "+
									"FROM ValidacionPorDato vd " +
									"WHERE vd.datoASolicitar = :dato " +
									"AND vd.validacion = :validacion " +
									"AND (vd.id <> :id OR :id is null) " +
									"AND vd.fechaDesasociacion IS NULL")
							.setParameter("dato", vd.getDatoASolicitar())
							.setParameter("validacion", vd.getValidacionPorRecurso())
							.setParameter("id", vd.getId())
							.getSingleResult();
		
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}	
	
	private Boolean existeValidacionPorNombreSalvoEsta(Validacion v) {

		Long cant = (Long) em.createQuery(
					"SELECT count(v) from Validacion v " +
					"WHERE upper(v.nombre) = :nombre AND " +
					"      v.fechaBaja IS NULL AND " +
					"      v.id != :id ")
					.setParameter("nombre", v.getNombre().toUpperCase())
					.setParameter("id", v.getId())
					.getSingleResult();
			
		return (cant > 0);
	}
	
	private Boolean hayDatosVivos(Validacion v){
		Long cant = (Long) em
					.createQuery("SELECT count(vxr) FROM  ValidacionPorRecurso vxr " +
							"WHERE vxr.validacion = :validacion AND vxr.fechaBaja is null")
					.setParameter("validacion", v)
					.getSingleResult();
		return (cant > 0);
	}
}
