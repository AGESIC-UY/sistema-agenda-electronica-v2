
package uy.gub.imm.sae.business.ws.guiatramites;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfOfExteriorDatos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfOfExteriorDatos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OfExteriorDatos" type="{http://tempuri.org/}OfExteriorDatos" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfOfExteriorDatos", propOrder = {
    "ofExteriorDatos"
})
public class ArrayOfOfExteriorDatos {

    @XmlElement(name = "OfExteriorDatos", nillable = true)
    protected List<OfExteriorDatos> ofExteriorDatos;

    /**
     * Gets the value of the ofExteriorDatos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ofExteriorDatos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOfExteriorDatos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OfExteriorDatos }
     * 
     * 
     */
    public List<OfExteriorDatos> getOfExteriorDatos() {
        if (ofExteriorDatos == null) {
            ofExteriorDatos = new ArrayList<OfExteriorDatos>();
        }
        return this.ofExteriorDatos;
    }

}
