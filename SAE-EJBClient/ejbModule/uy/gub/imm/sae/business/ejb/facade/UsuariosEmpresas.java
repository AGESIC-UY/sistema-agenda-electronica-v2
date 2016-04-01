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

import uy.gub.imm.sae.business.dto.UsuarioEmpresaRoles;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Oficina;
import uy.gub.imm.sae.entity.global.Organismo;
import uy.gub.imm.sae.entity.global.Tramite;
import uy.gub.imm.sae.entity.global.UnidadEjecutora;
import uy.gub.imm.sae.entity.global.Usuario;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;

public interface UsuariosEmpresas {

	public Usuario obtenerUsuarioPorCodigo(String codigo) throws ApplicationException;
	public List<Usuario> consultarUsuarios() throws ApplicationException;
	public List<Usuario> consultarUsuariosEmpresa(Integer empresaId) throws ApplicationException;
	public Usuario guardarUsuario(Usuario usuario) throws ApplicationException; 
	public void eliminarUsuario(Usuario usuario) throws ApplicationException;
	public Usuario generarYEnviarPassword(Usuario usuarioEditar) throws ApplicationException;
	
	public Empresa obtenerEmpresaPorId(Integer empresaId) throws ApplicationException;
	public Empresa obtenerEmpresaPorNombre(String nombre) throws ApplicationException;
	public List<Empresa> consultarEmpresas() throws ApplicationException;
	public Empresa guardarEmpresa(Empresa empresa) throws ApplicationException, UserException; 
	public void eliminarEmpresa(Empresa empresa) throws ApplicationException, UserException;

	public List<String> obtenerRolesUsuarioEmpresa(Integer usuarioId, Integer empresaId) throws ApplicationException;
	public void guardarRolesUsuarioEmpresa(UsuarioEmpresaRoles roles) throws ApplicationException;

	/**
	 * Devuelve la lista de organismos. Si el parámetro actualizar es false devuelve lo que tiene en la
	 * base de datos, si es true vacía la base de datos, consulta al servicio web, almacena los resultados
	 * en la base de datos y devuelve la lista de organismos.	 * 
	 * @param actualizar true si se debe consultar el servicio web para actualizar la base de datos o no.
	 * @return
	 * @throws ApplicationException
	 */
	public List<Organismo> obtenerOrganismos(boolean actualizar) throws ApplicationException;
	public List<UnidadEjecutora> obtenerUnidadesEjecutoras(boolean actualizar) throws ApplicationException;
	
	public List<Tramite> obtenerTramitesEmpresa(Integer empresaId, boolean actualizar) throws ApplicationException, UserException;
	public List<Oficina> obtenerOficinasTramite(String tramiteId, boolean actualizar) throws ApplicationException;
	public void eliminarUsuarioEmpresa(Usuario data, Empresa empActual) throws ApplicationException;
	public byte[] obtenerLogoEmpresaPorEmpresaId(Integer empId);
	public List<Empresa> consultarEmpresasPorUsuario(Usuario usuarioActual) throws ApplicationException;
	public List<Empresa> obtenerEmpresasPorDatasource(String dataSource);
	public List<Empresa> consultarTodasEmpresas() throws ApplicationException;
	public boolean empresaEsquemaValido(Integer empresaId);
	public boolean existeEsquema(String esquema);
	
	public List<String> obtenerIdiomasSoportados();
	
}
