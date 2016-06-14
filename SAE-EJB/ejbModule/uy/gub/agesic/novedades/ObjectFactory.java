
package uy.gub.agesic.novedades;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uy.gub.agesic.sae.novedades package. 
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

    private final static QName _PublicarResponse_QNAME = new QName("http://novedades.sae.agesic.gub.uy/", "publicarResponse");
    private final static QName _Publicar_QNAME = new QName("http://novedades.sae.agesic.gub.uy/", "publicar");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uy.gub.agesic.sae.novedades
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PublicarResponse }
     * 
     */
    public PublicarResponse createPublicarResponse() {
        return new PublicarResponse();
    }

    /**
     * Create an instance of {@link Publicar }
     * 
     */
    public Publicar createPublicar() {
        return new Publicar();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublicarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://novedades.sae.agesic.gub.uy/", name = "publicarResponse")
    public JAXBElement<PublicarResponse> createPublicarResponse(PublicarResponse value) {
        return new JAXBElement<PublicarResponse>(_PublicarResponse_QNAME, PublicarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Publicar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://novedades.sae.agesic.gub.uy/", name = "publicar")
    public JAXBElement<Publicar> createPublicar(Publicar value) {
        return new JAXBElement<Publicar>(_Publicar_QNAME, Publicar.class, null, value);
    }

}
