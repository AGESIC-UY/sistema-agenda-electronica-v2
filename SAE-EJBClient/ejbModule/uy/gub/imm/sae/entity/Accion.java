package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name = "ae_acciones")
public class Accion implements Serializable{

	private static final long serialVersionUID = 7042969790748684636L;
	
	private Integer id;
	private String nombre;
	private String host;
	private String descripcion;
	private String servicio;
	private Date fechaBaja;
	
	
	private List<AccionPorRecurso> accionesPorRecurso;
	private List<ParametroAccion> parametrosAccion;

	
	public Accion () {
		accionesPorRecurso = new ArrayList<AccionPorRecurso>();
		parametrosAccion = new ArrayList<ParametroAccion>();
	}
	
	public Accion (Accion a) {
		id = a.getId();
		nombre = a.getNombre();
		descripcion = a.getDescripcion();
		servicio = a.getServicio();
		host = a.getHost();
		fechaBaja = a.getFechaBaja();
		
		accionesPorRecurso = new ArrayList<AccionPorRecurso>();
		parametrosAccion = new ArrayList<ParametroAccion>();
	}
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_accion")
	@SequenceGenerator (name ="seq_accion", initialValue = 1, sequenceName = "s_ae_accion",allocationSize=1)
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
	
	@Column (length=100)
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	@Column (nullable=false, length=100)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	@Column (nullable=false, length=250)
	public String getServicio() {
		return servicio;
	}
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}
	
	@Column (name = "fecha_baja")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	
	@OneToMany(mappedBy = "accion")
	public List<AccionPorRecurso> getAccionesPorRecurso() {
		return accionesPorRecurso;
	}
	public void setAccionesPorRecurso(List<AccionPorRecurso> accionesPorRecurso) {
		this.accionesPorRecurso = accionesPorRecurso;
	}

	@OneToMany(mappedBy = "accion")
	public List<ParametroAccion> getParametrosAccion() {
		return parametrosAccion;
	}
	public void setParametrosAccion(
			List<ParametroAccion> parametrosAccion) {
		this.parametrosAccion = parametrosAccion;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Accion) {
			Accion accion = (Accion) obj;
			if (accion.getId().equals(this.getId())) {
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