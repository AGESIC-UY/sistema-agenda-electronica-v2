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

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import uy.gub.imm.sae.common.SofisHashMap;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Plantilla;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoTenant;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.TextoGlobal;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.login.SAEPrincipal;

@Stateless
@RolesAllowed({"RA_AE_ADMINISTRADOR","RA_AE_PLANIFICADOR","RA_AE_FCALL_CENTER","RA_AE_ANONIMO", "RA_AE_FATENCION", "RA_AE_LLAMADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
public class AgendaGeneralBean implements AgendaGeneralLocal, AgendaGeneralRemote {
	
	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;
	
	@Resource
	private SessionContext ctx;

	/**
	 * Retorna una lista de agendas vivas (fechaBaja == null) ordenada por nombre
	 * Solo retorna las agendas para las que el usuario tenga rol  Administrador/Planificador .
	 * Roles permitidos: Administrador, Planificador
	 */
	@SuppressWarnings("unchecked")
	public List<Agenda> consultarAgendas() throws ApplicationException{
		try{
			
			//Se obtienen todas las agendas para las cuales el usuario tiene
			//algún rol.
			
			String tenant = ((SAEPrincipal)ctx.getCallerPrincipal()).getTenant();
			String eql = "SELECT a " +
					 "FROM Agenda a " +
					 "WHERE '"+tenant+"'='"+tenant+"' AND " + 
					 "a.fechaBaja IS NULL " +
					 "ORDER BY a.descripcion";
			List<Agenda> agendas = (List<Agenda>) entityManager.createQuery(eql).getResultList();
			
			
			
			return agendas;
			
		} catch (Exception e){
			throw new ApplicationException(e);
		}
	}

	/**
	 * Retorna una lista de recursos vivos (fechaBaja == null)
	 */
	@SuppressWarnings("unchecked")
	public List<Recurso> consultarRecursos(Agenda a) throws ApplicationException{
		try{
			List<Recurso> recurso = (List<Recurso>) entityManager
									.createQuery("SELECT r from Recurso r " +
											"WHERE r.agenda = :a " +
											"AND r.fechaBaja IS NULL "+
											"ORDER BY r.nombre")
									.setParameter("a", a)
									.getResultList();
			return recurso;
			} catch (Exception e){
				throw new ApplicationException(e);
			}
	}

  /**
   * Retorna la lista de tramites asociados a la agenda
   */
  @SuppressWarnings("unchecked")
  public List<TramiteAgenda> consultarTramites(Agenda a) throws ApplicationException {
    try{
      List<TramiteAgenda> tramites = (List<TramiteAgenda>) entityManager
                  .createQuery("SELECT t from TramiteAgenda t " +
                      "WHERE t.agenda = :a " +
                      "ORDER BY t.tramiteNombre")
                  .setParameter("a", a)
                  .getResultList();
      return tramites;
      } catch (Exception e){
        throw new ApplicationException(e);
      }
  }
	
	
	/**
	 * Retorna una lista de plantillas vivas (fechaBaja == null)
	 * ordenadas por orden de creacion
	 * Controla que el usuario tenga rol Administrador/Planificador sobre la agenda del recurso <b>r</b>
	 * Roles permitidos: Administrador, Planificador
	 */
	@SuppressWarnings("unchecked")
	public List<Plantilla> consultarPlantillas(Recurso r) throws ApplicationException{
		try{
			List<Plantilla> plantilla = (List<Plantilla>) entityManager
									.createQuery("SELECT p from Plantilla p " +
											"WHERE p.recurso = :r " +
											"AND p.fechaBaja IS NULL ")
									.setParameter("r",r)
									// TODO CONTROLAR ROLES
									.getResultList();
			return plantilla;
			} catch (Exception e){
				throw new ApplicationException(e);
			}
	}

	
	@SuppressWarnings("unchecked")
	public Map<String, String> consultarTextos(String idioma) throws ApplicationException {
    Map<String, String> textos = new SofisHashMap();
		try{
			//Primero se cargan los textos globales
			List<TextoGlobal> tGlobales = (List<TextoGlobal>) globalEntityManager.createQuery("SELECT t from TextoGlobal t ").getResultList();
			if(tGlobales != null) {
				for(TextoGlobal tGlobal : tGlobales) {
					textos.put(tGlobal.getCodigo(), tGlobal.getTexto());
				}
			}
		} catch (Exception e){
			//No se pudieron cargar los textos globales, no se puede continuar
			throw new ApplicationException(e);
		}
			
		//Luego se cargan los textos sobreescritos por el tenant pudiendo pisar textos cargados antes
		//Atención: es probable que no haya aún un tennant configurado y por tanto no se puede hacer esta parte
		//(pero no hay forma de determinarlo)
		try {
			List<TextoTenant> tTenants = (List<TextoTenant>) entityManager.
					createQuery("SELECT t from TextoTenant t WHERE t.codigo.idioma=:idioma")
					.setParameter("idioma", idioma).getResultList();
			if(tTenants != null) {
				for(TextoTenant tTenant : tTenants) {
					textos.put(tTenant.getCodigo().getCodigo(), tTenant.getTexto());
				}
			}
		}catch(Exception pEx) {
			//Nada que hacer, se usan solo los textos globales
		}
		
		return textos;		
	}
	
	
}
