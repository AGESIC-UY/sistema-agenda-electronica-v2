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

package uy.gub.imm.sae.validaciones.ejb.ci;

import java.util.Map;

import javax.ejb.Stateless;

import uy.gub.imm.sae.validaciones.business.ejb.ResultadoValidacion;
import uy.gub.imm.sae.validaciones.business.ejb.ValidadorReservaLocal;
import uy.gub.imm.sae.validaciones.business.ejb.ValidadorReservaRemote;
import uy.gub.imm.sae.validaciones.business.ejb.exception.InvalidParametersException;
import uy.gub.imm.sae.validaciones.business.ejb.exception.UnexpectedValidationException;

@Stateless
public class ValidadorDatosPersonaBean implements
		ValidadorReservaLocal, ValidadorReservaRemote {

	// params keys
	private final String CI_KEY = "CI";
	private final String DIGITO_KEY = "DIGITO_CI";
	
	// nombres validaciones
	private final String VAL_DIGITO_VERIF_CI = "DIGITO_VERIFICADOR_CEDULA_IDENTIDAD";
	
	// errores
	private final String CODIGO_ERROR_DIGITO_VERIF_NO_CORRRESP_CI = "P002";
	private final String MENSAJE_ERROR_DIGITO_VERIF_NO_CORRRESP_CI = "Digito verificador no se corresponde con la cedula de identidad indicada.";
	
	/**
	 * @param nombreValidacion identifica la validacion que se quiere invocar, de las implementadas por este bean. Los valores posibles son: </br>
	 * 	&nbsp&nbsp&nbsp- DIGITO_VERIFICADOR_CEDULA_IDENTIDAD </br>
	 * @param params en el map de parametros se espera encontrar: </br>
	 * 	&nbsp&nbsp&nbsp- Si nombreValidacion = DIGITO_VERIFICADOR_CEDULA_IDENTIDAD</br>
	 * 	&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp- la cedula de identidad (sin digito verificador) a validar (Integer) con key CI</br>
	 * 	&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp- el digito verificador de la cedula (Integer) con key DIGITO_CI</br>
	 * @exception UnexpectedValidationException en caso de que se tenga algun error inesperado, por ejemplo, errores en la conexion a la base de datos
	 * @exception InvalidParametersException en caso de que no se encuentren los parametros necesarios para la ejecucion de la validacion invocada
	 * @return resultado de validacion OK si la validacion invocada resulta satisfactoria y errores o warnings en caso contrario.     
	 */
	public ResultadoValidacion validarDatosReserva(String nombreValidacion, Map<String, Object> params) 
		throws UnexpectedValidationException, InvalidParametersException {

		ResultadoValidacion res = null;
		
		try {
		
			if(params == null){
				throw new InvalidParametersException("Falta parametro <params>");
			} else if (nombreValidacion == null){
				throw new InvalidParametersException("Falta parametro <nombreValidacion>");
			} else if (nombreValidacion.equals(VAL_DIGITO_VERIF_CI)){				
				// obtenemos parametros
				Integer ci = (Integer)params.get((String)CI_KEY);
				Integer digito = (Integer)params.get((String)DIGITO_KEY);
				
				// validamos que el parametro CI haya sido pasado
				if(ci == null) {
					throw new InvalidParametersException("Falta parametro " + CI_KEY);
				}
				
				// validamos que el parametro DIGITO haya sido pasado
				if(digito == null) {
					throw new InvalidParametersException("Falta parametro " + DIGITO_KEY);
				}
				
				// invocamos validacion
				res = validarDigitoVerificadorCedulaIdentidad(ci, digito);
			} else {
				throw new InvalidParametersException("No existe una validacion con nombreValidacion = " + nombreValidacion);
			}
		} catch(UnexpectedValidationException uve) {
			throw uve;
		} catch(InvalidParametersException ipe) {
			throw ipe;
		} catch (Exception e) {
			throw new UnexpectedValidationException("Error inesperado catcheado como Exception", e);
		}

		return res;
	}

	private ResultadoValidacion validarDigitoVerificadorCedulaIdentidad(Integer ci, Integer digito) 
		throws UnexpectedValidationException, InvalidParametersException {
		
		ResultadoValidacion res = new ResultadoValidacion();
		
		Integer digitoGenerado = GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci);

		if(digito.intValue()!=digitoGenerado.intValue()){
			res.addError(CODIGO_ERROR_DIGITO_VERIF_NO_CORRRESP_CI, MENSAJE_ERROR_DIGITO_VERIF_NO_CORRRESP_CI);
		}

		return res;
	}
	
}
