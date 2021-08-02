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
import java.util.Collections;
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
import uy.gub.imm.sae.web.common.RecursoComparatorNombre;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;
import uy.gub.imm.sae.web.common.TicketUtiles;

public class ActualizacionMasivaMBean extends BaseMBean{

	public static final String MSG_ID = "pantalla";
	public static final String FORM_ID = "form";
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/UsuariosEmpresasBean!uy.gub.imm.sae.business.ejb.facade.UsuariosEmpresasRemote")
	private UsuariosEmpresas empresasEJB;
	
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
  	private AgendaGeneral generalEJB;
	
	private Agenda agenda;
	private SessionMBean sessionMBean;
	private Recurso recursoNuevo;
	private RecursoSessionMBean recursoSessionMBean;
	
	private Integer diasInicioVentanaIntranet;
	private Integer diasVentanaIntranet;
	private Integer diasInicioVentanaInternet;
	private Integer diasVentanaInternet;
	private RowList<Recurso> recursos;
  
    @PostConstruct
    public void initRecurso(){
	    limpiarMensajesError();
	    
	    
	    
	    try {
	    	List<Recurso> recursosList = generalEJB.consultarRecursos(agenda);
			Collections.sort(recursosList, new RecursoComparatorNombre());
			recursos = new RowList<>(recursosList);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}
    
    
    public void beforePhaseActualizar(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("crear_recurso"));
		}
	}
    
    
    /**
	 * Este método es usado para la pantalla de selección de recurso.
	 * Devuelve la lista de recursos de la agenda actual, a la cual añade un recurso adicional, con id=0 y nombre "ninguno"
	 * para permitir "desseleccionar" el recurso seleccionado (usado para generar reportes independientes del recurso)	 * 
	 * */
	public RowList<Recurso> getRecursosSeleccion() {
		
		
		RowList<Recurso> ret = new RowList<Recurso>();
		if(recursos !=null) {
			ret.addAll(recursos);
		}

		Recurso ninguno = new Recurso();
		ninguno.setId(0);
		ninguno.setNombre(sessionMBean.getTextos().get("ninguno"));
		ninguno.setDescripcion(sessionMBean.getTextos().get("ninguno"));
		ret.add(0, new Row<Recurso>(ninguno, ret));
		
		return ret;
	}
    
    public void actualizar(){
    	
    	
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

	
	
	

	public Integer getDiasInicioVentanaIntranet() {
		return diasInicioVentanaIntranet;
	}

	public void setDiasInicioVentanaIntranet(Integer diasInicioVentanaIntranet) {
		this.diasInicioVentanaIntranet = diasInicioVentanaIntranet;
	}

	public Integer getDiasVentanaIntranet() {
		return diasVentanaIntranet;
	}

	public void setDiasVentanaIntranet(Integer diasVentanaIntranet) {
		this.diasVentanaIntranet = diasVentanaIntranet;
	}

	public Integer getDiasInicioVentanaInternet() {
		return diasInicioVentanaInternet;
	}

	public void setDiasInicioVentanaInternet(Integer diasInicioVentanaInternet) {
		this.diasInicioVentanaInternet = diasInicioVentanaInternet;
	}

	public Integer getDiasVentanaInternet() {
		return diasVentanaInternet;
	}

	public void setDiasVentanaInternet(Integer diasVentanaInternet) {
		this.diasVentanaInternet = diasVentanaInternet;
	}



	public RowList<Recurso> getRecursos() {
		return recursos;
	}



	public void setRecursos(RowList<Recurso> recursos) {
		this.recursos = recursos;
	}



	

	

	
	
	

	
  
	
}
