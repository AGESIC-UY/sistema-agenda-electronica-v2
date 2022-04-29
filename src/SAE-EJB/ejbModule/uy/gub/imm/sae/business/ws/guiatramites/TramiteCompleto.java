
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TramiteCompleto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TramiteCompleto">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QueEs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DondeCuando" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Necesita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Costos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Solicitud" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OtrosDatos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Enlaces" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Organismo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmailOrganismo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FechaAct" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FechaProxAct" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Temas" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MetaTags" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Adjuntos" type="{http://tempuri.org/}ArrayOfVinculoDatos" minOccurs="0"/>
 *         &lt;element name="OfNacionales" type="{http://tempuri.org/}ArrayOfOfNacionalDatos" minOccurs="0"/>
 *         &lt;element name="OfExteriores" type="{http://tempuri.org/}ArrayOfOfExteriorDatos" minOccurs="0"/>
 *         &lt;element name="Normativas" type="{http://tempuri.org/}ArrayOfNormativaDatos" minOccurs="0"/>
 *         &lt;element name="ComoSeHace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Vinculos" type="{http://tempuri.org/}ArrayOfVinculoDatos" minOccurs="0"/>
 *         &lt;element name="DependeDe" type="{http://tempuri.org/}OrganismoArea" minOccurs="0"/>
 *         &lt;element name="AccesoOnLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TramiteCompleto", propOrder = {
    "id",
    "nombre",
    "queEs",
    "dondeCuando",
    "necesita",
    "costos",
    "solicitud",
    "otrosDatos",
    "enlaces",
    "organismo",
    "emailOrganismo",
    "fechaAct",
    "fechaProxAct",
    "temas",
    "metaTags",
    "adjuntos",
    "ofNacionales",
    "ofExteriores",
    "normativas",
    "comoSeHace",
    "vinculos",
    "dependeDe",
    "accesoOnLine"
})
public class TramiteCompleto {

    @XmlElement(name = "Id")
    protected String id;
    @XmlElement(name = "Nombre")
    protected String nombre;
    @XmlElement(name = "QueEs")
    protected String queEs;
    @XmlElement(name = "DondeCuando")
    protected String dondeCuando;
    @XmlElement(name = "Necesita")
    protected String necesita;
    @XmlElement(name = "Costos")
    protected String costos;
    @XmlElement(name = "Solicitud")
    protected String solicitud;
    @XmlElement(name = "OtrosDatos")
    protected String otrosDatos;
    @XmlElement(name = "Enlaces")
    protected String enlaces;
    @XmlElement(name = "Organismo")
    protected String organismo;
    @XmlElement(name = "EmailOrganismo")
    protected String emailOrganismo;
    @XmlElement(name = "FechaAct")
    protected String fechaAct;
    @XmlElement(name = "FechaProxAct")
    protected String fechaProxAct;
    @XmlElement(name = "Temas")
    protected String temas;
    @XmlElement(name = "MetaTags")
    protected String metaTags;
    @XmlElement(name = "Adjuntos")
    protected ArrayOfVinculoDatos adjuntos;
    @XmlElement(name = "OfNacionales")
    protected ArrayOfOfNacionalDatos ofNacionales;
    @XmlElement(name = "OfExteriores")
    protected ArrayOfOfExteriorDatos ofExteriores;
    @XmlElement(name = "Normativas")
    protected ArrayOfNormativaDatos normativas;
    @XmlElement(name = "ComoSeHace")
    protected String comoSeHace;
    @XmlElement(name = "Vinculos")
    protected ArrayOfVinculoDatos vinculos;
    @XmlElement(name = "DependeDe")
    protected OrganismoArea dependeDe;
    @XmlElement(name = "AccesoOnLine")
    protected String accesoOnLine;

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
     * Gets the value of the dondeCuando property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDondeCuando() {
        return dondeCuando;
    }

    /**
     * Sets the value of the dondeCuando property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDondeCuando(String value) {
        this.dondeCuando = value;
    }

    /**
     * Gets the value of the necesita property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNecesita() {
        return necesita;
    }

    /**
     * Sets the value of the necesita property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNecesita(String value) {
        this.necesita = value;
    }

    /**
     * Gets the value of the costos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCostos() {
        return costos;
    }

    /**
     * Sets the value of the costos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCostos(String value) {
        this.costos = value;
    }

    /**
     * Gets the value of the solicitud property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitud() {
        return solicitud;
    }

    /**
     * Sets the value of the solicitud property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitud(String value) {
        this.solicitud = value;
    }

    /**
     * Gets the value of the otrosDatos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtrosDatos() {
        return otrosDatos;
    }

    /**
     * Sets the value of the otrosDatos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtrosDatos(String value) {
        this.otrosDatos = value;
    }

    /**
     * Gets the value of the enlaces property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnlaces() {
        return enlaces;
    }

    /**
     * Sets the value of the enlaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnlaces(String value) {
        this.enlaces = value;
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
     * Gets the value of the emailOrganismo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailOrganismo() {
        return emailOrganismo;
    }

    /**
     * Sets the value of the emailOrganismo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailOrganismo(String value) {
        this.emailOrganismo = value;
    }

    /**
     * Gets the value of the fechaAct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaAct() {
        return fechaAct;
    }

    /**
     * Sets the value of the fechaAct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaAct(String value) {
        this.fechaAct = value;
    }

    /**
     * Gets the value of the fechaProxAct property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaProxAct() {
        return fechaProxAct;
    }

    /**
     * Sets the value of the fechaProxAct property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaProxAct(String value) {
        this.fechaProxAct = value;
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
     * Gets the value of the metaTags property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaTags() {
        return metaTags;
    }

    /**
     * Sets the value of the metaTags property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaTags(String value) {
        this.metaTags = value;
    }

    /**
     * Gets the value of the adjuntos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVinculoDatos }
     *     
     */
    public ArrayOfVinculoDatos getAdjuntos() {
        return adjuntos;
    }

    /**
     * Sets the value of the adjuntos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVinculoDatos }
     *     
     */
    public void setAdjuntos(ArrayOfVinculoDatos value) {
        this.adjuntos = value;
    }

    /**
     * Gets the value of the ofNacionales property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOfNacionalDatos }
     *     
     */
    public ArrayOfOfNacionalDatos getOfNacionales() {
        return ofNacionales;
    }

    /**
     * Sets the value of the ofNacionales property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOfNacionalDatos }
     *     
     */
    public void setOfNacionales(ArrayOfOfNacionalDatos value) {
        this.ofNacionales = value;
    }

    /**
     * Gets the value of the ofExteriores property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOfExteriorDatos }
     *     
     */
    public ArrayOfOfExteriorDatos getOfExteriores() {
        return ofExteriores;
    }

    /**
     * Sets the value of the ofExteriores property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOfExteriorDatos }
     *     
     */
    public void setOfExteriores(ArrayOfOfExteriorDatos value) {
        this.ofExteriores = value;
    }

    /**
     * Gets the value of the normativas property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfNormativaDatos }
     *     
     */
    public ArrayOfNormativaDatos getNormativas() {
        return normativas;
    }

    /**
     * Sets the value of the normativas property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfNormativaDatos }
     *     
     */
    public void setNormativas(ArrayOfNormativaDatos value) {
        this.normativas = value;
    }

    /**
     * Gets the value of the comoSeHace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComoSeHace() {
        return comoSeHace;
    }

    /**
     * Sets the value of the comoSeHace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComoSeHace(String value) {
        this.comoSeHace = value;
    }

    /**
     * Gets the value of the vinculos property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVinculoDatos }
     *     
     */
    public ArrayOfVinculoDatos getVinculos() {
        return vinculos;
    }

    /**
     * Sets the value of the vinculos property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVinculoDatos }
     *     
     */
    public void setVinculos(ArrayOfVinculoDatos value) {
        this.vinculos = value;
    }

    /**
     * Gets the value of the dependeDe property.
     * 
     * @return
     *     possible object is
     *     {@link OrganismoArea }
     *     
     */
    public OrganismoArea getDependeDe() {
        return dependeDe;
    }

    /**
     * Sets the value of the dependeDe property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganismoArea }
     *     
     */
    public void setDependeDe(OrganismoArea value) {
        this.dependeDe = value;
    }

    /**
     * Gets the value of the accesoOnLine property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccesoOnLine() {
        return accesoOnLine;
    }

    /**
     * Sets the value of the accesoOnLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccesoOnLine(String value) {
        this.accesoOnLine = value;
    }

}
