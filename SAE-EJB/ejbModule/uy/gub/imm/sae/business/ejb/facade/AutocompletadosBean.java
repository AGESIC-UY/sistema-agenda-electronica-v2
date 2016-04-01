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
import uy.gub.imm.sae.entity.ParametrosAutocompletar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.ServicioAutocompletar;
import uy.gub.imm.sae.entity.ServicioAutocompletarPorDato;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed("RA_AE_ADMINISTRADOR")
public class AutocompletadosBean implements AutocompletadosLocal, AutocompletadosRemote {

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager em;

	static Logger logger = Logger.getLogger(AutocompletadosBean.class);
	
	private void chequeoNoUsoDelParametro(ParametrosAutocompletar p) throws UserException {
		
		Long cant = (Long)em.createQuery(
				"select count(sxd) from ServicioAutocompletarPorDato sxd " +
				"where sxd.nombreParametro = :nombre and " +
				"      sxd.servicioPorRecurso.autocompletado.id = :autocompletado and " +
				"      sxd.fechaDesasociacion is null "
				).setParameter("nombre", p.getNombre()).setParameter("autocompletado", p.getServicio().getId().intValue()).getSingleResult();
		
		if (cant > 0) {
			throw new UserException("-1", "No se puede modificar/eliminar un parámetro si est� siendo utilizado en los datos autocompletados de alg�n recurso");
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void modificarAutocompletadosPorDatoDelAutocompletadoPorRecurso(ServicioPorRecurso sxr, List<ServicioAutocompletarPorDato> autocompletadosPorDatoModificados) throws UserException, BusinessException {
		
		List<ServicioAutocompletarPorDato> autocompletadosPorDatoActuales = em.createQuery("" +
				"from  ServicioAutocompletarPorDato sxd " +
				"where sxd.servicioPorRecurso = :sxr and sxd.fechaDesasociacion is null " +
				"")
				.setParameter("sxr", sxr).getResultList();
		
		Date ahora = new Date();
		
		Map<Integer, ServicioAutocompletarPorDato> mapModificados = new HashMap<Integer, ServicioAutocompletarPorDato>();
		for (ServicioAutocompletarPorDato sxdModificado : autocompletadosPorDatoModificados) {
			if (sxdModificado.getId() != null) {
				mapModificados.put(sxdModificado.getId(), sxdModificado);
			}
		}
		
		//Recorro los ServicioAutocompletarPorDato actuales:
		// 1- Dando de baja los que no esten en el mapa, pues significa que el usuario los quiere borrar.
		// 2- Modificando los que si esten en el mapa.
		for (ServicioAutocompletarPorDato sxdActual : autocompletadosPorDatoActuales) {

			if (mapModificados.containsKey(sxdActual.getId())) {
				//Modificar la ServicioAutocompletarPorDato
				ServicioAutocompletarPorDato sxdModificado = mapModificados.get(sxdActual.getId());
				
				if (sxdModificado.getNombreParametro() == null) {
					throw new UserException("-1","Falta indicar el parámetro");
				}
				try {
					ParametrosAutocompletar param = (ParametrosAutocompletar)em.createQuery(
						"from ParametrosAutocompletar p " +
						"where p.nombre = :nombre and p.fecha_baja is null and p.servicio = :s ")
						.setParameter("nombre", sxdModificado.getNombreParametro())
						.setParameter("s", sxr.getAutocompletado())
						.getSingleResult();
				} catch (NoResultException e) {
					throw new BusinessException("-1", "Se intenta asociar un parametro que no existe.");
				}
				
				DatoASolicitar campoActual = em.find(DatoASolicitar.class, sxdModificado.getDatoASolicitar().getId());

				if (campoActual == null || campoActual.getFechaBaja() != null || ! campoActual.getRecurso().equals(sxr.getRecurso())) {
					throw new BusinessException("-1", "Se intenta asociar un DatoASolicitar que no existe o no corresponde al recurso en cuesti�n");
				}
				
				sxdActual.setNombreParametro(sxdModificado.getNombreParametro());
				sxdActual.setDatoASolicitar(campoActual);
			}
			else {
				//Eliminar el ServicioAutocompletarPorDato
				sxdActual.setFechaDesasociacion(ahora);
			}
		}

		//Recorro los ServicioAutocompletarPorDato modificados buscando nuevos, es decir, los que no tengan id definido.
		for (ServicioAutocompletarPorDato sxdModificado : autocompletadosPorDatoModificados) {
			if (sxdModificado.getId() == null) {
				//ServicioAutocompletarPorDato nueva
				ServicioAutocompletarPorDato sxdNuevo = new ServicioAutocompletarPorDato();

				if (sxdModificado.getNombreParametro() == null) {
					throw new UserException("-1","Falta indicar el parámetro");
				}
				ParametrosAutocompletar param = (ParametrosAutocompletar)em.createQuery(
						"from  ParametrosAutocompletar p " +
						"where p.nombre = :nombre and p.fecha_baja is null and p.servicio = :s ")
						.setParameter("nombre", sxdModificado.getNombreParametro())
						.setParameter("s", sxr.getAutocompletado())
						.getSingleResult();
				
				if (param == null) {
					throw new BusinessException("-1", "Se intenta asociar un parametro que no existe.");
				}
				
				DatoASolicitar campoActual = em.find(DatoASolicitar.class, sxdModificado.getDatoASolicitar().getId());

				if (campoActual == null || campoActual.getFechaBaja() != null || ! campoActual.getRecurso().equals(sxr.getRecurso())) {
					throw new BusinessException("-1", "Se intenta asociar un DatoASolicitar que no existe o no corresponde al recurso en cuesti�n");
				}
				
				sxdNuevo.setNombreParametro(sxdModificado.getNombreParametro());
				sxdNuevo.setDatoASolicitar(campoActual);
				sxdNuevo.setServicioPorRecurso(sxr);				
				
				em.persist(sxdNuevo);
			}
		}
		
	}	
	
	
	//Se controla que la validación no est� ya asociada al dato.
	private Boolean existeAsocDatoAutocompletado(ServicioAutocompletarPorDato sad) throws ApplicationException{
		try{
			Long cant = (Long) em
							.createQuery("SELECT count(sad) "+
									"FROM ServicioAutocompletarPorDato sad " +
									"WHERE sad.datoASolicitar = :dato " +
									"AND sad.servicio = :servicio " +
									"AND (sad.id <> :id OR :id is null) " +
									"AND sad.fechaDesasociacion IS NULL")
							.setParameter("dato", sad.getDatoASolicitar())
							.setParameter("servicio", sad.getServicioPorRecurso())
							.setParameter("id", sad.getId())
							.getSingleResult();
		
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}	
	
	private Boolean existeAutocompletadoPorNombreSalvoEsta(ServicioAutocompletar s) {

		Long cant = (Long) em.createQuery(
					"SELECT count(s) from ServicioAutocompletar s " +
					"WHERE upper(s.nombre) = :nombre AND " +
					"      s.fechaBaja IS NULL AND " +
					"      s.id != :id ")
					.setParameter("nombre", s.getNombre().toUpperCase())
					.setParameter("id", s.getId())
					.getSingleResult();
			
		return (cant > 0);
	}
	
	private Boolean hayDatosVivos(ServicioAutocompletar s){
		Long cant = (Long) em
					.createQuery("SELECT count(sxr) FROM  ServicioPorRecurso sxr " +
							"WHERE sxr.autocompletado = :autocompletado AND sxr.fechaBaja is null")
					.setParameter("autocompletado", s)
					.getSingleResult();
		return (cant > 0);
	}
	
	
	///////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	public List<ServicioAutocompletar> consultarAutoCompletados()
			throws ApplicationException {
		try{
			List<ServicioAutocompletar> servicio = (List<ServicioAutocompletar>) em
									.createQuery("SELECT s from ServicioAutocompletar s " +
											"WHERE s.fechaBaja IS NULL " +
											" ORDER BY s.nombre")
									.getResultList();
			return servicio;
			} catch (Exception e){
				throw new ApplicationException(e);
			}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ParametrosAutocompletar> consultarParametrosDelAutoCompletado(
			ServicioAutocompletar a) throws ApplicationException {
		
		List<ParametrosAutocompletar> parametros = null;
		//List<ParametrosAutocompletar> parametrosRetorno = new ArrayList<ParametrosAutocompletar>();
		
		try {
			parametros = em.createQuery(
					"SELECT   p from ParametrosAutocompletar p " +
					"WHERE    p.servicio = :servicio AND " +
					"         p.fecha_baja IS NULL " +
					"ORDER BY p.nombre "
			).setParameter("servicio", a).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		parametros.size();
				
//		for (ParametrosAutocompletar parametroAutocompletar : parametros) {
//			if (parametroAutocompletar.getModo().equals("E")) {
//				parametroAutocompletar.setModo("Entrada");
//			}
//			else if (parametroAutocompletar.getModo().equals("S")) {
//				parametroAutocompletar.setModo("Salida");
//			}
//			parametrosRetorno.add(parametroAutocompletar);
//		}		
		
		return parametros;
	}
	
	public ServicioAutocompletar crearAutoCompletado(ServicioAutocompletar a)
			throws UserException, BusinessException {
		
		if (a.getNombre() == null || a.getNombre().equals("")) {
			throw new UserException("-1", "El nombre es requerido");
		}
		if (a.getNombre().length() > 50) {
			throw new UserException("-1", "El nombre es demasiado largo, máximo 50 caracteres.");
		}
		
		if (a.getServicio() == null || a.getServicio().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del servicio que implementa el dato autocompletado");
		}
		if (a.getServicio().length() > 250) {
			throw new UserException("-1", "El servicio es demasiado largo, máximo 250 caracteres.");
		}
		
		if (a.getHost() == null || a.getHost().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del host donde se encuentra el dato autocompletado");
		}
		if (a.getHost().length() > 100) {
			throw new UserException("-1", "El host es demasiado largo, máximo 100 caracteres.");
		}
		
		if (a.getDescripcion() == null || a.getDescripcion().equals("")) {
			throw new UserException("-1", "Debe indicar la descripción del servicio");
		}
		if (a.getDescripcion().length() > 100) {
			throw new UserException("-1", "La descripción es demasiado larga, máximo 100 caracteres.");
		}
				
		if (existeAutoCompletadoPorNombre(a.getNombre())) {
				throw new UserException("AE10031","Ya existe un dato autocompletado con el nombre "+ a.getNombre());
		}

		if (a.getFechaBaja() != null){
			throw new BusinessException("AE10028","No se puede dar de alta un dato autocompletado con fecha de baja: "+ a.getNombre());			
		}

		ServicioAutocompletar servicio = new ServicioAutocompletar(a);
		em.persist(servicio);
		
		logger.error("Cant parametros nuevos: "+ a.getParametrosAutocompletados().size());
		modificarParametrosAutoCompletado(servicio, a.getParametrosAutocompletados());
				
		servicio.getParametrosAutocompletados().size();
		
		return servicio;
	}
	
	
	private void modificarParametrosAutoCompletado(ServicioAutocompletar s, List<ParametrosAutocompletar> parametrosModificados) throws UserException {
		
		List<ParametrosAutocompletar> parametrosActuales = s.getParametrosAutocompletados();
		
		Date ahora = new Date();
		
		//Parametros nuevos/modificados para accederlos por id.
		Map<Integer, ParametrosAutocompletar> mapModificados = new HashMap<Integer, ParametrosAutocompletar>();
		for (ParametrosAutocompletar parametroModificado : parametrosModificados) {
			if (parametroModificado.getId() != null) {
				mapModificados.put(parametroModificado.getId(), parametroModificado);
			}
		}
		
		//Recorro los parametros actuales:
		// 1- Dando de baja los que no esten en el mapa, pues significa que el usuario los quiere borrar.
		// 2- Modificando los que si esten en el mapa.
		for (ParametrosAutocompletar pActual : parametrosActuales) {

			chequeoNoUsoDelParametro(pActual);

			if (mapModificados.containsKey(pActual.getId())) {
				//Modificar el parámetro
				ParametrosAutocompletar pModificado = mapModificados.get(pActual.getId());
				if (pModificado.getNombre() == null || pModificado.getNombre().equals("")) throw new UserException("-1","Falta indicar el nombre de un parámetro");
				if (pModificado.getNombre().length() > 50) throw new UserException("-1","El nombre del parámetro es demasiado largo, máximo 50 caracteres.");
				if (pModificado.getTipo()   == null) throw new UserException("-1","Falta indicar el tipo de un parámetro");
				if (pModificado.getModo() == null) throw new UserException("-1","Falta indicar el modo de un parámetro");
				
				pActual.setNombre(pModificado.getNombre());
				pActual.setTipo(pModificado.getTipo());
				//pActual.setModo(pModificado.getModo().substring(0, 1));
				pActual.setModo(pModificado.getModo());
			}
			else {
				//Eliminar el parámetro
				pActual.setFecha_baja(ahora);
			}
		}

		//Recorro los parametros modificados buscando parametros nuevos, es decir, los que no tengan id definido.
		for (ParametrosAutocompletar pModificado : parametrosModificados) {
			if (pModificado.getId() == null) {
				//Parametro nuevo
				ParametrosAutocompletar pNuevo = new ParametrosAutocompletar(pModificado);
				pNuevo.setFecha_baja(null);
				pNuevo.setServicio(s);
				if (pNuevo.getNombre() == null || pNuevo.getNombre().equals("")) throw new UserException("-1","Falta indicar el nombre de un parámetro");
				if (pNuevo.getNombre().length() > 50) throw new UserException("-1","El nombre del parámetro es demasiado largo, máximo 50 caracteres.");
				if (pNuevo.getTipo() == null) throw new UserException("-1","Falta indicar el tipo de un parámetro");
				if (pNuevo.getModo() == null) throw new UserException("-1","Falta indicar el modo de un parámetro");
								
				//pNuevo.setModo(pModificado.getModo().substring(0, 1));
				em.persist(pNuevo);
			}
		}
		
	}
	
	
	public void eliminarAutoCompletado(ServicioAutocompletar a) throws UserException, BusinessException {
		ServicioAutocompletar auto = (ServicioAutocompletar) em.find(ServicioAutocompletar.class, a.getId());
		
		if (auto == null) {
			throw new BusinessException("AE10029","No existe el dato autocompletado que se quiere eliminar: " + a.getId().toString());
		}
		
		//Se controla que no existan datos asociados vivos para el dato autocompletado.
		 if  (hayDatosVivos(auto)) {
			throw new UserException("AE10030","El dato autocompletado se encuentra asociado a algun recurso: " + a.getId().toString());
		}
		
		//Elimino todos los parametros		
		modificarParametrosAutoCompletado(auto, new ArrayList<ParametrosAutocompletar>());

		auto.setFechaBaja(new Date());
		
	}
	public Boolean existeAutoCompletadoPorNombre(String nombreAutoCompletado) {		
		Long cant = (Long) em
		.createQuery("SELECT count(s) from ServicioAutocompletar s " +
				"WHERE upper(s.nombre) = :nombre " +
				"AND s.fechaBaja IS NULL")
		.setParameter("nombre", nombreAutoCompletado.toUpperCase())
		.getSingleResult();

		return (cant > 0);
		
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	public ServicioAutocompletar modificarAutoCompletado(ServicioAutocompletar a)
			throws UserException, BusinessException {
		ServicioAutocompletar servicioActual = (ServicioAutocompletar) em.find(ServicioAutocompletar.class, a.getId());
		
		if (servicioActual == null) {
			throw new BusinessException ("AE10032","No existe el dato autocompletado que se quiere modificar: " + a.getId().toString());
		}
		
		if (a.getNombre() == null || a.getNombre().equals("")) {
			throw new UserException("-1", "El nombre es requerido");			
		}
		if (a.getNombre().length() > 50) {
			throw new UserException("-1", "El nombre es demasiado largo, máximo 50 caracteres.");
		}
		
		if (a.getServicio() == null || a.getServicio().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del servicio que implementa el dato autocompletado");
		}
		if (a.getServicio().length() > 250) {
			throw new UserException("-1", "El servicio es demasiado largo, máximo 250 caracteres.");
		}
		
		if (a.getHost() == null || a.getHost().equals("")) {
			throw new UserException("-1", "Debe indicar el nombre del host donde se encuentra el dato autocompletado");
		}
		if (a.getHost().length() > 100) {
			throw new UserException("-1", "El host es demasiado largo, máximo 100 caracteres.");
		}
		
		if (a.getDescripcion() == null || a.getDescripcion().equals("")) {
			throw new UserException("-1", "Debe indicar la descripción del servicio");
		}
		if (a.getDescripcion().length() > 100) {
			throw new UserException("-1", "La descripción es demasiado larga, máximo 100 caracteres.");
		}
		
		//Se controla que no exista otro dato autocompletado viva con el mismo nombre salvo la propia.
		if (existeAutocompletadoPorNombreSalvoEsta(a) ) {
			throw new UserException("AE10031","Ya existe un dato autocompletado con el nombre: "+ a.getNombre());
		}

		//Se controla que el dato autocompletado no tenga fecha de baja
		if (servicioActual.getFechaBaja() != null){
			throw new BusinessException("AE10028","No se puede modificar un un dato autocompletado con fecha de baja: "+ a.getNombre());			
		}
		
		servicioActual.setNombre(a.getNombre());
		servicioActual.setDescripcion(a.getDescripcion());
		servicioActual.setServicio(a.getServicio());
		servicioActual.setHost(a.getHost());
        
    	modificarParametrosAutoCompletado(servicioActual, a.getParametrosAutocompletados());
    	
    	servicioActual.getParametrosAutocompletados().size();
    	return servicioActual;
	}


	public ServicioAutocompletarPorDato asociarAutocompletadoPorDato(
			DatoASolicitar d, ServicioPorRecurso sr,
			ServicioAutocompletarPorDato sad) throws UserException,
			ApplicationException {
		
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
		ServicioPorRecurso servicioActual = (ServicioPorRecurso) em.find(ServicioPorRecurso.class, sr.getId());
		
		if (servicioActual == null) {
			throw new UserException("AE10032","No existe el dato autocompletado que se quiere modificar: " + sr.getId().toString());
		}
		
		sad.setDatoASolicitar(d);
		sad.setServicioPorRecurso(sr);
		
		//No se puede modificar un dato con fecha de baja
		if (servicioActual.getFechaBaja() != null) {
			throw new UserException("AE10063","No se puede utilizar dato autocompletado con fecha de baja");
		}
		
		//Se controla que la validación no est� ya asociada al dato.
		if (existeAsocDatoAutocompletado(sad)) {
			throw new UserException("AE10066","Ya existe ese dato autocompletado por Dato");
		}
		
		if (sad.getFechaDesasociacion() != null){
			throw new UserException("AE10065","Al asociar un dato a un autocompletado, la fecha de desasociacion debe ser nula");
		}
		
		em.persist(sad);
		return sad;
	}

	public ServicioPorRecurso crearAutocompletadoPorRecurso(
			ServicioPorRecurso sr) throws BusinessException, UserException {
		
		if (sr.getAutocompletado() == null || sr.getAutocompletado().getId() == null) {
			throw new BusinessException("-1", "Falta indicar el dato autocompletado que se quiere asignar al recurso");
		}
		
		ServicioAutocompletar s = em.find(ServicioAutocompletar.class, sr.getAutocompletado().getId());
		if (s == null || s.getFechaBaja() != null) {
			throw new BusinessException("-1", "No existe el dato autocompletado que se quiere asignar al recurso");
		}
		
		if (sr.getRecurso() == null || sr.getRecurso().getId() == null) {
			throw new BusinessException("-1", "Falta indicar el recurso al que se le quiere asignar el dato autocompletado"	);
		}
		
		Recurso r = em.find(Recurso.class, sr.getRecurso().getId());
		if (r == null || r.getFechaBaja() != null) {
			throw new BusinessException("-1", "No existe el recurso al que se le quiere asignar el dato autocompletado");
		}
		
		ServicioPorRecurso sxr = new ServicioPorRecurso();
		sxr.setAutocompletado(s);
		sxr.setRecurso(r);
		em.persist(sxr);

		modificarAutocompletadosPorDatoDelAutocompletadoPorRecurso(sxr, sr.getAutocompletadosPorDato());
		
		return sxr;
	}


	public void desasociarAutocompletadoPorDato(ServicioAutocompletarPorDato sad)
			throws UserException {

		ServicioAutocompletarPorDato sdActual = (ServicioAutocompletarPorDato) em.find(ServicioAutocompletarPorDato.class, sad.getId());
		
		if (sdActual == null) {
			throw new UserException("AE10067","No existe el dato autocompletado por Dato: " + sad.getId().toString());
		}	

		if (sdActual.getFechaDesasociacion() != null) {
			throw new UserException("AE10067","El dato autocompletado por dato esta desasociado: " + sad.getId().toString());
		}
		
		sdActual.setFechaDesasociacion(new Date());
	}


	public void eliminarAutocompletadoPorRecurso(ServicioPorRecurso sr)
			throws BusinessException {
		
		ServicioPorRecurso srActual = (ServicioPorRecurso) em.find(ServicioPorRecurso.class, sr.getId());
		
		if (srActual == null) {
			throw new BusinessException("-1","No existe el dato autocompletado asociado al recurso: " + sr.getId().toString());
		}
		
		for (ServicioAutocompletarPorDato sxd : srActual.getAutocompletadosPorDato()) {
			sxd.setFechaDesasociacion(new Date());
		}
		srActual.setFechaBaja(new Date());
		
	}


	public ServicioPorRecurso modificarAutocompletadoPorRecurso(
			ServicioPorRecurso sr) throws UserException, BusinessException {
		
		ServicioPorRecurso srActual = (ServicioPorRecurso) em.find(ServicioPorRecurso.class, sr.getId());
		
		if (srActual == null) {
			throw new BusinessException("AE10067","No existe el dato autocompletado asociado al recurso: " + sr.getId().toString());
		}

		if (srActual.getFechaBaja() != null) {
			throw new BusinessException("AE10067","El dato autocompletado asociado al recurso esta eliminada: " + sr.getId().toString());
		}				
		
		if (! srActual.getAutocompletado().getId().equals(sr.getAutocompletado().getId()) &&
			srActual.getAutocompletadosPorDato().size() > 0) {
			throw new UserException ("-1", "Para cambiar de dato autocompletado, primero debe eliminar todas las asociaciones de parámetros");
		}
		
		if (! srActual.getAutocompletado().getId().equals(sr.getAutocompletado().getId())) {
			ServicioAutocompletar s = em.find(ServicioAutocompletar.class, sr.getAutocompletado().getId());
			if (s == null || s.getFechaBaja() != null) {
				throw new BusinessException("-1", "No existe el dato autocompletado que se quiere asignar al recurso");
			}
			srActual.setAutocompletado(s);
		}
		else {
			modificarAutocompletadosPorDatoDelAutocompletadoPorRecurso(srActual, sr.getAutocompletadosPorDato());			
		}
		
		return srActual;
	}


	@SuppressWarnings("unchecked")
	public List<ServicioAutocompletarPorDato> obtenerAsociacionesAutocompletadoPorDato(
			ServicioPorRecurso sr) throws ApplicationException {
		
		List<ServicioAutocompletarPorDato> asociaciones = null;
		
		try {
			asociaciones = em.createQuery(
					"SELECT   sxd from ServicioAutocompletarPorDato sxd " +
					"WHERE    sxd.servicioPorRecurso = :sxr AND " +
					"         sxd.fechaDesasociacion IS NULL " +
					"ORDER BY sxd.nombreParametro "
			).setParameter("sxr", sr).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		return asociaciones;
	}


	@SuppressWarnings("unchecked")
	public List<ServicioPorRecurso> obtenerAutocompletadosDelRecurso(
			Recurso recurso) throws ApplicationException {
		
		List<ServicioPorRecurso> autocompletados = null;
		
		try {
			autocompletados = em.createQuery(
					"SELECT   sxr from ServicioPorRecurso sxr " +
					"WHERE    sxr.recurso = :recurso AND " +
					"         sxr.fechaBaja IS NULL "
			).setParameter("recurso", recurso).getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		return autocompletados;
	}
	
	
	
}
