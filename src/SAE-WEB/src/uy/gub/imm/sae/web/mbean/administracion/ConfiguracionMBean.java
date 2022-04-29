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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.entity.global.Configuracion;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class ConfiguracionMBean extends BaseMBean {

  public static final String MSG_ID = "pantalla";

  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionRemote")
  private uy.gub.imm.sae.business.ejb.facade.Configuracion configuracionEJB;

  private SessionMBean sessionMBean;

  private DataTable configuracionesDataTable;
  
  private Configuracion configuracionEditar;
  
  @PostConstruct
  public void postConstruct() {
  }

  public void beforePhase(PhaseEvent event) {
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("configuracion_global"));
    }
  }

  public SessionMBean getSessionMBean() {
    return sessionMBean;
  }

  public void setSessionMBean(SessionMBean sessionMBean) {
    this.sessionMBean = sessionMBean;
  }
  
  public DataTable getConfiguracionesDataTable() {
    return configuracionesDataTable;
  }
  
  public void setConfiguracionesDataTable(DataTable configuracionesDataTable) {
    this.configuracionesDataTable = configuracionesDataTable;
  }
  
  public RowList<Configuracion> getConfiguraciones() {
    try {
      return new RowList<Configuracion>(configuracionEJB.getAll());
    } catch (Exception ex) {
      addErrorMessage(ex, MSG_ID);
    }
    return null;
  }

  public Configuracion getConfiguracionEditar() {
    return configuracionEditar;
  }
  
  @SuppressWarnings("unchecked")
  public String modificar() {
    Row<Configuracion> rowSelect = (Row<Configuracion>)configuracionesDataTable.getRowData();
    if(rowSelect != null){
      configuracionEditar = rowSelect.getData();
    }
    return null;
  }
 
  public String guardar() {
    if(configuracionEditar!=null) {
      configuracionEJB.guardar(configuracionEditar);
    }
    return null;
  }
  
}