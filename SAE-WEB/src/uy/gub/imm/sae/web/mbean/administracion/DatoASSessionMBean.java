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
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;

public class DatoASSessionMBean extends SessionCleanerMBean implements RemovableFromSession {
	
	public static final String MSG_ID = "pantalla";
	
	//Booleana para saber si se despliega la tabla de Valores Posibles para el dato
	//private Boolean mostrarValor = false;

	//Booleana para saber si se despliega la tabla para agregar Valores Posibles
	private Boolean mostrarAgregarValor = false;
	
	//Booleana para saber si se despliega la tabla para modificar un valor posible
	private Boolean mostrarModifValor = false;
	
	//Booleana para saber si se despliega la tabla de Valores Posibles en la pantalla
	//de consulta de dato a solicitar
	private Boolean mostrarConsultarValor = false;
	

	private int pagValorRUpd;
	private int pagValorRCons;
	private int pagDatoASDel;
	private int pagDatoASCons = 1;
	private int pagDatoASUpd;
	private int pagDatoAgrupUpd;
	private DatoASolicitar datoSeleccionado;
	private List<DatoASolicitar> datosASolicitar;
	private AgrupacionDato agrupacionSeleccionada;
	private List<AgrupacionDato> agrupaciones;
	private ValorPosible valorDelDatoSeleccionado;

	private Integer agrupacionDatoId = null;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	private SessionMBean sessionMBean; 
	
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}
	public int getPagDatoASDel() {
		return pagDatoASDel;
	}
	public void setPagDatoASDel(int pagDatoASDel) {
		this.pagDatoASDel = pagDatoASDel;
	}
	public int getPagDatoASCons() {
		return pagDatoASCons;
	}
	public void setPagDatoASCons(int pagDatoASCons) {
		this.pagDatoASCons = pagDatoASCons;
	}
	public int getPagDatoASUpd() {
		return pagDatoASUpd;
	}
	public void setPagDatoASUpd(int pagDatoASUpd) {
		this.pagDatoASUpd = pagDatoASUpd;
	}
	
	
	public int getPagDatoAgrupUpd() {
		return pagDatoAgrupUpd;
	}
	public void setPagDatoAgrupUpd(int pagDatoAgrupUpd) {
		this.pagDatoAgrupUpd = pagDatoAgrupUpd;
	}
	public DatoASolicitar getDatoSeleccionado() {
		
		return datoSeleccionado;
	}
	public void setDatoSeleccionado(DatoASolicitar datoSeleccionado) {
		this.datoSeleccionado = datoSeleccionado;
	}
	
	public ValorPosible getValorDelDatoSeleccionado() {
		return valorDelDatoSeleccionado;
	}
	public void setValorDelDatoSeleccionado(ValorPosible valorDelDatoSeleccionado) {
	
		this.valorDelDatoSeleccionado = valorDelDatoSeleccionado;
	}

	public Boolean getMostrarModifValor() {
		return mostrarModifValor;
	}
	public void setMostrarModifValor(Boolean mostrarModifValor) {
		this.mostrarModifValor = mostrarModifValor;
	}
	
	public Boolean getMostrarConsultarValor() {
		return mostrarConsultarValor;
	}
	public void setMostrarConsultarValor(Boolean mostrarConsultarValor) {
		this.mostrarConsultarValor = mostrarConsultarValor;
	}
	
	public List<ValorPosible> getValoresDelDato() {
		if (datoSeleccionado != null ) {
			List<ValorPosible> valoresFromDB;
			try {
				valoresFromDB = recursosEJB.consultarValoresPosibles(datoSeleccionado);
				return valoresFromDB;
			} catch (ApplicationException e) {
				addErrorMessage(e, MSG_ID);
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	public int getPagValorRUpd() {
		return pagValorRUpd;
	}
	public void setPagValorRUpd(int pagValorRUpd) {
		this.pagValorRUpd = pagValorRUpd;
	}

	public int getPagValorRCons() {
		return pagValorRCons;
	}
	public void setPagValorRCons(int pagValorRCons) {
		this.pagValorRCons = pagValorRCons;
	}
	public AgrupacionDato getAgrupacionSeleccionada() {
		return agrupacionSeleccionada;
	}
	public void setAgrupacionSeleccionada(AgrupacionDato agrupacionSeleccionada) {
		this.agrupacionSeleccionada = agrupacionSeleccionada;
	}

	

	
	
	public List<AgrupacionDato> getAgrupaciones() {
		if (sessionMBean.getRecursoMarcado() != null && agrupaciones == null) {
			cargarAgrupaciones();
		}
		return agrupaciones;
	}
	
	public void cargarAgrupaciones() {
		try {
			agrupaciones = recursosEJB.consultarAgrupacionesDatos(sessionMBean.getRecursoMarcado());
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
	}
	

	public void clearAgrupaciones() {
		this.agrupaciones = null;
	}

	

	
	public Boolean getMostrarValor() {
		
		if (getDatoSeleccionado() != null &&
			getDatoSeleccionado().getTipo() == Tipo.LIST) {
			
			return true;
		}
		else {
			return false;
		}
	}
/*	
	public void setMostrarValor(Boolean mostrarValor) {
		this.mostrarValor = mostrarValor;
	}
*/
	public Boolean getMostrarAgregarValor() {
		return mostrarAgregarValor;
	}
	public void setMostrarAgregarValor(Boolean mostrarAgregarValor) {
		this.mostrarAgregarValor = mostrarAgregarValor;
	}

	public List<DatoASolicitar> getDatosASolicitar() {

		if (datosASolicitar == null) {
			cargarDatosSolicitar();
		}
		return datosASolicitar;
	}
	
	public void cargarDatosSolicitar() {
		try {
			List<DatoASolicitar> entidades = recursosEJB.consultarDatosSolicitar(sessionMBean.getRecursoMarcado());
			datosASolicitar = new ArrayList<DatoASolicitar>(entidades);
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
	}

	public void clearDatosASolicitar() {
		this.datosASolicitar = null;
	}	

	public void beforePhaseConsultar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Consultar Dato a Solicitar");
		}
	}
	
	public Integer getAgrupacionDatoId() {
		return agrupacionDatoId;
	}

	public void setAgrupacionDatoId(Integer agrupacionDatoId) {
		this.agrupacionDatoId = agrupacionDatoId;
	}
	
}


