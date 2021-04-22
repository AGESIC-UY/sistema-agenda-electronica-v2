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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.context.FacesContext;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.SelectItemComparator;

public class TextoRecursoMBean  extends BaseMBean {

	public static final String MSG_ID = "pantalla";

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;
	private SessionMBean sessionMBean;
	private TextoRecurso textoRec = new TextoRecurso();
	
	
	public Recursos getRecursosEJB() {
		return recursosEJB;
	}
	public void setRecursosEJB(Recursos recursosEJB) {
		this.recursosEJB = recursosEJB;
	}

	public TextoRecurso getTextoRec() {
		return textoRec;
	}
	public void setTextoRec(TextoRecurso textoRec) {
		this.textoRec = textoRec;
	}
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}
	
	public Recurso getRecursoMarcado() {
		return sessionMBean.getRecursoMarcado();
	}
	


	@PostConstruct
	public void initTextoRec(){
		Recurso recurso = sessionMBean.getRecursoMarcado();
		
		//Se controla que se haya Marcado un recurso para trabajar con los textos
		if (recurso == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			return;
		}
		
		//Se carga el idioma por defecto para los textos
		List<SelectItem> idiomasSoportados = getIdiomasSoportados();
		if(idiomasSoportados.isEmpty()) {
			//La agenda no tiene ningun idioma definido
			addErrorMessage(sessionMBean.getTextos().get("no_se_ha_definido_ningun_idioma_valido_para_la_agenda"), MSG_ID);
			return;
		}
		//Ver si alguno de los idiomas de la agenda es el por defecto y es un idioma aun soportado
		idiomaTextos = null;
		if(recurso.getTextosRecurso()!=null) {
			Agenda agenda = recurso.getAgenda();
			for(TextoRecurso textoRecurso : recurso.getTextosRecurso().values()) {
				//Los textos del recurso no tienen indicador de si son por defecto o no, ya que deben usar el mismo que los textos de la agenda
				TextoAgenda textoAgenda = agenda.getTextosAgenda().get(textoRecurso.getIdioma());
				if(textoAgenda!=null && textoAgenda.isPorDefecto()) {
					if(agenda.getIdiomas()!=null && agenda.getIdiomas().contains(textoRecurso.getIdioma())) {
						//Es el idioma por defecto y sigue estando soportado
						idiomaTextos = textoRecurso.getIdioma();
					}
				}
			}
		}
		if(idiomaTextos == null) {
			//No se encontro el idioma por defecto o no esta soportado
			idiomaTextos = (String) idiomasSoportados.get(0).getValue();
			TextoRecurso textoRecurso = recurso.getTextosRecurso().get(idiomaTextos);
			if(textoRecurso == null) {
				textoRecurso = new TextoRecurso();
				textoRecurso.setIdioma(idiomaTextos);
			}
		}
		cargarTextosEditables(idiomaTextos);
		
	}


	public void guardar(ActionEvent e) {

		if (sessionMBean.getRecursoMarcado() != null) {
			try {
 				//Guardar los textos del idoma actual
 				Recurso recurso = sessionMBean.getRecursoMarcado();
 				TextoRecurso textoRecurso = recurso.getTextosRecurso().get(idiomaTextos);
 				if(textoRecurso == null) {
 					textoRecurso = new TextoRecurso();
 					recurso.getTextosRecurso().put(idiomaTextos, textoRecurso);
 				}
 				copiarTextoRecurso(textoRec, textoRecurso);
				
				recursosEJB.modificarRecurso(sessionMBean.getRecursoMarcado(), sessionMBean.getUsuarioActual().getCodigo());
				addInfoMessage(sessionMBean.getTextos().get("recurso_modificado"), MSG_ID); 
			} catch (Exception ex) {
				addErrorMessage(ex, MSG_ID);
			}
		}
		else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}

	}


	public void cancelar(ActionEvent event) {
		if (sessionMBean.getRecursoMarcado() == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
			return;
		}
	}

	public void beforePhaseModifTexto(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
		if (!sessionMBean.tieneRoles(new String[] { "RA_AE_ADMINISTRADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS" })) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
		}
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_textos_de_recursos"));
		}
	}

	//=======================================================================
	
	private String idiomaTextos = null;
	
	public String getIdiomaTextos() {
		return idiomaTextos;
	}
	public void setIdiomaTextos(String idiomaTextos) {
		this.idiomaTextos = idiomaTextos;
	}
	public List<SelectItem> getIdiomasSoportados() {
		Recurso recurso = sessionMBean.getRecursoMarcado();
		List<SelectItem> idiomas = new ArrayList<SelectItem>();
		if(recurso != null) {
			Agenda agenda = recurso.getAgenda();
			if(agenda != null && agenda.getIdiomas()!=null) {
				String[] aIdiomas = agenda.getIdiomas().split(",");
				for(String idioma : aIdiomas) {
					if(!idioma.trim().isEmpty()) {
						Locale locale = new Locale(idioma);
						idiomas.add(new SelectItem(locale.getLanguage(), locale.getDisplayLanguage(locale)));
					}
				}
			}
			Collections.sort(idiomas, new SelectItemComparator());
		}
		return idiomas;
	}
	
	public void cambioIdiomaTextos(ValueChangeEvent event) {
		//Guardar los textos del idioma actual
		Recurso recurso = sessionMBean.getRecursoMarcado();
		TextoRecurso textoRecurso = recurso.getTextosRecurso().get(idiomaTextos);
		if(textoRecurso == null) {
			textoRecurso = new TextoRecurso();
			textoRecurso.setRecurso(recurso);
			textoRecurso.setIdioma(idiomaTextos);
			recurso.getTextosRecurso().put(idiomaTextos, textoRecurso);
		}
		copiarTextoRecurso(textoRec, textoRecurso);
		//Poner el nuevo idioma		
		idiomaTextos = (String) event.getNewValue();
		cargarTextosEditables(idiomaTextos);
	}
	
	private void cargarTextosEditables(String idioma) {
		Recurso recurso = sessionMBean.getRecursoMarcado();
		if(recurso.getTextosRecurso() == null) {
			recurso.setTextosRecurso(new HashMap<String, TextoRecurso>());
		}
		TextoRecurso textoRecurso = null;
		if(recurso.getTextosRecurso().isEmpty()) {
			//Es el primer idioma que se configura, es el idioma por defecto
			textoRecurso = new TextoRecurso();
			textoRecurso.setRecurso(recurso);
			textoRecurso.setIdioma(idioma);
			recurso.getTextosRecurso().put(idioma, textoRecurso);
		}else {
			textoRecurso = recurso.getTextosRecurso().get(idioma);
			if(textoRecurso == null) {
				//Es un idioma nuevo pero no es el primero, no es por defecto
				textoRecurso = new TextoRecurso();
				textoRecurso.setRecurso(recurso);
				textoRecurso.setIdioma(idioma);
				recurso.getTextosRecurso().put(idioma, textoRecurso);
			}else {
				//Nada para hacer
			}
		}

		textoRec = new TextoRecurso();
		copiarTextoRecurso(textoRecurso, textoRec);
	}
	
	private void copiarTextoRecurso(TextoRecurso original, TextoRecurso copia) {
		copia.setRecurso(original.getRecurso());
		copia.setIdioma(original.getIdioma());
		copia.setTextoPaso2(original.getTextoPaso2());
		copia.setTextoPaso3(original.getTextoPaso3());
		copia.setTituloCiudadanoEnLlamador(original.getTituloCiudadanoEnLlamador());
		copia.setTituloPuestoEnLlamador(original.getTituloPuestoEnLlamador());
		copia.setTicketEtiquetaUno(original.getTicketEtiquetaUno());
		copia.setTicketEtiquetaDos(original.getTicketEtiquetaDos());
		copia.setValorEtiquetaUno(original.getValorEtiquetaUno());
		copia.setValorEtiquetaDos(original.getValorEtiquetaDos());
	}
	
	
}
