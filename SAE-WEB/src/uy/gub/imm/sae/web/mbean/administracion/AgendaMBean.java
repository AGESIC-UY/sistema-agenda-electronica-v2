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
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.Agendas;
import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.entity.Agenda;
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
		if(agendaCrear.getDescripcion() == null || agendaCrear.getDescripcion().trim().equals("")){
			addErrorMessage(sessionMBean.getTextos().get("la_descripcion_de_la_agenda_es_obligatoria"), "form:descripcionAgenda");
			hayErrores = true;
		}
		if(agendaCrear.getTramiteCodigo() == null || agendaCrear.getTramiteCodigo().trim().isEmpty()){
			addErrorMessage(sessionMBean.getTextos().get("el_codigo_del_tramite_es_obligatorio"), "form:codigoTramite");
			hayErrores = true;
		}
		if(hayErrores) {
			return;
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
		Agenda a = sessionMBean.getAgendaSeleccionada();
		if (a != null){
 			try {
 				if(sessionMBean.getAgendaMarcada()!=null && sessionMBean.getAgendaMarcada().getId().equals(a.getId())) {
 					sessionMBean.desseleccionarAgenda();
 				}
 				agendasEJB.eliminarAgenda(a);
				sessionMBean.cargarAgendas();
 				agendasSeleccion = null;
				addInfoMessage(sessionMBean.getTextos().get("agenda_eliminada"), MSG_ID);
			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			}
		}
		else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		
	}

	@SuppressWarnings("unchecked")
	public String modificar() { 
		this.rowSelect = (Row<Agenda>)agendasDataTable.getRowData();
		if (this.rowSelect != null){
			Agenda agenda = rowSelect.getData();
			
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
		}
		else {
			sessionMBean.setAgendaSeleccionada(null);
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
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
			if(agendaSeleccionada.getDescripcion() == null || agendaSeleccionada.getDescripcion().trim().isEmpty()){
				addErrorMessage(sessionMBean.getTextos().get("la_descripcion_de_la_agenda_es_obligatoria"), "form:descripcionAgenda");
				hayErrores = true;
			}
			if(agendaSeleccionada.getTramiteCodigo() == null || agendaSeleccionada.getTramiteCodigo().trim().isEmpty()){
				addErrorMessage(sessionMBean.getTextos().get("el_codigo_del_tramite_es_obligatorio"), "form:codigoTramite");
				hayErrores = true;
			}
			if(hayErrores) {
				return null;
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
		}
		else {
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
			sessionMBean.setPantallaTitulo("Crear agenda");
		}
	}

	public void beforePhaseModificarConsultar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Consultar agenda");
		}
	}
	
	public void beforePhaseModificar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Modificar agenda");
		}
	}
	
	public void beforePhaseEliminar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Eliminar agenda");
		}
	}

	public void beforePhaseConsultar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Consultar agenda");
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
		//Cargar los tramites
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
		} catch (Exception uEx) {
			addErrorMessage(sessionMBean.getTextos().get("no_se_pudo_cargar_tramites"));
			setTramites(null);
		}
	}
	
	public void cambioTramite(ValueChangeEvent event) {
		limpiarMensajesError();
		String tramId = (String) event.getNewValue();
		if(tramId == null || !mapTramites.containsKey(tramId)) {
			this.agendaNueva.setTramiteId(null);
			this.agendaNueva.setTramiteCodigo("");
			this.agendaNueva.setNombre("");
			this.agendaNueva.setDescripcion("");
		}else {
			Tramite tramite = mapTramites.get(tramId);
			this.agendaNueva.setTramiteId(tramId);
			this.agendaNueva.setNombre(tramite.getNombre());
			this.agendaNueva.setDescripcion(tramite.getQuees());
			String[] partes = tramId.split("-");
			if(partes.length>1) {
				this.agendaNueva.setTramiteCodigo(partes[1]);
			}
		}
	}
	
	public void cambioTramiteMod(ValueChangeEvent event) {
		limpiarMensajesError();
		String tramId = (String) event.getNewValue();
		if(tramId!=null && mapTramites.containsKey(tramId)) {
			String[] partes = tramId.split("-");
			if(partes.length>1) {
				this.getAgendaSeleccionada().setTramiteCodigo(partes[1]);
			}
		}
	}
	
/*
	public void actualizarTramiteAgenda(String tramId) {
		if(tramId == null || !mapTramites.containsKey(tramId)) {
			this.agendaNueva.setTramiteId(null);
			this.agendaNueva.setTramiteCodigo("");
			this.agendaNueva.setNombre("");
			this.agendaNueva.setDescripcion("");
		}else {
			Tramite tramite = mapTramites.get(tramId);
			this.agendaNueva.setTramiteId(tramId);
			this.agendaNueva.setNombre(tramite.getNombre());
			this.agendaNueva.setDescripcion(tramite.getQuees());
			String[] partes = tramId.split("-");
			if(partes.length>1) {
				this.agendaNueva.setTramiteCodigo(partes[1]);
			}
		}
	}
*/	

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
	public void copiar(ActionEvent event) 
	{
		Agenda a=((Row<Agenda>)agendasDataTable.getRowData()).getData();
		if (a != null) 
		{
			try {
				agendasEJB.copiarAgenda(a);
				sessionMBean.cargarAgendas();
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
}