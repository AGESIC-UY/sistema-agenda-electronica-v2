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
public class AccionException extends UserException {


	private static final long serialVersionUID = 3617276942333856550L;

	private List<String> mensajes;

	
	public AccionException (String codigoError, String mensaje, List<String> mensajes) {
		super(codigoError, mensaje);
	
		this.mensajes = mensajes;
		if (this.mensajes == null) {
			this.mensajes = new ArrayList<String>();
		}
	}

	public int getCantMensajes() {
		return mensajes.size();
	}

	public String getMensaje(int index) {
		if (this.mensajes != null) {
			return this.mensajes.get(index);
		}
		else {
			return null;
		}
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
	
}
