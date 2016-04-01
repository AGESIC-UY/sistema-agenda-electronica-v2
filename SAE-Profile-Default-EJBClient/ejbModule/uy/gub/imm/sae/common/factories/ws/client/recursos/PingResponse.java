
package uy.gub.imm.sae.common.factories.ws.client.recursos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pingResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pingResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pingResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pingResponse", propOrder = {
    "pingResult"
})
public class PingResponse {

    protected String pingResult;

    /**
     * Gets the value of the pingResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPingResult() {
        return pingResult;
    }

    /**
     * Sets the value of the pingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPingResult(String value) {
        this.pingResult = value;
    }

}
