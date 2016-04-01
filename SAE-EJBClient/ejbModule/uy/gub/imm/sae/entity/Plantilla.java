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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table (name = "ae_plantillas")
public class Plantilla implements Serializable{

	private static final long serialVersionUID = 3685433874930262820L;

    private Integer id;
	private Date horaInicio;
    private Date horaFin;
    private Integer frecuencia;
    private Integer cupo;
    private Boolean generacionAutomatica;
    private Date fechaBaja;
    
    private Recurso recurso;
    private List<Disponibilidad> disponibilidades;
    private Set<DiasDeLaSemana> diasSemana;
    private Set<DiasDelMes> diasMes;
    private Set<Meses> meses;
    private Set<Anios> anios;

    
    public Plantilla () {
    	disponibilidades = new ArrayList<Disponibilidad>();
    	diasSemana = new HashSet<DiasDeLaSemana>();
    	diasMes = new HashSet<DiasDelMes>();
    	meses = new HashSet<Meses>();
    	anios = new HashSet<Anios>();
    }
    
    
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "seq_plantilla")
	@SequenceGenerator (name ="seq_plantilla", initialValue = 1, sequenceName = "s_ae_plantilla",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column (name = "hora_inicio", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	@Column (name = "hora_fin", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getHoraFin() {
		return horaFin;
	}
	public void setHoraFin(Date horaFin) {
		this.horaFin = horaFin;
	}
	@Column(nullable=false)
	public Integer getFrecuencia() {
		return frecuencia;
	}
	public void setFrecuencia(Integer frecuencia) {
		this.frecuencia = frecuencia;
	}
	@Column(nullable=false)
	public Integer getCupo() {
		return cupo;
	}
	public void setCupo(Integer cupo) {
		this.cupo = cupo;
	}
	
	@Column (name = "generacion_automatica", nullable=false)
	public Boolean getGeneracionAutomatica() {
		return generacionAutomatica;
	}
	public void setGeneracionAutomatica(Boolean generacionAutomatica) {
		this.generacionAutomatica = generacionAutomatica;
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
	@JoinColumn (name = "aere_id", nullable = false)
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}
	
	@OneToMany(mappedBy = "plantilla")
	public List<Disponibilidad> getDisponibilidades() {
		return disponibilidades;
	}
	public void setDisponibilidades(List<Disponibilidad> disponibilidades) {
		this.disponibilidades = disponibilidades;
	}


	@OneToMany(mappedBy = "plantilla", fetch=FetchType.EAGER,cascade={CascadeType.ALL})	
	public Set<DiasDeLaSemana> getDiasSemana() {
		return diasSemana;
	}
	public void setDiasSemana(Set<DiasDeLaSemana> diasSemana) {
		this.diasSemana = diasSemana;
	}
	
	@OneToMany(mappedBy = "plantilla", fetch=FetchType.EAGER,cascade={CascadeType.ALL})	
	public Set<DiasDelMes> getDiasMes() {
		return diasMes;
	}
	public void setDiasMes(Set<DiasDelMes> diasMes) {
		this.diasMes = diasMes;
	}
	
	@OneToMany(mappedBy = "plantilla", fetch=FetchType.EAGER,cascade={CascadeType.ALL})
	public Set<Meses> getMeses() {
		return meses;
	}
	public void setMeses(Set<Meses> meses) {
		this.meses = meses;
	}

	@OneToMany(mappedBy = "plantilla", fetch=FetchType.EAGER,cascade={CascadeType.ALL})
	public Set<Anios> getAnios() {
		return anios;
	}
	public void setAnios(Set<Anios> anios) {
		this.anios = anios;
	}

}
