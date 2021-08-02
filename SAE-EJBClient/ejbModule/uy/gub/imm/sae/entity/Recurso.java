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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.ColumnResult;
import javax.xml.bind.annotation.XmlTransient;
import uy.gub.imm.sae.common.enumerados.FormaCancelacion;

@Entity
@Table (name = "ae_recursos")
public class Recurso implements Serializable {
	private static final long serialVersionUID = -5197426783029830293L;

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
	private Integer largoListaEspera;
	private Integer version;
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
  
	//Datos obtenidos a partir de TramitesUy
	private String oficinaId; //Esto es inventado, las oficinas no tienen id en TramitesUy
	private String direccion;
	private String localidad;
	private String departamento;
	private String telefonos;
	private String horarios;
	
	@Column(name = "latitud")
	private BigDecimal latitud;
	@Column(name = "longitud")
	private BigDecimal longitud;
	
	private Agenda agenda;
	private List<Plantilla> plantillas;
	private List<AgrupacionDato> agrupacionDatos;
	private List<Disponibilidad> disponibilidades;
	private List<DatoDelRecurso> datoDelRecurso;
	private List<DatoASolicitar> datosASolicitar;
	private List<ValidacionPorRecurso> validacionesPorRecurso;
	private List<AccionPorRecurso> accionesPorRecurso;
	private List<ServicioPorRecurso> autocompletadosPorRecurso;

  private Map<String, TextoRecurso> textosRecurso;
	
  //Datos de atención presencial
  private Boolean presencialAdmite;
  private Integer presencialCupos;
  private Boolean presencialLunes;
  private Boolean presencialMartes;
  private Boolean presencialMiercoles;
  private Boolean presencialJueves;
  private Boolean presencialViernes;
  private Boolean presencialSabado;
  private Boolean presencialDomingo;
  
  //Datos de reserva múltiple
  private Boolean multipleAdmite;
  //Datos de cambios de reserva
  private Boolean cambiosAdmite;
  private Integer cambiosTiempo;
  private Integer cambiosUnidad; //Calendar.DATE, Calendar.HOUR, Calendar.MINUTE
  
  private Integer periodoValidacion;
  
  //Validación por IP
  private Boolean validarPorIP;
  private Integer cantidadPorIP;
  private Integer periodoPorIP;
  private String ipsSinValidacion;

  //Datos de cancelaciones de reserva
  private Integer cancelacionTiempo;
  private Integer cancelacionUnidad; //Calendar.DATE, Calendar.HOUR, Calendar.MINUTE
  private FormaCancelacion cancelacionTipo; //I=Inmediata, D=Diferida
  
  //Datos de integración con MiPerfil
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
  
  private Integer reservaPendienteTiempoMax;
  private Integer reservaMultiplePendienteTiempoMax;
  
  private AccionMiPerfil accionMiPerfil;
  
  private boolean seleccionado;
  
	public Recurso () {
		visibleInternet = false;
		plantillas = new ArrayList<Plantilla>();
		agrupacionDatos = new ArrayList<AgrupacionDato>();
		disponibilidades = new ArrayList<Disponibilidad>();
		datoDelRecurso = new ArrayList<DatoDelRecurso>();
		datosASolicitar = new ArrayList<DatoASolicitar>();
		validacionesPorRecurso = new ArrayList<ValidacionPorRecurso>();	
		accionesPorRecurso = new ArrayList<AccionPorRecurso>();
		
		fechaInicio = new Date();
		usarLlamador = true;
		sabadoEsHabil = false;
		domingoEsHabil = false;

		presencialAdmite = false;
		presencialCupos = 0;
		presencialLunes = false;
		presencialMartes = false;
		presencialMiercoles = false;
		presencialJueves = false;
		presencialViernes = false;
		presencialSabado = false;
	    presencialDomingo = false;
	    
	    multipleAdmite = false;
	    cambiosAdmite = false;
	    cambiosTiempo = null;
	    cambiosUnidad = null;
	    
	    periodoValidacion = null;
	    
	    validarPorIP = null;
	    cantidadPorIP = null;
	    periodoPorIP = null;
	    ipsSinValidacion = null;
	    
	    cancelacionTiempo = null;
	    cancelacionUnidad = null;
	    cancelacionTipo = null;
	    
	    miPerfilConHab = true;
	    miPerfilCanHab = true;
	    miPerfilRecHab = true;
	    miPerfilConTitulo="";
	    miPerfilConCorto="";
	    miPerfilConLargo="";
	    miPerfilConVencim=0;
	    miPerfilCanTitulo="";
	    miPerfilCanCorto="";
	    miPerfilCanLargo="";
	    miPerfilCanVencim=0;
	    miPerfilRecTitulo="";
	    miPerfilRecCorto="";
	    miPerfilRecLargo="";
	    miPerfilRecVencim=0;
	    miPerfilRecHora=0;
	    miPerfilRecDias=0;
    
	}
	
	/**
	 * Constructor por copia no en profundidad.
	 */
	public Recurso (Recurso original) {
		plantillas = new ArrayList<Plantilla>();
		agrupacionDatos = new ArrayList<AgrupacionDato>();
		disponibilidades = new ArrayList<Disponibilidad>();
		datoDelRecurso = new ArrayList<DatoDelRecurso>();
		datosASolicitar = new ArrayList<DatoASolicitar>();
		validacionesPorRecurso = new ArrayList<ValidacionPorRecurso>();	
		accionesPorRecurso = new ArrayList<AccionPorRecurso>();
		
		id = original.getId();
		nombre = original.getNombre();
		descripcion = original.getDescripcion();
		fechaInicio = original.getFechaInicio();
		fechaFin = original.getFechaFin();
		fechaInicioDisp = original.getFechaInicioDisp();
		fechaFinDisp = original.getFechaFinDisp();
		diasInicioVentanaIntranet = original.getDiasInicioVentanaIntranet();
		diasVentanaIntranet = original.getDiasVentanaIntranet();
		diasInicioVentanaInternet = original.getDiasInicioVentanaInternet();
		diasVentanaInternet = original.getDiasVentanaInternet();
		ventanaCuposMinimos = original.getVentanaCuposMinimos();
		cantDiasAGenerar = original.getCantDiasAGenerar();
		largoListaEspera = original.getLargoListaEspera();
		fechaBaja = original.getFechaBaja();
		mostrarNumeroEnLlamador = original.getMostrarNumeroEnLlamador();
		mostrarNumeroEnTicket = original.getMostrarNumeroEnTicket();
		fuenteTicket = original.getFuenteTicket();
		tamanioFuenteChica = original.getTamanioFuenteChica();
	    tamanioFuenteNormal = original.getTamanioFuenteNormal();
	    tamanioFuenteGrande = original.getTamanioFuenteGrande();
	    mostrarIdEnTicket = original.getMostrarIdEnTicket();
		usarLlamador = original.getUsarLlamador();
		serie = original.getSerie();
		visibleInternet = original.getVisibleInternet();
		sabadoEsHabil = original.getSabadoEsHabil();
		domingoEsHabil = original.getDomingoEsHabil();
		
		agenda = new Agenda(original.getAgenda());
		
		this.oficinaId = original.oficinaId;
		this.direccion = original.direccion;
		this.localidad = original.localidad;
		this.departamento = original.departamento;
		this.telefonos = original.telefonos;
		this.horarios = original.horarios;
		
		this.latitud = original.latitud;
		this.longitud = original.longitud;
	
	    this.presencialAdmite = original.presencialAdmite;
	    this.presencialCupos = original.presencialCupos;
	    this.presencialLunes = original.presencialLunes;
	    this.presencialMartes = original.presencialMartes;
	    this.presencialMiercoles = original.presencialMiercoles;
	    this.presencialJueves = original.presencialJueves;
	    this.presencialViernes = original.presencialViernes;
	    this.presencialSabado = original.presencialSabado;
	    this.presencialDomingo = original.presencialDomingo;
			
	    this.multipleAdmite = original.multipleAdmite;
	    
	    this.cambiosAdmite = original.cambiosAdmite;
	    this.cambiosTiempo = original.cambiosTiempo;
	    this.cambiosUnidad = original.cambiosUnidad;
	    
	    this.periodoValidacion = original.periodoValidacion;
	    
	    this.validarPorIP = original.validarPorIP;
	    this.cantidadPorIP = original.cantidadPorIP;
	    this.periodoPorIP = original.periodoPorIP;
	    this.ipsSinValidacion = original.ipsSinValidacion;
	    
	    this.cancelacionTiempo = original.cancelacionTiempo;
	    this.cancelacionUnidad = original.cancelacionUnidad;
	    this.cancelacionTipo = original.cancelacionTipo;
    
	    miPerfilConHab = original.miPerfilConHab;
	    miPerfilCanHab = original.miPerfilCanHab;
	    miPerfilRecHab = original.miPerfilRecHab;
	    miPerfilConTitulo = original.miPerfilConTitulo;
	    miPerfilConCorto = original.miPerfilConCorto;
	    miPerfilConLargo = original.miPerfilConLargo;
	    miPerfilConVencim = original.miPerfilConVencim;
	    miPerfilCanTitulo = original.miPerfilCanTitulo;
	    miPerfilCanCorto = original.miPerfilCanCorto;
	    miPerfilCanLargo = original.miPerfilCanLargo;
	    miPerfilCanVencim = original.miPerfilCanVencim;
	    miPerfilRecTitulo = original.miPerfilRecTitulo;
	    miPerfilRecCorto = original.miPerfilRecCorto;
	    miPerfilRecLargo = original.miPerfilRecLargo;
	    miPerfilRecVencim = original.miPerfilRecVencim;
	    miPerfilRecHora = original.miPerfilRecHora;
	    miPerfilRecDias = original.miPerfilRecDias;
	    
	    this.reservaPendienteTiempoMax = original.getReservaPendienteTiempoMax();
	    this.reservaMultiplePendienteTiempoMax = original.getReservaMultiplePendienteTiempoMax();
    
	}

	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "seq_recurso")
	@SequenceGenerator (name ="seq_recurso", initialValue = 1, sequenceName = "s_ae_recurso",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column (nullable = false, length = 100)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column (nullable = false, length = 1000)
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Column (name = "fecha_inicio", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaInicio() {
		return fechaInicio;
	}
	
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	@Column (name = "fecha_fin")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaFin() {
		return fechaFin;
	}
	
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@Column (name = "fecha_inicio_disp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaInicioDisp() {
		return fechaInicioDisp;
	}
	
	public void setFechaInicioDisp(Date fechaInicioDisp) {
		this.fechaInicioDisp = fechaInicioDisp;
	}

	@Column (name = "fecha_fin_disp")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaFinDisp() {
		return fechaFinDisp;
	}
	
	public void setFechaFinDisp(Date fechaFinDisp) {
		this.fechaFinDisp = fechaFinDisp;
	}
	
	@Column (name="dias_inicio_ventana_intranet", nullable = false)
	public Integer getDiasInicioVentanaIntranet() {
		return diasInicioVentanaIntranet;
	}
	public void setDiasInicioVentanaIntranet(Integer diasInicioVentanaIntranet) {
		this.diasInicioVentanaIntranet = diasInicioVentanaIntranet;
	}

	@Column (name="dias_ventana_intranet", nullable = false)
	public Integer getDiasVentanaIntranet() {
		return diasVentanaIntranet;
	}
	
	public void setDiasVentanaIntranet(Integer diasVentanaIntranet) {
		this.diasVentanaIntranet = diasVentanaIntranet;
	}

	@Column (name="dias_inicio_ventana_internet", nullable = false)
	public Integer getDiasInicioVentanaInternet() {
		return diasInicioVentanaInternet;
	}
	
	public void setDiasInicioVentanaInternet(Integer diasInicioVentanaInternet) {
		this.diasInicioVentanaInternet = diasInicioVentanaInternet;
	}
	
	@Column (name="dias_ventana_internet", nullable = false)
	public Integer getDiasVentanaInternet() {
		return diasVentanaInternet;
	}

	public void setDiasVentanaInternet(Integer diasVentanaInternet) {
		this.diasVentanaInternet = diasVentanaInternet;
	}

	@Column (name="ventana_cupos_minimos", nullable = false)
	public Integer getVentanaCuposMinimos() {
		return ventanaCuposMinimos;
	}
	
	public void setVentanaCuposMinimos(Integer ventanaCuposMinimos) {
		this.ventanaCuposMinimos = ventanaCuposMinimos;
	}
	
	@Column (name="cant_dias_a_generar", nullable = false)
	public Integer getCantDiasAGenerar() {
		return cantDiasAGenerar;
	}
	
	public void setCantDiasAGenerar(Integer cantDiasAGenerar) {
		this.cantDiasAGenerar = cantDiasAGenerar;
	}
	
	@Column (name="largo_lista_espera", nullable = true)
	public Integer getLargoListaEspera() {
		return largoListaEspera;
	}
	
	public void setLargoListaEspera(Integer largoListaEspera) {
		this.largoListaEspera = largoListaEspera;
	}

	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@Column (name = "mostrar_numero_en_llamador", nullable = false)
	public Boolean getMostrarNumeroEnLlamador() {
		return mostrarNumeroEnLlamador;
	}
	
	public void setMostrarNumeroEnLlamador(Boolean mostrarNumeroEnLlamador) {
		this.mostrarNumeroEnLlamador = mostrarNumeroEnLlamador;
	}
	
	@Column (name = "mostrar_numero_en_ticket", nullable = false)
	public Boolean getMostrarNumeroEnTicket() {
		return mostrarNumeroEnTicket;
	}
	
	public void setMostrarNumeroEnTicket(Boolean mostrarNumeroEnTicket) {
		this.mostrarNumeroEnTicket = mostrarNumeroEnTicket;
	}
	
	@Column (name = "usar_llamador", nullable = false)
	public Boolean getUsarLlamador() {
		return usarLlamador;
	}
	
	public void setUsarLlamador(Boolean usarLlamador) {
		this.usarLlamador = usarLlamador;
	}

	@Column (name = "serie", nullable = true)
	public String getSerie() {
		return serie;
	}
	
	public void setSerie(String serie) {
		this.serie = serie;
	}
	
	@Column (name="visible_internet", nullable = false)
	public Boolean getVisibleInternet() {
		return visibleInternet;
	}
	
	public void setVisibleInternet(Boolean visibleInternet) {
		this.visibleInternet = visibleInternet;
	}
	
	@Column (name = "sabado_es_habil", nullable = false)
	public Boolean getSabadoEsHabil() {
		return sabadoEsHabil;
	}
	
	public void setSabadoEsHabil(Boolean sabadoEsHabil) {
		this.sabadoEsHabil = sabadoEsHabil;
	}
	
  @Column (name = "domingo_es_habil", nullable = false)
  public Boolean getDomingoEsHabil() {
    return domingoEsHabil;
  }
  public void setDomingoEsHabil(Boolean domingoEsHabil) {
    this.domingoEsHabil = domingoEsHabil;
  }
	
	@XmlTransient
	@ManyToOne (optional = false)
	@JoinColumn (name = "aeag_id", nullable = false)
	public Agenda getAgenda() {
		return agenda;
	}
	
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
	
	@XmlTransient
	@OneToMany (mappedBy = "recurso")
	public List<Plantilla> getPlantillas() {
		return plantillas;
	}
	public void setPlantillas(List<Plantilla> plantillas) {
		this.plantillas = plantillas;
	}
	
	@OneToMany (mappedBy = "recurso")
	public List<AgrupacionDato> getAgrupacionDatos() {
		return agrupacionDatos;
	}
	
	public void setAgrupacionDatos(List<AgrupacionDato> agrupacionDatos) {
		this.agrupacionDatos = agrupacionDatos;
	}

	@OneToMany (mappedBy = "recurso")
	public List<DatoDelRecurso> getDatoDelRecurso() {
		return datoDelRecurso;
	}
	
	public void setDatoDelRecurso(List<DatoDelRecurso> datoDelRecurso) {
		this.datoDelRecurso = datoDelRecurso;
	}
	
	@XmlTransient
	@OneToMany (mappedBy = "recurso")
	public List<Disponibilidad> getDisponibilidades() {
		return disponibilidades;
	}
	
	public void setDisponibilidades(List<Disponibilidad> disponibilidades) {
		this.disponibilidades = disponibilidades;
	}
	@XmlTransient
	@OneToMany (mappedBy = "recurso")
	public List<DatoASolicitar> getDatoASolicitar() {
		return datosASolicitar;
	}
	
	public void setDatoASolicitar(List<DatoASolicitar> datosASolicitar) {
		this.datosASolicitar = datosASolicitar;
	}

	@XmlTransient
	@OneToMany (mappedBy = "recurso")
	public List<ValidacionPorRecurso> getValidacionesPorRecurso() {
		return validacionesPorRecurso;
	}

	public void setValidacionesPorRecurso(
			List<ValidacionPorRecurso> validacionesPorRecurso) {
		this.validacionesPorRecurso = validacionesPorRecurso;
	}
	
	@XmlTransient
	@OneToMany (mappedBy = "recurso")
	public List<AccionPorRecurso> getAccionesPorRecurso() {
		return accionesPorRecurso;
	}

	public void setAccionesPorRecurso(List<AccionPorRecurso> accionesPorRecurso) {
		this.accionesPorRecurso = accionesPorRecurso;
	}

	@Version
	@Column (name = "version", nullable = false)
	public Integer getVersion() {
		return version;
	}

	protected void setVersion(Integer version) {
		this.version = version;
	}

	@XmlTransient
	@OneToMany (mappedBy = "recurso")
	public List<ServicioPorRecurso> getAutocompletadosPorRecurso() {
		return autocompletadosPorRecurso;
	}
	
	public void setAutocompletadosPorRecurso(List<ServicioPorRecurso> autocompletadosPorRecurso) {
		this.autocompletadosPorRecurso = autocompletadosPorRecurso;
	}

	@Column(name="oficina_id")
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

	@OneToMany(mappedBy="recurso", fetch=FetchType.EAGER)
  @MapKey(name="idioma")
	public Map<String, TextoRecurso> getTextosRecurso() {
		return textosRecurso;
	}

	public void setTextosRecurso(Map<String, TextoRecurso> textosRecurso) {
		this.textosRecurso = textosRecurso;
	}

	@Column (name = "mostrar_id_en_ticket", nullable = false)
	public Boolean getMostrarIdEnTicket() {
		return mostrarIdEnTicket;
	}

	public void setMostrarIdEnTicket(Boolean mostrarIdEnTicket) {
		this.mostrarIdEnTicket = mostrarIdEnTicket;
	}

  @Column (name = "presencial_admite", nullable = false)
  public Boolean getPresencialAdmite() {
    return presencialAdmite;
  }

  public void setPresencialAdmite(Boolean presencialAdmite) {
    this.presencialAdmite = presencialAdmite;
  }

  @Column (name = "presencial_cupos", nullable = false)
  public Integer getPresencialCupos() {
    return presencialCupos;
  }

  public void setPresencialCupos(Integer presencialCupos) {
    this.presencialCupos = presencialCupos;
  }

  @Column (name = "presencial_lunes", nullable = false)
  public Boolean getPresencialLunes() {
    return presencialLunes;
  }

  public void setPresencialLunes(Boolean presencialLunes) {
    this.presencialLunes = presencialLunes;
  }

  @Column (name = "presencial_martes", nullable = false)
  public Boolean getPresencialMartes() {
    return presencialMartes;
  }

  public void setPresencialMartes(Boolean presencialMartes) {
    this.presencialMartes = presencialMartes;
  }

  @Column (name = "presencial_miercoles", nullable = false)
  public Boolean getPresencialMiercoles() {
    return presencialMiercoles;
  }

  public void setPresencialMiercoles(Boolean presencialMiercoles) {
    this.presencialMiercoles = presencialMiercoles;
  }

  @Column (name = "presencial_jueves", nullable = false)
  public Boolean getPresencialJueves() {
    return presencialJueves;
  }

  public void setPresencialJueves(Boolean presencialJueves) {
    this.presencialJueves = presencialJueves;
  }

  @Column (name = "presencial_viernes", nullable = false)
  public Boolean getPresencialViernes() {
    return presencialViernes;
  }

  public void setPresencialViernes(Boolean presencialViernes) {
    this.presencialViernes = presencialViernes;
  }

  @Column (name = "presencial_sabado", nullable = false)
  public Boolean getPresencialSabado() {
    return presencialSabado;
  }

  public void setPresencialSabado(Boolean presencialSabado) {
    this.presencialSabado = presencialSabado;
  }

  @Column (name = "presencial_domingo", nullable = false)
  public Boolean getPresencialDomingo() {
    return presencialDomingo;
  }

  public void setPresencialDomingo(Boolean presencialDomingo) {
    this.presencialDomingo = presencialDomingo;
  }

  @Column (name = "multiple_admite", nullable = false)
  public Boolean getMultipleAdmite() {
    return multipleAdmite;
  }

  public void setMultipleAdmite(Boolean multipleAdmite) {
    this.multipleAdmite = multipleAdmite;
  }

  @Column (name = "cambios_admite", nullable = false)
  public Boolean getCambiosAdmite() {
    return cambiosAdmite;
  }

  public void setCambiosAdmite(Boolean cambiosAdmite) {
    this.cambiosAdmite = cambiosAdmite;
  }

  @Column (name = "cambios_tiempo", nullable = false)
  public Integer getCambiosTiempo() {
    return cambiosTiempo;
  }

  public void setCambiosTiempo(Integer cambiosTiempo) {
    this.cambiosTiempo = cambiosTiempo;
  }

  @Column (name = "cambios_unidad", nullable = false)
  public Integer getCambiosUnidad() {
    return cambiosUnidad;
  }

  public void setCambiosUnidad(Integer cambiosUnidad) {
    this.cambiosUnidad = cambiosUnidad;
  }

  @Column (name = "fuente_ticket", nullable = false)
  public String getFuenteTicket() {
    return fuenteTicket;
  }

  public void setFuenteTicket(String fuenteTicket) {
    this.fuenteTicket = fuenteTicket;
  }

  @Column (name = "tamanio_fuente_grande", nullable = false)
  public Integer getTamanioFuenteGrande() {
    return tamanioFuenteGrande;
  }

  public void setTamanioFuenteGrande(Integer tamanioFuenteGrande) {
    this.tamanioFuenteGrande = tamanioFuenteGrande;
  }

  @Column (name = "tamanio_fuente_normal", nullable = false)
  public Integer getTamanioFuenteNormal() {
    return tamanioFuenteNormal;
  }

  public void setTamanioFuenteNormal(Integer tamanioFuenteNormal) {
    this.tamanioFuenteNormal = tamanioFuenteNormal;
  }

  @Column (name = "tamanio_fuente_chica", nullable = false)
  public Integer getTamanioFuenteChica() {
    return tamanioFuenteChica;
  }

  public void setTamanioFuenteChica(Integer tamanioFuenteChica) {
    this.tamanioFuenteChica = tamanioFuenteChica;
  }
  
  @Column (name = "periodo_validacion", nullable = false)
  public Integer getPeriodoValidacion() {
    return periodoValidacion;
  }

  public void setPeriodoValidacion(Integer periodoValidacion) {
    this.periodoValidacion = periodoValidacion;
  }

  @Column (name = "validar_por_ip", nullable = false)
  public Boolean getValidarPorIP() {
    return validarPorIP;
  }

  public void setValidarPorIP(Boolean validarPorIP) {
    this.validarPorIP = validarPorIP;
  }

  @Column (name = "cantidad_por_ip", nullable = false)
  public Integer getCantidadPorIP() {
    return cantidadPorIP;
  }

  public void setCantidadPorIP(Integer cantidadPorIP) {
    this.cantidadPorIP = cantidadPorIP;
  }

  @Column (name = "periodo_por_ip", nullable = false)
  public Integer getPeriodoPorIP() {
    return periodoPorIP;
  }

  public void setPeriodoPorIP(Integer periodoPorIP) {
    this.periodoPorIP = periodoPorIP;
  }

  @Column (name = "ips_sin_validacion", nullable = false)
  public String getIpsSinValidacion() {
    return ipsSinValidacion;
  }

  public void setIpsSinValidacion(String ipsSinValidacion) {
    this.ipsSinValidacion = ipsSinValidacion;
  }

  @Column (name = "cancela_tiempo", nullable = false)
  public Integer getCancelacionTiempo() {
    return cancelacionTiempo;
  }

  public void setCancelacionTiempo(Integer cancelacionTiempo) {
    this.cancelacionTiempo = cancelacionTiempo;
  }

  @Column (name = "cancela_unidad", nullable = false)
  public Integer getCancelacionUnidad() {
    return cancelacionUnidad;
  }

  public void setCancelacionUnidad(Integer cancelacionUnidad) {
    this.cancelacionUnidad = cancelacionUnidad;
  }

  @Column (name = "cancela_tipo", nullable = false)
  @Enumerated (EnumType.STRING)
  public FormaCancelacion getCancelacionTipo() {
    return cancelacionTipo;
  }

  public void setCancelacionTipo(FormaCancelacion cancelacionTipo) {
    this.cancelacionTipo = cancelacionTipo;
  }

  @Column (name = "mi_perfil_con_hab", nullable = false)
  public Boolean getMiPerfilConHab() {
    return miPerfilConHab;
  }

  public void setMiPerfilConHab(Boolean miPerfilConHab) {
    this.miPerfilConHab = miPerfilConHab;
  }

  @Column (name = "mi_perfil_can_hab", nullable = false)
  public Boolean getMiPerfilCanHab() {
    return miPerfilCanHab;
  }

  public void setMiPerfilCanHab(Boolean miPerfilCanHab) {
    this.miPerfilCanHab = miPerfilCanHab;
  }

  @Column (name = "mi_perfil_rec_hab", nullable = false)
  public Boolean getMiPerfilRecHab() {
    return miPerfilRecHab;
  }

  public void setMiPerfilRecHab(Boolean miPerfilRecHab) {
    this.miPerfilRecHab = miPerfilRecHab;
  }

  @Column (name = "mi_perfil_con_tit", nullable = false)
  public String getMiPerfilConTitulo() {
    return miPerfilConTitulo;
  }

  public void setMiPerfilConTitulo(String miPerfilConTitulo) {
    this.miPerfilConTitulo = miPerfilConTitulo;
  }

  @Column (name = "mi_perfil_con_cor", nullable = false)
  public String getMiPerfilConCorto() {
    return miPerfilConCorto;
  }

  public void setMiPerfilConCorto(String miPerfilConCorto) {
    this.miPerfilConCorto = miPerfilConCorto;
  }

  @Column (name = "mi_perfil_con_lar", nullable = false)
  public String getMiPerfilConLargo() {
    return miPerfilConLargo;
  }

  public void setMiPerfilConLargo(String miPerfilConLargo) {
    this.miPerfilConLargo = miPerfilConLargo;
  }

  @Column (name = "mi_perfil_con_ven", nullable = false)
  public Integer getMiPerfilConVencim() {
    return miPerfilConVencim;
  }

  public void setMiPerfilConVencim(Integer miPerfilConVencim) {
    this.miPerfilConVencim = miPerfilConVencim;
  }

  @Column (name = "mi_perfil_can_tit", nullable = false)
  public String getMiPerfilCanTitulo() {
    return miPerfilCanTitulo;
  }

  public void setMiPerfilCanTitulo(String miPerfilCanTitulo) {
    this.miPerfilCanTitulo = miPerfilCanTitulo;
  }

  @Column (name = "mi_perfil_can_cor", nullable = false)
  public String getMiPerfilCanCorto() {
    return miPerfilCanCorto;
  }

  public void setMiPerfilCanCorto(String miPerfilCanCorto) {
    this.miPerfilCanCorto = miPerfilCanCorto;
  }

  @Column (name = "mi_perfil_can_lar", nullable = false)
  public String getMiPerfilCanLargo() {
    return miPerfilCanLargo;
  }

  public void setMiPerfilCanLargo(String miPerfilCanLargo) {
    this.miPerfilCanLargo = miPerfilCanLargo;
  }

  @Column (name = "mi_perfil_can_ven", nullable = false)
  public Integer getMiPerfilCanVencim() {
    return miPerfilCanVencim;
  }

  public void setMiPerfilCanVencim(Integer miPerfilCanVencim) {
    this.miPerfilCanVencim = miPerfilCanVencim;
  }

  @Column (name = "mi_perfil_rec_tit", nullable = false)
  public String getMiPerfilRecTitulo() {
    return miPerfilRecTitulo;
  }

  public void setMiPerfilRecTitulo(String miPerfilRecTitulo) {
    this.miPerfilRecTitulo = miPerfilRecTitulo;
  }

  @Column (name = "mi_perfil_rec_cor", nullable = false)
  public String getMiPerfilRecCorto() {
    return miPerfilRecCorto;
  }

  public void setMiPerfilRecCorto(String miPerfilRecCorto) {
    this.miPerfilRecCorto = miPerfilRecCorto;
  }

  @Column (name = "mi_perfil_rec_lar", nullable = false)
  public String getMiPerfilRecLargo() {
    return miPerfilRecLargo;
  }

  public void setMiPerfilRecLargo(String miPerfilRecLargo) {
    this.miPerfilRecLargo = miPerfilRecLargo;
  }

  @Column (name = "mi_perfil_rec_ven", nullable = false)
  public Integer getMiPerfilRecVencim() {
    return miPerfilRecVencim;
  }

  public void setMiPerfilRecVencim(Integer miPerfilRecVencim) {
    this.miPerfilRecVencim = miPerfilRecVencim;
  }

  @Column (name = "mi_perfil_rec_hora", nullable = false)
  public Integer getMiPerfilRecHora() {
    return miPerfilRecHora;
  }

  public void setMiPerfilRecHora(Integer miPerfilRecHora) {
    this.miPerfilRecHora = miPerfilRecHora;
  }

  @Column (name = "mi_perfil_rec_dias", nullable = false)
  public Integer getMiPerfilRecDias() {
    return miPerfilRecDias;
  }

  public void setMiPerfilRecDias(Integer miPerfilRecDias) {
    this.miPerfilRecDias = miPerfilRecDias;
  }
  
  @Column (name = "reserva_pen_tiempo_max", nullable = false)
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

    @Transient
    public AccionMiPerfil getAccionMiPerfil() {
		return accionMiPerfil;
	}
    
	public void setAccionMiPerfil(AccionMiPerfil accionMiPerfil) {
		this.accionMiPerfil = accionMiPerfil;
	}
	
	
	
	@Transient
	public boolean isSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Recurso other = (Recurso) obj;
	if (cantDiasAGenerar == null) {
		if (other.cantDiasAGenerar != null)
			return false;
	} else if (!cantDiasAGenerar.equals(other.cantDiasAGenerar))
		return false;
	if (departamento == null) {
		if (other.departamento != null)
			return false;
	} else if (!departamento.equals(other.departamento))
		return false;
	if (descripcion == null) {
		if (other.descripcion != null)
			return false;
	} else if (!descripcion.equals(other.descripcion))
		return false;
	if (direccion == null) {
		if (other.direccion != null)
			return false;
	} else if (!direccion.equals(other.direccion))
		return false;
	if (domingoEsHabil == null) {
		if (other.domingoEsHabil != null)
			return false;
	} else if (!domingoEsHabil.equals(other.domingoEsHabil))
		return false;
	if (fechaFin == null) {
		if (other.fechaFin != null)
			return false;
	} else if (!fechaFin.equals(other.fechaFin))
		return false;
	if (fechaFinDisp == null) {
		if (other.fechaFinDisp != null)
			return false;
	} else if (!fechaFinDisp.equals(other.fechaFinDisp))
		return false;
	if (fechaInicio == null) {
		if (other.fechaInicio != null)
			return false;
	} else if (!fechaInicio.equals(other.fechaInicio))
		return false;
	if (fechaInicioDisp == null) {
		if (other.fechaInicioDisp != null)
			return false;
	} else if (!fechaInicioDisp.equals(other.fechaInicioDisp))
		return false;
	if (horarios == null) {
		if (other.horarios != null)
			return false;
	} else if (!horarios.equals(other.horarios))
		return false;
	if (id == null) {
		if (other.id != null)
			return false;
	} else if (!id.equals(other.id))
		return false;
	if (latitud == null) {
		if (other.latitud != null)
			return false;
	} else if (!latitud.equals(other.latitud))
		return false;
	if (localidad == null) {
		if (other.localidad != null)
			return false;
	} else if (!localidad.equals(other.localidad))
		return false;
	if (longitud == null) {
		if (other.longitud != null)
			return false;
	} else if (!longitud.equals(other.longitud))
		return false;
	if (nombre == null) {
		if (other.nombre != null)
			return false;
	} else if (!nombre.equals(other.nombre))
		return false;
	if (periodoValidacion == null) {
		if (other.periodoValidacion != null)
			return false;
	} else if (!periodoValidacion.equals(other.periodoValidacion))
		return false;
	if (sabadoEsHabil == null) {
		if (other.sabadoEsHabil != null)
			return false;
	} else if (!sabadoEsHabil.equals(other.sabadoEsHabil))
		return false;
	if (telefonos == null) {
		if (other.telefonos != null)
			return false;
	} else if (!telefonos.equals(other.telefonos))
		return false;
	if (visibleInternet == null) {
		if (other.visibleInternet != null)
			return false;
	} else if (!visibleInternet.equals(other.visibleInternet))
		return false;
	if (reservaPendienteTiempoMax == null) {
		if (other.reservaPendienteTiempoMax != null)
			return false;
	} else if (!reservaPendienteTiempoMax.equals(other.reservaPendienteTiempoMax))
		return false;
	if (reservaMultiplePendienteTiempoMax == null) {
		if (other.reservaMultiplePendienteTiempoMax != null)
			return false;
	} else if (!reservaMultiplePendienteTiempoMax.equals(other.reservaMultiplePendienteTiempoMax))
		return false;
	if (diasInicioVentanaIntranet == null) {
		if (other.diasInicioVentanaIntranet != null)
			return false;
	} else if (!diasInicioVentanaIntranet.equals(other.diasInicioVentanaIntranet))
		return false;
	if (diasVentanaIntranet == null) {
		if (other.diasVentanaIntranet != null)
			return false;
	} else if (!diasVentanaIntranet.equals(other.diasVentanaIntranet))
		return false;
	if (diasInicioVentanaInternet == null) {
		if (other.diasInicioVentanaInternet != null)
			return false;
	} else if (!diasInicioVentanaInternet.equals(other.diasInicioVentanaInternet))
		return false;
	if (diasVentanaInternet == null) {
		if (other.diasVentanaInternet != null)
			return false;
	} else if (!diasVentanaInternet.equals(other.diasVentanaInternet))
		return false;
	return true;
}
  
  
}
