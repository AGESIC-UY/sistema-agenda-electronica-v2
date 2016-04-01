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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValidacionPorDato;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR","RA_AE_ANONIMO", "RA_AE_LLAMADOR"})
public class RecursosBean implements RecursosLocal, RecursosRemote{

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;

	/**
	 * Crea el recurso <b>r</b> asociándolo a la agenda <b>a</b>.
	 * Controla la unicidad del nombre del recurso entre todos los recursos vivos (fechaBaja == null).
	 * Se controla que la agenda se encuentre viva.
	 * Se permite setear todos sus atributos menos la fechaBaja. 
	 * Controla:
	 *	fechaInicio <> NULL
	 *  fechaInicio <= fechaFin o fechaFin == NULL
	 *	fechaInicioDisp <> NULL 	 
	 *	fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL
	 *	fechaInicio <= fechaInicioDisp
	 * 	ventanaDiasMinimos > 0
	 *  ventanaCuposMinimos > 0
	 *	cantDiasAGenerar > 0
	 *	largoListaEspera > 0
	 *  largo del campo Serie menor o igual a 3
	 * 	cantDiasAGenerar >= ventanaDiasMinimos 
	 * Retorna el recurso con su identificador interno.
	 * Controla que el usuario tenga rol Planificador sobre la agenda <b>a</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 * @throws BusinessException 
	 */
	public Recurso crearRecurso(Agenda a, Recurso r) throws UserException, ApplicationException, BusinessException {
		
		if (a == null) {
			throw new BusinessException("AEB0001");
		}
		Agenda aManejada = (Agenda) entityManager.find(Agenda.class, a.getId());
		if (aManejada == null) {
			throw new BusinessException("AE10002", "No existe la agenda: "+a.getId()+" - "+a.getNombre());
		}
		//No se puede dar de alta un recurso para una agenda cerrada
		if (aManejada.getFechaBaja() != null) {
			throw new BusinessException("AE10021","No se puede dar de alta un recurso para una agenda cerrada");			
		}
		a = aManejada;
		aManejada = null; //De aquí en mas utilizo "a".
		
		r.setAgenda(a);
		
		//Controla la unicidad del nombre del recurso entre todos los recursos vivos (fechaBaja == null)
		//para la misma agenda.
		if (existeRecursoPorNombre(r) ) {
				throw new UserException("AE10010","Ya existe el recurso de nombre "+ r.getNombre());
		}
		if (existeRecursoPorDescripcion(r) ) {
			throw new UserException("AE10010","Ya existe el recurso con descripción "+ r.getDescripcion());
		}
		
		//Se controla que el recurso no tenga fecha de baja
		if (r.getFechaBaja() != null){
			throw new BusinessException("AE20011","No se puede dar de alta un recurso con fecha de baja: "+ r.getNombre());			
		}

		//fechaInicio <> NULL
		if (r.getFechaInicio() == null){
			throw new UserException("AE10012","Fecha de Inicio no puede ser nula");			
		}
		
		//Se setea hora en fecha de inicio 00:00:00
		r.setFechaInicio(Utiles.time2InicioDelDia(r.getFechaInicio()));

		//Si la fecha de Fin no es nula, se setea la hora al final del Día.
		if (r.getFechaFin() != null){
			r.setFechaFin(Utiles.time2FinDelDia(r.getFechaFin()));
		}

		//fechaInicio <= fechaFin o fechaFin == NULL
		if ((r.getFechaFin() != null) && (r.getFechaInicio().compareTo(r.getFechaFin()) > 0 )){
			throw new UserException("AE10013","Fecha de Inicio debe ser menor o igual a Fecha Fin o Fecha Fin es nula");			
		}

		//fechaInicioDisp <> NULL
		if (r.getFechaInicioDisp() == null){
			throw new UserException("AE10014","Fecha de Inicio de Disponibilidad no puede ser nula");			
		}

		if (r.getSabadoEsHabil() == null){
			throw new UserException("-1","El atributo SabadoEsHabil no puede ser nulo");			
		}

		//Se setea hora en fecha de inicio disp. 00:00:00
		r.setFechaInicioDisp(Utiles.time2InicioDelDia(r.getFechaInicioDisp()));

		//Si la fecha de Fin de disponibilidad no es nula, se setea la hora al final del Día.
		if (r.getFechaFinDisp() != null){
			r.setFechaFinDisp(Utiles.time2FinDelDia(r.getFechaFinDisp()));	
		}
	

		//fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL
		if ((r.getFechaFinDisp() != null) && (r.getFechaInicioDisp().compareTo(r.getFechaFinDisp()) > 0 )){
			throw new UserException("AE10015","Fecha de Fin Disponibilidad debe ser menor o igual a Fecha Fin Disponibilidad o Fecha Fin Disponibilidad es nula");			
		}

		//fechaInicio <= fechaInicioDisp
		if (r.getFechaInicio().compareTo(r.getFechaInicioDisp()) > 0 ){
			throw new UserException("AE10016","Fecha de Inicio debe ser menor o igual a Fecha inicio Disponibilidad");			
		}
		
		//diasInicioVentanaIntranet >= 0
		if (r.getDiasInicioVentanaIntranet() == null){
			throw new UserException("AE10083","Debe ingresar Días Inicio Ventana de Intranet");
		}
		if (r.getDiasInicioVentanaIntranet() < 0){
			throw new UserException("AE10023","Días Inicio Ventana de Intranet debe ser mayor o igual que cero");			
		}
		
		//diasVentanaIntranet > 0
		if (r.getDiasVentanaIntranet() == null){
			throw new UserException("AE10084","Debe ingresar Días Ventana de Intranet");
		}
		if (r.getDiasVentanaIntranet() <= 0){
			throw new UserException("AE10024","Días Ventana de Intranet debe ser mayor que cero");			
		}
		
		//diasInicioVentanaInternet >= 0
		if (r.getDiasInicioVentanaInternet() == null){
			throw new UserException("AE10085","Debe ingresar Días Inicio Ventana de Internet");
		}
		if (r.getDiasInicioVentanaInternet() < 0){
			throw new UserException("AE10025","Días Inicio Ventana de Internet debe ser mayor o igual que cero");			
		}
		
		//diasVentanaInternet > 0
		if (r.getDiasVentanaInternet() == null){
			throw new UserException("AE10086","Debe ingresar Días Ventana de Internet");
		}
		if (r.getDiasVentanaInternet() <= 0){
			throw new UserException("AE10026","Días Ventana de Internet debe ser mayor que cero");			
		}

		//ventanaCuposMinimos >= 0
		if (r.getVentanaCuposMinimos() == null){
			throw new UserException("AE10079","Debe ingresar Ventana de Cupos Mínimos");
		}
		if (r.getVentanaCuposMinimos() < 0 ){
			throw new UserException("AE10018","Ventana de Cupos Mínimos debe ser mayor o igual a cero");			
		}

		//cantDiasAGenerar > 0
		if (r.getCantDiasAGenerar() == null){
			throw new UserException("AE10082","Debe ingresar Cantidad de dias a Generar");
		}
		if (r.getCantDiasAGenerar() <= 0 ){
			throw new UserException("AE10019","Cantidad de Días a generar debe ser mayor que cero");			
		}

		//cantDiasAGenerar >= diasInicioVentanaIntranet + diasVentanaIntranet
		if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaIntranet() + r.getDiasVentanaIntranet()) < 0 ){
			throw new UserException("AE10022","Cantidad de Días a generar debe ser mayor o igual que la suma de los Días Inicio Ventana en Intranet y los Días Ventana de Intranet");			
		}

		//cantDiasAGenerar >= diasInicioVentanaInternet + diasVentanaInternet
		if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaInternet() + r.getDiasVentanaInternet()) < 0 ){
			throw new UserException("AE10022","Cantidad de Días a generar debe ser mayor o igual que la suma de los Días Inicio Ventana en Internet y los Días Ventana de Internet");			
		}
		
		//largoListaEspera > 0
		if ((r.getLargoListaEspera() != null) && (r.getLargoListaEspera() <= 0 )){
			throw new UserException("AE10021","Largo Lista de Espera debe ser mayor que cero");			
		}
		
		//Se controla que la serie no tenga largo mayor a 3
		if((r.getSerie() != null)&&(r.getSerie().length() > 3)){
			throw new UserException("AE10028","El largo del campo serie no puede ser mayor a 3");
		}
		
		//se controla que el campo "usaLlamador" no sea null
		if(r.getUsarLlamador() == null){
			throw new UserException("AE10029","El campo Usar LLamador no puede ser null.");
		}
		
		entityManager.persist(r);
		
		//paso a agregar agrupacion
		AgrupacionDato agrupDato = new AgrupacionDato();
		agrupDato.setNombre("Datos Personales");
		agrupDato.setOrden(1);
		agrupDato.setBorrarFlag(false);
		agregarAgrupacionDato(r, agrupDato);
		
		//agrego datos a solicitar tipo documento
		DatoASolicitar d1 = new DatoASolicitar();
		d1.setNombre("TipoDocumento");
		d1.setRequerido(true);
		d1.setFila(1);
		d1.setColumna(1);
		d1.setIncluirEnReporte(true);		
		d1.setAgrupacionDato(agrupDato);
		d1.setAnchoDespliegue(100);
		d1.setEsClave(true);
		d1.setEtiqueta("Tipo de Documento");
		d1.setIncluirEnLlamador(true);
		d1.setLargo(20);
		d1.setLargoEnLlamador(20);
		d1.setOrdenEnLlamador(1);
		d1.setRecurso(r);
		d1.setRequerido(true);
		d1.setTipo(Tipo.LIST);
		d1.setBorrarFlag(false);
		//persisto dato a solicitar
		agregarDatoASolicitar(r, agrupDato, d1);
		//Ingreso valores posibles
		//creo valor cédula
		ValorPosible vp1 = new ValorPosible();
		vp1.setDato(d1);
		vp1.setEtiqueta("Cédula de Identidad");
		vp1.setFechaDesde(r.getFechaInicio());
		vp1.setFechaHasta(r.getFechaFin());
		vp1.setOrden(1);
		vp1.setValor("CI");
		vp1.setBorrarFlag(false);
		agregarValorPosible(d1, vp1);
		
		//creo valor pasaporte
		ValorPosible vp2 = new ValorPosible();
		vp2.setDato(d1);
		vp2.setEtiqueta("Pasaporte");
		vp2.setFechaDesde(r.getFechaInicio());
		vp2.setFechaHasta(r.getFechaFin());
		vp2.setOrden(2);
		vp2.setValor("P");
		vp2.setBorrarFlag(false);
		agregarValorPosible(d1, vp2);
	
		
		//creo valor pasaporte
		ValorPosible vp3 = new ValorPosible();
		vp3.setDato(d1);
		vp3.setEtiqueta("Otro");
		vp3.setFechaDesde(r.getFechaInicio());
		vp3.setFechaHasta(r.getFechaFin());
		vp3.setOrden(3);
		vp3.setValor("O");
		vp3.setBorrarFlag(false);
		agregarValorPosible(d1, vp3);
		
		
		//agrego datos a solicitar Nro. documento
		DatoASolicitar d2 = new DatoASolicitar();
		d2.setNombre("NroDocumento");
		d2.setRequerido(true);
		d2.setFila(2);
		d2.setColumna(1);
		d2.setIncluirEnReporte(true);		
		d2.setAgrupacionDato(agrupDato);
		d2.setAnchoDespliegue(15);
		d2.setEsClave(true);
		d2.setEtiqueta("Nro. Documento");
		d2.setIncluirEnLlamador(true);
		d2.setLargo(10);
		d2.setLargoEnLlamador(10);
		d2.setOrdenEnLlamador(2);
		d2.setRecurso(r);
		d2.setRequerido(true);
		d2.setTipo(Tipo.STRING);
		d2.setBorrarFlag(false);
		//persisto dato a solicitar
		agregarDatoASolicitar(r, agrupDato, d2);
		
		//agrego datos a solicitar Nro. documento
		DatoASolicitar d3 = new DatoASolicitar();
		d3.setNombre("Mail");
		d3.setRequerido(true);
		d3.setFila(3);
		d3.setColumna(1);
		d3.setIncluirEnReporte(true);		
		d3.setAgrupacionDato(agrupDato);
		d3.setAnchoDespliegue(100);
		d3.setEsClave(true);
		d3.setEtiqueta("Mail");
		d3.setIncluirEnLlamador(false);
		d3.setLargo(100);
		d3.setLargoEnLlamador(100);
		d3.setOrdenEnLlamador(3);
		d3.setRecurso(r);
		d3.setRequerido(true);
		d3.setTipo(Tipo.STRING);
		d3.setBorrarFlag(false);
		//persisto dato a solicitar
		agregarDatoASolicitar(r, agrupDato, d3);
		return r;

	}
	
	/**
	 * Modifica los datos del recurso <b>r</b>.
	 * Controla la unicidad del nombre del recurso entre todos los recursos vivos (fechaBaja == null).
	 * Se permite setear todos sus atributos menos la fechaBaja. 
	 * Controla:
	 *	fechaInicio <> NULL
	 *  fechaInicio <= fechaFin o fechaFin == NULL
	 *	fechaInicioDisp <> NULL 	 
	 *	fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL
	 *	fechaInicio <= fechaInicioDisp
	 * 	ventanaDiasMinimos > 0
	 *  ventanaCuposMinimos > 0
	 *	cantDiasAGenerar > 0
	 *	largoListaEspera > 0
	 *  El largo del campo Serie no puede ser mayor a 3
	 * 	cantDiasAGenerar >= ventanaDiasMinimos
	 *  Si reservaMultiple = True => se podrá cambiar su valor a reservaMultiple = FALSE 
	 *                               solo si no existe reserva viva con más de una disponibilidad para ese recurso.
     *  No pueden quedar disponibilidades vivas fuera del período fechaInicioDisp y fechaFinDisp.
     *	No pueden quedar reservas vivas fuera del período fechaInicioDisp y fechaFinDisp.  
	 * Retorna el recurso con su identificador interno.
	 * Controla que el usuario tenga rol Planificador sobre la agenda <b>a</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws BusinessException 
	 * @throws ApplicationException 
	 */	
	public void modificarRecurso(Recurso r) throws UserException, BusinessException, ApplicationException {

		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		
		//No se puede modificar un recurso con fecha de baja
		if (recursoActual.getFechaBaja() != null) {
			throw new BusinessException("AE10024","No se puede modificar un recurso con fecha de baja");
		}
		
		//Controla la unicidad del nombre del recurso entre todos los recursos vivos (fechaBaja == null)
		//para la misma agenda.
		if (existeRecursoPorNombre(r) ) {
				throw new UserException("ya_existe_un_recurso_con_el_nombre_especificado");
		}
		
		//Se controla que el recurso no tenga fecha de baja
		if (r.getFechaBaja() != null){
			throw new BusinessException("AE10023","No se puede modificar la fecha de baja"+ r.getNombre());			
		}

		//fechaInicio <> NULL
		if (r.getFechaInicio() == null){
			throw new UserException("la_fecha_de_inicio_es_obligatoria");			
		}

		//Se setea hora en fecha de inicio 00:00:00
		r.setFechaInicio(Utiles.time2InicioDelDia(r.getFechaInicio()));

		//Si la fecha de Fin de disponibilidad no es nula, se setea la hora al final del Día.
		if (r.getFechaFin() != null){
			r.setFechaFin(Utiles.time2FinDelDia(r.getFechaFin()));
		}

		//fechaInicio <= fechaFin o fechaFin == NULL
		if (r.getFechaFin() == null){
			throw new UserException("la_fecha_de_fin_es_obligatoria");			
		}
		if (r.getFechaInicio().compareTo(r.getFechaFin()) > 0 ){
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");			
		}

		//fechaInicioDisp <> NULL
		if (r.getFechaInicioDisp() == null){
			throw new UserException("la_fecha_de_inicio_es_obligatoria");			
		}

		//Se setea hora en fecha de inicio disp. 00:00:00
		r.setFechaInicioDisp(Utiles.time2InicioDelDia(r.getFechaInicioDisp()));
		//Si la fecha de Fin de disponibilidad no es nula, se setea la hora al final del Día.
		if (r.getFechaFinDisp() != null){
			r.setFechaFinDisp(Utiles.time2FinDelDia(r.getFechaFinDisp()));
		}
		
		//fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL
		if (r.getFechaFinDisp() == null){
			throw new UserException("la_fecha_de_fin_es_obligatoria");			
		}
		if (r.getFechaInicioDisp().compareTo(r.getFechaFinDisp()) > 0 ){
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");			
		}

		//fechaInicio <= fechaInicioDisp
		if (r.getFechaInicio().compareTo(r.getFechaInicioDisp()) > 0 ){
			throw new UserException("la_fecha_de_inicio_de_disponibilidad_debe_ser_posterior_a_la_fecha_de_inicio");			
		}

		//diasInicioVentanaIntranet >= 0
		if (r.getDiasInicioVentanaIntranet() == null){
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (r.getDiasInicioVentanaIntranet() < 0){
			throw new UserException("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");			
		}
		
		//diasVentanaIntranet > 0
		if (r.getDiasVentanaIntranet() == null){
			throw new UserException("los_dias_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (r.getDiasVentanaIntranet() <= 0){
			throw new UserException("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");			
		}
		
		//diasInicioVentanaInternet >= 0
		if (r.getDiasInicioVentanaInternet() == null){
			throw new UserException("los_dias_de_la_ventana_de_internet_es_obligatorio");
		}
		if (r.getDiasInicioVentanaInternet() < 0){
			throw new UserException("los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero");			
		}
		
		//diasVentanaInternet > 0
		if (r.getDiasVentanaInternet() == null){
			throw new UserException("los_dias_de_la_ventana_de_internet_es_obligatorio");
		}
		if (r.getDiasVentanaInternet() <= 0){
			throw new UserException("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");			
		}

		//ventanaCuposMinimos >= 0
		if ((r.getVentanaCuposMinimos() == null)||(r.getVentanaCuposMinimos() < 0 )){
			throw new UserException("la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero");			
		}

		//cantDiasAGenerar > 0
		if ((r.getCantDiasAGenerar() == null)||(r.getCantDiasAGenerar() <= 0 )){
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero");			
		}

		//cantDiasAGenerar >= diasInicioVentanaIntranet + diasVentanaIntranet
		if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaIntranet() + r.getDiasVentanaIntranet()) < 0 ){
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet");			
		}

		//cantDiasAGenerar >= diasInicioVentanaInternet + diasVentanaInternet
		if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaInternet() + r.getDiasVentanaInternet()) < 0 ){
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet");			
		}
		
		//largoListaEspera > 0
		if ((r.getLargoListaEspera() != null) && (r.getLargoListaEspera() <= 0 )){
			throw new UserException("el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero");
		}
		
		//  Si reservaMultiple = True => se podrá cambiar su valor a reservaMultiple = FALSE 
		//  solo si no existe reserva viva con más de una disponibilidad para ese recurso.
		if ((recursoActual.getReservaMultiple()!= r.getReservaMultiple() )
				&& (r.getReservaMultiple() == false) ){
			//no existe reserva viva con más de una disponibilidad para ese recurso.
			if (existeReservaVivaMultiple(r)){
				throw new UserException("AE10025","No se puede desactivar reservaMultiple si existen reservas multiples vivas");
			}
		}

		//No pueden quedar disponibilidades vivas fuera del período fechaInicioDisp y fechaFinDisp.
		//OJO, hay que ver bien que se quiere controlar aca!!!
		if ( hayDispVivasPorFecha(r, r.getFechaInicioDisp(), r.getFechaFinDisp()) ){
			throw new UserException("no_se_puede_modificar_las_fechas_porque_quedarian_disponibilidades_fuera_del_periodo_especificado");
		}
		
	    //No pueden quedar reservas vivas fuera del período fechaInicioDisp y fechaFinDisp.
		if ( hayReservasVivasPorFecha(r,r.getFechaInicioDisp(),r.getFechaFinDisp()) ){
			throw new UserException("no_se_puede_modificar_las_fechas_porque_quedarian_reservas_vivas_fuera_del_periodo_especificado");
		}
		
		//Se controla que la serie no tenga largo mayor a 3
		if((r.getSerie() != null)&&(r.getSerie().length() > 3)){
			throw new UserException("AE10028","El largo del campo serie no puede ser mayor a 3");
		}
	
		recursoActual.setNombre(r.getNombre());
		recursoActual.setDescripcion(r.getDescripcion());
		recursoActual.setFechaInicio(r.getFechaInicio());
		recursoActual.setFechaFin(r.getFechaFin());
		recursoActual.setFechaInicioDisp(r.getFechaInicioDisp());
		recursoActual.setFechaFinDisp(r.getFechaFinDisp());
		recursoActual.setDiasInicioVentanaIntranet(r.getDiasInicioVentanaIntranet());
		recursoActual.setDiasVentanaIntranet(r.getDiasVentanaIntranet());
		recursoActual.setDiasInicioVentanaInternet(r.getDiasInicioVentanaInternet());
		recursoActual.setDiasVentanaInternet(r.getDiasVentanaInternet());
		recursoActual.setVentanaCuposMinimos(r.getVentanaCuposMinimos());
		recursoActual.setCantDiasAGenerar(r.getCantDiasAGenerar());
		recursoActual.setLargoListaEspera(r.getLargoListaEspera());
		recursoActual.setSerie(r.getSerie());
		recursoActual.setVisibleInternet(r.getVisibleInternet());
		recursoActual.setReservaMultiple(r.getReservaMultiple());
		recursoActual.setMostrarNumeroEnLlamador(r.getMostrarNumeroEnLlamador());
		recursoActual.setMostrarNumeroEnTicket(r.getMostrarNumeroEnTicket());
		recursoActual.setSabadoEsHabil(r.getSabadoEsHabil());
		
		recursoActual.setLocalidad(r.getLocalidad());
		recursoActual.setDepartamento(r.getDepartamento());
		recursoActual.setDireccion(r.getDireccion());
		recursoActual.setTelefonos(r.getTelefonos());
		recursoActual.setHorarios(r.getHorarios());
		recursoActual.setLatitud(r.getLatitud());
		recursoActual.setLongitud(r.getLongitud());

		for(TextoRecurso viejo : recursoActual.getTextosRecurso().values()) {
  		entityManager.remove(viejo);
  	}
  	recursoActual.setTextosRecurso(new HashMap<String, TextoRecurso>());
  	if(r.getTextosRecurso() != null) {
  		for(String idioma : r.getTextosRecurso().keySet()) {
	  		TextoRecurso viejo = r.getTextosRecurso().get(idioma);
	  		TextoRecurso nuevo = new TextoRecurso();

	  		nuevo.setRecurso(viejo.getRecurso());
	  		nuevo.setIdioma(viejo.getIdioma());
	  		nuevo.setTextoPaso2(viejo.getTextoPaso2());
	  		nuevo.setTextoPaso3(viejo.getTextoPaso3());
	  		nuevo.setTituloCiudadanoEnLlamador(viejo.getTituloCiudadanoEnLlamador());
	  		nuevo.setTituloPuestoEnLlamador(viejo.getTituloPuestoEnLlamador());
	  		nuevo.setTicketEtiquetaUno(viejo.getTicketEtiquetaUno());
	  		nuevo.setTicketEtiquetaDos(viejo.getTicketEtiquetaDos());
	  		nuevo.setValorEtiquetaUno(viejo.getValorEtiquetaUno());
	  		nuevo.setValorEtiquetaDos(viejo.getValorEtiquetaDos());
	  		recursoActual.getTextosRecurso().put(idioma, nuevo);
	  		entityManager.persist(nuevo);
  		}
  	}
  	
  	entityManager.merge(recursoActual);

	}

	/**
	 * Realiza una baja lógica del recurso <b>r</b> (se setea fechaBaja con la fecha actual del sistema).
	 * Controla que no existan disponibilidades vivas (fechaBaja == null) y futuras (fecha > fecha actual del sistema).
	 * Controla que no existan reservas vivas (estado R o P).
	 * Controla que no existan agrupaciones de datos en el recurso
	 * Controla que no existan Validaciones aplicadas en el recurso
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada. 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */	
	@SuppressWarnings("unchecked")
	public void eliminarRecurso(Recurso r) throws UserException, ApplicationException {
    Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
	
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
        
		//Se controla que no existan reservas vivas para el recurso. 
		 if (hayReservasVivas(recursoActual)){
			throw new UserException("no_se_puede_eliminar_el_recurso_porque_hay_reservas_vivas");
		}
		 
		 //Se eliminan disponibilidades.
		 List<Disponibilidad> disponibilades = (List<Disponibilidad>)entityManager.createQuery("SELECT d FROM Disponibilidad d " +
					"WHERE d.recurso = :recurso " +
					"  AND d.fecha >= :fecha" +
					"  AND d.horaFin >= :hora" +
					"  AND d.fechaBaja is null")
			.setParameter("recurso", recursoActual)
			.setParameter("fecha", new Date())
			.setParameter("hora", new Date())
			.getResultList();
		 
		for (Disponibilidad disponibilidad : disponibilades) {
			disponibilidad.setFechaBaja(new Date());
			//entityManager.merge(disponibilidad);
		}
//		 if  (hayDisponibilidadesVivas(recursoActual)) {
//			throw new UserException("no_se_puede_eliminar_el_recurso_porque_hay_disponibilidades_vivas" );
//		}

		//se eliminan agrupaciones de datos
		List<AgrupacionDato> listaAgrupacion = (List<AgrupacionDato>)entityManager
		.createQuery("SELECT ad FROM AgrupacionDato ad " +
				"WHERE ad.recurso = :recurso AND ad.fechaBaja is null")
		.setParameter("recurso", recursoActual)
		.getResultList();
		
		for (AgrupacionDato agrupacionDato : listaAgrupacion) {
			List<DatoASolicitar> lisrDatoAsolicitar = agrupacionDato.getDatosASolicitar();
			
			for (DatoASolicitar datoASolicitar : lisrDatoAsolicitar) {
				datoASolicitar.setFechaBaja(new Date());
				//entityManager.merge(datoASolicitar);
			}
			agrupacionDato.setFechaBaja(new Date());
			//entityManager.merge(agrupacionDato);
		}
		
//		//Se controla que no existan AgrupacionDeDatos vivas para el recurso. 
//		 if (hayAgrupacionDatoEnRecursoVivo(recursoActual)){
//			throw new UserException("no_se_puede_eliminar_el_recurso_porque_hay_agrupaciones_de_datos_vivas");
//		 }
		
		List<ValidacionPorRecurso> listValidacionRecurso = (List<ValidacionPorRecurso>)entityManager
		.createQuery("SELECT vxr FROM ValidacionPorRecurso vxr " +
				"WHERE vxr.recurso = :recurso AND vxr.fechaBaja is null")
		.setParameter("recurso", recursoActual)
		.getResultList();
		
		for (ValidacionPorRecurso validacionPorRecurso : listValidacionRecurso) {
			validacionPorRecurso.setFechaBaja(new Date());
			//entityManager.merge(validacionPorRecurso);
		}
//		 if (hayValidacionesEnRecursoVivos(recursoActual)){
//			 throw new UserException("no_se_puede_eliminar_el_recurso_porque_hay_validaciones_vivas");
//		}
		
		recursoActual.setFechaBaja(new Date());
	}

	public Recurso consultarRecurso(Recurso r) throws UserException{
    Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		return recursoActual;
	
	};
	/**
	 * Agrega un DatoDelRecurso <b>d</b> asociándolo al recurso <b>r</b>.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso <b>r</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */
	public DatoDelRecurso  agregarDatoDelRecurso(Recurso r, DatoDelRecurso d) throws UserException {
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		//No se puede agregar un dato a un recurso con fecha de baja
		if (recursoActual.getFechaBaja() != null) {
			throw new UserException("AE10024","No se puede modificar un recurso con fecha de baja");
		}

		d.setRecurso(r);
		
		if (d.getEtiqueta() == null){
			throw new UserException("AE10034","La etiqueta de la información del recurso no puede ser nula");
		}
		
		if (d.getOrden() == null || d.getOrden().intValue() < 0){
			throw new UserException("AE10035","El orden de la información del recurso debe ser mayor o igual a cero");			
		}
		
		if (d.getValor() == null || d.getValor().compareTo("")==0){
			throw new UserException("AE10036","El valor de la información del recurso no puede ser nulo");
		}

		entityManager.persist(d);
		return d;
	}

	/**
	 * Modifica los datos del DatoDelRecurso <b>d</b>.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso asociado a <b>d</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */	
	public void modificarDatoDelRecurso(DatoDelRecurso d) throws UserException {

		DatoDelRecurso datoActual = (DatoDelRecurso) entityManager.find(DatoDelRecurso.class, d.getId());
		
		if (datoActual == null) {
			throw new UserException("AE10033","No existe la información del recurso que se quiere modificar: " + d.getId().toString());
		}
		
		//No se puede modificar un dato de un recurso con fecha de baja
		if (datoActual.getRecurso().getFechaBaja() != null) {
			throw new UserException("AE10024","No se puede modificar un recurso con fecha de baja");
		}
		
		if (d.getEtiqueta() == null){
			throw new UserException("AE10034","La etiqueta de la información del recurso no puede ser nula");
		}
		
		if (d.getOrden() == null || d.getOrden().intValue() < 0){
			throw new UserException("AE10035","El orden de la información del recurso debe ser mayor o igual a cero");			
		}
		
		if (d.getValor() == null || d.getValor().compareTo("")==0){
			throw new UserException("AE10036","El valor de la información del recurso no puede ser nulo");
		}

		
		datoActual.setEtiqueta(d.getEtiqueta());
		datoActual.setOrden(d.getOrden());
		datoActual.setValor(d.getValor());
	}
	
	/**
	 * Realiza una baja física del DatoDelRecurso <b>d</b>
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso asociado a <b>d</b>. 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */	
	public void eliminarDatoDelRecurso(DatoDelRecurso d) throws UserException {

		DatoDelRecurso datoActual = (DatoDelRecurso) entityManager.find(DatoDelRecurso.class, d.getId());
		
		if (datoActual == null) {
			throw new UserException("AE10033","No existe la información del recurso: " + d.getId().toString());
		}
		entityManager.remove(datoActual);
	}

	/**
	 * Retorna una lista de datos del recurso
	 * Controla que el usuario tenga rol Administrador/Planificador sobre la agenda <b>a</b> del recurso
	 * Roles permitidos: Administrador, Planificador
	 */
	@SuppressWarnings("unchecked")
  @RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR","RA_AE_ANONIMO","RA_AE_FCALL_CENTER","RA_AE_FATENCION"})	
	public List<DatoDelRecurso> consultarDatosDelRecurso(Recurso r)throws ApplicationException, BusinessException {
		
		if (r == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		
		r = entityManager.find(Recurso.class, r.getId());
		if (r == null) {
			throw new BusinessException("-1", "No se encuentra la agenda indicada");
		}
		
		try{
			List<DatoDelRecurso> datosDelRecurso = (List<DatoDelRecurso>) entityManager
									.createQuery("SELECT d from DatoDelRecurso d " +
											     "WHERE d.recurso = :r " +
											     "ORDER BY d.orden")
									.setParameter("r", r)
									// TODO CONTROLAR ROLES
									.getResultList();
			return datosDelRecurso;
			} catch (Exception e){
				throw new ApplicationException(e);
			}
	}
		
	

	/**
	 * Agrega una AgrupacionDato  <b>d</b>.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso <b>r</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public AgrupacionDato agregarAgrupacionDato(Recurso r, AgrupacionDato a) throws UserException, ApplicationException {
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		if (recursoActual.getFechaBaja() != null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		a.setRecurso(r);
		if ( existeAgrupacionPorNombre(a) ){
			throw new UserException("ya_existe_una_agrupacion_con_el_nombre_especificado");
		}
		if (a.getNombre() == null){
			throw new UserException("el_nombre_de_la_agrupacion_es_obligatorio");
		}
		if (a.getOrden() == null){
			throw new UserException("el_orden_de_la_agrupacion_es_obligatorio");			
		}else {
			if(a.getOrden()<1) {
				throw new UserException("el_orden_de_la_agrupacion_debe_ser_mayor_a_cero");			
			}
		}
		entityManager.persist(a);
		return a;
	}

	/**
	 * Modifica Agrupaciones del dato <b>d</b>.
	 * Si tiene fecha baja no se puede modificar.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso asociado a <b>d</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */	
	public void modificarAgrupacionDato(AgrupacionDato a) throws UserException {
		
		AgrupacionDato agrupacionActual = (AgrupacionDato) entityManager.find(AgrupacionDato.class, a.getId());
		
		if (agrupacionActual == null) {
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}
		
		if (a.getNombre() == ""){
			throw new UserException("el_nombre_de_la_agrupacion_es_obligatorio");
		}
		
		if (a.getOrden() == null){
			throw new UserException("el_orden_de_la_agrupacion_es_obligatorio");			
		}else {
			if(a.getOrden().intValue()<0) {
				throw new UserException("el_orden_de_la_agrupacion_debe_ser_mayor_a_cero");			
			}
		}
		if (agrupacionActual.getFechaBaja() != null){
			throw new UserException("no_se_puede_modifcar_una_agrupacion_eliminada");			
		}
		
		agrupacionActual.setNombre(a.getNombre());
		agrupacionActual.setOrden(a.getOrden());
		
	}
	
	/**
	 * Realiza una baja logica de agrupaciones del dato <b>d</b>.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso asociado a <b>d</b>. 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */	
	public void eliminarAgrupacionDato(AgrupacionDato a, boolean controlarDatos) throws UserException, ApplicationException  {
		
		AgrupacionDato agrupacionActual = (AgrupacionDato) entityManager.find(AgrupacionDato.class, a.getId());
		
		if (agrupacionActual == null) {
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}
		if (agrupacionActual.getFechaBaja() != null){
			throw new UserException("la_agrupacion_ya_esta_eliminada");			
		}
		if(controlarDatos) {
			if (existeDatoASolicPorAgrupacion(a.getId())) {
				throw new UserException("no_se_puede_eliminar_la_agrupación_porque_tiene_datos_asociados");
			}
		}else {
			if(agrupacionActual.getDatosASolicitar()!=null) {
				for(DatoASolicitar dato : agrupacionActual.getDatosASolicitar()) {
					eliminarDatoASolicitar(dato);
				}
			}
		}
		
		agrupacionActual.setFechaBaja(new Date());
	}

	/**
	 * Retorna una lista de agrupaciones de datos del recurso
	 * Controla que el usuario tenga rol Administrador/Planificador sobre la agenda <b>a</b> del recurso
	 * Roles permitidos: Administrador, Planificador
	 * @throws ApplicationException 
	 */
	@SuppressWarnings("unchecked")
	public List<AgrupacionDato> consultarAgrupacionesDatos(Recurso r) throws ApplicationException{
		try{
			List<AgrupacionDato> agrupacionDato = (List<AgrupacionDato>) entityManager
									.createQuery("SELECT a from AgrupacionDato a " +
											     "WHERE a.recurso = :r " +
											     "AND a.fechaBaja is null " +
											     "ORDER BY a.orden")
									.setParameter("r", r)
									// TODO CONTROLAR ROLES
									.getResultList();
			return agrupacionDato;
			} catch (Exception e){
				throw new ApplicationException(e);
			}
	}
		


	/**
	 * Agrega un DatoASolicitar <b>d</b> asociándolo al recurso <b>r</b>.
	 * Controla que no exista otro dato vivo (fechaBaja == null) con el mismo nombre.
	 * Se permite setear todos sus atributos menos la fechaBaja.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso <b>r</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public DatoASolicitar agregarDatoASolicitar(Recurso r,AgrupacionDato a, DatoASolicitar d) 
		throws UserException, ApplicationException, BusinessException {

		
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		
		if (recursoActual == null) {
			throw new BusinessException("no_se_encuentra_el_recurso_especificado");
		}
		
		recursoActual.getAgrupacionDatos().size();
		
		
		if (a == null){
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}
		AgrupacionDato agrupacionActual = (AgrupacionDato) entityManager.find(AgrupacionDato.class, a.getId());
		if (agrupacionActual == null) {
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}
		
		if (agrupacionActual.getFechaBaja() != null) {
			throw new UserException("no_se_puede_modifcar_una_agrupacion_eliminada");
		}

		d.setRecurso(recursoActual);
		d.setAgrupacionDato(agrupacionActual);
		
		if (d.getNombre() == null || d.getNombre().equals("")){
			throw new UserException("el_nombre_del_dato_es_obligatorio");
		}

		if ( existeDatoASolicPorNombre (d.getNombre(), recursoActual.getId(), null) ) {
			throw new UserException("Ya existe un dato con el nombre especificado");
		}

		if (d.getEtiqueta() == null || d.getEtiqueta().equals("")){
			throw new UserException("la_etiqueta_del_dato_es_obligatoria");
		}

		if (d.getTipo() == null || d.getTipo().equals("")){
			throw new UserException("el_tipo_del_dato_es_obligatorio");
		}
		
		if (d.getFila() == null){
			throw new UserException("la_fila_del_dato_es_obligatoria");			
		}

		if (d.getFila().intValue() <1){
			throw new UserException("la_fila_del_dato_debe_ser_mayor_a_cero");			
		}
		
		if (d.getColumna() == null){
			throw new UserException("la_columna_del_dato_es_obligatoria");			
		}

		if (d.getLargo() == null){
			throw new UserException("el_largo_del_dato_es_obligatorio");			
		}

		if (d.getAnchoDespliegue() == null){
			throw new UserException("el_ancho_de_despliegue_es_obligatorio");			
		}
		
		if (d.getIncluirEnReporte()== true && d.getAnchoDespliegue().intValue()<= 0){
			throw new UserException("el_ancho_de_despliegue_debe_ser_mayor_a_cero");			
		}
		
		if (d.getOrdenEnLlamador() == null && d.getIncluirEnReporte()) {
			throw new UserException("el_orden_en_el_llamador_es_obligatorio");
		}
		
		else if (d.getIncluirEnLlamador() && d.getOrdenEnLlamador() == null){
			d.setOrdenEnLlamador(1);
		}
		
		if (d.getIncluirEnLlamador() && d.getLargoEnLlamador() == null){
			d.setLargoEnLlamador(d.getLargo());
		}

		
		entityManager.persist(d);
		return d;
	}

	/**
	 * Modifica los datos de un DatoASolicitar <b>d</b>.
	 * Controla que no exista otro dato vivo (fechaBaja == null) con el mismo nombre.
	 * Controla que no existan datos de reserva asociados al DatoASolicitar.
	 * Se permite setear todos sus atributos menos la fechaBaja.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso <b>r</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */	
	public void modificarDatoASolicitar(DatoASolicitar d) throws UserException, ApplicationException {

		DatoASolicitar datoActual = (DatoASolicitar) entityManager.find(DatoASolicitar.class, d.getId());
		
		if (datoActual == null) {
			throw new UserException("AE10055","No existe el dato a Solicitar: " + d.getId().toString());
		}

		//No se puede modificar un dato con fecha de baja
		if (datoActual.getFechaBaja() != null) {
			throw new UserException("AE10056","No se puede modificar un dato con fecha de baja");
		}

		if ( existeDatoASolicPorNombre(d.getNombre(), datoActual.getRecurso().getId(), d.getId()) ) {
			throw new UserException("AE10054","Ya existe ese dato a solicitar para el recurso");
		}

		if (d.getNombre() == null){
			throw new UserException("AE10045","El nombre del dato a solicitar no puede ser nulo");
		}

		if (d.getEtiqueta() == null){
			throw new UserException("AE10046","La etiqueta del dato a solicitar no puede ser nula");
		}

		if (d.getTipo() == null){
			throw new UserException("AE10047","El tipo del dato a solicitar no puede ser nulo");
		}
		
		if (d.getRequerido() == null){
			throw new UserException("AE10048","Se debe indicar si el dato a solicitar debe es requerido");
		}

		if (d.getEsClave() == null){
			throw new UserException("AE10049","Se debe indicar si el dato a solicitar debe es clave");
		}

		if (d.getFila() == null){
			throw new UserException("AE10050","La fila no puede ser nula");			
		}
		
		if (d.getColumna() == null){
			throw new UserException("AE10051","La columna no puede ser nula");			
		}

		if (d.getLargo() == null){
			throw new UserException("AE10052","El largo no puede ser nulo");			
		}

		if (d.getIncluirEnReporte() == null){
			throw new UserException("AE10120","Incluir en Reporte no puede ser nulo");			
		}

		if (d.getAnchoDespliegue() == null){
			throw new UserException("AE10121","El ancho de despliegue no puede ser nulo");			
		}

		if (d.getIncluirEnReporte()== true && d.getAnchoDespliegue().intValue()<= 0){
			throw new UserException("AE10122","El ancho de despliegue debe ser mayor que cero");			
		}
		
		if (d.getIncluirEnLlamador() == null) {
			throw new UserException("-1","Incluir en llamador no puede ser nulo");
		}
		
		if (d.getOrdenEnLlamador() == null && d.getIncluirEnReporte()) {
			throw new UserException("-1","Debe indicar el orden en el que se mostrará el dato en la pantalla del llamador");
		}
		
		else if (d.getOrdenEnLlamador() == null){
			d.setOrdenEnLlamador(1);
		}
		
		if (d.getLargoEnLlamador() == null){
			d.setLargoEnLlamador(d.getLargo());
		}
		
		
		datoActual.setNombre(d.getNombre());
		datoActual.setEtiqueta(d.getEtiqueta());
		datoActual.setTipo(d.getTipo());
		datoActual.setRequerido(d.getRequerido());
		datoActual.setEsClave(d.getEsClave());
		datoActual.setFila(d.getFila());
		datoActual.setColumna(d.getColumna());
		datoActual.setLargo(d.getLargo());
		datoActual.setTextoAyuda(d.getTextoAyuda());
    datoActual.setAgrupacionDato(d.getAgrupacionDato());
    datoActual.setIncluirEnReporte(d.getIncluirEnReporte());
    datoActual.setAnchoDespliegue(d.getAnchoDespliegue());
    datoActual.setIncluirEnLlamador(d.getIncluirEnLlamador());
    datoActual.setLargoEnLlamador(d.getLargoEnLlamador());
    datoActual.setOrdenEnLlamador(d.getOrdenEnLlamador());
	}
	

	/**
	 * Realiza una baja lógica del dato a solicitar <b>d</b> (se setea fechaBaja con la fecha actual del sistema).
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada. 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */	
	public void eliminarDatoASolicitar(DatoASolicitar d) throws UserException {

		DatoASolicitar datoActual = (DatoASolicitar) entityManager.find(DatoASolicitar.class, d.getId());
		if (datoActual == null) {
			throw new UserException("no_se_encuentra_el_dato_especificado");		
		}
		datoActual.setFechaBaja(new Date());
	}
	
	
	/**
	 * Agrega un ValorPosible asociándolo al DatoASolicitar.
	 * Controla que no exista otro valor posible con la misma etiqueta y/o valor que se solapen en el 
	 * período (fechaDesde, fechaHasta).
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso <b>r</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public ValorPosible agregarValorPosible(DatoASolicitar d, ValorPosible vp) throws UserException, ApplicationException {

		DatoASolicitar datoActual = (DatoASolicitar) entityManager.find(DatoASolicitar.class, d.getId());
		
		if (datoActual == null) {
			throw new UserException("no_se_encuentra_el_dato_especificado");
		}

		//No se puede modificar un dato con fecha de baja
		if (datoActual.getFechaBaja() != null) {
			throw new UserException("no_se_puede_modifcar_un_dato_eliminado");
		}

		vp.setDato(d);
		
		if (vp.getEtiqueta() == null){
			throw new UserException("la_etiqueta_del_dato_es_obligatoria");			
		}
		
		if (vp.getValor() == null){
			throw new UserException("el_valor_del_dato_es_obligatorio");
		}
		
		if (vp.getOrden() == null){
			throw new UserException("el_orden_del_dato_es_obligatorio");			
		}

		if (vp.getOrden().intValue() < 1){
			throw new UserException("el_orden_del_dato_debe_ser_mayor_a_cero");			
		}
		
		if (vp.getFechaDesde() == null){
			throw new UserException("la_fecha_de_inicio_es_obligatoria");			
		}
		
		//fechaDesde <= fechaHasta o fechaHasta == NULL
		if ((vp.getFechaHasta() != null) && (vp.getFechaDesde().compareTo(vp.getFechaHasta()) > 0 )){
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");			
		}
		if (existeValorPosiblePeriodo(vp)) {
			throw new UserException("ya_existe_otro_valor_con_la_misma_etiqueta_y_valor");
		}

		entityManager.persist(vp);
		return vp;
	}

	/**
	 * Modifica el ValorPosible.
	 * Controla que no exista otro valor posible con la misma etiqueta y/o valor que se solapen en el 
	 * período (fechaDesde, fechaHasta).
	 * Permite modificar fecha hasta , ya que significa la vigencia del valor
	 * Controla que el usuario tenga rol Planificador sobre la agenda del recurso <b>r</b> 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 * @throws ApplicationException 
	 */
	public void modificarValorPosible(ValorPosible v) throws UserException, ApplicationException {

		ValorPosible valorActual = (ValorPosible) entityManager.find(ValorPosible.class, v.getId());
		
		if (valorActual == null) {
			throw new UserException("AE10062","No existe el valor posible " + v.getId().toString());
		}

		//No se puede modificar un dato con fecha de baja
		if (valorActual.getDato().getFechaBaja() != null) {
			throw new UserException("AE10056","No se puede modificar un dato con fecha de baja");
		}

		v.setDato(valorActual.getDato());

		if (v.getEtiqueta() == null){
			throw new UserException("AE10057","La etiqueta no puede ser nula");			
		}
		
		if (v.getValor() == null){
			throw new UserException("AE10058","El valor no puede ser nulo");
		}
		
		if (v.getOrden() == null){
			throw new UserException("AE10059","El orden no puede ser nulo");			
		}

		if (v.getFechaDesde() == null){
			throw new UserException("AE10060","La fecha desde no puede ser nula");			
		}
		
		//fechaDesde <= fechaHasta o fechaHasta == NULL
		if ((v.getFechaHasta() != null) && (v.getFechaDesde().compareTo(v.getFechaHasta()) > 0 )){
			throw new UserException("AE10061","Fecha desde debe ser menor o igual a fecha hasta o fecha hasta es nula");			
		}

        //Controla que no exista otro valor posible con la misma etiqueta y/o valor que se solapen en el 
		// período (fechaDesde, fechaHasta).
		if (existeValorPosiblePeriodo(v)) {
			throw new UserException("AE10069","No puede existir otra etiqueta/valor posible que se solape en el período");
		}

		valorActual.setEtiqueta(v.getEtiqueta());
		valorActual.setOrden(v.getOrden());
		valorActual.setValor(v.getValor());
		valorActual.setFechaDesde(v.getFechaDesde());
		valorActual.setFechaHasta(v.getFechaHasta());
		
	}

	/**
	 * Realiza una baja física del ValorPosible
	 * Controla que el usuario tenga rol Planificador sobre la agenda asociada 
	 * Roles permitidos: Planificador
	 * @throws UserException 
	 */	
	public void eliminarValorPosible(ValorPosible v) throws UserException {

		ValorPosible valorActual = (ValorPosible) entityManager.find(ValorPosible.class, v.getId());
		
		if (valorActual == null) {
			throw new UserException("AE10062","No existe el valor posible " + v.getId().toString());
		}
		entityManager.remove(valorActual);
	
	}

	
	
	
	public Boolean existeRecursoPorNombre(Recurso r) throws ApplicationException{
		try{
		Long cant = (Long) entityManager
							.createQuery("SELECT count(r) from Recurso r " +
									"WHERE UPPER(r.nombre) = UPPER(:nombre) " +
									"AND (r.id <> :id OR :id is null)" +
									"AND r.agenda = :agenda " +
									"AND r.fechaBaja IS NULL")
							.setParameter("nombre", r.getNombre().toUpperCase())
							.setParameter("id", r.getId())
							.setParameter("agenda", r.getAgenda())
							.getSingleResult();
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}

		
	public Boolean existeRecursoPorDescripcion(Recurso r) throws ApplicationException{
		return false;
	}
	

	private Boolean existeReservaVivaMultiple(Recurso r) throws ApplicationException{
		try {
			
			Date ahora = new Date();
			
			List<?> a = (List<?>) entityManager
					.createQuery("SELECT r.id, count(d) FROM Disponibilidad d JOIN d.reservas r " +
							"WHERE d.recurso = :recurso " +
							"  AND d.fecha >= :fecha" +
							"  AND d.horaFin >= :hora_actual" +
							"  AND (r.estado = :reservado OR r.estado = :pendiente) " +
							"GROUP BY r.id " +
							"HAVING COUNT(d) > 1")
					.setParameter("recurso", r)
					.setParameter("fecha",ahora,TemporalType.DATE)
					.setParameter("hora_actual",ahora,TemporalType.TIMESTAMP)
					.setParameter("reservado", Estado.R)
					.setParameter("pendiente", Estado.P)
					.getResultList();

		return ( !a.isEmpty());
		}catch (Exception e){
			throw new ApplicationException(e);
		}
	}

	private Boolean hayReservasVivasPorFecha(Recurso r, Date desde, Date hasta) throws ApplicationException{
		try {Long cant = (Long) entityManager
					.createQuery("SELECT count(r) FROM Disponibilidad d JOIN d.reservas r " +
							"WHERE d.recurso = :recurso " +
							"  AND (d.fecha < :fecha_desde OR d.fecha > :fecha_hasta )" +
							"  AND (r.estado = :reservado OR r.estado = :pendiente) " )
					.setParameter("recurso", r)
					.setParameter("fecha_desde", desde,TemporalType.DATE)
					.setParameter("fecha_hasta", hasta,TemporalType.DATE)
					.setParameter("reservado", Estado.R)
					.setParameter("pendiente", Estado.P)
					.getSingleResult();

		return (cant > 0);
		}catch (Exception e){
			throw new ApplicationException(e);
		}
	}

	private Boolean hayDispVivasPorFecha(Recurso r, Date desde, Date hasta) throws ApplicationException{
		try {Long cant = (Long) entityManager
					.createQuery("SELECT count(d) FROM Disponibilidad d " +
							"WHERE d.recurso = :recurso " +
							"  AND (d.fecha < :fecha_desde OR d.fecha > :fecha_hasta )" +
							"  AND d.fechaBaja IS NULL")
					.setParameter("recurso", r)
					.setParameter("fecha_desde", desde)
					.setParameter("fecha_hasta", hasta)
					.getSingleResult();

		return (cant > 0);
		}catch (Exception e){
			throw new ApplicationException(e);
		}
	}

	private Boolean existeAgrupacionPorNombre(AgrupacionDato a) throws ApplicationException{
		try{
	
		Long cant = (Long) entityManager
							.createQuery("SELECT count(a) from AgrupacionDato a " +
									"WHERE a.nombre = :nombre " +
									"AND (a.id <> :id OR :id is null)" +
									"AND  a.recurso = :recurso " +
									"AND  a.fechaBaja IS NULL")
							.setParameter("nombre", a.getNombre())
							.setParameter("id", a.getId())
							.setParameter("recurso", a.getRecurso())
							.getSingleResult();
		
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}

	// Controla que no exista un dato a solicitar con el mismo nombre en la base.
	public boolean existeDatoASolicPorNombre(String n, Integer idRecurso, Integer idDatoSolicitar) throws ApplicationException{
		try{
			Long cant = (Long) entityManager
							.createQuery("SELECT count(d) "+
									"from DatoASolicitar d " +
									"WHERE upper(d.nombre) = upper(:n) " +
									"AND d.recurso.id = :recurso " +
									"AND d.fechaBaja IS NULL " +
									"AND (d.id <> :idDatoSolicitar or :idDatoSolicitar is null)")
							.setParameter("n", n)
							.setParameter("recurso", idRecurso)
							.setParameter("idDatoSolicitar", idDatoSolicitar)
							.getSingleResult();
		
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}

	// Controla que no existan datos a solicitar vivos para la agrupacion
	// ya que si existen esta no se puede eliminar
	private Boolean existeDatoASolicPorAgrupacion(Integer a) throws ApplicationException{
		try{
			Long cant = (Long) entityManager
							.createQuery("SELECT count(d) "+
									"from DatoASolicitar d " +
									"WHERE d.agrupacionDato.id = :a " +
									"AND d.fechaBaja IS NULL")
							.setParameter("a", a)
							.getSingleResult();
		
		return (cant > 0);
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}

	//Controla que no exista otro valor posible con el mismo valor/etiqueta que se solapen en el período (fechaDesde, fechaHasta).
	public boolean existeValorPosiblePeriodo(ValorPosible v) throws ApplicationException{
		
		String queryString = 
				"SELECT count(v) from ValorPosible v " +
				"WHERE (v.valor = :valor OR v.etiqueta = :etiqueta ) " +
				(v.getId() != null ? " AND v.id <> :id " : "") +	//Si estoy modifiando un valorPosible, no lo comparo consigo mismo.
				"AND  v.dato = :dato " +
				"AND (" +
				(v.getFechaHasta() == null ? " (v.fechaHasta is null) OR " + //Si el  nuevo valorPosible no tiene fechaHasta y en la DB hay otro sin fechaHasta => se solapan
				                             " (v.fechaHasta is not null and :desde <= v.fechaHasta) " //Si el nuevo valorPosible no tiene fechaHasta y comienza antes del fin del que existe en la DB => se solapan
				                             :
				                             " (v.fechaHasta is null and :hasta >= v.fechaDesde) OR " + //Si el  nuevo valorPosible termina dentro del período del que esta en la DB => se solapan
				                             " (v.fechaHasta is not null and :hasta >= v.fechaDesde and :desde <= v.fechaHasta) " ) + //Si el  nuevo valorPosible termina dentro del período del que esta en la DB => se solapan
				")";
		
		
		
		try{
			Query query = entityManager.createQuery(queryString);
			query.setParameter("valor", v.getValor())
 				 .setParameter("etiqueta", v.getEtiqueta())
 				 .setParameter("dato", v.getDato())
				 .setParameter("desde", v.getFechaDesde(), TemporalType.TIMESTAMP);
			
			if (v.getId() != null) {
				query.setParameter("id", v.getId());
			}
			if (v.getFechaHasta() != null) {
				query.setParameter("hasta", v.getFechaHasta(), TemporalType.TIMESTAMP);
			}

			Long cant = (Long) query.getSingleResult();
			
			return (cant > 0);
			
		} catch (Exception e){
			throw new ApplicationException(e);
		}

	}
	
		


	/**
	 * Retorna el arbol de AgrupacionDato/DatoASolicitar/ValorPosible vivos.
	 * La lista de objetos AgrupacionDato ordenada por el atributo orden.
	 * La lista de objetos DatoASolicitar ordenada por (fila,columna)
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR","RA_AE_ANONIMO", "RA_AE_FATENCION","RA_AE_FCALL_CENTER"})
	public List<AgrupacionDato> consultarDefinicionDeCampos(Recurso recurso) throws BusinessException {
		if (recurso == null) {
			throw new BusinessException("AE20084", "El recurso no puede ser nulo");
		}
		
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		
		List<AgrupacionDato> agrupacionesDTO = new ArrayList<AgrupacionDato>();

		//Obtengo las agrupaciones vivas del recurso
		List<AgrupacionDato> agrupaciones = (List<AgrupacionDato>) entityManager
											.createQuery(
											"from AgrupacionDato agrupacion " +
											"where agrupacion.recurso = :r and agrupacion.fechaBaja is null " +
											"order by agrupacion.orden ")
											.setParameter("r", recurso)
											.getResultList();
		
		//Para cada agrupacion obtengo los datos a solicitar de la misma.
		for(AgrupacionDato agrupacion: agrupaciones) {

			AgrupacionDato agrupacionDTO = new AgrupacionDato(agrupacion);
			agrupacionDTO.setRecurso(recurso);
			agrupacionesDTO.add(agrupacionDTO);
			
			List<DatoASolicitar> datosDTO = obtenerDatosASolicitar(agrupacion);
			agrupacionDTO.setDatosASolicitar(datosDTO);
			
		}
		
		return agrupacionesDTO;
	}
	
	/**
	 * Retorna el arbol de AgrupacionDato/DatoASolicitar/ValorPosible.
	 * La lista de objetos AgrupacionDato ordenada por el atributo orden.
	 * La lista de objetos DatoASolicitar ordenada por (fila,columna)
	 * Se usa para la consulta de Reservas por datos a Solicitar y actualmente
	 * solo permite traer todos los valores posibles, no los datos
	 * a solicitar ya que esto podría provocar solapamientos en el formulario
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR","RA_AE_ANONIMO","RA_AE_FCALL_CENTER","RA_AE_FATENCION"})
	public List<AgrupacionDato> consultarDefCamposTodos(Recurso recurso) throws BusinessException {
		if (recurso == null) {
			throw new BusinessException("AE20084", "El recurso no puede ser nulo");
		}
		
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		
		List<AgrupacionDato> agrupacionesDTO = new ArrayList<AgrupacionDato>();

		//Obtengo las agrupaciones vivas del recurso
		List<AgrupacionDato> agrupaciones = (List<AgrupacionDato>) entityManager
											.createQuery(
											"from AgrupacionDato agrupacion " +
											"where agrupacion.recurso = :r and agrupacion.fechaBaja is null " +
											"order by agrupacion.orden ")
											.setParameter("r", recurso)
											.getResultList();
		
		//Para cada agrupacion obtengo los datos a solicitar de la misma.
		for(AgrupacionDato agrupacion: agrupaciones) {

			AgrupacionDato agrupacionDTO = new AgrupacionDato(agrupacion); 
			agrupacionesDTO.add(agrupacionDTO);
			
			List<DatoASolicitar> datosDTO = obtenerTodosDatosASolicitar(agrupacion);
			agrupacionDTO.setDatosASolicitar(datosDTO);
			
		}
		
		return agrupacionesDTO;
	}
	

	@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR","RA_AE_ANONIMO","RA_AE_FATENCION"})
	public Boolean mostrarDatosASolicitarEnLlamador(Recurso recurso) throws BusinessException {

		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		
		Long cantAMostrar = (Long) entityManager
				.createQuery(
					"select count(dato) from DatoASolicitar dato " +
					"where dato.recurso = :recurso and " +
					"      dato.fechaBaja is null and " +
					"      dato.incluirEnLlamador = :incluir ")
				.setParameter("recurso", recurso)
				.setParameter("incluir", true)
				.getSingleResult();

		if (cantAMostrar > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private List<DatoASolicitar> obtenerDatosASolicitar(AgrupacionDato agrupacion) throws BusinessException {
		
		List<DatoASolicitar> datosDTO = new ArrayList<DatoASolicitar>();
		
		//Obtengo los datos a solicitar vivos de la agrupacion
		List<DatoASolicitar> datos = (List<DatoASolicitar>) entityManager
									.createQuery(
									"from DatoASolicitar dato " +
									"where dato.agrupacionDato = :agrupacion and " +
									"      dato.fechaBaja is null " +
									"order by dato.fila, dato.columna ")
									.setParameter("agrupacion", agrupacion)
									.getResultList();
		
		//Para cada dato a solicitar que sea del tipo Lista, obtengo los valores posibles.
		for (DatoASolicitar datoASolicitar : datos) {
			
			DatoASolicitar datoASolicitarDTO = new DatoASolicitar(datoASolicitar);
			datosDTO.add(datoASolicitarDTO);
			
			if (datoASolicitarDTO.getTipo() == Tipo.LIST) {
				List<ValorPosible> valoresDTO = obtenerValoresPosibles(datoASolicitar);
				datoASolicitarDTO.setValoresPosibles(valoresDTO);
			}
		}
		
		return datosDTO;
	}

	/**
	 * Obtiene la lista de Datos a Solicitar para armar la consulta de Reserva
	 * por datos de la reserva. No trae los datos a solicitar dados de baja,
	 * debido a que esto podría provocar solapamientos en el formulario dinámico
	 * si recupera todos los valores posibles de los datos a solicitar de tipo
	 * lista aunque ya hayan sido dados de baja.
	 * @param agrupacion
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private List<DatoASolicitar> obtenerTodosDatosASolicitar(AgrupacionDato agrupacion) throws BusinessException {
		
		List<DatoASolicitar> datosDTO = new ArrayList<DatoASolicitar>();
		
		//Obtengo los datos a solicitar vivos de la agrupacion
		List<DatoASolicitar> datos = (List<DatoASolicitar>) entityManager
									.createQuery(
									"from DatoASolicitar dato " +
									"where dato.agrupacionDato = :agrupacion and " +
									"      dato.fechaBaja is null " +
									"order by dato.fila, dato.columna ")
									.setParameter("agrupacion", agrupacion)
									.getResultList();
		
		//Para cada dato a solicitar que sea del tipo Lista, obtengo los valores posibles.
		for (DatoASolicitar datoASolicitar : datos) {
			
			DatoASolicitar datoASolicitarDTO = new DatoASolicitar(datoASolicitar);
			datosDTO.add(datoASolicitarDTO);
			
			if (datoASolicitarDTO.getTipo() == Tipo.LIST) {
				List<ValorPosible> valoresDTO = obtenerTodosValoresPosibles(datoASolicitar);
				datoASolicitarDTO.setValoresPosibles(valoresDTO);
			}
		}
		
		return datosDTO;
	}
	

	
	@SuppressWarnings("unchecked")
	private List<ValorPosible> obtenerValoresPosibles (DatoASolicitar dato) {
		
		List<ValorPosible> valoresDTO = new ArrayList<ValorPosible>(); 
		
		//Obtengo los valores posibles vivos del dato
		List<ValorPosible> valores = (List<ValorPosible>) entityManager
									.createQuery(
									"from ValorPosible valor " +
									"where valor.dato = :d and " +
									"      :ahora >= valor.fechaDesde and " +
									"      (valor.fechaHasta is null or :ahora <= valor.fechaHasta) " +
									"order by valor.orden ")
									.setParameter("d", dato)
									.setParameter("ahora", new Date(), TemporalType.TIMESTAMP)
									.getResultList();
		
		for (ValorPosible valorPosible : valores) {
			valoresDTO.add(new ValorPosible(valorPosible));
		}
		return valoresDTO;
	}

	/**
	 * Obtiene la lista de valores posibles para un dato a solicitar de tipo 
	 * lista sin filtrar por la vigencia, se usa para la consulta de Reserva
	 * por datos de la reserva.
	 * @param dato
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ValorPosible> obtenerTodosValoresPosibles (DatoASolicitar dato) {
		
		List<ValorPosible> valoresDTO = new ArrayList<ValorPosible>(); 
		
		//Obtengo los valores posibles vivos del dato
		List<ValorPosible> valores = (List<ValorPosible>) entityManager
									.createQuery(
									"from ValorPosible valor " +
									"where valor.dato = :d  " +
									"order by valor.orden, valor.fechaDesde ")
									.setParameter("d", dato)
									.getResultList();
		
		for (ValorPosible valorPosible : valores) {
			valoresDTO.add(new ValorPosible(valorPosible));
		}
		return valoresDTO;
	}



	@SuppressWarnings("unchecked")
	@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR","RA_AE_ANONIMO","RA_AE_FCALL_CENTER","RA_AE_FATENCION"})
	public List<DatoASolicitar> consultarDatosSolicitar(Recurso r) throws ApplicationException{
	
	List<DatoASolicitar> datosDTO = new ArrayList<DatoASolicitar>();
	
	//Obtengo los datos a solicitar vivos de la agrupacion
	List<DatoASolicitar> datos = (List<DatoASolicitar>) entityManager
								.createQuery(
								"from DatoASolicitar dato " +
								"where dato.recurso = :recurso and " +
								"      dato.fechaBaja is null " +
								"order by dato.agrupacionDato.orden, dato.fila, dato.nombre ")
								.setParameter("recurso", r)
								.getResultList();
	
	//Para cada dato a solicitar que sea del tipo Lista, obtengo los valores posibles.
	for (DatoASolicitar datoASolicitar : datos) {
		
		DatoASolicitar datoASolicitarDTO = new DatoASolicitar(datoASolicitar);
		datosDTO.add(datoASolicitarDTO);
		if (datoASolicitar.getTipo() == Tipo.LIST) {
			List<ValorPosible> valoresDTO = obtenerTodosValoresPosibles(datoASolicitar);
			datoASolicitarDTO.setValoresPosibles(valoresDTO);
		}
	
	}
	return datosDTO;
	}

	/**
	 * Retorna todos los valores posibles aunque no esten en el rango desde,hasta.
	 */
	public List<ValorPosible> consultarValoresPosibles(DatoASolicitar d)
			throws ApplicationException {
		return this.obtenerTodosValoresPosibles(d);
	}

	private Boolean hayReservasVivas(Recurso r) throws ApplicationException{
		try {Long cant = (Long) entityManager
					.createQuery("SELECT count(r) FROM Disponibilidad d JOIN d.reservas r " +
							"WHERE d.recurso = :recurso " +
							"  AND d.fecha >= :fecha" +
							"  AND d.horaFin >= :hora" +
							"  AND r.estado IN ('R','P')")
					.setParameter("recurso", r)
					.setParameter("fecha", new Date())
					.setParameter("hora", new Date())
					.getSingleResult();

		return (cant > 0);
		}catch (Exception e){
			throw new ApplicationException(e);
		}
	}
	

	public void copiarRecurso(Recurso r) throws BusinessException, ApplicationException, UserException {
		
		r = entityManager.find(Recurso.class, r.getId());
		if (r == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		
		if (r.getFechaBaja() != null) {
			throw new BusinessException("-1", "El recurso ha sido dado de baja");
		}		
		
		
		
		//1- Creo un nuevo Recurso y copio los atributos de r.
		//2- Creo un nuevo TextoRecurso y copio los atributos de r.textoRecurso.
		//3- Para cada ValidacionPorRecurso de r, creo una copia y la asigno al nuevo recurso
		//4- Para cada DatoDelRecurso de r, creo una copia y la asigno al nuevo recurso.
		//5- Para cada AgrupacionDato de r:
		//		creo una copia y la asigno al recurso nuevo.
		//
		//5.1  	Para cada DatoASolicitar de la AgrupacionDato:
		//			creo una copia y la asigno al recurso nuevo y a la AgrupacionDato nueva.
		//
		//5.2		Para cada ValorPosible del DatoASolicitar:
		//				creo una copia y la asigno al datoASolicitar nuevo.
		//
		//5.3		Para cada ValidacionPorDato del DatoASolicitar:
		//				Creo una ValidacionPorDato nueva y le asigno:
		//				el DatoASolicitar nuevo y
		//              la ValidacionPorRecurso nueva que se corresponde a la ValidacionPorRecurso original
		//              (Para esto usar un Map<ValidacionPorRecursoOrig, ValidacionPorRecursoCopia>
		//6- Se copian disponibilidades
		
		//1
		Recurso rCopia = new Recurso(r);
		rCopia.setId(null);
		rCopia.setAgenda(r.getAgenda());
		
		int contador = 0;
		do{
			contador++;
			rCopia.setNombre("Copia "+contador+" de "+r.getNombre());
			rCopia.setDescripcion("Copia "+contador+" de "+r.getDescripcion());
		}while(existeRecursoPorNombre(rCopia));
		
		entityManager.persist(rCopia);
		
		//2 
		rCopia.setTextosRecurso(new HashMap<String, TextoRecurso>());
  	if(r.getTextosRecurso() != null) {
  		for(String idioma : r.getTextosRecurso().keySet()) {
	  		TextoRecurso viejo = r.getTextosRecurso().get(idioma);
	  		TextoRecurso trCopia = new TextoRecurso();

	  		trCopia.setRecurso(viejo.getRecurso());
	  		trCopia.setIdioma(viejo.getIdioma());
	  		trCopia.setTextoPaso2(viejo.getTextoPaso2());
	  		trCopia.setTextoPaso3(viejo.getTextoPaso3());
	  		trCopia.setTituloCiudadanoEnLlamador(viejo.getTituloCiudadanoEnLlamador());
	  		trCopia.setTituloPuestoEnLlamador(viejo.getTituloPuestoEnLlamador());
	  		trCopia.setTicketEtiquetaUno(viejo.getTicketEtiquetaUno());
	  		trCopia.setTicketEtiquetaDos(viejo.getTicketEtiquetaDos());
	  		trCopia.setValorEtiquetaUno(viejo.getValorEtiquetaUno());
	  		trCopia.setValorEtiquetaDos(viejo.getValorEtiquetaDos());
	  		rCopia.getTextosRecurso().put(idioma, trCopia);
	  		entityManager.persist(trCopia);
  		}
  	}
		
		//3
		Map<ValidacionPorRecurso, ValidacionPorRecurso> validacionesDelRecurso = new HashMap<ValidacionPorRecurso, ValidacionPorRecurso>();
		for (ValidacionPorRecurso vxr : r.getValidacionesPorRecurso()) {
			if (vxr.getFechaBaja() == null) {
				ValidacionPorRecurso vxrCopia = new ValidacionPorRecurso();
				vxrCopia.setId(null);
				vxrCopia.setOrdenEjecucion(vxr.getOrdenEjecucion());
				vxrCopia.setRecurso(rCopia);
				vxrCopia.setValidacion(vxr.getValidacion());
				validacionesDelRecurso.put(vxr, vxrCopia);
				entityManager.persist(vxrCopia);
			}
		}
		
		//4
		for (DatoDelRecurso ddr : r.getDatoDelRecurso()) {
			
			DatoDelRecurso ddrCopia = new DatoDelRecurso();
			ddrCopia.setOrden(ddr.getOrden());
			ddrCopia.setEtiqueta(ddr.getEtiqueta());
			ddrCopia.setValor(ddr.getValor());
			ddrCopia.setRecurso(rCopia);
			rCopia.getDatoDelRecurso().add(ddrCopia);
			entityManager.persist(ddrCopia);
		}
		
		//5
		for (AgrupacionDato agrup : r.getAgrupacionDatos()) {
			
			if (agrup.getFechaBaja() == null) {
				AgrupacionDato agrupCopia = new AgrupacionDato(agrup);
				agrupCopia.setId(null);
				agrupCopia.setRecurso(rCopia);
				entityManager.persist(agrupCopia);
				
				//5.1
				for (DatoASolicitar campo : agrup.getDatosASolicitar()) {
					
					if (campo.getFechaBaja() == null) {
						DatoASolicitar campoCopia = new DatoASolicitar(campo);
						campoCopia.setId(null);
						campoCopia.setRecurso(rCopia);
						campoCopia.setAgrupacionDato(agrupCopia);
						
						agrupCopia.getDatosASolicitar().add(campoCopia);
						rCopia.getDatoASolicitar().add(campoCopia);
						entityManager.persist(campoCopia);
					
						//5.2
						for (ValorPosible vp : campo.getValoresPosibles()) {
							if (vp.getFechaHasta() == null || vp.getFechaHasta().after(new Date())) {
								ValorPosible vpCopia = new ValorPosible(vp);
								vpCopia.setId(null);
								vpCopia.setDato(campoCopia);
								entityManager.persist(vpCopia);
							}
						}
						
						//5.3
						for (ValidacionPorDato vxd : campo.getValidacionesPorDato()) {
							if (vxd.getFechaDesasociacion() == null) {
								ValidacionPorDato vxdCopia = new ValidacionPorDato();
								vxdCopia.setNombreParametro(vxd.getNombreParametro());
								vxdCopia.setDatoASolicitar(campoCopia);
								ValidacionPorRecurso vxrCopia = validacionesDelRecurso.get(vxd.getValidacionPorRecurso());
								vxdCopia.setValidacionPorRecurso(vxrCopia);
								entityManager.persist(vxdCopia);
							}
						}
					}
					
				}//Fin 5.1

			}
			
		}//Fin 5
		//6
		List<Disponibilidad> disponibilidades = r.getDisponibilidades();
		for (Disponibilidad disponibilidad : disponibilidades) {
			Disponibilidad disponibilidadCopia = new Disponibilidad();
			disponibilidadCopia.setCupo(disponibilidad.getCupo());
			disponibilidadCopia.setFecha(disponibilidad.getFecha());
			disponibilidadCopia.setFechaBaja(disponibilidad.getFechaBaja());
			disponibilidadCopia.setHoraFin(disponibilidad.getHoraFin());
			disponibilidadCopia.setHoraInicio(disponibilidad.getHoraInicio());
			disponibilidadCopia.setId(null);
			disponibilidadCopia.setNumerador(disponibilidad.getNumerador());
			disponibilidadCopia.setPlantilla(disponibilidad.getPlantilla());
			disponibilidadCopia.setRecurso(rCopia);
			disponibilidadCopia.setVersion(1);
			entityManager.persist(disponibilidadCopia);
		}
	}

	/**
	 * Retorna los servicios de autocompletar asociados al recurso.
	 * @param recurso
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	@RolesAllowed({"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR","RA_AE_ANONIMO","RA_AE_FCALL_CENTER","RA_AE_FATENCION"})
	public List<ServicioPorRecurso> consultarServicioAutocompletar (Recurso r) throws BusinessException {
		
		List<ServicioPorRecurso> servicios = (List<ServicioPorRecurso>) entityManager
										.createQuery(
										"from ServicioPorRecurso servicio " +
										"where servicio.recurso = :recurso and " +
										"      servicio.fechaBaja is null ")
										.setParameter("recurso", r)
										.getResultList();
		
		for (int i=0; i<servicios.size(); i++){
			servicios.get(i).getAutocompletado().getParametrosAutocompletados().size();
			servicios.get(i).getAutocompletadosPorDato().size();
		}
		
		return servicios;
	}
	
}
