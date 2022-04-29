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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name = "ae_llamadas")
public class Llamada implements Serializable {

	private static final long serialVersionUID = 7892423968988286911L;
	
	private Integer id;
	private Date    fecha;
	private Date    hora;
	private Integer numero;
	private String  etiqueta;
	private Integer puesto;
	
	private Reserva reserva;
	private Recurso recurso;
	
	public Llamada() {
		hora = null;
		numero = null;
		etiqueta = null;
		puesto = null;
	}

	Llamada(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_llamada")
	@SequenceGenerator (name ="seq_llamada", initialValue = 1, sequenceName = "s_ae_llamada",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column (name = "fecha", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Column (name = "hora", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getHora() {
		return hora;
	}
	public void setHora(Date hora) {
		this.hora = hora;
	}
	
	@Column (nullable = false)	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	
	@Column (length = 100, nullable = false)	
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	@Column (nullable = false)
	public Integer getPuesto() {
		return puesto;
	}
	public void setPuesto(Integer puesto) {
		this.puesto = puesto;
	}
	
	@OneToOne (optional = false)
	@JoinColumn (name = "aers_id", nullable = false)	
	public Reserva getReserva() {
		return reserva;
	}
	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aere_id", nullable = false)	
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Llamada llamada = (Llamada) o;
		return Objects.equals(id, llamada.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
