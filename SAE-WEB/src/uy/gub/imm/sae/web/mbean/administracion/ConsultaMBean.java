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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.web.common.FormularioDinReservaClient;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;

public class ConsultaMBean extends SessionCleanerMBean {
	
	public static final String MSG_ID = "pantalla";
	
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/ConsultasBean!uy.gub.imm.sae.business.ejb.facade.ConsultasRemote")
	private Consultas consultaEJB;
	
	private UIComponent campos;
	
	
	private SessionMBean sessionMBean;
	private ConsultaSessionMBean consultaSessionMBean;
	private String idReserva;
	private Integer dataScrollerPage;
	
	//consulta reserva por numero
	private Integer hora;
	private Integer min;
	private List<SelectItem> horas =  new ArrayList<SelectItem>();
	private List<SelectItem> minutos =  new ArrayList<SelectItem>();
	
	/* Pagina de consulta por Numero */
	private Date fechaHoraReserva;
	private String numeroReserva;
	private Reserva reservaConsultada;
	
	
	public ConsultaMBean(){
		
	}
	
	
	@PostConstruct
	public void initAgendaRecurso(){
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		String servletPath = request.getServletPath();
		
		if(servletPath.contains("consultarReservaId.xhtml") || servletPath.contains("consultarReservaDatos.xhtml") || servletPath.contains("consultarReservaNumero.xhtml")) {
			if (sessionMBean.getAgendaMarcada() == null){
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
			}
			if (sessionMBean.getRecursoMarcado() == null){
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			}
		}
		
		if(servletPath.contains("consultarReservaNumero.xhtml")) {
			this.cargarListaHoras();
			this.cargarListaMinutos();
		}
		
		if(servletPath.contains("consultarReservaPeriodo.xhtml") || servletPath.contains("consultarAsistenciaPeriodo.xhtml")) {
			//En estos casos se permite no tener seleccionado un recurso o agenda porque el reporte se hace para todos
			if (sessionMBean.getRecursoMarcado() == null){
				if (sessionMBean.getAgendaMarcada() == null){
					addAdvertenciaMessage("No tiene un recurso ni agenda seleccionada, el reporte se genera contemplando a todos los recursos y agendas");
				}else {
					addAdvertenciaMessage("No tiene un recurso seleccionado, el reporte se genera contemplando a todos los recursos de la agenda seleccionada");
				}
			}
		}
		
	}
	
	public void buscarReservaId(ActionEvent event){
		
		limpiarMensajesError();
		
		boolean huboError=false;
		Reserva reservaAux ;
		
		campos.getChildren().clear();
		// limpio campos en los que guardo mis datos de Session
		consultaSessionMBean.setReserva(null);
		consultaSessionMBean.setDisponibilidad(null);
		
		if (sessionMBean.getAgendaMarcada() == null && !huboError){
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		
		if (sessionMBean.getRecursoMarcado() == null && !huboError){
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}

		Integer iIdReserva = null; 
		if (idReserva==null || idReserva.trim().isEmpty()){
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("el_identificador_de_la_reserva_es_obligatorio"), "form:VNroReserva");
		}else {
			try {
				iIdReserva = Integer.valueOf(idReserva);
			}catch(Exception ex) {
				huboError = true;
				addErrorMessage(sessionMBean.getTextos().get("el_identificador_de_la_reserva_debe_ser_numerico"), "form:VNroReserva");
			}
		}
		
		
		if (!huboError){
					
			// Voy a negocio a buscar la reserva
			try {
				reservaAux = consultaEJB.consultarReservaId(iIdReserva, sessionMBean.getRecursoMarcado().getId());
				if (reservaAux== null){
					addErrorMessage(sessionMBean.getTextos().get("no_se_encuentra_la_reserva_especificada"), MSG_ID);
				} else {
					this.consultaSessionMBean.setReserva(reservaAux);
					this.consultaSessionMBean.setDisponibilidad(reservaAux.getDisponibilidades().get(0));
					List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(sessionMBean.getRecursoMarcado(), sessionMBean.getTimeZone());
					FormularioDinReservaClient.armarFormularioLecturaDinamico(sessionMBean.getRecursoMarcado(), this.consultaSessionMBean.getReserva(), this.campos, agrupaciones, sessionMBean.getFormatoFecha());
				}	
			
			} catch (ApplicationException ae) {
				addErrorMessage(ae.getMessage(), MSG_ID);
			} catch (BusinessException be) {
				addErrorMessage(be.getMessage(), MSG_ID);
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
	
		}	
	}

	
		public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	
	public String getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(String idReserva) {
		this.idReserva = idReserva;
	}

	public Integer getDataScrollerPage() {
		return dataScrollerPage;
	}

	public void setDataScrollerPage(Integer dataScrollerPage) {
		this.dataScrollerPage = dataScrollerPage;
	}

	public ConsultaSessionMBean getConsultaSessionMBean() {
		return consultaSessionMBean;
	}

	public void setConsultaSessionMBean(ConsultaSessionMBean consultaSessionMBean) {
		this.consultaSessionMBean = consultaSessionMBean;
	}

	public void beforePhaseConsultarReservaId(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reserva_por_id"));
		}
	}

	public void beforePhaseConsultarReservaNumero(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reserva_por_numero"));
		}
	}

	public void beforePhaseConsultarReservaPeriodo(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_reservas"));
		}
	}

	public void beforePhaseConsultarAsistenciaPeriodo(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_asistencias"));
		}
	}

	public void beforePhaseConsultarTiempoAtencion(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("gestionar_tokens"));
		}
	}
	
	public void beforePhaseConsultarAtencionFuncionario(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_atencion_funcionario"));
		}
	}
	
	public void beforePhaseConsultarTiempoAtencionFuncionario(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_tiempo_atencion_funcionario"));
		}
	}
	
	public UIComponent getCampos() {
		return campos;
	}


	public void setCampos(UIComponent campos) {
		this.campos = campos;
	}
	
	
	/**
	 * Pagina de consulta por Numero
	 *  
	 */

	public Date getFechaHoraReserva() {
		return fechaHoraReserva;
	}


	public void setFechaHoraReserva(Date fechaHoraReserva) {
		this.fechaHoraReserva = fechaHoraReserva;
	}


	public String getNumeroReserva() {
		return numeroReserva;
	}


	public void setNumeroReserva(String numeroReserva) {
		this.numeroReserva = numeroReserva;
	}


	public Reserva getReservaConsultada() {
		return reservaConsultada;
	}

	
	public void buscarReservaPorNumero(ActionEvent event){
		
		limpiarMensajesError();
		
		boolean huboError=false;
		
		campos.getChildren().clear();
		if (sessionMBean.getAgendaMarcada() == null){
			huboError = true;
			addErrorMessage("debe_haber_una_agenda_seleccionada");
		}
		
		if (sessionMBean.getRecursoMarcado() == null){
			huboError = true;
			addErrorMessage("debe_haber_un_recurso_seleccionado");
		}
		
		Integer iNumeroReserva = null;
		if (fechaHoraReserva == null){
			huboError = true;
			addErrorMessage("el_dia_y_la_hora_son_obligatorios", "form:fechaHoraReserva");
		}
		if (numeroReserva == null || numeroReserva.trim().isEmpty()){
			huboError = true;
			addErrorMessage("el_numero_es_obligatorio", "form:nroRes");
		}else {
			try {
				iNumeroReserva = Integer.valueOf(numeroReserva);
			}catch(NumberFormatException nfEx) {
				huboError = true;
				addErrorMessage("el_numero_ingresado_no_es_valido", "form:nroRes");
			}
		}
		
		if (!huboError){
			Calendar c = new GregorianCalendar();
			c.setTime(fechaHoraReserva); //Debe estar en GMT0	
			c.set(Calendar.HOUR_OF_DAY, hora);
			c.set(Calendar.MINUTE, min);
			c.set(Calendar.SECOND, 0);
			
			// Voy a negocio a buscar la reserva
			try {
				reservaConsultada = consultaEJB.consultarReservaPorNumero(sessionMBean.getRecursoMarcado(), c.getTime(), iNumeroReserva);
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(sessionMBean.getRecursoMarcado(), sessionMBean.getTimeZone());
				FormularioDinReservaClient.armarFormularioLecturaDinamico(sessionMBean.getRecursoMarcado(), reservaConsultada, this.campos, agrupaciones,sessionMBean.getFormatoFecha());
			} catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
			}
	
		}	
		
	}


	public Integer getHora() {
		return hora;
	}


	public void setHora(Integer hora) {
		this.hora = hora;
	}


	public Integer getMin() {
		return min;
	}


	public void setMin(Integer min) {
		this.min = min;
	}


	public List<SelectItem> getHoras() {
		return horas;
	}


	public void setHoras(List<SelectItem> horas) {
		this.horas = horas;
	}


	public List<SelectItem> getMinutos() {
		return minutos;
	}


	private void cargarListaHoras(){
		horas =  new ArrayList<SelectItem>();
	    Integer h = 0;
	    String labelH;
	    
	    while (h < 24){
			SelectItem s = new SelectItem();
			s.setValue(h);
			labelH = Integer.toString(h);
			if (labelH.length()<2){
				labelH = "0"+labelH;
			}
			s.setLabel(labelH);
			horas.add(s);
			h = h + 1;
		}
	}

	private void cargarListaMinutos(){
		minutos =  new ArrayList<SelectItem>();
    String labelH;
    for(int i=0; i<60;i++){
			SelectItem s = new SelectItem();
			s.setValue(i);
			labelH = Integer.toString(i);
			if (labelH.length()<2){
				labelH = "0"+labelH;
			}
			s.setLabel(labelH);
			minutos.add(s);
		}
	}
	
	
	
}
