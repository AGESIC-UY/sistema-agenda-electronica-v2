package uy.gub.imm.sae.business.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultadoEjecucion implements Serializable {
	
    private int elementos = 0;
	private final List<String> mensajes = new ArrayList<>();
	private final List<String> warnings = new ArrayList<>();
	private final List<String> errores = new ArrayList<>();
	
	public ResultadoEjecucion() {
		super();
	}

	public int getElementos() {
        return elementos;
    }

    public void setElementos(int elementos) {
        this.elementos = elementos;
    }

    public List<String> getMensajes() {
		return mensajes;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public List<String> getErrores() {
		return errores;
	}

}
