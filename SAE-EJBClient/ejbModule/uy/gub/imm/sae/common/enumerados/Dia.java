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

package uy.gub.imm.sae.common.enumerados;

import java.util.Calendar;

public enum Dia {
	LUNES(Calendar.MONDAY, "Lunes", "lun", "L"), MARTES(Calendar.TUESDAY, "Martes", "mie", "M"), MIERCOLES(Calendar.WEDNESDAY, "Miercoles", "mie", "M"), JUEVES(Calendar.THURSDAY, "Jueves", "jue", "J"), VIERNES(Calendar.FRIDAY, "Viernes", "vie", "V"), SABADO(Calendar.SATURDAY, "Sábado", "sab", "S"), DOMINGO(Calendar.SUNDAY, "Domingo", "dom", "D");

	private final int diaDeLaSemana; //Deben ser los mismos valores que utiliza java.util.Calendar
	private final String descripcion;
	private final String descripcionCorta;
	private final String descripcionMasCorta;
	
	private Dia(int diaDeLaSemana, String descripcion, String descripcionCorta, String descripcionMasCorta) {
		this.diaDeLaSemana = diaDeLaSemana;
		this.descripcion = descripcion;
		this.descripcionCorta = descripcionCorta;
		this.descripcionMasCorta = descripcionMasCorta;
	}
	
	public int getDiaDeLaSemana() {
		
		return diaDeLaSemana;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public String getDescripcionCorta() {
		return descripcionCorta;
	}
	public String getDescripcionMasCorta() {
		return descripcionMasCorta;
	}
}
