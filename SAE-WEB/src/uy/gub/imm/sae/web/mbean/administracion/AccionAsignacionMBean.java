package uy.gub.imm.sae.web.mbean.administracion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import uy.gub.imm.sae.business.ejb.facade.Acciones;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.Evento;
import uy.gub.imm.sae.entity.Accion;
import uy.gub.imm.sae.entity.AccionPorDato;
import uy.gub.imm.sae.entity.AccionPorRecurso;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ParametroAccion;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

import org.primefaces.component.datatable.DataTable;

public class AccionAsignacionMBean extends BaseMBean{

  public static final String MSG_ID = "pantalla";
  
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/AccionesBean!uy.gub.imm.sae.business.ejb.facade.AccionesRemote")
  private Acciones accionEJB;
  
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
  private Recursos recursosEJB;
  
  private SessionMBean sessionMBean;
  
  private DataTable accionesDataTable;
  
  private RowList<AccionPorRecurso> accionesDelRecurso;
  
  private AccionPorRecurso accionDelRecurso;
  private AccionPorRecurso accionDelRecursoEliminar;
  
  private Map<Integer, DatoASolicitar> datosASolicitarDelRecurso;
  private List<SelectItem> datosASolicitarDelRecursoItems;
  
  private List<ParametroAccion> parametrosAccion;
  
  private Map<Integer, Accion> acciones;
  private List<SelectItem> accionesItems;
  
  public SessionMBean getSessionMBean() {
    return sessionMBean;
  }
  public void setSessionMBean(SessionMBean sessionMBean) {
    this.sessionMBean = sessionMBean;
  }
  
  public void beforePhase(PhaseEvent event){
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("asociar_acciones_a_recurso"));
      if (sessionMBean.getAgendaMarcada() == null) {
        addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
      }
      if (sessionMBean.getRecursoMarcado() == null) {
        addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
      }
    }
  }
  
  public DataTable getAccionesDataTable() {
    return accionesDataTable;
  }
  
  public void setAccionesDataTable(DataTable accionesDataTable) {
    this.accionesDataTable = accionesDataTable;
  }
  
  private void cargarAccionesDelRecurso() {
    try {
      List<AccionPorRecurso> entidades = accionEJB.obtenerAccionesDelRecurso(sessionMBean.getRecursoMarcado());
      accionesDelRecurso = new RowList<AccionPorRecurso>(entidades);
    } catch(Exception e) {
      addErrorMessage(e, MSG_ID);
    }
  }
  
  public RowList<AccionPorRecurso> getAccionesDelRecurso() {
    if (accionesDelRecurso == null) {
      cargarAccionesDelRecurso();
    }
    return accionesDelRecurso;
  }
  
  public AccionPorRecurso getAccionDelRecurso() {
    return accionDelRecurso;
  }

  public void setAccionDelRecurso(AccionPorRecurso accionDelRecurso) {
    this.accionDelRecurso = accionDelRecurso;
  }
  
  @SuppressWarnings("unchecked")
  public void selecAccionEliminar(ActionEvent e){
    accionDelRecurso = null;
    accionDelRecursoEliminar = ((Row<AccionPorRecurso>)accionesDataTable.getRowData()).getData();
  }
  
  public void eliminarAccion(ActionEvent event) {
    if (accionDelRecursoEliminar != null){
      try {
        accionEJB.eliminarAccionPorRecurso(accionDelRecursoEliminar);
        accionDelRecursoEliminar=null;
        cargarAccionesDelRecurso();
        addInfoMessage(sessionMBean.getTextos().get("accion_eliminada"), MSG_ID);
      } catch (Exception ex) {
        addErrorMessage(ex, MSG_ID);
      }
    }
  }
  
  private void cargarAcciones() throws ApplicationException {
    
    acciones = new HashMap<Integer, Accion>();
    accionesItems = new ArrayList<SelectItem>();
    accionesItems.add(new SelectItem(0, "Sin especificar"));
    List<Accion> acciones0 = accionEJB.consultarAcciones();
    for(Accion accion : acciones0) {
      accionesItems.add(new SelectItem(accion.getId(), accion.getNombre()));
      acciones.put(accion.getId(), accion);
    }
  }
  
  public void agregarAccion(ActionEvent event) {
    try {
      this.accionDelRecurso = new AccionPorRecurso();
      this.accionDelRecurso.setAccion(new Accion());
      this.accionDelRecurso.setRecurso(sessionMBean.getRecursoMarcado());
      this.accionDelRecurso.setAccionesPorDato(new ArrayList<AccionPorDato>());
      cargarAcciones();
    } catch (Exception e) {
      addErrorMessage(e, MSG_ID);
    }
    cargarDatosASolicitar();
    cargarParametrosDeLaAccion();
  }

  @SuppressWarnings("unchecked")
  public void editarAccion(ActionEvent event) {
    Row<AccionPorRecurso> rowSelect = (Row<AccionPorRecurso>) accionesDataTable.getRowData();
    if (rowSelect != null){
      try {
        this.accionDelRecurso = rowSelect.getData();
        List<AccionPorDato> acciones = accionEJB.obtenerAsociacionesAccionPorDato(this.accionDelRecurso);
        this.accionDelRecurso.setAccionesPorDato(acciones);
        cargarAcciones();
      } catch (Exception e) {
        addErrorMessage(e, MSG_ID);
      }
      cargarDatosASolicitar();
      cargarParametrosDeLaAccion();
    }
  }
  
  public List<SelectItem> getAccionesItems() {
    return accionesItems;
  }  
  
  public List<SelectItem> getEventosItems() {
    List<SelectItem> items = new ArrayList<SelectItem>();
    for (Evento evento : Evento.values()) {
      items.add(new SelectItem(evento, sessionMBean.getTextos().get(evento.getDescripcion())));   
    }   
    return items;
  }
  
  private void cargarDatosASolicitar() {
    this.datosASolicitarDelRecurso = new HashMap<Integer, DatoASolicitar>();
    this.datosASolicitarDelRecursoItems = new ArrayList<SelectItem>();
    try {
      List<DatoASolicitar> datosASol = recursosEJB.consultarDatosSolicitar(sessionMBean.getRecursoMarcado());
      for(DatoASolicitar das : datosASol) {
        this.datosASolicitarDelRecursoItems.add(new SelectItem(das.getId(), das.getNombre()));
        this.datosASolicitarDelRecurso.put(das.getId(), das); 
      }
    } catch (Exception e) {
      addErrorMessage(e, MSG_ID);
    }
  }
  
  public List<SelectItem> getDatosASolicitarItems() {
    return datosASolicitarDelRecursoItems;
  }

  public List<SelectItem> getParametrosDeLaAccionItems() {
    List<SelectItem> parametrosDeLaAccionItems = new ArrayList<SelectItem>();
    for (ParametroAccion param: this.parametrosAccion) {
      parametrosDeLaAccionItems.add(new SelectItem(param.getNombre(), param.getNombre()));
    }
    return parametrosDeLaAccionItems;
  }
  
  public void cambioAccionDelRecurso(ValueChangeEvent event) {
    
    limpiarMensajesError();
    
    Integer accionId = (Integer) event.getNewValue();
    if(accionId!=null && accionId.intValue()>0) {
      Accion accion = acciones.get(accionId);
      this.accionDelRecurso.setAccion(accion);
    }else {
      this.accionDelRecurso.setAccion(new Accion());
    }
    cargarParametrosDeLaAccion();
  }
  
  private void cargarParametrosDeLaAccion() {
    try {
      Accion accion = accionDelRecurso.getAccion();
      if(accion.getId()!=null && accion.getId()>0) {
        this.parametrosAccion = accionEJB.consultarParametrosDeLaAccion(accion);
      }else {
        this.parametrosAccion = new ArrayList<ParametroAccion>();
      }
    } catch (Exception e) {
      addErrorMessage(e, MSG_ID);
    }
  }
  
  public void guardarAsociacion(ActionEvent event) {
    try {
      boolean hayErrores = false;
      
      if(this.accionDelRecurso.getAccion().getId() == null || this.accionDelRecurso.getAccion().getId().intValue()==0){
        addErrorMessage(sessionMBean.getTextos().get("la_accion_es_obligatoria"), "form:accionAsociacion");
        hayErrores = true;
      }
      if(this.accionDelRecurso.getOrdenEjecucion() == null){
        addErrorMessage(sessionMBean.getTextos().get("el_orden_de_ejecucion_es_obligatorio"), "form:ordenAsociacion");
        hayErrores = true;
      }else if(this.accionDelRecurso.getOrdenEjecucion().intValue()<1){
          addErrorMessage(sessionMBean.getTextos().get("el_orden_de_ejecucion_debe_ser_mayor_a_cero"), "form:ordenAsociacion");
          hayErrores = true;
      }
      
      int ind = 0;
      for(AccionPorDato param : this.accionDelRecurso.getAccionesPorDato()) {
        if(param.getDatoASolicitar().getId()==null || param.getDatoASolicitar().getId().intValue()==0) {
          addErrorMessage(sessionMBean.getTextos().get("el_dato_a_solicitar_es_obligatorio"), "form:tablaAccionesPorDato:"+ind+":parametroDatoASolicitar");
          hayErrores = true;
        }
        if(param.getNombreParametro() == null || param.getNombreParametro().trim().isEmpty()){
          addErrorMessage(sessionMBean.getTextos().get("el_parametro_es_obligatorio"), "form:tablaAccionesPorDato:"+ind+":parametroParametroDato");
          hayErrores = true;
        }
        ind++;
      }
      
      if(hayErrores) {
        return;
      }
      
      if(this.accionDelRecurso.getId()==null) {
        accionEJB.crearAccionPorRecurso(this.accionDelRecurso);
        addInfoMessage(sessionMBean.getTextos().get("accion_creada"), MSG_ID);
      }else {
        accionEJB.modificarAccionPorRecurso(this.accionDelRecurso);
        addInfoMessage(sessionMBean.getTextos().get("accion_modificada"), MSG_ID);
      }
      this.accionDelRecurso = null;
      cargarAccionesDelRecurso();
    } catch (Exception e) {
      addErrorMessage(e , MSG_ID);
    }
  }

  public void cancelarAsociacion(ActionEvent e) {
    this.accionDelRecurso = null;
    cargarAccionesDelRecurso();
  }
  
  public void crearAccionPorDato (ActionEvent event) {
    AccionPorDato accionPorDato = new AccionPorDato();
    accionPorDato.setDatoASolicitar(new DatoASolicitar());
    this.accionDelRecurso.getAccionesPorDato().add(accionPorDato);
  }
  
  public void eliminarAccionPorDato(Integer ordinal) {
    this.accionDelRecurso.getAccionesPorDato().remove(ordinal.intValue());
  }
}

