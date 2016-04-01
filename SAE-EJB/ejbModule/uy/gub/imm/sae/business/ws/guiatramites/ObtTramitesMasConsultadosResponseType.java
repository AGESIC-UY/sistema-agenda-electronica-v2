
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for obtTramitesMasConsultadosResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obtTramitesMasConsultadosResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="totalResultados" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="colTramites" type="{http://tempuri.org/}ArrayOfResumenTramite" minOccurs="0"/>
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
@XmlType(name = "obtTramitesMasConsultadosResponseType", propOrder = {
    "totalResultados",
    "colTramites",
    "advertencias",
    "errores"
})
public class ObtTramitesMasConsultadosResponseType {

    protected int totalResultados;
    protected ArrayOfResumenTramite colTramites;
    protected ArrayOfMensaje advertencias;
    protected ArrayOfMensaje errores;

    /**
     * Gets the value of the totalResultados property.
     * 
     */
    public int getTotalResultados() {
        return totalResultados;
    }

    /**
     * Sets the value of the totalResultados property.
     * 
     */
    public void setTotalResultados(int value) {
        this.totalResultados = value;
    }

    /**
     * Gets the value of the colTramites property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfResumenTramite }
     *     
     */
    public ArrayOfResumenTramite getColTramites() {
        return colTramites;
    }

    /**
     * Sets the value of the colTramites property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfResumenTramite }
     *     
     */
    public void setColTramites(ArrayOfResumenTramite value) {
        this.colTramites = value;
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
