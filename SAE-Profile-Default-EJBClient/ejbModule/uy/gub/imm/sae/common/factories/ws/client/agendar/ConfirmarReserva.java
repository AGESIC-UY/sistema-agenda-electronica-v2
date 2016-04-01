
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Reserva;


/**
 * <p>Java class for confirmarReserva complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmarReserva">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reserva" type="{http://montevideo.gub.uy/schema/sae/1.0/}reserva" minOccurs="0"/>
 *         &lt;element name="cancelarReservasPrevias" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="confirmarConWarning" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmarReserva", propOrder = {
    "reserva",
    "cancelarReservasPrevias",
    "confirmarConWarning"
})
public class ConfirmarReserva {

    protected Reserva reserva;
    protected Boolean cancelarReservasPrevias;
    protected Boolean confirmarConWarning;

    /**
     * Gets the value of the reserva property.
     * 
     * @return
     *     possible object is
     *     {@link Reserva }
     *     
     */
    public Reserva getReserva() {
        return reserva;
    }

    /**
     * Sets the value of the reserva property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reserva }
     *     
     */
    public void setReserva(Reserva value) {
        this.reserva = value;
    }

    /**
     * Gets the value of the cancelarReservasPrevias property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCancelarReservasPrevias() {
        return cancelarReservasPrevias;
    }

    /**
     * Sets the value of the cancelarReservasPrevias property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCancelarReservasPrevias(Boolean value) {
        this.cancelarReservasPrevias = value;
    }

    /**
     * Gets the value of the confirmarConWarning property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isConfirmarConWarning() {
        return confirmarConWarning;
    }

    /**
     * Sets the value of the confirmarConWarning property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConfirmarConWarning(Boolean value) {
        this.confirmarConWarning = value;
    }

}
