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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import uy.gub.imm.opencsv.ext.entity.CommonLabelValueImpl;
import uy.gub.imm.opencsv.ext.entity.LabelValue;
import uy.gub.imm.opencsv.ext.entity.TableCellValue;
import uy.gub.imm.opencsv.ext.file.StandardCSVFile;
import uy.gub.imm.opencsv.ext.printer.CSVWebFilePrinter;
import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.entity.AccionMiPerfil;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Oficina;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.TicketUtiles;

public class RecursoMBean extends BaseMBean{

	public static final String MSG_ID = "pantalla";
	public static final String FORM_ID = "form";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas empresasEJB;
	
  @EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
  private AgendaGeneral generalEJB;
	
	private SessionMBean sessionMBean;
	private Recurso recursoNuevo;
	private DatoDelRecurso datoDelRecursoNuevo;
	private RecursoSessionMBean recursoSessionMBean;
	
	//Tabla asociada en pantalla para poder saber en que recurso se posiciona. 
	private DataTable recursosDataTableModificar;
	private DataTable recursosDataTableEliminar;
	private DataTable recursosDataTableConsultar;
	
	//Tabla asociada tabla en pantalla para poder saber en que recurso se posiciona. 
	private DataTable datosDataTable;

	//Datos de los organismos 
	private Map<String, Oficina> mapOficinas = new HashMap<String, Oficina>();
	private List<SelectItem> oficinas = new ArrayList<SelectItem>(0);

	private UploadedFile archivo;

  List<SelectItem> nombresFuentes = null;
  List<SelectItem> tamaniosFuentes = null;
  
  List<SelectItem> cambiosUnidades = null;
  List<SelectItem> cancelacionUnidades = null;
  
  @PostConstruct
  public void initRecurso(){
    limpiarMensajesError();
    
    Agenda agenda = sessionMBean.getAgendaMarcada();
    //Se controla que se haya Marcado una agenda para trabajar con los recursos
    if (agenda == null){
      addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
    }
    
    try {
      List<Oficina> oficinas = cargarOficinasTramitesAgenda(agenda, false);
      setOficinas(oficinas);
    }catch(ApplicationException aEx) {
      addErrorMessage(aEx.getMessage());
      aEx.printStackTrace();
    }

    TicketUtiles ticketUtiles = new TicketUtiles();
    nombresFuentes = new ArrayList<SelectItem>();
    for(String fuente : ticketUtiles.obtenerFuentesDisponibles()) {
      nombresFuentes.add(new SelectItem(fuente, fuente));
    }
    tamaniosFuentes = new ArrayList<SelectItem>();
    for(int i=4; i<20; i++) {
      tamaniosFuentes.add(new SelectItem(i, ""+i));
    }
    
    cambiosUnidades = new ArrayList<SelectItem>();
    cambiosUnidades.add(new SelectItem(Calendar.DATE, sessionMBean.getTextos().get("dias")));
    cambiosUnidades.add(new SelectItem(Calendar.HOUR, sessionMBean.getTextos().get("horas")));
    cambiosUnidades.add(new SelectItem(Calendar.MINUTE, sessionMBean.getTextos().get("minutos")));
    
    cancelacionUnidades = new ArrayList<SelectItem>();
    cancelacionUnidades.add(new SelectItem(Calendar.DATE, sessionMBean.getTextos().get("dias")));
    cancelacionUnidades.add(new SelectItem(Calendar.HOUR, sessionMBean.getTextos().get("horas")));
    cancelacionUnidades.add(new SelectItem(Calendar.MINUTE, sessionMBean.getTextos().get("minutos")));
    
  }

	/**************************************************************************/
	/*                           Getters y Setters                            */	
	/**************************************************************************/	
  
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}
	
	public Recurso getRecursoNuevo() {
		if (recursoNuevo == null) {
			recursoNuevo = new Recurso();
			recursoNuevo.setFuenteTicket("helvetica");
			recursoNuevo.setTamanioFuenteChica(6);
      recursoNuevo.setTamanioFuenteNormal(10);
      recursoNuevo.setTamanioFuenteGrande(12);
      
//      //Creo la accion con valores por defecto y se la setteo al recursoNuevo
//      AccionMiPerfil accionMiPerfil = recursosEJB.obtenerAccionMiPerfilPorDefecto(recursoNuevo);
//      recursoNuevo.setAccionMiPerfil(accionMiPerfil);
          
      //Creo la accion con valores por defecto y se la setteo al recursoNuevo
      AccionMiPerfil accionMiPerfil = new AccionMiPerfil();
      
      accionMiPerfil.setRecurso(recursoNuevo);
      
      accionMiPerfil.setDestacada_con_1(true);
      accionMiPerfil.setTitulo_con_1("Ir a ubicacion");
      accionMiPerfil.setUrl_con_1("https://www.google.com.uy/maps/@{latitud},{longitud},15z");
      
      accionMiPerfil.setTitulo_con_2("Cancelar reserva");
      accionMiPerfil.setUrl_con_2("{linkBase}/sae/cancelarReserva/Paso1.xhtml?e={empresa}&a={agenda}&ri={reserva}");
      
      accionMiPerfil.setDestacada_can_1(true);
      accionMiPerfil.setTitulo_can_1("Ir a ubicacion");
      accionMiPerfil.setUrl_can_1("https://www.google.com.uy/maps/@{latitud},{longitud},15z");
      
      accionMiPerfil.setDestacada_rec_1(true);
      accionMiPerfil.setTitulo_rec_1("Ir a ubicacion");
      accionMiPerfil.setUrl_rec_1("https://www.google.com.uy/maps/@{latitud},{longitud},15z");
      
      accionMiPerfil.setTitulo_rec_2("Cancelar reserva");
      accionMiPerfil.setUrl_rec_2("{linkBase}/sae/cancelarReserva/Paso1.xhtml?e={empresa}&a={agenda}&ri={reserva}");
      
      recursoNuevo.setAccionMiPerfil(accionMiPerfil);
        
      
		}
		return recursoNuevo;
	}
	
	public void setRecursoNuevo(Recurso r){
		recursoNuevo = r;
	}
	
	//Recurso seleccionado para eliminacion/modificacion
	public Recurso getRecursoSeleccionado() {
		return sessionMBean.getRecursoSeleccionado();
	}

	public DataTable getRecursosDataTableModificar() {
		return recursosDataTableModificar;
	}

	public void setRecursosDataTableModificar(DataTable recursosDataTableModificar) {
		this.recursosDataTableModificar = recursosDataTableModificar;
	}

	public DataTable getRecursosDataTableEliminar() {
		return recursosDataTableEliminar;
	}

	public void setRecursosDataTableEliminar(DataTable recursosDataTableEliminar) {
		this.recursosDataTableEliminar = recursosDataTableEliminar;
	}

	public DataTable getRecursosDataTableConsultar() {
		return recursosDataTableConsultar;
	}

	public void setRecursosDataTableConsultar(DataTable recursosDataTableConsultar) {
		this.recursosDataTableConsultar = recursosDataTableConsultar;
	}

	//Tabla de Datos del Recursos	
	public DataTable getDatosDataTable() {
		return datosDataTable;
	}

	public void setDatosDataTable(DataTable datosDataTable) {
		this.datosDataTable = datosDataTable;
	}

	public DatoDelRecurso getDatoDelRecursoNuevo() {
		if (datoDelRecursoNuevo == null) {
			datoDelRecursoNuevo = new DatoDelRecurso();
		}
		return datoDelRecursoNuevo;
	}

	public UploadedFile getArchivo() {
		return archivo;
	}

	public void setArchivo(UploadedFile archivo) {
		this.archivo = archivo;
	}

	/**************************************************************************/
	/*                        Action Listener de Recurso                      */	
	/**************************************************************************/	
	
	public void crear(ActionEvent e) {
		
		limpiarMensajesError();
		
    Recurso recurso = getRecursoNuevo();
		boolean hayErrores = false;
		if(recurso.getNombre() == null || recurso.getNombre().equals("")){
			addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_recurso_es_obligatorio"), FORM_ID+":nombre");
      hayErrores = true;
		}
		if(recurso.getDescripcion() == null || recurso.getDescripcion().equals("")){
			addErrorMessage(sessionMBean.getTextos().get("la_descripcion_del_recurso_es_obligatoria"), FORM_ID+":descripcion");
      hayErrores = true;
		}
		recurso.setVentanaCuposMinimos(0);
		//Fechas de vigencia
		if (recurso.getFechaInicio() == null){
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_vigencia_es_obligatoria"), FORM_ID+":fechaInicio");
      hayErrores = true;
		}else if(Utiles.esFechaInvalida(recurso.getFechaInicio())) {
      addErrorMessage("la_fecha_de_inicio_de_vigencia_es_invalida", FORM_ID+":fechaInicio");
      hayErrores = true;
    }else {
			recurso.setFechaInicio(Utiles.time2InicioDelDia(recurso.getFechaInicio()));
		}
		if(recurso.getFechaFin() == null) {
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_vigencia_es_obligatoria"), FORM_ID+":fechaFin");
      hayErrores = true;
		}else if(Utiles.esFechaInvalida(recurso.getFechaFin())) {
      addErrorMessage("la_fecha_de_fin_de_vigencia_es_invalida", FORM_ID+":fechaFin");
      hayErrores = true;
    }else {
			recurso.setFechaFin(Utiles.time2FinDelDia(recurso.getFechaFin()));
		}
		if(recurso.getFechaInicio() != null && !Utiles.esFechaInvalida(recurso.getFechaInicio()) && 
		    recurso.getFechaFin() != null && !Utiles.esFechaInvalida(recurso.getFechaFin()) && recurso.getFechaInicio().compareTo(recurso.getFechaFin()) > 0) {
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_vigencia_debe_ser_posterior_a_la_fecha_de_inicio_de_vigencia"), FORM_ID+":fechaFin",FORM_ID+":fechaInicio");
      hayErrores = true;
		}
    //Fechas de disponibilidad
		if(recurso.getFechaInicioDisp() == null) {
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_disponibilidad_es_obligatoria"), FORM_ID+":fechaIniDispon");
      hayErrores = true;
		}else if(Utiles.esFechaInvalida(recurso.getFechaInicioDisp())) {
      addErrorMessage("la_fecha_de_inicio_de_disponibilidad_es_invalida", FORM_ID+":fechaIniDispon");
      hayErrores = true;
    }else {
			recurso.setFechaInicioDisp(Utiles.time2InicioDelDia(recurso.getFechaInicioDisp()));
		}
		if(recurso.getFechaFinDisp() == null){
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_disponibilidad_es_obligatoria"), FORM_ID+":fechaFinDispon");
      hayErrores = true;
		}else if(Utiles.esFechaInvalida(recurso.getFechaFinDisp())) {
      addErrorMessage("la_fecha_de_fin_de_disponibilidad_es_invalida", FORM_ID+":fechaFinDispon");
      hayErrores = true;
    }else {
			recurso.setFechaFinDisp(Utiles.time2FinDelDia(recurso.getFechaFinDisp()));	
		}
		if (recurso.getFechaInicioDisp()!=null && !Utiles.esFechaInvalida(recurso.getFechaInicioDisp()) &&
		    recurso.getFechaFinDisp()!=null && !Utiles.esFechaInvalida(recurso.getFechaFinDisp()) && 
		    recurso.getFechaInicioDisp().compareTo(recurso.getFechaFinDisp()) > 0){
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_disponibilidad_debe_ser_posterior_a_la_fecha_de_disponibilidad_de_vigencia"), FORM_ID+":fechaFinDispon",FORM_ID+":fechaIniDispon");
      hayErrores = true;
		}
		//Relaciones entre fechas de vigencia y disponibilidad
		if (recurso.getFechaInicioDisp()!=null && !Utiles.esFechaInvalida(recurso.getFechaInicioDisp()) && 
		    recurso.getFechaInicio()!=null && !Utiles.esFechaInvalida(recurso.getFechaInicio()) && 
		    recurso.getFechaInicio().compareTo(recurso.getFechaInicioDisp()) > 0) {
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_disponibilidad_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_vigencia"), FORM_ID+":fechaInicio",FORM_ID+":fechaIniDispon");
      hayErrores = true;
		}
		if (recurso.getFechaFinDisp()!=null && !Utiles.esFechaInvalida(recurso.getFechaFinDisp()) && 
		    recurso.getFechaFin()!=null && !Utiles.esFechaInvalida(recurso.getFechaFin()) && 
		    recurso.getFechaFinDisp().compareTo(recurso.getFechaFin()) > 0) {
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_disponibilidad_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_vigencia"), FORM_ID+":fechaFin",FORM_ID+":fechaFinDispon");
      hayErrores = true;
		}
		if (recurso.getDiasInicioVentanaIntranet() == null){
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":DiasInicioVIntranet");
      hayErrores = true;
		}else if (recurso.getDiasInicioVentanaIntranet().intValue() < 0){
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":DiasInicioVIntranet");
      hayErrores = true;
		}
		if (recurso.getDiasVentanaIntranet() == null){
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":DiasVIntranet");
      hayErrores = true;
		}else if (recurso.getDiasVentanaIntranet().intValue() <= 0){
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero"), FORM_ID+":DiasVIntranet");
      hayErrores = true;
		}
		if(recurso.getVisibleInternet()!=null && recurso.getVisibleInternet().booleanValue()) {
	    if (recurso.getDiasInicioVentanaInternet() == null){
	      addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":DiasInicioVInternet");
        hayErrores = true;
	    }else if (recurso.getDiasInicioVentanaInternet().intValue() < 0){
	      addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":DiasInicioVInternet");
        hayErrores = true;
	    }
  		if (recurso.getDiasVentanaInternet() == null){
  			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_es_obligatorio"), FORM_ID+":DiasVInternet");
        hayErrores = true;
  		}else if (recurso.getDiasVentanaInternet().intValue() <= 0){
  			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero"), FORM_ID+":DiasVInternet");
        hayErrores = true;
  		}
		}
	    if (recurso.getVentanaCuposMinimos() == null){
	      addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_es_obligatoria"), FORM_ID+":VCuposM");
	      hayErrores = true;
	    }
		if (recurso.getVentanaCuposMinimos()!=null && recurso.getVentanaCuposMinimos().intValue() < 0 ){
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":VCuposM");
      hayErrores = true;
		}
		if (recurso.getCantDiasAGenerar() == null){
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_es_obligatoria"), FORM_ID+":CantDias");
      hayErrores = true;
		}
		if (recurso.getCantDiasAGenerar()!=null && recurso.getCantDiasAGenerar().intValue() <= 0 ){
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero"), FORM_ID+":CantDias");
      hayErrores = true;
		}
		if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaIntranet() + recurso.getDiasVentanaIntranet()) < 0 ){
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet"), FORM_ID+":CantDias");
      hayErrores = true;
		}
		if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaInternet() + recurso.getDiasVentanaInternet()) < 0 ){
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet"), FORM_ID+":CantDias");
      hayErrores = true;
		}
		if (recurso.getLargoListaEspera() != null && recurso.getLargoListaEspera().intValue() <= 0 ){
			addErrorMessage(sessionMBean.getTextos().get("el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero"), FORM_ID+":largoLista");
      hayErrores = true;
		}
    if(recurso.getPresencialAdmite()!=null && recurso.getPresencialAdmite().booleanValue()) {
  		if(recurso.getPresencialCupos() != null && recurso.getPresencialCupos().intValue() <= 0 ) {
        addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_por_dia_debe_ser_mayor_a_cero"), FORM_ID+":cuposPresencial");
        hayErrores = true;
  		}
      if((recurso.getPresencialLunes()==null || !recurso.getPresencialLunes().booleanValue()) && 
          (recurso.getPresencialMartes()==null || !recurso.getPresencialMartes().booleanValue()) && 
          (recurso.getPresencialMiercoles()==null || !recurso.getPresencialMiercoles().booleanValue()) &&
          (recurso.getPresencialJueves()==null || !recurso.getPresencialJueves().booleanValue()) &&
          (recurso.getPresencialViernes()==null || !recurso.getPresencialViernes().booleanValue()) &&
          (recurso.getPresencialSabado()==null || !recurso.getPresencialSabado().booleanValue()) &&
          (recurso.getPresencialDomingo()==null || !recurso.getPresencialDomingo().booleanValue())) {
        addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_al_menos_un_dia"), FORM_ID+":diaAplicarHidden");
        hayErrores = true;
      }
    }
		recurso.setNombre(getRecursoNuevo().getNombre().trim());
		recurso.setDescripcion(getRecursoNuevo().getDescripcion().trim());
		try {
			if(recurso.getAgenda()==null) {
				recurso.setAgenda(sessionMBean.getAgendaMarcada());
			}
			if(recursosEJB.existeRecursoPorNombre(recurso)) {
				hayErrores = true;
				addErrorMessage(sessionMBean.getTextos().get("ya_existe_un_recurso_con_el_nombre_especificado"), FORM_ID+":nombre");
			}
		}catch (ApplicationException e1) {
			addErrorMessage(e1, MSG_ID);
		}
    if(recurso.getPeriodoValidacion()==null) {
      addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_es_obligatorio"), FORM_ID+":PeriodoValidacion");
      hayErrores = true;
    }else if(recurso.getPeriodoValidacion()<0){
      addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":PeriodoValidacion");
      hayErrores = true;
    }
    if(recurso.getValidarPorIP()!=null && recurso.getValidarPorIP()==true) {
      if(recurso.getCantidadPorIP()==null) {
        addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_reservas_por_ip_es_obligatoria"), FORM_ID+":cantidadPorIP");
        hayErrores = true;
      }else if(recurso.getCantidadPorIP()<=0){
        addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_reservas_por_ip_debe_ser_mayor_a_cero"), FORM_ID+":cantidadPorIP");
        hayErrores = true;
      }
      if(recurso.getPeriodoPorIP()==null) {
        addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_por_ip_es_obligatorio"), FORM_ID+":periodoPorIP");
        hayErrores = true;
      }else if(recurso.getPeriodoPorIP()<0){
        addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_por_ip_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":periodoPorIP");
        hayErrores = true;
      }
    }
    
    if(recurso.getCancelacionTiempo() == null) {
      addErrorMessage(sessionMBean.getTextos().get("el_tiempo_previo_para_cancelaciones_es_requerido"), FORM_ID+":cancelacionTiempo");
      hayErrores = true;
    }else if(recurso.getCancelacionTiempo().intValue() < 0 ) {
      addErrorMessage(sessionMBean.getTextos().get("el_tiempo_previo_para_cancelaciones_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":cancelacionTiempo");
      hayErrores = true;
    }
    if(recurso.getCancelacionTipo()==null) {
      addErrorMessage(sessionMBean.getTextos().get("el_tipo_de_cancelacion_es_obligatorio"), FORM_ID+":cancelacionTipo");
      hayErrores = true;
    }
    
    
    if (recurso.getReservaPendienteTiempoMax()!=null && recurso.getReservaPendienteTiempoMax().intValue() < 0 ){
		addErrorMessage(sessionMBean.getTextos().get("reserva_pendiente_tiempo_max_debe_ser_mayor_a_cero"), FORM_ID+":tiempoMaxReserva");
		hayErrores = true;
	}
    
    
    if (recurso.getReservaMultiplePendienteTiempoMax()!=null && recurso.getReservaMultiplePendienteTiempoMax().intValue() < 0 ){
		addErrorMessage(sessionMBean.getTextos().get("reserva_multiple_pendiente_tiempo_max_debe_ser_mayor_a_cero"), FORM_ID+":tiempoMaxReservaMultiple");
		hayErrores = true;
	}
    
		if(hayErrores) {
		  return;
		}
		try {
			recursosEJB.crearRecurso(sessionMBean.getAgendaMarcada(), recurso, sessionMBean.getUsuarioActual().getCodigo());
			sessionMBean.cargarRecursos();
			sessionMBean.desmarcarRecurso();
			//Se blanquea la info en la página
			this.setRecursoNuevo(null);
			addInfoMessage(sessionMBean.getTextos().get("recurso_creado"), MSG_ID);
		} catch (Exception ex) {
			addErrorMessage(ex, MSG_ID);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void selecRecursoEliminar(ActionEvent e){
		sessionMBean.setRecursoSeleccionado(((Row<Recurso>)this.getRecursosDataTableConsultar().getRowData()).getData());
	}
	
	public void eliminar(ActionEvent ev) {
		limpiarMensajesError();
		
		Recurso recurso = this.sessionMBean.getRecursoSeleccionado();
		if (recurso != null) {
 			try {
 				if(sessionMBean.getRecursoMarcado()!=null && sessionMBean.getRecursoMarcado().getId().equals(recurso.getId())) {
 					sessionMBean.desseleccionarRecurso();
 				}
				recursosEJB.eliminarRecurso(recurso, sessionMBean.getTimeZone(), sessionMBean.getUsuarioActual().getCodigo());
				/*
				  Se hace este chequeo porque cuando se accede con un usuario 'Administrador de recursos', luego de hacer alguna
				modificación en alguno de sus recursos, la aplicación recargaba la lista de recursos y se perdía la selección
				del recurso que le daba acceso a las opciones del menú
				*/
				if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR_DE_RECURSOS" }) || sessionMBean.getUsuarioActual().getSuperadmin()) {
					sessionMBean.cargarRecursos();
					sessionMBean.desmarcarRecurso();
				} else {
					sessionMBean.removeRecursoEnLista(recurso);
				}
 				addInfoMessage(sessionMBean.getTextos().get("recurso_eliminado"), MSG_ID);
 			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			} finally {
 				this.sessionMBean.setRecursoSeleccionado(null);
 			}
 		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
	}

	@SuppressWarnings("unchecked")
	public String modificar() {
    Recurso recurso1 = ((Row<Recurso>) this.getRecursosDataTableConsultar().getRowData()).getData();
		if (recurso1 != null) {
			recursoNuevo = new Recurso(recurso1);
			sessionMBean.setRecursoSeleccionado(recursoNuevo);
			//Se agrega para que si cambiamos de recurso no queden cargados los datos viejos 
			sessionMBean.setDatoDelRecursoSeleccionado(null);
			sessionMBean.setMostrarAgregarDato(false);
			
			//Cargo la accionMiPerfil, que contiene la info de todas las acciones del recurso
		    AccionMiPerfil accionMiPerfil = recursosEJB.obtenerAccionMiPerfilDeRecurso(recursoNuevo.getId());
		    //Le setteo la accion al recurso (transient)
		    sessionMBean.getRecursoSeleccionado().setAccionMiPerfil(accionMiPerfil);
		    
			return "modificar";
		} else {
			sessionMBean.setRecursoSeleccionado(null);
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void copiar(ActionEvent event) {
    Recurso recurso = ((Row<Recurso>) this.getRecursosDataTableConsultar().getRowData()).getData();
		if (recurso != null) {
			try {
				//Cargo la accionMiPerfil, que contiene la info de todas las acciones del recurso
			    AccionMiPerfil accionMiPerfil = recursosEJB.obtenerAccionMiPerfilDeRecurso(recurso.getId());
			    //Le setteo la accion al recurso (transient)
			    recurso.setAccionMiPerfil(accionMiPerfil);
			    
				Recurso nuevoR = recursosEJB.copiarRecurso(recurso, sessionMBean.getUsuarioActual().getCodigo());
				/*
				  Se hace este chequeo porque cuando se accede con un usuario 'Administrador de recursos', luego de hacer alguna
				modificación en alguno de sus recursos, la aplicación recargaba la lista de recursos y se perdía la selección
				del recurso que le daba acceso a las opciones del menú
				*/
				if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR_DE_RECURSOS" }) || sessionMBean.getUsuarioActual().getSuperadmin()) {
					sessionMBean.cargarRecursos();
					sessionMBean.desmarcarRecurso();
				} else {
					sessionMBean.addRecursoALista(nuevoR);
				}
				addInfoMessage(sessionMBean.getTextos().get("recurso_copiado"), MSG_ID);
			} catch (Exception e) {
				addErrorMessage(e, MSG_ID);
			}
		}
	}

	public String guardar() {
		limpiarMensajesError();
		if (sessionMBean.getRecursoSeleccionado() != null) {
 			try {
 				boolean hayErrores = false;
 				sessionMBean.getRecursoSeleccionado().setVentanaCuposMinimos(0);
 				sessionMBean.getRecursoSeleccionado().setNombre(sessionMBean.getRecursoSeleccionado().getNombre().trim());
 				sessionMBean.getRecursoSeleccionado().setDescripcion(sessionMBean.getRecursoSeleccionado().getDescripcion().trim());
 				Recurso recurso = sessionMBean.getRecursoSeleccionado();
 				if(recurso.getNombre() == null || recurso.getNombre().equals("")){
 					addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_recurso_es_obligatorio"), FORM_ID+":nombre");
          hayErrores = true;
 				}
 				if(recurso.getDescripcion() == null || recurso.getDescripcion().equals("")){
 					addErrorMessage(sessionMBean.getTextos().get("la_descripcion_del_recurso_es_obligatoria"), FORM_ID+":descripcion");
          hayErrores = true;
 				}
 				//Fechas de vigencia
 				if (recurso.getFechaInicio()==null){
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_vigencia_es_obligatoria"), FORM_ID+":fechaInicio");
          hayErrores = true;
 				}else if(Utiles.esFechaInvalida(recurso.getFechaInicio())) {
          addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_vigencia_es_invalida"), FORM_ID+":fechaInicio");
          hayErrores = true;
 				}else {
 					recurso.setFechaInicio(Utiles.time2InicioDelDia(recurso.getFechaInicio()));
 				}
 				if(recurso.getFechaFin()==null) {
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_vigencia_es_obligatoria"), FORM_ID+":fechaFin");
          hayErrores = true;
 				}else if(Utiles.esFechaInvalida(recurso.getFechaFin())) {
          addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_vigencia_es_invalida"), FORM_ID+":fechaFin");
          hayErrores = true;
        }else {
 					recurso.setFechaFin(Utiles.time2FinDelDia(recurso.getFechaFin()));
 				}
 				if(recurso.getFechaInicio()!=null && !Utiles.esFechaInvalida(recurso.getFechaInicio()) && 
 				    recurso.getFechaFin()!= null && !Utiles.esFechaInvalida(recurso.getFechaFin()) && 
 				    recurso.getFechaInicio().compareTo(recurso.getFechaFin()) > 0 ) {
 					hayErrores = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_vigencia_debe_ser_posterior_a_la_fecha_de_inicio_de_vigencia"), FORM_ID+":fechaFin",FORM_ID+":fechaInicio");
 				}
 				//Fechas de disponibilidad
 				if(recurso.getFechaInicioDisp() == null) {
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_disponibilidad_es_obligatoria"), FORM_ID+":fechaInicioDispon");
          hayErrores = true;
 				}else if(Utiles.esFechaInvalida(recurso.getFechaInicioDisp())) {
          addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_disponibilidad_es_invalida"), FORM_ID+":fechaInicioDispon");
          hayErrores = true;
        }else {
 					recurso.setFechaInicioDisp(Utiles.time2InicioDelDia(recurso.getFechaInicioDisp()));
 				}
 				if(recurso.getFechaFinDisp() == null) {
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_disponibilidad_es_obligatoria"), FORM_ID+":fechaFinDispon");
          hayErrores = true;
 				}else if(Utiles.esFechaInvalida(recurso.getFechaFinDisp())) {
          addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_disponibilidad_es_invalida"), FORM_ID+":fechaFinDispon");
          hayErrores = true;
        }else {
 					recurso.setFechaFinDisp(Utiles.time2FinDelDia(recurso.getFechaFinDisp()));	
 				}
 				if(recurso.getFechaInicioDisp()!=null && !Utiles.esFechaInvalida(recurso.getFechaInicioDisp()) &&
 				    recurso.getFechaFinDisp() != null && !Utiles.esFechaInvalida(recurso.getFechaFinDisp()) &&
 				    recurso.getFechaInicioDisp().compareTo(recurso.getFechaFinDisp())>0){
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_disponibilidad_debe_ser_posterior_a_la_fecha_de_disponibilidad_de_vigencia"), FORM_ID+":fechaFinDispon",FORM_ID+":fechaInicioDispon");
          hayErrores = true;
 				}
 				//Relaciones entre fechas de vigencia y disponibilidad
 				if (recurso.getFechaInicioDisp()!=null && !Utiles.esFechaInvalida(recurso.getFechaInicioDisp()) && 
 				    recurso.getFechaInicio()!=null && !Utiles.esFechaInvalida(recurso.getFechaInicio()) && 
 				    recurso.getFechaInicio().compareTo(recurso.getFechaInicioDisp()) > 0) {
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_de_disponibilidad_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_vigencia"), FORM_ID+":fechaInicio",FORM_ID+":fechaInicioDispon");
          hayErrores = true;
 				}
 				if (recurso.getFechaFinDisp()!=null && !Utiles.esFechaInvalida(recurso.getFechaFinDisp()) && 
 				    recurso.getFechaFin()!=null && !Utiles.esFechaInvalida(recurso.getFechaFin()) && 
 				    recurso.getFechaFinDisp().compareTo(recurso.getFechaFin())>0) {
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_de_disponibilidad_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_vigencia"), FORM_ID+":fechaFin",FORM_ID+":fechaFinDispon");
          hayErrores = true;
 				}
        if (recurso.getDiasInicioVentanaIntranet() == null){
          addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":diasIVIntranet");
          hayErrores = true;
        }else if (recurso.getDiasInicioVentanaIntranet().intValue() < 0){
          addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":diasIVIntranet");
          hayErrores = true;
        }
 				if (recurso.getDiasVentanaIntranet() == null){
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":diasVIntranet");
          hayErrores = true;
 				}else if (recurso.getDiasVentanaIntranet().intValue() <= 0){
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero"), FORM_ID+":diasVIntranet");
          hayErrores = true;
 				}
 		    if(recurso.getVisibleInternet()!=null && recurso.getVisibleInternet().booleanValue()) {
   				if (recurso.getDiasInicioVentanaInternet() == null){
   					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_internet_es_obligatorio"), FORM_ID+":diasIVInternet");
            hayErrores = true;
   				}else if (recurso.getDiasInicioVentanaInternet().intValue() < 0){
   					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":diasIVInternet");
            hayErrores = true;
   				}
          if (recurso.getDiasVentanaInternet() == null){
            addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_es_obligatorio"), FORM_ID+":diasVInternet");
            hayErrores = true;
          }else if (recurso.getDiasVentanaInternet().intValue() <= 0){
            addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero"), FORM_ID+":diasVInternet");
            hayErrores = true;
          }
        }
 				if (recurso.getVentanaCuposMinimos() == null){
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_es_obligatoria"), FORM_ID+":VCuposMin");
          hayErrores = true;
 				}
 				if (recurso.getVentanaCuposMinimos()!=null && recurso.getVentanaCuposMinimos().intValue() < 0 ){
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":VCuposMin");
          hayErrores = true;
 				}
 				if (recurso.getCantDiasAGenerar() == null){
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_es_obligatoria"), FORM_ID+":vDiasGen");
          hayErrores = true;
 				}
 				if (recurso.getCantDiasAGenerar()!=null && recurso.getCantDiasAGenerar().intValue() <= 0 ){
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero"), FORM_ID+":vDiasGen");
          hayErrores = true;
 				}
 				if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaIntranet() + recurso.getDiasVentanaIntranet()) < 0 ){
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet"), FORM_ID+":vDiasGen");
          hayErrores = true;
 				}
 				if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaInternet() + recurso.getDiasVentanaInternet()) < 0 ){
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet"), FORM_ID+":vDiasGen");
          hayErrores = true;
 				}
 				if ((recurso.getLargoListaEspera() != null) && (recurso.getLargoListaEspera().intValue() <= 0 )){
 					addErrorMessage(sessionMBean.getTextos().get("el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero"), FORM_ID+":largoListaEspera");
          hayErrores = true;
 				}
 		    if(recurso.getPresencialAdmite()!=null && recurso.getPresencialAdmite().booleanValue()) {
 		      if(recurso.getPresencialCupos() != null && recurso.getPresencialCupos().intValue() <= 0 ) {
 	          addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_por_dia_debe_ser_mayor_a_cero"), FORM_ID+":cuposPresencial");
            hayErrores = true;
 		      }
 		      if((recurso.getPresencialLunes()==null || !recurso.getPresencialLunes().booleanValue()) && 
 		          (recurso.getPresencialMartes()==null || !recurso.getPresencialMartes().booleanValue()) && 
 		          (recurso.getPresencialMiercoles()==null || !recurso.getPresencialMiercoles().booleanValue()) &&
 		          (recurso.getPresencialJueves()==null || !recurso.getPresencialJueves().booleanValue()) &&
 		          (recurso.getPresencialViernes()==null || !recurso.getPresencialViernes().booleanValue()) &&
 		          (recurso.getPresencialSabado()==null || !recurso.getPresencialSabado().booleanValue()) &&
 		          (recurso.getPresencialDomingo()==null || !recurso.getPresencialDomingo().booleanValue())) {
            addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_al_menos_un_dia"), FORM_ID+":diaAplicarHidden");
            hayErrores = true;
 		      }
 		    }
        if(recurso.getCambiosAdmite()!=null && recurso.getCambiosAdmite().booleanValue()) {
          if(recurso.getCambiosTiempo() == null) {
            addErrorMessage(sessionMBean.getTextos().get("el_tiempo_previo_para_cambios_es_requerido"), FORM_ID+":cambiosTiempo");
            hayErrores = true;
          }else if(recurso.getCambiosTiempo().intValue() <= 0 ) {
            addErrorMessage(sessionMBean.getTextos().get("el_tiempo_previo_para_cambios_debe_ser_mayor_a_cero"), FORM_ID+":cambiosTiempo");
            hayErrores = true;
          }
        }
 				recurso.setNombre(sessionMBean.getRecursoSeleccionado().getNombre().trim());
 				recurso.setDescripcion(sessionMBean.getRecursoSeleccionado().getDescripcion().trim());
 				try {
 					if(recurso.getAgenda()==null) {
 						recurso.setAgenda(sessionMBean.getAgendaMarcada());
 					}
 					if(recursosEJB.existeRecursoPorNombre(recurso)) {
 						addErrorMessage(sessionMBean.getTextos().get("ya_existe_un_recurso_con_el_nombre_especificado"), FORM_ID+":nombre");
            hayErrores = true;
 					}
 				}catch (ApplicationException e1) {
 					addErrorMessage(e1, MSG_ID);
 				}
 				if(recurso.getPeriodoValidacion()==null) {
          addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_es_obligatorio"), FORM_ID+":vPeriodoValidacion");
          hayErrores = true;
 				}else if(recurso.getPeriodoValidacion()<0){
          addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":vPeriodoValidacion");
          hayErrores = true;
 				}
        if(recurso.getValidarPorIP()!=null && recurso.getValidarPorIP()==true) {
          if(recurso.getCantidadPorIP()==null) {
            addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_reservas_por_ip_es_obligatoria"), FORM_ID+":cantidadPorIP");
            hayErrores = true;
          }else if(recurso.getCantidadPorIP()<=0){
            addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_reservas_por_ip_debe_ser_mayor_a_cero"), FORM_ID+":cantidadPorIP");
            hayErrores = true;
          }
          if(recurso.getPeriodoPorIP()==null) {
            addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_por_ip_es_obligatorio"), FORM_ID+":periodoPorIP");
            hayErrores = true;
          }else if(recurso.getPeriodoPorIP()<0){
            addErrorMessage(sessionMBean.getTextos().get("el_periodo_de_validacion_por_ip_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":periodoPorIP");
            hayErrores = true;
          }
        }
        if(recurso.getCancelacionTiempo() == null) {
          addErrorMessage(sessionMBean.getTextos().get("el_tiempo_previo_para_cancelaciones_es_requerido"), FORM_ID+":cancelacionTiempo");
          hayErrores = true;
        }else if(recurso.getCancelacionTiempo().intValue() < 0 ) {
          addErrorMessage(sessionMBean.getTextos().get("el_tiempo_previo_para_cancelaciones_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":cancelacionTiempo");
          hayErrores = true;
        }
        if(recurso.getCancelacionTipo()==null) {
          addErrorMessage(sessionMBean.getTextos().get("el_tipo_de_cancelacion_es_obligatorio"), FORM_ID+":cancelacionTipo");
          hayErrores = true;
        }
        
        
        if (recurso.getReservaPendienteTiempoMax()!=null && recurso.getReservaPendienteTiempoMax().intValue() < 0 ){
			addErrorMessage(sessionMBean.getTextos().get("reserva_pendiente_tiempo_max_debe_ser_mayor_a_cero"), FORM_ID+":tiempoMaxReserva");
			hayErrores = true;
		}
        
        
        if (recurso.getReservaMultiplePendienteTiempoMax()!=null && recurso.getReservaMultiplePendienteTiempoMax().intValue() < 0 ){
        	System.out.println("VALOR DE RESERVA MULTIPLE PENDIENTE MAX " + recurso.getReservaMultiplePendienteTiempoMax());
			addErrorMessage(sessionMBean.getTextos().get("reserva_multiple_pendiente_tiempo_max_debe_ser_mayor_a_cero"), FORM_ID+":tiempoMaxReservaMultiple");
			hayErrores = true;
		}
        
        
        
        
 				if(hayErrores) {
 					return null;
 				}
 				if(recurso.getIpsSinValidacion()!=null) {
 				  recurso.setIpsSinValidacion(recurso.getIpsSinValidacion().replace(" ", ";").replace(",", ";").replace("-", ";"));
 				}
				recursosEJB.modificarRecurso(sessionMBean.getRecursoSeleccionado(), sessionMBean.getUsuarioActual().getCodigo());
				/*
				  Se hace este chequeo porque cuando se accede con un usuario 'Administrador de recursos', luego de hacer alguna
				modificación en alguno de sus recursos, la aplicación recargaba la lista de recursos y se perdía la selección
				del recurso que le daba acceso a las opciones del menú
				*/
				if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR_DE_RECURSOS" }) || sessionMBean.getUsuarioActual().getSuperadmin()) {
					sessionMBean.cargarRecursos();
				} else {
					sessionMBean.setRecursoEnLista(recurso);
				}
				addInfoMessage(sessionMBean.getTextos().get("recurso_modificado"), MSG_ID);
 				return "guardar";
 			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			}
		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		
		return null;
	}
	

	/**************************************************************************/
	/*               Action Listeners de Datos del Recurso                    */	
	/**************************************************************************/	

	public void agregarDatoDelRecurso(ActionEvent e) {
		limpiarMensajesError();
		boolean hayError = false;
		if(getDatoDelRecursoNuevo().getEtiqueta() == null || getDatoDelRecursoNuevo().getEtiqueta().trim().isEmpty()){
			addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "form2:idEtiqueta");
			hayError = true;
		} 
		if(getDatoDelRecursoNuevo().getValor() == null || getDatoDelRecursoNuevo().getValor().trim().isEmpty()){
			addErrorMessage(sessionMBean.getTextos().get("el_valor_del_dato_es_obligatorio"), "form2:idValor");
			hayError = true;
		} 
		if(getDatoDelRecursoNuevo().getOrden() == null){
			addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "form2:VOrdDato");
			hayError = true;
		} 
		
		if(!hayError) {
			try {
				recursosEJB.agregarDatoDelRecurso(sessionMBean.getRecursoSeleccionado(), getDatoDelRecursoNuevo());
				addInfoMessage(sessionMBean.getTextos().get("dato_creado"), MSG_ID);
				datoDelRecursoNuevo = null;
				sessionMBean.setMostrarAgregarDato(false);
			} catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
			}
		}
	}

	public void guardarModifDato(ActionEvent event) {
		if (sessionMBean.getDatoDelRecursoSeleccionado() != null) {			
			limpiarMensajesError();
			boolean hayError = false;
			if(sessionMBean.getDatoDelRecursoSeleccionado().getEtiqueta() == null || sessionMBean.getDatoDelRecursoSeleccionado().getEtiqueta().trim().isEmpty()){
				addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "form2:etiqueta");
				hayError = true;
			} 
			if(sessionMBean.getDatoDelRecursoSeleccionado().getValor() == null || sessionMBean.getDatoDelRecursoSeleccionado().getValor().trim().isEmpty()){
				addErrorMessage(sessionMBean.getTextos().get("el_valor_del_dato_es_obligatorio"), "form2:valor");
				hayError = true;
			} 
			if(sessionMBean.getDatoDelRecursoSeleccionado().getOrden() == null){
				addErrorMessage(sessionMBean.getTextos().get("la_etiqueta_del_dato_es_obligatoria"), "form2:VOrden");
				hayError = true;
			} 
			
			if(!hayError) {
	 			try {
	 				recursosEJB.modificarDatoDelRecurso(sessionMBean.getDatoDelRecursoSeleccionado());
	 				addInfoMessage(sessionMBean.getTextos().get("dato_modificado"), MSG_ID);
	 				sessionMBean.setDatoDelRecursoSeleccionado(null);
	 			} catch (Exception e) {
	 				addErrorMessage(e, MSG_ID);
	 			}
			}
		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_dato_seleccionado"), MSG_ID);
		}
	}

	public void cancelarModifDato(ActionEvent event) {
		sessionMBean.setDatoDelRecursoSeleccionado(null);
		sessionMBean.cargarDatosDelRecurso();
		sessionMBean.setDatoDelRecursoSeleccionado(null);
	}

	public void cancelarAgregarDato(ActionEvent event) {
		sessionMBean.setMostrarAgregarDato(false);
		sessionMBean.cargarDatosDelRecurso();
	}
	
	@SuppressWarnings("unchecked")
	public void seleccionarDato(ActionEvent e) {
		DatoDelRecurso d = ((Row<DatoDelRecurso>) this.getDatosDataTable().getRowData()).getData();
  	sessionMBean.setMostrarAgregarDato(false);
		sessionMBean.setDatoDelRecursoSeleccionado(d); 
	}

	public void mostrarDatoDelRecurso(ActionEvent e) {
		sessionMBean.setMostrarAgregarDato(true);
		sessionMBean.setDatoDelRecursoSeleccionado(null);
	}
	
	@SuppressWarnings("unchecked")
	public void selecDatoEliminar(ActionEvent e){
		
		this.getSessionMBean().setDatoDelRecursoSeleccionado(((Row<DatoDelRecurso>) this.getDatosDataTable().getRowData()).getData());
	}

	public void eliminarDatoDelRecurso(ActionEvent event) {
		DatoDelRecurso d = this.getSessionMBean().getDatoDelRecursoSeleccionado();
		if (d != null) {
 			try {
 				recursosEJB.eliminarDatoDelRecurso(d); 
 				sessionMBean.cargarDatosDelRecurso();
 				
 				sessionMBean.setMostrarAgregarDato(false);
 				sessionMBean.setDatoDelRecursoSeleccionado(null);
 				addInfoMessage(sessionMBean.getTextos().get("dato_eliminado"), MSG_ID);
 			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			} finally {
 				this.getSessionMBean().setDatoDelRecursoSeleccionado(null);
 			}
		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_dato_seleccionado"), MSG_ID);
		}
	}
	
	/**************************************************************************/
	/*               Action  de Datos del Recurso  (navegación)               */	
	/**************************************************************************/	

	@SuppressWarnings("unchecked")
	public String consultarDatos() throws ApplicationException, BusinessException {
        //Se busca posición que se quiere consultar
    	Recurso r = ((Row<Recurso>) this.getRecursosDataTableConsultar().getRowData()).getData();
    	
		if (r != null) {
	        //La siguiente línea no está desplegando el recurso
			sessionMBean.setRecursoSeleccionado(r);
			List<DatoDelRecurso> datosDelRecurso= recursosEJB.consultarDatosDelRecurso(r);
			sessionMBean.getRecursoSeleccionado().setDatoDelRecurso(datosDelRecurso);
			return "consultarDatos";
		} else {
			sessionMBean.setRecursoSeleccionado(null);
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			return null;
		}
	}

	public String consultarRecursos() throws ApplicationException {
    if (sessionMBean.getAgendaMarcada() != null){
			return "consultarRecursos";
		} else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
			return null;
		}
	}

	//Navega a pantalla modificarConsultar para los recursos
	public String volverModificarConsultar() throws ApplicationException {
    if (sessionMBean.getAgendaMarcada() != null){
			return "volverModificarConsultar";
		}
		else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
			return null;
		}
	}

	public RecursoSessionMBean getRecursoSessionMBean() {
		return recursoSessionMBean;
	}

	public void setRecursoSessionMBean(RecursoSessionMBean recursoSessionMBean) {
		this.recursoSessionMBean = recursoSessionMBean;
	}
	
	
	public void beforePhaseCrear(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("crear_recurso"));
		}
	}
	
	public void beforePhaseModificarConsultar(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
				sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("consultar_recursos"));
		}
	}
	
	public void beforePhaseModificar(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
				sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_recurso"));
		}
	}

	public void beforePhaseImportar(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
				sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("importar_recurso"));
		}
	}
	
	public void setOficinas(List<Oficina> ofs) {
		mapOficinas = new HashMap<String, Oficina>();
		oficinas = new ArrayList<SelectItem>();
		oficinas.add(new SelectItem(0, "Sin especificar"));
		if(ofs == null) {
			return;
		}
		for(Oficina of : ofs) {
			oficinas.add(new SelectItem(of.getId(), of.getNombre()));
			mapOficinas.put(of.getId(), of);
		}
	}
	
	public List<SelectItem> getOficinas() {
		return oficinas;
	}
	
	public void cambioOficina(ValueChangeEvent event) {
		String ofId = (String) event.getNewValue();
		actualizarOficinaRecurso(ofId);
	}
	
	public void actualizarOficinaRecurso(String ofId) {
		if(ofId!=null && mapOficinas.containsKey(ofId)) {
			Oficina oficina = mapOficinas.get(ofId);
			this.recursoNuevo.setDescripcion(oficina.getComentarios());			
			this.recursoNuevo.setOficinaId(ofId);
			this.recursoNuevo.setDireccion(oficina.getDireccion());
			this.recursoNuevo.setLocalidad(oficina.getLocalidad());
			this.recursoNuevo.setDepartamento(oficina.getDepartamento());
			this.recursoNuevo.setTelefonos(oficina.getTelefonos());
			this.recursoNuevo.setHorarios(oficina.getHorarios());
		}
	}
	
	public String recargarOficinas(ActionEvent event) {
		//Cargar las oficinas segun el tramite asociado a la agenda
		try {
			//Cargar la lista de oficinas para el tramite asociado a la agenda
			Agenda agenda = sessionMBean.getAgendaMarcada();
			
      List<Oficina> oficinas = cargarOficinasTramitesAgenda(agenda, true);
      setOficinas(oficinas);
			
			String msg = sessionMBean.getTextos().get("se_cargaron_n_oficinas");
			if(msg!=null) {
				addInfoMessage(msg.replace("{cant}", ""+(oficinas==null?"0":""+oficinas.size())));
			}
		} catch (ApplicationException aEx) {
			setOficinas(null);
			addErrorMessage(sessionMBean.getTextos().get("no_se_pudo_cargar_oficinas"));
		}
		return null;
	}

	/**
	 * Exporta la configuración del recurso a un archivo XML para poder importarla en otra agenda
	 */
	@SuppressWarnings("unchecked")
  public void selecRecursoExportar() {
    Recurso recurso = ((Row<Recurso>) this.getRecursosDataTableConsultar().getRowData()).getData();
		if (recurso != null) {
			try {
				byte[] bytes = recursosEJB.exportarRecurso(recurso, sessionMBean.getVersion());
				
				FacesContext fc = FacesContext.getCurrentInstance();
		    ExternalContext ec = fc.getExternalContext();

		    ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
		    ec.setResponseContentType("application/xml"); 
		    ec.setResponseContentLength(bytes.length);
		    ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + recurso.getNombre() + ".xml" + "\"");
		    OutputStream output = ec.getResponseOutputStream();
		    output.write(bytes);
		    output.flush();
		    output.close();
		    fc.responseComplete();
				
			} catch (Exception e) {
				e.printStackTrace();
				addErrorMessage(e, MSG_ID);
			}
		}
	}
	
	public void subirArchivo(FileUploadEvent event) {
		archivo = event.getFile();
		addInfoMessage(sessionMBean.getTextos().get("archivo_cargado"), MSG_ID);
	}
	
	public void importar(){
		
		if(archivo==null || archivo.getSize()<1) {
			addErrorMessage(sessionMBean.getTextos().get("debe_cargar_un_archivo"), "archivo_input");
			return;
		}
		
		try {
			byte[] bytes = IOUtils.toByteArray(archivo.getInputstream());
			Recurso recurso = recursosEJB.importarRecurso(sessionMBean.getAgendaMarcada(), bytes, sessionMBean.getVersion(), sessionMBean.getUsuarioActual().getCodigo());
			if(recurso != null) {
				/*
				  Se hace este chequeo porque cuando se accede con un usuario 'Administrador de recursos', luego de hacer alguna
				modificación en alguno de sus recursos, la aplicación recargaba la lista de recursos y se perdía la selección
				del recurso que le daba acceso a las opciones del menú
				*/
				if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR_DE_RECURSOS" }) || sessionMBean.getUsuarioActual().getSuperadmin()) {
					sessionMBean.cargarRecursos();
				} else {
					sessionMBean.addRecursoALista(recurso);
				}
				addInfoMessage(sessionMBean.getTextos().get("recurso_importado_exitosamente"), MSG_ID);
			}
		} catch (IOException e) {
			addErrorMessage(sessionMBean.getTextos().get("no_se_pudo_cargar_el_archivo"), MSG_ID);
		}catch (UserException aEx) {
			addErrorMessage(aEx, MSG_ID);
		}
	}
	
	private List<Oficina> cargarOficinasTramitesAgenda(Agenda agenda, boolean actualizar) throws ApplicationException {
    List<Oficina> oficinas = new ArrayList<Oficina>();
    List<TramiteAgenda> tramites = generalEJB.consultarTramites(agenda);
    for(TramiteAgenda tramite : tramites) {
      oficinas.addAll(empresasEJB.obtenerOficinasTramite(tramite.getTramiteId(), actualizar));
    }
    return oficinas;
	}
	
	
	public List<SelectItem> getTamaniosFuentes() {
	  return tamaniosFuentes;
	}

  public List<SelectItem> getNombresFuentes() {
    return nombresFuentes;
  }
  
  public List<SelectItem> getcambiosUnidades() {
    return cambiosUnidades;
  }
  
  public List<SelectItem> getCancelacionUnidades() {
    return cancelacionUnidades;
  }

  /**
   * Método que genera un ticket dummy con una reserva falsa, utilizado para probar cómo queda luego de cambiar la fuente
   * @return
   */
  public String generarTicket(Recurso recurso) {
    
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.MILLISECOND, sessionMBean.getTimeZone().getOffset(cal.getTimeInMillis()));
    Reserva reserva = new Reserva();
    reserva.setCodigoSeguridad("00000");
    reserva.setNumero(0);
    reserva.setTrazabilidadGuid("XXXXXXXXX");
    reserva.setId(0);
    Disponibilidad disp = new Disponibilidad();
    disp.setFecha(cal.getTime());
    disp.setHoraInicio(cal.getTime());
    reserva.getDisponibilidades().add(disp);
    
    TicketUtiles ticketUtiles = new TicketUtiles();
    ticketUtiles.generarTicket(sessionMBean.getEmpresaActual(), sessionMBean.getAgendaMarcada(), recurso, sessionMBean.getTimeZone(), 
        reserva, sessionMBean.getFormatoFecha(), sessionMBean.getFormatoHora(), sessionMBean.getTextos(), false);
    
    return null;
  }
  
  public void reporteRecursos(ActionEvent e) {
      limpiarMensajesError();
      boolean hayErrores = false;
      
   
      try {
          Agenda agendaMarcada = sessionMBean.getAgendaMarcada();
          List<Recurso> recursos1 = generalEJB.consultarRecursos(agendaMarcada);
          List<List<TableCellValue>> contenido = new ArrayList<>();
          
          //Datos a desplegar en el reporte, en este casos las reservas por fecha y hora
          LabelValue[] filtros = {
        		  new CommonLabelValueImpl(sessionMBean.getTextos().get("agenda") + ": ", agendaMarcada.getNombre())
          };


          for (Recurso recurso: recursos1) {
              List<TableCellValue> filaDatos = new ArrayList<>();
              filaDatos.add(new TableCellValue(recurso.getId()));
              filaDatos.add(new TableCellValue(recurso.getNombre()));
              filaDatos.add(new TableCellValue(recurso.getDescripcion()));
              filaDatos.add(new TableCellValue(Utiles.date2string(recurso.getFechaInicio(), Utiles.DIA)));
              filaDatos.add(new TableCellValue(Utiles.date2string(recurso.getFechaFin(), Utiles.DIA)));
              filaDatos.add(new TableCellValue(Utiles.date2string(recurso.getFechaInicioDisp(), Utiles.DIA)));
              filaDatos.add(new TableCellValue(recurso.getDireccion()));
              filaDatos.add(new TableCellValue(recurso.getDepartamento()));
              filaDatos.add(new TableCellValue(recurso.getLocalidad()));
              filaDatos.add(new TableCellValue(recurso.getLatitud().toString()));
              filaDatos.add(new TableCellValue(recurso.getLongitud().toString()));
              filaDatos.add(new TableCellValue(recurso.getTelefonos()));
              filaDatos.add(new TableCellValue(recurso.getHorarios()));
              contenido.add(filaDatos);
          }

          String[] cabezales = {sessionMBean.getTextos().get("identificador"), sessionMBean.getTextos().get("nombre"), sessionMBean.getTextos().get("descripcion"),
              sessionMBean.getTextos().get("inicio_de_vigencia"), sessionMBean.getTextos().get("fin_de_vigencia"), sessionMBean.getTextos().get("inicio_de_disponibilidad"),
              sessionMBean.getTextos().get("direccion"), sessionMBean.getTextos().get("departamento"),
              sessionMBean.getTextos().get("localidad"), sessionMBean.getTextos().get("latitud"), sessionMBean.getTextos().get("longitud"),
              sessionMBean.getTextos().get("telefonos"), sessionMBean.getTextos().get("horarios")
          };
          StandardCSVFile fileCSV = new StandardCSVFile(filtros, cabezales, contenido);

          String nombre = sessionMBean.getTextos().get("reporte_recursos_por_agenda");
          nombre = nombre.replace(" ", "_");

          CSVWebFilePrinter printer = new CSVWebFilePrinter(fileCSV, nombre);
          printer.print();
      } catch (Exception e1) {
          addErrorMessage(e1);
      }
  }
  
  
  public void setNullReservaMultipleTiempoMax() {
	  if(sessionMBean.getRecursoSeleccionado()!=null){
		  sessionMBean.getRecursoSeleccionado().setReservaMultiplePendienteTiempoMax(0);  
	  }
	  
	  if(recursoNuevo!=null){
		  recursoNuevo.setReservaMultiplePendienteTiempoMax(0);
	  }
	  
  }
  
	
}
