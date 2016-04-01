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

import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.exceptions.NombreInvalidoException;

public class ValidacionesAgenda extends BaseMBean {

	private static final String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTVUWXYZ0123456789_";
	private static final String NUMEROS="1234567890";
	protected static final String CARACTER_INVALIDO_ERROR_MSG = "El campo 'Nombre' solo puede contener letras sin tildes, números y '_'";
	protected static final String PRIMER_CARACTER_INVALIDO_ERROR_MSG = "El campo 'Nombre' no puede comenzar con un número";
		
	protected static final String NOMBRE_UNICO_ERROR_MSG = "El valor del campo 'Nombre' debe ser único.";

	
	public boolean validarNombre(String nombre) throws NombreInvalidoException{
		
		nombre = nombre.toUpperCase();
		String caracValidos = VALID_CHARS;
		boolean nombreValido = true;

		
		for (int i = 0; (i < nombre.length() && nombreValido); i++) {
			char caracter = nombre.charAt(i);

			// Se chequea que el primer caracter no sea un numero
			if (i==0 && NUMEROS.indexOf(caracter) != -1){
				nombreValido = false;
				throw new NombreInvalidoException(PRIMER_CARACTER_INVALIDO_ERROR_MSG);
			}
			
			// Se chequea si los caracterss son validos
			if (caracValidos.indexOf(caracter) == -1) {
				nombreValido = false;
				throw new NombreInvalidoException(CARACTER_INVALIDO_ERROR_MSG);
			}
		}

		return nombreValido;
		
	}
}
