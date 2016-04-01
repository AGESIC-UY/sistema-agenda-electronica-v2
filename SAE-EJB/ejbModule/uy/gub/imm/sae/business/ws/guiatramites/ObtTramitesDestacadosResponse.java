
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
 *         &lt;element name="obtTramitesDestacadosResult" type="{http://tempuri.org/}obtTramitesDestacadosResponseType" minOccurs="0"/>
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
    "obtTramitesDestacadosResult"
})
@XmlRootElement(name = "obtTramitesDestacadosResponse")
public class ObtTramitesDestacadosResponse {

    protected ObtTramitesDestacadosResponseType obtTramitesDestacadosResult;

    /**
     * Gets the value of the obtTramitesDestacadosResult property.
     * 
     * @return
     *     possible object is
     *     {@link ObtTramitesDestacadosResponseType }
     *     
     */
    public ObtTramitesDestacadosResponseType getObtTramitesDestacadosResult() {
        return obtTramitesDestacadosResult;
    }

    /**
     * Sets the value of the obtTramitesDestacadosResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObtTramitesDestacadosResponseType }
     *     
     */
    public void setObtTramitesDestacadosResult(ObtTramitesDestacadosResponseType value) {
        this.obtTramitesDestacadosResult = value;
    }

}
