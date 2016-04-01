
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
 *         &lt;element name="obtTramitesPorOrgEnOrdenAlfabeticoResult" type="{http://tempuri.org/}obtTramitesEnOrdenAlfabeticoResponseType" minOccurs="0"/>
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
    "obtTramitesPorOrgEnOrdenAlfabeticoResult"
})
@XmlRootElement(name = "obtTramitesPorOrgEnOrdenAlfabeticoResponse")
public class ObtTramitesPorOrgEnOrdenAlfabeticoResponse {

    protected ObtTramitesEnOrdenAlfabeticoResponseType obtTramitesPorOrgEnOrdenAlfabeticoResult;

    /**
     * Gets the value of the obtTramitesPorOrgEnOrdenAlfabeticoResult property.
     * 
     * @return
     *     possible object is
     *     {@link ObtTramitesEnOrdenAlfabeticoResponseType }
     *     
     */
    public ObtTramitesEnOrdenAlfabeticoResponseType getObtTramitesPorOrgEnOrdenAlfabeticoResult() {
        return obtTramitesPorOrgEnOrdenAlfabeticoResult;
    }

    /**
     * Sets the value of the obtTramitesPorOrgEnOrdenAlfabeticoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObtTramitesEnOrdenAlfabeticoResponseType }
     *     
     */
    public void setObtTramitesPorOrgEnOrdenAlfabeticoResult(ObtTramitesEnOrdenAlfabeticoResponseType value) {
        this.obtTramitesPorOrgEnOrdenAlfabeticoResult = value;
    }

}
