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

package uy.gub.sae.autocompletados.business.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultadoAutocompletado implements Serializable {

	private static final long serialVersionUID = -7490940036519784073L;
	
	private Map<String, Object> resultados;
	
	private List<ErrorAutocompletado> errores;
	private List<WarningAutocompletado> warnings;
	
	public ResultadoAutocompletado() {
		super();
		resultados = new HashMap<String, Object>();
		errores = new ArrayList<ErrorAutocompletado>();
		warnings = new ArrayList<WarningAutocompletado>();
	}

	public List<ErrorAutocompletado> getErrores() {
		return errores;
	}
	
	public List<WarningAutocompletado> getWarnings() {
		return warnings;
	}
	
	public Map<String, Object> getResultados() {
		return resultados;
	}

	public void addError(ErrorAutocompletado error){
		errores.add(error);
	}
	
	public void addError(String codigo, String mensaje){		
		ErrorAutocompletado error = new ErrorAutocompletado(codigo, mensaje);		
		errores.add(error);
	}
	
	public void addWarning(WarningAutocompletado warning){
		warnings.add(warning);
	}
	
	public void addWarning(String codigo, String mensaje){		
		WarningAutocompletado warning = new WarningAutocompletado(codigo, mensaje);		
		warnings.add(warning);
	}
	
	public void addResultado(String nomResultado, Object objResultado) {
		resultados.put(nomResultado, objResultado);
	}
	
	public boolean hayErrores(){
		return !errores.isEmpty();
	}
	
	public boolean hayWarnings(){
		return !warnings.isEmpty();
	}
}
