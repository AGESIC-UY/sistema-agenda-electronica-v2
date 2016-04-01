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

package uy.gub.imm.sae.web.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import uy.gub.imm.sae.web.exceptions.NombreInvalidoException;

public class AgendaNombreValidator extends ValidacionesAgenda implements Validator {

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		String nombre = ((String) value).toUpperCase();
		
		try {
			validarNombre(nombre);
		} catch (NombreInvalidoException nie) {
			FacesMessage message = new FacesMessage(
					nie.getMessage(),
					nie.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}	
	
	/*	try {
			if (!huboError) {
				Agenda agenda = agendarReservasEJB
						.consultarAgendaPorNombre(nombre);
				if (agenda != null) {
					huboError = true;
					FacesMessage message = new FacesMessage(
							NOMBRE_UNICO_ERROR_MSG, NOMBRE_UNICO_ERROR_MSG);
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(message);
				}
			}
		} catch (RolException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			// Si no se encuentra la agenda que es lo esperado, no se hace nada
		}
 	*/
	
	}

}
