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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import uy.gub.imm.sae.entity.Plantilla;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR"})
public class PlantillasBean implements PlantillasLocal, PlantillasRemote{
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;

	/**
	 * Crea la plantilla <b>p</b> asociándola al recurso <b>r</b>
	 * Tambien se le setea  a Anios, DiasDeLaSemana, DiasDelMes y Meses
	 * la plantilla  
	 * Se permite setear todos sus atributos menos la fechaBaja. 
	 * Controla:
	 * 	horaInicio, horaFin y frecuencia son obligatorios
	 *	horaInicio < horaFin
	 *	frecuencia > 0
	 *	cupo > 0
	 * Retorna la plantilla con su identificador interno.
	 * 
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso <b>r</b>
	 * Roles permitidos: Planificador
	 */
	public Plantilla crearPlantilla(Recurso r, Plantilla p) throws UserException {
		// TODO Auto-generated method stub
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		
		if (recursoActual == null) {
			throw new UserException("AE10022","No existe el recurso: " + r.getId().toString());
		}
		
		//No se puede agregar una plantilla a un recurso con fecha de baja

		if (recursoActual.getFechaBaja() != null) {
			throw new UserException("AE10024","El recurso esta dado de baja");
		}

		p.setRecurso(r);

		if (p.getFechaBaja() != null){
			throw new UserException("AE10023","No se puede ingresar fecha de baja");
		}

		if (p.getHoraInicio() == null){
			throw new UserException("AE10071","La hora de inicio no puede ser nula");
		}
		
		if (p.getHoraFin() == null){
			throw new UserException("AE10072","La hora de fin no puede ser nulo");			
		}
		
		if (p.getFrecuencia() == null){
			throw new UserException("AE10073","La frecuencia no puede ser nula o cero");
		}

		if (p.getFrecuencia().intValue() == 0){
			throw new UserException("AE10073","La frecuencia no puede ser nula o cero");
		}

		if (p.getHoraInicio().compareTo(p.getHoraFin()) > 0 ){
			throw new UserException("AE10074","La hora de inicio no puede mayor que la hora de fin");
		}

		if (p.getFrecuencia() < 0){
			throw new UserException("AE10073","La frecuencia debe ser mayor a 0");
		}
		if (p.getCupo() < 0){
			throw new UserException("AE10075","El cupo debe ser mayor a 0");
		}
		
		entityManager.persist(p);
		return p;
	}

	/**
	 * Se realiza la baja lógica de la plantilla (se setea fechaBaja con la fecha actual del sistema).
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada.
	 * Roles permitidos: Planificador
	 */
	public void eliminarPlantilla(Plantilla p) throws UserException, ApplicationException {
		// TODO Auto-generated method stub
		Plantilla plantilla = (Plantilla) entityManager.find(Plantilla.class, p.getId());
		
		if (plantilla == null) {
			throw new UserException("AE10042","No existe la plantilla que se quiere eliminar: " + p.getId().toString());
		}
		plantilla.setFechaBaja(new Date());
	}
	

	/**
	 * Modificación de una plantilla <b>p</b>.
	 * Se permite modificar todos sus atributos menos la fechaBaja.
	 * Controla: Los mismos controles que se aplican en crearPlantilla
	 * 
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso asociado a la plantilla <b>p</b>
	 * Roles permitidos: Planificador
	 */
	public void modificarPlantilla(Plantilla p) throws UserException, ApplicationException {
		// TODO Auto-generated method stub
		Plantilla plantillaActual = (Plantilla) entityManager.find(Plantilla.class, p.getId());
		
		if (plantillaActual == null) {
			throw new UserException("AE10040","No existe la plantilla que se quiere modificar: " + p.getId().toString());
		}
			
		//Se controla que horaInicio, horaFin y frecuencia no sean null
		if (p.getHoraInicio() == null || p.getHoraFin() == null || p.getFrecuencia() == null ) {
			throw new UserException("AE10041","HoraInicio, HoraFin y Frecuencia deben tener valores: ");
		}
		
    	plantillaActual.setHoraInicio(p.getHoraInicio());
    	plantillaActual.setHoraFin(p.getHoraFin());
    	plantillaActual.setFrecuencia(p.getFrecuencia());
    	plantillaActual.setCupo(p.getCupo());
    	plantillaActual.setGeneracionAutomatica(p.getGeneracionAutomatica());
	}

}
