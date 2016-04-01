
package uy.gub.imm.sae.business.ws.guiatramites;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfOfNacionalDatos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfOfNacionalDatos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OfNacionalDatos" type="{http://tempuri.org/}OfNacionalDatos" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfOfNacionalDatos", propOrder = {
    "ofNacionalDatos"
})
public class ArrayOfOfNacionalDatos {

    @XmlElement(name = "OfNacionalDatos", nillable = true)
    protected List<OfNacionalDatos> ofNacionalDatos;

    /**
     * Gets the value of the ofNacionalDatos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ofNacionalDatos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOfNacionalDatos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OfNacionalDatos }
     * 
     * 
     */
    public List<OfNacionalDatos> getOfNacionalDatos() {
        if (ofNacionalDatos == null) {
            ofNacionalDatos = new ArrayList<OfNacionalDatos>();
        }
        return this.ofNacionalDatos;
    }

}
