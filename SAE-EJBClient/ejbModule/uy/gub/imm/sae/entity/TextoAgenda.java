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

package uy.gub.imm.sae.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table( name = "ae_textos_agenda" )
public class TextoAgenda implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String textoPaso1;
	private String textoPaso2;
	private String textoPaso3;
	private String textoSelecRecurso;
	private String textoTicketConf;
	private String textoCorreoConf;
	private String textoCorreoCanc;
	private String textoCorreoTras;
	private Agenda agenda;
	
	//No puede haber ningun registro en null porque da "HibernateException: null index column for collection"
	private String idioma;
	//Indica si este es el idioma por defecto
	private boolean porDefecto;
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_text_agenda")
	@SequenceGenerator (name ="seq_text_agenda", initialValue = 1, sequenceName = "s_ae_texto_agenda", allocationSize=1)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (nullable = true, length=1000, name="texto_paso1")
	public String getTextoPaso1() {
		return textoPaso1;
	}
	
	public void setTextoPaso1(String textoPaso1) {
		this.textoPaso1 = textoPaso1;
	}
	
	@Column (nullable = true, length=1000, name="texto_paso2")
	public String getTextoPaso2() {
		return textoPaso2;
	}
	
	public void setTextoPaso2(String textoPaso2) {
		this.textoPaso2 = textoPaso2;
	}
	
	@Column (nullable = true, length=1000, name="texto_paso3")
	public String getTextoPaso3() {
		return textoPaso3;
	}
	
	public void setTextoPaso3(String textoPaso3) {
		this.textoPaso3 = textoPaso3;
	}
	
	@Column (nullable = true, length=50, name="texto_sel_recurso")
	public String getTextoSelecRecurso() {
		if(textoSelecRecurso == null) {
			return "Recurso";
		}
		return textoSelecRecurso;
	}
	
	public void setTextoSelecRecurso(String textoSelecRecurso) {
		this.textoSelecRecurso = textoSelecRecurso;
	}

	@Column (nullable = true, length=1000, name="texto_ticket")
	public String getTextoTicketConf() {
		return textoTicketConf;
	}
	
	public void setTextoTicketConf(String textoTicketConf) {
		this.textoTicketConf = textoTicketConf;
	}

	@Column (nullable = true, length=50, name="texto_correo_conf")
	public String getTextoCorreoConf() {
		return textoCorreoConf;
	}
	
	public void setTextoCorreoConf(String textoCorreoConf) {
		this.textoCorreoConf = textoCorreoConf;
	}
	
	@Column (nullable = true, length=50, name="texto_correo_canc")
	public String getTextoCorreoCanc() {
		return textoCorreoCanc;
	}
	
	public void setTextoCorreoCanc(String textoCorreoCanc) {
		this.textoCorreoCanc = textoCorreoCanc;
	}

	@Column (nullable = true, length=50, name="texto_correo_tras")
	public String getTextoCorreoTras() {
		return textoCorreoTras;
	}
	
	public void setTextoCorreoTras(String textoCorreoTras) {
		this.textoCorreoTras = textoCorreoTras;
	}
	
	@XmlTransient
	@ManyToOne(optional=false)
	@JoinColumn (name = "aeag_id", nullable = false)
	public Agenda getAgenda() {
		return agenda;
	}
	
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
	
	@Column (name = "idioma", nullable = false)
	public String getIdioma() {
		return idioma;
	}
	
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	
	@Column (name = "por_defecto", nullable = false)
	public boolean isPorDefecto() {
		return porDefecto;
	}
	
	public void setPorDefecto(boolean porDefecto) {
		this.porDefecto = porDefecto;
	}

}
