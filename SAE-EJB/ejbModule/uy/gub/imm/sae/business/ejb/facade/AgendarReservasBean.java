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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
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
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Comunicacion;
import uy.gub.imm.sae.entity.Comunicacion.Tipo1;
import uy.gub.imm.sae.entity.Comunicacion.Tipo2;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.PreguntaCaptcha;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoTenant;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.TextoGlobal;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.AutocompletarException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.exception.UserCommitException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionException;

@Stateless
@RolesAllowed({"RA_AE_ADMINISTRADOR","RA_AE_PLANIFICADOR","RA_AE_FCALL_CENTER","RA_AE_FATENCION", "RA_AE_ANONIMO", "RA_AE_LLAMADOR"})
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
	
	@Resource
	private SessionContext ctx;
	
	//@Resource(mappedName="java:/sae/mail")
  //private Session mailSession;
	
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
			recurso = (Recurso) entityManager.createQuery("select distinct d.recurso from Reserva r join r.disponibilidades d where r.id=:reservaId and r.estado ='R'")
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
			recurso = (Recurso) entityManager.createQuery("from Recurso r where r.id = :id "+
											   "and r.fechaBaja is null "+
											   "and r.agenda = :a")
								  .setParameter("id", id)
								  .setParameter("a", a)
								  .getSingleResult();
			
		} catch (NoResultException e) {
			throw new BusinessException("-1", "No se encuentra el recurso de id "+id);
		} catch (Exception e) {
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

		//TODO chequear permisos
		
		List<Agenda> agendas = null;
		try {
			agendas = (List<Agenda>) entityManager.createQuery("from Agenda a where a.fechaBaja is null order by a.nombre")
					.getResultList();
		} catch (Exception e) {
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
		
		//chequearPermiso(a);

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

	public Boolean agendaActiva(Agenda a) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelarReserva(Empresa empresa, Recurso recurso, Reserva reserva) throws BusinessException, ApplicationException {
		
		if (recurso == null) {
			throw new BusinessException("AE20084", "El recurso no puede ser nulo");
		}

		if (reserva == null) {
			throw new BusinessException("AE20085", "La reserva no puede ser nula");
		}

		
		Reserva r = entityManager.find(Reserva.class, reserva.getId());
		
		if (r == null) {
			throw new BusinessException("AE10086", "No se encontro la reserva indicada");
		}
		if (r.getEstado() != Estado.R) {
			throw new BusinessException("AE10087", "Solo se pueden cancelar reservas en estado Reservada");
		}
		
		Integer id = reserva.getDisponibilidades().get(0).getRecurso().getId();
		if (recurso.getId().intValue() != id.intValue()) {
			throw new BusinessException("AE10088", "El recurso asociado a la reserva no se corresponde con el recurso indicado");			
		}
		
		//chequearPermiso(recurso.getAgenda());
		
		ReservaDTO reservaDTO = new ReservaDTO();
		reservaDTO.setEstado(r.getEstado().toString());
		reservaDTO.setFecha(r.getDisponibilidades().get(0).getFecha());
		reservaDTO.setHoraInicio(r.getDisponibilidades().get(0).getHoraInicio());
		reservaDTO.setId(r.getId());
		reservaDTO.setOrigen(r.getOrigen());
		reservaDTO.setNumero(r.getDisponibilidades().get(0).getNumerador() + 1);
		reservaDTO.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase()); //el usuario de sesion es el que esta cancelando la reserva anterior y ese dato lo enviamos hacia GAP
		if (r.getLlamada() != null) {
			reservaDTO.setPuestoLlamada(r.getLlamada().getPuesto());
		}
		
		//Cancelo la reserva, paso el estado a Cancelada.
		r.setEstado(Estado.C);
		r.setObservaciones(reserva.getObservaciones());
		r.setUcancela(ctx.getCallerPrincipal().getName().toLowerCase()); //se guarda en SAE el usuario que cancela la reserva anterior 

		//Registrar la cancelacion en el sistema de trazas del PEU
		Agenda agenda = r.getDisponibilidades().get(0).getRecurso().getAgenda();
		String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), agenda.getTramiteCodigo(), r.getId());
		if(transaccionId != null) {
			trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.CANCELACION);
		}
		
		//Publicar la novedad
		novedadesBean.publicarNovedad(empresa, reserva, Acciones.CANCELACION);
		
	}

	/**
	 * Consulta una reserva por numero y para un recurso.
	 * Si se pasa el recurso, se valida que la reserva con <b>numero</b> corresponda al recurso indicado.
	 * En caso contrario simplemente se busca la reserva por id y se la retorna (sin validar nada)
	 */
	public Reserva consultarReservaPorNumero(Recurso r, Integer numero) throws BusinessException {
		
		Reserva reserva = entityManager.find(Reserva.class, numero);
		if (reserva == null) {
			throw new BusinessException("-1", "No se encuentra la reserva indicada");
		}
		
		if(r!=null){
			boolean esReservaDeRecurso = false;
			
			for (Iterator<Disponibilidad> iterator = reserva.getDisponibilidades().iterator(); iterator.hasNext() && !esReservaDeRecurso;) {
				Disponibilidad disp = iterator.next();
				
				if(disp.getRecurso().getId()==r.getId()){
					esReservaDeRecurso = true;
				}
			}
			
			if(esReservaDeRecurso){
				throw new BusinessException("-1", "El numero de reserva no se corresponde con el recurso indicado");
			}
		}
		
		return reserva;
	}

	public List<Reserva> consultarReservasEnPeriodo(Recurso r, VentanaDeTiempo v) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Crea una nueva reserva en estado pendiente, controla que aun exista cupo.
	 * 
	 * Para controlar la existencia de cupo sin necesidad de utilizar bloqueo
	 * persiste la reserva y luego chequea que el cupo real no sea negativo, si esto
	 * sucede, elimina fisicamente la reserva y cancela la operacion.
	 * @throws BusinessException 
	 * @throws UserException 
	 */
	public Reserva marcarReserva(Disponibilidad d) throws BusinessException, UserException {
		
		if (d == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		
		d = entityManager.find(Disponibilidad.class, d.getId());
		if (d == null) {
			throw new BusinessException("-1", "No se encuentra la disponibilidad indicada");
		}		
		
//		chequearPermiso(d.getRecurso().getAgenda());
		
		//Se crea la reserva en una transaccion independiente
		Reserva reserva = helper.crearReservaPendiente(d);
		
		//Chequeo que el cupo real no de negativo
		//Si el cupo real da negativo, elimino la reserva pendiente y cancelo la operacion
		//De lo contrario la reserva se ha marcado con exito
		
		if (helper.chequeoCupoNegativo(d)) {
			reserva = entityManager.find(Reserva.class, reserva.getId());
			entityManager.remove(reserva);
			entityManager.flush();
			throw new UserCommitException("AE10069");
		}
		return reserva;
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
	
	
	public void validarDatosReserva(Empresa empresa, Reserva reserva) 
			throws BusinessException, ValidacionException, ApplicationException {
			
			if (reserva == null || reserva.getDatosReserva()==null) {
				throw new BusinessException("-1", "Parametro nulo");
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
			Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
			List<DatoASolicitar> campos = helper.obtenerDatosASolicitar(recurso);
			//Los elementos DatoASolicitar son managed, los elementos DatoReservas son unmanaged pues son nuevos
			Map<String, DatoReserva> valores = new HashMap<String, DatoReserva>();
			for (DatoReserva valor : datosNuevos) {
				valores.put(valor.getDatoASolicitar().getNombre(), valor);
			}
			//Validacion basica: campos obligatorios y formato
			helper.validarDatosReservaBasico(campos, valores);

			List<ValidacionPorRecurso> validaciones = helper.obtenerValidacionesPorRecurso(recurso);

			ReservaDTO reservaDTO = new ReservaDTO();
			reservaDTO.setEstado(reserva.getEstado().toString());
			reservaDTO.setFecha(reserva.getDisponibilidades().get(0).getFecha());
			reservaDTO.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
			reservaDTO.setId(reserva.getId());
			//Marcamos el origen de la reserva
			//"C" si tiene el rol de call center
			//"W" si tiene rol anonimo
			//"I" si tiene cualquier otro rol (FATENCION,PLANIFICADOR,ADMINISTRADOR)
			if (ctx.isCallerInRole("RA_AE_FCALL_CENTER")){
				reservaDTO.setOrigen("C");
			}else if (ctx.isCallerInRole("RA_AE_ANONIMO")){
				reservaDTO.setOrigen("W");
			}else{
				reservaDTO.setOrigen("I");
			}
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
	 * Si la reserva debe tener datos, estos son exigidos y validados en este metodo, incluyendo la verificacion de clave unica
	 * @throws ApplicationException 
	 * @throws BusinessException
	 * @return Reserva: Devuelve la reserva pues se le ha asignado un numero unico de reserva dentro de la hora a la que pertenece. 
	 * @throws UserException
	 * @throws ValidacionException 
	 */
	public Reserva confirmarReserva(Empresa empresa, Reserva reserva, String transaccionPadreId, Long pasoPadre) 
		throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException {
		
		if (reserva == null || reserva.getDatosReserva()==null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		
		Set<DatoReserva> datosNuevos = reserva.getDatosReserva();
		
		validarDatosReserva(empresa, reserva); 
		
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
		//Validacion basica: campos clave
		List<Reserva> reservasPrevias = helper.validarDatosReservaPorClave(recurso, reserva, campos, valores);
		// la validacion nunca deberia devolver mas de una reserva previa. en caso de que esto pase, se retorna un error al usuario
		if (reservasPrevias.size() > 1) {
			throw new UserException("no_es_posible_cancelar_sus_reservas_anteriores");
		}
		//Si Hay reservas repetidas, y la bandera esta en false lanzo una excepcion.
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

		//Pase las validaciones, procedo a persistir los DatoReserva
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
		
		reserva.setEstado(Estado.R);
		reserva.setNumero(disponibilidad.getNumerador());
		//Marcamos el origen de la reserva
		//"C" si tiene el rol de call center
		//"W" si tiene rol anonimo
		//"I" si tiene cualquier otro rol (FATENCION,PLANIFICADOR,ADMINISTRADOR)
		if (ctx.isCallerInRole("RA_AE_FCALL_CENTER")){
			reserva.setOrigen("C");
		}else if (ctx.isCallerInRole("RA_AE_ANONIMO")){
			reserva.setOrigen("W");
		}else{
			reserva.setOrigen("I");
		}
		
		reserva.setUcrea(ctx.getCallerPrincipal().getName().toLowerCase());
		
		//Asigno un codigo de seguridad
		String codigoSeguridad = ""+(new Date()).getTime();
		if(codigoSeguridad.length()>5) {
			codigoSeguridad = codigoSeguridad.substring(codigoSeguridad.length()-5);
		}
		reserva.setCodigoSeguridad(codigoSeguridad);
		
		//Registro en el sistema de trazas
    Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
		String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), agenda.getTramiteCodigo(), reserva.getId());
    
		if(empresa.getOid() != null && transaccionId != null) {

			//Para debug, estos son valores admitidos por Agesic
			//procesoId = "ID_PROCESO_TEST_1";
			//transaccionId = "2.16.858.0.0.2.3:ID_PROCESO_TEST_1:"+r.getId();
			
			//Registrar el cabezal en el sistema de trazabilidad del PEU
			String trazaGuid = trazaBean.registrarCabezal(empresa, reserva, transaccionId, agenda.getTramiteCodigo(), transaccionPadreId, pasoPadre);
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
	public List<Disponibilidad> obtenerDisponibilidades(Recurso r, VentanaDeTiempo v, TimeZone timezone) throws BusinessException {
		
		
		if (r == null || v == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		
		r = entityManager.find(Recurso.class, r.getId());
		if (r == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTime().getTime()));
		Date ahora = cal.getTime();
		
		if (v.getFechaInicial().before(ahora)) {
			v.setFechaInicial(ahora);
		}
		if (v.getFechaInicial().before(r.getFechaInicioDisp())) {
			v.setFechaInicial(r.getFechaInicioDisp());
		}

		
		String eql = "select d " +
				"from  Disponibilidad d " +
				"where d.recurso is not null and " +
				"      d.recurso = :r and " +
				"      d.fechaBaja is null and " +
				"      d.fecha between :fi and :ff and " +
		    	"      (d.fecha <> :fi or d.horaInicio >= :fiCompleta) " +
				"order by d.fecha asc, d.horaInicio ";
		
		List<Disponibilidad> disponibilidades =  entityManager.createQuery(eql)
		.setParameter("r", r)
		.setParameter("fi", v.getFechaInicial(), TemporalType.DATE)
		.setParameter("ff", v.getFechaFinal(), TemporalType.DATE)
		.setParameter("fiCompleta", v.getFechaInicial(), TemporalType.TIMESTAMP)
		.getResultList();

		String cons = "select d.id, d.fecha, d.horaInicio, count(reserva) " +
				"from  Disponibilidad d JOIN d.reservas reserva " +
				"where d.recurso is not null and " +
				"      d.recurso = :r and " +
				"      d.fechaBaja is null and " +
				"      d.fecha between :fi and :ff and " +
		    	"      (d.fecha <> :fi or d.horaInicio >= :fiCompleta) and " +
				"      (reserva.estado <> :cancelado) " +
				"group by d.id, d.fecha, d.horaInicio " +
				"order by d.fecha asc, d.horaInicio asc ";
		
		List<Object[]> cantReservasVivas =  entityManager.createQuery(cons)
		.setParameter("r", r)
		.setParameter("fi", v.getFechaInicial(), TemporalType.DATE)
		.setParameter("ff", v.getFechaFinal(), TemporalType.DATE)
		.setParameter("fiCompleta", v.getFechaInicial(), TemporalType.TIMESTAMP)
		.setParameter("cancelado", Estado.C)
		.getResultList();
		
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
	public VentanaDeTiempo obtenerVentanaCalendarioIntranet(Recurso r) throws BusinessException {

		if (r == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		r = entityManager.find(Recurso.class, r.getId());
		if (r == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}
	//	chequearPermiso(r.getAgenda());	

		//Ajusto el tamaño segun la cantidad de cupos minimos y la cantidad de cupos existentes.
		//Dentro de la ventana obtenida busco la primer y ultima disponibilidad para achicar aun mas la ventana.
		//Luego la agrando hasta cumplir con el cupo minimo.

		VentanaDeTiempo ventanaResultado;
		
		//1- Tamaño estetico: Es una ventana futura o comienza hoy.
		VentanaDeTiempo ventanaEstatica = helper.obtenerVentanaCalendarioEstaticaIntranet(r);
		
		//2- Obtengo una ventana mas chica ajustada segun los cupos que realmente estan disponibles, posiblemente vacia.
		VentanaDeTiempo ventanaAjustada = helper.obtenerVentanaCalendarioAjustadaIntranet(r, ventanaEstatica);
			
		//Si ventanCuposMinimos = 0, la ventana no se corre.
		if (r.getVentanaCuposMinimos() > 0){ 
			//3- Agrando la ventana hasta cumplir con los cupos minimos si es que existe disponibilidad suficiente.
			VentanaDeTiempo ventanaExtendida = helper.obtenerVentanaCalendarioExtendida(r, ventanaAjustada);
			ventanaResultado = ventanaExtendida;
		}
		else {
			ventanaResultado = ventanaAjustada;
		}

		return ventanaResultado;
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
	public VentanaDeTiempo obtenerVentanaCalendarioInternet(Recurso r) throws BusinessException {

		if (r == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		r = entityManager.find(Recurso.class, r.getId());
		if (r == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}
	//	chequearPermiso(r.getAgenda());	

		//Ajusto el tamaño segun la cantidad de cupos minimos y la cantidad de cupos existentes.
		//Dentro de la ventana obtenida busco la primer y ultima disponibilidad para achicar aun mas la ventana.
		//Luego la agrando hasta cumplir con el cupo minimo.

		VentanaDeTiempo ventanaResultado;
		
		//1- Tamaño estetico: Es una ventana futura o comienza hoy.
		VentanaDeTiempo ventanaEstatica = helper.obtenerVentanaCalendarioEstaticaInternet(r);
		
		//2- Obtengo una ventana mas chica ajustada segun los cupos que realmente estan disponibles, posiblemente vacia.
		VentanaDeTiempo ventanaAjustada = helper.obtenerVentanaCalendarioAjustadaInternet(r, ventanaEstatica);
			
		//Si ventanCuposMinimos = 0, la ventana no se corre.
		if (r.getVentanaCuposMinimos() > 0){ 
			//3- Agrando la ventana hasta cumplir con los cupos minimos si es que existe disponibilidad suficiente.
			VentanaDeTiempo ventanaExtendida = helper.obtenerVentanaCalendarioExtendida(r, ventanaAjustada);
			ventanaResultado = ventanaExtendida;
		}
		else {
			ventanaResultado = ventanaAjustada;
		}

		return ventanaResultado;
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
	public List<Integer> obtenerCuposPorDia(Recurso r, VentanaDeTiempo v) throws BusinessException {
		
		if (r == null || v == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		r = entityManager.find(Recurso.class, r.getId());
		if (r == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}
	//	chequearPermiso(r.getAgenda());
		
		//Obtengo la suma de cupos asignados por dia
		List<Object[]> cuposAsignados  = helper.obtenerCuposAsignados(r,v);
		//Obtengo la suma de cupos consumidos (reservas) por dia
		List<Object[]> cuposConsumidos = helper.obtenerCuposConsumidos(r,v);
		//Armo la lista de resultados, indicando los cupos para todos los dias solicitados en la ventana
		List<Integer> cuposXdia = helper.obtenerCuposXDia(v, cuposAsignados, cuposConsumidos);

		return cuposXdia;
	}
	
	public List<Reserva> consultarReservaPorDatos(Recurso r, Map<DatoASolicitar, DatoReserva> datos) {
		return null;
	}

	public Reserva consultarReservaPorDatosClave(Recurso r,	Map<DatoASolicitar, DatoReserva> datos) {
		return null;
	}

	public Reserva marcarReserva(List<Disponibilidad> disps) {
		return null;
	}

	public void reagendarReservas(List<Reserva> reservas, Date fechaHora) {
	}
	
	/**
	 * Asegura que el usuario logueado tenga los permisos necesarios sobre la agenda indicada en el parametro
	 * Controles:
	 *  Salvo que el usuario tenga el rol RA_AE_ADMINISTRADOR, exige que el usuario tenga por lo menos 
	 *  uno de los roles dinamicos indicados en la lista de prefijos de roles. Recordar que el rol dinamico es el resultado
	 *  del prefijo + nombre de la agenda.
	 * 
	 * @param agenda
	 * @param rolesGenerales
	 * @throws RolException
	 */
/*	
	private void chequearPermiso(Agenda agenda, SAERolPrefijo roles []) {
		//glabandera
		if (!ctx.isCallerInRole(SAERol.RA_AE_ANONIMO.toString())) {
			if (!ctx.isCallerInRole(SAERol.RA_AE_ADMINISTRADOR.toString()) && roles != null) {
				boolean accesoPermitido = false;
				for (SAERolPrefijo prefijo : roles) {
					if (ctx.isCallerInRole(prefijo + "_" + agenda.getNombre())) {
						accesoPermitido = true;
					}
				}
				if (! accesoPermitido) {
					throw new RolException("No tiene los privilegios suficientes"); 
				}
			}
		}
	}
*/
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

	public void enviarComunicacionesConfirmacion(String linkCancelacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException, UserException {
		//Enviar mail
		enviarMailConfirmacion(linkCancelacion, reserva, idioma, formatoFecha, formatoHora);
		//Almacenar datos para SMS y TextoAVoz, si corresponde
		almacenarSmsYTav(Comunicacion.Tipo2.RESERVA, reserva, formatoFecha, formatoHora);
	}
	
	public void enviarComunicacionesCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException, UserException {
		//Enviar mail
		enviarMailCancelacion(reserva, idioma, formatoFecha, formatoHora);
		//Almacenar datos para SMS y TextoAVoz, si corresponde
		almacenarSmsYTav(Comunicacion.Tipo2.CANCELA, reserva, formatoFecha, formatoHora);
	}
	
	private void enviarMailConfirmacion(String linkCancelacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException {
		//Se envía el mail obligatorio al usuario
		Recurso recurso = null;
		String email = null;
		try {
			recurso = reserva.getDisponibilidades().get(0).getRecurso();
			Agenda agenda = recurso.getAgenda();
			//TextoAgenda textoAgenda = agenda.getTextoAgenda();
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

					SimpleDateFormat sdfFecha = new SimpleDateFormat (formatoFecha);
					SimpleDateFormat sdfHora = new SimpleDateFormat (formatoHora);
					
					String texto = textoAgenda.getTextoCorreoConf();
					
					if(texto != null && !texto.isEmpty())
					{
						texto = texto.replace("{{AGENDA}}", recurso.getAgenda().getNombre());
						texto = texto.replace("{{RECURSO}}", recurso.getNombre());
						texto = texto.replace("{{DIRECCION}}", recurso.getDireccion());
						texto = texto.replace("{{FECHA}}", sdfFecha.format(reserva.getDisponibilidades().get(0).getFecha()));
						texto = texto.replace("{{HORA}}", sdfHora.format(reserva.getDisponibilidades().get(0).getHoraInicio()));
						texto = texto.replace("{{SERIE}}", recurso.getSerie()!=null?recurso.getSerie():"---");
						texto = texto.replace("{{NUMERO}}", reserva.getNumero()!=null?reserva.getNumero().toString():"---");
						texto = texto.replace("{{CODIGOSEGURIDAD}}", reserva.getCodigoSeguridad());
						texto = texto.replace("{{CODIGOTRAZABILIDAD}}", reserva.getTrazabilidadGuid()!=null?reserva.getTrazabilidadGuid():"---");
						texto = texto.replace("{{CANCELACION}}", linkCancelacion);
						texto = texto.replace("{{IDRESERVA}}", reserva.getId().toString());

						//MailUtiles.enviarMail(mailSession, email, "Confirmación de reserva", texto, MailUtiles.CONTENT_TYPE_HTML);
						MailUtiles.enviarMail(email, "Confirmación de reserva", texto, MailUtiles.CONTENT_TYPE_HTML);
					}
					
					
					Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva);
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
					Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.RESERVA, email, recurso, reserva);
					entityManager.persist(comunicacion);
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			throw new ApplicationException("no_se_pudo_enviar_el_correo_electronico_de_confirmacion_tome_nota_de_los_datos_de_la_reserva", mEx);
		}
		
	}


	private void enviarMailCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException {
		//Se envía el mail obligatorio al usuario
		String email = null;
		Recurso recurso = null;
		try {
			recurso = reserva.getDisponibilidades().get(0).getRecurso();
			Agenda agenda = recurso.getAgenda();
			//TextoAgenda textoAgenda = agenda.getTextoAgenda();
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

					SimpleDateFormat sdfFecha = new SimpleDateFormat(formatoFecha);
					SimpleDateFormat sdfHora = new SimpleDateFormat(formatoHora);
					
					String texto = textoAgenda.getTextoCorreoCanc();
					
					if(texto != null && !texto.isEmpty())
					{
						texto = texto.replace("{{AGENDA}}", recurso.getAgenda().getNombre());
						texto = texto.replace("{{RECURSO}}", recurso.getNombre());
						texto = texto.replace("{{DIRECCION}}", recurso.getDireccion());
						texto = texto.replace("{{FECHA}}", sdfFecha.format(reserva.getDisponibilidades().get(0).getFecha()));
						texto = texto.replace("{{HORA}}", sdfHora.format(reserva.getDisponibilidades().get(0).getHoraInicio()));
						texto = texto.replace("{{SERIE}}", recurso.getSerie()!=null?recurso.getSerie():"---");
						texto = texto.replace("{{NUMERO}}", reserva.getNumero()!=null?reserva.getNumero().toString():"---");
	
						//MailUtiles.enviarMail(mailSession, email, "Cancelación de reserva", texto, MailUtiles.CONTENT_TYPE_HTML);
						MailUtiles.enviarMail(email, "Cancelación de reserva", texto, MailUtiles.CONTENT_TYPE_HTML);
					}
						
					
					Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.CANCELA, email, recurso, reserva);
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
					Comunicacion comunicacion = new Comunicacion(Tipo1.EMAIL, Tipo2.CANCELA, email, recurso, reserva);
					entityManager.persist(comunicacion);
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			throw new ApplicationException("no_se_pudo_enviar_el_correo_electronico_de_confirmacion_tome_nota_de_los_datos_de_la_reserva", mEx);
		}
		
	}
	
	private void almacenarSmsYTav(Comunicacion.Tipo2 tipo2, Reserva reserva, String formatoFecha, String formatoHora) throws UserException {
		//Se envía el mail obligatorio al usuario
		try {
			Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
			//Obtener los numeros de telefono del usuario, que son los datos a solicitar llamados "TelefonoMovil" 
			//y "TelefonoFijo" en la agrupacion que no se puede borrar
			for(DatoReserva dato : reserva.getDatosReserva()) {
				DatoASolicitar datoSol = dato.getDatoASolicitar();
				if("TelefonoMovil".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
					//Tiene telefono movil, se envia SMS
					String telefonoMovil = dato.getValor();
					Comunicacion comunicacion = new Comunicacion(Tipo1.SMS, tipo2, telefonoMovil, recurso, reserva);
					entityManager.persist(comunicacion);
				}
				if("TelefonoFijo".equalsIgnoreCase(datoSol.getNombre()) && !datoSol.getAgrupacionDato().getBorrarFlag()) {
					//Tiene telefono movil, se envia SMS
					String telefonoFijo = dato.getValor();
					Comunicacion comunicacion = new Comunicacion(Tipo1.TEXTOAVOZ, tipo2, telefonoFijo, recurso, reserva);
					entityManager.persist(comunicacion);
				}
			}
		}catch(Exception ex) {
			throw new UserException("no_se_pudo_generar_las_comunicaciones");
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> consultarTextos(String idioma) throws ApplicationException {
		Map<String, String> textos = new HashMap<String, String>();
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
			//pEx.printStackTrace();
			//Nada que hacer, se usan solo los textos globales
		}
		
		return textos;		
	}
	
//	@SuppressWarnings("unchecked")
//	public List<String> consultarFrasesCaptcha(String idioma) throws ApplicationException {
//		List<String> frasesCaptcha = new ArrayList<String>();
//		try {
//			List<FraseCaptcha> frases = (List<FraseCaptcha>) entityManager.
//					createQuery("SELECT c from FraseCaptcha c WHERE c.idioma=:idioma")
//					.setParameter("idioma", idioma).getResultList();
//			if(frases != null) {
//				for(FraseCaptcha frase : frases) {
//					frasesCaptcha.add(frase.getFrase());
//				}
//			}
//		}catch(Exception pEx) {
//			//pEx.printStackTrace();
//		}
//		
//		return frasesCaptcha;		
//	}

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
	
}
