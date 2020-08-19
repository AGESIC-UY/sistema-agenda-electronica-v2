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
package uy.gub.imm.sae.common;

import java.io.InputStream;
import java.util.Properties;

import uy.gub.imm.sae.exception.ApplicationException;

public class SAEProfile {
	
	private static final String PROFILE = "/uy/gub/imm/sae/common/profile/profile.properties";
	private static final String PROFILE_KEY = "profile";
	public static final String DEPLOY_ENVIRONMENT = "uy.gub.imm.sae.escenario";

	public static final String ENVIRONMENT_PROFILE_HOST_PORT_KEY = "uy.gub.imm.sae.env.profile.host";
	public static final String ENVIRONMENT_PROFILE_WS_WSDL_HOST = "uy.gub.imm.sae.env.profile.ws.wsdl.host";
	public static final String ENVIRONMENT_PROFILE_WS_WSDL_CONTEXT_ROOT = "uy.gub.imm.sae.env.profile.ws.wsdl.contextroot";	
	
	public static final String PROFILE_UI_TEMPLATES_HEADER_KEY = "ui.templates.header.url";
	public static final String PROFILE_UI_TEMPLATES_HEADER_INTERNET_KEY = "ui.templates.header.internet.url";
	public static final String PROFILE_UI_TEMPLATES_FOOTER_KEY = "ui.templates.footer.url";
	public static final String PROFILE_UI_TEMPLATES_FOOTER_INTERNET_KEY = "ui.templates.footer.internet.url";
	public static final String PROFILE_UI_TEMPLATES_IMAGES_LOGO_TICKET_KEY = "ui.images.logo.ticket";
	
	public static final String EJB_AGENDAR_RESERVAS_JNDI_KEY = "uy.gub.imm.sae.service.ejb.agendar_reservas.jndi";
	public static final String EJB_RECURSOS_JNDI_KEY = "uy.gub.imm.sae.service.ejb.recursos.jndi";
	public static final String EJB_DEPURAR_RESERVAS_JNDI_KEY = "uy.gub.imm.sae.service.ejb.depurar_reservas.jndi";
	
	public static final String PROFILE_UI_RESOURCE_BASE_URL_KEY="ui.resource.base.url";
	
  public static final String EJB_CONSULTAS_JNDI_KEY = "uy.gub.imm.sae.service.ejb.consultas.jndi";
	public static final String EJB_COMUNICACIONES_JNDI_KEY =  "uy.gub.imm.sae.service.ejb.comunicaciones.jndi";
	
	//Instancia singleton
	private static SAEProfile instance;
	
	private String name;
	private Escenario environment;
	private Properties properties;
	
	private static Properties loadProperties(String propertiePath) throws ApplicationException {
		try {
			InputStream is = SAEProfile.class.getResourceAsStream(propertiePath);
			Properties prop = new Properties(); 
			prop.load(is);
			is.close();
			return prop;
		} catch (Exception e) {
			throw new ApplicationException("-1", String.format("Error al leer el archivo %s en el classpath", propertiePath), e);
		}
	}
	
	
	private SAEProfile() {
		
		try {
			//Cargo las propiedades para el perfil seleccionado
			Properties prop = loadProperties(PROFILE);
			//Crea el wrapper que accede a las propiedades del sistema.
			properties = new MyProperties(prop);

			//Obtengo el nombre del perfil configurado para esta instancia del sistema SAE
			name = properties.getProperty(PROFILE_KEY);
			
			//Obtengo el escenario de deploy (BACKEND | FRONTEND)
			environment = Escenario.valueOf(properties.getProperty(DEPLOY_ENVIRONMENT).toUpperCase());
			
		} catch (ApplicationException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	
	public static SAEProfile getInstance() {
		
		if (instance == null) {
			instance = new SAEProfile();
		}
		
		return instance;
	}

	
	public String getName() {
		return name;
	}

	/** Obtengo el escenario de deploy (BACKEND | FRONTEND) */
	public Escenario getEnvironment() {
		return environment;
	}


	public Properties getProperties() {
		return properties;
	}
	
	
	/**
	 * Este wrapper se encarga de consultar las propiedades del sistema en el caso de no encontrarlas en las properties del profile
	 * @author alvaro
	 *
	 */
	private class MyProperties extends Properties {
		
		private static final long serialVersionUID = 1L;

		public MyProperties(Properties prop) {
			super(prop);
		}

		@Override
		public String getProperty(String key, String defaultValue) {
			String value = System.getProperty(key);
			return (value != null ? value : super.getProperty(key, defaultValue));
		}

		@Override
		public String getProperty(String key) {
			String value = System.getProperty(key);
			return (value != null ? value : super.getProperty(key));
		}
	}
	
	
	/**
	 * Define los escenarios de deploy de la aplicación.
	 * @author snegreira
	 *
	 */
	public enum Escenario {
		BACKEND,
		FRONTEND;
	}

}
