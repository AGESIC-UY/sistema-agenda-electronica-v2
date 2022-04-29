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

package uy.com.sofis.sae.mrree.acciones;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.Stateless;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;

import uy.com.sofis.mrree.pcancelarsolicitud.PCancelarSolicitud;
import uy.com.sofis.mrree.pcancelarsolicitud.PCancelarSolicitudExecute;
import uy.com.sofis.mrree.pcancelarsolicitud.PCancelarSolicitudExecuteResponse;
import uy.com.sofis.mrree.pcancelarsolicitud.PCancelarSolicitudSoapPort;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.sae.acciones.business.ejb.EjecutorAccionRemote;
import uy.gub.sae.acciones.business.ejb.ErrorAccion;
import uy.gub.sae.acciones.business.ejb.ResultadoAccion;
import uy.gub.sae.acciones.business.ejb.exception.InvalidParametersException;
import uy.gub.sae.acciones.business.ejb.exception.UnexpectedAccionException;

/**
 * @author spio
 *
 */

@Stateless
public class CancelarSolicitud implements EjecutorAccionRemote {

  Logger logger = Logger.getLogger(CancelarSolicitud.class);
  
	@Override
	public ResultadoAccion ejecutar(String nombreAccion, Map<String, Object> params)
			throws UnexpectedAccionException, InvalidParametersException {

	  long invId = (new Date()).getTime();
    
    logger.info("["+invId+"] CancelarSolicitud - invocación recibida");
    
    for(String param : params.keySet()) {
      logger.debug("["+invId+"] CancelarSolicitud - parámetro <"+param+">: "+params.get(param)+(params.get(param)!=null?" ("+params.get(param).getClass().getName()+")":""));
    }

    ResourceBundle config = ResourceBundle.getBundle("config");

    PCancelarSolicitud service = new PCancelarSolicitud();
    PCancelarSolicitudSoapPort port = service.getPCancelarSolicitudSoapPort();
    
    BindingProvider bindingProvider = (BindingProvider) port;
    
    String urlServicio = config.getString("CANCELARSOLICITUD_SERVICIO");
    
    bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlServicio);
    
    ResultadoAccion res = new ResultadoAccion();
    PCancelarSolicitudExecute wsParams = new PCancelarSolicitudExecute();
    //SolicitudNumero
    ReservaDTO reserva = (ReservaDTO) params.get("RESERVA");
    if(reserva.getId()!=null) {
      wsParams.setSolicitudnumero(reserva.getId().toString());
    }else {
      res.addError("0", "Falta el dato <número de solicitud>");
    }

    if(res.isOK()) {
      try {
        if(urlServicio.startsWith("https")) {
          logger.info("["+invId+"] CancelarSolicitud - configurando seguridad... ");
          //Cargar el keystore
          KeyStore ts = KeyStore.getInstance( KeyStore.getDefaultType() );
          ts.load(new FileInputStream(config.getString("KEYSTORE_ARCHIVO")), config.getString("KEYSTORE_PASSWORD").toCharArray() );
          //Crear un trust manager que use el keystore
          TrustManagerFactory tmf = TrustManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
          tmf.init( ts );
          //Configurar los parámetros TLS
          SSLContext sslContext = SSLContext.getInstance("SSL");
          sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
          TLSClientParameters tlsParams = new TLSClientParameters();
          tlsParams.setSSLSocketFactory(sslContext.getSocketFactory());
          if(config.containsKey("DESHABILITAR_VERIFICACION_HOSTNAME") && "true".equalsIgnoreCase(config.getString("DESHABILITAR_VERIFICACION_HOSTNAME"))) {
            logger.debug("["+invId+"] CancelarSolicitud - deshabilitando verificación del hostname...");
            tlsParams.setDisableCNCheck(true);
          }
          //Crear un cliente CXF para el puerto
          Client client = ClientProxy.getClient(port);
          //Configurar el cliente para que use los parámetros TLS  
          HTTPConduit conduit = (HTTPConduit) client.getConduit();
          conduit.setTlsClientParameters(tlsParams);
          logger.info("["+invId+"] CancelarSolicitud - seguridad configurada, invocando servicio... ");
        }
        //Invocar el servicio
        PCancelarSolicitudExecuteResponse resp = port.execute(wsParams);
        logger.info("["+invId+"] CancelarSolicitud - servicio invocado, resultado: "+resp.getRetval());
      }catch(Exception ex) {
        res.addError("0", ex.getMessage());
      }
    }else {
      logger.info("["+invId+"] CancelarSolicitud - no se invoca al servicio porque hay errores en los parámetros");
      for(ErrorAccion error : res.getErrores()) {
        logger.debug("["+invId+"] CancelarSolicitud error: "+error.getMensaje());
      }
    }
    
		return res;
	}
	
  
}
