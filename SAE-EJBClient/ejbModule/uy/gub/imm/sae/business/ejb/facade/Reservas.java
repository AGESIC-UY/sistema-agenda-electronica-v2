package uy.gub.imm.sae.business.ejb.facade;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.exception.AccesoMultipleException;

public interface Reservas {
	public void modificarEstadoReserva(ReservaDTO reserva) throws AccesoMultipleException;
	
}
