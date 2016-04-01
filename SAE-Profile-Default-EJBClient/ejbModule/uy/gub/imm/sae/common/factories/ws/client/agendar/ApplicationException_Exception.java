
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebFault(name = "ApplicationException", targetNamespace = "http://montevideo.gub.uy/schema/sae/1.0/")
public class ApplicationException_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private ApplicationException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public ApplicationException_Exception(String message, ApplicationException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ApplicationException_Exception(String message, ApplicationException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: uy.gub.imm.sae.common.factories.ws.client.agendar.ApplicationException
     */
    public ApplicationException getFaultInfo() {
        return faultInfo;
    }

}
