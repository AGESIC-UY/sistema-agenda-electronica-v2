
package uy.gub.imm.sae.common.factories.ws.client.recursos;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValorPosible;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uy.gub.imm.sae.common.factories.ws.client.recursos package. 
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

    private final static QName _BusinessException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "BusinessException");
    private final static QName _ConsultarDatosDelRecurso_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarDatosDelRecurso");
    private final static QName _ConsultarDefinicionDeCamposResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarDefinicionDeCamposResponse");
    private final static QName _ConsultarDefinicionDeCampos_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarDefinicionDeCampos");
    private final static QName _ConsultarDatosDelRecursoResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarDatosDelRecursoResponse");
    private final static QName _Ping_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ping");
    private final static QName _ApplicationException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ApplicationException");
    private final static QName _PingResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "pingResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uy.gub.imm.sae.common.factories.ws.client.recursos
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Recurso }
     * 
     */
    public Recurso createRecurso() {
        return new Recurso();
    }

    /**
     * Create an instance of {@link ConsultarDatosDelRecursoResponse }
     * 
     */
    public ConsultarDatosDelRecursoResponse createConsultarDatosDelRecursoResponse() {
        return new ConsultarDatosDelRecursoResponse();
    }

    /**
     * Create an instance of {@link ApplicationException }
     * 
     */
    public ApplicationException createApplicationException() {
        return new ApplicationException();
    }

    /**
     * Create an instance of {@link ConsultarDefinicionDeCamposResponse }
     * 
     */
    public ConsultarDefinicionDeCamposResponse createConsultarDefinicionDeCamposResponse() {
        return new ConsultarDefinicionDeCamposResponse();
    }

    /**
     * Create an instance of {@link TextoRecurso }
     * 
     */
    public TextoRecurso createTextoRecurso() {
        return new TextoRecurso();
    }

    /**
     * Create an instance of {@link DatoDelRecurso }
     * 
     */
    public DatoDelRecurso createDatoDelRecurso() {
        return new DatoDelRecurso();
    }

    /**
     * Create an instance of {@link ValorPosible }
     * 
     */
    public ValorPosible createValorPosible() {
        return new ValorPosible();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link DatoASolicitar }
     * 
     */
    public DatoASolicitar createDatoASolicitar() {
        return new DatoASolicitar();
    }

    /**
     * Create an instance of {@link ConsultarDatosDelRecurso }
     * 
     */
    public ConsultarDatosDelRecurso createConsultarDatosDelRecurso() {
        return new ConsultarDatosDelRecurso();
    }

    /**
     * Create an instance of {@link BusinessException }
     * 
     */
    public BusinessException createBusinessException() {
        return new BusinessException();
    }

    /**
     * Create an instance of {@link ConsultarDefinicionDeCampos }
     * 
     */
    public ConsultarDefinicionDeCampos createConsultarDefinicionDeCampos() {
        return new ConsultarDefinicionDeCampos();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link AgrupacionDato }
     * 
     */
    public AgrupacionDato createAgrupacionDato() {
        return new AgrupacionDato();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BusinessException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "BusinessException")
    public JAXBElement<BusinessException> createBusinessException(BusinessException value) {
        return new JAXBElement<BusinessException>(_BusinessException_QNAME, BusinessException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarDatosDelRecurso }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarDatosDelRecurso")
    public JAXBElement<ConsultarDatosDelRecurso> createConsultarDatosDelRecurso(ConsultarDatosDelRecurso value) {
        return new JAXBElement<ConsultarDatosDelRecurso>(_ConsultarDatosDelRecurso_QNAME, ConsultarDatosDelRecurso.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarDefinicionDeCamposResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarDefinicionDeCamposResponse")
    public JAXBElement<ConsultarDefinicionDeCamposResponse> createConsultarDefinicionDeCamposResponse(ConsultarDefinicionDeCamposResponse value) {
        return new JAXBElement<ConsultarDefinicionDeCamposResponse>(_ConsultarDefinicionDeCamposResponse_QNAME, ConsultarDefinicionDeCamposResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarDefinicionDeCampos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarDefinicionDeCampos")
    public JAXBElement<ConsultarDefinicionDeCampos> createConsultarDefinicionDeCampos(ConsultarDefinicionDeCampos value) {
        return new JAXBElement<ConsultarDefinicionDeCampos>(_ConsultarDefinicionDeCampos_QNAME, ConsultarDefinicionDeCampos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarDatosDelRecursoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarDatosDelRecursoResponse")
    public JAXBElement<ConsultarDatosDelRecursoResponse> createConsultarDatosDelRecursoResponse(ConsultarDatosDelRecursoResponse value) {
        return new JAXBElement<ConsultarDatosDelRecursoResponse>(_ConsultarDatosDelRecursoResponse_QNAME, ConsultarDatosDelRecursoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "ping")
    public JAXBElement<Ping> createPing(Ping value) {
        return new JAXBElement<Ping>(_Ping_QNAME, Ping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ApplicationException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "ApplicationException")
    public JAXBElement<ApplicationException> createApplicationException(ApplicationException value) {
        return new JAXBElement<ApplicationException>(_ApplicationException_QNAME, ApplicationException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "pingResponse")
    public JAXBElement<PingResponse> createPingResponse(PingResponse value) {
        return new JAXBElement<PingResponse>(_PingResponse_QNAME, PingResponse.class, null, value);
    }

}
