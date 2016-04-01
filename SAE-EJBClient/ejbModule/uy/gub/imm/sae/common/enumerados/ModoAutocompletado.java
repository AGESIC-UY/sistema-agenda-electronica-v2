package uy.gub.imm.sae.common.enumerados;

public enum ModoAutocompletado {
	ENTRADA("Entrada"), SALIDA("Salida");

	private final String descripcion;

	private ModoAutocompletado(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}

}
