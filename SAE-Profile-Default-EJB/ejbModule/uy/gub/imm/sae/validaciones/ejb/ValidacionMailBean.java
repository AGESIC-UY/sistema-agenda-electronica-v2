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

package uy.gub.imm.sae.validaciones.ejb;

import java.util.Map;

import javax.ejb.Stateless;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import uy.gub.imm.sae.validaciones.business.ejb.ResultadoValidacion;
import uy.gub.imm.sae.validaciones.business.ejb.ValidadorReservaLocal;
import uy.gub.imm.sae.validaciones.business.ejb.ValidadorReservaRemote;
import uy.gub.imm.sae.validaciones.business.ejb.exception.InvalidParametersException;
import uy.gub.imm.sae.validaciones.business.ejb.exception.UnexpectedValidationException;

@Stateless
public class ValidacionMailBean implements
		ValidadorReservaLocal, ValidadorReservaRemote {

	
	/**
	 * 	^							#start of the line
  	 *		[_A-Za-z0-9-\\+]+		#  must start with string in the bracket [ ], must contains one or more (+)
  	 *		(						#   start of group #1
     *			\\.[_A-Za-z0-9-]+	#     follow by a dot "." and string in the bracket [ ], must contains one or more (+)
  	 *		)*						#   end of group #1, this group is optional (*)
     *			@					#     must contains a "@" symbol
     * 			[A-Za-z0-9-]+      	#       follow by string in the bracket [ ], must contains one or more (+)
     * 			(					#         start of group #2 - first level TLD checking
     * 				\\.[A-Za-z0-9]+ #           follow by a dot "." and string in the bracket [ ], must contains one or more (+)
     *			)*					#         end of group #2, this group is optional (*)
     *			(					#         start of group #3 - second level TLD checking
     *				\\.[A-Za-z]{2,} #           follow by a dot "." and string in the bracket [ ], with minimum length of 2
     * 			)					#         end of group #3
	 *	$							#end of the line
	 */
	
	
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	//Parametros
	private final String PARAM_MAIL = "MAIL";
	private final String PARAM_NOTIFICAR = "NOTIFICAR";
	
	//Nombre de la validacion
	private final String NOMBRE_VALIDACION_MAIL = "VALIDACION_MAIL";
	
	//Errores
	private final String CODIGO_ERROR_MAIL_INVALIDO = "-1";
	private final String MENSAJE_ERROR_MAIL_INVALIDO = "Dirección de correo inválida, verifque que el formato sea algo parecido a usuario@dominio.com";

	public ResultadoValidacion validarDatosReserva(String nombreValidacion, Map<String, Object> params) 
		throws UnexpectedValidationException, InvalidParametersException {

		ResultadoValidacion res = new ResultadoValidacion();
		
		try {
		
			if(params == null){
				throw new InvalidParametersException("Falta parametro <params>");
			} else if (nombreValidacion == null){
				throw new InvalidParametersException("Falta parametro <nombreValidacion>");
			} else if (nombreValidacion.equals(NOMBRE_VALIDACION_MAIL)){				
				// obtenemos parametros
				String mail = (String)params.get(PARAM_MAIL);
				Boolean notificar = (params.get(PARAM_NOTIFICAR) == null ? null : (Boolean)params.get(PARAM_NOTIFICAR));
				
				// invocamos validacion
				if (mail == null && notificar != null && notificar) {
					res.addError(CODIGO_ERROR_MAIL_INVALIDO, "Para recibir notificación debe ingregar una dirección de correo válida.");
				}
				else if (mail != null) {
					//Valido el mail
					
					if (!mail.matches(EMAIL_PATTERN)) {
						res.addError(CODIGO_ERROR_MAIL_INVALIDO, MENSAJE_ERROR_MAIL_INVALIDO);
					}
					else {
						//Por las dudas, pero con el filtro anterior este control es redundante.
				        try {
							new InternetAddress(mail);
						} catch (AddressException e) {
							res.addError(CODIGO_ERROR_MAIL_INVALIDO, MENSAJE_ERROR_MAIL_INVALIDO);
						}
					}
				}
			} else {
				throw new InvalidParametersException("No existe una validacion con nombreValidacion = " + nombreValidacion);
			}
		}catch(InvalidParametersException ipe) {
			throw ipe;
		} catch (Exception e) {
			throw new UnexpectedValidationException("Error inesperado catcheado como Exception", e);
		}

		return res;
	}

}
