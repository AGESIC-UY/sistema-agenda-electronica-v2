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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValidacionPorDato;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed("RA_AE_ADMINISTRADOR")
public class AgendasBean implements AgendasLocal,  AgendasRemote{

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	/**
	 * Crea la agenda <b>a</b> en el sistema.
	 * Controla la unicidad del nombre de la agenda entre todas las agendas vivas (fechaBaja == null).
	 * Retorna la agenda con su identificador interno.
	 * Roles permitidos: Administrador
	 * @throws UserException 
	 * @throws ApplicationException 
	 * @throws BusinessException 
	 */
	public Agenda crearAgenda(Agenda a) throws UserException, ApplicationException, BusinessException {
		//Se controla que no exista otra agenda con el mismo nombre
		if (existeAgendaPorNombre(a) ) {
				throw new UserException("ya_existe_una_agenda_con_el_nombre_especificado");
		}
		entityManager.persist(a);

		return a;
	}

	/**
	 * Se realiza la baja logica de la agenda (se setea fechaBaja con la fecha actual del sistema).
	 * Controla que no existan recursos vivos para esta esta agenda, si es asi no se la da de baja.	 * 
	 * Controla que no existan reservas vivas para esta esta agenda, si es asi no se la da de baja.
	 * Roles permitidos: Administrador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	@SuppressWarnings({"unchecked" })
	public void eliminarAgenda(Agenda a) throws UserException, ApplicationException {
		Agenda agenda = (Agenda) entityManager.find(Agenda.class, a.getId());
		
		if (agenda == null) {
			throw new UserException("no_se_encuentra_la_agenda_especificada");
		}
		
		//Se controla que no existan reservas vivas para la agenda.
		 if (hayReservasVivas(agenda)){
			throw new UserException("no_se_puede_eliminar_la_agenda_porque_hay_reservas_vivas");
		}
		
		//elimino recursos
		 List<Recurso> recursos = (List<Recurso>)entityManager
			.createQuery("SELECT r FROM Recurso r WHERE r.agenda = :agenda AND r.fechaBaja IS NULL")
			.setParameter("agenda", a)
			.getResultList(); 
		 
		for (Recurso recurso : recursos) {
			recursosEJB.eliminarRecurso(recurso);
		}
		agenda.setFechaBaja(new Date());
	}

	/**
	 * Se realiza la modificacion de la agenda <b>a</b>.
	 * Controla la unicidad del nombre de la agenda entre todas las agendas vivas (fechaBaja == null).
	 * Roles permitidos: Administrador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public void modificarAgenda(Agenda a) throws UserException, ApplicationException {
		Agenda agendaActual = (Agenda) entityManager.find(Agenda.class, a.getId());
		if (agendaActual == null) {
			throw new UserException("no_se_encuentra_la_agenda_especificada");
		}
		//Se controla que no exista otra agenda viva con el mismo nombre
		if (existeAgendaPorNombre(a) ) {
			throw new UserException("ya_existe_una_agenda_con_el_nombre_especificado");
		}
		
		if (agendaActual.getFechaBaja()!= null) {
			throw new UserException("AE10070","La agenda esta dada de baja no se puede modificar");
		}
		
		//Se controla que la descripción no sea nula
		if (a.getDescripcion() == null || a.getDescripcion().equals("")){
			throw new UserException("AE10001","No se puede dar de alta una agenda sin descripción");			
		}
		//Se controla que no exista otra agenda con la misma descripción
		if (existeAgendaPorDescripcion(a) ) {
				throw new UserException("AE10006","Ya existe una agenda con esa descripcion: "+ a.getDescripcion());
		}

  	agendaActual.setNombre(a.getNombre());
  	agendaActual.setDescripcion(a.getDescripcion());
  	agendaActual.setTimezone(a.getTimezone());
  	agendaActual.setIdiomas(a.getIdiomas());
  	agendaActual.setTramiteId(a.getTramiteId());
  	agendaActual.setTramiteCodigo(a.getTramiteCodigo());
  	agendaActual.setConCda(a.getConCda());
  	agendaActual.setConTrazabilidad(a.getConTrazabilidad());
  	agendaActual.setPublicarNovedades(a.getPublicarNovedades());
    	
  	for(TextoAgenda viejo : agendaActual.getTextosAgenda().values()) {
  		entityManager.remove(viejo);
  	}
  	agendaActual.setTextosAgenda(new HashMap<String, TextoAgenda>());
  	if(a.getTextosAgenda() != null) {
  		for(String idioma : a.getTextosAgenda().keySet()) {
	  		TextoAgenda viejo = a.getTextosAgenda().get(idioma);
	  		TextoAgenda nuevo = new TextoAgenda();
  			nuevo.setIdioma(viejo.getIdioma());
  			nuevo.setPorDefecto(viejo.isPorDefecto());
	  		nuevo.setTextoPaso1(viejo.getTextoPaso1());
	  		nuevo.setTextoPaso2(viejo.getTextoPaso2());
	  		nuevo.setTextoPaso3(viejo.getTextoPaso3());
	  		nuevo.setTextoSelecRecurso(viejo.getTextoSelecRecurso());
	  		nuevo.setTextoTicketConf(viejo.getTextoTicketConf());
	  		nuevo.setTextoCorreoConf(viejo.getTextoCorreoConf());
	  		nuevo.setTextoCorreoCanc(viejo.getTextoCorreoCanc());
	  		nuevo.setAgenda(agendaActual);
	  		agendaActual.getTextosAgenda().put(idioma, nuevo);
	  		entityManager.persist(nuevo);
  		}
  	}
  	
  	entityManager.merge(agendaActual);
  	
	}
	
	public void copiarAgenda(Agenda a) throws BusinessException, ApplicationException, UserException
	{
		a = entityManager.find(Agenda.class, a.getId());
		if (a == null) {
			throw new BusinessException("-1", "No se encuentra la agenda indicado");
		}
		//1- Creo una nueva Agenda y copio los atributos de a.
		Agenda aCopia = new Agenda(a);
		aCopia.setId(null);
		aCopia.setIdiomas(a.getIdiomas());
		aCopia.setTimezone(a.getTimezone());
		aCopia.setTramiteId(a.getTramiteId());
		aCopia.setTramiteCodigo(a.getTramiteCodigo());
		aCopia.setConCda(a.getConCda());
		aCopia.setConTrazabilidad(a.getConTrazabilidad());
		aCopia.setPublicarNovedades(a.getPublicarNovedades());
		
		int contador = 0;
		do{
			contador++;
			aCopia.setNombre("Copia "+contador+" de "+a.getNombre());
			aCopia.setDescripcion("Copia "+contador+" de "+a.getDescripcion());
		}while(existeAgendaPorNombre(aCopia));
		
		aCopia.setTextosAgenda(new HashMap<String, TextoAgenda>());
  	if(a.getTextosAgenda() != null) {
  		for(String idioma : a.getTextosAgenda().keySet()) {
	  		TextoAgenda viejo = a.getTextosAgenda().get(idioma);
	  		TextoAgenda nuevo = new TextoAgenda();
	  		nuevo.setTextoPaso1(viejo.getTextoPaso1());
	  		nuevo.setTextoPaso2(viejo.getTextoPaso2());
	  		nuevo.setTextoPaso3(viejo.getTextoPaso3());
	  		nuevo.setTextoSelecRecurso(viejo.getTextoSelecRecurso());
	  		nuevo.setTextoTicketConf(viejo.getTextoTicketConf());
	  		nuevo.setTextoCorreoConf(viejo.getTextoCorreoConf());
	  		nuevo.setTextoCorreoCanc(viejo.getTextoCorreoCanc());
	  		aCopia.getTextosAgenda().put(idioma, nuevo);
  		}
  	}
		
		entityManager.persist(aCopia);
		List<Recurso> recursos = a.getRecursos();
		for (Recurso r : recursos) {
			if(r.getFechaBaja() == null)
			{
				copiarRecursoParaAgenda(aCopia,r, r.getNombre(), r.getDescripcion());
			}
		}
	}
	
private void copiarRecursoParaAgenda(Agenda acopia, Recurso r, String nombre, String descripcion) throws BusinessException, ApplicationException, UserException {
		
		if (r == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		
		//1- Creo un nuevo Recurso y copio los atributos de r.
		//2- Creo un nuevo TextoRecurso y copio los atributos de r.textoRecurso.
		//3- Para cada ValidacionPorRecurso de r, creo una copia y la asigno al nuevo recurso
		//4- Para cada DatoDelRecurso de r, creo una copia y la asigno al nuevo recurso.
		//5- Para cada AgrupacionDato de r:
		//		creo una copia y la asigno al recurso nuevo.
		//
		//5.1  	Para cada DatoASolicitar de la AgrupacionDato:
		//			creo una copia y la asigno al recurso nuevo y a la AgrupacionDato nueva.
		//
		//5.2		Para cada ValorPosible del DatoASolicitar:
		//				creo una copia y la asigno al datoASolicitar nuevo.
		//
		//5.3		Para cada ValidacionPorDato del DatoASolicitar:
		//				Creo una ValidacionPorDato nueva y le asigno:
		//				el DatoASolicitar nuevo y
		//              la ValidacionPorRecurso nueva que se corresponde a la ValidacionPorRecurso original
		//              (Para esto usar un Map<ValidacionPorRecursoOrig, ValidacionPorRecursoCopia>

		
		//1
		Recurso rCopia = new Recurso(r);
		rCopia.setId(null);
		rCopia.setAgenda(acopia);
		rCopia.setNombre(nombre);
		rCopia.setDescripcion(descripcion);
		//Controla la unicidad del nombre del recurso nuevo entre todos los recursos vivos (fechaBaja == null) para la misma agenda.
		
		entityManager.persist(rCopia);
		
		//2 
		rCopia.setTextosRecurso(new HashMap<String, TextoRecurso>());
  	if(r.getTextosRecurso() != null) {
  		for(String idioma : r.getTextosRecurso().keySet()) {
	  		TextoRecurso viejo = r.getTextosRecurso().get(idioma);
	  		TextoRecurso trCopia = new TextoRecurso();

	  		trCopia.setRecurso(viejo.getRecurso());
	  		trCopia.setIdioma(viejo.getIdioma());
	  		trCopia.setTextoPaso2(viejo.getTextoPaso2());
	  		trCopia.setTextoPaso3(viejo.getTextoPaso3());
	  		trCopia.setTituloCiudadanoEnLlamador(viejo.getTituloCiudadanoEnLlamador());
	  		trCopia.setTituloPuestoEnLlamador(viejo.getTituloPuestoEnLlamador());
	  		trCopia.setTicketEtiquetaUno(viejo.getTicketEtiquetaUno());
	  		trCopia.setTicketEtiquetaDos(viejo.getTicketEtiquetaDos());
	  		trCopia.setValorEtiquetaUno(viejo.getValorEtiquetaUno());
	  		trCopia.setValorEtiquetaDos(viejo.getValorEtiquetaDos());
	  		rCopia.getTextosRecurso().put(idioma, trCopia);
	  		entityManager.persist(trCopia);
  		}
  	}

		//3
		Map<ValidacionPorRecurso, ValidacionPorRecurso> validacionesDelRecurso = new HashMap<ValidacionPorRecurso, ValidacionPorRecurso>();
		for (ValidacionPorRecurso vxr : r.getValidacionesPorRecurso()) {
			if (vxr.getFechaBaja() == null) {
				ValidacionPorRecurso vxrCopia = new ValidacionPorRecurso();
				vxrCopia.setId(null);
				vxrCopia.setOrdenEjecucion(vxr.getOrdenEjecucion());
				vxrCopia.setRecurso(rCopia);
				vxrCopia.setValidacion(vxr.getValidacion());
				validacionesDelRecurso.put(vxr, vxrCopia);
				entityManager.persist(vxrCopia);
			}
		}

		//4
		for (DatoDelRecurso ddr : r.getDatoDelRecurso()) {
			
			DatoDelRecurso ddrCopia = new DatoDelRecurso();
			ddrCopia.setOrden(ddr.getOrden());
			ddrCopia.setEtiqueta(ddr.getEtiqueta());
			ddrCopia.setValor(ddr.getValor());
			ddrCopia.setRecurso(rCopia);
			rCopia.getDatoDelRecurso().add(ddrCopia);
			entityManager.persist(ddrCopia);
		}
		
		//5
		for (AgrupacionDato agrup : r.getAgrupacionDatos()) {
			
			if (agrup.getFechaBaja() == null) {
				AgrupacionDato agrupCopia = new AgrupacionDato(agrup);
				agrupCopia.setId(null);
				agrupCopia.setRecurso(rCopia);
				entityManager.persist(agrupCopia);
				
				//5.1
				for (DatoASolicitar campo : agrup.getDatosASolicitar()) {
					
					if (campo.getFechaBaja() == null) {
						DatoASolicitar campoCopia = new DatoASolicitar(campo);
						campoCopia.setId(null);
						campoCopia.setRecurso(rCopia);
						campoCopia.setAgrupacionDato(agrupCopia);
						
						agrupCopia.getDatosASolicitar().add(campoCopia);
						rCopia.getDatoASolicitar().add(campoCopia);
						entityManager.persist(campoCopia);
					
						//5.2
						for (ValorPosible vp : campo.getValoresPosibles()) {
							if (vp.getFechaHasta() == null || vp.getFechaHasta().after(new Date())) {
								ValorPosible vpCopia = new ValorPosible(vp);
								vpCopia.setId(null);
								vpCopia.setDato(campoCopia);
								entityManager.persist(vpCopia);
							}
						}
						
						//5.3
						for (ValidacionPorDato vxd : campo.getValidacionesPorDato()) {
							if (vxd.getFechaDesasociacion() == null) {
								ValidacionPorDato vxdCopia = new ValidacionPorDato();
								vxdCopia.setNombreParametro(vxd.getNombreParametro());
								vxdCopia.setDatoASolicitar(campoCopia);
								ValidacionPorRecurso vxrCopia = validacionesDelRecurso.get(vxd.getValidacionPorRecurso());
								vxdCopia.setValidacionPorRecurso(vxrCopia);
								entityManager.persist(vxdCopia);
							}
						}
					}
					
				}//Fin 5.1

			}
			
		}//Fin 5
		//6
		List<Disponibilidad> disponibilidades = r.getDisponibilidades();
		for (Disponibilidad disponibilidad : disponibilidades) {
			Disponibilidad disponibilidadCopia = new Disponibilidad();
			disponibilidadCopia.setCupo(disponibilidad.getCupo());
			disponibilidadCopia.setFecha(disponibilidad.getFecha());
			disponibilidadCopia.setFechaBaja(disponibilidad.getFechaBaja());
			disponibilidadCopia.setHoraFin(disponibilidad.getHoraFin());
			disponibilidadCopia.setHoraInicio(disponibilidad.getHoraInicio());
			disponibilidadCopia.setId(null);
			disponibilidadCopia.setNumerador(disponibilidad.getNumerador());
			disponibilidadCopia.setPlantilla(disponibilidad.getPlantilla());
			disponibilidadCopia.setRecurso(rCopia);
			disponibilidadCopia.setVersion(1);
			entityManager.persist(disponibilidadCopia);
		}
	}
	
	private boolean hayReservasVivas(Agenda a) throws ApplicationException{
		try {Long cant = (Long) entityManager
					.createQuery("SELECT count(r) FROM Disponibilidad d JOIN d.reservas r " +
							"WHERE d.recurso.agenda = :agenda " +
							"  AND d.fecha >= :fecha" +
							"  AND d.horaFin >= :hora" +
							"  AND r.estado IN ('R','P')")
					.setParameter("agenda", a)
					.setParameter("fecha", new Date())
					.setParameter("hora", new Date())
					.getSingleResult();

		return (cant > 0);
		}catch (Exception e){
			throw new ApplicationException(e);
		}
	}
	
	public boolean existeAgendaPorNombre(Agenda a) throws ApplicationException{
		try{
		Long cant = (Long) entityManager
								.createQuery("SELECT count(a) from Agenda a " +
										"WHERE upper(a.nombre) = upper(:nombre) " +
										"and (a.id <> :id or :id is null) " +
										"AND a.fechaBaja IS NULL")
								.setParameter("nombre", a.getNombre())
								.setParameter("id", a.getId())
								.getSingleResult();
		
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}

	private Boolean existeAgendaPorDescripcion(Agenda a) throws ApplicationException{
		try{
		Long cant = (Long) entityManager
								.createQuery("SELECT count(a) from Agenda a " +
										"WHERE upper(a.descripcion) = upper(:descripcion) " +
										"and (a.id <> :id or :id is null) " +
										"AND a.fechaBaja IS NULL")
								.setParameter("descripcion", a.getDescripcion())
								.setParameter("id", a.getId())
								.getSingleResult();
		
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Agenda> consultarAgendas() throws ApplicationException {

		//TODO chequear permisos
		
		List<Agenda> agendas = null;
		try {
			agendas = (List<Agenda>) entityManager.createQuery("from Agenda a where a.fechaBaja is null order by a.nombre")
					.getResultList();
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
		
		return agendas;
	}

}
