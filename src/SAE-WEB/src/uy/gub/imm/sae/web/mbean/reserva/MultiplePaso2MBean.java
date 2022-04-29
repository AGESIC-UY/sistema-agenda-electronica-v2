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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.factories.BusinessLocator;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;

/*
 * Invocación desde un sistema externo:
 * https://192.168.1.13:8443/sae/agendarReserva/Paso1.xhtml?e=1000001&a=29&u=http%3A%2F%2Fgoogle.com.uy&p=13.61.789456;13.60.CI;13.62.@@email
 * 
 */
public class MultiplePaso2MBean extends BaseMBean {

	private static Logger logger = Logger.getLogger(MultiplePaso2MBean.class);

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

  private DateFormat formatoFechaHora = null;
	
	public void beforePhase(PhaseEvent phaseEvent) {
		disableBrowserCache(phaseEvent);
		if (phaseEvent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sesionMBean.limpiarPaso2();
		}
	}

	@PostConstruct
	public void init() {
		try {

      //Obtener el token
      TokenReserva token = sesionMBean.getTokenReserva();
      if(token == null) {
        //Falta el token
        addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
        errorInit = true;
      }
      
      formatoFechaHora = new SimpleDateFormat(sesionMBean.getFormatoFecha()+" "+sesionMBean.getFormatoHora());
		  
			BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
			agendarReservasEJB = bl.getAgendarReservas();
			recursosEJB = bl.getRecursos();
			
      // Cargar los recursos
			recursosItems = new ArrayList<SelectItem>();
			if(sesionMBean.getAgenda() != null) {
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
					if (recurso.getVisibleInternet() && recurso.getMultipleAdmite()) {
						recursosItems.add(new SelectItem(recurso.getId(), recurso.getNombre()));
					}
				}
				// Selecciono el recurso por defecto.
				if (!recursos.isEmpty()) {
		      Recurso recursoDefecto = null;
		      if(token != null && token.getRecurso()!=null) {
		        recursoDefecto = token.getRecurso();
		      }else {
		        recursoDefecto = sesionMBean.getRecurso();
		      }
		      
					if (recursoDefecto == null || !recursos.contains(recursoDefecto)) {
						if (sesionMBean.getRecurso() == null) {
							sesionMBean.setRecurso(recursos.get(0));
						}
					}else {
						// Se ingreso un recurso en la url y se encontro para la agenda.
						sesionMBean.setRecurso(recursoDefecto);
					}
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
	  if(sRecursoId==null || sRecursoId.trim().isEmpty()) {
	    return;
	  }
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
				  TokenReserva token = sesionMBean.getTokenReserva();
				  token.setRecurso(recurso);
				  token = agendarReservasEJB.guardarTokenReserva(token);
				  sesionMBean.setTokenReserva(token);
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

  public String paso1() {
    return sesionMBean.getUrlPaso1Reserva() + "&faces-redirect=true";
  }
  
  @PreDestroy
  public void preDestroy() {
    try {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
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
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
    }
  }
	
  public List<Reserva> getReservas() {
    TokenReserva token = sesionMBean.getTokenReserva();
    return agendarReservasEJB.obtenerReservasMultiples(token.getId(), false);
  }
  
  public String describirReserva(Reserva reserva) {
    String descripcion = reserva.getDocumento();
    if(reserva.getDisponibilidades()!=null && !reserva.getDisponibilidades().isEmpty()) {
      descripcion = descripcion + " | " + formatoFechaHora.format(reserva.getDisponibilidades().get(0).getHoraInicio()) ;
    }
    if(reserva.getNumero() != null) {
      descripcion = descripcion + " | Nro. " + reserva.getNumero();
    }
    return descripcion;
  }
  
  public String confirmarReservas() {
    return "confirmarReservas";
  }
  
  public String cancelarReservas() {
    try {
      TokenReserva token = sesionMBean.getTokenReserva();
      if(token != null) {
        token = agendarReservasEJB.cancelarReservasMultiples(token.getId());
        sesionMBean.setTokenReserva(token);
      }
      return "reservasCanceladas";    
    }catch(UserException uEx) {
      logger.error("Error al cancelar las reservas múltiples", uEx);
      addErrorMessage(sesionMBean.getTextos().get("ha_ocurrido_un_error_no_solucionable"), "form");
    }
    return null;
  }

  public TokenReserva getTokenReserva() {
    return sesionMBean.getTokenReserva();
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