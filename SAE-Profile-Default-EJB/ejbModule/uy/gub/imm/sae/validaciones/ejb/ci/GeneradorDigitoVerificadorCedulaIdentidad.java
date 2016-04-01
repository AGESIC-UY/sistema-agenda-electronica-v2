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

import org.apache.log4j.Logger;


public class GeneradorDigitoVerificadorCedulaIdentidad {

	static Logger logger = Logger.getLogger(GeneradorDigitoVerificadorCedulaIdentidad.class);
	
	private static final int[] numerosCi = {1, 1, 4, 3, 2, 9, 8, 7, 6, 3, 4};
	private static final int cantNumerosCi = 11;
	private static final int topeDigitos = 10;
	
	public static Integer calcularDigitoVerificadorCI(Integer ci){
		
		int dig = 0;
		String ciStr = ci.toString();
		int iters = cantNumerosCi-ciStr.length();
		int j = 0, suma = 0, digitoActual;
	
		while(iters<cantNumerosCi){
			digitoActual = Integer.valueOf(ciStr.substring(j, j+1)).intValue();
			
			suma += digitoActual*numerosCi[iters];
			
			iters++;
			j++;
		}
		
		dig = (topeDigitos - (suma%topeDigitos))%topeDigitos;
		
		return dig;
	}
	
	/*
	private static generarPotencias10()
	*/
	public static void main(String[] args) {
		String s = "12345678";
		
		logger.debug("INT_MAX: " + Integer.MAX_VALUE);
		logger.debug("LONG_MAX: " + Long.MAX_VALUE);
		
		for (int i = 0; i < s.length(); i++) {
			
			int a = s.charAt(i);			
			logger.debug(a);
			int b = s.codePointAt(i);			
			logger.debug(b);
			
			int c = Integer.valueOf(s.substring(i, i+1)).intValue();
			logger.debug(c); 
			
		}
		
		int ci = 3071179;
		int digito = 8;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));

		ci = 3071180;
		digito = 1;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));

		ci = 3071178;
		digito = 2;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));
		
		ci = 3485214;
		digito = 8;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));
		
		ci = 4389029;
		digito = 6;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));

		ci = 5348832;
		digito = 0;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));

		ci = 4122719;
		digito = 2;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));

		ci = 2716295;
		digito = 4;		
		logger.debug("ci: " + ci + " - " + digito);
		logger.debug("digito resultado: " + GeneradorDigitoVerificadorCedulaIdentidad.calcularDigitoVerificadorCI(ci));
	}
	
	
	
}

