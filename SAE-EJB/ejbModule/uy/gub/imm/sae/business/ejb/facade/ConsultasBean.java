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
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;

import uy.gub.imm.sae.business.dto.AtencionLLamadaReporteDT;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.common.enumerados.TipoCancelacion;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.entity.global.Configuracion;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Token;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
public class ConsultasBean implements ConsultasLocal, ConsultasRemote {

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;
	
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
  private AgendarReservas agendarReservasEJB;
  
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
  private Recursos recursosEJB;
  
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
  private UsuariosEmpresas empresasEJB;
  
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
	public List<Reserva> consultarReservaDatosPeriodo(List<DatoReserva> datos , Recurso recurso, Date fechaDesde, Date fechaHasta, String codigoTramite){
		String selectStr =	" SELECT DISTINCT(reserva) " ;
		String fromStr =   	" FROM Reserva reserva " +
						   					"	JOIN reserva.disponibilidades disp " +
					   						" JOIN reserva.datosReserva datoReserva " +
				   							"	JOIN datoReserva.datoASolicitar datoSolicitar	" ;
		String whereStr = 	" WHERE reserva.tramiteCodigo = :tramiteCodigo" +
		                    "   AND disp.recurso = :recurso " +
                        "   AND disp.presencial = false " + 
						  					" 	AND disp.fechaBaja IS NULL " + 
					  						"   AND disp.fecha >= :fechaDesde " +
                        "   AND disp.fecha <= :fechaHasta " +
				  							"   AND reserva.estado NOT IN (:usada,:cancelada) ";
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
			.setParameter("fechaDesde", fechaDesde, TemporalType.DATE)
      .setParameter("fechaHasta", fechaHasta, TemporalType.DATE)
      .setParameter("usada", Estado.U)
      .setParameter("cancelada", Estado.C)
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
	public List<Reserva> consultarReservasParaModificarCancelar(List<DatoReserva> datos, Recurso recurso, String codigoSeguridadReserva, TimeZone timezone){
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
			String valorDatoReserva = (String) rowReserva[9];
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
			  if(valorDatoReserva == null) {
			    valorDatoReserva = "";
			  }
				if (tipoDatoReserva == Tipo.LIST) {
					String valor = valoresPosiblesPorEtiqueta.get(datoASolicitarId).get(valorDatoReserva);
					if(valor==null) {
					  valor = valorDatoReserva;
					}
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
	public List<Map<String, Object>> consultarReservasPorTokenYDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc, 
	    String codTramite) throws UserException{
		if(idAgenda==null && idRecurso==null) {
			throw new UserException("debe_especificar_la_agenda_o_el_recurso");
		}
		//Determinar el esquema sobre el cual hay que hacer la consulta en base al token
		try {
			String query = "SELECT t FROM Token t WHERE t.token=:token";
			Token oToken = (Token) globalEntityManager.createQuery(query).setParameter("token", token).getSingleResult();
			String esquema = oToken.getEmpresa().getDatasource();
			query = "SELECT dis.hora_inicio as reservaFechaHora, rec.nombre as nombreRecurso, res.codigo_seguridad as codigoCancelacion"
					+ " FROM {esquema}.ae_reservas res "
					+ " JOIN {esquema}.ae_datos_reserva dr1 ON dr1.aers_id=res.id "
					+ " JOIN {esquema}.ae_datos_a_solicitar ds1 ON ds1.id=dr1.aeds_id "
					+ " JOIN {esquema}.ae_datos_reserva dr2 ON dr2.aers_id=res.id "
					+ " JOIN {esquema}.ae_datos_a_solicitar ds2 ON ds2.id=dr2.aeds_id "
					+ " JOIN {esquema}.ae_reservas_disponibilidades rd ON rd.aers_id=res.id "
					+ " JOIN {esquema}.ae_disponibilidades dis ON dis.id=rd.aedi_id "
			    + " JOIN {esquema}.ae_recursos rec ON rec.id=dis.aere_id ";
			//Si se tiene el id del recurso se filtra por ese valor, sino por el id de agenda
			if(idRecurso!=null) {
				query = query + " WHERE dis.aere_id=:idRecurso ";
			}else if(idAgenda!=null) {
				query = query + " WHERE rec.aeag_id=:idAgenda ";
			}else {
			  //Nunca va a entrar acá porque idRecurso o idAgenda deben estar, pero por las dudas
        query = query + " WHERE 1=0 ";
			}
			if(codTramite!=null && !codTramite.trim().isEmpty()) {
		      query = query + " AND res.tramite_codigo=:codTramite";
			}
			query = query
          + "   AND dis.presencial=false "
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
      if(codTramite!=null && !codTramite.trim().isEmpty()) {
        query1.setParameter("codTramite", codTramite.trim());
      }
			query1.setParameter("numDoc", numDoc);
			query1.setParameter("tipoDoc", tipoDoc);
			query1.setParameter("hoy", new Date(), TemporalType.DATE);
			@SuppressWarnings("unchecked")
      List<Object[]> ress = query1.getResultList();
			List<Map<String, Object>> resp = new ArrayList<Map<String, Object>>();
			for(Object[] res : ress) {
				Map<String, Object> reserva = new HashMap<String, Object>();
			  reserva.put("fechaHora", res[0]);
        reserva.put("nombreRecurso", res[1]);
        reserva.put("codigoCancelacion", res[2]);
				resp.add(reserva);
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
  
  /**
   * Esta función es utilizada por el servicio web REST para determinar las fechas de las reservas
   * que tiene una persona identificada por su tipo y número de documento en una agenda y recurso especial; la
   * empresa se determina en base al token, el cual debe estar registrado (el organismo que desee invocar el
   * servicio tendrá que solicitar un token para cada empresa).
   * No considera las reservas correspondientes a disponibilidades presenciales.
   */
  public List<Map<String, Object>> consultarReservasPorTokenYAgendaTramiteDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc, 
      String codTramite, Date fechaDesde, Date fechaHasta) throws UserException{
    if(idAgenda==null && idRecurso==null) {
      throw new UserException("debe_especificar_la_agenda_o_el_recurso");
    }
    try {
      if(fechaDesde == null) {
        if(fechaHasta == null) {
          //Ambas fechas son nullas, se considera solo el día de hoy
          fechaDesde = new Date();
          fechaHasta = fechaDesde;
        }else {
          //La fecha final no es nula, se usa como fecha desde un año hacia atrás
          Calendar calFechaHasta = new GregorianCalendar();
          calFechaHasta.setTime(fechaHasta);
          calFechaHasta.add(Calendar.YEAR, -1);
          fechaDesde = calFechaHasta.getTime();
        }
      }else {
        if(fechaHasta == null) {
          //La fecha inicial no es nula, se usa como fecha desde un año hacia adelante
          Calendar calFechaDesde = new GregorianCalendar();
          calFechaDesde.setTime(fechaDesde);
          calFechaDesde.add(Calendar.YEAR, 1);
          fechaHasta = calFechaDesde.getTime();
        }else {
          //Ninguna de las fechas es nula, hay que validar que el período no sea mayor a 1 año
          Calendar calFechaDesde = new GregorianCalendar();
          calFechaDesde.setTime(fechaDesde);
          Calendar calFechaHasta = new GregorianCalendar();
          calFechaHasta.setTime(fechaHasta);
          calFechaDesde.add(Calendar.YEAR, 1);
          if(calFechaDesde.before(calFechaHasta)) {
            throw new UserException("el_periodo_no_puede_ser_mayor_a_un_ano");
          }
        }
      }
      //Determinar el esquema sobre el cual hay que hacer la consulta en base al token
      String query = "SELECT t FROM Token t WHERE t.token=:token";
      Token oToken = (Token) globalEntityManager.createQuery(query).setParameter("token", token).getSingleResult();
      String esquema = oToken.getEmpresa().getDatasource();
      query = "SELECT res.id, res.estado, res.serie, res.numero, res.codigo_seguridad, res.trazabilidad_guid, res.tramite_codigo, res.tramite_nombre,"
          + " dis.hora_inicio, dis.presencial, rec.nombre as nombre_recurso, age.nombre as agenda_recurso, ate.funcionario, ate.asistio, ds.etiqueta, dr.valor"
          + " FROM {esquema}.ae_reservas res "
          + " JOIN {esquema}.ae_reservas_disponibilidades rd ON rd.aers_id=res.id "
          + " JOIN {esquema}.ae_disponibilidades dis ON dis.id=rd.aedi_id "
          + " JOIN {esquema}.ae_recursos rec ON rec.id=dis.aere_id "
          + " JOIN {esquema}.ae_agendas age ON age.id=rec.aeag_id "
          + " JOIN {esquema}.ae_datos_reserva dr ON dr.aers_id=res.id " 
          + " JOIN {esquema}.ae_datos_a_solicitar ds ON ds.id=dr.aeds_id ";
      if(numDoc!=null && !numDoc.trim().isEmpty()) {     
        query = query + " JOIN {esquema}.ae_datos_reserva dr1 ON dr1.aers_id=res.id "
            + " JOIN {esquema}.ae_datos_a_solicitar ds1 ON ds1.id=dr1.aeds_id ";
      }
      if(tipoDoc!=null && !tipoDoc.trim().isEmpty()) {     
        query = query +  " JOIN {esquema}.ae_datos_reserva dr2 ON dr2.aers_id=res.id "
            + " JOIN {esquema}.ae_datos_a_solicitar ds2 ON ds2.id=dr2.aeds_id ";
      }
      query = query + " LEFT JOIN {esquema}.ae_atencion ate ON ate.aers_id=res.id ";
      //Si se tiene el id del recurso se filtra por ese valor, sino por el id de agenda
      if(idRecurso!=null) {
        query = query + " WHERE dis.aere_id=:idRecurso ";
      }else if(idAgenda!=null) {
        query = query + " WHERE rec.aeag_id=:idAgenda ";
      }else {
        //Nunca va a entrar acá porque idRecurso o idAgenda deben estar, pero por las dudas
        query = query + " WHERE 1=0 ";
      }
      if(codTramite!=null && !codTramite.trim().isEmpty()) {
        query = query + " AND res.tramite_codigo=:codTramite";
      }
      if(numDoc!=null && !numDoc.trim().isEmpty()) {
        query = query + "   AND ds1.nombre='NroDocumento' AND dr1.valor=:numDoc ";
      }
      if(tipoDoc!=null && !tipoDoc.trim().isEmpty()) {
        query = query + "   AND ds2.nombre='TipoDocumento' AND dr2.valor=:tipoDoc ";
      }
      if(fechaDesde!=null) {
        query = query + "   AND DATE_TRUNC('day',dis.fecha) >=:fechaDesde ";
      }
      if(fechaHasta!=null) {
        query = query + "   AND DATE_TRUNC('day',dis.fecha) <=:fechaHasta ";
      }
      query = query + " ORDER BY res.id";
      query = query.replace("{esquema}", esquema);
      Query query1 = globalEntityManager.createNativeQuery(query);
      if(idRecurso!=null) {
        query1.setParameter("idRecurso", idRecurso);
      }else if(idAgenda!=null) {
        query1.setParameter("idAgenda", idAgenda);
      }
      if(codTramite!=null && !codTramite.trim().isEmpty()) {
        query1.setParameter("codTramite", codTramite.trim());
      }
      if(numDoc!=null && !numDoc.trim().isEmpty()) {
        query1.setParameter("numDoc", numDoc);
      }
      if(tipoDoc!=null && !tipoDoc.trim().isEmpty()) {
        query1.setParameter("tipoDoc", tipoDoc);
      }
      if(fechaDesde!=null) {
        query1.setParameter("fechaDesde", fechaDesde, TemporalType.DATE);
      }
      if(fechaHasta!=null) {
        query1.setParameter("fechaHasta", fechaHasta, TemporalType.DATE);
      }
      @SuppressWarnings("unchecked")
      List<Object[]> ress = query1.getResultList();
      List<Map<String, Object>> resp = new ArrayList<Map<String, Object>>();
      Integer reservaIdActual = null;
      Map<String, Object> reserva = null;
      Map<String,String> datos = null;
      for(Object[] res : ress) {
        Integer reservaId = (Integer)res[0];
        if(reservaIdActual==null || !reservaIdActual.equals(reservaId)) {
          reservaIdActual = reservaId;
          reserva = new HashMap<String, Object>();
          reserva.put("reservaId", reservaId);
          reserva.put("estado", res[1]);
          reserva.put("serie", res[2]);
          reserva.put("numero", res[3]);
          reserva.put("codigoCancelacion", res[4]);
          reserva.put("codigoTrazabilidad", res[5]);
          reserva.put("codigoTramite", res[6]);
          reserva.put("nombreTramite", res[7]);
          reserva.put("fechaHora", res[8]);
          reserva.put("presencial", res[9]);
          reserva.put("recurso", res[10]);
          reserva.put("agenda", res[11]);
          if(res[12]!=null) {
            reserva.put("funcionario", res[12]);
            reserva.put("asistio", res[13]);
          }
          datos = new HashMap<String,String>();
          reserva.put("datos", datos);
          resp.add(reserva);
        }
        datos.put((String)res[14], (String)res[15]);
      }
      return resp;
    }catch(NoResultException nrEx) {
      throw new UserException("no_se_encuentra_el_token_especificado");   
    }catch(NonUniqueResultException nurEx) {
      throw new UserException("no_se_encuentra_el_token_especificado");   
    }
  }

  /**
   * Esta función es utilizada por el servicio web REST para determinar las reservas vigentes
   * que tiene una persona identificada por su tipo y número de documento en una agenda (y recurso opcional); la
   * empresa se determina en base al token, el cual debe estar registrado (el organismo que desee invocar el
   * servicio tendrá que solicitar un token para cada empresa).
   * No considera las reservas correspondientes a disponibilidades presenciales.
   */
  public List<Map<String, Object>> consultarReservasVigentesPorTokenYAgendaTramiteDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc, 
      String codTramite) throws UserException{
    if(idAgenda==null && idRecurso==null) {
      throw new UserException("debe_especificar_la_agenda_o_el_recurso");
    }
    if(StringUtils.isBlank(tipoDoc) || StringUtils.isBlank(numDoc)) {
      throw new UserException("debe_especificar_el_tipo_y_numero_de_documento");
    }
    if(StringUtils.isBlank(codTramite)) {
      throw new UserException("el_codigo_del_tramite_es_obligatorio");
    }
    try {
      //Determinar el esquema sobre el cual hay que hacer la consulta en base al token
      String query = "SELECT t FROM Token t WHERE t.token=:token";
      Token oToken = (Token) globalEntityManager.createQuery(query).setParameter("token", token).getSingleResult();
      String esquema = oToken.getEmpresa().getDatasource();
      query = "SELECT res.id, res.estado, res.serie, res.numero, res.codigo_seguridad, res.trazabilidad_guid, res.tramite_codigo, res.tramite_nombre,"
          + " dis.hora_inicio, dis.presencial, rec.nombre as nombre_recurso, age.nombre as agenda_recurso, ate.funcionario, ate.asistio, ds.etiqueta, dr.valor"
          + " FROM {esquema}.ae_reservas res "
          + " JOIN {esquema}.ae_reservas_disponibilidades rd ON rd.aers_id=res.id "
          + " JOIN {esquema}.ae_disponibilidades dis ON dis.id=rd.aedi_id "
          + " JOIN {esquema}.ae_recursos rec ON rec.id=dis.aere_id "
          + " JOIN {esquema}.ae_agendas age ON age.id=rec.aeag_id "
          + " JOIN {esquema}.ae_datos_reserva dr ON dr.aers_id=res.id " 
          + " JOIN {esquema}.ae_datos_a_solicitar ds ON ds.id=dr.aeds_id ";
      if(numDoc!=null && !numDoc.trim().isEmpty()) {     
        query = query + " JOIN {esquema}.ae_datos_reserva dr1 ON dr1.aers_id=res.id "
            + " JOIN {esquema}.ae_datos_a_solicitar ds1 ON ds1.id=dr1.aeds_id ";
      }
      if(tipoDoc!=null && !tipoDoc.trim().isEmpty()) {     
        query = query +  " JOIN {esquema}.ae_datos_reserva dr2 ON dr2.aers_id=res.id "
            + " JOIN {esquema}.ae_datos_a_solicitar ds2 ON ds2.id=dr2.aeds_id ";
      }
      query = query + " LEFT JOIN {esquema}.ae_atencion ate ON ate.aers_id=res.id ";
      //Si se tiene el id del recurso se filtra por ese valor, sino por el id de agenda
      if(idRecurso!=null) {
        query = query + " WHERE dis.aere_id=:idRecurso ";
      }else if(idAgenda!=null) {
        query = query + " WHERE rec.aeag_id=:idAgenda ";
      }else {
        //Nunca va a entrar acá porque idRecurso o idAgenda deben estar, pero por las dudas
        query = query + " WHERE 1=0 ";
      }
      query = query + "   AND res.estado=:estado";
      query = query + "   AND res.tramite_codigo=:codTramite";
      query = query + "   AND ds1.nombre='NroDocumento' AND dr1.valor=:numDoc ";
      query = query + "   AND ds2.nombre='TipoDocumento' AND dr2.valor=:tipoDoc ";
      query = query + "   AND DATE_TRUNC('day',dis.fecha)>=DATE_TRUNC('day',now()) ";
      query = query + " ORDER BY res.id";
      query = query.replace("{esquema}", esquema);
      Query query1 = globalEntityManager.createNativeQuery(query);
      if(idRecurso!=null) {
        query1.setParameter("idRecurso", idRecurso);
      }else if(idAgenda!=null) {
        query1.setParameter("idAgenda", idAgenda);
      }
      query1.setParameter("estado", Estado.R.name());
      query1.setParameter("codTramite", codTramite.trim());
      query1.setParameter("numDoc", numDoc);
      query1.setParameter("tipoDoc", tipoDoc);
      @SuppressWarnings("unchecked")
      List<Object[]> ress = query1.getResultList();
      List<Map<String, Object>> resp = new ArrayList<Map<String, Object>>();
      Integer reservaIdActual = null;
      Map<String, Object> reserva = null;
      Map<String,String> datos = null;
      for(Object[] res : ress) {
        Integer reservaId = (Integer)res[0];
        if(reservaIdActual==null || !reservaIdActual.equals(reservaId)) {
          reservaIdActual = reservaId;
          reserva = new HashMap<String, Object>();
          reserva.put("reservaId", reservaId);
          reserva.put("estado", res[1]);
          reserva.put("serie", res[2]);
          reserva.put("numero", res[3]);
          reserva.put("codigoCancelacion", res[4]);
          reserva.put("codigoTrazabilidad", res[5]);
          reserva.put("codigoTramite", res[6]);
          reserva.put("nombreTramite", res[7]);
          reserva.put("fechaHora", res[8]);
          reserva.put("presencial", res[9]);
          reserva.put("recurso", res[10]);
          reserva.put("agenda", res[11]);
          if(res[12]!=null) {
            reserva.put("funcionario", res[12]);
            reserva.put("asistio", res[13]);
          }
          datos = new HashMap<String,String>();
          reserva.put("datos", datos);
          resp.add(reserva);
        }
        datos.put((String)res[14], (String)res[15]);
      }
      return resp;
    }catch(NoResultException nrEx) {
      throw new UserException("no_se_encuentra_el_token_especificado");   
    }catch(NonUniqueResultException nurEx) {
      throw new UserException("no_se_encuentra_el_token_especificado");   
    }
  }
  
  
  /**
   * Permite obtener los recursos para una agenda, según los requerimientos de la operación "recursos_por_agenda" del servicio web REST
   */
  public Map<String, Object> consultarRecursosPorAgenda(Integer idEmpresa, Integer idAgenda, String idioma) throws UserException {
    if(idEmpresa==null) {
      throw new UserException("debe_especificar_la_empresa");
    }
    if(idAgenda==null) {
      throw new UserException("debe_especificar_la_agenda");
    }
    //Obtener la agenda
    Agenda agenda;
    try {
      agenda = agendarReservasEJB.consultarAgendaPorId(idAgenda);
      if(agenda==null) {
        throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
    }catch(ApplicationException | BusinessException ex) {
      throw new UserException("no_se_encuentra_la_agenda_especificada");
    }
    try {
      //Respuesta
      Map<String, Object> resp = new HashMap<String, Object>();
      //Textos del paso 1 de la agenda
      if(idioma!=null && !idioma.isEmpty() && agenda.getTextosAgenda().containsKey(idioma)) {
        resp.put("textoRecurso", agenda.getTextosAgenda().get(idioma).getTextoSelecRecurso());
        resp.put("textoPaso1", agenda.getTextosAgenda().get(idioma).getTextoPaso1());
      }else {
        if(!agenda.getTextosAgenda().isEmpty()) {
          resp.put("textoRecurso", agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoSelecRecurso());
          resp.put("textoPaso1", agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoPaso1());
        }
      }
      //Obtener los recursos de la agenda
      List<Recurso> recursos = agendarReservasEJB.consultarRecursos(agenda);
      //Extraer de los recursos solo los datos requeridos
      List<Map<String, Object>> recs = new ArrayList<Map<String, Object>>();
      for (Recurso recurso : recursos) {
        if (recurso.getVisibleInternet()) {
          Map<String, Object> rec = new HashMap<String, Object>();
          rec.put("id", recurso.getId());
          rec.put("nombre", recurso.getNombre());
          rec.put("direccion", recurso.getDireccion());
          rec.put("telefono", recurso.getTelefonos());
          rec.put("latitud", recurso.getLatitud()!=null?recurso.getLatitud().toString():"");
          rec.put("longitud", recurso.getLongitud().toString()!=null?recurso.getLongitud().toString():"");
          rec.put("multiple", recurso.getMultipleAdmite()!=null && recurso.getMultipleAdmite()?"1":"0");
          rec.put("cambios", recurso.getCambiosAdmite()!=null && recurso.getCambiosAdmite()?"1":"0");
          if(recurso.getCambiosAdmite()!=null && recurso.getCambiosAdmite() && recurso.getCambiosTiempo()!=null && recurso.getCambiosUnidad()!=null) {
            String cambiosLimite = recurso.getCambiosTiempo().toString();
            switch(recurso.getCambiosUnidad())  {
              case Calendar.DATE:
                cambiosLimite = cambiosLimite + " dias";
                break;
              case Calendar.HOUR:
                cambiosLimite = cambiosLimite + " horas";
                break;
              case Calendar.MINUTE:
                cambiosLimite = cambiosLimite + " minutos";
                break;
            }
            rec.put("cambios_limite", cambiosLimite);
          }
          
          
          recs.add(rec);
        }
      }
      resp.put("recursos", recs);
      return resp;
    }catch(Exception nrEx) {
      throw new UserException("error_no_solucionable");   
    }
  }

  /**
   * Permite consultar las disponibilidades para un recurso, según los requerimientos de la operación "disponibilidades_por_recurso" del servicio web REST
   */
  public Map<String, Object> consultarDisponibilidadesPorRecurso(Integer idEmpresa, Integer idAgenda, Integer idRecurso, String idioma) throws UserException {
    if(idEmpresa==null) {
      throw new UserException("debe_especificar_la_empresa");
    }
    if(idAgenda==null) {
      throw new UserException("debe_especificar_la_agenda");
    }
    if(idRecurso==null) {
      throw new UserException("debe_especificar_el_recurso");
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
      agenda = agendarReservasEJB.consultarAgendaPorId(idAgenda);
      if(agenda==null) {
        throw new UserException("no_se_encuentra_la_agenda_especificada");
      }
    }catch(ApplicationException | BusinessException ex) {
      throw new UserException("no_se_encuentra_la_agenda_especificada");
    }
    Recurso recurso;
    try {
      recurso = agendarReservasEJB.consultarRecursoPorId(agenda, idRecurso);
      if(recurso==null) {
        throw new UserException("no_se_encuentra_lel_recurso_especificado");
      }
    }catch(ApplicationException | BusinessException ex) {
      throw new UserException("no_se_encuentra_lel_recurso_especificado");
    }
    try {
      //Respuesta
      Map<String, Object> resp = new HashMap<String, Object>();
      //Textos del paso 2 de la agenda y el recurso (según idioma)
      if(idioma!=null && !idioma.isEmpty() && agenda.getTextosAgenda().containsKey(idioma)) {
        resp.put("textoAgendaPaso2", agenda.getTextosAgenda().get(idioma).getTextoPaso2());
      }else {
        if(!agenda.getTextosAgenda().isEmpty()) {
          resp.put("textoAgendaPaso2", agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoPaso2());
        }
      }
      if(idioma!=null && !idioma.isEmpty() && recurso.getTextosRecurso().containsKey(idioma)) {
        resp.put("textoRecursoPaso2", recurso.getTextosRecurso().get(idioma).getTextoPaso2());
      }else {
        if(!recurso.getTextosRecurso().isEmpty()) {
          resp.put("textoRecursoPaso2", recurso.getTextosRecurso().values().toArray(new TextoRecurso[0])[0].getTextoPaso2());
        }
      }
      //Configurar las ventanas
      VentanaDeTiempo ventanaCalendario = agendarReservasEJB.obtenerVentanaCalendarioInternet(recurso);
      //Determinar el timezone según la agenda o la empresa
      TimeZone timezone = TimeZone.getDefault();
      if(agenda.getTimezone()!=null && !agenda.getTimezone().isEmpty()) {
        timezone = TimeZone.getTimeZone(agenda.getTimezone());
      }else {
        if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
          timezone = TimeZone.getTimeZone(empresa.getTimezone());
        }
      }
      //Obtener las disponibilidades para la ventana
      List<Disponibilidad> disps = agendarReservasEJB.obtenerDisponibilidades(recurso, ventanaCalendario, timezone);
      SimpleDateFormat formatoClave = new SimpleDateFormat("yyyyMMdd HH:mm");
      //La clave está compuesta por la tupla [fecha hora id-disponibilidad] y el valor es la cantidad de cupos disponibles
      Map<String, Integer> cuposPorFechaHora = new HashMap<String, Integer>();
      for (Disponibilidad disp : disps) {
        cuposPorFechaHora.put(formatoClave.format(disp.getHoraInicio())+" "+disp.getId(), disp.getCupo());
      }
      //Retornar la respuesta
      resp.put("disponibilidades", cuposPorFechaHora);
      return resp;
    }catch(Exception ex) {
      ex.printStackTrace();
      throw new UserException("error_no_solucionable"); 
    }
  }

  /**
   * Valida que el token y el id de la empresa existan y se correspondan
   */
  public boolean validarTokenEmpresa(String token, Integer idEmpresa) {
    try {
      //Validar el token
      String query = "SELECT t FROM Token t WHERE t.token=:token AND t.empresa.id=:idEmpresa";
      globalEntityManager.createQuery(query).setParameter("token", token).setParameter("idEmpresa", idEmpresa).getSingleResult();
      return true;
    }catch(NoResultException nrEx) {
      return false;   
    }
  }
  
  public Map<String, Object> consultarDatosEmpresa(Integer idEmpresa) {
    try {
      Empresa empresa = globalEntityManager.find(Empresa.class, idEmpresa);
      if(empresa == null) {
        return null;
      }
      Map<String, Object> datos = new HashMap<>();
      datos.put("FORMATO_FECHA", empresa.getFormatoFecha()!=null?empresa.getFormatoFecha():"dd/MM/yyyy");
      datos.put("FORMATO_HORA", empresa.getFormatoHora()!=null?empresa.getFormatoHora():"HH:mm");
      return datos;
    }catch(Exception ex) {
      return null;   
    }
  }
  
  public String consultarConfiguracion(String clave) {
    Configuracion conf = globalEntityManager.find(Configuracion.class, clave);
    return (conf==null?null:conf.getValor());
  }
  
  /**
   * Devuelve la lista de reservas canceladas según los parámetros indicados.
   * No toma en cuenta el campo fechaLiberacion
   */
  public List<ReservaDTO> consultarReservasCanceladas(Recurso recurso, String codigoTramite, Date reservaFechaDesde, Date reservaFechaHasta, 
      Date creacionFechaDesde, Date creacionFechaHasta, Date cancelacionFechaDesde, Date cancelacionFechaHasta) {
    
    String consulta = "SELECT r.id, r.serie, r.numero, r.origen, r.ipOrigen, d.fecha, d.horaInicio," +
        " r.tramiteCodigo, r.tramiteNombre, r.fechaCreacion, r.ucrea, r.fcancela, r.ucancela, r.fechaLiberacion " +
        " FROM Reserva r " +
        " JOIN r.disponibilidades d " +
        " WHERE r.estado = :cancelada ";
    if(recurso!=null ) {
      consulta = consulta + " AND d.recurso = :recurso";
    }
    if(codigoTramite!=null && !codigoTramite.trim().isEmpty()) {
      consulta = consulta + " AND r.tramiteCodigo = :tramiteCodigo";
    }
    if(reservaFechaDesde!=null) {
      consulta = consulta + " AND d.fecha >= :reservaFechaDesde";
    }
    if(reservaFechaHasta!=null) {
      consulta = consulta + " AND d.fecha <= :reservaFechaHasta";
    }
    if(creacionFechaDesde!=null) {
      consulta = consulta + " AND d.fechaCreacion >= :creacionFechaDesde";
    }
    if(creacionFechaHasta!=null) {
      consulta = consulta + " AND d.fechaCreacion <= :creacionFechaHasta";
    }
    if(cancelacionFechaDesde!=null) {
      consulta = consulta + " AND d.fcancela >= :cancelacionFechaDesde";
    }
    if(cancelacionFechaHasta!=null) {
      consulta = consulta + " AND d.fcancela <= :cancelacionFechaHasta";
    }
    consulta = consulta + " ORDER BY r.id DESC";
    Query query = entityManager.createQuery(consulta).setParameter("cancelada", Estado.C);
    if(recurso!=null ) {
      query.setParameter("recurso", recurso);
    }
    if(codigoTramite!=null && !codigoTramite.trim().isEmpty()) {
      query.setParameter("tramiteCodigo", codigoTramite);
    }
    if(reservaFechaDesde!=null) {
      query.setParameter("reservaFechaDesde", reservaFechaDesde, TemporalType.DATE);
    }
    if(reservaFechaHasta!=null) {
      query.setParameter("reservaFechaHasta", reservaFechaHasta, TemporalType.DATE);
    }

    if(creacionFechaDesde!=null) {
      query.setParameter("creacionFechaDesde", creacionFechaDesde, TemporalType.DATE);
    }
    if(creacionFechaHasta!=null) {
      query.setParameter("creacionFechaHasta", creacionFechaHasta, TemporalType.DATE);
    }
    if(cancelacionFechaDesde!=null) {
      query.setParameter("cancelacionFechaDesde", cancelacionFechaDesde, TemporalType.DATE);
    }
    if(cancelacionFechaHasta!=null) {
      query.setParameter("cancelacionFechaHasta", cancelacionFechaHasta, TemporalType.DATE);
    }
    @SuppressWarnings("unchecked")
    List<Object[]> resultados = query.getResultList();
    List<ReservaDTO> reservas = new ArrayList<>();
    ReservaDTO reservaDTO = null;
    Iterator<Object[]> iterator = resultados.iterator();
    while (iterator.hasNext()) {
      Object[] rowReserva = iterator.next();
      Integer reservaId        = (Integer)rowReserva[0];
      String reservaSerie    = (String)rowReserva[1];
      Integer reservaNumero    = (Integer)rowReserva[2];
      String reservaOrigen    = (String) rowReserva[3];
      String ipOrigen    = (String) rowReserva[4];
      Date dispFecha        = (Date) rowReserva[5];
      Date dispHoraInicio   = (Date) rowReserva[6];
      String tramiteCodigo    = (String) rowReserva[7];
      String tramiteNombre    = (String) rowReserva[8];
      Date fcrea           = (Date) rowReserva[9];
      String ucrea         = (String) rowReserva[10];
      Date fcancela           = (Date) rowReserva[11];
      String ucancela         = (String) rowReserva[12];
      Date flibera           = (Date) rowReserva[13];
      
      reservaDTO = new ReservaDTO();
      reservaDTO.setId(reservaId);
      reservaDTO.setSerie(reservaSerie);
      reservaDTO.setNumero(reservaNumero);
      reservaDTO.setOrigen(reservaOrigen);
      reservaDTO.setIpOrigen(ipOrigen);
      reservaDTO.setFecha(dispFecha);
      reservaDTO.setHoraInicio(dispHoraInicio);
      reservaDTO.setTramiteCodigo(tramiteCodigo);
      reservaDTO.setTramiteNombre(tramiteNombre);
      reservaDTO.setUcrea(ucrea);
      reservaDTO.setFcrea(fcrea);
      reservaDTO.setUcancela(ucancela);
      reservaDTO.setFcancela(fcancela);
      reservaDTO.setFlibera(flibera);
      
      reservas.add(reservaDTO);
    }
    
    return reservas;
  }
}
