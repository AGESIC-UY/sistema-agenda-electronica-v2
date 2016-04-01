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

import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;

public class ConsultaSessionMBean extends SessionCleanerMBean implements RemovableFromSession {

	private Integer reservaPage;
	private Reserva reserva;
	private Disponibilidad disponibilidad;
	
	
	public Disponibilidad getDisponibilidad() {
		return disponibilidad;
	}

	public void setDisponibilidad(Disponibilidad disponibilidad) {
		this.disponibilidad = disponibilidad;
	}

	public ConsultaSessionMBean() {
		super();
		reserva = new Reserva();
		reserva.setEstado(null);
		reserva.setFechaCreacion(null);
		
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	
	public Integer getReservaPage() {
		return reservaPage;
	}

	public void setReservaPage(Integer reservaPage) {
		this.reservaPage = reservaPage;
		
	}

	
	
}
