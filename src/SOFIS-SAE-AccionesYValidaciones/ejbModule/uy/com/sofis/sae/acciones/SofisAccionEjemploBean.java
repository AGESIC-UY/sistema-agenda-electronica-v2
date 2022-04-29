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

package uy.com.sofis.sae.acciones;

import java.util.Map;

import javax.ejb.Stateless;

import uy.gub.sae.acciones.business.ejb.EjecutorAccionRemote;
import uy.gub.sae.acciones.business.ejb.ResultadoAccion;
import uy.gub.sae.acciones.business.ejb.exception.InvalidParametersException;
import uy.gub.sae.acciones.business.ejb.exception.UnexpectedAccionException;

@Stateless
public class SofisAccionEjemploBean implements EjecutorAccionRemote {

	@Override
	public ResultadoAccion ejecutar(String nombreAccion, Map<String, Object> params)
			throws UnexpectedAccionException, InvalidParametersException {

		ResultadoAccion res = new ResultadoAccion();
		
    System.out.println("SofisAccionEjemplo1Bean.ejecutar -- ******************************");
    System.out.println("SofisAccionEjemplo1Bean.ejecutar -- nombreAccion="+nombreAccion);
    for(String param : params.keySet()) {
      System.out.println("SofisAccionEjemplo1Bean.ejecutar -- ** "+param + " = "+params.get(param));
    }
    System.out.println("SofisAccionEjemplo1Bean.ejecutar -- ******************************");
			
    if(params.containsKey("test")) {
      String valor = (String) params.get("test");
      if(valor.toLowerCase().contains("ping")) {
        res.addError("0", valor.toLowerCase().replace("ping","pong"));
      }
    }
    
		return res;
	}
	

	
	
}
