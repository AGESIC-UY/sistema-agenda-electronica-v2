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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.CupoPorDia;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

import org.primefaces.component.datatable.DataTable;


public class DisponibilidadMBean extends BaseMBean {
	public static final String MSG_ID = "pantalla";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/DisponibilidadesBean!uy.gub.imm.sae.business.ejb.facade.DisponibilidadesRemote")
	private Disponibilidades disponibilidadesEJB;

	public SessionMBean sessionMBean;
	public DispSessionMBean dispSessionMBean;
	public DisponibilidadReserva dispAux = new DisponibilidadReserva();
	
	//Tabla asociada a tabla en pantalla para poder saber en que d�a se posiciona. 
	private DataTable cuposDataTable;
	
	private DataTable tablaDispMatutina;
	private DataTable tablaDispVespertina;

	private DataTable tablaDispMatutinaModif;
	private DataTable tablaDispVespertinaModif;
	private int tipoOperacion;
	private String valor;
	private boolean selectAllMatutino;
	private boolean selectAllVespertino;
	private String horasInicio;
	
	@PostConstruct
	public void initDisponibilidad(){
		
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

	public DataTable getCuposDataTable() {
		return cuposDataTable;
	}

	public void setCuposDataTable(DataTable cuposDataTable) {
		this.cuposDataTable = cuposDataTable;
	}

	public void obtenerCuposCons(ActionEvent e){
		VentanaDeTiempo ventana = new VentanaDeTiempo();
		if (dispSessionMBean.getFechaDesde() != null) {
			//Se setea hora 00:00:00
			ventana.setFechaInicial(Utiles.time2InicioDelDia(dispSessionMBean.getFechaDesde()));
			if (dispSessionMBean.getFechaHasta()== null) {
				try {
				  dispSessionMBean.setFechaHasta(disponibilidadesEJB.ultFechaGenerada(sessionMBean.getRecursoMarcado()));
				}	catch (Exception ex){
					addErrorMessage(sessionMBean.getTextos().get("no_se_pudo_obtener_la_ultima_fecha_generada"), MSG_ID);
				}
			}
			if (dispSessionMBean.getFechaHasta() != null){
  			ventana.setFechaFinal(Utiles.time2FinDelDia(dispSessionMBean.getFechaHasta()));
  			dispSessionMBean.setCuposPorDia(obtenerCupos(ventana));
			}
		} else{
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), MSG_ID);
		}
	}
	
	public void obtenerCuposModif(ActionEvent e){
		limpiarMensajesError();
		VentanaDeTiempo v = new VentanaDeTiempo();
		if (dispSessionMBean.getFechaModifCupo() != null ) {
			v.setFechaInicial(Utiles.time2InicioDelDia(dispSessionMBean.getFechaModifCupo()) );
			v.setFechaFinal(Utiles.time2FinDelDia(dispSessionMBean.getFechaModifCupo()));
			dispSessionMBean.setDisponibilidadesDelDiaMatutinaModif(null);
			dispSessionMBean.setDisponibilidadesDelDiaVespertinaModif(null) ;
			configurarDisponibilidadesDelDiaModif();
		} else{
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), "form:fecha");
		}
	}
	
	private RowList<CupoPorDia> obtenerCupos(VentanaDeTiempo ventana){
		RowList<CupoPorDia> cuposAux = null;
		try{
			if (sessionMBean.getRecursoMarcado() != null){
				if (ventana.getFechaInicial() != null && ventana.getFechaFinal() != null && ventana.getFechaInicial().compareTo(ventana.getFechaFinal()) <= 0 ) {
				  //Obtener los cupos para el período indicado por la ventana
					List<Integer> cupos = agendarReservasEJB.obtenerCuposPorDia(sessionMBean.getRecursoMarcado(), ventana, sessionMBean.getTimeZone());
					Calendar fecha = Calendar.getInstance();
					fecha.setTime(ventana.getFechaInicial());
					Calendar fechaFin = Calendar.getInstance();
					fechaFin.setTime(ventana.getFechaFinal());
					int i = 0;
					List<CupoPorDia> listaCupos = new ArrayList<CupoPorDia>();
					//Iterar por cada día y determinar cuántos cupos tiene
					//Si para un día hay disponibilidades pero no hay cupos significa que ya pasó la hora de la última disponibilidad (pero igual hay que mostrar el día)
					while ( !fecha.after( fechaFin ) ){
					  boolean hayDisponFecha = disponibilidadesEJB.hayDisponibilidadesFecha(sessionMBean.getRecursoMarcado(), fecha.getTime());
						if (cupos.get(i) != -1 || hayDisponFecha){
							CupoPorDia cupoPorDia = new CupoPorDia();
							cupoPorDia.setDia(fecha.getTime());
							if (cupos.get(i) == -1) {
								cupoPorDia.setCupoDisponible(0);
							} else {
							  cupoPorDia.setCupoDisponible(cupos.get(i));
							}
							listaCupos.add(cupoPorDia);
						}
						i++;
						fecha.add(Calendar.DAY_OF_MONTH, 1);
					}
					cuposAux= new RowList<CupoPorDia>(listaCupos);
				} else{
					if(ventana.getFechaInicial() == null) {
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"));
					}
					if(ventana.getFechaFinal() == null) {
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"));
					}
					if (ventana.getFechaInicial() != null && ventana.getFechaFinal() != null && ventana.getFechaInicial().compareTo(ventana.getFechaFinal()) > 0){
						addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"));
					}
				}
			} else{
				addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"));
			}
		}catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
		}
		return cuposAux;
	}

	public DispSessionMBean getDispSessionMBean() {
		return dispSessionMBean;
	}

	public void setDispSessionMBean(DispSessionMBean dispSessionMBean) {
		this.dispSessionMBean = dispSessionMBean;
	}

	/*
	----------------------------------------------------------------------------
	A partir de acá estoy probando disponibilidades en la mañana y la tarde
	*/

	public RowList<DisponibilidadReserva> getDisponibilidadesMatutina() {
		return dispSessionMBean.getDisponibilidadesDelDiaMatutina();
	}

	public RowList<DisponibilidadReserva> getDisponibilidadesVespertina() {
		return dispSessionMBean.getDisponibilidadesDelDiaVespertina();
	}

	public DataTable getTablaDispMatutina() {
		return tablaDispMatutina;
	}

	public void setTablaDispMatutina(DataTable tablaDispMatutina) {
		this.tablaDispMatutina = tablaDispMatutina;
	}

	public DataTable getTablaDispVespertina() {
		return tablaDispVespertina;
	}

	public void setTablaDispVespertina(DataTable tablaDispVespertina) {
		this.tablaDispVespertina = tablaDispVespertina;
	}

	@SuppressWarnings("unchecked")
	public String configurarDisponibilidadesDelDia() {

		List<DisponibilidadReserva> dispMatutinas   = new ArrayList<DisponibilidadReserva>();
		List<DisponibilidadReserva> dispVespertinas = new ArrayList<DisponibilidadReserva>();

		//Determinar la fecha seleccionada
		CupoPorDia cupoPorDia = ((Row<CupoPorDia>) this.getCuposDataTable().getRowData()).getData();
		if (cupoPorDia != null) {
		  //Armar la ventana de tiempo con solo un día
			dispSessionMBean.setFechaActual(cupoPorDia.getDia());
			VentanaDeTiempo ventana = new VentanaDeTiempo();
			ventana.setFechaInicial(Utiles.time2InicioDelDia(dispSessionMBean.getFechaActual()));
			ventana.setFechaFinal(Utiles.time2FinDelDia(dispSessionMBean.getFechaActual()));
			try {
			  //Determinar las disponibilidades para la ventana (día actual)
				List<DisponibilidadReserva> lista = disponibilidadesEJB.obtenerDisponibilidadesReservas(sessionMBean.getRecursoMarcado(), ventana);
				for (DisponibilidadReserva d : lista) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(d.getHoraInicio());
					if (cal.get(Calendar.AM_PM) == Calendar.AM) {
						dispMatutinas.add(d);
					} else {
						dispVespertinas.add(d);
					}
				}
		    dispSessionMBean.setDisponibilidadesDelDiaMatutina(new RowList<DisponibilidadReserva>(dispMatutinas));
		    dispSessionMBean.setDisponibilidadesDelDiaVespertina(new RowList<DisponibilidadReserva>(dispVespertinas));
		    return "consultarPorDia";
			} catch (Exception e) { 
				addErrorMessage(e);
				return null;
			}
		}	else {
			return null;
		}

	}

	public void configurarDisponibilidadesDelDiaModif() {

		List<DisponibilidadReserva> dispMatutinas   = new ArrayList<DisponibilidadReserva>();
		List<DisponibilidadReserva> dispVespertinas = new ArrayList<DisponibilidadReserva>();

		VentanaDeTiempo ventana = new VentanaDeTiempo();
		ventana.setFechaInicial(Utiles.time2InicioDelDia(dispSessionMBean.getFechaModifCupo()));
		ventana.setFechaFinal(Utiles.time2FinDelDia(dispSessionMBean.getFechaModifCupo()));
			
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
		
		
		dispSessionMBean.setDisponibilidadesDelDiaMatutinaModif(new RowList<DisponibilidadReserva>(dispMatutinas));
		dispSessionMBean.setDisponibilidadesDelDiaVespertinaModif(new RowList<DisponibilidadReserva>(dispVespertinas));
	}

	public DataTable getTablaDispMatutinaModif() {
		return tablaDispMatutinaModif;
	}

	public void setTablaDispMatutinaModif(DataTable tablaDispMatutinaModif) {
		this.tablaDispMatutinaModif = tablaDispMatutinaModif;
	}

	public DataTable getTablaDispVespertinaModif() {
		return tablaDispVespertinaModif;
	}

	public void setTablaDispVespertinaModif(DataTable tablaDispVespertinaModif) {
		this.tablaDispVespertinaModif = tablaDispVespertinaModif;
	}	
	
	public void beforePhaseConsultar (PhaseEvent event) {
	  //Verificar que el usuario tiene permisos para acceder a esta página
	  if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_PLANIFICADOR_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
	  }
	  //Establecer el título de la pantalla
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("consultar_disponibilidades"));
		}
	}
	
	public void beforePhaseModifCupo (PhaseEvent event) {
    //Verificar que el usuario tiene permisos para acceder a esta página
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_PLANIFICADOR_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    //Establecer el título de la pantalla
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_cupos"));
		}
	}
	
	public void beforePhaseConsultarXdia (PhaseEvent event) {
    //Verificar que el usuario tiene permisos para acceder a esta página
    if(!sessionMBean.tieneRoles(new String[]{"RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_PLANIFICADOR_X_RECURSO"})) {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
    }
    //Establecer el título de la pantalla
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("disponibilidades"));
		}
	}

	@SuppressWarnings("unchecked")
	public void seleccionarCupoMat(ActionEvent e) {
		dispSessionMBean.setDispSeleccionado(null);
		DisponibilidadReserva dr = ((Row<DisponibilidadReserva>)this.getTablaDispMatutinaModif().getRowData()).getData();
  	
		if (dr != null) {
			dispSessionMBean.setDispSeleccionado(dr); 
			dispAux.setCupo(dr.getCupo());
			dispAux.setHoraInicio(dr.getHoraInicio());
			dispSessionMBean.setModificarTodos(false);
		}
	}

	@SuppressWarnings("unchecked")
	public void seleccionarCupoVesp(ActionEvent e) {
		dispSessionMBean.setDispSeleccionado(null);
		DisponibilidadReserva dr = ((Row<DisponibilidadReserva>)this.getTablaDispVespertinaModif().getRowData()).getData();
  	
		if (dr != null) {
			dispSessionMBean.setDispSeleccionado(dr); 
			dispAux.setCupo(dr.getCupo());
			dispAux.setHoraInicio(dr.getHoraInicio());
			dispSessionMBean.setModificarTodos(false);
		}
	}

	public void cancelarModifDisp(ActionEvent event) {
		VentanaDeTiempo v = new VentanaDeTiempo();
		if (dispSessionMBean.getFechaModifCupo() != null ) {
			v.setFechaInicial(Utiles.time2InicioDelDia(dispSessionMBean.getFechaModifCupo()) );
			v.setFechaFinal(Utiles.time2FinDelDia(dispSessionMBean.getFechaModifCupo()));
			dispSessionMBean.setDisponibilidadesDelDiaMatutina(null);
			dispSessionMBean.setDisponibilidadesDelDiaVespertina(null) ;
			obtenerCupos(v);
			configurarDisponibilidadesDelDiaModif();
		} else{
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_es_obligatoria"), MSG_ID);
		}
		this.selectAllMatutino = false;
		this.selectAllVespertino = false;
		this.horasInicio = null;
		this.tipoOperacion = 1;
		this.valor = "0";
		dispSessionMBean.setModificarTodos(false);
		dispSessionMBean.setDispSeleccionado(null);
	}

	public void guardarModifDisp(ActionEvent event) {
		
		limpiarMensajesError();
		
		int valorCupo = 0;

		if(this.valor == null || this.valor.trim().isEmpty()) {
			addErrorMessage(sessionMBean.getTextos().get("el_valor_es_obligatorio"), "form:valor");
			return;
		}else {
			try {
				valorCupo = Integer.valueOf(this.valor).intValue();
			} catch (Exception e) {
				addErrorMessage(sessionMBean.getTextos().get("el_valor_para_el_campo_cantidad_de_cupos_debe_ser_numerico"), "form:valor");
				return;
			}
			if(valorCupo<0) {
				addErrorMessage(sessionMBean.getTextos().get("no_se_permiten_valores_negativos"), "form:valor");
				return;
			}
			if ((this.tipoOperacion==2)&&(valorCupo==0)) {
				addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_a_disminuir_no_puede_ser_cero"), "form:valor");
				return;
			}else if ((this.tipoOperacion==1)&&(valorCupo==0)) {
				addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_a_aumentar_no_puede_ser_cero"), "form:valor");	
				return;
			}
		}
		
		RowList<DisponibilidadReserva> listDispReserva = new RowList<>(); 
		listDispReserva.addAll(dispSessionMBean.getDisponibilidadesDelDiaMatutinaModif());
		listDispReserva.addAll(dispSessionMBean.getDisponibilidadesDelDiaVespertinaModif());

		for (Row<DisponibilidadReserva> row : listDispReserva) {
			if(row.getData().isSeleccionado()) {
				int cupo = row.getData().getCupo();
				if(this.tipoOperacion==1) {
				  //Aumentar valor
					cupo = cupo+valorCupo;
				}else if(this.tipoOperacion==2) {
				  //Disminuir valor
					cupo = cupo-valorCupo;
					if(cupo<0) {
						cupo = 0;
					}
				}else {
				  //Establecer valor
					cupo = valorCupo;
				}
				Disponibilidad disp = new Disponibilidad();
				disp.setId(row.getData().getId());
				disp.setCupo(cupo);
				
				if (dispSessionMBean.getModificarTodos()) {
					try {
						List<String> advertencias = disponibilidadesEJB.modificarCupoPeriodoValorOperacion(disp, sessionMBean.getTimeZone(), valorCupo, this.tipoOperacion, dispSessionMBean.getDiasAplicar());
						for(String advertencia : advertencias) {
							addAdvertenciaMessage(sessionMBean.getTextos().get("para_diahora_el_cupo_se_modifico_parcialmente_porque_hay_mas_reservas").replace("{diahora}",  advertencia), MSG_ID);
						}
					} catch (Exception e) {
		 				addErrorMessage(e, MSG_ID);
		 				return;
		 			}					
				}else {
					try {
						Integer nuevoCupo = disponibilidadesEJB.modificarCupoDeDisponibilidad(disp);
						if(nuevoCupo!=null && !nuevoCupo.equals(disp.getCupo())) {
							addAdvertenciaMessage(sessionMBean.getTextos().get("el_cupo_se_modifico_parcialmente_porque_hay_mas_reservas"), MSG_ID);
						}
					} catch (Exception e) {
		 				addErrorMessage(e, MSG_ID);
		 				return;
		 			}
				}
			}
		}
		
		VentanaDeTiempo v = new VentanaDeTiempo();
		//Se setea hora 00:00:00
		v.setFechaInicial(Utiles.time2InicioDelDia(dispSessionMBean.getFechaModifCupo()) );
		v.setFechaFinal(Utiles.time2FinDelDia(dispSessionMBean.getFechaModifCupo()));
		dispSessionMBean.setDisponibilidadesDelDiaMatutina(null);
		dispSessionMBean.setDisponibilidadesDelDiaVespertina(null) ;
		obtenerCupos(v);
		configurarDisponibilidadesDelDiaModif();
		dispSessionMBean.setModificarTodos(false);
		addInfoMessage(sessionMBean.getTextos().get("disponibilidades_modificadas"), MSG_ID);
		this.selectAllVespertino = false;
		this.selectAllMatutino	= false;
		this.horasInicio = null;
		this.tipoOperacion = 1;
		this.valor = "0";
		dispSessionMBean.setModificarTodos(false);
	}
	
	public void controlSelectDisponibilidades(ActionEvent event)
	{
		limpiarMensajesError();
		
		horasInicio = null;
		RowList<DisponibilidadReserva> listDispReservaVespertina = dispSessionMBean.getDisponibilidadesDelDiaVespertinaModif();
		RowList<DisponibilidadReserva> listDispReservaMatutina = dispSessionMBean.getDisponibilidadesDelDiaMatutinaModif();
		boolean error = true;
		SimpleDateFormat format = new SimpleDateFormat(sessionMBean.getFormatoHora());
		GregorianCalendar cal = new GregorianCalendar();
		if (listDispReservaMatutina != null) {
			for (Row<DisponibilidadReserva> row : listDispReservaMatutina) {
				if (row.getData().isSeleccionado()) {
					error = false;
					if (horasInicio==null) {
						cal.setTime(row.getData().getHoraInicio());   
				    horasInicio = format.format(cal.getTime());
					}else {
						cal.setTime(row.getData().getHoraInicio());   
						horasInicio = horasInicio +", "+format.format(cal.getTime());
					}
					
				}
			}
			if(listDispReservaVespertina!=null) {
				for (Row<DisponibilidadReserva> row : listDispReservaVespertina) {
					if (row.getData().isSeleccionado()) {
						error = false;
						if (horasInicio==null) {
							cal.setTime(row.getData().getHoraInicio());   
							horasInicio = format.format(cal.getTime());
						}else {
							cal.setTime(row.getData().getHoraInicio());   
							horasInicio = horasInicio+", "+format.format(cal.getTime());
						}
					}
				}
			}
			
		}else if(listDispReservaVespertina!=null) {
			for (Row<DisponibilidadReserva> row : listDispReservaVespertina) {
				if (row.getData().isSeleccionado()) {
					error = false;
					if (horasInicio==null) {
						cal.setTime(row.getData().getHoraInicio());   
				        horasInicio = format.format(cal.getTime());
					}else {
						cal.setTime(row.getData().getHoraInicio());   
						horasInicio = format.format(cal.getTime()) +","+ horasInicio;
					}
				}
			}
		}
		
		if(error) {
			addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_al_menos_una_disponibilidad"), MSG_ID);
		}
		
	}

	public void seleccionarTodosMatutino() {
		RowList<DisponibilidadReserva> listDispReservaMatutina = dispSessionMBean.getDisponibilidadesDelDiaMatutinaModif();
		if (this.selectAllMatutino) {
			for (Row<DisponibilidadReserva> row : listDispReservaMatutina) {
				row.getData().setSeleccionado(true);
			}
		}else {
			for (Row<DisponibilidadReserva> row : listDispReservaMatutina) {
				row.getData().setSeleccionado(false);
			}
		}
	}
	
	public void seleccionarTodosVespertino() {
		RowList<DisponibilidadReserva> listDispReservaVespertina = dispSessionMBean.getDisponibilidadesDelDiaVespertinaModif();
		if (this.selectAllVespertino) {
			for (Row<DisponibilidadReserva> row : listDispReservaVespertina) {
				row.getData().setSeleccionado(true);
			}
		}else {
			for (Row<DisponibilidadReserva> row : listDispReservaVespertina) {
				row.getData().setSeleccionado(false);
			}
		}
	}
	
	public int getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(int tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}


	public boolean isSelectAllMatutino() {
		return selectAllMatutino;
	}

	public void setSelectAllMatutino(boolean selectAllMatutino) {
		this.selectAllMatutino = selectAllMatutino;
	}

	public boolean isSelectAllVespertino() {
		return selectAllVespertino;
	}

	public void setSelectAllVespertino(boolean selectAllVespertino) {
		this.selectAllVespertino = selectAllVespertino;
	}

	public String getHorasInicio() {
		return horasInicio;
	}

	public void setHorasInicio(String horasInicio) {
		this.horasInicio = horasInicio;
	}

}
