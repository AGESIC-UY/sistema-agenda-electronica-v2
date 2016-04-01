
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="obtTramitePorIdSinContarVisitaResult" type="{http://tempuri.org/}obtTramitesPorIdResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "obtTramitePorIdSinContarVisitaResult"
})
@XmlRootElement(name = "obtTramitePorIdSinContarVisitaResponse")
public class ObtTramitePorIdSinContarVisitaResponse {

    protected ObtTramitesPorIdResponse obtTramitePorIdSinContarVisitaResult;

    /**
     * Gets the value of the obtTramitePorIdSinContarVisitaResult property.
     * 
     * @return
     *     possible object is
     *     {@link ObtTramitesPorIdResponse }
     *     
     */
    public ObtTramitesPorIdResponse getObtTramitePorIdSinContarVisitaResult() {
        return obtTramitePorIdSinContarVisitaResult;
    }

    /**
     * Sets the value of the obtTramitePorIdSinContarVisitaResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObtTramitesPorIdResponse }
     *     
     */
    public void setObtTramitePorIdSinContarVisitaResult(ObtTramitesPorIdResponse value) {
        this.obtTramitePorIdSinContarVisitaResult = value;
    }

}
