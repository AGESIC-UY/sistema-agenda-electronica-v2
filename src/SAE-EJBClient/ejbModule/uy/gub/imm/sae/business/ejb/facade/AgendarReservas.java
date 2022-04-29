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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import uy.gub.imm.sae.business.dto.ResultadoEjecucion;
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
	public boolean isRecursoVisibleInternet(Recurso recurso) throws ApplicationException, BusinessException;
	public List<Agenda> consultarAgendas() throws ApplicationException, BusinessException;
	public TramiteAgenda consultarTramitePorCodigo(Agenda a, String codigo) throws ApplicationException;
	public List<TramiteAgenda> consultarTramites(Agenda a) throws ApplicationException;
	public List<Recurso> consultarRecursos(Agenda a) throws ApplicationException, BusinessException;
	
	public VentanaDeTiempo obtenerVentanaCalendarioIntranet(Recurso r) throws UserException;
	public VentanaDeTiempo obtenerVentanaCalendarioInternet(Recurso r) throws UserException;
	public List<Integer> obtenerCuposPorDia(Recurso r, VentanaDeTiempo v, TimeZone timezone) throws UserException;
	public List<Disponibilidad> obtenerDisponibilidades(Recurso r, VentanaDeTiempo v, TimeZone timezone) throws UserException;
	public List<Disponibilidad> obtenerDisponibilidades(Recurso recurso, VentanaDeTiempo ventana, TimeZone timezone, boolean ajustarVentanaSegunAhora) throws UserException;
  /**
   * Crea una nueva reserva en estado pendiente, controla que aun exista cupo.
   */
	public Reserva marcarReserva(Disponibilidad d, TokenReserva token, String ipOrigen) throws UserException;
  /**
   * Crea una nueva reserva en estado pendiente, controla que aun exista cupo y que no exista otra reserva diferente con los mismos datos clave.
   */
	public Reserva marcarReservaValidandoDatos(Disponibilidad disponibilidad, Reserva reserva, TokenReserva token, String ipOrigen) throws UserException;
	public void desmarcarReserva(Reserva r) throws BusinessException;
	public void validarDatosReserva(Empresa e, Reserva r) throws BusinessException, ValidacionException, ApplicationException;
	public Reserva confirmarReserva(Empresa e, Reserva r, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException;
	
	public Reserva consultarReservaPorId(Integer idReserva) throws UserException;
	public void cancelarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva) throws UserException;
	public void cancelarReserva(Empresa e, Recurso recurso, Reserva reserva) throws UserException;
	public void liberarReserva(Integer idEmpresa, Integer idReserva) throws UserException;
	
	
	public Map<String, Object> autocompletarCampo(ServicioPorRecurso s, Map<String, Object> datosParam) throws ApplicationException, BusinessException, AutocompletarException, UserException;
	
	public Empresa obtenerEmpresaPorId(Integer empresaId) throws ApplicationException;
	public Recurso consultarRecursoPorReservaId(Integer reservId) throws ApplicationException, BusinessException;
	public Map<String, String> consultarTextos(String idioma) throws ApplicationException;
	
	public Map<String, String> consultarPreguntasCaptcha(String idioma) throws ApplicationException ;	

	public void limpiarTrazas();

	public boolean hayCupoPresencial(Disponibilidad disponibilidad);
	public Reserva confirmarReservaPresencial(Empresa empresa, Reserva reserva) throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException;
	
	//public List<Integer> cancelarReservasPeriodo(Empresa empresa, Recurso recurso, VentanaDeTiempo ventana, String idioma, String formatoFecha, String formatoHora,Boolean enviaCorreo, String asunto, String cuerpo) throws UserException;
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
   * Está pensado para ser invocado mediante el servicio web REST modificarReserva.
   * @return
   * @throws UserException
   */
  public Reserva modificarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva, Integer idDisponibilidad, 
      TokenReserva tokenReserva, String idioma, String ipOrigen) throws UserException;
  
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
	
	public TokenReserva generarTokenReserva(Integer idRecurso, String cedula, String nombre, String correoe, String codigoTramite, String ipOrigen) throws UserException;
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
  
  public Reserva modificarReservaNotificar(Integer idReserva, Boolean notificar) throws UserException;
  public void modificarReservaMultipleNotificar(TokenReserva tokenReserva, Boolean notificar) throws UserException; 
  
  
  /* Servicios para reserva múltiple vacunación COVID */
  
  
  /*
   * Crea dos nuevas reservas en estado pendiente, controla que aun exista cupo.
  */
  public Reserva marcarReservasPares(Disponibilidad d,Disponibilidad d2, TokenReserva token, String ipOrigen) throws UserException;
  
  Reserva generarYConfirmarReservasVacunacion(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idDisponibilidad, String valoresCampos,
          String idTransaccionPadre, String pasoTransaccionPadre, TokenReserva tokenReserva, String idioma, Date fechaReservaDos, String tipoDocReservaDos, String tipoDosisReservaDos) throws UserException;
  
  public Reserva confirmarReservasPares(Empresa e, Reserva r, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException;
  
  public TokenReserva cancelarReservasParesMultiple(Integer tokenId, Integer reservaId) throws UserException;
  
  public void cancelarReservaVacunacion(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva,Integer idReserva2, Boolean masiva) throws UserException;
  
  /**
   * Verifica que se pueda mover reservas de un recurso a otro, incluso entre diferentes agendas.
   * @param empresa
   * @param recursoOrigen
   * @param recursoDestino
   * @param fecha
   * @return
   * @throws UserException
   */

	public ResultadoEjecucion validarMoverReservas(Empresa empresa, Recurso recursoOrigen, Recurso recursoDestino, VentanaDeTiempo ventanaOrigen, VentanaDeTiempo ventanaDestino) 
		throws UserException, ApplicationException, BusinessException;
	
    public Long obtenerReservasConfirmadasRecursoOrigen(Recurso recurso, VentanaDeTiempo periodo) throws UserException;	

	public ResultadoEjecucion ejecutarMoverReservas(Empresa empresa, Recurso recursoOrigen, Recurso recursoDestino,VentanaDeTiempo ventanaOrigen, VentanaDeTiempo ventanaDestino, boolean enviarComunicaciones, 
			String linkBase, boolean generarNovedades, String uuid) throws UserException, ApplicationException, BusinessException;
	


}
