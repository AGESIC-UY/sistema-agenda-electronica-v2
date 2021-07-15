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

package uy.gub.imm.sae.business.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReservaDTO implements Serializable {

  private static final long serialVersionUID = -5249355111057790773L;
  private Integer id;
  private Integer numero;
  private String serie;
  private String estado;
  private Date fecha;
  private Date horaInicio;
  private Map<String, Object> datos;
  private Integer puestoLlamada;
  private String origen;
  private Date fcrea;
  private String ucrea;
  private Boolean asistio;
  private String numeroDocumento;
  private String tramiteCodigo;
  private String tramiteNombre;
  private String codigoSeguridad;
  private Boolean presencial;
  private String ucancela;
  private String tcancela;
  private Date fcancela;
  private String ipOrigen;
  private Date flibera;

  public ReservaDTO() {
    datos = new HashMap<String, Object>();
  }

  public Map<String, Object> getDatos() {
    return datos;
  }

  public void setDatos(Map<String, Object> datos) {
    this.datos = datos;
  }

  public Integer getNumero() {
    return numero;
  }

  public void setNumero(Integer numero) {
    this.numero = numero;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public Date getFecha() {
    return fecha;
  }

  public void setFecha(Date fecha) {
    this.fecha = fecha;
  }

  public Date getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(Date horaInicio) {
    this.horaInicio = horaInicio;
  }

  public Integer getPuestoLlamada() {
    return puestoLlamada;
  }

  public void setPuestoLlamada(Integer puestoLlamada) {
    this.puestoLlamada = puestoLlamada;
  }

  public String getOrigen() {
    return origen;
  }

  public void setOrigen(String origen) {
    this.origen = origen;
  }

  public String getUcrea() {
    return ucrea;
  }

  public void setUcrea(String ucrea) {
    this.ucrea = ucrea;
  }

  public String getUcancela() {
    return ucancela;
  }

  public void setUcancela(String ucancela) {
    this.ucancela = ucancela;
  }

  public void setAsistio(Boolean asistio) {
    this.asistio = asistio;
  }

  public Boolean getAsistio() {
    return asistio;
  }

  public String getNumeroDocumento() {
    return numeroDocumento;
  }

  public void setNumeroDocumento(String numeroDocumento) {
    this.numeroDocumento = numeroDocumento;
  }

  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  public String getTramiteNombre() {
    return tramiteNombre;
  }

  public void setTramiteNombre(String tramiteNombre) {
    this.tramiteNombre = tramiteNombre;
  }

  public String getCodigoSeguridad() {
    return codigoSeguridad;
  }

  public void setCodigoSeguridad(String codigoSeguridad) {
    this.codigoSeguridad = codigoSeguridad;
  }

  public Boolean getPresencial() {
    return presencial;
  }

  public void setPresencial(Boolean presencial) {
    this.presencial = presencial;
  }

  public String getTcancela() {
    return tcancela;
  }

  public void setTcancela(String tcancela) {
    this.tcancela = tcancela;
  }

  public Date getFcancela() {
    return fcancela;
  }

  public void setFcancela(Date fcancela) {
    this.fcancela = fcancela;
  }

  public String getSerie() {
    return serie;
  }

  public void setSerie(String serie) {
    this.serie = serie;
  }

  public String getIpOrigen() {
    return ipOrigen;
  }

  public void setIpOrigen(String ipOrigen) {
    this.ipOrigen = ipOrigen;
  }

  public Date getFcrea() {
    return fcrea;
  }

  public void setFcrea(Date fcrea) {
    this.fcrea = fcrea;
  }

  public Date getFlibera() {
    return flibera;
  }

  public void setFlibera(Date flibera) {
    this.flibera = flibera;
  }

@Override
public String toString() {
	return "ReservaDTO [id=" + id + ", numero=" + numero + ", serie=" + serie
			+ ", estado=" + estado + ", fecha=" + fecha + ", horaInicio="
			+ horaInicio + ", datos=" + datos + ", puestoLlamada="
			+ puestoLlamada + ", origen=" + origen + ", fcrea=" + fcrea
			+ ", ucrea=" + ucrea + ", asistio=" + asistio
			+ ", numeroDocumento=" + numeroDocumento + ", tramiteCodigo="
			+ tramiteCodigo + ", tramiteNombre=" + tramiteNombre
			+ ", codigoSeguridad=" + codigoSeguridad + ", presencial="
			+ presencial + ", ucancela=" + ucancela + ", tcancela=" + tcancela
			+ ", fcancela=" + fcancela + ", ipOrigen=" + ipOrigen
			+ ", flibera=" + flibera + "]";
}
  
  

}
