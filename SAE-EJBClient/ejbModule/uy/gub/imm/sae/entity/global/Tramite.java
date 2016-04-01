package uy.gub.imm.sae.entity.global;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ae_tramites")
public class Tramite implements Serializable {

	private static final long serialVersionUID = 7173791316892020470L;
	
	private String id;
	private Integer empresaId;
	private String nombre;
	private String quees;
	private String temas;
	private boolean online;
	private List<Oficina> oficinas;

	//No es autogenerada, la entidad se obtiene de un servicio web y ya viene con id
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name="empresa_id")
	public Integer getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Integer empresaId) {
		this.empresaId = empresaId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getQuees() {
		return quees;
	}

	public void setQuees(String quees) {
		this.quees = quees;
	}

	public String getTemas() {
		return temas;
	}

	public void setTemas(String temas) {
		this.temas = temas;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@OneToMany(mappedBy="tramite")
	public List<Oficina> getOficinas() {
		return oficinas;
	}

	public void setOficinas(List<Oficina> oficinas) {
		this.oficinas = oficinas;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Tramite) {
			Tramite tramite = (Tramite) obj;
			if (tramite.getId().equals(this.getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


}
