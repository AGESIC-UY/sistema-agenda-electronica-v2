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

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import uy.gub.imm.sae.entity.global.Configuracion;

@Stateless
@RolesAllowed("RA_AE_ANONIMO")
public class ConfiguracionBean implements ConfiguracionLocal, ConfiguracionRemote {

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;
	
	public String getString(String clave) {
		String eql = "SELECT c FROM Configuracion c WHERE c.clave=:clave";
		try {
			Configuracion conf =  (Configuracion) globalEntityManager.createQuery(eql).setParameter("clave", clave).getSingleResult();
			return conf.getValor();
		}catch(Exception ex) {
			return null;
		}
	}
	
	public Boolean getBoolean(String clave) {
		String valor = getString(clave);
		if(valor==null) {
			return null;
		}
		return Boolean.valueOf(valor);
	}
	
	public Long getLong(String clave) {
		String valor = getString(clave);
		if(valor==null) {
			return null;
		}
		return Long.valueOf(valor);
	}

  @SuppressWarnings("unchecked")
  @Override
  public List<Configuracion> getAll() {
    String eql = "SELECT c FROM Configuracion c ORDER BY c.clave";
    try {
      return  (List<Configuracion>)globalEntityManager.createQuery(eql).getResultList();
    }catch(Exception ex) {
      return null;
    }
  }

  @Override
  public void guardar(Configuracion conf) {
    globalEntityManager.merge(conf);
  }

}
