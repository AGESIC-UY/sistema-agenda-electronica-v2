
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Disponibilidad;


/**
 * <p>Java class for marcarReservaDisponible complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="marcarReservaDisponible">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="disponibilidad" type="{http://montevideo.gub.uy/schema/sae/1.0/}disponibilidad" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "marcarReservaDisponible", propOrder = {
    "disponibilidad"
})
public class MarcarReservaDisponible {

    protected Disponibilidad disponibilidad;

    /**
     * Gets the value of the disponibilidad property.
     * 
     * @return
     *     possible object is
     *     {@link Disponibilidad }
     *     
     */
    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    /**
     * Sets the value of the disponibilidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link Disponibilidad }
     *     
     */
    public void setDisponibilidad(Disponibilidad value) {
        this.disponibilidad = value;
    }

}
