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

import java.util.ArrayList;
import java.util.List;

public class RowList<T> extends ArrayList<Row<T>> {

	private static final long serialVersionUID = 1L;
	
	private Row<T> selectedRow;
	
	public RowList() {
		super();
	}
	
	public RowList(List<T> lista) {
		super();
		for (T elem : lista) {
			Row<T> row = new Row<T>(elem,this);
			this.add(row);
			//Solo tiene sentido si se sobreescribe get/setSelectedRow para manejar sesion
			if (isSelected(elem)) {
				setSelectedRow(row);
			}
		}
	}

	public void clear() {
		super.clear();
		setSelectedRow(null);
	}
	
	public Row<T> getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(Row<T> selectedRow) {
		this.selectedRow = selectedRow;
	}
	
	//Si se esta creando una lista con un elemento previamente seleccionado, sobreescribir este metodo
	//al igual que los respectivos get/setSelectedRow
	public boolean isSelected(T data) {
		return false;
	}
}
