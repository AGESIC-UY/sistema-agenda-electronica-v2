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

package uy.gub.imm.sae.business.common.factories;

import uy.gub.imm.sae.business.ejb.facade.Calendario;
import uy.gub.imm.sae.common.SAEProfile;
import uy.gub.imm.sae.exception.ApplicationException;

public class CalendarioFactory {

	private static final String CALENDARIO_IMPL_KEY="calendario-impl";
	
	private static Calendario buildCalendario(String locatorClassName) throws ApplicationException {
		
	try {
			Class<?> cbl = Class.forName(locatorClassName);
			return (Calendario)cbl.newInstance();
		} catch (ClassNotFoundException e) {
			throw new ApplicationException("-1", String.format("No se puede encontrar la clase %s", locatorClassName), e);
		} catch (InstantiationException e) {
			throw new ApplicationException("-1", String.format("No se puede instanciar la clase %s", locatorClassName), e);
		} catch (IllegalAccessException e) {
			throw new ApplicationException("-1", String.format("No se puede acceder al constructor de la clase %s", locatorClassName), e);
		}
		
	}
	
	public static Calendario getCalendario() throws ApplicationException {
		String locatorClassName = SAEProfile.getInstance().getProperties().getProperty(CALENDARIO_IMPL_KEY);
		return buildCalendario(locatorClassName);			
	}
	
}
