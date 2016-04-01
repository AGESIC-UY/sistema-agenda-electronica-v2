
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;


/**
 * <p>Java class for consultarReservaPorDatosClave complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarReservaPorDatosClave">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="recurso" type="{http://montevideo.gub.uy/schema/sae/1.0/}recurso" minOccurs="0"/>
 *         &lt;element name="datos" type="{http://montevideo.gub.uy/schema/sae/1.0/}hashMap" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarReservaPorDatosClave", propOrder = {
    "recurso",
    "datos"
})
public class ConsultarReservaPorDatosClave {

    protected Recurso recurso;
    protected HashMap<DatoASolicitar, DatoReserva> datos;

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

    /**
     * Gets the value of the datos property.
     * 
     * @return
     *     possible object is
     *     {@link HashMap }
     *     
     */
    public HashMap<DatoASolicitar, DatoReserva> getDatos() {
        return datos;
    }

    /**
     * Sets the value of the datos property.
     * 
     * @param value
     *     allowed object is
     *     {@link HashMap }
     *     
     */
    public void setDatos(HashMap<DatoASolicitar, DatoReserva> value) {
        this.datos = value;
    }

}
