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
@Table (name = "ae_serv_autocompletar")
public class ServicioAutocompletar implements Serializable {

	private static final long serialVersionUID = 5612066412225148822L;
	
	private Integer id;
	private String nombre;
	private String host;
	private String descripcion;
	private String servicio;
	private Date fechaBaja;
	
	private List<ParametrosAutocompletar> parametrosAutocompletados;
	
	
	public ServicioAutocompletar () {
		parametrosAutocompletados = new ArrayList<ParametrosAutocompletar>();
	}
	
	public ServicioAutocompletar (ServicioAutocompletar s) {
		id = s.getId();
		nombre = s.getNombre();
		descripcion = s.getDescripcion();
		servicio = s.getServicio();
		host = s.getHost();
		fechaBaja = s.getFechaBaja();
		
		parametrosAutocompletados = new ArrayList<ParametrosAutocompletar>();
	}
	
	
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE, generator="seq_serv_autocompletar")
	@SequenceGenerator (name ="seq_serv_autocompletar", initialValue = 1, sequenceName = "s_ae_serv_autocompletar",allocationSize=1)
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

	public void setParametrosAutocompletados(
			List<ParametrosAutocompletar> parametrosAutocompletados) {
		this.parametrosAutocompletados = parametrosAutocompletados;
	}

	@OneToMany(mappedBy = "servicio")
	public List<ParametrosAutocompletar> getParametrosAutocompletados() {
		return parametrosAutocompletados;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof ServicioAutocompletar) {
			ServicioAutocompletar val = (ServicioAutocompletar) obj;
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
