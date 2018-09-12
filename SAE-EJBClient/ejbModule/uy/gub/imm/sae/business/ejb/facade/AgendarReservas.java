/*
 * SAE - Sistema de Agenda Electronica
 * Copyright (C) 2009  IMM - Intendencia Municipal de Montevideo
 *
 * This file is part of SAE.

 * SAE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uy.gub.imm.sae.business.ejb.facade;


import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.AutocompletarException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionException;

public interface AgendarReservas {

	public Agenda consultarAgendaPorId(Integer id) throws ApplicationException, BusinessException;
	public Recurso consultarRecursoPorId(Agenda a, Integer id) throws ApplicationException, BusinessException;
	public List<Agenda> consultarAgendas() throws ApplicationException, BusinessException;
  public TramiteAgenda consultarTramitePorCodigo(Agenda a, String codigo) throws ApplicationException;
	public List<TramiteAgenda> consultarTramites(Agenda a) throws ApplicationException;
	public List<Recurso> consultarRecursos(Agenda a) throws ApplicationException, BusinessException;
	
	public VentanaDeTiempo obtenerVentanaCalendarioIntranet(Recurso r) throws UserException;
	public VentanaDeTiempo obtenerVentanaCalendarioInternet(Recurso r) throws UserException;
	public List<Integer> obtenerCuposPorDia(Recurso r, VentanaDeTiempo v, TimeZone timezone) throws UserException;
	public List<Disponibilidad> obtenerDisponibilidades(Recurso r, VentanaDeTiempo v, TimeZone timezone) throws UserException;
  /**
   * Crea una nueva reserva en estado pendiente, controla que aun exista cupo.
   */
	public Reserva marcarReserva(Disponibilidad d, TokenReserva token) throws UserException;
  /**
   * Crea una nueva reserva en estado pendiente, controla que aun exista cupo y que no exista otra reserva diferente con los mismos datos clave.
   */
	public Reserva marcarReservaValidandoDatos(Disponibilidad disponibilidad, Reserva reserva, TokenReserva token) throws UserException;
	public void desmarcarReserva(Reserva r) throws BusinessException;
	public void validarDatosReserva(Empresa e, Reserva r) throws BusinessException, ValidacionException, ApplicationException;
	public Reserva confirmarReserva(Empresa e, Reserva r, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException;
	
	public Reserva consultarReservaPorId(Integer idReserva) throws UserException;
  public void cancelarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva) throws UserException;
	public void cancelarReserva(Empresa e, Recurso recurso, Reserva reserva) throws UserException;
	
	public Map<String, Object> autocompletarCampo(ServicioPorRecurso s, Map<String, Object> datosParam) throws ApplicationException, BusinessException, AutocompletarException, UserException;
	
	public Empresa obtenerEmpresaPorId(Integer empresaId) throws ApplicationException;
	public Recurso consultarRecursoPorReservaId(Integer reservId) throws ApplicationException, BusinessException;
	public Map<String, String> consultarTextos(String idioma) throws ApplicationException;
	
	public Map<String, String> consultarPreguntasCaptcha(String idioma) throws ApplicationException ;	
	/**
	 * Estos metodos son usados para enviar las comunicaciones cada vez que un usuario reserva o cancela una reservación.
	 * Por el momento hay tres tipos de comunicaciones: por mail, por sms y por texto a voz.
	 * Para enviar un mail, es necesario que exista un campo llamado "Mail" dentro de la agrupacion "DatosPersonales".
	 *   El texto del mail se obtiene incorporando los datos de la reserva en el texto definido para el envío de mail en la Agenda.
	 * En el caso de SMS y TAV en realidad no se envía nada sino que se escribe en una tabla llamada ae_comunicaciones a la espera
	 * 	de que un proceso externo los tome y marque como procesadas. 
	 * Para poder enviar un SMS es necesario que exista un campo llamado "TelefonoMovil" dentro de la agrupacion "DatosPersonales".
	 * Para poder enviar un TAV es necesario que exista un campo llamado "TelefonoFijo" dentro de la agrupacion "DatosPersonales".
	 */
	public void enviarComunicacionesConfirmacion(String linkCancelacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException, UserException;
	public void enviarComunicacionesCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException,UserException;
	/**
	 * Este método es similares a enviarComunicacionesConfirmacion pero aplica a un TokenReserva que contenga múltiples reservas en lugar de a una sola de ellas.
	 */
  public void enviarComunicacionesConfirmacion(String templateLinkCancelacion, TokenReserva tokenReserva, String idioma, String formatoFecha, String formatoHora) throws UserException;
	
	public void limpiarTrazas();

	public boolean hayCupoPresencial(Disponibilidad disponibilidad);
	public Reserva confirmarReservaPresencial(Empresa empresa, Reserva reserva) throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException;
	
	public List<Integer> cancelarReservasPeriodo(Empresa empresa, Recurso recurso, VentanaDeTiempo ventana, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) throws UserException;
	
	/**
	 * Este método es utilizado para marcar la reserva y confirmarla en un solo paso.
	 * Está pensado para ser invocado mediante el servicio web REST confirmarReserva.
	 * @return
	 * @throws UserException
	 */
	public Reserva generarYConfirmarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idDisponibilidad, String valoresCampos, 
	    String idTransaccionPadre, String pasoTransaccionPadre, TokenReserva tokenReserva, String idioma) throws UserException;
	
  /**
   * Este método es utilizado para modificar una reserva. Solo se permite modificar la disponibilidad, no el recurso
   * porque los datos a solicitar podrían ser diferentes ni los datos ingresados porque podrían ser de otra persona. 
   * Está pensado para ser invocado mediante el servicio web REST modificarrReserva.
   * @return
   * @throws UserException
   */
  public Reserva modificarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva, Integer idDisponibilidad, 
      TokenReserva tokenReserva, String idioma) throws UserException;
  
  /**
   * Este método es utilizado para modificar una reserva. Solo se permite modificar la disponibilidad, no el recurso
   * porque los datos a solicitar podrían ser diferentes ni los datos ingresados porque podrían ser de otra persona. 
   * La reserva original mantiene todos los datos, excepto la disponibilidad a la cual apunta, que cambia por la
   * disponibilidad de la reserva nueva. Si se puede guardar la reserva original se elimina de la base de datos la
   * reserva nueva.
   * Está pensado para ser invocado desde la interfaz web.
   * @return
   * @throws UserException
   */
  public Reserva modificarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReservaOriginal, Integer idReservaNueva, 
      String idioma) throws UserException;
  
	//Para las reservas múltiples
	
	public TokenReserva generarTokenReserva(Integer idRecurso, String cedula, String nombre, String correoe, String codigoTramite) throws UserException;
  public TokenReserva obtenerTokenReserva(String token);
	public TokenReserva guardarTokenReserva(TokenReserva token);
  public List<Reserva> obtenerReservasMultiples(Integer tokenId, boolean incluirIncompletas);
  public TokenReserva confirmarReservasMultiples(Integer empresaId, Integer tokenId, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) throws UserException;
  /**
   * Elimina una reservas asociada al token indicado.
   * Solo puede ser invocado si el token está pendiente de confirmación; una vez confirmado no se puede cancelar ninguna reserva
   */
  public TokenReserva cancelarReservaMultiple(Integer tokenId, Integer reservaId) throws UserException;
  /**
   * Elimina todas las reservas asociadas al token y marca a éste como cancelado.
   * Solo puede ser invocado si el token está pendiente de confirmación; una vez confirmado no se puede cancelar
   */
  public TokenReserva cancelarReservasMultiples(Integer tokenId) throws UserException;
}
