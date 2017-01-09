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
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Validadores;

public class AgrupacionMBean extends BaseMBean {
	public static final String MSG_ID = "pantalla";

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	public SessionMBean sessionMBean;
	private DatoASSessionMBean datoASSessionMBean;
	
	private AgrupacionDato agrupacionDatoNuevo;

	@PostConstruct
	public void initRecurso(){
		//Se controla que se haya Marcado una agenda y recurso
		if (sessionMBean.getAgendaMarcada() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() == null) {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}
	public DatoASSessionMBean getDatoASSessionMBean() {
		return datoASSessionMBean;
	}

	public void setDatoASSessionMBean(DatoASSessionMBean datoASSessionMBean) {
		this.datoASSessionMBean = datoASSessionMBean;
	}
	
	public void beforePhaseModificarAgrupaciones(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("gestionar_agrupaciones"));
		}
	}

	/*
	 * MODIFICACION
	 * 
	 */
	
	public void seleccionarAgrupacion(int rowIndex) {
	  limpiarMensajesError();
		AgrupacionDato a = datoASSessionMBean.getAgrupaciones().get(rowIndex);
		if (a != null) {
			datoASSessionMBean.setAgrupacionSeleccionada(a);
		}else{
			datoASSessionMBean.setAgrupacionSeleccionada(null);
		}
		agrupacionDatoNuevo=null;
	}

	public void modificarAgrupacion(ActionEvent event) {
	  
	  limpiarMensajesError();
		
		AgrupacionDato agrupacion = datoASSessionMBean.getAgrupacionSeleccionada();
		if (agrupacion != null) {
			boolean huboError = false;
			if (agrupacion.getNombre() == null || agrupacion.getNombre().trim().isEmpty()) {
				addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_agrupacion_es_obligatorio"), "formModificarAgrup:nombre");
				huboError = true;
			}else {
	      boolean nombreValido = Validadores.validarNombreIdentificador(agrupacion.getNombre());
	      if (!nombreValido) {
	        addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_agrupacion_no_es_valido"), "formModificarAgrup:nombre");
	        huboError = true;
	      }
	    }
      if (agrupacion.getEtiqueta() == null || agrupacion.getEtiqueta().trim().isEmpty()) {
        addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_de_la_agrupacion_es_obligatoria"), "formModificarAgrup:etiqueta");
        huboError = true;
      }
			if (agrupacion.getOrden() == null) {
				addErrorMessage(sessionMBean.getTextos().get("el_orden_de_la_agrupacion_es_obligatorio"), "formModificarAgrup:VOrdAgrup");
				huboError = true;
			}else {
				if (agrupacion.getOrden().intValue()<1) {
					addErrorMessage(sessionMBean.getTextos().get("el_orden_de_la_agrupacion_debe_ser_mayor_a_cero"), "formModificarAgrup:VOrdAgrup");
					huboError = true;
				}
			}
			
			if (!huboError){
				try {
					recursosEJB.modificarAgrupacionDato(agrupacion);
					datoASSessionMBean.clearAgrupaciones();
					addInfoMessage(sessionMBean.getTextos().get("agrupacion_modificada"),	MSG_ID);
					datoASSessionMBean.setAgrupacionSeleccionada(null);
				} catch (Exception e) {
					addErrorMessage(e, MSG_ID);
				}
			}
			
			datoASSessionMBean.cargarAgrupaciones();
			
		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agrupacion_seleccionada"),	MSG_ID);
		}
	}
	
	public void cancelarModificarAgrupacion(ActionEvent event) {
		datoASSessionMBean.setAgrupacionSeleccionada(null);
		datoASSessionMBean.cargarAgrupaciones();
	}
	
	public void seleccionarAgrupacionParaEliminar(int rowIndex) {
	  limpiarMensajesError();
		AgrupacionDato a = datoASSessionMBean.getAgrupaciones().get(rowIndex);
		datoASSessionMBean.setAgrupacionSeleccionada(a);
	}

	public void eliminarAgrupacion(ActionEvent event) {
		eliminarAgrupacion(true);
	}
	
	public void eliminarAgrupacionYDatos(ActionEvent event) {
		eliminarAgrupacion(false);
	}
	
	private void eliminarAgrupacion(boolean controlarDatos) {
		AgrupacionDato agrupacion = datoASSessionMBean.getAgrupacionSeleccionada();
		if (agrupacion != null && agrupacion.getBorrarFlag()) {
			try {
				recursosEJB.eliminarAgrupacionDato(agrupacion, controlarDatos);
				datoASSessionMBean.clearAgrupaciones();
				datoASSessionMBean.setAgrupacionSeleccionada(null);
				datoASSessionMBean.setAgrupacionSeleccionada(null);
				setAgrupacionDatoNuevo(null);
				addInfoMessage(sessionMBean.getTextos().get("agrupacion_eliminada"), MSG_ID);
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
			
			datoASSessionMBean.cargarAgrupaciones();
			
		} else {
			if (!agrupacion.getBorrarFlag()) {
				addErrorMessage(sessionMBean.getTextos().get("no_se_permite_eliminar_esta_agrupacion"),	MSG_ID);
			}else {
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agrupacion_seleccionada"),	MSG_ID);
			}
			
		}
	}

	public void mostrarCrearAgrupacion() {
		setAgrupacionDatoNuevo(new AgrupacionDato());
		datoASSessionMBean.setAgrupacionSeleccionada(null);
	}

	public void crearAgrupacion(ActionEvent e) {
	  
	  limpiarMensajesError();
	  
		boolean huboError = false;
		if (sessionMBean.getAgendaMarcada() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
			huboError = true;
		}
		if (sessionMBean.getRecursoMarcado() == null) {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			huboError = true;
		}

		AgrupacionDato agrupacion = getAgrupacionDatoNuevo();
		if (agrupacion.getNombre() == null || agrupacion.getNombre().trim().isEmpty()) {
			addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_agrupacion_es_obligatorio"), "formModificarAgrup:cNombre");
			huboError = true;
		}else {
      boolean nombreValido = Validadores.validarNombreIdentificador(agrupacion.getNombre());
      if (!nombreValido) {
        addErrorMessage(sessionMBean.getTextos().get("el_nombre_de_la_agrupacion_no_es_valido"), "formModificarAgrup:cNombre");
        huboError = true;
      }
    }
    if (agrupacion.getEtiqueta() == null || agrupacion.getEtiqueta().trim().isEmpty()) {
      addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_de_la_agrupacion_es_obligatoria"), "formModificarAgrup:cEtiqueta");
      huboError = true;
    }
		if (agrupacion.getOrden() == null) {
			addErrorMessage(sessionMBean.getTextos().get("el_orden_de_la_agrupacion_es_obligatorio"), "formModificarAgrup:VOrdUpd");
			huboError = true;
		}else {
			if (agrupacion.getOrden().intValue()<1) {
				addErrorMessage(sessionMBean.getTextos().get("el_orden_de_la_agrupacion_debe_ser_mayor_a_cero"), "formModificarAgrup:VOrdUpd");
				huboError = true;
			}
		}
		
		if (!huboError){
			try {
				recursosEJB.agregarAgrupacionDato(sessionMBean.getRecursoMarcado(), agrupacion);
				datoASSessionMBean.clearAgrupaciones();
				setAgrupacionDatoNuevo(null);
				addInfoMessage(sessionMBean.getTextos().get("agrupacion_creada"), MSG_ID);
				setAgrupacionDatoNuevo(null);
			} catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
			}
		}
	}
	
	public void cancelarCrearAgrupacion(ActionEvent event) {
		setAgrupacionDatoNuevo(null);
	}

	public AgrupacionDato getAgrupacionDatoNuevo() {
		return agrupacionDatoNuevo;
	}

	public void setAgrupacionDatoNuevo(AgrupacionDato agrupacionDatoNuevo) {
		this.agrupacionDatoNuevo = agrupacionDatoNuevo;
	}
	
	public boolean getMostrarModificarAgrupacion() {
		return datoASSessionMBean.getAgrupacionSeleccionada()!=null;
	}
	
	public boolean getMostrarAgregarAgrupacion() {
		return agrupacionDatoNuevo!=null;
	}
	
}
