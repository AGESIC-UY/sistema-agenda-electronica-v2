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

package uy.gub.imm.sae.web.mbean.administracion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.RowList;
import uy.gub.imm.sae.business.ejb.facade.Disponibilidades;
import uy.gub.imm.sae.common.DisponibilidadReserva;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;


public class CrearDisponibilidadMBean extends BaseMBean {
	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/DisponibilidadesBean!uy.gub.imm.sae.business.ejb.facade.DisponibilidadesRemote")
	private Disponibilidades disponibilidadesEJB;

	private SessionMBean sessionMBean;
	private CrearDispSessionMBean crearDispSessionMBean;
	
	private List<SelectItem> horas =  new ArrayList<SelectItem>();
	private List<SelectItem> minutos =  new ArrayList<SelectItem>();
		
	@PostConstruct
	public void initGenDisponibilidad(){
		
		//Se controla que se haya Marcado una agenda para trabajar con los recursos
		if (sessionMBean.getAgendaMarcada() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		this.cargarListaHoras();
		this.cargarListaMinutos();
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public void beforePhaseCrearDisponibilidades (PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Generar Disponibilidades para un Día");
		}
	}

	
	public void crearDisponibilidades(ActionEvent event){
		limpiarMensajesError();
		
		boolean huboError = false;
		
		try{
			if (crearDispSessionMBean.getFechaCrear() == null ) {
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), "formCrearDisponibilidad:fecha");
				huboError = true;
			}else if (!disponibilidadesEJB.esDiaHabil(crearDispSessionMBean.getFechaCrear(), sessionMBean.getRecursoMarcado())){
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_no_corresponde_a_un_dia_habil"), "formCrearDisponibilidad:fecha");
				huboError = true;
			}else {
				if (crearDispSessionMBean.getFechaCrear().before(sessionMBean.getRecursoMarcado().getFechaInicioDisp())){
					addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso"), "formCrearDisponibilidad:fecha");
					huboError = true;
				}
				if (sessionMBean.getRecursoMarcado().getFechaFinDisp() != null){
					if (crearDispSessionMBean.getFechaCrear().after(sessionMBean.getRecursoMarcado().getFechaFinDisp())){
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso"), "formCrearDisponibilidad:fecha");
						huboError = true;
					}
				}
			}
			
			Date fecha = crearDispSessionMBean.getFechaCrear();
			if(fecha==null) {
				fecha = new Date();
			}
			
			Calendar c0 = new GregorianCalendar();
			c0.setTime(fecha); //Debe estar en GMT0
			
			Calendar c1 = new GregorianCalendar();
			c1.set(Calendar.YEAR, c0.get(Calendar.YEAR));
			c1.set(Calendar.MONTH, c0.get(Calendar.MONTH));
			c1.set(Calendar.DAY_OF_MONTH, c0.get(Calendar.DAY_OF_MONTH));
			c1.set(Calendar.HOUR_OF_DAY, crearDispSessionMBean.getHoraD());
			c1.set(Calendar.MINUTE, crearDispSessionMBean.getMinD());
			c1.set(Calendar.SECOND, 0);
			
			crearDispSessionMBean.setHoraDesde(c1.getTime());
			if (crearDispSessionMBean.getHoraDesde() == null ) {
				addErrorMessage(sessionMBean.getTextos().get("la_hora_de_inicio_es_obligatoria"), "formCrearDisponibilidad:seleccionHoraD");
				huboError = true;
			}
				
			Calendar c2 = new GregorianCalendar();
			c2.set(Calendar.YEAR, c0.get(Calendar.YEAR));
			c2.set(Calendar.MONTH, c0.get(Calendar.MONTH));
			c2.set(Calendar.DAY_OF_MONTH, c0.get(Calendar.DAY_OF_MONTH));
			c2.set(Calendar.HOUR_OF_DAY, crearDispSessionMBean.getHoraH());
			c2.set(Calendar.MINUTE, crearDispSessionMBean.getMinH());
			c2.set(Calendar.SECOND, 0);
			
			crearDispSessionMBean.setHoraHasta(c2.getTime());
			if (crearDispSessionMBean.getHoraHasta() == null ) {
				addErrorMessage(sessionMBean.getTextos().get("la_hora_de_fin_es_obligatoria"), "formCrearDisponibilidad:seleccionHoraH");
				huboError = true;
			}
			
			if (crearDispSessionMBean.getHoraDesde().compareTo(crearDispSessionMBean.getHoraHasta()) >= 0){
				addErrorMessage(sessionMBean.getTextos().get("la_hora_de_fin_debe_ser_posterior_a_la_hora_de_inicio"), "formCrearDisponibilidad:seleccionHoraD", "formCrearDisponibilidad:seleccionHoraH");
				huboError = true;
			}
			
			if (crearDispSessionMBean.getFrecuencia() == null){
				addErrorMessage(sessionMBean.getTextos().get("la_frecuencia_es_obligatoria"), "formCrearDisponibilidad:frecuencia");
				huboError = true;
			}else if (crearDispSessionMBean.getFrecuencia().intValue() < 1){
				addErrorMessage(sessionMBean.getTextos().get("la_frecuencia_debe_ser_mayor_que_cero"), "formCrearDisponibilidad:frecuencia");
				huboError = true;
			}
			
			if (crearDispSessionMBean.getCupo() == null){
				addErrorMessage(sessionMBean.getTextos().get("el_cupo_por_periodo_es_obligatorio"), "formCrearDisponibilidad:cupos");
				huboError = true;
			}else if (crearDispSessionMBean.getCupo().intValue() < 1){
				addErrorMessage(sessionMBean.getTextos().get("el_cupo_por_periodo_debe_ser_mayor_a_cero"), "formCrearDisponibilidad:cupos");
				huboError = true;
			}
			
			if (huboError) {
				return;
			}
		
			disponibilidadesEJB.generarDisponibilidadesNuevas(sessionMBean.getRecursoMarcado(),crearDispSessionMBean.getFechaCrear(), 
					crearDispSessionMBean.getHoraDesde(), crearDispSessionMBean.getHoraHasta(), 
					crearDispSessionMBean.getFrecuencia(), crearDispSessionMBean.getCupo());
			addInfoMessage(sessionMBean.getTextos().get("disponibilidades_creadas"), MSG_ID);
			this.configurarDisponibilidadesDelDia();
		} catch (OptimisticLockException lockE){
			addErrorMessage(sessionMBean.getTextos().get("error_de_acceso_concurrente"), MSG_ID);
		}	catch (PersistenceException persE){
			addErrorMessage(sessionMBean.getTextos().get("error_de_acceso_concurrente"), MSG_ID);				
		} catch (EJBException eEx){
			if (eEx.getCause() instanceof OptimisticLockException){
				addErrorMessage(sessionMBean.getTextos().get("error_de_acceso_concurrente"), MSG_ID);					
			}	else{
				addErrorMessage(eEx, MSG_ID);
			}
		} catch (Exception ex) {
			addErrorMessage(ex, MSG_ID);
		}
	}

	public CrearDispSessionMBean getCrearDispSessionMBean() {
		return crearDispSessionMBean;
	}

	public void setCrearDispSessionMBean(CrearDispSessionMBean crearDispSessionMBean) {
		this.crearDispSessionMBean = crearDispSessionMBean;
	}

	private void cargarListaHoras(){
		horas =  new ArrayList<SelectItem>();
	    Integer h = 0;
	    String labelH;
	    
	    while (h < 24){
			SelectItem s = new SelectItem();
			s.setValue(h);
			labelH = Integer.toString(h);
			if (labelH.length()<2){
				labelH = "0"+labelH;
			}
			s.setLabel(labelH);
			horas.add(s);
			h = h + 1;
		}
	}

	private void cargarListaMinutos(){

		minutos =  new ArrayList<SelectItem>();
	    Integer h = 0;
	    String labelH;
	    
	    while (h < 60){
			SelectItem s = new SelectItem();
			s.setValue(h);
			labelH = Integer.toString(h);
			if (labelH.length()<2){
				labelH = "0"+labelH;
			}
			s.setLabel(labelH);
			minutos.add(s);
			h = h + 15;
		}
	}

	public List<SelectItem> getHoras() {
		return horas;
	}

	public void setHoras(List<SelectItem> horas) {
		this.horas = horas;
	}

	public List<SelectItem> getMinutos() {
		return minutos;
	}

	public void setMinutos(List<SelectItem> minutos) {
		this.minutos = minutos;
	}

	public void consultarDisponibilidadesDelDia(ActionEvent event) {
		
		limpiarMensajesError();
		
		try {
			boolean huboError = false;
			if (crearDispSessionMBean.getFechaCrear() == null ) {
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), "formCrearDisponibilidad:fecha");
				huboError = true;
			}else if (!disponibilidadesEJB.esDiaHabil(crearDispSessionMBean.getFechaCrear(), sessionMBean.getRecursoMarcado())){
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_no_corresponde_a_un_dia_habil"), "formCrearDisponibilidad:fecha");
				huboError = true;
			}else {
				if (crearDispSessionMBean.getFechaCrear().before(sessionMBean.getRecursoMarcado().getFechaInicioDisp())){
					addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso"), "formCrearDisponibilidad:fecha");
					huboError = true;
				}
				if (sessionMBean.getRecursoMarcado().getFechaFinDisp() != null){
					if (crearDispSessionMBean.getFechaCrear().after(sessionMBean.getRecursoMarcado().getFechaFinDisp())){
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso"), "formCrearDisponibilidad:fecha");
						huboError = true;
					}
				}
			}
			
			if(!huboError){
				this.configurarDisponibilidadesDelDia();
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public void configurarDisponibilidadesDelDia() {

		List<DisponibilidadReserva> dispMatutinas   = new ArrayList<DisponibilidadReserva>();
		List<DisponibilidadReserva> dispVespertinas = new ArrayList<DisponibilidadReserva>();

		VentanaDeTiempo ventana = new VentanaDeTiempo();
		
	ventana.setFechaInicial(Utiles.time2InicioDelDia(crearDispSessionMBean.getFechaCrear()));
	ventana.setFechaFinal(Utiles.time2FinDelDia(crearDispSessionMBean.getFechaCrear()));
			
		try {
			List<DisponibilidadReserva> lista = disponibilidadesEJB.obtenerDisponibilidadesReservas(sessionMBean.getRecursoMarcado(), ventana);
				
			for (DisponibilidadReserva d : lista) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(d.getHoraInicio());
					//cal.setTimeZone(sessionMBean.getTimeZone());
					
					if (cal.get(Calendar.AM_PM) == Calendar.AM) {
						//Matutino
						dispMatutinas.add(d);
					}
					else {
						//Vespertino
						dispVespertinas.add(d);
					}
			}
				
				
		} catch (Exception ex) {
			
			ex.printStackTrace();
			
			addErrorMessage(ex);
		}

		crearDispSessionMBean.setDisponibilidadesDelDiaMatutina(new RowList<DisponibilidadReserva>(dispMatutinas));
		crearDispSessionMBean.setDisponibilidadesDelDiaVespertina(new RowList<DisponibilidadReserva>(dispVespertinas));
	}

	public String getMensajePeriodoDisponibilidad() {
		if(sessionMBean.getRecursoMarcado() != null) {
			DateFormat df = new SimpleDateFormat(sessionMBean.getFormatoFecha());
			if (sessionMBean.getRecursoMarcado().getFechaFinDisp()!=null)
			{
				return sessionMBean.getTextos().get("la_fecha_debe_estar_comprendida_en_el_periodo_fdesde_a_fhasta")
						.replace("{fdesde}", df.format(sessionMBean.getRecursoMarcado().getFechaInicioDisp()))
						.replace("{fhasta}", df.format(sessionMBean.getRecursoMarcado().getFechaFinDisp()));
			}else
			{
				return sessionMBean.getTextos().get("la_fecha_debe_ser_posterior_a_la_fecha_fdesde")
						.replace("{fdesde}", df.format(sessionMBean.getRecursoMarcado().getFechaInicioDisp()));
			}
			
		}
		return "";
	}
	
}
