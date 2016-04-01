
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Agenda;


/**
 * <p>Java class for consultarAgendaPorNombreResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarAgendaPorNombreResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="consultarAgendaPorNombreResult" type="{http://montevideo.gub.uy/schema/sae/1.0/}agenda" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarAgendaPorNombreResponse", propOrder = {
    "consultarAgendaPorNombreResult"
})
public class ConsultarAgendaPorNombreResponse {

    protected Agenda consultarAgendaPorNombreResult;

    /**
     * Gets the value of the consultarAgendaPorNombreResult property.
     * 
     * @return
     *     possible object is
     *     {@link Agenda }
     *     
     */
    public Agenda getConsultarAgendaPorNombreResult() {
        return consultarAgendaPorNombreResult;
    }

    /**
     * Sets the value of the consultarAgendaPorNombreResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Agenda }
     *     
     */
    public void setConsultarAgendaPorNombreResult(Agenda value) {
        this.consultarAgendaPorNombreResult = value;
    }

}
