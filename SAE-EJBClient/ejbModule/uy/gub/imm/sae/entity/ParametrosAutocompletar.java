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
import javax.xml.bind.annotation.XmlTransient;

import uy.gub.imm.sae.common.enumerados.ModoAutocompletado;

@Entity
@Table (name = "ae_parametros_autocompletar")
public class ParametrosAutocompletar implements Serializable {

	private static final long serialVersionUID = -2723637210745097368L;
	
	private Integer id;
	private String nombre;
	private String tipo;
	private ModoAutocompletado modo;
	private Date fecha_baja;
	
	private ServicioAutocompletar servicio;
	
	
	public ParametrosAutocompletar () {}
	
	public ParametrosAutocompletar (ParametrosAutocompletar p) {
		id = p.getId();
		nombre = p.getNombre();
		tipo = p.getTipo();
		modo = p.modo;
		fecha_baja = p.getFecha_baja();
	}
	

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_parametros_autocompletar")
	@SequenceGenerator (name ="seq_parametros_autocompletar", initialValue = 1, sequenceName = "s_ae_parametros_autocompletar",allocationSize=1)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column (nullable=false, length=50)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column (nullable=false, length=30)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column (nullable=false)
	public ModoAutocompletado getModo() {
		return modo;
	}

	public void setModo(ModoAutocompletado modo) {
		this.modo = modo;
	}

	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFecha_baja() {
		return fecha_baja;
	}

	public void setFecha_baja(Date fecha_baja) {
		this.fecha_baja = fecha_baja;
	}

	@XmlTransient
	@ManyToOne (optional = false)
	@JoinColumn (name = "aeserv_id")
	public ServicioAutocompletar getServicio() {
		return servicio;
	}

	public void setServicio(ServicioAutocompletar servicio) {
		this.servicio = servicio;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof ParametrosAutocompletar) {
			ParametrosAutocompletar val = (ParametrosAutocompletar) obj;
			if (val.getId().equals(this.getId())) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	

}
