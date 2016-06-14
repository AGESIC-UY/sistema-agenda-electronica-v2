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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.primefaces.component.datatable.DataTable;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresas;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.global.Oficina;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;

public class RecursoMBean extends BaseMBean{

	public static final String MSG_ID = "pantalla";
	public static final String FORM_ID = "form";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas empresasEJB;
	
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

	@PostConstruct
	public void initRecurso(){
		limpiarMensajesError();
		
		Agenda agenda = sessionMBean.getAgendaMarcada();
		//Se controla que se haya Marcado una agenda para trabajar con los recursos
		if (agenda == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		
		try {
			//Cargar la lista de oficinas para el tramite asociado a la agenda
			if(agenda != null && agenda.getTramiteId() != null) {
				List<Oficina> oficinas = empresasEJB.obtenerOficinasTramite(agenda.getTramiteId(), false);
				setOficinas(oficinas);
			}else {
				setOficinas(null);
			}
			
		}catch(ApplicationException aEx) {
			addErrorMessage(aEx.getMessage());
			aEx.printStackTrace();
		}
		
	}

	/**************************************************************************/
	/*                        Action Listener de Recurso                      */	
	/**************************************************************************/	
	
	public void crear(ActionEvent e) {
		
		limpiarMensajesError();
		
		boolean error = false;
		if(getRecursoNuevo().getNombre() == null || getRecursoNuevo().getNombre().equals("")){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_recurso_es_obligatorio"), FORM_ID+":nombre");
		}
		if(getRecursoNuevo().getDescripcion() == null || getRecursoNuevo().getDescripcion().equals("")){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_descripcion_del_recurso_es_obligatoria"), FORM_ID+":descripcion");
			
		}
		
		Recurso r = getRecursoNuevo();
		r.setVentanaCuposMinimos(0);
		if (r.getFechaInicio() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), FORM_ID+":fechaInicio");
		}else {
			r.setFechaInicio(Utiles.time2InicioDelDia(r.getFechaInicio()));
		}
		if(r.getFechaFin() == null) {
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), FORM_ID+":fechaFin");
		} else {
			r.setFechaFin(Utiles.time2FinDelDia(r.getFechaFin()));
		}
		
		if(r.getFechaInicio() != null && r.getFechaFin() != null && r.getFechaInicio().compareTo(r.getFechaFin()) > 0) {
			error = true;
			addErrorMessage("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio", FORM_ID+":fechaFin",FORM_ID+":fechaInicio");
		}
		if(r.getFechaInicioDisp() == null) {
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), FORM_ID+":fechaIniDispon");
		}else {
			r.setFechaInicioDisp(Utiles.time2InicioDelDia(r.getFechaInicioDisp()));
		}
		//Si la fecha de Fin de disponibilidad no es nula, se setea la hora al final del Día.
		if (r.getFechaFinDisp() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), FORM_ID+":fechaFinDispon");
		} else {
			r.setFechaFinDisp(Utiles.time2FinDelDia(r.getFechaFinDisp()));	
		}
		if (r.getFechaInicioDisp() != null && r.getFechaFinDisp() != null && r.getFechaInicioDisp().compareTo(r.getFechaFinDisp()) > 0){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), FORM_ID+":fechaFinDispon",FORM_ID+":fechaIniDispon");
		}
		
		if (r.getFechaInicioDisp() != null && r.getFechaInicio()!= null && r.getFechaInicio().compareTo(r.getFechaInicioDisp()) > 0) {
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso"), FORM_ID+":fechaInicio",FORM_ID+":fechaIniDispon");
		}
		if (r.getFechaFinDisp() != null && r.getFechaFin()!= null && r.getFechaFinDisp().compareTo(r.getFechaFin()) > 0) {
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso"), FORM_ID+":fechaFin",FORM_ID+":fechaFinDispon");
		}
		
		
		if (r.getDiasInicioVentanaIntranet() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":diasVIntranet");
		}
		if (r.getDiasInicioVentanaIntranet() != null && r.getDiasInicioVentanaIntranet() < 0){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_a_cero"), FORM_ID+":diasVIntranet");
		}
		if (r.getDiasVentanaIntranet() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":DiasVIntranet");
		}
		if (r.getDiasVentanaIntranet() != null && r.getDiasVentanaIntranet() <= 0){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero"), FORM_ID+":DiasVIntranet");
		}
		if (r.getDiasInicioVentanaInternet() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":DiasInicioVInternet");
		}
		if (r.getDiasInicioVentanaInternet()!=null && r.getDiasInicioVentanaInternet() < 0){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_a_cero"), FORM_ID+":DiasInicioVInternet");
		}
		if (r.getDiasVentanaInternet() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_es_obligatorio"), FORM_ID+":DiasVInternet");
		}
		if (r.getDiasVentanaInternet()!=null && r.getDiasVentanaInternet() <= 0){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero"), FORM_ID+":DiasVInternet");
		}
		if (r.getVentanaCuposMinimos() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_es_obligatoria"), FORM_ID+":VCuposM");
		}
		if (r.getVentanaCuposMinimos()!=null && r.getVentanaCuposMinimos() < 0 ){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":VCuposM");
		}
		if (r.getCantDiasAGenerar() == null){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_es_obligatoria"), FORM_ID+":CantDias");
		}
		if (r.getCantDiasAGenerar()!=null && r.getCantDiasAGenerar() <= 0 ){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero"), FORM_ID+":CantDias");
		}
		if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaIntranet() + r.getDiasVentanaIntranet()) < 0 ){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet"), FORM_ID+":CantDias");
		}
		if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaInternet() + r.getDiasVentanaInternet()) < 0 ){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet"), FORM_ID+":CantDias");
		}
		if ((r.getLargoListaEspera() != null) && (r.getLargoListaEspera() <= 0 )){
			error = true;
			addErrorMessage(sessionMBean.getTextos().get("el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero"), FORM_ID+":largoLista");
		}
		r.setNombre(getRecursoNuevo().getNombre().trim());
		r.setDescripcion(getRecursoNuevo().getDescripcion().trim());
		
		try {
			
			if(r.getAgenda()==null)
			{
				r.setAgenda(sessionMBean.getAgendaMarcada());
			}
			if(recursosEJB.existeRecursoPorNombre(r))
			{
				error = true;
				addErrorMessage(sessionMBean.getTextos().get("ya_existe_un_recurso_con_el_nombre_especificado"), FORM_ID+":nombre");
			}
		} catch (ApplicationException e1) {
			// TODO Auto-generated catch block
			addErrorMessage(e1, MSG_ID);
		}
		
		if(!error) {
			try {
				
				
				recursosEJB.crearRecurso(sessionMBean.getAgendaMarcada(), r);
				sessionMBean.cargarRecursos();
				sessionMBean.desmarcarRecurso();
				//Se blanquea la info en la página
				this.setRecursoNuevo(null);
				addInfoMessage(sessionMBean.getTextos().get("recurso_creado"), MSG_ID);
			
			
			} catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void selecRecursoEliminar(ActionEvent e){
		sessionMBean.setRecursoSeleccionado(((Row<Recurso>)this.getRecursosDataTableConsultar().getRowData()).getData());
	}
	
	
	public void eliminar(ActionEvent ev) {
		limpiarMensajesError();
		
		Recurso r = this.sessionMBean.getRecursoSeleccionado();
		if ( r != null) {
 			try {
 				if(sessionMBean.getRecursoMarcado()!=null && sessionMBean.getRecursoMarcado().getId().equals(r.getId())) {
 					sessionMBean.desseleccionarRecurso();
 				}
 				
 				recursosEJB.eliminarRecurso(r);
 				sessionMBean.cargarRecursos();
 				sessionMBean.desmarcarRecurso();

 				addInfoMessage(sessionMBean.getTextos().get("recurso_eliminado"), MSG_ID);
 			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			} finally {
 				this.sessionMBean.setRecursoSeleccionado(null);
 			}
 		}
		else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
	}

	/**************************************************************************/
	/*                       Action  de Recurso  (navegación)                 */	
	/**************************************************************************/	
	
	@SuppressWarnings("unchecked")
	public String modificar() {

    Recurso r = ((Row<Recurso>) this.getRecursosDataTableConsultar().getRowData()).getData();
		
		if (r != null) {
			sessionMBean.setRecursoSeleccionado(r);
			
			//Se agrega para que si cambiamos de recurso no queden cargados los datos viejos 
			sessionMBean.setDatoDelRecursoSeleccionado(null);
			sessionMBean.setMostrarAgregarDato(false);

			//sessionMBean.cargarDatosDelRecurso();
			
			recursoNuevo = r;
			
			return "modificar";
		}
		else {
			sessionMBean.setRecursoSeleccionado(null);
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			return null;
		}
		
	}

	@SuppressWarnings("unchecked")
	public void copiar(ActionEvent event) {
    Recurso r = ((Row<Recurso>) this.getRecursosDataTableConsultar().getRowData()).getData();
		if (r != null) {
			try {
				recursosEJB.copiarRecurso(r);
				sessionMBean.cargarRecursos();
				sessionMBean.desmarcarRecurso();
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
 				boolean error = false;
 				
 				// Se hace un trim del nombre del recurso, para evitar que
 				// tenga blancos al principio o al final
 				sessionMBean.getRecursoSeleccionado().setVentanaCuposMinimos(0);
 				sessionMBean.getRecursoSeleccionado().setNombre(sessionMBean.getRecursoSeleccionado().getNombre().trim());
 				sessionMBean.getRecursoSeleccionado().setDescripcion(sessionMBean.getRecursoSeleccionado().getDescripcion().trim());
 				Recurso r = sessionMBean.getRecursoSeleccionado();
 				if(r.getNombre() == null || r.getNombre().equals("")){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("el_nombre_del_recurso_es_obligatorio"), FORM_ID+":nombre");
 				}
 				if(r.getDescripcion() == null || r.getDescripcion().equals("")){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_descripcion_del_recurso_es_obligatoria"), FORM_ID+":descripcion");
 				}
 				if (r.getFechaInicio() == null){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), FORM_ID+":fechaInicio");
 				}else {
 					r.setFechaInicio(Utiles.time2InicioDelDia(r.getFechaInicio()));
 				}
 				if(r.getFechaFin()==null) {
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), FORM_ID+":fechaFin");
 				} else {
 					r.setFechaFin(Utiles.time2FinDelDia(r.getFechaFin()));
 				}
 				if(r.getFechaInicio() != null && r.getFechaFin() != null && r.getFechaInicio().compareTo(r.getFechaFin()) > 0 ) {
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), FORM_ID+":fechaFin",FORM_ID+":fechaInicio");
 				}
 				if(r.getFechaInicioDisp() == null) {
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_es_obligatoria"), FORM_ID+":fechaInicioDispon");
 				}else {
 					r.setFechaInicioDisp(Utiles.time2InicioDelDia(r.getFechaInicioDisp()));
 				}
 				if(r.getFechaFinDisp() == null) {
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_es_obligatoria"), FORM_ID+":fechaFinDispon");
 				}else {
 					r.setFechaFinDisp(Utiles.time2FinDelDia(r.getFechaFinDisp()));	
 				}
 				if(r.getFechaInicioDisp()!=null && r.getFechaFinDisp() != null && r.getFechaInicioDisp().compareTo(r.getFechaFinDisp()) > 0 ){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio"), FORM_ID+":fechaFinDispon",FORM_ID+":fechaInicioDispon");
 				}
 				
 				if (r.getFechaInicioDisp() != null && r.getFechaInicio()!= null && r.getFechaInicio().compareTo(r.getFechaInicioDisp()) > 0) {
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_inicio_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso"), FORM_ID+":fechaInicio",FORM_ID+":fechaIniDispon");
 				}
 				if (r.getFechaFinDisp() != null && r.getFechaFin()!= null && r.getFechaFinDisp().compareTo(r.getFechaFin()) > 0) {
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_fecha_de_fin_debe_ser_igual_o_anterior_a_la_fecha_de_fin_de_la_disponibilidad_del_recurso"), FORM_ID+":fechaFin",FORM_ID+":fechaFinDispon");
 				}
 				
 				
 				if (r.getDiasInicioVentanaIntranet() == null){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio"), FORM_ID+":diasVIntranet");
 				}
 				if (r.getDiasInicioVentanaIntranet()!=null && r.getDiasInicioVentanaIntranet() < 0){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_a_cero"), FORM_ID+":diasVIntranet");
 				}
 				if (r.getDiasVentanaInternet() == null){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_es_obligatorio"), FORM_ID+":DiasVInternet");
 				}
 				if (r.getDiasVentanaInternet() != null && r.getDiasVentanaInternet() <= 0){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero"), FORM_ID+":DiasVInternet");
 				}
 				if (r.getDiasInicioVentanaInternet() == null){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_internet_es_obligatorio"), FORM_ID+":diasIVInternet");
 				}
 				if (r.getDiasInicioVentanaInternet() != null && r.getDiasInicioVentanaInternet() < 0){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_a_cero"), FORM_ID+":diasIVInternet");
 				}
 				if (r.getVentanaCuposMinimos() == null){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_es_obligatoria"), FORM_ID+":VCuposMin");
 				}
 				if (r.getVentanaCuposMinimos()!=null && r.getVentanaCuposMinimos() < 0 ){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero"), FORM_ID+":VCuposMin");
 				}
 				if (r.getCantDiasAGenerar() == null){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_es_obligatoria"), FORM_ID+":vDiasGen");
 				}
 				if (r.getCantDiasAGenerar()!=null && r.getCantDiasAGenerar() <= 0 ){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero"), FORM_ID+":vDiasGen");
 				}
 				if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaIntranet() + r.getDiasVentanaIntranet()) < 0 ){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet"), FORM_ID+":vDiasGen");
 				}
 				if (r.getCantDiasAGenerar().compareTo(r.getDiasInicioVentanaInternet() + r.getDiasVentanaInternet()) < 0 ){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet"), FORM_ID+":vDiasGen");
 				}
 				if ((r.getLargoListaEspera() != null) && (r.getLargoListaEspera() <= 0 )){
 					error = true;
 					addErrorMessage(sessionMBean.getTextos().get("el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero"), FORM_ID+":largoListaEspera");
 				}
 				r.setNombre(sessionMBean.getRecursoSeleccionado().getNombre().trim());
 				r.setDescripcion(sessionMBean.getRecursoSeleccionado().getDescripcion().trim());
 				
 				try {
 					
 					if(r.getAgenda()==null)
 					{
 						r.setAgenda(sessionMBean.getAgendaMarcada());
 					}
 					if(recursosEJB.existeRecursoPorNombre(r))
 					{
 						error = true;
 						addErrorMessage(sessionMBean.getTextos().get("ya_existe_un_recurso_con_el_nombre_especificado"), FORM_ID+":nombre");
 					}
 				} catch (ApplicationException e1) {
 					addErrorMessage(e1, MSG_ID);
 				}
 				
 				if(error) {
 					return null;
 				}
 				
 				recursosEJB.modificarRecurso(sessionMBean.getRecursoSeleccionado());
 				addInfoMessage(sessionMBean.getTextos().get("recurso_modificado"), MSG_ID); 				
				sessionMBean.cargarRecursos();
 				return "guardar";
 			} catch (Exception e) {
 				addErrorMessage(e, MSG_ID);
 			}
		}
		else {
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
	 				sessionMBean.setDatoDelRecursoSeleccionado(null);;
	 			} catch (Exception e) {
	 				addErrorMessage(e, MSG_ID);
	 			}
			}
		}
		else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_dato_seleccionado"), MSG_ID);
		}
	}

	public void cancelarModifDato(ActionEvent event) {
		sessionMBean.setDatoDelRecursoSeleccionado(null);
		sessionMBean.cargarDatosDelRecurso();
		sessionMBean.setDatoDelRecursoSeleccionado(null);;
	}

	public void cancelarAgregarDato(ActionEvent event) {
		sessionMBean.setMostrarAgregarDato(false);
		sessionMBean.cargarDatosDelRecurso();
	}
	
	public String cancelarRecurso() {
		
		Recurso recurso = sessionMBean.getRecursoSeleccionado();
		
		Recurso recursoBase;
		try {
			recursoBase = recursosEJB.consultarRecurso(recurso);
			sessionMBean.getRecursoSeleccionado().setAgenda(recursoBase.getAgenda());
			sessionMBean.getRecursoSeleccionado().setCantDiasAGenerar(recursoBase.getCantDiasAGenerar());
			sessionMBean.getRecursoSeleccionado().setDepartamento(recursoBase.getDepartamento());
			sessionMBean.getRecursoSeleccionado().setDescripcion(recursoBase.getDescripcion());
			sessionMBean.getRecursoSeleccionado().setDiasInicioVentanaInternet(recursoBase.getDiasInicioVentanaInternet());
			sessionMBean.getRecursoSeleccionado().setDiasInicioVentanaIntranet(recursoBase.getDiasInicioVentanaIntranet());
			sessionMBean.getRecursoSeleccionado().setDiasVentanaInternet(recursoBase.getDiasVentanaInternet());
			sessionMBean.getRecursoSeleccionado().setDiasVentanaIntranet(recursoBase.getDiasVentanaIntranet());
			sessionMBean.getRecursoSeleccionado().setDireccion(recursoBase.getDireccion());
			
			
			sessionMBean.getRecursoSeleccionado().setFechaFin(recursoBase.getFechaFin());
			sessionMBean.getRecursoSeleccionado().setFechaFinDisp(recursoBase.getFechaFinDisp());
			sessionMBean.getRecursoSeleccionado().setFechaInicio(recursoBase.getFechaInicio());
			sessionMBean.getRecursoSeleccionado().setFechaInicioDisp(recursoBase.getFechaInicioDisp());
			sessionMBean.getRecursoSeleccionado().setHorarios(recursoBase.getHorarios());
			sessionMBean.getRecursoSeleccionado().setLargoListaEspera(recursoBase.getLargoListaEspera());
			sessionMBean.getRecursoSeleccionado().setLatitud(recursoBase.getLatitud());
			sessionMBean.getRecursoSeleccionado().setLocalidad(recursoBase.getLocalidad());
			sessionMBean.getRecursoSeleccionado().setLongitud(recursoBase.getLongitud());
			sessionMBean.getRecursoSeleccionado().setMostrarNumeroEnLlamador(recursoBase.getMostrarNumeroEnLlamador());
			sessionMBean.getRecursoSeleccionado().setMostrarNumeroEnTicket(recursoBase.getMostrarNumeroEnTicket());
			sessionMBean.getRecursoSeleccionado().setNombre(recursoBase.getNombre());
			sessionMBean.getRecursoSeleccionado().setReservaMultiple(recursoBase.getReservaMultiple());
			sessionMBean.getRecursoSeleccionado().setSabadoEsHabil(recursoBase.getSabadoEsHabil());
			sessionMBean.getRecursoSeleccionado().setSerie(recursoBase.getSerie());
			sessionMBean.getRecursoSeleccionado().setTelefonos(recursoBase.getTelefonos());
			sessionMBean.getRecursoSeleccionado().setVentanaCuposMinimos(recursoBase.getVentanaCuposMinimos());
			sessionMBean.getRecursoSeleccionado().setVisibleInternet(recursoBase.getVisibleInternet());
		} catch (UserException e) {
			e.printStackTrace();
		}
		
		return "cancelar";
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
		}
		else {
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
		}
		else {
			sessionMBean.setRecursoSeleccionado(null);
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			return null;
		}
	}

	public String consultarRecursos() throws ApplicationException {
    if (sessionMBean.getAgendaMarcada() != null){
			return "consultarRecursos";
		}
		else {
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

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			//
			if(sessionMBean.getAgendaMarcada()!=null)
			{
				sessionMBean.setPantallaTitulo("Crear recurso para trámite "+sessionMBean.getAgendaMarcada().getNombre());
			}else
			{
				sessionMBean.setPantallaTitulo("Crear recurso");
			}
			
		}
	}
	public void beforePhaseEliminar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Eliminar recurso");
		}
	}	
	public void beforePhaseConsultar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo("Consultar recurso");
		}
	}
	public void beforePhaseModificarConsultar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			if(sessionMBean.getAgendaMarcada()!=null)
			{
				sessionMBean.setPantallaTitulo("Consultar recurso para trámite "+sessionMBean.getAgendaMarcada().getNombre());
			}else
			{
				sessionMBean.setPantallaTitulo("Consultar recurso");
			}
			
		}
	}
	
	public void beforePhaseModificar(PhaseEvent event) {

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			if(sessionMBean.getAgendaMarcada()!=null)
			{
				sessionMBean.setPantallaTitulo("Modificar recurso para trámite "+sessionMBean.getAgendaMarcada().getNombre());
			}else
			{
				sessionMBean.setPantallaTitulo("Modificar recurso");
			}
			
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
			String tramiteId = agenda.getTramiteId();
			List<Oficina> oficinas = empresasEJB.obtenerOficinasTramite(tramiteId, true);
			setOficinas(oficinas);
		} catch (ApplicationException aEx) {
			setOficinas(null);
			addErrorMessage(aEx.getMessage());
		}
		return null;
	}

	
}
