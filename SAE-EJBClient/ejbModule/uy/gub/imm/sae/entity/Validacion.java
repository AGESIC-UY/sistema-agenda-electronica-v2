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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name = "ae_validaciones")
public class Validacion implements Serializable{

	private static final long serialVersionUID = 7042969790748684636L;
	
	private Integer id;
	private String nombre;
	private String host;
	private String descripcion;
	private String servicio;
	private Date fechaBaja;
	
	private List<ValidacionPorRecurso> validacionesPorRecurso;
	private List<ParametroValidacion> parametrosValidacion;

	
	public Validacion () {
		validacionesPorRecurso = new ArrayList<ValidacionPorRecurso>();
		parametrosValidacion = new ArrayList<ParametroValidacion>();
	}
	
	public Validacion (Validacion v) {
		id = v.getId();
		nombre = v.getNombre();
		descripcion = v.getDescripcion();
		servicio = v.getServicio();
		host = v.getHost();
		fechaBaja = v.getFechaBaja();
		validacionesPorRecurso = new ArrayList<ValidacionPorRecurso>();
		parametrosValidacion = new ArrayList<ParametroValidacion>();
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_validacion")
	@SequenceGenerator (name ="seq_validacion", initialValue = 1, sequenceName = "s_ae_validacion",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column (nullable=false, length=50)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column (length=100)
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	@Column (nullable=false, length=100)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	@Column (nullable=false, length=250)
	public String getServicio() {
		return servicio;
	}
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}
	
	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	
	@OneToMany(mappedBy = "validacion")
	public List<ValidacionPorRecurso> getValidacionesPorRecurso() {
		return validacionesPorRecurso;
	}
	public void setValidacionesPorRecurso(List<ValidacionPorRecurso> validacionesPorRecurso) {
		this.validacionesPorRecurso = validacionesPorRecurso;
	}

	@OneToMany(mappedBy = "validacion")
	public List<ParametroValidacion> getParametrosValidacion() {
		return parametrosValidacion;
	}
	public void setParametrosValidacion(
			List<ParametroValidacion> parametrosValidacion) {
		this.parametrosValidacion = parametrosValidacion;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Validacion) {
			Validacion val = (Validacion) obj;
			if (val.getId().equals(this.getId())) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	
}
