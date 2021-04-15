/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TramitesExterior;

import javax.jws.WebService;
import tramitesexterior.PAltaSolicitudExecute;
import tramitesexterior.PAltaSolicitudExecuteResponse;

/**
 *
 * @author spio
 */
@WebService(serviceName = "PAltaSolicitud", portName = "PAltaSolicitudSoapPort", endpointInterface = "tramitesexterior.PAltaSolicitudSoapPort", targetNamespace = "TramitesExterior")
public class AltaSolicitud {

  //https://agenda2.sofis.com.uy:13002/SAE-ServiciosMRREEStubs/PAltaSolicitud
  
  public PAltaSolicitudExecuteResponse execute(PAltaSolicitudExecute parameters) {
    
    System.out.println("[AltaSolicitud] Invocación recibida");
    System.out.println("[AltaSolicitud] getConsulado="+parameters.getConsulado());
    System.out.println("[AltaSolicitud] getFechaNac="+parameters.getFechaNac());
    System.out.println("[AltaSolicitud] getFechaSolicitud="+parameters.getFechaSolicitud());
    System.out.println("[AltaSolicitud] getSolicituddptouruguay="+parameters.getSolicituddptouruguay());
    System.out.println("[AltaSolicitud] getSolicitudemail="+parameters.getSolicitudemail());
    System.out.println("[AltaSolicitud] getSolicitudnombremadre="+parameters.getSolicitudnombremadre());
    System.out.println("[AltaSolicitud] getSolicitudnombrepadre="+parameters.getSolicitudnombrepadre());
    System.out.println("[AltaSolicitud] getSolicitudnumero="+parameters.getSolicitudnumero());
    System.out.println("[AltaSolicitud] getSolicitudprimerapellido="+parameters.getSolicitudprimerapellido());
    System.out.println("[AltaSolicitud] getSolicitudprimernombre="+parameters.getSolicitudprimernombre());
    System.out.println("[AltaSolicitud] getSolicitudsegundoapellido="+parameters.getSolicitudsegundoapellido());
    System.out.println("[AltaSolicitud] getSolicitudsegundonombre="+parameters.getSolicitudsegundonombre());
    System.out.println("[AltaSolicitud] getSolicitudtelefono="+parameters.getSolicitudtelefono());
    System.out.println("[AltaSolicitud] getTramite="+parameters.getTramite());
    System.out.println("[AltaSolicitud] getSolicitudanioemigracion="+parameters.getSolicitudanioemigracion());
    System.out.println("[AltaSolicitud] getSolicitudnumerodocumento="+parameters.getSolicitudnumerodocumento());
    System.out.println("[AltaSolicitud] getSolicitudnumerollamado="+parameters.getSolicitudnumerollamado());

    PAltaSolicitudExecuteResponse resp = new PAltaSolicitudExecuteResponse();
    
    int n = Integer.valueOf(parameters.getSolicitudnumero().substring(parameters.getSolicitudnumero().length()-1));
    if(n % 2 == 0) {
      resp.setRetval("No se permite procesar la solicitud");
    }else {
      resp.setRetval("");
    }
    return resp;
    
  }
  
}
