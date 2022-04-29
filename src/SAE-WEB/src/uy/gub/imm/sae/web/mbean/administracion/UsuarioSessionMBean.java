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

package uy.gub.imm.sae.web.mbean.administracion;

import uy.gub.imm.sae.business.dto.UsuarioEmpresaRoles;
import uy.gub.imm.sae.entity.global.Usuario;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;



public class UsuarioSessionMBean extends SessionCleanerMBean implements RemovableFromSession {
	
	public static final String MSG_ID = "pantalla";
	
	private Usuario usuarioEditar;
	private UsuarioEmpresaRoles usuarioEmpresaRolesEditar;
	private Usuario usuarioEliminar;
	public Usuario getUsuarioEditar() {
		return usuarioEditar;
	}

	public void setUsuarioEditar(Usuario usuarioEditar) {
		this.usuarioEditar = usuarioEditar;
	}

	public UsuarioEmpresaRoles getUsuarioEmpresaRolesEditar() {
		return usuarioEmpresaRolesEditar;
	}

	public void setUsuarioEmpresaRolesEditar(UsuarioEmpresaRoles usuarioEmpresaRolesEditar) {
		this.usuarioEmpresaRolesEditar = usuarioEmpresaRolesEditar;
	}

	public Usuario getUsuarioEliminar() {
		return usuarioEliminar;
	}

	public void setUsuarioEliminar(Usuario usuarioEliminar) {
		this.usuarioEliminar = usuarioEliminar;
	}

	
	
}


