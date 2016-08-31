package uy.gub.imm.sae.exportar;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RecursoExportar {

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
	private Boolean mostrarNumeroEnLlamador;
	private Boolean visibleInternet;
	private Boolean mostrarNumeroEnTicket;
	private Boolean mostrarIdEnTicket;
	private Boolean usarLlamador;
	private String serie;
	private Boolean sabadoEsHabil;

	// Datos obtenidos a partir de TramitesUy
	private String oficinaId; // Esto es inventado, las oficinas no tienen id en
								// TramitesUy
	private String direccion;
	private String localidad;
	private String departamento;
	private String telefonos;
	private String horarios;

	private BigDecimal latitud;
	private BigDecimal longitud;

	@XmlElement(name = "agrupaciones")
	private List<AgrupacionDatoExport> agrupaciones = new ArrayList<AgrupacionDatoExport>();

	private Map<String, TextoRecursoExportar> textosRecurso = new HashMap<String, TextoRecursoExportar>();

	public RecursoExportar() {
		super();
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

	public Integer getLargoListaEspera() {
		return largoListaEspera;
	}

	public void setLargoListaEspera(Integer largoListaEspera) {
		this.largoListaEspera = largoListaEspera;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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

	public Boolean getVisibleInternet() {
		return visibleInternet;
	}

	public void setVisibleInternet(Boolean visibleInternet) {
		this.visibleInternet = visibleInternet;
	}

	public Boolean getMostrarNumeroEnTicket() {
		return mostrarNumeroEnTicket;
	}

	public void setMostrarNumeroEnTicket(Boolean mostrarNumeroEnTicket) {
		this.mostrarNumeroEnTicket = mostrarNumeroEnTicket;
	}

	public Boolean getUsarLlamador() {
		return usarLlamador;
	}

	public void setUsarLlamador(Boolean usarLlamador) {
		this.usarLlamador = usarLlamador;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public Boolean getSabadoEsHabil() {
		return sabadoEsHabil;
	}

	public void setSabadoEsHabil(Boolean sabadoEsHabil) {
		this.sabadoEsHabil = sabadoEsHabil;
	}

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

	public List<AgrupacionDatoExport> getAgrupaciones() {
		return agrupaciones;
	}

	public void setAgrupaciones(List<AgrupacionDatoExport> agrupaciones) {
		this.agrupaciones = agrupaciones;
	}

	public Map<String, TextoRecursoExportar> getTextosRecurso() {
		return textosRecurso;
	}

	public void setTextosRecurso(Map<String, TextoRecursoExportar> textosRecurso) {
		this.textosRecurso = textosRecurso;
	}

	public Boolean getMostrarIdEnTicket() {
		return mostrarIdEnTicket;
	}

	public void setMostrarIdEnTicket(Boolean mostrarIdEnTicket) {
		this.mostrarIdEnTicket = mostrarIdEnTicket;
	}

}
