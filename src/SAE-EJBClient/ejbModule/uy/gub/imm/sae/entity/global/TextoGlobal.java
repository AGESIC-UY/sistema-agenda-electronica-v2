package uy.gub.imm.sae.entity.global;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ae_textos")
public class TextoGlobal implements Serializable {

	private static final long serialVersionUID = 7173791316892020470L;
	
	private String codigo;
	private String texto;

	@Id
	@Column(nullable = false, length = 100)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(nullable = false, length = 4096)
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public TextoGlobal() {
	}

	TextoGlobal(String codigo) {
		this.codigo = codigo;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		TextoGlobal textoGlobal = (TextoGlobal) obj;
		return Objects.equals(codigo, textoGlobal.codigo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}
}
