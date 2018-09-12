package uy.gub.imm.sae.web.mbean.reserva;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.event.PhaseEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONArray;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class ModificarPaso2MBean extends BaseMBean {

	static Logger LOGGER = Logger.getLogger(ModificarPaso2MBean.class);

	private AgendarReservas agendarReservasEJB;
	private SesionMBean sesionMBean; 
	
  private Row<Disponibilidad> rowSelectMatutina;
  private Row<Disponibilidad> rowSelectVespertina;
  private RowList<Disponibilidad> disponibilidadesMatutina;
  private RowList<Disponibilidad> disponibilidadesVespertina;
  private JSONArray jsonArrayFchDisp;
  private String diaSeleccionadoStr;
  private String fechaFormatSelect;
  private List<SelectItem> selectItemsDispMatutina;
  private List<SelectItem> selectItemsDispVespertina;
  private String selectIdMatutina;
  private String selectIdVespertina;

  private String filtroHorarios = "MV";
	
	private boolean errorInit;
	
	@PostConstruct
	public void init() {
		try {
	    errorInit = false;
	    try {
	      agendarReservasEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getAgendarReservas();
	      if(sesionMBean.getAgenda()==null || sesionMBean.getRecurso()==null || sesionMBean.getReservaModificar1()==null) {
	        addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
	        errorInit = true;
	        return;
	      }
	    } catch (ApplicationException ex) {
	      addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
	      errorInit = true;
	    }
		  
      configurarCalendario();
      configurarDisponibilidadesDelDia();
	    
	    
		  
/*		  
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			
      LOGGER.debug("Parámetros GET: ");
      LOGGER.debug("              : e=["+request.getParameter("e")+"]");
      LOGGER.debug("              : a=["+request.getParameter("a")+"]");
      LOGGER.debug("              : r=["+request.getParameter("r")+"]");
      LOGGER.debug("              : i=["+request.getParameter("i")+"]");
      LOGGER.debug("              : u=["+request.getParameter("u")+"]");
      LOGGER.debug("              : t=["+request.getParameter("t")+"]");

      String sEmpresaId = request.getParameter("e"); //Id de la empresa
      String sAgendaId = request.getParameter("a"); //Id de la agenda
      String sRecursoId = request.getParameter("r"); //Id del recurso
      String sReservaId = request.getParameter("ri"); //Id de la reserva
      
      String sIdioma = request.getParameter("i"); //Idioma (es,en,...)
      String sUrl = request.getParameter("u"); //URL de retorno al confirmar (URL encoded)
      String sTraza = request.getParameter("t"); //Código de trazabilidad y paso padre (<trazguid>-<paso>)

			if(sEmpresaId!=null && sAgendaId!=null && sRecursoId!=null && sReservaId!=null) {
				try{
					empresaId = Integer.valueOf(sEmpresaId);
					agendaId = Integer.valueOf(sAgendaId);
					reservaId = Integer.valueOf(sReservaId);
					recursoId = Integer.valueOf(sRecursoId);
					
					limpiarSession();
					
					sesionMBean.setEmpresaId(empresaId);
					sesionMBean.setAgendaId(agendaId);
					sesionMBean.setRecursoId(recursoId);
					sesionMBean.setReservaId(reservaId);
	        sesionMBean.setUrlTramite(sUrl);
		      if(sIdioma!=null) {
		        sesionMBean.setIdiomaActual(sIdioma);
		      }else {
		        if(request.getLocale()!=null) {
		          sesionMBean.setIdiomaActual(request.getLocale().getLanguage());
		        }else {
		          sesionMBean.setIdiomaActual(Locale.getDefault().getLanguage());
		        }
		      }
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
				
				if(empresaId==null || agendaId==null || reservaId==null || recursoId==null) {
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
			Recurso recurso = null;
			try {
				recurso = agendarReservasEJB.consultarRecursoPorReservaId(reservaId);
			}catch(Exception ex) {
				recurso = null;
				ex.printStackTrace();
			}
			if (recurso==null || !sesionMBean.getAgenda().getId().equals(recurso.getAgenda().getId())) {
				addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva"));
				limpiarSession();
				errorInit = true;
				return;
			}
			sesionMBean.setRecurso(recurso);

			//Obtener la reserva
			Reserva reserva = consultaEJB.consultarReservaId(reservaId, recurso.getId());
			if(reserva == null) {
        addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva"));
        limpiarSession();
        errorInit = true;
        return;
			}else {
        if(!reserva.getEstado().toString().equals("R")) {
          //Determinar si la reserva está en estado Reservada
          addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva"));
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
            addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_la_reserva"));
            limpiarSession();
            errorInit = true;
            return;
			    }
        }
				
				this.sesionMBean.setReservaModificar1(reserva);
				this.sesionMBean.setCodigoSeguridadReserva("");
				this.sesionMBean.setRenderedVolverBotom(false);
			}
			
      configurarCalendario();
      configurarDisponibilidadesDelDia();
*/			

		} catch (Exception e) {
		  LOGGER.error(e);
			redirect(ERROR_PAGE_OUTCOME);
		}
	}

  public void beforePhase(PhaseEvent phaseEvent) {
    disableBrowserCache(phaseEvent);
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

  private void configurarCalendario() throws RolException, UserException {
    Recurso recurso = sesionMBean.getRecurso();
    VentanaDeTiempo ventanaCalendario = agendarReservasEJB.obtenerVentanaCalendarioInternet(recurso);
    sesionMBean.setVentanaCalendario(ventanaCalendario);
    VentanaDeTiempo ventanaMesSeleccionado = new VentanaDeTiempo();
    Calendar cal = Calendar.getInstance();
    cal.setTime(ventanaCalendario.getFechaInicial());
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    ventanaMesSeleccionado.setFechaInicial(Utiles.time2InicioDelDia(cal.getTime()));
    cal.setTime(ventanaCalendario.getFechaFinal());
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    ventanaMesSeleccionado.setFechaFinal(Utiles.time2FinDelDia(cal.getTime()));
    sesionMBean.setVentanaMesSeleccionado(ventanaMesSeleccionado);
    cargarCuposADesplegar(recurso, ventanaMesSeleccionado);
    sesionMBean.setCurrentDate(ventanaCalendario.getFechaInicial());
    sesionMBean.setDiaSeleccionado(null);
  }

  private void cargarCuposADesplegar(Recurso recurso, VentanaDeTiempo ventana) {
    List<Integer> listaCupos = null;
    try {
      listaCupos = agendarReservasEJB.obtenerCuposPorDia(recurso, ventana, sesionMBean.getTimeZone());
      // Se carga la fecha inicial
      Calendar cont = Calendar.getInstance();
      cont.setTime(Utiles.time2InicioDelDia(sesionMBean.getVentanaMesSeleccionado().getFechaInicial()));

      Integer i = 0;

      Date inicioDisp = sesionMBean.getVentanaCalendario().getFechaInicial();
      Date finDisp = sesionMBean.getVentanaCalendario().getFechaFinal();

      jsonArrayFchDisp = new JSONArray();
      // Recorro la ventana dia a dia y voy generando la lista completa de
      // cupos x dia con -1, 0, >0 según corresponda.
      while (!cont.getTime().after(sesionMBean.getVentanaMesSeleccionado().getFechaFinal())) {
        if (cont.getTime().before(inicioDisp) || cont.getTime().after(finDisp)) {
          listaCupos.set(i, -1);
        } else {
          if (listaCupos.get(i) > 0) {
            String dateStr = String.valueOf(cont.get(Calendar.DAY_OF_MONTH))+"/"+ String.valueOf(cont.get(Calendar.MONTH) + 1)+ "/" + String.valueOf(cont.get(Calendar.YEAR));
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
	
  private void configurarDisponibilidadesDelDia() {
    List<Disponibilidad> dispMatutinas = new ArrayList<Disponibilidad>();
    List<Disponibilidad> dispVespertinas = new ArrayList<Disponibilidad>();
    if (sesionMBean.getDiaSeleccionado() != null) {
      VentanaDeTiempo ventana = new VentanaDeTiempo();
      ventana.setFechaInicial(Utiles.time2InicioDelDia(sesionMBean.getDiaSeleccionado()));
      ventana.setFechaFinal(Utiles.time2FinDelDia(sesionMBean.getDiaSeleccionado()));
      try {
        List<Disponibilidad> lista = agendarReservasEJB.obtenerDisponibilidades(sesionMBean.getRecurso(), ventana, sesionMBean.getTimeZone());
        for (Disponibilidad d : lista) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(d.getHoraInicio());
          if(d.getCupo()<0) {
            d.setCupo(0);
          }
          if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            dispMatutinas.add(d);
          } else {
            dispVespertinas.add(d);
          }
        }
      } catch (Exception e) {
        addErrorMessage(e);
      }
    }

    sesionMBean.setDisponibilidadesDelDiaMatutina(new RowList<Disponibilidad>(dispMatutinas));
    sesionMBean.setDisponibilidadesDelDiaVespertina(new RowList<Disponibilidad>(dispVespertinas));
  }

  public String getDescripcion() {
    TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
    if (textoAgenda != null) {
      String str = textoAgenda.getTextoPaso2();
      if (str != null)
        return str;
      else
        return "";
    } else {
      return null;
    }
  }

  public String getDescripcionRecurso() {
    TextoRecurso textoRecurso = getTextoRecurso(sesionMBean.getRecurso(), sesionMBean.getIdiomaActual());
    if (textoRecurso != null) {
      return textoRecurso.getTextoPaso2();
    } else {
      return null;
    }
  }

  public String getEtiquetaDelRecurso() {
    TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
    if (textoAgenda != null) {
      return textoAgenda.getTextoSelecRecurso();
    } else {
      return null;
    }
  }

  public Date getDiaSeleccionado() {
    return sesionMBean.getDiaSeleccionado();
  }

  public Boolean getHayDisponibilidadesMatutina() {
    if(sesionMBean.getDisponibilidadesDelDiaMatutina()==null) {
      return false;
    }
    return !sesionMBean.getDisponibilidadesDelDiaMatutina().isEmpty();
  }

  public RowList<Disponibilidad> getDisponibilidadesMatutina() {
    this.disponibilidadesMatutina = sesionMBean.getDisponibilidadesDelDiaMatutina();
    if(this.disponibilidadesMatutina==null) {
      this.disponibilidadesMatutina = new RowList<Disponibilidad>();
    }
    return this.disponibilidadesMatutina;
  }

  public Boolean getHayDisponibilidadesVespertina() {
    if(sesionMBean.getDisponibilidadesDelDiaVespertina()==null) {
      return false;
    }
    return !sesionMBean.getDisponibilidadesDelDiaVespertina().isEmpty();
  }

  public RowList<Disponibilidad> getDisponibilidadesVespertina() {
    this.disponibilidadesVespertina = sesionMBean.getDisponibilidadesDelDiaVespertina();
    if(this.disponibilidadesVespertina == null) {
      this.disponibilidadesVespertina = new RowList<Disponibilidad>();
    }
    return this.disponibilidadesVespertina;
  }

	
	public SesionMBean getSesionMBean() {
		return sesionMBean;
	}

	public void setSesionMBean(SesionMBean sesionMBean) {
		this.sesionMBean = sesionMBean;
	}

  public void seleccionarFecha() {
    DateFormat format = new SimpleDateFormat(sesionMBean.getFormatoFecha());
    Date date = null;
    try {
      date = format.parse(this.diaSeleccionadoStr);
      //Verificar que la fecha esté en el rango permitido (que no la modifiquen en el medio)
      Date inicio_disp = sesionMBean.getVentanaCalendario().getFechaInicial();
      Date fin_disp = sesionMBean.getVentanaCalendario().getFechaFinal();
      if(date.before(inicio_disp) || date.after(fin_disp)) {
        addErrorMessage(sesionMBean.getTextos().get("fecha_no_valida"));
        sesionMBean.setDiaSeleccionado(null);
        configurarDisponibilidadesDelDia();
        return;
      }
      
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(date);
      fechaFormatSelect = cal.get(Calendar.DAY_OF_MONTH)+" de "+deduccionMes(cal.get(Calendar.MONTH))+" "+cal.get(Calendar.YEAR);
      sesionMBean.setDiaSeleccionado(date);
      configurarDisponibilidadesDelDia();
    } catch (ParseException e1) {
      e1.printStackTrace();
    } catch (RolException e) {
      e.printStackTrace();

    }

  }

  public void seleccionarHorarioMatutino(SelectEvent event) {
    if (this.rowSelectMatutina != null) {
      setRowSelectVespertina(null);
    }
  }

  public void seleccionarHorarioVespertino(SelectEvent event) {
    if (this.rowSelectVespertina != null) {
      setRowSelectMatutina(null);
    }
  }

  public String siguientePaso() {
    if (!(rowSelectMatutina == null) || !(rowSelectVespertina == null)) {
      Row<Disponibilidad> row = null;
      if (!(rowSelectMatutina == null)) {
        row = rowSelectMatutina;
        if (row.getData().getCupo() > 0) {
          sesionMBean.getDisponibilidadesDelDiaMatutina().setSelectedRow(row);
          sesionMBean.getDisponibilidadesDelDiaVespertina().setSelectedRow(null);
        } else {
          addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_un_horario"));
          return null;
        }
      } else {
        row = rowSelectVespertina;
        if (row.getData().getCupo() > 0) {
          sesionMBean.getDisponibilidadesDelDiaMatutina().setSelectedRow(null);
          sesionMBean.getDisponibilidadesDelDiaVespertina().setSelectedRow(row);
        } else {
          addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_un_horario"));
          return null;
        }
      }
      try {
        //Intentar marcar la reserva para el día seleccionado, validando que no exista otra reserva para los mismos datos,
        //teniendo en cuenta que si es el mismo día que la reserva original sí va a existir y hay que ignorarla
        Reserva reserva = agendarReservasEJB.marcarReservaValidandoDatos(row.getData(), sesionMBean.getReservaModificar1(), null);
        sesionMBean.setReservaModificar2(reserva);
        return "siguientePaso";
      } catch (ValidacionClaveUnicaException vcuEx) {
        //Ya hay otra reserva con el mismo valor en todos los campos clave
        addErrorMessage("ya_tiene_una_reserva_para_el_dia_seleccionado");
        return null;
      } catch (Exception ex) {
        addErrorMessage(ex);
        configurarDisponibilidadesDelDia();
        return null;
      }
    } else {
      addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_un_horario"));
      return null;
    }
  }

  public JSONArray getJsonArrayFchDisp() {
    return jsonArrayFchDisp;
  }

  public void setJsonArrayFchDisp(JSONArray jsonArrayFchDisp) {
    this.jsonArrayFchDisp = jsonArrayFchDisp;
  }

  public String getDiaSeleccionadoStr() {
    return diaSeleccionadoStr;
  }

  public void setDiaSeleccionadoStr(String diaSeleccionadoStr) {
    this.diaSeleccionadoStr = diaSeleccionadoStr;
  }

  public String getSelectIdMatutina() {
    return selectIdMatutina;
  }

  public void setSelectIdMatutina(String selectIdMatutina) {
    this.selectIdMatutina = selectIdMatutina;
  }

  public List<SelectItem> getSelectItemsDispVespertina() {
    return selectItemsDispVespertina;
  }

  public void setSelectItemsDispVespertina(
      List<SelectItem> selectItemsDispVespertina) {
    this.selectItemsDispVespertina = selectItemsDispVespertina;
  }

  public List<SelectItem> getSelectItemsDispMatutina() {

    return selectItemsDispMatutina;

  }

  public void setSelectItemsDispMatutina(
      List<SelectItem> selectItemsDispVespertina) {
    this.selectItemsDispVespertina = selectItemsDispVespertina;
  }

  public String getSelectIdVespertina() {
    return selectIdVespertina;
  }

  public void setSelectIdVespertina(String selectIdVespertina) {
    this.selectIdVespertina = selectIdVespertina;
  }

  public Row<Disponibilidad> getRowSelectMatutina() {
    return rowSelectMatutina;
  }

  public void setRowSelectMatutina(Row<Disponibilidad> rowSelectMatutina) {

    this.rowSelectMatutina = rowSelectMatutina;
  }

  public Row<Disponibilidad> getRowSelectVespertina() {
    return rowSelectVespertina;
  }

  public void setRowSelectVespertina(Row<Disponibilidad> rowSelectVespertina) {
    this.rowSelectVespertina = rowSelectVespertina;
  }

  public String Paso1() {
    return sesionMBean.getUrlPaso1Reserva() + "&faces-redirect=true";
  }

  public String getFechaFormatSelect() {
    return fechaFormatSelect;
  }

  public void setFechaFormatSelect(String fechaFormatSelect) {
    this.fechaFormatSelect = fechaFormatSelect;
  }

  private String deduccionMes(int i) {
    
    String nombreMes = null;
    switch (i) {
    case Calendar.JANUARY:
      nombreMes = "Enero";
      break;
    case Calendar.FEBRUARY:
      nombreMes = "Febrero";
      break;
    case Calendar.MARCH:
      nombreMes = "Marzo";
      break;
    case Calendar.APRIL:
      nombreMes = "Abril";
      break;
    case Calendar.MAY:
      nombreMes = "Mayo";
      break;
    case Calendar.JUNE:
      nombreMes = "Junio";
      break;
    case Calendar.JULY:
      nombreMes = "Julio";
      break;
    case Calendar.AUGUST:
      nombreMes = "Agosto";
      break;
    case Calendar.SEPTEMBER:
      nombreMes = "Setiembre";
      break;
    case Calendar.OCTOBER:
      nombreMes = "Octubre";
      break;
    case Calendar.NOVEMBER:
      nombreMes = "Noviembre";
      break;
    default:
      nombreMes = "Diciembre";
      break;
    }
    return nombreMes;
  }

  public String getFiltroHorarios() {
    
    return filtroHorarios;
  }

  public void setFiltroHorarios(String filtroHorarios) {
    this.filtroHorarios = filtroHorarios;
  }

  public String claseSegunCupo(Disponibilidad disponibilidad) {
    if(disponibilidad.getCupo()==null || disponibilidad.getCupo()<1) {
      return "cupoNoSeleccionable";
    }
    return "";
  }

	public boolean isErrorInit() {
		return errorInit;
	}

  @PreDestroy
  public void preDestroy() {
    try {
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      this.agendarReservasEJB = null;
      this.sesionMBean = null;
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
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
