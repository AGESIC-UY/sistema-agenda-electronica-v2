package uy.gub.imm.sae.business.ejb.facade;

import java.util.Calendar;
import java.util.List;

import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ParametroAccion;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Accion;
import uy.gub.imm.sae.entity.AccionPorDato;
import uy.gub.imm.sae.entity.AccionPorRecurso;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

public interface DepuracionReservas {
	
	public void eliminarReservas(Empresa empresa,Calendar fechaLimiteIndividual,Calendar fechaLimiteMultiple,Integer idRecurso, List<Integer> idsReservaIndividuales, List<Integer>idsReservaMultiples);

}