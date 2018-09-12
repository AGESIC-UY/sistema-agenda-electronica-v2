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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.factories.BusinessLocator;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.login.Utilidades;
import uy.gub.imm.sae.web.common.BaseMBean;

/*
 * Invocación desde un sistema externo:
 * https://192.168.1.13:8443/sae/agendarReserva/Paso1.xhtml?e=1000001&a=29&u=http%3A%2F%2Fgoogle.com.uy&p=13.61.789456;13.60.CI;13.62.@@email
 * 
 */
public class Paso1MBean extends BaseMBean {

	private static Logger LOGGER = Logger.getLogger(Paso1MBean.class);

	private AgendarReservas agendarReservasEJB;

	private Recursos recursosEJB;

	private SesionMBean sesionMBean;

	private String mensajeError = null;

	private List<Recurso> recursos;
	private List<SelectItem> recursosItems;

	private List<DatoDelRecurso> infoRecurso;
	private boolean errorInit;
	private String urlMapa;
	
	private boolean recursoTieneDisponibilidad = true;

	public void beforePhase(PhaseEvent phaseEvent) {
		disableBrowserCache(phaseEvent);
		if (phaseEvent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sesionMBean.limpiarPaso2();
		}
	}

	@PostConstruct
	public void init() {
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      LOGGER.debug("Parámetros GET: ");
      LOGGER.debug("              : e=["+request.getParameter("e")+"]");
      LOGGER.debug("              : a=["+request.getParameter("a")+"]");
      LOGGER.debug("              : r=["+request.getParameter("r")+"]");
      LOGGER.debug("              : i=["+request.getParameter("i")+"]");
      LOGGER.debug("              : u=["+request.getParameter("u")+"]");
      LOGGER.debug("              : p=["+request.getParameter("p")+"]");
      LOGGER.debug("              : t=["+request.getParameter("t")+"]");
      LOGGER.debug("              : q=["+request.getParameter("q")+"]");

      String sEmpresaId = request.getParameter("e"); //Id de la empresa
			String sAgendaId = request.getParameter("a"); //Id de la agenda
			String sRecursoId = request.getParameter("r"); //Id del recurso
			
			String sIdioma = request.getParameter("i"); //Idioma (es,en,...)
			String sUrl = request.getParameter("u"); //URL de retorno al confirmar (URL encoded)
			String sParms = request.getParameter("p"); //Parámetros para autocompletar: <idagrupacion>.<iddato>.<valor>;)
			String sTraza = request.getParameter("t"); //Código de trazabilidad y paso padre (<trazguid>-<paso>)
			String sTramite = request.getParameter("q"); //Código de trámite
			
			if(sParms!=null) {
				sesionMBean.setParmsDatosCiudadano(sParms);
			}else {
				sesionMBean.setParmsDatosCiudadano(null);
			}
			if(sUrl!=null) {
				sesionMBean.setUrlTramite(sUrl);
			}else {
				sesionMBean.setUrlTramite(null);
			}
			if(sTraza!=null) {
				sesionMBean.setCodigoTrazabilidadPadre(sTraza);
			}else {
				sesionMBean.setCodigoTrazabilidadPadre(null);
			}
      if(sTramite!=null) {
        sesionMBean.setCodigoTramite(sTramite);
      }else {
        sesionMBean.setCodigoTramite(null);
      }
			
			if(sIdioma!=null) {
				sesionMBean.setIdiomaActual(sIdioma);
			}else {
				if(request.getLocale()!=null) {
					sesionMBean.setIdiomaActual(request.getLocale().getLanguage());
				}else {
					sesionMBean.setIdiomaActual(Locale.getDefault().getLanguage());
				}
			}
			
			errorInit = false;
			Integer empresaId = null;
			Integer agendaId = null;
			Integer recursoId = null;
			
			if(sEmpresaId==null || sAgendaId==null) {
				addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
				errorInit = true;
				return;
			}
			try{
				empresaId = Integer.valueOf(sEmpresaId);
				agendaId = Integer.valueOf(sAgendaId);
				if(sRecursoId!=null) {
					recursoId = Integer.valueOf(sRecursoId);
				}
			}catch(Exception e) {
				addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
				errorInit = true;
				return;
			}
			
			//Poner en sesion los datos de la empresa  y la agenda para la válvula de CDA 
			//(necesita estos datos para determinar si la agenda particular requiere o no CDA)
			HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			httpSession.setAttribute("e", empresaId.toString());
			httpSession.setAttribute("a", agendaId.toString());
			
			String remoteUser = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
			
			try {
				// Crear un usuario falso temporal
				String falsoUsuario = null;
				if(remoteUser == null) {
					//No hay usuario, se crea uno
					sesionMBean.setUsuarioCda(null);
					falsoUsuario = "sae" + empresaId;
				}else {
					//Hay usuario, dos alternativas: es de cda o es local de otra empresa
					if(!remoteUser.startsWith("sae")) {
						//Es un usuario de CDA
						sesionMBean.setUsuarioCda(remoteUser);
						falsoUsuario = remoteUser;
					}else  {
						//Ees un usuario de otra empresa
						falsoUsuario = "sae" + empresaId;
						sesionMBean.setUsuarioCda(null);
					}
					//Desloguear al usuario actual (inválido)
					try {
						request.logout();
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				//Si no es un usuario de CDA se añade un número randómico para evitar conflictos con otros usuarios
				if(falsoUsuario.startsWith("sae")) {
	        Random random = new Random();
					falsoUsuario = falsoUsuario + "-" + ((new Date()).getTime()+random.nextInt(1000));
				}
				falsoUsuario = falsoUsuario+ "/" + empresaId;
				// Autenticarlo
				String password = Utilidades.encriptarPassword(falsoUsuario);
				request.login(falsoUsuario, password);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ApplicationException(sesionMBean.getTextos().get("no_se_pudo_registrar_un_usuario_anonimo"));
			}
			
			//Cargar los textos dependientes del idioma
			//Es necesario hacerlo de nuevo porque recién ahora se sabe a cuál esquema ir a buscarlos
			//Es especialmente necesario para las pregunta de captcha ya que no tienen idioma por defecto
			sesionMBean.cargarTextos();
			
			BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
			agendarReservasEJB = bl.getAgendarReservas();
			recursosEJB = bl.getRecursos();
			
			// Guardar la empresa en la sesion
			try {
				Empresa empresa = agendarReservasEJB.obtenerEmpresaPorId(empresaId);
				if (empresa==null || empresa.getFechaBaja()!=null) {
					addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
					sesionMBean.setEmpresaActual(null);
					errorInit = true;
					return;
				}else {
					sesionMBean.setEmpresaActual(empresa);
				}
			} catch (Exception e){
				addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
				errorInit = true;
				return;
			}
			recursosItems = new ArrayList<SelectItem>();
			String paginaDeRetorno = request.getParameter("pagina_retorno");
			if (empresaId != null && agendaId != null) {
				try {
					sesionMBean.seleccionarAgenda(agendaId);
					sesionMBean.setPaginaDeRetorno(paginaDeRetorno);
				} catch (Exception  ae) {
					addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
					errorInit = true;
					return;
				}
				String url = "/agendarReserva/Paso1.xhtml?e=" + empresaId + "&a=" + agendaId;
				if (recursoId != null) {
					url = url + "&r=" + recursoId;
				}
				if(sIdioma != null) {
					url = url + "&i=" + sIdioma;
				}
				if(sUrl != null) {
					url = url + "&u=" + sUrl;
				}
				if(sParms != null) {
					url = url + "&p=" + sParms;
				}
				if(sTraza != null) {
					url = url + "&t=" + sTraza;
				}
        if(sTramite != null) {
          url = url + "&q=" + sTramite;
        }
				sesionMBean.setUrlPaso1Reserva(url);
			}
			Recurso recursoDefecto = null;
			// Cargo los recursos
			if (sesionMBean.getAgenda() != null) {
				try{
					recursos = agendarReservasEJB.consultarRecursos(sesionMBean.getAgenda());
					if(recursos.isEmpty()) {
					  recursoTieneDisponibilidad = false;
						errorInit = true;
						return;
					}
				}catch(Exception ex) {
					addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
					errorInit = true;
					return;
				}	
				for (Recurso recurso : recursos) {
					if (recurso.getVisibleInternet()) {
						SelectItem item = new SelectItem();
						item.setLabel(recurso.getNombre());
						item.setValue(recurso.getId());
						recursosItems.add(item);
						// Si es el recurso que se ingreso por parametro se guarda para seleccionarlo por defecto.
						if (recursoId != null && recurso.getId().equals(recursoId)) {
							recursoDefecto = recurso;
						}
					}
				}
				// Selecciono el recurso por defecto.
				if (!recursos.isEmpty()) {
					if (recursoDefecto == null) {
					  if(recursoId != null) {
	            addAdvertenciaMessage(sesionMBean.getTextos().get("el_recurso_especificado_no_es_valido"));
					  }
						// No se ingreso un recurso en la url, o no existe ese recurso vivo para la agenda.
						// Si hay un recurso seleccionado, me quedo con ese, sino se carga el primero.
						if (sesionMBean.getRecurso() == null) {
							sesionMBean.setRecurso(recursos.get(0));
						}
					}else {
						// Se ingreso un recurso en la url y se encontro para la agenda.
						sesionMBean.setRecurso(recursoDefecto);
					}
				}
	      List<String> idiomasDisponibles = Arrays.asList(sesionMBean.getAgenda().getIdiomas().split(","));
	      if(!idiomasDisponibles.contains(sesionMBean.getIdiomaActual()) && !idiomasDisponibles.isEmpty()) {
	        LOGGER.debug("Cambiando el idioma porque el actual ("+sesionMBean.getIdiomaActual()+") no es un idioma soportado");
	        sesionMBean.cambioIdiomaActual(idiomasDisponibles.get(0));
	      }
			}
			mostrarMapa(sesionMBean.getRecurso());
		} catch (Exception e) {
			addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
			errorInit = true;
		}
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public String getAgendaNombre() {
		if (sesionMBean.getAgenda() != null) {
			return sesionMBean.getAgenda().getNombre();
		} else {
			return null;
		}
	}

	public String getRecursoId() {
		if (sesionMBean.getRecurso() != null) {
			return sesionMBean.getRecurso().getId().toString();
		} else {
			return null;
		}
	}

	public void setRecursoId(String sRecursoId) {
		Integer recursoId = Integer.valueOf(sRecursoId);
		if (!sesionMBean.getRecurso().getId().equals(recursoId)) {
			try {
				Boolean encontre = false;
				Iterator<Recurso> iter = recursos.iterator();
				Recurso r = null;
				while (iter.hasNext() && !encontre) {
					r = iter.next();
					if (r.getId().equals(recursoId)) {
						sesionMBean.setRecurso(r);
						encontre = true;
					}
				}
			} catch (Exception e) {
				addErrorMessage(e);
			}
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

	public List<SelectItem> getRecursosItems() {
		return recursosItems;
	}

	public Date getCurrentDate() {
		return sesionMBean.getCurrentDate();
	}

	public void setCurrentDate(Date current) {
		sesionMBean.setCurrentDate(current);
	}

	public Date getDiaSeleccionado() {
		// Siempre retorna null para que ante una vuelta atrás (del paso 2 al 1) con el botón del browser se redibuje el calendario sin tener un día marcado.
		// Esto es necesario pues solo se ejecuta el método setDiaSeleccionado si se da el evento onchanged en las celdas del calendario. Por lo tanto si el 
	  // día estuviera marcado en una vuelta atrás un click sobre la celda de este día no daría el efecto deseado (ejecutar el submit ajax en el evento onchanged).
		return null;
	}

	public void setDiaSeleccionado(Date dia) {
		sesionMBean.setDiaSeleccionado(dia);
	}

	public SesionMBean getSesionMBean() {
		return sesionMBean;
	}

	public void setSesionMBean(SesionMBean sesionMBean) {
		this.sesionMBean = sesionMBean;
	}

	public String getDireccionCompleta() {
		Recurso recurso = sesionMBean.getRecurso();
		if (recurso == null) {
			return "";
		}
		StringBuilder direccion = new StringBuilder("");
		if (recurso.getDireccion() != null) {
			direccion.append(recurso.getDireccion());
		}
		if (recurso.getLocalidad() != null) {
			if (direccion.length() > 0) {
				direccion.append(" - ");
			}
			direccion.append(recurso.getLocalidad());
		}
		if (recurso.getDepartamento() != null) {
			if (direccion.length() > 0) {
				direccion.append(" - ");
			}
			direccion.append(recurso.getDepartamento());
		}
		return direccion.toString();
	}

	public String getHorario() {
		Recurso recurso = sesionMBean.getRecurso();
		if (recurso == null) {
			return "";
		}
		if (recurso.getHorarios() != null) {
			return recurso.getHorarios();
		}
		return "";
	}

	public String getDescripcion() {
		if (getMensajeError() != null) {
			return null;
		}
		Agenda agenda = sesionMBean.getAgenda();
		if (agenda != null) {
			TextoAgenda textoAgenda = getTextoAgenda(agenda, sesionMBean.getIdiomaActual());
			if (textoAgenda != null) {
				String str = textoAgenda.getTextoPaso1();
				if (str != null) {
					return str;
				} else {
					return "";
				}
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	public String getEtiquetaSeleccionDelRecurso() {
		if (getMensajeError() != null) {
			return null;
		}
		Agenda agenda = sesionMBean.getAgenda();
		if (agenda != null) {
			TextoAgenda textoAgenda = getTextoAgenda(agenda, sesionMBean.getIdiomaActual());
			if (textoAgenda != null) {
				String str = textoAgenda.getTextoSelecRecurso();
				if (str != null) {
					return str;
				}else {
					return "";
				}
			}else {
				return "";
			}
		}else {
			return "";
		}
	}

	public String siguientePaso() {
		if (sesionMBean.getRecurso() != null) {
			try {
	      Recurso recurso = sesionMBean.getRecurso();
				VentanaDeTiempo ventanaCalendario = agendarReservasEJB.obtenerVentanaCalendarioInternet(recurso);
        List<Integer> listaCupos = agendarReservasEJB.obtenerCuposPorDia(recurso, ventanaCalendario, sesionMBean.getTimeZone());
				// Se carga la fecha inicial
				Calendar cont = Calendar.getInstance();
        cont.setTime(Utiles.time2InicioDelDia(ventanaCalendario.getFechaInicial()));
				int i = 0;
				Date inicio_disp = ventanaCalendario.getFechaInicial();
				Date fin_disp = ventanaCalendario.getFechaFinal();
				//boolean tieneDiponibilidad = false; 
				recursoTieneDisponibilidad = false;
        while (!cont.getTime().after(ventanaCalendario.getFechaFinal()) && !recursoTieneDisponibilidad) {
					if (cont.getTime().before(inicio_disp) || cont.getTime().after(fin_disp)) {
						listaCupos.set(i, -1);
					} else {
						if (listaCupos.get(i) > 0) {
						  recursoTieneDisponibilidad = true;
						}
					}
					cont.add(Calendar.DAY_OF_MONTH, 1);
					i++;
				}
				if(recursoTieneDisponibilidad) {
					return "siguientePaso";
				}else {
					mostrarMapa(recurso);
					return null;
				}
			} catch (Exception ex) {
				addErrorMessage(sesionMBean.getTextos().get("sin_disponibilidades"));
				ex.printStackTrace();
				return null;
			}
		}else {
			addErrorMessage(sesionMBean.getTextos().get("debe_especificar_el_recurso"));
			return null;
		}
	}

	public void cambioRecurso(ValueChangeEvent event) {
	  recursoTieneDisponibilidad = true;
		String sRecursoId = (String) event.getNewValue();
		Integer recursoId = Integer.valueOf(sRecursoId);
		Boolean encontre = false;
		Iterator<Recurso> iter = recursos.iterator();
		Recurso recurso = null;
		while (iter.hasNext() && !encontre) {
			recurso = iter.next();
			if (recurso.getId().equals(recursoId)) {
				mostrarMapa(recurso);
			}
		}
	}

	public void mostrarMapa(Recurso recurso) {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String schema = request.getScheme();
		String host = request.getServerName();
		String port = ""+request.getServerPort();
		String domain = request.getContextPath();
		if(!domain.startsWith("/")) {
			domain = "/" + domain;
		}
		if(!domain.endsWith("/")) {
			domain = domain + "/";
		}
		urlMapa = schema+"://"+host+":"+port+domain+"mapa/mapa2.html?";
		String lat = "";
		String lon = "";
		if(recurso != null) {
			if(recurso.getLatitud()!=null) {
				lat = recurso.getLatitud().toString();
			}
			if(recurso.getLongitud()!=null) {
				lon = recurso.getLongitud().toString();
			}
		}
		//Ejemplo: lat=-34.868297562379980&lon=-55.275735855102540"
		urlMapa = urlMapa+"lat="+lat+"&lon="+lon;
	}
	
	public String getUrlMapa() {
		if(urlMapa == null) {
			mostrarMapa(sesionMBean.getRecurso());
		}
		return urlMapa;
	}
	
	public boolean isErrorInit() {
		return errorInit;
	}

	public void setErrorInit(boolean errorInit) {
		this.errorInit = errorInit;
	}
	
  public boolean isRecursoTieneDisponibilidad() {
    return recursoTieneDisponibilidad;
  }

  @PreDestroy
  public void preDestroy() {
    try {
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      this.agendarReservasEJB = null;
      if(this.infoRecurso != null) {
        this.infoRecurso.clear();
      }
      this.infoRecurso = null;
      if(this.recursos!=null) {
        this.recursos.clear();
      }
      this.recursos = null;
      this.recursosEJB = null;
      if(this.recursosItems!=null) {
        this.recursosItems.clear();
      }
      this.recursosItems = null;
      this.sesionMBean = null;
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
    }
  }
	
}