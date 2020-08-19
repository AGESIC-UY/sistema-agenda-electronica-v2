
package com.sofis.agesic.ursea.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArrayOfLugDisponibles complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfLugDisponibles">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LugDisponibles" type="{http://tempuri.org/}LugDisponibles" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfLugDisponibles", propOrder = {
    "lugDisponibles"
})
public class ArrayOfLugDisponibles {

    @XmlElement(name = "LugDisponibles", nillable = true)
    protected List<LugDisponibles> lugDisponibles;

    /**
     * Gets the value of the lugDisponibles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lugDisponibles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLugDisponibles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LugDisponibles }
     * 
     * 
     */
    public List<LugDisponibles> getLugDisponibles() {
        if (lugDisponibles == null) {
            lugDisponibles = new ArrayList<LugDisponibles>();
        }
        return this.lugDisponibles;
    }

}
