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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Comunicaciones;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.AutocompletarException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAutocompletarException;
import uy.gub.imm.sae.exception.ErrorValidacionCommitException;
import uy.gub.imm.sae.exception.ErrorValidacionException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionException;
import uy.gub.imm.sae.exception.ValidacionIPException;
import uy.gub.imm.sae.exception.ValidacionPorCampoException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;

/**
 * Maneja la lógica de generación dinámica de los componentes gráficos que
 * constituyen los datos a solicitarse para realizar la reserva.
 * @author im2716295
 *
 *
 */
public class Paso3MBean extends BaseMBean {

	static Logger logger = Logger.getLogger(Paso3MBean.class);
	public static final String FORMULARIO_ID = "datosReserva";
	public static final String DATOS_RESERVA_MBEAN = "datosReservaMBean";
	
	private AgendarReservas agendarReservasEJB;
	private Recursos recursosEJB;
	private Comunicaciones comunicacionesEJB;
	
	private SesionMBean sesionMBean;
	
	private UIComponent campos;
	private Map<String, Object> datosReservaMBean;
	private FormularioDinamicoReserva formularioDin;

	private Map<String, TramiteAgenda> tramitesAgenda;
	private List<SelectItem> tramites;
	private String tramiteCodigo;
	private String aceptaCondiciones;

	private boolean errorInit;
	
	private boolean yaExisteReservaCamposClave = false;
	
	@PostConstruct
	public void init() {
		errorInit = false;
		yaExisteReservaCamposClave = false;
		try {
			agendarReservasEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getAgendarReservas();
			if (sesionMBean.getAgenda() == null || sesionMBean.getRecurso() == null
					|| !agendarReservasEJB.isRecursoVisibleInternet(sesionMBean.getRecurso())) {
				addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
				errorInit = true;
				return;
			}
		  	recursosEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getRecursos();
		  	comunicacionesEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getComunicaciones();
		} catch (ApplicationException | BusinessException ex) {
			addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
			errorInit = true;
		}
		try {
      this.tramiteCodigo = null;
      tramitesAgenda = new HashMap<String, TramiteAgenda>();
      tramites = new ArrayList<SelectItem>();
      
  		Reserva reserva = sesionMBean.getReserva();
  		Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
  		List<TramiteAgenda> tramites0 = agendarReservasEJB.consultarTramites(agenda);

  		//Si se especificó el trámite a realizar en la URL (parámetro "q") no se permite al ciudadano 
  		//seleccionar el trámite (si el trámite especificado no corresponde a un trámite asociado a la
  		//agenda se ignora el parámetro).
  		//Si no se especificó el trámite en la URL pero la agenda está asociada a un único trámite se
  		//selecciona auntomáticamente el único trámite y tampoco se permite modificar la selección
  		String codigoTramite = sesionMBean.getCodigoTramite();
  		if(codigoTramite != null) {
  		  //Buscar el trámite correspondiente al código
  		  for(TramiteAgenda tramite : tramites0) {
  		    if(codigoTramite.equals(tramite.getTramiteCodigo())) {
            tramiteCodigo = tramite.getTramiteCodigo();
            tramitesAgenda.put(tramiteCodigo, tramite);
            break;
  		    }
  		  }
  		}
  		//Si no se identifico un trámite continuar con el procedimiento usual
  		if(tramiteCodigo == null) {
        if(tramites0.size()==1) {
          //Hay un solo trámite asociado a la agenda, se selecciona solo ese
          TramiteAgenda tramite = tramites0.get(0);
          tramiteCodigo = tramite.getTramiteCodigo();
          tramitesAgenda.put(tramiteCodigo, tramite);
        }else {
          //El ciudadano debe seleccionar el trámite
          tramites.add(new SelectItem("", "Sin especificar"));
      		for(TramiteAgenda tramite : tramites0) {
      		  tramitesAgenda.put(tramite.getTramiteCodigo(), tramite);
      		  tramites.add(new SelectItem(tramite.getTramiteCodigo(), tramite.getTramiteNombre()));
      		}
        }
  		}
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
			return  descripcion;
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
	
	public String getClausulaConsentimiento() {
		Empresa empresa = sesionMBean.getEmpresaActual();
		String texto = sesionMBean.getTextos().get("clausula_de_consentimiento_informado_texto");
		texto = texto.replace("{finalidad}", empresa.getCcFinalidad());
		texto = texto.replace("{responsable}", empresa.getCcResponsable());
		texto = texto.replace("{direccion}", empresa.getCcDireccion());
		return texto;
	}
	
	public UIComponent getCampos() {
		return campos;
	}
	
	public void setCampos(UIComponent campos) {
		this.campos = campos;
		try {
			Recurso recurso = sesionMBean.getRecurso();
			//El chequeo de recurso != null es en caso de un acceso directo a la pagina, es solo
			//para que no salte la excepcion en el log, pues de todas formas sera redirigido a una pagina de error.
			if (campos.getChildCount() == 0 && recurso != null) {
				if (formularioDin == null) {
					List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso, sesionMBean.getTimeZone());
					sesionMBean.setDatosASolicitar(obtenerCampos(agrupaciones));
					String valoresCampos = sesionMBean.getParmsDatosCiudadano();
					if(valoresCampos!=null) {
						cargarCamposPorDefecto(valoresCampos, agrupaciones);
					}
					formularioDin = new FormularioDinamicoReserva(DATOS_RESERVA_MBEAN, FORMULARIO_ID, FormularioDinamicoReserva.TipoFormulario.EDICION, this.datosReservaMBean, sesionMBean.getFormatoFecha());
					formularioDin.armarFormulario(agrupaciones, null);
				}
				UIComponent formulario = formularioDin.getComponenteFormulario();
				campos.getChildren().add(formulario);
			}
		} catch (Exception e) {
			addErrorMessage(e, FORMULARIO_ID);
		}
	}
	
	public Map<String,Object> getDatosReservaMBean() {
		return datosReservaMBean;
	}

	public void setDatosReservaMBean(Map<String,Object> datosReservaMBean) {
		this.datosReservaMBean = datosReservaMBean;
	}

	public String confirmarReserva() {
		limpiarMensajesError();
		yaExisteReservaCamposClave = false;
		sesionMBean.setReservaConfirmada(null);
		sesionMBean.setEnvioCorreoReserva(Boolean.TRUE);
		try {
			boolean hayError = false;
			if(this.tramiteCodigo==null || this.tramiteCodigo.isEmpty()) {
        hayError = true;
        addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_el_tramite"), FORM_ID, FORM_ID+":tramite");
			}
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
			List<String> idComponentes = new ArrayList<String>();
			Set<DatoReserva> datos = new HashSet<DatoReserva>();
			for (String nombre : datosReservaMBean.keySet()) {
				Object valor = datosReservaMBean.get(nombre);
				idComponentes.add(nombre);
				if (valor!=null && ! valor.toString().trim().equals("")) {
					DatoReserva dato = new DatoReserva();
					dato.setDatoASolicitar(sesionMBean.getDatosASolicitar().get(nombre));
					String sValor = valor.toString().trim();
					if(Tipo.STRING.equals(dato.getDatoASolicitar().getTipo()) || Tipo.NUMBER.equals(dato.getDatoASolicitar().getTipo())) {
  					//Esto es un workaround para un problema en la codificación de los strings que tienen tildes
  	        sValor = Utiles.convertirISO88591aUTF8(sValor);
					}
					dato.setValor(sValor);
					datos.add(dato);
				}
			}
			FormularioDinamicoReserva.desmarcarCampos(idComponentes, campos);
			Reserva reserva = sesionMBean.getReserva();
			reserva.setDatosReserva(datos);
			agendarReservasEJB.validarDatosReserva(sesionMBean.getEmpresaActual(), reserva);
			if(hayError) {
				return null;
			}
      reserva.setTramiteCodigo(this.tramiteCodigo);
      reserva.setTramiteNombre(tramitesAgenda.get(this.tramiteCodigo).getTramiteNombre());
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
			boolean confirmada = false;
			while (!confirmada) {
				try {
					Reserva rConfirmada = agendarReservasEJB.confirmarReserva(sesionMBean.getEmpresaActual(), reserva, transaccionPadreId, pasoPadre, false);
					reserva.setSerie(rConfirmada.getSerie());
					reserva.setNumero(rConfirmada.getNumero());
					reserva.setCodigoSeguridad(rConfirmada.getCodigoSeguridad());
					reserva.setTrazabilidadGuid(rConfirmada.getTrazabilidadGuid());
					confirmada = true;
				} catch (AccesoMultipleException e){
					//Reintento hasta tener exito, en algun momento no me va a dar acceso multiple.
				}
			}
			
			//La reserva se confirmo, por lo tanto muevo la reseva a confirmada en la sesion para evitar problemas de reload de pagina.
			sesionMBean.setReservaConfirmada(reserva);
			sesionMBean.setReserva(null);
			
			//Enviar el mail de confirmacion
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso(); 
			Agenda agenda = recurso.getAgenda();
			String linkBase = request.getScheme()+"://"+request.getServerName();
			if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
			  linkBase = linkBase + ":" + request.getServerPort();
			}
			String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri="+reserva.getId();
      String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&r="+recurso.getId()+"&ri="+reserva.getId();
      comunicacionesEJB.enviarComunicacionesConfirmacion(sesionMBean.getEmpresaActual(), linkCancelacion, linkModificacion, reserva, sesionMBean.getIdiomaActual(), 
			    sesionMBean.getFormatoFecha(), sesionMBean.getFormatoHora());

		}catch(ValidacionPorCampoException e) {
			//Alguno de los campos no tiene el formato esperado
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				if (e.getMensaje(i) != null) {
					DatoASolicitar dato = sesionMBean.getDatosASolicitar().get(e.getNombreCampo(i));
					String mensaje = sesionMBean.getTextos().get(e.getMensaje(i));
					if(mensaje == null) {
						mensaje = sesionMBean.getTextos().get("el_valor_ingresado_no_es_aceptable");
					}
					mensaje = mensaje.replace("{campo}", dato.getEtiqueta());
					addErrorMessage(mensaje, "form", "form:"+dato.getNombre());
					idComponentesError.add(e.getNombreCampo(i));
				}
				idComponentesError.add(e.getNombreCampo(i));
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		}catch(ErrorValidacionException e) {
			//Algun grupo de campos no es valido según alguna validacion
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				idComponentesError.add(e.getNombreCampo(i));
			}
			String mensaje = e.getMensaje(0);
			for(int i = 1; i < e.getCantMensajes(); i++) {
				mensaje += "  |  "+e.getMensaje(i);
			}
			addErrorMessage(mensaje);
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		}catch(ErrorValidacionCommitException e) { 
			//Algun grupo de campos no es valido según alguna validacion
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				idComponentesError.add(e.getNombreCampo(i));
			}
			String mensaje = e.getMensaje(0);
			for(int i = 1; i < e.getCantMensajes(); i++) {
				mensaje += "  |  "+e.getMensaje(i);
			}
			addErrorMessage(mensaje);
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		}catch (ValidacionClaveUnicaException vcuEx) {
		  //Ya hay otra reserva con el mismo valor en todos los campos clave
		  yaExisteReservaCamposClave = true;
		  String mensajeReservaPrevia = "ya_tiene_una_reserva_para_el_dia_seleccionado";
		  Recurso recurso = sesionMBean.getRecurso();
		  if(recurso.getPeriodoValidacion()!=null && recurso.getPeriodoValidacion()>0) {
		    Reserva reserva = sesionMBean.getReserva();
	      Date fecha = reserva.getDisponibilidades().get(0).getFecha();
	      Date fechaDesde = fecha;
	      Date fechaHasta = fecha;
	      Integer periodoValidacion = recurso.getPeriodoValidacion();
	      if(periodoValidacion==null) {
	        periodoValidacion=0;
	      }
	      Calendar cal = new GregorianCalendar();
	      cal.setTime(fecha);
	      cal.add(Calendar.DATE, -1*periodoValidacion);
	      cal.set(Calendar.HOUR_OF_DAY, 0);
	      cal.set(Calendar.MINUTE, 0);
	      cal.set(Calendar.SECOND, 0);
	      cal.set(Calendar.MILLISECOND, 0);
	      fechaDesde = cal.getTime();
	      cal.setTime(fecha);
	      cal.add(Calendar.DATE, 1*periodoValidacion);
	      cal.set(Calendar.HOUR_OF_DAY, 23);
	      cal.set(Calendar.MINUTE, 59);
	      cal.set(Calendar.SECOND, 59);
	      cal.set(Calendar.MILLISECOND, 999);
	      fechaHasta = cal.getTime();
        DateFormat df = new SimpleDateFormat(sesionMBean.getFormatoFecha());
		    String periodo = df.format(fechaDesde) + " - " + df.format(fechaHasta);
		    mensajeReservaPrevia = sesionMBean.getTextos().get("ya_tiene_una_reserva_para_el_periodo").replace("{periodo}", periodo);;
		  }
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < vcuEx.getCantCampos(); i++) {
				idComponentesError.add(vcuEx.getNombreCampo(i));
        addErrorMessage(mensajeReservaPrevia, "form:"+vcuEx.getNombreCampo(i));
			}
      //Si hay más de un trámite también forma parte de la clave
			if(tramites.size()>1) {
        idComponentesError.add("tramite");
        addErrorMessage(mensajeReservaPrevia, "form:tramite");
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		}catch(ValidacionIPException vipEx) {
		  Recurso recurso = sesionMBean.getRecurso();
		  int cantReservas = (recurso.getCantidadPorIP()==null?0:recurso.getCantidadPorIP());
	    addErrorMessage(sesionMBean.getTextos().get("limite_de_reservas_para_la_direccion_ip_alcanzado").replace("{cantidad}", ""+cantReservas));
	    return null;
		}catch (ValidacionException e) {
			//Faltan campos requeridos
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				DatoASolicitar dato = sesionMBean.getDatosASolicitar().get(e.getNombreCampo(i));
				String mensaje = sesionMBean.getTextos().get("debe_completar_el_campo_campo").replace("{campo}", dato.getEtiqueta());
				addErrorMessage(mensaje, "form", "form:"+dato.getNombre());
				idComponentesError.add(e.getNombreCampo(i));
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		}catch(BusinessException bEx) {
		  //Seguramente esto fue lanzado por una Accion
			addErrorMessage(bEx.getMessage());
			bEx.printStackTrace();
			return null;
		}catch(UserException uEx) {
			sesionMBean.setEnvioCorreoReserva(Boolean.FALSE);
			datosReservaMBean.clear();
			return "reservaConfirmada";
		}
		catch(Exception ex) {
      addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
      ex.printStackTrace();
      return null;
    } finally {
      //Si no hay una reserva confirmada es porque falló alguna validación y hay que deshacer el cambio de caracteres
      if(sesionMBean.getReservaConfirmada() == null) {
        for(String nombre : datosReservaMBean.keySet()) {
          Object valor = datosReservaMBean.get(nombre);
          DatoASolicitar datoSol = sesionMBean.getDatosASolicitar().get(nombre);
          if(valor!=null && datoSol!=null && (Tipo.STRING.equals(datoSol.getTipo()) || Tipo.NUMBER.equals(datoSol.getTipo())) && !valor.toString().trim().isEmpty()) {
            //Esto es un workaround para un problema en la codificación de los strings que tienen tildes
            String sValor = Utiles.convertirISO88591aUTF8(valor.toString().trim());
            datosReservaMBean.put(nombre, sValor);
          }
        }
      }
    }
		//Blanqueo el formulario de datos de la reserva
		datosReservaMBean.clear();
		return "reservaConfirmada";
	}

	
	/**
	 * @param agrupaciones de algun recurso
	 * @return retorna todos los datos a solicitar definidos en la lista de agrupaciones 
	 *         en un mapa cuya clave es el nombre del campo 
	 */
	private Map<String, DatoASolicitar> obtenerCampos(List<AgrupacionDato> agrupaciones) {
		
		Map<String, DatoASolicitar> camposXnombre = new HashMap<String, DatoASolicitar>();
		
		for (AgrupacionDato agrupacion : agrupaciones) {
			for (DatoASolicitar dato : agrupacion.getDatosASolicitar()) {
				camposXnombre.put(dato.getNombre(), dato);
			}
		}
		
		return camposXnombre;
	}
	
	public String autocompletarCampo() {
		
		Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		String claves = (String)requestParameterMap.get("paramIdsServicio");
		
		try {
			List<String> idComponentes = new ArrayList<String>();
			
			for (String nombre : datosReservaMBean.keySet()) {
				idComponentes.add(nombre);
			}
			
			FormularioDinamicoReserva.desmarcarCampos(idComponentes, campos);			
			
			String[] arrParamIdServicio = claves.split("\\|");
			
			for (String paramIdServicio : arrParamIdServicio){
				ServicioPorRecurso sRec = new ServicioPorRecurso();
				sRec.setId(new Integer(paramIdServicio));
				
				Map<String, Object> valoresAutocompletar = this.agendarReservasEJB.autocompletarCampo(sRec,datosReservaMBean);
				
				for (String nombre : valoresAutocompletar.keySet()) {
					
					datosReservaMBean.put(nombre, valoresAutocompletar.get(nombre).toString());
				}
			}
			
		} catch (ErrorAutocompletarException e) {
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				idComponentesError.add(e.getNombreCampo(i));
			}
			String mensaje = e.getMensaje(0);
			for(int i = 1; i < e.getCantMensajes(); i++) {
				mensaje += "  |  "+e.getMensaje(i);
			}
			addErrorMessage(mensaje, FORMULARIO_ID);
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			
			return null;
		} catch (AutocompletarException e) {
			//Faltan campos requeridos
			addErrorMessage(e.getMessage(), FORMULARIO_ID);
		
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				idComponentesError.add(e.getNombreCampo(i));
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);

			return null;
		} catch (Exception e) {
			addErrorMessage(e, FORMULARIO_ID);
			return null;
		}
		
		return null;
	}

	public String getAceptaCondiciones() {
		return aceptaCondiciones;
	}

	public void setAceptaCondiciones(String aceptaCondiciones) {
		this.aceptaCondiciones = aceptaCondiciones;
	}

	/**
	 * El parámetro valoresCampos debe ser una lista separada por punto y coma de elementos de la forma <agrupacion>.<dato>.<valor>, 
	 * donde <agrupacion> es el identificador o el nombre de la agrupación, <dato> es el identificador o el nombre del dato, y <valor> 
	 * es el valor a poner en el campo. Notar que el valor no puede contener el signo ";" porque sería interpretado como un cambio de
	 * parámetro. 
	 * @param valoresCampos
	 * @param agrupaciones
	 */
	private void cargarCamposPorDefecto(String valoresCampos, List<AgrupacionDato> agrupaciones) {
	  //Armar dos mapas con los datos a solicitar, uno por ids y otro por nombre para accederlos más rápido
	  Map<String, DatoASolicitar> porId = new HashMap<String, DatoASolicitar>();
    Map<String, DatoASolicitar> porNombre = new HashMap<String, DatoASolicitar>();
    for(AgrupacionDato agrupacion : agrupaciones) {
      for(DatoASolicitar datoSolicitar : agrupacion.getDatosASolicitar()) {
        porId.put(agrupacion.getId().toString()+"."+datoSolicitar.getId().toString(), datoSolicitar);
        porNombre.put(agrupacion.getNombre()+"."+datoSolicitar.getNombre(), datoSolicitar);
      }
    }
	  //Obtener cada uno de los campos cortando el string por los punto y coma
	  String parametros[] = valoresCampos.split("\\;");
		//Procesar cada uno de los parámetros, que deberían ser de la forma <agrupacion>.<dato>.<valor>
		for (String parm : parametros) {
		  //Obtener las tres partes (agrupación, dato, valor)
			String agrupCampoValor[] = parm.split("\\.", 3);
      String sAgrupacion = null;
      String sDatoSol = null;
      String sValor = null;
			if(agrupCampoValor.length==3) {
  			sAgrupacion = agrupCampoValor[0];
  			sDatoSol = agrupCampoValor[1];
  			sValor = agrupCampoValor[2];
  			//Determinar el dato a solicitar indicado por la agrupación y el dato
        //Si tanto la agrupacion como el dato son numéricos se asume que son los ids, sino se asume que son los nombres
  			DatoASolicitar datoASolicitar = null;
  			try {
  			  Integer.valueOf(sAgrupacion);
          Integer.valueOf(sDatoSol);
          //Son identificadores, buscar el dato a solicitar correspondiente
          datoASolicitar = porId.get(sAgrupacion+"."+sDatoSol);
        }catch(NumberFormatException nfEx) {
          //No son identificadores, son nombres
          datoASolicitar = porNombre.get(sAgrupacion+"."+sDatoSol);
  			}
  			//Si se encontró un dato a solicitar poner el valor apropiado
  			if(datoASolicitar != null) {
  			  try {
      			if(datoASolicitar.getTipo()==Tipo.DATE) {
              DateFormat format = new SimpleDateFormat("yyyyMMdd");
              Date valor = format.parse(sValor);
              this.datosReservaMBean.put(datoASolicitar.getNombre(), valor);            
      			}else if(datoASolicitar.getTipo()==Tipo.LIST || datoASolicitar.getTipo()==Tipo.STRING ) {
      				this.datosReservaMBean.put(datoASolicitar.getNombre(), sValor);
      			}else if(datoASolicitar.getTipo()==Tipo.NUMBER) {
      				Integer valor = Integer.valueOf(sValor);
      				this.datosReservaMBean.put(datoASolicitar.getNombre(), valor);
      			}else if(datoASolicitar.getTipo()==Tipo.BOOLEAN) {
              Boolean valor = Boolean.valueOf(sValor);
              this.datosReservaMBean.put(datoASolicitar.getNombre(), valor);
            }
  			  }catch(Exception ex) {
  			    //Seguramente el valor no puede ser convertido al formato apropiado
  			    ex.printStackTrace();
  			  }
  			}
	    }
		}
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

  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  public List<SelectItem> getTramites() {
    return tramites;
  }
	
  public boolean isYaExisteReservaCamposClave() {
    return yaExisteReservaCamposClave;
  }

  @PreDestroy
  public void preDestroy() {
    try {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      this.agendarReservasEJB = null;
      this.campos = null;
      if(this.datosReservaMBean!=null) {
        this.datosReservaMBean.clear();
      }
      this.datosReservaMBean = null;
      this.formularioDin = null;
      this.recursosEJB = null;
      this.sesionMBean = null;
      if(this.tramites != null) {
        this.tramites.clear();
      }
      this.tramites = null;
      if(this.tramitesAgenda!=null) {
        this.tramitesAgenda.clear();
      }
      this.tramitesAgenda = null;
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
    }
  }
  
}