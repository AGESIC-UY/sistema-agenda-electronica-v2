package uy.gub.imm.sae.business.ejb.facade;

import java.util.Map;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.enumerados.Evento;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAccionException;

public interface AccionesHelperLocal {

	public void ejecutarAccionesPorEvento(Map<String, DatoReserva> valores, ReservaDTO reserva, Recurso recurso, Evento evento) throws ApplicationException, BusinessException, ErrorAccionException;
}
