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
package uy.gub.imm.sae.web.common;

public enum TipoMonitor {
	
	MONITOR_17P(17, 4, "17\""),
	MONITOR_19P(19, 4, "19\""),
	MONITOR_22P(22, 5, "22\"");
	
	private Integer pulgadas;
	private Integer lineas;
	private String etiqueta;
	
	private TipoMonitor(Integer pulgadas, Integer lineas, String etiqueta) {
		this.pulgadas = pulgadas;
		this.lineas = lineas;
		this.etiqueta = etiqueta;
	}

	public Integer getPulgadas() {
		return pulgadas;
	}

	public void setPulgadas(Integer pulgadas) {
		this.pulgadas = pulgadas;
	}

	public Integer getLineas() {
		return lineas;
	}

	public void setLineas(Integer lineas) {
		this.lineas = lineas;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	/** Retorna el enumerado a partir de las pulgadas */
	public static TipoMonitor fromPulgadas(Integer pulgadas) {
		if (pulgadas != null) {
			for (TipoMonitor t : TipoMonitor.values()) {
				if (pulgadas.intValue() == t.pulgadas.intValue()) {
					return t;
				}
			}
		}
		return null;
	}
}
