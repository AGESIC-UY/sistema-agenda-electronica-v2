
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OfNacionalDatos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OfNacionalDatos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Departamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Localidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "OfNacionalDatos", propOrder = {
    "departamento",
    "localidad",
    "direccion",
    "horario",
    "telefonos",
    "comentarios"
})
public class OfNacionalDatos {

    @XmlElement(name = "Departamento")
    protected String departamento;
    @XmlElement(name = "Localidad")
    protected String localidad;
    @XmlElement(name = "Direccion")
    protected String direccion;
    @XmlElement(name = "Horario")
    protected String horario;
    @XmlElement(name = "Telefonos")
    protected String telefonos;
    @XmlElement(name = "Comentarios")
    protected String comentarios;

    /**
     * Gets the value of the departamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * Sets the value of the departamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartamento(String value) {
        this.departamento = value;
    }

    /**
     * Gets the value of the localidad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalidad() {
        return localidad;
    }

    /**
     * Sets the value of the localidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalidad(String value) {
        this.localidad = value;
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
