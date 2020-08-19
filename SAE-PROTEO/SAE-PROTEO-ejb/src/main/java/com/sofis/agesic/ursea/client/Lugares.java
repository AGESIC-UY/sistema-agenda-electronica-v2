
package com.sofis.agesic.ursea.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Lugares complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Lugares">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listLugaresDisponibles" type="{http://tempuri.org/}ArrayOfLugDisponibles" minOccurs="0"/>
 *         &lt;element name="MsgError" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Lugares", propOrder = {
    "listLugaresDisponibles",
    "msgError"
})
public class Lugares {

    protected ArrayOfLugDisponibles listLugaresDisponibles;
    @XmlElement(name = "MsgError")
    protected String msgError;

    /**
     * Obtiene el valor de la propiedad listLugaresDisponibles.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLugDisponibles }
     *     
     */
    public ArrayOfLugDisponibles getListLugaresDisponibles() {
        return listLugaresDisponibles;
    }

    /**
     * Define el valor de la propiedad listLugaresDisponibles.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLugDisponibles }
     *     
     */
    public void setListLugaresDisponibles(ArrayOfLugDisponibles value) {
        this.listLugaresDisponibles = value;
    }

    /**
     * Obtiene el valor de la propiedad msgError.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgError() {
        return msgError;
    }

    /**
     * Define el valor de la propiedad msgError.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgError(String value) {
        this.msgError = value;
    }

}
