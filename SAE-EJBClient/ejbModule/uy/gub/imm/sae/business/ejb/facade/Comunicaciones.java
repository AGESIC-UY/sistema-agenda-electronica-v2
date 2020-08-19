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


import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.UserException;

public interface Comunicaciones {

  public void enviarComunicacionesConfirmacion(Empresa empresa, String linkCancelacion, String linkModificacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws UserException;
  
  public void enviarComunicacionesConfirmacion(Empresa empresa, String templateLinkCancelacion, String templateLinkModificacion, TokenReserva tokenReserva, String idioma, String formatoFecha, String formatoHora) throws UserException;
  
  public void enviarComunicacionesCancelacion(Empresa empresa, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws UserException;

  public void enviarComunicacionesCancelacion(Empresa empresa, Reserva reserva, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) throws UserException;

}
