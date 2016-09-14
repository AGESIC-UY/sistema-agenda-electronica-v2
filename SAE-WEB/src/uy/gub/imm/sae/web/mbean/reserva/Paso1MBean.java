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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJBAccessException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONArray;

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
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.login.Utilidades;
import uy.gub.imm.sae.web.common.SAECalendarioDataSource;

/*
 * Invocación desde un sistema externo:
 * https://192.168.1.13:8443/sae/agendarReserva/Paso1.xhtml?e=1000001&a=29&u=http%3A%2F%2Fgoogle.com.uy&p=13.61.789456;13.60.CI;13.62.@@email
 * 
 */


public class Paso1MBean extends PasoMBean implements SAECalendarioDataSource {

	static Logger logger = Logger.getLogger(Paso1MBean.class);

	private AgendarReservas agendarReservasEJB;

	private Recursos recursosEJB;

	private SesionMBean sesionMBean;

	/*
	 * Será utilizado solamente en casos extermos, como que no tenga permiso para
	 * acceder a la agenda, o la misma no sea valida, etc...
	 */
	private String mensajeError = null;

	private List<Recurso> recursos;
	private List<SelectItem> recursosItems;

	private List<DatoDelRecurso> infoRecurso;
	private JSONArray jsonArrayFchDisp;
	private boolean errorInit;
	private String urlMapa;

	public JSONArray getJsonArrayFchDisp() {
		return jsonArrayFchDisp;
	}

	public void setJsonArrayFchDisp(JSONArray jsonArrayFchDisp) {
		this.jsonArrayFchDisp = jsonArrayFchDisp;
	}

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

			String sEmpresaId = request.getParameter("e"); //Id de la empresa
			String sAgendaId = request.getParameter("a"); //Id de la agenda
			String sRecursoId = request.getParameter("r"); //Id del recurso
			
			String sIdioma = request.getParameter("i"); //Idioma (es,en,...)
			String sUrl = request.getParameter("u"); //URL de retorno al confirmar (URL encoded)
			String sParms = request.getParameter("p"); //Parámetros para autocompletar: <idagrupacion>.<iddato>.<valor>;)
			String sTraza = request.getParameter("t"); //Código de trazabilidad y paso padre (<trazguid>-<paso>)
			
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
			
			if(sEmpresaId==null || sAgendaId ==null) {
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
			
			if (remoteUser == null || !remoteUser.startsWith("sae" + empresaId)) {
				//No hay usuario o hay un usuario que no es de esta empresa (puede ser de CDA u otra empresa)
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
							//falsoUsuario = "sae"+remoteUser;
							falsoUsuario = remoteUser;
						}else  {
							//Ees un usuario de otra empresa
							falsoUsuario = "sae" + empresaId;;
							sesionMBean.setUsuarioCda(null);
						}
						//Desloguear al usuario actual (inválido)
						try {
							request.logout();
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					}
					Random random = new Random();
					//Si no es un usuario de CDA se añade un número randómico para evitar conflictos con otros usuarios
					if(falsoUsuario.startsWith("cda")) {
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
			}
			
			//Cargar los textos dependientes del idioma
			sesionMBean.cargarTextos();
			//Restauar beans necesarios
			
			BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
			agendarReservasEJB = bl.getAgendarReservas();
			recursosEJB =bl.getRecursos();
			
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
			boolean soloCuerpo = Boolean.parseBoolean(request.getParameter("solo_cuerpo"));
			if (empresaId != null && agendaId != null) {
				// Se esta indicando a que agenda se desea acceder
				if (sesionMBean.getAgenda() == null || !sesionMBean.getAgenda().getId().equals(agendaId)) {
					// Y es distinta de la actualmente seleccionada
					try {
						sesionMBean.seleccionarAgenda(agendaId);
						sesionMBean.setPaginaDeRetorno(paginaDeRetorno);
						sesionMBean.setSoloCuerpo(soloCuerpo);
					} catch (Exception  ae) {
						addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
						errorInit = true;
						return;
					}
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

				sesionMBean.setUrlPaso1Reserva(url);
			}

			Recurso recursoDefecto = null;
			// Cargo los recursos
			if (sesionMBean.getAgenda() != null) {
				try{
					recursos = agendarReservasEJB.consultarRecursos(sesionMBean.getAgenda());
					if(recursos.size()==0) {
						addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
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

						// Si es el recurso que se ingreso por parametro se guarda para
						// seleccionarlo por defecto.
						if (recursoId != null && recurso.getId().equals(recursoId)) {
							recursoDefecto = recurso;
						}
					} else if (recursoId != null && recurso.getId().equals(recursoId)) {
						throw new RolException(sesionMBean.getTextos().get("recurso_no_habilitado_para_ser_accedido_desde_internet"));
					}
				}

				// Selecciono el recurso por defecto.
				if (!recursos.isEmpty()) {
					if (recursoDefecto == null) {
						// No se ingreso un recurso en la url, o no existe ese recurso vivo
						// para la agenda.
						// Si hay un recurso seleccionado, me quedo con ese, sino se carga
						// el primero.
						if (sesionMBean.getRecurso() == null) {
							sesionMBean.setRecurso(recursos.get(0));
						}
					} else {
						// Se ingreso un recurso en la url y se encontro para la agenda.
						sesionMBean.setRecurso(recursoDefecto);
					}
				}
			}

			mostrarMapa(sesionMBean.getRecurso());
		} catch (RolException e1) {
			// El usuario no tiene suficientes privilegios para acceder a la agenda
			setMensajeError(sesionMBean.getTextos().get("acceso_denegado"));
		} catch (EJBAccessException e2) {
			// El usuario no tiene suficientes privilegios para acceder a la agenda
			setMensajeError(sesionMBean.getTextos().get("acceso_denegado"));
		} catch (Exception e) {
			addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
		}
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	/**
	 * Es necesario pues debo forzar a que desde cada paso que se ejecute el init
	 * de este managed bean, pues es en el donde se analizan los parametros del
	 * request de donde se saca el solo_cuerpo
	 * 
	 * @return
	 */
	public Boolean getSoloCuerpo() {
		return sesionMBean.getSoloCuerpo();
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

		// Siempre retorno null, asi de esta forma ante una vuelta atras (del paso 2
		// al 1) con el boton
		// del browser, se redibuja el calendario sin tener dia marcado.
		// Esto lo necesito pues solo se ejecuta el setDiaSeleccionado si se da el
		// evento onchanged
		// en las celdas del calendario.
		// Por lo tanto si el dia estuviera marcado en una vuelta atrás, un click
		// sobre la celda de este dia
		// no daria el efecto deseado (ejecutar el submit ajax en el evento
		// onchanged)
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
		Agenda a = sesionMBean.getAgenda();
		if (a != null) {
			//TextoAgenda textoAgenda = a.getTextoAgenda();
			TextoAgenda textoAgenda = getTextoAgenda(a, sesionMBean.getIdiomaActual());
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
		if (getMensajeError() != null)
			return null;
		Agenda a = sesionMBean.getAgenda();
		if (a != null) {
			//TextoAgenda textoAgenda = a.getTextoAgenda();
			TextoAgenda textoAgenda = getTextoAgenda(a, sesionMBean.getIdiomaActual());
			if (textoAgenda != null) {
				String str = textoAgenda.getTextoSelecRecurso();
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

	// Implementacion de la interfaz SAECalendarioDataSource
	public List<Integer> obtenerCuposXDia(Date desde, Date hasta) {
		if (getMensajeError() != null)
			return null;

		// Si cambio el mes: actualizo.
		if (!sesionMBean.getVentanaMesSeleccionado().getFechaInicial().equals(Utiles.time2InicioDelDia(desde))
				|| !sesionMBean.getVentanaMesSeleccionado().getFechaFinal().equals(Utiles.time2FinDelDia(hasta))) {

			sesionMBean.getVentanaMesSeleccionado().setFechaInicial(Utiles.time2InicioDelDia(desde));
			sesionMBean.getVentanaMesSeleccionado().setFechaFinal(Utiles.time2FinDelDia(hasta));

			sesionMBean.setCuposXdiaMesSeleccionado(null);

			try {
				cargarCuposADesplegar(sesionMBean.getRecurso(), sesionMBean.getVentanaMesSeleccionado());

			} catch (Exception e) {
				addErrorMessage(e);
			}
		}

		return sesionMBean.getCuposXdiaMesSeleccionado();
	}

	private void cargarCuposADesplegar(Recurso r, VentanaDeTiempo ventanaMesSeleccionado) {

		List<Integer> listaCupos = null;
		try {
			listaCupos = agendarReservasEJB.obtenerCuposPorDia(r, ventanaMesSeleccionado);
			// Se carga la fecha inicial
			Calendar cont = Calendar.getInstance();
			cont.setTime(Utiles.time2InicioDelDia(sesionMBean.getVentanaMesSeleccionado().getFechaInicial()));

			Integer i = 0;

			Date inicio_disp = sesionMBean.getVentanaCalendario().getFechaInicial();
			Date fin_disp = sesionMBean.getVentanaCalendario().getFechaFinal();

			jsonArrayFchDisp = new JSONArray();
			// Recorro la ventana dia a dia y voy generando la lista completa de cupos
			// x dia con -1, 0, >0 según corresponda.
			while (!cont.getTime().after(sesionMBean.getVentanaMesSeleccionado().getFechaFinal())) {
				if (cont.getTime().before(inicio_disp) || cont.getTime().after(fin_disp)) {
					listaCupos.set(i, -1);
				} else {
					if (listaCupos.get(i) > 0) {
						String dateStr = String.valueOf(cont.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(cont.get(Calendar.MONTH) + 1) + "/"
								+ String.valueOf(cont.get(Calendar.YEAR));
						jsonArrayFchDisp.put(dateStr);
					}
				}
				cont.add(Calendar.DAY_OF_MONTH, 1);
				i++;
			}
			sesionMBean.setCuposXdiaMesSeleccionado(listaCupos);
		} catch (Exception e) {
			addErrorMessage(e);
		}
	}

	public String siguientePaso() {
		if (sesionMBean.getRecurso() != null)
		{
			Recurso recurso = sesionMBean.getRecurso();
			try {
				VentanaDeTiempo ventanaCalendario = agendarReservasEJB.obtenerVentanaCalendarioInternet(recurso);
				VentanaDeTiempo ventanaMesSeleccionado = new VentanaDeTiempo();
				Calendar cal = Calendar.getInstance();
				cal.setTime(ventanaCalendario.getFechaInicial());
				cal.set(Calendar.DAY_OF_MONTH,cal.getActualMinimum(Calendar.DAY_OF_MONTH));
				ventanaMesSeleccionado.setFechaInicial(Utiles.time2InicioDelDia(cal.getTime()));
				cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				ventanaMesSeleccionado.setFechaFinal(Utiles.time2FinDelDia(cal.getTime()));
				
				List<Integer> listaCupos = agendarReservasEJB.obtenerCuposPorDia(recurso,ventanaMesSeleccionado);
				// Se carga la fecha inicial
				Calendar cont = Calendar.getInstance();
				cont.setTime(Utiles.time2InicioDelDia(ventanaMesSeleccionado.getFechaInicial()));

				Integer i = 0;

				Date inicio_disp = ventanaCalendario.getFechaInicial();
				Date fin_disp = ventanaCalendario.getFechaFinal();
				boolean tieneDiponibilidad = false; 
				while (!cont.getTime().after(ventanaMesSeleccionado.getFechaFinal()) && tieneDiponibilidad == false) {
					if (cont.getTime().before(inicio_disp)
							|| cont.getTime().after(fin_disp)) {
						listaCupos.set(i, -1);
					} else {
						if (listaCupos.get(i) > 0) {
							
							tieneDiponibilidad = true;
						}

					}
					cont.add(Calendar.DAY_OF_MONTH, 1);
					i++;
				}
				if(tieneDiponibilidad)
				{
					return "siguientePaso";
				}else
				{
					addErrorMessage(sesionMBean.getTextos().get("sin_disponibilidades"));
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
		//lat=-34.868297562379980&lon=-55.275735855102540"
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

}