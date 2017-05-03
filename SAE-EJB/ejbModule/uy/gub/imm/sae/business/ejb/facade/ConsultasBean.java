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
import uy.gub.imm.sae.common.enumerados.TipoCancelacion;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.entity.global.Token;
import uy.gub.imm.sae.exception.UserException;

@Stateless
public class ConsultasBean implements ConsultasLocal, ConsultasRemote{

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;
	
	/**
	 * Obtiene una reserva por su identificador.
	 * No toma en cuenta si la reserva corresponde o no a una disponibilidad presencial.
	 */
	public Reserva consultarReservaId(Integer idReserva, Integer idRecurso) throws UserException {
		if (idReserva == null) {
			throw new UserException("debe_especificar_la_reserva");
		}
	  //Cargar la reserva
	  Reserva reserva = entityManager.find(Reserva.class, idReserva);
    if (reserva != null) {
      //Verificar que la reserva corresponda al recurso indicado
      if(!reserva.getDisponibilidades().get(0).getRecurso().getId().equals(idRecurso)) {
        throw new UserException("la_reserva_no_corresponde_al_recurso_seleccionado");
      }
      //Forzar la carga de datos lazy
      reserva.getDisponibilidades().size();
      reserva.getDatosReserva().size();
    }
    return reserva;
	}
	
  /**
   * Obtiene una reserva por la combinación de fecha/hora/número.
   * No toma en cuenta a las reservas que corresponden a disponibilidades presenciales.
   */
	public Reserva consultarReservaPorNumero(Recurso recurso, Date fechaHoraInicio, Integer numero) throws UserException {
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null || recurso.getFechaBaja() != null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		if (fechaHoraInicio == null) {
			throw new UserException("el_dia_y_la_hora_son_obligatorios");
		}
		if (numero == null) {
			throw new UserException("el_numero_es_obligatorio");
		}
		try {
		  Reserva reserva = (Reserva) entityManager.createQuery(
				 "SELECT res " +
				 "FROM Reserva res " +
				 "JOIN res.disponibilidades d " +
				 "WHERE d.recurso = :recurso " +
         "  AND d.fechaBaja IS NULL " +
         "  AND d.presencial = false " +
				 "  AND d.fecha = :fecha " +
				 "  AND d.horaInicio = :horaInicio " +
				 "  AND res.numero = :numero")
			 .setParameter("recurso", recurso)
			 .setParameter("fecha", fechaHoraInicio, TemporalType.DATE)
			 .setParameter("horaInicio", fechaHoraInicio, TemporalType.TIMESTAMP)
			 .setParameter("numero", numero)
			 .getSingleResult();
	    reserva.getDisponibilidades().size();
	    reserva.getDatosReserva().size();
	    return reserva;
		} catch ( NoResultException e) { 
			throw new UserException("no_se_encuentra_la_reserva_especificada");
		}
	}
	
	/**
	 * Busca las reservas confirmadas existentes que tengan los mismos valores en todos los datos claves y que correspondan al trámite indicado.
	 * No considera reservas que corresponden a disponibilidades presenciales. 
	 * @param datos
	 * @param recurso
	 * @param fecha
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Reserva> consultarReservaDatosFecha(List<DatoReserva> datos , Recurso recurso, Date fecha, String codigoTramite){
		String selectStr =	" SELECT DISTINCT(reserva) " ;
		String fromStr =   	" FROM Reserva reserva " +
						   					"	JOIN reserva.disponibilidades disp " +
					   						" JOIN reserva.datosReserva datoReserva " +
				   							"	JOIN datoReserva.datoASolicitar datoSolicitar	" ;
		String whereStr = 	" WHERE reserva.tramiteCodigo = :tramiteCodigo" +
		                    "   AND disp.recurso = :recurso " +
                        "   AND disp.presencial = false " + 
						  					" 	AND disp.fechaBaja IS NULL " + 
					  						"   AND disp.fecha = :fecha " +
				  							"   AND reserva.estado NOT IN ('U','C') ";
		boolean hayCamposClaveNulos = false;
		if (! datos.isEmpty()) {
			whereStr = whereStr + "    AND (" ;
			boolean primerRegistro = true;
			int i = 0;
			for (DatoReserva datoR : datos){
				if (datoR != null){
					if (primerRegistro){
						whereStr = whereStr + 
						" (UPPER(datoReserva.valor) = '" + datoR.getValor().toUpperCase() 
						  + "' AND datoSolicitar.id = " + datoR.getDatoASolicitar().getId() + ") ";
						primerRegistro = false;
					} else {
						String joinFromAux = " JOIN reserva.datosReserva datoReserva" + i 
						    +" JOIN datoReserva" + i + ".datoASolicitar datoSolicitar" + i;
						fromStr = fromStr + joinFromAux;
						whereStr = whereStr + " AND (UPPER(datoReserva" + i + ".valor) = '" 
						    + datoR.getValor().toUpperCase() + "' AND datoSolicitar" + i 
						    + ".id = " + datoR.getDatoASolicitar().getId() + ") ";
					}
				} else {
					hayCamposClaveNulos = true;
				}
				i++;
			}
			whereStr = whereStr + ")" ;
		}
		String consulta = selectStr + fromStr + whereStr  + " ORDER BY reserva.id ";
		List<Reserva> reservas = (List<Reserva>)entityManager.createQuery(consulta)
      .setParameter("tramiteCodigo", codigoTramite)
			.setParameter("recurso", recurso)
			.setParameter("fecha", fecha, TemporalType.DATE)
			.getResultList();
		
		/* 12/03/2010 - Corrige que al traer reservas con la misma clave que se ingreso, 
		 * se filtren las que tengan algun dato clave mas ingresado (por ejemplo cuando 
		 * un gestor reservo para otras personas y luego reserva para si mismo, 
		 * sin ingresar los datos clave opcional) 
		 */
		if(hayCamposClaveNulos) {
		  reservas = filtrarReservasConMasDatos(reservas, datos);
		}
		//Forzar la carga de datos lazy 
		for (Reserva r : reservas){
			r.getDisponibilidades().size();
			r.getDatosReserva().size();
		}
		return reservas;
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
	
  /**
   * Busca las reservas confirmadas existentes que tengan los mismos valores en todos los datos claves y que correspondan al trámite indicado.
   * No considera reservas que corresponden a disponibilidades presenciales. 
   * @param datos
   * @param recurso
   * @param fecha
   * @return
   */
	@SuppressWarnings("unchecked")
	public List<Reserva> consultarReservaDatos(List<DatoReserva> datos, Recurso recurso){
		List<Reserva> reservas = new ArrayList<Reserva>();
		if (! datos.isEmpty()){
			String selectStr = " SELECT DISTINCT(reserva) " ;
			String fromStr = " FROM Reserva reserva " +
							         "	JOIN reserva.disponibilidades disp " +
		  				         "  JOIN reserva.datosReserva datoReserva " +
		  				         "  JOIN datoReserva.datoASolicitar datoSolicitar	" ;
			String whereStr = " WHERE disp.recurso = :recurso " +
                        "   AND disp.presencial = false " +
		  				          "   AND reserva.estado <> 'P' " ;
			boolean primerRegistro = true;
			int i = 0;
			for (DatoReserva datoR : datos){
				if((datoR.getValor() != null) && (!datoR.getValor().equalsIgnoreCase("NoSeleccion"))){
					if ((primerRegistro)){
						whereStr = whereStr + " AND ((upper(datoReserva.valor) = '" 
						    + datoR.getValor().toUpperCase() + "' AND datoSolicitar.id = " 
						    + datoR.getDatoASolicitar().getId() + ") ";
						primerRegistro = false;
					} else {
						fromStr = fromStr + " JOIN reserva.datosReserva datoReserva" + i 
						                  + " JOIN datoReserva" + i + ".datoASolicitar datoSolicitar" + i;
						whereStr = whereStr + " AND (UPPER(datoReserva" + i + ".valor) = '" 
						                    + datoR.getValor().toUpperCase() + "' AND datoSolicitar" + i 
						                    + ".id = " + datoR.getDatoASolicitar().getId() + ") ";
					}
				}
				i++;
			}
			String consulta = selectStr + fromStr + whereStr  + ") ORDER BY reserva.fechaCreacion DESC ";
			
			reservas = (List<Reserva>)entityManager.createQuery(consulta)
				.setParameter("recurso", recurso)
				.getResultList();
			//Forzar la carga de datos lazy
			for (Reserva reserva : reservas){
			  reserva.getDisponibilidades().size();
			  reserva.getDatosReserva().size();
			}
		}
		return reservas;
	}

	/**
	 * Busca reservas en base a los datos especificados para ser usada para la cancelación.
	 * No toma en cuenta las reservas correspondientes a atenciones presenciales.
	 */
	@SuppressWarnings("unchecked")
	public List<Reserva> consultarReservasParaCancelar(List<DatoReserva> datos, Recurso recurso, String codigoSeguridadReserva, TimeZone timezone){
		String selectStr = "SELECT DISTINCT(reserva) " ;
		String fromStr = " FROM Reserva reserva " +
						         " JOIN reserva.disponibilidades disp " +
	  				         " JOIN reserva.datosReserva datoReserva " +
	  				         " JOIN datoReserva.datoASolicitar datoSolicitar	" ;
		String whereStr = " WHERE disp.recurso = :recurso " +
		                  "  AND disp.presencial = false " +
					            "  AND reserva.estado = 'R' " +
					            "  AND reserva.codigoSeguridad = :codigoSeguridad " +
					            "  AND disp.horaInicio >= :fecha " ;
		boolean primerRegistro = true;
		int i = 0;
		for (DatoReserva datoR : datos){
			if((datoR.getValor() != null) && (!datoR.getValor().equalsIgnoreCase("NoSeleccion"))){
				if ((primerRegistro)){
					whereStr = whereStr + 
					" AND ((upper(datoReserva.valor) = '" + datoR.getValor().toUpperCase() + "'" +
					" AND datoSolicitar.id = " + datoR.getDatoASolicitar().getId() + ") ";
					primerRegistro = false;
				} else {
					String joinFromAux =  " JOIN reserva.datosReserva datoReserva" + i +
										            " JOIN datoReserva" + i + ".datoASolicitar datoSolicitar" + i;
					fromStr = fromStr + joinFromAux;
					whereStr = whereStr + " AND (UPPER(datoReserva" + i + ".valor) = '" + datoR.getValor().toUpperCase() + "'" +
						" AND datoSolicitar" + i + ".id = " + datoR.getDatoASolicitar().getId() + ") ";
				}
			}
			i++;
		}
		String consulta = selectStr + fromStr + whereStr ;
    consulta = consulta + ") ORDER BY reserva.fechaCreacion DESC ";

    Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTime().getTime()));
		List<Reserva> reservas = (List<Reserva>)entityManager.createQuery(consulta)
							.setParameter("recurso", recurso)
							.setParameter("codigoSeguridad", codigoSeguridadReserva)
							.setParameter("fecha", cal.getTime(), TemporalType.TIMESTAMP)
							.getResultList();
		//Forzar la carga de datos lazy
		for (Reserva reserva : reservas){
		  reserva.getDisponibilidades().size();
		  reserva.getDatosReserva().size();
		}
		return reservas;
	}
	
	/**
	 * Busca las reservas de un recurso para un período de tiempo según el estado.
	 */
	public List<ReservaDTO> consultarReservasPorPeriodoEstado(Recurso recurso, VentanaDeTiempo ventana, List<Estado> estados, Boolean atencionPresencial) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
    if (ventana == null) {
      throw new UserException("debe_especificar_la_ventana");
    }
		if (ventana.getFechaInicial() == null || ventana.getFechaFinal() == null) {
			throw new UserException("la_ventana_especificada_no_es_valida");
		}
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}		
		return consultarReservasPorPeriodoEstadosDisponibilidades(recurso, ventana, estados, new ArrayList<Integer>(), atencionPresencial);
	}

  /**
   * Busca las reservas de un recurso para un período de tiempo que estén en el estado Reservado (esperando ser llamadas por primera vez).
   */
	public List<ReservaDTO> consultarReservasEnEspera(Recurso recurso, Boolean atencionPresencial, TimeZone timezone) throws UserException {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTimeInMillis()));
		Date hoy = cal.getTime();
		VentanaDeTiempo periodo = new VentanaDeTiempo();
		periodo.setFechaInicial(Utiles.time2FinDelDia(hoy));
		periodo.setFechaFinal(Utiles.time2FinDelDia(hoy));
		List<Estado> estados = new ArrayList<Estado>();
		estados.add(Estado.R);
		List<Integer> disponibilidadesIds = consultarDisponibilidadesReservadasYUtilizadas(recurso, periodo, atencionPresencial);
		return consultarReservasPorPeriodoEstadosDisponibilidades(recurso, periodo, estados, disponibilidadesIds, atencionPresencial);
	}

  /**
   * Busca las reservas de un recurso para un período de tiempo que estén en el estado Reservado o Usado (esperando ser llamadas).
   */
	public List<ReservaDTO> consultarReservasEnEsperaUtilizadas(Recurso recurso, Boolean atencionPresencial, TimeZone timezone) throws UserException {
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTimeInMillis()));
		Date hoy = cal.getTime();

		VentanaDeTiempo periodo = new VentanaDeTiempo();
		periodo.setFechaInicial(Utiles.time2FinDelDia(hoy));
		periodo.setFechaFinal(Utiles.time2FinDelDia(hoy));
		List<Estado> estados = new ArrayList<Estado>();
		estados.add(Estado.R);
		estados.add(Estado.U);

		List<Integer> disponibilidadesIds = consultarDisponibilidadesReservadasYUtilizadas(recurso, periodo, atencionPresencial);
		
		return consultarReservasPorPeriodoEstadosDisponibilidades(recurso, periodo, estados, disponibilidadesIds, atencionPresencial);
	}

  /**
   * Consulta las disponibilidades que tienen alguna reserva para el período indicado.	
   * Si el parámetro atencionPresencial es null se ignora, sino se busca que la disponibilidad coincida con ese parámetro
   */
	@SuppressWarnings("unchecked")	
	private List<Integer> consultarDisponibilidadesReservadasYUtilizadas(Recurso recurso, VentanaDeTiempo periodo, Boolean atencionPresencial) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
		Query queryR = entityManager.createQuery(
				"SELECT distinct d.id, d.fecha, d.horaInicio " +
				"FROM Reserva r " +
				"JOIN r.disponibilidades d " +
				"WHERE d.recurso.id = :recurso " +
				(atencionPresencial!=null?(atencionPresencial?"  AND d.presencial = true ":"  AND d.presencial = false "):"") +
				"  AND d.fecha BETWEEN :fi AND :ff " +
				"  AND r.estado = :estado " +
				"ORDER BY d.fecha, d.horaInicio");
		queryR.setParameter("recurso", recurso.getId());
		queryR.setParameter("estado", Estado.R);
		queryR.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
		queryR.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);
		if (recurso.getLargoListaEspera() != null && recurso.getLargoListaEspera() > 0){
			queryR.setMaxResults(recurso.getLargoListaEspera());
		}
		List<Object[]> dispsR = queryR.getResultList();
		List<Object[]> dispsU = null;
		if (!dispsR.isEmpty()) {
			Query queryU = entityManager.createQuery(
				"SELECT DISTINCT d.id, d.fecha, d.horaInicio " +
				"FROM Reserva r " +
				"JOIN r.disponibilidades d " +
				"WHERE d.recurso.id = :recurso " +
				(atencionPresencial!=null?(atencionPresencial?"  AND d.presencial = true ":"  AND d.presencial = false "):"") +
				"  AND d.fecha BETWEEN :fi AND :ff " +
				"  AND r.estado = :estado " +
				"	 AND d.horaInicio < :hora " +
				"ORDER BY d.fecha DESC, d.horaInicio DESC " 
			);
			queryU.setParameter("recurso", recurso.getId());
			queryU.setParameter("estado", Estado.U);
			queryU.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
			queryU.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);
			queryU.setParameter("hora", (Date)dispsR.get(0)[2], TemporalType.TIMESTAMP);
			if (recurso.getLargoListaEspera() != null && recurso.getLargoListaEspera().intValue() > 0){
				queryU.setMaxResults(recurso.getLargoListaEspera());
			}
			dispsU = queryU.getResultList();
		}	else {
			Query queryU = entityManager.createQuery(
					"SELECT distinct d.id, d.fecha, d.horaInicio " +
					"FROM Reserva r " +
					"JOIN r.disponibilidades d " +
					"WHERE d.recurso.id = :recurso " +
					(atencionPresencial!=null?(atencionPresencial?"  AND d.presencial = true ":"  AND d.presencial = false "):"") +
					"  AND d.fecha BETWEEN :fi AND :ff " +
					"  AND r.estado = :estado " +
					"ORDER BY d.fecha DESC, d.horaInicio DESC ");
			queryU.setParameter("recurso", recurso.getId());
			queryU.setParameter("estado", Estado.U);
			queryU.setParameter("fi", periodo.getFechaInicial(), TemporalType.DATE);
			queryU.setParameter("ff", periodo.getFechaFinal(), TemporalType.DATE);
			if (recurso.getLargoListaEspera() != null && recurso.getLargoListaEspera().intValue() > 0){
				queryU.setMaxResults(recurso.getLargoListaEspera());
			}
			dispsU = queryU.getResultList();
		}
		List<Integer> dispIds = new ArrayList<Integer>();
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
	private List<ReservaDTO> consultarReservasPorPeriodoEstadosDisponibilidades(Recurso recurso, VentanaDeTiempo periodo, List<Estado> estados, List<Integer> disponibilidadesIds, Boolean atencionPresencial) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		Map<Integer, Map<String,String>> valoresPosiblesPorEtiqueta = armoMapaCampoValorEtiqueta(recurso);
		int i;
		String whereEstados = null;
		for (i=1; i <= estados.size(); i++) {
			if (whereEstados != null) {
				whereEstados += " OR ";
			} else {
				whereEstados = "";
			}
			whereEstados += "r.estado = :estado" + i;
		}
		String whereDispIds = null;
		for (i=1; i <= disponibilidadesIds.size(); i++) {
			if (whereDispIds != null) {
				whereDispIds += " OR ";
			} else {
				whereDispIds = "";
			}
			whereDispIds += "d.id = :dispId" + i;
		}
		String where = "";
		if (whereEstados != null) {
			where += "AND ( " + whereEstados + " ) ";
		}
		if (whereDispIds != null) {
			where += "AND ( " + whereDispIds + " ) ";
		}
		if(atencionPresencial != null) {
  		if(atencionPresencial.booleanValue()) {
        where += "AND d.presencial = true ";
  		}else {
        where += "AND d.presencial = false ";
  		}
		}
		String queryString = 
			"SELECT r.id, r.numero, r.estado, d.id, d.fecha, d.horaInicio, das.id, das.nombre, das.tipo, dr.valor, ll.puesto, a.asistio, r.tramiteCodigo, "	+ 
			    "r.tramiteNombre, d.presencial, r.fcancela, r.ucancela, r.tcancela " +
			"FROM Reserva r " +
			"JOIN r.disponibilidades d " +
			"LEFT JOIN r.datosReserva dr " +
			"LEFT JOIN dr.datoASolicitar das " +
			"LEFT JOIN r.llamada ll " +
			"LEFT JOIN r.atenciones a "+
			"WHERE d.recurso.id = :recurso " +
			"  AND d.fecha BETWEEN :fi AND :ff " +
			where +
			"ORDER BY d.fecha, d.horaInicio, r.numero "; 
		
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
			String reservaEstado    = ((Estado) rowReserva[2]).toString();
			Date dispFecha        = (Date) rowReserva[4];
			Date dispHoraInicio   = (Date) rowReserva[5];
			Integer datoASolicitarId = (Integer) rowReserva[6];
			String nombreDatoReserva= (String) rowReserva[7];
			Tipo tipoDatoReserva  = (Tipo) rowReserva[8];
			Object valorDatoReserva = (Object) rowReserva[9];
			Integer puesto           = (Integer)rowReserva[10];
			Boolean asistio          = (Boolean)rowReserva[11];
      String tramiteCodigo    = (String) rowReserva[12];
      String tramiteNombre    = (String) rowReserva[13];
      Boolean presencial       = (Boolean)rowReserva[14];
      Date fcancela           = (Date) rowReserva[15];
      String ucancela         = (String) rowReserva[16];
      TipoCancelacion tcancela    = (TipoCancelacion) rowReserva[17];
			if (idReservaActual == null || ! idReservaActual.equals(reservaId)) {
				idReservaActual = reservaId;
				if (reservaDTO != null) {
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
		    reservaDTO.setTramiteCodigo(tramiteCodigo);
		    reservaDTO.setTramiteNombre(tramiteNombre);
		    reservaDTO.setPresencial(presencial);
        reservaDTO.setFcancela(fcancela);
        reservaDTO.setUcancela(ucancela);
        reservaDTO.setTcancela(tcancela==null?null:tcancela.toString());
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
	
	
	@SuppressWarnings("unchecked")
  public List<ReservaDTO> consultarReservasUsadasPeriodo(Recurso recurso, VentanaDeTiempo ventana, Boolean atencionPresencial) throws UserException {
		if(recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
    if(ventana == null) {
      throw new UserException("debe_especificar_la_ventana");
    }
		if (ventana.getFechaInicial() == null || ventana.getFechaFinal() == null) {
			throw new UserException("la_ventana_especificada_no_es_valida");
		}
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}		
		
    Map<Integer, Map<String,String>> valoresPosiblesPorEtiqueta = armoMapaCampoValorEtiqueta(recurso);
    
    String queryString = 
      "SELECT r.id, r.numero, r.estado, d.id, d.fecha, d.horaInicio, das.id, das.nombre, das.tipo, dr.valor, ll.puesto, at.asistio, r.tramiteCodigo, r.tramiteNombre, d.presencial " +
      "FROM Reserva r " +
      "JOIN r.disponibilidades d " +
      "LEFT JOIN r.datosReserva dr " +
      "LEFT JOIN dr.datoASolicitar das " +
      "LEFT JOIN r.llamada ll " +
      "LEFT JOIN r.atenciones at "+
      "WHERE d.recurso.id = :recurso " +
      (atencionPresencial!=null?(atencionPresencial.booleanValue()?"  AND d.presencial = true ":"  AND d.presencial = false "):"") +
      "  AND d.fecha BETWEEN :fi AND :ff " +
      "  AND r.estado = 'U' " +
      "ORDER BY d.fecha, d.horaInicio, r.numero"; 

    Query query = entityManager.createQuery(queryString);
    query.setParameter("recurso", recurso.getId());
    query.setParameter("fi", ventana.getFechaInicial(), TemporalType.DATE);
    query.setParameter("ff", ventana.getFechaFinal(), TemporalType.DATE);

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
      Date    dispFecha        = (Date) rowReserva[4];
      Date    dispHoraInicio   = (Date) rowReserva[5];
      Integer datoASolicitarId = (Integer) rowReserva[6];
      String  nombreDatoReserva= (String) rowReserva[7];
      Tipo    tipoDatoReserva  = (Tipo) rowReserva[8];
      Object  valorDatoReserva = (Object) rowReserva[9];
      Integer puesto           = (Integer)rowReserva[10];
      Boolean asistio          = (Boolean)rowReserva[11];
      String  tramiteCodigo    = (String) rowReserva[12];
      String  tramiteNombre    = (String) rowReserva[13];
      Boolean presencial       = (Boolean) rowReserva[14];
      if (idReservaActual == null || ! idReservaActual.equals(reservaId)) {
        idReservaActual = reservaId;
        if (reservaDTO != null) {
          reservas.add(reservaDTO);
        }
        reservaDTO = new ReservaDTO();
        reservaDTO.setId(reservaId);
        reservaDTO.setNumero(reservaNumero);
        reservaDTO.setEstado(reservaEstado);
        reservaDTO.setFecha(dispFecha);
        reservaDTO.setHoraInicio(dispHoraInicio);
        reservaDTO.setPresencial(presencial);
        if (puesto != null) {
          reservaDTO.setPuestoLlamada(puesto);
        }
        if (asistio != null){
          reservaDTO.setAsistio(asistio);
        }
        reservaDTO.setTramiteCodigo(tramiteCodigo);
        reservaDTO.setTramiteNombre(tramiteNombre);
      }
      if (nombreDatoReserva != null) {
        if (tipoDatoReserva == Tipo.LIST) {
          String valor = valoresPosiblesPorEtiqueta.get(datoASolicitarId).get(valorDatoReserva);
          reservaDTO.getDatos().put(nombreDatoReserva, valor);      
        }else {
          reservaDTO.getDatos().put(nombreDatoReserva, valorDatoReserva);     
        }
        if("NroDocumento".equals(nombreDatoReserva) && nombreDatoReserva!=null) {
          reservaDTO.setNumeroDocumento(valorDatoReserva.toString());
        }
      }
    }
    if (reservaDTO != null) {
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
	  //Atenciones
		String queryString = "select distinct a.funcionario, l.recurso.agenda.nombre, l.recurso.nombre, l.puesto, l.hora, a.fechaCreacion, a.asistio, l.reserva.id, "
		    + "l.reserva.serie, l.reserva.numero, l.reserva.fechaCreacion, l.reserva.tramiteCodigo, l.reserva.tramiteNombre "
				+ "FROM Atencion a "
				+ "JOIN a.reserva.llamada l "
				+ "WHERE a.reserva = l.reserva "
				+ "  AND DATE_TRUNC('day',l.fecha) >=:fD "
				+ "  AND DATE_TRUNC('day',l.fecha) <=:fH "
				+ "  AND l.hora <= a.fechaCreacion";
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
			AtencionLLamadaReporteDT atencionLlamada = new AtencionLLamadaReporteDT((String)row[0], (String)row[1], (String)row[2], (Integer)row[3], (Date)row[4], 
			    (Date)row[5], resolucionAtencion, (Integer)row[7], (String)row[8], (Integer)row[9], (Date)row[10], (String)row[11], (String)row[12]);
			listAtencionLlamada.add(atencionLlamada);
		}
		//Llamadas que no tienen atenciones
		queryString = "select l.recurso.agenda.nombre, l.recurso.nombre, l.puesto, l.hora, l.reserva.id, l.reserva.serie, l.reserva.numero, l.reserva.fechaCreacion, "
		    + "l.reserva.tramiteCodigo, l.reserva.tramiteNombre "
				+ "FROM Llamada l "
				+ "WHERE DATE_TRUNC('day', l.fecha) >=:fD "
				+ "  AND DATE_TRUNC('day', l.fecha) <=:fH "
				+ "  AND l.reserva.id NOT IN ("
				+ "       SELECT a.reserva.id "
				+ "       FROM Atencion a "
				+ "       WHERE DATE_TRUNC('day', a.fechaCreacion) >=:fD "
				+ "         AND DATE_TRUNC('day', a.fechaCreacion) <=:fH)";
		query = entityManager.createQuery(queryString);
		query.setParameter("fD", fechaDesde, TemporalType.DATE);
		query.setParameter("fH", fechaHasta, TemporalType.DATE);
		resultados = query.getResultList();
		iterator = resultados.iterator();
		while (iterator.hasNext()) {
			Object[] row = iterator.next();
			AtencionLLamadaReporteDT atencionLlamada = new AtencionLLamadaReporteDT(null, (String)row[0], (String)row[1], (Integer)row[2], (Date)row[3], null, null, 
			    (Integer)row[4], (String)row[5], (Integer)row[6], (Date)row[7], (String)row[8], (String)row[9]);
			listAtencionLlamada.add(atencionLlamada);
		}
		return listAtencionLlamada;
	}
	
	/**
	 * Esta función es utilizada por el servicio web REST para determinar las fechas de las reservas
	 * que tiene una persona identificada por su tipo y número de documento en una agenda y recurso especial; la
	 * empresa se determina en base al token, el cual debe estar registrado (el organismo que desee invocar el
	 * servicio tendrá que solicitar un token para cada empresa).
	 * No considera las reservas correspondientes a disponibilidades presenciales.
	 */
	public List<Date> consultarReservasPorTokenYDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc) throws UserException{
			
		if(idAgenda==null && idRecurso==null) {
			throw new UserException("debe_especificar_la_agenda_o_el_recurso");
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
          + "   AND dis.presencial = false "
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
			throw new UserException("no_se_encuentra_el_token_especificado");		
		}catch(NonUniqueResultException nurEx) {
			throw new UserException("no_se_encuentra_el_token_especificado");		
		}
		
	}
		
	
  @SuppressWarnings("unchecked")
  public List<AtencionLLamadaReporteDT> consultarAtencionesPresencialesRecursoPeriodo(Recurso recurso, Date fechaDesde, Date fechaHasta) {
    //Atenciones
    String queryString = "select distinct a.funcionario, l.recurso.agenda.nombre, l.recurso.nombre, l.puesto, l.hora, a.fechaCreacion, a.asistio, "
        + "l.reserva.id, l.reserva.serie, l.reserva.numero, l.reserva.fechaCreacion, l.reserva.tramiteCodigo, l.reserva.tramiteNombre "
        + "FROM Atencion a, Llamada l, IN(l.reserva.disponibilidades) d "
        + "WHERE a.reserva = l.reserva "
        + "  AND l.recurso = :recurso"
        + "  AND d.presencial = true"
        + "  AND DATE_TRUNC('day',l.fecha) >=:fD "
        + "  AND DATE_TRUNC('day',l.fecha) <=:fH "
        + "  AND l.hora <= a.fechaCreacion"; //Esto es solo para considerar la última atención de todas las llamadas de la misma reserva
    Query query = entityManager.createQuery(queryString);
    query.setParameter("fD", fechaDesde, TemporalType.DATE);
    query.setParameter("fH", fechaHasta, TemporalType.DATE);
    query.setParameter("recurso", recurso);
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
      AtencionLLamadaReporteDT atencionLlamada = new AtencionLLamadaReporteDT((String)row[0], (String)row[1], (String)row[2], (Integer)row[3], (Date)row[4], 
          (Date)row[5], resolucionAtencion, (Integer)row[7], (String)row[8], (Integer)row[9], (Date)row[10], (String)row[11], (String)row[12]);
      
      listAtencionLlamada.add(atencionLlamada);
    }
    //Llamadas que no tienen atenciones
    queryString = "select l.recurso.agenda.nombre, l.recurso.nombre, l.puesto, l.hora, l.reserva.id, l.reserva.serie, l.reserva.numero, l.reserva.fechaCreacion, "
        + "l.reserva.tramiteCodigo, l.reserva.tramiteNombre "
        + "FROM Llamada l, IN(l.reserva.disponibilidades) d "
        + "WHERE l.recurso = :recurso"
        + "  AND d.presencial = true "
        + "  AND DATE_TRUNC('day', l.fecha) >=:fD "
        + "  AND DATE_TRUNC('day', l.fecha) <=:fH "
        + "  AND l.reserva.id NOT IN ("
        + "       SELECT a.reserva.id "
        + "       FROM Atencion a "
        + "       WHERE DATE_TRUNC('day', a.fechaCreacion) >=:fD "
        + "         AND DATE_TRUNC('day', a.fechaCreacion) <=:fH)";
    query = entityManager.createQuery(queryString);
    query.setParameter("fD", fechaDesde, TemporalType.DATE);
    query.setParameter("fH", fechaHasta, TemporalType.DATE);
    query.setParameter("recurso", recurso);
    resultados = query.getResultList();
    iterator = resultados.iterator();
    while (iterator.hasNext()) {
      Object[] row = iterator.next();
      AtencionLLamadaReporteDT atencionLlamada = new AtencionLLamadaReporteDT(null, (String)row[0], (String)row[1], (Integer)row[2], (Date)row[3], null, null, 
          (Integer)row[4], (String)row[5], (Integer)row[6], (Date)row[7], (String)row[8], (String)row[9]);
      
      listAtencionLlamada.add(atencionLlamada);
    }
    return listAtencionLlamada;
  }
}
