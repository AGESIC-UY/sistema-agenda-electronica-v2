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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Token;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class TokensMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas usuariosEJB;
	
	private SessionMBean sessionMBean;
	private RowList<Token> tokensSeleccion;
	private Row<Token> rowSelect;
	private DataTable tokensDataTable;
	
	@PostConstruct
	public void postConstruct() {
	}
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public Row<Token> getRowSelect() {
		return rowSelect;
	}
	
	public void setRowSelect(Row<Token> rowSelect) {
		this.rowSelect = rowSelect;
	}

	public DataTable getTokensDataTable() {
		return tokensDataTable;
	}

	public void setTokensDataTable(DataTable tokensDataTable) {
		this.tokensDataTable = tokensDataTable;
	}

	//Lista de usuarios para seleccionar en la eliminacion
	public RowList<Token> getTokensSeleccion() {
		try {
			Empresa empresaActual = sessionMBean.getEmpresaActual();
			List<Token> entidades = usuariosEJB.consultarTokensEmpresa(empresaActual.getId());
			tokensSeleccion = new RowList<Token>(entidades);
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
		return tokensSeleccion;
	}
	
	
	public void beforePhaseGestionar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("gestionar_tokens"));
		}
	}
	// Campos para la creación de un nuevo token
	
	private String crearTokenToken = "";
	private String crearTokenNombre = "";
	private String crearTokenEmail = "";

	public String getCrearTokenToken() {
		return crearTokenToken;
	}

	public void setCrearTokenToken(String crearTokenToken) {
		this.crearTokenToken = crearTokenToken;
	}

	public String getCrearTokenNombre() {
		return crearTokenNombre;
	}

	public void setCrearTokenNombre(String crearTokenNombre) {
		this.crearTokenNombre = crearTokenNombre;
	}

	public String getCrearTokenEmail() {
		return crearTokenEmail;
	}

	public void setCrearTokenEmail(String crearTokenEmail) {
		this.crearTokenEmail = crearTokenEmail;
	}
	
	public String crearToken() {
		limpiarMensajesError();
		
		boolean hayErrores = false;
		if(this.crearTokenNombre==null || this.crearTokenNombre.trim().isEmpty()) {
			addErrorMessage(sessionMBean.getTextos().get("el_nombre_es_obligatorio"), "form:tokenNombre");
			hayErrores = true;
		}
		if(this.crearTokenEmail==null || this.crearTokenEmail.trim().isEmpty()) {
			addErrorMessage(sessionMBean.getTextos().get("el_correo_electronico_es_obligatorio"), "form:tokenEmail");
			hayErrores = true;
		}
		if(hayErrores) {
			return null;
		}
		try {
			this.crearTokenToken = usuariosEJB.crearToken(sessionMBean.getEmpresaActual().getId(), this.crearTokenNombre, this.crearTokenEmail);
		}catch(UserException uEx) {
			addErrorMessage(uEx, null);
		}
		return null;
	}
	
	public String cancelarToken() {
		this.crearTokenEmail="";
		this.crearTokenNombre="";
		this.crearTokenToken="";
		return null;
	}
	
	// Campos para la eliminación de un token
	
	private String eliminarTokenToken=null;
	
	@SuppressWarnings("unchecked")
	public void selecTokenEliminar(ActionEvent e){
		Token tokenEliminar = ((Row<Token>)tokensDataTable.getRowData()).getData();
		eliminarTokenToken = tokenEliminar.getToken();
	}
	
	public String eliminar() {
		if(this.eliminarTokenToken != null) {
			try {
				usuariosEJB.eliminarToken(this.eliminarTokenToken);
			}catch(Exception ex) {
				addErrorMessage(ex, null);
			}
		}
		return null;
	}
	
	
}