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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReservaYDato {

		private String nombreAgenda;
		private String nombreRecurso;
		private BigDecimal idReserva;
		private String estadoReserva;
		private String obsReserva;
		private Date fechaDisp;
		private Date horaInicioDisp;
		private BigDecimal idAgrupDatos;
		private String nombreAgrupDatos;
		private BigDecimal orden;
		private BigDecimal fila;
		private BigDecimal columna;
		private String nombre;
		private String etiqueta;
		private String valor;
		private String valorEtiqueta;
		private String datosComoHtml;
		
		private Map<String, Object> datos;
		
		public ReservaYDato() {
			datos = new HashMap<String, Object>();
		}
		public Map<String, Object> getDatos() {
			return datos;
		}
		public void setDatos(Map<String, Object> datos) {
			this.datos = datos;
		}
		public String getNombreAgenda() {
			return nombreAgenda;
		}
		public void setNombreAgenda(String nombreAgenda) {
			this.nombreAgenda = nombreAgenda;
		}
		public String getNombreRecurso() {
			return nombreRecurso;
		}
		public void setNombreRecurso(String nombreRecurso) {
			this.nombreRecurso = nombreRecurso;
		}
		public BigDecimal getIdReserva() {
			return idReserva;
		}
		public void setIdReserva(BigDecimal idReserva) {
			this.idReserva = idReserva;
		}
		public String getEstadoReserva() {
			return estadoReserva;
		}
		public void setEstadoReserva(String estadoReserva) {
			this.estadoReserva = estadoReserva;
		}
		public String getObsReserva() {
			return obsReserva;
		}
		public void setObsReserva(String obsReserva) {
			this.obsReserva = obsReserva;
		}
		public Date getFechaDisp() {
			return fechaDisp;
		}
		public void setFechaDisp(Date fechaDisp) {
			this.fechaDisp = fechaDisp;
		}
		public Date getHoraInicioDisp() {
			return horaInicioDisp;
		}
		public void setHoraInicioDisp(Date horaInicioDisp) {
			this.horaInicioDisp = horaInicioDisp;
		}
		public BigDecimal getIdAgrupDatos() {
			return idAgrupDatos;
		}
		public void setIdAgrupDatos(BigDecimal idAgrupDatos) {
			this.idAgrupDatos = idAgrupDatos;
		}
		public String getNombreAgrupDatos() {
			return nombreAgrupDatos;
		}
		public void setNombreAgrupDatos(String nombreAgrupDatos) {
			this.nombreAgrupDatos = nombreAgrupDatos;
		}
		public BigDecimal getOrden() {
			return orden;
		}
		public void setOrden(BigDecimal orden) {
			this.orden = orden;
		}
		public BigDecimal getFila() {
			return fila;
		}
		public void setFila(BigDecimal fila) {
			this.fila = fila;
		}
		public BigDecimal getColumna() {
			return columna;
		}
		public void setColumna(BigDecimal columna) {
			this.columna = columna;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getEtiqueta() {
			return etiqueta;
		}
		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		public String getValorEtiqueta() {
			return valorEtiqueta;
		}
		public void setValorEtiqueta(String valorEtiqueta) {
			this.valorEtiqueta = valorEtiqueta;
		}
		public String getDatosComoHtml() {
			return datosComoHtml;
		}
		public void setDatosComoHtml(String datosComoHtml) {
			this.datosComoHtml = datosComoHtml;
		}
		
		
}
