package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uy.gub.imm.sae.common.enumerados.Evento;

@Entity
@Table (name = "ae_acciones_por_recurso")
public class AccionPorRecurso implements Serializable{
	private static final long serialVersionUID = 1L;
  private Integer id;
	private Integer ordenEjecucion;
	private Date fechaBaja;

	private Accion accion;
	private Recurso recurso;
	private Evento evento;
	private List<AccionPorDato> accionesPorDato;

	public AccionPorRecurso() {
	}

	AccionPorRecurso(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_accionporrec")
	@SequenceGenerator (name ="seq_accionporrec", initialValue = 1, sequenceName = "s_ae_accionrecurso",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column (name = "orden_ejecucion")	
	public Integer getOrdenEjecucion() {
		return ordenEjecucion;
	}
	public void setOrdenEjecucion(Integer ordenEjecucion) {
		this.ordenEjecucion = ordenEjecucion;
	}

	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aeac_id", nullable = false)
	public Accion getAccion() {
		return accion;
	}
	public void setAccion(Accion accion) {
		this.accion = accion;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aere_id", nullable = false)
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}
	
	@OneToMany(mappedBy = "accionPorRecurso")
	public List<AccionPorDato> getAccionesPorDato() {
		return accionesPorDato;
	}
	public void setAccionesPorDato(List<AccionPorDato> accionesPorDato) {
		this.accionesPorDato = accionesPorDato;
	}
	
	@Column (nullable = false, length=1)
	@Enumerated (EnumType.STRING)
	public Evento getEvento() {
		return evento;
	}
	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AccionPorRecurso that = (AccionPorRecurso) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
