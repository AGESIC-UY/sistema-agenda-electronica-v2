package uy.gub.imm.sae.web.mbean.reserva;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.business.ejb.facade.Disponibilidades;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.login.Utilidades;
import uy.gub.imm.sae.web.common.BaseMBean;

/*
 Línea 19, luego de <p:idleMonitor timeout="10000" >
        <p:ajax event="idle" listener="#{presencialAutogestionMBean.limpiarFormulario}" update="formDatosReserva" />
 
 Línea 28, luego de <h:form id="reservaConfirmada">
        <p:poll widgetVar="pollRC" interval="4" autoStart="false" listener="#{presencialAutogestionMBean.limpiarFormulario}" 
         update="pnlReserva" oncomplete="PF('reservaConfirmada').hide();" />
         
 Línea 23, en <p:dialog closable="false" shadowOpacity="0.4" autosized="false" widgetVar="reservaConfirmada" zindex="2000" modal="true" resizable="false"
 onShow="PF('pollRC').start()" onHide="PF('pollRC').stop()"
 
 */

public class PresencialAutogestionMBean extends BaseMBean {

  static Logger logger = Logger.getLogger(PresencialAutogestionMBean.class);
  
  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
  private AgendarReservas agendarReservasEJB;

  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
  private Recursos recursosEJB;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/DisponibilidadesBean!uy.gub.imm.sae.business.ejb.facade.DisponibilidadesRemote")
  private Disponibilidades disponibilidadesEJB;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/ConsultasBean!uy.gub.imm.sae.business.ejb.facade.ConsultasRemote")
  private Consultas consultaEJB;
  
  private SesionMBean sesionMBean; 
  
  private boolean hayErrorInit;

	private boolean conCedula = true;
  private List<SelectItem> tiposDocumento  = new ArrayList<SelectItem>();
  private String tipoDocumento;
  private String numeroDocumento;
  private String tramiteCodigo;

  private Map<String, TramiteAgenda> tramitesAgenda;
  private List<SelectItem> tramites;
  
  private List<DatoASolicitar> datosSolicitar;
  
  private Disponibilidad disponibilidadPresencialDia;
  
  public SesionMBean getSesionMBean() {
    return sesionMBean;
  }

  public void setSesionMBean(SesionMBean sesionMBean) {
    this.sesionMBean = sesionMBean;
  }

  public boolean isConCedula() {
    return conCedula;
  }

  public void setConCedula(boolean conCedula) {
    this.conCedula = conCedula;
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
  
  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  public List<SelectItem> getTramites() {
    return tramites;
  }

  public boolean isHayErrorInit() {
    return hayErrorInit;
  }

  @PostConstruct
  public void init() {
    try {
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      //Estos tres son obligatorios
      String sEmpresaId = request.getParameter("e");
      String sAgendaId = request.getParameter("a");
      String sRecursoId = request.getParameter("r");
      
      Integer empresaId = null;
      Integer agendaId = null;
      Integer recursoId = null;
      
      if(sEmpresaId!=null && sAgendaId!=null && sRecursoId!=null) {
        try{
          empresaId = Integer.valueOf(sEmpresaId);
          agendaId = Integer.valueOf(sAgendaId);
          recursoId = Integer.valueOf(sRecursoId);
          limpiarSession();
          sesionMBean.setEmpresaId(empresaId);
          sesionMBean.setAgendaId(agendaId);
          sesionMBean.setRecursoId(recursoId);
        }catch(Exception ex) {
          addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
          limpiarSession();
          hayErrorInit = true;
          return;
        }
      }else {
        //No hay parámetros, pueden estar ya en la sesion
        empresaId = sesionMBean.getEmpresaId();
        agendaId = sesionMBean.getAgendaId();
        recursoId = sesionMBean.getRecursoId();
        if(empresaId==null || agendaId==null || recursoId==null) {
          //Tampoco están en sesión
          addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
          limpiarSession();
          hayErrorInit = true;
          return;
        }
      }
      
      try {
        if(request.getRemoteUser()!=null) {
          request.logout();
        }
        // Crear un usuario falso temporal
        String falsoUsuario = "sae" + empresaId;
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
      
      // Guardar la empresa en la sesion
      try {
        Empresa empresa = agendarReservasEJB.obtenerEmpresaPorId(empresaId);
        sesionMBean.setEmpresaActual(empresa);
        if (empresa==null) {
          addErrorMessage(sesionMBean.getTextos().get("la_empresa_especificada_no_es_valida"));
          limpiarSession();
          hayErrorInit = true;
          return;
        }
      } catch (Exception e) {
        addErrorMessage(sesionMBean.getTextos().get("la_empresa_especificada_no_es_valida"));
        limpiarSession();
        hayErrorInit = true;
        return;
      }
      
      //Guardar la agenda
      try {
        sesionMBean.seleccionarAgenda(agendaId);
      }catch (Exception  ae) {
        addErrorMessage(sesionMBean.getTextos().get("la_agenda_especificada_no_es_valida"));
        limpiarSession();
        hayErrorInit = true;
        return;
      }

      //Guardar el recurso
      Recurso recurso = null;
      try {
        List<Recurso> recursos = agendarReservasEJB.consultarRecursos(sesionMBean.getAgenda());
        for (Recurso recurso0 : recursos) {
          if (recurso0.getId().equals(recursoId)) {
            recurso = recurso0;
            sesionMBean.setRecurso(recurso0);
            break;
          }
        }
        if(sesionMBean.getRecurso() == null) {
          addErrorMessage(sesionMBean.getTextos().get("el_recurso_especificado_no_es_valido"));
          limpiarSession();
          hayErrorInit = true;
          return;
        }
        this.sesionMBean.setRenderedVolverBotom(true);
      }catch(Exception ex) {
        addErrorMessage(sesionMBean.getTextos().get("la_agenda_especificada_no_es_valida"));
        limpiarSession();
        hayErrorInit = true;
        return;
      }

      //Determinar si el recurso admite atención presencial
      if(recurso.getPresencialAdmite()==null || !recurso.getPresencialAdmite().booleanValue()) {
        addErrorMessage(sesionMBean.getTextos().get("el_recurso_no_admite_atencion_presencial"));
        hayErrorInit = true;
        return;
      }else {
        //Determinar si el recurso está vigente para el día actual
        Calendar hoy = new GregorianCalendar();
        hoy.add(Calendar.MILLISECOND, sesionMBean.getTimeZone().getOffset(hoy.getTimeInMillis()));
        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MILLISECOND, 0);
        if(recurso.getFechaInicioDisp().after(hoy.getTime()) || recurso.getFechaFinDisp().before(hoy.getTime())) {
          addErrorMessage(sesionMBean.getTextos().get("la_recurso_no_esta_vigente"));
          hayErrorInit = true;
          return;
        }    
        //Determinar si el recurso admite atención presencial para el día actual
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.MILLISECOND, sesionMBean.getTimeZone().getOffset(cal.getTimeInMillis()));
        int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
        if((diaSemana==Calendar.MONDAY && (recurso.getPresencialLunes()==null || !recurso.getPresencialLunes().booleanValue())) ||
           (diaSemana==Calendar.TUESDAY && (recurso.getPresencialMartes()==null || !recurso.getPresencialMartes().booleanValue())) ||
           (diaSemana==Calendar.WEDNESDAY && (recurso.getPresencialMiercoles()==null || !recurso.getPresencialMiercoles().booleanValue())) ||
           (diaSemana==Calendar.THURSDAY && (recurso.getPresencialJueves()==null || !recurso.getPresencialJueves().booleanValue())) ||
           (diaSemana==Calendar.FRIDAY && (recurso.getPresencialViernes()==null || !recurso.getPresencialViernes().booleanValue())) ||
           (diaSemana==Calendar.SATURDAY && (recurso.getPresencialSabado()==null || !recurso.getPresencialSabado().booleanValue())) ||
           (diaSemana==Calendar.SUNDAY && (recurso.getPresencialDomingo()==null || !recurso.getPresencialDomingo().booleanValue()))) {
          addErrorMessage(sesionMBean.getTextos().get("el_recurso_no_admite_atencion_presencial_para_hoy"));
          hayErrorInit = true;
          return;
        }
      }
      
      //Determinar si quedan cupos disponibles para hoy
      disponibilidadPresencialDia = disponibilidadesEJB.obtenerDisponibilidadPresencial(recurso, sesionMBean.getTimeZone());
      if(disponibilidadPresencialDia == null) {
        addErrorMessage(sesionMBean.getTextos().get("el_recurso_no_admite_atencion_presencial"));
        hayErrorInit = true;
        return;
      }
      
      //Cargar los tramites de la agenda
      this.tramiteCodigo = null;
      tramitesAgenda = new HashMap<String, TramiteAgenda>();
      tramites = new ArrayList<SelectItem>();
      
      List<TramiteAgenda> tramites0 = agendarReservasEJB.consultarTramites(sesionMBean.getAgenda());
      if(tramites0.size()==1) {
        TramiteAgenda tramite = tramites0.get(0);
        tramiteCodigo = tramite.getTramiteCodigo();
        tramitesAgenda.put(tramiteCodigo, tramite);
      }else {
        tramites.add(new SelectItem("", sesionMBean.getTextos().get("seleccionar")));
        for(TramiteAgenda tramite : tramites0) {
          tramitesAgenda.put(tramite.getTramiteCodigo(), tramite);
          tramites.add(new SelectItem(tramite.getTramiteCodigo(), tramite.getTramiteNombre()));
        }
      }
      
      tiposDocumento = new ArrayList<SelectItem>();
      tiposDocumento.add(new SelectItem("", sesionMBean.getTextos().get("seleccionar")));
      datosSolicitar = recursosEJB.consultarDatosSolicitar(sesionMBean.getRecurso());
      if(datosSolicitar!=null) {
        for(DatoASolicitar datoSolicitar : datosSolicitar) {
          if(!datoSolicitar.getAgrupacionDato().getBorrarFlag() && datoSolicitar.getNombre().equals("TipoDocumento")) {
            for(ValorPosible valor : datoSolicitar.getValoresPosibles()) {
              tiposDocumento.add(new SelectItem(valor.getValor(), valor.getEtiqueta()));
            }
          }
        }
      }
      
      limpiarFormulario();
    } catch (Exception e) {
      logger.error(e);
      redirect(ERROR_PAGE_OUTCOME);
    }
  }
	
  public void beforePhase(PhaseEvent phaseEvent) {
    disableBrowserCache(phaseEvent);
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

	public void sinCedula() {
	  this.conCedula = false;
	  this.tipoDocumento = "";
	}
	
	public void solicitarTurno(ActionEvent event) {
	  //Si el tipo de documento es cédula de identidad se debe limpiar el número
    if("CI".equals(tipoDocumento)) {
      if(numeroDocumento!=null) {
        numeroDocumento = numeroDocumento.replaceAll("[.-]", "");
      }
    }
	  //Validar los campos obligatorios
    boolean huboError = false;
    if(tipoDocumento==null || tipoDocumento.trim().isEmpty()) {
      huboError = true;
      addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_el_tipo_de_documento"));
    }
    if(numeroDocumento==null || numeroDocumento.trim().isEmpty()) {
      huboError = true;
      addErrorMessage(sesionMBean.getTextos().get("debe_ingresar_el_numero_de_documento"));
    }
    //Si es cédula de identidad validar el formato
    if(!huboError && "CI".equals(tipoDocumento)) {
      boolean ciOk = Utiles.validarDigitoVerificadorCI(numeroDocumento);
      if(!ciOk) {
        huboError = true;
        addErrorMessage(sesionMBean.getTextos().get("cedula_de_identidad_invalida"));
      }
    }
    if(tramiteCodigo==null || tramiteCodigo.trim().isEmpty()) {
      huboError = true;
      addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_el_tramite"));
    }
    if(huboError) {
      return;
    }
    
    try {
      Recurso recurso = sesionMBean.getRecurso();
      //Obtener una reserva presencial (lanza UserException si ya no quedan)
      Disponibilidad disponibilidad = disponibilidadesEJB.obtenerDisponibilidadPresencial(recurso, sesionMBean.getTimeZone());
      Reserva reserva = agendarReservasEJB.marcarReserva(disponibilidad);
      //Completar los datos de la reserva
      Set<DatoReserva> datosReserva = new HashSet<DatoReserva>();
      if(datosSolicitar!=null) {
        for(DatoASolicitar datoSolicitar : datosSolicitar) {
          DatoReserva datoReserva = new DatoReserva();
          datoReserva.setDatoASolicitar(datoSolicitar);
          //Si es el tipo de documento o numero de documento se pone lo ingresado por el usuario
          //Si es otro campo y es obligatorio se pone un valor por defecto
          if(datoSolicitar.getNombre().equals("TipoDocumento")) {
            datoReserva.setValor(tipoDocumento);
            datosReserva.add(datoReserva);
          }else if(datoSolicitar.getNombre().equals("NroDocumento")) {
            datoReserva.setValor(numeroDocumento);
            datosReserva.add(datoReserva);
          }else if(datoSolicitar.getRequerido()!=null && datoSolicitar.getRequerido().booleanValue()) {
            switch(datoSolicitar.getTipo()) {
              case BOOLEAN:
                datoReserva.setValor(Boolean.FALSE.toString());
                break;
              case DATE:
                datoReserva.setValor(Utiles.getFechaInvalida().toString());
                break;
              case LIST:
                if(datoSolicitar.getValoresPosibles()!=null && !datoSolicitar.getValoresPosibles().isEmpty()) {
                  datoReserva.setValor(datoSolicitar.getValoresPosibles().get(0).getValor());
                }
                break;
              case NUMBER:
                datoReserva.setValor("0");
                break;
              case STRING:
                if(datoSolicitar.getNombre().equals("Mail")) {
                  datoReserva.setValor("Reserva"+reserva.getId()+"@sae.uy");
                }else {
                  datoReserva.setValor("---");
                }
                break;
            }
            datosReserva.add(datoReserva);
          }
        }
      }
      reserva.setDatosReserva(datosReserva);
      //Completar los datos del trámite
      reserva.setTramiteCodigo(tramiteCodigo);
      reserva.setTramiteNombre(tramitesAgenda.containsKey(tramiteCodigo)?tramitesAgenda.get(tramiteCodigo).getTramiteNombre():"");
      //Validar los datos de la reserva
      agendarReservasEJB.validarDatosReserva(sesionMBean.getEmpresaActual(), reserva);
      //Confirmar la reserva
      boolean confirmada = false;
      while (!confirmada) {
        try {
          Reserva rConfirmada = agendarReservasEJB.confirmarReservaPresencial(sesionMBean.getEmpresaActual(), reserva);
          reserva.setSerie(rConfirmada.getSerie());
          reserva.setNumero(rConfirmada.getNumero());
          reserva.setCodigoSeguridad(rConfirmada.getCodigoSeguridad());
          reserva.setTrazabilidadGuid(rConfirmada.getTrazabilidadGuid());
          confirmada = true;
        } catch (AccesoMultipleException e){
          //Reintento hasta tener exito, en algun momento no me va a dar acceso multiple.
        }
      }
      RequestContext.getCurrentInstance().execute("PF('reservaConfirmada').show();");
    }catch (ValidacionClaveUnicaException vcuEx) {
      addErrorMessage(sesionMBean.getTextos().get("ya_existe_una_reserva_para_el_dia_especificado_con_los_datos_proporcionados"));
    }catch (UserException uEx) {
      if("el_horario_acaba_de_quedar_sin_cupos".equals(uEx.getCodigoError())) {
        addErrorMessage(sesionMBean.getTextos().get("no_hay_cupos_disponibles_para_hoy"));
        this.hayErrorInit = true;
      }else {
        addErrorMessage(sesionMBean.getTextos().get(uEx.getCodigoError()));
      }
    }catch(Exception ex) {
      addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
      ex.printStackTrace();
    }
	}
	
	public void limpiarFormulario() {
	  this.conCedula = true;
    boolean existeCI = false;
    if(datosSolicitar!=null) {
      for(DatoASolicitar dato : datosSolicitar) {
        if(!dato.getAgrupacionDato().getBorrarFlag() && dato.getNombre().equals("TipoDocumento")) {
          for(ValorPosible valor : dato.getValoresPosibles()) {
            if("CI".equalsIgnoreCase(valor.getValor())) {
              existeCI = true;
            }
          }
        }
      }
    }
    if(existeCI) {
      tipoDocumento = "CI";
      conCedula = true;
    }else {
      if(!tiposDocumento.isEmpty()) {
        tipoDocumento = (String) tiposDocumento.get(0).getValue();
      }else {
        tipoDocumento = "";
      }
      conCedula = false;
    }
    numeroDocumento = "";
    if(tramitesAgenda!=null && tramitesAgenda.size()==1) {
      tramiteCodigo = ((TramiteAgenda)tramitesAgenda.values().toArray()[0]).getTramiteCodigo();
    }else {
      tramiteCodigo = "";
    }
    //Verificar que queden cupos para atención presencial
    if(!agendarReservasEJB.hayCupoPresencial(disponibilidadPresencialDia)) {
      addErrorMessage(sesionMBean.getTextos().get("no_hay_cupos_disponibles_para_hoy"));
      hayErrorInit = true;
      return;
    }
	}
	
  public void mantenerSesionActiva() {
    //Nada para hacer, solo se utiliza para mantener la sesión activa indefinidamente
    logger.info("Manteniendo la sesesión activa...");
  }
  
}
