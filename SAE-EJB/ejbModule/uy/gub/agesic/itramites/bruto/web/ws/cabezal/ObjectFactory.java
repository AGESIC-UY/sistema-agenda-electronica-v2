
package uy.gub.agesic.itramites.bruto.web.ws.cabezal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uy.gub.agesic.itramites.bruto.web.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ResponseDTO_QNAME = new QName("http://ws.web.bruto.itramites.agesic.gub.uy/", "responseDTO");
    private final static QName _Persist_QNAME = new QName("http://ws.web.bruto.itramites.agesic.gub.uy/", "persist");
    private final static QName _PersistResponse_QNAME = new QName("http://ws.web.bruto.itramites.agesic.gub.uy/", "persistResponse");
    private final static QName _CabezalDTO_QNAME = new QName("http://ws.web.bruto.itramites.agesic.gub.uy/", "cabezalDTO");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uy.gub.agesic.itramites.bruto.web.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CabezalDTO }
     * 
     */
    public CabezalDTO createCabezalDTO() {
        return new CabezalDTO();
    }

    /**
     * Create an instance of {@link PersistResponse }
     * 
     */
    public PersistResponse createPersistResponse() {
        return new PersistResponse();
    }

    /**
     * Create an instance of {@link Persist }
     * 
     */
    public Persist createPersist() {
        return new Persist();
    }

    /**
     * Create an instance of {@link ResponseDTO }
     * 
     */
    public ResponseDTO createResponseDTO() {
        return new ResponseDTO();
    }

    /**
     * Create an instance of {@link CabezalResponseDTO }
     * 
     */
    public CabezalResponseDTO createCabezalResponseDTO() {
        return new CabezalResponseDTO();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.web.bruto.itramites.agesic.gub.uy/", name = "responseDTO")
    public JAXBElement<ResponseDTO> createResponseDTO(ResponseDTO value) {
        return new JAXBElement<ResponseDTO>(_ResponseDTO_QNAME, ResponseDTO.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Persist }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.web.bruto.itramites.agesic.gub.uy/", name = "persist")
    public JAXBElement<Persist> createPersist(Persist value) {
        return new JAXBElement<Persist>(_Persist_QNAME, Persist.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PersistResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.web.bruto.itramites.agesic.gub.uy/", name = "persistResponse")
    public JAXBElement<PersistResponse> createPersistResponse(PersistResponse value) {
        return new JAXBElement<PersistResponse>(_PersistResponse_QNAME, PersistResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CabezalDTO }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.web.bruto.itramites.agesic.gub.uy/", name = "cabezalDTO")
    public JAXBElement<CabezalDTO> createCabezalDTO(CabezalDTO value) {
        return new JAXBElement<CabezalDTO>(_CabezalDTO_QNAME, CabezalDTO.class, null, value);
    }

}
