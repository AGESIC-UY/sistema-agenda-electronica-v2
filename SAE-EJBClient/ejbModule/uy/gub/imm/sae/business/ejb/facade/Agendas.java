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

import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;



public interface Agendas {

	public boolean existeAgendaPorNombre(Agenda agenda) throws ApplicationException;
	public Agenda crearAgenda(Agenda agenda) throws UserException, ApplicationException, BusinessException;
	public void eliminarAgenda(Agenda agenda, TimeZone timezone, String codigoUsuario) throws UserException, ApplicationException;
	public void modificarAgenda(Agenda agenda) throws UserException, ApplicationException;
	public void copiarAgenda(Agenda agenda) throws BusinessException, ApplicationException, UserException;
	public List<Agenda> consultarAgendas() throws ApplicationException;
}
