package uy.gub.imm.sae.entity.global;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ae_unidadesejecutoras")
public class UnidadEjecutora implements Serializable {

	private static final long serialVersionUID = 7173791316892020470L;
	
	private Integer id;
	private String codigo;
	private String nombre;

	//No es autogenerada, la entidad se obtiene de un servicio web y ya viene con id
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable = false, length = 25)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(nullable = false, length = 100)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof UnidadEjecutora) {
			UnidadEjecutora uejecutora = (UnidadEjecutora) obj;
			if (uejecutora.getId().equals(this.getId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
