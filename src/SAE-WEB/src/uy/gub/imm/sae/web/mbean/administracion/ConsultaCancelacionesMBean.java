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
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.RowList;

public class ConsultaCancelacionesMBean extends BaseMBean {

  public static final String MSG_ID = "pantalla";
  
  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
  private AgendarReservas agendarReservasEJB;

  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConsultasBean!uy.gub.imm.sae.business.ejb.facade.ConsultasRemote")
  private Consultas consultaEJB;

  private SessionMBean sessionMBean;

  private Date reservaFechaDesde;
  private Date reservaFechaHasta;

  private Date creacionFechaDesde;
  private Date creacionFechaHasta;

  private Date cancelacionFechaDesde;
  private Date cancelacionFechaHasta;
  
  private Map<String, TramiteAgenda> tramitesAgenda;
  private List<SelectItem> tramites = new ArrayList<>();
  private String tramiteCodigo;

  private RowList<ReservaDTO> reservasCanceladas = new RowList<>();
  
  private Integer idReservaLiberar = null;
  
  public void beforePhaseConsultarCancelaciones(PhaseEvent event) {
    if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR" })) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("consulta_de_cancelaciones"));
    }
  }

  @PostConstruct
  public void init() {
    if (sessionMBean.getAgendaMarcada() == null) {
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
      return;
    }
    try {
      Agenda agenda = sessionMBean.getAgendaMarcada();
      List<TramiteAgenda> tramites0 = agendarReservasEJB.consultarTramites(agenda);
      this.tramiteCodigo = null;
      tramitesAgenda = new HashMap<String, TramiteAgenda>();
      tramites = new ArrayList<SelectItem>();
      tramites.add(new SelectItem("", "Sin especificar"));
      for (TramiteAgenda tramite : tramites0) {
        tramitesAgenda.put(tramite.getTramiteCodigo(), tramite);
        tramites.add(new SelectItem(tramite.getTramiteCodigo(), tramite.getTramiteNombre()));
      }
      if(sessionMBean.getRecursoMarcado()==null) {
        addAdvertenciaMessage(sessionMBean.getTextos().get("reporte_para_todos_los_recursos"));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      redirect("inicio");
      return;
    }
  }

  public String consultarCancelaciones() {
    Recurso recurso = sessionMBean.getRecursoMarcado();
    List<ReservaDTO> reservas = consultaEJB.consultarReservasCanceladas(recurso, tramiteCodigo, reservaFechaDesde, reservaFechaHasta, 
        creacionFechaDesde, creacionFechaHasta, cancelacionFechaDesde, cancelacionFechaHasta);
    reservasCanceladas = new RowList<>(reservas);
    return null;
  }

  public boolean mostrarLiberarReserva(Date fechaLiberacion) {
    return fechaLiberacion!=null && fechaLiberacion.after(new Date());
  }
  
  public void selecReservaLiberar(Integer idReserva){
    idReservaLiberar = idReserva;
  }
  
  public String liberarReserva() {
    if(idReservaLiberar==null) {
      return null;
    }
    try {
      Empresa empresa = sessionMBean.getEmpresaActual();
      agendarReservasEJB.liberarReserva(empresa.getId(), idReservaLiberar);
      consultarCancelaciones();
      addInfoMessage(sessionMBean.getTextos().get("reserva_liberada"), MSG_ID);
    }catch(UserException uEx) {
      addAdvertenciaMessage(sessionMBean.getTextos().get(uEx.getCodigoError()));
    }
    return null;
  }
  
  public String mostrarUsuario(String usuario) {
    if(usuario==null) {
      return "";
    }
    if(usuario.startsWith("sae2-")) {
      return "usuario";
    }else {
      return usuario;
    }
  }
  
  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  public List<SelectItem> getTramites() {
    return tramites;
  }

  public RowList<ReservaDTO> getReservasCanceladas() {
    return reservasCanceladas;
  }

  public SessionMBean getSessionMBean() {
    return sessionMBean;
  }

  public void setSessionMBean(SessionMBean sessionMBean) {
    this.sessionMBean = sessionMBean;
  }

  public Date getReservaFechaDesde() {
    return reservaFechaDesde;
  }

  public void setReservaFechaDesde(Date reservaFechaDesde) {
    this.reservaFechaDesde = reservaFechaDesde;
  }

  public Date getReservaFechaHasta() {
    return reservaFechaHasta;
  }

  public void setReservaFechaHasta(Date reservaFechaHasta) {
    this.reservaFechaHasta = reservaFechaHasta;
  }

  public Date getCreacionFechaDesde() {
    return creacionFechaDesde;
  }

  public void setCreacionFechaDesde(Date creacionFechaDesde) {
    this.creacionFechaDesde = creacionFechaDesde;
  }

  public Date getCreacionFechaHasta() {
    return creacionFechaHasta;
  }

  public void setCreacionFechaHasta(Date creacionFechaHasta) {
    this.creacionFechaHasta = creacionFechaHasta;
  }

  public Date getCancelacionFechaDesde() {
    return cancelacionFechaDesde;
  }

  public void setCancelacionFechaDesde(Date cancelacionFechaDesde) {
    this.cancelacionFechaDesde = cancelacionFechaDesde;
  }

  public Date getCancelacionFechaHasta() {
    return cancelacionFechaHasta;
  }

  public void setCancelacionFechaHasta(Date cancelacionFechaHasta) {
    this.cancelacionFechaHasta = cancelacionFechaHasta;
  }

}
