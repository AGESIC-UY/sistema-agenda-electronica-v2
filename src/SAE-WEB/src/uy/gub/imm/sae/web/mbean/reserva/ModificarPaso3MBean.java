package uy.gub.imm.sae.web.mbean.reserva;
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

import java.util.Date;
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
import uy.gub.imm.sae.business.ejb.facade.Comunicaciones;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;

/**
 * Maneja la lógica de generación dinámica de los componentes gráficos que
 * constituyen los datos a solicitarse para realizar la reserva.
 * @author im2716295
 *
 *
 */
public class ModificarPaso3MBean extends BaseMBean {

	static Logger logger = Logger.getLogger(ModificarPaso3MBean.class);
	public static final String FORMULARIO_ID = "datosReserva";
	public static final String DATOS_RESERVA_MBEAN = "datosReservaMBean";
	
	private AgendarReservas agendarReservasEJB;
	private Comunicaciones comunicacionesEJB;

	private SesionMBean sesionMBean;
	
	private String tramiteNombre;
	private String aceptaCondiciones;

	private boolean errorInit;
	
	@PostConstruct
	public void init() {
		errorInit = false;
		try {
			if (sesionMBean.getAgenda() == null || sesionMBean.getRecurso() == null) {
				addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
				errorInit = true;
				return;
			}
      agendarReservasEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getAgendarReservas();
      comunicacionesEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getComunicaciones();
		} catch (ApplicationException ex) {
			addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
			errorInit = true;
		}
		try {
  		Reserva reserva = sesionMBean.getReservaModificar1();
  		tramiteNombre = reserva.getTramiteNombre();
		}catch(Exception ex) {
		  ex.printStackTrace();
      addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
      errorInit = true;
		}
	}
	
  public void beforePhase(PhaseEvent event) {
    disableBrowserCache(event);
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
		} else {
			return null;
		}
	}	

	public String getRecursoDescripcion() {
		if (sesionMBean.getRecurso() != null) {
			String descripcion = sesionMBean.getRecurso().getNombre();
			if(descripcion != null && !descripcion.equals(sesionMBean.getRecurso().getDireccion())) {
				descripcion = descripcion + " - " + sesionMBean.getRecurso().getDireccion();
			}
			return descripcion;
		} else {
			return null;
		}
	}
	
	public String getDescripcion() {
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			String str = textoAgenda.getTextoPaso3();
			if(str == null) {
				return "";
			}	else {
				return str;
			}
		}	else {
			return null;
		}		
	}

	public String getDescripcionRecurso() {
		TextoRecurso textoRecurso = getTextoRecurso(sesionMBean.getRecurso(), sesionMBean.getIdiomaActual());
		if (textoRecurso != null) {
			return textoRecurso.getTextoPaso3();
		} else {
			return null;
		}		
	}
	
	public String getEtiquetaDelRecurso() {
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		} else {
			return null;
		}
	}

	public Date getFechaSeleccionada() {
		return sesionMBean.getDiaSeleccionado();
	}
	
	public Date getHoraSeleccionada() {
		if (sesionMBean.getDisponibilidad() != null) {
			return sesionMBean.getDisponibilidad().getHoraInicio();
		} else {
			return null;
		}
	}
	
	public String getClausulaConsentimiento() {
		Empresa empresa = sesionMBean.getEmpresaActual();
		String texto = sesionMBean.getTextos().get("clausula_de_consentimiento_informado_texto");
		texto = texto.replace("{finalidad}", empresa.getCcFinalidad());
		texto = texto.replace("{responsable}", empresa.getCcResponsable());
		texto = texto.replace("{direccion}", empresa.getCcDireccion());
		return texto;
	}
	
	public String confirmarReserva() {
		limpiarMensajesError();
		sesionMBean.setReservaConfirmada(null);
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
				return null;
			}

      Reserva rConfirmada = agendarReservasEJB.modificarReserva(sesionMBean.getEmpresaActual().getId(), sesionMBean.getAgenda().getId(),
          sesionMBean.getRecurso().getId(), sesionMBean.getReservaModificar1().getId(), sesionMBean.getReservaModificar2().getId(), sesionMBean.getIdiomaActual());
      
      try {
        //Enviar el mail de cancelación de la reserva original
        comunicacionesEJB.enviarComunicacionesCancelacion(sesionMBean.getEmpresaActual(), sesionMBean.getReservaModificar1(), sesionMBean.getIdiomaActual(), 
            sesionMBean.getFormatoFecha(), sesionMBean.getFormatoHora());
  			//Enviar el mail de confirmacion de la reserva nueva
  			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Recurso recurso = rConfirmada.getDisponibilidades().get(0).getRecurso(); 
  			Agenda agenda = recurso.getAgenda();
  			String linkBase = request.getScheme()+"://"+request.getServerName();
  			if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
  			  linkBase = linkBase + ":" + request.getServerPort();
  			}
  			String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri="+rConfirmada.getId();
  			String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&r="+recurso.getId()+"&ri="+rConfirmada.getId();
  			comunicacionesEJB.enviarComunicacionesConfirmacion(sesionMBean.getEmpresaActual(), linkCancelacion, linkModificacion, rConfirmada, sesionMBean.getIdiomaActual(), 
  			    sesionMBean.getFormatoFecha(), sesionMBean.getFormatoHora());
      }catch(UserException ex) {
        addAdvertenciaMessage(sesionMBean.getTextos().get(ex.getCodigoError()));
      }
			//La reserva se confirmo, por lo tanto muevo la reseva a confirmada en la sesion para evitar problemas de reload de pagina.
			sesionMBean.setReservaConfirmada(rConfirmada);
			sesionMBean.setReserva(null);
		}catch(Exception ex) {
      addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
      ex.printStackTrace();
      return null;
    }
		return "reservaConfirmada";
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

	public boolean isErrorInit() {
		return errorInit;
	}

  public String getTramiteNombre() {
    return tramiteNombre;
  }

  @PreDestroy
  public void preDestroy() {
    try {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      this.agendarReservasEJB = null;
      this.sesionMBean = null;
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
    }
  }
  
  public Date getFechaOriginal() {
    if(sesionMBean.getReservaModificar1()==null) {
      return null;
    }
    return sesionMBean.getReservaModificar1().getDisponibilidades().get(0).getFecha();
  }
  
  public Date getHoraOriginal() {
    if(sesionMBean.getReservaModificar1()==null) {
      return null;
    }
    return sesionMBean.getReservaModificar1().getDisponibilidades().get(0).getHoraInicio();
  }
  
  public Date getFechaNueva() {
    if(sesionMBean.getReservaModificar2()==null) {
      return null;
    }
    return sesionMBean.getReservaModificar2().getDisponibilidades().get(0).getFecha();
  }
  
  public Date getHoraNueva() {
    if(sesionMBean.getReservaModificar2()==null) {
      return null;
    }
    return sesionMBean.getReservaModificar2().getDisponibilidades().get(0).getHoraInicio();
  }
  
}