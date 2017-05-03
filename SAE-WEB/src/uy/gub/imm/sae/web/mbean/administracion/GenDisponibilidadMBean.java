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
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.CupoPorDia;
import uy.gub.imm.sae.web.common.RowList;
import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Disponibilidades;
import uy.gub.imm.sae.common.DisponibilidadReserva;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;

public class GenDisponibilidadMBean extends BaseMBean {
	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/DisponibilidadesBean!uy.gub.imm.sae.business.ejb.facade.DisponibilidadesRemote")
	private Disponibilidades disponibilidadesEJB;

	private SessionMBean sessionMBean;
	private GenDispSessionMBean genDispSessionMBean;
	private DisponibilidadReserva dispAux = new DisponibilidadReserva();
	
	@PostConstruct
	public void initGenDisponibilidad(){
		//Se controla que se haya Marcado una agenda para trabajar con los recursos
		if (sessionMBean.getAgendaMarcada() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		
	}

	public DisponibilidadReserva getDispAux() {
		return dispAux;
	}

	public void setDispAux(DisponibilidadReserva dispAux) {
		this.dispAux = dispAux;
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public void obtenerCuposModelo(ActionEvent e){
		limpiarMensajesError();
		try {
			boolean hayError = false;
			if (sessionMBean.getAgendaMarcada() == null){
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"));
				hayError = true;
			}
			if (sessionMBean.getRecursoMarcado() == null){
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"));
				hayError = true;
			}
			if (genDispSessionMBean.getFechaModelo() == null ) {
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), "formGenerarPatronDia:fecha");
				hayError = true;
			}else if (Utiles.esFechaInvalida(genDispSessionMBean.getFechaModelo())) {
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_invalida"), "formGenerarPatronDia:fecha");
        hayError = true;
      }else if(!disponibilidadesEJB.esDiaHabil(genDispSessionMBean.getFechaModelo(), sessionMBean.getRecursoMarcado())){
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_no_corresponde_a_un_dia_habil"), "formGenerarPatronDia:fecha");
				hayError = true;
			}else {
				if (genDispSessionMBean.getFechaModelo().before(sessionMBean.getRecursoMarcado().getFechaInicioDisp())){
					addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso"), "formGenerarPatronDia:fecha");
					hayError = true;
				}
				if (sessionMBean.getRecursoMarcado().getFechaFinDisp() != null){
					if (genDispSessionMBean.getFechaModelo().after(sessionMBean.getRecursoMarcado().getFechaFinDisp())){
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso"), "formGenerarPatronDia:fecha");
						hayError = true;
					}
				}
			}
			VentanaDeTiempo v = new VentanaDeTiempo();
			if (!hayError ) {
				v.setFechaInicial(Utiles.time2InicioDelDia(genDispSessionMBean.getFechaModelo()) );
				v.setFechaFinal(Utiles.time2FinDelDia(genDispSessionMBean.getFechaModelo()));
				genDispSessionMBean.setDisponibilidadesDelDiaMatutinaModif(null);
				genDispSessionMBean.setDisponibilidadesDelDiaVespertinaModif(null) ;
				configurarDisponibilidadesDelDiaModif();
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public RowList<CupoPorDia> obtenerCupos(VentanaDeTiempo v){
		
		limpiarMensajesError();
		
		RowList<CupoPorDia> cuposAux = null;
		try{
			if (sessionMBean.getRecursoMarcado() != null){
				if (v.getFechaInicial() != null && v.getFechaFinal() != null &&
						v.getFechaInicial().compareTo(v.getFechaFinal()) <= 0 ) {
					List<Integer> cupos = agendarReservasEJB.obtenerCuposPorDia(sessionMBean.getRecursoMarcado(), v, sessionMBean.getTimeZone());
					Calendar fecha = Calendar.getInstance();
					fecha.setTime(v.getFechaInicial());
					Calendar fechaFin = Calendar.getInstance();
					fechaFin.setTime(v.getFechaFinal());
					Integer i = 0;
					List<CupoPorDia> lista_cupos = new ArrayList<CupoPorDia>();
					while ( !fecha.after( fechaFin ) ){
						if (cupos.get(i) != -1){			
							CupoPorDia cupoPorDia = new CupoPorDia();
							cupoPorDia.setDia(fecha.getTime());
							cupoPorDia.setCupoDisponible(cupos.get(i));
							lista_cupos.add(cupoPorDia);
						}
						i++;
						fecha.add(Calendar.DAY_OF_MONTH, 1);
					}
					cuposAux= new RowList<CupoPorDia>(lista_cupos);
				}	else{
					if(v.getFechaInicial()==null) {
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "formGenerarPatronDia:fechaDesde");
					}
					if(v.getFechaFinal()==null) {
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), "formGenerarPatronDia:fechaHasta");
					}
					if(v.getFechaInicial()!=null && v.getFechaFinal()!=null &&  v.getFechaInicial().compareTo(v.getFechaFinal()) > 0){
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "formGenerarPatronDia:fechaDesde", "formGenerarPatronDia:fechaHasta");
					}
				}
			}
			else{
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			}
				
		}catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
		}
		return cuposAux;
	}

	public void configurarDisponibilidadesDelDiaModif() {
		
		List<DisponibilidadReserva> dispMatutinas   = new ArrayList<DisponibilidadReserva>();
		List<DisponibilidadReserva> dispVespertinas = new ArrayList<DisponibilidadReserva>();

		VentanaDeTiempo ventana = new VentanaDeTiempo();
		ventana.setFechaInicial(Utiles.time2InicioDelDia(genDispSessionMBean.getFechaModelo()));
		ventana.setFechaFinal(Utiles.time2FinDelDia(genDispSessionMBean.getFechaModelo()));
			
		try {
			List<DisponibilidadReserva> lista = disponibilidadesEJB.obtenerDisponibilidadesReservas(sessionMBean.getRecursoMarcado(), ventana);
			
			for (DisponibilidadReserva d : lista) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(d.getHoraInicio());
				
				if (cal.get(Calendar.AM_PM) == Calendar.AM) {
					//Matutino
					dispMatutinas.add(d);
				}
				else {
					//Vespertino
					dispVespertinas.add(d);
				}
			}
			
		} catch (Exception e) { 
				addErrorMessage(e);
		}

		genDispSessionMBean.setDisponibilidadesDelDiaMatutinaModif(new RowList<DisponibilidadReserva>(dispMatutinas));
		genDispSessionMBean.setDisponibilidadesDelDiaVespertinaModif(new RowList<DisponibilidadReserva>(dispVespertinas));
	}

	public void beforePhaseGenerarDisponibilidades (PhaseEvent event) {
    //Verificar que el usuario tiene permisos para acceder a esta página
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_PLANIFICADOR_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    //Establecer el título de la pantalla
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("copiar_dia"));
		}
	}

	public void generarDisponibilidades(ActionEvent event){
		limpiarMensajesError();
		try {
			boolean hayError = false;
			if (genDispSessionMBean.getFechaModelo() == null ) {
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), "formGenerarPatronDia:fecha");
				hayError = true;
			}else if(Utiles.esFechaInvalida(genDispSessionMBean.getFechaModelo())) {
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_invalida"), "formGenerarPatronDia:fecha");
        hayError = true;
			}else if (!disponibilidadesEJB.esDiaHabil(genDispSessionMBean.getFechaModelo(), sessionMBean.getRecursoMarcado())){
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_no_corresponde_a_un_dia_habil"), "formGenerarPatronDia:fecha");
				hayError = true;
			}else {
				if (genDispSessionMBean.getFechaModelo().before(sessionMBean.getRecursoMarcado().getFechaInicioDisp())){
					addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso"), "formGenerarPatronDia:fecha");
					hayError = true;
				}
				if (sessionMBean.getRecursoMarcado().getFechaFinDisp() != null){
					if (genDispSessionMBean.getFechaModelo().after(sessionMBean.getRecursoMarcado().getFechaFinDisp())){
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso"), "formGenerarPatronDia:fecha");
						hayError = true;
					}
				}
			}
      Calendar hoy = new GregorianCalendar();
      hoy.add(Calendar.MILLISECOND, sessionMBean.getTimeZone().getOffset(hoy.getTimeInMillis()));
      hoy.set(Calendar.HOUR_OF_DAY, 0);
      hoy.set(Calendar.MINUTE, 0);
      hoy.set(Calendar.SECOND, 0);
      hoy.set(Calendar.MILLISECOND, 0);
			if (genDispSessionMBean.getFechaInicial() == null ) {
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "formGenerarPatronDia:fechaDesde");
				hayError = true;
			}else if (Utiles.esFechaInvalida(genDispSessionMBean.getFechaInicial())) {
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_invalida"), "formGenerarPatronDia:fechaDesde");
        hayError = true;
      }else {
        if(genDispSessionMBean.getFechaInicial().before(hoy.getTime())) {
          addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_debe_ser_igual_o_posterior_a_hoy"), "formGenerarPatronDia:fechaDesde");
          hayError = true;
        }
			}
			if (genDispSessionMBean.getFechaFinal() == null) {
				addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), "formGenerarPatronDia:fechaHasta");
				hayError = true;
			}else if (Utiles.esFechaInvalida(genDispSessionMBean.getFechaFinal())) {
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_invalida"), "formGenerarPatronDia:fechaHasta");
        hayError = true;
      }else {
        if(genDispSessionMBean.getFechaFinal().before(hoy.getTime())) {
          addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_igual_o_posterior_a_hoy"), "formGenerarPatronDia:fechaHasta");
          hayError = true;
        }
      }
			if(genDispSessionMBean.getFechaInicial()!=null && !Utiles.esFechaInvalida(genDispSessionMBean.getFechaInicial()) &&
			    genDispSessionMBean.getFechaFinal()!=null && !Utiles.esFechaInvalida(genDispSessionMBean.getFechaFinal())){
				if(genDispSessionMBean.getFechaInicial().compareTo(genDispSessionMBean.getFechaFinal()) > 0) {
					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "formGenerarPatronDia:fechaDesde", "formGenerarPatronDia:fechaHasta");
					hayError = true;
				}else {
					if (genDispSessionMBean.getFechaInicial().before(sessionMBean.getRecursoMarcado().getFechaInicioDisp())){
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso"), "formGenerarPatronDia:fechaDesde");
						hayError = true;
					}
					if (sessionMBean.getRecursoMarcado().getFechaFinDisp() != null){
						if (genDispSessionMBean.getFechaFinal().after(sessionMBean.getRecursoMarcado().getFechaFinDisp())){
							addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso"), "formGenerarPatronDia:fechaHasta");
							hayError = true;
						}
					}
					int cantDias = 0;
					Calendar calendario = new GregorianCalendar();
					calendario.setTime(genDispSessionMBean.getFechaInicial());
					while (!calendario.getTime().after(genDispSessionMBean.getFechaFinal())) {
						cantDias = cantDias + 1;
						calendario.add(Calendar.DATE, 1);
					}
					if (cantDias > sessionMBean.getRecursoMarcado().getCantDiasAGenerar()){
						addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_comprendidos_en_el_periodo_debe_ser_menor_que_la_cantidad_de_dias_a_generar_para"), "formGenerarPatronDia:fechaHasta");
						hayError = true;
					}
				}
			}
			if(hayError) {
			  return;
			}
			try{
				VentanaDeTiempo ventana = new VentanaDeTiempo();
				ventana.setFechaInicial(Utiles.time2InicioDelDia(genDispSessionMBean.getFechaInicial()) );
				ventana.setFechaFinal(Utiles.time2FinDelDia(genDispSessionMBean.getFechaFinal()));
				disponibilidadesEJB.generarDisponibilidades(sessionMBean.getRecursoMarcado(),genDispSessionMBean.getFechaModelo(), ventana, genDispSessionMBean.getDiasAplicar());
				addInfoMessage(sessionMBean.getTextos().get("disponibilidades_creadas"));
			} catch (OptimisticLockException lockE){
				addErrorMessage(sessionMBean.getTextos().get("error_de_acceso_concurrente"));
			} catch (PersistenceException persE){
				addErrorMessage(sessionMBean.getTextos().get("error_de_acceso_concurrente"));				
			} catch (EJBException eE){
				if (eE.getCause() instanceof OptimisticLockException){
					addErrorMessage(sessionMBean.getTextos().get("error_de_acceso_concurrente"));			
				}else {
					addErrorMessage(eE);
				}
			} catch (Exception e) {
				addErrorMessage(e);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public GenDispSessionMBean getGenDispSessionMBean() {
		return genDispSessionMBean;
	}

	public void setGenDispSessionMBean(GenDispSessionMBean genDispSessionMBean) {
		this.genDispSessionMBean = genDispSessionMBean;
	}
	
	public String getMensajePeriodoDisponibilidad() {
		if(sessionMBean.getRecursoMarcado() != null) {
			DateFormat df = new SimpleDateFormat(sessionMBean.getFormatoFecha());
			return sessionMBean.getTextos().get("la_fecha_debe_estar_comprendida_en_el_periodo_fdesde_a_fhasta")
					.replace("{fdesde}", df.format(sessionMBean.getRecursoMarcado().getFechaInicioDisp()))
					.replace("{fhasta}", df.format(sessionMBean.getRecursoMarcado().getFechaFinDisp()));
		}
		return "";
	}
	
	public String getMensajePeriodoDisponibilidad2() {
		if(sessionMBean.getRecursoMarcado() != null) {
			DateFormat df = new SimpleDateFormat(sessionMBean.getFormatoFecha());
			return sessionMBean.getTextos().get("las_fechas_deben_estar_comprendidas_en_el_periodo_fdesde_a_fhasta_y_no_abarcar_mas_de_dias_dias")
					.replace("{fdesde}", df.format(sessionMBean.getRecursoMarcado().getFechaInicioDisp()))
					.replace("{fhasta}", df.format(sessionMBean.getRecursoMarcado().getFechaFinDisp()))
					.replace("{dias}", sessionMBean.getRecursoMarcado().getCantDiasAGenerar().toString());
		}
		return "";
	}
}
