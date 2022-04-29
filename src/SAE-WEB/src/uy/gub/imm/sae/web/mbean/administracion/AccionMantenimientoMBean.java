package uy.gub.imm.sae.web.mbean.administracion;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.ejb.facade.Acciones;
import uy.gub.imm.sae.entity.Accion;
import uy.gub.imm.sae.entity.ParametroAccion;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;


public class AccionMantenimientoMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
		
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/AccionesBean!uy.gub.imm.sae.business.ejb.facade.AccionesRemote")
  private Acciones accionEJB;

	private SessionMBean sessionMBean;
	
  private DataTable accionesDataTable;
  
  private RowList<Accion> accionesSeleccion;

  private Accion accion; //Accion que está siendo editada o modificada
  private Accion accionEliminar; //Acción seleccionada para eliminar

  public void beforePhase(PhaseEvent event){
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("mantenimiento_de_acciones"));
    }
  }
  
  public SessionMBean getSessionMBean() {
    return sessionMBean;
  }

  public void setSessionMBean(SessionMBean sessionMBean) {
    this.sessionMBean = sessionMBean;
  }
  
  public DataTable getAccionesDataTable() {
    return accionesDataTable;
  }
  
  public void setAccionesDataTable(DataTable accionesDataTable) {
    this.accionesDataTable = accionesDataTable;
  }
  
  private void cargarAcciones() {
    try {
      List<Accion> entidades = accionEJB.consultarAcciones();
      accionesSeleccion = new RowList<Accion>(entidades);
    } catch (Exception e) {
      addErrorMessage(e, MSG_ID);
    }
  }
  
  public RowList<Accion> getAccionesSeleccion() {
    if(accionesSeleccion==null) {
      cargarAcciones();
    }
    return accionesSeleccion;
  }   

  public Accion getAccion() {
    return accion;
  }

  public void setAccion(Accion accion) {
    this.accion = accion;
  }
  
  public void crearAccion(ActionEvent event) {
    this.accion = new Accion();
  }

  @SuppressWarnings("unchecked")
  public void editarAccion(ActionEvent event) {
  
    Row<Accion> rowSelect = (Row<Accion>) accionesDataTable.getRowData();
    if (rowSelect != null){
      this.accion = rowSelect.getData();
      
      List<ParametroAccion> parametros = new ArrayList<ParametroAccion>();
      try {
        parametros = accionEJB.consultarParametrosDeLaAccion(this.accion);
      } catch (Exception ex) {
        ex.printStackTrace();
        addErrorMessage(ex, MSG_ID);
      }
      this.accion.setParametrosAccion(parametros);
    }
  }
  
  @SuppressWarnings("unchecked")
  public void selecAccionEliminar(ActionEvent e){
    accionEliminar = null;
    accionEliminar = ((Row<Accion>)accionesDataTable.getRowData()).getData();
  }
  
  public void eliminarAccion(ActionEvent event) {
    if (accionEliminar != null){
      try {
        accionEJB.eliminarAccion(accionEliminar);
        accionEliminar=null;
        cargarAcciones();
        addInfoMessage(sessionMBean.getTextos().get("accion_eliminada"), MSG_ID);
      } catch (Exception ex) {
        addErrorMessage(ex, MSG_ID);
      }
    }
  }
  
  public void crearParametro(ActionEvent event) {
    this.accion.getParametrosAccion().add(new ParametroAccion());
  }
  
  public void eliminarParametro(Integer ordinal) {
    this.accion.getParametrosAccion().remove(ordinal.intValue());
  }
  
  public void guardarAccion(ActionEvent event) {
    try {
      boolean hayErrores = false;
      
      if(this.accion.getNombre() == null || this.accion.getNombre().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_accion_es_obligatorio"), "form:nombreAccion");
        hayErrores = true;
      }else if (this.accion.getNombre().length() > 50) {
        addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_accion_es_demasiado_largo"), "form:nombreAccion");
        hayErrores = true;
      }
      if(this.accion.getDescripcion() == null || this.accion.getDescripcion().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("la_descripcion_de_la_accion_es_obligatoria"), "form:descripcionAccion");
        hayErrores = true;
      }else if (this.accion.getDescripcion().length() > 100) {
        addErrorMessage(sessionMBean.getTextos().get("la_descripcion_de_la_accion_es_demasiado_larga"), "form:descripcionAccion");
        hayErrores = true;
      }
      if(this.accion.getServicio() == null || this.accion.getServicio().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("el_servicio_de_la_accion_es_obligatorio"), "form:servicioAccion");
        hayErrores = true;
      }else if (this.accion.getServicio().length() > 250) {
        addErrorMessage(sessionMBean.getTextos().get("el_servicio_de_la_accion_es_demasiado_largo"), "form:servicioAccion");
        hayErrores = true;
      }
      if(this.accion.getHost() == null || this.accion.getHost().trim().isEmpty()){
        addErrorMessage(sessionMBean.getTextos().get("el_host_de_la_accion_es_obligatorio"), "form:hostAccion");
        hayErrores = true;
      }else if (this.accion.getHost().length() > 50) {
        addErrorMessage(sessionMBean.getTextos().get("el_host_de_la_accion_es_demasiado_largo"), "form:hostAccion");
        hayErrores = true;
      }
      
      int ind = 0;
      for(ParametroAccion param : this.accion.getParametrosAccion()) {
        if(param.getNombre() == null || param.getNombre().trim().isEmpty()){
          addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_parametro_es_obligatorio"), "form:parametrosAccion:"+ind+":parametroNombre");
          addErrorMessage("", "form:parametrosAccion:parametroNombre");
          hayErrores = true;
        }else if (param.getNombre().length() > 50) {
          addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_parametro_es_demasiado_largo"), "form:parametrosAccion:"+ind+":parametroNombre");
          addErrorMessage("", "form:parametrosAccion:parametroNombre");
          hayErrores = true;
        }
        ind++;
      }
      
      if(hayErrores) {
        return;
      }
      
      if(this.accion.getId()==null) {
        accionEJB.crearAccion(this.accion);
        addInfoMessage(sessionMBean.getTextos().get("accion_creada"), MSG_ID);
      }else {
        accionEJB.modificarAccion(this.accion);
        addInfoMessage(sessionMBean.getTextos().get("accion_modificada"), MSG_ID);
      }
      this.accion = null;
      cargarAcciones();
    } catch (Exception ex) {
      ex.printStackTrace();
      addErrorMessage(ex , MSG_ID);
    }
  }
  
  public void cancelarAccion(ActionEvent e) {
    this.accion = null;
    cargarAcciones();
  }
  
}

