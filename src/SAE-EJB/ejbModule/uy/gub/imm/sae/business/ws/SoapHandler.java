/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.imm.sae.business.ws;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;

/**
 *
 * @author spio
 */
public class SoapHandler implements SOAPHandler<SOAPMessageContext> {

  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    Logger.getLogger("sae.ws").trace("=========================== PROPIEDADES ==================================");
    for(String k : context.keySet()) {
      Logger.getLogger("sae.ws").trace(k+" ==> "+context.get(k));
    }
    Logger.getLogger("sae.ws").trace("=========================== PROPIEDADES ==================================");

    Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    //if this is a request, true for outbound messages, false for inbound
    try  {
      SOAPMessage soapMsg = context.getMessage();
      SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();
      //if no header, add one
      if (soapHeader == null) {
        soapHeader = soapEnv.addHeader();
      }

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      soapMsg.writeTo(stream);
      String message = new String(stream.toByteArray(), "utf-8");
      
      if (isRequest) {
        Logger.getLogger("sae.ws").debug("=========================== ENVIADO ==================================");
        Logger.getLogger("sae.ws").debug(message);
        Logger.getLogger("sae.ws").debug("=========================== ******* ==================================");
      } else
      {
        Logger.getLogger("sae.ws").debug("=========================== RECIBIDO ==================================");
        Logger.getLogger("sae.ws").debug(message);
        Logger.getLogger("sae.ws").debug("=========================== ******* ==================================");
      }
    } catch (Exception e) {
      Logger.getLogger("sae.ws").error("=========================== ERROR ==================================");
      Logger.getLogger("sae.ws").error(e);
      Logger.getLogger("sae.ws").error("=========================== ERROR ==================================");
    }

    //continue other handler chain
    return true;
  }

  @Override
  public boolean handleFault(SOAPMessageContext context) {
    try {
      SOAPMessage soapMsg = context.getMessage();
      SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
      SOAPHeader soapHeader = soapEnv.getHeader();
      //if no header, add one
      if (soapHeader == null) {
        soapHeader = soapEnv.addHeader();
      }
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      soapMsg.writeTo(stream);
      String message = new String(stream.toByteArray(), "utf-8");
      Logger.getLogger("sae.ws").debug("=========================== FAULT ==================================");
      Logger.getLogger("sae.ws").debug(message);
      Logger.getLogger("sae.ws").debug("=========================== ******* ==================================");
    } catch (Exception e) {
      Logger.getLogger("sae.ws").error("=========================== FAULT ==================================");
      Logger.getLogger("sae.ws").error(e);
      Logger.getLogger("sae.ws").error("=========================== ******* ==================================");
    }
    return true;
  }

  @Override
  public void close(MessageContext context)
  {
  }

  @Override
  public Set<QName> getHeaders()
  {
    return null;
  }
}
