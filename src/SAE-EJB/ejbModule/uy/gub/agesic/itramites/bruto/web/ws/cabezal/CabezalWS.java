
package uy.gub.agesic.itramites.bruto.web.ws.cabezal;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * La numeración de los enumerados está basada en el modelo de trazas definido por Unit
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "CabezalWS", targetNamespace = "http://ws.web.bruto.itramites.agesic.gub.uy/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CabezalWS {


    /**
     * 
     * @param traza
     * @return
     *     returns uy.gub.agesic.itramites.bruto.web.ws.CabezalResponseDTO
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "persist", targetNamespace = "http://ws.web.bruto.itramites.agesic.gub.uy/", className = "uy.gub.agesic.itramites.bruto.web.ws.Persist")
    @ResponseWrapper(localName = "persistResponse", targetNamespace = "http://ws.web.bruto.itramites.agesic.gub.uy/", className = "uy.gub.agesic.itramites.bruto.web.ws.PersistResponse")
    public CabezalResponseDTO persist(
        @WebParam(name = "traza", targetNamespace = "")
        CabezalDTO traza);

}