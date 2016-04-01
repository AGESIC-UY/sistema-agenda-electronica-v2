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

public class RowListMultipleSelect<T> extends ArrayList<RowMultipleSelect<T>> {

	private static final long serialVersionUID = 1L;
	
	private List<RowMultipleSelect<T>> selectedRows;
	
	public RowListMultipleSelect() {
		super();		
	}
	
	public RowListMultipleSelect(List<T> lista) {
		super();
		selectedRows = new ArrayList<RowMultipleSelect<T>>();
		for (T elem : lista) {
			RowMultipleSelect<T> row = new RowMultipleSelect<T>(elem,this);
			this.add(row);
			//Solo tiene sentido si se sobreescribe get/setSelectedRow para manejar sesion
			if (isSelected(elem)) {
				getSelectedRows().add(row);
			}
		}
	}
	
	public void clear() {
		super.clear();
		setSelectedRows(new ArrayList<RowMultipleSelect<T>>());
	}
	
	public List<RowMultipleSelect<T>> getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(List<RowMultipleSelect<T>> selectedRows) {
		this.selectedRows = selectedRows;
	}
	
	//Si se esta creando una lista con un elemento previamente seleccionado, sobreescribir este metodo
	//al igual que los respectivos get/setSelectedRow
	public boolean isSelected(T data) {
		return false;
	}
}
