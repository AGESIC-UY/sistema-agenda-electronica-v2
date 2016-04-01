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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name = "ae_validaciones_por_recurso")
public class ValidacionPorRecurso implements Serializable{
	private Integer id;
	private Integer ordenEjecucion;
	private Date fechaBaja;

	private Validacion validacion;
	private Recurso recurso;
	private List<ValidacionPorDato> validacionesPorDato;
	private List<ValorConstanteValidacionRecurso> constantesValidacion;

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_valporrec")
	@SequenceGenerator (name ="seq_valporrec", initialValue = 1, sequenceName = "s_ae_valrecurso",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (name = "orden_ejecucion")	
	public Integer getOrdenEjecucion() {
		return ordenEjecucion;
	}
	public void setOrdenEjecucion(Integer ordenEjecucion) {
		this.ordenEjecucion = ordenEjecucion;
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

	@ManyToOne (optional = false)
	@JoinColumn (name = "aere_id", nullable = false)
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}
	
	@OneToMany(mappedBy = "validacionPorRecurso")
	public List<ValidacionPorDato> getValidacionesPorDato() {
		return validacionesPorDato;
	}
	public void setValidacionesPorDato(List<ValidacionPorDato> validacionesPorDato) {
		this.validacionesPorDato = validacionesPorDato;
	}

	@OneToMany(mappedBy = "validacionPorRecurso")
	public List<ValorConstanteValidacionRecurso> getConstantesValidacion() {
		return constantesValidacion;
	}
	
	public void setConstantesValidacion(List<ValorConstanteValidacionRecurso> constantes){
		this.constantesValidacion = constantes;
	}

}
