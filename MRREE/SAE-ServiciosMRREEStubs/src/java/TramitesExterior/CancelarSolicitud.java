/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TramitesExterior;

import javax.jws.WebService;
import tramitesexterior.PCancelarSolicitudExecute;
import tramitesexterior.PCancelarSolicitudExecuteResponse;

/**
 *
 * @author spio
 */
@WebService(serviceName = "PCancelarSolicitud", portName = "PCancelarSolicitudSoapPort", endpointInterface = "tramitesexterior.PCancelarSolicitudSoapPort", targetNamespace = "TramitesExterior", wsdlLocation = "WEB-INF/wsdl/NewWebServiceFromWSDL/apcancelarsolicitud.wsdl")
public class CancelarSolicitud {

  //https://agenda2.sofis.com.uy:13002/SAE-ServiciosMRREEStubs/PCancelarSolicitud?wsdl
  
  public tramitesexterior.PCancelarSolicitudExecuteResponse execute(PCancelarSolicitudExecute parameters) {

    System.out.println("[CancelarSolicitud] Invocación recibida");
    System.out.println("[CancelarSolicitud] getSolicitudnumero="+parameters.getSolicitudnumero());
    
    PCancelarSolicitudExecuteResponse resp = new PCancelarSolicitudExecuteResponse();
    int n = Integer.valueOf(parameters.getSolicitudnumero().substring(parameters.getSolicitudnumero().length()-1));
    if(n % 2 == 0) {
      resp.setRetval("No se permite cancelar la reserva");
    }else {
      resp.setRetval("");
    }
    return resp;
  }
  
}
