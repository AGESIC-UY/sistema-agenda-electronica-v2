
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.Recurso;


/**
 * <p>Java class for consultarRecursosResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarRecursosResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="consultarRecursosResult" type="{http://montevideo.gub.uy/schema/sae/1.0/}recurso" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarRecursosResponse", propOrder = {
    "consultarRecursosResult"
})
public class ConsultarRecursosResponse {

    protected List<Recurso> consultarRecursosResult;

    /**
     * Gets the value of the consultarRecursosResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the consultarRecursosResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConsultarRecursosResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Recurso }
     * 
     * 
     */
    public List<Recurso> getConsultarRecursosResult() {
        if (consultarRecursosResult == null) {
            consultarRecursosResult = new ArrayList<Recurso>();
        }
        return this.consultarRecursosResult;
    }

}
