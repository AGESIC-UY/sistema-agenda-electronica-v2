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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import uy.gub.agesic.novedades.Acciones;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.ejb.servicios.ServiciosNovedadesBean;
import uy.gub.imm.sae.business.ejb.servicios.ServiciosTrazabilidadBean;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.Llamada;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed({"RA_AE_ADMINISTRADOR","RA_AE_PLANIFICADOR","RA_AE_FATENCION", "RA_AE_LLAMADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
public class LlamadasBean implements LlamadasLocal, LlamadasRemote {

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager em;

	@EJB
	private LlamadasHelperLocal helper;	

	@EJB
	private ConsultasLocal consultas;
	
	@EJB
	private ServiciosTrazabilidadBean trazaBean;
	
	@EJB
	private ServiciosNovedadesBean novedadesBean;
	
	@Resource
	private SessionContext ctx;
	
  static Logger logger = Logger.getLogger(LlamadasBean.class);
  
	public List<ReservaDTO> obtenerReservasEnEspera(Recurso recurso, List<Estado> estados, boolean atencionPresencial, TimeZone timezone) throws UserException {
		VentanaDeTiempo hoy = new VentanaDeTiempo();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTimeInMillis()));
		Date ahora = cal.getTime();
		hoy.setFechaInicial(Utiles.time2InicioDelDia(ahora));
		hoy.setFechaFinal(Utiles.time2FinDelDia(ahora));
		if (estados.size() == 1 && estados.contains(Estado.R) ) {
			return consultas.consultarReservasEnEspera(recurso, atencionPresencial, timezone);
		} else if (estados.size() == 2 && estados.contains(Estado.R) && estados.contains(Estado.U)) {
			return consultas.consultarReservasEnEsperaUtilizadas(recurso, atencionPresencial, timezone);
		} else {
			return consultas.consultarReservasPorPeriodoEstado(recurso, hoy, estados, atencionPresencial);
		}
	
	}	
	
	
	/**
	 * Obtiene la siguiente reserva del dia en la lista de espera y 
	 * automáticamente le cambia el estado a U (Utilizada) y genera una llamada con la reserva y el puesto.
	 * Utiliza el campo VERSION en la entidad Rerserva para realizar un bloque optimista
	 * Si dos transacciones toman la misma reserva para cambiarle el estado, solo uno tendra exito, por 
	 * lo que este metodo, frente a una excepcion de bloqueo optimista vuelve a intenar con la
	 * siguiente reserva hasta obtener una o que se acaben las reservas en la lista de espera, 
	 * en cuyo caso retorna null.  
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	public Reserva siguienteEnEspera(Recurso recurso, Integer puesto, boolean presencial) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
		 
		Reserva reserva = null;
		Integer recursoId = recurso.getId();
		
		//Lista de espera 
		Query query = em.createQuery("SELECT r.id " +
				"FROM Reserva r " +
				"JOIN r.disponibilidades d " +
				"WHERE d.recurso.id = :recurso " +
        "  AND "+(presencial?"d.presencial=true":"d.presencial=false") + " " +
				"  AND r.estado = :estado " +
				"  AND d.fecha = :hoy " +
				"ORDER BY d.fecha, d.horaInicio, r.id")
  		.setParameter("recurso", recursoId)
  		.setParameter("estado", Estado.R)
  		.setParameter("hoy", new Date(), TemporalType.DATE);
		List<Integer> resultados = (List<Integer>)query.getResultList();
		//Busco la siguiente reserva a ser llamada con mutua exlucion y lo logro al insertar la llamada, 
		//con el uso de clave de unicidad en la tabla de llamadas por id de la reserva.
		Boolean buscarSiguiente = true;
		//Iterator<Reserva> iter = resultados.iterator();
		Iterator<Integer> iter = resultados.iterator();
		while (buscarSiguiente && iter.hasNext()) {
			Integer reservaId = iter.next();
			try {
				reserva = helper.hacerLlamadaMutex(recursoId, reservaId, puesto);
				if (reserva != null) {
					buscarSiguiente = false;
				}
			} catch (EJBException e) {
				if (!(e.getCausedByException() instanceof OptimisticLockException)) {
					throw e;
				}
			}
		}
		if ( ! buscarSiguiente ) {
			return reserva;	
		}	else {
			return null;
		}
	}
	
	/**
	 * Vuelve a colocar la reserva indicada para ser llamada, es decir, 
	 * simula el comportamiento del metodo siguienteEnEspera para una reserva que ya fue utilizada,
	 * generando una llamada con la reserva y el puesto.
	 * @throws BusinessException 
	 */
	public Reserva volverALlamar(Recurso recurso, Integer puesto, Reserva reserva) throws UserException {
    if (recurso == null) {
      throw new UserException("debe_especificar_el_recurso");
    }

		Integer reservaId = reserva.getId();
		try {
			reserva = helper.reintentarLlamadaMutex(recurso.getId(), reservaId, puesto);
		} catch (EJBException e) {
			if (e.getCausedByException() instanceof OptimisticLockException) {
				reserva = null;
			} else {
				throw e;
			}
		}
		return reserva;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Llamada> obtenerLlamadas(List<Recurso> recursos, Integer cantLlamadas) throws UserException {

		List<Object[]> llamadas    = new ArrayList<Object[]>();
		List<Llamada> llamadasDTO = new ArrayList<Llamada>();
		
		if (recursos == null || recursos.isEmpty()) {
			throw new UserException("debe_especificar_el_recurso");
		}

		Query query = em.createQuery("SELECT ll.id, ll.etiqueta, ll.fecha, ll.hora, ll.numero, ll.puesto, ll.reserva " +
				"FROM Llamada ll " +
				"WHERE ll.recurso.id IN (:recursos) " +
				"  AND ll.fecha = :hoy " +				
				"ORDER BY ll.hora DESC");
		List<Integer> recursosIds = new ArrayList<Integer>();
		for (Recurso recurso : recursos) {
			recursosIds.add(recurso.getId());
		}
		query.setParameter("recursos", recursosIds);
		query.setParameter("hoy", new Date(), TemporalType.DATE);
			
		query.setMaxResults(cantLlamadas);
		
		llamadas = (List<Object[]>)query.getResultList();
		
		Map<Integer, Integer> puestos = new HashMap<Integer, Integer>();
		for (Object[] llamada : llamadas) {
			Llamada dto = new Llamada();
			dto.setId((Integer)llamada[0]);
			dto.setEtiqueta((String)llamada[1]);
			dto.setFecha((Date)llamada[2]);
			dto.setHora((Date)llamada[3]);
			dto.setNumero((Integer)llamada[4]);
			if (puestos.containsKey(llamada[5])) {
				dto.setPuesto(null);
			} else {
				dto.setPuesto((Integer)llamada[5]);
				puestos.put((Integer)llamada[5], (Integer)llamada[5]);
			}
			dto.setReserva((Reserva)llamada[6]);
			llamadasDTO.add(dto);
		}
		
		return llamadasDTO;
	}

	/**
	 * Deja constancia de que el ciudadano asistio a la cita reservada.
	 * @throws BusinessException 
	 */
	public void marcarAsistencia(Empresa empresa, Recurso recurso, Reserva reserva) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
		if (reserva == null){
			throw new UserException("debe_especificar_la_reserva");
		}
		Atencion atencion = new Atencion();
		atencion.setReserva(reserva);
		atencion.setAsistio(true);
		atencion.setDuracion(1);
		atencion.setFuncionario(ctx.getCallerPrincipal().getName());
		em.persist(atencion);
		//Registrar la asistencia en el sistema de trazas del PEU
		String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), reserva.getTramiteCodigo(), reserva.getId());
		if(transaccionId != null) {
			trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.ASISTENCIA);
		}
		//Publicar la novedad
		novedadesBean.publicarNovedad(empresa, reserva, Acciones.ASISTENCIA);
	}


	/**
	 * Deja constancia de que el ciudadano asistio a la cita reservada
	 * @throws BusinessException 
	 */
	public void marcarInasistencia(Empresa empresa, Recurso recurso, Reserva reserva) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
		if (reserva == null){
			throw new UserException("debe_especificar_la_reserva");
		}
		Atencion atencion = new Atencion();
		atencion.setReserva(reserva);
		atencion.setAsistio(false);
		atencion.setDuracion(1);
		atencion.setFuncionario(ctx.getCallerPrincipal().getName());
		em.persist(atencion);
		
		//Registrar la asistencia en el sistema de trazas del PEU
		String transaccionId = trazaBean.armarTransaccionId(empresa.getOid(), reserva.getTramiteCodigo(), reserva.getId());
		if(transaccionId != null) {
			trazaBean.registrarLinea(empresa, reserva, transaccionId, recurso.getNombre(), ServiciosTrazabilidadBean.Paso.INASISTENCIA);
		}
		
		//Publicar la novedad
		novedadesBean.publicarNovedad(empresa, reserva, Acciones.INASISTENCIA);
		
	}

}
