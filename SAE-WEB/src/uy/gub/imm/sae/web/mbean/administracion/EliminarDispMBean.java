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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Disponibilidades;
import uy.gub.imm.sae.common.DisponibilidadReserva;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Dia;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.CeldaDia;
import uy.gub.imm.sae.web.common.RowList;

public class EliminarDispMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/DisponibilidadesBean!uy.gub.imm.sae.business.ejb.facade.DisponibilidadesRemote")
	Disponibilidades disponibilidadesEJB;
	
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
  private AgendarReservas agendarReservasEJB;
	
	private SessionMBean sessionMBean;

	@PostConstruct
	public void init(){
		//Se controla que se haya Marcado una agenda para trabajar con los recursos
		if (sessionMBean.getAgendaMarcada() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		if (sessionMBean.getRecursoMarcado() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
	}
	
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public void beforePhaseEliminarSemana(PhaseEvent event) {
    //Verificar que el usuario tiene permisos para acceder a esta página
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_PLANIFICADOR_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    //Establecer el título de la pantalla
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("eliminar_disponibilidades_semana"));
		}
	}
	
  public void beforePhaseEliminarPeriodo(PhaseEvent event) {
    //Verificar que el usuario tiene permisos para acceder a esta página
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_PLANIFICADOR_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    //Establecer el título de la pantalla
    if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
      sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("eliminar_disponibilidades_periodo"));
    }
  }
	
  //==============================================================================================
  // Eliminación por semana
  
  private Date semana;
  private List<List<Object>> horariosSemanales;
  
	public Date getSemana() {
		return semana;
	}
	public void setSemana(Date semana) {
		this.semana = semana;
	}
	
	public void consultarSemana(ActionEvent event) {
		limpiarMensajesError();
		horariosSemanales = null;
		if(semana==null) {
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), "form:semana");
		}else if(Utiles.esFechaInvalida(semana)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_invalida"), "form:semana");
    }else {
			try {
	      VentanaDeTiempo ventanaSemana = obtenerSemana(semana);
				List<DisponibilidadReserva> disponibilidades = disponibilidadesEJB.obtenerDisponibilidadesReservas(sessionMBean.getRecursoMarcado(), ventanaSemana);
				horariosSemanales = armarHorariosSemanales(disponibilidades);
			} catch (Exception e) {
				addErrorMessage(e);
			}
		}
	}
	
	private VentanaDeTiempo obtenerSemana(Date dia) {
		Calendar c = Calendar.getInstance();
		c.setTime(dia);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		Date semanaInicio = Utiles.time2InicioDelDia(c.getTime());
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
		Date semanaFin = Utiles.time2FinDelDia(c.getTime());
		VentanaDeTiempo v = new VentanaDeTiempo();
		v.setFechaInicial(semanaInicio);
		v.setFechaFinal(semanaFin);
		return v;
	}
	
	public List<List<Object>> getHorariosSemanales() {
		return horariosSemanales;
	}
	
	public List<Dia> getDiasDeLaSemana() {
		List<Dia> dias = new ArrayList<Dia>();
		for (Dia d : Dia.values()) {
			dias.add(d);
		}
		return dias;
	}
	
	public void eliminar(ActionEvent event) {
		if (semana == null) {
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), "form:semana");
			return;
		}else if (Utiles.esFechaInvalida(semana)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_invalida"), "form:semana");
      return;
    }
		VentanaDeTiempo vSemana = obtenerSemana(semana);
		try {
			int cantEliminadas = disponibilidadesEJB.eliminarDisponibilidades(sessionMBean.getRecursoMarcado(), vSemana);
			if(cantEliminadas > 0) {
			  addInfoMessage(sessionMBean.getTextos().get("disponibilidades_eliminadas"));
			}else {
        addAdvertenciaMessage(sessionMBean.getTextos().get("no_hay_disponibilidades_para_el_periodo_especificado"));
			}
		} catch (Exception ex) {
			addErrorMessage(ex);
		}
	}
	
	private List<List<Object>> armarHorariosSemanales(List<DisponibilidadReserva> disponibilidades) {
		//Esta lista de horas representa la primer columna de la matriz de horarios semanales
		//y por lo tanto determina la cantidad de filas de la matriz.
		List<Date> horas = obtenerHorasInicioFin(disponibilidades);
		//Matriz de horarios semanales inicializada con cada hora por fila
		List<List<Object>> matrizHorarios = armarHorariosSemanalesVacios(horas);
		//Se completan las celdas de la matriz configurando para cada <hora,dia> los valores respectivos
		//del objeto CeldaDia de forma tal que represente correctamente la disponibilidad que cae en dicho intervalo de tiempo.
		llenarHorariosSemanales(matrizHorarios, disponibilidades);
		return matrizHorarios;
	}

	//Obtiene todas las horas de inicio y fin ordenadas y sin repetidos para la lista de disponibilidades.
	private List<Date> obtenerHorasInicioFin(List<DisponibilidadReserva> disponibilidades) {
		Date hoy = Calendar.getInstance().getTime();
		SortedSet<Date> horas = new TreeSet<Date>();
		for (DisponibilidadReserva dr : disponibilidades) {
			Date i = setDia(dr.getHoraInicio(), hoy);
			if (! horas.contains(i)) {
				horas.add(i);
			}
			Date f = setDia(dr.getHoraFin(), hoy);
			if (! horas.contains(f)) {
				horas.add(f);
			}
		}
		List<Date> listaHoras = new ArrayList<Date>(horas.size());
		for (Date h : horas) {
			listaHoras.add(h);
		}
		return listaHoras;
	}
	
	//Crea la matriz de horas x diasDeLaSemana con tantas filas como horas y 7 columnas para cada dia.
	//En la primer columna incluye las horas respectivas
	private List<List<Object>> armarHorariosSemanalesVacios(List<Date> horas) {
		List<List<Object>> matriz = new ArrayList<List<Object>>(horas.size());
		for (Date h : horas) {
			List<Object> horarioSemanal = new ArrayList<Object>(8);
			horarioSemanal.add(h);
			for (int i = 0; i < 7; i++) {
				horarioSemanal.add(new CeldaDia());
			}
			matriz.add(horarioSemanal);
		}
		return matriz;
	}
	
	//Para cada celda <hora,dia> de la matriz (CeldaDia) se configura el objeto CeldaDia respectivo
	//de forma tal que represente correctamente la disponibilidad que cae en dicho intervalo de tiempo.
	private void llenarHorariosSemanales(List<List<Object>> horariosSemanales, List<DisponibilidadReserva> disponibilidades) {
		//Armo indice por hora para acceder orden 1 a las filas de la matriz
		Map<Date, Integer> filaIndice = new HashMap<Date, Integer>();
		for (int i = 0; i < horariosSemanales.size(); i++) {
			filaIndice.put((Date)horariosSemanales.get(i).get(0), i);
		}
		//Armo indice por dia para acceder orden 1 a las columnas de la matriz
		//Comienza en 1 pues la primer columna son las horas y el resto son los dias de lunes a domingo
		Map<Integer, Integer> columnaIndice = new HashMap<Integer, Integer>();
		columnaIndice.put(Calendar.MONDAY, 1);
		columnaIndice.put(Calendar.TUESDAY, 2);
		columnaIndice.put(Calendar.WEDNESDAY, 3);
		columnaIndice.put(Calendar.THURSDAY, 4);
		columnaIndice.put(Calendar.FRIDAY, 5);
		columnaIndice.put(Calendar.SATURDAY, 6);
		columnaIndice.put(Calendar.SUNDAY, 7);
		Date dia = null;
		if (! horariosSemanales.isEmpty()) {
			dia = (Date) horariosSemanales.get(0).get(0);
		}
		boolean esPar = false;
		//Para cada disponibilidad lleno las celdas que correspondan
		for (DisponibilidadReserva d : disponibilidades) {
			//Calculo el rango de filas
			Date horaI = setDia(d.getHoraInicio(), dia);
			Date horaF = setDia(d.getHoraFin(), dia);
			Integer filaI = filaIndice.get(horaI);
			Integer filaF = filaIndice.get(horaF) - 1; //La celda de horaFin no pertenece a esta disponibilidad
			//Calculo la columna
			Calendar cal = Calendar.getInstance();
			cal.setTime(d.getFecha());
			Integer columna = columnaIndice.get(cal.get(Calendar.DAY_OF_WEEK));
			for(int fila = filaI; fila <= filaF; fila++) {
				CeldaDia celda = (CeldaDia)horariosSemanales.get(fila).get(columna);
				celda.setEsGrupo(true);
				celda.setEsGrupoPar(esPar);
				if (fila == filaI) {
					celda.setPrimera(true);
					celda.setContenido(d.getCupo().toString());
				}
				if (fila == filaF) {
					celda.setUltima(true);
					esPar = ! esPar; //Al cambiar de grupo, cambio el estado par/impar
				}
			}
		}
	}

	private Date setDia(Date hora, Date dia) {
		Calendar calHora = Calendar.getInstance();
		calHora.setTime(hora);
		Calendar calDia = Calendar.getInstance();
		calDia.setTime(dia);
		calHora.set(Calendar.YEAR, calDia.get(Calendar.YEAR));
		calHora.set(Calendar.DAY_OF_YEAR, calDia.get(Calendar.DAY_OF_YEAR));
		return calHora.getTime();
	}
	
  //==============================================================================================
  // Eliminación por período
	
  private Date fechaDesde;
  private Date fechaHasta;
  private RowList<DisponibilidadReserva> cuposPorDia;
	
  public Date getFechaDesde() {
    return fechaDesde;
  }

  public void setFechaDesde(Date fechaDesde) {
    this.fechaDesde = fechaDesde;
  }

  public Date getFechaHasta() {
    return fechaHasta;
  }

  public void setFechaHasta(Date fechaHasta) {
    this.fechaHasta = fechaHasta;
  }

  public RowList<DisponibilidadReserva> getCuposPorDia() {
    return cuposPorDia;
  }
  
  public void setCuposPorDia(RowList<DisponibilidadReserva> cuposPorDia) {
    this.cuposPorDia = cuposPorDia;
  }
  
  public void obtenerCuposPeriodo(ActionEvent e){
    limpiarMensajesError();
    boolean hayErrores = false;
    if (fechaDesde == null) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "form:Fdesde");
      hayErrores = true;
    }else if(Utiles.esFechaInvalida(fechaDesde)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_invalida"), "form:Fdesde");
      hayErrores = true;
    }
    if (fechaHasta == null) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), "form:Fhasta");
      hayErrores = true;
    }else if(Utiles.esFechaInvalida(fechaHasta)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_invalida"), "form:Fhasta");
      hayErrores = true;
    }
    if(fechaDesde!=null && !Utiles.esFechaInvalida(fechaDesde) && fechaHasta!=null && !Utiles.esFechaInvalida(fechaHasta)) {
      if(fechaDesde.after(fechaHasta)) {
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "form:Fdesde", "form:Fhasta");
      }
    }
    if(hayErrores) {
      setCuposPorDia(null);
      return;
    }
    VentanaDeTiempo ventana = new VentanaDeTiempo();
    ventana.setFechaInicial(Utiles.time2InicioDelDia(fechaDesde));
    ventana.setFechaFinal(Utiles.time2FinDelDia(fechaHasta));
    setCuposPorDia(obtenerCuposPeriodo(ventana));
  }
  
  private RowList<DisponibilidadReserva> obtenerCuposPeriodo(VentanaDeTiempo ventana){
    RowList<DisponibilidadReserva> cuposAux = null;
    try{
      if (sessionMBean.getRecursoMarcado() != null){
        List<DisponibilidadReserva> dispsRess = disponibilidadesEJB.obtenerDisponibilidadesReservas(sessionMBean.getRecursoMarcado(), ventana);
        cuposAux= new RowList<DisponibilidadReserva>(dispsRess);
      } else{
        addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"));
      }
    }catch (Exception ex) {
        addErrorMessage(ex, MSG_ID);
    }
    return cuposAux;
  }
  
  public void eliminarDisponibilidadesPeriodo() {
    limpiarMensajesError();
    boolean hayErrores = false;
    if(fechaDesde==null) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), "form:Fdesde");
      hayErrores = true;
    }else if(Utiles.esFechaInvalida(fechaDesde)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_invalida"), "form:Fdesde");
      hayErrores = true;
    }
    if (fechaHasta == null) {
      hayErrores = true;
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), "form:Fhasta");
    }else if(Utiles.esFechaInvalida(fechaHasta)) {
      addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_invalida"), "form:Fhasta");
      hayErrores = true;
    }
    if(fechaDesde!=null && !Utiles.esFechaInvalida(fechaDesde) && fechaHasta!=null && !Utiles.esFechaInvalida(fechaHasta)) {
      if(fechaDesde.after(fechaHasta)) {
        addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), "form:Fdesde", "form:Fhasta");
      }
    }
    if(hayErrores) {
      return;
    }
    try {
      VentanaDeTiempo ventana = new VentanaDeTiempo();
      ventana.setFechaInicial(Utiles.time2InicioDelDia(fechaDesde));
      ventana.setFechaFinal(Utiles.time2FinDelDia(fechaHasta));
      int cantEliminadas = disponibilidadesEJB.eliminarDisponibilidades(sessionMBean.getRecursoMarcado(), ventana);
      if(cantEliminadas > 0) {
        addInfoMessage(sessionMBean.getTextos().get("disponibilidades_eliminadas"));
      }else {
        addAdvertenciaMessage(sessionMBean.getTextos().get("no_hay_disponibilidades_para_el_periodo_especificado"));
      }
    } catch (Exception ex) {
      addErrorMessage(ex);
    }
  }
	
}
