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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import uy.gub.imm.sae.business.dto.AtencionLLamadaReporteDT;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.entity.global.Token;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed({"RA_AE_FCALL_CENTER","RA_AE_PLANIFICADOR", "RA_AE_ADMINISTRADOR","RA_AE_ANONIMO","RA_AE_FATENCION"})
public class ConsultasBean implements ConsultasLocal, ConsultasRemote{

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;
	
	public Reserva consultarReservaId(Integer idReserva, Integer idRecurso) throws ApplicationException, BusinessException {
		
		Reserva reserva = null;
		
		if (idReserva == null)
			throw new BusinessException("-1", "El Nro. de la reserva no puede ser nulo.");
		
		try {
		
			 reserva = entityManager.find(Reserva.class, idReserva);
			 if (reserva != null) {
				 reserva.getDisponibilidades().size();
				 reserva.getDatosReserva().size();
			 }
		
		}catch (Exception e) {
			throw new ApplicationException(e);
		}
		if (reserva != null) {
			if (reserva.getDisponibilidades().iterator().next().getRecurso().getId().intValue() != idRecurso){
				throw new BusinessException("-1","La reserva no corresponde al recurso.");
			}
		}
		return reserva;
	}

	
	public Reserva consultarReservaPorNumero(Recurso r, Date fechaHoraInicio, Integer numero) throws BusinessException, UserException {
		
		Reserva reserva = null;
		
		r = entityManager.find(Recurso.class, r.getId());
		if (r == null || r.getFechaBaja() != null)
			throw new BusinessException("no_se_encuentra_el_recurso_especificado");

		if (fechaHoraInicio == null)
			throw new BusinessException("el_dia_y_la_hora_son_obligatorios");

		if (numero == null)
			throw new BusinessException("el_numero_es_obligatorio");
		
		try {
		reserva = (Reserva) entityManager.createQuery(
				 "select res " +
				 "from  Reserva res join res.disponibilidades d " +
				 "where d.recurso = :recurso and " +
				 "      d.fecha = :fecha and" +
				 "      d.horaInicio = :horaInicio and " +
				 "      res.numero = :numero ")
				 .setParameter("recurso", r)
				 .setParameter("fecha", fechaHoraInicio, TemporalType.DATE)
				 .setParameter("horaInicio", fechaHoraInicio, TemporalType.TIMESTAMP)
				 .setParameter("numero", numero)
				 .getSingleResult();
		} 
		catch ( NoResultException e) { 
			throw new UserException("no_se_encuentra_la_reserva_especificada");
		}

		reserva.getDisponibilidades().size();
		reserva.getDatosReserva().size();

		return reserva;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Reserva> consultarReservaDatosHora(List<DatoReserva> datos , Recurso recurso, Date fecha){
		List<Reserva> resultados = new ArrayList<Reserva>();
		
		String selectStr =	" SELECT distinct(reserva) " ;
		String fromStr =   	" FROM Reserva reserva " +
						   					"	JOIN reserva.disponibilidades disp " +
					   						" JOIN reserva.datosReserva datoReserva " +
				   							"	JOIN datoReserva.datoASolicitar datoSolicitar	" ;
		String whereStr = 	" WHERE disp.recurso = :recurso " +
						  					" 	AND disp.fechaBaja is null " + 
					  						"   AND disp.fecha = :fecha " +
				  							"   AND reserva.estado NOT IN ('U','C') ";
					  							
			boolean hayCamposClaveNulos = false;
			if (! datos.isEmpty()) {
				whereStr = whereStr + "    and (" ;
			
				boolean primerRegistro = true;
				int i = 0;
				for (DatoReserva datoR : datos){
					
					if (datoR != null){
						if (primerRegistro){
							whereStr = whereStr + 
							" 	   (upper(datoReserva.valor) = '" + datoR.getValor().toUpperCase() + "' and " +
							" 	   datoSolicitar.id = " + datoR.getDatoASolicitar().getId() + ") ";
							primerRegistro = false;
						} else {
							String joinFromAux =  " join  reserva.datosReserva datoReserva" + i +
												  " join datoReserva" + i + ".datoASolicitar datoSolicitar" + i;
							fromStr = fromStr + joinFromAux;
							whereStr = whereStr + " AND " +
								" 	 ( upper(datoReserva" + i + ".valor) = '" + datoR.getValor().toUpperCase() + "' and " +
								" 	   datoSolicitar" + i + ".id = " + datoR.getDatoASolicitar().getId() + ") ";
						}
					} else {
						hayCamposClaveNulos = true;
					}
					i++;
				}
				whereStr = whereStr + ")" ;
			}
			String consulta = selectStr + fromStr + whereStr  + " ORDER BY reserva.id ";
			
			try {
				resultados = (List<Reserva>)entityManager.createQuery(consulta)
									.setParameter("recurso", recurso)
									.setParameter("fecha", fecha, TemporalType.DATE)
									.getResultList();
				
				/* 12/03/2010 - Corrige que al traer reservas con la misma clave que se ingreso, 
				 * se filtren las que tengan algun dato clave mas ingresado (por ejemplo cuando 
				 * un gestor reservo para otras personas y luego reserva para si mismo, 
				 * sin ingresar los datos clave opcional) 
				 */
				if(hayCamposClaveNulos) {
					resultados = filtrarReservasConMasDatos(resultados, datos);
				}
			} catch (Exception e){ 
				
			}
			// 	recorro las reservas para obtener las listas disponibilidades y datos reservas 
			for (Reserva r : resultados){
				r.getDisponibilidades().size();
				r.getDatosReserva().size();
			}
		return resultados;
	}
	
	private List<Reserva> filtrarReservasConMasDatos(List<Reserva> reservasIni, List<DatoReserva> datosIngresadosClave){
		List<Reserva> reservasResult = new ArrayList<Reserva>();
		
		boolean hayAlgunoQueNoEsta = false;
		for (Iterator<Reserva> iterator1 = reservasIni.iterator(); iterator1.hasNext();) {
			Reserva reserva = iterator1.next();
			
			hayAlgunoQueNoEsta = false;
			for (Iterator<DatoReserva> iterator = reserva.getDatosReserva().iterator(); iterator.hasNext() && !hayAlgunoQueNoEsta;) {
				DatoReserva datoI = iterator.next();
				if(datoI.getDatoASolicitar().getEsClave() && !existeDatoReservaEnDatosIngresados(datosIngresadosClave, datoI.getDatoASolicitar())){
					hayAlgunoQueNoEsta = true;
				}
			}
			
			if(!hayAlgunoQueNoEsta){
				reservasResult.add(reserva);
			}
		}
		
		return reservasResult;
	}

	private boolean existeDatoReservaEnDatosIngresados(List<DatoReserva> datosIngresadosClave, DatoASolicitar datoSolicReservaFiltrar){
	
		boolean esta = false; 
		
		for (Iterator<DatoReserva> iterator = datosIngresadosClave.iterator(); iterator.hasNext() && !esta;) {
			DatoReserva datoClave = iterator.next();
			if(datoClave!=null && datoClave.getDatoASolicitar().getId().equals(datoSolicReservaFiltrar.getId())){
				esta = true;
			}			
		}
		
		return esta;
	}
	

	@SuppressWarnings("unchecked")
	public List<Reserva> consultarReservaDatos(List<DatoReserva> datos ,Recurso recurso){
		List<Reserva> resultados = new ArrayList<Reserva>();
		
		if (! datos.isEmpty()){

			String selectStr = " SELECT distinct(reserva) " ;
			String fromStr =   " FROM Reserva reserva " +
							   "	   join  reserva.disponibilidades disp " +
		  				       "       join  reserva.datosReserva datoReserva " +
		  				       "	   join datoReserva.datoASolicitar datoSolicitar	" ;
	
			String whereStr = " WHERE disp.recurso = :recurso " +
						  "		  and reserva.estado <> 'P' " ;
		 
			boolean primerRegistro = true;
			int i = 0;
			for (DatoReserva datoR : datos){
				if((datoR.getValor() != null) && (!datoR.getValor().equalsIgnoreCase("NoSeleccion"))){
					if ((primerRegistro)){
						whereStr = whereStr + 
						" 	   and ((upper(datoReserva.valor) = '" + datoR.getValor().toUpperCase() + "' and " +
						" 	   datoSolicitar.id = " + datoR.getDatoASolicitar().getId() + ") ";
						primerRegistro = false;
					} else {
				
						String joinFromAux =  " join  reserva.datosReserva datoReserva" + i +
											  " join datoReserva" + i + ".datoASolicitar datoSolicitar" + i;
						fromStr = fromStr + joinFromAux;
				
						whereStr = whereStr + " AND " +
							" 	 ( upper(datoReserva" + i + ".valor) = '" + datoR.getValor().toUpperCase() + "' and " +
							" 	   datoSolicitar" + i + ".id = " + datoR.getDatoASolicitar().getId() + ") ";
				
					}
				}
				i++;
			}

			String consulta = selectStr + fromStr + whereStr ;
	
			// Agrego el ORDER BY parentesis final de la consulta
			consulta = consulta + ") ORDER BY reserva.fechaCreacion DESC ";
	
			try {
				resultados = (List<Reserva>)entityManager.createQuery(consulta)
									.setParameter("recurso", recurso)
									.getResultList();
			} catch (Exception e){}
	
			// 	recorro las reservas para obtener las listas disponibilidades y datos reservas 
			for (Reserva r : resultados){
				r.getDisponibilidades().size();
				r.getDatosReserva().size();
			}
		}
		return resultados;
	}

	@SuppressWarnings("unchecked")
	public List<Reserva> consultarReservasParaCancelar(List<DatoReserva> datos, Recurso recurso, String codigoSeguridadReserva,
			TimeZone timezone){
		List<Reserva> resultados = new ArrayList<Reserva>();
		
		String selectStr = " SELECT distinct(reserva) " ;
		String fromStr =   " FROM Reserva reserva " +
						   "	   join  reserva.disponibilidades disp " +
	  				       "       join  reserva.datosReserva datoReserva " +
	  				       "	   join datoReserva.datoASolicitar datoSolicitar	" ;
	
		String whereStr = " WHERE disp.recurso = :recurso " +
					  "		  and reserva.estado = 'R' " +
					  "		  and reserva.codigoSeguridad = :codigoSeguridad " +
					  "		  and disp.horaInicio >= :fecha " ;
	 
		boolean primerRegistro = true;
		int i = 0;
		for (DatoReserva datoR : datos){
			if((datoR.getValor() != null) && (!datoR.getValor().equalsIgnoreCase("NoSeleccion"))){
				if ((primerRegistro)){
					whereStr = whereStr + 
					" 	   and ((upper(datoReserva.valor) = '" + datoR.getValor().toUpperCase() + "' and " +
					" 	   datoSolicitar.id = " + datoR.getDatoASolicitar().getId() + ") ";
					primerRegistro = false;
				} else {
				
					String joinFromAux =  " join  reserva.datosReserva datoReserva" + i +
										  " join datoReserva" + i + ".datoASolicitar datoSolicitar" + i;
					fromStr = fromStr + joinFromAux;
				
					whereStr = whereStr + " AND " +
						" 	 ( upper(datoReserva" + i + ".valor) = '" + datoR.getValor().toUpperCase() + "' and " +
						" 	   datoSolicitar" + i + ".id = " + datoR.getDatoASolicitar().getId() + ") ";
				
				}
			}
			i++;
		}

		String consulta = selectStr + fromStr + whereStr ;
	
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTime().getTime()));
		
		// Agrego el ORDER BY parentesis final de la consulta
		consulta = consulta + ") ORDER BY reserva.fechaCreacion DESC ";
		try {
			resultados = (List<Reserva>)entityManager.createQuery(consulta)
								.setParameter("recurso", recurso)
								.setParameter("codigoSeguridad", codigoSeguridadReserva)
								.setParameter("fecha", cal.getTime(), TemporalType.TIMESTAMP)
								.getResultList();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		// 	recorro las reservas para obtener las listas disponibilidades y datos reservas 
		for (Reserva r : resultados){
			r.getDisponibilidades().size();
			r.getDatosReserva().size();
		}
		return resultados;
	}
	
	public List<ReservaDTO> consultarReservasPorPeriodoEstado(Recurso recurso, VentanaDeTiempo periodo, Estado estado) throws BusinessException {
		List<Estado> estados = new ArrayList<Estado>();
		estados.add(estado);
		return consultarReservasPorPeriodoEstado(recurso, periodo, estados);
	}

	public List<ReservaDTO> consultarReservasPorPeriodoEstado(Recurso recurso, VentanaDeTiempo periodo, List<Estado> estados) throws BusinessException {
		if (recurso == null || periodo == null || estados == null || estados.size() == 0 || estados.get(0) == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		if (periodo.getFechaInicial() == null || periodo.getFechaFinal() == null) {
			throw new BusinessException("-1", "El periodo debe tener inicio y fin");
		}
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		return consultarReservasPorPeriodoEstadosDisponibilidades(recurso, periodo, estados, new ArrayList<Integer>());
		
	}

	public List<ReservaDTO> consultarReservasEnEspera(Recurso recurso, TimeZone timezone) throws BusinessException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTimeInMillis()));
		Date hoy = cal.getTime();
		VentanaDeTiempo periodo = new VentanaDeTiempo();
		periodo.setFechaInicial(Utiles.time2FinDelDia(hoy));
		periodo.setFechaFinal(Utiles.time2FinDelDia(hoy));
		List<Estado> estados = new ArrayList<Estado>();
		estados.add(Estado.R);
		List<Integer> disponibilidadesIds = consultarDisponibilidadesReservadasYUtilizadas(recurso, periodo);
		return consultarReservasPorPeriodoEstadosDisponibilidades(recurso, periodo, estados, disponibilidadesIds);
	}

	public List<ReservaDTO> consultarReservasEnEsperaUtilizadas(Recurso recurso, TimeZone timezone) throws BusinessException {
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTimeInMillis()));
		Date hoy = cal.getTime();

		VentanaDeTiempo periodo = new VentanaDeTiempo();
		periodo.setFechaInicial(Utiles.time2FinDelDia(hoy));
		periodo.setFechaFinal(Utiles.time2FinDelDia(hoy));
		List<Estado> estados = new ArrayList<Estado>();
		estados.add(Estado.R);
		estados.add(Estado.U);

		List<Integer> disponibilidadesIds = consultarDisponibilidadesReservadasYUtilizadas(recurso, periodo);
		
		return consultarReservasPorPeriodoEstadosDisponibilidades(recurso, periodo, estados, disponibilidadesIds);
	}

	
	@SuppressWarnings("unchecked")	
	private List<Integer> consultarDisponibilidadesReservadasYUtilizadas(Recurso recurso, VentanaDeTiempo periodo) throws BusinessException {


		if (recurso == null) {
			throw new BusinessException("AE20084", "El recurso no puede ser nulo");
		}
		
		
		//Busco las primeras 2 disponibilidades con reservas en estado R
		Query queryR = entityManager.createQuery(
				"select distinct d.id, d.fecha, d.horaInicio " +
				"from   Reserva r " +
				"       join r.disponibilidades d " +
				"where   " +
				"       d.recurso.id = :recurso and " +
				"       d.fecha between :fi and :ff  and " +
				"       r.estado = :estado " +
				"order by d.fecha, d.horaInicio " 
		);

		queryR.setParameter("recurso", recurso.getId());
		queryR.setParameter("estado", Estado.R);
		queryR.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
		queryR.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);
		
		if (recurso.getLargoListaEspera() != null && recurso.getLargoListaEspera() > 0){
			queryR.setMaxResults(recurso.getLargoListaEspera());
		}
		
		List<Object[]> dispsR = queryR.getResultList();

		List<Object[]> dispsU = null;
		
		if (dispsR.size()>0) {
			//Si hay disponibilidades en estado R, busco las 2 inmediatamente anteriores en estado U
			
			Query queryU = entityManager.createQuery(
				"select distinct d.id, d.fecha, d.horaInicio " +
				"from   Reserva r " +
				"       join r.disponibilidades d " +
				"where   " +
				"       d.recurso.id = :recurso and " +
				"       d.fecha between :fi and :ff  and " +
				"       r.estado = :estado and " +
				"		d.horaInicio < :hora " +
				"order by d.fecha desc, d.horaInicio desc " 
			);

			queryU.setParameter("recurso", recurso.getId());
			queryU.setParameter("estado", Estado.U);
			queryU.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
			queryU.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);
			queryU.setParameter("hora", (Date)dispsR.get(0)[2], TemporalType.TIMESTAMP);
						
			if (recurso.getLargoListaEspera() != null && recurso.getLargoListaEspera() > 0){
				queryU.setMaxResults(recurso.getLargoListaEspera());
			}
			
			dispsU = queryU.getResultList();
			
		}
		else {
			//Busco las 2 ultimas en estado U
			Query queryU = entityManager.createQuery(
					"select distinct d.id, d.fecha, d.horaInicio " +
					"from   Reserva r " +
					"       join r.disponibilidades d " +
					"where   " +
					"       d.recurso.id = :recurso and " +
					"       d.fecha between :fi and :ff  and " +
					"       r.estado = :estado " +
					"order by d.fecha desc, d.horaInicio desc " 
				);

				queryU.setParameter("recurso", recurso.getId());
				queryU.setParameter("estado", Estado.U);
				queryU.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
				queryU.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);
							
				if (recurso.getLargoListaEspera() != null && recurso.getLargoListaEspera() > 0){
					queryU.setMaxResults(recurso.getLargoListaEspera());
				}
				
				dispsU = queryU.getResultList();
		}
		
		
		List<Integer> dispIds = new ArrayList<Integer>();
		
//		if (dispsU != null) {
//			
//			if (dispsU.size() == 2) {
//				dispIds.add((Integer)dispsU.get(1)[0]);
//			}
//			
//			if (dispsU.size() >= 1 && (dispsR == null || dispsR.size() == 0 || !dispsR.get(0)[0].equals(dispsU.get(0)[0])) ) {
//				dispIds.add((Integer)dispsU.get(0)[0]);
//			}
//		}
//		
//		if (dispsR != null) {
//
//			if (dispsR.size() >= 1) {
//				dispIds.add((Integer)dispsR.get(0)[0]);
//			}
//			
//			if (dispsR.size() == 2) {
//				dispIds.add((Integer)dispsR.get(1)[0]);
//			}
//			
//		}
		
		if (dispsU != null) {
			
			for (int i= dispsU.size() - 1; i > 0; i--){
				dispIds.add((Integer)dispsU.get(i)[0]);
			}
			
			if (dispsU.size() >= 1 && (dispsR == null || dispsR.size() == 0 || !dispsR.get(0)[0].equals(dispsU.get(0)[0])) ) {
				dispIds.add((Integer)dispsU.get(0)[0]);
			}
		}
		
		if (dispsR != null) {
			
			for (int i= dispsR.size() - 1; i >= 0; i--){
				dispIds.add((Integer)dispsR.get(i)[0]);
			}
		}
		
		return dispIds;
	}
	
	
	
	@SuppressWarnings("unchecked")	
	private List<ReservaDTO> consultarReservasPorPeriodoEstadosDisponibilidades(Recurso recurso, VentanaDeTiempo periodo, List<Estado> estados, List<Integer> disponibilidadesIds ) throws BusinessException {
		if (recurso == null) {
			throw new BusinessException("AE20084", "El recurso no puede ser nulo");
		}
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		Map<Integer, Map<String,String>> valoresPosiblesPorEtiqueta = armoMapaCampoValorEtiqueta(recurso);
		int i;
		String whereEstados = null;
		for (i=1; i <= estados.size(); i++) {
			if (whereEstados != null) {
				whereEstados += " or ";
			} else {
				whereEstados = "";
			}
			whereEstados += "r.estado = :estado" + i;
		}
		String whereDispIds = null;
		for (i=1; i <= disponibilidadesIds.size(); i++) {
			if (whereDispIds != null) {
				whereDispIds += " or ";
			} else {
				whereDispIds = "";
			}
			whereDispIds += "d.id = :dispId" + i;
		}
		String where = "";
		if (whereEstados != null) {
			where += "and ( " + whereEstados + " ) ";
		}
		if (whereDispIds != null) {
			where += "and ( " + whereDispIds + " ) ";
		}
		String queryString = 
			"select r.id, r.numero, r.estado, d.id, d.fecha, d.horaInicio, das.id, das.nombre, das.tipo, dr.valor, ll.puesto, a.asistio " +
			"from   Reserva r " +
			"       join r.disponibilidades d " +
			"       left join r.datosReserva dr " +
			"		    left join dr.datoASolicitar das " +
			"       left join r.llamada ll " +
			"       left join r.atenciones a "+
			"where   " +
			"       d.recurso.id = :recurso and " +
			"       d.fecha between :fi and :ff " +
					where +
			"order by d.fecha, d.horaInicio, r.numero "; 

		Query query = entityManager.createQuery(queryString);
		query.setParameter("recurso", recurso.getId());
		query.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
		query.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);

		i = 1;
		for (Estado estado : estados) {
			query.setParameter("estado"+i, estado);
			i++;
		}

		i = 1;
		for (Integer dispId : disponibilidadesIds) {
			query.setParameter("dispId"+i, dispId);
			i++;
		}

		List<Object[]> resultados = query.getResultList();
		List<ReservaDTO> reservas = new ArrayList<ReservaDTO>();
		Integer idReservaActual = null;
		ReservaDTO reservaDTO = null;
		Iterator<Object[]> iterator = resultados.iterator();
		while (iterator.hasNext()) {
			Object[] rowReserva = iterator.next();
			Integer reservaId        = (Integer)rowReserva[0];
			Integer reservaNumero    = (Integer)rowReserva[1];
			String  reservaEstado    = ((Estado) rowReserva[2]).toString();
			//Integer dispId           = (Integer)rowReserva[3];
			Date    dispFecha        = (Date) rowReserva[4];
			Date    dispHoraInicio   = (Date) rowReserva[5];
			Integer datoASolicitarId = (Integer) rowReserva[6];
			String  nombreDatoReserva= (String) rowReserva[7];
			Tipo    tipoDatoReserva  = (Tipo) rowReserva[8];
			Object  valorDatoReserva = (Object) rowReserva[9];
			Integer puesto           = (Integer)rowReserva[10];
			Boolean asistio          = (Boolean)rowReserva[11];
			if (idReservaActual == null || ! idReservaActual.equals(reservaId)) {
				idReservaActual = reservaId;
				if (reservaDTO != null) {
					//Cambio de reserva, guardo la actual como resultado
					reservas.add(reservaDTO);
				}
				reservaDTO = new ReservaDTO();
				reservaDTO.setId(reservaId);
				reservaDTO.setNumero(reservaNumero);
				reservaDTO.setEstado(reservaEstado);
				reservaDTO.setFecha(dispFecha);
				reservaDTO.setHoraInicio(dispHoraInicio);
				reservaDTO.setPuestoLlamada(puesto);
				reservaDTO.setAsistio(asistio);
			}
			if (nombreDatoReserva != null) {
				if (tipoDatoReserva == Tipo.LIST) {
					String valor = valoresPosiblesPorEtiqueta.get(datoASolicitarId).get(valorDatoReserva);
					reservaDTO.getDatos().put(nombreDatoReserva, valor);			
				} else {
					reservaDTO.getDatos().put(nombreDatoReserva, valorDatoReserva);			
				}
				if("NroDocumento".equals(nombreDatoReserva) && nombreDatoReserva!=null) {
					reservaDTO.setNumeroDocumento(valorDatoReserva.toString());
				}
			}
		}
		if (reservaDTO != null) {
			//Al salir del loop, siempre me queda la ultima reserva para agregar al resultado
			reservas.add(reservaDTO);
		}
		return reservas;
	}
	
	
	private Map<Integer, Map<String,String>> armoMapaCampoValorEtiqueta(Recurso recurso) {
		
		Map<Integer, Map<String,String>> valoresPosibles = new HashMap<Integer, Map<String,String>>();
		for (DatoASolicitar ds: recurso.getDatoASolicitar()) {
			if (ds.getTipo() == Tipo.LIST) {
				Map<String, String> valores = new HashMap<String, String>();
				valoresPosibles.put(ds.getId(), valores);
				
				for(ValorPosible vp: ds.getValoresPosibles()) {
					valores.put(vp.getValor(), vp.getEtiqueta());
				}
			}
		}
		
		return valoresPosibles;
	}
	
	
	public List<ReservaDTO> consultarReservasUsadasPeriodo(Recurso recurso, VentanaDeTiempo periodo) throws BusinessException {
		
	
		if (recurso == null || periodo == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		if (periodo.getFechaInicial() == null || periodo.getFechaFinal() == null) {
			throw new BusinessException("-1", "El periodo debe tener inicio y fin");
		}
		
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		
		return consultarReservasUsadasPorPeriodoDisponibilidades(recurso, periodo, new ArrayList<Integer>());
		
	}

	@SuppressWarnings("unchecked")	
	private List<ReservaDTO> consultarReservasUsadasPorPeriodoDisponibilidades(Recurso recurso, VentanaDeTiempo periodo, List<Integer> disponibilidadesIds ) throws BusinessException {

		if (recurso == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		

		Map<Integer, Map<String,String>> valoresPosiblesPorEtiqueta = armoMapaCampoValorEtiqueta(recurso);
		
		int i;
		String whereDispIds = null;
		for (i=1; i <= disponibilidadesIds.size(); i++) {
			if (whereDispIds != null) {
				whereDispIds += " or ";
			}
			else {
				whereDispIds = "";
			}
			
			whereDispIds += "d.id = :dispId" + i;
		}
		String where = "";
		where += "and ( r.estado = 'U' ) ";
		if (whereDispIds != null) {
			where += "and ( " + whereDispIds + " ) ";
		}
		

		//Esta consulta no funciona con reserva multiples.
		//Asumo que no existen reservas multiples
		String queryString = 
			"select r.id, r.numero, r.estado, d.id, d.fecha, d.horaInicio, das.id, das.nombre, das.tipo, dr.valor, ll.puesto, at.asistio " +
			"from   Reserva r " +
			"       join r.disponibilidades d " +
			"       left join r.datosReserva dr " +
			"		left join dr.datoASolicitar das " +
			"       left join r.llamada ll " +
			"       left join r.atenciones at "+
			"where   " +
			"       d.recurso.id = :recurso and " +
			"       d.fecha between :fi and :ff " +
					where +
			"order by d.fecha, d.horaInicio, r.numero "; 

		Query query = entityManager.createQuery(queryString);
		query.setParameter("recurso", recurso.getId());
		query.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
		query.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);

		i = 1;
		for (Integer dispId : disponibilidadesIds) {
			query.setParameter("dispId"+i, dispId);
			i++;
		}

		
		
		List<Object[]> resultados = query.getResultList();
		
		List<ReservaDTO> reservas = new ArrayList<ReservaDTO>();

		Integer idReservaActual = null;
		ReservaDTO reservaDTO = null;
		
		Iterator<Object[]> iterator = resultados.iterator();
		while (iterator.hasNext()) {

			Object[] rowReserva = iterator.next();
		
			Integer reservaId        = (Integer)rowReserva[0];
			Integer reservaNumero    = (Integer)rowReserva[1];
			String  reservaEstado    = ((Estado) rowReserva[2]).toString();
//			Integer dispId           = (Integer)rowReserva[3];
			Date    dispFecha        = (Date) rowReserva[4];
			Date    dispHoraInicio   = (Date) rowReserva[5];
			Integer datoASolicitarId = (Integer) rowReserva[6];
			String  nombreDatoReserva= (String) rowReserva[7];
			Tipo    tipoDatoReserva  = (Tipo) rowReserva[8];
			Object  valorDatoReserva = (Object) rowReserva[9];
			Integer puesto           = (Integer)rowReserva[10];
			Boolean asistio          = (Boolean)rowReserva[11];


			if (idReservaActual == null || ! idReservaActual.equals(reservaId)) {
				//recien arranco o cambio de reserva: 
				
				idReservaActual = reservaId;
				
				if (reservaDTO != null) {
					//Cambio de reserva, guardo la actual como resultado

					reservas.add(reservaDTO);
				}
				
				reservaDTO = new ReservaDTO();
				
				reservaDTO.setId(reservaId);
				reservaDTO.setNumero(reservaNumero);
				reservaDTO.setEstado(reservaEstado);
				reservaDTO.setFecha(dispFecha);
				reservaDTO.setHoraInicio(dispHoraInicio);
				if (puesto != null) {
					reservaDTO.setPuestoLlamada(puesto);
				}
				if (asistio != null){
					reservaDTO.setAsistio(asistio);
				}
			}
			//El else indica que estoy iterando sobre los datos de la reserva
			
			if (nombreDatoReserva != null) {
				
				
				if (tipoDatoReserva == Tipo.LIST) {
					//Lista de valores: etiqueta del valor
					String valor = valoresPosiblesPorEtiqueta.get(datoASolicitarId).get(valorDatoReserva);
					reservaDTO.getDatos().put(nombreDatoReserva, valor);			
				}
				else {
					//Tipo simple: valor
					reservaDTO.getDatos().put(nombreDatoReserva, valorDatoReserva);			
				}
				
				if("NroDocumento".equals(nombreDatoReserva) && nombreDatoReserva!=null) {
					reservaDTO.setNumeroDocumento(valorDatoReserva.toString());
				}
				
			}


		}

		if (reservaDTO != null) {
			//Al salir del loop, siempre me queda la ultima reserva para agregar al resultado

			reservas.add(reservaDTO);
		}
		
		
		return reservas;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<Atencion> consultarTodasAtencionesPeriodo(Date fechaDesde, Date fechaHasta) {
		String queryString = "select a from Atencion a where date_trunc('day',a.fechaCreacion) >=:fD "
				+ "and date_trunc('day',a.fechaCreacion) <=:fH";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("fD", fechaDesde, TemporalType.DATE);
		query.setParameter("fH", fechaHasta, TemporalType.DATE);
		List<Atencion> resultado = (List<Atencion>)query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<AtencionLLamadaReporteDT> consultarLlamadasAtencionPeriodo(Date fechaDesde, Date fechaHasta)
	{
		String queryString = "select distinct a.funcionario, l.recurso.agenda.nombre, l.recurso.nombre, l.puesto, l.hora, a.fechaCreacion, a.asistio, l.reserva.id, l.reserva.fechaCreacion "
							+ "from Atencion a join a.reserva.llamada l "
							+ "where a.reserva = l.reserva and "
							+ "date_trunc('day',l.fecha) >=:fD and date_trunc('day',l.fecha) <=:fH "
							+ "and l.hora <= a.fechaCreacion";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("fD", fechaDesde, TemporalType.DATE);
		query.setParameter("fH", fechaHasta, TemporalType.DATE);
		List<Object[]> resultados = query.getResultList();
		Iterator<Object[]> iterator = resultados.iterator();
		List<AtencionLLamadaReporteDT> listAtencionLlamada = new ArrayList<AtencionLLamadaReporteDT>();
		while (iterator.hasNext()) {
			Object[] row = iterator.next();
			Boolean asistio = (Boolean)row[6];
			String resolucionAtencion = null;
			if (asistio) {
				resolucionAtencion = "Asistió";
			}else {
				resolucionAtencion = "No Asistió";
			}
			AtencionLLamadaReporteDT atencionLlamada = new AtencionLLamadaReporteDT((String)row[0],(String)row[1],(String)row[2],(Integer)row[3],(Date)row[4],(Date)row[5],resolucionAtencion,(Integer)row[7],(Date)row[8]);
			listAtencionLlamada.add(atencionLlamada);
		}
		queryString = "select l.recurso.agenda.nombre, l.recurso.nombre, l.puesto, l.hora, l.reserva.id, l.reserva.fechaCreacion "
				+ "from Llamada l "
				+ "where date_trunc('day', l.fecha) >=:fD and date_trunc('day', l.fecha) <=:fH "
				+ "and l.reserva.id not IN (select a.reserva.id from Atencion a where date_trunc('day', a.fechaCreacion) >=:fD and "
				+ "date_trunc('day', a.fechaCreacion) <=:fH)";
		query = entityManager.createQuery(queryString);
		query.setParameter("fD", fechaDesde, TemporalType.DATE);
		query.setParameter("fH", fechaHasta, TemporalType.DATE);
		resultados = query.getResultList();
		iterator = resultados.iterator();
		while (iterator.hasNext()) {
			Object[] row = iterator.next();
			AtencionLLamadaReporteDT atencionLlamada = new AtencionLLamadaReporteDT(null,(String)row[0],(String)row[1],(Integer)row[2],(Date)row[3],null,null,(Integer)row[4],(Date)row[5]);
			listAtencionLlamada.add(atencionLlamada);
		}
		return listAtencionLlamada;
	}
	
	/**
	 * Esta función es utilizada por el servicio web REST para determinar las fechas de las reservas
	 * que tiene una persona identificada por su tipo y número de documento en una agenda y recurso especial; la
	 * empresa se determina en base al token, el cual debe estar registrado (el organismo que desee invocar el
	 * servicio tendrá que solicitar un token para cada empresa)
	 */
	public List<Date> consultarReservasPorTokenYDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc)
			throws BusinessException{
			
			if(idAgenda==null && idRecurso==null) {
				throw new BusinessException("Debe especificar el recurso o la agenda");
			}
		
			//Determinar el esquema sobre el cual hay que hacer la consulta en base al token
			try {
				String query = "SELECT t FROM Token t WHERE t.token=:token";
				Token oToken = (Token) globalEntityManager.createQuery(query).setParameter("token", token).getSingleResult();
				String esquema = oToken.getEmpresa().getDatasource();
				query = "SELECT dis.id, dis.fecha, dis.hora_inicio "
						+ " FROM {esquema}.ae_reservas res "
						+ " JOIN {esquema}.ae_datos_reserva dr1 ON dr1.aers_id=res.id "
						+ " JOIN {esquema}.ae_datos_a_solicitar ds1 ON ds1.id=dr1.aeds_id "
						+ " JOIN {esquema}.ae_datos_reserva dr2 ON dr2.aers_id=res.id "
						+ " JOIN {esquema}.ae_datos_a_solicitar ds2 ON ds2.id=dr2.aeds_id "
						+ " JOIN {esquema}.ae_reservas_disponibilidades rd ON rd.aers_id=res.id "
						+ " JOIN {esquema}.ae_disponibilidades dis ON dis.id=rd.aedi_id ";
				//Si se tiene el id del recurso se filtra por ese valor, sino por el id de agenda
				if(idRecurso!=null) {
					query = query + " WHERE dis.aere_id=:idRecurso ";
				}else if(idAgenda!=null) {
					query = query 
						+ " JOIN {esquema}.ae_recursos rec ON rec.id=dis.aere_id "
						+ " WHERE rec.aeag_id=:idAgenda ";
				}
				query = query
						+ "   AND ds1.nombre='NroDocumento' "
						+ "   AND dr1.valor=:numDoc "
						+ "   AND ds2.nombre='TipoDocumento' "
						+ "   AND dr2.valor=:tipoDoc "
						+ "   AND dis.fecha>=:hoy "
						+ " ORDER BY dis.fecha, dis.hora_inicio";
				query = query.replace("{esquema}", esquema);
				Query query1 = globalEntityManager.createNativeQuery(query);
				if(idRecurso!=null) {
					query1.setParameter("idRecurso", idRecurso);
				}else if(idAgenda!=null) {
					query1.setParameter("idAgenda", idAgenda);
				}
				query1.setParameter("numDoc", numDoc);
				query1.setParameter("tipoDoc", tipoDoc);
				query1.setParameter("hoy", new Date(), TemporalType.DATE);
				@SuppressWarnings("unchecked")
        List<Object[]> ress = query1.getResultList();
				List<Date> resp = new ArrayList<Date>();
				for(Object[] res : ress) {
					Date fecha = (Date) res[2];
					resp.add(fecha);
				}
				return resp;
			}catch(NoResultException nrEx) {
				throw new BusinessException("No se encontró el token especificado");		
			}catch(NonUniqueResultException nurEx) {
				throw new BusinessException("Se encontró más de un token");		
			}
			
		}
			
}
