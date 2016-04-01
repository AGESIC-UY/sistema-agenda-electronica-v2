package uy.gub.sae.autocompletados.business.ejb;

import java.util.Map;

import uy.gub.sae.autocompletados.business.ejb.exception.InvalidParametersException;
import uy.gub.sae.autocompletados.business.ejb.exception.UnexpectedAutocompletadoException;

public interface AutocompletadorReserva {
	
	public ResultadoAutocompletado autocompletarDatosReserva(String nombreAutocompletado, Map<String, Object> params) throws UnexpectedAutocompletadoException, InvalidParametersException;

}
