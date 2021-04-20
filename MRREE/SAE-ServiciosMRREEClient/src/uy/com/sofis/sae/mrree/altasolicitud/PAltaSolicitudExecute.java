
package uy.com.sofis.sae.mrree.altasolicitud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Solicitudnumero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Fecha_solicitud" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudnumerodocumento" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Fecha_nac" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudprimernombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudsegundonombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudprimerapellido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudsegundoapellido" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudemail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudtelefono" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudanioemigracion" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="Solicituddptouruguay" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudnombremadre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudnombrepadre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Tramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Consulado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Solicitudnumerollamado" type="{http://www.w3.org/2001/XMLSchema}long"/>
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
    "solicitudnumero",
    "fechaSolicitud",
    "solicitudnumerodocumento",
    "fechaNac",
    "solicitudprimernombre",
    "solicitudsegundonombre",
    "solicitudprimerapellido",
    "solicitudsegundoapellido",
    "solicitudemail",
    "solicitudtelefono",
    "solicitudanioemigracion",
    "solicituddptouruguay",
    "solicitudnombremadre",
    "solicitudnombrepadre",
    "tramite",
    "consulado",
    "solicitudnumerollamado"
})
@XmlRootElement(name = "PAltaSolicitud.Execute")
public class PAltaSolicitudExecute {

    @XmlElement(name = "Solicitudnumero", required = true)
    protected String solicitudnumero;
    @XmlElement(name = "Fecha_solicitud", required = true)
    protected String fechaSolicitud;
    @XmlElement(name = "Solicitudnumerodocumento")
    protected long solicitudnumerodocumento;
    @XmlElement(name = "Fecha_nac", required = true)
    protected String fechaNac;
    @XmlElement(name = "Solicitudprimernombre", required = true)
    protected String solicitudprimernombre;
    @XmlElement(name = "Solicitudsegundonombre", required = true)
    protected String solicitudsegundonombre;
    @XmlElement(name = "Solicitudprimerapellido", required = true)
    protected String solicitudprimerapellido;
    @XmlElement(name = "Solicitudsegundoapellido", required = true)
    protected String solicitudsegundoapellido;
    @XmlElement(name = "Solicitudemail", required = true)
    protected String solicitudemail;
    @XmlElement(name = "Solicitudtelefono", required = true)
    protected String solicitudtelefono;
    @XmlElement(name = "Solicitudanioemigracion")
    protected short solicitudanioemigracion;
    @XmlElement(name = "Solicituddptouruguay", required = true)
    protected String solicituddptouruguay;
    @XmlElement(name = "Solicitudnombremadre", required = true)
    protected String solicitudnombremadre;
    @XmlElement(name = "Solicitudnombrepadre", required = true)
    protected String solicitudnombrepadre;
    @XmlElement(name = "Tramite", required = true)
    protected String tramite;
    @XmlElement(name = "Consulado", required = true)
    protected String consulado;
    @XmlElement(name = "Solicitudnumerollamado")
    protected long solicitudnumerollamado;

    /**
     * Gets the value of the solicitudnumero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudnumero() {
        return solicitudnumero;
    }

    /**
     * Sets the value of the solicitudnumero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudnumero(String value) {
        this.solicitudnumero = value;
    }

    /**
     * Gets the value of the fechaSolicitud property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Sets the value of the fechaSolicitud property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaSolicitud(String value) {
        this.fechaSolicitud = value;
    }

    /**
     * Gets the value of the solicitudnumerodocumento property.
     * 
     */
    public long getSolicitudnumerodocumento() {
        return solicitudnumerodocumento;
    }

    /**
     * Sets the value of the solicitudnumerodocumento property.
     * 
     */
    public void setSolicitudnumerodocumento(long value) {
        this.solicitudnumerodocumento = value;
    }

    /**
     * Gets the value of the fechaNac property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaNac() {
        return fechaNac;
    }

    /**
     * Sets the value of the fechaNac property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaNac(String value) {
        this.fechaNac = value;
    }

    /**
     * Gets the value of the solicitudprimernombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudprimernombre() {
        return solicitudprimernombre;
    }

    /**
     * Sets the value of the solicitudprimernombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudprimernombre(String value) {
        this.solicitudprimernombre = value;
    }

    /**
     * Gets the value of the solicitudsegundonombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudsegundonombre() {
        return solicitudsegundonombre;
    }

    /**
     * Sets the value of the solicitudsegundonombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudsegundonombre(String value) {
        this.solicitudsegundonombre = value;
    }

    /**
     * Gets the value of the solicitudprimerapellido property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudprimerapellido() {
        return solicitudprimerapellido;
    }

    /**
     * Sets the value of the solicitudprimerapellido property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudprimerapellido(String value) {
        this.solicitudprimerapellido = value;
    }

    /**
     * Gets the value of the solicitudsegundoapellido property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudsegundoapellido() {
        return solicitudsegundoapellido;
    }

    /**
     * Sets the value of the solicitudsegundoapellido property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudsegundoapellido(String value) {
        this.solicitudsegundoapellido = value;
    }

    /**
     * Gets the value of the solicitudemail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudemail() {
        return solicitudemail;
    }

    /**
     * Sets the value of the solicitudemail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudemail(String value) {
        this.solicitudemail = value;
    }

    /**
     * Gets the value of the solicitudtelefono property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudtelefono() {
        return solicitudtelefono;
    }

    /**
     * Sets the value of the solicitudtelefono property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudtelefono(String value) {
        this.solicitudtelefono = value;
    }

    /**
     * Gets the value of the solicitudanioemigracion property.
     * 
     */
    public short getSolicitudanioemigracion() {
        return solicitudanioemigracion;
    }

    /**
     * Sets the value of the solicitudanioemigracion property.
     * 
     */
    public void setSolicitudanioemigracion(short value) {
        this.solicitudanioemigracion = value;
    }

    /**
     * Gets the value of the solicituddptouruguay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicituddptouruguay() {
        return solicituddptouruguay;
    }

    /**
     * Sets the value of the solicituddptouruguay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicituddptouruguay(String value) {
        this.solicituddptouruguay = value;
    }

    /**
     * Gets the value of the solicitudnombremadre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudnombremadre() {
        return solicitudnombremadre;
    }

    /**
     * Sets the value of the solicitudnombremadre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudnombremadre(String value) {
        this.solicitudnombremadre = value;
    }

    /**
     * Gets the value of the solicitudnombrepadre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudnombrepadre() {
        return solicitudnombrepadre;
    }

    /**
     * Sets the value of the solicitudnombrepadre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudnombrepadre(String value) {
        this.solicitudnombrepadre = value;
    }

    /**
     * Gets the value of the tramite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTramite() {
        return tramite;
    }

    /**
     * Sets the value of the tramite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTramite(String value) {
        this.tramite = value;
    }

    /**
     * Gets the value of the consulado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsulado() {
        return consulado;
    }

    /**
     * Sets the value of the consulado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsulado(String value) {
        this.consulado = value;
    }

    /**
     * Gets the value of the solicitudnumerollamado property.
     * 
     */
    public long getSolicitudnumerollamado() {
        return solicitudnumerollamado;
    }

    /**
     * Sets the value of the solicitudnumerollamado property.
     * 
     */
    public void setSolicitudnumerollamado(long value) {
        this.solicitudnumerollamado = value;
    }

}
