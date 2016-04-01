
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
 *         &lt;element name="obtStatusPorIdTramiteResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "obtStatusPorIdTramiteResult"
})
@XmlRootElement(name = "obtStatusPorIdTramiteResponse")
public class ObtStatusPorIdTramiteResponse {

    protected String obtStatusPorIdTramiteResult;

    /**
     * Gets the value of the obtStatusPorIdTramiteResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObtStatusPorIdTramiteResult() {
        return obtStatusPorIdTramiteResult;
    }

    /**
     * Sets the value of the obtStatusPorIdTramiteResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObtStatusPorIdTramiteResult(String value) {
        this.obtStatusPorIdTramiteResult = value;
    }

}
