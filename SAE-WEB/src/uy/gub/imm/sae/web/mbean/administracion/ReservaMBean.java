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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Comunicaciones;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.business.ejb.facade.Disponibilidades;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.DisponibilidadReserva;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinReservaClient;
import uy.gub.imm.sae.web.common.RowList;

import org.primefaces.component.datagrid.DataGrid;

public class ReservaMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConsultasBean!uy.gub.imm.sae.business.ejb.facade.ConsultasRemote")
	private Consultas consultaEJB;

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/DisponibilidadesBean!uy.gub.imm.sae.business.ejb.facade.DisponibilidadesRemote")
  private Disponibilidades disponibilidadesEJB;
	
  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ComunicacionesBean!uy.gub.imm.sae.business.ejb.facade.ComunicacionesRemote")
  private Comunicaciones comunicacionesEJB;
  
	private ReservaSessionMBean reservaSessionMBean;
	private SessionMBean sessionMBean;

	private Map<String, DatoASolicitar> datosASolicitar;
	private Map<String, Object> datosFiltroReservaMBean;

	private DataGrid reservasDataTable;

	private UIComponent filtroConsulta;
	private UIComponent campos;

	@PostConstruct
	public void initAgendaRecurso() {
		
		boolean hayError = false;
		// Se controla que se haya Marcado una agenda para trabajar con los recursos
		if (sessionMBean.getAgendaMarcada() == null) {
			hayError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		// Se controla que se haya Marcado un recurso
		if (sessionMBean.getRecursoMarcado() == null) {
			hayError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}

		if(hayError) {
			return;
		}
		
		// guardo en session los datos a solicitar del recurso
		List<DatoASolicitar> listaDatoSolicitar = recursosEJB.consultarDatosSolicitar(sessionMBean.getRecursoMarcado());
		Map<String, DatoASolicitar> datoSolicMap = new HashMap<String, DatoASolicitar>();
		for (DatoASolicitar dato : listaDatoSolicitar) {
			datoSolicMap.put(dato.getNombre(), dato);
		}
		setDatosASolicitar(datoSolicMap);

	}

  public void beforePhaseCancelarReserva(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_FCALL_CENTER", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("cancelar_reserva"));
    }
  }
  
  public void beforePhaseCancelarReservasPeriodo(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("cancelar_reserva_por_periodo"));
    }
  }
	
	public String volverPagInicio() {
		return "volver";
	}

	public void buscarReservaDatos(ActionEvent e) {
	  limpiarMensajesError();
		boolean huboError = false;
		if (sessionMBean.getAgendaMarcada() == null && !huboError) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() == null && !huboError) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		if(huboError) {
			return;
		}
		try {
  		List<DatoReserva> datos = FormularioDinReservaClient.obtenerDatosReserva(datosFiltroReservaMBean, datosASolicitar);
  		if (datos.size() <= 1) {
  			huboError = true;
  			addErrorMessage(sessionMBean.getTextos().get("debe_ingresar_al_menos_dos_de_los_datos_solicitados"), FORM_ID);
  		}
  		if (sessionMBean.getCodigoSeguridadReserva()==null || sessionMBean.getCodigoSeguridadReserva().trim().isEmpty()) {
  			huboError = true;
  			addErrorMessage(sessionMBean.getTextos().get("debe_ingresar_codigo_de_seguridad"), FORM_ID+":codSeg");
  		}
  		if (!huboError) {
  		  List<Reserva> reservas = (ArrayList<Reserva>) consultaEJB.consultarReservasParaModificarCancelar(datos, sessionMBean.getRecursoMarcado(), 
  					sessionMBean.getCodigoSeguridadReserva(), sessionMBean.getTimeZone());
  			this.reservaSessionMBean.setListaReservas(reservas);
  			if (reservas.isEmpty()) {
  			  addErrorMessage("No se encontraron reservas con los filtros de búsqueda.", MSG_ID);
  			} else {
  				this.reservaSessionMBean.setListaReservas(reservas);
  			}
  		}
		}catch(Exception ex) {
		  addErrorMessage(ex);
	  }
	}

	public void cancelarReserva(ActionEvent event) {
	  limpiarMensajesError();
		boolean huboError = false;
		if (sessionMBean.getAgendaMarcada() == null) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() == null) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		if (reservaSessionMBean.getReservaDatos() == null || reservaSessionMBean.getReservaDatos().getId() == null) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_una_reserva"), MSG_ID);
		}else if (reservaSessionMBean.getReservaDatos().getEstado() != Estado.R) {
			huboError = true;
			addErrorMessage(sessionMBean.getTextos().get("no_es_posible_cancelar_la_reserva"), MSG_ID);
		}
		if (!huboError) {
			try {
				//Cancelar la reserva
				agendarReservasEJB.cancelarReserva(sessionMBean.getEmpresaActual(), sessionMBean.getRecursoMarcado(), reservaSessionMBean.getReservaDatos());
				//Recargar la reserva cancelada
				Reserva r = consultaEJB.consultarReservaPorNumero(sessionMBean.getRecursoMarcado(), 
						reservaSessionMBean.getDisponibilidad().getHoraInicio(), reservaSessionMBean.getReservaDatos().getNumero());
				reservaSessionMBean.setReservaDatos(r);
				List<DatoReserva> datos = FormularioDinReservaClient.obtenerDatosReserva(datosFiltroReservaMBean,	datosASolicitar);
				try {
  				//Enviar el mail de confirmacion
				  comunicacionesEJB.enviarComunicacionesCancelacion(sessionMBean.getEmpresaActual(), r, sessionMBean.getIdiomaActual(), sessionMBean.getFormatoFecha(), 
  				    sessionMBean.getFormatoHora());
				}catch(UserException ex) {
          addAdvertenciaMessage(sessionMBean.getTextos().get(ex.getCodigoError()));
        }
				//Recargar la lista de reservas
				ArrayList<Reserva> reservas = new ArrayList<Reserva>();
				reservas = (ArrayList<Reserva>) consultaEJB.consultarReservasParaModificarCancelar(datos, sessionMBean.getRecursoMarcado(), 
						sessionMBean.getCodigoSeguridadReserva(), sessionMBean.getTimeZone());
				this.reservaSessionMBean.setListaReservas(reservas);
				addInfoMessage(sessionMBean.getTextos().get("reserva_cancelada"), MSG_ID);
			} catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
				ex.printStackTrace();
			}
		}
	}

	public Boolean getConfirmarDeshabilitado() {
		if (reservaSessionMBean.getReservaDatos() == null	
		    || reservaSessionMBean.getReservaDatos().getEstado() == Estado.C 
		    || reservaSessionMBean.getReservaDatos().getEstado() == Estado.U) {
			return true;
		} else {
			return false;
		}
	}

	public UIComponent getFiltroConsulta() {
		return filtroConsulta;
	}

	public void setFiltroConsulta(UIComponent filtroConsulta) {
		this.filtroConsulta = filtroConsulta;
		if(this.sessionMBean.getRecursoMarcado() != null) {
			try {
				List<AgrupacionDato> agrupaciones0 = recursosEJB.consultarDefCamposTodos(this.sessionMBean.getRecursoMarcado());
				List<AgrupacionDato> agrupaciones = new ArrayList<AgrupacionDato>();
				for (AgrupacionDato agrupacionDato : agrupaciones0) {
					if (!agrupacionDato.getBorrarFlag()) {
						agrupaciones.add(agrupacionDato);
						break;
					}
				}
				FormularioDinReservaClient.armarFormularioEdicionDinamico(this.sessionMBean.getRecursoMarcado(), filtroConsulta, agrupaciones, sessionMBean.getFormatoFecha());
			} catch (BusinessException be) {
				addErrorMessage(be, MSG_ID);
			} catch (Exception e) {
				addErrorMessage(e);
			}
		}
	}

	public DataGrid getReservasDataTable() {
		return reservasDataTable;
	}

	public void setReservasDataTable(DataGrid reservasDataTable) {
		this.reservasDataTable = reservasDataTable;
	}

	public Map<String, DatoASolicitar> getDatosASolicitar() {
		return datosASolicitar;
	}

	public void setDatosASolicitar(Map<String, DatoASolicitar> datosASolicitar) {
		this.datosASolicitar = datosASolicitar;
	}

	public UIComponent getCampos() {
		return campos;
	}

	public void setCampos(UIComponent campos) {
		this.campos = campos;
		try {
			List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(sessionMBean.getRecursoMarcado(), sessionMBean.getTimeZone());
			FormularioDinReservaClient.armarFormularioLecturaDinamico(sessionMBean.getRecursoMarcado(),	this.reservaSessionMBean.getReservaDatos(), this.campos, agrupaciones, 
			    sessionMBean.getFormatoFecha());
		} catch (BusinessException be) {
			addErrorMessage(be, MSG_ID);
		} catch (Exception e) {
			addErrorMessage(e);
		}
	}

	public Map<String, Object> getDatosFiltroReservaMBean() {
		return datosFiltroReservaMBean;
	}

	public void setDatosFiltroReservaMBean(Map<String, Object> datosFiltroReservaMBean) {
		this.datosFiltroReservaMBean = datosFiltroReservaMBean;
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public ReservaSessionMBean getReservaSessionMBean() {
		return reservaSessionMBean;
	}

	public void setReservaSessionMBean(ReservaSessionMBean reservaSessionMBean) {
		this.reservaSessionMBean = reservaSessionMBean;
	}

	public void selecReservaEliminar(ActionEvent e) {
		int iSelectedPos = getReservasDataTable().getRowIndex();
		Reserva r = reservaSessionMBean.getListaReservas().get(iSelectedPos);
		reservaSessionMBean.setReservaDatos(r);
		reservaSessionMBean.setDisponibilidad(r.getDisponibilidades().get(0));
	}
	
	//======================================================================
	// Cancelación de reservas en un período de tiempo
	
  private Date fechaDesde;
  private Date fechaHasta;
  private RowList<DisponibilidadReserva> cuposPorDia;
  private String asuntoMensaje;
  private String cuerpoMensaje;
  
  public Date getFechaDesde() {
    return fechaDesde;
  }

  public void setFechaDesde(Date fechaDesde) {
    this.fechaDesde = fechaDesde;
  }

  public Date getFechaHasta() {
    return fechaHasta;
  }

  public void setFechaHasta(Date fechaHasta) {
    this.fechaHasta = fechaHasta;
  }

  public RowList<DisponibilidadReserva> getCuposPorDia() {
    return cuposPorDia;
  }
  
  public void setCuposPorDia(RowList<DisponibilidadReserva> cuposPorDia) {
    this.cuposPorDia = cuposPorDia;
  }
	
  public String getAsuntoMensaje() {
    return asuntoMensaje;
  }

  public void setAsuntoMensaje(String asuntoMensaje) {
    this.asuntoMensaje = asuntoMensaje;
  }

  public String getCuerpoMensaje() {
    return cuerpoMensaje;
  }

  public void setCuerpoMensaje(String cuerpoMensaje) {
    this.cuerpoMensaje = cuerpoMensaje;
  }

  public void obtenerReservasPeriodo(ActionEvent e){
    limpiarMensajesError();
    boolean hayErrores = false;
    if(fechaDesde == null) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "form:Fdesde");
      hayErrores = true;
    }else if(Utiles.esFechaInvalida(fechaDesde)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_invalida"), "form:Fdesde");
      hayErrores = true;
    }
    if(fechaHasta == null) {
      hayErrores = true;
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), "form:Fhasta");
    }else if(Utiles.esFechaInvalida(fechaHasta)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_invalida"), "form:Fhasta");
      hayErrores = true;
    }
    if(fechaDesde!=null && !Utiles.esFechaInvalida(fechaDesde) && fechaHasta!=null && !Utiles.esFechaInvalida(fechaHasta)) {
      if(fechaDesde.after(fechaHasta)) {
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "form:Fdesde", "form:Fhasta");
      }
    }
    if(hayErrores) {
      setCuposPorDia(null);
      return;
    }
    VentanaDeTiempo ventana = new VentanaDeTiempo();
    ventana.setFechaInicial(Utiles.time2InicioDelDia(fechaDesde));
    ventana.setFechaFinal(Utiles.time2FinDelDia(fechaHasta));
    try{
      List<DisponibilidadReserva> dispsRess = disponibilidadesEJB.obtenerDisponibilidadesReservas(sessionMBean.getRecursoMarcado(), ventana);
      cuposPorDia = new RowList<DisponibilidadReserva>(dispsRess);
    }catch (Exception ex) {
        addErrorMessage(ex, MSG_ID);
    }
  }

  /**
   * Cancela todas las reservas existentes en el recurso actual para el período especificado, enviando un mensaje de 
   * confirmación a cada uno de los ciudadanos. Además, elimina las disponibilidades existentes en el mismo período para
   * que no se pueda generar otra reserva.
   */
  public void cancelarReservasPeriodo() {
    boolean hayErrores = false;
    if (sessionMBean.getAgendaMarcada() == null) {
      hayErrores = true;
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
    }
    if (sessionMBean.getRecursoMarcado() == null) {
      hayErrores = true;
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
    }
    if(fechaDesde == null) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "form:Fdesde");
      hayErrores = true;
    }else if(Utiles.esFechaInvalida(fechaDesde)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_invalida"), "form:Fdesde");
      hayErrores = true;
    }
    if(fechaHasta == null) {
      hayErrores = true;
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), "form:Fhasta");
    }else if(Utiles.esFechaInvalida(fechaHasta)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_invalida"), "form:Fhasta");
      hayErrores = true;
    }
    if(fechaDesde!=null && !Utiles.esFechaInvalida(fechaDesde) && fechaHasta!=null && !Utiles.esFechaInvalida(fechaHasta)) {
      if(fechaDesde.after(fechaHasta)) {
        hayErrores = true;
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "form:Fdesde", "form:Fhasta");
      }
    }
    if (asuntoMensaje == null || asuntoMensaje.isEmpty()) {
      hayErrores = true;
      addErrorMessage(sessionMBean.getTextos().get("el_asunto_del_mensaje_es_obligatorio"), "form:txtAsunto");
    }
    if (cuerpoMensaje == null || cuerpoMensaje.isEmpty()) {
      hayErrores = true;
      addErrorMessage(sessionMBean.getTextos().get("el_cuerpo_del_mensaje_es_obligatorio"), "form:txtCuerpo");
    }
    if (!hayErrores) {
      try {
        VentanaDeTiempo ventana = new VentanaDeTiempo();
        ventana.setFechaInicial(Utiles.time2InicioDelDia(fechaDesde));
        ventana.setFechaFinal(Utiles.time2FinDelDia(fechaHasta));
        //Cancelar las reservas
        List<Integer> reservasSinEnviarComunicacion = agendarReservasEJB.cancelarReservasPeriodo(sessionMBean.getEmpresaActual(), sessionMBean.getRecursoMarcado(),  
            ventana, sessionMBean.getIdiomaActual(), sessionMBean.getFormatoFecha(), sessionMBean.getFormatoHora(), asuntoMensaje, cuerpoMensaje);
        //Si para alguna reserva no se pudo enviar la comunicación mostrar su identificador en pantalla
        if(!reservasSinEnviarComunicacion.isEmpty()) {
          addAdvertenciaMessage(sessionMBean.getTextos().get("no_se_pudo_enviar_comunicacion_para_las_reservas")+": "+reservasSinEnviarComunicacion.toString(), MSG_ID);
        }
        addInfoMessage(sessionMBean.getTextos().get("reservas_canceladas"), MSG_ID);
        //Eliminar las disponibilidades
        disponibilidadesEJB.eliminarDisponibilidades(sessionMBean.getRecursoMarcado(), ventana);
      } catch (Exception e) {
        addErrorMessage(e, MSG_ID);
      }
    }
  }

}
