package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Objects;

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

	TextoTenant(TextoGlobalId codigo) {
		this.codigo = codigo;
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TextoTenant that = (TextoTenant) o;
		return Objects.equals(codigo, that.codigo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}
}
