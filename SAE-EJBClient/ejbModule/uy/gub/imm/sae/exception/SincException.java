package uy.gub.imm.sae.exception;

public class SincException extends Exception {

	private String codigoError;

	public SincException(String message) {
		super(message);
	}
	
	public SincException(String codigoError, String message) {
		super(message);
		this.codigoError = codigoError;
	}

	public SincException(String codigoError, String message, Throwable cause) {
		super(message, cause);
		this.codigoError = codigoError;
	}

	public String getCodigoError() {
		return codigoError;
	}
}
