package uy.gub.imm.sae.web.mbean.administracion;

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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Disponibilidades;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.ModoAutocompletado;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.ParametrosAutocompletar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioAutocompletarPorDato;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.AutocompletarException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAutocompletarException;
import uy.gub.imm.sae.exception.ErrorValidacionCommitException;
import uy.gub.imm.sae.exception.ErrorValidacionException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionException;
import uy.gub.imm.sae.exception.ValidacionPorCampoException;
import uy.gub.imm.sae.exception.WarningAutocompletarException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;

/**
 * Maneja la lógica de generación dinámica de los componentes gráficos que
 * constituyen los datos a solicitarse para realizar la reserva.
 * 
 * @author im2716295
 *
 *
 */

public class AtencionPresencialMBean extends BaseMBean {

  public static final String MSG_ID = "pantalla";
  
	static Logger logger = Logger.getLogger(AtencionPresencialMBean.class);
	public static final String FORMULARIO_ID = "datosReserva";
	public static final String DATOS_RESERVA_MBEAN = "datosReservaMBean";

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/DisponibilidadesBean!uy.gub.imm.sae.business.ejb.facade.DisponibilidadesRemote")
  private Disponibilidades disponibilidadesEJB;
	
	private SessionMBean sessionMBean;

	private UIComponent campos;
	private UIComponent camposError;
	private Map<String, Object> datosReservaMBean;
	private FormularioDinamicoReserva formularioDin;

  private Map<String, TramiteAgenda> tramitesAgenda;
  private List<SelectItem> tramites;
  private String tramiteCodigo;
	private String aceptaCondiciones;
	
	private Reserva reserva;
	private Reserva reservaConfirmada;

	public void beforePhase(PhaseEvent event) {
	  
		disableBrowserCache(event);
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("atencion_presencial"));
		}
	}

	@PostConstruct
	public void init() {

    Agenda agenda = sessionMBean.getAgendaMarcada();
    Recurso recurso = sessionMBean.getRecursoMarcado();
    
    boolean hayError = false;
    
    //Verificar si hay una agenda y un recurso seleccionados
    if(agenda == null) {
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
      hayError = true;
    }
    if(recurso == null) {
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
      hayError = true;
    }
    
    if(hayError) {
      return;
    }
    
    //Determinar si el recurso admite atención presencial
    if(recurso.getPresencialAdmite()==null || !recurso.getPresencialAdmite().booleanValue()) {
      addErrorMessage(sessionMBean.getTextos().get("el_recurso_no_admite_atencion_presencial"), MSG_ID);
      return;
    }else {
      //Determinar si el recurso admite atención presencial para el día actual
      Calendar cal = new GregorianCalendar();
      cal.add(Calendar.MILLISECOND, sessionMBean.getTimeZone().getOffset(cal.getTimeInMillis()));
      int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
      if((diaSemana==Calendar.MONDAY && (recurso.getPresencialLunes()==null || !recurso.getPresencialLunes().booleanValue())) ||
         (diaSemana==Calendar.TUESDAY && (recurso.getPresencialMartes()==null || !recurso.getPresencialMartes().booleanValue())) ||
         (diaSemana==Calendar.WEDNESDAY && (recurso.getPresencialMiercoles()==null || !recurso.getPresencialMiercoles().booleanValue())) ||
         (diaSemana==Calendar.THURSDAY && (recurso.getPresencialJueves()==null || !recurso.getPresencialJueves().booleanValue())) ||
         (diaSemana==Calendar.FRIDAY && (recurso.getPresencialViernes()==null || !recurso.getPresencialViernes().booleanValue())) ||
         (diaSemana==Calendar.SATURDAY && (recurso.getPresencialSabado()==null || !recurso.getPresencialSabado().booleanValue())) ||
         (diaSemana==Calendar.SUNDAY && (recurso.getPresencialDomingo()==null || !recurso.getPresencialDomingo().booleanValue()))) {
        addErrorMessage(sessionMBean.getTextos().get("el_recurso_no_admite_atencion_presencial_para_hoy"), MSG_ID);
        return;
      }
    }
    
    //Determinar si quedan cupos disponibles para hoy
    Disponibilidad disponibilidad = disponibilidadesEJB.obtenerDisponibilidadPresencial(recurso, sessionMBean.getTimeZone());
    if(disponibilidad == null) {
      addErrorMessage(sessionMBean.getTextos().get("el_recurso_no_admite_atencion_presencial"), MSG_ID);
      return;
    }
    
    if(disponibilidad.getCupo().intValue() <= disponibilidad.getNumerador().intValue()) {
      addErrorMessage(sessionMBean.getTextos().get("no_hay_cupos_disponibles_para_hoy"), MSG_ID);
      return;
    }
    
    //Cargar en sesión la agenda y el recurso
    sessionMBean.setAgenda(agenda);
    sessionMBean.setRecurso(recurso);
    
    //Cargar los tramites de la agenda
    try {
      this.tramiteCodigo = null;
      tramitesAgenda = new HashMap<String, TramiteAgenda>();
      tramites = new ArrayList<SelectItem>();
      
      List<TramiteAgenda> tramites0 = agendarReservasEJB.consultarTramites(agenda);
      if(tramites0.size()==1) {
        TramiteAgenda tramite = tramites0.get(0);
        tramiteCodigo = tramite.getTramiteCodigo();
        tramitesAgenda.put(tramiteCodigo, tramite);
      }else {
        tramites.add(new SelectItem("", "Sin especificar"));
        for(TramiteAgenda tramite : tramites0) {
          tramitesAgenda.put(tramite.getTramiteCodigo(), tramite);
          tramites.add(new SelectItem(tramite.getTramiteCodigo(), tramite.getTramiteNombre()));
        }
      }
    }catch(Exception ex) {
      ex.printStackTrace();
      addErrorMessage(ex.getMessage());
    }
	}

	public Reserva getReservaConfirmada() {
    return reservaConfirmada;
  }

  public boolean getRecursoPermitirAtencionPresencial() {
    Recurso recurso = sessionMBean.getRecursoMarcado();
    if(recurso==null || recurso.getPresencialAdmite()==null || !recurso.getPresencialAdmite().booleanValue()) {
      return false;
    }
    //Determinar si el recurso admite atención presencial para el día actual
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.MILLISECOND, sessionMBean.getTimeZone().getOffset(cal.getTimeInMillis()));
    int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
    if((diaSemana==Calendar.MONDAY && (recurso.getPresencialLunes()==null || !recurso.getPresencialLunes().booleanValue())) ||
       (diaSemana==Calendar.TUESDAY && (recurso.getPresencialMartes()==null || !recurso.getPresencialMartes().booleanValue())) ||
       (diaSemana==Calendar.WEDNESDAY && (recurso.getPresencialMiercoles()==null || !recurso.getPresencialMiercoles().booleanValue())) ||
       (diaSemana==Calendar.THURSDAY && (recurso.getPresencialJueves()==null || !recurso.getPresencialJueves().booleanValue())) ||
       (diaSemana==Calendar.FRIDAY && (recurso.getPresencialViernes()==null || !recurso.getPresencialViernes().booleanValue())) ||
       (diaSemana==Calendar.SATURDAY && (recurso.getPresencialSabado()==null || !recurso.getPresencialSabado().booleanValue())) ||
       (diaSemana==Calendar.SUNDAY && (recurso.getPresencialDomingo()==null || !recurso.getPresencialDomingo().booleanValue()))) {
      return false;
    }
    
    Disponibilidad disponibilidad = disponibilidadesEJB.obtenerDisponibilidadPresencial(recurso, sessionMBean.getTimeZone());
    if(disponibilidad == null || disponibilidad.getCupo().intValue() <= disponibilidad.getNumerador().intValue()) {
      return false;
    }
    return true;
	}
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public String getClausulaConsentimiento() {
		Empresa empresa = sessionMBean.getEmpresaActual();
		String texto = sessionMBean.getTextos().get("clausula_de_consentimiento_informado_texto");
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
			Recurso recurso = sessionMBean.getRecurso();

			// El chequeo de recurso != null es en caso de un acceso directo a
			// la pagina, es solo
			// para que no salte la excepcion en el log, pues de todas formas
			// sera redirigido a una pagina de error.
			if (campos.getChildCount() == 0 && recurso != null) {

				if (formularioDin == null) {
					List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso, sessionMBean.getTimeZone());
					sessionMBean.setDatosASolicitar(obtenerCampos(agrupaciones));
					formularioDin = new FormularioDinamicoReserva(DATOS_RESERVA_MBEAN, FORMULARIO_ID,
							FormularioDinamicoReserva.TipoFormulario.EDICION, sessionMBean.getFormatoFecha());

					HashMap<Integer, HashMap<Integer, ServicioPorRecurso>> serviciosAutocompletar = new HashMap<Integer, HashMap<Integer, ServicioPorRecurso>>();

					List<ServicioPorRecurso> lstServiciosPorRecurso = recursosEJB.consultarServicioAutocompletar(recurso);

					for (ServicioPorRecurso sRec : lstServiciosPorRecurso) {
						List<ServicioAutocompletarPorDato> lstDatos = sRec.getAutocompletadosPorDato();
						List<ParametrosAutocompletar> parametros = sRec.getAutocompletado().getParametrosAutocompletados();

						DatoASolicitar ultimo = null;
						for (ParametrosAutocompletar param : parametros) {
							if (ModoAutocompletado.SALIDA.equals(param.getModo())) {
								for (ServicioAutocompletarPorDato sDato : lstDatos) {
									if (sDato.getNombreParametro().equals(param.getNombre())) {
										if (ultimo == null) {
											ultimo = sDato.getDatoASolicitar();
										} else {
											if (sDato.getDatoASolicitar().getAgrupacionDato().getOrden().intValue() > ultimo.getAgrupacionDato().getOrden().intValue()) {
												HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = serviciosAutocompletar.get(ultimo.getId());
												if (auxServiciosRecurso.size() > 1) {
													auxServiciosRecurso.remove(sRec.getId());
												} else {
													serviciosAutocompletar.remove(ultimo.getId());
												}
												ultimo = sDato.getDatoASolicitar();
											} else if (sDato.getDatoASolicitar().getAgrupacionDato().getOrden().intValue() == ultimo.getAgrupacionDato().getOrden().intValue()) {
												if (sDato.getDatoASolicitar().getFila().intValue() > ultimo.getFila().intValue()) {
													HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = serviciosAutocompletar.get(ultimo.getId());
													if (auxServiciosRecurso.size() > 1) {
														auxServiciosRecurso.remove(sRec.getId());
													} else {
														serviciosAutocompletar.remove(ultimo.getId());
													}
													ultimo = sDato.getDatoASolicitar();
												} else if (sDato.getDatoASolicitar().getFila().intValue() == ultimo.getFila().intValue()) {
													if (sDato.getDatoASolicitar().getColumna().intValue() > ultimo.getColumna().intValue()) {
														HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = serviciosAutocompletar.get(ultimo.getId());
														if (auxServiciosRecurso.size() > 1) {
															auxServiciosRecurso.remove(sRec.getId());
														} else {
															serviciosAutocompletar.remove(ultimo.getId());
														}
														ultimo = sDato.getDatoASolicitar();
													}
												}
											}
										}
										if (serviciosAutocompletar.containsKey(ultimo.getId())) {
											serviciosAutocompletar.get(ultimo.getId()).put(sRec.getId(), sRec);
										} else {
											HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = new HashMap<Integer, ServicioPorRecurso>();
											auxServiciosRecurso.put(sRec.getId(), sRec);
											serviciosAutocompletar.put(ultimo.getId(), auxServiciosRecurso);
										}
									}
								}
							}
						}
					}
					formularioDin.armarFormulario(agrupaciones, serviciosAutocompletar);
				}
				UIComponent formulario = formularioDin.getComponenteFormulario();
				campos.getChildren().add(formulario);
			}
		} catch (Exception e) {
			addErrorMessage(e, FORMULARIO_ID);
		}
	}

	public UIComponent getCamposError() {
		return camposError;
	}

	public void setCamposError(UIComponent camposError) {
		this.camposError = camposError;

		try {
			Recurso recurso = sessionMBean.getRecurso();

			// El chequeo de recurso != null es en caso de un acceso directo a
			// la pagina, es solo
			// para que no salte la excepcion en el log, pues de todas formas
			// sera redirigido a una pagina de error.
			if (camposError.getChildCount() == 0 && recurso != null) {

				if (formularioDin == null) {
					List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso, sessionMBean.getTimeZone());
					sessionMBean.setDatosASolicitar(obtenerCampos(agrupaciones));
					formularioDin = new FormularioDinamicoReserva(DATOS_RESERVA_MBEAN, FORMULARIO_ID,	FormularioDinamicoReserva.TipoFormulario.EDICION, sessionMBean.getFormatoFecha());
					HashMap<Integer, HashMap<Integer, ServicioPorRecurso>> serviciosAutocompletar = new HashMap<Integer, HashMap<Integer, ServicioPorRecurso>>();
					List<ServicioPorRecurso> lstServiciosPorRecurso = recursosEJB.consultarServicioAutocompletar(recurso);
					for (ServicioPorRecurso sRec : lstServiciosPorRecurso) {
						List<ServicioAutocompletarPorDato> lstDatos = sRec.getAutocompletadosPorDato();
						List<ParametrosAutocompletar> parametros = sRec.getAutocompletado().getParametrosAutocompletados();
						DatoASolicitar ultimo = null;
						for (ParametrosAutocompletar param : parametros) {
							if (ModoAutocompletado.SALIDA.equals(param.getModo())) {
								for (ServicioAutocompletarPorDato sDato : lstDatos) {
									if (sDato.getNombreParametro().equals(param.getNombre())) {
										if (ultimo == null) {
											ultimo = sDato.getDatoASolicitar();
										} else {
											if (sDato.getDatoASolicitar().getAgrupacionDato().getOrden().intValue() > ultimo.getAgrupacionDato().getOrden().intValue()) {
												HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = serviciosAutocompletar.get(ultimo.getId());
												if (auxServiciosRecurso.size() > 1) {
													auxServiciosRecurso.remove(sRec.getId());
												} else {
													serviciosAutocompletar.remove(ultimo.getId());
												}
												ultimo = sDato.getDatoASolicitar();
											} else if (sDato.getDatoASolicitar().getAgrupacionDato().getOrden().intValue() == ultimo.getAgrupacionDato().getOrden().intValue()) {
												if (sDato.getDatoASolicitar().getFila().intValue() > ultimo.getFila().intValue()) {
													HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = serviciosAutocompletar.get(ultimo.getId());
													if (auxServiciosRecurso.size() > 1) {
														auxServiciosRecurso.remove(sRec.getId());
													} else {
														serviciosAutocompletar.remove(ultimo.getId());
													}
													ultimo = sDato.getDatoASolicitar();
												} else if (sDato.getDatoASolicitar().getFila().intValue() == ultimo.getFila().intValue()) {
													if (sDato.getDatoASolicitar().getColumna().intValue() > ultimo.getColumna().intValue()) {
														HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = serviciosAutocompletar.get(ultimo.getId());
														if (auxServiciosRecurso.size() > 1) {
															auxServiciosRecurso.remove(sRec.getId());
														} else {
															serviciosAutocompletar.remove(ultimo.getId());
														}
														ultimo = sDato.getDatoASolicitar();
													}
												}
											}
										}
										if (serviciosAutocompletar.containsKey(ultimo.getId())) {
											serviciosAutocompletar.get(ultimo.getId()).put(sRec.getId(), sRec);
										} else {
											HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = new HashMap<Integer, ServicioPorRecurso>();
											auxServiciosRecurso.put(sRec.getId(), sRec);
											serviciosAutocompletar.put(ultimo.getId(), auxServiciosRecurso);
										}
									}
								}
							}
						}
					}
					formularioDin.armarFormulario(agrupaciones, serviciosAutocompletar);
				}
				UIComponent errores = formularioDin.getComponenteMensajes();
				camposError.getChildren().add(errores);
			}
		} catch (Exception e) {
			addErrorMessage(e, FORMULARIO_ID);
		}
	}

	public Map<String, Object> getDatosReservaMBean() {
		return datosReservaMBean;
	}

	public void setDatosReservaMBean(Map<String, Object> datosReservaMBean) {
		this.datosReservaMBean = datosReservaMBean;
	}

	public String confirmarReserva() {
		limpiarMensajesError();
		try {
			boolean hayError = false;
      if(this.tramiteCodigo==null || this.tramiteCodigo.isEmpty()) {
        hayError = true;
        addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_el_tramite"), FORM_ID+":tramite");
      }
			HtmlPanelGroup clausulaGroup = (HtmlPanelGroup) FacesContext.getCurrentInstance().getViewRoot().findComponent("form1:clausula");
			String clausulaStyleClass = clausulaGroup.getStyleClass();
			if (this.aceptaCondiciones==null || !this.aceptaCondiciones.equals("SI")) {
				hayError = true;
				addErrorMessage(sessionMBean.getTextos().get("debe_aceptar_los_terminos_de_la_clausula_de_consentimiento_informado"), FORM_ID+":condiciones");
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
			
			List<String> idComponentes = new ArrayList<String>();
			Set<DatoReserva> datos = new HashSet<DatoReserva>();
			for (String nombre : datosReservaMBean.keySet()) {
				Object valor = datosReservaMBean.get(nombre);
				idComponentes.add(nombre);
				if (valor != null && ! valor.toString().trim().equals("")) {
					DatoReserva dato = new DatoReserva();
					dato.setDatoASolicitar(sessionMBean.getDatosASolicitar().get(nombre));
					dato.setValor(valor.toString());
					datos.add(dato);
				}
			}
			
			FormularioDinamicoReserva.desmarcarCampos(idComponentes, campos);
			
			if(reserva == null) {
  			Recurso recurso = sessionMBean.getRecursoMarcado();
  			Disponibilidad disponibilidad = disponibilidadesEJB.obtenerDisponibilidadPresencial(recurso, sessionMBean.getTimeZone());
  			reserva = agendarReservasEJB.marcarReserva(disponibilidad);
			}
			reserva.setDatosReserva(datos);
			
			agendarReservasEJB.validarDatosReserva(sessionMBean.getEmpresaActual(), reserva);
			
			if(hayError) {
				return null;
			}
			
      reserva.setTramiteCodigo(this.tramiteCodigo);
      reserva.setTramiteNombre(tramitesAgenda.get(this.tramiteCodigo).getTramiteNombre());
			
			boolean confirmada = false;
			while (!confirmada) {
				try {
					Reserva rConfirmada = agendarReservasEJB.confirmarReservaPresencial(sessionMBean.getEmpresaActual(), reserva);
					reserva.setSerie(rConfirmada.getSerie());
					reserva.setNumero(rConfirmada.getNumero());
					reserva.setCodigoSeguridad(rConfirmada.getCodigoSeguridad());
					reserva.setTrazabilidadGuid(rConfirmada.getTrazabilidadGuid());
					confirmada = true;
				} catch (AccesoMultipleException e){
					//Reintento hasta tener exito, en algun momento no me va a dar acceso multiple.
				}
			}
			
			//generarTicket(reserva);
			reservaConfirmada = reserva;
			reserva = null;
			
		} catch (ValidacionPorCampoException e) {
			//Alguno de los campos no tiene el formato esperado
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				if (e.getMensaje(i) != null) {
					DatoASolicitar dato = sessionMBean.getDatosASolicitar().get(e.getNombreCampo(i));
					String mensaje = sessionMBean.getTextos().get(e.getMensaje(i));
					if(mensaje == null) {
						mensaje = sessionMBean.getTextos().get("el_valor_ingresado_no_es_aceptable");
					}
					mensaje = mensaje.replace("{campo}", dato.getEtiqueta());
					addErrorMessage(mensaje, "form1:"+dato.getNombre());
					idComponentesError.add(e.getNombreCampo(i));
				}
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		} catch (ErrorValidacionException e) {
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
		} catch (ErrorValidacionCommitException e) { 
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
		}	catch (ValidacionClaveUnicaException vcuEx) {
			addErrorMessage(vcuEx);
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < vcuEx.getCantCampos(); i++) {
				idComponentesError.add(vcuEx.getNombreCampo(i));
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		}
		catch (ValidacionException e) {
			//Faltan campos requeridos
			List<String> idComponentesError = new ArrayList<String>();
			for(int i = 0; i < e.getCantCampos(); i++) {
				DatoASolicitar dato = sessionMBean.getDatosASolicitar().get(e.getNombreCampo(i));
				String mensaje = sessionMBean.getTextos().get("debe_completar_el_campo_campo").replace("{campo}", dato.getEtiqueta());
				addErrorMessage(mensaje, "form1", "form1:"+dato.getNombre());
				idComponentesError.add(e.getNombreCampo(i));
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError, campos);
			return null;
		} catch(BusinessException bEx) {
      //Seguramente esto fue lanzado por una Accion
      addErrorMessage(bEx.getMessage());
      bEx.printStackTrace();
      return null;
    } catch(Exception ex) {
			addErrorMessage(sessionMBean.getTextos().get("sistema_en_mantenimiento"));
			ex.printStackTrace();
			return null;
		}
		//Blanqueo el formulario de datos de la reserva
		datosReservaMBean.clear();
		
		return null;
	}
	
	/**
	 * @param agrupaciones
	 *            de algun recurso
	 * @return retorna todos los datos a solicitar definidos en la lista de
	 *         agrupaciones en un mapa cuya clave es el nombre del campo
	 */
	private Map<String, DatoASolicitar> obtenerCampos(
			List<AgrupacionDato> agrupaciones) {

		Map<String, DatoASolicitar> camposXnombre = new HashMap<String, DatoASolicitar>();

		for (AgrupacionDato agrupacion : agrupaciones) {
			for (DatoASolicitar dato : agrupacion.getDatosASolicitar()) {
				camposXnombre.put(dato.getNombre(), dato);
			}
		}

		return camposXnombre;
	}

	public String autocompletarCampo() {

		Map<String, String> requestParameterMap = FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();

		String claves = (String) requestParameterMap.get("paramIdsServicio");

		try {
			List<String> idComponentes = new ArrayList<String>();

			for (String nombre : datosReservaMBean.keySet()) {
				idComponentes.add(nombre);
			}

			FormularioDinamicoReserva.desmarcarCampos(idComponentes, campos);

			String[] arrParamIdServicio = claves.split("\\|");

			for (String paramIdServicio : arrParamIdServicio) {
				ServicioPorRecurso sRec = new ServicioPorRecurso();
				sRec.setId(new Integer(paramIdServicio));

				Map<String, Object> valoresAutocompletar = this.agendarReservasEJB
						.autocompletarCampo(sRec, datosReservaMBean);

				for (String nombre : valoresAutocompletar.keySet()) {

					datosReservaMBean.put(nombre,
							valoresAutocompletar.get(nombre).toString());
				}
			}

		} catch (ErrorAutocompletarException e) {
			List<String> idComponentesError = new ArrayList<String>();
			for (int i = 0; i < e.getCantCampos(); i++) {
				idComponentesError.add(e.getNombreCampo(i));
			}
			String mensaje = e.getMensaje(0);
			for (int i = 1; i < e.getCantMensajes(); i++) {
				mensaje += "  |  " + e.getMensaje(i);
			}
			addErrorMessage(mensaje, FORMULARIO_ID);
			FormularioDinamicoReserva.marcarCamposError(idComponentesError,
					campos);

			return null;
		} catch (WarningAutocompletarException e) {

			List<String> idComponentesError = new ArrayList<String>();
			for (int i = 0; i < e.getCantCampos(); i++) {
				idComponentesError.add(e.getNombreCampo(i));
			}
			String mensaje = e.getMensaje(0);
			for (int i = 1; i < e.getCantMensajes(); i++) {
				mensaje += "  |  " + e.getMensaje(i);
			}
			addInfoMessage(mensaje, FORMULARIO_ID);
			FormularioDinamicoReserva.marcarCamposError(idComponentesError,
					campos);

			return null;
		} catch (AutocompletarException e) {
			// Faltan campos requeridos
			addErrorMessage(e.getMessage(), FORMULARIO_ID);

			List<String> idComponentesError = new ArrayList<String>();
			for (int i = 0; i < e.getCantCampos(); i++) {
				idComponentesError.add(e.getNombreCampo(i));
			}
			FormularioDinamicoReserva.marcarCamposError(idComponentesError,
					campos);

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
	
  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  public List<SelectItem> getTramites() {
    return tramites;
  }
	
  public String imprimirTicket() {
    
    if(reservaConfirmada == null) {
      addErrorMessage(sessionMBean.getTextos().get("debe_confirmar_la_reserva"), MSG_ID);
      return null;
    }
    
    try {
      BaseFont helveticaBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      BaseFont helveticaOblique = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
      
      SimpleDateFormat sdfFecha = new SimpleDateFormat (sessionMBean.getFormatoFecha());
      
      Rectangle pageSize = new Rectangle(210,225);
      
      Document document = new Document(pageSize);
      document.addTitle(sessionMBean.getTextos().get("ticket_de_reserva"));
      
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      PdfWriter pdfWriter = PdfWriter.getInstance(document, os);

      document.addTitle(sessionMBean.getTextos().get("confirmacion"));
      document.addSubject("SAE");
      document.addAuthor("SAE");
      document.addKeywords("SAE,ticket,"+sessionMBean.getTextos().get("confirmacion")+","+sessionMBean.getTextos().get("reserva"));
      document.addProducer();
      document.addCreator("SAE");
      document.addHeader("Producer", "SAE");
      
      document.open();
      
      PdfContentByte pdfContent = pdfWriter.getDirectContent();

      Empresa empresa = sessionMBean.getEmpresaActual();
      if(empresa != null && empresa.getLogo()!=null) {
        Image img = Image.getInstance(empresa.getLogo());
        img.scaleAbsolute(100,30);
        img.setAbsolutePosition(55, 170);
        document.add(img);
      }
      
      
      int posY = 170;
      
      //Dibujo primer línea
      LineSeparator line = new LineSeparator();
      line.setAlignment(LineSeparator.ALIGN_CENTER);
      line.setLineColor(BaseColor.BLACK);
      line.setLineWidth(0.5f);
      line.drawLine(pdfContent, 10, 200,  posY);
      posY = posY - 15;

      //Nombre de la agenda (trámite)
      pdfContent.beginText();
      pdfContent.setFontAndSize(helveticaBold, 10);
      pdfContent.setTextMatrix(15, posY);
      pdfContent.showText(sessionMBean.getAgenda().getNombre());
      pdfContent.endText();
      posY = posY - 15;
      
      //Nombre del recurso (oficina)
      pdfContent.beginText();
      pdfContent.setFontAndSize(helveticaBold, 10);
      pdfContent.setTextMatrix(15, posY);
      pdfContent.showText(sessionMBean.getRecurso().getNombre());
      pdfContent.endText();
      posY = posY - 15;
      
      //Fecha
      pdfContent.beginText();
      pdfContent.setFontAndSize(helveticaBold, 12);
      pdfContent.setTextMatrix(15, posY);
      pdfContent.showText(sessionMBean.getTextos().get("fecha")+":");
      pdfContent.endText();
      pdfContent.beginText();
      pdfContent.setFontAndSize(helveticaBold, 12);
      pdfContent.setTextMatrix(130, posY);
      pdfContent.showText(sdfFecha.format(reservaConfirmada.getDisponibilidades().get(0).getFecha()));
      pdfContent.endText();
      posY = posY - 15;
      
      //Serie y número
      String serieNumeroLabel = "";
      String serieNumeroValue = "";
      String serie = reservaConfirmada.getSerie();
      if (serie != null && !serie.trim().isEmpty()) {
        serieNumeroLabel = sessionMBean.getTextos().get("serie") + "/";
        serieNumeroValue = serie + "/";
      }
      serieNumeroLabel = serieNumeroLabel + sessionMBean.getTextos().get("numero");
      serieNumeroValue = serieNumeroValue + reservaConfirmada.getNumero().toString();
      pdfContent.beginText();
      pdfContent.setFontAndSize(helveticaBold, 12);
      pdfContent.setTextMatrix(15, posY);
      pdfContent.showText(serieNumeroLabel+":");
      pdfContent.endText();
      pdfContent.beginText();
      pdfContent.setFontAndSize(helveticaBold, 12);
      pdfContent.setTextMatrix(130, posY);
      pdfContent.showText(serieNumeroValue);
      pdfContent.endText();
      posY = posY - 15;
      
      if(sessionMBean.getAgenda().getConTrazabilidad()!=null && sessionMBean.getAgenda().getConTrazabilidad().booleanValue()) {
        pdfContent.beginText();
        pdfContent.setFontAndSize(helveticaBold, 10);
        pdfContent.setTextMatrix(15, posY);
        pdfContent.showText(sessionMBean.getTextos().get("codigo_de_trazabilidad")+":");
        pdfContent.endText();
  
        pdfContent.beginText();
        pdfContent.setFontAndSize(helveticaBold, 12);
        pdfContent.setTextMatrix(130, posY);
        pdfContent.showText(reservaConfirmada.getTrazabilidadGuid());
        pdfContent.endText();
        posY = posY - 15;
      }
      
      //Dibujo una línea separadora
      posY = posY + 10;
      line.setLineColor(BaseColor.BLACK);
      line.drawLine(pdfWriter.getDirectContent(), 10, 200,  posY);
      posY = posY - 15;
      
      pdfContent.beginText();
      pdfContent.setFontAndSize(helveticaOblique, 6);
      pdfContent.setTextMatrix(20, posY);
      
      //Cambiar el timezone del formateador de hora para ajustar la fecha de hoy
      SimpleDateFormat sdfFechaHora = new SimpleDateFormat (sessionMBean.getFormatoFecha() + " " + sessionMBean.getFormatoHora());
      sdfFechaHora.setTimeZone(sessionMBean.getTimeZone());
      pdfContent.showText(sessionMBean.getTextos().get("reserva_realizada_el")+" "+sdfFechaHora.format(new Date()));
      
      pdfContent.endText();
      posY = posY - 10;
      
      pdfWriter.addJavaScript("this.print({bUI: true, bSilent: true, bShrinkToFit: true});",false); 
      pdfWriter.addJavaScript("this.closeDoc(true);");   
      
      document.close();
      
      FacesContext facesContext = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
      response.setContentType("application/pdf");  
      //response.setHeader("Content-Disposition","attachment");
      os.writeTo(response.getOutputStream());
      response.getOutputStream().flush();
      response.getOutputStream().close();
      facesContext.responseComplete();
      
      //RequestContext.getCurrentInstance().update("foo:bar");
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return null;
  }
  
  public String nuevaReserva() {
    tramiteCodigo = null;
    aceptaCondiciones = null;
    reservaConfirmada = null;
    return null;
  }
  
}
