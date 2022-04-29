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

package uy.gub.imm.sae.common.factories.ejb;

import java.io.File;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import uy.gub.imm.sae.common.SAEProfile;
import uy.gub.imm.sae.exception.ApplicationException;

public class LookupAnonimoLocalBusinessLocator extends LookupLocalBusinessLocator {

	private static final String LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_USER_NAME_KEY="locator.contexto.no.autenticado.backend.user.name";
	private static final String LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_USER_PASSWORD_KEY="locator.contexto.no.autenticado.backend.user.password";
	
	protected String getUsuarioAnonimo() throws ApplicationException{
		
		String user = SAEProfile.getInstance().getProperties().getProperty(LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_USER_NAME_KEY); 
		if (user == null) {
			throw new ApplicationException("-1", 
					"El nombre del usuario anonimo no está configurado: " + 
					LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_USER_NAME_KEY + " es null");
		}
		return user;
	}
	
	protected String getPasswordUsuarioAnonimo(String usuario) throws ApplicationException{

		String password = SAEProfile.getInstance().getProperties().getProperty(LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_USER_PASSWORD_KEY); 
		if (password == null) {
			throw new ApplicationException("-1", 
					"La password del usuario anonimo no está configurada: " + 
					LOCATOR_CONTEXTO_NO_AUTENTICADO_BACKEND_USER_PASSWORD_KEY + " es null");
		}
		return password;

	}
	
	@Override
	protected Context getContext() throws ApplicationException {

		String usuario_anonimo = getUsuarioAnonimo();
		
		try {
			Properties prop = new Properties();
			String dir = System.getProperty("jboss.server.config.dir");
			if(!dir.endsWith(File.separator)) {
				dir = dir + File.separator;
			}
			System.setProperty("jboss.ejb.client.properties.file.path", dir+"jboss-ejb-client.properties");
			
			prop.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming"); 
			prop.put("jboss.naming.client.ejb.context", true); 
			return new InitialContext(prop);
			
	    } catch (NamingException e) {
	    	throw new ApplicationException("-1", "Imposible crear contexto para usuario anonimo", e);
	    } catch (Exception e) {
			throw new ApplicationException("-1", "Imposible crear contexto para usuario anonimo", e);
		}
	}
	
}
