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

import uy.gub.imm.sae.common.DisponibilidadReserva;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Plantilla;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.exception.UserException;

public interface Disponibilidades {
	public List<Date> generarDisponibilidadesNuevas(Recurso r, Date fecha, Date horaDesde, Date horaHasta, Integer frecuencia, Integer cupo) throws UserException, ApplicationException;
	public void generarDisponibilidades(Recurso r, Date f, VentanaDeTiempo periodo, Boolean[] dias) throws UserException, ApplicationException;	
	public void generarPatronSemana(Recurso r, VentanaDeTiempo semana, VentanaDeTiempo periodo) throws BusinessException, UserException, ApplicationException;	
	public List<Disponibilidades> consultarDisponibilidadesSolapadas(Recurso r, Plantilla p, VentanaDeTiempo v);
	public void generarDisponibilidaesAutomaticamente();
	public void eliminarDisponibilidades(Recurso r, VentanaDeTiempo v) throws BusinessException, UserException;
	public List<DisponibilidadReserva> obtenerDisponibilidadesReservas(Recurso r, VentanaDeTiempo v) throws BusinessException, RolException;
	public int modificarCupoDeDisponibilidad(Disponibilidad d) throws UserException, BusinessException;
	public void modificarCupoPeriodo(Disponibilidad d) throws UserException, BusinessException;
	public Integer cantDisponibilidadesDia(Recurso r, Date f) throws UserException, BusinessException;
	public Date ultFechaGenerada(Recurso r) throws UserException, BusinessException;
	public List<String> modificarCupoPeriodoValorOperacion(Disponibilidad d, TimeZone timezone, int valor,int tipoOperacion, Boolean[] dias) throws UserException, BusinessException ;
	public boolean esDiaHabil(Date fecha, Recurso r) throws ApplicationException;
}
