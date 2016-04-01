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

package uy.gub.imm.sae.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ValidacionException extends UserException {


	private static final long serialVersionUID = 3617276942333856550L;

	private List<String> nombreCampos;
	private List<String> mensajes;
	
	public ValidacionException (String codigoError, List<String> nombreCampos, List<String> mensajes) {
		super(codigoError);
	
		if (nombreCampos == null ||	nombreCampos.size() == 0) {
			throw new RuntimeException("Una ValidacionException debe tener al menos un nombre de campo.");
		}
		
		this.nombreCampos = nombreCampos;
		this.mensajes = mensajes;
		if (this.mensajes == null) {
			this.mensajes = new ArrayList<String>();
		}
	}

	public int getCantCampos() {
		return nombreCampos.size();
	}

	public int getCantMensajes() {
		return mensajes.size();
	}

	public String getNombreCampo(int index) {
		return this.nombreCampos.get(index);
	}

	public String getMensaje(int index) {
		if (this.mensajes != null) {
			return this.mensajes.get(index);
		}
		else {
			return null;
		}
	}
	
	public List<String> getNombresCampos(){
		return nombreCampos;
	}
	
	public List<String> getMensajes() {
		return mensajes;
	}

	public boolean isMensaje (){
		if (this.mensajes != null && this.mensajes.size() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isNombreCampo(){
		if (this.nombreCampos != null && this.nombreCampos.size() > 0) {
			return true;
		}
		return false;
	}
}
