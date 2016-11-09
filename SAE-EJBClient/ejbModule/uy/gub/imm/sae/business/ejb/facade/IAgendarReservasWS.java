package uy.gub.imm.sae.business.ejb.facade;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorValidacionCommitException;
import uy.gub.imm.sae.exception.ErrorValidacionException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionException;

@WebService
public interface IAgendarReservasWS {

	@WebMethod
	public @WebResult(name = "confirmarReservaResult") Reserva confirmarReserva
		(
			@WebParam(name = "empresa") Empresa e,
			@WebParam(name = "reserva") Reserva r,
			@WebParam(name = "trazabilidadIdTransaccionPadre") String transaccionPadreId,
			@WebParam(name = "trazabilidadPasoPadre") Long pasoPadre,
			@WebParam(name = "inicioAsistido") boolean inicioAsistido
		)
		throws 
			ApplicationException, BusinessException, AccesoMultipleException, ValidacionException, UserException,
			ErrorValidacionException,	ErrorValidacionCommitException,	ValidacionClaveUnicaException;

	@WebMethod
	public @WebResult(name = "consultarAgendaPorIdResult") Agenda consultarAgendaPorId
		(
			@WebParam(name = "id") Integer id
		)
		throws 
			ApplicationException, BusinessException;
	
	@WebMethod
	public @WebResult(name = "consultarRecursosResult") ArrayList<Recurso> consultarRecursos
		(
			@WebParam(name = "agenda") Agenda a
		) 
		throws 
			ApplicationException, BusinessException;

	@WebMethod
	public @WebResult(name = "consultarReservaPorDatosClaveResult") Reserva consultarReservaPorDatosClave
		(
			@WebParam(name = "recurso") Recurso r, 
			@WebParam(name = "datos") HashMap<DatoASolicitar, DatoReserva> datos
		) 
		throws 
			ApplicationException;

	@WebMethod
	public void desmarcarReserva
		(
			@WebParam(name = "reserva") Reserva r
		) 
		throws 
			BusinessException;

	@WebMethod(operationName = "marcarReservaDisponible")
	public @WebResult(name = "marcarReservaDisponibleResult") Reserva marcarReserva
		(
			@WebParam(name = "disponibilidad") Disponibilidad d
		) 
		throws 
			BusinessException, UserException;

	@WebMethod
	public @WebResult(name = "obtenerCuposPorDiaResult")  ArrayList<Integer> obtenerCuposPorDia
		(
			@WebParam(name = "recurso") Recurso r, 
			@WebParam(name = "ventanaDeTiempo") VentanaDeTiempo v,
      @WebParam(name = "timezone") TimeZone t
		)
		throws 
			BusinessException;

	@WebMethod
	public @WebResult(name = "obtenerDisponibilidadesResult") ArrayList<Disponibilidad> obtenerDisponibilidades
		(
			@WebParam(name = "recurso") Recurso r, 
			@WebParam(name = "ventanaDeTiempo") VentanaDeTiempo v,
			@WebParam(name = "timezone") String tz
		)
		throws 
			BusinessException;

	@WebMethod
	public @WebResult(name = "obtenerVentanaCalendarioInternetResult") VentanaDeTiempo obtenerVentanaCalendarioInternet
		(
			@WebParam(name = "recurso") Recurso r
		)
		throws 
			BusinessException;
	
	@WebMethod
	public  @WebResult(name = "pingResult") String ping();
}
