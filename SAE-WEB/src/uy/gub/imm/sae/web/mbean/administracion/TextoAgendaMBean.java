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

import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.Agendas;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.SelectItemComparator;

public class TextoAgendaMBean  extends BaseMBean{

	public static final String MSG_ID = "pantalla";

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendasBean!uy.gub.imm.sae.business.ejb.facade.AgendasRemote")
	private Agendas agendasEJB;

  @EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
  private AgendaGeneral generalEJB;
	
	public SessionMBean sessionMBean;
	public TextoAgenda textoAux = new TextoAgenda();
  private String idiomaTextos = null;
  
	public TextoAgenda getTextoAux() {
		return textoAux;
	}
	public void setTextoAux(TextoAgenda textoAux) {
		this.textoAux = textoAux;
	}
	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}
	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	@PostConstruct
	public void initTextoAg(){
		Agenda agenda = sessionMBean.getAgendaMarcada();
		//Se controla que se haya Marcado una agenda para trabajar con los textos
		if (agenda == null){
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
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
		if(agenda.getTextosAgenda()!=null) {
			for(TextoAgenda textoAgenda : agenda.getTextosAgenda().values()) {
				if(textoAgenda.isPorDefecto()) {
					if(agenda.getIdiomas()!=null && agenda.getIdiomas().contains(textoAgenda.getIdioma())) {
						//Es el idioma por defecto y sigue estando soportado
						idiomaTextos = textoAgenda.getIdioma();
					}else {
						//Era el idioma por defecto pero ya no esta soportado
						textoAgenda.setPorDefecto(false);
					}
				}
			}
		}
		if(idiomaTextos == null) {
			//No se encontro el idioma por defecto o no esta soportado
			idiomaTextos = (String) idiomasSoportados.get(0).getValue();
			TextoAgenda textoAgenda = agenda.getTextosAgenda().get(idiomaTextos);
			if(textoAgenda == null) {
				textoAgenda = new TextoAgenda();
				textoAgenda.setIdioma(idiomaTextos);
			}
			textoAgenda.setPorDefecto(true);
		}
		cargarTextosEditables(idiomaTextos);
	}
	
	public void guardar(ActionEvent e) {
		if(sessionMBean.getAgendaMarcada() != null) {
 			try {
 				//Guardar los textos del idoma actual
 				Agenda agenda = sessionMBean.getAgendaMarcada();
 				TextoAgenda textoAgenda = agenda.getTextosAgenda().get(idiomaTextos);
 				if(textoAgenda == null) {
 					textoAgenda = new TextoAgenda();
 					agenda.getTextosAgenda().put(idiomaTextos, textoAgenda);
 				}
 				copiarTextoAgenda(textoAux, textoAgenda);
 				//Si este idioma esta marcado como por defecto, desmarcar cualquier otro que pudiera haber
 				if(textoAgenda.isPorDefecto()) {
 					for(TextoAgenda ta : agenda.getTextosAgenda().values()) {
 						//Se compara la referencia, no el valor!
 						if(ta != textoAgenda) {
 							ta.setPorDefecto(false);
 						}
 					}
 				}
 				List<TramiteAgenda> tramites = generalEJB.consultarTramites(agenda);
 				agenda.setTramites(tramites);
				agendasEJB.modificarAgenda(agenda);
				addInfoMessage(sessionMBean.getTextos().get("agenda_modificada"), MSG_ID); 
 			}catch (Exception ex) {
 				addErrorMessage(ex, MSG_ID);
 			}
		}else {
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
	}
	
	public Agendas getAgendasEJB() {
		return agendasEJB;
	}
	public void setAgendasEJB(Agendas agendasEJB) {
		this.agendasEJB = agendasEJB;
	}
	
	public void beforePhaseModifTexto(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("modificar_textos_de_agendas"));
		}
	}
	
	public String getIdiomaTextos() {
		return idiomaTextos;
	}
	
	public void setIdiomaTextos(String idiomaTextos) {
		this.idiomaTextos = idiomaTextos;
	}
	
	public List<SelectItem> getIdiomasSoportados() {
		Agenda agenda = sessionMBean.getAgendaMarcada();
		List<SelectItem> idiomas = new ArrayList<SelectItem>();
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
		return idiomas;
	}
	
	public void cambioIdiomaTextos(ValueChangeEvent event) {
		//Guardar los textos del idioma actual
		Agenda agenda = sessionMBean.getAgendaMarcada();
		TextoAgenda textoAgenda = agenda.getTextosAgenda().get(idiomaTextos);
		if(textoAgenda == null) {
			textoAgenda = new TextoAgenda();
			textoAgenda.setAgenda(agenda);
			textoAgenda.setIdioma(idiomaTextos);
			agenda.getTextosAgenda().put(idiomaTextos, textoAgenda);
		}
		copiarTextoAgenda(textoAux, textoAgenda);
		//Poner el nuevo idioma		
		idiomaTextos = (String) event.getNewValue();
		cargarTextosEditables(idiomaTextos);
	}
	
	private void cargarTextosEditables(String idioma) {
		Agenda agenda = sessionMBean.getAgendaMarcada();
		if(agenda.getTextosAgenda() == null) {
			agenda.setTextosAgenda(new HashMap<String, TextoAgenda>());
		}
		TextoAgenda textoAgenda = null;
		if(agenda.getTextosAgenda().isEmpty()) {
			//Es el primer idioma que se configura, es el idioma por defecto
			textoAgenda = new TextoAgenda();
			textoAgenda.setIdioma(idioma);
			textoAgenda.setPorDefecto(true);
			agenda.getTextosAgenda().put(idioma, textoAgenda);
		}else {
			textoAgenda = agenda.getTextosAgenda().get(idioma);
			if(textoAgenda == null) {
				//Es un idioma nuevo pero no es el primero, no es por defecto
				textoAgenda = new TextoAgenda();
				textoAgenda.setIdioma(idioma);
				textoAgenda.setPorDefecto(false);
				agenda.getTextosAgenda().put(idioma, textoAgenda);
			}else {
				//Nada para hacer
			}
		}
		textoAux = new TextoAgenda();
		copiarTextoAgenda(textoAgenda, textoAux);
	}
	
	private void copiarTextoAgenda(TextoAgenda original, TextoAgenda copia) {
		copia.setAgenda(original.getAgenda());
		copia.setIdioma(original.getIdioma());
		copia.setPorDefecto(original.isPorDefecto());
		copia.setTextoPaso1(original.getTextoPaso1());
		copia.setTextoPaso2(original.getTextoPaso2());
		copia.setTextoPaso3(original.getTextoPaso3());
		copia.setTextoSelecRecurso(original.getTextoSelecRecurso());
		copia.setTextoTicketConf(original.getTextoTicketConf());
		copia.setTextoCorreoConf(original.getTextoCorreoConf());
		copia.setTextoCorreoCanc(original.getTextoCorreoCanc());
	}

}
