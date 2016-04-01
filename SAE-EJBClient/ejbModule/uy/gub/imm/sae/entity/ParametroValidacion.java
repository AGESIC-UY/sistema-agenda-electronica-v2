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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uy.gub.imm.sae.common.enumerados.Tipo;


@Entity
@Table (name = "ae_parametros_validacion")
public class ParametroValidacion implements Serializable{

	private static final long serialVersionUID = 7561219589121881567L;
	
	private Integer id;
	private String nombre;
	private Tipo tipo;	
	private Integer largo;
	private Date fechaBaja;
	
	private Validacion validacion;

	public ParametroValidacion () {
		
	}
	
	public ParametroValidacion (ParametroValidacion p) {
		id = p.getId();
		nombre = p.getNombre();
		tipo = p.getTipo();
		largo = p.getLargo();
		fechaBaja = p.getFechaBaja();
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_paramval")
	@SequenceGenerator (name ="seq_paramval", initialValue = 1, sequenceName = "s_ae_paramval",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (nullable = false, length=50)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column (nullable = false, length=30)
	@Enumerated(EnumType.STRING)
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	
	@Column (nullable = false)
	public Integer getLargo() {
		return largo;
	}
	public void setLargo(Integer largo) {
		this.largo = largo;
	}

	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aeva_id", nullable = false)
	public Validacion getValidacion() {
		return validacion;
	}
	public void setValidacion(Validacion validacion) {
		this.validacion = validacion;
	}

}
