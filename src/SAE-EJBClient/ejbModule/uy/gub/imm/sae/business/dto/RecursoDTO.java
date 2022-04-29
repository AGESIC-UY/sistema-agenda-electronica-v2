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
import java.math.BigDecimal;
import java.util.Date;

public class RecursoDTO implements Serializable {

	private static final long serialVersionUID = -5249355111057790773L;
	private Integer id;
	private String nombre;
	private String descripcion;
	private String direccion;
	private String localidad;
	private String departamento;
	private BigDecimal latitud;
	private BigDecimal longitud;
	private String telefonos;
	private String horarios;
	
	//Las fechas deben ser en formato "yyyyMMdd"
	private String fechaInicio;
	private String fechaFin;
	private String fechaInicioDisp;
	private String fechaFinDisp;
	private String visibleInternet; //(posibles valores S, N);
	private String sabadoEsHabil; //(posibles valores S, N);
	private String domingoEsHabil; //(posibles valores S, N);

	private Integer cantDiasAGenerar;
	private Integer periodoValidacion; //(cantidad de días);

	public RecursoDTO() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public BigDecimal getLatitud() {
		return latitud;
	}

	public void setLatitud(BigDecimal latitud) {
		this.latitud = latitud;
	}

	public BigDecimal getLongitud() {
		return longitud;
	}

	public void setLongitud(BigDecimal longitud) {
		this.longitud = longitud;
	}

	public String getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}

	public String getHorarios() {
		return horarios;
	}

	public void setHorarios(String horarios) {
		this.horarios = horarios;
	}

	public String getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getFechaInicioDisp() {
		return fechaInicioDisp;
	}

	public void setFechaInicioDisp(String fechaInicioDisp) {
		this.fechaInicioDisp = fechaInicioDisp;
	}

	public String getFechaFinDisp() {
		return fechaFinDisp;
	}

	public void setFechaFinDisp(String fechaFinDisp) {
		this.fechaFinDisp = fechaFinDisp;
	}

	public String getVisibleInternet() {
		return visibleInternet;
	}

	public void setVisibleInternet(String visibleInternet) {
		this.visibleInternet = visibleInternet;
	}

	public String getSabadoEsHabil() {
		return sabadoEsHabil;
	}

	public void setSabadoEsHabil(String sabadoEsHabil) {
		this.sabadoEsHabil = sabadoEsHabil;
	}

	public String getDomingoEsHabil() {
		return domingoEsHabil;
	}

	public void setDomingoEsHabil(String domingoEsHabil) {
		this.domingoEsHabil = domingoEsHabil;
	}

	public Integer getCantDiasAGenerar() {
		return cantDiasAGenerar;
	}

	public void setCantDiasAGenerar(Integer cantDiasAGenerar) {
		this.cantDiasAGenerar = cantDiasAGenerar;
	}

	public Integer getPeriodoValidacion() {
		return periodoValidacion;
	}

	public void setPeriodoValidacion(Integer periodoValidacion) {
		this.periodoValidacion = periodoValidacion;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
  
  
}