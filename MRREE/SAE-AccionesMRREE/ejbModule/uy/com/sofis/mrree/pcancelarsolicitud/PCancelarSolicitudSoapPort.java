package uy.com.sofis.mrree.pcancelarsolicitud;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.1.7
 * 2016-10-05T12:57:30.342-03:00
 * Generated source version: 3.1.7
 * 
 */
@WebService(targetNamespace = "TramitesExterior", name = "PCancelarSolicitudSoapPort")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface PCancelarSolicitudSoapPort {

    @WebMethod(operationName = "Execute", action = "TramitesExterioraction/APCANCELARSOLICITUD.Execute")
    @WebResult(name = "PCancelarSolicitud.ExecuteResponse", targetNamespace = "TramitesExterior", partName = "parameters")
    public PCancelarSolicitudExecuteResponse execute(
        @WebParam(partName = "parameters", name = "PCancelarSolicitud.Execute", targetNamespace = "TramitesExterior")
        PCancelarSolicitudExecute parameters
    );
}
