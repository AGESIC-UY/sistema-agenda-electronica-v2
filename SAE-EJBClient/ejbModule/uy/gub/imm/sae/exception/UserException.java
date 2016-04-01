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

package uy.gub.imm.sae.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class UserException extends BaseException {

	private static final long serialVersionUID = 1526884004143036357L;

	public UserException(String codigoError) {
		super(codigoError);
	}
	
	public UserException(String codigoError, String message) {
		super(codigoError, message);
	}

	public UserException(String codigoError, String message, Throwable cause) {
		super(codigoError, message, cause);
	}

}
