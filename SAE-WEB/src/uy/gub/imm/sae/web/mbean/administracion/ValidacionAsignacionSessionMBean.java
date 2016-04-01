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

package uy.gub.imm.sae.web.mbean.administracion;

import java.util.List;

import javax.annotation.PostConstruct;

import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ParametroValidacion;
import uy.gub.imm.sae.entity.Validacion;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;

public class ValidacionAsignacionSessionMBean extends SessionCleanerMBean implements RemovableFromSession {

	private Boolean modoCreacion;
	private Boolean modoEdicion;
	
	private Integer validacionesDelRecursoTablePageIndex;
	private Integer validacionesPorDatoTablePageIndex;

 	private List<Validacion> validaciones;
 	
	private ValidacionPorRecurso validacionDelRecurso;
	private List<DatoASolicitar> datosASolicitarDelRecurso;
	private List<DatoASolicitar> datosASolicitarDelRecursoCopia;
	private List<String> nombresParametrosValidacion;
	private List<ParametroValidacion> parametrosValidacion;
	
	//Esta coleccion deben estar sincronizadas con la respectiva de validacionDelRecurso, es responsabilidad del ManagedBean que las maneja.
	//private Map<Integer, ValidacionPorDato> asignacionesMap;
	
	
	@PostConstruct
	public void init() {
		modoCreacion = false;
		modoEdicion = false;
		
		validacionesDelRecursoTablePageIndex = 1;
		validacionesPorDatoTablePageIndex = 1;
	}
	
	public Integer getValidacionesDelRecursoTablePageIndex() {
		return validacionesDelRecursoTablePageIndex;
	}

	public void setValidacionesDelRecursoTablePageIndex(
			Integer validacionesDelRecursoTablePageIndex) {
		this.validacionesDelRecursoTablePageIndex = validacionesDelRecursoTablePageIndex;
	}

	public Integer getValidacionesPorDatoTablePageIndex() {
		return validacionesPorDatoTablePageIndex;
	}

	public void setValidacionesPorDatoTablePageIndex(
			Integer validacionesPorDatoTablePageIndex) {
		this.validacionesPorDatoTablePageIndex = validacionesPorDatoTablePageIndex;
	}

	public Boolean getModoCreacion() {
		return modoCreacion;
	}

	public void setModoCreacion(Boolean modoCreacion) {
		this.modoCreacion = modoCreacion;
	}

	public Boolean getModoEdicion() {
		return modoEdicion;
	}

	public void setModoEdicion(Boolean modoEdicion) {
		this.modoEdicion = modoEdicion;
	}

	public ValidacionPorRecurso getValidacionDelRecurso() {
		return validacionDelRecurso;
	}

	public void setValidacionDelRecurso(ValidacionPorRecurso validacionDelRecurso) {
		this.validacionDelRecurso = validacionDelRecurso;
	}

	public List<DatoASolicitar> getDatosASolicitarDelRecurso() {
		return datosASolicitarDelRecurso;
	}

	public void setDatosASolicitarDelRecurso(
			List<DatoASolicitar> datosASolicitarDelRecurso) {
		this.datosASolicitarDelRecurso = datosASolicitarDelRecurso;
	}

	public List<Validacion> getValidaciones() {
		return validaciones;
	}
	
	
	public List<DatoASolicitar> getDatosASolicitarDelRecursoCopia() {
		return datosASolicitarDelRecursoCopia;
	}

	public void setDatosASolicitarDelRecursoCopia(
			List<DatoASolicitar> datosASolicitarDelRecursoCopia) {
		this.datosASolicitarDelRecursoCopia = datosASolicitarDelRecursoCopia;
	}

	public List<String> getNombresParametrosValidacion() {
		return nombresParametrosValidacion;
	}

	public void setNombresParametrosValidacion(
			List<String> nombresParametrosValidacion) {
		this.nombresParametrosValidacion = nombresParametrosValidacion;
	}

	public List<ParametroValidacion> getParametrosValidacion() {
		return parametrosValidacion;
	}

	public void setParametrosValidacion(
			List<ParametroValidacion> parametrosValidacion) {
		this.parametrosValidacion = parametrosValidacion;
	}

	public void setValidaciones(List<Validacion> validaciones) {
		this.validaciones = validaciones;
	}

/*
	public Map<Integer, ValidacionPorDato> getAsignacionesMap() {
		return asignacionesMap;
	}

	public void setAsignacionesMap(Map<Integer, ValidacionPorDato> asignacionesMap) {
		this.asignacionesMap = asignacionesMap;
	}
*/

}
