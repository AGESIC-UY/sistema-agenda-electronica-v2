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
import javax.persistence.Version;

import uy.gub.imm.sae.common.enumerados.Estado;

@Entity
@Table (name = "ae_tokens_reservas")
public class TokenReserva implements Serializable {

	private static final long serialVersionUID = 3500715468120358550L;

	private Integer id;
	private String token;
  private Recurso recurso;
  private Date fechaInicio;
  private Date ultimaReserva;
  private Estado estado;
  private String cedula;
  private String nombre;
  private String correoe;
  private String tramite; //Código del trámite (TramiteAgenda.tramiteCodigo)
  private String ipOrigen;
	private String notas;

	private Integer version;

	public TokenReserva () {
		estado = Estado.P;
		fechaInicio = new Date();
		ultimaReserva = null;
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_token_reserva")
	@SequenceGenerator (name ="seq_token_reserva", initialValue = 1, sequenceName = "s_ae_token_reserva",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
  @Column (length = 50)
  public String getToken() {
    return token;
  }
  
  public void setToken(String token) {
    this.token = token;
  }
	
  @ManyToOne (optional = false)
  @JoinColumn (name = "aere_id", nullable = false)
  public Recurso getRecurso() {
    return recurso;
  }

  public void setRecurso(Recurso recurso) {
    this.recurso = recurso;
  }
	
  @Column (name = "fecha_inicio", nullable=false)
  @Temporal(TemporalType.TIMESTAMP)
  public Date getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(Date fechaInicio) {
    this.fechaInicio = fechaInicio;
  }
	
  @Column (name = "ultima_reserva", nullable=false)
  @Temporal(TemporalType.TIMESTAMP)
  public Date getUltimaReserva() {
    return ultimaReserva;
  }

  public void setUltimaReserva(Date ultimaReserva) {
    this.ultimaReserva = ultimaReserva;
  }
  
	@Column (nullable = false, length=1)
	@Enumerated (EnumType.STRING)
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

  @Column(nullable = false, length = 100)
  public String getCedula() {
    return cedula;
  }

  public void setCedula(String cedula) {
    this.cedula = cedula;
  }

  @Column(nullable = false, length = 100)
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  @Column(nullable = false, length = 100)
  public String getCorreoe() {
    return correoe;
  }

  public void setCorreoe(String correoe) {
    this.correoe = correoe;
  }

	@Column (name="notas", length = 4000)
	public String getNotas() {
		return notas;
	}
	
	public void setNotas(String notas) {
		this.notas = notas;
	}
	
	@Column(name="tramite", length = 10)
  public String getTramite() {
    return tramite;
  }
	
  public void setTramite(String tramite) {
    this.tramite = tramite;
  }
  
  @Column (name = "ip_origen")
  public String getIpOrigen() {
    return ipOrigen;
  }

  public void setIpOrigen(String ipOrigen) {
    this.ipOrigen = ipOrigen;
  }

	@Version
	@Column(name="version", nullable=false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "TokenReserva [id="+ id + "," + token + "]";
	}

}
