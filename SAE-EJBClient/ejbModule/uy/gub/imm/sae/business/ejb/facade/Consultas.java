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
import java.util.TimeZone;

import uy.gub.imm.sae.business.dto.AtencionLLamadaReporteDT;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.exception.UserException;

public interface Consultas {

	
	public Reserva consultarReservaId(Integer id, Integer recId) throws UserException;
	public Reserva consultarReservaPorNumero(Recurso r, Date fechaHoraInicio, Integer numero) throws UserException;

	public List<ReservaDTO> consultarReservasPorPeriodoEstado(Recurso recurso, VentanaDeTiempo periodo, Estado estado, boolean atencionPresencial) throws UserException;
	public List<ReservaDTO> consultarReservasPorPeriodoEstado(Recurso recurso, VentanaDeTiempo periodo, List<Estado> estados, boolean atencionPresencial) throws UserException;
	public List<ReservaDTO> consultarReservasEnEspera(Recurso recurso, boolean atencionPresencial, TimeZone timezone) throws UserException;
	public List<ReservaDTO> consultarReservasEnEsperaUtilizadas(Recurso recurso, boolean atencionPresencial, TimeZone timezone) throws UserException;
	public List<Reserva> consultarReservaDatos(List<DatoReserva> datos ,Recurso recurso);
	public List<Reserva> consultarReservaDatosFecha(List<DatoReserva> datos, Recurso recurso, Date fecha, String codigoTramite);	
	public List<ReservaDTO> consultarReservasUsadasPeriodo(Recurso recurso, VentanaDeTiempo periodo) throws UserException;
	public List<Reserva> consultarReservasParaCancelar(List<DatoReserva> datos ,Recurso recurso,String codigoSeguridadReserva, TimeZone timezone);
	public List<Atencion> consultarTodasAtencionesPeriodo(Date fechaDesde,Date fechaHasta);
	public List<AtencionLLamadaReporteDT> consultarLlamadasAtencionPeriodo(Date fechaDesde, Date fechaHasta);
	
	public List<Date> consultarReservasPorTokenYDocumento(String token, Integer idAgenda, Integer idRecurso, String tipoDoc, String numDoc) throws UserException;
}
