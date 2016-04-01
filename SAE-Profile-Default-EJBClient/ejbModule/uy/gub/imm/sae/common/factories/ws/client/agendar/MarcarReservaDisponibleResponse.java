
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Reserva;


/**
 * <p>Java class for marcarReservaDisponibleResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="marcarReservaDisponibleResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="marcarReservaDisponibleResult" type="{http://montevideo.gub.uy/schema/sae/1.0/}reserva" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "marcarReservaDisponibleResponse", propOrder = {
    "marcarReservaDisponibleResult"
})
public class MarcarReservaDisponibleResponse {

    protected Reserva marcarReservaDisponibleResult;

    /**
     * Gets the value of the marcarReservaDisponibleResult property.
     * 
     * @return
     *     possible object is
     *     {@link Reserva }
     *     
     */
    public Reserva getMarcarReservaDisponibleResult() {
        return marcarReservaDisponibleResult;
    }

    /**
     * Sets the value of the marcarReservaDisponibleResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reserva }
     *     
     */
    public void setMarcarReservaDisponibleResult(Reserva value) {
        this.marcarReservaDisponibleResult = value;
    }

}
