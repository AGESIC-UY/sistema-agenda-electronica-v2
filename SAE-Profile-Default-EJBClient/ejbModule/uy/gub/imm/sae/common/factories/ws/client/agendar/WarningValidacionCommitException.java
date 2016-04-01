
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WarningValidacionCommitException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WarningValidacionCommitException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cantCampos" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cantMensajes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoError" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigosErrorMensajes" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mensaje" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="mensajes" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreCampo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="nombreValidacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombresCampos" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WarningValidacionCommitException", propOrder = {
    "cantCampos",
    "cantMensajes",
    "codigoError",
    "codigosErrorMensajes",
    "mensaje",
    "mensajes",
    "message",
    "nombreCampo",
    "nombreValidacion",
    "nombresCampos"
})
public class WarningValidacionCommitException {

    protected int cantCampos;
    protected int cantMensajes;
    protected String codigoError;
    protected List<String> codigosErrorMensajes;
    protected boolean mensaje;
    protected List<String> mensajes;
    protected String message;
    protected boolean nombreCampo;
    protected String nombreValidacion;
    protected List<String> nombresCampos;

    /**
     * Gets the value of the cantCampos property.
     * 
     */
    public int getCantCampos() {
        return cantCampos;
    }

    /**
     * Sets the value of the cantCampos property.
     * 
     */
    public void setCantCampos(int value) {
        this.cantCampos = value;
    }

    /**
     * Gets the value of the cantMensajes property.
     * 
     */
    public int getCantMensajes() {
        return cantMensajes;
    }

    /**
     * Sets the value of the cantMensajes property.
     * 
     */
    public void setCantMensajes(int value) {
        this.cantMensajes = value;
    }

    /**
     * Gets the value of the codigoError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoError() {
        return codigoError;
    }

    /**
     * Sets the value of the codigoError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoError(String value) {
        this.codigoError = value;
    }

    /**
     * Gets the value of the codigosErrorMensajes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codigosErrorMensajes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodigosErrorMensajes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<String> getCodigosErrorMensajes() {
        if (codigosErrorMensajes == null) {
            codigosErrorMensajes = new ArrayList<String>();
        }
        return this.codigosErrorMensajes;
    }

    /**
     * Gets the value of the mensaje property.
     * 
     */
    public boolean isMensaje() {
        return mensaje;
    }

    /**
     * Sets the value of the mensaje property.
     * 
     */
    public void setMensaje(boolean value) {
        this.mensaje = value;
    }

    /**
     * Gets the value of the mensajes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mensajes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMensajes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<String> getMensajes() {
        if (mensajes == null) {
            mensajes = new ArrayList<String>();
        }
        return this.mensajes;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the nombreCampo property.
     * 
     */
    public boolean isNombreCampo() {
        return nombreCampo;
    }

    /**
     * Sets the value of the nombreCampo property.
     * 
     */
    public void setNombreCampo(boolean value) {
        this.nombreCampo = value;
    }

    /**
     * Gets the value of the nombreValidacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreValidacion() {
        return nombreValidacion;
    }

    /**
     * Sets the value of the nombreValidacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreValidacion(String value) {
        this.nombreValidacion = value;
    }

    /**
     * Gets the value of the nombresCampos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nombresCampos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNombresCampos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<String> getNombresCampos() {
        if (nombresCampos == null) {
            nombresCampos = new ArrayList<String>();
        }
        return this.nombresCampos;
    }

}
