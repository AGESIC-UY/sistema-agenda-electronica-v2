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

package uy.gub.imm.sae.web.mbean.administracion;

import java.util.Date;

import javax.ejb.EJB;

import uy.gub.imm.sae.business.ejb.facade.AgendaGeneral;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.DisponibilidadReserva;
import uy.gub.imm.sae.web.common.CupoPorDia;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.RowList;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;


public class GenDispSessionMBean extends SessionCleanerMBean implements RemovableFromSession {
	
	public static final String MSG_ID = "pantalla";
		
//	@EJB( name="ejb/AgendaGeneralBean")
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendaGeneralBean!uy.gub.imm.sae.business.ejb.facade.AgendaGeneralRemote")
	private AgendaGeneral generalEJB;
//	@EJB( name="ejb/RecursosBean")
	@EJB(mappedName="java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosRemote")
	private Recursos recursosEJB;

	private CupoPorDia cupoPorDiaSeleccionado;
	private RowList<CupoPorDia> cuposPorDia;
	
	private DisponibilidadReserva dispSeleccionado;
	private Boolean mostrarDisponibilidad = true;
	
	//private List<Disponibilidad> disponibilidades;
	private RowList<DisponibilidadReserva> disponibilidadesDelDiaMatutina;
	private RowList<DisponibilidadReserva> disponibilidadesDelDiaVespertina;
	private RowList<DisponibilidadReserva> disponibilidadesDelDiaMatutinaModif;
	private RowList<DisponibilidadReserva> disponibilidadesDelDiaVespertinaModif;

	
	private Date fechaDesde;
	private Date fechaHasta;
	private Date fechaActual;
	//private Date fechaModifCupo;
	
	//Fecha para tomar como modelo para generar disponibilidades.
	private Date fechaModelo;
	private Date fechaInicial;
	private Date fechaFinal;

	
	
	private int pagCupo;
	//private int pagDisp;


	public Date getFechaModelo() {
		return fechaModelo;
	}
	public void setFechaModelo(Date fechaModelo) {
		this.fechaModelo = fechaModelo;
	}

	
	public int getPagCupo() {
		return pagCupo;
	}
	public void setPagCupo(int pagCupo) {
		this.pagCupo = pagCupo;
	}
	public Date getFechaActual() {
		return fechaActual;
	}
	public void setFechaActual(Date fechaActual) {
		this.fechaActual = fechaActual;
	}


	
	public CupoPorDia getCupoPorDiaSeleccionado() {
		return cupoPorDiaSeleccionado;
	}
	public void setCupoPorDiaSeleccionado(CupoPorDia cupoPorDiaSeleccionado) {
		this.cupoPorDiaSeleccionado = cupoPorDiaSeleccionado;
	}
	public RowList<CupoPorDia> getCuposPorDia() {
		return cuposPorDia;
	}
	public void setCuposPorDia(RowList<CupoPorDia> cuposPorDia) {
		this.cuposPorDia = cuposPorDia;
	}
	
	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	/*
	public int getPagDisp() {
		return pagDisp;
	}
	public void setPagDisp(int pagDisp) {
		this.pagDisp = pagDisp;
	}

	public List<Disponibilidad> getDisponibilidades() {
		return disponibilidades;
	}

	public void setDisponibilidades(List<Disponibilidad> disponibilidades) {
		this.disponibilidades = disponibilidades;
	}
	*/
	
	public RowList<DisponibilidadReserva> getDisponibilidadesDelDiaMatutina() {
		return disponibilidadesDelDiaMatutina;
	}
	public void setDisponibilidadesDelDiaMatutina(
			RowList<DisponibilidadReserva> disponibilidadesDelDiaMatutina) {
		this.disponibilidadesDelDiaMatutina = disponibilidadesDelDiaMatutina;
	}
	public RowList<DisponibilidadReserva> getDisponibilidadesDelDiaVespertina() {
		return disponibilidadesDelDiaVespertina;
	}
	public void setDisponibilidadesDelDiaVespertina(
			RowList<DisponibilidadReserva> disponibilidadesDelDiaVespertina) {
		this.disponibilidadesDelDiaVespertina = disponibilidadesDelDiaVespertina;
	}
	
	
	
	public DisponibilidadReserva getDispSeleccionado() {
		return dispSeleccionado;
	}
	public void setDispSeleccionado(DisponibilidadReserva dispSeleccionado) {
		this.dispSeleccionado = dispSeleccionado;
	}

	public Boolean getMostrarDisponibilidad() {

		if (this.getDispSeleccionado() != null ) {
			this.mostrarDisponibilidad = true;
		}
		else {
			this.mostrarDisponibilidad = false;
		}
		return this.mostrarDisponibilidad;
	}


	public void setMostrarDisponibilidad(Boolean mostrarDisponibilidad) {
		this.mostrarDisponibilidad = mostrarDisponibilidad;
	}
	
	public RowList<DisponibilidadReserva> getDisponibilidadesDelDiaMatutinaModif() {
		return disponibilidadesDelDiaMatutinaModif;
	}
	public void setDisponibilidadesDelDiaMatutinaModif(
			RowList<DisponibilidadReserva> disponibilidadesDelDiaMatutinaModif) {
		this.disponibilidadesDelDiaMatutinaModif = disponibilidadesDelDiaMatutinaModif;
	}
	public RowList<DisponibilidadReserva> getDisponibilidadesDelDiaVespertinaModif() {
		return disponibilidadesDelDiaVespertinaModif;
	}
	public void setDisponibilidadesDelDiaVespertinaModif(
			RowList<DisponibilidadReserva> disponibilidadesDelDiaVespertinaModif) {
		this.disponibilidadesDelDiaVespertinaModif = disponibilidadesDelDiaVespertinaModif;
	}
	public Date getFechaInicial() {
		return fechaInicial;
	}
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	public Date getFechaFinal() {
		return fechaFinal;
	}
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	
	/*
	public Date getFechaModifCupo() {
		return fechaModifCupo;
	}
	public void setFechaModifCupo(Date fechaModifCupo) {
		this.fechaModifCupo = fechaModifCupo;
	}
	*/


}


