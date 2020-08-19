
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
 *         &lt;element name="GrabarReservaWebResult" type="{http://tempuri.org/}Reserva" minOccurs="0"/>
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
    "grabarReservaWebResult"
})
@XmlRootElement(name = "GrabarReservaWebResponse")
public class GrabarReservaWebResponse {

    @XmlElement(name = "GrabarReservaWebResult")
    protected Reserva grabarReservaWebResult;

    /**
     * Obtiene el valor de la propiedad grabarReservaWebResult.
     * 
     * @return
     *     possible object is
     *     {@link Reserva }
     *     
     */
    public Reserva getGrabarReservaWebResult() {
        return grabarReservaWebResult;
    }

    /**
     * Define el valor de la propiedad grabarReservaWebResult.
     * 
     * @param value
     *     allowed object is
     *     {@link Reserva }
     *     
     */
    public void setGrabarReservaWebResult(Reserva value) {
        this.grabarReservaWebResult = value;
    }

}
