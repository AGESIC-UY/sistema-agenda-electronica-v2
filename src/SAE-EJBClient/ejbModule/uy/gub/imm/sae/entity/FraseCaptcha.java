package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ae_frases_captcha")
public class FraseCaptcha implements Serializable {

  private static final long serialVersionUID = 1L;

	private String clave;
	private String frase;
	private String idioma;

	public FraseCaptcha() {
	}

	FraseCaptcha(String clave) {
		this.clave = clave;
	}

	@Id
	@Column(nullable = false, length = 100)
	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}
  
	@Column(nullable = false, length = 4096)
	public String getFrase() {
		return frase;
	}

	public void setFrase(String frase) {
		this.frase = frase;
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
		FraseCaptcha that = (FraseCaptcha) o;
		return Objects.equals(clave, that.clave);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clave);
	}
}
