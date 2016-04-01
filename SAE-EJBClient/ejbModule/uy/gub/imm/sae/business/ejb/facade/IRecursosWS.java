package uy.gub.imm.sae.business.ejb.facade;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;

@WebService
public interface IRecursosWS {

	@WebMethod
	public @WebResult(name = "consultarDatosDelRecursoResult") ArrayList<DatoDelRecurso> consultarDatosDelRecurso
		(
				@WebParam(name = "recurso") Recurso r
		)
		throws 
			ApplicationException, BusinessException;

	@WebMethod
	public @WebResult(name = "consultarDefinicionDeCamposResult") ArrayList<AgrupacionDato> consultarDefinicionDeCampos
		(
				@WebParam(name = "recurso") Recurso recurso
		)
		throws 
			BusinessException;

	@WebMethod
	public  @WebResult(name = "pingResult") String ping();
}
