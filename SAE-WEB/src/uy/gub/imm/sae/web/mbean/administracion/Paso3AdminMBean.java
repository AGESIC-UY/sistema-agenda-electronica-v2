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

import java.util.ArrayList;
import java.util.Date;
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
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.ModoAutocompletado;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.ParametrosAutocompletar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioAutocompletarPorDato;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
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

public class Paso3AdminMBean extends BaseMBean {

	static Logger logger = Logger.getLogger(Paso3AdminMBean.class);
	public static final String FORMULARIO_ID = "datosReserva";
	public static final String DATOS_RESERVA_MBEAN = "datosReservaMBean";

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	private SessionMBean sessionMBean;

	private UIComponent campos;
	private Map<String, Object> datosReservaMBean;
	private FormularioDinamicoReserva formularioDin;

  private Map<String, TramiteAgenda> tramitesAgenda;
  private List<SelectItem> tramites;
  private String tramiteCodigo;
	private String aceptaCondiciones;

	public void beforePhase(PhaseEvent event) {
		disableBrowserCache(event);

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("realizar_reserva"));
			if (sessionMBean.getReserva() == null) {
				// Se ha apretado el boton de back o algun acceso directo
				FacesContext ctx = FacesContext.getCurrentInstance();
				ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "pasoAnterior");
			}
		}
	}

	@PostConstruct
	public void init() {

		if (sessionMBean.getAgenda() == null || sessionMBean.getRecurso() == null) {
			redirect("inicio");
			return;
		}
		
    try {
      this.tramiteCodigo = null;
      tramitesAgenda = new HashMap<String, TramiteAgenda>();
      tramites = new ArrayList<SelectItem>();
      
      Reserva reserva = sessionMBean.getReserva();
      Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
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
      redirect("inicio");
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
		} else {
			return null;
		}
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

	public String getDescripcion() {
		TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgenda(), sessionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoPaso3();
		} else {
			return null;
		}
	}

	public String getDescripcionRecurso() {
		TextoRecurso textoRecurso = getTextoRecurso(sessionMBean.getRecursoMarcado(), sessionMBean.getIdiomaActual());
		if (textoRecurso != null) {
			return textoRecurso.getTextoPaso3();
		} else {
			return null;
		}
	}

	public String getEtiquetaDelRecurso() {
		TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgendaMarcada(), sessionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		} else {
			return null;
		}
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
					formularioDin = new FormularioDinamicoReserva(DATOS_RESERVA_MBEAN, FORMULARIO_ID, FormularioDinamicoReserva.TipoFormulario.EDICION, null, sessionMBean.getFormatoFecha());

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
											if (sDato.getDatoASolicitar().getAgrupacionDato().getOrden().intValue() > ultimo
													.getAgrupacionDato().getOrden().intValue()) {
												HashMap<Integer, ServicioPorRecurso> auxServiciosRecurso = serviciosAutocompletar.get(ultimo.getId());
												if (auxServiciosRecurso.size() > 1) {
													auxServiciosRecurso.remove(sRec.getId());
												} else {
													serviciosAutocompletar.remove(ultimo.getId());
												}
												ultimo = sDato.getDatoASolicitar();
											} else if (sDato.getDatoASolicitar().getAgrupacionDato().getOrden().intValue() == 
											    ultimo.getAgrupacionDato().getOrden().intValue()) {
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
			
			HtmlPanelGroup clausulaGroup = (HtmlPanelGroup) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:clausula");
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
			Reserva reserva = sessionMBean.getReserva();
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
					Reserva rConfirmada = agendarReservasEJB.confirmarReserva(sessionMBean.getEmpresaActual(), reserva, null, null, true);
					reserva.setSerie(rConfirmada.getSerie());
					reserva.setNumero(rConfirmada.getNumero());
					reserva.setCodigoSeguridad(rConfirmada.getCodigoSeguridad());
					reserva.setTrazabilidadGuid(rConfirmada.getTrazabilidadGuid());
					confirmada = true;
				} catch (AccesoMultipleException e){
					//Reintento hasta tener exito, en algun momento no me va a dar acceso multiple.
				}
			}
			
			//Enviar el mail de confirmacion
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String linkCancelacion = request.getScheme()+"://"+request.getServerName();
			if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
				linkCancelacion = linkCancelacion + ":" + request.getServerPort();
			}
			Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
			linkCancelacion = linkCancelacion + "/sae/cancelarReserva/Paso1.xhtml?e="+sessionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri="+reserva.getId();
			agendarReservasEJB.enviarComunicacionesConfirmacion(linkCancelacion, reserva, sessionMBean.getIdiomaActual(), sessionMBean.getFormatoFecha(), sessionMBean.getFormatoHora());

			//La reserva se confirm�, por lo tanto muevo la reseva a confirmada en la sesion para evitar problemas de reload de pagina.
			sessionMBean.setReservaConfirmada(reserva);
			sessionMBean.setReserva(null);
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
					addErrorMessage(mensaje, "form:"+dato.getNombre());
					idComponentesError.add(e.getNombreCampo(i));
				}
				//idComponentesError.add(e.getNombreCampo(i));
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
				addErrorMessage(mensaje, "form", "form:"+dato.getNombre());
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
		
		return "reservaConfirmada";
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
	
}
