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

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;

/**
 * Presenta todos los datos de la reserva y da la opción de imprimir un recibo.
 * @author im2716295
 *
 */
public class MultiplePasoFinalMBean extends BaseMBean {

	private SesionMBean sesionMBean;
	
	private AgendarReservas agendarReservasEJB;
	
	private Logger logger = Logger.getLogger(MultiplePasoFinalMBean.class);

	private boolean errorInit;
	
  private String aceptaCondiciones;
	
	
	@PostConstruct
	public void init() {
		errorInit = false;
		try {
		  agendarReservasEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getAgendarReservas();
			if (sesionMBean.getAgenda() == null || sesionMBean.getRecurso() == null) {
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
	
	public void beforePhase (PhaseEvent event) {
		disableBrowserCache(event);
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

	public boolean isErrorInit() {
		return errorInit;
	}
	
  @PreDestroy
  public void preDestroy() {
    
    try {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      this.sesionMBean = null;
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
    }
  }
  
  public List<Reserva> getReservas() {
    TokenReserva token = sesionMBean.getTokenReserva();
    return agendarReservasEJB.obtenerReservasMultiples(token.getId(), false);
  }
  
  public String getClausulaConsentimiento() {
    Empresa empresa = sesionMBean.getEmpresaActual();
    String texto = sesionMBean.getTextos().get("clausula_de_consentimiento_informado_texto");
    texto = texto.replace("{finalidad}", empresa.getCcFinalidad());
    texto = texto.replace("{responsable}", empresa.getCcResponsable());
    texto = texto.replace("{direccion}", empresa.getCcDireccion());
    return texto;
  }
  
  public String getAceptaCondiciones() {
    return aceptaCondiciones;
  }

  public void setAceptaCondiciones(String aceptaCondiciones) {
    this.aceptaCondiciones = aceptaCondiciones;
  }

  // =========================================================
  // Captcha
  
  private String textoIndicativoCaptcha;
  private String textoCaptchaUsuario;
  
  public String getTextoIndicativoCaptcha() {
    Random rand = new Random();
    
    //Elegir una frase de todas las disponibles, si no hay ninguna solo se usa la palabra "test"
    String pregunta = "test";
    String respuesta = "test";
    Map<String, String> preguntasCaptcha = sesionMBean.getPreguntasCaptcha();
    if(preguntasCaptcha != null && !preguntasCaptcha.isEmpty()) {
      String[] preguntas = preguntasCaptcha.keySet().toArray(new String[preguntasCaptcha.size()]);
      int ind = rand.nextInt(preguntas.length);
      pregunta = preguntas[ind];
      respuesta = preguntasCaptcha.get(pregunta);
    }
    textoIndicativoCaptcha = pregunta;
    sesionMBean.setPaso3Captcha(respuesta);
    return textoIndicativoCaptcha;
  }

  public String getTextoCaptchaUsuario() {
    return textoCaptchaUsuario;
  }

  public void setTextoCaptchaUsuario(String textoCaptchaUsuario) {
    this.textoCaptchaUsuario = textoCaptchaUsuario;
  }
  
  public void recargarCaptcha() {
    sesionMBean.setPaso3Captcha(null);
    HtmlPanelGroup captchaGroup = (HtmlPanelGroup) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:captcha");
    String captchaStyleClass = captchaGroup.getStyleClass();
    if(captchaStyleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
      captchaStyleClass = captchaStyleClass.replace(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR,"");
      captchaGroup.setStyleClass(captchaStyleClass);
    }
  }

  public String otraReserva() {
    return "otraReserva";
  }
  
  public String inicio() {
    return sesionMBean.getUrlPaso1Reserva() + "&faces-redirect=true";
  }
  
  public boolean confirmarReservas() {
    limpiarMensajesError();
    try {
      boolean hayError = false;
      HtmlPanelGroup clausulaGroup = (HtmlPanelGroup) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:clausula");
      String clausulaStyleClass = clausulaGroup.getStyleClass();
      if (this.aceptaCondiciones==null || !this.aceptaCondiciones.equals("SI")) {
        hayError = true;
        addErrorMessage(sesionMBean.getTextos().get("debe_aceptar_los_terminos_de_la_clausula_de_consentimiento_informado"), FORM_ID, FORM_ID+":condiciones");
        if(!clausulaStyleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
          clausulaStyleClass = clausulaStyleClass+" "+FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR;
          clausulaGroup.setStyleClass(clausulaStyleClass);
        }
      }else {
        if(clausulaStyleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
          clausulaStyleClass = clausulaStyleClass.replace(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR,"");
          clausulaGroup.setStyleClass(clausulaStyleClass);
        }
      }
      HtmlPanelGroup captchaGroup = (HtmlPanelGroup) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:captcha");
      String captchaStyleClass = captchaGroup.getStyleClass();
      if (textoCaptchaUsuario==null || textoCaptchaUsuario.trim().isEmpty()) {
        hayError = true;
        addErrorMessage(sesionMBean.getTextos().get("debe_responder_la_pregunta_de_seguridad"), FORM_ID, FORM_ID+":secureText");
        if(!captchaStyleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
          captchaStyleClass = captchaStyleClass+" "+FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR;
          captchaGroup.setStyleClass(captchaStyleClass);
        }
      }else if (!textoCaptchaUsuario.equalsIgnoreCase(sesionMBean.getPaso3Captcha())) {
        hayError = true;
        addErrorMessage(sesionMBean.getTextos().get("la_respuesta_a_la_pregunta_de_seguridad_no_es_correcta"), FORM_ID, FORM_ID+":secureText");
        if(!captchaStyleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
          captchaStyleClass = captchaStyleClass+" "+FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR;
          captchaGroup.setStyleClass(captchaStyleClass);
        }
      }else {
        if(captchaStyleClass.contains(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR)) {
          captchaStyleClass = captchaStyleClass.replace(FormularioDinamicoReserva.STYLE_CLASS_DATO_CON_ERROR,"");
          captchaGroup.setStyleClass(captchaStyleClass);
        }
      }

      if(hayError) {
        return false;
      }
      
      TokenReserva tokenReserva = sesionMBean.getTokenReserva();
      
      String transaccionPadreId = null;
      Long pasoPadre = null;
      if(sesionMBean.getCodigoTrazabilidadPadre()!=null) {
        String transaccionPadre[] = sesionMBean.getCodigoTrazabilidadPadre().split("-");
        transaccionPadreId = transaccionPadre[0];
        if(transaccionPadre.length==2) {
          try {
            pasoPadre = Long.valueOf(transaccionPadre[1]);
          }catch(Exception ex) {
            pasoPadre = null;
          }
        }
      }
      
      tokenReserva = agendarReservasEJB.confirmarReservasMultiples(sesionMBean.getEmpresaActual().getId(), tokenReserva.getId(), transaccionPadreId, pasoPadre, false);
      
      //Armar el texto del ticket
      String textoTicket = null;
      Agenda agenda = tokenReserva.getRecurso().getAgenda();
      if(sesionMBean.getIdiomaActual()!=null && !sesionMBean.getIdiomaActual().isEmpty() && agenda.getTextosAgenda().containsKey(sesionMBean.getIdiomaActual())) {
        textoTicket = agenda.getTextosAgenda().get(sesionMBean.getIdiomaActual()).getTextoTicketConf();
      }else {
        if(!agenda.getTextosAgenda().isEmpty()) {
          textoTicket = agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoTicketConf();
        }
      }
      //Armar el texto del link de cancelación (template)
      HttpServletRequest httpRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      String linkCancelacion = httpRequest.getScheme()+"://"+httpRequest.getServerName();
      if("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=80 || "https".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=443) {
        linkCancelacion = linkCancelacion + ":" + httpRequest.getServerPort();
      }
      linkCancelacion = linkCancelacion + "/sae/cancelarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri={idReserva}";
      //Enviar las comunicaciones
      agendarReservasEJB.enviarComunicacionesConfirmacion(linkCancelacion, tokenReserva, sesionMBean.getIdiomaActual(), sesionMBean.getFormatoFecha(), sesionMBean.getFormatoHora());
      //La reserva se confirmo, por lo tanto muevo la reseva a confirmada en la sesion para evitar problemas de reload de pagina.
      sesionMBean.setReserva(null);
      sesionMBean.setTokenReserva(tokenReserva);
    }catch(Exception ex) {
      addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
      ex.printStackTrace();
      return false;
    }
    return true;
  }
  
  public TokenReserva getTokenReserva() {
    return sesionMBean.getTokenReserva();
  }
  
  public String getUrlTramite() {
    return sesionMBean.getUrlTramite();
  }
  
  public String cancelarReservas() {
    try {
      TokenReserva token = sesionMBean.getTokenReserva();
      if(token != null) {
        token = agendarReservasEJB.cancelarReservasMultiples(token.getId());
        sesionMBean.setTokenReserva(token);
      }
      return null;
    }catch(UserException uEx) {
      logger.error("Error al cancelar las reservas múltiples", uEx);
      addErrorMessage(sesionMBean.getTextos().get("ha_ocurrido_un_error_no_solucionable"), "form");
    }
    return null;    
  }
  
  public void marcarReservaCancelar(Reserva reserva) {
    sesionMBean.setReservaCancelar(reserva);
  }

  public String cancelarReserva() {
    try {
      Reserva reserva = sesionMBean.getReservaCancelar();
      if(reserva == null) {
        return null;
      }
      TokenReserva token = reserva.getToken();
      if(token != null && token.getEstado()==Estado.P) {
        token = agendarReservasEJB.cancelarReservaMultiple(token.getId(), reserva.getId());
        sesionMBean.setTokenReserva(token);
        sesionMBean.setReservaCancelar(null);
      }
      return null;
    }catch(UserException uEx) {
      logger.error("Error al cancelar las reservas múltiples", uEx);
      addErrorMessage(sesionMBean.getTextos().get("ha_ocurrido_un_error_no_solucionable"), "form");
    }
    return null;    
  }
  
}
