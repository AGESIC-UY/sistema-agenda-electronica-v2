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

package uy.gub.imm.sae.validaciones.business.dto;

import java.io.Serializable;
import java.util.Date;

public class RecursoDTO implements Serializable {

	private static final long serialVersionUID = 4779695576174171858L;

	private Integer id;
	private String nombre;
	private String descripcion;
	private Date fechaInicio;
	private Date fechaFin;
	private Date fechaInicioDisp;
	private Date fechaFinDisp;
	private Integer diasInicioVentanaIntranet;
	private Integer diasVentanaIntranet;
	private Integer diasInicioVentanaInternet;
	private Integer diasVentanaInternet;
	private Integer ventanaCuposMinimos;
	private Integer cantDiasAGenerar;
	private Boolean reservaMultiple;
	private Date fechaBaja;
	private Boolean mostrarNumeroEnLlamador;

	public RecursoDTO () {
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

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Date getFechaInicioDisp() {
		return fechaInicioDisp;
	}

	public void setFechaInicioDisp(Date fechaInicioDisp) {
		this.fechaInicioDisp = fechaInicioDisp;
	}

	public Date getFechaFinDisp() {
		return fechaFinDisp;
	}

	public void setFechaFinDisp(Date fechaFinDisp) {
		this.fechaFinDisp = fechaFinDisp;
	}

	public Integer getDiasInicioVentanaIntranet() {
		return diasInicioVentanaIntranet;
	}

	public void setDiasInicioVentanaIntranet(Integer diasInicioVentanaIntranet) {
		this.diasInicioVentanaIntranet = diasInicioVentanaIntranet;
	}

	public Integer getDiasVentanaIntranet() {
		return diasVentanaIntranet;
	}

	public void setDiasVentanaIntranet(Integer diasVentanaIntranet) {
		this.diasVentanaIntranet = diasVentanaIntranet;
	}

	public Integer getDiasInicioVentanaInternet() {
		return diasInicioVentanaInternet;
	}

	public void setDiasInicioVentanaInternet(Integer diasInicioVentanaInternet) {
		this.diasInicioVentanaInternet = diasInicioVentanaInternet;
	}

	public Integer getDiasVentanaInternet() {
		return diasVentanaInternet;
	}

	public void setDiasVentanaInternet(Integer diasVentanaInternet) {
		this.diasVentanaInternet = diasVentanaInternet;
	}

	public Integer getVentanaCuposMinimos() {
		return ventanaCuposMinimos;
	}

	public void setVentanaCuposMinimos(Integer ventanaCuposMinimos) {
		this.ventanaCuposMinimos = ventanaCuposMinimos;
	}

	public Integer getCantDiasAGenerar() {
		return cantDiasAGenerar;
	}

	public void setCantDiasAGenerar(Integer cantDiasAGenerar) {
		this.cantDiasAGenerar = cantDiasAGenerar;
	}

	public Boolean getReservaMultiple() {
		return reservaMultiple;
	}

	public void setReservaMultiple(Boolean reservaMultiple) {
		this.reservaMultiple = reservaMultiple;
	}

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	public Boolean getMostrarNumeroEnLlamador() {
		return mostrarNumeroEnLlamador;
	}

	public void setMostrarNumeroEnLlamador(Boolean mostrarNumeroEnLlamador) {
		this.mostrarNumeroEnLlamador = mostrarNumeroEnLlamador;
	}
}
