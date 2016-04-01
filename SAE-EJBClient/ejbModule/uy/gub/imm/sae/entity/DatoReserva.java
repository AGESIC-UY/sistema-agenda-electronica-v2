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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table (name = "ae_datos_reserva")
public class DatoReserva implements Serializable{

	private static final long serialVersionUID = -150709155835844199L;
	
	private Integer id;
	private String valor;
	
	private DatoASolicitar datoASolicitar;
	private Reserva reserva;
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_datoReserva")
	@SequenceGenerator (name ="seq_datoReserva", initialValue = 1, sequenceName = "s_ae_datoreserva",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (nullable=false, length=100)
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}

	@ManyToOne (fetch = FetchType.EAGER, optional = false)
	@JoinColumn (name = "aeds_id", nullable = false)
	public DatoASolicitar getDatoASolicitar() {
		return datoASolicitar;
	}
	public void setDatoASolicitar(DatoASolicitar datoASolicitar) {
		this.datoASolicitar = datoASolicitar;
	}

	@XmlTransient
	@ManyToOne (optional = false)
	@JoinColumn (name = "aers_id", nullable = false)
	public Reserva getReserva() {
		return reserva;
	}
	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	@Override
	public String toString() {
		return "DatoReserva [datoASolic="+ (datoASolicitar!=null?datoASolicitar.getNombre():"")+ ",valor=" + valor +"]";
	}
	
}
