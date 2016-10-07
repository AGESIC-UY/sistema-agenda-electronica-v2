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

import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Plantilla;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.exception.ApplicationException;

public interface AgendaGeneral {

	public List<Agenda> consultarAgendas() throws ApplicationException;
	public List<Recurso> consultarRecursos(Agenda a) throws ApplicationException;
  public List<TramiteAgenda> consultarTramites(Agenda a) throws ApplicationException;
	public List<Plantilla> consultarPlantillas(Recurso r) throws ApplicationException;
	public Map<String, String> consultarTextos(String idioma) throws ApplicationException;
}
