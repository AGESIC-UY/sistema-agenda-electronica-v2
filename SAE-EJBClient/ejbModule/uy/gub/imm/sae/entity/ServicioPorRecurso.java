package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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


@Entity
@Table (name = "ae_servicio_por_recurso")
public class ServicioPorRecurso implements Serializable {

	private static final long serialVersionUID = 2807856902600523708L;

	private Integer id;	
	private Date fechaBaja;

	private ServicioAutocompletar autocompletado;
	private Recurso recurso;
	private List<ServicioAutocompletarPorDato> autocompletadosPorDato;
	
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_servporrec")
	@SequenceGenerator (name ="seq_servporrec", initialValue = 1, sequenceName = "s_ae_servrecurso",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	@JoinColumn (name = "aeserv_id", nullable = false)
	public ServicioAutocompletar getAutocompletado() {
		return autocompletado;
	}
	public void setAutocompletado(ServicioAutocompletar autocompletado) {
		this.autocompletado = autocompletado;
	}
	
	@ManyToOne (optional = false)
	@JoinColumn (name = "aere_id", nullable = false)
	public Recurso getRecurso() {
		return recurso;
	}
	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}
	
	@OneToMany(mappedBy = "servicioPorRecurso")
	public List<ServicioAutocompletarPorDato> getAutocompletadosPorDato() {
		return autocompletadosPorDato;
	}
	public void setAutocompletadosPorDato(
			List<ServicioAutocompletarPorDato> autocompletadosPorDato) {
		this.autocompletadosPorDato = autocompletadosPorDato;
	}
	
	
	
}
