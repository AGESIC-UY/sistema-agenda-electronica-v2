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

import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.ParametrosAutocompletar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.ServicioAutocompletar;
import uy.gub.imm.sae.entity.ServicioAutocompletarPorDato;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;

public interface Autocompletados {
	
	public ServicioAutocompletar crearAutoCompletado(ServicioAutocompletar a)throws UserException, BusinessException;
	public ServicioAutocompletar modificarAutoCompletado(ServicioAutocompletar a)throws UserException, BusinessException;
	public void eliminarAutoCompletado(ServicioAutocompletar a)throws UserException, BusinessException;
	public List<ServicioAutocompletar> consultarAutoCompletados()throws ApplicationException;
	public Boolean existeAutoCompletadoPorNombre(String nombreAutoCompletado) throws ApplicationException;
	public List<ParametrosAutocompletar> consultarParametrosDelAutoCompletado(ServicioAutocompletar a) throws ApplicationException;
	
	//M�todos para manejar la asociacion de datos autocompletados a recursos.
	public List<ServicioPorRecurso> obtenerAutocompletadosDelRecurso(Recurso recurso) throws ApplicationException;
	public List<ServicioAutocompletarPorDato> obtenerAsociacionesAutocompletadoPorDato(ServicioPorRecurso sr) throws ApplicationException;
	public ServicioAutocompletarPorDato asociarAutocompletadoPorDato(DatoASolicitar d, ServicioPorRecurso sr, ServicioAutocompletarPorDato sad) throws UserException, ApplicationException;
	public void desasociarAutocompletadoPorDato(ServicioAutocompletarPorDato sad) throws UserException;
	public ServicioPorRecurso modificarAutocompletadoPorRecurso(ServicioPorRecurso sr) throws UserException, BusinessException;
	public ServicioPorRecurso crearAutocompletadoPorRecurso(ServicioPorRecurso sr) throws BusinessException, UserException;
	public void eliminarAutocompletadoPorRecurso(ServicioPorRecurso sr) throws BusinessException;
	
}
