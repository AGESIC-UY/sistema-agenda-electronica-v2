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

package uy.gub.imm.sae.validaciones.business.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultadoValidacion implements Serializable {

	private static final long serialVersionUID = -581709626898012672L;
	private List<ErrorValidacion> errores;
	private List<WarningValidacion> warnings;
	private List<ErrorValidacion> erroresConCommit;
	private List<WarningValidacion> warningsConCommit;
	
	public ResultadoValidacion() {
		super();
		errores = new ArrayList<ErrorValidacion>();
		warnings = new ArrayList<WarningValidacion>();
		erroresConCommit = new ArrayList<ErrorValidacion>();
		warningsConCommit = new ArrayList<WarningValidacion>();
	}

	public List<ErrorValidacion> getErrores() {
		return errores;
	}
	
	public List<WarningValidacion> getWarnings() {
		return warnings;
	}
	
	public List<ErrorValidacion> getErroresConCommit() {
		return erroresConCommit;
	}
	
	public List<WarningValidacion> getWarningsConCommit() {
		return warningsConCommit;
	}
	
	public void addWarning(WarningValidacion warning){
		warnings.add(warning);
	}
	
	public void addWarningConCommit(WarningValidacion warning){
		warningsConCommit.add(warning);
	}
	
	public void addWarning(String codigo, String mensaje){
		WarningValidacion warning = new WarningValidacion(codigo, mensaje);
		warnings.add(warning);
	}
	
	public void addWarningConCommit(String codigo, String mensaje){
		WarningValidacion warning = new WarningValidacion(codigo, mensaje);
		warningsConCommit.add(warning);
	}
	
	public void addError(ErrorValidacion error){
		errores.add(error);
	}
	
	public void addErrorConCommit(ErrorValidacion error){
		erroresConCommit.add(error);
	}

	public void addError(String codigo, String mensaje){		
		ErrorValidacion error = new ErrorValidacion(codigo, mensaje);		
		errores.add(error);
	}
	
	public void addErrorConCommit(String codigo, String mensaje){		
		ErrorValidacion error = new ErrorValidacion(codigo, mensaje);		
		erroresConCommit.add(error);
	}
	
	public boolean isOK(){
		return errores.isEmpty() && warnings.isEmpty() && erroresConCommit.isEmpty() && warningsConCommit.isEmpty();
	}
	
	public boolean hayErrores(){
		return !errores.isEmpty();
	}
	
	public boolean hayWarnings(){
		return !warnings.isEmpty();
	}
	
	public boolean hayErroresConCommit(){
		return !erroresConCommit.isEmpty();
	}
	
	public boolean hayWarningsConCommit(){
		return !warningsConCommit.isEmpty();
	}
}
