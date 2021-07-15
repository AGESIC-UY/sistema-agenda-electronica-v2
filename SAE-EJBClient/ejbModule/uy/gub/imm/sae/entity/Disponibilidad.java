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
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table (name = "ae_disponibilidades")
public class Disponibilidad implements Serializable {
	
	private static final long serialVersionUID = -598747846987644476L;
	
	private Integer id;
	private Date fecha; //Fecha de la ventana, en el timezone de la agenda correspondiente
	private Date horaInicio; //Hora de inicio de la vemtana, en el timezone de la agenda correspondiente
	private Date horaFin; //Hora de fin de la ventana, en el timezone de la agenda correspondiente
	private Integer cupo;
	private Date fechaBaja;
	private Integer numerador;
	private Integer version;
	
	private Recurso recurso;
	private Plantilla plantilla;
	private List<Reserva> reservas;
	
	private Boolean presencial;
	
	public Disponibilidad () {
		numerador = 0;
		presencial = false;
		reservas = new ArrayList<Reserva>();
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "seq_disponibilidad")
	@SequenceGenerator (name ="seq_disponibilidad", initialValue = 1, sequenceName = "s_ae_disponibilidad",allocationSize=1)		
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (nullable = false)
	@Temporal(TemporalType.DATE)	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
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

	@Column (nullable = false)
	public Integer getCupo() {
		return cupo;
	}
	public void setCupo(Integer cupo) {
		this.cupo = cupo;
	}

	@Column (nullable = false)
	public Integer getNumerador() {
		return numerador;
	}
	public void setNumerador(Integer numerador) {
		this.numerador = numerador;
	}

	@Version
	@Column(name="version", nullable=false)
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

  @Column(name="presencial", nullable=false)
	public Boolean getPresencial() {
    return presencial;
  }

  public void setPresencial(Boolean presencial) {
    this.presencial = presencial;
  }

  @Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@XmlTransient
	@ManyToOne (optional = false)
	@JoinColumn (name = "aere_id", nullable = false)
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}
	
	@XmlTransient
	@ManyToMany (mappedBy = "disponibilidades")
	public List<Reserva> getReservas() {
		return reservas;
	}
	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}
	
	@XmlTransient
	@ManyToOne (optional = true)
	@JoinColumn (name = "aepl_id", nullable = true)
	public Plantilla getPlantilla() {
		return plantilla;
	}
	public void setPlantilla(Plantilla plantilla) {
		this.plantilla = plantilla;
	}

	@Override
	public String toString() {
		return "Disponibilidad [id="+id+",fecha=" + fecha + ",horaIni=" + horaInicio +"]";
	}
	
	@Transient
	public Integer getFrecuencia(){
		long diff = this.horaFin.getTime()-this.horaInicio.getTime();
		return new Long(TimeUnit.MILLISECONDS.toMinutes(diff)).intValue();
	}

	
}
