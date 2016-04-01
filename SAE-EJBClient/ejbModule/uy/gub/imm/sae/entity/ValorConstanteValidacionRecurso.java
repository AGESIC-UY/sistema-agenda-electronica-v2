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
@Table (name = "AE_VALOR_CONSTANTE_VAL_REC")
public class ValorConstanteValidacionRecurso implements Serializable{

	private static final long serialVersionUID = -4436680475226016793L;

	private Integer id;
	private String nombreConstante;
	private Date fechaDesasociacion;
	
	private String valor;
	private ValidacionPorRecurso validacionPorRecurso;
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_valorConstante")
	@SequenceGenerator (name ="seq_valorConstante", initialValue = 1, sequenceName = "S_AE_VALCONSTANTE",allocationSize=1)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column (name = "FECHA_DESASOCIACION")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaDesasociacion() {
		return fechaDesasociacion;
	}
	public void setFechaDesasociacion(Date fechaDesasociacion) {
		this.fechaDesasociacion = fechaDesasociacion;
	}

	@ManyToOne (optional = false)
	@JoinColumn (name = "AEVR_ID", nullable = false)
	public ValidacionPorRecurso getValidacionPorRecurso() {
		return validacionPorRecurso;
	}
	public void setValidacionPorRecurso(ValidacionPorRecurso validacionPorRecurso) {
		this.validacionPorRecurso = validacionPorRecurso;
	}
	
	@Column (name = "NOMBRE_CONSTANTE", nullable=false, length=50)
	public String getNombreConstante() {
		return nombreConstante;
	}
	public void setNombreConstante(String nombreConstante) {
		this.nombreConstante = nombreConstante;
	}
	
	@Column (name="VALOR", nullable=false, length=100)
	public String getValor(){
		return valor;
	}
	
	public void setValor (String valor){
		this.valor=valor;
	}

}
