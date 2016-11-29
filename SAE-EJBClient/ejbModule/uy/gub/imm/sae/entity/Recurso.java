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
	private Boolean mostrarNumeroEnTicket;
	private Boolean mostrarIdEnTicket;
	private Boolean usarLlamador;
	private String serie;
	private Boolean sabadoEsHabil;
  private Boolean domingoEsHabil;
	
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
	}
	
	/**
	 * Constructor por copia no en profundidad.
	 */
	public Recurso (Recurso r) {
		plantillas = new ArrayList<Plantilla>();
		agrupacionDatos = new ArrayList<AgrupacionDato>();
		disponibilidades = new ArrayList<Disponibilidad>();
		datoDelRecurso = new ArrayList<DatoDelRecurso>();
		datosASolicitar = new ArrayList<DatoASolicitar>();
		validacionesPorRecurso = new ArrayList<ValidacionPorRecurso>();	
		accionesPorRecurso = new ArrayList<AccionPorRecurso>();
		
		id = r.getId();
		nombre = r.getNombre();
		descripcion = r.getDescripcion();
		fechaInicio = r.getFechaInicio();
		fechaFin = r.getFechaFin();
		fechaInicioDisp = r.getFechaInicioDisp();
		fechaFinDisp = r.getFechaFinDisp();
		diasInicioVentanaIntranet = r.getDiasInicioVentanaIntranet();
		diasVentanaIntranet = r.getDiasVentanaIntranet();
		diasInicioVentanaInternet = r.getDiasInicioVentanaInternet();
		diasVentanaInternet = r.getDiasVentanaInternet();
		ventanaCuposMinimos = r.getVentanaCuposMinimos();
		cantDiasAGenerar = r.getCantDiasAGenerar();
		largoListaEspera = r.getLargoListaEspera();
		fechaBaja = r.getFechaBaja();
		mostrarNumeroEnLlamador = r.getMostrarNumeroEnLlamador();
		mostrarNumeroEnTicket = r.getMostrarNumeroEnTicket();
		mostrarIdEnTicket = r.getMostrarIdEnTicket();
		usarLlamador = r.getUsarLlamador();
		serie = r.getSerie();
		visibleInternet = r.getVisibleInternet();
		sabadoEsHabil = r.getSabadoEsHabil();
		domingoEsHabil = r.getDomingoEsHabil();
		
		agenda = new Agenda(r.getAgenda());
		
		
		this.oficinaId = r.oficinaId;
		this.direccion = r.direccion;
		this.localidad = r.localidad;
		this.departamento = r.departamento;
		this.telefonos = r.telefonos;
		this.horarios = r.horarios;
		
		this.latitud = r.latitud;
		this.longitud = r.longitud;
		
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
	@Column (name="reserva_multiple", nullable = false)
	public Boolean getReservaMultiple() {
		//Se eliminó el atributo reservaMultiple;
		return false;
	}
	public void setReservaMultiple(Boolean reservaMultiple) {
		//Nada para hacer, se eliminó el atributo reservaMultiple
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

	public void setAccionesPorRecurso(
			List<AccionPorRecurso> accionesPorRecurso) {
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
	
	public void setAutocompletadosPorRecurso(
			List<ServicioPorRecurso> autocompletadosPorRecurso) {
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

}
