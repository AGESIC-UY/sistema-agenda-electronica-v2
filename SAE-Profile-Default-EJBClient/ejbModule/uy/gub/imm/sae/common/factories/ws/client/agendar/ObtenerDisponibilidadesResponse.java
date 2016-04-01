
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Disponibilidad;


/**
 * <p>Java class for obtenerDisponibilidadesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obtenerDisponibilidadesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="obtenerDisponibilidadesResult" type="{http://montevideo.gub.uy/schema/sae/1.0/}disponibilidad" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerDisponibilidadesResponse", propOrder = {
    "obtenerDisponibilidadesResult"
})
public class ObtenerDisponibilidadesResponse {

    protected List<Disponibilidad> obtenerDisponibilidadesResult;

    /**
     * Gets the value of the obtenerDisponibilidadesResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the obtenerDisponibilidadesResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObtenerDisponibilidadesResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Disponibilidad }
     * 
     * 
     */
    public List<Disponibilidad> getObtenerDisponibilidadesResult() {
        if (obtenerDisponibilidadesResult == null) {
            obtenerDisponibilidadesResult = new ArrayList<Disponibilidad>();
        }
        return this.obtenerDisponibilidadesResult;
    }

}
