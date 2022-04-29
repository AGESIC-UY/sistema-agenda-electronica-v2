
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for obtTramitesEnOrdenAlfabeticoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obtTramitesEnOrdenAlfabeticoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="letra" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pagina" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cantidadEltos" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="campoOrden" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="metodoOrden" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtTramitesEnOrdenAlfabeticoType", propOrder = {
    "letra",
    "pagina",
    "cantidadEltos",
    "campoOrden",
    "metodoOrden"
})
public class ObtTramitesEnOrdenAlfabeticoType {

    protected String letra;
    protected int pagina;
    protected int cantidadEltos;
    protected String campoOrden;
    protected String metodoOrden;

    /**
     * Gets the value of the letra property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLetra() {
        return letra;
    }

    /**
     * Sets the value of the letra property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLetra(String value) {
        this.letra = value;
    }

    /**
     * Gets the value of the pagina property.
     * 
     */
    public int getPagina() {
        return pagina;
    }

    /**
     * Sets the value of the pagina property.
     * 
     */
    public void setPagina(int value) {
        this.pagina = value;
    }

    /**
     * Gets the value of the cantidadEltos property.
     * 
     */
    public int getCantidadEltos() {
        return cantidadEltos;
    }

    /**
     * Sets the value of the cantidadEltos property.
     * 
     */
    public void setCantidadEltos(int value) {
        this.cantidadEltos = value;
    }

    /**
     * Gets the value of the campoOrden property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoOrden() {
        return campoOrden;
    }

    /**
     * Sets the value of the campoOrden property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoOrden(String value) {
        this.campoOrden = value;
    }

    /**
     * Gets the value of the metodoOrden property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetodoOrden() {
        return metodoOrden;
    }

    /**
     * Sets the value of the metodoOrden property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetodoOrden(String value) {
        this.metodoOrden = value;
    }

}
