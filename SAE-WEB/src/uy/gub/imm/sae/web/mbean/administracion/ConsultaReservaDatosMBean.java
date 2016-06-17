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
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
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
		// Se controla que se haya Marcado una agenda para trabajar con los
		// recursos
		if (sessionMBean.getAgendaMarcada() == null) {
			addErrorMessage("Debe tener una agenda seleccionada", MSG_ID);
			
		}

		// Se controla que se haya Marcado un recurso
		if (sessionMBean.getRecursoMarcado() == null) {
			addErrorMessage("Debe tener un recurso seleccionado", MSG_ID);
		}
		if ((sessionMBean.getRecursoMarcado() == null)||(sessionMBean.getRecursoMarcado() == null))
		{
			return;
		}
		try {
			// guardo en session los datos a solicitar del recurso
			List<DatoASolicitar> listaDatoSolicitar = recursosEJB
					.consultarDatosSolicitar(sessionMBean.getRecursoMarcado());

/*			List<DatoASolicitar> listaDatoSolicitar = sessionMBean.getRecursoMarcado().getDatoASolicitar();
		*/
			Map<String, DatoASolicitar> datoSolicMap = new HashMap<String, DatoASolicitar>();
			for (DatoASolicitar dato : listaDatoSolicitar) {
				datoSolicMap.put(dato.getNombre(), dato);
			}
			setDatosASolicitar(datoSolicMap);

		} 
	catch (ApplicationException e) {
			addErrorMessage(e.getMessage());
			return;
		}

	}

	public String volverPagInicio() {

		// Este objeto limpia la session
		//SessionCleaner sc = new SessionCleaner();
		return "volver";
	}

	
	public void buscarReservaDatos(ActionEvent e) {
		boolean huboError = false;
		ArrayList<Reserva> reservas = new ArrayList<Reserva>();

		List<DatoReserva> datos = FormularioDinReservaClient.obtenerDatosReserva(datosFiltroReservaMBean, datosASolicitar);

		if (sessionMBean.getAgendaMarcada() == null && !huboError) {
			huboError = true;
			addErrorMessage("Debe seleccionar una agenda.", MSG_ID);
		}

		if (sessionMBean.getRecursoMarcado() == null && !huboError) {
			huboError = true;
			addErrorMessage("Debe seleccionar un recurso.", MSG_ID);
		}

		if (!huboError) {

			// Voy a negocio a buscar las reservas
			reservas = (ArrayList<Reserva>) consultaEJB.consultarReservaDatos(
					datos, sessionMBean.getRecursoMarcado());
			this.consReservaDatosSessionMBean.setListaReservas(reservas);
			if (reservas.isEmpty()) {
				addErrorMessage(
						"No se encontraron reservas con los filtros de búsqueda.",
						MSG_ID);
			} else {
				this.consReservaDatosSessionMBean.setListaReservas(reservas);
			}
		}

	}

	public String verDetalleReserva() {
		int iSelectedPos = getReservasDataTable().getRowIndex();
		Reserva r = this.consReservaDatosSessionMBean.getListaReservas().get(
				iSelectedPos);
		this.consReservaDatosSessionMBean.setReservaDatos(r);
		this.consReservaDatosSessionMBean.setDisponibilidad(r.getDisponibilidades().get(0));
		return "detalleReserva";
	}

/*	public void armarFormularioEdicionDinamico(Recurso recurso,
			UIComponent filtroConsulta, List<AgrupacionDato> agrupaciones)
			throws Exception {
		// Recurso recurso = sessionMBean.getRecursoMarcado();

		// El chequeo de recurso != null es en caso de un acceso directo a la
		// pagina, es solo
		// para que no salte la excepcion en el log, pues de todas formas sera
		// redirigido a una pagina de error.
		if (filtroConsulta.getChildCount() == 0 && recurso != null) {
			// List<AgrupacionDato> agrupaciones =
			// recursosEJB.consultarDefinicionDeCampos(recurso);
			setDatosASolicitar(FormularioDinamicoReserva
					.obtenerCampos(agrupaciones));
			FormularioDinamicoReserva formularioDin = new FormularioDinamicoReserva(
					DATOS_FILTRO_RESERVA_MBEAN, FORMULARIO_ID);
			UIComponent formulario = formularioDin
					.armarFormulario(agrupaciones);
			filtroConsulta.getChildren().add(formulario);
		}
	}

	private List<DatoReserva> obtenerDatosReserva(Map<String, Object> origen) {

		List<DatoReserva> datos = new ArrayList<DatoReserva>();

		for (String nombre : datosFiltroReservaMBean.keySet()) {
			Object valor = datosFiltroReservaMBean.get(nombre);

			if (valor != null && !valor.toString().equals("")) {
				DatoReserva dato = new DatoReserva();
				dato.setDatoASolicitar(getDatosASolicitar().get(nombre));
				// TODO DatoReserva implemetar correctamente el parser de object
				// a string para cada tipo.
				dato.setValor(valor.toString());
				datos.add(dato);
			}
		}
		return datos;
	}

	/** **************************************************************************** */
	/*
	 * private void armarFormularioLecturaDinamico(Recurso recurso, Reserva
	 * reserva, UIComponent campos){
	 *  /* Recurso recurso = sessionMBean.getRecursoMarcado(); Reserva reserva =
	 * consultaSessionMBean.getReserva();
	 */
	/*
	 * try{ //El chequeo de recurso != null es en caso de un acceso directo a la
	 * pagina, es solo //para que no salte la excepcion en el log, pues de todas
	 * formas sera redirigido a una pagina de error. /* if
	 * (campos.getChildCount() == 0 && recurso != null &&
	 * (!reserva.getDatosReserva().isEmpty())) { List<AgrupacionDato>
	 * agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso); Map<String,
	 * Object> valores = obtenerValores(reserva.getDatosReserva());
	 * FormularioDinamicoReserva formularioDin = new
	 * FormularioDinamicoReserva(valores); UIComponent formulario =
	 * formularioDin.armarFormulario(agrupaciones);
	 * campos.getChildren().add(formulario); } } catch (Exception e) {
	 * addErrorMessage(e); }
	 *  }
	 * 
	 * private Map<String, Object> obtenerValores(List<DatoReserva> datos) {
	 * 
	 * Map<String, Object> valores = new HashMap<String, Object>();
	 * 
	 * for (DatoReserva dato : datos) { //TODO parsear el valor de string a
	 * object segun el tipo del DatoASolicitar
	 * valores.put(dato.getDatoASolicitar().getNombre(), dato.getValor()); }
	 * 
	 * return valores; }
	 * 
	 */
	/** **************************************************************************** */
	public void beforePhaseConsultarReservaDatos(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean
					.setPantallaTitulo("Consultar reserva por Datos de Reserva");
		}
	}

	public void beforePhaseDetalleReserva(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean
					.setPantallaTitulo("Consultar reserva por Datos de Reserva");
		}
	}

	public UIComponent getFiltroConsulta() {
		return filtroConsulta;
	}

	public void setFiltroConsulta(UIComponent filtroConsulta) {
		this.filtroConsulta = filtroConsulta;
		if (this.sessionMBean.getRecursoMarcado() != null)
		{
			try {
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefCamposTodos(this.sessionMBean.getRecursoMarcado());
				FormularioDinReservaClient.armarFormularioEdicionDinamico(this.sessionMBean.getRecursoMarcado(), filtroConsulta, agrupaciones,sessionMBean.getFormatoFecha());
				} catch (BusinessException be) {
					addErrorMessage(be, MSG_ID);
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
			List<AgrupacionDato> agrupaciones = recursosEJB
					.consultarDefinicionDeCampos(sessionMBean.getRecursoMarcado(), sessionMBean.getTimeZone());
			FormularioDinReservaClient.armarFormularioLecturaDinamico(
					sessionMBean.getRecursoMarcado(),
					this.consReservaDatosSessionMBean.getReservaDatos(),
					this.campos, agrupaciones,sessionMBean.getFormatoFecha());
		} catch (BusinessException be) {
			addErrorMessage(be, MSG_ID);
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
