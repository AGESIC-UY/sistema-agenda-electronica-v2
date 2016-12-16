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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import uy.gub.imm.opencsv.ext.entity.CommonLabelValueImpl;
import uy.gub.imm.opencsv.ext.entity.LabelValue;
import uy.gub.imm.opencsv.ext.entity.TableCellValue;
import uy.gub.imm.opencsv.ext.file.StandardCSVFile;
import uy.gub.imm.opencsv.ext.printer.CSVWebFilePrinter;
import uy.gub.imm.sae.business.dto.AtencionLLamadaReporteDT;
import uy.gub.imm.sae.business.dto.AtencionReporteDT;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.Agendas;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.reporte.Columna;

public class ReporteMBean extends BaseMBean {

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/ConsultasBean!uy.gub.imm.sae.business.ejb.facade.ConsultasRemote")
	private Consultas consultaEJB;
	
	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
	private AgendaGeneral generalEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendasBean!uy.gub.imm.sae.business.ejb.facade.AgendasRemote")
	private Agendas agendasEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas usuariosEmpresasEJB;
	
	private Date fechaDesde;
	private Date fechaHasta;
	private Estado estado;
	private Boolean unaPaginaPorHora;
	private String estadoDescripcion;
	private List<SelectItem> estadosReserva =  new ArrayList<SelectItem>();
	private Estado estadoReservaSeleccionado;
	private boolean todasLasEmpresas;
	private SessionMBean sessionMBean;
	
	public static final String MSG_ID = "pantalla";
	
	static Logger logger = Logger.getLogger(ReporteMBean.class);
	
	@PostConstruct
	public void cargarDatos(){
		limpiarMensajesError();
		cargarListaEstados();
	}
	
	
	private void cargarListaEstados(){
		estadosReserva =  new ArrayList<SelectItem>();
	  Reserva r = new Reserva();
		for(Estado e: Estado.values()){
			if (! e.equals(Estado.P)){
				SelectItem s = new SelectItem();
				s.setValue(e);
				s.setLabel(r.getEstadoDescripcion(e));
				estadosReserva.add(s);
			}
		}
	}

	public String reporteReservaFecha() {
		
		limpiarMensajesError();
		
		boolean error = false;
		
		Agenda agendaMarcada = sessionMBean.getAgendaMarcada();
		Recurso recursoMarcado = sessionMBean.getRecursoMarcado();
		
		if (fechaDesde == null){
			error = true;
			addErrorMessage("la_fecha_de_inicio_es_obligatoria", "form:fechaDesde");
		}
		
		if (fechaHasta == null){
			error = true;
			addErrorMessage("la_fecha_de_fin_es_obligatoria", "form:fechaHasta");
		}
		
		if(fechaDesde!=null && fechaHasta!=null) {
			Calendar c1 =  new GregorianCalendar();
			c1.setTime(fechaDesde);
			Calendar c2 =  new GregorianCalendar();
			c2.setTime(fechaHasta);
			if(c1.after(c2)) {
				error = true;
				addErrorMessage("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio", "form:fechaDesde", "form:fechaHasta");
			}
		}
		
		if (estadoReservaSeleccionado == null){
			error = true;
			addErrorMessage("el_estado_es_obligatorio", "form:estado");
		}

		if (!error){
			VentanaDeTiempo periodo = new VentanaDeTiempo();
			periodo.setFechaInicial(fechaDesde);
			periodo.setFechaFinal(fechaHasta);
			try {
				List<Recurso> recursos = new ArrayList<Recurso>();
				if(recursoMarcado != null) {
					//Hay un recurso marcado, el reporte es para ese recurso
					recursos.add(recursoMarcado);
				}else if(agendaMarcada != null) {
					//No hay un recurso marcado pero sí una agenda, se hace para todos los recursos de esa agenda
				  recursos.addAll(generalEJB.consultarRecursos(agendaMarcada));
				}else {
					//Se hace para todos los recursos de todas las agendas de la empresa
					List<Agenda> agendas = agendasEJB.consultarAgendas();
					if(agendas != null) {
						for(Agenda agenda : agendas) {
						  recursos.addAll(generalEJB.consultarRecursos(agenda));
						}
					}
				}
				
				List<List<TableCellValue>> contenido = new ArrayList<List<TableCellValue>>();
				for(Recurso recurso : recursos) {
					//Definicion de los campos dinamicos del reporte
					List<Columna> defColumnas = new ArrayList<Columna>();
					List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso, sessionMBean.getTimeZone());
					for(AgrupacionDato grupo: agrupaciones) {
						for(DatoASolicitar campo: grupo.getDatosASolicitar()) {
							if (campo.getIncluirEnReporte()) {
								Columna col = new Columna();
								col.setId(campo.getNombre());
								col.setNombre(campo.getEtiqueta());
								col.setClase(String.class);
								col.setAncho(campo.getAnchoDespliegue());
								defColumnas.add(col);
							}
						}
					}
			    List<Estado> estados = new ArrayList<Estado>();
			    estados.add(estadoReservaSeleccionado);
					List<ReservaDTO> reservas = consultaEJB.consultarReservasPorPeriodoEstado(recurso, periodo, estados, null);
					List<List<TableCellValue>> contenido1 = armarContenido(recurso, reservas, agrupaciones);
					contenido.addAll(contenido1);
					
				}
				
				String[] defColPlanilla = {};
				LabelValue[] filtros = {
						new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_desde")+": ",Utiles.date2string(fechaDesde, Utiles.DIA)),
						new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_hasta")+": ", Utiles.date2string(fechaHasta, Utiles.DIA)),
						new CommonLabelValueImpl(sessionMBean.getTextos().get("estado")+": ", estadoReservaSeleccionado.getDescripcion() )
				};
				
        StandardCSVFile fileCSV = new StandardCSVFile(filtros, defColPlanilla, contenido); 
        
        String nombre = sessionMBean.getTextos().get("reporte_reservas");
        nombre = nombre.replace(" ", "_");
        
        CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, nombre);
        printer.print(); 
				
			} catch (Exception e1) {
				addErrorMessage(e1);
			}

		}
		return null;
	}
	
	public void reporteAsistenciaFecha(ActionEvent e) {
		
		limpiarMensajesError();
		
		boolean error = false;
		
		Agenda agendaMarcada = sessionMBean.getAgendaMarcada();
		Recurso recursoMarcado = sessionMBean.getRecursoMarcado();
		
		if (fechaDesde == null){
			error = true;
			addErrorMessage("la_fecha_de_inicio_es_obligatoria", "form:fechaDesde");
		}
		
		if (fechaHasta == null){
			error = true;
			addErrorMessage("la_fecha_de_fin_es_obligatoria", "form:fechaHasta");
		}
		
		if(fechaDesde!=null && fechaHasta!=null) {
			Calendar c1 =  new GregorianCalendar();
			c1.setTime(fechaDesde);
			Calendar c2 =  new GregorianCalendar();
			c2.setTime(fechaHasta);
			if(c1.after(c2)) {
				error = true;
				addErrorMessage("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio", "form:fechaDesde", "form:fechaHasta");
			}
		}

		estadoReservaSeleccionado=Estado.U;

		if (!error){
			VentanaDeTiempo periodo = new VentanaDeTiempo();
			periodo.setFechaInicial(fechaDesde);
			periodo.setFechaFinal(fechaHasta);
			
			try {
				String[] defColPlanilla = {};
				//Datos a desplegar en el reporte, en este casos las reservas por fecha y hora
				LabelValue[] filtros = {
						new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_desde")+": ",Utiles.date2string(fechaDesde, Utiles.DIA)),
						new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_hasta")+": ", Utiles.date2string(fechaHasta, Utiles.DIA))
				};

				List<Recurso> recursos = new ArrayList<Recurso>();
				if(recursoMarcado != null) {
					//Hay un recurso marcado, el reporte es para ese recurso
					recursos.add(recursoMarcado);
				}else if(agendaMarcada != null) {
					//No hay un recurso marcado pero sí una agenda, se hace para todos los recursos de esa agenda
				  recursos.addAll(generalEJB.consultarRecursos(agendaMarcada));
				}else {
					//Se hace para todos los recursos de todas las agendas de la empresa
					List<Agenda> agendas = agendasEJB.consultarAgendas();
					if(agendas != null) {
						for(Agenda agenda : agendas) {
						  recursos.addAll(generalEJB.consultarRecursos(agenda));
						}
					}
				}
				List<List<TableCellValue>> contenido = new ArrayList<List<TableCellValue>>();
				for(Recurso recurso : recursos) {
					List<ReservaDTO> reservas = consultaEJB.consultarReservasUsadasPeriodo(recurso, periodo, null);
					List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso, sessionMBean.getTimeZone());
					List<List<TableCellValue>> contenido1 = armarContenido(recurso, reservas, agrupaciones);
					contenido.addAll(contenido1);
				}
				
        StandardCSVFile fileCSV = new StandardCSVFile(filtros, defColPlanilla, contenido); 
        
        String nombre = sessionMBean.getTextos().get("reporte_asistencias");
        nombre = nombre.replace(" ", "_");
        
        CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, nombre);
        printer.print(); 
			} catch (Exception e1) {
				addErrorMessage(e1);
			}
		}
	}
	
	public void reporteAtencionPeriodo(ActionEvent e) {
		limpiarMensajesError();
		
		boolean error = false;
		
		if (fechaDesde == null){
			error = true;
			addErrorMessage("la_fecha_de_inicio_es_obligatoria", "form:fechaDesde");
		}
		
		if (fechaHasta == null){
			error = true;
			addErrorMessage("la_fecha_de_fin_es_obligatoria", "form:fechaHasta");
		}
	
		if(fechaDesde!=null && fechaHasta!=null) {
			Calendar c1 =  new GregorianCalendar();
			c1.setTime(fechaDesde);
			Calendar c2 =  new GregorianCalendar();
			c2.setTime(fechaHasta);
			if(c1.after(c2)) {
				error = true;
				addErrorMessage("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio", "form:fechaDesde", "form:fechaHasta");
			}
		}
		
		if(!error) {
			List<AtencionReporteDT> listAtencionReportDT = new ArrayList<AtencionReporteDT>();
			if (this.todasLasEmpresas) {
					Empresa empresaActual = sessionMBean.getEmpresaActual();
					try {
						for (Empresa emp : usuariosEmpresasEJB.consultarEmpresas()) {
							try {
								sessionMBean.seleccionarEmpresa(emp.getId());
								List<AtencionReporteDT> listAtencionReport = obtenerDatosReporteAtencionPeriodo(this.fechaDesde,this.fechaHasta);
								listAtencionReportDT.addAll(listAtencionReport);
							} catch (Exception e1) {
								logger.info("Empresa "+emp.getNombre()+" con esquema mal definida");
							}
						}
						
					} catch (ApplicationException e1) {
						e1.printStackTrace();
					}finally {
						sessionMBean.seleccionarEmpresa(empresaActual.getId());
					}
			}else {
				listAtencionReportDT = obtenerDatosReporteAtencionPeriodo(this.fechaDesde, this.fechaHasta);
			}
			
			List<List<TableCellValue>> contenido = new ArrayList<List<TableCellValue>>();
			for (AtencionReporteDT atencionReporteDT : listAtencionReportDT) {
				List<TableCellValue> filaDatos = new ArrayList<TableCellValue>();
				if(this.todasLasEmpresas)
				{
					filaDatos.add(new TableCellValue(atencionReporteDT.getNombEmpresa()));
				}
				filaDatos.add(new TableCellValue(atencionReporteDT.getNombAgenda()));
				filaDatos.add(new TableCellValue(atencionReporteDT.getNombFuncionario()));
				filaDatos.add(new TableCellValue(atencionReporteDT.getAsistencias()));
				filaDatos.add(new TableCellValue(atencionReporteDT.getInasistencias()));
				int total = atencionReporteDT.getAsistencias() + atencionReporteDT.getInasistencias();
				filaDatos.add(new TableCellValue(total));
				contenido.add(filaDatos);
			}
			
			LabelValue[] filtros = { 
			   new CommonLabelValueImpl("Fecha desde: ",Utiles.date2string(fechaDesde, Utiles.DIA)),
			   new CommonLabelValueImpl("Fecha hasta: ", Utiles.date2string(fechaHasta, Utiles.DIA))
			};
			StandardCSVFile fileCSV ;
			if (this.todasLasEmpresas) {
				String[]  cabezales = {sessionMBean.getTextos().get("empresa"), sessionMBean.getTextos().get("agenda"), sessionMBean.getTextos().get("funcionario"),
				    sessionMBean.getTextos().get("asistencias"), sessionMBean.getTextos().get("inasistencias"), sessionMBean.getTextos().get("atenciones")};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}else {
				String[]  cabezales = {sessionMBean.getTextos().get("agenda"), sessionMBean.getTextos().get("funcionario"),
				    sessionMBean.getTextos().get("asistencias"), sessionMBean.getTextos().get("inasistencias"), sessionMBean.getTextos().get("atenciones")};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}
						
      String nombre = sessionMBean.getTextos().get("reporte_atencion_funcionario");
      nombre = nombre.replace(" ", "_");
			
			
			CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, nombre);
      printer.print(); 
		}
	}
	
	private List<AtencionReporteDT> obtenerDatosReporteAtencionPeriodo(Date fechaDesde, Date fechaHasta) {
		List<Atencion> atenciones = consultaEJB.consultarTodasAtencionesPeriodo(fechaDesde,fechaHasta);
		Collections.sort(atenciones, new AtencionComparator());
		String AgendaProcesada = "";
		String FuncionarioProcesado = "";
		int asistencias = 0;
		int inasistencias = 0;
		List<AtencionReporteDT> listAtencionReport = new ArrayList<AtencionReporteDT>(); 
		for (Atencion atencion : atenciones) {
			Agenda a = atencion.getReserva().getDisponibilidades().get(0).getRecurso().getAgenda();
			if (!AgendaProcesada.equals(a.getNombre()) || !FuncionarioProcesado.equals(atencion.getFuncionario()) ) {
				int total = asistencias + inasistencias; 
				if(total != 0) {
					AtencionReporteDT itemAtencion;
					if(this.todasLasEmpresas) {
						itemAtencion = new AtencionReporteDT(sessionMBean.getEmpresaActual().getNombre(),AgendaProcesada, FuncionarioProcesado, asistencias, inasistencias);
					}else {
						itemAtencion = new AtencionReporteDT(null,AgendaProcesada, FuncionarioProcesado, asistencias, inasistencias);
					}
					listAtencionReport.add(itemAtencion);
				}
				//se inician contadores
				if(atencion.getAsistio()) {
					asistencias = 1;
					inasistencias = 0;
				}else {
					asistencias = 0;
					inasistencias = 1;
				}
				AgendaProcesada = a.getNombre();
				FuncionarioProcesado = atencion.getFuncionario();
			}else {
				if(atencion.getAsistio()) {
					asistencias++;
					
				}else {
					inasistencias++;
				}
			}
		}
		int total = asistencias + inasistencias; 
		if(total != 0) {
			AtencionReporteDT itemAtencion;
			if(this.todasLasEmpresas) {
				itemAtencion = new AtencionReporteDT(sessionMBean.getEmpresaActual().getNombre(),AgendaProcesada, FuncionarioProcesado, asistencias, inasistencias);
			}else {
				itemAtencion = new AtencionReporteDT(null,AgendaProcesada, FuncionarioProcesado, asistencias, inasistencias);
			}
			listAtencionReport.add(itemAtencion);
		}
		
		return listAtencionReport;
	}
	
	public void reporteTiemposAtencion() 	{
		
		limpiarMensajesError();
		
		boolean error = false;
		
		if (fechaDesde == null){
			error = true;
			addErrorMessage("la_fecha_de_inicio_es_obligatoria", "form:fechaDesde");
		}
		
		if (fechaHasta == null){
			error = true;
			addErrorMessage("la_fecha_de_fin_es_obligatoria", "form:fechaHasta");
		}
		
		if(fechaDesde!=null && fechaHasta!=null) {
			Calendar c1 =  new GregorianCalendar();
			c1.setTime(fechaDesde);
			Calendar c2 =  new GregorianCalendar();
			c2.setTime(fechaHasta);
			if(c1.after(c2)) {
				error = true;
				addErrorMessage("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio", "form:fechaDesde", "form:fechaHasta");
			}
		}
		
		if(!error){
			List<AtencionLLamadaReporteDT> llamadas = new ArrayList<AtencionLLamadaReporteDT>();
			if (this.todasLasEmpresas) {
					Empresa empresaActual = sessionMBean.getEmpresaActual();
					try {
						for (Empresa emp : usuariosEmpresasEJB.consultarEmpresas()) {
							try {
								sessionMBean.seleccionarEmpresa(emp.getId());
								List<AtencionLLamadaReporteDT> llamada	= consultaEJB.consultarLlamadasAtencionPeriodo(fechaDesde,fechaHasta);
								Collections.sort(llamada, new AtencionLlamadaReporteComparator());
								int i=0;
								while (i<llamada.size()) {
									llamada.get(i).setNombEmpresa(emp.getNombre());
									i++;
								}
								llamadas.addAll(llamada);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					} catch (ApplicationException e1) {
						e1.printStackTrace();
					}finally {
						sessionMBean.seleccionarEmpresa(empresaActual.getId());
					}
			}else {
				llamadas	= consultaEJB.consultarLlamadasAtencionPeriodo(fechaDesde,fechaHasta);
				Collections.sort(llamadas, new AtencionLlamadaReporteComparator());
			}
			
			List<List<TableCellValue>> contenido = new ArrayList<List<TableCellValue>>();
			for (AtencionLLamadaReporteDT atencionLlamadaReporteDT : llamadas) {
				List<TableCellValue> filaDatos = new ArrayList<TableCellValue>();
				if(this.todasLasEmpresas) {
					filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombEmpresa()));
				}
				filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombAgenda()));
				filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombRecurso()));
				filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getReservaId()));
        filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getTramiteNombre()));
				filaDatos.add(new TableCellValue(Utiles.date2string(atencionLlamadaReporteDT.getFechaHoraReserva(), Utiles.DIA)));
				filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getPuesto()));
				if(atencionLlamadaReporteDT.getNombFuncionario()!=null) {
					filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombFuncionario()));
				}else {
					filaDatos.add(new TableCellValue("---"));
				}
				if(atencionLlamadaReporteDT.getFechaHoraAtencion()!=null) {
					long tiempo = atencionLlamadaReporteDT.getFechaHoraAtencion().getTime() - atencionLlamadaReporteDT.getFechaHoraLlamada().getTime(); 
					tiempo = Math.abs(tiempo/(1000 * 60));
					filaDatos.add(new TableCellValue(String.valueOf(tiempo)));
				}else {
					filaDatos.add(new TableCellValue("---"));
				}
				
				if(atencionLlamadaReporteDT.getAtencion()!= null) {
					filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getAtencion()));
				}else {
					filaDatos.add(new TableCellValue(sessionMBean.getTextos().get("no_marcado")));
				}
				
				contenido.add(filaDatos);
			}
			
			LabelValue[] filtros = { 
	          		   new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_desde")+": ",Utiles.date2string(fechaDesde, Utiles.DIA)),
	          		   new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_hasta")+": ", Utiles.date2string(fechaHasta, Utiles.DIA))
	             };
			StandardCSVFile fileCSV ;
			if (this.todasLasEmpresas) {
				String[]  cabezales = {sessionMBean.getTextos().get("empresa"), sessionMBean.getTextos().get("tramite"), sessionMBean.getTextos().get("oficina"),
				    sessionMBean.getTextos().get("reserva"), sessionMBean.getTextos().get("tramite"), sessionMBean.getTextos().get("fecha"),
				    sessionMBean.getTextos().get("puesto"), sessionMBean.getTextos().get("funcionario"), sessionMBean.getTextos().get("tiempo_en_minutos"),
				    sessionMBean.getTextos().get("atencion")};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}else {
				String[]  cabezales = {sessionMBean.getTextos().get("tramite"), sessionMBean.getTextos().get("oficina"), sessionMBean.getTextos().get("reserva"),
				    sessionMBean.getTextos().get("tramite"), sessionMBean.getTextos().get("fecha"), sessionMBean.getTextos().get("puesto"), 
				    sessionMBean.getTextos().get("funcionario"), sessionMBean.getTextos().get("tiempo_en_minutos"), sessionMBean.getTextos().get("atencion")};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}
			
      String nombre = sessionMBean.getTextos().get("reporte_tiempo_atencion_funcionario");
      nombre = nombre.replace(" ", "_");
			
						
			CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, nombre);
			printer.print(); 
			
		}
	}
	
  public void reporteAtencionPresencialPeriodo(ActionEvent e) {
    
    limpiarMensajesError();
    
    boolean error = false;
    
    if (fechaDesde == null){
      error = true;
      addErrorMessage("la_fecha_de_inicio_es_obligatoria", "form:fechaDesde");
    }
    
    if (fechaHasta == null){
      error = true;
      addErrorMessage("la_fecha_de_fin_es_obligatoria", "form:fechaHasta");
    }
    
    if(fechaDesde!=null && fechaHasta!=null) {
      Calendar c1 =  new GregorianCalendar();
      c1.setTime(fechaDesde);
      Calendar c2 =  new GregorianCalendar();
      c2.setTime(fechaHasta);
      if(c1.after(c2)) {
        error = true;
        addErrorMessage("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio", "form:fechaDesde", "form:fechaHasta");
      }
    }
    Recurso recursoMarcado = sessionMBean.getRecursoMarcado();
    if(recursoMarcado == null) {
      error = true;
      addErrorMessage("debe_haber_un_recurso_seleccionado");
    }
    
    if (!error){
      
      try {
        //Datos a desplegar en el reporte, en este casos las reservas por fecha y hora
        LabelValue[] filtros = {
            new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_hasta")+": ", Utiles.date2string(fechaHasta, Utiles.DIA)),
            new CommonLabelValueImpl(sessionMBean.getTextos().get("fecha_desde")+": ", Utiles.date2string(fechaDesde, Utiles.DIA))
        };

        List<List<TableCellValue>> contenido = new ArrayList<List<TableCellValue>>();
        List<AtencionLLamadaReporteDT> llamadas = consultaEJB.consultarAtencionesPresencialesRecursoPeriodo(recursoMarcado, fechaDesde, fechaHasta);
        Collections.sort(llamadas, new AtencionLlamadaReporteComparator());
        for (AtencionLLamadaReporteDT llamada : llamadas) {
          List<TableCellValue> filaDatos = new ArrayList<TableCellValue>();
          filaDatos.add(new TableCellValue(llamada.getReservaId()));
          filaDatos.add(new TableCellValue(Utiles.date2string(llamada.getFechaHoraReserva(), Utiles.DIA)));
          filaDatos.add(new TableCellValue(llamada.getNumero()));
          filaDatos.add(new TableCellValue(llamada.getTramiteNombre()));
          if(llamada.getFechaHoraAtencion()!=null) {
            long tiempo = llamada.getFechaHoraAtencion().getTime() - llamada.getFechaHoraLlamada().getTime(); 
            tiempo = Math.abs(tiempo/(1000 * 60));
            filaDatos.add(new TableCellValue(String.valueOf(tiempo)));
          }else {
            filaDatos.add(new TableCellValue("---"));
          }
          if(llamada.getAtencion()!= null) {
            filaDatos.add(new TableCellValue(llamada.getAtencion()));
          }else {
            filaDatos.add(new TableCellValue(sessionMBean.getTextos().get("no_marcado")));
          }
          contenido.add(filaDatos);
        }
        
        String[] cabezales = {sessionMBean.getTextos().get("reserva"), sessionMBean.getTextos().get("fecha"), sessionMBean.getTextos().get("numero"),
            sessionMBean.getTextos().get("tramite"), sessionMBean.getTextos().get("tiempo_en_minutos"), sessionMBean.getTextos().get("atencion")};
        StandardCSVFile fileCSV = new StandardCSVFile(filtros, cabezales, contenido);
        
        String nombre = sessionMBean.getTextos().get("reporte_atencion_presencial");
        nombre = nombre.replace(" ", "_");
              
        CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, nombre);
        printer.print(); 

      } catch (Exception e1) {
        addErrorMessage(e1);
      }
    }
  } 

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

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public String getEstadoDescripcion() {
		return estadoDescripcion;
	}

	public Boolean getUnaPaginaPorHora() {
		return unaPaginaPorHora;
	}

	public void setUnaPaginaPorHora(Boolean unaPaginaPorHora) {
		this.unaPaginaPorHora = unaPaginaPorHora;
	}

	public void setEstadoDescripcion(String estadoDescripcion) {
		this.estadoDescripcion = estadoDescripcion;
	}

	public List<SelectItem> getEstadosReserva() {
		return estadosReserva;
	}

	public void setEstadosReserva(List<SelectItem> estadosReserva) {
		this.estadosReserva = estadosReserva;
	}

	public Estado getEstadoReservaSeleccionado() {
		return estadoReservaSeleccionado;
	}

	public void setEstadoReservaSeleccionado(Estado estadoReservaSeleccionado) {
		this.estadoReservaSeleccionado = estadoReservaSeleccionado;
	}

	// Arma la lista de etiquetas para encabezar la planilla excel
	private String[] armarCabezales(List<AgrupacionDato> datos){
		String[] cabezales = {sessionMBean.getTextos().get("identificador"), sessionMBean.getTextos().get("fecha"),
				sessionMBean.getTextos().get("hora"), sessionMBean.getTextos().get("numero")};
		
		for(AgrupacionDato grupo: datos) {
			for(DatoASolicitar campo: grupo.getDatosASolicitar()) {
				if (campo.getIncluirEnReporte()) {
					String[] aux = new String[cabezales.length+1];
					aux[cabezales.length] = campo.getEtiqueta();
					for (int i=0;i<cabezales.length; i++){
						aux[i]=cabezales[i];
					}
					cabezales = aux;
				}
			}
		}
		String[] aux = new String[cabezales.length+3];
		int i=0;
		for (i=0;i<cabezales.length; i++){
			aux[i]=cabezales[i];
		}
		aux[i]= sessionMBean.getTextos().get("numero_de_puesto");
		aux[i+1]= sessionMBean.getTextos().get("asistio");
    aux[i+2]= sessionMBean.getTextos().get("tramite");
		cabezales = aux;
		return cabezales;
	}

	// Arma el contenido de la planilla excel
	private List<List<TableCellValue>> armarContenido(Recurso recurso, List<ReservaDTO> reservas, List<AgrupacionDato> agrupaciones) {
		List<List<TableCellValue>> resultado = new ArrayList<List<TableCellValue>>();

		//Una linea en blanco
		List<TableCellValue> filaCabezal = new ArrayList<TableCellValue>();
		resultado.add(filaCabezal);
		
		//Cabezal de agenda
		filaCabezal = new ArrayList<TableCellValue>();
		filaCabezal.add(new TableCellValue(sessionMBean.getTextos().get("agenda")+": "));
		filaCabezal.add(new TableCellValue(recurso.getAgenda().getNombre()));
		resultado.add(filaCabezal);

		filaCabezal = new ArrayList<TableCellValue>();
		filaCabezal.add(new TableCellValue(sessionMBean.getTextos().get("recurso")+": "));
		filaCabezal.add(new TableCellValue(recurso.getNombre()));
		resultado.add(filaCabezal);
		
		String[] cabezales = armarCabezales(agrupaciones);
		filaCabezal = new ArrayList<TableCellValue>();
		for(String cabezal : cabezales) {
			filaCabezal.add(new TableCellValue(cabezal));
		}
		resultado.add(filaCabezal);
		
		for (ReservaDTO reserva:reservas) {
			List<TableCellValue> filaDatos = new ArrayList<TableCellValue>();
			filaDatos.add(new TableCellValue(reserva.getId().toString()));
      filaDatos.add(new TableCellValue(Utiles.date2string(reserva.getFecha(), Utiles.DIA)));
			if(reserva.getPresencial()!=null && reserva.getPresencial().booleanValue()) {
        filaDatos.add(new TableCellValue(sessionMBean.getTextos().get("presencial")));
			}else {
	      filaDatos.add(new TableCellValue(Utiles.date2string(reserva.getHoraInicio(), Utiles.HORA)));
			}
			filaDatos.add(new TableCellValue(reserva.getNumero()));
			for(AgrupacionDato grupo: agrupaciones) {
				for(DatoASolicitar campo: grupo.getDatosASolicitar()) {
					if (campo.getIncluirEnReporte()) {
						String clave = campo.getNombre();
						TableCellValue valor;
						if (reserva.getDatos().containsKey(clave)){
							valor = new TableCellValue(reserva.getDatos().get(clave).toString());
						}
						else {
							valor = new TableCellValue("");
						}
						filaDatos.add(valor);
					}
				}
			}
			
			filaDatos.add(new TableCellValue(reserva.getPuestoLlamada()!=null?reserva.getPuestoLlamada().toString():""));
			
			if(reserva.getAsistio()==null) {
				filaDatos.add(new TableCellValue(""));
			}else {
				filaDatos.add(new TableCellValue(reserva.getAsistio().booleanValue()?sessionMBean.getTextos().get("si"):sessionMBean.getTextos().get("no")));
			}
			
      filaDatos.add(new TableCellValue(reserva.getTramiteNombre()));
			
			resultado.add(filaDatos);
		}
		
		return resultado;
	}

	public Boolean getTodasLasEmpresas() {
		return todasLasEmpresas;
	}

	public void setTodasLasEmpresas(Boolean todasLasEmpresas) {
		this.todasLasEmpresas = todasLasEmpresas;
	}

	public class AtencionComparator implements Comparator<Atencion> {
		@Override
		public int compare(Atencion o1, Atencion o2) {
			Agenda a1 = o1.getReserva().getDisponibilidades().get(0).getRecurso().getAgenda();
			Agenda a2 = o2.getReserva().getDisponibilidades().get(0).getRecurso().getAgenda();
			String nomb1 = a1.getNombre();
			String nomb2 = a2.getNombre();
			if (nomb1.compareTo(nomb2)==0) {
				return o1.getFuncionario().compareTo(o2.getFuncionario());
			}
			return nomb1.compareTo(nomb2);
		}
	}
	
	public class AtencionLlamadaReporteComparator implements Comparator<AtencionLLamadaReporteDT> {
		@Override
		public int compare(AtencionLLamadaReporteDT o1, AtencionLLamadaReporteDT o2) {
			return o1.getReservaId() < o2.getReservaId() ? -1 : o1.getReservaId() > o2.getReservaId() ? 1 : 0;
		}
	}
	
	
}
