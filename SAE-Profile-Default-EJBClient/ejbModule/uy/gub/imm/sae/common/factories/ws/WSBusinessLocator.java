package uy.gub.imm.sae.common.factories.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.SAEProfile;
import uy.gub.imm.sae.common.factories.ejb.LookupAnonimoLocalBusinessLocator;
import uy.gub.imm.sae.common.factories.ws.client.agendar.AgendarReservasService;
import uy.gub.imm.sae.common.factories.ws.client.agendar.AgendarReservasWS;
import uy.gub.imm.sae.common.factories.ws.client.recursos.ManejoRecursosService;
import uy.gub.imm.sae.common.factories.ws.client.recursos.RecursosWS;
import uy.gub.imm.sae.exception.ApplicationException;

public class WSBusinessLocator extends LookupAnonimoLocalBusinessLocator {
	
	public WSBusinessLocator() {
		super();
	}
/*glabandera getAgendarReservas() ahora devuelve AgendarReservasRemote 09/11/2015
	public AgendarReservasPort getAgendarReservas() throws ApplicationException {
		return new AgendarReservasPort(getAgendarReservasService());
	}
*/
	public Recursos getRecursos() throws ApplicationException {
		return new RecursosPort(getRecursosService());
	}
	
	private AgendarReservasWS getAgendarReservasService() throws ApplicationException {
		final String baseName = "AgendarReservas";
		final String serviceName = baseName + "Service";
		final String schema = "http://montevideo.gub.uy/schema/sae/1.0/";
		URL wsdlLocation;
		try {
			wsdlLocation = new URL(
									SAEProfile.getInstance().getProperties().getProperty(SAEProfile.ENVIRONMENT_PROFILE_WS_WSDL_HOST) + 
									"/" + SAEProfile.getInstance().getProperties().getProperty(SAEProfile.ENVIRONMENT_PROFILE_WS_WSDL_CONTEXT_ROOT) + 
									"/" + baseName + "?wsdl");
			QName qname = new QName(schema, serviceName);
			AgendarReservasService service = new AgendarReservasService(wsdlLocation, qname);
			return  service.getAgendarReservasPort();
			
//			serviceCache.put(name, service);
		} catch (MalformedURLException e) {
			throw new ApplicationException(e);
		}
	}
	
	private RecursosWS getRecursosService() throws ApplicationException {
		final String baseName = "ManejoRecursos";
		final String serviceName = baseName + "Service";
		final String schema = "http://montevideo.gub.uy/schema/sae/1.0/";
		URL wsdlLocation;
		try {
			wsdlLocation = new URL(
					SAEProfile.getInstance().getProperties().getProperty(SAEProfile.ENVIRONMENT_PROFILE_WS_WSDL_HOST) + 
					"/" + SAEProfile.getInstance().getProperties().getProperty(SAEProfile.ENVIRONMENT_PROFILE_WS_WSDL_CONTEXT_ROOT) + 
					"/" + baseName + "?wsdl");
			QName qname = new QName(schema, serviceName);
			ManejoRecursosService service = new ManejoRecursosService(wsdlLocation, qname);
			return service.getManejoRecursosPort();
			
//			serviceCache.put(name, service);
		} catch (MalformedURLException e) {
			throw new ApplicationException(e);
		}
	}
	

	public static class ServiceCache {
		
		private static ServiceCache serviceCache;
		private Map<String, Service> cache = new HashMap<String, Service>();
		
		private ServiceCache() {			
		}
		
		public static ServiceCache getInstance() {
			if (serviceCache == null) {
				serviceCache = new ServiceCache();
			}
			return serviceCache;
		}
		
		public Service getService(String name) {
			return cache.get(name);
		}
		
		public void put(String name, Service service) {
			cache.put(name, service);		
		}
	}

}
