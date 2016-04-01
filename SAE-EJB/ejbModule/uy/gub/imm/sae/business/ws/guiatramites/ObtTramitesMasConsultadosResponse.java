
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
 *         &lt;element name="obtTramitesMasConsultadosResult" type="{http://tempuri.org/}obtTramitesMasConsultadosResponseType" minOccurs="0"/>
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
    "obtTramitesMasConsultadosResult"
})
@XmlRootElement(name = "obtTramitesMasConsultadosResponse")
public class ObtTramitesMasConsultadosResponse {

    protected ObtTramitesMasConsultadosResponseType obtTramitesMasConsultadosResult;

    /**
     * Gets the value of the obtTramitesMasConsultadosResult property.
     * 
     * @return
     *     possible object is
     *     {@link ObtTramitesMasConsultadosResponseType }
     *     
     */
    public ObtTramitesMasConsultadosResponseType getObtTramitesMasConsultadosResult() {
        return obtTramitesMasConsultadosResult;
    }

    /**
     * Sets the value of the obtTramitesMasConsultadosResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObtTramitesMasConsultadosResponseType }
     *     
     */
    public void setObtTramitesMasConsultadosResult(ObtTramitesMasConsultadosResponseType value) {
        this.obtTramitesMasConsultadosResult = value;
    }

}
