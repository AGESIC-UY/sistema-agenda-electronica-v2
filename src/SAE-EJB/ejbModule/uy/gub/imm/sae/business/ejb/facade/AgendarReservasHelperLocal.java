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

import javax.ejb.Local;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAutocompletarException;
import uy.gub.imm.sae.exception.ErrorValidacionCommitException;
import uy.gub.imm.sae.exception.ErrorValidacionException;
import uy.gub.imm.sae.exception.ValidacionException;
import uy.gub.imm.sae.exception.WarningAutocompletarException;

@Local
public interface AgendarReservasHelperLocal {
	public VentanaDeTiempo obtenerVentanaCalendarioEstaticaIntranet (Recurso recurso);
	public VentanaDeTiempo obtenerVentanaCalendarioEstaticaInternet (Recurso recurso);
	public List<Object[]> obtenerCuposAsignados(Recurso r, VentanaDeTiempo ventana, TimeZone timezone);
	public List<Object[]> obtenerCuposConsumidos(Recurso r, VentanaDeTiempo ventana, TimeZone timezone);
	public List<Integer> obtenerCuposXDia(VentanaDeTiempo ventana, List<Object[]> cuposAsignados, List<Object[]> cuposConsumidos);
	public Reserva crearReservaPendiente(Disponibilidad d, TokenReserva token, String ipOrigen);
	public Reserva crearReservasParesPendiente(Disponibilidad d,Disponibilidad d2, TokenReserva token, String ipOrigen) throws ApplicationException;
	public boolean chequeoCupoDisponible(Disponibilidad d, boolean reservaTomada);
	public List<DatoASolicitar> obtenerDatosASolicitar(Recurso r);
	public List<ValidacionPorRecurso> obtenerValidacionesPorRecurso(Recurso r);
	public void validarDatosReservaBasico(List<DatoASolicitar> campos, Map<String, DatoReserva> valores) throws ValidacionException;
	public void validarDatosReservaExtendido(List<ValidacionPorRecurso> validaciones, List<DatoASolicitar> campos, Map<String, DatoReserva> valores, ReservaDTO reservaDTO) throws ApplicationException, BusinessException, ErrorValidacionException, ErrorValidacionCommitException;
	public List<Reserva> validarDatosReservaPorClave(Recurso recurso, Disponibilidad disponibilidad, List<DatoASolicitar> campos, Map<String, DatoReserva> valores, String tramite, Integer idReserva);
	public Map<String, Object> autocompletarCampo(ServicioPorRecurso s, Map<String, Object> datosParam) throws ApplicationException, BusinessException, ErrorAutocompletarException, WarningAutocompletarException;
	//Cuenta las reservas existentes para la IP dada, en el período considerado por el recurso (hoy +- intervalo)
	//Si se pasa un id de reserva la excluye de la cuenta
	public int cantidadReservasPorIp(Recurso recurso, String ipOrigen, Integer reservaId);
	public void eliminarReservasParesPendientes(Reserva reserva1) throws ApplicationException;
	public void eliminarReservaPendiente(Reserva reserva1) throws ApplicationException;
	
	/*Métodos para activar y desactivar un recurso en internet en una transacción separada mientras se realizan tareas de mantenimiento de larga duración*/
	public void desactivarRecursoVisibleInternet(Recurso recurso);
	public void activarRecursoVisibleInternet(Recurso recurso);
	
	
}
