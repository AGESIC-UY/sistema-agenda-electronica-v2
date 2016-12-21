package uy.gub.imm.sae.business.ejb.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

//import org.jboss.wsf.spi.annotation.WebContext;



import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
@WebService(serviceName = "ManejoRecursosService",  targetNamespace = "http://montevideo.gub.uy/schema/sae/1.0/", portName = "ManejoRecursosPort")
//@WebContext(urlPattern = "/ManejoRecursos")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class RecursosWS implements IRecursosWS {
	
	@WebMethod
	public @WebResult(name = "consultarDatosDelRecursoResult") ArrayList<DatoDelRecurso> consultarDatosDelRecurso
		(
			@WebParam(name = "recurso") Recurso r
		) 
			throws ApplicationException, BusinessException {
		
		Recursos recursosEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getRecursos();

		List<DatoDelRecurso> lst = recursosEJB.consultarDatosDelRecurso(r);
		
		if (lst instanceof ArrayList){
			return (ArrayList<DatoDelRecurso>)lst;
		}else{
			ArrayList<DatoDelRecurso> arrLst = new ArrayList<DatoDelRecurso>(lst);
			return arrLst;
		}
	}

	@WebMethod
	public @WebResult(name = "Result") ArrayList<AgrupacionDato> consultarDefinicionDeCampos
		(
			@WebParam(name = "recurso") Recurso recurso,
			@WebParam(name = "timezone") TimeZone timezone
		)
			throws UserException {

		Recursos recursosEJB = null;

		try{
			recursosEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getRecursos();
		}catch (ApplicationException e){
			throw new UserException(e.getCodigoError(),e.getMessage(),e.getCause());
		}

		List<AgrupacionDato> lst = recursosEJB.consultarDefinicionDeCampos(recurso, timezone);
		
		if (lst instanceof ArrayList){
			return (ArrayList<AgrupacionDato>)lst;
		}else{
			ArrayList<AgrupacionDato> arrLst = new ArrayList<AgrupacionDato>(lst);
			return arrLst;
		}
	}
	
	@WebMethod
	public @WebResult(name = "pingResult") String ping() {
		return "pong";
	}
}