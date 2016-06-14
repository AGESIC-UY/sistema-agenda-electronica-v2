
package uy.gub.agesic.novedades;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for publicar complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="publicar">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="tipoDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paisDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaHoraReserva" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroReserva" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oidOrganismo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreOrganismo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoAgenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreAgenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoRecurso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreRecurso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accion" type="{http://novedades.sae.agesic.gub.uy/}acciones" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publicar", propOrder = {
    "timestamp",
    "tipoDocumento",
    "paisDocumento",
    "numeroDocumento",
    "fechaHoraReserva",
    "numeroReserva",
    "oidOrganismo",
    "nombreOrganismo",
    "codigoAgenda",
    "nombreAgenda",
    "codigoRecurso",
    "nombreRecurso",
    "accion"
})
public class Publicar {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    protected String tipoDocumento;
    protected String paisDocumento;
    protected String numeroDocumento;
    protected String fechaHoraReserva;
    protected String numeroReserva;
    protected String oidOrganismo;
    protected String nombreOrganismo;
    protected String codigoAgenda;
    protected String nombreAgenda;
    protected String codigoRecurso;
    protected String nombreRecurso;
    protected Acciones accion;

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the tipoDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Sets the value of the tipoDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumento(String value) {
        this.tipoDocumento = value;
    }

    /**
     * Gets the value of the paisDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaisDocumento() {
        return paisDocumento;
    }

    /**
     * Sets the value of the paisDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaisDocumento(String value) {
        this.paisDocumento = value;
    }

    /**
     * Gets the value of the numeroDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * Sets the value of the numeroDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroDocumento(String value) {
        this.numeroDocumento = value;
    }

    /**
     * Gets the value of the fechaHoraReserva property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaHoraReserva() {
        return fechaHoraReserva;
    }

    /**
     * Sets the value of the fechaHoraReserva property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaHoraReserva(String value) {
        this.fechaHoraReserva = value;
    }

    /**
     * Gets the value of the numeroReserva property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroReserva() {
        return numeroReserva;
    }

    /**
     * Sets the value of the numeroReserva property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroReserva(String value) {
        this.numeroReserva = value;
    }

    /**
     * Gets the value of the oidOrganismo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOidOrganismo() {
        return oidOrganismo;
    }

    /**
     * Sets the value of the oidOrganismo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOidOrganismo(String value) {
        this.oidOrganismo = value;
    }

    /**
     * Gets the value of the nombreOrganismo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreOrganismo() {
        return nombreOrganismo;
    }

    /**
     * Sets the value of the nombreOrganismo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreOrganismo(String value) {
        this.nombreOrganismo = value;
    }

    /**
     * Gets the value of the codigoAgenda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoAgenda() {
        return codigoAgenda;
    }

    /**
     * Sets the value of the codigoAgenda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoAgenda(String value) {
        this.codigoAgenda = value;
    }

    /**
     * Gets the value of the nombreAgenda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreAgenda() {
        return nombreAgenda;
    }

    /**
     * Sets the value of the nombreAgenda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreAgenda(String value) {
        this.nombreAgenda = value;
    }

    /**
     * Gets the value of the codigoRecurso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRecurso() {
        return codigoRecurso;
    }

    /**
     * Sets the value of the codigoRecurso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRecurso(String value) {
        this.codigoRecurso = value;
    }

    /**
     * Gets the value of the nombreRecurso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreRecurso() {
        return nombreRecurso;
    }

    /**
     * Sets the value of the nombreRecurso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreRecurso(String value) {
        this.nombreRecurso = value;
    }

    /**
     * Gets the value of the accion property.
     * 
     * @return
     *     possible object is
     *     {@link Acciones }
     *     
     */
    public Acciones getAccion() {
        return accion;
    }

    /**
     * Sets the value of the accion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Acciones }
     *     
     */
    public void setAccion(Acciones value) {
        this.accion = value;
    }

}
