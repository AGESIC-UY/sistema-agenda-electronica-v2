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

package uy.gub.imm.sae.web.mbean.administracion;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.web.common.BaseMBean;

public class Paso1AdminMBean extends BaseMBean {
	
	static Logger logger = Logger.getLogger(Paso1AdminMBean.class);
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
		
	private SessionMBean sessionMBean;

	/* Será utilizado solamente en casos extermos, como que no tenga permiso para acceder a la agenda, o la misma no sea valida, etc...*/
	private boolean errorInit = false;
	
	private List<Recurso> recursos;
	private List<SelectItem> recursosItems;

	private List<DatoDelRecurso> infoRecurso;
	public static final String MSG_ID = "pantalla";
	
	private String urlMapa;
	
  private boolean recursoTieneDisponibilidad = true;

	public void beforePhase (PhaseEvent phaseEvent) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_FCALL_CENTER", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		disableBrowserCache(phaseEvent);
		if (phaseEvent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("realizar_reserva"));
			sessionMBean.limpiarPaso2();
		}
	}

	@PostConstruct
	public void init() {
		try {
		  errorInit = false;
			recursosItems = new ArrayList<SelectItem>();
			Agenda agenda = sessionMBean.getAgendaMarcada();
      if (agenda == null) {
        addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"));
        errorInit = true;
        return;
      }
			Recurso recursoDefecto = sessionMBean.getRecursoMarcado();
			//Cargo los recursos
			sessionMBean.setAgenda(agenda);
			recursos = agendarReservasEJB.consultarRecursos(agenda);
			if(recursos.isEmpty()) {
        addErrorMessage(sessionMBean.getTextos().get("no_hay_recursos_disponibles_para_la_agenda_seleccionada"));
        errorInit = true;
        return;
			}
			for (Recurso recurso : recursos) {
				SelectItem item = new SelectItem();
				item.setLabel(recurso.getNombre());
				item.setValue(recurso.getId());
				recursosItems.add(item);
			}
			//Selecciono el recurso por defecto.
			if (! recursos.isEmpty() ) {
				if (recursoDefecto == null ){
					//No se ingreso un recurso en la url, o no existe ese recurso vivo para la agenda.
					//Si hay un recurso seleccionado, me quedo con ese, sino se carga el primero.
					if (sessionMBean.getRecurso() == null){
						sessionMBean.setRecurso(recursos.get(0));
					}
				} else {
					//Se ingreso un recurso en la url y se encontro para la agenda.
					sessionMBean.setRecurso(recursoDefecto);
				}
			} 
			mostrarMapa(sessionMBean.getRecurso());
		} catch (Exception ex) {
			ex.printStackTrace();
			redirect(ERROR_PAGE_OUTCOME);
		}
	}
	
	public boolean getErrorInit() {
		return errorInit;
	}

	public String getAgendaNombre() {
		if(sessionMBean.getAgenda() != null) {
			return sessionMBean.getAgenda().getNombre();
		}else {
			return null;
		}
	}
	
	public String getRecursoId() {
		if (sessionMBean.getRecurso() != null) {
			return sessionMBean.getRecurso().getId().toString();
		}else {
			return null;
		}
	}

	public void setRecursoId(String sRecursoId) {
		Integer recursoId = Integer.valueOf(sRecursoId);
		if (!sessionMBean.getRecurso().getId().equals(recursoId)) {
			try {
				Boolean encontre = false;
				Iterator<Recurso> iter = recursos.iterator();
				while (iter.hasNext() && ! encontre) {
					Recurso r = iter.next();
					if (r.getId().equals(recursoId)) {
						sessionMBean.setRecurso(r);
						encontre = true;
					}
				}
			}catch (Exception e) {
				addErrorMessage(e,MSG_ID);
			}
		}
	}

	public List<DatoDelRecurso> getInfoRecurso() {
		if(infoRecurso == null) {
			if(sessionMBean.getRecurso() != null) {
				try {
					infoRecurso = recursosEJB.consultarDatosDelRecurso(sessionMBean.getRecurso());
					if (infoRecurso.isEmpty()) {
						infoRecurso = null;
					}
				}catch (Exception e) {
					addErrorMessage(e,MSG_ID);
				}
			}
		}
		return infoRecurso;
	}
	
	public List<SelectItem> getRecursosItems() {
		return recursosItems;
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}
	
	public String getDireccionCompleta() {
		Recurso recurso = sessionMBean.getRecurso();
		if(recurso == null) {
			return "";
		}
		StringBuilder direccion = new StringBuilder("");
		if(recurso.getDireccion()!=null) {
			direccion.append(recurso.getDireccion());
		}
		if(recurso.getLocalidad() != null) {
			if(direccion.length()>0) {
				direccion.append(" - ");
			}
			direccion.append(recurso.getLocalidad());
		}
		if(recurso.getDepartamento() != null) {
			if(direccion.length()>0) {
				direccion.append(" - ");
			}
			direccion.append(recurso.getDepartamento());
		}
		return direccion.toString();
	}

	public String getHorario() {
		Recurso recurso = sessionMBean.getRecurso();
		if(recurso == null) {
			return "";
		}
		if(recurso.getHorarios()!=null) {
			return recurso.getHorarios();
		}
		return "";
	}
	
	public String getDescripcion() {
		Agenda a = sessionMBean.getAgenda();
		if (a != null) {
			TextoAgenda ta = getTextoAgenda(a, sessionMBean.getIdiomaActual());
			if (ta!=null)	{
				String str = ta.getTextoPaso1();
				if (str!=null) {
					return str;
				}	else {
					return "";
				}
			} else {
				return "";
			}
		}else{
			return "";
		}
	}

	public String getEtiquetaSeleccionDelRecurso() {
		Agenda a = sessionMBean.getAgenda();
		if (a != null) {
			TextoAgenda textoAgenda = getTextoAgenda(a, sessionMBean.getIdiomaActual());
			if (textoAgenda!=null) {
				String str = textoAgenda.getTextoSelecRecurso();
				if (str !=null) {
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
	  if(sessionMBean.getRecurso() != null) {
			try {
	      Recurso recurso = sessionMBean.getRecurso();
				VentanaDeTiempo ventanaCalendario = agendarReservasEJB.obtenerVentanaCalendarioInternet(recurso);
				List<Integer> listaCupos = agendarReservasEJB.obtenerCuposPorDia(recurso, ventanaCalendario, sessionMBean.getTimeZone());
				Calendar cont = Calendar.getInstance();
        cont.setTime(Utiles.time2InicioDelDia(ventanaCalendario.getFechaInicial()));
				int i = 0;
				Date inicio_disp = ventanaCalendario.getFechaInicial();
				Date fin_disp = ventanaCalendario.getFechaFinal();
				recursoTieneDisponibilidad = false; 
        while (!cont.getTime().after(ventanaCalendario.getFechaFinal()) && !recursoTieneDisponibilidad) {
					if (cont.getTime().before(inicio_disp) || cont.getTime().after(fin_disp)) {
						listaCupos.set(i, -1);
					}else {
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
			}catch (Exception e) {
				addErrorMessage(e, MSG_ID);
				return null;
			}
		}else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
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
		while (iter.hasNext() && ! encontre) {
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
		urlMapa = urlMapa+"lat="+lat+"&lon="+lon;
	}
	
	public String getUrlMapa() {
		if(urlMapa == null) {
			mostrarMapa(sessionMBean.getRecurso());
		}
		return urlMapa;
	}
	
  public boolean isRecursoTieneDisponibilidad() {
    return recursoTieneDisponibilidad;
  }
	
}
