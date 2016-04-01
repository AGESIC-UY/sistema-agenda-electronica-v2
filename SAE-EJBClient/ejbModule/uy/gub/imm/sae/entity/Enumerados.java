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

package uy.gub.imm.sae.entity;

public interface Enumerados {

	
	/**
	 * Roles generales (estáticos o declarativos) que utiliza el sistema.
	 */
	public static enum SAERol {RA_AE_ADMINISTRADOR, RA_AE_PLANIFICADOR, RA_AE_FCALL_CENTER,RA_AE_FATENCION, RA_AE_ANONIMO};
	
	/**
	 * Prefijos de los roles específicos (dinámicos) utilizados en el sistema
	 * A este prefijo se le debe concatenar el nombre de la agenda para obtener el rol específico real.
	 * Ejemplo: agenda.getNombre() == 'PEPE' => rol de atencion para la agenda PEPE == RA_AE_PLANI_PEPE
	 */
	public static enum SAERolPrefijo {RA_AE_PLANI, RA_AE_FCALL, RA_AE_FATEN};
	
}
