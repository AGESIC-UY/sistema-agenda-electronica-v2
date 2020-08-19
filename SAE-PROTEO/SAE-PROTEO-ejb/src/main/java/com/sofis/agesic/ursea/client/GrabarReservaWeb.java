
package com.sofis.agesic.ursea.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdOficina" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Fecha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TurnoHora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Serie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NumeroCredencial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FechaNacimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NombrePadre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NombreMadre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TipoTramite" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrimerApellido" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SegundoApellido" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrimerNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SegundoNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PruebaCiudadania" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Cedula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "idOficina",
    "fecha",
    "turnoHora",
    "serie",
    "numeroCredencial",
    "fechaNacimiento",
    "nombrePadre",
    "nombreMadre",
    "tipoTramite",
    "primerApellido",
    "segundoApellido",
    "primerNombre",
    "segundoNombre",
    "pruebaCiudadania",
    "cedula"
})
@XmlRootElement(name = "GrabarReservaWeb")
public class GrabarReservaWeb {

    @XmlElement(name = "IdOficina")
    protected int idOficina;
    @XmlElement(name = "Fecha")
    protected String fecha;
    @XmlElement(name = "TurnoHora")
    protected String turnoHora;
    @XmlElement(name = "Serie")
    protected String serie;
    @XmlElement(name = "NumeroCredencial")
    protected String numeroCredencial;
    @XmlElement(name = "FechaNacimiento")
    protected String fechaNacimiento;
    @XmlElement(name = "NombrePadre")
    protected String nombrePadre;
    @XmlElement(name = "NombreMadre")
    protected String nombreMadre;
    @XmlElement(name = "TipoTramite")
    protected String tipoTramite;
    @XmlElement(name = "PrimerApellido")
    protected String primerApellido;
    @XmlElement(name = "SegundoApellido")
    protected String segundoApellido;
    @XmlElement(name = "PrimerNombre")
    protected String primerNombre;
    @XmlElement(name = "SegundoNombre")
    protected String segundoNombre;
    @XmlElement(name = "PruebaCiudadania")
    protected String pruebaCiudadania;
    @XmlElement(name = "Cedula")
    protected String cedula;

    /**
     * Obtiene el valor de la propiedad idOficina.
     * 
     */
    public int getIdOficina() {
        return idOficina;
    }

    /**
     * Define el valor de la propiedad idOficina.
     * 
     */
    public void setIdOficina(int value) {
        this.idOficina = value;
    }

    /**
     * Obtiene el valor de la propiedad fecha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Define el valor de la propiedad fecha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFecha(String value) {
        this.fecha = value;
    }

    /**
     * Obtiene el valor de la propiedad turnoHora.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTurnoHora() {
        return turnoHora;
    }

    /**
     * Define el valor de la propiedad turnoHora.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTurnoHora(String value) {
        this.turnoHora = value;
    }

    /**
     * Obtiene el valor de la propiedad serie.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerie() {
        return serie;
    }

    /**
     * Define el valor de la propiedad serie.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerie(String value) {
        this.serie = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroCredencial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroCredencial() {
        return numeroCredencial;
    }

    /**
     * Define el valor de la propiedad numeroCredencial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroCredencial(String value) {
        this.numeroCredencial = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaNacimiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Define el valor de la propiedad fechaNacimiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaNacimiento(String value) {
        this.fechaNacimiento = value;
    }

    /**
     * Obtiene el valor de la propiedad nombrePadre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombrePadre() {
        return nombrePadre;
    }

    /**
     * Define el valor de la propiedad nombrePadre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombrePadre(String value) {
        this.nombrePadre = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreMadre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreMadre() {
        return nombreMadre;
    }

    /**
     * Define el valor de la propiedad nombreMadre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreMadre(String value) {
        this.nombreMadre = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoTramite.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoTramite() {
        return tipoTramite;
    }

    /**
     * Define el valor de la propiedad tipoTramite.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoTramite(String value) {
        this.tipoTramite = value;
    }

    /**
     * Obtiene el valor de la propiedad primerApellido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimerApellido() {
        return primerApellido;
    }

    /**
     * Define el valor de la propiedad primerApellido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimerApellido(String value) {
        this.primerApellido = value;
    }

    /**
     * Obtiene el valor de la propiedad segundoApellido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSegundoApellido() {
        return segundoApellido;
    }

    /**
     * Define el valor de la propiedad segundoApellido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSegundoApellido(String value) {
        this.segundoApellido = value;
    }

    /**
     * Obtiene el valor de la propiedad primerNombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimerNombre() {
        return primerNombre;
    }

    /**
     * Define el valor de la propiedad primerNombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimerNombre(String value) {
        this.primerNombre = value;
    }

    /**
     * Obtiene el valor de la propiedad segundoNombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSegundoNombre() {
        return segundoNombre;
    }

    /**
     * Define el valor de la propiedad segundoNombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSegundoNombre(String value) {
        this.segundoNombre = value;
    }

    /**
     * Obtiene el valor de la propiedad pruebaCiudadania.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPruebaCiudadania() {
        return pruebaCiudadania;
    }

    /**
     * Define el valor de la propiedad pruebaCiudadania.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPruebaCiudadania(String value) {
        this.pruebaCiudadania = value;
    }

    /**
     * Obtiene el valor de la propiedad cedula.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Define el valor de la propiedad cedula.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCedula(String value) {
        this.cedula = value;
    }

}
