
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
 *         &lt;element name="obtArbolTemasResult" type="{http://tempuri.org/}obtArbolTemasResponseType" minOccurs="0"/>
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
    "obtArbolTemasResult"
})
@XmlRootElement(name = "obtArbolTemasResponse")
public class ObtArbolTemasResponse {

    protected ObtArbolTemasResponseType obtArbolTemasResult;

    /**
     * Gets the value of the obtArbolTemasResult property.
     * 
     * @return
     *     possible object is
     *     {@link ObtArbolTemasResponseType }
     *     
     */
    public ObtArbolTemasResponseType getObtArbolTemasResult() {
        return obtArbolTemasResult;
    }

    /**
     * Sets the value of the obtArbolTemasResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObtArbolTemasResponseType }
     *     
     */
    public void setObtArbolTemasResult(ObtArbolTemasResponseType value) {
        this.obtArbolTemasResult = value;
    }

}
