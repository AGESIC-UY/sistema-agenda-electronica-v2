package uy.gub.imm.sae.entity.global;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ae_oficinas")
public class Oficina implements Serializable {

	private static final long serialVersionUID = 7173791316892020470L;
	
	private String id;
	private Tramite tramite;
	private String nombre;
	private String direccion;
	private String localidad;
	private String departamento;
	private String telefonos;
	private String horarios;
	private String comentarios;

	public Oficina() {
	}

	Oficina(String id) {
		this.id = id;
	}

	//No es autogenerada, la entidad se obtiene de un servicio web y ya viene con id
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(name="tramite_id")
	public Tramite getTramite() {
		return tramite;
	}

	public void setTramite(Tramite tramite) {
		this.tramite = tramite;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getTelefonos() {
		return telefonos;
	}

	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}

	public String getHorarios() {
		return horarios;
	}

	public void setHorarios(String horarios) {
		this.horarios = horarios;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Oficina oficina = (Oficina) obj;
		return Objects.equals(id, oficina.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
