package uy.gub.imm.sae.entity.global;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
@Table (name = "ae_novedades")
public class Novedad implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Date fechaCreacion;
	private Date fechaUltIntento;
	private Integer intentos;
	private String datos;
	private Boolean enviado;

	private Empresa empresa;
	private Integer reservaId;
	
	public Novedad() {
		super();
	}

	Novedad(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_novedades")
	@SequenceGenerator (name ="seq_novedades", initialValue = 1, sequenceName = "s_ae_novedades",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Novedad novedad = (Novedad) o;
		return Objects.equals(id, novedad.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
