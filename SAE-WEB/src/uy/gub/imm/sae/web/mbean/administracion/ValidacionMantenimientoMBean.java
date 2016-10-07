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

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.ejb.facade.Validaciones;
import uy.gub.imm.sae.entity.ParametroValidacion;
import uy.gub.imm.sae.entity.Validacion;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class ValidacionMantenimientoMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/ValidacionesBean!uy.gub.imm.sae.business.ejb.facade.ValidacionesRemote")
	private Validaciones validacionEJB;

	private SessionMBean sessionMBean;

	private DataTable validacionesDataTable;
	
  private RowList<Validacion> validacionesSeleccion;
	
  private Validacion validacion; //Accion que está siendo editada o modificada
  private Validacion validacionEliminar; //Acción seleccionada para eliminar
  
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

  public DataTable getValidacionesDataTable() {
    return validacionesDataTable;
  }
  
  public void setValidacionesDataTable(DataTable validacionesDataTable) {
    this.validacionesDataTable = validacionesDataTable;
  }
	
  private void cargarValidaciones() {
    try {
      List<Validacion> entidades = validacionEJB.consultarValidaciones();
      validacionesSeleccion = new RowList<Validacion>(entidades);
    } catch (Exception e) {
      addErrorMessage(e, MSG_ID);
    }
  }
  
  public RowList<Validacion> getValidacionesSeleccion() {
    if(validacionesSeleccion==null) {
      cargarValidaciones();
    }
    return validacionesSeleccion;
  }   
  
  public Validacion getValidacion() {
    return validacion;
  }

  public void setValidacion(Validacion validacion) {
    this.validacion = validacion;
  }
  
  public void crearValidacion(ActionEvent event) {
    this.validacion = new Validacion();
  }
  
  @SuppressWarnings("unchecked")
  public void editarValidacion(ActionEvent event) {
  
    Row<Validacion> rowSelect = (Row<Validacion>) validacionesDataTable.getRowData();
    if (rowSelect != null){
      this.validacion = rowSelect.getData();
      List<ParametroValidacion> parametros = new ArrayList<ParametroValidacion>();
      try {
        parametros = validacionEJB.consultarParametrosDeLaValidacion(this.validacion);
      } catch (Exception ex) {
        ex.printStackTrace();
        addErrorMessage(ex, MSG_ID);
      }
      this.validacion.setParametrosValidacion(parametros);
    }
  }
  
  @SuppressWarnings("unchecked")
  public void selecValidacionEliminar(ActionEvent e){
    validacion = null;
    validacionEliminar = ((Row<Validacion>)validacionesDataTable.getRowData()).getData();
  }
  
  public void eliminarValidacion(ActionEvent event) {
    if (validacionEliminar != null){
      try {
        validacionEJB.eliminarValidacion(validacionEliminar);
        validacionEliminar=null;
        cargarValidaciones();
        addInfoMessage(sessionMBean.getTextos().get("validacion_eliminada"), MSG_ID);
      } catch (Exception ex) {
        addErrorMessage(ex, MSG_ID);
      }
    }
  }
  
  public void crearParametro(ActionEvent event) {
    this.validacion.getParametrosValidacion().add(new ParametroValidacion());
  }
  
  public void eliminarParametro(Integer ordinal) {
    this.validacion.getParametrosValidacion().remove(ordinal.intValue());
  }
  
  public void guardarValidacion(ActionEvent event) {
    try {
      boolean hayErrores = false;
      
      if(this.validacion.getNombre() == null || this.validacion.getNombre().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_validacion_es_obligatorio"), "form:nombreValidacion");
        hayErrores = true;
      }
      if(this.validacion.getDescripcion() == null || this.validacion.getDescripcion().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("la_descripcion_de_la_validacion_es_obligatoria"), "form:descripcionValidacion");
        hayErrores = true;
      }
      if(this.validacion.getServicio() == null || this.validacion.getServicio().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("el_servicio_de_la_validacion_es_obligatorio"), "form:servicioValidacion");
        hayErrores = true;
      }
      if(this.validacion.getHost() == null || this.validacion.getHost().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("el_host_de_la_validacion_es_obligatorio"), "form:hostValidacion");
        hayErrores = true;
      }
      
      int ind = 0;
      for(ParametroValidacion param : this.validacion.getParametrosValidacion()) {
        if(param.getNombre() == null || param.getNombre().trim().isEmpty()){
          addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_parametro_es_obligatorio"), "form:parametrosValidacion:"+ind+":parametroNombre");
          addErrorMessage("", "form:parametrosValidacion:parametroNombre");
          hayErrores = true;
        }else if (param.getNombre().length() > 50) {
          addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_parametro_es_demasiado_largo"), "form:parametrosValidacion:"+ind+":parametroNombre");
          addErrorMessage("", "form:parametrosValidacion:parametroNombre");
          hayErrores = true;
        }
        ind++;
      }
      
      if(hayErrores) {
        return;
      }
      
      if(this.validacion.getId()==null) {
        validacionEJB.crearValidacion(this.validacion);
        addInfoMessage(sessionMBean.getTextos().get("validacion_creada"), MSG_ID);
      }else {
        validacionEJB.modificarValidacion(this.validacion);
        addInfoMessage(sessionMBean.getTextos().get("validacion_modificada"), MSG_ID);
      }
      this.validacion = null;
      cargarValidaciones();
    } catch (Exception e) {
      addErrorMessage(e , MSG_ID);
    }
  }
  
  
  public void cancelarValidacion(ActionEvent e) {
    this.validacion = null;
    cargarValidaciones();
  }
}
