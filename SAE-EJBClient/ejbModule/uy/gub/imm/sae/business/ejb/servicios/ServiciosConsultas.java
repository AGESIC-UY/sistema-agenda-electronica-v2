package uy.gub.imm.sae.business.ejb.servicios;

import java.util.List;
import java.util.Map;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.exception.BusinessException;

public interface ServiciosConsultas {
	// Agregada para la validación de la UEPS (umbral de re-agendas)
	public List<ReservaDTO> consultarReservasPorClave(Integer recId, Map<String, String> datosClave) throws BusinessException;

}
