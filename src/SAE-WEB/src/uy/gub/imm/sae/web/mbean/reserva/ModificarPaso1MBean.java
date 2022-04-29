package uy.gub.imm.sae.web.mbean.reserva;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.primefaces.component.datagrid.DataGrid;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.login.Utilidades;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.FormularioDinReservaClient;

public class ModificarPaso1MBean extends BaseMBean {

	static Logger logger = Logger.getLogger(ModificarPaso1MBean.class);

	private AgendarReservas agendarReservasEJB;
	private Consultas consultaEJB;
	private Recursos recursosEJB;
	private SesionMBean sesionMBean; 
	private UIComponent filtroConsulta;
	private UIComponent campos;
	private Map<String, DatoASolicitar> datosASolicitar;
	private Map<String, Object> datosFiltroReservaMBean;
	private boolean errorInit;
	private DataGrid reservasDataTable;
	
	//VALIDAR_DATOS, BUSCAR_RESERVAS, LISTAR_RESERVAS
	private String mostrar = "";

	private List<SelectItem> tiposDocumento  = new ArrayList<SelectItem>();
	private String tipoDocumento;
	private String numeroDocumento;
	private String codigoSeguridad;
	
	
	@PostConstruct
	public void init() {
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			//Estos dos son obligatorios
			String sEmpresaId = request.getParameter("e");
			String sAgendaId = request.getParameter("a");
			//De estos dos, uno debe estar necesariamente
			String sRecursoId = request.getParameter("r");
			String sReservaId = request.getParameter("ri");
			
			Integer empresaId = null;
			Integer agendaId = null;
			Integer recursoId = null;
			Integer reservaId = null;
			
			if(sEmpresaId!=null && sAgendaId!=null && (sRecursoId!=null || sReservaId!=null)) {
				try{
					empresaId = Integer.valueOf(sEmpresaId);
					agendaId = Integer.valueOf(sAgendaId);
					if (sReservaId != null) {
						//Se especificó una reserva por id
						reservaId = Integer.valueOf(sReservaId);
						recursoId = null;
					}else {
						//No se especificó una reserva pero sí un recurso
						recursoId = Integer.valueOf(sRecursoId);
					}
					
					limpiarSession();
					
					sesionMBean.setEmpresaId(empresaId);
					sesionMBean.setAgendaId(agendaId);
					sesionMBean.setRecursoId(recursoId);
					sesionMBean.setReservaId(reservaId);
					
				}catch(Exception ex) {
					addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
					limpiarSession();
					errorInit = true;
					return;
				}
			}else {
				//No hay parámetros, pueden estar ya en la sesion
				empresaId = sesionMBean.getEmpresaId();
				agendaId = sesionMBean.getAgendaId();
				reservaId = sesionMBean.getReservaId();
				recursoId = sesionMBean.getRecursoId();
				
				if(empresaId==null || agendaId==null || (reservaId==null && recursoId==null)) {
					//Tampoco están en sesión
					addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
					limpiarSession();
					errorInit = true;
					return;
				}
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
						falsoUsuario = remoteUser;
						sesionMBean.setUsuarioCda(remoteUser);
					}else  {
						//Es un usuario de otra empresa
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
				Random random = new Random();
				if(falsoUsuario.startsWith("sae")) {
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
			
			agendarReservasEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getAgendarReservas();
			recursosEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getRecursos();
			consultaEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getConsultas();
			
			// Guardar la empresa en la sesion
			try {
				Empresa empresa = agendarReservasEJB.obtenerEmpresaPorId(empresaId);
				sesionMBean.setEmpresaActual(empresa);
				if (empresa==null) {
					addErrorMessage(sesionMBean.getTextos().get("la_empresa_especificada_no_es_valida"));
					limpiarSession();
					errorInit = true;
					return;
				}
			} catch (Exception e) {
				addErrorMessage(sesionMBean.getTextos().get("la_empresa_especificada_no_es_valida"));
				limpiarSession();
				errorInit = true;
				return;
			}
			
			//Guardar la agenda
			try {
				sesionMBean.seleccionarAgenda(agendaId);
			} catch (Exception  ae) {
				addErrorMessage(sesionMBean.getTextos().get("la_agenda_especificada_no_es_valida"));
				limpiarSession();
				errorInit = true;
				return;
			}

			//Si hay recurso obtenerlo
			if(reservaId != null) {
				//Primero obtener el recurso de la reserva
				Recurso recurso = null;
				try {
					recurso = agendarReservasEJB.consultarRecursoPorReservaId(reservaId);
				}catch(Exception ex) {
					recurso = null;
					ex.printStackTrace();
				}
				if (recurso==null || !sesionMBean.getAgenda().getId().equals(recurso.getAgenda().getId())) {
					addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva_especificada"));
					limpiarSession();
					errorInit = true;
					return;
				}
				sesionMBean.setRecurso(recurso);
				recursoId = recurso.getId();
				
		    if(recurso.getCambiosAdmite()==null || !recurso.getCambiosAdmite().booleanValue()) {
          addErrorMessage(sesionMBean.getTextos().get("el_recurso_no_admite_cambios_de_reservas"));
          limpiarSession();
          errorInit = true;
          return;
		    }
				
				
				//Obtener la reserva
				Reserva reserva = consultaEJB.consultarReservaId(reservaId, recurso.getId());
				
				if(reserva == null) {
          addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva_especificada"));
          limpiarSession();
          errorInit = true;
          return;
				}else {
	        if(!reserva.getEstado().toString().equals("R")) {
	          //Determinar si la reserva está en estado Reservada
	          addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva_especificada"));
	          limpiarSession();
	          errorInit = true;
	          return;
	        }else {
  				  //Determinar si la reserva está vigente
  			    Calendar calAhora = new GregorianCalendar();
  			    calAhora.add(Calendar.MILLISECOND, sesionMBean.getTimeZone().getOffset(calAhora.getTimeInMillis()));
  			    Calendar calReserva = new GregorianCalendar();
  			    calReserva.setTime(reserva.getDisponibilidades().get(0).getHoraInicio());
  			    if(calReserva.before(calAhora)) {
              addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva_especificada"));
              limpiarSession();
              errorInit = true;
              return;
  			    }
  			    
  			    if(recurso.getCambiosTiempo()!=null && recurso.getCambiosUnidad()!=null) {
  			      calReserva.add(recurso.getCambiosUnidad(), recurso.getCambiosTiempo() * -1);
  			    }
  			    if(calReserva.before(calAhora)) {
              addErrorMessage(sesionMBean.getTextos().get("la_reserva_especificada_ya_no_admite_cambios"));
              limpiarSession();
              errorInit = true;
              return;
  			    }
	        }
				}
				
				this.sesionMBean.setReservaModificar1(reserva);
				
				mostrar = "VALIDAR_DATOS";
			} else if(recursoId != null) {
				List<Recurso> recursos = agendarReservasEJB.consultarRecursos(sesionMBean.getAgenda());
				for (Recurso recurso0 : recursos) {
					if (recurso0.getId().equals(recursoId)) {
						sesionMBean.setRecurso(recurso0);
						break;
					}
				}
				if(sesionMBean.getRecurso() == null) {
					addErrorMessage(sesionMBean.getTextos().get("el_recurso_especificado_no_es_valido"));
					limpiarSession();
					errorInit = true;
					return;
				}
				this.sesionMBean.setRenderedVolverBotom(true);
				
				mostrar = "BUSCAR_RESERVAS";
			}

//			String url = null;
//			if(empresaId!=null && agendaId!=null && recursoId!=null) {
//				url = "/sae/modificarReserva/Paso1.xhtml?e=" + empresaId + "&a=" + agendaId + "&r=" + recursoId+"&faces-redirect=true";
//			}
//			sesionMBean.setUrlModificarReserva(url);
			
			try {
				// guardo en session los datos a solicitar del recurso
				List<DatoASolicitar> listaDatoSolicitar = recursosEJB.consultarDatosSolicitar(sesionMBean.getRecurso());
				Map<String, DatoASolicitar> datoSolicMap = new HashMap<String, DatoASolicitar>();
				for (DatoASolicitar dato : listaDatoSolicitar) {
					datoSolicMap.put(dato.getNombre(), dato);
				}
				setDatosASolicitar(datoSolicMap);
			} catch (Exception ex) {
				addErrorMessage(sesionMBean.getTextos().get("el_recurso_especificado_no_es_valido"));
				limpiarSession();
				ex.printStackTrace();
				return;
			}
		} catch (Exception e) {
			logger.error(e);
			redirect(ERROR_PAGE_OUTCOME);
		}
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
    Recurso recurso = sesionMBean.getRecurso();
    if (recurso != null) {
      String descripcion = recurso.getNombre();
      if (descripcion != null
          && !descripcion.equals(recurso.getDireccion())) {
        descripcion = descripcion + " - " + recurso.getDireccion();
      }
      return descripcion;
    } else {
      return null;
    }
  }
	
	public UIComponent getFiltroConsulta() {
		return filtroConsulta;
	}

	public void setFiltroConsulta(UIComponent filtroConsulta) {
		if(errorInit || (this.sesionMBean.getReserva()==null && this.sesionMBean.getRecurso()==null)) {
			return;
		}
    this.filtroConsulta = filtroConsulta;
		try {
			if (this.sesionMBean.getReservaId()!=null && this.sesionMBean.getEmpresaId()!= null) {
				if(this.sesionMBean.getReservaDatos() == null) {
					Reserva reserva = consultaEJB.consultarReservaId(this.sesionMBean.getReservaId(), sesionMBean.getRecurso().getId());
					sesionMBean.setReservaDatos(reserva);
				}
				tiposDocumento = new ArrayList<SelectItem>();
				List<DatoASolicitar> datos = recursosEJB.consultarDatosSolicitar(sesionMBean.getRecurso());
				if(datos!=null) {
					for(DatoASolicitar dato : datos) {
						if(!dato.getAgrupacionDato().getBorrarFlag() && dato.getNombre().equals(DatoASolicitar.TIPO_DOCUMENTO)) {
							for(ValorPosible valor : dato.getValoresPosibles()) {
								tiposDocumento.add(new SelectItem(valor.getValor(), valor.getEtiqueta()));
							}
						}
					}
				}
				tipoDocumento = (String) tiposDocumento.get(0).getValue();
				numeroDocumento = "";
			}else {
				AgrupacionDato agrupacion = null;
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefCamposTodos(this.sesionMBean.getRecurso());
				for (AgrupacionDato agrupacionDato : agrupaciones) {
					if (!agrupacionDato.getBorrarFlag()) {
						agrupacion = agrupacionDato;
						break;
					}
				}
				agrupaciones.clear();
				agrupaciones.add(agrupacion);
				FormularioDinReservaClient.armarFormularioEdicionDinamico(this.sesionMBean.getRecurso(), filtroConsulta, agrupaciones, sesionMBean.getFormatoFecha());
			}
		} catch (Exception e) {
			addErrorMessage(e);
		}
	}

	public void buscarReservaDatos(ActionEvent e) {

		limpiarMensajesError();
		
		boolean huboError = false;

		if (sesionMBean.getAgenda() == null) {
			huboError = true;
			addErrorMessage(sesionMBean.getTextos().get("debe_especificar_la_agenda."));
		}
		if (sesionMBean.getRecurso() == null) {
			huboError = true;
			addErrorMessage(sesionMBean.getTextos().get("debe_especificar_el_recurso"));
		}

		if(huboError) {
			return;
		}

		try {
  		List<DatoReserva> datos;
  		if (this.sesionMBean.getReservaId() != null && this.sesionMBean.getEmpresaId() != null) {
  			datos = new ArrayList<DatoReserva>();
  			datos.addAll(this.sesionMBean.getReservaDatos().getDatosReserva());
  		} else {
  			datos = FormularioDinReservaClient.obtenerDatosReserva(datosFiltroReservaMBean, datosASolicitar);
  		}
  		if (datos.size() <= 1) {
  			huboError = true;
  			addErrorMessage(sesionMBean.getTextos().get("debe_ingresar_al_menos_dos_de_los_datos_solicitados"));
  		}
  		if (sesionMBean.getCodigoSeguridadReserva().trim().isEmpty()) {
  			huboError = true;
  			addErrorMessage(sesionMBean.getTextos().get("debe_ingresar_codigo_de_seguridad"));
  		}
  		if(huboError) {
  			return;
  		}
  		List<Reserva> reservas = (ArrayList<Reserva>) consultaEJB.consultarReservasParaModificarCancelar(datos, sesionMBean.getRecurso(), sesionMBean.getCodigoSeguridadReserva(), sesionMBean.getTimeZone());
  		if (reservas.isEmpty()) {
  			this.sesionMBean.setListaReservas(new ArrayList<Reserva>());
  			addErrorMessage(sesionMBean.getTextos().get("los_datos_ingresados_no_son_correctos"));
  		} else {
  			this.sesionMBean.setListaReservas(reservas);
  			mostrar = "LISTAR_RESERVAS";
  		}
		}catch(Exception ex) {
		  addErrorMessage(ex);
		}

	}

	public Map<String, DatoASolicitar> getDatosASolicitar() {
		return datosASolicitar;
	}

	public void setDatosASolicitar(Map<String, DatoASolicitar> datosASolicitar) {
		this.datosASolicitar = datosASolicitar;
	}

	public String selecReservaEliminar(){
		int iSelectedPos = getReservasDataTable().getRowIndex();
		Reserva reserva = this.sesionMBean.getListaReservas().get(iSelectedPos);
		this.sesionMBean.setReservaModificar1(reserva);
		return "siguientePaso";
	}
	
	public Map<String, Object> getDatosFiltroReservaMBean() {
		return datosFiltroReservaMBean;
	}

	public void setDatosFiltroReservaMBean(
			Map<String, Object> datosFiltroReservaMBean) {
		this.datosFiltroReservaMBean = datosFiltroReservaMBean;
	}

	public UIComponent getCampos() {
		return campos;
	}

	public void setCampos(UIComponent campos) {
		this.campos = campos;
		try {
			List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(sesionMBean.getRecurso(), sesionMBean.getTimeZone());
			FormularioDinReservaClient.armarFormularioLecturaDinamico(sesionMBean.getRecurso(), this.sesionMBean.getReservaDatos(), this.campos, agrupaciones, sesionMBean.getFormatoFecha());
		} catch (BusinessException be) {
			addErrorMessage(be);
		} catch (Exception e) {
			addErrorMessage(e);
		}
	}

	public void beforePhase(PhaseEvent phaseEvent) {
		disableBrowserCache(phaseEvent);
	}

	public Boolean getConfirmarDeshabilitado() {

		if (sesionMBean.getReservaDatos() == null
				|| sesionMBean.getReservaDatos().getEstado() == Estado.C
				|| sesionMBean.getReservaDatos().getEstado() == Estado.U) {

			return true;
		} else {
			return false;
		}
	}

	public String getUrlBuscarReservas() {
		return sesionMBean.getUrlCancelarReserva() ;
	}

	public DataGrid getReservasDataTable() {
		return reservasDataTable;
	}

	public void setReservasDataTable(DataGrid reservasDataTable) {
		this.reservasDataTable = reservasDataTable;
	}
	
	private void limpiarSession() {
		sesionMBean.setAgenda(null);
		sesionMBean.setListaReservas(null);
		sesionMBean.setReservaDatos(null);
		sesionMBean.setDisponibilidadCancelarReserva(null);
		sesionMBean.setUrlCancelarReserva(null);
		sesionMBean.setEmpresaId(null);
		sesionMBean.setAgendaId(null);
		sesionMBean.setRecursoId(null);
		sesionMBean.setReservaId(null);
		sesionMBean.setCodigoSeguridadReserva(null);
		sesionMBean.setEmpresaActual(null);
	}

	public boolean isErrorInit() {
		return errorInit;
	}

	public String getMostrar() {
		return mostrar;
	}

	public String getCodigoSeguridad() {
		return codigoSeguridad;
	}

	public void setCodigoSeguridad(String codigoSeguridad) {
		this.codigoSeguridad = codigoSeguridad;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<SelectItem> getTiposDocumento() {
		return tiposDocumento;
	}

	public void setTiposDocumento(List<SelectItem> tiposDocumento) {
		this.tiposDocumento = tiposDocumento;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	public String verificarDatos() {
		limpiarMensajesError();
		if(codigoSeguridad==null || codigoSeguridad.trim().isEmpty()) {
			addErrorMessage(sesionMBean.getTextos().get("el_codigo_de_seguridad_es_obligatorio"));
      return null;
		}
		Reserva reserva = this.sesionMBean.getReservaDatos();
		if(reserva==null || !reserva.getCodigoSeguridad().equals(codigoSeguridad)) {
			addErrorMessage(sesionMBean.getTextos().get("los_datos_ingresados_no_son_correctos"));
			return null;
		}
		List<Reserva> reservas = new ArrayList<Reserva>();
		reservas.add(reserva);
		this.sesionMBean.setListaReservas(reservas);
		return "siguientePaso";
	}
	
  @PreDestroy
  public void preDestroy() {
    try {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      this.agendarReservasEJB = null;
      this.campos = null;
      this.consultaEJB = null;
      if(this.datosASolicitar!=null) {
        this.datosASolicitar.clear();
      }
      this.datosASolicitar = null;
      if(this.datosFiltroReservaMBean!=null) {
        this.datosFiltroReservaMBean.clear();
      }
      this.datosFiltroReservaMBean = null;
      this.filtroConsulta = null;
      this.recursosEJB = null;
      this.reservasDataTable = null;
      this.sesionMBean = null;
      if(this.tiposDocumento!=null) {
        this.tiposDocumento.clear();
      }
      this.tiposDocumento = null;
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
    }
  }
	
  public Date getFechaOriginal() {
    if(sesionMBean.getReservaModificar1()==null) {
      return null;
    }
    return sesionMBean.getReservaModificar1().getDisponibilidades().get(0).getFecha();
  }
  
  public Date getHoraOriginal() {
    if(sesionMBean.getReservaModificar1()==null) {
      return null;
    }
    return sesionMBean.getReservaModificar1().getDisponibilidades().get(0).getHoraInicio();
  }
  
}
