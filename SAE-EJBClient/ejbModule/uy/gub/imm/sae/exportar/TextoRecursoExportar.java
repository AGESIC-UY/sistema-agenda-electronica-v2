package uy.gub.imm.sae.exportar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "textoRecurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextoRecursoExportar {
	
	private String idioma;
	private String textoPaso2;
	private String textoPaso3;
    
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public String getTextoPaso2() {
		return textoPaso2;
	}
	public void setTextoPaso2(String textoPaso2) {
		this.textoPaso2 = textoPaso2;
	}
	
	public String getTextoPaso3() {
		return textoPaso3;
	}

	public void setTextoPaso3(String textoPaso3) {
		this.textoPaso3 = textoPaso3;
	}

    
    
    
}
