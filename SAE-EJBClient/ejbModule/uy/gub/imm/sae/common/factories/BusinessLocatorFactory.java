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

package uy.gub.imm.sae.common.factories;

import uy.gub.imm.sae.common.SAEProfile;
import uy.gub.imm.sae.exception.ApplicationException;

public class BusinessLocatorFactory {

	private static final String LOCATOR_CONTEXTO_AUTENTICADO_BACKEND_KEY="locator.contexto.autenticado.backend.impl";
	private static final String LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_KEY="locator.contexto.no.autenticado.backend.impl";

	private static final String LOCATOR_CONTEXTO_NO_AUTENTICADO_FRONTEND_KEY="locator.contexto.frontend.impl";
	
	private static BusinessLocator buildBusinessLocator(String locatorClassName) throws ApplicationException {
		
		try {
			Class<?> cbl = Class.forName(locatorClassName);
			return (BusinessLocator)cbl.newInstance();
		} catch (ClassNotFoundException e) {
			throw new ApplicationException("-1", String.format("No se puede encontrar la clase %s", locatorClassName), e);
		} catch (InstantiationException e) {
			throw new ApplicationException("-1", String.format("No se puede instanciar la clase %s", locatorClassName), e);
		} catch (IllegalAccessException e) {
			throw new ApplicationException("-1", String.format("No se puede acceder al constructor de la clase %s", locatorClassName), e);
		}
		
	}
	
	public static BusinessLocator getLocatorContextoAutenticado() throws ApplicationException {
		String locatorClassName = SAEProfile.getInstance().getProperties().getProperty(LOCATOR_CONTEXTO_AUTENTICADO_BACKEND_KEY);
		return buildBusinessLocator(locatorClassName);			
	}
	
	public static BusinessLocator getLocatorContextoNoAutenticado() throws ApplicationException {
		String locatorClassName = SAEProfile.getInstance().getProperties().getProperty(
				(SAEProfile.getInstance().getEnvironment().equals(SAEProfile.Escenario.BACKEND) ?
						LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_KEY
						:
						LOCATOR_CONTEXTO_NO_AUTENTICADO_FRONTEND_KEY
				));
		return buildBusinessLocator(locatorClassName);			
	}
	
}
