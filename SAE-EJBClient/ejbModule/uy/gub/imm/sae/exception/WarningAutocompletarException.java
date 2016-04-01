package uy.gub.imm.sae.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class WarningAutocompletarException extends ValidacionException {


	private static final long serialVersionUID = -8647088213281009039L;
	private List<String> codigosErrorMensajes;
	private String nombreAutocompletado;
	
	/**
	 * Las listas deben tener un tamaño > 0.
	 * @param codigoError
	 * @param nombreCampos es el nombre de los campos para los que hay algun mensaje de error en la validacion
	 * @param mensajes     son los mensajes de error de la validacion
	 */
	public WarningAutocompletarException(String codigoError, List<String> nombreCampos, List<String> mensajes) {
		super(codigoError, nombreCampos, mensajes);
		
		if (nombreCampos == null ||
			mensajes ==  null ||
			nombreCampos.size() == 0 ||
			mensajes.size() == 0) {
			throw new RuntimeException("La lista de mensajes y nombre de campos deben tener a lo menos un elemento");
		}
	}

	public WarningAutocompletarException(String codigoError, List<String> nombreCampos, List<String> mensajes,  List<String> codigosErrorMensajes, String nombreAutocompletado) {
		super(codigoError, nombreCampos, mensajes);
		
		this.codigosErrorMensajes = codigosErrorMensajes;
		if(this.codigosErrorMensajes == null){
			this.codigosErrorMensajes = new ArrayList<String>();
		}
		this.nombreAutocompletado = nombreAutocompletado;
		
		if (nombreCampos == null ||
			mensajes ==  null ||
			nombreCampos.size() == 0 ||
			mensajes.size() == 0) {
			throw new RuntimeException("La lista de mensajes y nombre de campos deben tener a lo menos un elemento");
		}
	}

	public List<String> getCodigosErrorMensajes() {
		return codigosErrorMensajes;
	}

	public void setCodigosErrorMensajes(List<String> codigosErrorMensajes) {
		this.codigosErrorMensajes = codigosErrorMensajes;
	}
	
	public String getNombreAutocompletado() {
		return nombreAutocompletado;
	}

	public void setNombreAutocompletado(String nombreAutocompletado) {
		this.nombreAutocompletado = nombreAutocompletado;
	}

}

