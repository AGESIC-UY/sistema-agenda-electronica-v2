
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.common.VentanaDeTiempo;


/**
 * <p>Java class for obtenerVentanaCalendarioInternetResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obtenerVentanaCalendarioInternetResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="obtenerVentanaCalendarioInternetResult" type="{http://montevideo.gub.uy/schema/sae/1.0/}ventanaDeTiempo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerVentanaCalendarioInternetResponse", propOrder = {
    "obtenerVentanaCalendarioInternetResult"
})
public class ObtenerVentanaCalendarioInternetResponse {

    protected VentanaDeTiempo obtenerVentanaCalendarioInternetResult;

    /**
     * Gets the value of the obtenerVentanaCalendarioInternetResult property.
     * 
     * @return
     *     possible object is
     *     {@link VentanaDeTiempo }
     *     
     */
    public VentanaDeTiempo getObtenerVentanaCalendarioInternetResult() {
        return obtenerVentanaCalendarioInternetResult;
    }

    /**
     * Sets the value of the obtenerVentanaCalendarioInternetResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link VentanaDeTiempo }
     *     
     */
    public void setObtenerVentanaCalendarioInternetResult(VentanaDeTiempo value) {
        this.obtenerVentanaCalendarioInternetResult = value;
    }

}
