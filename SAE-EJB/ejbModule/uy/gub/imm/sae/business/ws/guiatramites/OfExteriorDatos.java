
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OfExteriorDatos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OfExteriorDatos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Ciudad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Direccion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Horario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Telefonos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Comentarios" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OfExteriorDatos", propOrder = {
    "pais",
    "ciudad",
    "direccion",
    "horario",
    "telefonos",
    "comentarios"
})
public class OfExteriorDatos {

    @XmlElement(name = "Pais")
    protected String pais;
    @XmlElement(name = "Ciudad")
    protected String ciudad;
    @XmlElement(name = "Direccion")
    protected String direccion;
    @XmlElement(name = "Horario")
    protected String horario;
    @XmlElement(name = "Telefonos")
    protected String telefonos;
    @XmlElement(name = "Comentarios")
    protected String comentarios;

    /**
     * Gets the value of the pais property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPais() {
        return pais;
    }

    /**
     * Sets the value of the pais property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPais(String value) {
        this.pais = value;
    }

    /**
     * Gets the value of the ciudad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Sets the value of the ciudad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiudad(String value) {
        this.ciudad = value;
    }

    /**
     * Gets the value of the direccion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Sets the value of the direccion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccion(String value) {
        this.direccion = value;
    }

    /**
     * Gets the value of the horario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHorario() {
        return horario;
    }

    /**
     * Sets the value of the horario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHorario(String value) {
        this.horario = value;
    }

    /**
     * Gets the value of the telefonos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefonos() {
        return telefonos;
    }

    /**
     * Sets the value of the telefonos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefonos(String value) {
        this.telefonos = value;
    }

    /**
     * Gets the value of the comentarios property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComentarios() {
        return comentarios;
    }

    /**
     * Sets the value of the comentarios property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComentarios(String value) {
        this.comentarios = value;
    }

}
