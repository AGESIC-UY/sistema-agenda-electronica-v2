package uy.gub.imm.sae.entity;

import java.io.Serializable;

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
	
}
