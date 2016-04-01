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

package uy.gub.imm.sae.common;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class VentanaDeTiempo implements Serializable {
	
	private static final long serialVersionUID = 6411357860430889572L;

	private Date fechaInicial;
	private Date fechaFinal;
	
	public VentanaDeTiempo() {
		
		fechaInicial = null;
		fechaFinal = null;

	}
	
	public VentanaDeTiempo( VentanaDeTiempo v) {
		fechaInicial = v.getFechaInicial();
		fechaFinal = v.getFechaFinal();
	}
	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * Setea la fecha final al valor resultado de sumar cantidadDias a la fecha inicial
	 * Fecha inicial debe estar seteada
	 * @param cantidadDias
	 */
	public void setFechaFinal(Integer cantidadDias) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaInicial);
		cal.add(Calendar.DAY_OF_MONTH, cantidadDias);
		fechaFinal = cal.getTime();
	}

}

