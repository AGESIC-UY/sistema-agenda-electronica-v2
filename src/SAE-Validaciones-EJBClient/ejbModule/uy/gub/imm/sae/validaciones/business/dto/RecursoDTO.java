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
  private Boolean presencialAdmite;
  private Integer presencialCupos;
  private Boolean presencialLunes;
  private Boolean presencialMartes;
  private Boolean presencialMiercoles;
  private Boolean presencialJueves;
  private Boolean presencialViernes;
  private Boolean presencialSabado;
  private Boolean presencialDomingo;
  private Boolean multipleAdmite;
  private Boolean cambiosAdmite;
  private Integer cambiosTiempo;
  private Integer cambiosUnidad;
  private Integer periodoValidacion;
  private Boolean validarPorIP;
  private Integer cantidadPorIP;
  private Integer periodoPorIP;
  private String ipsSinValidacion;

  private Integer cancelacionTiempo;
  private Integer cancelacionUnidad;
  private String cancelacionTipo;
  
  private Boolean conMiPerfilConfirmacion;
  private Boolean conMiPerfilCancelacion;
  private Boolean conMiPerfilRecordatorio;
  
  private Boolean miPerfilConHab;
  private Boolean miPerfilCanHab;
  private Boolean miPerfilRecHab;
  private String miPerfilConTitulo;
  private String miPerfilConCorto;
  private String miPerfilConLargo;
  private Integer miPerfilConVencim;
  private String miPerfilCanTitulo;
  private String miPerfilCanCorto;
  private String miPerfilCanLargo;
  private Integer miPerfilCanVencim;
  private String miPerfilRecTitulo;
  private String miPerfilRecCorto;
  private String miPerfilRecLargo;
  private Integer miPerfilRecVencim;
  private Integer miPerfilRecHora;
  private Integer miPerfilRecDias;
  
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

  public Boolean getPresencialAdmite() {
    return presencialAdmite;
  }

  public void setPresencialAdmite(Boolean presencialAdmite) {
    this.presencialAdmite = presencialAdmite;
  }

  public Integer getPresencialCupos() {
    return presencialCupos;
  }

  public void setPresencialCupos(Integer presencialCupos) {
    this.presencialCupos = presencialCupos;
  }

  public Boolean getPresencialLunes() {
    return presencialLunes;
  }

  public void setPresencialLunes(Boolean presencialLunes) {
    this.presencialLunes = presencialLunes;
  }

  public Boolean getPresencialMartes() {
    return presencialMartes;
  }

  public void setPresencialMartes(Boolean presencialMartes) {
    this.presencialMartes = presencialMartes;
  }

  public Boolean getPresencialMiercoles() {
    return presencialMiercoles;
  }

  public void setPresencialMiercoles(Boolean presencialMiercoles) {
    this.presencialMiercoles = presencialMiercoles;
  }

  public Boolean getPresencialJueves() {
    return presencialJueves;
  }

  public void setPresencialJueves(Boolean presencialJueves) {
    this.presencialJueves = presencialJueves;
  }

  public Boolean getPresencialViernes() {
    return presencialViernes;
  }

  public void setPresencialViernes(Boolean presencialViernes) {
    this.presencialViernes = presencialViernes;
  }

  public Boolean getPresencialSabado() {
    return presencialSabado;
  }

  public void setPresencialSabado(Boolean presencialSabado) {
    this.presencialSabado = presencialSabado;
  }

  public Boolean getPresencialDomingo() {
    return presencialDomingo;
  }

  public void setPresencialDomingo(Boolean presencialDomingo) {
    this.presencialDomingo = presencialDomingo;
  }

  public Boolean getMultipleAdmite() {
    return multipleAdmite;
  }

  public void setMultipleAdmite(Boolean multipleAdmite) {
    this.multipleAdmite = multipleAdmite;
  }

  public Boolean getCambiosAdmite() {
    return cambiosAdmite;
  }

  public void setCambiosAdmite(Boolean cambiosAdmite) {
    this.cambiosAdmite = cambiosAdmite;
  }

  public Integer getCambiosTiempo() {
    return cambiosTiempo;
  }

  public void setCambiosTiempo(Integer cambiosTiempo) {
    this.cambiosTiempo = cambiosTiempo;
  }

  public Integer getCambiosUnidad() {
    return cambiosUnidad;
  }

  public void setCambiosUnidad(Integer cambiosUnidad) {
    this.cambiosUnidad = cambiosUnidad;
  }

  public Integer getPeriodoValidacion() {
    return periodoValidacion;
  }

  public void setPeriodoValidacion(Integer periodoValidacion) {
    this.periodoValidacion = periodoValidacion;
  }

  public Boolean getValidarPorIP() {
    return validarPorIP;
  }

  public void setValidarPorIP(Boolean validarPorIP) {
    this.validarPorIP = validarPorIP;
  }

  public Integer getCantidadPorIP() {
    return cantidadPorIP;
  }

  public void setCantidadPorIP(Integer cantidadPorIP) {
    this.cantidadPorIP = cantidadPorIP;
  }

  public Integer getPeriodoPorIP() {
    return periodoPorIP;
  }

  public void setPeriodoPorIP(Integer periodoPorIP) {
    this.periodoPorIP = periodoPorIP;
  }

  public String getIpsSinValidacion() {
    return ipsSinValidacion;
  }

  public void setIpsSinValidacion(String ipsSinValidacion) {
    this.ipsSinValidacion = ipsSinValidacion;
  }

  public Integer getCancelacionTiempo() {
    return cancelacionTiempo;
  }

  public void setCancelacionTiempo(Integer cancelacionTiempo) {
    this.cancelacionTiempo = cancelacionTiempo;
  }

  public Integer getCancelacionUnidad() {
    return cancelacionUnidad;
  }

  public void setCancelacionUnidad(Integer cancelacionUnidad) {
    this.cancelacionUnidad = cancelacionUnidad;
  }

  public String getCancelacionTipo() {
    return cancelacionTipo;
  }

  public void setCancelacionTipo(String cancelacionTipo) {
    this.cancelacionTipo = cancelacionTipo;
  }

  public Boolean getConMiPerfilConfirmacion() {
    return conMiPerfilConfirmacion;
  }

  public void setConMiPerfilConfirmacion(Boolean conMiPerfilConfirmacion) {
    this.conMiPerfilConfirmacion = conMiPerfilConfirmacion;
  }

  public Boolean getConMiPerfilCancelacion() {
    return conMiPerfilCancelacion;
  }

  public void setConMiPerfilCancelacion(Boolean conMiPerfilCancelacion) {
    this.conMiPerfilCancelacion = conMiPerfilCancelacion;
  }

  public Boolean getConMiPerfilRecordatorio() {
    return conMiPerfilRecordatorio;
  }

  public void setConMiPerfilRecordatorio(Boolean conMiPerfilRecordatorio) {
    this.conMiPerfilRecordatorio = conMiPerfilRecordatorio;
  }

  public Boolean getMiPerfilConHab() {
    return miPerfilConHab;
  }

  public void setMiPerfilConHab(Boolean miPerfilConHab) {
    this.miPerfilConHab = miPerfilConHab;
  }

  public Boolean getMiPerfilCanHab() {
    return miPerfilCanHab;
  }

  public void setMiPerfilCanHab(Boolean miPerfilCanHab) {
    this.miPerfilCanHab = miPerfilCanHab;
  }

  public Boolean getMiPerfilRecHab() {
    return miPerfilRecHab;
  }

  public void setMiPerfilRecHab(Boolean miPerfilRecHab) {
    this.miPerfilRecHab = miPerfilRecHab;
  }

  public String getMiPerfilConTitulo() {
    return miPerfilConTitulo;
  }

  public void setMiPerfilConTitulo(String miPerfilConTitulo) {
    this.miPerfilConTitulo = miPerfilConTitulo;
  }

  public String getMiPerfilConCorto() {
    return miPerfilConCorto;
  }

  public void setMiPerfilConCorto(String miPerfilConCorto) {
    this.miPerfilConCorto = miPerfilConCorto;
  }

  public String getMiPerfilConLargo() {
    return miPerfilConLargo;
  }

  public void setMiPerfilConLargo(String miPerfilConLargo) {
    this.miPerfilConLargo = miPerfilConLargo;
  }

  public Integer getMiPerfilConVencim() {
    return miPerfilConVencim;
  }

  public void setMiPerfilConVencim(Integer miPerfilConVencim) {
    this.miPerfilConVencim = miPerfilConVencim;
  }

  public String getMiPerfilCanTitulo() {
    return miPerfilCanTitulo;
  }

  public void setMiPerfilCanTitulo(String miPerfilCanTitulo) {
    this.miPerfilCanTitulo = miPerfilCanTitulo;
  }

  public String getMiPerfilCanCorto() {
    return miPerfilCanCorto;
  }

  public void setMiPerfilCanCorto(String miPerfilCanCorto) {
    this.miPerfilCanCorto = miPerfilCanCorto;
  }

  public String getMiPerfilCanLargo() {
    return miPerfilCanLargo;
  }

  public void setMiPerfilCanLargo(String miPerfilCanLargo) {
    this.miPerfilCanLargo = miPerfilCanLargo;
  }

  public Integer getMiPerfilCanVencim() {
    return miPerfilCanVencim;
  }

  public void setMiPerfilCanVencim(Integer miPerfilCanVencim) {
    this.miPerfilCanVencim = miPerfilCanVencim;
  }

  public String getMiPerfilRecTitulo() {
    return miPerfilRecTitulo;
  }

  public void setMiPerfilRecTitulo(String miPerfilRecTitulo) {
    this.miPerfilRecTitulo = miPerfilRecTitulo;
  }

  public String getMiPerfilRecCorto() {
    return miPerfilRecCorto;
  }

  public void setMiPerfilRecCorto(String miPerfilRecCorto) {
    this.miPerfilRecCorto = miPerfilRecCorto;
  }

  public String getMiPerfilRecLargo() {
    return miPerfilRecLargo;
  }

  public void setMiPerfilRecLargo(String miPerfilRecLargo) {
    this.miPerfilRecLargo = miPerfilRecLargo;
  }

  public Integer getMiPerfilRecVencim() {
    return miPerfilRecVencim;
  }

  public void setMiPerfilRecVencim(Integer miPerfilRecVencim) {
    this.miPerfilRecVencim = miPerfilRecVencim;
  }

  public Integer getMiPerfilRecHora() {
    return miPerfilRecHora;
  }

  public void setMiPerfilRecHora(Integer miPerfilRecHora) {
    this.miPerfilRecHora = miPerfilRecHora;
  }

  public Integer getMiPerfilRecDias() {
    return miPerfilRecDias;
  }

  public void setMiPerfilRecDias(Integer miPerfilRecDias) {
    this.miPerfilRecDias = miPerfilRecDias;
  }
	
}
