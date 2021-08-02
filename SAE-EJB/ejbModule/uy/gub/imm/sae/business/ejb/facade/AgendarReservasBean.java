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

package uy.gub.imm.sae.business.ejb.facade;


import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;



import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import uy.gub.agesic.novedades.Acciones;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.dto.ResultadoEjecucion;
import uy.gub.imm.sae.business.ejb.servicios.ServiciosNovedadesBean;
import uy.gub.imm.sae.business.ejb.servicios.ServiciosTrazabilidadBean;
import uy.gub.imm.sae.common.SofisHashMap;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.Evento;
import uy.gub.imm.sae.common.enumerados.FormaCancelacion;
import uy.gub.imm.sae.common.enumerados.TipoCancelacion;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.PreguntaCaptcha;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TextoTenant;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.TextoGlobal;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.AutocompletarException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAccionException;
import uy.gub.imm.sae.exception.ErrorValidacionException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionException;
import uy.gub.imm.sae.exception.ValidacionIPException;

@Stateless
public class AgendarReservasBean implements AgendarReservasLocal, AgendarReservasRemote {
  
  @PersistenceContext(unitName = "AGENDA-GLOBAL")
  private EntityManager globalEntityManager;

  @PersistenceContext(unitName = "SAE-EJB")
  private EntityManager entityManager;

  @EJB
  private AgendarReservasHelperLocal helper; 

  @EJB
  private ServiciosTrazabilidadBean trazaBean;

  @EJB
  private ServiciosNovedadesBean novedadesBean;

  @EJB
  private AccionesHelperLocal helperAccion;

  @Resource
  private SessionContext ctx;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
  private Recursos recursosEJB;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
  private UsuariosEmpresas empresasEJB;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/ComunicacionesBean!uy.gub.imm.sae.business.ejb.facade.ComunicacionesRemote")
  private Comunicaciones comunicacionesBean;

  @EJB
  private DisponibilidadesLocal disponibilidadEJB;
  
    @Resource(lookup = "java:jboss/infinispan/container/sae")
    private EmbeddedCacheManager saeCacheManager;
    
  static Logger logger = Logger.getLogger(AgendarReservasBean.class);
  
  
  /**
  * Retorna la agenda cuyo nombre sea <b>id</b> siempre y cuando esta viva (fechaBaja == null).
  * @throws ApplicationException 
  * @throws EntidadNoEncontradaException 
  * @throws ApplicationException 
  * @throws BusinessException 
  * @throws ParametroIncorrectoException 
  */
  public Agenda consultarAgendaPorId(Integer id) throws ApplicationException, BusinessException {
    if(id == null) {
      return null;
    }
    Agenda agenda = null;
    try {
      agenda = (Agenda) entityManager.createQuery("select a from Agenda a where a.id=:id and a.fechaBaja is null").setParameter("id", id).getSingleResult();
      return agenda;
    }catch(NonUniqueResultException nurEx) {
      throw new ApplicationException("no_se_encuentra_la_agenda_especificada");
    }catch(NoResultException nrEx) {
      throw new ApplicationException("no_se_encuentra_la_agenda_especificada");
    }
  }
  
  /**
  * Retorna el recurso cuyo que esté asociada a la reserva con el identificador <b>reservId</b> 
  * @throws ApplicationException 
  * @throws EntidadNoEncontradaException 
  * @throws ApplicationException 
  * @throws BusinessException 
  * @throws ParametroIncorrectoException 
  */
  public Recurso consultarRecursoPorReservaId(Integer reservId) throws ApplicationException, BusinessException {
    if(reservId == null) {
      return null;
    }
    Recurso recurso = null;
    try {
      recurso = (Recurso) entityManager
        .createQuery("select distinct d.recurso from Reserva r join r.disponibilidades d where r.id=:reservaId and r.estado ='R'")
        .setParameter("reservaId", reservId)
        .getSingleResult();
      return recurso;
    }catch(NonUniqueResultException nurEx) {
      throw new ApplicationException("no_se_encuentra_el_recurso_especificado");
    }catch(NoResultException nrEx) {
      return null;
    }
  }
  
  /**
   * Retorna el recurso cuyo id sea <b>id</b> siempre y cuando esta vivo (fechaBaja == null).
   * Controla que el usuario tenga rol FuncionarioAtencion sobre la agenda <b>a</b>
   * Roles permitidos: FuncionarioAtencion
   * @throws BusinessException 
   * @throws ApplicationException 
   * @throws BusinessException 
   * @throws ParametroIncorrectoException 
   */
  public Recurso consultarRecursoPorId(Agenda a, Integer id) throws ApplicationException, BusinessException  {
    if (a == null || id == null) {
      throw new BusinessException("-1","Parametros nulos");
    }
    a = consultarAgendaPorId(a.getId());
    if (a == null) {
      throw new BusinessException("-1","No se encuentra la agenda");
    }
    Recurso recurso = null;
    try {
      recurso = (Recurso) entityManager.createQuery("from Recurso r where r.id = :id and r.fechaBaja is null and r.agenda = :a")
        .setParameter("id", id)
        .setParameter("a", a)
        .getSingleResult();
    }catch (NoResultException e) {
      throw new BusinessException("-1", "No se encuentra el recurso de id "+id);
    }catch (Exception e) {
      throw new ApplicationException(e);
    }
    return recurso;   
  }
  
  /**
   * Retorna una lista de agendas vivas (fechaBaja == null) ordenada por nombre
   * Solo retorna las agendas para las que el usuario tenga rol FuncionarioAtencion.
   * Solo retorna las agendas tales que una posterior llamada a consultarRecursos retorne
   * por lo menos un recurso.
   * Roles permitidos: FuncionarioAtencion
   * @throws ApplicationException 
   * @throws ApplicationException 
   * @throws BusinessException 
   */
  @SuppressWarnings("unchecked")
  public List<Agenda> consultarAgendas() throws ApplicationException, BusinessException {
    List<Agenda> agendas = null;
    try {
      agendas = (List<Agenda>) entityManager.createQuery("from Agenda a where a.fechaBaja is null order by a.nombre")
          .getResultList();
    }catch (Exception e) {
      throw new ApplicationException(e);
    }
    List<Agenda> agendasValidas = new ArrayList<Agenda>();
    for (Agenda agenda : agendas) {
      if (consultarRecursos(agenda).size() != 0) {
        agendasValidas.add(agenda);
      }
    }
    return agendasValidas;
  }

  /**
   * Retorna una lista de recursos vivos (fechaBaja == null) ordenados por nombre
   * Solo retorna aquellos recursos tales que la fecha actual este comprendida entre
   * la fechaInicio y fechaFin del recurso. 
   * Controla que el usuario tenga rol FuncionarioAtencion sobre la agenda <b>a</b>
   * Roles permitidos: FuncionarioAtencion
   * @throws ApplicationException 
   * @throws BusinessException 
   */
  @SuppressWarnings("unchecked")
  public List<Recurso> consultarRecursos(Agenda a) throws ApplicationException, BusinessException {
    if (a == null) {
      throw new BusinessException("-1", "Parametro nulo");
    }
    a = entityManager.find(Agenda.class, a.getId());
    if (a == null) {
      throw new BusinessException("-1", "No se encuentra la agenda indicada");
    }
    List<Recurso> recursos = null;
    Date ahora = new Date();
    try {
      recursos = (List<Recurso>)entityManager.createQuery(
        "from Recurso r where " +
        "r.agenda = :a and " +
        "r.fechaBaja is null and  " +
        "r.fechaInicio <= :ahora and " +
        "(r.fechaFin is null or r.fechaFin >= :ahora)" +
        "order by r.descripcion")
        .setParameter("a", a)
        .setParameter("ahora", ahora, TemporalType.TIMESTAMP)
        .getResultList();
    }catch (Exception e) {
      throw new ApplicationException(e);
    }
    return recursos;
  }

  public void cancelarReserva(Empresa empresa, Recurso recurso, Reserva reserva) throws UserException {
    cancelarReserva(empresa, recurso, reserva, false);
  }
  
  public void cancelarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva) throws UserException {
    if(idEmpresa==null) {
      throw new UserException("debe_especificar_la_empresa");
    }
    if(idAgenda==null) {
      throw new UserException("debe_especificar_la_agenda");
    }
    if(idRecurso==null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    if(idReserva==null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    //Obtener la empresa
    Empresa empresa;
    try {
      empresa = empresasEJB.obtenerEmpresaPorId(idEmpresa);
      if(empresa==null) {
        throw new UserException("no_se_encuentra_la_empresa_especificada");
      }
    }catch(ApplicationException aEx) {
      throw new UserException("no_se_encuentra_la_empresa_especificada");
    }
    //Obtener la agenda
    Agenda agenda;
    try {
      agenda = consultarAgendaPorId(idAgenda);
      if(agenda==null) {
        throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
    }catch(ApplicationException | BusinessException ex) {
      throw new UserException("no_se_encuentra_la_agenda_especificada");
    }
    //Obtener la reserva
    Reserva reserva = (Reserva) entityManager.find(Reserva.class, idReserva);
    if(reserva==null) {
      throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
    }
    if(!Estado.R.equals(reserva.getEstado())) {
      throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
    }
    //Obtener el recurso
    Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
    //Cancelar la reserva
    cancelarReserva(empresa, recurso, reserva, false);
  }
  
  private void cancelarReserva(Empresa empresa, Recurso recurso, Reserva reserva, boolean masiva) throws UserException {
    if (recurso == null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    if (reserva == null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    Reserva r = entityManager.find(Reserva.class, reserva.getId());
    if (r == null) {
      throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
    }
    //Determinar el timezone de la reserva
    TimeZone timezone = TimeZone.getDefault();
    if(recurso.getAgenda().getTimezone()!=null && !recurso.getAgenda().getTimezone().isEmpty()) {
      timezone = TimeZone.getTimeZone(recurso.getAgenda().getTimezone());
    }else {
      if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
        timezone = TimeZone.getTimeZone(empresa.getTimezone());
      }
    }
    //Determinar si aún se está a tiempo de cancelar la reserva (no aplica si es masiva)
    if(!masiva) {
      Calendar horaLimiteTimezone = new GregorianCalendar();
      horaLimiteTimezone.setTime(reserva.getFechaHora());
      horaLimiteTimezone.add(recurso.getCancelacionUnidad(), -1*recurso.getCancelacionTiempo());
      Calendar horaActualTimezone = new GregorianCalendar();
      horaActualTimezone.add(Calendar.MILLISECOND, timezone.getOffset(horaActualTimezone.getTime().getTime()));
      if(!horaActualTimezone.before(horaLimiteTimezone)) {
        throw new UserException("ha_expirado_el_plazo_de_cancelacion");
      }
    } 
    ReservaDTO reservaDTO = new ReservaDTO();
    reservaDTO.setEstado(r.getEstado().toString());
    reservaDTO.setFecha(r.getDisponibilidades().get(0).getFecha());
    reservaDTO.setHoraInicio(r.getDisponibilidades().get(0).getHoraInicio());
    reservaDTO.setId(r.getId());
    reservaDTO.setOrigen(r.getOrigen());
    reservaDTO.setNumero(r.getDisponibilidades().get(0).getNumerador() + 1);
    reservaDTO.setTramiteCodigo(r.getTramiteCodigo());
    reservaDTO.setTramiteNombre(r.getTramiteNombre());
    reservaDTO.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase()); //el usuario de sesion es el que esta cancelando la reserva anterior y ese dato lo enviamos hacia GAP
    reservaDTO.setTcancela(masiva?TipoCancelacion.M.toString():TipoCancelacion.I.toString());
    reservaDTO.setFcancela(new Date());
    if (r.getLlamada() != null) {
      reservaDTO.setPuestoLlamada(r.getLlamada().getPuesto());
    }
    //Ejecuto acciones asociadas al evento cancelar
    Map<String, DatoReserva> valores = new HashMap<String, DatoReserva>();
    for (DatoReserva valor : reserva.getDatosReserva()) {
      valores.put(valor.getDatoASolicitar().getNombre(), valor);
    }
    try{
      helperAccion.ejecutarAccionesPorEvento(valores, reservaDTO, recurso, Evento.C);
    }catch (Exception ex){
      throw new UserException(ex.getMessage());
    }
    r.setEstado(Estado.C);
    r.setObservaciones(reserva.getObservaciones());
    r.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase());
    r.setTcancela(masiva?TipoCancelacion.M:TipoCancelacion.I);
    r.setFcancela(reservaDTO.getFcancela());
    //Determinar la fecha de liberación según la cancelación sea inmediata o diferida
    if(recurso.getCancelacionTipo()==null || FormaCancelacion.I.equals(recurso.getCancelacionTipo())) {
      //Es inmediata, el cupo se libera al instante
      r.setFechaLiberacion(r.getFcancela());
    }else {
      //Es diferida, se selecciona una fecha aleatoria
      Date fechaHoraAtencion = r.getDisponibilidades().get(0).getHoraInicio();
      
      
      //Determinar la fecha actual según el timezone de la reserva
      Calendar fechaHoraActual = new GregorianCalendar();
      fechaHoraActual.add(Calendar.MILLISECOND, timezone.getOffset(fechaHoraActual.getTime().getTime()));
      
      //Determinar la diferencia entre la fecha actual y la fecha de atención
      long difMilisegundos = fechaHoraAtencion.getTime() - fechaHoraActual.getTime().getTime();
      if(difMilisegundos<0) {
        //No debería pasar
        difMilisegundos = 0;
      }
      
      //Elegir un desplazamiento aleatorio
      long valorMinimo = Math.round(difMilisegundos * 0.1);
      long valorMaximo = Math.round(difMilisegundos * 0.8);
      long milisegundosAleatorios = ThreadLocalRandom.current().nextLong(valorMinimo, valorMaximo);
      if(milisegundosAleatorios>Integer.MAX_VALUE) {
        milisegundosAleatorios = Integer.MAX_VALUE;
      }
      //Calcular la fecha y hora de liberación como la hora actual más el desplazamiento
      Calendar fechaHoraLiberacion = new GregorianCalendar();
      fechaHoraLiberacion.add(Calendar.MILLISECOND, (int)milisegundosAleatorios);
      r.setFechaLiberacion(fechaHoraLiberacion.getTime());
    }
    //Registrar la cancelacion en el sistema de trazas del PEU
    String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), r.getTramiteCodigo(), r.getId());
    if(transaccionId != null) {
      trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.CANCELACION);
    }
    //Publicar la novedad
    novedadesBean.publicarNovedad(empresa, reserva, Acciones.CANCELACION);
  }

  /**
   * Libera el cupo de una reserva cancelada pero no liberada aún
   */
  public void liberarReserva(Integer idEmpresa, Integer idReserva) throws UserException {
    if(idEmpresa==null) {
      throw new UserException("debe_especificar_la_empresa");
    }
    if(idReserva==null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    //Obtener la empresa
    Empresa empresa;
    try {
      empresa = empresasEJB.obtenerEmpresaPorId(idEmpresa);
      if(empresa==null) {
        throw new UserException("no_se_encuentra_la_empresa_especificada");
      }
    }catch(ApplicationException aEx) {
      throw new UserException("no_se_encuentra_la_empresa_especificada");
    }
    //Obtener la reserva
    Reserva reserva = (Reserva) entityManager.find(Reserva.class, idReserva);
    if(reserva==null || !Estado.C.equals(reserva.getEstado())) {
      throw new UserException("no_se_encuentra_la_reserva_o_no_esta_cancelada");
    }
    if(reserva.getFechaLiberacion()==null || reserva.getFechaLiberacion().before(new Date())) {
      //Ya está liberada, no hay nada para hacer
      return;
    }
    reserva.setFechaLiberacion(new Date());
    entityManager.merge(reserva);
  }
  
  
  /**
   * Consulta una reserva por numero y para un recurso.
   * En caso contrario simplemente se busca la reserva por id y se la retorna (sin validar nada)
   */
  public Reserva consultarReservaPorId(Integer idReserva) throws UserException {
    if(idReserva==null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    Reserva reserva = entityManager.find(Reserva.class, idReserva);
    if (reserva==null) {
      throw new UserException("no_se_encuentra_la_reserva_especificada");
    }
    return reserva;
  }

  /**
   * Para controlar la existencia de cupo sin necesidad de utilizar bloqueo persiste la reserva y luego chequea que el cupo real no sea negativo, 
   * si esto sucede, elimina fisicamente la reserva y cancela la operacion.
   * @throws BusinessException 
   * @throws UserException 
   */
  public Reserva marcarReserva(Disponibilidad disponibilidad, TokenReserva token, String ipOrigen) throws UserException {
    if (disponibilidad == null) {
      throw new UserException("debe_especificar_la_disponibilidad");
    }
    disponibilidad = entityManager.find(Disponibilidad.class, disponibilidad.getId());
    if (disponibilidad == null) {
      throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
    }   
    //Se crea la reserva en una transaccion independiente
    Reserva reserva = helper.crearReservaPendiente(disponibilidad, token, ipOrigen);
    //Chequeo que el cupo real no de negativo
    //Si el cupo real da negativo, elimino la reserva pendiente y cancelo la operacion
    //De lo contrario la reserva se ha marcado con exito  
    if (helper.chequeoCupoDisponible(disponibilidad, true)) {
      try{
        helper.eliminarReservaPendiente(reserva);
      } catch (ApplicationException ex) {
              throw new UserException("error_marcar_reserva");
          }
          throw new UserException("el_horario_acaba_de_quedar_sin_cupos");
    }
    return reserva;
  }
    
  public Reserva marcarReservaValidandoDatos(Disponibilidad disponibilidad, Reserva reserva, TokenReserva token, String ipOrigen) throws UserException {
    if (disponibilidad == null) {
      throw new UserException("debe_especificar_la_disponibilidad");
    }
    disponibilidad = entityManager.find(Disponibilidad.class, disponibilidad.getId());
    if (disponibilidad == null) {
      throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
    }
    
    Recurso recurso = disponibilidad.getRecurso();
    List<DatoASolicitar> campos = helper.obtenerDatosASolicitar(recurso);
    Map<String, DatoReserva> valores = new HashMap<String, DatoReserva>();
    for (DatoReserva valor : reserva.getDatosReserva()) {
      valores.put(valor.getDatoASolicitar().getNombre(), valor);
    }
    //Se crea la reserva nueva en una transaccion independiente
    Reserva reservaNueva = helper.crearReservaPendiente(disponibilidad, token, ipOrigen);
    List<Reserva> reservasPrevias = helper.validarDatosReservaPorClave(recurso, disponibilidad, campos, valores, reserva.getTramiteCodigo(),null);
    //Si hay reservas repetidas y no son la misma que la pasada por parámetro se lanza una excepcion.
    if (!reservasPrevias.isEmpty()) {
      for(Reserva reservaPrevia : reservasPrevias) {
        if(!reservaPrevia.getId().equals(reserva.getId())) {
          //Son dos reservas que coinciden en los datos obligatorios pero no son la misma
          List<String> nombreCamposClave = new ArrayList<String>();
          for (DatoASolicitar campo : campos) {
            if (campo.getEsClave() ) {
              nombreCamposClave.add(campo.getNombre());
            }
          }
          reservaNueva = entityManager.find(Reserva.class, reservaNueva.getId()); 
          entityManager.remove(reservaNueva);
          entityManager.flush();
          throw new ValidacionClaveUnicaException("no_es_posible_confirmar_su_reserva", nombreCamposClave);
        }
      }
    }
    //Validaciones por IP (si el recurso lo exige y la reserva incluye la IP; no incluye la IP si es desde el backend o autogestión)
    if(recurso.getValidarPorIP()!=null && recurso.getValidarPorIP()==true && ipOrigen!=null) {
      //Determinar si la ip es una de las excluidas de la validación
      boolean validar = true;
      if(recurso.getIpsSinValidacion()!=null) {
        String ips[] = recurso.getIpsSinValidacion().split(";");
        for(String ip : ips) {
          if(ip.trim().equals(reserva.getIpOrigen())) {
            validar = false;
            break;
          }
        }
      }
      if(validar) {
        int cantidadReservasIP = helper.cantidadReservasPorIp(recurso, ipOrigen, reserva.getId());
        if(cantidadReservasIP >= recurso.getCantidadPorIP()) {
          throw new ValidacionIPException("limite_de_reservas_para_la_direccion_ip_alcanzado");
        }
      }
    }
    //Chequear que el cupo real no de negativo
    //Si el cupo real da negativo, se elimina la reserva pendiente y cancela la operación
    //De lo contrario la reserva se ha marcado con éxito
    if (helper.chequeoCupoDisponible(disponibilidad, true)) {
      reservaNueva = entityManager.find(Reserva.class, reservaNueva.getId());
      entityManager.remove(reservaNueva);
      entityManager.flush();
      throw new UserException("el_horario_acaba_de_quedar_sin_cupos");
    }
    return reservaNueva;
  }
  
  /**
   * Elimina fisicamente una reserva marcada como pendiente.
   */
  public void desmarcarReserva(Reserva r) throws BusinessException {
    if (r == null) {
      throw new BusinessException("-1", "Parametro nulo");
    }
    r = entityManager.find(Reserva.class, r.getId());
    if (r == null) {
      throw new BusinessException("-1", "No se encuentra la reserva indicada");
    }   
    if (r.getEstado() != Estado.P) {
      throw new BusinessException("-1", "Solo se puede desmarcar reservas en estado pendiente");
    }
    entityManager.remove(r);
  }
  
  public void validarDatosReserva(Empresa empresa, Reserva reserva) throws BusinessException, ValidacionException, ApplicationException {
    if (reserva == null || reserva.getDatosReserva()==null) {
      throw new BusinessException("debe_especificar_la_reserva");
    }
    Set<DatoReserva> datosNuevos = reserva.getDatosReserva();
    reserva = entityManager.find(Reserva.class, reserva.getId());
    if (reserva == null) {
      throw new BusinessException("no_se_encuentra_la_reserva_especificada");
    }
    if (reserva.getEstado() == Estado.U) {
      throw new BusinessException("no_es_posible_confirmar_su_reserva");
    }
    if (reserva.getEstado() != Estado.P) {
      throw new BusinessException("no_es_posible_confirmar_su_reserva");
    }
    //Armo las estructuras de Map necesarias para poder ejecutar las validaciones sobre los datos de la reserva
    Disponibilidad disponibilidad = reserva.getDisponibilidades().get(0);
    Recurso recurso = disponibilidad.getRecurso();
    List<DatoASolicitar> campos = helper.obtenerDatosASolicitar(recurso);
    Map<String, DatoReserva> valores = new HashMap<String, DatoReserva>();
    for (DatoReserva valor : datosNuevos) {
      valores.put(valor.getDatoASolicitar().getNombre(), valor);
    }
    //Validacion basica: campos obligatorios y formato
    helper.validarDatosReservaBasico(campos, valores);
    //Validacion por campos clave: no puede haber otra reserva con los mismos datos
    List<Reserva> reservasPrevias = helper.validarDatosReservaPorClave(recurso, disponibilidad, campos, valores, reserva.getTramiteCodigo(),reserva.getId());
    if (!reservasPrevias.isEmpty()) {
      List<String> nombreCamposClave = new ArrayList<String>();
      for (DatoASolicitar campo : campos) {
        if (campo.getEsClave() ) {
          nombreCamposClave.add(campo.getNombre());
        }
      }
      throw new ValidacionClaveUnicaException("no_es_posible_confirmar_su_reserva", nombreCamposClave);     
    }
    //Validaciones por IP (si el recurso lo exige y la reserva incluye la IP; no incluye la IP si es desde el backend o autogestión)
    if(recurso.getValidarPorIP()!=null && recurso.getValidarPorIP()==true && reserva.getIpOrigen()!=null) {
      //Determinar si la ip es una de las excluidas de la validación
      boolean validar = true;
      if(recurso.getIpsSinValidacion()!=null) {
        String ips[] = recurso.getIpsSinValidacion().split(";");
        for(String ip : ips) {
          if(ip.trim().equals(reserva.getIpOrigen())) {
            validar = false;
            break;
          }
        }
      }
      if(validar) {
        int cantidadReservasIP = helper.cantidadReservasPorIp(recurso, reserva.getIpOrigen(), reserva.getId());
        if(cantidadReservasIP >= recurso.getCantidadPorIP()) {
          throw new ValidacionIPException("limite_de_reservas_para_la_direccion_ip_alcanzado");
        }
      }
    }
    //Validaciones extendidas: definidas por el usuario
    List<ValidacionPorRecurso> validaciones = helper.obtenerValidacionesPorRecurso(recurso);
    ReservaDTO reservaDTO = new ReservaDTO();
    reservaDTO.setEstado(reserva.getEstado().toString());
    reservaDTO.setFecha(reserva.getDisponibilidades().get(0).getFecha());
    reservaDTO.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
    reservaDTO.setId(reserva.getId());
    if (ctx.isCallerInRole("RA_AE_FCALL_CENTER") || ctx.isCallerInRole("RA_AE_ADMINISTRADOR_DE_RECURSOS")){
      reservaDTO.setOrigen("C"); //Call center
    }else if (ctx.isCallerInRole("RA_AE_ANONIMO")){
      reservaDTO.setOrigen("W"); //Web
    }else{
      reservaDTO.setOrigen("I"); //Otro
    }
    reservaDTO.setTramiteCodigo(reserva.getTramiteCodigo());
    reservaDTO.setTramiteNombre(reserva.getTramiteNombre());
    reservaDTO.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
    reservaDTO.setNumero(reserva.getDisponibilidades().get(0).getNumerador() + 1);
    if (reserva.getLlamada() != null) {
      reservaDTO.setPuestoLlamada(reserva.getLlamada().getPuesto());
    }
    //Ejecucion de los validadores personalizados.
    helper.validarDatosReservaExtendido(validaciones, campos, valores, reservaDTO);
  }

  /**
   * Confirma la reserva.
   * Solo aplica si la reserva existe y está en el estado P (Pendiente).
   * Si la reserva debe tener datos, estos son exigidos y validados en este metodo, incluyendo la verificacion de clave unica
   * Si se trata de una reserva individual (el token es nulo) queda en estado Reservada (confirmada); si se trata de una reserva 
   *   perteneciente a un bloque de reservas múltiples permanece en estado pendiente
   * @throws ApplicationException 
   * @throws BusinessException
   * @return Reserva: Devuelve la reserva pues se le ha asignado un numero unico de reserva dentro de la hora a la que pertenece. 
   * @throws UserException
   * @throws ValidacionException 
   */
  public Reserva confirmarReserva(Empresa empresa, Reserva reserva, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) 
    throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException {
    if (reserva == null || reserva.getDatosReserva()==null) {
      throw new BusinessException("debe_especificar_la_reserva");
    }
    //Esto se lee antes de recuperar la reserva de la base porque puede haber cambiado
    Set<DatoReserva> datosNuevos = reserva.getDatosReserva();
    String tramiteCodigo = reserva.getTramiteCodigo();
    String tramiteNombre = reserva.getTramiteNombre();
    //Si no se cargó el nombre se busca ahora
    if(tramiteNombre == null && tramiteCodigo!=null) {
      TramiteAgenda tramite = consultarTramitePorCodigo(reserva.getDisponibilidades().get(0).getRecurso().getAgenda(), tramiteCodigo);
      if(tramite != null) {
        tramiteNombre = tramite.getTramiteNombre();
      }
    }
    //Volver a cargar la reserva para evitar conflicto de versiones
    reserva = entityManager.find(Reserva.class, reserva.getId());
    if (reserva == null) {
      throw new UserException("no_se_encuentra_la_reserva_especificada");
    }
    if (reserva.getEstado() != Estado.P) {
      throw new UserException("no_es_posible_confirmar_su_reserva");
    }
    //Pisar estos datos con lo que vino de la interfaz
    for(DatoReserva dato : datosNuevos) {
      reserva.getDatosReserva().add(dato);
    }
    reserva.setTramiteCodigo(tramiteCodigo);
    reserva.setTramiteNombre(tramiteNombre);
    //Ajustar el número de documento si es una cédula (dejar solo dígitos)
    String tipoDocumento = null;
    String numeroDocumento = null;
    for(DatoReserva datoReserva : reserva.getDatosReserva()) {
      if(DatoASolicitar.NUMERO_DOCUMENTO.equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre()))  {
        numeroDocumento = datoReserva.getValor();
      }
      if(DatoASolicitar.TIPO_DOCUMENTO.equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre()))  {
        tipoDocumento = datoReserva.getValor();
      }
    }
    if("CI".equalsIgnoreCase(tipoDocumento) && numeroDocumento!=null) {
      numeroDocumento = numeroDocumento.replaceAll("[.-]", "");
      for(DatoReserva datoReserva : reserva.getDatosReserva()) {
        if(DatoASolicitar.NUMERO_DOCUMENTO.equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre()))  {
          datoReserva.setValor(numeroDocumento);
        }
      }
    }
    //Validar los datos de la reserva: campos obligatorios, campos clave duplicados, validaciones extendidas 
    validarDatosReserva(empresa, reserva);
    //Pasó las validaciones
    Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
    List<DatoASolicitar> campos = helper.obtenerDatosASolicitar(recurso);
    Map<String, DatoASolicitar> camposMap = new HashMap<String, DatoASolicitar>();
    for (DatoASolicitar datoASolicitar : campos) {
      camposMap.put(datoASolicitar.getNombre(), datoASolicitar);
    }
    Map<String, DatoReserva> valores = new HashMap<String, DatoReserva>();
    for (DatoReserva valor : datosNuevos) {
      valores.put(valor.getDatoASolicitar().getNombre(), valor);
    }
    for (DatoReserva dato: valores.values()) {
      DatoASolicitar campo = camposMap.get(dato.getDatoASolicitar().getNombre());
      DatoReserva datoNuevo = new DatoReserva();
      datoNuevo.setValor(dato.getValor());
      datoNuevo.setReserva(reserva);
      datoNuevo.setDatoASolicitar(campo);
      entityManager.persist(datoNuevo);
      reserva.getDatosReserva().add(datoNuevo);
    }
    //Confirmo la reserva, paso el estado a Reservada (si no es múltiple) y le asigno el numero de reserva dentro de la disponibilidad.
    //Con mutua exclusion en el acceso al numerador de la disponibilidad
    Disponibilidad disponibilidad = reserva.getDisponibilidades().get(0);
    try {
      disponibilidad.setNumerador(disponibilidad.getNumerador()+1);
      entityManager.flush();
    } catch(OptimisticLockException olEx){
      throw new AccesoMultipleException("error_de_acceso_concurrente");
    }
    String origen;
    if (ctx.isCallerInRole("RA_AE_FCALL_CENTER") || ctx.isCallerInRole("RA_AE_ADMINISTRADOR_DE_RECURSOS")){
      origen = "C"; //Call Center
    }else if (ctx.isCallerInRole("RA_AE_ANONIMO")){
      origen = "W"; //Web
    }else{
      origen = "I"; //Otro
    }
    //Ejecutar las acciones asociadas al evento reservar
    try{
      ReservaDTO reservaDTO = new ReservaDTO();
      reservaDTO.setEstado(reserva.getEstado().toString());
      reservaDTO.setFecha(reserva.getDisponibilidades().get(0).getFecha());
      reservaDTO.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
      reservaDTO.setId(reserva.getId());
      reservaDTO.setOrigen(origen);
      reservaDTO.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
      reservaDTO.setNumero(reserva.getDisponibilidades().get(0).getNumerador() + 1);
      reservaDTO.setTramiteCodigo(reserva.getTramiteCodigo());
      reservaDTO.setTramiteNombre(reserva.getTramiteNombre());
      if (reserva.getLlamada() != null) {
        reservaDTO.setPuestoLlamada(reserva.getLlamada().getPuesto());
      }
      helperAccion.ejecutarAccionesPorEvento(valores, reservaDTO, recurso, Evento.R);
    }catch (ErrorAccionException eaEx){
      throw new BusinessException(eaEx.getCodigoError(), eaEx.getMensajes().toString());
    }
    if(reserva.getToken() == null) {
      reserva.setEstado(Estado.R);
    }
    reserva.setSerie(recurso.getSerie());
    reserva.setNumero(disponibilidad.getNumerador());
    reserva.setOrigen(origen);
    reserva.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
    //Asignar un codigo de seguridad
    String codigoSeguridad = ""+(new Date()).getTime();
    if(codigoSeguridad.length()>5) {
      codigoSeguridad = codigoSeguridad.substring(codigoSeguridad.length()-5);
    }
    reserva.setCodigoSeguridad(codigoSeguridad);
    //Si no es parte de una reserva múltiple se registra en el sistema de trazabilidad y en el de novedades
    if(reserva.getToken() == null) {
      //Registrar en el sistema de trazas
      String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), reserva.getTramiteCodigo(), reserva.getId());
      if(empresa.getOid() != null && transaccionId != null) {
        //Registrar el cabezal en el sistema de trazabilidad del PEU
        String trazaGuid = trazaBean.registrarCabezal(empresa, reserva, transaccionId, reserva.getTramiteCodigo(), inicioAsistido, transaccionPadreId, pasoPadre);
        if(trazaGuid != null) {
          reserva.setTrazabilidadGuid(trazaGuid);
        }else {
          reserva.setTrazabilidadGuid("---");
        }
        //Registrar la primera linea en el sistema de trazabilidad del PEU
        //ToDo: esto habria que hacerlo solo si pudo invocar el cabezal; en otro caso solo habría que guardar la invocación
        //en la base de datos para futuros intentos
        trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.RESERVA);
        //Publicar la novedad
        novedadesBean.publicarNovedad(empresa, reserva, Acciones.RESERVA);
      }
    }
    return reserva;
  }

  public List<Disponibilidad> obtenerDisponibilidades(Recurso recurso, VentanaDeTiempo ventana, TimeZone timezone) throws UserException {
    return obtenerDisponibilidades(recurso, ventana, timezone, true);
  }
  
  @SuppressWarnings("unchecked")
  public List<Disponibilidad> obtenerDisponibilidades(Recurso recurso, VentanaDeTiempo ventana, TimeZone timezone, boolean ajustarVentanaSegunAhora) throws UserException {
    if (recurso == null) {
      throw new UserException("debe_especificar_el_recurso");
    }
      if (ventana == null) {
        throw new UserException("debe_especificar_la ventana");
      }
    recurso = entityManager.find(Recurso.class, recurso.getId());
    if (recurso == null) {
      throw new UserException("no_se_encuentra_el_recurso_especificado");
    }
    //Ajustar la hora segun el timezone
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTime().getTime()));
    Date ahora = cal.getTime();
    if (ajustarVentanaSegunAhora && ventana.getFechaInicial().before(ahora)) {
      ventana.setFechaInicial(ahora);
    }
    if (ventana.getFechaInicial().before(recurso.getFechaInicioDisp())) {
      ventana.setFechaInicial(recurso.getFechaInicioDisp());
    }
    //Determinar todas las disponibilidades
    //No se debe considerar las disponibilidades presenciales
    String eql = "SELECT d " +
        "FROM Disponibilidad d " +
        "WHERE d.recurso IS NOT NULL " +
        "  AND d.presencial = false " +
        "  AND d.recurso = :r " +
        "  AND d.fechaBaja IS NULL " +
        "  AND d.horaInicio >= :fi " +
        "  AND d.horaFin <= :ff " +
        "ORDER BY d.fecha ASC, d.horaInicio ";
    List<Disponibilidad> disponibilidades =  entityManager.createQuery(eql)
    .setParameter("r", recurso)
    .setParameter("fi", ventana.getFechaInicial(), TemporalType.TIMESTAMP)
    .setParameter("ff", ventana.getFechaFinal(), TemporalType.TIMESTAMP)
    .getResultList();
    
    
    //Determinar las reservas vivas
    //No se debe considerar las disponibilidades presenciales
    //Para las reservas canceladas hay que ver si ya se liberó el cupo o aún no
    String cons = "SELECT d.id, d.fecha, d.horaInicio, COUNT(r) " +
        "FROM Disponibilidad d " +
        "JOIN d.reservas r " +
        "WHERE d.recurso IS NOT NULL " +
        "  AND d.recurso = :recurso " +
        "  AND d.fechaBaja IS NULL " +
        "  AND d.horaInicio >= :finicio " +
        "  AND d.horaFin <= :ffin " +
        "  AND (d.fecha <> :finicio OR d.horaInicio >= :fiCompleta) " +
        "  AND (r.estado <> :cancelado OR r.fechaLiberacion>=:ahora) " +
        "GROUP BY d.id, d.fecha, d.horaInicio " +
        "ORDER BY d.fecha asc, d.horaInicio ASC ";
    List<Object[]> cantReservasVivas =  entityManager.createQuery(cons)
    .setParameter("recurso", recurso)
    .setParameter("finicio", ventana.getFechaInicial(), TemporalType.TIMESTAMP)
    .setParameter("ffin", ventana.getFechaFinal(), TemporalType.TIMESTAMP)
    .setParameter("fiCompleta", ventana.getFechaInicial(), TemporalType.TIMESTAMP)
    .setParameter("cancelado", Estado.C)
    .setParameter("ahora", new Date(), TemporalType.TIMESTAMP)
    .getResultList();
    //Quitar de las disponibilidades las reservas vivas
    Map<Integer, Integer> cantResVivasPorDispon = new HashMap<Integer, Integer>();
    for(Object[] oo : cantReservasVivas) {
      cantResVivasPorDispon.put((Integer)oo[0], ((Long)oo[3]).intValue());
    }
    
    List<Disponibilidad> disp = new ArrayList<Disponibilidad>();
    for (Disponibilidad d : disponibilidades) {
      Integer cant =  cantResVivasPorDispon.get(d.getId());
      if(cant == null) {
        cant = 0;
      }
      Disponibilidad dto = new Disponibilidad();
      dto.setId(d.getId());
      dto.setFecha(d.getFecha());
      dto.setHoraInicio(d.getHoraInicio());
      dto.setHoraFin(dto.getHoraFin());
      dto.setRecurso(null);
      dto.setCupo(d.getCupo() - cant);

      disp.add(dto);
    }
    return disp;
  }


  /**
   * Devuelve las fechas limites para las cuales se tienen disponibilidades, 
   * cumpliendo con los periodos para realizar reservas indicado en el recurso, es decir:
   * El inicio del calendario es el primer dia que tenga asigando disponibilidades, 
   * que este dentro del periodo de vigencia de las disponibilidades indicado en el recurso 
   * y sea mayor o igual al dia actual.
   * El fin del calendario es el ultimo dia que tenga asignado disponibilidades,
   * que este dentro del periodo de vigencia de las disponibilidades indicado en el recurso 
   * y tratando de satisfacer que:
   *        La diferencia entre fin - inicio sea mayor o igual a ventanaDiasMinimos.
   *          Los cupos disponibles en la ventana sea mayor o igual a ventanaCuposMinimos. Si 
   *          esto no fuera posible se debe agrandar la ventana hasta llegar
   *          a la ultima disponibilidad sin pasarse del periodo de fin de disponibilidades.
   *          
   * En este caso la ventana de tiempo retornada representa dias, por lo cual, la hora del dia
   * esta asignada a las 00:00:00.
   *  
   * @param recurso
   * @return
   * @throws BusinessException 
   * @throws RolException 
   */
  public VentanaDeTiempo obtenerVentanaCalendarioIntranet(Recurso recurso) throws UserException {
    if (recurso == null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    recurso = entityManager.find(Recurso.class, recurso.getId());
    if (recurso == null) {
      throw new UserException("no_se_encuentra_el_recurso_especificado");
    }
    VentanaDeTiempo ventanaEstatica = helper.obtenerVentanaCalendarioEstaticaIntranet(recurso);
    return ventanaEstatica;
  }

  /**
   * Devuelve las fechas limites para las cuales se tienen disponibilidades, 
   * cumpliendo con los periodos para realizar reservas indicado en el recurso, es decir:
   * El inicio del calendario es el primer dia que tenga asigando disponibilidades, 
   * que este dentro del periodo de vigencia de las disponibilidades indicado en el recurso 
   * y sea mayor o igual al dia actual.
   * El fin del calendario es el ultimo dia que tenga asignado disponibilidades,
   * que este dentro del periodo de vigencia de las disponibilidades indicado en el recurso 
   * y tratando de satisfacer que:
   *        La diferencia entre fin - inicio sea mayor o igual a ventanaDiasMinimos.
   *          Los cupos disponibles en la ventana sea mayor o igual a ventanaCuposMinimos. Si 
   *          esto no fuera posible se debe agrandar la ventana hasta llegar
   *          a la ultima disponibilidad sin pasarse del periodo de fin de disponibilidades.
   *          
   * En este caso la ventana de tiempo retornada representa dias, por lo cual, la hora del dia
   * esta asignada a las 00:00:00.
   *  
   * @param recurso
   * @return
   * @throws BusinessException 
   * @throws RolException 
   */
  public VentanaDeTiempo obtenerVentanaCalendarioInternet(Recurso recurso) throws UserException {
    if (recurso == null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    recurso = entityManager.find(Recurso.class, recurso.getId());
    if (recurso == null) {
      throw new UserException("no_se_encuentra_el_recurso_especificado");
    }
    VentanaDeTiempo ventanaEstatica = helper.obtenerVentanaCalendarioEstaticaInternet(recurso);
    return ventanaEstatica;
  }

  /**
   * Devuelve una lista de enteros indicando los cupos disponibles, por dia, asignados al recurso. 
   * Es decir, para cada dia de la ventana de tiempo, devuelve la cantidad de cupos 
   * disponibles en ese dia para el recurso dado.
   * 
   * Si el dia de inicio de la ventana es "hoy", solo se toman en cuenta los cupos a partir de "ahora",
   * es decir, las disponibilidades cuya horaInicio sea mayor al tiempo actual.
   * Para los dias de la ventana que no tengan disponibilidad o sean dias del pasado ( < "hoy"), 
   * se devolvera un valor negativo indicando que ese dia es no-agendable.
   * Para los dias de la ventana que no les queden cupos libres, se devolvera 0. 
   * @param recurso
   * @param ventana
   * @return 
   */
  @RolesAllowed({"RA_AE_ADMINISTRADOR","RA_AE_FCALL_CENTER", "RA_AE_PLANIFICADOR","RA_AE_FATENCION", "RA_AE_ANONIMO", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
  public List<Integer> obtenerCuposPorDia(Recurso recurso, VentanaDeTiempo ventana, TimeZone timezone) throws UserException {
    if (recurso == null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    if (ventana == null) {
      throw new UserException("debe_especificar_la ventana");
    }
    recurso = entityManager.find(Recurso.class, recurso.getId());
    if (recurso == null) {
      throw new UserException("no_se_encuentra_el_recurso_especificado");
    }
    //Obtener los cupos para cada dia y hora
    List<Object[]> cuposAsignados  = helper.obtenerCuposAsignados(recurso, ventana, timezone);
    //Obtengo las reservas vivas para cada día y hora
    List<Object[]> cuposConsumidos = helper.obtenerCuposConsumidos(recurso,ventana, timezone);
    //Armar la lista de resultados indicando los cupos para todos los dias solicitados en la ventana
    List<Integer> cuposXdia = helper.obtenerCuposXDia(ventana, cuposAsignados, cuposConsumidos);
    return cuposXdia;
  }
  
  /**
   * Completa campos de la reserva.
   * Los datos necesarios para el servicio son exigidos en el mismo
   * @throws ApplicationException 
   * @throws BusinessException
   * @throws UserException
   * @throws AutocompletarException 
   * @return Map<String, Object>: devuelve un Map con los valores que se desea asignar a cada campo. 
   */
  public Map<String, Object> autocompletarCampo(ServicioPorRecurso s, Map<String, Object> datosParam) 
    throws ApplicationException, BusinessException, AutocompletarException, UserException {
    if (s == null) {
      throw new BusinessException("-1", "Parametro nulo");
    }
    s = entityManager.find(ServicioPorRecurso.class, s.getId());
    if (s == null) {
      throw new BusinessException("-1", "No se encuentra el servicio indicado");
    }
    s.getAutocompletadosPorDato().size();
    s.getAutocompletado().getParametrosAutocompletados().size();
    Map<String, Object> campos = helper.autocompletarCampo(s,datosParam);
    return campos;
  }

  @Override
  public Empresa obtenerEmpresaPorId(Integer empresaId) throws ApplicationException {
    if(empresaId == null) {
      return null;
    }
    try {
      Empresa empresa = globalEntityManager.find(Empresa.class, empresaId);
      return empresa;
    }catch(NonUniqueResultException nurEx) {
      throw new ApplicationException("no_se_encuentra_la_empresa_especificada");
    }catch(NoResultException nrEx) {
      throw new ApplicationException("no_se_encuentra_la_empresa_especificada");
    }
  }

/*  
  public void enviarComunicacionesConfirmacion(Empresa empresa, String linkCancelacion, String linkModificacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws UserException {
    //Enviar mail
    String cuerpo = enviarMailConfirmacion(linkCancelacion, linkModificacion, reserva, idioma, formatoFecha, formatoHora);
    boolean mailOk = (cuerpo!=null);
    //Almacenar datos para SMS y TextoAVoz, si corresponde
    almacenarSmsYTav(Comunicacion.Tipo2.RESERVA, reserva, formatoFecha, formatoHora);
    //Enviar aviso a MiPerfil
    boolean mipOk = enviarAvisoMiPerfil(empresa, reserva, Comunicacion.Tipo2.RESERVA);
    if(!mailOk && !mipOk) {
      throw new UserException("no_se_pudo_enviar_notificacion_de_confirmacion_tome_nota_de_los_datos_de_la_reserva");
    }
  }
*/  
  /**
   * Este método es similares a enviarComunicacionesConfirmacion pero aplica a un TokenReserva que contenga múltiples reservas en lugar de a una sola.
   */
/*  
  public void enviarComunicacionesConfirmacion(Empresa empresa, String templateLinkCancelacion, String templateLinkModificacion, TokenReserva tokenReserva, String idioma, String formatoFecha, String formatoHora) throws UserException {
    List<Reserva> reservas = obtenerReservasMultiples(tokenReserva.getId(), false);
    StringBuilder cuerpos = new StringBuilder();
    //Para cada reserva enviar la comunicación y añadir el cuerpo al texto que se enviará a la persona que realiza las reservas
    for(Reserva reserva : reservas) {
      if(reserva.getEstado().equals(Estado.R)) {
        //Enviar mail
        String linkCancelacion = templateLinkCancelacion.replace("{idReserva}", reserva.getId().toString());
        String linkModificacion = templateLinkModificacion.replace("{idReserva}", reserva.getId().toString());
        String cuerpo;
        try {
          cuerpo = enviarMailConfirmacion(linkCancelacion, linkModificacion, reserva, idioma, formatoFecha, formatoHora);
        }catch(Exception ex) {
          cuerpo = null;
        }
        if(cuerpo == null) {
          cuerpo = "Reserva id {idReserva} confirmada.".replace("{idReserva}", reserva.getId().toString());
        }
        cuerpos.append("<br />Documento: "+reserva.getTipoDocumento()+" "+reserva.getNumeroDocumento()+"<br /><br />" + cuerpo);
        cuerpos.append("<br />*************************<br />");
        //Almacenar datos para SMS y TextoAVoz, si corresponde
        almacenarSmsYTav(Comunicacion.Tipo2.RESERVA, reserva, formatoFecha, formatoHora);
        //Enviar aviso a MiPerfil
        enviarAvisoMiPerfil(empresa, reserva, Comunicacion.Tipo2.RESERVA);
      }
    }
    //Enviar las comunicaciones a la persona que realiza las reservas
    if(tokenReserva.getCorreoe()!=null) {
      try {
        String cuerpo = cuerpos.toString();
        MailUtiles.enviarMail(tokenReserva.getCorreoe(), "Confirmación de reserva múltiple", cuerpo, MailUtiles.CONTENT_TYPE_HTML);
        //No se guarda el cuerpo porque podría ser muy grande si hay muchas reservas, y puede verse en el envío de cada reserva individual
        Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVAMULTIPLE, tokenReserva.getCorreoe(), tokenReserva.getRecurso(), tokenReserva, "");
        comunicacion.setProcesado(true);
        entityManager.persist(comunicacion);
      }catch(MessagingException mEx) {
        try {
          Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVAMULTIPLE, tokenReserva.getCorreoe(), tokenReserva.getRecurso(), tokenReserva, "");
          entityManager.persist(comunicacion);
        }catch(Exception ex) {
          logger.error("No se pudo enviar una comunicación por confirmación de reserva múltiple", ex);
          ex.printStackTrace();
        }
      }
    }
  }
*/
  
/*  
  public void enviarComunicacionesCancelacion(Empresa empresa, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws UserException {
    enviarComunicacionesCancelacion(empresa, reserva, idioma, formatoFecha, formatoHora, null, null);
  }
*/
  
/*  
  private void enviarComunicacionesCancelacion(Empresa empresa, Reserva reserva, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) throws UserException {
    //Enviar mail
    boolean mailOk = enviarMailCancelacion(reserva, idioma, formatoFecha, formatoHora, asunto, cuerpo);
    //Almacenar datos para SMS y TextoAVoz, si corresponde
    almacenarSmsYTav(Comunicacion.Tipo2.CANCELA, reserva, formatoFecha, formatoHora);
    //Enviar aviso a MiPerfil
    boolean mipOk = enviarAvisoMiPerfil(empresa, reserva, Comunicacion.Tipo2.CANCELA);
    //Si no se pudo enviar mail ni aviso a mi perfil lanzar una excepción
    if(!mailOk && !mipOk) {
      throw new UserException("no_se_pudo_enviar_notificacion_de_cancelacion");
    }
  }
*/
  
/*  
  private String enviarMailConfirmacion(String linkCancelacion, String linkModificacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) {
    //Se envía el mail obligatorio al usuario
    Recurso recurso = null;
    String email = null;
    try {
      recurso = reserva.getDisponibilidades().get(0).getRecurso();
      Agenda agenda = recurso.getAgenda();
      TextoAgenda textoAgenda = null;
      if(agenda.getTextosAgenda()!=null) {
        textoAgenda = agenda.getTextosAgenda().get(idioma);
      }
      if(textoAgenda == null) {
        for(TextoAgenda ta : agenda.getTextosAgenda().values()) {
          if(ta.isPorDefecto()) {
            textoAgenda = ta;
          }
        }
      }
      if(textoAgenda == null) {
        textoAgenda = new TextoAgenda();
      }
      //Obtener el mail del usuario, que es el dato a solicitar llamado "Mail" en la agrupacion que no se puede borrar
      String cuerpo = null;
      cuerpo = textoAgenda.getTextoCorreoConf();
      if(cuerpo != null && !cuerpo.isEmpty()) {
        cuerpo = Metavariables.remplazarMetavariables(cuerpo, reserva, formatoFecha, formatoHora, linkCancelacion, linkModificacion);
        for(DatoReserva dato : reserva.getDatosReserva()) {
          DatoASolicitar datoSol = dato.getDatoASolicitar();
          if(DatoASolicitar.CORREO_ELECTRONICO.equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
            email = dato.getValor();
            MailUtiles.enviarMail(email, "Confirmación de reserva", cuerpo, MailUtiles.CONTENT_TYPE_HTML);
            Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, cuerpo);
            comunicacion.setProcesado(true);
            entityManager.persist(comunicacion);
          }
        }
        return cuerpo;
      }else {
        //Indicar que no se pudo enviar el mail
        return null;
      }
    }catch(MessagingException mEx) {
      logger.warn("No se pudo enviar el mail de confirmación", mEx);
      try {
        if(recurso != null) {
          if(email == null) {
            email = "***";
          }
          Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, null);
          entityManager.persist(comunicacion);
        }
      }catch(Exception ex) {
        //
      }
      //Indicar que no se pudo enviar el mail
      return null;
    }
  }
*/
  
/*  
  private boolean enviarMailCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) {
    //Se envía el mail obligatorio al usuario
    Recurso recurso = null;
    try {
      recurso = reserva.getDisponibilidades().get(0).getRecurso();
      if(cuerpo == null) {
        Agenda agenda = recurso.getAgenda();
        TextoAgenda textoAgenda = null;
        if(agenda.getTextosAgenda()!=null) {
          textoAgenda = agenda.getTextosAgenda().get(idioma);
        }
        if(textoAgenda == null) {
          for(TextoAgenda ta : agenda.getTextosAgenda().values()) {
            if(ta.isPorDefecto()) {
              textoAgenda = ta;
            }
          }
        }
        if(textoAgenda != null) {
          cuerpo = textoAgenda.getTextoCorreoCanc();
        }
      }
      if(asunto == null) {
        asunto = "Cancelación de reserva";
      }
      //Obtener el mail del usuario, que es el dato a solicitar llamado "Mail" en la agrupacion que no se puede borrar
      String emailTo = null;
      for(DatoReserva dato : reserva.getDatosReserva()) {
        DatoASolicitar datoSol = dato.getDatoASolicitar();
        if(DatoASolicitar.CORREO_ELECTRONICO.equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
          emailTo = dato.getValor();
        }
      }
      if(emailTo != null) {
        if(cuerpo != null && !cuerpo.isEmpty()) {
          cuerpo = Metavariables.remplazarMetavariables(cuerpo, reserva, formatoFecha, formatoHora, "", "");
          MailUtiles.enviarMail(emailTo, asunto, cuerpo, MailUtiles.CONTENT_TYPE_HTML);
        }
        Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.CANCELA, emailTo, recurso, reserva, asunto+"/"+cuerpo);
        comunicacion.setProcesado(true);
        entityManager.persist(comunicacion);
      }else {
        //Indicar que no se pudo enviar el mail
        return false;
      }
      //Indicar que se pudo enviar el mail
      return true;
    }catch(MessagingException mEx) {
      //Indicar que no se pudo enviar el mail
      logger.warn("No se pudo enviar el mail de cancelación", mEx);
      return false;
    }
  }
*/
  
/*  
  private boolean almacenarSmsYTav(Comunicacion.Tipo2 tipo2, Reserva reserva, String formatoFecha, String formatoHora) {
    //Se envía el mail obligatorio al usuario
    try {
      Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
      //Obtener los numeros de telefono del usuario, que son los datos a solicitar llamados "TelefonoMovil"  y "TelefonoFijo" en la agrupacion que no se puede borrar
      for(DatoReserva dato : reserva.getDatosReserva()) {
        DatoASolicitar datoSol = dato.getDatoASolicitar();
        if("TelefonoMovil".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
          //Tiene telefono movil, se envia SMS
          String telefonoMovil = dato.getValor();
          Comunicacion comunicacion = new Comunicacion(Tipo1.SMS, tipo2, telefonoMovil, recurso, reserva, null);
          entityManager.persist(comunicacion);
        }
        if("TelefonoFijo".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
          //Tiene telefono movil, se envia SMS
          String telefonoFijo = dato.getValor();
          Comunicacion comunicacion = new Comunicacion(Tipo1.TEXTOAVOZ, tipo2, telefonoFijo, recurso, reserva, null);
          entityManager.persist(comunicacion);
        }
      }
      return true;
    }catch(Exception ex) {
      return false;
    }
  }
*/
  
/*  
  private boolean enviarAvisoMiPerfil(Empresa empresa, Reserva reserva, Comunicacion.Tipo2 tipoAviso) {
    return miPerfilBean.enviarAviso(empresa, reserva, tipoAviso);
  }
*/
  
  @SuppressWarnings("unchecked")
  public Map<String, String> consultarTextos(String idioma) throws ApplicationException {
    Map<String, String> textos = new SofisHashMap();
    try{
      //Primero se cargan los textos globales
      List<TextoGlobal> tGlobales = (List<TextoGlobal>) globalEntityManager.createQuery("SELECT t from TextoGlobal t ").getResultList();
      if(tGlobales != null) {
        for(TextoGlobal tGlobal : tGlobales) {
          textos.put(tGlobal.getCodigo(), tGlobal.getTexto());
        }
      }
    } catch (Exception e){
      //No se pudieron cargar los textos globales, no se puede continuar
      throw new ApplicationException(e);
    }
    //Luego se cargan los textos sobreescritos por el tenant pudiendo pisar textos cargados antes
    //Atención: es probable que no haya aún un tennant configurado y por tanto no se puede hacer esta parte
    //(pero no hay forma de determinarlo)
    try {
      List<TextoTenant> tTenants = (List<TextoTenant>) entityManager.
          createQuery("SELECT t from TextoTenant t WHERE t.codigo.idioma=:idioma")
          .setParameter("idioma", idioma).getResultList();
      if(tTenants != null) {
        for(TextoTenant tTenant : tTenants) {
          textos.put(tTenant.getCodigo().getCodigo(), tTenant.getTexto());
        }
      }
    }catch(Exception pEx) {
      //Nada que hacer, se usan solo los textos globales
    }
    return textos;    
  }
  
  @SuppressWarnings("unchecked")
  public Map<String, String> consultarPreguntasCaptcha(String idioma) throws ApplicationException {
    Map<String, String> preguntasCaptcha = new HashMap<String, String>();
    try {
      List<PreguntaCaptcha> pcs = (List<PreguntaCaptcha>) entityManager.
          createQuery("SELECT p from PreguntaCaptcha p WHERE p.idioma=:idioma")
          .setParameter("idioma", idioma).getResultList();
      if(pcs != null) {
        for(PreguntaCaptcha pc : pcs) {
          preguntasCaptcha.put(pc.getPregunta(), pc.getRespuesta());
        }
      }
    }catch(Exception pEx) {
      //pEx.printStackTrace();
    }
    return preguntasCaptcha;    
  }
  
  public void limpiarTrazas() {
    trazaBean.finalizarTrazas();
  }
  
  @SuppressWarnings("unchecked")
  public List<TramiteAgenda> consultarTramites(Agenda agenda) throws ApplicationException {
    try{
      List<TramiteAgenda> tramites = (List<TramiteAgenda>) entityManager
        .createQuery("SELECT t from TramiteAgenda t " +
            "WHERE t.agenda = :agenda " +
            "ORDER BY t.tramiteNombre")
        .setParameter("agenda", agenda)
        .getResultList();
      return tramites;
      } catch (Exception e){
        throw new ApplicationException(e);
      }
  }

  @SuppressWarnings("unchecked")
  /**
   * Busca un trámite correspondiente a la agenda indicada con el código dado. No debería pasar que haya más de uno,
   * pero si lo hay devuelve el más nuevo (mayor id).
   * @param agenda
   * @param codigo
   * @return
   * @throws ApplicationException
   */
  public TramiteAgenda consultarTramitePorCodigo(Agenda agenda, String codigo) throws ApplicationException {
    try{
      List<TramiteAgenda> tramites = (List<TramiteAgenda>) entityManager.createQuery("SELECT t from TramiteAgenda t " +
            "WHERE t.agenda = :agenda AND t.tramiteCodigo=:codigo ORDER BY t.id DESC")
        .setParameter("agenda", agenda).setParameter("codigo", codigo).getResultList();
      if(tramites.isEmpty()) {
        return null;
      }
      return tramites.get(0);
    } catch (Exception e){
      throw new ApplicationException(e);
    }
  }
  
  public boolean hayCupoPresencial(Disponibilidad disponibilidad) {
    return !helper.chequeoCupoDisponible(disponibilidad, false);
  }
  
  /**
   * Confirma la reserva.
   * Si la reserva debe tener datos, estos son exigidos y validados en este metodo, incluyendo la verificacion de clave unica
   * @throws ApplicationException 
   * @throws BusinessException
   * @return Reserva: Devuelve la reserva pues se le ha asignado un numero unico de reserva dentro de la hora a la que pertenece. 
   * @throws UserException
   * @throws ValidacionException 
   */
  public Reserva confirmarReservaPresencial(Empresa empresa, Reserva reserva) 
    throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException {
    if (reserva == null || reserva.getDatosReserva()==null) {
      throw new BusinessException("debe_especificar_la_reserva");
    }
    //Esto se lee antes de recuperar la reserva de la base porque puede haber cambiado
    Set<DatoReserva> datosNuevos = reserva.getDatosReserva();
    String tramiteCodigo = reserva.getTramiteCodigo();
    String tramiteNombre = reserva.getTramiteNombre();
    reserva = entityManager.find(Reserva.class, reserva.getId());
    if (reserva == null) {
      throw new UserException("no_se_encuentra_la_reserva_especificada");
    }
    if (reserva.getEstado() == Estado.U) {
      throw new UserException("no_es_posible_confirmar_su_reserva");
    }
    if (reserva.getEstado() != Estado.P) {
      throw new UserException("no_es_posible_confirmar_su_reserva");
    }
    //Pisar estos datos con lo que vino de la interfaz
    for(DatoReserva dato : datosNuevos) {
      reserva.getDatosReserva().add(dato);
    }
    reserva.setTramiteCodigo(tramiteCodigo);
    reserva.setTramiteNombre(tramiteNombre);
    //Validar los datos de la reserva: campos obligatorios, campos clave duplicados, validaciones extendidas 
    validarDatosReserva(empresa, reserva);
    //Pasó las validaciones
    Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
    List<DatoASolicitar> campos = helper.obtenerDatosASolicitar(recurso);
    Map<String, DatoASolicitar> camposMap = new HashMap<String, DatoASolicitar>();
    for (DatoASolicitar datoASolicitar : campos) {
      camposMap.put(datoASolicitar.getNombre(), datoASolicitar);
    }
    Map<String, DatoReserva> valores = new HashMap<String, DatoReserva>();
    for (DatoReserva valor : datosNuevos) {
      valores.put(valor.getDatoASolicitar().getNombre(), valor);
    }
    for (DatoReserva dato: valores.values()) {
      DatoASolicitar campo = camposMap.get(dato.getDatoASolicitar().getNombre());
      DatoReserva datoNuevo = new DatoReserva();
      datoNuevo.setValor(dato.getValor());
      datoNuevo.setReserva(reserva);
      datoNuevo.setDatoASolicitar(campo);
      entityManager.persist(datoNuevo);
      reserva.getDatosReserva().add(datoNuevo);
    }
    //Confirmo la reserva, paso el estado a Reservada y le asigno el numero de reserva dentro de la disponibilidad.
    //Con mutua exclusion en el acceso al numerador de la disponibilidad
    Disponibilidad disponibilidad = reserva.getDisponibilidades().get(0);
    try {
      disponibilidad.setNumerador(disponibilidad.getNumerador()+1);
      entityManager.flush();
    } catch(OptimisticLockException olEx){
      throw new AccesoMultipleException("error_de_acceso_concurrente");
    }
    //Ejecuto acciones asociadas al evento reservar
    try{
      ReservaDTO reservaDTO = new ReservaDTO();
      reservaDTO.setEstado(reserva.getEstado().toString());
      reservaDTO.setFecha(reserva.getDisponibilidades().get(0).getFecha());
      reservaDTO.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
      reservaDTO.setId(reserva.getId());
      reservaDTO.setOrigen("P");
      reservaDTO.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
      reservaDTO.setNumero(reserva.getDisponibilidades().get(0).getNumerador() + 1);
      reservaDTO.setTramiteCodigo(reserva.getTramiteCodigo());
      reservaDTO.setTramiteNombre(reserva.getTramiteNombre());
      if (reserva.getLlamada() != null) {
        reservaDTO.setPuestoLlamada(reserva.getLlamada().getPuesto());
      }
      helperAccion.ejecutarAccionesPorEvento(valores, reservaDTO, recurso, Evento.R);
    }catch (ErrorAccionException eaEx){
      throw new BusinessException(eaEx.getCodigoError(), eaEx.getMensajes().toString());
    }
    reserva.setEstado(Estado.R);
    reserva.setSerie("AP");
    reserva.setNumero(disponibilidad.getNumerador());
    reserva.setOrigen("P");
    reserva.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
    //Asigno un codigo de seguridad
    reserva.setCodigoSeguridad("-");
    //Registro en el sistema de trazas
    String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), reserva.getTramiteCodigo(), reserva.getId());
    if(empresa.getOid() != null && transaccionId != null) {
      //Registrar el cabezal en el sistema de trazabilidad del PEU
      String trazaGuid = trazaBean.registrarCabezal(empresa, reserva, transaccionId, reserva.getTramiteCodigo(), true, null, null);
      if(trazaGuid != null) {
        reserva.setTrazabilidadGuid(trazaGuid);
      }else {
        reserva.setTrazabilidadGuid("---");
      }
      //Registrar la primera linea en el sistema de trazabilidad del PEU
      //ToDo: esto habria que hacerlo solo si pudo invocar el cabezal; en otro caso solo habría que guardar la invocación
      //en la base de datos para futuros intentos
      trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.RESERVA);
      //Publicar la novedad
      novedadesBean.publicarNovedad(empresa, reserva, Acciones.RESERVA);
    }
    return reserva;
  }  
  
  @SuppressWarnings("unchecked")
  public List<Integer> cancelarReservasPeriodo(Empresa empresa, Recurso recurso, VentanaDeTiempo ventana, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) throws UserException {
    //Obtener las reservas en estado Pendiente, Reservado o Usado para el período indicado
    List<Reserva> reservas = entityManager.createQuery("SELECT r FROM Reserva r " +
      "JOIN r.disponibilidades d " +
      "WHERE d.recurso.id = :recursoId " +
      "  AND r.estado IN ('P', 'R', 'U') " +
      "  AND d.fecha BETWEEN :fi AND :ff " +
      "  AND d.presencial = false ")
      .setParameter("recursoId", recurso.getId())
      .setParameter("fi", ventana.getFechaInicial())
      .setParameter("ff", ventana.getFechaFinal())
      .getResultList();
    List<Integer> reservasSinEnviarComunicacion = new ArrayList<Integer>();
    //Para cada reserva encontrada, cancelarla, enviando las comunicaciones correspondientes
    for(Reserva reserva : reservas) {
      Estado estadoReserva = reserva.getEstado();
      cancelarReserva(empresa, recurso, reserva, true);
      //Enviar mail (si no estaba pendiente)
      if(!estadoReserva.equals(Estado.P)) {
        try {
          comunicacionesBean.enviarComunicacionesCancelacion(empresa, reserva, idioma, formatoFecha, formatoHora, asunto, cuerpo);
        }catch(Exception ex) {
          reservasSinEnviarComunicacion.add(reserva.getId());
        }
      }
    }
    return reservasSinEnviarComunicacion;
  }
  
  /**
   * Este método es utilizado para marcar la reserva y confirmarla en un solo paso.
   * Está pensado para ser invocado mediante el servicio web REST confirmarReserva.
   * @return
   * @throws UserException
   */
  public Reserva generarYConfirmarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idDisponibilidad, String valoresCampos, 
      String idTransaccionPadre, String pasoTransaccionPadre, TokenReserva tokenReserva, String idioma) throws UserException {
    if(idEmpresa==null) {
      throw new UserException("debe_especificar_la_empresa");
    }
    if(idAgenda==null) {
      throw new UserException("debe_especificar_la_agenda");
    }
    if(idRecurso==null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    if(idDisponibilidad==null) {
      throw new UserException("debe_especificar_la_disponibilidad");
    }
    Reserva reserva = null;
    //Obtener la empresa
    Empresa empresa;
    try {
      empresa = empresasEJB.obtenerEmpresaPorId(idEmpresa);
      if(empresa==null) {
        throw new UserException("no_se_encuentra_la_empresa_especificada");
      }
    }catch(ApplicationException aEx) {
      throw new UserException("no_se_encuentra_la_empresa_especificada");
    }
    //Obtener la agenda
    Agenda agenda;
    try {
      agenda = consultarAgendaPorId(idAgenda);
      if(agenda==null) {
        throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
    }catch(ApplicationException | BusinessException ex) {
      throw new UserException("no_se_encuentra_la_agenda_especificada");
    }
    //Determinar el timezone según la agenda o la empresa
    TimeZone timezone = TimeZone.getDefault();
    if(agenda.getTimezone()!=null && !agenda.getTimezone().isEmpty()) {
      timezone = TimeZone.getTimeZone(agenda.getTimezone());
    }else {
      if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
        timezone = TimeZone.getTimeZone(empresa.getTimezone());
      }
    }
    //Obtener la disponibilidad
    Disponibilidad disponibilidad = (Disponibilidad) entityManager.find(Disponibilidad.class, idDisponibilidad);
    if(disponibilidad==null || !disponibilidad.getRecurso().getId().equals(idRecurso)) {
      throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
    }
    //Marcar la reserva
    reserva = marcarReserva(disponibilidad, tokenReserva, null);
    //Cargar las agrupaciones
    List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(disponibilidad.getRecurso(), timezone);
    //Cargar los datos en la reserva; la respuesta puede contener datos que no están dentro de los datos a solicitar del recurso
    Map<String, String> datosAdicionales = cargarDatosReserva(reserva, agrupaciones, valoresCampos);
    //Cargar los datos de trazabilidad
    Long pasoPadre = null;
    if(pasoTransaccionPadre != null) {
      try {
        pasoPadre = Long.valueOf(pasoTransaccionPadre);
      }catch(Exception ex) {
        throw new UserException("error_no_solucionable");
      }
    }
    //Cargar los datos del trámite si fueron incluidos (deben tener como clave tramite.codigo y opcionalmente tramite.nombre)
    if(tokenReserva!=null) {
      datosAdicionales.put("tramite.codigo", tokenReserva.getTramite());
      datosAdicionales.put("tramite.nombre", null);
    }
    String tramiteCodigo = datosAdicionales.get("tramite.codigo");
    if(tramiteCodigo==null || tramiteCodigo.trim().isEmpty()) {
      throw new UserException("el_codigo_del_tramite_es_obligatorio");
    }
    String tramiteNombre = datosAdicionales.get("tramite.nombre");
    //Si no se especifica el nombre del trámite se busca uno asociado a la agenda
    if(tramiteNombre==null || tramiteNombre.trim().isEmpty()) {
      tramiteNombre = "---";
      for(TramiteAgenda tramite : agenda.getTramites()) {
        if(tramiteCodigo.equals(tramite.getTramiteCodigo())) {
          tramiteNombre = tramite.getTramiteNombre();
        }
      }
    }
    reserva.setTramiteCodigo(tramiteCodigo);
    reserva.setTramiteNombre(tramiteNombre);
    boolean confirmada = false;
    try {
      while (!confirmada) {
        try {
          Reserva rConfirmada = confirmarReserva(empresa, reserva, idTransaccionPadre, pasoPadre, false);
          reserva.setSerie(rConfirmada.getSerie());
          reserva.setNumero(rConfirmada.getNumero());
          reserva.setCodigoSeguridad(rConfirmada.getCodigoSeguridad());
          reserva.setTrazabilidadGuid(rConfirmada.getTrazabilidadGuid());
          confirmada = true;
        } catch (AccesoMultipleException e){
          //Reintento hasta tener exito, en algun momento no me va a dar acceso multiple.
        }
      }
      return reserva;
    }catch(Exception ex) {
      if(ex instanceof UserException) {
        throw (UserException)ex;
      }
      logger.error("No se pudo generar y confirmar la reserva", ex);
      throw new UserException("error_no_solucionable");
    }
  }
  
  /**
   * Carga los datos de la reserva obtenidos por parámetro en la reserva.
   * Los datos deben estar en el mismo formato en el que son pasados por GET como valores por defecto para los campos, es decir
   * [agrupacion.campo.valor;]*.
   * Los datos son cargados directamente en la reserva, excepto en los casos en que la clave agrupacion.campo no se encuentre en
   * la lista de datos a solicitar del recurso, en cuyo caso se devuelven en el resultado.
   * @param reserva
   * @param agrupaciones
   * @param valoresCampos
   * @return
   */
  private Map<String, String> cargarDatosReserva(Reserva reserva, List<AgrupacionDato> agrupaciones, String valoresCampos) {
    //Lista de datos obtenidos por parámetro que no están dentro de los datos a solicitar del recurso
    //(Por ejemplo, el código y nombre del trámite (tramite.codigo y tramite.nombre)
    Map<String, String> datosAdicionales = new HashMap<String, String>();
    //Map<String, Object> datosReserva = new HashMap<String, Object>();
    //Armar dos mapas con los datos a solicitar, uno por ids y otro por nombre para accederlos más rápido
    Map<String, DatoASolicitar> porId = new HashMap<String, DatoASolicitar>();
    Map<String, DatoASolicitar> porNombre = new HashMap<String, DatoASolicitar>();
    for(AgrupacionDato agrupacion : agrupaciones) {
      for(DatoASolicitar datoSolicitar : agrupacion.getDatosASolicitar()) {
        porId.put(agrupacion.getId().toString()+"."+datoSolicitar.getId().toString(), datoSolicitar);
        porNombre.put(agrupacion.getNombre()+"."+datoSolicitar.getNombre(), datoSolicitar);
      }
    }
    //Obtener cada uno de los campos cortando el string por los punto y coma
    String parametros[] = valoresCampos.split("\\;");
    //Procesar cada uno de los parámetros, que deberían ser de la forma <agrupacion>.<dato>.<valor>
    for (String parm : parametros) {
      //Obtener las tres partes (agrupación, dato, valor)
      String agrupCampoValor[] = parm.split("\\.", 3);
      String sAgrupacion = null;
      String sDatoSol = null;
      String sValor = null;
      if(agrupCampoValor.length==3) {
        sAgrupacion = agrupCampoValor[0];
        sDatoSol = agrupCampoValor[1];
        sValor = agrupCampoValor[2];
        //Determinar el dato a solicitar indicado por la agrupación y el dato
        //Si tanto la agrupacion como el dato son numéricos se asume que son los ids, sino se asume que son los nombres
        DatoASolicitar datoASolicitar = null;
        try {
          Integer.valueOf(sAgrupacion);
          Integer.valueOf(sDatoSol);
          //Son identificadores, buscar el dato a solicitar correspondiente
          datoASolicitar = porId.get(sAgrupacion+"."+sDatoSol);
        }catch(NumberFormatException nfEx) {
          //No son identificadores, son nombres
          datoASolicitar = porNombre.get(sAgrupacion+"."+sDatoSol);
        }
        //Si se encontró un dato a solicitar poner el valor apropiado
        if(datoASolicitar != null) {
          DatoReserva datoReserva = new DatoReserva();
          datoReserva.setReserva(reserva);
          datoReserva.setValor(sValor);
          datoReserva.setDatoASolicitar(datoASolicitar);
          reserva.getDatosReserva().add(datoReserva);
        }else {
          datosAdicionales.put(sAgrupacion+"."+sDatoSol, sValor);
        }
      }
    }
    return datosAdicionales;
  }
  
  
  public TokenReserva generarTokenReserva(Integer idRecurso, String cedula, String nombre, String correoe, String codigoTramite, String ipOrigen) {
    
    TokenReserva token = new TokenReserva();

    Recurso recurso = null;
    if(idRecurso!=null) {
      recurso = entityManager.find(Recurso.class, idRecurso);
    }
    
    token.setCedula(cedula);
    token.setNombre(nombre);
    token.setCorreoe(correoe);
    token.setEstado(Estado.P);
    token.setFechaInicio(new Date());
    token.setRecurso(recurso);
    token.setToken(UUID.randomUUID().toString());
    token.setTramite(codigoTramite);
    token.setIpOrigen(ipOrigen);
    
    entityManager.persist(token);
    entityManager.flush();
    entityManager.refresh(token);
    
    return token;
  }
  
  @SuppressWarnings("unchecked")
  public TokenReserva obtenerTokenReserva(String token) {
    //Solo debería haber uno, pero por las dudas se ordena por id y se queda con el último
    String eql = "SELECT t FROM TokenReserva t WHERE t.token=:token ORDER BY t.id DESC";
    List<TokenReserva> tokens = (List<TokenReserva>) entityManager.createQuery(eql).setParameter("token", token).getResultList();
    if(!tokens.isEmpty()) {
      return tokens.get(0);
    }
    return null;
  }
  
  public TokenReserva guardarTokenReserva(TokenReserva token) {
    token = entityManager.merge(token);
    return token;
  }
  
  /**
   * Devuelve todas las reservas asociadas al token indicado.
   * Si el parámetro incluirIncompletas es false solo incluye las que tengan al menos los datos obligatorios completos.
   */
  @SuppressWarnings("unchecked")
  public List<Reserva> obtenerReservasMultiples(Integer tokenId, boolean incluirIncompletas) {
    String eql = "SELECT r FROM Reserva r WHERE r.token.id=:tokenId" + (incluirIncompletas?"":" AND r.datosReserva IS NOT EMPTY")+" ORDER BY r.id";
    return (List<Reserva>) entityManager.createQuery(eql).setParameter("tokenId", tokenId).getResultList();
  }
  
  /**
   * Confirma todas las reservas asociadas al token que tengan al menos los datos obligatorios completos.
   * Las reservas que no tienen los datos obligatorios completos son eliminadas.
   */
  @SuppressWarnings("unchecked")
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public TokenReserva confirmarReservasMultiples(Integer empresaId, Integer tokenId, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) throws UserException {
    if (tokenId == null) {
      throw new UserException("debe_especificar_el_token");
    }
    TokenReserva token = entityManager.find(TokenReserva.class, tokenId);
    if(token == null) {
      throw new UserException("no_se_encuentra_el_token");
    }
    String eql = "SELECT r FROM Reserva r WHERE r.token.id=:tokenId ORDER BY r.id";
    List<Reserva> reservas = (List<Reserva>) entityManager.createQuery(eql).setParameter("tokenId", tokenId).getResultList();
    if(reservas==null || reservas.isEmpty()){
        throw new UserException("no_existen_reservas_para_confirmar");
    }
    
    // ====================================================
    Empresa empresa=globalEntityManager.find(Empresa.class, empresaId);
    // Validar todas las reservas antes de proceder
    for (Reserva reserva:reservas) {
        if (!reserva.getDatosReserva().isEmpty()) {
            try {
                validarDatosReserva(empresa, reserva);
            }
            catch(ErrorValidacionException evex){
              throw evex;
            }
            catch(ValidacionException vex){
              throw vex;
            }
            catch (Exception ex) {
                throw new UserException("alguna_persona_ya_tiene_reservas_confirmadas");
            }
        }
    }
    
    
    for(Reserva reserva : reservas) {
      //Si la reserva tiene datos se confirma; si no tiene datos (quedó incompleta: eligió fecha y hora pero no completó datos) se borra
      if(!reserva.getDatosReserva().isEmpty()) {
        //Cambiar el estado de la reserva
        reserva.setEstado(Estado.R);
        //Si no se cargó el nombre del trámite se busca ahora
        if(reserva.getTramiteNombre() == null && reserva.getTramiteCodigo()!=null) {
          try {
            TramiteAgenda tramite = consultarTramitePorCodigo(reserva.getDisponibilidades().get(0).getRecurso().getAgenda(), reserva.getTramiteCodigo());
            if(tramite != null) {
              reserva.setTramiteNombre(tramite.getTramiteNombre());
            }
          }catch(Exception ex) {
            //Nada para hacer, se queda sin nombre el trámite
          }
        }
        //Registrar en el sistema de trazas
        String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), reserva.getTramiteCodigo(), reserva.getId());
        if(empresa.getOid() != null && transaccionId != null) {
          //Registrar el cabezal en el sistema de trazabilidad del PEU
          String trazaGuid = trazaBean.registrarCabezal(empresa, reserva, transaccionId, reserva.getTramiteCodigo(), inicioAsistido, transaccionPadreId, pasoPadre);
          if(trazaGuid != null) {
            reserva.setTrazabilidadGuid(trazaGuid);
          }else {
            reserva.setTrazabilidadGuid("---");
          }
          //Registrar la primera linea en el sistema de trazabilidad del PEU
          //ToDo: esto habria que hacerlo solo si pudo invocar el cabezal; en otro caso solo habría que guardar la invocación
          //en la base de datos para futuros intentos
          trazaBean.registrarLinea(empresa, reserva, transaccionId, reserva.getDisponibilidades().get(0).getRecurso().getNombre(), ServiciosTrazabilidadBean.Paso.RESERVA);
          //Publicar la novedad
          novedadesBean.publicarNovedad(empresa, reserva, Acciones.RESERVA);
        }
      }else {
        entityManager.remove(reserva);
      }
    }
    token.setEstado(Estado.R);
    return token;
  }
  
  @SuppressWarnings("unchecked")
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public TokenReserva cancelarReservasMultiples(Integer tokenId) throws UserException {
    if (tokenId == null) {
      throw new UserException("debe_especificar_el_token");
    }
    TokenReserva token = entityManager.find(TokenReserva.class, tokenId);
    if(token == null) {
      throw new UserException("no_se_encuentra_el_token");
    }
    //Recuperar todas las reservas del token
    String eql = "SELECT r FROM Reserva r WHERE r.token.id=:tokenId ORDER BY r.id";
    List<Reserva> reservas = (List<Reserva>) entityManager.createQuery(eql).setParameter("tokenId", tokenId).getResultList();
    //Para cada reserva eliminar primero sus datos y luego la propia reserva
    for(Reserva reserva : reservas) {
      for(DatoReserva dato : reserva.getDatosReserva()) {
        entityManager.remove(dato);
      }
      entityManager.remove(reserva);
    }
    //Marcar el token como cancelado
    token.setEstado(Estado.C);
    return token;
  }
  
  /**
   * Elimina una reserva.
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public TokenReserva cancelarReservaMultiple(Integer tokenId, Integer reservaId) throws UserException {
    if (reservaId == null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    if (tokenId == null) {
      throw new UserException("debe_especificar_el_token");
    }
    TokenReserva tokenReserva = entityManager.find(TokenReserva.class, tokenId);
    if (tokenReserva == null) {
      throw new UserException("no_se_encuentra_el_token_especificado");
    }
    if(tokenReserva.getEstado() == Estado.R) {
      throw new UserException("el_token_esta_confirmado");
    }
    if(tokenReserva.getEstado() == Estado.C) {
      throw new UserException("el_token_esta_cancelado");
    }
    Reserva reserva = entityManager.find(Reserva.class, reservaId);
    if (reserva == null || reserva.getToken()==null || !reserva.getToken().getToken().equals(tokenReserva.getToken())) {
      throw new UserException("no_se_encuentra_la_reserva_especificada");
    }
    //Verificar el estado de la reserva
    if(tokenReserva.getEstado() == Estado.C) {
      throw new UserException("el_token_esta_cancelado");
    }
    if(reserva.getEstado() == Estado.U) {
      throw new UserException("la_reserva_esta_utilizada");
    }
    //Eliminar los datos de la reserva y la propia reserva 
    for(DatoReserva dato : reserva.getDatosReserva()) {
      entityManager.remove(dato);
    }
    entityManager.remove(reserva);
    //Actualizar el token
    TokenReserva token = reserva.getToken();
    token.setUltimaReserva(new Date());
    return token;
  }
  
  /**
   * Este método es utilizado para modificar una reserva. Solo se permite modificar la disponibilidad, no el recurso
   * porque los datos a solicitar podrían ser diferentes ni los datos ingresados porque podrían ser de otra persona. 
   * Está pensado para ser invocado mediante el servicio web REST modificarReserva.
   * @return
   * @throws UserException
   */
  public Reserva modificarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva, Integer idDisponibilidad, 
      TokenReserva tokenReserva, String idioma, String ipOrigen) throws UserException {
    if(idEmpresa==null) {
      throw new UserException("debe_especificar_la_empresa");
    }
    if(idAgenda==null) {
      throw new UserException("debe_especificar_la_agenda");
    }
    if(idRecurso==null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    if(idReserva==null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    if(idDisponibilidad==null) {
      throw new UserException("debe_especificar_la_disponibilidad");
    }
    //Obtener la empresa
    Empresa empresa;
    try {
      empresa = empresasEJB.obtenerEmpresaPorId(idEmpresa);
      if(empresa==null) {
        throw new UserException("no_se_encuentra_la_empresa_especificada");
      }
    }catch(ApplicationException aEx) {
      throw new UserException("no_se_encuentra_la_empresa_especificada");
    }
    //Obtener la agenda
    Agenda agenda;
    try {
      agenda = consultarAgendaPorId(idAgenda);
      if(agenda==null) {
        throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
    }catch(ApplicationException | BusinessException ex) {
      throw new UserException("no_se_encuentra_la_agenda_especificada");
    }
    //Obtener la reserva
    Reserva reservaOriginal = (Reserva) entityManager.find(Reserva.class, idReserva);
    if(reservaOriginal==null) {
      throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
    }
    if(!reservaOriginal.getEstado().toString().equals("R")) {
      throw new UserException("no_se_encuentra_la_reserva_especificada");
    }
    Recurso recurso = reservaOriginal.getDisponibilidades().get(0).getRecurso();
    if(recurso.getCambiosAdmite()==null || !recurso.getCambiosAdmite().booleanValue()) {
      throw new UserException("el_recurso_no_admite_cambios_de_reservas");
    }
    //Obtener la disponibilidad nueva
    Disponibilidad disponibilidad = (Disponibilidad) entityManager.find(Disponibilidad.class, idDisponibilidad);
    if(disponibilidad==null || !disponibilidad.getRecurso().getId().equals(recurso.getId())) {
      throw new UserException("no_se_encuentra_la_disponibilidad");
    }

    //Determinar si la reserva está dentro del plazo de modificación
    TimeZone timezone = TimeZone.getDefault();
    if(agenda.getTimezone()!=null && !agenda.getTimezone().isEmpty()) {
      timezone = TimeZone.getTimeZone(agenda.getTimezone());
    }else {
      if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
        timezone = TimeZone.getTimeZone(empresa.getTimezone());
      }
    }
    Calendar calAhora = new GregorianCalendar();
    calAhora.add(Calendar.MILLISECOND, timezone.getOffset(calAhora.getTimeInMillis()));
    Calendar calReserva = new GregorianCalendar();
    calReserva.setTime(reservaOriginal.getDisponibilidades().get(0).getHoraInicio());
    //Restar el tiempo configurado
    if(recurso.getCambiosTiempo()!=null && recurso.getCambiosUnidad()!=null) {
      calReserva.add(recurso.getCambiosUnidad(), recurso.getCambiosTiempo() * -1);
    }
    if(calReserva.before(calAhora)) {
      throw new UserException("la_reserva_especificada_ya_no_admite_cambios");
    }
    //Obtener la disponibilidad nueva
    if(disponibilidad.getHoraInicio().before(calAhora.getTime())) {
      throw new UserException("no_se_encuentra_la_disponibilidad");
    }
    //Marcar la reserva
    Reserva reservaNueva = marcarReservaValidandoDatos(disponibilidad, reservaOriginal, null, ipOrigen);
    //Si pudo hacer eso, la disponibilidad tiene cupo, se puede apuntar la reserva existente a ella y eliminar la reserva dummy
    reservaOriginal.getDisponibilidades().clear();
    reservaOriginal.getDisponibilidades().add(reservaNueva.getDisponibilidades().get(0));
    reservaNueva.getDisponibilidades().clear();
    reservaNueva = entityManager.merge(reservaNueva);
    entityManager.remove(reservaNueva);
    //Confirmar la reserva
    boolean confirmada = false;
    try {
      while (!confirmada) {
        try {
          Reserva rConfirmada = actualizarReserva(empresa, reservaOriginal, null, null, false);
          reservaOriginal.setSerie(rConfirmada.getSerie());
          reservaOriginal.setNumero(rConfirmada.getNumero());
          reservaOriginal.setCodigoSeguridad(rConfirmada.getCodigoSeguridad());
          reservaOriginal.setTrazabilidadGuid(rConfirmada.getTrazabilidadGuid());
          confirmada = true;
        } catch (AccesoMultipleException e){
          //Reintentar hasta tener exito, en algun momento no va a dar acceso multiple.
        }
      }
      return reservaOriginal;
    }catch(Exception ex) {
      if(ex instanceof UserException) {
        throw (UserException)ex;
      }
      logger.error("No se pudo modificar la reserva", ex);
      throw new UserException("error_no_solucionable");
    }
  }
  
  public Reserva modificarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReservaOriginal, Integer idReservaNueva, String idioma) 
      throws UserException {
    if(idEmpresa==null) {
      throw new UserException("debe_especificar_la_empresa");
    }
    if(idAgenda==null) {
      throw new UserException("debe_especificar_la_agenda");
    }
    if(idRecurso==null) {
      throw new UserException("debe_especificar_el_recurso");
    }
    if(idReservaOriginal==null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    if(idReservaNueva==null) {
      throw new UserException("debe_especificar_la_reserva");
    }
    
    //Obtener la empresa
    Empresa empresa;
    try {
      empresa = empresasEJB.obtenerEmpresaPorId(idEmpresa);
      if(empresa==null) {
        throw new UserException("no_se_encuentra_la_empresa_especificada");
      }
    }catch(ApplicationException aEx) {
      throw new UserException("no_se_encuentra_la_empresa_especificada");
    }
    //Obtener la agenda
    Agenda agenda;
    try {
      agenda = consultarAgendaPorId(idAgenda);
      if(agenda==null) {
        throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
    }catch(ApplicationException | BusinessException ex) {
      throw new UserException("no_se_encuentra_la_agenda_especificada");
    }
    //Obtener la reserva original, debe estar en estado reservada
    Reserva reservaOriginal = (Reserva) entityManager.find(Reserva.class, idReservaOriginal);
    if(reservaOriginal==null) {
      throw new UserException("no_se_encuentra_la_reserva");
    }
    if(!Estado.R.equals(reservaOriginal.getEstado())) {
      throw new UserException("no_se_encuentra_la_reserva");
    }
    
    //Determinar si la reserva está vigente
    TimeZone timezone = TimeZone.getDefault();
    if(agenda.getTimezone()!=null && !agenda.getTimezone().isEmpty()) {
      timezone = TimeZone.getTimeZone(agenda.getTimezone());
    }else {
      if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
        timezone = TimeZone.getTimeZone(empresa.getTimezone());
      }
    }
    Calendar calAhora = new GregorianCalendar();
    calAhora.add(Calendar.MILLISECOND, timezone.getOffset(calAhora.getTimeInMillis()));
    Calendar calReserva = new GregorianCalendar();
    calReserva.setTime(reservaOriginal.getDisponibilidades().get(0).getHoraInicio());
    if(calReserva.before(calAhora)) {
      throw new UserException("no_se_encuentra_la_reserva_especificada");
    }
    
    //Obtener la reserva nueva, debe estar en estado pendiente
    Reserva reservaNueva = (Reserva) entityManager.find(Reserva.class, idReservaNueva);
    if(reservaNueva==null) {
      throw new UserException("no_se_encuentra_la_disponibilidad");
    }
    if(!Estado.P.equals(reservaNueva.getEstado())) {
      throw new UserException("no_se_encuentra_la_disponibilidad");
    }
    
    Disponibilidad disponibilidad = reservaOriginal.getDisponibilidades().get(0);
    if(disponibilidad.getHoraInicio().before(calAhora.getTime())) {
      throw new UserException("no_se_encuentra_la_disponibilidad");
    }
    
    reservaOriginal.getDisponibilidades().clear();
    reservaOriginal.getDisponibilidades().add(reservaNueva.getDisponibilidades().get(0));
    
    //Eliminar la reserva nueva
    reservaNueva.getDisponibilidades().clear();
    entityManager.remove(reservaNueva);
    
    //Confirmar la reserva
    boolean confirmada = false;
    try {
      while (!confirmada) {
        try {
          reservaOriginal = actualizarReserva(empresa, reservaOriginal, null, null, false);
          confirmada = true;
        } catch (AccesoMultipleException e){
          //Reintento hasta tener exito, en algun momento no me va a dar acceso multiple.
        }
      }
      return reservaOriginal;
    }catch(Exception ex) {
      if(ex instanceof UserException) {
        throw (UserException)ex;
      }
      logger.error("No se pudo generar y confirmar la reserva", ex);
      throw new UserException("error_no_solucionable");
    }
  }
  
  private Reserva actualizarReserva(Empresa empresa, Reserva reserva, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) 
        throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException {    
    //Confirmo la reserva, paso el estado a Reservada (si no es múltiple) y le asigno el numero de reserva dentro de la disponibilidad.
    //Con mutua exclusion en el acceso al numerador de la disponibilidad
    Disponibilidad disponibilidad = reserva.getDisponibilidades().get(0);
    try {
      disponibilidad.setNumerador(disponibilidad.getNumerador()+1);
      entityManager.flush();
    } catch(OptimisticLockException olEx){
      throw new AccesoMultipleException("error_de_acceso_concurrente");
    }
    String origen;
    if (ctx.isCallerInRole("RA_AE_FCALL_CENTER") || ctx.isCallerInRole("RA_AE_ADMINISTRADOR_DE_RECURSOS")){
      origen = "C"; //Call Center
    }else if (ctx.isCallerInRole("RA_AE_ANONIMO")){
      origen = "W"; //Web
    }else{
      origen = "I"; //Otro
    }
    Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
    //Ejecutar las acciones asociadas al evento reservar
    try{
      ReservaDTO reservaDTO = new ReservaDTO();
      reservaDTO.setEstado(reserva.getEstado().toString());
      reservaDTO.setFecha(reserva.getDisponibilidades().get(0).getFecha());
      reservaDTO.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
      reservaDTO.setId(reserva.getId());
      reservaDTO.setOrigen(origen);
      reservaDTO.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
      reservaDTO.setNumero(reserva.getDisponibilidades().get(0).getNumerador() + 1);
      reservaDTO.setTramiteCodigo(reserva.getTramiteCodigo());
      reservaDTO.setTramiteNombre(reserva.getTramiteNombre());
      if (reserva.getLlamada() != null) {
        reservaDTO.setPuestoLlamada(reserva.getLlamada().getPuesto());
      }
      Map<String, DatoReserva> valores = new HashMap<String, DatoReserva>();
      for (DatoReserva valor : reserva.getDatosReserva()) {
        valores.put(valor.getDatoASolicitar().getNombre(), valor);
      }
      helperAccion.ejecutarAccionesPorEvento(valores, reservaDTO, recurso, Evento.C);
      helperAccion.ejecutarAccionesPorEvento(valores, reservaDTO, recurso, Evento.R);
    }catch (ErrorAccionException eaEx){
      throw new BusinessException(eaEx.getCodigoError(), eaEx.getMensajes().toString());
    }
    if(reserva.getToken() == null) {
      reserva.setEstado(Estado.R);
    }
    reserva.setSerie(recurso.getSerie());
    reserva.setNumero(disponibilidad.getNumerador());
    reserva.setOrigen(origen);
    reserva.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());

    //Registrar en el sistema de trazas una línea para la cancelación de la vieja y otra para la confirmación de la nueva
    String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), reserva.getTramiteCodigo(), reserva.getId());
    if(empresa.getOid() != null && transaccionId != null) {
      trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.CANCELACION);
      trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.RESERVA);
      //Publicar la novedad
      novedadesBean.publicarNovedad(empresa, reserva, Acciones.CANCELACION);
      novedadesBean.publicarNovedad(empresa, reserva, Acciones.RESERVA);
    }

    return reserva;
  }
  
  
  public Reserva modificarReservaNotificar(Integer idReserva, Boolean notificar) throws UserException {
    //Obtener la reserva
    Reserva reservaOriginal = (Reserva) entityManager.find(Reserva.class, idReserva);
    if(reservaOriginal==null) {
      throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
    }
    //modifico el valor "notificar" de la reserva
    reservaOriginal.setNotificar(notificar);
    reservaOriginal = entityManager.merge(reservaOriginal);
      return reservaOriginal;
  }
  
  public void modificarReservaMultipleNotificar(TokenReserva tokenReserva, Boolean notificar) throws UserException {
    //Obtener las reservas asociadas al token
    String eql = "SELECT r FROM Reserva r WHERE r.token.id=:tokenId AND r.datosReserva IS NOT EMPTY ORDER BY r.id";
    @SuppressWarnings("unchecked")
    List<Reserva> reservas = ( List<Reserva>)entityManager.createQuery(eql).setParameter("tokenId", tokenReserva.getId()).getResultList();
    //Procesar cada reserva e ir setteandole la propiedad "notificar"
    for(Reserva reserva : reservas) {
      try{
        modificarReservaNotificar(reserva.getId(),notificar);
      }catch(Exception ex) {
          if(ex instanceof UserException) {
            throw (UserException)ex;
          }  
      }
    }  
  }
  
  
  
  /* Nuevos métodos para el componente de vacunación */
  
  
      /*
   * Para controlar la existencia de cupo sin necesidad de utilizar bloqueo persiste la reserva y luego chequea que el cupo real no sea negativo, si esto
   * sucede, elimina fisicamente la reserva y cancela la operacion.
   * En este caso se hará el mismo procedimiento para la reserva dos, solo que si no hay disponibilidad para la reserva uno ni para la reserva dos, se eliminan las dos reservas
   * @throws BusinessException
   * @throws UserException
   */
  @Override
  public Reserva marcarReservasPares(Disponibilidad disponibilidad,Disponibilidad disponibilidad2, TokenReserva token, String ipOrigen) throws UserException {
      if (disponibilidad == null) {
          throw new UserException("debe_especificar_la_disponibilidad");
      }
      disponibilidad = entityManager.find(Disponibilidad.class, disponibilidad.getId());
      if (disponibilidad == null) {
          throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
      }
      
      if (disponibilidad2 == null) {
          throw new UserException("debe_especificar_la_disponibilidad");
      }
      disponibilidad2 = entityManager.find(Disponibilidad.class, disponibilidad2.getId());
      if (disponibilidad2 == null) {
          throw new UserException("no_se_encuentra_la_disponibilidad2_especificada");
      }
       
      Reserva reserva = new Reserva();
      try {
          //Se crean las dos reserva para primera y segunda dosis, en una transaccion independiente
          reserva = helper.crearReservasParesPendiente(disponibilidad,disponibilidad2, token, ipOrigen);
          
          //Chequeo que el cupo real no de negativo tanto en la reserva de la dosis uno como en la dosis dos
          //Si alguno de los cupo reales da negativo, elimino la dos reservas pendiente y cancelo la operacion
          //De lo contrario las reservas se han marcado con exito
          if (helper.chequeoCupoDisponible(disponibilidad, true) || helper.chequeoCupoDisponible(disponibilidad2, true)) {
              helper.eliminarReservasParesPendientes(reserva);
              
              throw new UserException("el_horario_acaba_de_quedar_sin_cupos");
          }
          
      } catch (ApplicationException ex) {
          throw new UserException("error_marcar_reservas_pares");
      }
      
      return reserva;
  }

  
  /*
   * Confirma la reserva. Solo aplica si la reserva existe y está en el estado P (Pendiente). Si la reserva debe tener datos, estos son exigidos y validados
   * en este metodo, incluyendo la verificacion de clave unica Si se trata de una reserva individual (el token es nulo) queda en estado Reservada
   * (confirmada); si se trata de una reserva perteneciente a un bloque de reservas múltiples permanece en estado pendiente
   *
   * @throws ApplicationException
   * @throws BusinessException
   * @return Reserva: Devuelve la reserva pues se le ha asignado un numero unico de reserva dentro de la hora a la que pertenece.
   * @throws UserException
   * @throws ValidacionException
   */
  @Override
  public Reserva confirmarReservasPares(Empresa empresa, Reserva reserva, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido)
          throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException {
      if (reserva == null || reserva.getDatosReserva() == null || reserva.getReservaHija() == null) {
          throw new BusinessException("debe_especificar_la_reserva");
      }
      //Esto se lee antes de recuperar la reserva de la base porque puede haber cambiado
      Set<DatoReserva> datosNuevos = reserva.getDatosReserva();
      Set<DatoReserva> datosNuevos2 = reserva.getReservaHija().getDatosReserva();
      
      String tramiteCodigo = reserva.getTramiteCodigo();
      String tramiteNombre = reserva.getTramiteNombre();
      //Si no se cargó el nombre se busca ahora
      if (tramiteNombre == null && tramiteCodigo != null) {
          TramiteAgenda tramite = consultarTramitePorCodigo(reserva.getDisponibilidades().get(0).getRecurso().getAgenda(), tramiteCodigo);
          if (tramite != null) {
              tramiteNombre = tramite.getTramiteNombre();
          }
      }
      
      //Volver a cargar la reserva para evitar conflicto de versiones
      reserva = entityManager.find(Reserva.class, reserva.getId());
      if (reserva == null) {
          throw new UserException("no_se_encuentra_la_reserva_especificada");
      }
      if (reserva.getEstado() != Estado.P) {
          throw new UserException("no_es_posible_confirmar_su_reserva");
      }
      
      //Volver a cargar la reserva para evitar conflicto de versiones
      Reserva reserva2 = entityManager.find(Reserva.class, reserva.getReservaHija().getId());
      if (reserva2 == null) {
          throw new UserException("no_se_encuentra_la_reserva_especificada");
      }
      if (reserva.getEstado() != Estado.P) {
          throw new UserException("no_es_posible_confirmar_su_reserva");
      }
      
      //Pisar estos datos con lo que vino de la interfaz
      for (DatoReserva dato : datosNuevos) {
          reserva.getDatosReserva().add(dato);
      }
      reserva.setTramiteCodigo(tramiteCodigo);
      reserva.setTramiteNombre(tramiteNombre);
      
      for (DatoReserva dato : datosNuevos2) {
          reserva2.getDatosReserva().add(dato);
      }
      reserva2.setTramiteCodigo(tramiteCodigo);
      reserva2.setTramiteNombre(tramiteNombre);
      
      //Ajustar el número de documento si es una cédula (dejar solo dígitos)
      String tipoDocumento = null;
      String numeroDocumento = null;
      for (DatoReserva datoReserva : reserva.getDatosReserva()) {
          if (DatoASolicitar.NUMERO_DOCUMENTO.equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre())) {
              numeroDocumento = datoReserva.getValor();
          }
          if (DatoASolicitar.TIPO_DOCUMENTO.equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre())) {
              tipoDocumento = datoReserva.getValor();
          }
      }
      if ("CI".equalsIgnoreCase(tipoDocumento) && numeroDocumento != null) {
          numeroDocumento = numeroDocumento.replaceAll("[.-]", "");
          for (DatoReserva datoReserva : reserva.getDatosReserva()) {
              if (DatoASolicitar.NUMERO_DOCUMENTO.equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre())) {
                  datoReserva.setValor(numeroDocumento);
              }
          }
      }
      
      //Validar los datos de la reserva: campos obligatorios, campos clave duplicados, validaciones extendidas 
      validarDatosReserva(empresa, reserva);
      validarDatosReserva(empresa, reserva2);
//      
      //Pasó las validaciones
      Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
      List<DatoASolicitar> campos = helper.obtenerDatosASolicitar(recurso);
      
      Map<String, DatoASolicitar> camposMap = new HashMap<>();
      for (DatoASolicitar datoASolicitar : campos) {
          camposMap.put(datoASolicitar.getNombre(), datoASolicitar);
      }
      //Reserva 1
      Map<String, DatoReserva> valores = new HashMap<>();
      for (DatoReserva valor : datosNuevos) {
          valores.put(valor.getDatoASolicitar().getNombre(), valor);
      }
      for (DatoReserva dato : valores.values()) {
          DatoASolicitar campo = camposMap.get(dato.getDatoASolicitar().getNombre());
          DatoReserva datoNuevo = new DatoReserva();
          datoNuevo.setValor(dato.getValor());
          datoNuevo.setReserva(reserva);
          datoNuevo.setDatoASolicitar(campo);
          entityManager.persist(datoNuevo);
          reserva.getDatosReserva().add(datoNuevo);
      }
      
      //Reserva 2
      Map<String, DatoReserva> valores2 = new HashMap<>();
      for (DatoReserva valor : datosNuevos2) {
          valores2.put(valor.getDatoASolicitar().getNombre(), valor);
      }
      for (DatoReserva dato : valores2.values()) {
          DatoASolicitar campo = camposMap.get(dato.getDatoASolicitar().getNombre());
          DatoReserva datoNuevo = new DatoReserva();
          datoNuevo.setValor(dato.getValor());
          datoNuevo.setReserva(reserva2);
          datoNuevo.setDatoASolicitar(campo);
          entityManager.persist(datoNuevo);
          reserva2.getDatosReserva().add(datoNuevo);
      }
      
      //Confirmo la reserva, paso el estado a Reservada (si no es múltiple) y le asigno el numero de reserva dentro de la disponibilidad.
      //Con mutua exclusion en el acceso al numerador de la disponibilidad
      Disponibilidad disponibilidad = reserva.getDisponibilidades().get(0);
      try {
          disponibilidad.setNumerador(disponibilidad.getNumerador() + 1);
          entityManager.flush();
      } catch (OptimisticLockException olEx) {
          throw new AccesoMultipleException("error_de_acceso_concurrente");
      }
      
      Disponibilidad disponibilidad2 = reserva2.getDisponibilidades().get(0);
      try {
          disponibilidad2.setNumerador(disponibilidad2.getNumerador() + 1);
          entityManager.flush();
      } catch (OptimisticLockException olEx) {
          throw new AccesoMultipleException("error_de_acceso_concurrente");
      }
      
      
      ReservaDTO reservaDTO = new ReservaDTO();
      reservaDTO.setEstado(reserva.getEstado().toString());
      reservaDTO.setFecha(reserva.getDisponibilidades().get(0).getFecha());
      reservaDTO.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
      reservaDTO.setId(reserva.getId());
      reservaDTO.setOrigen(reserva.getOrigen());
      reservaDTO.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
      reservaDTO.setNumero(reserva.getDisponibilidades().get(0).getNumerador() + 1);
      reservaDTO.setTramiteCodigo(reserva.getTramiteCodigo());
      reservaDTO.setTramiteNombre(reserva.getTramiteNombre());
      if (reserva.getLlamada() != null) {
          reservaDTO.setPuestoLlamada(reserva.getLlamada().getPuesto());
      }
      
      if (reserva.getToken() == null) {
          reserva.setEstado(Estado.R);
      }
      //Reserva 1
      reserva.setSerie(recurso.getSerie());
      reserva.setNumero(disponibilidad.getNumerador());
      //reserva.setOrigen(origen);
      reserva.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
      //Asignar un codigo de seguridad
      String codigoSeguridad = "" + (new Date()).getTime();
      if (codigoSeguridad.length() > 5) {
          codigoSeguridad = codigoSeguridad.substring(codigoSeguridad.length() - 5);
      }
      reserva.setCodigoSeguridad(codigoSeguridad);
      
      //Reserva 2
      reserva2.setSerie(recurso.getSerie());
      reserva2.setNumero(disponibilidad2.getNumerador());
      //reserva.setOrigen(origen);
      reserva2.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
      reserva2.setCodigoSeguridad(codigoSeguridad);
      
      reserva.setReservaHija(reserva2);

      return reserva;
  }

  
  /*
   * Este método es utilizado para marcar las dos reservas de vacunación y confirmarlas en un solo paso. Está pensado para ser invocado mediante el servicio web REST
   * confirmarReserva.
   *
   * @return
   * @throws UserException
   */
  @Override
  public Reserva generarYConfirmarReservasVacunacion(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idDisponibilidad, String valoresCampos,
          String idTransaccionPadre, String pasoTransaccionPadre, TokenReserva tokenReserva, String idioma, Date fechaReservaDos, String tipoDocReservaDos, String tipoDosisReservaDos) throws UserException {
    String tipoDosis = null;
    
    if (idEmpresa == null) {
          throw new UserException("debe_especificar_la_empresa");
      }
      if (idAgenda == null) {
          throw new UserException("debe_especificar_la_agenda");
      }
      if (idRecurso == null) {
          throw new UserException("debe_especificar_el_recurso");
      }
      if (idDisponibilidad == null) {
          throw new UserException("debe_especificar_la_disponibilidad");
      }
      if (fechaReservaDos == null) {
          throw new UserException("debe_especificar_la_fecha_reserva_dos");
      }
      if (tipoDocReservaDos == null) {
          throw new UserException("debe_especificar_el_tipo_doc_reserva_dos");
      }
      
      if(tipoDosisReservaDos!=null){
        tipoDosis = tipoDosisReservaDos;
      }
      
      //Obtener la empresa
      Empresa empresa;
      try {
          empresa = empresasEJB.obtenerEmpresaPorId(idEmpresa);
          if (empresa == null) {
              throw new UserException("no_se_encuentra_la_empresa_especificada");
          }
      } catch (ApplicationException aEx) {
          throw new UserException("no_se_encuentra_la_empresa_especificada");
      }
      //Obtener la agenda
      Agenda agenda;
      try {
          agenda = consultarAgendaPorId(idAgenda);
          if (agenda == null) {
              throw new UserException("no_se_encuentra_la_agenda_especificada");
          }
      } catch (ApplicationException | BusinessException ex) {
          throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
      
      //Determinar el timezone según la agenda o la empresa
      TimeZone timezone = TimeZone.getDefault();
      if (agenda.getTimezone() != null && !agenda.getTimezone().isEmpty()) {
          timezone = TimeZone.getTimeZone(agenda.getTimezone());
      } else if (empresa.getTimezone() != null && !empresa.getTimezone().isEmpty()) {
          timezone = TimeZone.getTimeZone(empresa.getTimezone());
      }
      
      //Obtener la disponibilidad Reserva 1
      Disponibilidad disponibilidad = (Disponibilidad) entityManager.find(Disponibilidad.class, idDisponibilidad);
      if (disponibilidad == null || !disponibilidad.getRecurso().getId().equals(idRecurso)) {
          throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
      }
      
      //Obtener la disponibilidad Reserva 2
      Disponibilidad disponibilidad2 = disponibilidadEJB.obtenerDisponibilidadEnHoraInicio(disponibilidad.getRecurso(),fechaReservaDos);
      if (disponibilidad2 == null || !disponibilidad2.getRecurso().getId().equals(idRecurso)) {
          throw new UserException("no_se_encuentra_la_disponibilidad2_especificada");
      }
      
      if(disponibilidad2.getHoraInicio().before(disponibilidad.getHoraInicio()) || disponibilidad2.getHoraInicio().equals(disponibilidad.getHoraInicio())){
        throw new UserException("la_fecha_reserva_dos_debe_ser_posterior_a_la_fecha_reserva_uno");
      }
      
      
      //Marcar la reserva
      Reserva reserva = marcarReservasPares(disponibilidad,disponibilidad2, tokenReserva, null);
      //Cargar las agrupaciones
      List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(disponibilidad.getRecurso(), timezone);
      //Cargar los datos en la reserva; la respuesta puede contener datos que no están dentro de los datos a solicitar del recurso
      Map<String, String> datosAdicionales = cargarDatosReservaPares(reserva, agrupaciones, valoresCampos,tipoDocReservaDos,tipoDosis);
      //Cargar los datos de trazabilidad
      Long pasoPadre = null;
      if (pasoTransaccionPadre != null) {
          try {
              pasoPadre = Long.valueOf(pasoTransaccionPadre);
          } catch (Exception ex) {
              throw new UserException("error_no_solucionable");
          }
      }
      //Cargar los datos del trámite si fueron incluidos (deben tener como clave tramite.codigo y opcionalmente tramite.nombre)
      if (tokenReserva != null) {
          datosAdicionales.put("tramite.codigo", tokenReserva.getTramite());
          datosAdicionales.put("tramite.nombre", null);
      }
      String tramiteCodigo = datosAdicionales.get("tramite.codigo");
      if (tramiteCodigo == null || tramiteCodigo.trim().isEmpty()) {
          throw new UserException("el_codigo_del_tramite_es_obligatorio");
      }
      String tramiteNombre = datosAdicionales.get("tramite.nombre");
      //Si no se especifica el nombre del trámite se busca uno asociado a la agenda
      if (tramiteNombre == null || tramiteNombre.trim().isEmpty()) {
          tramiteNombre = "---";
          for (TramiteAgenda tramite : agenda.getTramites()) {
              if (tramiteCodigo.equals(tramite.getTramiteCodigo())) {
                  tramiteNombre = tramite.getTramiteNombre();
              }
          }
      }
      
      try {
          validarDatosReserva(empresa, reserva);
          validarDatosReserva(empresa, reserva.getReservaHija());

          reserva.setTramiteCodigo(tramiteCodigo);
          reserva.setTramiteNombre(tramiteNombre);
          
          reserva.getReservaHija().setTramiteCodigo(tramiteCodigo);
          reserva.getReservaHija().setTramiteNombre(tramiteNombre);
          
          boolean confirmada = false;
          while (!confirmada) {
              try {
                  Reserva rConfirmada = confirmarReservasPares(empresa, reserva, idTransaccionPadre, pasoPadre, false);
                  reserva.setSerie(rConfirmada.getSerie());
                  reserva.setNumero(rConfirmada.getNumero());
                  reserva.setCodigoSeguridad(rConfirmada.getCodigoSeguridad());
                  reserva.setTrazabilidadGuid(rConfirmada.getTrazabilidadGuid());
                  confirmada = true;
              } catch (AccesoMultipleException e) {
                logger.error("HOLA "  + e.getMessage());
                  //Reintento hasta tener exito, en algun momento no me va a dar acceso multiple.
              }
          }
          return reserva;
      } catch (Exception ex) {
          if (ex instanceof UserException) {
              throw (UserException) ex;
          }
          logger.error("No se pudo generar y confirmar la reserva", ex);
          throw new UserException("error_no_solucionable");
      }
  }

  
  
   /*
   * Carga los datos de la reserva obtenidos por parámetro en la reserva. Los datos deben estar en el mismo formato en el que son pasados por GET como valores
   * por defecto para los campos, es decir [agrupacion.campo.valor;]*. Los datos son cargados directamente en la reserva, excepto en los casos en que la clave
   * agrupacion.campo no se encuentre en la lista de datos a solicitar del recurso, en cuyo caso se devuelven en el resultado.
   *
   * @param reserva
   * @param agrupaciones
   * @param valoresCampos
   * @return
   */
  private Map<String, String> cargarDatosReservaPares(Reserva reserva, List<AgrupacionDato> agrupaciones, String valoresCampos, String tipoDocReserva2,String tipoDosisReserva2) {
      //Lista de datos obtenidos por parámetro que no están dentro de los datos a solicitar del recurso
      //(Por ejemplo, el código y nombre del trámite (tramite.codigo y tramite.nombre)
      Map<String, String> datosAdicionales = new HashMap<>();
      //Armar dos mapas con los datos a solicitar, uno por ids y otro por nombre para accederlos más rápido
      Map<String, DatoASolicitar> porId = new HashMap<>();
      Map<String, DatoASolicitar> porNombre = new HashMap<>();
      for (AgrupacionDato agrupacion : agrupaciones) {
          for (DatoASolicitar datoSolicitar : agrupacion.getDatosASolicitar()) {
              porId.put(agrupacion.getId().toString() + "." + datoSolicitar.getId().toString(), datoSolicitar);
              porNombre.put(agrupacion.getNombre() + "." + datoSolicitar.getNombre(), datoSolicitar);
          }
      }
      //Obtener cada uno de los campos cortando el string por los punto y coma
      String parametros[] = valoresCampos.split("\\;");
      
      //Obtener las tres partes (agrupación, dato, valor) de tipo documento y tipo dosis para la reserva 2
      String agrupCampoValorReserva2[] = tipoDocReserva2.split("\\.", 3);
      String sAgrupacionTipoDosis, sDatoSolTipoDosis, sValorTipoDosis;
      if(tipoDosisReserva2!=null){
        String agrupCampo2ValorReserva2[] = tipoDosisReserva2.split("\\.", 3);
        if(agrupCampo2ValorReserva2.length==3){
          sAgrupacionTipoDosis = agrupCampo2ValorReserva2[0];
          sDatoSolTipoDosis = agrupCampo2ValorReserva2[1];
        sValorTipoDosis = agrupCampo2ValorReserva2[2];
        }
        else{
          String constanteSplit[] = Utiles.SEGUNDA_DOSIS.split("\\.", 3);
          sAgrupacionTipoDosis = constanteSplit[0];
          sDatoSolTipoDosis = constanteSplit[1];
        sValorTipoDosis = constanteSplit[2];
        }
      }
      else{
        String constanteSplit[] = Utiles.SEGUNDA_DOSIS.split("\\.", 3);
        sAgrupacionTipoDosis = constanteSplit[0];
      sDatoSolTipoDosis = constanteSplit[1];
      sValorTipoDosis = constanteSplit[2];
      }
      
      
      //Procesar cada uno de los parámetros, que deberían ser de la forma <agrupacion>.<dato>.<valor>
      for (String parm : parametros) {
          //Obtener las tres partes (agrupación, dato, valor)
          String agrupCampoValor[] = parm.split("\\.", 3);
          
          
          
          if (agrupCampoValor.length == 3 && agrupCampoValorReserva2.length==3) {
              String sAgrupacion = agrupCampoValor[0];
              String sDatoSol = agrupCampoValor[1];
              String sValor = agrupCampoValor[2];
              
              
              //Determinar el dato a solicitar indicado por la agrupación y el dato
              //Si tanto la agrupacion como el dato son numéricos se asume que son los ids, sino se asume que son los nombres
              DatoASolicitar datoASolicitar;
              try {
                  Integer.valueOf(sAgrupacion);
                  Integer.valueOf(sDatoSol);
                  //Son identificadores, buscar el dato a solicitar correspondiente
                  datoASolicitar = porId.get(sAgrupacion + "." + sDatoSol);
              } catch (NumberFormatException nfEx) {
                  //No son identificadores, son nombres
                  datoASolicitar = porNombre.get(sAgrupacion + "." + sDatoSol);
              }
              //Si se encontró un dato a solicitar poner el valor apropiado
              if (datoASolicitar != null) {
                  DatoReserva datoReserva = new DatoReserva();
                  datoReserva.setReserva(reserva);
                  datoReserva.setValor(sValor);
                  datoReserva.setDatoASolicitar(datoASolicitar);
                  reserva.getDatosReserva().add(datoReserva);
                  
                  DatoReserva datoReserva2 = new DatoReserva();
                  datoReserva2.setReserva(reserva.getReservaHija());
                  //Aquí buscamos que el dato a solicitar sea el mismo enviado en tipoDocReserva2
                  //y asignamos el valor de ese este parámetro
                  
                  if(agrupCampoValorReserva2.length==3 && datoASolicitar.equals(porNombre.get(agrupCampoValorReserva2[0] + "." + agrupCampoValorReserva2[1]))){
                      datoReserva2.setValor(agrupCampoValorReserva2[2]);
                  }
                  else if(datoASolicitar.equals(porNombre.get(sAgrupacionTipoDosis + "." + sDatoSolTipoDosis))){
                      datoReserva2.setValor(sValorTipoDosis);
                  }
                  else{
                      datoReserva2.setValor(sValor);
                  }
                  
                  datoReserva2.setDatoASolicitar(datoASolicitar);
                  reserva.getReservaHija().getDatosReserva().add(datoReserva2);
                  
              } else {
                  datosAdicionales.put(sAgrupacion + "." + sDatoSol, sValor);
              }
          }
      }
      
      return datosAdicionales;
  }

  
  /*
   * Elimina dos reservas pares, primera dosis y segunda dosis de vacuanación
  */
  @Override
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public TokenReserva cancelarReservasParesMultiple(Integer tokenId, Integer reservaId) throws UserException {
      Reserva reserva2 = null;
      
      if (reservaId == null) {
          throw new UserException("debe_especificar_la_reserva");
      }
      if (tokenId == null) {
          throw new UserException("debe_especificar_el_token");
      }
      TokenReserva tokenReserva = entityManager.find(TokenReserva.class, tokenId);
      if (tokenReserva == null) {
          throw new UserException("no_se_encuentra_el_token_especificado");
      }
      if (tokenReserva.getEstado() == Estado.R) {
          throw new UserException("el_token_esta_confirmado");
      }
      if (tokenReserva.getEstado() == Estado.C) {
          throw new UserException("el_token_esta_cancelado");
      }
      Reserva reserva = entityManager.find(Reserva.class, reservaId);
      if (reserva == null || reserva.getToken() == null || !reserva.getToken().getToken().equals(tokenReserva.getToken())) {
          throw new UserException("no_se_encuentra_la_reserva_especificada");
      }
              
      //Pregunta si la reserva tiene otra reserva asociada, si fue en pares por vacunación
      if(reserva.getReservaHija()!=null){
          reserva2 = entityManager.find(Reserva.class, reserva.getReservaHija().getId());
          if (reserva2 == null || reserva2.getToken() == null || !reserva2.getToken().getToken().equals(tokenReserva.getToken())) {
              throw new UserException("no_se_encuentra_la_reserva_especificada");
          }
      }
      
      //Verificar el estado de la reserva
      if (tokenReserva.getEstado() == Estado.C) {
          throw new UserException("el_token_esta_cancelado");
      }
      if (reserva.getEstado() == Estado.U) {
          throw new UserException("la_reserva_esta_utilizada");
      }
      
      //Aplica cuando la reserva fue en pares por vacunación
      if (reserva2.getEstado() == Estado.U) {
          throw new UserException("la_reserva_esta_utilizada");
      }
      
      //Eliminar los datos de la reserva de la segunda dosis y la propia reserva
      if(reserva2.getDatosReserva()!=null){ 
        for (DatoReserva dato : reserva2.getDatosReserva()) {
            entityManager.remove(dato);
        }
      }
      
      entityManager.remove(reserva2);
      


      //Eliminar los datos de la reserva de la primer dosis y la propia reserva
      if(reserva.getDatosReserva()!=null){  
        for (DatoReserva dato : reserva.getDatosReserva()) {
            entityManager.remove(dato);
        }
      }
      entityManager.remove(reserva);
          
      
      //Actualizar el token
      TokenReserva token = reserva.getToken();
      token.setUltimaReserva(new Date());
      return token;
  }
  
  
  @Override
  public void cancelarReservaVacunacion(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva,Integer idReserva2, Boolean masiva) throws UserException {
      if (idEmpresa == null) {
          throw new UserException("debe_especificar_la_empresa");
      }
      if (idAgenda == null) {
          throw new UserException("debe_especificar_la_agenda");
      }
      if (idRecurso == null) {
          throw new UserException("debe_especificar_el_recurso");
      }
      if (idReserva == null) {
          throw new UserException("debe_especificar_la_reserva");
      }
      if (idReserva2 == null) {
          throw new UserException("debe_especificar_la_reserva_segunda");
      }
      
      //Obtener la empresa
      Empresa empresa;
      try {
          empresa = empresasEJB.obtenerEmpresaPorId(idEmpresa);
          if (empresa == null) {
              throw new UserException("no_se_encuentra_la_empresa_especificada");
          }
      } catch (ApplicationException aEx) {
          throw new UserException("no_se_encuentra_la_empresa_especificada");
      }
      
      //Obtener la agenda
      Agenda agenda;
      try {
          agenda = consultarAgendaPorId(idAgenda);
          if (agenda == null) {
              throw new UserException("no_se_encuentra_la_agenda_especificada");
          }
      } catch (ApplicationException | BusinessException ex) {
          throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
      
      //Obtener la reserva primer dosis
      Reserva reserva = (Reserva) entityManager.find(Reserva.class, idReserva);
      if (reserva == null) {
          throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
      }
      if (!Estado.R.equals(reserva.getEstado())) {
          throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
      }
      if (reserva.getEstado() == Estado.U) {
          throw new UserException("la_reserva_primer_dosis_esta_utilizada");
      }
      
      
      //Obtener la reserva segunda dosis
      Reserva reserva2 = (Reserva) entityManager.find(Reserva.class, idReserva2);
      if (reserva2 == null) {
          throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
      }
      if (!Estado.R.equals(reserva.getEstado())) {
          throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
      }
      
      
      //Obtener el recurso
      Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
      //Cancelar la reserva
      
      if (recurso == null) {
          throw new UserException("debe_especificar_el_recurso");
      }
      

      //Determinar el timezone de la reserva
      TimeZone timezone = TimeZone.getDefault();
      if (recurso.getAgenda().getTimezone() != null && !recurso.getAgenda().getTimezone().isEmpty()) {
          timezone = TimeZone.getTimeZone(recurso.getAgenda().getTimezone());
      } else if (empresa.getTimezone() != null && !empresa.getTimezone().isEmpty()) {
          timezone = TimeZone.getTimeZone(empresa.getTimezone());
      }
      
      //Determinar si aún se está a tiempo de cancelar la reserva (no aplica si es masiva)
      if (!masiva) {
          Calendar horaLimiteTimezone = new GregorianCalendar();
          horaLimiteTimezone.setTime(reserva.getFechaHora());
          horaLimiteTimezone.add(recurso.getCancelacionUnidad(), -1 * recurso.getCancelacionTiempo());
          Calendar horaActualTimezone = new GregorianCalendar();
          horaActualTimezone.add(Calendar.MILLISECOND, timezone.getOffset(horaActualTimezone.getTime().getTime()));
          if (!horaActualTimezone.before(horaLimiteTimezone)) {
              throw new UserException("ha_expirado_el_plazo_de_cancelacion");
          }
      }
      
      List<Reserva> reservas = new ArrayList<Reserva>();
      reservas.add(reserva);
      reservas.add(reserva2);
      
      for(Reserva r: reservas){
          ReservaDTO reservaDTO = new ReservaDTO();
          reservaDTO.setEstado(r.getEstado().toString());
          reservaDTO.setFecha(r.getDisponibilidades().get(0).getFecha());
          reservaDTO.setHoraInicio(r.getDisponibilidades().get(0).getHoraInicio());
          reservaDTO.setId(r.getId());
          reservaDTO.setOrigen(r.getOrigen());
          reservaDTO.setNumero(r.getDisponibilidades().get(0).getNumerador() + 1);
          reservaDTO.setTramiteCodigo(r.getTramiteCodigo());
          reservaDTO.setTramiteNombre(r.getTramiteNombre());
          reservaDTO.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase()); //el usuario de sesion es el que esta cancelando la reserva anterior y ese dato lo enviamos hacia GAP
          reservaDTO.setTcancela(masiva ? TipoCancelacion.M.toString() : TipoCancelacion.I.toString());
          reservaDTO.setFcancela(new Date());
          if (r.getLlamada() != null) {
              reservaDTO.setPuestoLlamada(r.getLlamada().getPuesto());
          }

          //Ejecuto acciones asociadas al evento cancelar
          Map<String, DatoReserva> valores = new HashMap<>();
          for (DatoReserva valor : r.getDatosReserva()) {
              valores.put(valor.getDatoASolicitar().getNombre(), valor);
          }
          try {
              helperAccion.ejecutarAccionesPorEvento(valores, reservaDTO, recurso, Evento.C);
          } catch (Exception ex) {
              throw new UserException(ex.getMessage());
          }
      
      
      
          r.setEstado(Estado.C);
          r.setObservaciones(r.getObservaciones());
          r.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase());
          r.setTcancela(masiva ? TipoCancelacion.M : TipoCancelacion.I);
          r.setFcancela(reservaDTO.getFcancela());



          //Determinar la fecha de liberación según la cancelación sea inmediata o diferida
          if (recurso.getCancelacionTipo() == null || FormaCancelacion.I.equals(recurso.getCancelacionTipo())) {
              //Es inmediata, el cupo se libera al instante
              r.setFechaLiberacion(r.getFcancela());
          } else {
              //Es diferida, se selecciona una fecha aleatoria
              Date fechaHoraAtencion = r.getDisponibilidades().get(0).getHoraInicio();

              //Determinar la fecha actual según el timezone de la reserva
              Calendar fechaHoraActual = new GregorianCalendar();
              fechaHoraActual.add(Calendar.MILLISECOND, timezone.getOffset(fechaHoraActual.getTime().getTime()));

              //Determinar la diferencia entre la fecha actual y la fecha de atención
              long difMilisegundos = fechaHoraAtencion.getTime() - fechaHoraActual.getTime().getTime();
              if (difMilisegundos < 0) {
                  //No debería pasar
                  difMilisegundos = 0;
              }

              //Elegir un desplazamiento aleatorio
              long valorMinimo = Math.round(difMilisegundos * 0.1);
              long valorMaximo = Math.round(difMilisegundos * 0.8);
              long milisegundosAleatorios = ThreadLocalRandom.current().nextLong(valorMinimo, valorMaximo);
              if (milisegundosAleatorios > Integer.MAX_VALUE) {
                  milisegundosAleatorios = Integer.MAX_VALUE;
              }
              //Calcular la fecha y hora de liberación como la hora actual más el desplazamiento
              Calendar fechaHoraLiberacion = new GregorianCalendar();
              fechaHoraLiberacion.add(Calendar.MILLISECOND, (int) milisegundosAleatorios);
              r.setFechaLiberacion(fechaHoraLiberacion.getTime());
          }
          //Registrar la cancelacion en el sistema de trazas del PEU
          String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), r.getTramiteCodigo(), r.getId());
          if (transaccionId != null) {
              trazaBean.registrarLinea(empresa, r, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.CANCELACION);
          }
          //Publicar la novedad
          novedadesBean.publicarNovedad(empresa, r, Acciones.CANCELACION);
      }
  }

    @Override
  public ResultadoEjecucion validarMoverReservas(Empresa empresa, Recurso recursoOrigen, Recurso recursoDestino,VentanaDeTiempo ventanaOrigen, VentanaDeTiempo ventanaDestino) 
      throws UserException, ApplicationException, BusinessException {
    ResultadoEjecucion ret = new ResultadoEjecucion();
    
    //Refrescar los recursos para que carguen los datos a solicitar y las disponibilidades
    recursoOrigen = consultarRecursoPorId(recursoOrigen.getAgenda(), recursoOrigen.getId());
    recursoDestino = consultarRecursoPorId(recursoDestino.getAgenda(), recursoDestino.getId());
    int cantidadReservasTotalesAMover = 0;
    SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");
    Boolean cuposDisponiblesReservas = Boolean.FALSE;
    
    //Primero validar si los recursos son iguales
    if(recursoOrigen.equals(recursoDestino)){
      //Si son iguales solo verificar las disponibilidades
      //======================================================================================================
      //Verificar si la fecha inicio destino está dentro de la ventana del recurso origen, se traslapa
      if(ventanaDestino.getFechaInicial().after(ventanaOrigen.getFechaInicial()) && ventanaDestino.getFechaInicial().before(ventanaOrigen.getFechaFinal())){
        //aquí las disponibilidades algunas se van a reutilizar validación es distinta
        Integer reservas = 0;
        Integer cuposDisponibles = 0;
        //Disponibilidades y cupos
        List<Object[]> cuposDestino = obtenerCuposDisponiblesPorDisponibilidad(recursoDestino, ventanaDestino);

			    //Para cada disponibilidad en el origen verificar si existe en el destino y si tiene cupos disponibles suficientes para las reservas
				List<Object[]> reservasConfirmadasyPendientesOrigen = obtenerReservasConfirmadasPorDisponibilidad(recursoOrigen, ventanaOrigen);
				
				if(!cuposDestino.isEmpty()){
					//vamos a crear un maps uno para el recurso destino con reservas ->horaInicio
					Map<String, Integer> reservasPorHoraInicio = new HashMap<String, Integer>();
					
					for(Object[] reservaOrigen : reservasConfirmadasyPendientesOrigen){
						reservasPorHoraInicio.put(formatoHora.format((Date)reservaOrigen[0]), ((BigInteger)reservaOrigen[2]).intValue());
						reservas = reservas + ((BigInteger)reservaOrigen[2]).intValue();
					}
					
					for(Object[] cupoDestino : cuposDestino){
						cuposDisponibles = cuposDisponibles + ((BigInteger)cupoDestino[2]).intValue();
						if(reservasPorHoraInicio.get(formatoHora.format((Date)cupoDestino[1]))!=null && reservasPorHoraInicio.get(formatoHora.format((Date)cupoDestino[1]))!=0){
							cuposDisponibles = cuposDisponibles + reservasPorHoraInicio.get(formatoHora.format((Date)cupoDestino[1]));
						}
					}
					
					if(cuposDisponibles>=reservas){
						cantidadReservasTotalesAMover = reservas;
					}
					else{
						ret.getErrores().add("No existen cupos suficientes para las reservas que se desean mover, teniendo en cuenta los intervalos mínimos entre horarios.");
					}
				}
				else{
					ret.getErrores().add("No existen disponibilidades a partir del horario de inicio del recurso destino para las reservas que se desean mover");
				}				
			}
			else{
				//misma fecha o diferente fecha, mismo recurso
				//Disponibilidades y cupos
				//se obtienen las disponibilidades del destino con la frecuencia
				List<Object[]> cuposDestino = obtenerCuposFrecuenciaDisponibles(recursoDestino, ventanaDestino);


          //Para cada disponibilidad en el origen verificar si existe en el destino y si tiene cupos disponibles suficientes para las reservas
        List<Object[]> reservasConfirmadasyPendientesOrigen = obtenerReservasConfirmadasPorFrecuencia(recursoOrigen, ventanaOrigen);

				//vamos a crear un maps uno para el recurso destino con frecuencia ->cupos disponible
				Map<String, Integer> cuposDisponiblesPorFrecuencia = new HashMap<String, Integer>();
				
				if(!cuposDestino.isEmpty()){
				
					for(Object[] cupoDestino : cuposDestino) {
						cuposDisponiblesPorFrecuencia.put(((Integer)cupoDestino[0]).toString(), ((Integer)cupoDestino[1]).intValue());
					}
					
					
					for(Object[] reservaOrigen : reservasConfirmadasyPendientesOrigen) {
						
						Integer cupoDisponible =  cuposDisponiblesPorFrecuencia.get(((Integer)reservaOrigen[0]).toString());
						if(cupoDisponible!=null && ((Integer)reservaOrigen[1])!=null){
							if(cupoDisponible>=(Integer)reservaOrigen[1]){
								cuposDisponiblesReservas = Boolean.TRUE;
								cantidadReservasTotalesAMover = cantidadReservasTotalesAMover + ((Integer)reservaOrigen[1]).intValue();
							}
							else{
								cuposDisponiblesReservas = Boolean.FALSE;
							}
						}
					}
					
					if(!cuposDisponiblesReservas){
						ret.getErrores().add("No existen cupos suficientes para las reservas que se desean mover, teniendo en cuenta los intervalos mínimos entre horarios.");
					}
					
				}
				else{
					ret.getErrores().add("No existen disponibilidades a partir del horario de inicio del recurso destino para las reservas que se desean mover.");
				}
			}
		}
		else{
			//Validaciones previas (algunas son críticas otras son advertencias y queda a criterio del usuario continuar)
			//======================================================================================================
			//Período de validación y política de reservas múltiples
			//El período de validación debería ser compatible (el destino debe ser el mismo)
			if(recursoDestino.getPeriodoValidacion().intValue()==recursoOrigen.getPeriodoValidacion().intValue()) {
				ret.getMensajes().add("El período de validación del recurso destino es compatible con el del recurso origen.");
			}else {
				ret.getErrores().add("El período de validación del recurso destino no es el mismo que el del recurso origen.");
			}
			//La aceptación o no de reservas múltiples debería ser el mismo (ambos aceptan o ninguno acepta)
			if(recursoDestino.getMultipleAdmite() != recursoOrigen.getMultipleAdmite()) {
				ret.getErrores().add("La política de aceptación de reservas múltiples no es la misma en el recurso origen y en el recurso destino.");
			}else {
				ret.getMensajes().add("La política de aceptación de reservas múltiples del recurso destino es compatible con la del recurso origen.");
			}
			//======================================================================================================
			//Datos a solicitar (claves, obligatorios, tipos de datos)
			//Los datos a solicitar clave en el recurso destino deben existir en el recurso origen y deberían ser clave también 
			//Todos los datos a solicitar no-clave pero obligatorios en el recurso destino deben existir en el recurso origen y deberían ser obligatorios 
			//Todos los datos a solicitar deben ser de tipos compatibles
			int cantidadErroresAntes = ret.getErrores().size();
			int cantidadWarningsAntes = ret.getWarnings().size();
			for(DatoASolicitar datoSolicitarDestino : recursosEJB.consultarDatosSolicitar(recursoDestino)) {
				//Buscar el dato equivalente en el recurso origen
				DatoASolicitar datoSolicitarOrigen = null;
				for(DatoASolicitar datoSolicitarOrigen0 : recursosEJB.consultarDatosSolicitar(recursoOrigen)) {
					if(datoSolicitarOrigen0.getNombre().equals(datoSolicitarDestino.getNombre()) 
							&& datoSolicitarOrigen0.getAgrupacionDato().getNombre().equals(datoSolicitarDestino.getAgrupacionDato().getNombre())) {
						datoSolicitarOrigen = datoSolicitarOrigen0;
					}
				}
				
				if(datoSolicitarOrigen!=null) {
					if(datoSolicitarDestino.getEsClave()) {
						//El dato es clave en el destino, debería ser clave en el origen
						if(!datoSolicitarOrigen.getEsClave()) {
							//El dato existe en el origen pero no es clave
							ret.getErrores().add("El dato '"+datoSolicitarDestino.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarDestino.getEtiqueta()+"' "
									+ "es clave en el recurso destino pero no es clave en el recurso origen.");
						}
					} 
					if(datoSolicitarDestino.getRequerido()) {
						if(!datoSolicitarOrigen.getRequerido()) {
							//El dato existe en el origen pero no es obligatorio
							ret.getErrores().add("El dato '"+datoSolicitarDestino.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarDestino.getEtiqueta()+"' "
									+ "es requerido en el recurso destino pero no es requerido en el recurso origen.");
						}
					}
					if(datoSolicitarDestino.getIncluirEnNovedades()){
						//El dato es incluido en novedades en el recurso origen pero no en el destino (podría causar duplicaciones al mover reservas)
						if(!datoSolicitarOrigen.getIncluirEnNovedades()) {
							ret.getErrores().add("El dato '"+datoSolicitarDestino.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarDestino.getEtiqueta()+"' "
									+ "es incluido en novedades en el recurso destino pero no es incluido en novedades en el recurso origen.");
						}
					}
				}
				else{
					ret.getErrores().add("El dato '"+datoSolicitarDestino.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarDestino.getEtiqueta()+"' "
							+ "existe en el recurso destino pero no existe en el recurso origen.");
				}
				


				//Si el dato existe en el origen el tipo debe ser compatible con el destino
				if(datoSolicitarOrigen!=null) {
					//Existe en el origen, verificar que los tipos de datos sean iguales
					if(datoSolicitarDestino.getTipo() != datoSolicitarOrigen.getTipo()) {
						ret.getErrores().add("El tipo del dato '"+datoSolicitarDestino.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarDestino.getEtiqueta()+"' "
								+ "no es el mismo en el recurso destino y en el recurso origen.");
					}else {
						//Si es el mismo tipo de datos deberían ser compatibles las restricciones
						switch(datoSolicitarDestino.getTipo()) {
							case STRING:
							case NUMBER:
								//El largo máximo en el destino debe ser igual o mayor al largo máximo en el origen
								if(datoSolicitarDestino.getLargo() < datoSolicitarOrigen.getLargo()) {
									ret.getErrores().add("El tipo del dato '"+datoSolicitarDestino.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarDestino.getEtiqueta()+"' "
											+ "no es compatible en el recurso destino y en el recurso origen (largo máximo).");
								}
								break;
							case LIST:
								//Todos los valores válidos del origen deberían ser válidos en el destino
								boolean opcionesOk = true;
								for(ValorPosible valorPosibleOrigen : datoSolicitarOrigen.getValoresPosibles()) {
									boolean opcionOk = false;
									for(ValorPosible valorPosibleDestino : datoSolicitarDestino.getValoresPosibles()) {
										if(valorPosibleOrigen.getValor().equals(valorPosibleDestino.getValor())) {
											opcionOk = true;
										}
									}
									opcionesOk = opcionesOk && opcionOk;
								}
								if(!opcionesOk) {
									ret.getErrores().add("El tipo del dato '"+datoSolicitarDestino.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarDestino.getEtiqueta()+"' "
											+ "no es compatible en el recurso destino y en el recurso origen (opciones).");
								}
								break;
							default:
								//No hay restricciones para comparar (BOOLEAN, DATE)
						}
					}
				}
				
			}
			
			//Verificar si quedó algún dato clave del origen que no esté en el destino
			for(DatoASolicitar datoSolicitarOrigen : recursosEJB.consultarDatosSolicitar(recursoOrigen)) {
				DatoASolicitar datoSolicitarDestino = null;
				for(DatoASolicitar datoSolicitarDestino0 : recursosEJB.consultarDatosSolicitar(recursoDestino)) {
					if(datoSolicitarDestino0.getNombre().equals(datoSolicitarOrigen.getNombre()) 
							&& datoSolicitarDestino0.getAgrupacionDato().getNombre().equals(datoSolicitarOrigen.getAgrupacionDato().getNombre())) {
						datoSolicitarDestino = datoSolicitarDestino0;
					}
				}
				
				if(datoSolicitarDestino!=null){
				
					if(datoSolicitarOrigen.getEsClave()) {
						//El dato es clave en el recurso origen pero no en el destino (podría causar duplicaciones al mover reservas)
						if(!datoSolicitarDestino.getEsClave()) {
							ret.getErrores().add("El dato '"+datoSolicitarOrigen.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarOrigen.getEtiqueta()+"' "
									+ "es clave en el recurso origen pero no es clave en el recurso destino.");
						}
					}
					if(datoSolicitarOrigen.getRequerido()){
						//El dato es requerido en el recurso origen pero no en el destino (podría causar duplicaciones al mover reservas)
						if(!datoSolicitarDestino.getRequerido()) {
							ret.getErrores().add("El dato '"+datoSolicitarOrigen.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarOrigen.getEtiqueta()+"' "
									+ "es requerido en el recurso origen pero no es requerido en el recurso destino.");
						}
					}
					if(datoSolicitarOrigen.getIncluirEnNovedades()){
						//El dato es incluido en novedades en el recurso origen pero no en el destino (podría causar duplicaciones al mover reservas)
						if(!datoSolicitarDestino.getIncluirEnNovedades()) {
							ret.getErrores().add("El dato '"+datoSolicitarOrigen.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarOrigen.getEtiqueta()+"' "
									+ "es incluido en novedades en el recurso origen pero no es incluido en novedades en el recurso destino.");
						}
					}
				}
				else{
					ret.getErrores().add("El dato '"+datoSolicitarOrigen.getAgrupacionDato().getEtiqueta()+"->"+datoSolicitarOrigen.getEtiqueta()+"' "
							+ "existe en el recurso origen pero no existe en el recurso destino.");
				}
			}
			//Si no se añadió ningún mensaje nuevo por motivo de los datos a solicitar, es porque todos están OK
			if(cantidadErroresAntes == ret.getErrores().size() && cantidadWarningsAntes == ret.getWarnings().size()) {
				ret.getMensajes().add("No hay incompatibilidades entre los datos a solicitar en el recurso destino y el recurso origen.");
			}		
			//======================================================================================================
			//Disponibilidades y cupos
			//se obtienen las disponibilidades del destino con la frecuencia
			List<Object[]> cuposDestino = obtenerCuposFrecuenciaDisponibles(recursoDestino, ventanaDestino);
		    //Para cada disponibilidad en el origen verificar si existe en el destino y si tiene cupos disponibles suficientes para las reservas
			List<Object[]> reservasConfirmadasyPendientesOrigen = obtenerReservasConfirmadasPorFrecuencia(recursoOrigen, ventanaOrigen);
			//vamos a crear un maps uno para el recurso destino con frecuencia ->cupos disponible
			Map<String, Integer> cuposDisponiblesPorFrecuencia = new HashMap<String, Integer>();
			if(!cuposDestino.isEmpty()){
				for(Object[] cupoDestino : cuposDestino) {
					cuposDisponiblesPorFrecuencia.put(((Integer)cupoDestino[0]).toString(), ((Integer)cupoDestino[1]).intValue());
				}				
				for(Object[] reservaOrigen : reservasConfirmadasyPendientesOrigen) {
					Integer cupoDisponible =  cuposDisponiblesPorFrecuencia.get(((Integer)reservaOrigen[0]).toString());
					if(cupoDisponible!=null){
						if((Integer)reservaOrigen[1]!=null){
							if(cupoDisponible>=(Integer)reservaOrigen[1]){
								cuposDisponiblesReservas = Boolean.TRUE;
								cantidadReservasTotalesAMover = cantidadReservasTotalesAMover + ((Integer)reservaOrigen[1]).intValue();
							}
							else{
								cuposDisponiblesReservas = Boolean.FALSE;
							}
						}
					}
				}
				
				if(!cuposDisponiblesReservas){
					ret.getErrores().add("No existen cupos suficientes para las reservas que se desean mover, teniendo en cuenta los intervalos mínimos entre horarios.");
				}
			}
			else{
				ret.getErrores().add("No existen disponibilidades a partir del horario de inicio del recurso destino para las reservas que se desean mover");
			}
			
		}
		
		if(cantidadReservasTotalesAMover > 0) {
	        ret.setElementos(cantidadReservasTotalesAMover);
	    }else {
	    	if(ret.getErrores().isEmpty()){
	    		ret.getErrores().add("No hay reservas para mover.");
	    	}
	    }
		
		//Devolver el resultado		
		return ret;
	}

  
    @Override
  public ResultadoEjecucion ejecutarMoverReservas(Empresa empresa, Recurso recursoOrigen, Recurso recursoDestino, VentanaDeTiempo ventanaOrigen, VentanaDeTiempo ventanaDestino, 
      boolean enviarComunicaciones, String linkBase, boolean generarNovedades, String uuid) throws UserException, ApplicationException, BusinessException {
    ResultadoEjecucion ret = new ResultadoEjecucion();
        Cache saeCache = saeCacheManager.getCache("sae");
    boolean recursoOrigenVisibleInternet = false;
    boolean recursoDestinoVisibleInternet = false;
    SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
    Boolean traslape = Boolean.FALSE;
    
    try {
      //Refrescar los recursos para que carguen los datos a solicitar y las disponibilidades
      recursoOrigen = consultarRecursoPorId(recursoOrigen.getAgenda(), recursoOrigen.getId());
      recursoDestino = consultarRecursoPorId(recursoDestino.getAgenda(), recursoDestino.getId());
      //Primero validar si los recursos son iguales
      if(recursoOrigen.equals(recursoDestino)){
        //Los recursos son iguales
        
        //verificar si hay traslape
        if(ventanaDestino.getFechaInicial().after(ventanaOrigen.getFechaInicial()) && ventanaDestino.getFechaInicial().before(ventanaOrigen.getFechaFinal())){
          traslape = Boolean.TRUE;
        }
        
        //Determinar si los recursos son visibles en internet
        recursoOrigenVisibleInternet = BooleanUtils.isTrue(recursoOrigen.getVisibleInternet());
        //Deshabilitar el recurso para que no sea visible en internet
        if(recursoOrigenVisibleInternet){
          helper.desactivarRecursoVisibleInternet(recursoOrigen);
        }
        
        //Determinar el timezone del recurso 
        TimeZone timezoneOrigen = TimeZone.getDefault();
        if(recursoOrigen.getAgenda().getTimezone()!=null && !recursoOrigen.getAgenda().getTimezone().isEmpty()) {
          timezoneOrigen = TimeZone.getTimeZone(recursoOrigen.getAgenda().getTimezone());
        }else {
          if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
            timezoneOrigen = TimeZone.getTimeZone(empresa.getTimezone());
          }
        }
        
        //Se va obtener las disponibilidades del recurso destino
        //sabiendo que son el mismo recurso, algunas de esas disponibilidades pueden ya existir si se traslapan
        List<Disponibilidad> disponibilidadesOrigen = obtenerDisponibilidades(recursoOrigen, ventanaOrigen, timezoneOrigen, false);

				List<Disponibilidad> disponibilidadesDestino = obtenerDisponibilidades(recursoDestino, ventanaDestino, timezoneOrigen, false);
				SimpleDateFormat formatoClave = new SimpleDateFormat("HH:mm");
				Map<String, Disponibilidad> disponibilidadesDestinoPorHora = new HashMap<String, Disponibilidad>();
				for (Disponibilidad disponibilidadDestino : disponibilidadesDestino) {
					disponibilidadesDestinoPorHora.put(formatoClave.format(disponibilidadDestino.getHoraInicio()), disponibilidadDestino);
		        }
				
				//Armar un mapeo entre los datos a solicitar en el recurso origen y los del recurso destino
			    //La clave es dato.agrupacion.nombre:dato.nombre
			    Map<String, DatoASolicitar> datosSolicitarPorClave = new HashMap<String, DatoASolicitar>();
			    for(DatoASolicitar datoSolicitarDestino : recursosEJB.consultarDatosSolicitar(recursoDestino)) {
			    	String claveDato = datoSolicitarDestino.getAgrupacionDato().getNombre()+":"+datoSolicitarDestino.getNombre();
		    		datosSolicitarPorClave.put(claveDato, datoSolicitarDestino);
			    }				
				
				//Procesar las reservas del día seleccionado
			    List<Reserva> reservasOrigen = new ArrayList<Reserva>();
			    List<Reserva> reservasMovidasOrigen = new ArrayList<Reserva>();
			    List<Reserva> reservasMovidasDestino = new ArrayList<Reserva>();
			    List<Reserva> reservasACancelar = new ArrayList();
	            int reservasMovidas = 0;
	            int reservasTraslapadas = 0;
	            
	            if(traslape){
		            for (Disponibilidad disponibilidadOrigen : disponibilidadesOrigen) {
		            	Disponibilidad dispOrigen = entityManager.find(Disponibilidad.class, disponibilidadOrigen.getId());
		    			reservasTraslapadas = reservasTraslapadas + dispOrigen.getReservas().size();
		            }
	            }
	            
	            
			    for (Disponibilidad disponibilidadOrigen : disponibilidadesOrigen) {
			    	int reservasPendientes = 0;
		            int reservasReservadas = 0;
			    	Disponibilidad dispOrigen = entityManager.find(Disponibilidad.class, disponibilidadOrigen.getId());
			    	//logger.info("DISPONIBILIDAD ORIGEN " + dispOrigen.getId() + "HORA INICIO " + dispOrigen.getHoraInicio() + "CUPO " + dispOrigen.getCupo());
			    	reservasOrigen = new ArrayList<Reserva>(dispOrigen.getReservas());
			    	//logger.info("RESERVAS ORIGEN SIZE " + reservasOrigen.size() );
			    	if(!reservasOrigen.isEmpty()){
			    		
			    		Integer reservasYaMovidasOrigen = 0;
				    	//Recorrer la disponibilidades destino
				    	for(Disponibilidad disponibilidadDestino : disponibilidadesDestino){
				    	    //Refrescar la disponibilidad porque solo se tiene un stub con datos mínimos
				    		Disponibilidad dispoDestino = entityManager.find(Disponibilidad.class, disponibilidadDestino.getId());
				    		//logger.info("DISPONIBILIDAD DESTINO " + dispoDestino.getId() + "HORA INICIO " + dispoDestino.getHoraInicio());

                //Preguntar si las reservasOrigen caben en la disponibilidad destino en la primera que quepa se mete y luego se hace break;
                if(dispOrigen.getFrecuencia()==dispoDestino.getFrecuencia()){
                  //Verificar los cupos en destino donde quepan todas las reservas de la disponibilidad origen
                  //aqui antes de preguntar si el cupo es mayor a 0, sumar en dado caso que se traslapen
                  if(traslape && dispOrigen.getHoraInicio().compareTo(dispoDestino.getHoraInicio())==0){
                    disponibilidadDestino.setCupo(disponibilidadDestino.getCupo()+dispOrigen.getReservas().size());
                  }

                  if(disponibilidadDestino.getCupo()>0){
                    //Procesar cada reserva que se desea mover
                    for(Reserva reservaOrigen : reservasOrigen) {
                    	
                      if(reservaOrigen.getEstado() == Estado.R && disponibilidadDestino.getCupo()>0) {
                          if(dispoDestino==null) {
                              //No debería haber llegado hasta acá: hay reservas en el origen pero no existe la disponibilidad en el destino
                              throw new BusinessException("no_se_puedo_mover_las_reservas");
                          }
                        //Mover la reserva
                        Reserva reservaDestino = moverReserva(reservaOrigen, dispoDestino, datosSolicitarPorClave);
                        //Registrar las reservas a cancelar
                        reservaOrigen.setIdDestino(reservaDestino.getId());
                        reservasACancelar.add(reservaOrigen);
                        //Registrar las reservas (origen y destino) para enviar las comunicaciones al final
                        //No se envían ahora por si hay que hacer roll-back)
                        reservasMovidasOrigen.add(reservaOrigen);
                        reservasMovidasDestino.add(reservaDestino);
                        reservasMovidas++;
                        reservasYaMovidasOrigen++;
                        reservasReservadas++;
                        disponibilidadDestino.setCupo(disponibilidadDestino.getCupo()-1);
                        
                      }else if(reservaOrigen.getEstado() == Estado.P) {
                        //Simplemente se cancela;
                        reservaOrigen.setEstado(Estado.C);
                        reservaOrigen.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase());
                        reservaOrigen.setFcancela(new Date());
                        reservasMovidas++;
                        reservasPendientes++;
                      }
                      else {
                        //Reservas en estado U o C se ignoran
                      }
                      saeCache.put(uuid, reservasMovidas);
                    }
                    reservasOrigen.removeAll(reservasACancelar);
                    //cancelar reservas origen
                    for(Reserva reservaCancelar : reservasACancelar){
                      
                      reservaCancelar.setEstado(Estado.C);
                      reservaCancelar.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase());
                      reservaCancelar.setFcancela(new Date());
                      String observacionOrigen = "Reserva movida a "+dispoDestino.getRecurso().getAgenda().getNombre()+"/"+
                          dispoDestino.getRecurso().getNombre()+"; id destino: "+reservaCancelar.getIdDestino();
                      if(observacionOrigen.length()>95) {
                        observacionOrigen = observacionOrigen.substring(0, 95);
                      }
                      reservaCancelar.setObservaciones(observacionOrigen);
                      entityManager.merge(reservaCancelar);
                    }
                    //Refrescar la disponibilidad origen porque solo se tiene un stub con datos mínimos
                    //Y poner los cupos en 0
                    //Preguntar primero si se traslapan para reutilizar disponibilidades
                    //primero preguntar si son del mismo día
                  try {
                	  
                    Date fechaDestino = formatoFecha.parse(formatoFecha.format(dispoDestino.getHoraInicio()));
                    Date fechaOrigen = formatoFecha.parse(formatoFecha.format(dispOrigen.getHoraInicio()));
                      if(fechaDestino.compareTo(fechaOrigen)==0){

//                        
                        if(traslape && disponibilidadesDestinoPorHora.get(formatoClave.format(dispOrigen.getHoraInicio()))==null){
                          //logger.info("cupos cuando se traslapa y las horas inicio no son iguales");
                          dispOrigen.setCupo(0);
                        }
                        else if(traslape){
                          // no hace nada
                        }
                        else if(reservasPendientes>0 && reservasReservadas==0){
                            // no hace nada
                        }
                        else if(reservasReservadas==0){
                            // no hace nada
                        }
                        else{
                          dispOrigen.setCupo(0);
                        }
                      }
                      else if(reservasPendientes>0 && reservasReservadas==0){
                          // no hace nada
                      }
                      else if(reservasReservadas==0){
                          // no hace nada
                      }
                      else{
                        dispOrigen.setCupo(0);
                        //entityManager.merge(dispOrigen);
                      }
                  } catch (ParseException e) {
                    throw new ApplicationException("error_mover_reservas");
                  }
                  
                  
                  }

                }
              }
            }
          }
          
        
        //Si se procesaron todas las reservas y no se produjo ningún error se generan las novedades y se mandan las comunicaciones
          if(generarNovedades || enviarComunicaciones) {
            for(int i = 0; i<reservasMovidasOrigen.size(); i++) {
              Reserva reservaOrigen = reservasMovidasOrigen.get(i);
              Reserva reservaDestino = reservasMovidasDestino.get(i);
              if(generarNovedades) {
                novedadesBean.publicarNovedad(empresa, reservaOrigen, Acciones.CANCELACION);
                novedadesBean.publicarNovedad(empresa, reservaDestino, Acciones.RESERVA);
              }
              if(enviarComunicaciones) {
                //ToDo: cambiar esto para enviar mail de movimiento en lugar de cancelación/confirmación
                String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+empresa.getId()+"&a="+recursoDestino.getAgenda().getId()+
                    "&ri="+reservaDestino.getId();
                  String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+empresa.getId()+"&a="+recursoDestino.getAgenda().getId()+
                      "&r="+recursoDestino.getId()+"&ri="+reservaDestino.getId();
                  comunicacionesBean.enviarComunicacionesTraslado(empresa, linkCancelacion, linkModificacion, reservaOrigen, reservaDestino, null, 
                      empresa.getFormatoFecha(), empresa.getFormatoHora());
              }
            }
          }
          
          if(traslape){
//            logger.info("reservas movidas " + reservasMovidas);
//            logger.info("reservas traslapadas " + reservasTraslapadas);
//            logger.info("reservas movidas destino " + reservasMovidasDestino.size());
            
            ret.getMensajes().add("Reservas movidas correctamente; cantidad: "+ (reservasTraslapadas) + ".");
          }
          else{
            ret.getMensajes().add("Reservas movidas correctamente; cantidad: "+reservasMovidasOrigen.size()+".");
          }
          

      }
      else{
      
        //Determinar si los recursos son visibles en internet
        recursoOrigenVisibleInternet = BooleanUtils.isTrue(recursoOrigen.getVisibleInternet());
        recursoDestinoVisibleInternet = BooleanUtils.isTrue(recursoDestino.getVisibleInternet());
        //Deshabilitar ambos recursos en internet (si la tienen)
        if(recursoOrigenVisibleInternet){
          helper.desactivarRecursoVisibleInternet(recursoOrigen);
        }
        if(recursoDestinoVisibleInternet){
          helper.desactivarRecursoVisibleInternet(recursoDestino);
        }
        //Determinar el timezone del recurso origen
        TimeZone timezoneOrigen = TimeZone.getDefault();
        if(recursoOrigen.getAgenda().getTimezone()!=null && !recursoOrigen.getAgenda().getTimezone().isEmpty()) {
          timezoneOrigen = TimeZone.getTimeZone(recursoOrigen.getAgenda().getTimezone());
        }else {
          if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
            timezoneOrigen = TimeZone.getTimeZone(empresa.getTimezone());
          }
        }
        //Determinar el timezone del recurso destino
        TimeZone timezoneDestino = TimeZone.getDefault();
        if(recursoDestino.getAgenda().getId().equals(recursoOrigen.getAgenda().getId())) {
          timezoneDestino = timezoneOrigen;
        }else {
          if(recursoDestino.getAgenda().getTimezone()!=null && !recursoDestino.getAgenda().getTimezone().isEmpty()) {
              timezoneDestino = TimeZone.getTimeZone(recursoDestino.getAgenda().getTimezone());
          }else {
            if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
                timezoneDestino = TimeZone.getTimeZone(empresa.getTimezone());
            }
          }
        }
        
        //Obtener las disponibilidades en ambos recursos
        List<Disponibilidad> disponibilidadesOrigen = obtenerDisponibilidades(recursoOrigen, ventanaOrigen, timezoneOrigen, false);
        List<Disponibilidad> disponibilidadesDestino = obtenerDisponibilidades(recursoDestino, ventanaDestino, timezoneDestino, false);

          //Armar un mapeo entre los datos a solicitar en el recurso origen y los del recurso destino
          //La clave es dato.agrupacion.nombre:dato.nombre
          Map<String, DatoASolicitar> datosSolicitarPorClave = new HashMap<String, DatoASolicitar>();
          for(DatoASolicitar datoSolicitarDestino : recursosEJB.consultarDatosSolicitar(recursoDestino)) {
            String claveDato = datoSolicitarDestino.getAgrupacionDato().getNombre()+":"+datoSolicitarDestino.getNombre();
            datosSolicitarPorClave.put(claveDato, datoSolicitarDestino);
          }
          //Procesar las reservas 
          List<Reserva> reservasOrigen = new ArrayList<Reserva>();
          List<Reserva> reservasMovidasOrigen = new ArrayList<Reserva>();
          List<Reserva> reservasMovidasDestino = new ArrayList<Reserva>();
          List<Reserva> reservasACancelar = new ArrayList();
          int reservasMovidas = 0;
          
          
          for (Disponibilidad disponibilidadOrigen : disponibilidadesOrigen) {
            int reservasPendientes = 0;
            int reservasReservadas = 0;  
            Disponibilidad dispOrigen = entityManager.find(Disponibilidad.class, disponibilidadOrigen.getId());
            //logger.info("DISPONIBILIDAD ORIGEN " + dispOrigen.getId() + " CUPO " + dispOrigen.getCupo());
            reservasOrigen = new ArrayList<Reserva>();
            reservasOrigen = dispOrigen.getReservas();
            if(!reservasOrigen.isEmpty()){
              
              Integer reservasYaMovidasOrigen = 0;
              //Recorrer la disponibilidades destino
              for(Disponibilidad disponibilidadDestino : disponibilidadesDestino){
                  //Refrescar la disponibilidad porque solo se tiene un stub con datos mínimos
                Disponibilidad dispoDestino = entityManager.find(Disponibilidad.class, disponibilidadDestino.getId());
                if(reservasYaMovidasOrigen==reservasOrigen.size()){
                  break;
                }
                    
                //logger.info("DISPONIBILIDAD DESTINO ID " + disponibilidadDestino.getId() + " CUPO " + disponibilidadDestino.getCupo());
                //Preguntar si las reservasOrigen caben en la disponibilidad destino en la primera que quepa se mete y luego se hace break;
                if(dispOrigen.getFrecuencia()==dispoDestino.getFrecuencia()){
                  //Verificar los cupos en destino donde quepan todas las reservas de la disponibilidad origen
                  if(disponibilidadDestino.getCupo()>0){
                    //Procesar cada reserva que se desea mover
                    for(Reserva reservaOrigen : reservasOrigen) {
                      if(reservaOrigen.getEstado() == Estado.R && disponibilidadDestino.getCupo()>0) {
                          if(dispoDestino==null) {
                              //No debería haber llegado hasta acá: hay reservas en el origen pero no existe la disponibilidad en el destino
                              throw new BusinessException("no_se_puedo_mover_las_reservas");
                          }               
                        //Mover la reserva
                        Reserva reservaDestino = moverReserva(reservaOrigen, dispoDestino, datosSolicitarPorClave);
                        //Registrar las reservas a cancelar
                        reservaOrigen.setIdDestino(reservaDestino.getId());
                        reservasACancelar.add(reservaOrigen);
                        
                        //Registrar las reservas (origen y destino) para enviar las comunicaciones al final
                        //No se envían ahora por si hay que hacer roll-back)
                        reservasMovidasOrigen.add(reservaOrigen);
                        reservasMovidasDestino.add(reservaDestino);
                        reservasMovidas++;
                        reservasYaMovidasOrigen++;
                        disponibilidadDestino.setCupo(disponibilidadDestino.getCupo()-1);
                        reservasReservadas++;
                        
                      }else if(reservaOrigen.getEstado() == Estado.P) {
                        //Simplemente se cancela;
                        reservaOrigen.setEstado(Estado.C);
                        reservaOrigen.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase());
                        reservaOrigen.setFcancela(new Date());
                        reservasMovidas++;
                        reservasPendientes++;
                      }
                      else {
                        //Reservas en estado U o C se ignoran
                      }
                      saeCache.put(uuid, reservasMovidas);
                    }
                    reservasOrigen.removeAll(reservasACancelar);
                    
                    //cancelar reservas origen
                    for(Reserva reservaCancelar : reservasACancelar){
                      
                      reservaCancelar.setEstado(Estado.C);
                      reservaCancelar.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase());
                      reservaCancelar.setFcancela(new Date());
                      String observacionOrigen = "Reserva movida a "+dispoDestino.getRecurso().getAgenda().getNombre()+"/"+
                          dispoDestino.getRecurso().getNombre()+"; id destino: "+reservaCancelar.getIdDestino();
                      if(observacionOrigen.length()>95) {
                        observacionOrigen = observacionOrigen.substring(0, 95);
                      }
                      reservaCancelar.setObservaciones(observacionOrigen);
                      entityManager.merge(reservaCancelar);
                    }
                    //Refrescar la disponibilidad origen porque solo se tiene un stub con datos mínimos
                    //Y poner los cupos en 0
                    
                    if(reservasPendientes>0 && reservasReservadas==0){
                        // no hace nada
                    }
                    else if(reservasReservadas==0){
                        // no hace nada
                    }
                    else{
                    	dispOrigen.setCupo(0);
                    }
                  
                  
                  
                  }

                }
              }
            }
          }
              
              
          //Si se procesaron todas las reservas y no se produjo ningún error se generan las novedades y se mandan las comunicaciones
          if(generarNovedades || enviarComunicaciones) {
            for(int i = 0; i<reservasMovidasOrigen.size(); i++) {
              Reserva reservaOrigen = reservasMovidasOrigen.get(i);
              Reserva reservaDestino = reservasMovidasDestino.get(i);
              if(generarNovedades) {
                novedadesBean.publicarNovedad(empresa, reservaOrigen, Acciones.CANCELACION);
                novedadesBean.publicarNovedad(empresa, reservaDestino, Acciones.RESERVA);
              }
              if(enviarComunicaciones) {
                //ToDo: cambiar esto para enviar mail de movimiento en lugar de cancelación/confirmación
                String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+empresa.getId()+"&a="+recursoDestino.getAgenda().getId()+
                    "&ri="+reservaDestino.getId();
                  String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+empresa.getId()+"&a="+recursoDestino.getAgenda().getId()+
                      "&r="+recursoDestino.getId()+"&ri="+reservaDestino.getId();
                  comunicacionesBean.enviarComunicacionesTraslado(empresa, linkCancelacion, linkModificacion, reservaOrigen, reservaDestino, null, 
                      empresa.getFormatoFecha(), empresa.getFormatoHora());
              }
            }
          }
        ret.getMensajes().add("Reservas movidas correctamente; cantidad: "+reservasMovidasOrigen.size()+".");
      }
    }catch(UserException | BusinessException ex) {
      ret.getErrores().add(ex.getCodigoError());
    }catch(ApplicationException aEx) {
      throw aEx;
    }finally {
      //Volver a habilitar ambos recursos en internet (si la tenían)
      if(recursoOrigenVisibleInternet){
        helper.activarRecursoVisibleInternet(recursoOrigen);
      }
      if(recursoDestinoVisibleInternet){
        helper.activarRecursoVisibleInternet(recursoDestino);
      }   
    }
    //Devolver el resultado
    return ret;
  }
    
  private List<Object[]> obtenerCuposDisponiblesPorDisponibilidad(Recurso recurso, VentanaDeTiempo periodo) {
      String sQuery = "SELECT ad.id,ad.hora_inicio, ad.cupo - count(ar.id)," +
              "abs((DATE_PART('day', ad.hora_fin - ad.hora_inicio) * 24 + " +
              "DATE_PART('hour', ad.hora_fin - ad.hora_inicio)) * 60 + " +
              "DATE_PART('minute', ad.hora_fin - ad.hora_inicio)) frecuencia " +  
                " FROM ae_disponibilidades ad LEFT JOIN ae_reservas_disponibilidades ard ON ard.aedi_id = ad.id " 
                      + "LEFT JOIN ae_reservas ar ON ar.id = ard.aers_id AND ar.estado <> 'C' WHERE ad.aere_id = :idRecurso AND ad.fecha = :fecha "
                      + "AND ad.hora_inicio>=:fi AND ad.hora_fin<=:ff "
                      + "GROUP BY ad.id, frecuencia";
      Query query = entityManager.createNativeQuery(sQuery);
        query.setParameter("idRecurso", recurso.getId());
        query.setParameter("fecha", periodo.getFechaInicial(), TemporalType.DATE);
        query.setParameter("fi", periodo.getFechaInicial(), TemporalType.TIMESTAMP);
    query.setParameter("ff", periodo.getFechaFinal(), TemporalType.TIMESTAMP);
        return (List<Object[]>)query.getResultList();
	}
	
	private List<Object[]> obtenerCuposFrecuenciaDisponibles(Recurso recurso, VentanaDeTiempo periodo) {
	    String sQuery = "select CAST(t.frecuencia as INTEGER), CAST (sum(t.cupos) as INTEGER) " +
						"from ( " +
						"SELECT abs((DATE_PART('day', ad.hora_fin - ad.hora_inicio) * 24 + " + 
						"DATE_PART('hour', ad.hora_fin - ad.hora_inicio)) * 60 + " + 
						"DATE_PART('minute', ad.hora_fin - ad.hora_inicio)) frecuencia, (ad.cupo- (select count(r.id) "+ 
						"FROM ae_disponibilidades d " + 
						"LEFT JOIN ae_reservas_disponibilidades rd ON rd.aedi_id = d.id " + 
						"LEFT JOIN ae_reservas r ON r.id = rd.aers_id AND r.estado <> 'C' WHERE d.aere_id = :idRecurso " + 
						"and d.id=ad.id)) cupos "+
						"FROM ae_disponibilidades ad LEFT JOIN ae_reservas_disponibilidades ard ON ard.aedi_id = ad.id " + 
						"LEFT JOIN ae_reservas ar ON ar.id = ard.aers_id AND ar.estado <> 'C' WHERE ad.aere_id = :idRecurso " + 
						"AND ad.hora_inicio>=:fi AND ad.hora_fin<=:ff  " + 
						"group by frecuencia,ad.id) as t " +
						"group by t.frecuencia";
	    Query query = entityManager.createNativeQuery(sQuery);

        query.setParameter("idRecurso", recurso.getId());
        query.setParameter("fi", periodo.getFechaInicial(), TemporalType.TIMESTAMP);
    query.setParameter("ff", periodo.getFechaFinal(), TemporalType.TIMESTAMP);
        return (List<Object[]>)query.getResultList();
  }
  
  private List<Object[]> obtenerReservasConfirmadasPorFrecuencia(Recurso recurso, VentanaDeTiempo periodo) {
        String sQuery = "select CAST(t.frecuencia as INTEGER), CAST(sum(t.reservas) as INTEGER) "+
						"from ( " +
						"SELECT " + 
						"abs((DATE_PART('day', ad.hora_fin - ad.hora_inicio) * 24 + " + 
						"DATE_PART('hour', ad.hora_fin - ad.hora_inicio)) * 60 + " + 
						"DATE_PART('minute', ad.hora_fin - ad.hora_inicio)) frecuencia, " +
						"(select count(r.id) FROM ae_disponibilidades d " +
						"JOIN ae_reservas_disponibilidades rd ON rd.aedi_id = d.id " + 
						"JOIN ae_reservas r ON r.id = rd.aers_id AND r.estado <> 'C' WHERE d.aere_id = :idRecurso and d.id=ad.id) reservas " +
						"FROM ae_disponibilidades ad  JOIN ae_reservas_disponibilidades ard ON ard.aedi_id = ad.id " + 
						"JOIN ae_reservas ar ON ar.id = ard.aers_id WHERE ad.aere_id = :idRecurso AND ad.presencial = false " +
						"AND ad.fecha_baja is NULL AND ar.estado = 'R' " + 
						"AND ad.hora_inicio>=:fi AND ad.hora_fin<=:ff " + 
						"GROUP BY frecuencia,ad.id) as t " +
						"GROUP BY t.frecuencia";

        Query query = entityManager.createNativeQuery(sQuery);
        query.setParameter("idRecurso", recurso.getId());
        query.setParameter("fi", periodo.getFechaInicial(), TemporalType.TIMESTAMP);
    query.setParameter("ff", periodo.getFechaFinal(), TemporalType.TIMESTAMP);
        return (List<Object[]>)query.getResultList();
    }

	
	private List<Object[]> obtenerReservasConfirmadasPorDisponibilidad(Recurso recurso, VentanaDeTiempo periodo) {
        String sQuery = "SELECT ad.hora_inicio, ar.estado, count(*)," +
                "abs((DATE_PART('day', ad.hora_fin - ad.hora_inicio) * 24 + " +
            "DATE_PART('hour', ad.hora_fin - ad.hora_inicio)) * 60 + " +
            "DATE_PART('minute', ad.hora_fin - ad.hora_inicio)) frecuencia " + 
                "FROM ae_disponibilidades ad JOIN ae_reservas_disponibilidades ard ON ard.aedi_id = ad.id "
                        + "JOIN ae_reservas ar ON ar.id = ard.aers_id WHERE ad.aere_id = :idRecurso AND ad.fecha = :fecha AND ad.presencial = false " 
                        + "AND ad.fecha_baja is NULL AND ar.estado = 'R' "
                        + "AND ad.hora_inicio>=:fi AND ad.hora_fin<=:ff "
                        + "GROUP BY ad.id, ad.hora_inicio, ar.estado";
        Query query = entityManager.createNativeQuery(sQuery);
        query.setParameter("idRecurso", recurso.getId());
        query.setParameter("fecha", periodo.getFechaInicial(), TemporalType.DATE);
        query.setParameter("fi", periodo.getFechaInicial(), TemporalType.TIMESTAMP);
    query.setParameter("ff", periodo.getFechaFinal(), TemporalType.TIMESTAMP);
        return (List<Object[]>)query.getResultList();
    }
    
    
    public Long obtenerReservasConfirmadasRecursoOrigen(Recurso recurso, VentanaDeTiempo periodo) throws UserException {
      try{
      
     
          String sQuery = "SELECT COUNT(*) FROM Reserva r JOIN r.disponibilidades d WHERE d.recurso.id=:idRecurso "+ 
                    "AND r.estado = 'R' AND d.fechaBaja is NULL " +
                          "AND d.presencial = false AND d.horaInicio>=:fi " +
                  "AND d.horaFin<=:ff";
          
          logger.info("HORA INICIO " + periodo.getFechaInicial());
          logger.info("HORA FIN " + periodo.getFechaFinal());
          
          Query query = entityManager.createQuery(sQuery);
          query.setParameter("idRecurso", recurso.getId());
          //query.setParameter("fecha", periodo.getFechaInicial(), TemporalType.DATE);
          query.setParameter("fi", periodo.getFechaInicial());
      query.setParameter("ff", periodo.getFechaFinal());
      Long cant = (Long) query.getSingleResult();
          
          
          if(cant>0){
              return cant;
          }
          else{
            return new Long("0");
          }
      }
      catch (Exception ex) {
          logger.error("No se pudo obtener las reservas del recurso origen", ex);
          throw new UserException("error_no_solucionable");
      }
    }
    
    
    private Reserva moverReserva(Reserva reservaOrigen, Disponibilidad disponibilidadDestino, Map<String, DatoASolicitar> datosSolicitarPorClave) throws UserException {
      
      try{
        //Crear la reserva destino
        Reserva reservaDestino = new Reserva();
        //Copiar los datos que se mantienen sin cambios
        reservaDestino.setSerie(reservaOrigen.getSerie());
        reservaDestino.setNumero(reservaOrigen.getNumero());
        reservaDestino.setEstado(reservaOrigen.getEstado());
        reservaDestino.setFechaCreacion(reservaOrigen.getFechaCreacion());
        reservaDestino.setFechaActualizacion(reservaOrigen.getFechaActualizacion());
        reservaDestino.setOrigen(reservaOrigen.getOrigen());
        reservaDestino.setUcrea(reservaOrigen.getUcrea());
        reservaDestino.setFcancela(reservaOrigen.getFcancela());
        reservaDestino.setTcancela(reservaOrigen.getTcancela());
        reservaDestino.setCodigoSeguridad(reservaOrigen.getCodigoSeguridad());
        reservaDestino.setTrazabilidadGuid(reservaOrigen.getTrazabilidadGuid());
        reservaDestino.setTramiteCodigo(reservaOrigen.getTramiteCodigo());
        reservaDestino.setTramiteNombre(reservaOrigen.getTramiteNombre());
        reservaDestino.setIpOrigen(reservaOrigen.getIpOrigen());
        reservaDestino.setMiPerfilNotificada(reservaOrigen.isMiPerfilNotificada());
        reservaDestino.setNotificar(reservaOrigen.getNotificar());      
        //Las reservas hijas no cambian de recurso o agenda
        reservaDestino.setReservaHija(reservaOrigen.getReservaHija());      
        //En el caso de que sea parte de una reserva múltiple se reutiliza el mismo token
        //Si necesitan cancelar todo el bloque la nueva reserva debe ser considerada parte del mismo
        reservaDestino.setToken(reservaOrigen.getToken());
        
        //Campos que apuntan al destino
        reservaDestino.getDisponibilidades().add(disponibilidadDestino);
        disponibilidadDestino.getReservas().add(reservaDestino);
        
        //Campos calculados
        reservaDestino.setFechaLiberacion(reservaOrigen.getFechaLiberacion());
        
        Disponibilidad disponibilidadOrigen = reservaOrigen.getDisponibilidades().get(0);
        String observacionDestino = "Reserva movida de "+disponibilidadOrigen.getRecurso().getAgenda().getNombre()+"/"+
            disponibilidadOrigen.getRecurso().getNombre()+"; id original: "+reservaOrigen.getId();
        if(observacionDestino.length()>95) {
          observacionDestino = observacionDestino.substring(0, 95);
        }
        reservaDestino.setObservaciones(observacionDestino);
        
        //Campos que se limpian
        reservaDestino.setVersion(0);
        reservaDestino.setLlamada(null);
        reservaDestino.setAtenciones(null);
        
        //Campos nuevos
        //Id de la reserva original
        reservaDestino.setIdOrigen(reservaOrigen.getId());      
        
        //Guardar la reserva
        entityManager.persist(reservaDestino);
  
        //Datos de la reserva (hay que hacerlo después de haber guardado la reserva sino da error)
        for(DatoReserva datoReservaOriginal : reservaOrigen.getDatosReserva()) {
          DatoASolicitar datoSolicitarOriginal = datoReservaOriginal.getDatoASolicitar();
          //La clave es agrupacion.nombre:dato.nombre
          String claveDato = datoSolicitarOriginal.getAgrupacionDato().getNombre()+":"+datoSolicitarOriginal.getNombre();
          DatoASolicitar datoSolicitarDestino = datosSolicitarPorClave.get(claveDato);
          //Si el dato a solicitar no existe en el destino, se ignora
          if(datoSolicitarDestino != null) {
            DatoReserva datoReservaDestino = new DatoReserva();
            datoReservaDestino.setReserva(reservaDestino);
            datoReservaDestino.setDatoASolicitar(datoSolicitarDestino);
            datoReservaDestino.setValor(datoReservaOriginal.getValor());
            reservaDestino.getDatosReserva().add(datoReservaDestino);
            //Guardar el dato
              entityManager.persist(datoReservaDestino);
          }
        }     
        
        
        return reservaDestino;
      }
      catch (Exception ex) {
          logger.error("No se pudo mover la reserva", ex);
          throw new UserException("error_no_solucionable");
      }
      
      
    }
  
}
