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

package uy.gub.imm.sae.common;

public class PersonaReglas {

	private static final int LARGO_MAX = 7;
	private static final int LARGO_MIN = 1;
	
	public static boolean validarCedula(String cedula, int digitoVerificador) {

		int[] claves = {2, 9, 8, 7, 6, 3, 4};
		int suma = 0;
		int mod10 = 0;
		int digitoCalculado = 0;
		
		//Valido rangos y formato.
		if (cedula.length() > LARGO_MAX || cedula.length() < LARGO_MIN || 
			digitoVerificador < 0 || digitoVerificador > 9) {
			return false;
		}

		int leftPad = LARGO_MAX - cedula.length();
		for (int i = 0; i < cedula.length(); i++) {
			try {
				int d = Integer.parseInt(String.valueOf(cedula.charAt(i)));
				suma += d * claves[i + leftPad ]; 
			} catch (NumberFormatException e) {
				return false;
			}
		}

		mod10 = suma % 10; 
		if (mod10 != 0) { 
		  digitoCalculado = 10 - mod10; 
		}
		
		if (digitoVerificador == digitoCalculado) {
			return true;
		}
		else {
			return false;	
		}
	}
	/* Solo por documentacion: 
	 * El siguiente codigo es el pl/sql que utilice como referencia 
	 * en la implementacion del algoritmo de validacion
	 */
	/* CODIGO PL/SQL
		DECLARE 
		 v_nro_doc VARCHAR2(7); 
		 v_clave  VARCHAR2(7) := '2987634'; 
		 v_ind  NUMBER(1) := 1; 
		 v_suma  NUMBER(10) := 0; 
		 v_mod10  NUMBER(10) := 0; 
		 v_dig  NUMBER(1) := 0; 
		BEGIN 
		 v_nro_doc := LPAD(p_nro_doc,7,'0'); 
		 WHILE v_ind <= 7 LOOP 
		  v_suma := v_suma + TO_NUMBER(NVL(SUBSTR(v_clave,v_ind,1),0)) * 
		       TO_NUMBER(NVL(SUBSTR(v_nro_doc,v_ind,1),0)); 
		  v_ind := v_ind + 1; 
		 END LOOP; 
		 v_mod10 := MOD(v_suma,10); 
		 IF v_mod10 <> 0 THEN 
		  v_dig := 10 - v_mod10; 
		 END IF; 
		 RETURN v_dig; 
		END;
	*/ 
	 
}
