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

package uy.gub.imm.sae.web.common.reporte;

public class Columna {

	public enum Alineacion {IZQUIERDA, CENTRO, DERECHA}
	
	private String id;
	private String nombre;
	private Class<?> clase;
	private int ancho;
	private Alineacion alineacion;

	public Columna () {
		alineacion = Alineacion.IZQUIERDA;
	}
	
	public Columna (String id, String nombre, Class<?> clase, int ancho) {
		setId(id);
		setNombre(nombre);
		setClase(clase);
		setAncho(ancho);
		alineacion = Alineacion.IZQUIERDA;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Class<?> getClase() {
		return clase;
	}
	public void setClase(Class<?> clase) {
		this.clase = clase;
	}
	public int getAncho() {
		return ancho;
	}
	public void setAncho(int ancho) {
		this.ancho = ancho;
	}
	public Alineacion getAlineacion() {
		return alineacion;
	}
	public void setAlineacion(Alineacion alineacion) {
		this.alineacion = alineacion;
	}

	public String getAlineacionHtml(){
		
		if 		(alineacion.equals(Alineacion.IZQUIERDA)) {
			
			return "left";
		}
		else if (alineacion.equals(Alineacion.CENTRO)) {
			
			return "center";
		}
		else {
			
			return "right";
		}
	}
}
