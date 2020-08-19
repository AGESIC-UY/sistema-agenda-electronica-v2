
package com.sofis.agesic.ursea.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="LugaresDisponiblesResult" type="{http://tempuri.org/}Lugares" minOccurs="0"/>
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
    "lugaresDisponiblesResult"
})
@XmlRootElement(name = "LugaresDisponiblesResponse")
public class LugaresDisponiblesResponse {

    @XmlElement(name = "LugaresDisponiblesResult")
    protected Lugares lugaresDisponiblesResult;

    /**
     * Obtiene el valor de la propiedad lugaresDisponiblesResult.
     * 
     * @return
     *     possible object is
     *     {@link Lugares }
     *     
     */
    public Lugares getLugaresDisponiblesResult() {
        return lugaresDisponiblesResult;
    }

    /**
     * Define el valor de la propiedad lugaresDisponiblesResult.
     * 
     * @param value
     *     allowed object is
     *     {@link Lugares }
     *     
     */
    public void setLugaresDisponiblesResult(Lugares value) {
        this.lugaresDisponiblesResult = value;
    }

}
