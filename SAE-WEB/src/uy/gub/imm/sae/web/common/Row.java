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

import java.io.Serializable;

public class Row<T> implements Serializable {
	
	private static final long serialVersionUID = 3345492499383513579L;

	private T data;

	//Puntero a la lista que contiene este elemento.
	private RowList<T> rowListRef;
	
	public Row (T data, RowList<T> rowList) {
		this.data = data;
		this.rowListRef = rowList;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Boolean getSelected() {
		return (this.equals(rowListRef.getSelectedRow()));
	}

	public void setSelected(Boolean selected) {
		if (selected) {
			rowListRef.setSelectedRow(this);
		}
		else if (getSelected()) {
			rowListRef.setSelectedRow(null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals (Object o){
		return (o==null ? false : data.equals(((Row<T>)o).getData()));
	}
}
