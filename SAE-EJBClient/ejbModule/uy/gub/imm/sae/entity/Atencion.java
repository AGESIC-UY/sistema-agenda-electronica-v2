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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name = "ae_atencion")
public class Atencion implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date fechaCreacion;
	private Date fechaActualizacion;

	private Boolean asistio;
	private String funcionario;
	private Integer duracion;
	
	private Reserva reserva;

	
	public Atencion () {
		fechaCreacion = new Date();
		fechaActualizacion = fechaCreacion;
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_atencion")
	@SequenceGenerator (name ="seq_atencion", initialValue = 1, sequenceName = "S_AE_ATENCION",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	

	@Column (name = "fcrea", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Column (name = "fact", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}


	@Column (nullable=false)
	public Boolean getAsistio() {
		return asistio;
	}

	public void setAsistio(Boolean asistio) {
		this.asistio = asistio;
	}

	@Column (name = "funcionario", nullable=false)
	public String getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(String funcionario) {
		this.funcionario = funcionario;
	}

	@Column (nullable=false)
	public Integer getDuracion() {
		return duracion;
	}

	public void setDuracion(Integer duracion) {
		this.duracion = duracion;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aers_id", nullable = false)
	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}
	

	
}
