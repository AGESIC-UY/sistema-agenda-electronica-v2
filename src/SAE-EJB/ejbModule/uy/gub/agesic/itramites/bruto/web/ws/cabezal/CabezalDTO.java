
package uy.gub.agesic.itramites.bruto.web.ws.cabezal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for cabezalDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cabezalDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipoProceso" type="{http://ws.web.bruto.itramites.agesic.gub.uy/}tipoProcesoEnum"/>
 *         &lt;element name="idProceso" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="idTransaccion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="edicionModelo" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="idTransaccionPadre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pasoPadre" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="cantidadPasosProceso" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="inicioAsistidoProceso" type="{http://ws.web.bruto.itramites.agesic.gub.uy/}inicioAsistidoProcesoEnum" minOccurs="0"/>
 *         &lt;element name="canalDeInicio" type="{http://ws.web.bruto.itramites.agesic.gub.uy/}canalDeInicioEnum" minOccurs="0"/>
 *         &lt;element name="fechaHoraOrganismo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="datoExtra1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datoExtra2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datoExtra3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datoExtra4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cabezalDTO", propOrder = {
    "tipoProceso",
    "idProceso",
    "idTransaccion",
    "edicionModelo",
    "idTransaccionPadre",
    "pasoPadre",
    "cantidadPasosProceso",
    "inicioAsistidoProceso",
    "canalDeInicio",
    "fechaHoraOrganismo",
    "datoExtra1",
    "datoExtra2",
    "datoExtra3",
    "datoExtra4"
})
@XmlRootElement
public class CabezalDTO {

    protected int tipoProceso;
    protected long idProceso;
    @XmlElement(required = true)
    protected String idTransaccion;
    protected long edicionModelo;
    protected String idTransaccionPadre;
    protected Long pasoPadre;
    protected Long cantidadPasosProceso;
    protected Integer inicioAsistidoProceso;
    protected Integer canalDeInicio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaHoraOrganismo;
    protected String datoExtra1;
    protected String datoExtra2;
    protected String datoExtra3;
    protected String datoExtra4;

    /**
     * Gets the value of the tipoProceso property.
     * 
     */
    public int getTipoProceso() {
        return tipoProceso;
    }

    /**
     * Sets the value of the tipoProceso property.
     * 
     */
    public void setTipoProceso(int value) {
        this.tipoProceso = value;
    }

    /**
     * Gets the value of the idProceso property.
     * 
     */
    public long getIdProceso() {
        return idProceso;
    }

    /**
     * Sets the value of the idProceso property.
     * 
     */
    public void setIdProceso(long value) {
        this.idProceso = value;
    }

    /**
     * Gets the value of the idTransaccion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdTransaccion() {
        return idTransaccion;
    }

    /**
     * Sets the value of the idTransaccion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdTransaccion(String value) {
        this.idTransaccion = value;
    }

    /**
     * Gets the value of the edicionModelo property.
     * 
     */
    public long getEdicionModelo() {
        return edicionModelo;
    }

    /**
     * Sets the value of the edicionModelo property.
     * 
     */
    public void setEdicionModelo(long value) {
        this.edicionModelo = value;
    }

    /**
     * Gets the value of the idTransaccionPadre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdTransaccionPadre() {
        return idTransaccionPadre;
    }

    /**
     * Sets the value of the idTransaccionPadre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdTransaccionPadre(String value) {
        this.idTransaccionPadre = value;
    }

    /**
     * Gets the value of the pasoPadre property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPasoPadre() {
        return pasoPadre;
    }

    /**
     * Sets the value of the pasoPadre property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPasoPadre(Long value) {
        this.pasoPadre = value;
    }

    /**
     * Gets the value of the cantidadPasosProceso property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCantidadPasosProceso() {
        return cantidadPasosProceso;
    }

    /**
     * Sets the value of the cantidadPasosProceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCantidadPasosProceso(Long value) {
        this.cantidadPasosProceso = value;
    }

    /**
     * Gets the value of the inicioAsistidoProceso property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInicioAsistidoProceso() {
        return inicioAsistidoProceso;
    }

    /**
     * Sets the value of the inicioAsistidoProceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInicioAsistidoProceso(Integer value) {
        this.inicioAsistidoProceso = value;
    }

    /**
     * Gets the value of the canalDeInicio property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCanalDeInicio() {
        return canalDeInicio;
    }

    /**
     * Sets the value of the canalDeInicio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCanalDeInicio(Integer value) {
        this.canalDeInicio = value;
    }

    /**
     * Gets the value of the fechaHoraOrganismo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaHoraOrganismo() {
        return fechaHoraOrganismo;
    }

    /**
     * Sets the value of the fechaHoraOrganismo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaHoraOrganismo(XMLGregorianCalendar value) {
        this.fechaHoraOrganismo = value;
    }

    /**
     * Gets the value of the datoExtra1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatoExtra1() {
        return datoExtra1;
    }

    /**
     * Sets the value of the datoExtra1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatoExtra1(String value) {
        this.datoExtra1 = value;
    }

    /**
     * Gets the value of the datoExtra2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatoExtra2() {
        return datoExtra2;
    }

    /**
     * Sets the value of the datoExtra2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatoExtra2(String value) {
        this.datoExtra2 = value;
    }

    /**
     * Gets the value of the datoExtra3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatoExtra3() {
        return datoExtra3;
    }

    /**
     * Sets the value of the datoExtra3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatoExtra3(String value) {
        this.datoExtra3 = value;
    }

    /**
     * Gets the value of the datoExtra4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatoExtra4() {
        return datoExtra4;
    }

    /**
     * Sets the value of the datoExtra4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatoExtra4(String value) {
        this.datoExtra4 = value;
    }

}
