/*
 * SAE - Sistema de Agenda Electrónica
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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.security.SecurityContext;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.security.SecurityContextFactory;
import org.picketbox.commons.cipher.Base64;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Usuario;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;
import uy.gub.imm.sae.web.common.SelectItemComparator;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;

public class SessionMBean extends SessionCleanerMBean {

	public static final String MSG_ID = "pantalla";

	private Usuario usuarioActual;
	private Empresa empresaActual;
	private List<SelectItem> empresasUsuario;
	private byte[] empresaActualLogoBytes;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
	private AgendaGeneral generalEJB;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas usuariosEmpresasEJB;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;
	
	
	@PostConstruct
	public void postConstruct() {
		//Se cargan los textos antes que los datos del usuario porque hay cosas que dependen de esto
		cargarTextos();
		//Se cargan los datos del usuario
		cargarDatosUsuario();
		//Se vuelven a cargar los textos despues de los datos del usuario para incluir los traducidos en el idioma actual
		cargarTextos();
	}
	
	private String idiomaActual = Locale.getDefault().getLanguage();
	
	// Pagina que se debe desplegar en la sección "pantalla" de la pagina principal
	private String viewId;
	private String pantallaTitulo;

	private RowList<Agenda> agendas;
	private Row<Agenda> rowSelectAgenda;

	private RowList<Recurso> recursos;
	private Row<Recurso> rowSelectRecurso;

	private RowList<DatoDelRecurso> datosDelRecurso;

	// Agenda/Recurso seleccionados para modificacion es necesario pues al navegar de la pagina 
	// modificarConsultar a modificar se pierde la row seleccionada en la tabla de agendas/recursos :(
	private Agenda agendaSeleccionada;
	private Recurso recursoSeleccionado;
	private DatoDelRecurso datoDelRecursoSeleccionado;

	// Booleana para saber si se despliega la tabla de Dato del Recurso
	private Boolean mostrarDato = true;
	// Booleana para saber si se despliega la tabla para agregar Dato del Recurso
	private Boolean mostrarAgregarDato = false;

	// Booleana para saber si se utiliza llamador o no
	private Boolean mostrarLlamador = true;

	// Variables para redirigir a sitio de Reservas
	private static final String URL_BASE_TO_FORWARD_RESERVA = "/agendarReserva/Paso1.xhtml?agenda=";

	private DatosUrlToForwardReserva datosUrlToForwardReserva = new DatosUrlToForwardReserva();

	private int pagina;

	private Map<String, DatoASolicitar> datosASolicitar;

	// Numero de puesto asignado al usuario en el momento de atender reservas
	// con el modulo Llamador
	private String puesto = "";

	private String codigoSeguridadReserva;
	
	private List<SelectItem> idiomasSoportados = null;

	public TimeZone getTimeZone() {
		//Primero se devuelve la de la Agenda, si tiene
		Agenda agenda = getAgendaMarcada();
		if(agenda!=null && agenda.getTimezone()!=null  && !agenda.getTimezone().trim().isEmpty()) {
			return TimeZone.getTimeZone(agenda.getTimezone());
		}
		//Si no tiene la Agenda, se devuelve la de la Empresa
		if(empresaActual != null && empresaActual.getTimezone() != null) {
			return TimeZone.getTimeZone(empresaActual.getTimezone());
		}
		//En otro caso se devuelve uno por defecto
		return TimeZone.getDefault();
	}
	
	public String getFormatoFecha() {
		if(empresaActual != null && empresaActual.getFormatoFecha() != null) {
			return empresaActual.getFormatoFecha();
		}
		return "dd/MM/yyyy";
	}

	public String getLocale() {
		return idiomaActual;
	}
	
	/*
	 * En el DatePicker el formato usa lo siguiente:
	 *   dd: día en dos dígitos
	 *   mm: mes en dos dígitos
	 *   yy: año en cuatro dígitos
	 */
	public String getFormatoFechaDatepicker() {
		String formatoJava = "dd/MM/yyyy";
		if(empresaActual != null && empresaActual.getFormatoFecha() != null) {
			formatoJava = empresaActual.getFormatoFecha();
		}
		return formatoJava.replace("yyyy", "yy").replace("MM", "mm");
	}
	
	
	public String getFormatoHora() {
		if(empresaActual != null && empresaActual.getFormatoHora() != null) {
			return empresaActual.getFormatoHora();
		}
		return "HH:mm";
	}
	
	// *****************************************************************************************************
	// ***************************Pasos para la reserva
	// *****************************************************************************************************
	private RowList<Disponibilidad> disponibilidadesDelDiaMatutina;
	private RowList<Disponibilidad> disponibilidadesDelDiaVespertina;
	private Disponibilidad disponibilidad;
	private Reserva reserva;
	//Estos dos datos solo se deben usar para la reserva, en el resto de los casos se debe usar getAgendaMarcada y getRecursoMarcado
	private Agenda agenda;
	private Recurso recurso;
	private Date diaSeleccionado;
	private VentanaDeTiempo ventanaCalendario;
	private VentanaDeTiempo ventanaMesSeleccionado;
	private List<Integer> cuposXdiaMesSeleccionado;
	private Date currentDate;
	private Reserva reservaConfirmada;

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public String getViewId() {
		if (viewId == null) {
			viewId = "/administracion/inicio.xhtml";
		}
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getPantallaTitulo() {
		return pantallaTitulo;
	}

	public void setPantallaTitulo(String pantallaTitulo) {
		this.pantallaTitulo = pantallaTitulo;
	}

	public void beforePhaseInicio(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			setPantallaTitulo(getTextos().get("inicio"));
		}
	}

	public void beforePhaseSeleccionAgendaRecurso(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			setPantallaTitulo(getTextos().get("seleccionar_agenda_recurso"));
		}
	}

	public RowList<Agenda> getAgendas() {
		return agendas;
	}

	// Agenda seleccionada en pantalla de selección de agendas y recursos
	public Agenda getAgendaMarcada() {
		if (agendas != null && agendas.getSelectedRow() != null) {
			return agendas.getSelectedRow().getData();
		} else {
			return null;
		}
	}

	// glabandera seteo agernda marcada
	public void setAgendaMarcada(Row<Agenda> SelectAgenda) {
		if (agendas != null) {
			agendas.setSelectedRow(SelectAgenda);
		}

	}

	public void desmarcarAgenda() {
		agendas.setSelectedRow(null);
	}

	public RowList<Recurso> getRecursos() {
		return recursos;
	}

	// Recurso seleccionado en pantalla de selección de agendas y recursos
	public Recurso getRecursoMarcado() {
		if (recursos != null && recursos.getSelectedRow() != null) {
			return recursos.getSelectedRow().getData();
		} else {
			return null;
		}
	}

	public void desmarcarRecurso() {
		recursos.setSelectedRow(null);
	}

	public RowList<DatoDelRecurso> getDatosDelRecurso() {
		cargarDatosDelRecurso();
		return datosDelRecurso;
	}

	public void setDatosDelRecurso(RowList<DatoDelRecurso> datosDelRecurso) {
		this.datosDelRecurso = datosDelRecurso;
	}

	public Agenda getAgendaSeleccionada() {
		return agendaSeleccionada;
	}

	public void setAgendaSeleccionada(Agenda agenda) {
		this.agendaSeleccionada = agenda;
	}

	public Recurso getRecursoSeleccionado() {
		return recursoSeleccionado;
	}

	public void setRecursoSeleccionado(Recurso recurso) {
		this.recursoSeleccionado = recurso;
		if (recurso != null) {
			this.desmarcarRecurso();
		}
	}

	public DatoDelRecurso getDatoDelRecursoSeleccionado() {
		return datoDelRecursoSeleccionado;
	}

	public void setDatoDelRecursoSeleccionado(
			DatoDelRecurso datoDelRecursoSeleccionado) {
		this.datoDelRecursoSeleccionado = datoDelRecursoSeleccionado;
	}

	public Boolean getMostrarAgregarDato() {
		return mostrarAgregarDato;
	}

	public void setMostrarAgregarDato(Boolean mostrarAgregarDato) {
		this.mostrarAgregarDato = mostrarAgregarDato;
	}

	public void seleccionarAgenda(SelectEvent event) {
		removeMBeansFromSession();
		if(this.rowSelectAgenda==null || this.rowSelectAgenda.getData().getId()==0) {
			setAgendaMarcada(null);
		}else {
			setAgendaMarcada(this.rowSelectAgenda);
		}
		cargarRecursos();
	}

	public void desseleccionarAgenda() {
		removeMBeansFromSession();
		setAgendaMarcada(null);
		cargarRecursos();
	}
	
	public void desseleccionarRecurso(SelectEvent a) {
		desseleccionarRecurso();
	}

	public void desseleccionarRecurso() {
		removeMBeansFromSession();
		if (this.rowSelectRecurso == null || this.rowSelectRecurso.getData().getId()==0) {
			this.recursos.setSelectedRow(null);
		}else {
			this.recursos.setSelectedRow(rowSelectRecurso);
		}
		this.setRecursoSeleccionado(null);
	}
	
	public void cargarAgendas() {
		List<Agenda> entidades;
		try {
			entidades = generalEJB.consultarAgendas();
			Agenda ninguna = new Agenda();
			ninguna.setId(0);
			ninguna.setNombre(getTextos().get("ninguna"));
			ninguna.setDescripcion(getTextos().get("ninguna"));
			entidades.add(0, ninguna);
			agendas = new RowList<Agenda>(entidades);
			if (recursos != null) {
				recursos.clear();
			}
		} catch (Exception e) {
			addErrorMessage(e, MSG_ID);
		}
	}

	// Si hay agenda selecciondada, se cargan los recursos asociados.
	// En caso contrario se vacía la lista de recursos
	public void cargarRecursos() {

		if (getAgendaMarcada() != null) {
			try {
				List<Recurso> entidades;
				entidades = generalEJB.consultarRecursos(getAgendaMarcada());
				recursos = new RowList<Recurso>(entidades);
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		} else {
			if (recursos != null) {
				recursos.clear();
			}
		}
	}
	
	/**
	 * Este método es usado para la pantalla de selección de recurso.
	 * Devuelve la lista de recursos de la agenda actual, a la cual añade un recurso adicional, con id=0 y nombre "ninguno"
	 * para permitir "desseleccionar" el recurso seleccionado (usado para generar reportes independientes del recurso)	 * 
	 * */
	public RowList<Recurso> getRecursosSeleccion() {
		
		RowList<Recurso> ret = new RowList<Recurso>();
		if(recursos==null || recursos.isEmpty()) {
			cargarRecursos();
		}
		
		if(recursos !=null) {
			ret.addAll(recursos);
		}

		Recurso ninguno = new Recurso();
		ninguno.setId(0);
		ninguno.setNombre(getTextos().get("ninguno"));
		ninguno.setDescripcion(getTextos().get("ninguno"));
		ret.add(0, new Row<Recurso>(ninguno, ret));
		
		return ret;
		
	}

	// Si hay recurso selecciondada, se cargan los datos del recurso asociados.
	// En caso contrario se vacía la lista de datosDelRecurso
	public void cargarDatosDelRecurso() {

		if (this.getRecursoSeleccionado() != null) {
			try {
				List<DatoDelRecurso> entidades;
				entidades = recursosEJB.consultarDatosDelRecurso(this
						.getRecursoSeleccionado());
				datosDelRecurso = new RowList<DatoDelRecurso>(entidades);
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		} else {
			if (datosDelRecurso != null) {
				datosDelRecurso.clear();
			}
		}
	}

	public Boolean getMostrarDato() {

		if (this.getDatoDelRecursoSeleccionado() != null) {
			mostrarDato = true;
		} else {
			mostrarDato = false;
		}
		return mostrarDato;
	}

	public DatosUrlToForwardReserva getDatosUrlToForwardReserva() {
		return datosUrlToForwardReserva;
	}

	public void setDatosUrlToForwardReserva(
			DatosUrlToForwardReserva datosUrlToForwardReserva) {
		this.datosUrlToForwardReserva = datosUrlToForwardReserva;
	}

	public String getUrlAgendarReservas() {

		String urlAgendarReserva = "#";

		if (getAgendaMarcada() != null && getRecursoMarcado() != null) {

			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) ctx
					.getExternalContext().getRequest();
			String urlRetorno = "#";

			try {
				urlRetorno = URLEncoder.encode(request.getRequestURL()
						.toString(), "utf-8");

				urlAgendarReserva = request.getContextPath()
						+ URL_BASE_TO_FORWARD_RESERVA
						+ URLEncoder.encode(getAgendaMarcada().getNombre(), "utf-8")
						+ "&recurso="
						+ URLEncoder.encode(getRecursoMarcado().getNombre(), "utf-8") + "&pagina_retorno=" + urlRetorno;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}

		return urlAgendarReserva;
	}

	public class DatosUrlToForwardReserva {
		private String urlReservaAgendaToForward;
		private boolean sePuedeHacerForwardAgendar;

		public DatosUrlToForwardReserva() {
			urlReservaAgendaToForward = "#";
			sePuedeHacerForwardAgendar = false;
		}

		public String getUrlReservaAgendaToForward() {
			return urlReservaAgendaToForward;
		}

		public void setUrlReservaAgendaToForward(
				String urlReservaAgendaToForward) {
			this.urlReservaAgendaToForward = urlReservaAgendaToForward;
		}

		public boolean isSePuedeHacerForwardAgendar() {
			return sePuedeHacerForwardAgendar;
		}

		public void setSePuedeHacerForwardAgendar(
				boolean sePuedeHacerForwardAgendar) {
			this.sePuedeHacerForwardAgendar = sePuedeHacerForwardAgendar;
		}
	}

	public Map<String, DatoASolicitar> getDatosASolicitar() {
		return datosASolicitar;
	}

	public void setDatosASolicitar(Map<String, DatoASolicitar> datosASolicitar) {
		this.datosASolicitar = datosASolicitar;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public Boolean getMostrarLlamador() {
		if (recursos != null && recursos.getSelectedRow() != null) {
			mostrarLlamador = recursos.getSelectedRow().getData()
					.getUsarLlamador();
		} else {
			mostrarLlamador = true;
		}
		return mostrarLlamador;
	}

	public void setMostrarLlamador(Boolean mostrarLlamador) {
		this.mostrarLlamador = mostrarLlamador;
	}

	public Row<Agenda> getRowSelectAgenda() {
		return rowSelectAgenda;
	}

	public void setRowSelectAgenda(Row<Agenda> rowSelectAgenda) {
		this.rowSelectAgenda = rowSelectAgenda;
	}

	public Row<Recurso> getRowSelectRecurso() {
		return rowSelectRecurso;
	}

	public void setRowSelectRecurso(Row<Recurso> rowSelectRecurso) {
		this.rowSelectRecurso = rowSelectRecurso;
	}

	public Usuario getUsuarioActual() {
		return usuarioActual;
	}

	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuarioActual = usuarioActual;
	}

	public Empresa getEmpresaActual() {
		return empresaActual;
	}

	public void setEmpresaActual(Empresa empresaActual) {
		this.empresaActual = empresaActual;
	}

	public void setEmpresasUsuario(List<SelectItem> empresasUsuario) {
		this.empresasUsuario = empresasUsuario;
	}

	public List<SelectItem> getEmpresasUsuario() {
		return empresasUsuario;
	}

	public String getEmpresaActualId() {
		if (empresaActual != null) {
			return empresaActual.getId().toString();
		}
		return null;
	}

	
	public void cargarDatosUsuario() {
		
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = request.getSession();
			
			//Esto es para prevenir el ataque de Session Fixation: cada vez que se pide recargar los datos del usuario se
			//genera un id de sesión nuevo, y se copian todos los datos que estaban de la anterior
			//1-Almacenar en un map temporal todas las propiedades de la sesión actual
			Map<String, Object> map0 = new HashMap<String, Object>();
			Enumeration<String> en = session.getAttributeNames();
			while(en.hasMoreElements()) {
				String attNombre = en.nextElement();
				map0.put(attNombre, session.getAttribute(attNombre));
			}
			//2-Invalidar la sesión actual
			session.invalidate();
			//3-Crear una nueva sesión
			session = request.getSession(true);
			//4-Poner en la sesión nueva los datos de la sesión anterior
			for(String nombre : map0.keySet()) {
				session.setAttribute(nombre, map0.get(nombre));
			}
			
			if (usuarioActual == null) {
				// No esta definido el usuario actual, se carga ahora
				//Si está en la sesión el atributo "codigocda" se utiliza como código de usuario el atributo "documentocda",
				//sino se utiliza el atributo remoteUser; además, si viene de CDA hay que autenticarlo programáticamente (la
				//contraseña usada es la encriptacion con RSA y la clave privada del certificado usado por la válvula, se debe
				//verificar con la clave pública del mismo certificado)
				//Nota: la válvula pone valores como los siguientes: codigocda=uy-ci-88888889, documentocda=88888889
				Map<String, Object> sessionAttrs = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
				if(sessionAttrs.containsKey("codigocda") && sessionAttrs.containsKey("documentocda") && 
						sessionAttrs.containsKey("codigocda_encriptado") && sessionAttrs.containsKey("documentocda_encriptado")) {
					
					//Viene de la válvula de CDA
					
					String codigoCda = (String)sessionAttrs.get("documentocda");
					byte[] codigoCdaEncriptado = (byte[])sessionAttrs.get("documentocda_encriptado");

					loginUsername = codigoCda;
					loginPassword = Base64.encodeBytes(codigoCdaEncriptado);
					
					try {
		        if (request.getUserPrincipal() != null) {
		        	request.logout();
		        }
		        
						SecurityContext secContext = SecurityContextFactory.createSecurityContext("SDSAE");
						SecurityContextAssociation.setSecurityContext(secContext);
						request.login(loginUsername, loginPassword);
						Principal principal = request.getUserPrincipal();
						SecurityContextAssociation.setPrincipal(principal);
					}catch(Exception ex) {
						//No se pudo loguear al usuario
						//Se crea un usuario artificial solo para poder ofrecer el link "logout" si es de CDA
						usuarioActual = new Usuario();
						usuarioActual.setCodigo(loginUsername);
						usuarioActual.setNombre(loginUsername);
						FacesContext ctx = FacesContext.getCurrentInstance();
						ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
						return;
					}
					
					//Quitar las propiedades que puso la válvula de CDA
					session.removeAttribute("codigocda");
					session.removeAttribute("documentocda");
					session.removeAttribute("codigocda_encriptado");
					session.removeAttribute("documentocda_encriptado");
				}
				
				/* */
				
				String codigo = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
				if (codigo != null) {
					try {
						usuarioActual = usuariosEmpresasEJB.obtenerUsuarioPorCodigo(codigo);
						empresasUsuario = new ArrayList<SelectItem>();
						if (usuarioActual != null) {
							// Si es superadmin tiene acceso a todas las empresas, sino solo a las que tiene asociadas
							if (usuarioActual.isSuperadmin()) {
								for (Empresa emp : usuariosEmpresasEJB.consultarEmpresas()) {
									empresasUsuario.add(new SelectItem(emp.getId(), emp.getNombre()));
								}
							} else {
								for (Empresa emp : usuariosEmpresasEJB.consultarEmpresasPorUsuario(usuarioActual)) {
									empresasUsuario.add(new SelectItem(emp.getId(), emp.getNombre()));
								}
							}
						}
						if (!empresasUsuario.isEmpty()) {
							seleccionarEmpresa((Integer) empresasUsuario.get(0).getValue());
							empresaActualLogoBytes = usuariosEmpresasEJB.obtenerLogoEmpresaPorEmpresaId((Integer) empresasUsuario.get(0).getValue());
						} else {
							empresaActual = null;
						}
					} catch (ApplicationException aEx) {
						aEx.printStackTrace();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cargarEmpresasUsuario() {
		empresasUsuario = new ArrayList<SelectItem>();
		try {
			if (usuarioActual != null) {
				List<Empresa> empresas = null;
				if (usuarioActual.isSuperadmin()) {
					empresas = usuariosEmpresasEJB.consultarEmpresas();
				} else {
					empresas = usuarioActual.getEmpresas();
				}
				if(empresas!= null && !empresas.isEmpty()) {
					for (Empresa emp : empresas) {
						empresasUsuario.add(new SelectItem(emp.getId(), emp.getNombre()));
					}
					if(empresaActual == null || !empresas.contains(empresaActual)) {
						empresaActual = empresas.get(0);
					}
				}else {
					empresaActual = null;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void cambioEmpresa(ValueChangeEvent event) {
		try {
			Integer empId = Integer.valueOf((String) event.getNewValue());
			cambioEmpresa(empId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cambioEmpresa(Integer empId) {
		empresaActual = null;
		try {
			// Seleccionar la nueva empresa
			seleccionarEmpresa(empId);
			//Buscar el logo de la empresa
			empresaActualLogoBytes = usuariosEmpresasEJB.obtenerLogoEmpresaPorEmpresaId(empId);
			// Desmarcar la agenda y el recurso (pertenecen a otra empresa)
			if (recursos != null) {
				recursos.setSelectedRow(null);
			}
			if (agendas != null) {
				agendas.setSelectedRow(null);
			}
			// Cargar nuevamente los textos
			cargarTextos();
			// Navegar a la pagina de inicio
			ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
			String contextPath = ectx.getRequestContextPath();
			ectx.redirect(contextPath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Metodo que dado el id de una empresa busca la empresa correspondiente
	 * entre las empresas del usuario actual
	 * 
	 * @param orgId
	 */
	public void seleccionarEmpresa(Integer empresaId) {
		try {
			empresaActual = null;
			empresaActual = usuariosEmpresasEJB.obtenerEmpresaPorId(empresaId);
			String usuarioCompuesto = loginUsername + "/" + empresaActual.getId();
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			// Desloguear al usuario actual
			request.logout();
			// Volver a loguearlo en la nueva empresa
			request.login(usuarioCompuesto, loginPassword);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// Cargar las agendas del usuario para la empresa actual
		cargarAgendas();
	}

	public String cerrarSesion() {
		//Si la autenticación fue con la válvula de CDA se invoca al logout de CDA, sino se hace logout local
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		try {
			request.logout();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		fc.getExternalContext().invalidateSession();
		return "inicio";
	}

	public String getTipoLogout() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		if(session.getAttribute("CDAServiceProviderValve")!=null) {
			return "CDA";
		}else {
			return "LOCAL";
		}
	}
	
	private String loginUsername;
	private String loginPassword;

	/**
	 * Operacion para autenticar usuarios. Nota: aunque se utiliza JAAS, no se
	 * usa el mecanismo directo (j_security_check, j_username, j_password)
	 * porque es necesario redirigir al usuario siempre a la pagina de inicio, y
	 * el mecanismo directo siempre lleva al usuario a la pagina que solicito
	 * (la cual podria depender de que el usuario haga algo antes de acceder a
	 * ella, por ejemplo seleccionar una empresa, o una agenda). Los parametros
	 * se toman directamente del request ("formLogin:username") y
	 * ("formLogin:password") para no inroducir dos atributos en el session bean
	 * que despues no se van a usar nunca mas. Hay que tener cuidado de que si
	 * cambia el nombre del form ("formLogin") se puede afectar a este metodo.
	 * 
	 * @return
	 */
	public String iniciarSesion() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();

		String username = request.getParameter("formLogin:username");
		String password = request.getParameter("formLogin:password");
		
		if(username==null || username.isEmpty() || password==null || password.isEmpty()) {
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe ingresar su código de usuario  y contraseña"));
			return null;
		}
		try {
			
			request.login(username, password);
			this.loginUsername = username;
			this.loginPassword = password;

			cargarDatosUsuario();

			return "inicio";
		} catch (Exception ex) {
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Código de usuario o contraseña incorrectos"));
			return null;
		}

	}
	
	public StreamedContent getEmpresaActualLogo() {
		// No se puede cachear porque un stream y la segunda vez que el cliente
		// lo pide está cerrado y no es muestra la imagen
		if (empresaActual != null && empresaActualLogoBytes != null) {
			return new DefaultStreamedContent(new ByteArrayInputStream(empresaActualLogoBytes));
		}
		return null;
	}

	public String getEmpresaActualLogoTexto() {
		if (empresaActual != null) {
			if (empresaActual.getLogoTexto() != null
					&& !empresaActual.getLogoTexto().trim().isEmpty()) {
				return empresaActual.getLogoTexto();
			} else {
				return empresaActual.getNombre();
			}
		}
		return "Logo";
	}

	public String getEmpresaActualNombre() {
		if (empresaActual != null) {
			return empresaActual.getNombre();
		} else {
			return "";
		}
	}
	
	// *****************************************************************************************************
	// ***************************Pasos para la reserva
	// Admin***********************************************
	// *****************************************************************************************************
	public void limpiarPaso2() {

		setDisponibilidadesDelDiaMatutina(null);
		setDisponibilidadesDelDiaVespertina(null);

		setDisponibilidad(null);
		setReserva(null);

		limpiarPaso3();
	}

	public void limpiarPaso3() {
	}

	public RowList<Disponibilidad> getDisponibilidadesDelDiaMatutina() {
		return disponibilidadesDelDiaMatutina;
	}

	public void setDisponibilidadesDelDiaMatutina(
			RowList<Disponibilidad> disponibilidadesDelDiaMatutina) {
		this.disponibilidadesDelDiaMatutina = disponibilidadesDelDiaMatutina;
	}

	public RowList<Disponibilidad> getDisponibilidadesDelDiaVespertina() {
		return disponibilidadesDelDiaVespertina;
	}

	public void setDisponibilidadesDelDiaVespertina(
			RowList<Disponibilidad> disponibilidadesDelDiaVespertina) {
		this.disponibilidadesDelDiaVespertina = disponibilidadesDelDiaVespertina;
	}

	public Disponibilidad getDisponibilidad() {
		return disponibilidad;
	}

	public void setDisponibilidad(Disponibilidad disponibilidad) {
		this.disponibilidad = disponibilidad;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public Recurso getRecurso() {
		return recurso;
	}

	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}

	public Date getDiaSeleccionado() {
		return diaSeleccionado;
	}

	public void setDiaSeleccionado(Date diaSeleccionado) {
		this.diaSeleccionado = diaSeleccionado;
	}

	public VentanaDeTiempo getVentanaMesSeleccionado() {
		return ventanaMesSeleccionado;
	}

	public void setVentanaMesSeleccionado(VentanaDeTiempo ventanaMesSeleccionado) {
		this.ventanaMesSeleccionado = ventanaMesSeleccionado;
	}

	public List<Integer> getCuposXdiaMesSeleccionado() {
		return cuposXdiaMesSeleccionado;
	}

	public void setCuposXdiaMesSeleccionado(
			List<Integer> cuposXdiaMesSeleccionado) {
		this.cuposXdiaMesSeleccionado = cuposXdiaMesSeleccionado;
	}

	public VentanaDeTiempo getVentanaCalendario() {
		return ventanaCalendario;
	}

	public void setVentanaCalendario(VentanaDeTiempo ventanaCalendario) {
		this.ventanaCalendario = ventanaCalendario;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Reserva getReservaConfirmada() {
		return reservaConfirmada;
	}

	public void setReservaConfirmada(Reserva reservaConfirmada) {
		this.reservaConfirmada = reservaConfirmada;
	}

	public String getCodigoSeguridadReserva() {
		return codigoSeguridadReserva;
	}

	public void setCodigoSeguridadReserva(String codigoSeguridadReserva) {
		this.codigoSeguridadReserva = codigoSeguridadReserva;
	}

	//*******************************************************************
	// PARA LOS TEXTOS FIJOS
	//*******************************************************************
	
	private Map<String, String> textos = new HashMap<String, String>();

	public void cargarTextos() {
		try {
			textos = generalEJB.consultarTextos(idiomaActual);
		} catch (ApplicationException e) {
			textos = new HashMap<String, String>();
			e.printStackTrace();
		}
	}
	
  public Map<String, String> getTextos() {
    return textos;
  }
	
	public List<SelectItem> getIdiomasSoportados() {
		if(idiomasSoportados == null) {
			List<String> sIdiomas = usuariosEmpresasEJB.obtenerIdiomasSoportados();
			Locale localeActual = new Locale(idiomaActual);
			idiomasSoportados = new ArrayList<SelectItem>();
			if(sIdiomas != null && !sIdiomas.isEmpty() ) {
				for(String sIdioma : sIdiomas) {
					Locale locale = new Locale(sIdioma);
					idiomasSoportados.add(new SelectItem(locale.getLanguage(), locale.getDisplayLanguage(localeActual)));
				}
			}else {
				for(Locale locale : Locale.getAvailableLocales()) {
					if(locale.getCountry().isEmpty()) {
						idiomasSoportados.add(new SelectItem(locale.getLanguage(), locale.getDisplayLanguage(localeActual)));
					}
				}
			}
		}
		Collections.sort(idiomasSoportados, new SelectItemComparator());
		return idiomasSoportados;
	}

	public String getIdiomaActual() {
		return idiomaActual;
	}

	public void setIdiomaActual(String idiomaActual) {
		this.idiomaActual = idiomaActual;
	}

	public void cambioIdiomaActual(ValueChangeEvent event) {
		idiomasSoportados = null;
		idiomaActual = (String) event.getNewValue();
		cargarTextos();
	}
	
	public byte[] getEmpresaActualLogoBytes() {
		
		return empresaActualLogoBytes;
	}

	public void setEmpresaActualLogoBytes(byte[] empresaActualLogoBytes) {
		this.empresaActualLogoBytes = empresaActualLogoBytes;
	}
	
	public boolean getBackendConCda() {
		Object backendConCda = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("CDAServiceProviderValve");
		if(backendConCda!=null && backendConCda.toString().equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}
	
	public void controlarAccesoRestringido(ComponentSystemEvent event) {
    try {
			FacesContext ctx = FacesContext.getCurrentInstance();
	    Principal principal = ctx.getExternalContext().getUserPrincipal();
	    if(principal == null) {
				ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
	    }
    }catch(Exception ex) {
    	//
    }
	}
	
	

	
	
	public void limpiarTrazas() {
		agendarReservasEJB.limpiarTrazas();
	}

}
