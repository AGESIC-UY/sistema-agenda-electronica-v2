package uy.gub.imm.sae.entity;

import java.io.Serializable;

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

}
