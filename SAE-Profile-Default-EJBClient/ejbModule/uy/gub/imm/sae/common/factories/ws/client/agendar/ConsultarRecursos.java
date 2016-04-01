
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Agenda;


/**
 * <p>Java class for consultarRecursos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarRecursos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agenda" type="{http://montevideo.gub.uy/schema/sae/1.0/}agenda" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarRecursos", propOrder = {
    "agenda"
})
public class ConsultarRecursos {

    protected Agenda agenda;

    /**
     * Gets the value of the agenda property.
     * 
     * @return
     *     possible object is
     *     {@link Agenda }
     *     
     */
    public Agenda getAgenda() {
        return agenda;
    }

    /**
     * Sets the value of the agenda property.
     * 
     * @param value
     *     allowed object is
     *     {@link Agenda }
     *     
     */
    public void setAgenda(Agenda value) {
        this.agenda = value;
    }

}
