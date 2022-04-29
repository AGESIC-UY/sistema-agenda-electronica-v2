
package uy.gub.imm.sae.business.ws.wstramite;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="ObtenerOrganismosResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "obtenerOrganismosResult"
})
@XmlRootElement(name = "ObtenerOrganismosResponse")
public class ObtenerOrganismosResponse {

    @XmlElement(name = "ObtenerOrganismosResult")
    protected String obtenerOrganismosResult;

    /**
     * Gets the value of the obtenerOrganismosResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObtenerOrganismosResult() {
        return obtenerOrganismosResult;
    }

    /**
     * Sets the value of the obtenerOrganismosResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObtenerOrganismosResult(String value) {
        this.obtenerOrganismosResult = value;
    }

}
