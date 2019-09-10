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
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.Reserva;
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
	
  public void beforePhaseConsultarReservaId(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_FCALL_CENTER", "AE_R_GENERADORREPORTES_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reserva_por_id"));
    }
  }

  public void beforePhaseConsultarReservaNumero(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_FCALL_CENTER", "AE_R_GENERADORREPORTES_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reserva_por_numero"));
    }
  }

  public void beforePhaseConsultarReservaPeriodo(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "AE_R_GENERADORREPORTES_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_reservas"));
    }
  }

  public void beforePhaseConsultarAsistenciaPeriodo(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "AE_R_GENERADORREPORTES_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_asistencias"));
    }
  }

  public void beforePhaseConsultarAtencionFuncionario(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_atencion_funcionario"));
    }
  }
  
  public void beforePhaseConsultarTiempoAtencionFuncionario(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_tiempo_atencion_funcionario"));
    }
  }
  
  public void beforePhaseConsultarAtencionPresencialPeriodo(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "AE_R_GENERADORREPORTES_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (sessionMBean.getAgendaMarcada() == null){
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
    }
    if (sessionMBean.getRecursoMarcado() == null){
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_atencion_presencial"));
    }
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
					addAdvertenciaMessage(sessionMBean.getTextos().get("reporte_para_todas_las_agendas_y_recursos"));
				}else {
					addAdvertenciaMessage(sessionMBean.getTextos().get("reporte_para_todos_los_recursos"));
				}
			}
		}
	}

  public void beforePhaseConsultarCancelaciones(PhaseEvent event) {
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("reporte_cancelaciones"));
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

	public UIComponent getCampos() {
		return campos;
	}

	public void setCampos(UIComponent campos) {
		this.campos = campos;
	}
	
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
	
  /**
   * Realiza la búsqueda de una reserva por su identificador.
   * No excluye a las reservas correspondientes a disponibilidades presenciales.
   * @param event
   */
  public void buscarReservaId(ActionEvent event){
    
    limpiarMensajesError();
    
    boolean huboError=false;
    
    campos.getChildren().clear();
    
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
      try {
        reservaConsultada = consultaEJB.consultarReservaId(iIdReserva, sessionMBean.getRecursoMarcado().getId());
        if (reservaConsultada == null){
          addErrorMessage(sessionMBean.getTextos().get("no_se_encuentra_la_reserva_especificada"), MSG_ID);
        } else {
          List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(sessionMBean.getRecursoMarcado(), sessionMBean.getTimeZone());
          FormularioDinReservaClient.armarFormularioLecturaDinamico(sessionMBean.getRecursoMarcado(), reservaConsultada, this.campos, agrupaciones, sessionMBean.getFormatoFecha());
        }
      } catch (Exception ex) {
        addErrorMessage(ex, MSG_ID);
      }
    } 
  }
	
  /**
   * Realiza la búsqueda de una reserva por la combinación de fecha/hora/número.
   * No considera a las reservas correspondientes a disponibilidades presenciales.
   * @param event
   */
	public void buscarReservaPorNumero(ActionEvent event){
		limpiarMensajesError();
		reservaConsultada = null;
		boolean hayErrores=false;
		campos.getChildren().clear();
		if (sessionMBean.getAgendaMarcada() == null){
			addErrorMessage("debe_haber_una_agenda_seleccionada");
      hayErrores = true;
		}
		if (sessionMBean.getRecursoMarcado() == null){
			addErrorMessage("debe_haber_un_recurso_seleccionado");
      hayErrores = true;
		}
		Integer iNumeroReserva = null;
		if(fechaHoraReserva == null){
			addErrorMessage("el_dia_y_la_hora_son_obligatorios", "form:fechaHoraReserva");
      hayErrores = true;
		}else if(Utiles.esFechaInvalida(fechaHoraReserva)) {
      addErrorMessage("la_fecha_es_invalida", "form:fechaHoraReserva");
      hayErrores = true;
		}
		if (numeroReserva == null || numeroReserva.trim().isEmpty()){
			addErrorMessage("el_numero_es_obligatorio", "form:nroRes");
      hayErrores = true;
		}else {
			try {
				iNumeroReserva = Integer.valueOf(numeroReserva);
			}catch(NumberFormatException nfEx) {
				addErrorMessage("el_numero_ingresado_no_es_valido", "form:nroRes");
        hayErrores = true;
			}
		}
		
		if (!hayErrores){
			Calendar c = new GregorianCalendar();
			c.setTime(fechaHoraReserva); //Debe estar en GMT0	
			c.set(Calendar.HOUR_OF_DAY, hora);
			c.set(Calendar.MINUTE, min);
			c.set(Calendar.SECOND, 0);
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
