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

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.RowList;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;


public class RecursoSessionMBean extends SessionCleanerMBean implements RemovableFromSession {
	
	public static final String MSG_ID = "pantalla";
	
	private int pagRecursoDel;
	private int pagRecursoCons;
	private int pagRecursoUpd;
	private int pagDatoRCons;
	private int pagDatoRUpd;
	private int pagDatoRDel;
	private Recurso recursoSeleccionado;
	private DatoDelRecurso datoSolicSeleccionado;
	
	private Integer diasInicioVentanaIntranet;
	private Integer diasVentanaIntranet;
	private Integer diasInicioVentanaInternet;
	private Integer diasVentanaInternet;
	private RowList<Recurso> recursos;
	private Boolean aplicaTodos = Boolean.FALSE;
	 
	
	public DatoDelRecurso getDatoSolicSeleccionado() {
		return datoSolicSeleccionado;
	}
	public void setDatoSolicSeleccionado(DatoDelRecurso datoSolicSeleccionado) {
		this.datoSolicSeleccionado = datoSolicSeleccionado;
	}
	public int getPagDatoRCons() {
		return pagDatoRCons;
	}
	public void setPagDatoRCons(int pagDatoRCons) {
		this.pagDatoRCons = pagDatoRCons;
	}
	public int getPagDatoRUpd() {
		return pagDatoRUpd;
	}
	public void setPagDatoRUpd(int pagDatoRUpd) {
		this.pagDatoRUpd = pagDatoRUpd;
	}
	public int getPagRecursoDel() {
		return pagRecursoDel;
	}
	public void setPagRecursoDel(int pagRecursoDel) {
		this.pagRecursoDel = pagRecursoDel;
	}
	public int getPagRecursoCons() {
		return pagRecursoCons;
	}
	public void setPagRecursoCons(int pagRecursoCons) {
		this.pagRecursoCons = pagRecursoCons;
	}
	public int getPagRecursoUpd() {
		return pagRecursoUpd;
	}
	public void setPagRecursoUpd(int pagRecursoUpd) {
		this.pagRecursoUpd = pagRecursoUpd;
	}
	public int getPagDatoRDel() {
		return pagDatoRDel;
	}
	public void setPagDatoRDel(int pagDatoRDel) {
		this.pagDatoRDel = pagDatoRDel;
	}
	public Recurso getRecursoSeleccionado() {
		return recursoSeleccionado;
	}
	public void setRecursoSeleccionado(Recurso recursoSeleccionado) {
		this.recursoSeleccionado = recursoSeleccionado;
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
	public RowList<Recurso> getRecursos() {
		return recursos;
	}
	public void setRecursos(RowList<Recurso> recursos) {
		this.recursos = recursos;
	}
	public Boolean getAplicaTodos() {
		return aplicaTodos;
	}
	public void setAplicaTodos(Boolean aplicaTodos) {
		this.aplicaTodos = aplicaTodos;
	}
	
	
	
	

}


