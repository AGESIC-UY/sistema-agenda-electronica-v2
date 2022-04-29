package uy.gub.imm.sae.entity;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uy.gub.imm.sae.common.enumerados.Tipo;

@Entity
@Table (name = "AE_CONSTANTE_VALIDACION")
public class ConstanteValidacion implements Serializable{

	private static final long serialVersionUID = 2179139446137901656L;
	
	private Integer id;
	private String nombre;
	private Tipo tipo;	
	private Integer largo;
	private Date fechaBaja;
	
	private Validacion validacion;

	public ConstanteValidacion () {}
	
	public ConstanteValidacion (ConstanteValidacion c) {
		id = c.getId();
		nombre = c.getNombre();
		tipo = c.getTipo();
		largo = c.getLargo();
		fechaBaja = c.getFechaBaja();
	}

	ConstanteValidacion(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_constVal")
	@SequenceGenerator (name ="seq_constVal", initialValue = 1, sequenceName = "S_AE_CONSTVAL",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (nullable = false, length=50)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column (nullable = false, length=30)
	@Enumerated(EnumType.STRING)
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}
	
	@Column (nullable = false)
	public Integer getLargo() {
		return largo;
	}
	public void setLargo(Integer largo) {
		this.largo = largo;
	}

	@Column (name = "FECHA_BAJA")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "AEVA_ID", nullable = false)
	public Validacion getValidacion() {
		return validacion;
	}
	public void setValidacion(Validacion validacion) {
		this.validacion = validacion;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConstanteValidacion that = (ConstanteValidacion) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
