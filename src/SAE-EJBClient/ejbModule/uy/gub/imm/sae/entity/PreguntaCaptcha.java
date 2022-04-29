package uy.gub.imm.sae.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ae_preguntas_captcha")
public class PreguntaCaptcha implements Serializable {

  private static final long serialVersionUID = 1L;

	private String clave;
	private String idioma;
	private String pregunta;
	private String respuesta;

	public PreguntaCaptcha() {
	}

	public PreguntaCaptcha(String clave) {
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
  
	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PreguntaCaptcha that = (PreguntaCaptcha) o;
		return Objects.equals(clave, that.clave);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clave);
	}
}
