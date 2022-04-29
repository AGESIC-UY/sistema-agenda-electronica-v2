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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Configuracion;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.factories.BusinessLocator;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.login.Utilidades;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.SofisJSFUtils;

/*
 * Invocación desde un sistema externo:
 * https://192.168.1.13:8443/sae/reservaMultiple/Paso1.xhtml?e=1000001&a=29
 * 
 */
public class MultiplePaso1MBean extends BaseMBean {

  private static Logger logger = Logger.getLogger(MultiplePaso1MBean.class);

  private AgendarReservas agendarReservasEJB;

  private SesionMBean sesionMBean;

  private String mensajeError = null;

  private boolean errorInit;

  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionRemote")
  private Configuracion configuracionEJB;
  
  private String cedula;
  private String nombre;
  private String correoe;

  //private Map<String, TramiteAgenda> tramitesAgenda;
  private List<SelectItem> tramites;
  private String tramiteCodigo;

  private DateFormat formatoFechaHora = null;
  
  public void beforePhase(PhaseEvent phaseEvent) {
    disableBrowserCache(phaseEvent);
    if (phaseEvent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      // sesionMBean.limpiarPaso2();
    }
  }

  @PostConstruct
  public void init() {
    try {
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      logger.debug("Parámetros GET: ");
      logger.debug("              : e=[" + request.getParameter("e") + "]");
      logger.debug("              : a=[" + request.getParameter("a") + "]");
      logger.debug("              : r=[" + request.getParameter("r") + "]");
      logger.debug("              : i=[" + request.getParameter("i") + "]");
      logger.debug("              : u=[" + request.getParameter("u") + "]");
      logger.debug("              : p=[" + request.getParameter("p") + "]");
      logger.debug("              : t=[" + request.getParameter("t") + "]");
      logger.debug("              : q=[" + request.getParameter("q") + "]");
      logger.debug("              : m=[" + request.getParameter("m") + "]");

      String sEmpresaId = request.getParameter("e"); // Id de la empresa
      String sAgendaId = request.getParameter("a"); // Id de la agenda
      String sRecursoId = request.getParameter("r"); // Id del recurso

      String sIdioma = request.getParameter("i"); // Idioma (es,en,...)
      String sUrl = request.getParameter("u"); // URL de retorno al confirmar (URL encoded)
      String sParms = request.getParameter("p"); // Parámetros para autocompletar: <agrupacion>.<dato>.<valor>;)
      String sTraza = request.getParameter("t"); // Código de trazabilidad y paso padre (<trazguid>-<paso>)
      String sTramite = request.getParameter("q"); // Código de trámite
      String sToken = request.getParameter("m"); //Token de reserva múltiple

      if(sParms != null) {
        sesionMBean.setParmsDatosCiudadano(sParms);
      }else {
        sesionMBean.setParmsDatosCiudadano(null);
      }
      if(sUrl != null) {
        sesionMBean.setUrlTramite(sUrl);
      }else {
        sesionMBean.setUrlTramite(null);
      }
      if(sTraza != null) {
        sesionMBean.setCodigoTrazabilidadPadre(sTraza);
      }else {
        sesionMBean.setCodigoTrazabilidadPadre(null);
      }
      if(sTramite != null) {
        sesionMBean.setCodigoTramite(sTramite);
      }else {
        sesionMBean.setCodigoTramite(null);
      }
      if(sIdioma != null) {
        sesionMBean.setIdiomaActual(sIdioma);
      } else {
        if (request.getLocale() != null) {
          sesionMBean.setIdiomaActual(request.getLocale().getLanguage());
        } else {
          sesionMBean.setIdiomaActual(Locale.getDefault().getLanguage());
        }
      }

      errorInit = false;
      Integer empresaId = null;
      Integer agendaId = null;
      Integer recursoId = null;

      if (sEmpresaId == null || sAgendaId == null) {
        addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
        errorInit = true;
        return;
      }
      try {
        empresaId = Integer.valueOf(sEmpresaId);
        agendaId = Integer.valueOf(sAgendaId);
        if (sRecursoId != null) {
          recursoId = Integer.valueOf(sRecursoId);
        }
      } catch (Exception e) {
        addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
        errorInit = true;
        return;
      }

      // Poner en sesion los datos de la empresa y la agenda para la válvula de CDA
      // (necesita estos datos para determinar si la agenda particular requiere o no CDA)
      HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
      httpSession.setAttribute("e", empresaId.toString());
      httpSession.setAttribute("a", agendaId.toString());

      String remoteUser = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();

      try {
        // Crear un usuario falso temporal
        String falsoUsuario = null;
        if (remoteUser == null) {
          // No hay usuario, se crea uno
          sesionMBean.setUsuarioCda(null);
          falsoUsuario = "sae" + empresaId;
        } else {
          // Hay usuario, dos alternativas: es de cda o es local de otra empresa
          if (!remoteUser.startsWith("sae")) {
            // Es un usuario de CDA
            sesionMBean.setUsuarioCda(remoteUser);
            falsoUsuario = remoteUser;
          } else {
            // Ees un usuario de otra empresa
            falsoUsuario = "sae" + empresaId;
            sesionMBean.setUsuarioCda(null);
          }
          // Desloguear al usuario actual (inválido)
          try {
            request.logout();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        // Si no es un usuario de CDA se añade un número randómico para evitar
        // conflictos con otros usuarios
        if (falsoUsuario.startsWith("sae")) {
          Random random = new Random();
          falsoUsuario = falsoUsuario + "-" + ((new Date()).getTime() + random.nextInt(1000));
        }
        falsoUsuario = falsoUsuario + "/" + empresaId;
        // Autenticarlo
        String password = Utilidades.encriptarPassword(falsoUsuario);
        request.login(falsoUsuario, password);
      } catch (Exception ex) {
        ex.printStackTrace();
        throw new ApplicationException(sesionMBean.getTextos().get("no_se_pudo_registrar_un_usuario_anonimo"));
      }

      // Cargar los textos dependientes del idioma
      // Es necesario hacerlo de nuevo porque recién ahora se sabe a cuál
      // esquema ir a buscarlos
      // Es especialmente necesario para las pregunta de captcha ya que no
      // tienen idioma por defecto
      sesionMBean.cargarTextos();

      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      agendarReservasEJB = bl.getAgendarReservas();

      formatoFechaHora = new SimpleDateFormat(sesionMBean.getFormatoFecha()+" "+sesionMBean.getFormatoHora());
      
      // Guardar la empresa en la sesion
      try {
        Empresa empresa = agendarReservasEJB.obtenerEmpresaPorId(empresaId);
        if (empresa == null || empresa.getFechaBaja() != null) {
          addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
          sesionMBean.setEmpresaActual(null);
          errorInit = true;
          return;
        } else {
          sesionMBean.setEmpresaActual(empresa);
        }
      } catch (Exception e) {
        addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
        errorInit = true;
        return;
      }
      
      String paginaDeRetorno = request.getParameter("pagina_retorno");
      if (empresaId != null && agendaId != null) {
        try {
          sesionMBean.seleccionarAgenda(agendaId);
          sesionMBean.setPaginaDeRetorno(paginaDeRetorno);
        } catch (Exception ae) {
          addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
          errorInit = true;
          return;
        }
        String url = "/reservaMultiple/Paso1.xhtml?e=" + empresaId + "&a=" + agendaId;
        if (recursoId != null) {
          url = url + "&r=" + recursoId;
        }
        if (sIdioma != null) {
          url = url + "&i=" + sIdioma;
        }
        if (sUrl != null) {
          url = url + "&u=" + sUrl;
        }
        if (sParms != null) {
          url = url + "&p=" + sParms;
        }
        if (sTraza != null) {
          url = url + "&t=" + sTraza;
        }
        if (sTramite != null) {
          url = url + "&q=" + sTramite;
        }
        if (sToken != null) {
          url = url + "&m=" + sToken;
        }
        sesionMBean.setUrlPaso1Reserva(url);
      }
      
      if (sesionMBean.getAgenda() != null) {
        //Cargar los trámites
        this.tramiteCodigo = null;
        this.tramites = new ArrayList<SelectItem>();
        List<TramiteAgenda> tramites0 = agendarReservasEJB.consultarTramites(sesionMBean.getAgenda());
        //Si se especificó el trámite a realizar en la URL (parámetro "q") no se permite al ciudadano 
        //seleccionar el trámite (si el trámite especificado no corresponde a un trámite asociado a la
        //agenda se ignora el parámetro).
        //Si no se especificó el trámite en la URL pero la agenda está asociada a un único trámite se
        //selecciona auntomáticamente el único trámite y tampoco se permite modificar la selección
        //String codigoTramite = sesionMBean.getCodigoTramite();
        if(sTramite != null) {
          //Buscar el trámite correspondiente al código
          for(TramiteAgenda tramite : tramites0) {
            if(sTramite.equals(tramite.getTramiteCodigo())) {
              tramiteCodigo = tramite.getTramiteCodigo();
              break;
            }
          }
        }
        //Si no se identifico un trámite continuar con el procedimiento usual
        if(this.tramiteCodigo == null) {
          if(tramites0.size()==1) {
            //Hay un solo trámite asociado a la agenda, se selecciona solo ese
            TramiteAgenda tramite = tramites0.get(0);
            this.tramiteCodigo = tramite.getTramiteCodigo();
          }else {
            //El ciudadano debe seleccionar el trámite
            this.tramites.add(new SelectItem("", "Sin especificar"));
            for(TramiteAgenda tramite : tramites0) {
              this.tramites.add(new SelectItem(tramite.getTramiteCodigo(), tramite.getTramiteNombre()));
            }
          }
        }
        
        Recurso recursoDefecto = null;
        if (sToken != null) {
          TokenReserva token = agendarReservasEJB.obtenerTokenReserva(sToken);
          sesionMBean.setTokenReserva(null);
          if(token == null) {
            addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_el_token"));
            errorInit = true;
          }else {
            if(token.getEstado() == Estado.R) {
              addErrorMessage(sesionMBean.getTextos().get("el_token_esta_confirmado"));
              errorInit = true;
            }else if(token.getEstado() == Estado.C) {
              addErrorMessage(sesionMBean.getTextos().get("el_token_esta_cancelado"));
              errorInit = true;
            }else {
              Long tiempoMaximo = configuracionEJB.getLong("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO");
              if(tiempoMaximo == null) {
                tiempoMaximo = 10L;
              }
              Date fechaToken = token.getUltimaReserva()!=null?token.getUltimaReserva():token.getFechaInicio();
              long diferenciaMinutos = (((new Date()).getTime() - fechaToken.getTime()) / 60000);
              if(diferenciaMinutos > tiempoMaximo) {
                addErrorMessage(sesionMBean.getTextos().get("el_token_ha_expirado"));
                errorInit = true;
              }else if(token.getRecurso()!=null && recursoId!=null && token.getRecurso().getId().intValue() != recursoId.intValue()) {
                addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_el_token"));
                errorInit = true;
              }else {
                this.sesionMBean.setTokenReserva(token);
                this.cedula = token.getCedula();
                this.nombre = token.getNombre();
                this.correoe = token.getCorreoe();
                this.tramiteCodigo = token.getTramite();
                recursoDefecto = token.getRecurso();
              }
            }
          }
        }else {
          TokenReserva token = sesionMBean.getTokenReserva(); 
          if(token != null) {
            sesionMBean.setTokenReserva(null);
            if(token.getEstado() != Estado.P) {
              sesionMBean.setTokenReserva(null);
            }else {
              Long tiempoMaximo = configuracionEJB.getLong("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO");
              if(tiempoMaximo == null) {
                tiempoMaximo = 10L;
              }
              Date fechaToken = token.getUltimaReserva()!=null?token.getUltimaReserva():token.getFechaInicio();
              long diferenciaMinutos = (((new Date()).getTime() - fechaToken.getTime()) / 60000);
              if(diferenciaMinutos > tiempoMaximo) {
                addErrorMessage(sesionMBean.getTextos().get("el_token_ha_expirado"));
                errorInit = true;
              }else if(token.getRecurso()!=null && recursoId!=null && token.getRecurso().getId().intValue() != recursoId.intValue()) {
                addErrorMessage(sesionMBean.getTextos().get("no_se_encuentra_el_token"));
                errorInit = true;
              }else {
                this.sesionMBean.setTokenReserva(token);
                this.cedula = token.getCedula();
                this.nombre = token.getNombre();
                this.correoe = token.getCorreoe();
                this.tramiteCodigo = token.getTramite();
                recursoDefecto = token.getRecurso();
              }
            }
          }
        }
        //Guardar el código del trámite
        this.sesionMBean.setCodigoTramite(this.tramiteCodigo);
        //Guardar el recurso
        if(recursoDefecto==null && recursoId != null) {
          recursoDefecto = agendarReservasEJB.consultarRecursoPorId(sesionMBean.getAgenda(), recursoId);
        }
        sesionMBean.setRecurso(recursoDefecto);
        //Cambiar el idioma
        List<String> idiomasDisponibles = Arrays.asList(sesionMBean.getAgenda().getIdiomas().split(","));
        if (!idiomasDisponibles.contains(sesionMBean.getIdiomaActual()) && !idiomasDisponibles.isEmpty()) {
          sesionMBean.cambioIdiomaActual(idiomasDisponibles.get(0));
        }
      }else {
        logger.error("No se pudo incializar la reserva múltiple: no hay agenda seleccionada");
        addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
        errorInit = true;
      }
    } catch (Exception ex) {
      logger.error("No se pudo incializar la reserva múltiple", ex);
      addErrorMessage(sesionMBean.getTextos().get("sistema_en_mantenimiento"));
      errorInit = true;
    }
  }

  public String getCedula() {
    return cedula;
  }

  public void setCedula(String cedula) {
    this.cedula = cedula;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getCorreoe() {
    return correoe;
  }

  public void setCorreoe(String correoe) {
    this.correoe = correoe;
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

  public SesionMBean getSesionMBean() {
    return sesionMBean;
  }

  public void setSesionMBean(SesionMBean sesionMBean) {
    this.sesionMBean = sesionMBean;
  }

  public String siguientePaso() {
    limpiarMensajesError();
    boolean hayErrores = false;
    if (cedula == null || cedula.trim().isEmpty()) {
      hayErrores = true;
      addErrorMessage(sesionMBean.getTextos().get("la_cedula_es_obligatoria"), "form", "form:cedula");
    }
    if (nombre == null || nombre.trim().isEmpty()) {
      hayErrores = true;
      addErrorMessage(sesionMBean.getTextos().get("el_nombre_es_obligatorio"), "form", "form:nombre");
    }
    if (correoe == null || correoe.trim().isEmpty()) {
      hayErrores = true;
      addErrorMessage(sesionMBean.getTextos().get("el_correo_electronico_es_obligatorio"), "form", "form:correoe");
    } else {
      Pattern pat = Pattern.compile(Utiles.EMAIL_PATTERN);
      Matcher mat = pat.matcher(correoe);
      if (!mat.find()) {
        addErrorMessage(sesionMBean.getTextos().get("correo_electronico_no_valido"), "form", "form:correoe");
        hayErrores = true;
      }
    }
    if(this.tramiteCodigo==null || this.tramiteCodigo.isEmpty()) {
      hayErrores = true;
      addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_el_tramite"), "form", "form:tramite");
    }

    if(hayErrores) {
      return null;
    }
    
    sesionMBean.setCodigoTramite(this.tramiteCodigo);
    
    //Si no hay un token creado, crearlo ahora
    TokenReserva token = sesionMBean.getTokenReserva();
    if(token != null) {
      token.setCedula(cedula);
      token.setCorreoe(correoe);
      token.setNombre(nombre);
      if(getReservas().isEmpty()) {
        //Solo si no hay reservas se permite modificar el código del trámite
        token.setTramite(this.tramiteCodigo);
      }
      token = agendarReservasEJB.guardarTokenReserva(token);
      sesionMBean.setTokenReserva(token);
      return "siguientePaso";
    }
    try {
      //Se genera el token sin el recurso
      String ipOrigen = SofisJSFUtils.obtenerDireccionIPCliente(FacesContext.getCurrentInstance());
      token = agendarReservasEJB.generarTokenReserva(null, cedula, nombre, correoe, tramiteCodigo, ipOrigen);
      //Se almacena el token en la sesión
      sesionMBean.setTokenReserva(token);
      //Ir al siguiente paso
      return "siguientePaso";
    }catch(Exception ex) {
      logger.error("Error al generar el token para la reserva múltiple", ex);
        addErrorMessage(sesionMBean.getTextos().get("error_generando_token"), "form");
        return null;
    }
  }

  public boolean isErrorInit() {
    return errorInit;
  }

  public void setErrorInit(boolean errorInit) {
    this.errorInit = errorInit;
  }

  @PreDestroy
  public void preDestroy() {
    try {
      logger.debug("Destruyendo una instancia de " + this.getClass().getName() + ", liberando objetos...");
      this.agendarReservasEJB = null;
      this.sesionMBean = null;
      
      if(this.tramites != null) {
        this.tramites.clear();
      }
      this.tramites = null;
      
      logger.debug("Destruyendo una instancia de " + this.getClass().getName() + ", objetos liberados.");
    } catch (Exception ex) {
      logger.debug("Destruyendo una instancia de " + this.getClass().getName() + ", error.", ex);
    }
  }

  public List<Reserva> getReservas() {
    TokenReserva token = sesionMBean.getTokenReserva();
    if(token != null) {
      return agendarReservasEJB.obtenerReservasMultiples(token.getId(), false);
    }
    return null;
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
  
  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  public List<SelectItem> getTramites() {
    return tramites;
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