
package uy.com.sofis.mrree.paltasolicitud;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 3.1.7
 * 2016-10-04T18:29:38.160-03:00
 * Generated source version: 3.1.7
 * 
 */
public final class PAltaSolicitudSoapPort_PAltaSolicitudSoapPort_Client {

    private static final QName SERVICE_NAME = new QName("TramitesExterior", "PAltaSolicitud");

    private PAltaSolicitudSoapPort_PAltaSolicitudSoapPort_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = PAltaSolicitud.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        PAltaSolicitud ss = new PAltaSolicitud(wsdlURL, SERVICE_NAME);
        PAltaSolicitudSoapPort port = ss.getPAltaSolicitudSoapPort();  
        
        {
        System.out.println("Invoking execute...");
        uy.com.sofis.mrree.paltasolicitud.PAltaSolicitudExecute _execute_parameters = new uy.com.sofis.mrree.paltasolicitud.PAltaSolicitudExecute();
        _execute_parameters.setSolicitudnumero("Solicitudnumero1727774581");
        _execute_parameters.setFechaSolicitud("FechaSolicitud-2030394338");
        _execute_parameters.setSolicitudnumerodocumento(3254437177705580800l);
        _execute_parameters.setFechaNac("FechaNac-223200789");
        _execute_parameters.setSolicitudprimernombre("Solicitudprimernombre1458496703");
        _execute_parameters.setSolicitudsegundonombre("Solicitudsegundonombre-312094323");
        _execute_parameters.setSolicitudprimerapellido("Solicitudprimerapellido567334049");
        _execute_parameters.setSolicitudsegundoapellido("Solicitudsegundoapellido-1240771712");
        _execute_parameters.setSolicitudemail("Solicitudemail-1059603506");
        _execute_parameters.setSolicitudtelefono("Solicitudtelefono1617223644");
        _execute_parameters.setSolicitudanioemigracion((short)19342);
        _execute_parameters.setSolicituddptouruguay("Solicituddptouruguay873924082");
        _execute_parameters.setSolicitudnombremadre("Solicitudnombremadre924826930");
        _execute_parameters.setSolicitudnombrepadre("Solicitudnombrepadre-315908276");
        _execute_parameters.setTramite("Tramite-1995772429");
        _execute_parameters.setConsulado("Consulado1204272643");
        _execute_parameters.setSolicitudnumerollamado(4936924072978526536l);
        uy.com.sofis.mrree.paltasolicitud.PAltaSolicitudExecuteResponse _execute__return = port.execute(_execute_parameters);
        System.out.println("execute.result=" + _execute__return);


        }

        System.exit(0);
    }

}
