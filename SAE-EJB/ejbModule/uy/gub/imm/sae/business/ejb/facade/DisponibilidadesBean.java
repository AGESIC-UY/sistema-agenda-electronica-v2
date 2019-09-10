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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.utilidades.SimpleCalendario;
import uy.gub.imm.sae.common.DisponibilidadReserva;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR"})
public class DisponibilidadesBean implements DisponibilidadesLocal, DisponibilidadesRemote {
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;

	@EJB
	private DisponibilidadesSingleton dispSingleton;
	
	static Logger logger = Logger.getLogger(DisponibilidadesBean.class);
	
	/**
	 * Elmina las disponibilidades del recurso indicado que estén dentro de la ventana especificada
	 * No toma en cuenta las disponibilidades presenciales
	 */
	@SuppressWarnings("unchecked")
	public int eliminarDisponibilidades(Recurso recurso, VentanaDeTiempo ventana) throws UserException {
		
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		
		//La fecha final no puede ser nula
		if (ventana.getFechaFinal() == null){
			throw new UserException("la_fecha_de_fin_es_obligatoria");			
		}
		
		entityManager.lock(recurso,LockModeType.WRITE);
		entityManager.flush();

		//Determinar la cantidad de reservas vivas para el periodo indicado
		//(si hay reservas no se pueden eliminar las disponibilidades)
		//No considerar las disponibilidades presenciales
		Long cantReservasVivas =  (Long) entityManager.createQuery(
  		"SELECT COUNT(d.id) " +
  		"FROM Disponibilidad d " + 
  		"JOIN d.reservas reserva " +
  		"WHERE d.recurso = :r " +
      "  AND d.presencial = false " +
  		"  AND d.fechaBaja IS NULL " +
  		"  AND d.fecha BETWEEN :fi AND :ff " +
  		"  AND reserva.estado <> :cancelado" +
      "  AND reserva.estado <> :pendiente" )
  		.setParameter("r", recurso)
  		.setParameter("fi", ventana.getFechaInicial(), TemporalType.DATE)
  		.setParameter("ff", ventana.getFechaFinal(), TemporalType.DATE)
  		.setParameter("cancelado", Estado.C)
      .setParameter("pendiente", Estado.P)
  		.getSingleResult();
		
		if ( cantReservasVivas > 0){
			throw new UserException("no_se_puede_eliminar_las_disponibilidades_porque_hay_reservas_vivas");			
		}
		
		//Se obtienen las disponibilidades a eliminar
		//No considerar las disponibilidades presenciales
		List<Disponibilidad> disponibilidades =  entityManager.createQuery(
  		"SELECT d " +
  		"FROM Disponibilidad d " +
  		"WHERE d.recurso = :r " +
      "  AND d.presencial = false " +
  		"  AND d.fechaBaja IS NULL " +
  		"  AND d.fecha BETWEEN :fi AND :ff " +
  		"ORDER BY d.fecha ASC, d.horaInicio")
  		.setParameter("r", recurso)
  		.setParameter("fi", ventana.getFechaInicial(), TemporalType.DATE)
  		.setParameter("ff", ventana.getFechaFinal(), TemporalType.DATE)
  		.getResultList();				
		
		Date ahora = new Date();
		for (Disponibilidad d : disponibilidades) {
			d.setFechaBaja(ahora);
		}

		return disponibilidades.size();
	}

	/**
	 * Genera disponibilidades en una fecha para un recurso.
	 * Controla: 
	 * 1) Que no exista una disponibilidad viva para la misma fecha y la misma hora.
	 * 2) Solo se generen disponibilidades para días que se encuentren marcados como hábiles en sp_dias.
	 * 3) Si ya hay disponibilidades para alguna hora se registra y se devuelve.
	 * @return true si hubo conflicto en alguna hora, false en otro caso
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public List<Date> generarDisponibilidadesNuevas(Recurso r, Date fecha, Date horaDesde, Date horaHasta, Integer frecuencia, Integer cupo) throws UserException {
		
		Recurso rManaged = entityManager.find(Recurso.class, r.getId());
		if (rManaged == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		
		entityManager.lock(rManaged,LockModeType.WRITE);
		entityManager.flush();
		
		if (fecha == null){
			throw new UserException("la_fecha_es_obligatoria");
		}

		if (horaDesde == null){
			throw new UserException("la_hora_de_inicio_es_obligatoria");
		}

		if (horaHasta == null){
			throw new UserException("la_hora_de_fin_es_obligatoria");
		}

		if ( horaDesde.compareTo(horaHasta) >= 0){
			throw new UserException("la_hora_de_fin_debe_ser_posterior_a_la_hora_de_inicio");
		}
		
		if (frecuencia == null || frecuencia.intValue() <= 0){
			throw new UserException("la_frecuencia_debe_ser_mayor_que_cero");			
		}
		
		if (cupo == null || cupo.intValue() <= 0){
			throw new UserException("el_cupo_total_debe_ser_mayor_a_cero");
		}
		
		if (fecha.before(rManaged.getFechaInicioDisp())){
			throw new UserException("la_fecha_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso");
		}
		
		if (rManaged.getFechaFinDisp() != null){
			if (fecha.after(rManaged.getFechaFinDisp())){
				throw new UserException("la_fecha_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso");
			}
		}

		if (!esDiaHabil(fecha, r)){
			throw new UserException("la_fecha_no_corresponde_a_un_dia_habil");			
		} 

		Calendar calHoraInicio = new GregorianCalendar();
		Calendar calHoraFin = new GregorianCalendar();
		
		calHoraInicio.setTime(horaDesde);
		calHoraInicio.set(Calendar.MILLISECOND, 0);
		
		calHoraFin.setTime(horaDesde);
		
		//Se debe registrar las horas para las cuales ya había disponibilidades
		List<Date> horasConflicto = new ArrayList<Date>();
		
		//Para cada intervalo determinar si existe una disponibilidad y si no existe crearla
		while (calHoraInicio.getTime().before(horaHasta)) {
			calHoraFin.add(Calendar.MINUTE, frecuencia);
			if (!existeDisponibilidadEnHoraInicio(rManaged, calHoraInicio.getTime())){
				//Crear la disponibilidad 
				generarNuevaDisponibilidad(rManaged, fecha, calHoraInicio.getTime(),calHoraFin.getTime(), cupo);
			}else {
			  //Registra la hora de conflicto
			  horasConflicto.add(calHoraInicio.getTime());
			}
			//Actualizar al siguiente intervalo
			calHoraInicio.add(Calendar.MINUTE, frecuencia);
		}
		
		return horasConflicto;
	}
	

	/**
	 * Genera disponibilidades en una ventana de tiempo para un recurso. Toma como modelo 
	 * las disponibilidades generadas para una fecha.
	 * Controla: 
	 * 1) Los días a generar se encuentren entre fechaInicioDisp y fechaFinDisp del recurso.
	 * 2) La cantidad de días a generar no puede superar cantDiasAGenerar del recurso.
	 * 3) Solo se generen disponibilidades para días que se encuentren marcados
	 *    como hábiles en sp_dias.
	 * El parámetro dias debe ser un array de 6 lugares indicando para cada día si aplica o no (0=lunes,1=martes,2...,5=sábado)
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("unchecked")
	public void generarDisponibilidades(Recurso recurso, Date fechaModelo, VentanaDeTiempo v, Boolean[] dias) throws UserException {

	  recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		
		entityManager.lock(recurso, LockModeType.WRITE);
		entityManager.flush();
		
		fechaModelo = Utiles.time2InicioDelDia(fechaModelo);
		v.setFechaInicial(Utiles.time2InicioDelDia(v.getFechaInicial()));
		v.setFechaFinal(Utiles.time2FinDelDia(v.getFechaFinal()));

    //Se controla fecha inicial con fechaInicioDisp
		if (v.getFechaInicial().compareTo(recurso.getFechaInicioDisp()) < 0){
			throw new UserException("la_fecha_de_inicio_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso");
		}
		
		//La fecha final no puede ser nula
		if (v.getFechaFinal() == null){
			throw new UserException("la_fecha_de_fin_es_obligatoria");			
		}
		
		//La fecha final tiene que ser <= a fechaFinDisp o fechaFinDisp es nula
		if (recurso.getFechaFinDisp() != null){
			if (v.getFechaFinal().compareTo(recurso.getFechaFinDisp()) > 0){
				throw new UserException("la_fecha_de_fin_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso");				
			}
		}

		//La cantidad de dias a generar debe ser menor o igual a cantDiasAGenerar del Recurso. 
		Calendar calendario = new GregorianCalendar();
		int cantDias = 0;
		calendario.setTime(v.getFechaInicial());
		while (!calendario.getTime().after(v.getFechaFinal())) {
			cantDias = cantDias + 1;
			calendario.add(Calendar.DATE, 1);
		}
		
		if (cantDias > recurso.getCantDiasAGenerar()){
			throw new UserException("la_cantidad_de_dias_comprendidos_en_el_periodo_debe_ser_menor_que_la_cantidad_de_dias_a_generar_para");
		}

    //La fecha inicial debe ser menor o igual a la fecha final
		if (v.getFechaInicial().compareTo(v.getFechaFinal()) > 0){
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}

		//Se obtienen las disponibilidades generardas para la fecha a tomar como modelo
		//No se debe considerar las disponibilidades presenciales
		List<Disponibilidad> disponibilidades =  entityManager
		.createQuery(
			"SELECT d " +
			"FROM  Disponibilidad d " +
			"WHERE d.recurso IS NOT NULL " +
			"  AND d.recurso = :r " +
      "  AND d.presencial = false " +
			"  AND d.fechaBaja IS NULL " +
			"  AND d.fecha = :f " +
			"ORDER BY d.horaInicio ")
		.setParameter("r", recurso)
		.setParameter("f", fechaModelo, TemporalType.DATE)
		.getResultList();		
		
		//Si la lista obtenida es vacía no se puede continuar
		if (disponibilidades == null || disponibilidades.size() == 0){
			throw new UserException("no_existen_disponibilidades_generadas_para_la_fecha_especificada");			
		}

		Calendar cal = new GregorianCalendar();

		//Se inicializa en el primer día a generar.
		cal.setTime(v.getFechaInicial());
		while(!cal.getTime().after(v.getFechaFinal())) {
			if (esDiaHabil(cal.getTime(), recurso) && considerarDia(cal, dias))	{
				//Se controla que no existan disponibilidades (no presenciales) generadas para el día
				List<Disponibilidad> dispCtrl =  entityManager.createQuery(
					"SELECT d " +
					"FROM Disponibilidad d " +
					"WHERE d.recurso IS NOT NULL " +
					"  AND d.recurso = :r " +
		      "  AND d.presencial = false " +
					"  AND d.fechaBaja IS NULL " +
					"  AND d.fecha BETWEEN :fi AND :ff " +
					"ORDER BY d.horaInicio ")
					.setParameter("r", recurso)
					.setParameter("fi", cal.getTime(), TemporalType.DATE)
					.setParameter("ff", cal.getTime(), TemporalType.DATE)		
					.getResultList();		
				if(dispCtrl.isEmpty()){
					//Se recorren las disponibilidades para la fecha ingresada como modelo.
					for (Disponibilidad d : disponibilidades) {
						generarNuevaDisponibilidad(recurso, cal.getTime(),	d.getHoraInicio(), d.getHoraFin(), d.getCupo());
					}
				}
			}
			cal.add(Calendar.DATE, 1);
		}
	}
	
	/**
	 * Genera una nueva disponibilidad.
	 * Se debe tener en cuenta que hora inicio y hora fin tienen también la fecha.
	 * Para que la agenda funcione correctamente la fecha para ambos casos deberá ser igual que la fecha de la disponibilidad.
	 */
	private void generarNuevaDisponibilidad(Recurso recurso, Date fecha, Date horaInicio, Date horaFin, Integer cupo){
		Disponibilidad nuevaDisp = new Disponibilidad();

		nuevaDisp.setFecha(Utiles.time2InicioDelDia(fecha));
		nuevaDisp.setCupo(cupo);
		nuevaDisp.setRecurso(recurso);
		
		Calendar cal = Calendar.getInstance();
		Calendar calAux = Calendar.getInstance();
		cal.setTime(fecha);
		
		//disp.horaInicio tendrá la misma fecha que disp.fecha
		calAux.setTime(horaInicio);
		cal.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, calAux.get(Calendar.SECOND));
		nuevaDisp.setHoraInicio(cal.getTime());

		//disp.horaFin tendrá la misma fecha que disp.fecha
		calAux.setTime(horaFin);
		cal.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, calAux.get(Calendar.SECOND));
		nuevaDisp.setHoraFin(cal.getTime());
		
		entityManager.persist(nuevaDisp);
	}
	
	/**
	 * Obtiene información sobre las disponibilidades y las reservas para cada día de la ventana especificada para el recurso indicado.
	 * No considera las disponibilidades presenciales.
	 * Para las reservas canceladas toma en cuenta la fechaLiberacion
	 */
	@SuppressWarnings("unchecked")
	public List<DisponibilidadReserva> obtenerDisponibilidadesReservas(Recurso recurso, VentanaDeTiempo ventana) throws UserException, RolException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
    if (ventana == null) {
      throw new UserException("debe_especificar_la_ventana");
    }
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}		
		if (ventana.getFechaInicial().before(recurso.getFechaInicioDisp())) {
			ventana.setFechaInicial(recurso.getFechaInicioDisp());
		}
		//Determinar las disponibilidades para el período
		//No considerar las disponibilidades presenciales
    List<Disponibilidad> disponibilidades =  entityManager.createQuery(
    "SELECT d " +
    "FROM Disponibilidad d " +
    "WHERE d.recurso IS NOT NULL " +
    "  AND d.recurso = :recurso " +
    "  AND d.presencial = false " +
    "  AND d.fechaBaja IS NULL " +
    "  AND d.fecha BETWEEN :finicio AND :ffin " +
    "ORDER BY d.fecha ASC, d.horaInicio ")
    .setParameter("recurso", recurso)
    .setParameter("finicio", ventana.getFechaInicial(), TemporalType.DATE)
    .setParameter("ffin", ventana.getFechaFinal(), TemporalType.DATE)
    .getResultList();   
    //Determinar las reservas vivas para el período
    //No considerar las disponibilidades presenciales
    //Si la reserva está cancelada hay que tomar en cuenta el campo fechaLiberacion
		List<Object[]> cantReservasVivas =  entityManager.createQuery(
  		"SELECT d.id, d.fecha, d.horaInicio, COUNT(r) " +
  		"FROM Disponibilidad d " + 
  		"JOIN d.reservas r " +
  		"WHERE d.recurso IS NOT NULL " +
  		"  AND d.recurso = :recurso " +
      "  AND d.presencial = false " +
  		"  AND d.fechaBaja IS NULL " +
  		"  AND d.fecha BETWEEN :finicio AND :ffin " +
  		"  AND (r.estado <> :cancelado OR r.fechaLiberacion>=:ahora) " +
  		"GROUP BY d.id, d.fecha, d.horaInicio " +
  		"ORDER BY d.fecha ASC, d.horaInicio ASC ")
		.setParameter("recurso", recurso)
		.setParameter("finicio", ventana.getFechaInicial(), TemporalType.DATE)
		.setParameter("ffin", ventana.getFechaFinal(), TemporalType.DATE)
		.setParameter("cancelado", Estado.C)
    .setParameter("ahora", new Date())
		.getResultList();		
		//Calcular las disponibilidades para cada día
		List<DisponibilidadReserva> listadreserva = new ArrayList<DisponibilidadReserva>();
		Iterator<Object[]> cantReservasVivasIter = cantReservasVivas.iterator();
		Object row [] = null;
		if (cantReservasVivasIter.hasNext()) {
			row = cantReservasVivasIter.next();
		}
		for (Disponibilidad d : disponibilidades) {
			int cant = 0;
			if (row != null && row[0].equals(d.getId())) {
				cant = ((Long)row[3]).intValue();
				if (cantReservasVivasIter.hasNext()) {
					row = cantReservasVivasIter.next();
				}
			}
			DisponibilidadReserva dreserva = new DisponibilidadReserva();
			dreserva.setId(d.getId());
			dreserva.setFecha(d.getFecha());
			dreserva.setHoraInicio(d.getHoraInicio());
			dreserva.setHoraFin(d.getHoraFin());
			dreserva.setRecurso(null);
			dreserva.setCupo(d.getCupo());
			dreserva.setCupoDisponible(d.getCupo() - cant);
			dreserva.setCantReservas(cant);
			listadreserva.add(dreserva);			
		}		
		return listadreserva;
	}

	/**
	 * Devuelve true si pudo hacer la modificación solicitada, o false en otro caso (por ejemplo, si 
	 * el nuevo cupo es menor que la cantidad de reservas ya hechas, se pone como cupo este último valor y no
	 * el solicitado)
	 */
	public int modificarCupoDeDisponibilidad(Disponibilidad d) throws UserException  {
		Disponibilidad dispActual = (Disponibilidad) entityManager.find(Disponibilidad.class, d.getId());
		if (dispActual == null) {
			throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
		}
		if (d.getCupo()== null){
			throw new UserException("el_cupo_total_es_obligatorio");
		}
		int cantReservas = cantReservas(dispActual);
		Integer nuevoCupo = null;
		if (cantReservas > d.getCupo()){
			nuevoCupo = cantReservas;
		}else {
			nuevoCupo = d.getCupo();
		}
		dispActual.setCupo(nuevoCupo);
		return nuevoCupo;
	}

	
	/**
	 * Determina la cantidad de reservas existentes para la disponibilidad indicada
	 * La disponibilidad puede o no ser presencial
	 * Esta operación NO toma en cuenta la fecha de liberación
	 * @param disponibilidad
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int cantReservas(Disponibilidad disponibilidad){
		//Se obtiene lista de Reservas
		List<Object[]> listaReservas = (List<Object[]>) entityManager.createQuery(
  		"SELECT d.fecha, COUNT(r) " +
  		"FROM Disponibilidad d " +
  		"LEFT JOIN d.reservas r " +
  		"WHERE d = :disp AND (r IS NULL OR r.estado <> :cancelado) " +
  		"GROUP BY d.fecha " +
  		"ORDER BY d.fecha ASC")
		.setParameter("disp", disponibilidad)
		.setParameter("cancelado", Estado.C)
		.getResultList();
		int cuposDisp = 0;
		Iterator<Object[]> iReservas = listaReservas.iterator();
		while ( iReservas.hasNext()){
			Object[] reserva = iReservas.next();
			cuposDisp = cuposDisp + ((Long)reserva[1]).intValue(); 
		}
		return cuposDisp;
	}

  /**
   * Modifica el cupo de la fecha actual
   * No considera las disponibilidades presenciales
   */
	@SuppressWarnings("unchecked")
	public void modificarCupoPeriodo(Disponibilidad disponibilidad) throws UserException  {

    if (disponibilidad.getCupo()== null){
      throw new UserException("el_cupo_total_es_obligatorio");
    }
    int cupo = disponibilidad.getCupo();
	  
    disponibilidad = (Disponibilidad) entityManager.find(Disponibilidad.class, disponibilidad.getId());

		if (disponibilidad == null) {
			throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
		}

    Date ahora = new Date();
		
		List<Disponibilidad> disponibilidades = entityManager.createQuery(
  		"SELECT d " +
  		"FROM Disponibilidad d " +
  		"WHERE d.recurso IS NOT NULL " +
  		"  AND d.recurso = :r " +
      "  AND d.presencial = false " +
  		"  AND d.fechaBaja IS NULL " +
  		"  AND d.fecha >= :fi " +
    	"  AND (d.fecha <> :hoy OR d.horaInicio >= :ahora) " +
  		"ORDER BY d.fecha ASC, d.horaInicio")
		.setParameter("r", disponibilidad.getRecurso())
		.setParameter("fi", disponibilidad.getFecha(), TemporalType.DATE)
		.setParameter("hoy", ahora, TemporalType.DATE)
		.setParameter("ahora", ahora, TemporalType.TIMESTAMP)
		.getResultList();
		Calendar hora = Calendar.getInstance();
		hora.setTime(disponibilidad.getHoraInicio());

		String horaInicio = Integer.toString(hora.get(Calendar.HOUR_OF_DAY));
		String minutosInicio = Integer.toString(hora.get(Calendar.MINUTE));

		Iterator<Disponibilidad> iDispon = disponibilidades.iterator();
		while ( iDispon.hasNext()){
			Disponibilidad disp = iDispon.next();

			Calendar horaPost = Calendar.getInstance();
			horaPost.setTime(disp.getHoraInicio());

			String horaInicioP = Integer.toString(horaPost.get(Calendar.HOUR_OF_DAY));
			String minutosInicioP = Integer.toString(horaPost.get(Calendar.MINUTE));

			if (horaInicio.equals(horaInicioP) && minutosInicio.equals(minutosInicioP) )
			{
				//Si se está disminuyendo la cantidad de cupos, se controla que la cantidad
				//de reservas con estado <> C <= que la cantidad de cupos.
				if (disp.getCupo().intValue() > cupo){
					if (cantReservas(disp) > cupo){
						Calendar diaError = Calendar.getInstance();
						diaError.setTime(disp.getFecha());
						throw new UserException("AE10083","El valor del cupo debe ser mayor o igual a la cantidad de reservas existentes ("+diaError.get(Calendar.DAY_OF_MONTH) + " / " +
								(diaError.get(Calendar.MONTH)+1) + " / " +
								diaError.get(Calendar.YEAR)+")" );				
					}
				}
				disp.setCupo(disponibilidad.getCupo());
				
			}
		}
		

	}
	
	/**
	 * Modifica el cupo de la fecha actual y de todos los días posteriores para la hora indicada por la disponibilidad.
	 * La acción aplicada es la indicada (aumentar, disminuir, establecer) y el valor el indicado.
	 * No considera las disponibilidades presenciales
	 */
	@SuppressWarnings("unchecked")
	public List<String> modificarCupoPeriodoValorOperacion(Disponibilidad disponibilidad, TimeZone timezone, int valor, int tipoOperacion, Boolean[] dias) throws UserException  {
		
	  disponibilidad = (Disponibilidad) entityManager.find(Disponibilidad.class, disponibilidad.getId());
    if (disponibilidad == null) {
      throw new UserException("no_se_encuentra_la_disponibilidad_especificada");
    }

    //Ajustar la hora según el timezone
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset((new Date()).getTime()));
		Date ahora = cal.getTime();

		List<Disponibilidad> disponibilidades =  entityManager
		.createQuery("SELECT d " +
	      "FROM Disponibilidad d " +
	      "WHERE d.recurso IS NOT NULL " +
	      "  AND d.recurso = :r " +
	      "  AND d.presencial = false " +
	      "  AND d.fechaBaja IS NULL " +
	      "  AND d.fecha >= :fi " +
	      "  AND (d.fecha <> :hoy OR d.horaInicio >= :ahora) " +
	      "ORDER BY d.fecha ASC, d.horaInicio ")
		.setParameter("r", disponibilidad.getRecurso())
		.setParameter("fi", disponibilidad.getFecha(), TemporalType.DATE)
		.setParameter("hoy", ahora, TemporalType.DATE)
		.setParameter("ahora", ahora, TemporalType.TIMESTAMP)
		.getResultList();
		
		Calendar hora = Calendar.getInstance();
		hora.setTime(disponibilidad.getHoraInicio());

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		List<String> diasHorasWarn = new ArrayList<String>();
		
		String horaInicio = Integer.toString(hora.get(Calendar.HOUR_OF_DAY));
		String minutosInicio = Integer.toString(hora.get(Calendar.MINUTE));
		Iterator<Disponibilidad> iDispon = disponibilidades.iterator();
		while ( iDispon.hasNext()){
			Disponibilidad disp = iDispon.next();
			
			//Solo aplicar el cambio si es uno de los dias marcados
			Calendar fecha = new GregorianCalendar();
			fecha.setTime(disp.getFecha());
			if(considerarDia(fecha, dias)) {
				Calendar horaPost = Calendar.getInstance();
				horaPost.setTime(disp.getHoraInicio());

				String horaInicioP = Integer.toString(horaPost.get(Calendar.HOUR_OF_DAY));
				String minutosInicioP = Integer.toString(horaPost.get(Calendar.MINUTE));
				if (horaInicio.equals(horaInicioP) && minutosInicio.equals(minutosInicioP) ) {
					int cupoDisp = disp.getCupo();
					if (tipoOperacion == 1) {
					  //Aumentar valor
						disp.setCupo(cupoDisp+valor);
					}else if (tipoOperacion == 2) {
					  //Disminuir valor
						cupoDisp = cupoDisp-valor;
						if(cupoDisp<0) {
							cupoDisp = 0;
						}
						int cantReservas = cantReservas(disp);
						if (cantReservas > cupoDisp) {
							disp.setCupo(cantReservas);
							Calendar diaError = Calendar.getInstance();
							diaError.setTime(disp.getHoraInicio());
							diasHorasWarn.add(df.format(diaError.getTime()));
						}else {
							disp.setCupo(cupoDisp);
						}
					}else {
					  //Establecer valor
						cupoDisp = valor;
						int cantReservas = cantReservas(disp);
						if (cantReservas > cupoDisp) {
							disp.setCupo(cantReservas);
							Calendar diaError = Calendar.getInstance();
							diaError.setTime(disp.getHoraInicio());
							diasHorasWarn.add(df.format(diaError.getTime()));
						}else {
							disp.setCupo(cupoDisp);
						}
					}
				}
			}
			
			
		}
		return diasHorasWarn;
	}
	
	/**
	 * Determina cuántas disponibilidades hay para la fecha indicada
	 * No toma en cuenta las disponibilidades presenciales
	 */
	@SuppressWarnings("unchecked")
	public boolean hayDisponibilidadesFecha(Recurso recurso, Date fecha) throws UserException {
		List<Disponibilidad> dispo = entityManager
		.createQuery(
				"SELECT d " +
				"FROM Disponibilidad d " +
				"WHERE d.recurso IS NOT NULL " +
				"  AND d.presencial = false " +
				"  AND d.recurso = :r " +
				"  AND d.fechaBaja IS NULL " +
				"  AND d.fecha = :fi ")
			.setParameter("r", recurso)
			.setParameter("fi", fecha)
			.getResultList();
		return !dispo.isEmpty();
	}

	/**
	 * Retorna true si existen disponibilidades vivas para un recurso, fecha y hora de inicio.
	 * No considera las disponibilidades presenciales
	 */
	@SuppressWarnings("unchecked")
	private boolean existeDisponibilidadEnHoraInicio(Recurso recurso, Date horaInicio){
		//Se obtienen las disponibilidades vivas para un recurso generadas para la fecha y hora inicio indicadas.
		//No se debe considerar las disponibilidades presenciales
		String consulta = "SELECT d " +
				"FROM  Disponibilidad d " +
				"WHERE d.recurso IS NOT NULL " +
        "  AND d.presencial = false " +
				"  AND d.recurso.id = :rid " +
				"  AND d.fechaBaja IS NULL " +
				"  AND d.fecha = :f " +
				"  AND d.horaInicio = :hi " +
				"ORDER BY d.horaInicio ";
		List<Disponibilidad> disponibilidades =  entityManager.createQuery(consulta)
		.setParameter("rid", recurso.getId())
		.setParameter("f", horaInicio, TemporalType.DATE)
		.setParameter("hi", horaInicio, TemporalType.TIMESTAMP)
		.getResultList();		
		
		//Sólo se puede continuar si la lista obtenida es vacía
		return !disponibilidades.isEmpty();
	}

	/**
	 * Devuelve true si la fecha indicada corresponde a un día habil
	 * Si sábado o domingo toma en cuenta la configuración del recurso
	 */
	public boolean esDiaHabil(Date fecha, Recurso recurso) {
		Calendario calendario = new SimpleCalendario(null);
		return calendario.esDiaHabil(fecha, recurso);
	}

	/**
	 * Determina la última fecha para la cual se generaron disponibilidades para el recurso indicado
	 */
	public Date ultFechaGenerada(Recurso recurso) throws UserException {
		Object maximo = entityManager.createQuery(
  			"SELECT MAX(d.fecha) " +
  			"FROM Disponibilidad d " +
  			"WHERE d.recurso = :r " +
        "  AND d.presencial = false " +
  			"  AND d.fechaBaja IS NULL ")
  		.setParameter("r", recurso).getSingleResult();
		Date ultFecha = (Date)maximo;
		return ultFecha;
	}
	
	/**
	 * Obtiene la disponibilidad presencial para el día actual para el recurso indicado
	 */
	public Disponibilidad obtenerDisponibilidadPresencial(Recurso recurso, TimeZone timezone) {
	  return dispSingleton.obtenerDisponibilidadPresencial(entityManager, recurso, timezone);
	}

	//=====================================================================================================
	
	/**
	 * Determina si se debe considerar la fecha indicada para aplicar algún procedimiento
	 * según el mapa de días especificado.
	 * @param cal
	 * @param dias
	 * @return
	 */
  private boolean considerarDia(Calendar cal, Boolean[] dias) {
    if(cal==null || dias==null) {
      return false;
    }
    int diaSemana = cal.get(Calendar.DAY_OF_WEEK);
    switch(diaSemana) {
      case Calendar.MONDAY:
        return dias.length>0 && dias[0]!=null && dias[0];
      case Calendar.TUESDAY:
        return dias.length>1 && dias[1]!=null && dias[1];
      case Calendar.WEDNESDAY:
        return dias.length>2 && dias[2]!=null && dias[2];
      case Calendar.THURSDAY:
        return dias.length>3 && dias[3]!=null && dias[3];
      case Calendar.FRIDAY:
        return dias.length>4 && dias[4]!=null && dias[4];
      case Calendar.SATURDAY:
        return dias.length>5 && dias[5]!=null && dias[5];
      case Calendar.SUNDAY:
        return dias.length>6 && dias[6]!=null && dias[6];
      default:
        return false;
    }
  }
  

}
