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

import java.net.URLEncoder;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Llamadas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Llamada;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.TipoMonitor;

public class LlamadorMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/LlamadasBean!uy.gub.imm.sae.business.ejb.facade.LlamadasRemote")
	private Llamadas llamadasEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
	private AgendaGeneral generalEJB;

	private LlamadorSessionMBean llamadorSessionMBean;
	private SessionMBean sessionMBean;

	// Variables para redirigir al llamador
	private static final String URL_BASE_TO_FORWARD_LLAMADOR = "/administracion/llamador/listaDeLlamadas.xhtml?agenda=";
	
	@PostConstruct
	public void init() {
		//Se controla que se haya Marcado una agenda para trabajar con los recursos
	  boolean hayError = false;
    if (sessionMBean.getAgendaMarcada() == null){
      hayError = true;
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"));
    }	  
		if (sessionMBean.getRecursoMarcado() == null) {
      hayError = true;
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"));
		}
		if(!hayError) {
			try {
				if (llamadorSessionMBean.getMostrarDatos() == null) {
					llamadorSessionMBean.setMostrarDatos(recursosEJB.mostrarDatosASolicitarEnLlamador(sessionMBean.getRecursoMarcado()));
				}
			} catch (Exception e) {
				addErrorMessage(new ApplicationException(e));
			}
		}
	}
	
  /** Configura titulo pantalla de configuracion del llamador cuando se accede desde la administracion */
  public void beforePhaseConfiguracionLlamador(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_LLAMADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("configuracion_del_llamador"));
    }
  }
  
	/** Valida parametros para armar el llamador generico */
	public void beforePhaseListaDeLlamadas(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_LLAMADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("lista_de_llamadas"));
			try {
				Map<String, String> parametros = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
				if (parametros.get("agenda") != null && parametros.get("recursos") != null && parametros.get("tipoMonitor") != null) {					
					Integer agendaId = Integer.valueOf(parametros.get("agenda"));
					Agenda agenda = agendarReservasEJB.consultarAgendaPorId(agendaId);
					if (agenda == null) {
						throw new InvalidParameterException("La agenda no existe.");
					}
					List<String> recursosParam = Arrays.asList(parametros.get("recursos").split(","));
					if (recursosParam.isEmpty()) {
						throw new InvalidParameterException("Recursos inválidos.");
					}
					List<Recurso> recursosAgenda = generalEJB.consultarRecursos(agenda);					
					List<Recurso> recursos = new ArrayList<Recurso>();
					for(Recurso r : recursosAgenda) {
						if(recursosParam.contains(r.getId().toString())) {
							recursos.add(r);
						}
					}
					llamadorSessionMBean.setRecursos(recursos);
					TipoMonitor tipoMonitor = TipoMonitor.fromPulgadas(Integer.valueOf(parametros.get("tipoMonitor")));
					llamadorSessionMBean.setTipoMonitor((tipoMonitor != null ? tipoMonitor :  TipoMonitor.fromPulgadas(Integer.valueOf("22"))));
				}
			}
			catch (Exception e) {
				addErrorMessage(new ApplicationException(e), MSG_ID);
			}
		}
	}
	
  public LlamadorSessionMBean getLlamadorSessionMBean() {
    return llamadorSessionMBean;
  }

  public void setLlamadorSessionMBean(LlamadorSessionMBean llamadorSessionMBean) {
    this.llamadorSessionMBean = llamadorSessionMBean;
  }

  public SessionMBean getSessionMBean() {
    return sessionMBean;
  }

  public void setSessionMBean(SessionMBean sessionMBean) {
    this.sessionMBean = sessionMBean;
  }
  
	public String getPulgadasMonitor() {
		return llamadorSessionMBean.getTipoMonitor().getPulgadas().toString();
	}

	public String[] getPulgadasMonitorArray() {
		String[] ret = new String[1];
		ret[0] = llamadorSessionMBean.getTipoMonitor().getPulgadas().toString();
		return ret;
	}
	
	public Integer getCantLlamadas() {
	  if(llamadorSessionMBean.getTipoMonitor()==null) {
	    return 0;
	  }
	  return llamadorSessionMBean.getTipoMonitor().getLineas();
	}

	public String getNombreAgenda() {
		if (sessionMBean.getAgendaMarcada() != null) {
			return sessionMBean.getAgendaMarcada().getNombre().toUpperCase();
		} else {
			return null;
		}
	}
	
	public String getDescripcionRecurso() {
		if (sessionMBean.getRecursoMarcado() != null) {
			return sessionMBean.getRecursoMarcado().getNombre().toUpperCase();
		} else {
			return "";
		}
	}
	
	public String getNombreColumnaPuesto() {
		TextoRecurso textoRecurso = getTextoRecurso(sessionMBean.getRecursoMarcado(), sessionMBean.getIdiomaActual());
		if (textoRecurso != null) {
			return textoRecurso.getTituloPuestoEnLlamador();
		} else {
			return "";
		}
	}

	public String getNombreColumnaDatos() {
		TextoRecurso textoRecurso = getTextoRecurso(sessionMBean.getRecursoMarcado(), sessionMBean.getIdiomaActual());
		if (sessionMBean.getRecursoMarcado() != null) {
			return textoRecurso.getTituloCiudadanoEnLlamador();
		}	else {
			return "";
		}
	}

	public Boolean getMostrarNumero() {
		if (sessionMBean.getRecursoMarcado() != null) {
			return sessionMBean.getRecursoMarcado().getMostrarNumeroEnLlamador();
		} else {
			return false;
		}
	}

	public Boolean getMostrarDatos() {
		return llamadorSessionMBean.getMostrarDatos();
	}

	/*
	 * Determina si hay nueva llamada desde el ultimo refresco
	 */
	public void refrescarLlamadas() {
		try {
			List<Llamada> llamadasNuevas = llamadasEJB.obtenerLlamadas(llamadorSessionMBean.getRecursos(), getCantLlamadas());
			List<Llamada> llamadasAnteriores = llamadorSessionMBean.getLlamadas();
			if(llamadasAnteriores == null) {
				llamadasAnteriores = new ArrayList<Llamada>();
			}
			//Ver si son llamadas nuevas o estaban desde antes
			//A las nuevas hay que destacarlas..
			for(Llamada llamada : llamadasNuevas) {
				if(!llamadasAnteriores.contains(llamada)) {
					llamadorSessionMBean.getLlamadasADestacar().add(llamada);
				}
			}
			llamadorSessionMBean.setLlamadas(llamadasNuevas);
			Llamada llamadaADestacar = llamadorSessionMBean.getLlamadasADestacar().poll();
			if(llamadaADestacar != null) {
			  Reserva reserva = llamadaADestacar.getReserva();
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.execute("setearValor('varPuesto','"+reserva.getLlamada().getPuesto()+"');");
				requestContext.execute("setearValor('varDocumento','"+(reserva.getNumeroDocumento()!=null?reserva.getNumeroDocumento():"")+"');");
				requestContext.execute("setearValor('varSerie','"+(reserva.getSerie()!=null?reserva.getSerie():"")+"');");
        requestContext.execute("setearValor('varNumero','"+(reserva.getNumero()!=null?reserva.getNumero().toString():"")+"');");
				requestContext.execute("PF('llamadaDestacada').show();");
			}
		} catch (UserException uEx) {
			addErrorMessage(uEx, MSG_ID);
		}
	}

	/**
	 * Abre el llamador pasando como parámetro vía url los datos necesarios.
	 * Se pensó así para automatizar la carga del llamador en los locales coemrciales mediante login script.
	 * @return
	 */
	public void abrirLlamador() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
		StringBuffer urlLlamador = new StringBuffer(request.getContextPath() + URL_BASE_TO_FORWARD_LLAMADOR);
		if (sessionMBean.getAgendaMarcada() != null) {
			if (sessionMBean.getRecursoMarcado() != null) {
				try {
					urlLlamador.append(sessionMBean.getAgendaMarcada().getId())
						.append("&recursos=")
						.append(sessionMBean.getRecursoMarcado().getId())
						.append("&tipoMonitor=")
						.append(URLEncoder.encode(llamadorSessionMBean.getTipoMonitor().getPulgadas().toString(), "utf-8"));
						FacesContext.getCurrentInstance().getExternalContext().redirect(urlLlamador.toString());
				} catch (Exception e) {
					addErrorMessage(new ApplicationException(e), MSG_ID);
				}
			} else {
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			}
		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
	}
	
	/** 
	 * Retorna la lista de monitores para configurar el llamador 
	*/
	public List<SelectItem> getTiposDeMonitores() {
		List<SelectItem> listaMonitores = new ArrayList<SelectItem>();
		for (TipoMonitor tipoMonitor : TipoMonitor.values()) {
			listaMonitores.add(new SelectItem(tipoMonitor.getPulgadas(), tipoMonitor.getEtiqueta()));
		}
		return listaMonitores;
	}
	
	public void cambioTipoMonitor(ValueChangeEvent e) {
		llamadorSessionMBean.setTipoMonitor(TipoMonitor.fromPulgadas(Integer.valueOf(e.getNewValue().toString())));
	}

	public List<LlamadasPorHorario> getLlamadasPorHorario() {
		List<Llamada> llamadas = llamadorSessionMBean.getLlamadas();
		if(llamadas == null) {
			return new ArrayList<LlamadasPorHorario>(0);
		}
		Map<String, LlamadasPorHorario> mapLlamadasPorHorario = new HashMap<String, LlamadasPorHorario>();
		
		DateFormat df = new SimpleDateFormat("HHmm");
		for(Llamada llamada : llamadas) {
		  Reserva reserva = llamada.getReserva();
			String horaLLamada = null;
			if(reserva.getPresencial()!=null && reserva.getPresencial().booleanValue()) {
			  horaLLamada = "9999";
			}else {
			  Disponibilidad disp = reserva.getDisponibilidades().get(0);
			  horaLLamada = df.format(disp.getHoraInicio());
			}
			
			LlamadasPorHorario llamadasPorHorario = mapLlamadasPorHorario.get(horaLLamada);
			if(llamadasPorHorario == null) {
			  Disponibilidad disp = reserva.getDisponibilidades().get(0);
				llamadasPorHorario = new LlamadasPorHorario(disp.getHoraInicio(), disp.getPresencial());
				mapLlamadasPorHorario.put(horaLLamada, llamadasPorHorario);
			}
			String puesto = llamada.getPuesto()!=null?llamada.getPuesto().toString():"";
			String documento = "";
			for(DatoReserva dato : reserva.getDatosReserva()) {
				DatoASolicitar datoSol = dato.getDatoASolicitar();
				if(DatoASolicitar.NUMERO_DOCUMENTO.equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
					documento = dato.getValor();
				}
			}
			String serie = reserva.getSerie()!=null?reserva.getSerie():"";
      String numero = reserva.getNumero()!=null?reserva.getNumero().toString():"";
      Boolean presencial = reserva.getDisponibilidades().get(0).getPresencial();
			LlamadaPorHorario llamada1 = new LlamadaPorHorario(puesto, documento, serie, numero, presencial);
			llamadasPorHorario.getLlamadas().add(llamada1);
		}
		
		List<String> horarios = new ArrayList<String>();
		horarios.addAll(mapLlamadasPorHorario.keySet());
		Collections.sort(horarios);
		
		List<LlamadasPorHorario> llamadasPorHorario = new ArrayList<LlamadasPorHorario>(llamadas.size());
		for(String horario : horarios) {
			llamadasPorHorario.add(mapLlamadasPorHorario.get(horario));
		}
		
		return llamadasPorHorario;
	}


	public class LlamadasPorHorario {
		private Date hora;
		private Boolean presencial;
		private List<LlamadaPorHorario> llamadas;
		public LlamadasPorHorario(Date hora, Boolean presencial) {
			this.hora = hora;
      this.presencial = presencial;
			this.llamadas = new ArrayList<LlamadaPorHorario>();
		}
		public Date getHora() {
			return this.hora;
		}
		public List<LlamadaPorHorario> getLlamadas() {
			return this.llamadas;
		}
    public Boolean getPresencial() {
      return presencial;
    }
		
	}
	
	public class LlamadaPorHorario {
		private String puesto;
		private String documento;
		private String serie;
		private String numero;
		private Boolean presencial;
		public LlamadaPorHorario(String puesto, String documento, String serie, String numero, Boolean presencial) {
			super();
			this.puesto = puesto;
			this.documento = documento;
			this.serie = serie;
			this.numero = numero;
      this.presencial = presencial;
		}
		public String getPuesto() {
			return puesto;
		}
		public String getDocumento() {
			return documento;
		}
		public String getNumero() {
			return numero;
		}
    public String getSerie() {
      return serie;
    }
    public Boolean getPresencial() {
      return presencial;
    }
	}
	
	private boolean detenerPolling = false;

	public boolean isDetenerPolling() {
		return detenerPolling;
	}
	
}