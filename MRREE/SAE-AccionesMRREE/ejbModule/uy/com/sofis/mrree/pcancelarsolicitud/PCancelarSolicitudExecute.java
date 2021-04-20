
package uy.com.sofis.mrree.pcancelarsolicitud;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Solicitudnumero" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "solicitudnumero"
})
@XmlRootElement(name = "PCancelarSolicitud.Execute")
public class PCancelarSolicitudExecute {

    @XmlElement(name = "Solicitudnumero", required = true)
    protected String solicitudnumero;

    /**
     * Gets the value of the solicitudnumero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSolicitudnumero() {
        return solicitudnumero;
    }

    /**
     * Sets the value of the solicitudnumero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSolicitudnumero(String value) {
        this.solicitudnumero = value;
    }

}
