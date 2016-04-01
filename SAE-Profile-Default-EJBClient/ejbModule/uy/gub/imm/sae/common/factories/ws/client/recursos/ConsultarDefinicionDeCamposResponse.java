
package uy.gub.imm.sae.common.factories.ws.client.recursos;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import uy.gub.imm.sae.entity.AgrupacionDato;


/**
 * <p>Java class for consultarDefinicionDeCamposResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="consultarDefinicionDeCamposResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://montevideo.gub.uy/schema/sae/1.0/}agrupacionDato" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultarDefinicionDeCamposResponse", propOrder = {
    "result"
})
public class ConsultarDefinicionDeCamposResponse {

    @XmlElement(name = "Result")
    protected List<AgrupacionDato> result;

    /**
     * Gets the value of the result property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the result property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AgrupacionDato }
     * 
     * 
     */
    public List<AgrupacionDato> getResult() {
        if (result == null) {
            result = new ArrayList<AgrupacionDato>();
        }
        return this.result;
    }

}
