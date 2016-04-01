
package uy.gub.imm.sae.business.ws.guiatramites;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfNormativaDatos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfNormativaDatos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NormativaDatos" type="{http://tempuri.org/}NormativaDatos" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfNormativaDatos", propOrder = {
    "normativaDatos"
})
public class ArrayOfNormativaDatos {

    @XmlElement(name = "NormativaDatos", nillable = true)
    protected List<NormativaDatos> normativaDatos;

    /**
     * Gets the value of the normativaDatos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the normativaDatos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNormativaDatos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NormativaDatos }
     * 
     * 
     */
    public List<NormativaDatos> getNormativaDatos() {
        if (normativaDatos == null) {
            normativaDatos = new ArrayList<NormativaDatos>();
        }
        return this.normativaDatos;
    }

}
