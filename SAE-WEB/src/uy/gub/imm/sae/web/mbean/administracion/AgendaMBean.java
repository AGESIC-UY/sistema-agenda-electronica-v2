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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIInput;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.Agendas;
import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Tramite;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class AgendaMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
	private AgendaGeneral generalEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendasBean!uy.gub.imm.sae.business.ejb.facade.AgendasRemote")
	private Agendas agendasEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas empresasEJB;
	
	private SessionMBean sessionMBean;
	private AgendaSessionMBean agendaSessionMBean;
	private Agenda agendaNueva;
	private RowList<Agenda> agendasSeleccion;
	private Row<Agenda> rowSelect;
	private DataTable agendasDataTable;
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	//Lista de agendas para seleccionar en la eliminacion/modificacion.
	public RowList<Agenda> getAgendasSeleccion() {
		try {
			List<Agenda> entidades = generalEJB.consultarAgendas();
			agendasSeleccion = new RowList<Agenda>(entidades);
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
		return agendasSeleccion;
	}		
	
	public Agenda getAgendaNueva() {
		if (agendaNueva == null) {
			agendaNueva = new Agenda();
		}
		return agendaNueva;
	}

	//Agenda seleccionada para eliminacion/modificacion
	public Agenda getAgendaSeleccionada() {
		return sessionMBean.getAgendaSeleccionada();
	}

	@PostConstruct
	public void postConstruct() {
		if(sessionMBean.getEmpresaActual() == null) {
			addErrorMessage(sessionMBean.getTextos().get("debe_especificar_la_empresa"));
		}
	}

	public void crear(ActionEvent e) {
		limpiarMensajesError();
		boolean hayErrores = false;
		Agenda agendaCrear = getAgendaNueva();
		if(agendaCrear.getNombre() == null || agendaCrear.getNombre().trim().equals("")){
			addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_agenda_es_obligatorio"), "form:nombreAgenda");
			hayErrores = true;
		}
    if(agendaSessionMBean.getIdiomasSeleccionados().isEmpty()){
      addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_al_menos_un_idioma"), "form:idiomasAgenda");
      hayErrores = true;
    }
		if(agendaCrear.getTramites().isEmpty()) {
		  addErrorMessage(sessionMBean.getTextos().get("debe_haber_al_menos_un_tramite"));
		  hayErrores = true;
		}else {
		  int ind = 0;
		  List<String> tramitesUsados = new ArrayList<String>(agendaCrear.getTramites().size());
		  boolean hayTramitesRepetidos = false;
		  for(TramiteAgenda tramite : agendaCrear.getTramites()) {
		    if(tramite.getTramiteCodigo() == null || tramite.getTramiteCodigo().trim().isEmpty()){
	        addErrorMessage(sessionMBean.getTextos().get("el_codigo_y_el_nombre_del_tramite_son_obligatorios"), "form:tramites:"+ind+":codigoTramite");
          addErrorMessage("", "form:tramites:codigoTramite");
		      hayErrores = true;
		    }else if(tramite.getTramiteNombre() == null || tramite.getTramiteNombre().trim().isEmpty()){
          addErrorMessage(sessionMBean.getTextos().get("el_codigo_y_el_nombre_del_tramite_son_obligatorios"), "form:tramites:"+ind+":codigoTramite");
          addErrorMessage("", "form:tramites:codigoTramite");
          hayErrores = true;
        }else {
          //Esto es necesario porque al utilizar el método addErrorMessage para colorear en rojo el fondo de los campos con error, dado que todas las instancias
          //del bloque comparten el mismo componente JSF, todas ellas quedan con color rojo, incluso si no tienen error (JSF utiliza el mismo componente para todos los divs).
          //Esto lo que hace es agendar un código JavaScript que le quita la clase 'form-group-con-error' al componente.
          String compDomId = "form:tramites:"+ind+":fgCodigoTramite";
          RequestContext.getCurrentInstance().execute("document.getElementById('"+compDomId+"').className=document.getElementById('"+compDomId+"').className.replace('form-group-con-error','')");
          String claveTramite = "["+tramite.getTramiteCodigo().trim()+"]["+tramite.getTramiteNombre().trim()+"]";
          claveTramite = claveTramite.toLowerCase();
          if(tramitesUsados.contains(claveTramite)) {
            hayTramitesRepetidos = true;
          }else {
            tramitesUsados.add(claveTramite);
          }
        }
        ind++;
		  }
		  if(hayTramitesRepetidos) {
	      addErrorMessage(sessionMBean.getTextos().get("hay_tramites_repetidos"));
	      hayErrores = true;
		  }
		}
		if(hayErrores) {
			return;
		}
    if(agendaCrear.getDescripcion() == null){
      agendaCrear.setDescripcion("");
    }
		String sIdiomasSeleccionados = "";
		if(agendaSessionMBean.getIdiomasSeleccionados() != null) {
			for(String idioma : agendaSessionMBean.getIdiomasSeleccionados()) {
				if(!sIdiomasSeleccionados.isEmpty()) {
					sIdiomasSeleccionados += ",";
				}
				sIdiomasSeleccionados += idioma;
			}
		}
		agendaCrear.setIdiomas(sIdiomasSeleccionados);
		//Guardar la agenda en la base de datos
		try {
			if(agendasEJB.existeAgendaPorNombre(agendaCrear)) {
				addErrorMessage(sessionMBean.getTextos().get("ya_existe_una_agenda_con_el_nombre_especificado"), "form:nombreAgenda");
				return;
			}
			agendasEJB.crearAgenda(agendaCrear);
			sessionMBean.cargarAgendas();
			agendasSeleccion = null;
			agendaNueva = null;
			addInfoMessage(sessionMBean.getTextos().get("agenda_creada"), MSG_ID);
		} catch (Exception ex) {
			addErrorMessage(ex, MSG_ID);
		}
	}

	@SuppressWarnings("unchecked")
	public void selecAgendaEliminar(ActionEvent e){
		sessionMBean.setAgendaSeleccionada(((Row<Agenda>)agendasDataTable.getRowData()).getData());
	}
	
	public void eliminar(ActionEvent event) {
		Agenda agenda = sessionMBean.getAgendaSeleccionada();
		if (agenda != null){
 			try {
 				if(sessionMBean.getAgendaMarcada()!=null && sessionMBean.getAgendaMarcada().getId().equals(agenda.getId())) {
 					sessionMBean.desseleccionarAgenda();
 				}
 				agendasEJB.eliminarAgenda(agenda, sessionMBean.getTimeZone(), sessionMBean.getUsuarioActual().getCodigo());
				sessionMBean.cargarAgendas();
 				agendasSeleccion = null;
				addInfoMessage(sessionMBean.getTextos().get("agenda_eliminada"), MSG_ID);
			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			}
		}else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
	}

	@SuppressWarnings("unchecked")
	public String modificar() {
	  try {
  		this.rowSelect = (Row<Agenda>)agendasDataTable.getRowData();
  		if (this.rowSelect != null){
  			Agenda agenda = rowSelect.getData();
  			//Cargar los trámites de la agenda
  			List<TramiteAgenda> tramites = generalEJB.consultarTramites(agenda);
  			agenda.setTramites(tramites);
  			//Cargar los idiomas seleccionados
  			sessionMBean.setAgendaSeleccionada(agenda);
  			List<String> idiomasSeleccionados = new ArrayList<String>();
  			if(agenda.getIdiomas() != null) {
  				String[] idiomas = agenda.getIdiomas().split(",");
  				for(String idioma : idiomas) {
  					idiomasSeleccionados.add(idioma);
  				}
  			}
  			agendaSessionMBean.setIdiomasSeleccionados(idiomasSeleccionados);
  			return "modificar";
  		} else {
  			sessionMBean.setAgendaSeleccionada(null);
  			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
  			return null;
  		}
	  }catch(ApplicationException aEx) {
      addErrorMessage(aEx.getMessage(), MSG_ID);
      return null;
	  }
	}

	public String guardar() {
		limpiarMensajesError();
		Agenda agendaSeleccionada = sessionMBean.getAgendaSeleccionada();
		if (agendaSeleccionada != null) {
			boolean hayErrores = false;
			if(agendaSeleccionada.getNombre() == null || agendaSeleccionada.getNombre().trim().equals("")){
				addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_agenda_es_obligatorio"), "form:nombreAgenda");
				hayErrores = true;
			}
	    if(agendaSessionMBean.getIdiomasSeleccionados().isEmpty()){
	      addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_al_menos_un_idioma"), "form:idiomasAgenda");
	      hayErrores = true;
	    }
	    if(agendaSeleccionada.getTramites().isEmpty()) {
	      addErrorMessage(sessionMBean.getTextos().get("debe_haber_al_menos_un_tramite"));
	      hayErrores = true;
	    }else {
	      int ind = 0;
	      List<String> tramitesUsados = new ArrayList<String>(agendaSeleccionada.getTramites().size());
	      boolean hayTramitesRepetidos = false;
	      for(TramiteAgenda tramite : agendaSeleccionada.getTramites()) {
	        if(tramite.getTramiteCodigo() == null || tramite.getTramiteCodigo().trim().isEmpty()){
	          addErrorMessage(sessionMBean.getTextos().get("el_codigo_y_el_nombre_del_tramite_son_obligatorios"), "form:tramites:"+ind+":codigoTramite");
            addErrorMessage("", "form:tramites:codigoTramite");
	          hayErrores = true;
	        }else if(tramite.getTramiteNombre() == null || tramite.getTramiteNombre().trim().isEmpty()){
            addErrorMessage(sessionMBean.getTextos().get("el_codigo_y_el_nombre_del_tramite_son_obligatorios"), "form:tramites:"+ind+":codigoTramite");
            addErrorMessage("", "form:tramites:codigoTramite");
	          hayErrores = true;
	        }else {
	          //Esto es necesario porque al utilizar el método addErrorMessage para colorear en rojo el fondo de los campos con error, dado que todas las instancias
	          //del bloque comparten el mismo componente JSF, todas ellas quedan con color rojo, incluso si no tienen error (JSF utiliza el mismo componente para todos los divs).
	          //Esto lo que hace es agendar un código JavaScript que le quita la clase 'form-group-con-error' al componente.
	          String compDomId = "form:tramites:"+ind+":fgCodigoTramite";
	          RequestContext.getCurrentInstance().execute("document.getElementById('"+compDomId+"').className=document.getElementById('"+compDomId+"').className.replace('form-group-con-error','')");
	          String claveTramite = "["+tramite.getTramiteCodigo().trim()+"]["+tramite.getTramiteNombre().trim()+"]";
	          claveTramite = claveTramite.toLowerCase();
	          if(tramitesUsados.contains(claveTramite)) {
	            hayTramitesRepetidos = true;
	          }else {
	            tramitesUsados.add(claveTramite);
	          }
	        }
	        ind++;
	      }
	      if(hayTramitesRepetidos) {
	        addErrorMessage(sessionMBean.getTextos().get("hay_tramites_repetidos"));
	        hayErrores = true;
	      }
	    }
			if(hayErrores) {
				return null;
			}
      if(agendaSeleccionada.getDescripcion() == null){
        agendaSeleccionada.setDescripcion("");
      }
			String sIdiomasSeleccionados = "";
			if(agendaSessionMBean.getIdiomasSeleccionados() != null) {
				for(String idioma : agendaSessionMBean.getIdiomasSeleccionados()) {
					if(!sIdiomasSeleccionados.isEmpty()) {
						sIdiomasSeleccionados += ",";
					}
					sIdiomasSeleccionados += idioma;
				}
			}
			agendaSeleccionada.setIdiomas(sIdiomasSeleccionados);
 			try {
 				if(agendasEJB.existeAgendaPorNombre(agendaSeleccionada)) {
 					addErrorMessage(sessionMBean.getTextos().get("ya_existe_una_agenda_con_el_nombre_especificado"), "form:nombreAgenda");
 					return null;
 				}
 				agendasEJB.modificarAgenda(agendaSeleccionada);
				sessionMBean.cargarAgendas();
 				agendasSeleccion = null;
 				sessionMBean.setAgendaSeleccionada(null);
				addInfoMessage(sessionMBean.getTextos().get("agenda_modificada"), MSG_ID);
				return "guardar";
 			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			}
		}else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		return null;
	}
	
	public AgendaSessionMBean getAgendaSessionMBean() {
		return agendaSessionMBean;
	}
	
	public void setAgendaSessionMBean(AgendaSessionMBean agendaSessionMBean) {
		this.agendaSessionMBean = agendaSessionMBean;
	}

	public void beforePhaseCrear(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("crear_agenda"));
		}
	}

	public void beforePhaseModificarConsultar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("consultar_agendas"));
		}
	}
	
	public void beforePhaseModificar(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_agenda"));
		}
	}
	
	public Row<Agenda> getRowSelect() {
		return rowSelect;
	}
	public void setRowSelect(Row<Agenda> rowSelect) {
		this.rowSelect = rowSelect;
	}
	public DataTable getAgendasDataTable() {
		return agendasDataTable;
	}
	public void setAgendasDataTable(DataTable agendasDataTable) {
		this.agendasDataTable = agendasDataTable;
	}
	
	//=============================================================================
	//Datos de los tramites 
	private Map<String, Tramite> mapTramites = new HashMap<String, Tramite>();
	private List<SelectItem> tramites = new ArrayList<SelectItem>(0);
	
	public Map<String, Tramite> getMapTramites() {
		return this.mapTramites;
	}
	
	public void setTramites(List<Tramite> trams) {
		mapTramites = new HashMap<String, Tramite>();
		tramites = new ArrayList<SelectItem>(0);
		tramites.add(new SelectItem(0, "Sin especificar"));
		if(trams == null) {
			return;
		}
		tramites = new ArrayList<SelectItem>(trams.size());
		tramites.add(new SelectItem(0, "Sin especificar"));
		for(Tramite tram : trams) {
			tramites.add(new SelectItem(tram.getId(), tram.getNombre()));
			mapTramites.put(tram.getId(), tram);
		}
	}
	
	public List<SelectItem> getTramites() {
		if(tramites == null || tramites.isEmpty()) {
			recargarTramites(false);
		}
		return tramites;
	}
	
	
	public String recargarTramites(ActionEvent event) {
		recargarTramites(true);
		return null;
	}
	
	private void recargarTramites(boolean actualizar) {
		try {
			if(sessionMBean.getEmpresaActual() == null) {
				setTramites(null);
				return;
			}
			Integer empresaId = sessionMBean.getEmpresaActual().getId();
			List<Tramite> trams = empresasEJB.obtenerTramitesEmpresa(empresaId, actualizar);
			setTramites(trams);
			String msg = sessionMBean.getTextos().get("se_cargaron_n_tramites");
			if(msg!=null) {
				addInfoMessage(msg.replace("{cant}", ""+(trams==null?"0":""+trams.size())));
			}
		} catch (UserException ex) {
      addErrorMessage(ex);
      ex.printStackTrace();
      setTramites(null);
    }	catch (Exception ex) {
			addErrorMessage(sessionMBean.getTextos().get("no_se_pudo_cargar_tramites"));
			ex.printStackTrace();
			setTramites(null);
		}
	}

	public List<SelectItem> getTimezones() {
		String[] ids = TimeZone.getAvailableIDs();
		List<SelectItem> timezones = new ArrayList<SelectItem>();
		timezones.add(new SelectItem("", sessionMBean.getTextos().get("misma_que_la_de_la_empresa")));
		for (String id : ids) {
			TimeZone timezone = TimeZone.getTimeZone(id);
			long hours = TimeUnit.MILLISECONDS.toHours(timezone.getRawOffset());
			long minutes = TimeUnit.MILLISECONDS.toMinutes(timezone.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
			timezones.add(new SelectItem(id, id+" (GMT "+(hours > 0?"+":"")+hours+":"+(minutes < 10?"0":"")+minutes+")"));
		}
		return timezones;		
	}

	@SuppressWarnings("unchecked")
	public void copiar(ActionEvent event) {
		Agenda a=((Row<Agenda>)agendasDataTable.getRowData()).getData(); 
		if (a != null) {
			try {
				agendasEJB.copiarAgenda(a);
				sessionMBean.cargarAgendas();
        addInfoMessage(sessionMBean.getTextos().get("agenda_copiada"), MSG_ID);
			} catch (BusinessException e) {
				addErrorMessage(e, MSG_ID);
			} catch (ApplicationException e) {
				addErrorMessage(e, MSG_ID);
			} catch (UserException e) {
				addErrorMessage(e, MSG_ID);
			}
		}
	}

	public List<SelectItem> getIdiomasDisponibles() {
		return sessionMBean.getIdiomasSoportados();
	}
	
	public List<String> getIdiomasSeleccionados() {
		return agendaSessionMBean.getIdiomasSeleccionados();
	}
	
	public void setIdiomasSeleccionados(List<String> idiomasSeleccionados) {
		agendaSessionMBean.setIdiomasSeleccionados(idiomasSeleccionados);
	}
	
	public void agregarTramite() {
	  limpiarMensajesError();
	  TramiteAgenda tramite = new TramiteAgenda();
	  tramite.setAgenda(this.agendaNueva);
	  this.agendaNueva.getTramites().add(tramite);
	}
	
  public void cambioTramite(ValueChangeEvent event) {
    limpiarMensajesError();
    UIInput input = (UIInput) event.getComponent();
    TramiteAgenda tramiteAgenda = (TramiteAgenda) input.getAttributes().get("tramiteAgenda");
    String tramIdViejo = (String) event.getOldValue();
    String tramId = (String) event.getNewValue();
    //Si el valor nuevo es "0" y el viejo no está en la lista de trámites el cambio es porque
    //cambió la lista de trámites y no puede seleccionar el trámite correspondiente. En este
    //caso se cambia el id del trámite pero no el código y nombre
    if("0".equals(tramId) && !mapTramites.containsKey(tramIdViejo)) {
      tramiteAgenda.setTramiteId(null);
    }else {
      if(tramId == null || !mapTramites.containsKey(tramId)) {
        tramiteAgenda.setTramiteId(null);
        tramiteAgenda.setTramiteCodigo("");
        tramiteAgenda.setTramiteNombre("");
      }else {
        Tramite tramite = mapTramites.get(tramId);
        tramiteAgenda.setTramiteId(tramId);
        tramiteAgenda.setTramiteNombre(tramite.getNombre());
        String[] partes = tramId.split("-");
        if(partes.length>1) {
          tramiteAgenda.setTramiteCodigo(partes[1]);
        }
      }
    }
  }

  public void quitarTramite(Integer ordinal) {
    limpiarMensajesError();
    TramiteAgenda ta = this.agendaNueva.getTramites().remove(ordinal.intValue());
    ta.setAgenda(null);
  }
  
  public void agregarTramiteMod() {
    limpiarMensajesError();
    TramiteAgenda tramite = new TramiteAgenda();
    tramite.setAgenda(this.agendaNueva);
    this.getAgendaSeleccionada().getTramites().add(tramite);
  }
  
  public void quitarTramiteMod(Integer ordinal) {
    limpiarMensajesError();
    TramiteAgenda ta = this.getAgendaSeleccionada().getTramites().remove(ordinal.intValue());
    ta.setAgenda(null);
  }
  
}