
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Mensaje complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Mensaje">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CodMensaje" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Descripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DatoExtra" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Mensaje", propOrder = {
    "codMensaje",
    "descripcion",
    "datoExtra"
})
public class Mensaje {

    @XmlElement(name = "CodMensaje")
    protected String codMensaje;
    @XmlElement(name = "Descripcion")
    protected String descripcion;
    @XmlElement(name = "DatoExtra")
    protected String datoExtra;

    /**
     * Gets the value of the codMensaje property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodMensaje() {
        return codMensaje;
    }

    /**
     * Sets the value of the codMensaje property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodMensaje(String value) {
        this.codMensaje = value;
    }

    /**
     * Gets the value of the descripcion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Sets the value of the descripcion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    /**
     * Gets the value of the datoExtra property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatoExtra() {
        return datoExtra;
    }

    /**
     * Sets the value of the datoExtra property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatoExtra(String value) {
        this.datoExtra = value;
    }

}
