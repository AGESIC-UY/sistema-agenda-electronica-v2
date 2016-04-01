
package uy.gub.imm.sae.common.factories.ws.client.recursos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Recurso;


/**
 * <p>Java class for consultarDatosDelRecurso complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarDatosDelRecurso">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="recurso" type="{http://montevideo.gub.uy/schema/sae/1.0/}recurso" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarDatosDelRecurso", propOrder = {
    "recurso"
})
public class ConsultarDatosDelRecurso {

    protected Recurso recurso;

    /**
     * Gets the value of the recurso property.
     * 
     * @return
     *     possible object is
     *     {@link Recurso }
     *     
     */
    public Recurso getRecurso() {
        return recurso;
    }

    /**
     * Sets the value of the recurso property.
     * 
     * @param value
     *     allowed object is
     *     {@link Recurso }
     *     
     */
    public void setRecurso(Recurso value) {
        this.recurso = value;
    }

}
