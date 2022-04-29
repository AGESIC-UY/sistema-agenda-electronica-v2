package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable 
public class TextoGlobalId implements Serializable {
	private static final long serialVersionUID = -9199361111203039359L;
	private String codigo;
	private String idioma;
	
	public TextoGlobalId() {
		codigo = null;
		idioma = null;
	}

	public TextoGlobalId(String codigo, String idioma) {
		this.codigo = codigo;
		this.idioma = idioma;
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TextoGlobalId that = (TextoGlobalId) o;
		return Objects.equals(codigo, that.codigo) && Objects.equals(idioma, that.idioma);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo, idioma);
	}
}
