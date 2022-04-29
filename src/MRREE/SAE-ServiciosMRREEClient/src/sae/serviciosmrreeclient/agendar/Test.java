/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sae.serviciosmrreeclient.agendar;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import uy.com.sofis.sae.mrree.altasolicitud.PAltaSolicitud;
import uy.com.sofis.sae.mrree.altasolicitud.PAltaSolicitudExecute;
import uy.com.sofis.sae.mrree.altasolicitud.PAltaSolicitudSoapPort;

/**
 *
 * @author spio
 */
public class Test {
  
  public static void main(String[] args) {
    
    QName qname = new QName("TramitesExterior", "PAltaSolicitud");
    PAltaSolicitud service = new PAltaSolicitud(null, qname); // null for ignore WSDL
    PAltaSolicitudSoapPort port = service.getPAltaSolicitudSoapPort();
    BindingProvider bindingProvider = (BindingProvider) port;
    bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://agenda2.sofis.com.uy:13002/SAE-ServiciosMRREEStubs/PAltaSolicitud");

    port.execute(new PAltaSolicitudExecute());
    
  }
  
}
