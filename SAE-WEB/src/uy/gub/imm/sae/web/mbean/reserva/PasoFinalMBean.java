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

package uy.gub.imm.sae.web.mbean.reserva;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.utilidades.Metavariables;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;
import uy.gub.imm.sae.web.common.TicketUtiles;

/**
 * Presenta todos los datos de la reserva y da la opción de imprimir un recibo.
 * @author im2716295
 *
 */
public class PasoFinalMBean extends BaseMBean {

	private Recursos recursosEJB;
	
	private SesionMBean sesionMBean;
	
	private List<DatoDelRecurso> infoRecurso;
	
	private UIComponent campos;
	
	private Logger logger = Logger.getLogger(PasoFinalMBean.class);

	private boolean errorInit;
	
	@PostConstruct
	public void init() {
		errorInit = false;
		try {
			recursosEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getRecursos();
			if (sesionMBean.getAgenda() == null || sesionMBean.getRecurso() == null || sesionMBean.getReservaConfirmada() == null) {
				addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
				errorInit = true;
				return;
			}
		} catch (ApplicationException ex) {
			addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
			errorInit = true;
		}
	}	

	public SesionMBean getSesionMBean() {
		return sesionMBean;
	}

	public void setSesionMBean(SesionMBean sesionMBean) {
		this.sesionMBean = sesionMBean;
	}
	
	public String getAgendaNombre() {
		if (sesionMBean.getAgenda() != null) {
			return sesionMBean.getAgenda().getNombre();
		}
		else {
			return null;
		}
	}	
	
	public String getEtiquetaDelRecurso() {
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		} else {
			return "";
		}
	}
	
	public List<DatoDelRecurso> getInfoRecurso() {

		if (infoRecurso == null) {
			if (sesionMBean.getRecurso() != null) {
				try {
					infoRecurso = recursosEJB.consultarDatosDelRecurso(sesionMBean.getRecurso());
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
			Recurso recurso = sesionMBean.getRecurso();
			//El chequeo de recurso != null es en caso de un acceso directo a la pagina, es solo
			//para que no salte la excepcion en el log, pues de todas formas sera redirigido a una pagina de error.
			if (campos.getChildCount() == 0 && recurso != null) {
				mensajeError = "RESERVA: ";
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso, sesionMBean.getTimeZone());
				Reserva rtmp = sesionMBean.getReservaConfirmada();
				if (rtmp == null) {
					mensajeError += "nulo";
				} else {
					mensajeError += rtmp.getId() + ":" + rtmp.getFechaCreacion() + ":";
					if (rtmp.getDatosReserva()== null) {
						mensajeError += "DatosReserva nulo";
					}
				}
				
				Map<String, Object> valores = obtenerValores(sesionMBean.getReservaConfirmada().getDatosReserva());
				FormularioDinamicoReserva formularioDin = new FormularioDinamicoReserva(valores, sesionMBean.getFormatoFecha());
				formularioDin.armarFormulario(agrupaciones, null);
				UIComponent formulario = formularioDin.getComponenteFormulario();
				campos.getChildren().add(formulario);
			}
		} catch (Exception e) {
			logger.error(mensajeError, e);
			addErrorMessage(sesionMBean.getTextos().get("ha_ocurrido_un_error_grave"));
			errorInit = true;
		}
	}

	public String getDescripcion() {
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if(textoAgenda != null) {
			String str = textoAgenda.getTextoTicketConf();
			if(str!=null) {
        Agenda agenda = sesionMBean.getAgenda();
			  HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String linkBase = request.getScheme()+"://"+request.getServerName();
	      if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
	        linkBase = linkBase + ":" + request.getServerPort();
	      }
	      Reserva reserva = sesionMBean.getReservaConfirmada();
	      Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
	      String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri="+reserva.getId();
	      String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&r="+recurso.getId()+"&ri="+reserva.getId();
			  str = Metavariables.remplazarMetavariables(str, reserva, sesionMBean.getFormatoFecha(), sesionMBean.getFormatoHora(), linkCancelacion, linkModificacion);
				return str;
			}	else {
				return "";
			}
		}
		else {
			return "";
		}
	}
	

	/**
	 * @param datos de alguna reserva
	 * @return retorna los valores de cada dato en un mapa cuya clave es el nombre del campo 
	 */
	private Map<String, Object> obtenerValores(Set<DatoReserva> datos) {
		Map<String, Object> valores = new HashMap<String, Object>();
		for (DatoReserva dato : datos) {
			valores.put(dato.getDatoASolicitar().getNombre(), dato.getValor());
		}
		return valores;
	}
	
	public void beforePhase (PhaseEvent event) {
		disableBrowserCache(event);
	}
	
	public Date getDiaSeleccionado() {
		return sesionMBean.getDiaSeleccionado();
	}

	public Date getHoraSeleccionada() {
		if (sesionMBean.getDisponibilidad() != null) {
			return sesionMBean.getDisponibilidad().getHoraInicio();
		} else {
			return null;
		}
	}

	public String getReservaNumero() {
		if(sesionMBean.getReservaConfirmada() != null && sesionMBean.getReservaConfirmada().getNumero()!=null) {
			return "No. " + sesionMBean.getReservaConfirmada().getNumero();
		}
		return "";
	}
	
	public String getRecursoDescripcion() {
		Recurso recurso = sesionMBean.getRecurso();
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

  public String generarTicket(boolean imprimir) {
    TicketUtiles ticketUtiles = new TicketUtiles();
    ticketUtiles.generarTicket(sesionMBean.getEmpresaActual(), sesionMBean.getAgenda(), sesionMBean.getRecurso(), sesionMBean.getTimeZone(), 
        sesionMBean.getReservaConfirmada(), sesionMBean.getFormatoFecha(), sesionMBean.getFormatoHora(), sesionMBean.getTextos(), imprimir);
    return null;
  }
	
	public String getUrlCancelacion() {
		Reserva reserva = sesionMBean.getReservaConfirmada();
		if (reserva != null) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String linkCancelacion = request.getScheme()+"://"+request.getServerName();
			if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
				linkCancelacion = linkCancelacion + ":" + request.getServerPort();
			}
			Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
			
			linkCancelacion = linkCancelacion + "/sae/cancelarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri="+reserva.getId();
			return linkCancelacion;
		}
		return null;
	}
	
	public String getUrlTramite() {
		return sesionMBean.getUrlTramite();
	}
	
	public boolean isErrorInit() {
		return errorInit;
	}
	
  @PreDestroy
  public void preDestroy() {
    
    try {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      
      this.campos = null;
      if(this.infoRecurso != null) {
        this.infoRecurso.clear();
      }
      this.infoRecurso = null;
      this.recursosEJB = null;
      this.sesionMBean = null;
      
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
      
    }
  }
	
}
