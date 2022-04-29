
package uy.gub.agesic.itramites.bruto.web.ws.cabezal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for persist complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="persist">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="traza" type="{http://ws.web.bruto.itramites.agesic.gub.uy/}cabezalDTO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "persist1", propOrder = {
    "traza"
})
public class Persist {

    protected CabezalDTO traza;

    /**
     * Gets the value of the traza property.
     * 
     * @return
     *     possible object is
     *     {@link CabezalDTO }
     *     
     */
    public CabezalDTO getTraza() {
        return traza;
    }

    /**
     * Sets the value of the traza property.
     * 
     * @param value
     *     allowed object is
     *     {@link CabezalDTO }
     *     
     */
    public void setTraza(CabezalDTO value) {
        this.traza = value;
    }

}
