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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinReservaClient;

public class ConsultaReservaDatosMBean extends BaseMBean {
	public static final String MSG_ID = "pantalla";

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/ConsultasBean!uy.gub.imm.sae.business.ejb.facade.ConsultasRemote")
	private Consultas consultaEJB;

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	private SessionMBean sessionMBean;
	private ConsultaReservaDSessionMBean consReservaDatosSessionMBean;

	private Map<String, DatoASolicitar> datosASolicitar;
	private Map<String, Object> datosFiltroReservaMBean;

	private DataTable reservasDataTable;

	private UIComponent filtroConsulta;
	private UIComponent campos;

	@PostConstruct
	public void initAgendaRecurso() {
		if (sessionMBean.getAgendaMarcada() == null) {
		  addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() == null) {
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		if (sessionMBean.getAgendaMarcada() == null || sessionMBean.getRecursoMarcado() == null) {
			return;
		}
		List<DatoASolicitar> listaDatoSolicitar = recursosEJB.consultarDatosSolicitar(sessionMBean.getRecursoMarcado());
		Map<String, DatoASolicitar> datoSolicMap = new HashMap<String, DatoASolicitar>();
		for (DatoASolicitar dato : listaDatoSolicitar) {
			datoSolicMap.put(dato.getNombre(), dato);
		}
		setDatosASolicitar(datoSolicMap);
	}

  public void beforePhaseConsultarReservaDatos(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_FCALL_CENTER", "AE_R_GENERADORREPORTES_X_RECURSO", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reserva_por_datos"));
    }
  }

  public void beforePhaseDetalleReserva(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_FCALL_CENTER", "AE_R_GENERADORREPORTES_X_RECURSO", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("datos_de_la_reserva"));
    }
  }

	public String volverPagInicio() {
		return "volver";
	}
	
	/**
	 * Busca reservas para el recurso actual que coincidan con los datos ingresados por el usuario.
	 * No considera reservas que corresponden a disponibilidades presenciales.
	 * @param e
	 */
	public void buscarReservaDatos(ActionEvent e) {
		boolean huboError = false;
		if(sessionMBean.getAgendaMarcada() == null && !huboError) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if(sessionMBean.getRecursoMarcado() == null && !huboError) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		if (!huboError) {
		  try {
  	    List<DatoReserva> datos = FormularioDinReservaClient.obtenerDatosReserva(datosFiltroReservaMBean, datosASolicitar);
  	    List<Reserva> reservas = (ArrayList<Reserva>) consultaEJB.consultarReservaDatos(datos, sessionMBean.getRecursoMarcado());
  			this.consReservaDatosSessionMBean.setListaReservas(reservas);
  			if(reservas.isEmpty()) {
  				addErrorMessage(sessionMBean.getTextos().get("no_se_encontraron_reservas"),	MSG_ID);
  			} else {
  				this.consReservaDatosSessionMBean.setListaReservas(reservas);
  			}
		  }catch(Exception ex) {
		    addErrorMessage(ex);
		  }
		}
	}

	public String verDetalleReserva() {
		int iSelectedPos = getReservasDataTable().getRowIndex();
		Reserva reserva = this.consReservaDatosSessionMBean.getListaReservas().get(iSelectedPos);
		this.consReservaDatosSessionMBean.setReservaDatos(reserva);
		this.consReservaDatosSessionMBean.setDisponibilidad(reserva.getDisponibilidades().get(0));
		return "detalleReserva";
	}

	public UIComponent getFiltroConsulta() {
		return filtroConsulta;
	}

	public void setFiltroConsulta(UIComponent filtroConsulta) {
		this.filtroConsulta = filtroConsulta;
		if (this.sessionMBean.getRecursoMarcado() != null) {
			try {
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefCamposTodos(this.sessionMBean.getRecursoMarcado());
				FormularioDinReservaClient.armarFormularioEdicionDinamico(this.sessionMBean.getRecursoMarcado(), filtroConsulta, agrupaciones,sessionMBean.getFormatoFecha());
			} catch (Exception e) {
				addErrorMessage(e);
			}
		}
	}

	public DataTable getReservasDataTable() {
		return reservasDataTable;
	}

	public void setReservasDataTable(DataTable reservasDataTable) {
		this.reservasDataTable = reservasDataTable;
	}

	public Map<String, DatoASolicitar> getDatosASolicitar() {
		return datosASolicitar;
	}

	public void setDatosASolicitar(Map<String, DatoASolicitar> datosASolicitar) {
		this.datosASolicitar = datosASolicitar;
	}

	public UIComponent getCampos() {
		return campos;
	}

	public void setCampos(UIComponent campos) {
		this.campos = campos;
		try {
			List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(sessionMBean.getRecursoMarcado(), sessionMBean.getTimeZone());
			FormularioDinReservaClient.armarFormularioLecturaDinamico(sessionMBean.getRecursoMarcado(), this.consReservaDatosSessionMBean.getReservaDatos(), this.campos, agrupaciones,sessionMBean.getFormatoFecha());
		} catch (Exception e) {
			addErrorMessage(e);
		}

	}

	public Map<String, Object> getDatosFiltroReservaMBean() {
		return datosFiltroReservaMBean;
	}

	public void setDatosFiltroReservaMBean(
			Map<String, Object> datosFiltroReservaMBean) {
		this.datosFiltroReservaMBean = datosFiltroReservaMBean;
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public ConsultaReservaDSessionMBean getConsReservaSession() {
		return consReservaDatosSessionMBean;
	}

	public void setConsReservaSession(
			ConsultaReservaDSessionMBean consReservaSession) {
		this.consReservaDatosSessionMBean = consReservaSession;
	}

	public ConsultaReservaDSessionMBean getConsReservaDatosSessionMBean() {
		return consReservaDatosSessionMBean;
	}

	public void setConsReservaDatosSessionMBean(
			ConsultaReservaDSessionMBean consReservaDatosSessionMBean) {
		this.consReservaDatosSessionMBean = consReservaDatosSessionMBean;
	}
	
	/*********************************************************************/
	/*            c a n c e l a c i o n   d e   r e s e r v a            */
	/*********************************************************************/
	public void cancelarReserva(ActionEvent event) {
		boolean huboError=false;
		
		if (sessionMBean.getAgendaMarcada() == null){
			huboError = true;
			addErrorMessage("Debe seleccionar una agenda.", MSG_ID);
		}
		
		if (sessionMBean.getRecursoMarcado() == null){
			huboError = true;
			addErrorMessage("Debe seleccionar un recurso.", MSG_ID);
		}

		if (consReservaDatosSessionMBean.getReservaDatos() == null || 
			consReservaDatosSessionMBean.getReservaDatos().getId()== null){
			huboError = true;
			addErrorMessage("Debe consultar la reserva para poder cancelarla", MSG_ID);
		}
		
		if (consReservaDatosSessionMBean.getReservaDatos().getEstado() != Estado.R){
			huboError = true;
			addErrorMessage("No es posible cancelar reservas en estado "+consReservaDatosSessionMBean.getReservaDatos().getEstadoDescripcion(), MSG_ID);
		}
		
		Date ahora = new Date();
		if (consReservaDatosSessionMBean.getDisponibilidad().getHoraInicio().compareTo(ahora) < 0){
			huboError = true;
			addErrorMessage("No es posible cancelar reservas ya pasadas.", MSG_ID);
		}

		if (!huboError){
			try {
				//Reserva reserva = consultaEJB.consultarReservaId(.getIdReserva());
				
				agendarReservasEJB.cancelarReserva(sessionMBean.getEmpresaActual(), sessionMBean.getRecursoMarcado(), consReservaDatosSessionMBean.getReservaDatos());
				Reserva r;
				r = consultaEJB.consultarReservaPorNumero(
						sessionMBean.getRecursoMarcado(), consReservaDatosSessionMBean.getDisponibilidad().getHoraInicio(), consReservaDatosSessionMBean.getReservaDatos().getNumero());
				consReservaDatosSessionMBean.setReservaDatos(r);

				addInfoMessage("Reserva cancelada correctamente.", "pantalla");
				
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		}
	
	}
	
	public Boolean getConfirmarDeshabilitado() {
		Date ahora = new Date();
		if (consReservaDatosSessionMBean.getReservaDatos() == null || 
				consReservaDatosSessionMBean.getReservaDatos().getEstado() == Estado.C ||
				consReservaDatosSessionMBean.getReservaDatos().getEstado() == Estado.U ||
				consReservaDatosSessionMBean.getDisponibilidad().getHoraInicio().compareTo(ahora) < 0) {
			
			return true;
		}
		else {
			return false;
		}
	}



}
