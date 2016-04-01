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
public class WarningValidacionException extends ValidacionException {


	private static final long serialVersionUID = -8647088213281009039L;
	private List<String> codigosErrorMensajes;
	private String nombreValidacion;
	
	/**
	 * Las listas deben tener un tamaño > 0.
	 * @param codigoError
	 * @param nombreCampos es el nombre de los campos para los que hay algun mensaje de error en la validacion
	 * @param mensajes     son los mensajes de error de la validacion
	 */
	public WarningValidacionException(String codigoError, List<String> nombreCampos, List<String> mensajes) {
		super(codigoError, nombreCampos, mensajes);
		
		if (nombreCampos == null ||
			mensajes ==  null ||
			nombreCampos.size() == 0 ||
			mensajes.size() == 0) {
			throw new RuntimeException("La lista de mensajes y nombre de campos deben tener a lo menos un elemento");
		}
	}

	public WarningValidacionException(String codigoError, List<String> nombreCampos, List<String> mensajes,  List<String> codigosErrorMensajes, String nombreValidacion) {
		super(codigoError, nombreCampos, mensajes);
		
		this.codigosErrorMensajes = codigosErrorMensajes;
		if(this.codigosErrorMensajes == null){
			this.codigosErrorMensajes = new ArrayList<String>();
		}
		this.nombreValidacion = nombreValidacion;
		
		if (nombreCampos == null ||
			mensajes ==  null ||
			nombreCampos.size() == 0 ||
			mensajes.size() == 0) {
			throw new RuntimeException("La lista de mensajes y nombre de campos deben tener a lo menos un elemento");
		}
	}

	public List<String> getCodigosErrorMensajes() {
		return codigosErrorMensajes;
	}

	public void setCodigosErrorMensajes(List<String> codigosErrorMensajes) {
		this.codigosErrorMensajes = codigosErrorMensajes;
	}
	
	public String getNombreValidacion() {
		return nombreValidacion;
	}

	public void setNombreValidacion(String nombreValidacion) {
		this.nombreValidacion = nombreValidacion;
	}

}
