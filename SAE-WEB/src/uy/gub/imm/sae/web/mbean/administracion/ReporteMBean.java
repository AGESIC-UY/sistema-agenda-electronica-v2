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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uy.gub.imm.opencsv.ext.entity.CommonLabelValueImpl;
import uy.gub.imm.opencsv.ext.entity.LabelValue;
import uy.gub.imm.opencsv.ext.entity.TableCellValue;
import uy.gub.imm.opencsv.ext.file.StandardCSVFile;
import uy.gub.imm.opencsv.ext.printer.CSVWebFilePrinter;
import uy.gub.imm.sae.business.dto.AtencionLLamadaReporteDT;
import uy.gub.imm.sae.business.dto.AtencionReporteDT;
import uy.gub.imm.sae.business.dto.ReservaDTO;
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
import uy.gub.imm.sae.web.common.reporte.ReporteProvider;

public class ReporteMBean extends BaseMBean {

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/ConsultasBean!uy.gub.imm.sae.business.ejb.facade.ConsultasRemote")
	private Consultas consultaEJB;
	
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
		
		if (sessionMBean.getAgendaMarcada() == null){
			error = true;
			addErrorMessage("debe_haber_una_agenda_seleccionada", MSG_ID);
		}
		
		if (sessionMBean.getRecursoMarcado() == null){
			error = true;
			addErrorMessage("debe_haber_un_recurso_seleccionado", MSG_ID);
		}
	
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
			
			InputStream inputStream = null;
			
			try {
				
				//Diseño basico del reporte al que se le agregará la definición de los campos dinamicos.
				String archivoJrxml = null;				
				if (unaPaginaPorHora) {
					archivoJrxml = "/uy/gub/imm/sae/web/reporte/ReservaPeriodoEstadoHoraPlanilla.jrxml";				
				}
				else {
					archivoJrxml = "/uy/gub/imm/sae/web/reporte/ReservaPeriodoEstadoPlanilla.jrxml";				
				}
				
				//Definicion de los campos dinamicos del reporte
				List<Columna> defColumnas = new ArrayList<Columna>();
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recursoMarcado);
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
				
				//Nombre del atributo en el que se espera encontrar un Map con los campos dinamicos
				//En este caso el objeto de iteracion es ReservaDTO y el atributo ReservaDTO.getDatos es el
				//Map que contendra las parejas <nombreCampo, valor> para cada campo dinamico del reporte.
				String atributoCamposDinamicos = "datos";
				
				//Armo los parametros esperados por el reporte de reservas por fecha y hora
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ID_AGENDA", agendaMarcada.getId());
				params.put("ID_RECURSO", recursoMarcado.getId());
				params.put("FECHA_DESDE", fechaDesde);
				params.put("FECHA_HASTA", fechaHasta);
				params.put("ESTADO", estadoReservaSeleccionado);
				params.put("NOMBRE_AGENDA", agendaMarcada.getDescripcion());
				params.put("NOMBRE_RECURSO", recursoMarcado.getDescripcion());
				
				//Datos a desplegar en el reporte, en este casos las reservas por fecha y hora
				List<ReservaDTO> reservas = consultaEJB.consultarReservasPorPeriodoEstado(recursoMarcado, periodo, estadoReservaSeleccionado);

				FacesContext ctx = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
				inputStream = this.getClass().getResourceAsStream(archivoJrxml);
				
				byte[] pdf = ReporteProvider.generarReporteDinamico(inputStream, defColumnas, atributoCamposDinamicos, params, reservas);
				ReporteProvider.exportarReporteComoPdf(response, pdf);

			} catch (Exception e1) {
				addErrorMessage(e1);
			} finally{
				try {
					if (inputStream != null) inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
		return null;
	}
	
	public void reporteAsistenciaFecha(ActionEvent e) {
		
		limpiarMensajesError();
		
		boolean error = false;
		
		Agenda agendaMarcada = sessionMBean.getAgendaMarcada();
		Recurso recursoMarcado = sessionMBean.getRecursoMarcado();
		
		if (sessionMBean.getAgendaMarcada() == null){
			error = true;
			addErrorMessage("debe_haber_una_agenda_seleccionada", MSG_ID);
		}
		
		if (sessionMBean.getRecursoMarcado() == null){
			error = true;
			addErrorMessage("debe_haber_un_recurso_seleccionado", MSG_ID);
		}
	
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
			
			InputStream inputStream = null;
			try {
				//Definicion de los campos dinamicos del reporte
				//List<Columna> defColumnas = new ArrayList<Columna>();
				
				// TODO: Se debe ver bien que campos se cargan en la planilla.
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recursoMarcado);
				String[] defColPlanilla = armarCabezales(agrupaciones) ;
				//Nombre del atributo en el que se espera encontrar un Map con los campos dinamicos
				//En este caso el objeto de iteracion es ReservaDTO y el atributo ReservaDTO.getDatos es el
				//Map que contendra las parejas <nombreCampo, valor> para cada campo dinamico del reporte.
				//String atributoCamposDinamicos = "datos";
				//Datos a desplegar en el reporte, en este casos las reservas por fecha y hora
				List<ReservaDTO> reservas = consultaEJB.consultarReservasUsadasPeriodo(recursoMarcado, periodo);
				// Aqu� se debe armar la lista de listas de valores, para pasarle al archivo
				
				List<List<TableCellValue>> contenido = armarContenido(reservas, agrupaciones); 

				//TODO: transformar(reservas);
//				StandardCSVFile fileCSV = null;
	               LabelValue[] filtros = {new CommonLabelValueImpl("Agenda: ",agendaMarcada.getDescripcion() ), 
	            		   new CommonLabelValueImpl("Recurso: ",recursoMarcado.getDescripcion()),
	            		   new CommonLabelValueImpl("Fecha desde: ",Utiles.date2string(fechaDesde, Utiles.DIA)),
	            		   new CommonLabelValueImpl("Fecha hasta: ", Utiles.date2string(fechaHasta, Utiles.DIA))
	               };
	            
	            StandardCSVFile fileCSV = new StandardCSVFile(filtros, defColPlanilla, contenido); 
	                CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, "ReporteAsistencia");
	                printer.print(); 
	                
			} catch (Exception e1) {
				
				addErrorMessage(e1);
			} finally{
				try {
					
					if (inputStream != null) inputStream.close();
				} catch (IOException e1) {
					
				}
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
						for (Empresa emp : usuariosEmpresasEJB.consultarEmpresas())
						{
							try {
								sessionMBean.seleccionarEmpresa(emp.getId());
								List<AtencionReporteDT> listAtencionReport = obtenerDatosReporteAtencionPeriodo(this.fechaDesde,this.fechaHasta);
								listAtencionReportDT.addAll(listAtencionReport);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								logger.info("Empresa "+emp.getNombre()+" con esquema mal definida");
								//e1.printStackTrace();
							}
						}
						
					} catch (ApplicationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}finally
					{
						sessionMBean.seleccionarEmpresa(empresaActual.getId());
					}
			}else {
				listAtencionReportDT = obtenerDatosReporteAtencionPeriodo(this.fechaDesde,this.fechaHasta);
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
				String[]  cabezales = {"Empresa","Tramite","Funcionario","Asistencias","Inasistencias","Total Atenciones"};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}else {
				String[]  cabezales = {"Tramite","Funcionario","Asistencias","Inasistencias","Total Atenciones"};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}
						
			CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, "ReporteAtencionFuncionario");
      printer.print(); 
		}
	}
	
	private List<AtencionReporteDT> obtenerDatosReporteAtencionPeriodo(Date fechaDesde, Date fechaHasta)
	{
		List<Atencion> atenciones =consultaEJB.consultarTodasAtencionesPeriodo(fechaDesde,fechaHasta);
		Collections.sort(atenciones, new AtencionComparator());
		String AgendaProcesada = "";
		String FuncionarioProcesado = "";
		int asistencias = 0;
		int inasistencias = 0;
		List<AtencionReporteDT> listAtencionReport = new ArrayList<AtencionReporteDT>(); 
		for (Atencion atencion : atenciones) {
			Agenda a = atencion.getReserva().getDisponibilidades().get(0).getRecurso().getAgenda();
			
			if (!AgendaProcesada.equals(a.getNombre()) || !FuncionarioProcesado.equals(atencion.getFuncionario()) ) 
			{
				int total = asistencias + inasistencias; 
				if(total != 0)
				{
					AtencionReporteDT itemAtencion;
					if(this.todasLasEmpresas)
					{
						itemAtencion = new AtencionReporteDT(sessionMBean.getEmpresaActual().getNombre(),AgendaProcesada, FuncionarioProcesado, asistencias, inasistencias);
					}else
					{
						itemAtencion = new AtencionReporteDT(null,AgendaProcesada, FuncionarioProcesado, asistencias, inasistencias);
					}
					
					listAtencionReport.add(itemAtencion);
				}
				//se inician contadores
				if(atencion.getAsistio())
				{
					asistencias = 1;
					inasistencias = 0;
				}else
				{
					asistencias = 0;
					inasistencias = 1;
				}
				
				
				AgendaProcesada = a.getNombre();
				FuncionarioProcesado = atencion.getFuncionario();
				
			}else
			{
				if(atencion.getAsistio())
				{
					asistencias++;
					
				}else
				{
					inasistencias++;
				}
				
				
			}
			
			
			
		}
		int total = asistencias + inasistencias; 
		if(total != 0)
		{
			AtencionReporteDT itemAtencion;
			if(this.todasLasEmpresas)
			{
				itemAtencion = new AtencionReporteDT(sessionMBean.getEmpresaActual().getNombre(),AgendaProcesada, FuncionarioProcesado, asistencias, inasistencias);
			}else
			{
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
		
		if(!error)
		{
			
			List<AtencionLLamadaReporteDT> listAtencionLLamadaReportDT = new ArrayList<AtencionLLamadaReporteDT>();
			if (this.todasLasEmpresas)
			{
					Empresa empresaActual = sessionMBean.getEmpresaActual();
					try {
						for (Empresa emp : usuariosEmpresasEJB.consultarEmpresas())
						{
							try {
								sessionMBean.seleccionarEmpresa(emp.getId());
								Empresa empresaActualaux = sessionMBean.getEmpresaActual();
								List<AtencionLLamadaReporteDT> listAtencionLlamada	= consultaEJB.consultarLlamadasAtencionPeriodo(fechaDesde,fechaHasta);
								Collections.sort(listAtencionLlamada, new AtencionLlamadaReporteComparator());
								int i=0;
								while (i<listAtencionLlamada.size()) {
									listAtencionLlamada.get(i).setNombEmpresa(emp.getNombre());
									i++;
								}
								
								listAtencionLLamadaReportDT.addAll(listAtencionLlamada);
								
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								logger.info("Empresa "+emp.getNombre()+" con esquema mal definida");
								//e1.printStackTrace();
							}
						}
						
					} catch (ApplicationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}finally
					{
						sessionMBean.seleccionarEmpresa(empresaActual.getId());
					}
					
				
			}else
			{
				listAtencionLLamadaReportDT	= consultaEJB.consultarLlamadasAtencionPeriodo(fechaDesde,fechaHasta);
				Collections.sort(listAtencionLLamadaReportDT, new AtencionLlamadaReporteComparator());
			}
			
			List<List<TableCellValue>> contenido = new ArrayList<List<TableCellValue>>();
			for (AtencionLLamadaReporteDT atencionLlamadaReporteDT : listAtencionLLamadaReportDT) {
				List<TableCellValue> filaDatos = new ArrayList<TableCellValue>();
				if(this.todasLasEmpresas)
				{
					filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombEmpresa()));
				}
				filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombAgenda()));
				filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombRecurso()));
				filaDatos.add(new TableCellValue(Utiles.date2string(atencionLlamadaReporteDT.getFechaHoraReserva(), Utiles.DIA)));
				filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getPuesto()));
				if(atencionLlamadaReporteDT.getNombFuncionario()!=null)
				{
					filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getNombFuncionario()));
				}else
				{
					filaDatos.add(new TableCellValue("---"));
				}
				if(atencionLlamadaReporteDT.getFechaHoraAtencion()!=null)
				{
					long tiempo = atencionLlamadaReporteDT.getFechaHoraAtencion().getTime() - atencionLlamadaReporteDT.getFechaHoraLlamada().getTime(); 
					tiempo = tiempo/(1000 * 60);
					filaDatos.add(new TableCellValue(String.valueOf(tiempo)));
				}else
				{
					filaDatos.add(new TableCellValue("---"));
				}
				
				if(atencionLlamadaReporteDT.getAtencion()!= null)
				{
					filaDatos.add(new TableCellValue(atencionLlamadaReporteDT.getAtencion()));
				}else
				{
					filaDatos.add(new TableCellValue("No Marcado"));
				}
				
				contenido.add(filaDatos);
			}
			
			LabelValue[] filtros = { 
	          		   new CommonLabelValueImpl("Fecha desde: ",Utiles.date2string(fechaDesde, Utiles.DIA)),
	          		   new CommonLabelValueImpl("Fecha hasta: ", Utiles.date2string(fechaHasta, Utiles.DIA))
	             };
			StandardCSVFile fileCSV ;
			if (this.todasLasEmpresas)
			{
				String[]  cabezales = {"Empresa","Tramite","Oficina","Fecha Reserva","Puesto","Funcionario","tiempo atención(min)","Atención"};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}else
			{
				String[]  cabezales = {"Tramite","Oficina","Fecha Reserva","Puesto","Funcionario","tiempo atención(min)","Atención"};
				fileCSV = new StandardCSVFile(filtros, cabezales, contenido); 
			}
						
			CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, "ReporteTiempoAtencion");
			printer.print(); 
			
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
		String[] cabezales = {"Fecha","Hora","Numero"};
		
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
		String[] aux = new String[cabezales.length+2];
		int i=0;
		for (i=0;i<cabezales.length; i++){
			aux[i]=cabezales[i];
		}
		aux[i]= "Puesto";
		aux[i+1]= "Asistio";
		cabezales = aux;
		return cabezales;
	}

	// Arma el contenido de la planilla excel
	private List<List<TableCellValue>> armarContenido(List<ReservaDTO> reservas, List<AgrupacionDato> agrupaciones) {
		List<List<TableCellValue>> resultado = new ArrayList<List<TableCellValue>>();

		for (ReservaDTO reserva:reservas) {
			List<TableCellValue> filaDatos = new ArrayList<TableCellValue>();
			filaDatos.add(new TableCellValue(Utiles.date2string(reserva.getFecha(), Utiles.DIA)));
			filaDatos.add(new TableCellValue(Utiles.date2string(reserva.getHoraInicio(), Utiles.HORA)));
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
			filaDatos.add(new TableCellValue(reserva.getPuestoLlamada()));
			filaDatos.add(new TableCellValue(reserva.getAsistio()));
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
			if (nomb1.compareTo(nomb2)==0)
			{
				return o1.getFuncionario().compareTo(o2.getFuncionario());
			}
			
			return nomb1.compareTo(nomb2);
		}
	}
	
	public class AtencionLlamadaReporteComparator implements Comparator<AtencionLLamadaReporteDT> {

		@Override
		public int compare(AtencionLLamadaReporteDT o1, AtencionLLamadaReporteDT o2) {
		
			return o1.getReservaId() < o2.getReservaId() ? -1
			       : o1.getReservaId() > o2.getReservaId() ? 1
			       : 0;
		}
	}
	
	
}
