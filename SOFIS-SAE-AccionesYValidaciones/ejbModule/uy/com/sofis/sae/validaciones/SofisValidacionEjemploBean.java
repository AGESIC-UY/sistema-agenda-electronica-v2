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

package uy.com.sofis.sae.validaciones;

import java.util.Map;

import javax.ejb.Stateless;

import uy.gub.imm.sae.validaciones.business.ejb.ResultadoValidacion;
import uy.gub.imm.sae.validaciones.business.ejb.ValidadorReservaRemote;
import uy.gub.imm.sae.validaciones.business.ejb.exception.InvalidParametersException;
import uy.gub.imm.sae.validaciones.business.ejb.exception.UnexpectedValidationException;

@Stateless
public class SofisValidacionEjemploBean implements ValidadorReservaRemote {

	
	public ResultadoValidacion validarDatosReserva(String nombreValidacion, Map<String, Object> params) 
		throws UnexpectedValidationException, InvalidParametersException {

    System.out.println("SofisValidacionEjemplo1Bean.ejecutar -- ******************************");
    System.out.println("SofisValidacionEjemplo1Bean.ejecutar -- nombreValidacion="+nombreValidacion);
    for(String param : params.keySet()) {
      System.out.println("SofisValidacionEjemplo1Bean.ejecutar -- ** "+param + " = "+params.get(param));
    }
    System.out.println("SofisValidacionEjemplo1Bean.ejecutar -- ******************************");
		
    ResultadoValidacion res = new ResultadoValidacion();


    if(params.containsKey("test")) {
      String valor = (String) params.get("test");
      if(valor.toLowerCase().contains("ping")) {
        res.addError("0", valor.toLowerCase().replace("ping","pong"));
      }
    }
    
		return res;
	}

}
