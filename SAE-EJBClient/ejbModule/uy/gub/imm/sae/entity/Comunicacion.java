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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table (name = "ae_comunicaciones")
public class Comunicacion implements Serializable {

	public enum Tipo1 {EMAIL, SMS, TEXTOAVOZ}
	public enum Tipo2 {RESERVA, CANCELA}
	
	private static final long serialVersionUID = 3500715468120358550L;

	private Integer id;
	
	private Tipo1 tipo1; //EMAIL, SMS, TEXTOAVOZ
	private Tipo2 tipo2; //RESERVA, CANCELA
	
	private String destino; //Dirección de email, número de celular, número fijo
	
	private Recurso recurso;
	
	private Reserva reserva;
	
	private Boolean procesado;
	
	public Comunicacion () {
	}

	public Comunicacion(Tipo1 tipo1, Tipo2 tipo2, String destino, Recurso recurso, Reserva reserva) {
		super();
		this.tipo1 = tipo1;
		this.tipo2 = tipo2;
		this.destino = destino;
		this.recurso = recurso;
		this.reserva = reserva;
		this.procesado = false;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_comunicacion")
	@SequenceGenerator (name ="seq_comunicacion", initialValue = 1, sequenceName = "s_ae_comunicaciones", allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="tipo_1")
	@Enumerated(EnumType.STRING)
	public Tipo1 getTipo1() {
		return tipo1;
	}

	public void setTipo1(Tipo1 tipo1) {
		this.tipo1 = tipo1;
	}

	@Column(name="tipo_2")
	@Enumerated(EnumType.STRING)
	public Tipo2 getTipo2() {
		return tipo2;
	}

	public void setTipo2(Tipo2 tipo2) {
		this.tipo2 = tipo2;
	}

	@Column(name="destino")
	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "recurso_id", nullable = false)
	public Recurso getRecurso() {
		return recurso;
	}

	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "reserva_id", nullable = false)
	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	@Column(name="procesado")
	public Boolean getProcesado() {
		return procesado;
	}

	public void setProcesado(Boolean procesado) {
		this.procesado = procesado;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
