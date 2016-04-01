
package uy.gub.imm.sae.business.ws.guiatramites;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="pIdTramite" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "pIdTramite"
})
@XmlRootElement(name = "obtStatusPorIdTramite")
public class ObtStatusPorIdTramite {

    protected int pIdTramite;

    /**
     * Gets the value of the pIdTramite property.
     * 
     */
    public int getPIdTramite() {
        return pIdTramite;
    }

    /**
     * Sets the value of the pIdTramite property.
     * 
     */
    public void setPIdTramite(int value) {
        this.pIdTramite = value;
    }

}
