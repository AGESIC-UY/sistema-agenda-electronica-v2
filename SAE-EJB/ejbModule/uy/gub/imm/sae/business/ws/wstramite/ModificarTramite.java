
package uy.gub.imm.sae.business.ws.wstramite;

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
 *         &lt;element name="pTramiteEstandar" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="pUE" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pOrg" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "pTramiteEstandar",
    "pue",
    "pOrg",
    "pUsuario",
    "pPassword"
})
@XmlRootElement(name = "ModificarTramite")
public class ModificarTramite {

    protected byte[] pTramiteEstandar;
    @XmlElement(name = "pUE")
    protected int pue;
    protected int pOrg;
    protected String pUsuario;
    protected String pPassword;

    /**
     * Gets the value of the pTramiteEstandar property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getPTramiteEstandar() {
        return pTramiteEstandar;
    }

    /**
     * Sets the value of the pTramiteEstandar property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setPTramiteEstandar(byte[] value) {
        this.pTramiteEstandar = value;
    }

    /**
     * Gets the value of the pue property.
     * 
     */
    public int getPUE() {
        return pue;
    }

    /**
     * Sets the value of the pue property.
     * 
     */
    public void setPUE(int value) {
        this.pue = value;
    }

    /**
     * Gets the value of the pOrg property.
     * 
     */
    public int getPOrg() {
        return pOrg;
    }

    /**
     * Sets the value of the pOrg property.
     * 
     */
    public void setPOrg(int value) {
        this.pOrg = value;
    }

    /**
     * Gets the value of the pUsuario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPUsuario() {
        return pUsuario;
    }

    /**
     * Sets the value of the pUsuario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPUsuario(String value) {
        this.pUsuario = value;
    }

    /**
     * Gets the value of the pPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPPassword() {
        return pPassword;
    }

    /**
     * Sets the value of the pPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPPassword(String value) {
        this.pPassword = value;
    }

}
