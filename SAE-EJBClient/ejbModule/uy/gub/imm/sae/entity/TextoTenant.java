package uy.gub.imm.sae.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ae_textos")
public class TextoTenant implements Serializable {

	private static final long serialVersionUID = 7173791316892020470L;
	
	
	private TextoGlobalId codigo;
	private String texto;
	
	public TextoTenant() {
		
	}

	@Id
	@Column(nullable = false, length = 100)
	public TextoGlobalId getCodigo() {
		return codigo;
	}

	public void setCodigo(TextoGlobalId codigo) {
		this.codigo = codigo;
	}

	@Column(nullable = false, length = 4096)
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof TextoTenant) {
			TextoTenant tGlobal = (TextoTenant) obj;
			if (tGlobal.getCodigo().equals(this.getCodigo())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
