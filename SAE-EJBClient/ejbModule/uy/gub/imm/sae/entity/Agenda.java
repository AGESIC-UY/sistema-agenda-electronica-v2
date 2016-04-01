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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table( name = "ae_agendas" )
public class Agenda implements Serializable {

	private static final long serialVersionUID = -8141535473333482435L;
	
	private Integer id;
	private String nombre;
	private String descripcion;
	private Boolean conCda;
	private Date   fechaBaja;
	
	private List<Recurso> recursos;
	
	private String tramiteId;
	private String tramiteCodigo;
	
	private String timezone;
	private String idiomas;
	
  private Map<String, TextoAgenda> textosAgenda;
	
	public Agenda () {
		recursos = new ArrayList<Recurso>();
	}
	
	/**
	 * Constructor por copia no en profundidad.
	 */
	public Agenda (Agenda a) {
		
		id = a.getId();
		nombre = a.getNombre();
		descripcion = a.getDescripcion();
		fechaBaja = a.getFechaBaja();
		
		textosAgenda = new HashMap<String, TextoAgenda>();
		recursos = new ArrayList<Recurso>();
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_agenda")
	@SequenceGenerator (name ="seq_agenda", initialValue = 1, sequenceName = "s_ae_agenda", allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column (nullable = false, length=100)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fin) {
		fechaBaja = fin;
	}
	
	@XmlTransient
	@OneToMany (mappedBy = "agenda")
	public List<Recurso> getRecursos() {
		return recursos;
	}
	public void setRecursos(List<Recurso> recursos) {
		this.recursos = recursos;
	}
	
	@Column (nullable = false, length=1000)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name="tramite_id")
	public String getTramiteId() {
		return tramiteId;
	}

	public void setTramiteId(String tramiteId) {
		this.tramiteId = tramiteId;
	}

	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getIdiomas() {
		return idiomas;
	}

	public void setIdiomas(String idiomas) {
		this.idiomas = idiomas;
	}

	@OneToMany(mappedBy="agenda", fetch=FetchType.EAGER)
  @MapKey(name="idioma")
	public Map<String, TextoAgenda> getTextosAgenda() {
		return textosAgenda;
	}

	public void setTextosAgenda(Map<String, TextoAgenda> textosAgenda) {
		this.textosAgenda = textosAgenda;
	}

	@Column(name="con_cda")
	public Boolean getConCda() {
		return conCda;
	}

	public void setConCda(Boolean conCda) {
		this.conCda = conCda;
	}

	@Column(name="tramite_codigo")
	public String getTramiteCodigo() {
		return tramiteCodigo;
	}

	public void setTramiteCodigo(String tramiteCodigo) {
		this.tramiteCodigo = tramiteCodigo;
	}

	
	
}
