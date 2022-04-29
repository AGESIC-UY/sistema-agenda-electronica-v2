
package uy.gub.agesic.itramites.bruto.web.ws.linea;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for lineaDTO complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lineaDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idTransaccion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="edicionModelo" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="idOficina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oficina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaHoraOrganismo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="tipoRegistroTrazabilidad" type="{http://ws.web.bruto.itramites.agesic.gub.uy/lineaService}tipoRegistroTrazabilidadEnum" minOccurs="0"/>
 *         &lt;element name="paso" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="descripcionDelPaso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="aclaraciones" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pasoDelProceso" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="idOficinaDestino" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oficinaDestino" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estadoProceso" type="{http://ws.web.bruto.itramites.agesic.gub.uy/lineaService}estadoProcesoEnum" minOccurs="0"/>
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
@XmlType(name = "lineaDTO", propOrder = {
    "idTransaccion",
    "edicionModelo",
    "idOficina",
    "oficina",
    "fechaHoraOrganismo",
    "tipoRegistroTrazabilidad",
    "paso",
    "descripcionDelPaso",
    "aclaraciones",
    "pasoDelProceso",
    "idOficinaDestino",
    "oficinaDestino",
    "estadoProceso",
    "datoExtra1",
    "datoExtra2",
    "datoExtra3",
    "datoExtra4"
})
@XmlRootElement
public class LineaDTO {

    @XmlElement(required = true)
    protected String idTransaccion;
    protected long edicionModelo;
    protected String idOficina;
    protected String oficina;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaHoraOrganismo;
    protected Integer tipoRegistroTrazabilidad;
    protected Long paso;
    protected String descripcionDelPaso;
    protected String aclaraciones;
    protected Long pasoDelProceso;
    protected String idOficinaDestino;
    protected String oficinaDestino;
    protected Integer estadoProceso;
    protected String datoExtra1;
    protected String datoExtra2;
    protected String datoExtra3;
    protected String datoExtra4;

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
     * Gets the value of the idOficina property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdOficina() {
        return idOficina;
    }

    /**
     * Sets the value of the idOficina property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdOficina(String value) {
        this.idOficina = value;
    }

    /**
     * Gets the value of the oficina property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOficina() {
        return oficina;
    }

    /**
     * Sets the value of the oficina property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOficina(String value) {
        this.oficina = value;
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
     * Gets the value of the tipoRegistroTrazabilidad property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTipoRegistroTrazabilidad() {
        return tipoRegistroTrazabilidad;
    }

    /**
     * Sets the value of the tipoRegistroTrazabilidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTipoRegistroTrazabilidad(Integer value) {
        this.tipoRegistroTrazabilidad = value;
    }

    /**
     * Gets the value of the paso property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPaso() {
        return paso;
    }

    /**
     * Sets the value of the paso property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPaso(Long value) {
        this.paso = value;
    }

    /**
     * Gets the value of the descripcionDelPaso property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionDelPaso() {
        return descripcionDelPaso;
    }

    /**
     * Sets the value of the descripcionDelPaso property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionDelPaso(String value) {
        this.descripcionDelPaso = value;
    }

    /**
     * Gets the value of the aclaraciones property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAclaraciones() {
        return aclaraciones;
    }

    /**
     * Sets the value of the aclaraciones property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAclaraciones(String value) {
        this.aclaraciones = value;
    }

    /**
     * Gets the value of the pasoDelProceso property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPasoDelProceso() {
        return pasoDelProceso;
    }

    /**
     * Sets the value of the pasoDelProceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPasoDelProceso(Long value) {
        this.pasoDelProceso = value;
    }

    /**
     * Gets the value of the idOficinaDestino property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdOficinaDestino() {
        return idOficinaDestino;
    }

    /**
     * Sets the value of the idOficinaDestino property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdOficinaDestino(String value) {
        this.idOficinaDestino = value;
    }

    /**
     * Gets the value of the oficinaDestino property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOficinaDestino() {
        return oficinaDestino;
    }

    /**
     * Sets the value of the oficinaDestino property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOficinaDestino(String value) {
        this.oficinaDestino = value;
    }

    /**
     * Gets the value of the estadoProceso property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getEstadoProceso() {
        return estadoProceso;
    }

    /**
     * Sets the value of the estadoProceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setEstadoProceso(Integer value) {
        this.estadoProceso = value;
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
