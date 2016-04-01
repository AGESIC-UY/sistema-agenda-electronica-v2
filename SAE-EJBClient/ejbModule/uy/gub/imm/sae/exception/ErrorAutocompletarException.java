package uy.gub.imm.sae.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ErrorAutocompletarException  extends AutocompletarException{


	private static final long serialVersionUID = -1435404652467824082L;
	private List<String> codigosErrorMensajes;
	private String nombreAutocompletado;

	/**
	 * Las listas deben tener un tama�o > 0.
	 * @param codigoError
	 * @param nombreCampos es el nombre de los campos para los que hay algun mensaje de error en el servicio de autocompletado
	 * @param mensajes     son los mensajes de error del servicio de autocompletado
	 */
	public ErrorAutocompletarException(String codigoError, List<String> nombreCampos, List<String> mensajes) {
		super(codigoError,null, nombreCampos, mensajes);
		
		if (nombreCampos == null ||
			mensajes ==  null ||
			nombreCampos.size() == 0 ||
			mensajes.size() == 0) {
			throw new RuntimeException("La lista de mensajes y nombre de campos deben tener a lo menos un elemento");
		}
	}

	public ErrorAutocompletarException(String codigoError, List<String> nombreCampos, List<String> mensajes,  List<String> codigosErrorMensajes, String nombreAutocompletado) {
		super(codigoError,null, nombreCampos, mensajes);
		
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
