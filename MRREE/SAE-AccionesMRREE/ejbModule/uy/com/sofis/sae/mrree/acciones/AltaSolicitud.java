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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

import uy.com.sofis.mrree.paltasolicitud.PAltaSolicitud;
import uy.com.sofis.mrree.paltasolicitud.PAltaSolicitudExecute;
import uy.com.sofis.mrree.paltasolicitud.PAltaSolicitudExecuteResponse;
import uy.com.sofis.mrree.paltasolicitud.PAltaSolicitudSoapPort;
import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.sae.acciones.business.dto.RecursoDTO;
import uy.gub.sae.acciones.business.ejb.EjecutorAccionRemote;
import uy.gub.sae.acciones.business.ejb.ErrorAccion;
import uy.gub.sae.acciones.business.ejb.ResultadoAccion;
import uy.gub.sae.acciones.business.ejb.exception.InvalidParametersException;
import uy.gub.sae.acciones.business.ejb.exception.UnexpectedAccionException;

/**
 * Parámetros:
 *   SolicitudNumeroDocumento
 *   FechaNac
 *   SolicitudPrimerNombre
 *   SolicitudSegundoNombre
 *   SolicitudPrimerApellido
 *   SolicitudSegundoApellido
 *   SolicitudEmail
 *   SolicitudTelefono
 *   SolicitudAnioEmigracion
 *   SolicitudDptoUruguay
 *   SolicitudNombreMadre
 *   SolicitudNombrePadre
 *   Tramite
 *   Consulado
 * @author spio
 *
 */

@Stateless
public class AltaSolicitud implements EjecutorAccionRemote {
  
  Logger logger = Logger.getLogger(AltaSolicitud.class);

	@Override
	public ResultadoAccion ejecutar(String nombreAccion, Map<String, Object> params)
			throws UnexpectedAccionException, InvalidParametersException {

	  long invId = (new Date()).getTime();
	  
    logger.info("["+invId+"] AltaSolicitud - invocación recibida");
    
    for(String param : params.keySet()) {
      logger.debug("["+invId+"] AltaSolicitud - parámetro <"+param+">: "+params.get(param)+(params.get(param)!=null?" ("+params.get(param).getClass().getName()+")":""));
    }
		
    ReservaDTO reserva = (ReservaDTO) params.get("RESERVA");
    
    RecursoDTO recurso = (RecursoDTO) params.get("RECURSO");
    
    logger.debug("["+invId+"] AltaSolicitud - Trámite: "+reserva.getTramiteCodigo()+"/"+reserva.getTramiteNombre());
    
    ResourceBundle config = ResourceBundle.getBundle("config");
    
    PAltaSolicitud service = new PAltaSolicitud();
    PAltaSolicitudSoapPort port = service.getPAltaSolicitudSoapPort();

    BindingProvider bindingProvider = (BindingProvider) port;
    
    String urlServicio = config.getString("ALTASOLICITUD_SERVICIO");
    
    bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlServicio);
    
    ResultadoAccion res = new ResultadoAccion();
    PAltaSolicitudExecute wsParams = new PAltaSolicitudExecute();
    //Consulado
    if(esValido(recurso.getAgendaDescripcion())) {
      logger.debug("["+invId+"] AltaSolicitud Parámetro 'Consulado': "+recurso.getAgendaDescripcion());
      wsParams.setConsulado(recurso.getAgendaDescripcion());
    }else {
      res.addError("0", "Falta el dato <consulado>");
    }
    //Trámite
    if(esValido(recurso.getDescripcion())) {
      logger.debug("["+invId+"] AltaSolicitud Parámetro 'Tramite': "+recurso.getDescripcion());
      wsParams.setTramite(recurso.getDescripcion());
    }else {
      res.addError("0", "Falta el dato <trámite>");
    }
    //SolicitudNumero
    if(reserva.getId()!=null) {
      logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudnumero': "+reserva.getId().toString());
      wsParams.setSolicitudnumero(reserva.getId().toString());
    }else {
      res.addError("0", "Falta el dato <número de solicitud>");
    }
    //FechaSolicitud
    if(reserva.getHoraInicio()!=null) {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
      logger.debug("["+invId+"] AltaSolicitud Parámetro 'FechaSolicitud': "+df.format(reserva.getHoraInicio()));
      wsParams.setFechaSolicitud(df.format(reserva.getHoraInicio()));
    }else {
      res.addError("0", "Falta el dato <fecha de solicitud>");
    }
    //SolicitudNumeroDocumento
    try {
      String solicitudnumerodocumento = (String) params.get("SolicitudNumeroDocumento");
      if(esValido(solicitudnumerodocumento)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudnumerodocumento': "+solicitudnumerodocumento);
        wsParams.setSolicitudnumerodocumento(Long.valueOf(solicitudnumerodocumento));
      }else {
        res.addError("0", "Falta el dato <número de documento>");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <número de documento> no es válido");
    }
    //FechaNac
    Object oFechaNac = params.get("FechaNac");
    if(oFechaNac==null) {
      res.addError("0", "Falta el dato <fecha de nacimiento>");
    }else {
      if(oFechaNac instanceof Date) {
        Date fechaNac = (Date) oFechaNac;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'FechaNac': "+df.format(fechaNac));
        wsParams.setFechaNac(df.format(fechaNac));
      }else if(oFechaNac instanceof String) {
        try {
          DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
          Date fechaNac = df.parse(oFechaNac.toString());
          df = new SimpleDateFormat("dd/MM/yyyy");
          logger.debug("["+invId+"] AltaSolicitud Parámetro 'FechaNac': "+df.format(fechaNac));
          wsParams.setFechaNac(df.format(fechaNac));
        }catch(Exception ex1) {
          res.addError("0", "El dato <fecha de nacimiento> no es válido");
        }
      }else {
        res.addError("0", "El dato <fecha de nacimiento> no es válido");
      }
    }
    //SolicitudPrimerNombre
    try {
      String solicitudprimernombre = (String) params.get("SolicitudPrimerNombre");
      if(esValido(solicitudprimernombre)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudprimernombre': "+solicitudprimernombre);
        wsParams.setSolicitudprimernombre(solicitudprimernombre);
      }else {
        res.addError("0", "Falta el dato <primer nombre>");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <primer nombre> no es válido");
    }
    //SolicitudSegundoNombre
    try {
      String solicitudsegundonombre = (String) params.get("SolicitudSegundoNombre");
      if(esValido(solicitudsegundonombre)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudsegundonombre': "+solicitudsegundonombre);
        wsParams.setSolicitudsegundonombre(solicitudsegundonombre);
      }else {
        wsParams.setSolicitudsegundonombre("");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <segundo nombre>");
    }
    //SolicitudPrimerApellido
    try {
      String solicitudprimerapellido = (String) params.get("SolicitudPrimerApellido");
      if(esValido(solicitudprimerapellido)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudprimerapellido': "+solicitudprimerapellido);
        wsParams.setSolicitudprimerapellido(solicitudprimerapellido);
      }else {
        res.addError("0", "Falta el dato <primer apellido>");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <primer apellido> no es válido");
    }
    //SolicitudSegundoApellido
    try {
      String solicitudsegundoapellido = (String) params.get("SolicitudSegundoApellido");
      if(esValido(solicitudsegundoapellido)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudsegundoapellido': "+solicitudsegundoapellido);
        wsParams.setSolicitudsegundoapellido(solicitudsegundoapellido);
      }else {
        wsParams.setSolicitudsegundoapellido("");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <segundo apellido> no es válido");
    }
    //Solicitudemail
    try {
      String solicitudemail = (String) params.get("SolicitudEmail");
      if(esValido(solicitudemail)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudemail': "+solicitudemail);
        wsParams.setSolicitudemail(solicitudemail);
      }else {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudemail': ''");
        wsParams.setSolicitudemail("");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <email> no es válido");
    }
    //SolicitudTelefono
    try {
      String solicitudtelefono = (String) params.get("SolicitudTelefono");
      if(esValido(solicitudtelefono)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudtelefono': "+solicitudtelefono);
        wsParams.setSolicitudtelefono(solicitudtelefono);
      }else {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudtelefono': ''");
        wsParams.setSolicitudtelefono("");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <teléfono> no es válido");
    }
    //SolicitudAnioEmigracion
    Object oSolicitudAnioEmigracion = params.get("SolicitudAnioEmigracion");
    if(oSolicitudAnioEmigracion==null) {
      wsParams.setSolicitudanioemigracion((short)0);
    }else if(oSolicitudAnioEmigracion instanceof Integer) {
      Integer solicitudanioemigracion = (Integer) oSolicitudAnioEmigracion;
      logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudanioemigracion': "+solicitudanioemigracion);
      wsParams.setSolicitudanioemigracion(solicitudanioemigracion.shortValue());
    }else if(oSolicitudAnioEmigracion instanceof String) {
      try {
        Integer solicitudanioemigracion = Integer.valueOf(oSolicitudAnioEmigracion.toString());
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudanioemigracion': "+solicitudanioemigracion);
        wsParams.setSolicitudanioemigracion(solicitudanioemigracion.shortValue());
      }catch(Exception ex) {
        res.addError("0", "El dato <año de emigración> no es válido");
      }
    }else {
      res.addError("0", "El dato <año de emigración> no es válido");
    }
    //SolicitudDptoUruguay
    try {
      String solicituddptouruguay = (String) params.get("SolicitudDptoUruguay");
      if(esValido(solicituddptouruguay)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicituddptouruguay': "+solicituddptouruguay);
        wsParams.setSolicituddptouruguay(solicituddptouruguay);
      }else {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicituddptouruguay': ''");
        wsParams.setSolicituddptouruguay("");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <departamento> no es válido");
    }
    //SolicitudNombreMadre
    try {
      String solicitudnombremadre = (String) params.get("SolicitudNombreMadre");
      if(esValido(solicitudnombremadre)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudnombremadre': "+solicitudnombremadre);
        wsParams.setSolicitudnombremadre(solicitudnombremadre);
      }else {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudnombremadre': ''");
        wsParams.setSolicitudnombremadre("");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <nombre de la madre> no es válido");
    }
    //SolicitudNombrePadre
    try {
      String solicitudnombrepadre = (String) params.get("SolicitudNombrePadre");
      if(esValido(solicitudnombrepadre)) {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudnombrepadre': "+solicitudnombrepadre);
        wsParams.setSolicitudnombrepadre(solicitudnombrepadre);
      }else {
        logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudnombrepadre': ''");
        wsParams.setSolicitudnombrepadre("");
      }
    }catch(Exception ex) {
      res.addError("0", "El dato <nombre del padre> no es válido");
    }
    //SolicitudNumeroLlamado
    if(reserva.getNumero()!=null) {
      logger.debug("["+invId+"] AltaSolicitud Parámetro 'Solicitudnumerollamado': "+reserva.getNumero().longValue());
      wsParams.setSolicitudnumerollamado(reserva.getNumero().longValue());
    }else {
      res.addError("0", "Falta el dato <número de reserva>");
    }

    if(res.isOK()) {
      try {
        if(urlServicio.startsWith("https")) {
          logger.info("["+invId+"] AltaSolicitud - configurando seguridad... ");
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
            logger.debug("["+invId+"] AltaSolicitud - deshabilitando verificación del hostname...");
            tlsParams.setDisableCNCheck(true);
          }
          //Crear un cliente CXF para el puerto
          Client client = ClientProxy.getClient(port);
          //Configurar el cliente para que use los parámetros TLS  
          HTTPConduit conduit = (HTTPConduit) client.getConduit();
          conduit.setTlsClientParameters(tlsParams);
        }
        //Invocar el servicio
        logger.info("["+invId+"] AltaSolicitud - seguridad configurada, invocando servicio... ");
        PAltaSolicitudExecuteResponse resp = port.execute(wsParams);
        if(resp.getRetval()==null || resp.getRetval().trim().isEmpty()) {
          //Si no hay respuesta se asume que la invocación fue correcta
          logger.info("["+invId+"] AltaSolicitud - servicio invocado, resultado: "+resp.getRetval());
        }else {
          //Si hay respuesta debe ser un mensaje de error
          res.addError("0", resp.getRetval());
        }
      }catch(Exception ex) {
        logger.info("["+invId+"] AltaSolicitud - no se pudo invocar al servicio web!! ", ex);
        res.addError("0", ex.getMessage());
      }
    }else {
      logger.info("["+invId+"] AltaSolicitud - no se invoca al servicio porque hay errores en los parámetros");
      for(ErrorAccion error : res.getErrores()) {
        logger.debug("["+invId+"] AltaSolicitud error: "+error.getMensaje());
      }
    }
		return res;
	}
	

	private boolean esValido(String campo) {
	  return campo!=null && !campo.trim().isEmpty();
	}
	

}
