package uy.gub.imm.sae.entity.global;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@Entity
@Table (name = "ae_empresas")
public class Empresa implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String nombre;
	private String datasource;
	private Date   fechaBaja;
	
	//Datos del organismo
	private Integer organismoId; //Si no es null significa que los datos se obtuvieron del servicio web
	private String organismoCodigo;
	private String organismoNombre;
	
	//Datos de la unidad ejecutora
	private Integer unidadEjecutoraId; //Si no es null significa que los datos se obtuvieron del servicio web
	private String unidadEjecutoraCodigo;
	private String unidadEjecutoraNombre;
	
	//OID del Organismo según el PEU
	//Para esto hay que buscar el organismo en http://unaoid.gub.uy/Organizacion.aspx
	private String oid;
	
	//Datos para el formulario de consentimiento
	private String ccFinalidad;
	private String ccResponsable;
	private String ccDireccion;
	
	@Basic(fetch = FetchType.LAZY)
  @Lob
  private byte[] logo;
	private String logoTexto;
	
	private String timezone;
	private String formatoFecha;
	private String formatoHora;
	
	private String piePublico;

	public Empresa() {
	}

	Empresa(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_empresa")
	@SequenceGenerator (name ="seq_empresa", initialValue = 1, sequenceName = "s_ae_empresa",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column (nullable=false, length=100)
	public String getNombre() {
		return nombre;
	}
	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fin) {
		fechaBaja = fin;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	@Column (nullable=false, length=25)
	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	
	@Column (name = "org_id")
	public Integer getOrganismoId() {
		return organismoId;
	}
	public void setOrganismoId(Integer organismoId) {
		this.organismoId = organismoId;
	}
	@Column (name = "org_codigo")
	public String getOrganismoCodigo() {
		return organismoCodigo;
	}
	public void setOrganismoCodigo(String organismoCodigo) {
		this.organismoCodigo = organismoCodigo;
	}
	@Column (name = "org_nombre")
	public String getOrganismoNombre() {
		return organismoNombre;
	}
	public void setOrganismoNombre(String organismoNombre) {
		this.organismoNombre = organismoNombre;
	}
	
	@Column (name = "unej_id")
	public Integer getUnidadEjecutoraId() {
		return unidadEjecutoraId;
	}
	public void setUnidadEjecutoraId(Integer unidadEjecutoraId) {
		this.unidadEjecutoraId = unidadEjecutoraId;
	}
	@Column (name = "unej_codigo")
	public String getUnidadEjecutoraCodigo() {
		return unidadEjecutoraCodigo;
	}
	public void setUnidadEjecutoraCodigo(String unidadEjecutoraCodigo) {
		this.unidadEjecutoraCodigo = unidadEjecutoraCodigo;
	}
	@Column (name = "unej_nombre")
	public String getUnidadEjecutoraNombre() {
		return unidadEjecutoraNombre;
	}
	public void setUnidadEjecutoraNombre(String unidadEjecutoraNombre) {
		this.unidadEjecutoraNombre = unidadEjecutoraNombre;
	}
	public byte[] getLogo() {
		return logo;
	}
	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	@Column (name = "logo_texto")
	public String getLogoTexto() {
		return logoTexto;
	}
	public void setLogoTexto(String logoTexto) {
		this.logoTexto = logoTexto;
	}
	@Column (name = "oid")
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	@Column (name = "cc_finalidad")
	public String getCcFinalidad() {
		return ccFinalidad;
	}
	public void setCcFinalidad(String ccFinalidad) {
		this.ccFinalidad = ccFinalidad;
	}
	@Column (name = "cc_responsable")
	public String getCcResponsable() {
		return ccResponsable;
	}
	public void setCcResponsable(String ccResponsable) {
		this.ccResponsable = ccResponsable;
	}
	@Column (name = "cc_direccion")
	public String getCcDireccion() {
		return ccDireccion;
	}
	public void setCcDireccion(String ccDireccion) {
		this.ccDireccion = ccDireccion;
	}
	
	public String getTimezone() {
		if(timezone == null) {
			timezone = TimeZone.getDefault().getID();
		}
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	@Column (name = "formato_fecha")
	public String getFormatoFecha() {
		return formatoFecha;
	}
	public void setFormatoFecha(String formatoFecha) {
		this.formatoFecha = formatoFecha;
	}
	@Column (name = "formato_hora")
	public String getFormatoHora() {
		return formatoHora;
	}
	public void setFormatoHora(String formatoHora) {
		this.formatoHora = formatoHora;
	}
	@Column (name = "pie_publico")
	public String getPiePublico() {
		return piePublico;
	}
	public void setPiePublico(String piePublico) {
		this.piePublico = piePublico;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Empresa empresa = (Empresa) obj;
		return Objects.equals(id, empresa.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
