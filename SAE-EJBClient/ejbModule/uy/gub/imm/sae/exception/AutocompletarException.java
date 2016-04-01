package uy.gub.imm.sae.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class AutocompletarException extends UserException {

	private static final long serialVersionUID = 3617276942333856550L;

	private List<String> nombreCampos;
	private List<String> mensajes;

	
	public AutocompletarException (String codigoError, String mensaje, List<String> nombreCampos, List<String> mensajes) {
		super(codigoError, mensaje);
	
		if (nombreCampos == null ||	nombreCampos.size() == 0) {
			throw new RuntimeException("Una AutocompletarException debe tener al menos un nombre de campo.");
		}
		
		this.nombreCampos = nombreCampos;
		this.mensajes = mensajes;
		if (this.mensajes == null) {
			this.mensajes = new ArrayList<String>();
		}
	}

	public int getCantCampos() {
		return nombreCampos.size();
	}

	public int getCantMensajes() {
		return mensajes.size();
	}

	public String getNombreCampo(int index) {
		return this.nombreCampos.get(index);
	}

	public String getMensaje(int index) {
		if (this.mensajes != null) {
			return this.mensajes.get(index);
		}
		else {
			return null;
		}
	}
	
	public List<String> getNombresCampos(){
		return nombreCampos;
	}
	
	public List<String> getMensajes() {
		return mensajes;
	}

	public boolean isMensaje (){
		if (this.mensajes != null && this.mensajes.size() > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isNombreCampo(){
		if (this.nombreCampos != null && this.nombreCampos.size() > 0) {
			return true;
		}
		return false;
	}
}
