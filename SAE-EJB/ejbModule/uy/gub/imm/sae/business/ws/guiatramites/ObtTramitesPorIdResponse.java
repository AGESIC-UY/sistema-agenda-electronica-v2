
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for obtTramitesPorIdResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obtTramitesPorIdResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="datosTramite" type="{http://tempuri.org/}TramiteCompleto" minOccurs="0"/>
 *         &lt;element name="advertencias" type="{http://tempuri.org/}ArrayOfMensaje" minOccurs="0"/>
 *         &lt;element name="errores" type="{http://tempuri.org/}ArrayOfMensaje" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtTramitesPorIdResponse", propOrder = {
    "datosTramite",
    "advertencias",
    "errores"
})
public class ObtTramitesPorIdResponse {

    protected TramiteCompleto datosTramite;
    protected ArrayOfMensaje advertencias;
    protected ArrayOfMensaje errores;

    /**
     * Gets the value of the datosTramite property.
     * 
     * @return
     *     possible object is
     *     {@link TramiteCompleto }
     *     
     */
    public TramiteCompleto getDatosTramite() {
        return datosTramite;
    }

    /**
     * Sets the value of the datosTramite property.
     * 
     * @param value
     *     allowed object is
     *     {@link TramiteCompleto }
     *     
     */
    public void setDatosTramite(TramiteCompleto value) {
        this.datosTramite = value;
    }

    /**
     * Gets the value of the advertencias property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public ArrayOfMensaje getAdvertencias() {
        return advertencias;
    }

    /**
     * Sets the value of the advertencias property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public void setAdvertencias(ArrayOfMensaje value) {
        this.advertencias = value;
    }

    /**
     * Gets the value of the errores property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public ArrayOfMensaje getErrores() {
        return errores;
    }

    /**
     * Sets the value of the errores property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMensaje }
     *     
     */
    public void setErrores(ArrayOfMensaje value) {
        this.errores = value;
    }

}
