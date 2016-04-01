package uy.gub.imm.sae.entity.global;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name = "ae_trazabilidad")
public class Trazabilidad implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String transaccionId;
	private Date fechaCreacion;
	private Date fechaUltIntento;
	private Integer intentos;
	private String datos;
	private Boolean enviado;
	private Boolean esCabezal;

	private Empresa empresa;
	private Integer reservaId;
	
	public Trazabilidad() {
		super();
	}
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_trazabilidad")
	@SequenceGenerator (name ="seq_trazabilidad", initialValue = 1, sequenceName = "s_ae_trazabilidad",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="transaccion_id")
	public String getTransaccionId() {
		return transaccionId;
	}
	public void setTransaccionId(String transaccionId) {
		this.transaccionId = transaccionId;
	}
	@Column(name="fecha_creacion")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	@Column(name="fecha_ult_intento")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaUltIntento() {
		return fechaUltIntento;
	}
	public void setFechaUltIntento(Date fechaUltIntento) {
		this.fechaUltIntento = fechaUltIntento;
	}
	@Column(name="intentos")
	public Integer getIntentos() {
		return intentos;
	}
	public void setIntentos(Integer intentos) {
		this.intentos = intentos;
	}
	@Column(name="datos")
		public String getDatos() {
		return datos;
	}
	public void setDatos(String datos) {
		this.datos = datos;
	}
	@Column(name="enviado")
	public Boolean getEnviado() {
		return enviado;
	}
	public void setEnviado(Boolean enviado) {
		this.enviado = enviado;
	}
	@Column(name="es_cabezal")
	public Boolean getEsCabezal() {
		return esCabezal;
	}
	public void setEsCabezal(Boolean esCabezal) {
		this.esCabezal = esCabezal;
	}
	@ManyToOne
	@JoinColumn (name = "empresa_id")
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	@Column(name="reserva_id")
	public Integer getReservaId() {
		return reservaId;
	}
	public void setReservaId(Integer reservaId) {
		this.reservaId = reservaId;
	}
	
	
}
