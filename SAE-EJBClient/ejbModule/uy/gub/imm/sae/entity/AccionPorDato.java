package uy.gub.imm.sae.entity;

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
@Table (name = "ae_acciones_por_dato")
public class AccionPorDato implements Serializable{

	private static final long serialVersionUID = -620846748056072002L;
	
	private Integer id;
	private String nombreParametro;
	private Date fechaDesasociacion;
	
	private DatoASolicitar datoASolicitar;
	private AccionPorRecurso accionPorRecurso;
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_accionPorDato")
	@SequenceGenerator (name ="seq_accionPorDato", initialValue = 1, sequenceName = "s_ae_acciondato",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column (name = "fecha_desasociacion")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaDesasociacion() {
		return fechaDesasociacion;
	}
	public void setFechaDesasociacion(Date fechaDesasociacion) {
		this.fechaDesasociacion = fechaDesasociacion;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aeds_id", nullable = false)
	public DatoASolicitar getDatoASolicitar() {
		return datoASolicitar;
	}
	public void setDatoASolicitar(DatoASolicitar datoASolicitar) {
		this.datoASolicitar = datoASolicitar;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "aear_id", nullable = false)
	public AccionPorRecurso getAccionPorRecurso() {
		return accionPorRecurso;
	}
	public void setAccionPorRecurso(AccionPorRecurso accionPorRecurso) {
		this.accionPorRecurso = accionPorRecurso;
	}
	
	@Column (name = "nombre_parametro", nullable=false, length=50)
	public String getNombreParametro() {
		return nombreParametro;
	}
	public void setNombreParametro(String nombreParametro) {
		this.nombreParametro = nombreParametro;
	}

}
