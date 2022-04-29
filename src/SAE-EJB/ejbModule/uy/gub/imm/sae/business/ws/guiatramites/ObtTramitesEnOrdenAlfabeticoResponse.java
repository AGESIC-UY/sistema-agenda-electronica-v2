
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
 *         &lt;element name="obtTramitesEnOrdenAlfabeticoResult" type="{http://tempuri.org/}obtTramitesEnOrdenAlfabeticoResponseType" minOccurs="0"/>
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
    "obtTramitesEnOrdenAlfabeticoResult"
})
@XmlRootElement(name = "obtTramitesEnOrdenAlfabeticoResponse")
public class ObtTramitesEnOrdenAlfabeticoResponse {

    protected ObtTramitesEnOrdenAlfabeticoResponseType obtTramitesEnOrdenAlfabeticoResult;

    /**
     * Gets the value of the obtTramitesEnOrdenAlfabeticoResult property.
     * 
     * @return
     *     possible object is
     *     {@link ObtTramitesEnOrdenAlfabeticoResponseType }
     *     
     */
    public ObtTramitesEnOrdenAlfabeticoResponseType getObtTramitesEnOrdenAlfabeticoResult() {
        return obtTramitesEnOrdenAlfabeticoResult;
    }

    /**
     * Sets the value of the obtTramitesEnOrdenAlfabeticoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObtTramitesEnOrdenAlfabeticoResponseType }
     *     
     */
    public void setObtTramitesEnOrdenAlfabeticoResult(ObtTramitesEnOrdenAlfabeticoResponseType value) {
        this.obtTramitesEnOrdenAlfabeticoResult = value;
    }

}
