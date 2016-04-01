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

package uy.gub.imm.sae.web.mbean.administracion;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

//import org.richfaces.component.html.HtmlDataTable;
//import org.richfaces.component.html.HtmlDatascroller;


import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.ejb.facade.Validaciones;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.ParametroValidacion;
import uy.gub.imm.sae.entity.Validacion;
import uy.gub.imm.sae.web.common.BaseMBean;

public class ValidacionMantenimientoMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/ValidacionesBean!uy.gub.imm.sae.business.ejb.facade.ValidacionesLocal")
	private Validaciones validacionEJB;

	private SessionMBean sessionMBean;
	private ValidacionMantenimientoSessionMBean validacionMantenimientoSessionMBean;

	
	private List<Validacion> validaciones;
	private DataTable validacionesTable;
	
	private DataTable parametrosTable;
	
	public void beforePhase(PhaseEvent event){
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Mantenimiento de validaciones");
		}
	}
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public ValidacionMantenimientoSessionMBean getValidacionMantenimientoSessionMBean() {
		return validacionMantenimientoSessionMBean;
	}

	public void setValidacionMantenimientoSessionMBean(
			ValidacionMantenimientoSessionMBean validacionMantenimientoSessionMBean) {
		this.validacionMantenimientoSessionMBean = validacionMantenimientoSessionMBean;
	}

	public List<Validacion> getValidaciones() {
		validaciones = new ArrayList<Validacion>();
		try {
			validaciones = validacionEJB.consultarValidaciones();
		} catch(Exception e) {
			addErrorMessage(e,MSG_ID);
		}
		return validaciones;
	}

	public void setValidaciones(List<Validacion> validaciones) {
		this.validaciones = validaciones;
	}

	public DataTable getValidacionesTable() {
		return validacionesTable;
	}

	public void setValidacionesTable(DataTable validacionesTable) {
		this.validacionesTable = validacionesTable;
	}

	public void eliminarValidacion(ActionEvent event) {
		Validacion validacion = (Validacion)getValidacionesTable().getRowData();
		try {
			validacionEJB.eliminarValidacion(validacion);
			validacionMantenimientoSessionMBean.setModoCreacion(false);
			validacionMantenimientoSessionMBean.setModoEdicion(false);
			validacionMantenimientoSessionMBean.setValidacion(null);
		}
		catch (Exception e) {
			addErrorMessage(e,MSG_ID);
		}
	}
	
	public DataTable getParametrosTable() {
		return parametrosTable;
	}

	public void setParametrosTable(DataTable parametrosTable) {
		this.parametrosTable = parametrosTable;
	}
	
	public List<SelectItem> getTiposDeDato() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (Tipo t : Tipo.values()) {
			items.add(new SelectItem(t, sessionMBean.getTextos().get(t.getDescripcion())));
		}
		return items;
	}

	public void editar(ActionEvent event) {
		Validacion v = (Validacion)getValidacionesTable().getRowData();
		validacionMantenimientoSessionMBean.setModoEdicion(true);
		validacionMantenimientoSessionMBean.setModoCreacion(false);
		List<ParametroValidacion> parametros = new ArrayList<ParametroValidacion>();
		try {
			parametros = validacionEJB.consultarParametrosDeLaValidacion(v);
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
		validacionMantenimientoSessionMBean.setValidacion(v);
		validacionMantenimientoSessionMBean.getValidacion().setParametrosValidacion(parametros);
	}
	
	public void guardarEdicion(ActionEvent event) {
		try {
			//Modifica la validacion y sus parametros
			validacionEJB.modificarValidacion(validacionMantenimientoSessionMBean.getValidacion());
			addInfoMessage("Validación guardada correctamente", MSG_ID);
			validacionMantenimientoSessionMBean.setModoEdicion(false);
			validacionMantenimientoSessionMBean.setValidacion(null);
		} catch (Exception e) {
			addErrorMessage(e , MSG_ID);
		}
	}
	
	public void cancelarEdicion(ActionEvent e) {
		validacionMantenimientoSessionMBean.setModoEdicion(false);
		validacionMantenimientoSessionMBean.setValidacion(null);
	}

	public void crear(ActionEvent event) {
		Validacion v = new Validacion();
		validacionMantenimientoSessionMBean.setModoEdicion(false);
		validacionMantenimientoSessionMBean.setModoCreacion(true);
		validacionMantenimientoSessionMBean.setValidacion(v);
	}

	public void guardarCreacion(ActionEvent event) {
		try {
			//Crea la validacion y sus parametros
			validacionEJB.crearValidacion(validacionMantenimientoSessionMBean.getValidacion());
			addInfoMessage("Validación creada correctamente", MSG_ID);
			validacionMantenimientoSessionMBean.setModoCreacion(false);
			validacionMantenimientoSessionMBean.setValidacion(null);
		} catch (Exception e) {
			addErrorMessage(e , MSG_ID);
		}
	}
	
	public void cancelarCreacion(ActionEvent e) {
		validacionMantenimientoSessionMBean.setModoCreacion(false);
		validacionMantenimientoSessionMBean.setValidacion(null);
	}
	
	public void crearParametro(ActionEvent event) {
		validacionMantenimientoSessionMBean.getValidacion().getParametrosValidacion().add(new ParametroValidacion());
		int size = validacionMantenimientoSessionMBean.getValidacion().getParametrosValidacion().size();
		validacionMantenimientoSessionMBean.setParametrosTablePageIndex(Double.valueOf(size / parametrosTable.getRows()).intValue()+1);
	}
	
	public void eliminarParametro(ActionEvent event) {
		int i = getParametrosTable().getRowIndex();
		validacionMantenimientoSessionMBean.getValidacion().getParametrosValidacion().remove(i);
	}
}
