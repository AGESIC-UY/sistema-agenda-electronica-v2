
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
 *         &lt;element name="obtTramitePorIdResult" type="{http://tempuri.org/}obtTramitesPorIdResponse" minOccurs="0"/>
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
    "obtTramitePorIdResult"
})
@XmlRootElement(name = "obtTramitePorIdResponse")
public class ObtTramitePorIdResponse {

    protected ObtTramitesPorIdResponse obtTramitePorIdResult;

    /**
     * Gets the value of the obtTramitePorIdResult property.
     * 
     * @return
     *     possible object is
     *     {@link ObtTramitesPorIdResponse }
     *     
     */
    public ObtTramitesPorIdResponse getObtTramitePorIdResult() {
        return obtTramitePorIdResult;
    }

    /**
     * Sets the value of the obtTramitePorIdResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObtTramitesPorIdResponse }
     *     
     */
    public void setObtTramitePorIdResult(ObtTramitesPorIdResponse value) {
        this.obtTramitePorIdResult = value;
    }

}
