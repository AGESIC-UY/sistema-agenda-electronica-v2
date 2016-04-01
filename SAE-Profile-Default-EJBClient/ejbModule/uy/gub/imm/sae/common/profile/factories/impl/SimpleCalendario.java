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

package uy.gub.imm.sae.common.profile.factories.impl;

import java.util.Calendar;
import java.util.Date;

import uy.gub.imm.sae.business.ejb.facade.Calendario;
import uy.gub.imm.sae.entity.Recurso;

public class SimpleCalendario implements Calendario {

	public Boolean esDiaHabil(Date dia, Recurso r) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dia);
		int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
		
		return ((diaSemana!=Calendar.SATURDAY || r.getSabadoEsHabil()) && diaSemana!=Calendar.SUNDAY); 
	}
	
}
