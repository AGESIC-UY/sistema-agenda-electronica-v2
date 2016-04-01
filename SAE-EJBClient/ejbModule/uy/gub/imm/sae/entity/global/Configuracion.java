package uy.gub.imm.sae.entity.global;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ae_configuracion")
public class Configuracion implements Serializable {

	private static final long serialVersionUID = -5219038312184384285L;

	private String clave;
	private String valor;

	@Id
	@Column(nullable = false, length = 100)
	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	@Column(nullable = false, length = 4096)
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Configuracion) {
			Configuracion tGlobal = (Configuracion) obj;
			if (tGlobal.getClave().equals(this.getClave())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
