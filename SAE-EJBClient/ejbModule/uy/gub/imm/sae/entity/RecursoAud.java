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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import uy.gub.imm.sae.common.enumerados.FormaCancelacion;

@Entity
@Table(name = "ae_recursos_aud")
public class RecursoAud implements Serializable {
	private static final long serialVersionUID = -5197426783029830293L;

	private Integer id;
	private Integer idRecurso;
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
	private Integer largoListaEspera;
	private Date fechaBaja;
	private Boolean mostrarNumeroEnLlamador = false;
	private Boolean visibleInternet;
	private Boolean usarLlamador;
	private String serie;
	private Boolean sabadoEsHabil;
	private Boolean domingoEsHabil;

	private Boolean mostrarNumeroEnTicket;
	private Boolean mostrarIdEnTicket;
	private String fuenteTicket;
	private Integer tamanioFuenteGrande;
	private Integer tamanioFuenteNormal;
	private Integer tamanioFuenteChica;

	// Datos obtenidos a partir de TramitesUy
	private String oficinaId; // Esto es inventado, las oficinas no tienen id en
								// TramitesUy
	private String direccion;
	private String localidad;
	private String departamento;
	private String telefonos;
	private String horarios;

	@Column(name = "latitud")
	private BigDecimal latitud;
	@Column(name = "longitud")
	private BigDecimal longitud;

	private Integer agenda;

	// Datos de atención presencial
	private Boolean presencialAdmite;
	private Integer presencialCupos;
	private Boolean presencialLunes;
	private Boolean presencialMartes;
	private Boolean presencialMiercoles;
	private Boolean presencialJueves;
	private Boolean presencialViernes;
	private Boolean presencialSabado;
	private Boolean presencialDomingo;

	// Datos de reserva múltiple
	private Boolean multipleAdmite;
	// Datos de cambios de reserva
	private Boolean cambiosAdmite;
	private Integer cambiosTiempo;
	private Integer cambiosUnidad; // Calendar.DATE, Calendar.HOUR,
									// Calendar.MINUTE

	private Integer periodoValidacion;

	// Validación por IP
	private Boolean validarPorIP;
	private Integer cantidadPorIP;
	private Integer periodoPorIP;
	private String ipsSinValidacion;

	// Datos de cancelaciones de reserva
	private Integer cancelacionTiempo;
	private Integer cancelacionUnidad; // Calendar.DATE, Calendar.HOUR,
										// Calendar.MINUTE
	private FormaCancelacion cancelacionTipo; // I=Inmediata, D=Diferida

	// Datos de integración con MiPerfil
	private Boolean miPerfilConHab;
	private String miPerfilConTitulo;
	private String miPerfilConCorto;
	private String miPerfilConLargo;
	private Integer miPerfilConVencim;

	private Boolean miPerfilCanHab;
	private String miPerfilCanTitulo;
	private String miPerfilCanCorto;
	private String miPerfilCanLargo;
	private Integer miPerfilCanVencim;

	private Boolean miPerfilRecHab;
	private String miPerfilRecTitulo;
	private String miPerfilRecCorto;
	private String miPerfilRecLargo;
	private Integer miPerfilRecVencim;
	private Integer miPerfilRecHora;
	private Integer miPerfilRecDias;
	
	//cambios de tiempos máximos de reservas pendiente
	private Integer reservaPendienteTiempoMax;
    private Integer reservaMultiplePendienteTiempoMax;
	
	//Auditorías
	private Date fechaModificacion;
	private String usuario;
	private Integer version;
	private Integer tipoOperacion;

	/**
	 * Constructor por copia no en profundidad.
	 */
	public RecursoAud(Recurso recurso) {
		this.idRecurso = recurso.getId();
		this.nombre = recurso.getNombre();
		this.descripcion = recurso.getDescripcion();
		this.fechaInicio = recurso.getFechaInicio();
		this.fechaFin = recurso.getFechaFin();
		this.fechaInicioDisp = recurso.getFechaInicioDisp();
		this.fechaFinDisp = recurso.getFechaFinDisp();
		this.diasInicioVentanaIntranet = recurso.getDiasInicioVentanaIntranet();
		this.diasVentanaIntranet = recurso.getDiasVentanaIntranet();
		this.diasInicioVentanaInternet = recurso.getDiasInicioVentanaInternet();
		this.diasVentanaInternet = recurso.getDiasVentanaInternet();
		this.ventanaCuposMinimos = recurso.getVentanaCuposMinimos();
		this.cantDiasAGenerar = recurso.getCantDiasAGenerar();
		this.largoListaEspera = recurso.getLargoListaEspera();
		this.fechaBaja = recurso.getFechaBaja();
		this.mostrarNumeroEnLlamador = recurso.getMostrarNumeroEnLlamador();
		this.visibleInternet = recurso.getVisibleInternet();
		this.usarLlamador = recurso.getUsarLlamador();
		this.serie = recurso.getSerie();
		this.sabadoEsHabil = recurso.getSabadoEsHabil();
		this.domingoEsHabil = recurso.getDomingoEsHabil();
		this.mostrarNumeroEnTicket = recurso.getMostrarNumeroEnTicket();
		this.mostrarIdEnTicket = recurso.getMostrarIdEnTicket();
		this.fuenteTicket = recurso.getFuenteTicket();
		this.tamanioFuenteChica = recurso.getTamanioFuenteChica();
		this.tamanioFuenteNormal = recurso.getTamanioFuenteNormal();
		this.tamanioFuenteGrande = recurso.getTamanioFuenteGrande();
		this.oficinaId = recurso.getOficinaId();
		this.direccion = recurso.getDireccion();
		this.localidad = recurso.getLocalidad();
		this.departamento = recurso.getDepartamento();
		this.telefonos = recurso.getTelefonos();
		this.horarios = recurso.getHorarios();
		this.latitud = recurso.getLatitud();
		this.longitud = recurso.getLongitud();
		this.agenda = recurso.getAgenda().getId();
		this.presencialAdmite = recurso.getPresencialAdmite();
		this.presencialCupos = recurso.getPresencialCupos();
		this.presencialLunes = recurso.getPresencialLunes();
		this.presencialMartes = recurso.getPresencialMartes();
		this.presencialMiercoles = recurso.getPresencialMiercoles();
		this.presencialJueves = recurso.getPresencialJueves();
		this.presencialViernes = recurso.getPresencialViernes();
		this.presencialSabado = recurso.getPresencialSabado();
		this.presencialDomingo = recurso.getPresencialDomingo();
		this.multipleAdmite = recurso.getMultipleAdmite();
		this.cambiosAdmite = recurso.getCambiosAdmite();
		this.cambiosTiempo = recurso.getCambiosTiempo();
		this.cambiosUnidad = recurso.getCambiosUnidad();
		this.periodoValidacion = recurso.getPeriodoValidacion();
		this.validarPorIP = recurso.getValidarPorIP();
		this.cantidadPorIP = recurso.getCantidadPorIP();
		this.periodoPorIP = recurso.getPeriodoPorIP();
		this.ipsSinValidacion = recurso.getIpsSinValidacion();
		this.cancelacionTiempo = recurso.getCancelacionTiempo();
		this.cancelacionUnidad = recurso.getCancelacionUnidad();
		this.cancelacionTipo = recurso.getCancelacionTipo();
		this.miPerfilConHab = recurso.getMiPerfilConHab();
		this.miPerfilConTitulo = recurso.getMiPerfilConTitulo();
		this.miPerfilConCorto = recurso.getMiPerfilConCorto();
		this.miPerfilConLargo = recurso.getMiPerfilConLargo();
		this.miPerfilConVencim = recurso.getMiPerfilConVencim();
		this.miPerfilCanHab = recurso.getMiPerfilCanHab();
		this.miPerfilCanTitulo = recurso.getMiPerfilCanTitulo();
		this.miPerfilCanCorto = recurso.getMiPerfilCanCorto();
		this.miPerfilCanLargo = recurso.getMiPerfilCanLargo();
		this.miPerfilCanVencim = recurso.getMiPerfilCanVencim();
		this.miPerfilRecHab = recurso.getMiPerfilRecHab();
		this.miPerfilRecTitulo = recurso.getMiPerfilRecTitulo();
		this.miPerfilRecCorto = recurso.getMiPerfilRecCorto();
		this.miPerfilRecLargo = recurso.getMiPerfilRecLargo();
		this.miPerfilRecVencim = recurso.getMiPerfilRecVencim();
		this.miPerfilRecHora = recurso.getMiPerfilRecHora();
		this.miPerfilRecDias = recurso.getMiPerfilRecDias();
		this.version = recurso.getVersion();
		this.reservaPendienteTiempoMax = recurso.getReservaPendienteTiempoMax();
		this.reservaMultiplePendienteTiempoMax = recurso.getReservaMultiplePendienteTiempoMax();
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "seq_recurso_aud")
	@SequenceGenerator (name ="seq_recurso_aud", initialValue = 1, sequenceName = "s_ae_recurso_aud",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "id_recurso", nullable = false)
	public Integer getIdRecurso() {
		return idRecurso;
	}

	public void setIdRecurso(Integer idRecurso) {
		this.idRecurso = idRecurso;
	}

	@Column(nullable = false, length = 100)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(nullable = false, length = 1000)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "fecha_inicio", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Column(name = "fecha_fin")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@Column(name = "fecha_inicio_disp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaInicioDisp() {
		return fechaInicioDisp;
	}

	public void setFechaInicioDisp(Date fechaInicioDisp) {
		this.fechaInicioDisp = fechaInicioDisp;
	}

	@Column(name = "fecha_fin_disp")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaFinDisp() {
		return fechaFinDisp;
	}

	public void setFechaFinDisp(Date fechaFinDisp) {
		this.fechaFinDisp = fechaFinDisp;
	}

	@Column(name = "dias_inicio_ventana_intranet", nullable = false)
	public Integer getDiasInicioVentanaIntranet() {
		return diasInicioVentanaIntranet;
	}

	public void setDiasInicioVentanaIntranet(Integer diasInicioVentanaIntranet) {
		this.diasInicioVentanaIntranet = diasInicioVentanaIntranet;
	}

	@Column(name = "dias_ventana_intranet", nullable = false)
	public Integer getDiasVentanaIntranet() {
		return diasVentanaIntranet;
	}

	public void setDiasVentanaIntranet(Integer diasVentanaIntranet) {
		this.diasVentanaIntranet = diasVentanaIntranet;
	}

	@Column(name = "dias_inicio_ventana_internet", nullable = false)
	public Integer getDiasInicioVentanaInternet() {
		return diasInicioVentanaInternet;
	}

	public void setDiasInicioVentanaInternet(Integer diasInicioVentanaInternet) {
		this.diasInicioVentanaInternet = diasInicioVentanaInternet;
	}

	@Column(name = "dias_ventana_internet", nullable = false)
	public Integer getDiasVentanaInternet() {
		return diasVentanaInternet;
	}

	public void setDiasVentanaInternet(Integer diasVentanaInternet) {
		this.diasVentanaInternet = diasVentanaInternet;
	}

	@Column(name = "ventana_cupos_minimos", nullable = false)
	public Integer getVentanaCuposMinimos() {
		return ventanaCuposMinimos;
	}

	public void setVentanaCuposMinimos(Integer ventanaCuposMinimos) {
		this.ventanaCuposMinimos = ventanaCuposMinimos;
	}

	@Column(name = "cant_dias_a_generar", nullable = false)
	public Integer getCantDiasAGenerar() {
		return cantDiasAGenerar;
	}

	public void setCantDiasAGenerar(Integer cantDiasAGenerar) {
		this.cantDiasAGenerar = cantDiasAGenerar;
	}

	@Column(name = "largo_lista_espera", nullable = true)
	public Integer getLargoListaEspera() {
		return largoListaEspera;
	}

	public void setLargoListaEspera(Integer largoListaEspera) {
		this.largoListaEspera = largoListaEspera;
	}

	@Column(name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@Column(name = "mostrar_numero_en_llamador", nullable = false)
	public Boolean getMostrarNumeroEnLlamador() {
		return mostrarNumeroEnLlamador;
	}

	public void setMostrarNumeroEnLlamador(Boolean mostrarNumeroEnLlamador) {
		this.mostrarNumeroEnLlamador = mostrarNumeroEnLlamador;
	}

	@Column(name = "mostrar_numero_en_ticket", nullable = false)
	public Boolean getMostrarNumeroEnTicket() {
		return mostrarNumeroEnTicket;
	}

	public void setMostrarNumeroEnTicket(Boolean mostrarNumeroEnTicket) {
		this.mostrarNumeroEnTicket = mostrarNumeroEnTicket;
	}

	@Column(name = "usar_llamador", nullable = false)
	public Boolean getUsarLlamador() {
		return usarLlamador;
	}

	public void setUsarLlamador(Boolean usarLlamador) {
		this.usarLlamador = usarLlamador;
	}

	@Column(name = "serie", nullable = true)
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Column(name = "visible_internet", nullable = false)
	public Boolean getVisibleInternet() {
		return visibleInternet;
	}

	public void setVisibleInternet(Boolean visibleInternet) {
		this.visibleInternet = visibleInternet;
	}

	@Column(name = "sabado_es_habil", nullable = false)
	public Boolean getSabadoEsHabil() {
		return sabadoEsHabil;
	}

	public void setSabadoEsHabil(Boolean sabadoEsHabil) {
		this.sabadoEsHabil = sabadoEsHabil;
	}

	@Column(name = "domingo_es_habil", nullable = false)
	public Boolean getDomingoEsHabil() {
		return domingoEsHabil;
	}

	public void setDomingoEsHabil(Boolean domingoEsHabil) {
		this.domingoEsHabil = domingoEsHabil;
	}

	@Column(name = "agenda", nullable = false)
	public Integer getAgenda() {
		return agenda;
	}

	public void setAgenda(Integer agenda) {
		this.agenda = agenda;
	}

	@Column(name = "version", nullable = false)
	public Integer getVersion() {
		return version;
	}

	protected void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "oficina_id")
	public String getOficinaId() {
		return oficinaId;
	}

	public void setOficinaId(String oficinaId) {
		this.oficinaId = oficinaId;
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

	@Column(name = "mostrar_id_en_ticket", nullable = false)
	public Boolean getMostrarIdEnTicket() {
		return mostrarIdEnTicket;
	}

	public void setMostrarIdEnTicket(Boolean mostrarIdEnTicket) {
		this.mostrarIdEnTicket = mostrarIdEnTicket;
	}

	@Column(name = "presencial_admite", nullable = false)
	public Boolean getPresencialAdmite() {
		return presencialAdmite;
	}

	public void setPresencialAdmite(Boolean presencialAdmite) {
		this.presencialAdmite = presencialAdmite;
	}

	@Column(name = "presencial_cupos", nullable = false)
	public Integer getPresencialCupos() {
		return presencialCupos;
	}

	public void setPresencialCupos(Integer presencialCupos) {
		this.presencialCupos = presencialCupos;
	}

	@Column(name = "presencial_lunes", nullable = false)
	public Boolean getPresencialLunes() {
		return presencialLunes;
	}

	public void setPresencialLunes(Boolean presencialLunes) {
		this.presencialLunes = presencialLunes;
	}

	@Column(name = "presencial_martes", nullable = false)
	public Boolean getPresencialMartes() {
		return presencialMartes;
	}

	public void setPresencialMartes(Boolean presencialMartes) {
		this.presencialMartes = presencialMartes;
	}

	@Column(name = "presencial_miercoles", nullable = false)
	public Boolean getPresencialMiercoles() {
		return presencialMiercoles;
	}

	public void setPresencialMiercoles(Boolean presencialMiercoles) {
		this.presencialMiercoles = presencialMiercoles;
	}

	@Column(name = "presencial_jueves", nullable = false)
	public Boolean getPresencialJueves() {
		return presencialJueves;
	}

	public void setPresencialJueves(Boolean presencialJueves) {
		this.presencialJueves = presencialJueves;
	}

	@Column(name = "presencial_viernes", nullable = false)
	public Boolean getPresencialViernes() {
		return presencialViernes;
	}

	public void setPresencialViernes(Boolean presencialViernes) {
		this.presencialViernes = presencialViernes;
	}

	@Column(name = "presencial_sabado", nullable = false)
	public Boolean getPresencialSabado() {
		return presencialSabado;
	}

	public void setPresencialSabado(Boolean presencialSabado) {
		this.presencialSabado = presencialSabado;
	}

	@Column(name = "presencial_domingo", nullable = false)
	public Boolean getPresencialDomingo() {
		return presencialDomingo;
	}

	public void setPresencialDomingo(Boolean presencialDomingo) {
		this.presencialDomingo = presencialDomingo;
	}

	@Column(name = "multiple_admite", nullable = false)
	public Boolean getMultipleAdmite() {
		return multipleAdmite;
	}

	public void setMultipleAdmite(Boolean multipleAdmite) {
		this.multipleAdmite = multipleAdmite;
	}

	@Column(name = "cambios_admite", nullable = false)
	public Boolean getCambiosAdmite() {
		return cambiosAdmite;
	}

	public void setCambiosAdmite(Boolean cambiosAdmite) {
		this.cambiosAdmite = cambiosAdmite;
	}

	@Column(name = "cambios_tiempo", nullable = false)
	public Integer getCambiosTiempo() {
		return cambiosTiempo;
	}

	public void setCambiosTiempo(Integer cambiosTiempo) {
		this.cambiosTiempo = cambiosTiempo;
	}

	@Column(name = "cambios_unidad", nullable = false)
	public Integer getCambiosUnidad() {
		return cambiosUnidad;
	}

	public void setCambiosUnidad(Integer cambiosUnidad) {
		this.cambiosUnidad = cambiosUnidad;
	}

	@Column(name = "fuente_ticket", nullable = false)
	public String getFuenteTicket() {
		return fuenteTicket;
	}

	public void setFuenteTicket(String fuenteTicket) {
		this.fuenteTicket = fuenteTicket;
	}

	@Column(name = "tamanio_fuente_grande", nullable = false)
	public Integer getTamanioFuenteGrande() {
		return tamanioFuenteGrande;
	}

	public void setTamanioFuenteGrande(Integer tamanioFuenteGrande) {
		this.tamanioFuenteGrande = tamanioFuenteGrande;
	}

	@Column(name = "tamanio_fuente_normal", nullable = false)
	public Integer getTamanioFuenteNormal() {
		return tamanioFuenteNormal;
	}

	public void setTamanioFuenteNormal(Integer tamanioFuenteNormal) {
		this.tamanioFuenteNormal = tamanioFuenteNormal;
	}

	@Column(name = "tamanio_fuente_chica", nullable = false)
	public Integer getTamanioFuenteChica() {
		return tamanioFuenteChica;
	}

	public void setTamanioFuenteChica(Integer tamanioFuenteChica) {
		this.tamanioFuenteChica = tamanioFuenteChica;
	}

	@Column(name = "periodo_validacion", nullable = false)
	public Integer getPeriodoValidacion() {
		return periodoValidacion;
	}

	public void setPeriodoValidacion(Integer periodoValidacion) {
		this.periodoValidacion = periodoValidacion;
	}

	@Column(name = "validar_por_ip", nullable = false)
	public Boolean getValidarPorIP() {
		return validarPorIP;
	}

	public void setValidarPorIP(Boolean validarPorIP) {
		this.validarPorIP = validarPorIP;
	}

	@Column(name = "cantidad_por_ip", nullable = false)
	public Integer getCantidadPorIP() {
		return cantidadPorIP;
	}

	public void setCantidadPorIP(Integer cantidadPorIP) {
		this.cantidadPorIP = cantidadPorIP;
	}

	@Column(name = "periodo_por_ip", nullable = false)
	public Integer getPeriodoPorIP() {
		return periodoPorIP;
	}

	public void setPeriodoPorIP(Integer periodoPorIP) {
		this.periodoPorIP = periodoPorIP;
	}

	@Column(name = "ips_sin_validacion", nullable = false)
	public String getIpsSinValidacion() {
		return ipsSinValidacion;
	}

	public void setIpsSinValidacion(String ipsSinValidacion) {
		this.ipsSinValidacion = ipsSinValidacion;
	}

	@Column(name = "cancela_tiempo", nullable = false)
	public Integer getCancelacionTiempo() {
		return cancelacionTiempo;
	}

	public void setCancelacionTiempo(Integer cancelacionTiempo) {
		this.cancelacionTiempo = cancelacionTiempo;
	}

	@Column(name = "cancela_unidad", nullable = false)
	public Integer getCancelacionUnidad() {
		return cancelacionUnidad;
	}

	public void setCancelacionUnidad(Integer cancelacionUnidad) {
		this.cancelacionUnidad = cancelacionUnidad;
	}

	@Column(name = "cancela_tipo", nullable = false)
	@Enumerated(EnumType.STRING)
	public FormaCancelacion getCancelacionTipo() {
		return cancelacionTipo;
	}

	public void setCancelacionTipo(FormaCancelacion cancelacionTipo) {
		this.cancelacionTipo = cancelacionTipo;
	}

	@Column(name = "mi_perfil_con_hab", nullable = false)
	public Boolean getMiPerfilConHab() {
		return miPerfilConHab;
	}

	public void setMiPerfilConHab(Boolean miPerfilConHab) {
		this.miPerfilConHab = miPerfilConHab;
	}

	@Column(name = "mi_perfil_can_hab", nullable = false)
	public Boolean getMiPerfilCanHab() {
		return miPerfilCanHab;
	}

	public void setMiPerfilCanHab(Boolean miPerfilCanHab) {
		this.miPerfilCanHab = miPerfilCanHab;
	}

	@Column(name = "mi_perfil_rec_hab", nullable = false)
	public Boolean getMiPerfilRecHab() {
		return miPerfilRecHab;
	}

	public void setMiPerfilRecHab(Boolean miPerfilRecHab) {
		this.miPerfilRecHab = miPerfilRecHab;
	}

	@Column(name = "mi_perfil_con_tit", nullable = false)
	public String getMiPerfilConTitulo() {
		return miPerfilConTitulo;
	}

	public void setMiPerfilConTitulo(String miPerfilConTitulo) {
		this.miPerfilConTitulo = miPerfilConTitulo;
	}

	@Column(name = "mi_perfil_con_cor", nullable = false)
	public String getMiPerfilConCorto() {
		return miPerfilConCorto;
	}

	public void setMiPerfilConCorto(String miPerfilConCorto) {
		this.miPerfilConCorto = miPerfilConCorto;
	}

	@Column(name = "mi_perfil_con_lar", nullable = false)
	public String getMiPerfilConLargo() {
		return miPerfilConLargo;
	}

	public void setMiPerfilConLargo(String miPerfilConLargo) {
		this.miPerfilConLargo = miPerfilConLargo;
	}

	@Column(name = "mi_perfil_con_ven", nullable = false)
	public Integer getMiPerfilConVencim() {
		return miPerfilConVencim;
	}

	public void setMiPerfilConVencim(Integer miPerfilConVencim) {
		this.miPerfilConVencim = miPerfilConVencim;
	}

	@Column(name = "mi_perfil_can_tit", nullable = false)
	public String getMiPerfilCanTitulo() {
		return miPerfilCanTitulo;
	}

	public void setMiPerfilCanTitulo(String miPerfilCanTitulo) {
		this.miPerfilCanTitulo = miPerfilCanTitulo;
	}

	@Column(name = "mi_perfil_can_cor", nullable = false)
	public String getMiPerfilCanCorto() {
		return miPerfilCanCorto;
	}

	public void setMiPerfilCanCorto(String miPerfilCanCorto) {
		this.miPerfilCanCorto = miPerfilCanCorto;
	}

	@Column(name = "mi_perfil_can_lar", nullable = false)
	public String getMiPerfilCanLargo() {
		return miPerfilCanLargo;
	}

	public void setMiPerfilCanLargo(String miPerfilCanLargo) {
		this.miPerfilCanLargo = miPerfilCanLargo;
	}

	@Column(name = "mi_perfil_can_ven", nullable = false)
	public Integer getMiPerfilCanVencim() {
		return miPerfilCanVencim;
	}

	public void setMiPerfilCanVencim(Integer miPerfilCanVencim) {
		this.miPerfilCanVencim = miPerfilCanVencim;
	}

	@Column(name = "mi_perfil_rec_tit", nullable = false)
	public String getMiPerfilRecTitulo() {
		return miPerfilRecTitulo;
	}

	public void setMiPerfilRecTitulo(String miPerfilRecTitulo) {
		this.miPerfilRecTitulo = miPerfilRecTitulo;
	}

	@Column(name = "mi_perfil_rec_cor", nullable = false)
	public String getMiPerfilRecCorto() {
		return miPerfilRecCorto;
	}

	public void setMiPerfilRecCorto(String miPerfilRecCorto) {
		this.miPerfilRecCorto = miPerfilRecCorto;
	}

	@Column(name = "mi_perfil_rec_lar", nullable = false)
	public String getMiPerfilRecLargo() {
		return miPerfilRecLargo;
	}

	public void setMiPerfilRecLargo(String miPerfilRecLargo) {
		this.miPerfilRecLargo = miPerfilRecLargo;
	}

	@Column(name = "mi_perfil_rec_ven", nullable = false)
	public Integer getMiPerfilRecVencim() {
		return miPerfilRecVencim;
	}

	public void setMiPerfilRecVencim(Integer miPerfilRecVencim) {
		this.miPerfilRecVencim = miPerfilRecVencim;
	}

	@Column(name = "mi_perfil_rec_hora", nullable = false)
	public Integer getMiPerfilRecHora() {
		return miPerfilRecHora;
	}

	public void setMiPerfilRecHora(Integer miPerfilRecHora) {
		this.miPerfilRecHora = miPerfilRecHora;
	}

	@Column(name = "mi_perfil_rec_dias", nullable = false)
	public Integer getMiPerfilRecDias() {
		return miPerfilRecDias;
	}

	public void setMiPerfilRecDias(Integer miPerfilRecDias) {
		this.miPerfilRecDias = miPerfilRecDias;
	}

	@Column(name = "fecha_modificacion", nullable = false)
	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	@Column(name = "usuario", nullable = false)
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Column(name = "tipo_operacion", nullable = false)
	public Integer getTipoOperacion() {
		return tipoOperacion;
	}

	public void setTipoOperacion(Integer tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	
	@Column (name = "reserva_pend_tiempo_max", nullable = false)
    public Integer getReservaPendienteTiempoMax() {
	    return reservaPendienteTiempoMax;
	}
	
	public void setReservaPendienteTiempoMax(Integer reservaPendienteTiempoMax) {
		this.reservaPendienteTiempoMax = reservaPendienteTiempoMax;
	}
	  
	  
	@Column (name = "reserva_multiple_pend_tiempo_max", nullable = false)
	public Integer getReservaMultiplePendienteTiempoMax() {
		return reservaMultiplePendienteTiempoMax;
	}
	
	public void setReservaMultiplePendienteTiempoMax(Integer reservaMultiplePendienteTiempoMax) {
		this.reservaMultiplePendienteTiempoMax = reservaMultiplePendienteTiempoMax;
	}

	
}