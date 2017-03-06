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

package uy.gub.imm.sae.web.common;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.component.outputpanel.OutputPanel;

import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.mbean.administracion.SessionMBean;
import uy.gub.imm.sae.web.mbean.reserva.SesionMBean;

public abstract class BaseMBean {
	
	public static final String version = "2.0.3";

	protected static final String FORM_ID = "form";
	
	/** 
	 * Si el error sucede en capa de negocio y/o en la lógica del presentación 
	 * de forma tal que permite volver a una página coherente, se utiliza este mensaje de error genérico 
	 * que se le desplega al usuario en un componente message general.
	 */
	private static final String MENSAJE_MANTENIMIENTO = "Sistema en mantenimiento, por favor intente más tarde.";
	
	/**
	 * Si el error que sucede no es esperado o 
	 * no pude mostrarse una pagina coherente con el mensaje anterior, 
	 * entonces se realiza un redirect a la pagina de error 
	 * configurada en el faces-config para este outcome
	 */
	static protected String ERROR_PAGE_OUTCOME = "error";
	
	public String getVersion() {
		return version;
	}

	/**
	 * Desmarca los campos marcados con error (fondo rojo)
	 */
	protected void limpiarMensajesError() {
		for(UIComponent comp : FacesContext.getCurrentInstance().getViewRoot().getChildren()) {
			limpiarMensajeError(comp);
		}
	}
	
  /**
   * Desmarca los campos marcados con error (fondo rojo)
   */
	private void limpiarMensajeError(UIComponent comp) {
		if(comp instanceof OutputPanel) {
			OutputPanel panel = (OutputPanel)comp;
			if(panel.getStyleClass()!=null && panel.getStyleClass().contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
				panel.setStyleClass(panel.getStyleClass().replace(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR, ""));;
			}
		}else if(comp instanceof HtmlPanelGroup) {
			HtmlPanelGroup panel = (HtmlPanelGroup)comp;
			if(panel.getStyleClass()!=null && panel.getStyleClass().contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
				panel.setStyleClass(panel.getStyleClass().replace(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR, ""));;
			}
		}
		if(comp.getChildCount()>0) {
			for(UIComponent comp1 : comp.getChildren()) {
				limpiarMensajeError(comp1);
			}
		}
	}

	/**
	 * Añade un mensaje de error sin especificar un componente en pantalla
	 */
	protected void addErrorMessage (Exception ex) {
		addErrorMessage(ex, FORM_ID);
	}

  /**
   * Añade un mensaje de error sin especificar un componente en pantalla
   */
	protected void addErrorMessage (String mensaje) {
		addErrorMessage(new UserException(mensaje, mensaje), FORM_ID);
	}

  /**
   * Añade un mensaje de error asociados a todos los componentes en pantalla indicados
   */
	protected void addErrorMessage (String mensaje, String... idsComponentes) {
		for(String idComponente : idsComponentes) {
			addErrorMessage(new UserException(mensaje, mensaje), idComponente);
		}
	}

  /**
   * Añade un mensaje de error asociado al componente en pantalla indicado
   * Para que funcione correctamente, el componente debe estar dentro de un <p:outputPanel> que a su vez
   * esté dentro de otro <p:outputPanel> con styleClass="form-group"; además, dentro del primer outputPanel
   * debería haber un componente <p:message>. También funciona con <h:panelGroup>.
   * <p:outputPanel styleClass="form-group">
   *   <p:outputLabel value="Etiqueta" for="idComponente" />
   *   <p:outputPanel>
   *     <p:inputText id="idComponente" value="..." />
   *     <p:message for="idComponente" />
   *   </p:outputPanel>
   * </p:outputPanel>
   */
	protected void addErrorMessage(Exception e, String idComponente) {
		FacesMessage m;
		if (e instanceof UserException) {
			UserException ue = (UserException) e;
			m = new FacesMessage(FacesMessage.SEVERITY_ERROR, getTexto(ue.getCodigoError()), null);
		} else {
			m = new FacesMessage(FacesMessage.SEVERITY_ERROR, MENSAJE_MANTENIMIENTO , null);
			e.printStackTrace(System.err);
		}
		FacesContext.getCurrentInstance().addMessage(idComponente, m);
		//Ver si se puede poner la clase al contenedor (el parent inmediato o el siguiente debe ser un outputPanel
		//o panelGroup con la clase form-group
		try {
			UIComponent comp = FacesContext.getCurrentInstance().getViewRoot().findComponent(idComponente);
			if(comp!=null && comp instanceof UIInput) {
				comp = comp.getParent();
				if(comp!=null) {
					comp = comp.getParent();
					if(comp!=null && comp instanceof OutputPanel) {
						OutputPanel panel = (OutputPanel)comp;
						if(panel.getStyleClass()!=null && panel.getStyleClass().contains("form-group")) {
							addErrorSytleClassToFormGroup(panel);
						}
					}else if(comp instanceof HtmlPanelGroup) {
						HtmlPanelGroup panel = (HtmlPanelGroup)comp;
						if(panel.getStyleClass()!=null && panel.getStyleClass().contains("form-group")) {
							addErrorSytleClassToFormGroup(panel);
						}
					}
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addErrorSytleClassToFormGroup(OutputPanel panel) {
		String styleClass = panel.getStyleClass();
		if(!styleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
			styleClass = styleClass+" "+FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR;
			panel.setStyleClass(styleClass);
		}
	}
	
	private void addErrorSytleClassToFormGroup(HtmlPanelGroup panel) {
		String styleClass = panel.getStyleClass();
		if(!styleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
			styleClass = styleClass+" "+FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR;
			panel.setStyleClass(styleClass);
		}
	}
	
	protected void addInfoMessage (String mensaje) {
		addInfoMessage(mensaje, FORM_ID);
	}
	
	protected void addInfoMessage (String mensaje, String idComponente) {
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, mensaje, null);
		FacesContext.getCurrentInstance().addMessage(idComponente, m);
	}
	
	protected void addAdvertenciaMessage (String mensaje) {
		addAdvertenciaMessage(mensaje, FORM_ID);
	}
	
	protected void addAdvertenciaMessage(String mensaje, String idComponente) {
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN, mensaje, null);
		FacesContext.getCurrentInstance().addMessage(idComponente, m);
	}
	
	/**
	 * Redirecciona a la pagina asociada al respectivo outcome
	 * segun lo configurado en las reglas de navegacion del faces-config
	 * */
	protected void redirect(String from_outcome) {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (! fc.getResponseComplete()) {
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, from_outcome);
			fc.responseComplete();
		}
	}
	
	/**
	 * Deshabilta el cache del navegador para la pagina que se esta respondiendo.
	 */
	protected void disableBrowserCache(PhaseEvent phaseEvent) { 
		if (phaseEvent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      FacesContext facesContext = phaseEvent.getFacesContext();
      HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Cache-Control", "no-cache");
      response.addHeader("Cache-Control", "no-store");
      response.addHeader("Cache-Control", "must-revalidate");
      response.addHeader("Expires", "Mon, 1 Jan 2006 05:00:00 GMT"); //en el pasado
		}
	}

	/**
	 * Devuelve el texto correspondiente a la clave indicada en el idioma actualmente seleccionado.
	 * @param clave
	 * @return
	 */
	private String getTexto(String clave) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> textos = null;
		try {
			SessionMBean sessionMBean = context.getApplication().evaluateExpressionGet(context, "#{sessionMBean}", SessionMBean.class);
			textos = sessionMBean.getTextos();
		}catch(Throwable ex) {
			textos = null;
		}
		if(textos == null) {
			try {
				SesionMBean sesionMBean = context.getApplication().evaluateExpressionGet(context, "#{sesionMBean}", SesionMBean.class);
				textos = sesionMBean.getTextos();
			}catch(Throwable ex) {
				textos = null;
			}
		}
		if(textos == null || !textos.containsKey(clave)) {
			return clave;
		}
		return textos.get(clave);
	}
	
	protected TextoAgenda getTextoAgenda(Agenda agenda, String idioma) {
		TextoAgenda textoAgenda = null;
		if(agenda.getTextosAgenda()!=null) {
			textoAgenda = agenda.getTextosAgenda().get(idioma);
		}
		if(textoAgenda == null) {
			for(TextoAgenda ta : agenda.getTextosAgenda().values()) {
				if(ta.isPorDefecto()) {
					textoAgenda = ta;
				}
			}
		}
		if(textoAgenda == null) {
			textoAgenda = new TextoAgenda();
		}
		return textoAgenda;
	}
	
	protected TextoRecurso getTextoRecurso(Recurso recurso, String idioma) {
		TextoRecurso textoRecurso = null;
		if(recurso != null) {
			if(recurso.getTextosRecurso()!=null) {
				textoRecurso = recurso.getTextosRecurso().get(idioma);
			}
			if(textoRecurso == null) {
				for(TextoAgenda ta : recurso.getAgenda().getTextosAgenda().values()) {
					//Los textosRecurso no tienen idioma por defecto, se usa el mismo que el de textosAgenda
					if(ta.isPorDefecto()) {
						textoRecurso = recurso.getTextosRecurso().get(ta.getIdioma());;
					}
				}
			}
		}
		if(textoRecurso == null) {
			textoRecurso = new TextoRecurso();
		}
		return textoRecurso;
	}
	
	public String getSimboloAnterior() {
		//Hex: 25C0, Dec: 9664 -- &#9664;
		return "◀";
	}

	public String getSimboloSiguiente() {
		//Hex: 25B6, Dec: 9654 -- &#9654;
		return "▶";
	}
	
	
}
