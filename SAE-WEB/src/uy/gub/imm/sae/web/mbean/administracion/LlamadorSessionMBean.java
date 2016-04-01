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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import uy.gub.imm.sae.entity.Llamada;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;
import uy.gub.imm.sae.web.common.TipoMonitor;

public class LlamadorSessionMBean extends SessionCleanerMBean implements RemovableFromSession {

	/** Tipo de monitor configurado para la lista de llamadas */
	private TipoMonitor tipoMonitor;
	private List<Llamada> llamadas;
	private Boolean mostrarDatos;
	private List<Recurso> recursos;

	private Queue<Llamada> llamadasADestacar = new LinkedList<Llamada>();
	
	public TipoMonitor getTipoMonitor() {
		return tipoMonitor;
	}

	public void setTipoMonitor(TipoMonitor tipoMonitor) {
		this.tipoMonitor = tipoMonitor;
	}

	public List<Llamada> getLlamadas() {
		return llamadas;
	}

	public void setLlamadas(List<Llamada> llamadas) {
		this.llamadas = llamadas;
	}

	public Boolean getMostrarDatos() {
		return mostrarDatos;
	}
	public void setMostrarDatos(Boolean mostrarDatos) {
		this.mostrarDatos = mostrarDatos;
	}
	public List<Recurso> getRecursos() {
		return recursos;
	}
	public void setRecursos(List<Recurso> recursos) {
		this.recursos = recursos;
	}

	public Queue<Llamada> getLlamadasADestacar() {
		return llamadasADestacar;
	}

	
}
