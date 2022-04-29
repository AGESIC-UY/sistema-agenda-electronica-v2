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
import java.util.TimeZone;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Llamada;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.UserException;

public interface Llamadas {
	
	public Reserva siguienteEnEspera(Recurso recurso, Integer puesto, boolean presencial) throws UserException;
	public Reserva volverALlamar(Recurso recurso, Integer puesto, Reserva reserva) throws UserException;
	public List<ReservaDTO> obtenerReservasEnEspera(Recurso recurso, List<Estado> estados, boolean atencionPresencial, TimeZone timezone) throws UserException;
	public List<Llamada> obtenerLlamadas(List<Recurso> recursos, Integer cantLlamadas) throws UserException;
	public void marcarAsistencia(Empresa empresa, Recurso recursoMarcado, Reserva r) throws UserException;
	public void marcarInasistencia(Empresa empresa, Recurso recursoMarcado, Reserva r) throws UserException;
}
