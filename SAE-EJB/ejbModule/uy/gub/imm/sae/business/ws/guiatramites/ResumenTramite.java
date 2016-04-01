
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResumenTramite complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResumenTramite">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QueEs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Temas" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Organismo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OnLine" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResumenTramite", propOrder = {
    "id",
    "nombre",
    "queEs",
    "temas",
    "organismo",
    "onLine",
    "url"
})
public class ResumenTramite {

    @XmlElement(name = "Id")
    protected String id;
    @XmlElement(name = "Nombre")
    protected String nombre;
    @XmlElement(name = "QueEs")
    protected String queEs;
    @XmlElement(name = "Temas")
    protected String temas;
    @XmlElement(name = "Organismo")
    protected String organismo;
    @XmlElement(name = "OnLine")
    protected boolean onLine;
    @XmlElement(name = "URL")
    protected String url;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Gets the value of the queEs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueEs() {
        return queEs;
    }

    /**
     * Sets the value of the queEs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueEs(String value) {
        this.queEs = value;
    }

    /**
     * Gets the value of the temas property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemas() {
        return temas;
    }

    /**
     * Sets the value of the temas property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemas(String value) {
        this.temas = value;
    }

    /**
     * Gets the value of the organismo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganismo() {
        return organismo;
    }

    /**
     * Sets the value of the organismo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganismo(String value) {
        this.organismo = value;
    }

    /**
     * Gets the value of the onLine property.
     * 
     */
    public boolean isOnLine() {
        return onLine;
    }

    /**
     * Sets the value of the onLine property.
     * 
     */
    public void setOnLine(boolean value) {
        this.onLine = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

}
