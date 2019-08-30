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


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import uy.gub.agesic.novedades.Acciones;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.ejb.servicios.ServiciosNovedadesBean;
import uy.gub.imm.sae.business.ejb.servicios.ServiciosTrazabilidadBean;
import uy.gub.imm.sae.business.utilidades.MailUtiles;
import uy.gub.imm.sae.business.utilidades.Metavariables;
import uy.gub.imm.sae.common.SofisHashMap;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.Evento;
import uy.gub.imm.sae.common.enumerados.TipoCancelacion;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Comunicacion;
import uy.gub.imm.sae.entity.Comunicacion.Tipo1;
import uy.gub.imm.sae.entity.Comunicacion.Tipo2;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.PreguntaCaptcha;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoTenant;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.TextoGlobal;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.AutocompletarException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAccionException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionException;

@Stateless
public class AgendarReservasBean implements AgendarReservasLocal, AgendarReservasRemote{
	
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
		//Registrar la cancelacion en el sistema de trazas del PEU
		String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), r.getTramiteCodigo(), r.getId());
		if(transaccionId != null) {
			trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.CANCELACION);
		}
		//Publicar la novedad
		novedadesBean.publicarNovedad(empresa, reserva, Acciones.CANCELACION);
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
	public Reserva marcarReserva(Disponibilidad disponibilidad, TokenReserva token) throws UserException {
		if (disponibilidad == null) {
			throw new UserException("debe_especificar_la_disponibilidad");
		}
		disponibilidad = entityManager.find(Disponibilidad.class, disponibilidad.getId());
		if (disponibilidad == null) {
			throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
		}		
		//Se crea la reserva en una transaccion independiente
    Reserva reserva = helper.crearReservaPendiente(disponibilidad, token);
		//Chequeo que el cupo real no de negativo
		//Si el cupo real da negativo, elimino la reserva pendiente y cancelo la operacion
		//De lo contrario la reserva se ha marcado con exito
		if (helper.chequeoCupoDisponible(disponibilidad, true)) {
			reserva = entityManager.find(Reserva.class, reserva.getId());
			entityManager.remove(reserva);
			entityManager.flush();
			throw new UserException("el_horario_acaba_de_quedar_sin_cupos");
		}
		return reserva;
	}
		
  public Reserva marcarReservaValidandoDatos(Disponibilidad disponibilidad, Reserva reserva, TokenReserva token) throws UserException {
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
    Reserva reservaNueva = helper.crearReservaPendiente(disponibilidad, token);
    List<Reserva> reservasPrevias = helper.validarDatosReservaPorClave(recurso, disponibilidad, campos, valores, reserva.getTramiteCodigo());
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
    //Chequeo que el cupo real no de negativo
    //Si el cupo real da negativo, elimino la reserva pendiente y cancelo la operacion
    //De lo contrario la reserva se ha marcado con exito
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
    List<Reserva> reservasPrevias = helper.validarDatosReservaPorClave(recurso, disponibilidad, campos, valores, reserva.getTramiteCodigo());
    //Si Hay reservas repetidas lanzo una excepcion.
    if (!reservasPrevias.isEmpty()) {
      //Se carga lista de camposClave
      List<String> nombreCamposClave = new ArrayList<String>();
      for (DatoASolicitar campo : campos) {
        if (campo.getEsClave() ) {
          nombreCamposClave.add(campo.getNombre());
        }
      }
      throw new ValidacionClaveUnicaException("no_es_posible_confirmar_su_reserva", nombreCamposClave);     
    }
		//Validaciones extendidas: definidas por el usuario
		List<ValidacionPorRecurso> validaciones = helper.obtenerValidacionesPorRecurso(recurso);
		ReservaDTO reservaDTO = new ReservaDTO();
		reservaDTO.setEstado(reserva.getEstado().toString());
		reservaDTO.setFecha(reserva.getDisponibilidades().get(0).getFecha());
		reservaDTO.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
		reservaDTO.setId(reserva.getId());
		if (ctx.isCallerInRole("RA_AE_FCALL_CENTER")){
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
		  if("NroDocumento".equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre()))  {
		    numeroDocumento = datoReserva.getValor();
		  }
      if("TipoDocumento".equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre()))  {
        tipoDocumento = datoReserva.getValor();
      }
		}
		if("CI".equalsIgnoreCase(tipoDocumento) && numeroDocumento!=null) {
		  numeroDocumento = numeroDocumento.replaceAll("[.-]", "");
		  for(DatoReserva datoReserva : reserva.getDatosReserva()) {
	      if("NroDocumento".equalsIgnoreCase(datoReserva.getDatoASolicitar().getNombre()))  {
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
    if (ctx.isCallerInRole("RA_AE_FCALL_CENTER")){
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

	@SuppressWarnings("unchecked")
	public List<Disponibilidad> obtenerDisponibilidades(Recurso recurso, VentanaDeTiempo ventana, TimeZone timezone) throws UserException {
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
		if (ventana.getFechaInicial().before(ahora)) {
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
				"  AND d.fecha between :fi AND :ff " +
		    "  AND (d.fecha <> :fi OR d.horaInicio >= :fiCompleta) " +
				"ORDER BY d.fecha ASC, d.horaInicio ";
		List<Disponibilidad> disponibilidades =  entityManager.createQuery(eql)
		.setParameter("r", recurso)
		.setParameter("fi", ventana.getFechaInicial(), TemporalType.DATE)
		.setParameter("ff", ventana.getFechaFinal(), TemporalType.DATE)
		.setParameter("fiCompleta", ventana.getFechaInicial(), TemporalType.TIMESTAMP)
		.getResultList();
		//Determinar las reservas vivas
		//No se debe considerar las disponibilidades presenciales
		String cons = "SELECT d.id, d.fecha, d.horaInicio, COUNT(reserva) " +
				"FROM Disponibilidad d " +
				"JOIN d.reservas reserva " +
				"WHERE d.recurso IS NOT NULL " +
				"  AND d.recurso = :r " +
				"  AND d.fechaBaja IS NULL " +
				"  AND d.fecha BETWEEN :fi AND :ff " +
		    "  AND (d.fecha <> :fi OR d.horaInicio >= :fiCompleta) " +
				"  AND (reserva.estado <> :cancelado) " +
				"GROUP BY d.id, d.fecha, d.horaInicio " +
				"ORDER BY d.fecha asc, d.horaInicio ASC ";
		List<Object[]> cantReservasVivas =  entityManager.createQuery(cons)
		.setParameter("r", recurso)
		.setParameter("fi", ventana.getFechaInicial(), TemporalType.DATE)
		.setParameter("ff", ventana.getFechaFinal(), TemporalType.DATE)
		.setParameter("fiCompleta", ventana.getFechaInicial(), TemporalType.TIMESTAMP)
		.setParameter("cancelado", Estado.C)
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
	 *      	La diferencia entre fin - inicio sea mayor o igual a ventanaDiasMinimos.
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
	 *      	La diferencia entre fin - inicio sea mayor o igual a ventanaDiasMinimos.
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
	@RolesAllowed({"RA_AE_ADMINISTRADOR","RA_AE_FCALL_CENTER", "RA_AE_PLANIFICADOR","RA_AE_FATENCION", "RA_AE_ANONIMO"})
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
		//Obtengo las reservas hechas para cada día y hora
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

	public void enviarComunicacionesConfirmacion(String linkCancelacion, String linkModificacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException, UserException {
		//Enviar mail
		enviarMailConfirmacion(linkCancelacion, linkModificacion, reserva, idioma, formatoFecha, formatoHora);
		//Almacenar datos para SMS y TextoAVoz, si corresponde
		almacenarSmsYTav(Comunicacion.Tipo2.RESERVA, reserva, formatoFecha, formatoHora);
	}
	
  public void enviarComunicacionesConfirmacion(String templateLinkCancelacion, String templateLinkModificacion, TokenReserva tokenReserva, String idioma, String formatoFecha, String formatoHora) throws UserException {
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
  
	public void enviarComunicacionesCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws UserException {
		//Enviar mail
		enviarMailCancelacion(reserva, idioma, formatoFecha, formatoHora, null, null);
		//Almacenar datos para SMS y TextoAVoz, si corresponde
		almacenarSmsYTav(Comunicacion.Tipo2.CANCELA, reserva, formatoFecha, formatoHora);
	}
	
	private String enviarMailConfirmacion(String linkCancelacion, String linkModificacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException {
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
  				if("Mail".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
  					email = dato.getValor();
						MailUtiles.enviarMail(email, "Confirmación de reserva", cuerpo, MailUtiles.CONTENT_TYPE_HTML);
  					Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, cuerpo);
  					comunicacion.setProcesado(true);
  					entityManager.persist(comunicacion);
  				}
  			}
        return cuerpo;
      }else {
        return null;
      }
		}catch(MessagingException mEx) {
			try {
				if(recurso != null) {
					if(email == null) {
						email = "***";
					}
					Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, null);
					entityManager.persist(comunicacion);
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			throw new ApplicationException("no_se_pudo_enviar_el_correo_electronico_de_confirmacion_tome_nota_de_los_datos_de_la_reserva", mEx);
		}
	}

	private String enviarMailCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) throws UserException {
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
				if("Mail".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
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
	      throw new UserException("no_hay_una_direccion_de_correo_a_la_cual_enviar_el_mensaje");
			}
			return cuerpo;
		}catch(MessagingException mEx) {
			throw new UserException("no_se_pudo_enviar_el_correo_electronico_de_confirmacion_tome_nota_de_los_datos_de_la_reserva");
		}
	}
	
	private void almacenarSmsYTav(Comunicacion.Tipo2 tipo2, Reserva reserva, String formatoFecha, String formatoHora) throws UserException {
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
		}catch(Exception ex) {
			throw new UserException("no_se_pudo_generar_las_comunicaciones");
		}
	}

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
          enviarMailCancelacion(reserva, idioma, formatoFecha, formatoHora, asunto, cuerpo);
          //Almacenar datos para SMS y TextoAVoz, si corresponde
          almacenarSmsYTav(Comunicacion.Tipo2.CANCELA, reserva, formatoFecha, formatoHora);
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
    reserva = marcarReserva(disponibilidad, tokenReserva);
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
  
  
  public TokenReserva generarTokenReserva(Integer idRecurso, String cedula, String nombre, String correoe, String codigoTramite) {
    
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
        Empresa empresa = globalEntityManager.find(Empresa.class, empresaId);
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
  
  
  /*
  public void enviarComunicacionesConfirmacionReservaMultiple(String linkCancelacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException, UserException {
    //Enviar mail
    enviarMailConfirmacion(linkCancelacion, reserva, idioma, formatoFecha, formatoHora);
    //Almacenar datos para SMS y TextoAVoz, si corresponde
    almacenarSmsYTav(Comunicacion.Tipo2.RESERVAMULTIPLE, reserva, formatoFecha, formatoHora);
  }
  
  private void enviarMailConfirmacion(String linkCancelacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException {
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
      for(DatoReserva dato : reserva.getDatosReserva()) {
        DatoASolicitar datoSol = dato.getDatoASolicitar();
        if("Mail".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
          email = dato.getValor();
          String cuerpo = textoAgenda.getTextoCorreoConf();
          if(cuerpo != null && !cuerpo.isEmpty()) {
            cuerpo = Metavariables.remplazarMetavariables(cuerpo, reserva, formatoFecha, formatoHora, linkCancelacion);
            MailUtiles.enviarMail(email, "Confirmación de reserva", cuerpo, MailUtiles.CONTENT_TYPE_HTML);
          }
          Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, cuerpo);
          comunicacion.setProcesado(true);
          entityManager.persist(comunicacion);
        }
      }
    }catch(MessagingException mEx) {
      try {
        if(recurso != null) {
          if(email == null) {
            email = "***";
          }
          Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva, null);
          entityManager.persist(comunicacion);
        }
      }catch(Exception ex) {
        ex.printStackTrace();
      }
      throw new ApplicationException("no_se_pudo_enviar_el_correo_electronico_de_confirmacion_tome_nota_de_los_datos_de_la_reserva", mEx);
    }
  }
  */
  
  /**
   * Este método es utilizado para modificar una reserva. Solo se permite modificar la disponibilidad, no el recurso
   * porque los datos a solicitar podrían ser diferentes ni los datos ingresados porque podrían ser de otra persona. 
   * Está pensado para ser invocado mediante el servicio web REST modificarReserva.
   * @return
   * @throws UserException
   */
  public Reserva modificarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva, Integer idDisponibilidad, 
      TokenReserva tokenReserva, String idioma) throws UserException {
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
    Reserva reservaNueva = marcarReservaValidandoDatos(disponibilidad, reservaOriginal, null);
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
    if (ctx.isCallerInRole("RA_AE_FCALL_CENTER")){
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
  
}
