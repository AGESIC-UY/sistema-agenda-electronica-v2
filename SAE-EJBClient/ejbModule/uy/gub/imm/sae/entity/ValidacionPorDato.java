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
@Table (name = "ae_validaciones_por_dato")
public class ValidacionPorDato implements Serializable{

	private static final long serialVersionUID = -620846748056072002L;
	
	private Integer id;
	private String nombreParametro;
	private Date fechaDesasociacion;
	
	private DatoASolicitar datoASolicitar;
	private ValidacionPorRecurso validacionPorRecurso;
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_valPorDato")
	@SequenceGenerator (name ="seq_valPorDato", initialValue = 1, sequenceName = "s_ae_valdato",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column (name = "fecha_desasociacion")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaDesasociacion() {
		return fechaDesasociacion;
	}
	public void setFechaDesasociacion(Date fechaDesasociacion) {
		this.fechaDesasociacion = fechaDesasociacion;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aeds_id", nullable = false)
	public DatoASolicitar getDatoASolicitar() {
		return datoASolicitar;
	}
	public void setDatoASolicitar(DatoASolicitar datoASolicitar) {
		this.datoASolicitar = datoASolicitar;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aevr_id", nullable = false)
	public ValidacionPorRecurso getValidacionPorRecurso() {
		return validacionPorRecurso;
	}
	public void setValidacionPorRecurso(ValidacionPorRecurso validacionPorRecurso) {
		this.validacionPorRecurso = validacionPorRecurso;
	}
	
	@Column (name = "nombre_parametro", nullable=false, length=50)
	public String getNombreParametro() {
		return nombreParametro;
	}
	public void setNombreParametro(String nombreParametro) {
		this.nombreParametro = nombreParametro;
	}

}
