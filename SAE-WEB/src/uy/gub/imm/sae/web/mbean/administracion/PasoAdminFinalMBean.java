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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.event.PhaseEvent;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;
/**
 * Presenta todos los datos de la reserva y da la opción de imprimir un recibo.
 * @author im2716295
 *
 */
public class PasoAdminFinalMBean extends PasoAdminMBean {

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	private SessionMBean sessionMBean;
	
	private List<DatoDelRecurso> infoRecurso;
	
	private UIComponent campos;
	
	private Logger logger = Logger.getLogger(PasoAdminFinalMBean.class);

	
	@PostConstruct
	public void init() {

			if (sessionMBean.getAgenda() == null || sessionMBean.getRecurso() == null || sessionMBean.getReservaConfirmada() == null) {
				
				logger.debug("RESERVA: ESTADO INVALIDO PASO FINAL" + "  Agenda: "+sessionMBean.getAgenda()+ " Recurso: "+sessionMBean.getRecurso()+ " ReservaConfirmada: "+sessionMBean.getReservaConfirmada());
				
				redirect(ESTADO_INVALIDO_PAGE_OUTCOME);
				return;
			}
		
	}	

	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}
	
	public String getAgendaNombre() {
		if (sessionMBean.getAgenda() != null) {
			return sessionMBean.getAgenda().getNombre();
		}
		else {
			return null;
		}
	}	
	
	public String getEtiquetaDelRecurso() {
		//TextoAgenda textoAgenda = sessionMBean.getAgenda().getTextoAgenda();
		TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgenda(), sessionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		}
		else {
			return null;
		}
	}
	
	public List<DatoDelRecurso> getInfoRecurso() {

		if (infoRecurso == null) {
			if (sessionMBean.getRecurso() != null) {
				try {
					infoRecurso = recursosEJB.consultarDatosDelRecurso(sessionMBean.getRecurso());
					if (infoRecurso.isEmpty()) {
						infoRecurso = null;
					}
				} catch (Exception e) {
					addErrorMessage(e);
				}
			}
		}
		return infoRecurso;
	}	
	
	public UIComponent getCampos() {
		return campos;
	}
	
	public void setCampos(UIComponent campos) {
		
		this.campos = campos;
		
		String mensajeError = "";
		try {
			Recurso recurso = sessionMBean.getRecurso();

			
			//El chequeo de recurso != null es en caso de un acceso directo a la pagina, es solo
			//para que no salte la excepcion en el log, pues de todas formas sera redirigido a una pagina de error.
			if (campos.getChildCount() == 0 && recurso != null) {
				
				
				mensajeError = "RESERVA: ";
				
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso);
				
				Reserva rtmp = sessionMBean.getReservaConfirmada();

				if (rtmp == null) {
					mensajeError += "nulo";
				}
				else {
					mensajeError += rtmp.getId() + ":" + rtmp.getFechaCreacion() + ":";
					
					if (rtmp.getDatosReserva()== null) {
						mensajeError += "DatosReserva nulo";
					}
				
				}
				
				Map<String, Object> valores = obtenerValores(sessionMBean.getReservaConfirmada().getDatosReserva());
				FormularioDinamicoReserva formularioDin = new FormularioDinamicoReserva(valores, sessionMBean.getFormatoFecha());
				formularioDin.armarFormulario(agrupaciones, null);
				UIComponent formulario = formularioDin.getComponenteFormulario();
				campos.getChildren().add(formulario);
			}
		} catch (Exception e) {
			logger.error(mensajeError, e);
			redirect(ERROR_PAGE_OUTCOME);
		}
	}

	public String getDescripcion() {
		//TextoAgenda textoAgenda = sessionMBean.getAgenda().getTextoAgenda();
		TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgenda(), sessionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			String str = textoAgenda.getTextoTicketConf();
			if(str!=null)
				return str;
			else
				return "";
		}
		else {
			return null;
		}
	}
	

	/**
	 * @param datos de alguna reserva
	 * @return retorna los valores de cada dato en un mapa cuya clave es el nombre del campo 
	 */
	private Map<String, Object> obtenerValores(Set<DatoReserva> datos) {
		
		Map<String, Object> valores = new HashMap<String, Object>();
		
		for (DatoReserva dato : datos) {
			//TODO parsear el valor de string a object segun el tipo del DatoASolicitar
			valores.put(dato.getDatoASolicitar().getNombre(), dato.getValor());
		}
		
		return valores;
	}
	
	public void beforePhase (PhaseEvent event) {
		disableBrowserCache(event);
	}
	
	public Date getDiaSeleccionado() {
		return sessionMBean.getDiaSeleccionado();
	}

	public Date getHoraSeleccionada() {
		if (sessionMBean.getDisponibilidad() != null) {
			return sessionMBean.getDisponibilidad().getHoraInicio();
		} else {
			return null;
		}
	}

	public String getReservaNumero() {
		if(sessionMBean.getReservaConfirmada() != null) {
			return "No. " + sessionMBean.getReservaConfirmada().getNumero();
		}
		return "";
	}
	
	public String getRecursoDescripcion() {
		Recurso recurso = sessionMBean.getRecurso();
		if (recurso != null) {
			String descripcion = recurso.getNombre();
			if(descripcion != null && !descripcion.equals(recurso.getDireccion())) {
				descripcion = descripcion + " - " + recurso.getDireccion();
			}
			return  descripcion;
		} else {
			return null;
		}
	}

	
	
}

	
	
