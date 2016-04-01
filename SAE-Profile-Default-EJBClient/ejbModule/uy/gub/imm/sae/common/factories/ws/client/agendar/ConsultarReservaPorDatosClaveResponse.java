
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Reserva;


/**
 * <p>Java class for consultarReservaPorDatosClaveResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarReservaPorDatosClaveResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="consultarReservaPorDatosClaveResult" type="{http://montevideo.gub.uy/schema/sae/1.0/}reserva" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarReservaPorDatosClaveResponse", propOrder = {
    "consultarReservaPorDatosClaveResult"
})
public class ConsultarReservaPorDatosClaveResponse {

    protected Reserva consultarReservaPorDatosClaveResult;

    /**
     * Gets the value of the consultarReservaPorDatosClaveResult property.
     * 
     * @return
     *     possible object is
     *     {@link Reserva }
     *     
     */
    public Reserva getConsultarReservaPorDatosClaveResult() {
        return consultarReservaPorDatosClaveResult;
    }

    /**
     * Sets the value of the consultarReservaPorDatosClaveResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reserva }
     *     
     */
    public void setConsultarReservaPorDatosClaveResult(Reserva value) {
        this.consultarReservaPorDatosClaveResult = value;
    }

}
