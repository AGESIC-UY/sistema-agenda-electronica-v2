
package uy.gub.imm.sae.common.factories.ws.client.agendar;

import java.util.HashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.Atencion;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValorPosible;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uy.gub.imm.sae.common.factories.ws.client.agendar package. 
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

    private final static QName _ErrorValidacionException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ErrorValidacionException");
    private final static QName _ObtenerDisponibilidades_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "obtenerDisponibilidades");
    private final static QName _AccesoMultipleException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "AccesoMultipleException");
    private final static QName _ObtenerCuposPorDia_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "obtenerCuposPorDia");
    private final static QName _Ping_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ping");
    private final static QName _ApplicationException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ApplicationException");
    private final static QName _ConfirmarReservaResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "confirmarReservaResponse");
    private final static QName _ConsultarAgendaPorNombreResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarAgendaPorNombreResponse");
    private final static QName _UserException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "UserException");
    private final static QName _ValidacionPorCampoException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ValidacionPorCampoException");
    private final static QName _ConsultarReservaPorDatosClave_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarReservaPorDatosClave");
    private final static QName _ValidacionClaveUnicaException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ValidacionClaveUnicaException");
    private final static QName _MarcarReservaDisponibleResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "marcarReservaDisponibleResponse");
    private final static QName _MarcarReservaDisponible_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "marcarReservaDisponible");
    private final static QName _BusinessException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "BusinessException");
    private final static QName _ObtenerCuposPorDiaResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "obtenerCuposPorDiaResponse");
    private final static QName _UserCommitException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "UserCommitException");
    private final static QName _WarningValidacionException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "WarningValidacionException");
    private final static QName _ConfirmarReserva_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "confirmarReserva");
    private final static QName _ConsultarRecursosResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarRecursosResponse");
    private final static QName _ConsultarRecursos_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarRecursos");
    private final static QName _ConsultarReservaPorDatosClaveResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarReservaPorDatosClaveResponse");
    private final static QName _DesmarcarReservaResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "desmarcarReservaResponse");
    private final static QName _ObtenerVentanaCalendarioInternetResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "obtenerVentanaCalendarioInternetResponse");
    private final static QName _ObtenerVentanaCalendarioInternet_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "obtenerVentanaCalendarioInternet");
    private final static QName _ErrorValidacionCommitException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ErrorValidacionCommitException");
    private final static QName _ConsultarAgendaPorNombre_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "consultarAgendaPorNombre");
    private final static QName _DesmarcarReserva_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "desmarcarReserva");
    private final static QName _PingResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "pingResponse");
    private final static QName _WarningValidacionCommitException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "WarningValidacionCommitException");
    private final static QName _ObtenerDisponibilidadesResponse_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "obtenerDisponibilidadesResponse");
    private final static QName _ValidacionException_QNAME = new QName("http://montevideo.gub.uy/schema/sae/1.0/", "ValidacionException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uy.gub.imm.sae.common.factories.ws.client.agendar
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DesmarcarReserva }
     * 
     */
    public DesmarcarReserva createDesmarcarReserva() {
        return new DesmarcarReserva();
    }

    /**
     * Create an instance of {@link MarcarReservaDisponibleResponse }
     * 
     */
    public MarcarReservaDisponibleResponse createMarcarReservaDisponibleResponse() {
        return new MarcarReservaDisponibleResponse();
    }

    /**
     * Create an instance of {@link ObtenerCuposPorDia }
     * 
     */
    public ObtenerCuposPorDia createObtenerCuposPorDia() {
        return new ObtenerCuposPorDia();
    }

    /**
     * Create an instance of {@link ObtenerCuposPorDiaResponse }
     * 
     */
    public ObtenerCuposPorDiaResponse createObtenerCuposPorDiaResponse() {
        return new ObtenerCuposPorDiaResponse();
    }

    /**
     * Create an instance of {@link ConsultarReservaPorDatosClaveResponse }
     * 
     */
    public ConsultarReservaPorDatosClaveResponse createConsultarReservaPorDatosClaveResponse() {
        return new ConsultarReservaPorDatosClaveResponse();
    }

    /**
     * Create an instance of {@link ObtenerVentanaCalendarioInternetResponse }
     * 
     */
    public ObtenerVentanaCalendarioInternetResponse createObtenerVentanaCalendarioInternetResponse() {
        return new ObtenerVentanaCalendarioInternetResponse();
    }

    /**
     * Create an instance of {@link ValidacionException }
     * 
     */
    public ValidacionException createValidacionException() {
        return new ValidacionException();
    }

    /**
     * Create an instance of {@link AccesoMultipleException }
     * 
     */
    public AccesoMultipleException createAccesoMultipleException() {
        return new AccesoMultipleException();
    }

    /**
     * Create an instance of {@link ConfirmarReservaResponse }
     * 
     */
    public ConfirmarReservaResponse createConfirmarReservaResponse() {
        return new ConfirmarReservaResponse();
    }

    /**
     * Create an instance of {@link AgrupacionDato }
     * 
     */
    public AgrupacionDato createAgrupacionDato() {
        return new AgrupacionDato();
    }

    /**
     * Create an instance of {@link TextoAgenda }
     * 
     */
    public TextoAgenda createTextoAgenda() {
        return new TextoAgenda();
    }

    /**
     * Create an instance of {@link Ping }
     * 
     */
    public Ping createPing() {
        return new Ping();
    }

    /**
     * Create an instance of {@link DatoReserva }
     * 
     */
    public DatoReserva createDatoReserva() {
        return new DatoReserva();
    }

    /**
     * Create an instance of {@link ConsultarReservaPorDatosClave }
     * 
     */
    public ConsultarReservaPorDatosClave createConsultarReservaPorDatosClave() {
        return new ConsultarReservaPorDatosClave();
    }

    /**
     * Create an instance of {@link ConsultarRecursos }
     * 
     */
    public ConsultarRecursos createConsultarRecursos() {
        return new ConsultarRecursos();
    }

    /**
     * Create an instance of {@link ObtenerDisponibilidades }
     * 
     */
    public ObtenerDisponibilidades createObtenerDisponibilidades() {
        return new ObtenerDisponibilidades();
    }

    /**
     * Create an instance of {@link WarningValidacionCommitException }
     * 
     */
    public WarningValidacionCommitException createWarningValidacionCommitException() {
        return new WarningValidacionCommitException();
    }

    /**
     * Create an instance of {@link UserCommitException }
     * 
     */
    public UserCommitException createUserCommitException() {
        return new UserCommitException();
    }

    /**
     * Create an instance of {@link ErrorValidacionCommitException }
     * 
     */
    public ErrorValidacionCommitException createErrorValidacionCommitException() {
        return new ErrorValidacionCommitException();
    }

    /**
     * Create an instance of {@link ObtenerDisponibilidadesResponse }
     * 
     */
    public ObtenerDisponibilidadesResponse createObtenerDisponibilidadesResponse() {
        return new ObtenerDisponibilidadesResponse();
    }

    /**
     * Create an instance of {@link ConsultarRecursosResponse }
     * 
     */
    public ConsultarRecursosResponse createConsultarRecursosResponse() {
        return new ConsultarRecursosResponse();
    }

    /**
     * Create an instance of {@link ConfirmarReserva }
     * 
     */
    public ConfirmarReserva createConfirmarReserva() {
        return new ConfirmarReserva();
    }

    /**
     * Create an instance of {@link VentanaDeTiempo }
     * 
     */
    public VentanaDeTiempo createVentanaDeTiempo() {
        return new VentanaDeTiempo();
    }

    /**
     * Create an instance of {@link WarningValidacionException }
     * 
     */
    public WarningValidacionException createWarningValidacionException() {
        return new WarningValidacionException();
    }

    /**
     * Create an instance of {@link Reserva }
     * 
     */
    public Reserva createReserva() {
        return new Reserva();
    }

    /**
     * Create an instance of {@link MarcarReservaDisponible }
     * 
     */
    public MarcarReservaDisponible createMarcarReservaDisponible() {
        return new MarcarReservaDisponible();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap<DatoASolicitar, DatoReserva> createHashMap() {
        return new HashMap<DatoASolicitar, DatoReserva>();
    }

    /**
     * Create an instance of {@link ValidacionPorCampoException }
     * 
     */
    public ValidacionPorCampoException createValidacionPorCampoException() {
        return new ValidacionPorCampoException();
    }

    /**
     * Create an instance of {@link UserException }
     * 
     */
    public UserException createUserException() {
        return new UserException();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link Atencion }
     * 
     */
    public Atencion createAtencion() {
        return new Atencion();
    }

    /**
     * Create an instance of {@link ValidacionClaveUnicaException }
     * 
     */
    public ValidacionClaveUnicaException createValidacionClaveUnicaException() {
        return new ValidacionClaveUnicaException();
    }

    /**
     * Create an instance of {@link ConsultarAgendaPorNombre }
     * 
     */
    public ConsultarAgendaPorNombre createConsultarAgendaPorNombre() {
        return new ConsultarAgendaPorNombre();
    }

    /**
     * Create an instance of {@link Disponibilidad }
     * 
     */
    public Disponibilidad createDisponibilidad() {
        return new Disponibilidad();
    }

    /**
     * Create an instance of {@link BusinessException }
     * 
     */
    public BusinessException createBusinessException() {
        return new BusinessException();
    }

    /**
     * Create an instance of {@link DatoASolicitar }
     * 
     */
    public DatoASolicitar createDatoASolicitar() {
        return new DatoASolicitar();
    }

    /**
     * Create an instance of {@link ValorPosible }
     * 
     */
    public ValorPosible createValorPosible() {
        return new ValorPosible();
    }

    /**
     * Create an instance of {@link TextoRecurso }
     * 
     */
    public TextoRecurso createTextoRecurso() {
        return new TextoRecurso();
    }

    /**
     * Create an instance of {@link DesmarcarReservaResponse }
     * 
     */
    public DesmarcarReservaResponse createDesmarcarReservaResponse() {
        return new DesmarcarReservaResponse();
    }

    /**
     * Create an instance of {@link Recurso }
     * 
     */
    public Recurso createRecurso() {
        return new Recurso();
    }

    /**
     * Create an instance of {@link DatoDelRecurso }
     * 
     */
    public DatoDelRecurso createDatoDelRecurso() {
        return new DatoDelRecurso();
    }

    /**
     * Create an instance of {@link ObtenerVentanaCalendarioInternet }
     * 
     */
    public ObtenerVentanaCalendarioInternet createObtenerVentanaCalendarioInternet() {
        return new ObtenerVentanaCalendarioInternet();
    }

    /**
     * Create an instance of {@link ApplicationException }
     * 
     */
    public ApplicationException createApplicationException() {
        return new ApplicationException();
    }

    /**
     * Create an instance of {@link Agenda }
     * 
     */
    public Agenda createAgenda() {
        return new Agenda();
    }

    /**
     * Create an instance of {@link ErrorValidacionException }
     * 
     */
    public ErrorValidacionException createErrorValidacionException() {
        return new ErrorValidacionException();
    }

    /**
     * Create an instance of {@link ConsultarAgendaPorNombreResponse }
     * 
     */
    public ConsultarAgendaPorNombreResponse createConsultarAgendaPorNombreResponse() {
        return new ConsultarAgendaPorNombreResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorValidacionException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "ErrorValidacionException")
    public JAXBElement<ErrorValidacionException> createErrorValidacionException(ErrorValidacionException value) {
        return new JAXBElement<ErrorValidacionException>(_ErrorValidacionException_QNAME, ErrorValidacionException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerDisponibilidades }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "obtenerDisponibilidades")
    public JAXBElement<ObtenerDisponibilidades> createObtenerDisponibilidades(ObtenerDisponibilidades value) {
        return new JAXBElement<ObtenerDisponibilidades>(_ObtenerDisponibilidades_QNAME, ObtenerDisponibilidades.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccesoMultipleException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "AccesoMultipleException")
    public JAXBElement<AccesoMultipleException> createAccesoMultipleException(AccesoMultipleException value) {
        return new JAXBElement<AccesoMultipleException>(_AccesoMultipleException_QNAME, AccesoMultipleException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerCuposPorDia }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "obtenerCuposPorDia")
    public JAXBElement<ObtenerCuposPorDia> createObtenerCuposPorDia(ObtenerCuposPorDia value) {
        return new JAXBElement<ObtenerCuposPorDia>(_ObtenerCuposPorDia_QNAME, ObtenerCuposPorDia.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfirmarReservaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "confirmarReservaResponse")
    public JAXBElement<ConfirmarReservaResponse> createConfirmarReservaResponse(ConfirmarReservaResponse value) {
        return new JAXBElement<ConfirmarReservaResponse>(_ConfirmarReservaResponse_QNAME, ConfirmarReservaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarAgendaPorNombreResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarAgendaPorNombreResponse")
    public JAXBElement<ConsultarAgendaPorNombreResponse> createConsultarAgendaPorNombreResponse(ConsultarAgendaPorNombreResponse value) {
        return new JAXBElement<ConsultarAgendaPorNombreResponse>(_ConsultarAgendaPorNombreResponse_QNAME, ConsultarAgendaPorNombreResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "UserException")
    public JAXBElement<UserException> createUserException(UserException value) {
        return new JAXBElement<UserException>(_UserException_QNAME, UserException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidacionPorCampoException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "ValidacionPorCampoException")
    public JAXBElement<ValidacionPorCampoException> createValidacionPorCampoException(ValidacionPorCampoException value) {
        return new JAXBElement<ValidacionPorCampoException>(_ValidacionPorCampoException_QNAME, ValidacionPorCampoException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarReservaPorDatosClave }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarReservaPorDatosClave")
    public JAXBElement<ConsultarReservaPorDatosClave> createConsultarReservaPorDatosClave(ConsultarReservaPorDatosClave value) {
        return new JAXBElement<ConsultarReservaPorDatosClave>(_ConsultarReservaPorDatosClave_QNAME, ConsultarReservaPorDatosClave.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidacionClaveUnicaException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "ValidacionClaveUnicaException")
    public JAXBElement<ValidacionClaveUnicaException> createValidacionClaveUnicaException(ValidacionClaveUnicaException value) {
        return new JAXBElement<ValidacionClaveUnicaException>(_ValidacionClaveUnicaException_QNAME, ValidacionClaveUnicaException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MarcarReservaDisponibleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "marcarReservaDisponibleResponse")
    public JAXBElement<MarcarReservaDisponibleResponse> createMarcarReservaDisponibleResponse(MarcarReservaDisponibleResponse value) {
        return new JAXBElement<MarcarReservaDisponibleResponse>(_MarcarReservaDisponibleResponse_QNAME, MarcarReservaDisponibleResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MarcarReservaDisponible }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "marcarReservaDisponible")
    public JAXBElement<MarcarReservaDisponible> createMarcarReservaDisponible(MarcarReservaDisponible value) {
        return new JAXBElement<MarcarReservaDisponible>(_MarcarReservaDisponible_QNAME, MarcarReservaDisponible.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerCuposPorDiaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "obtenerCuposPorDiaResponse")
    public JAXBElement<ObtenerCuposPorDiaResponse> createObtenerCuposPorDiaResponse(ObtenerCuposPorDiaResponse value) {
        return new JAXBElement<ObtenerCuposPorDiaResponse>(_ObtenerCuposPorDiaResponse_QNAME, ObtenerCuposPorDiaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserCommitException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "UserCommitException")
    public JAXBElement<UserCommitException> createUserCommitException(UserCommitException value) {
        return new JAXBElement<UserCommitException>(_UserCommitException_QNAME, UserCommitException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WarningValidacionException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "WarningValidacionException")
    public JAXBElement<WarningValidacionException> createWarningValidacionException(WarningValidacionException value) {
        return new JAXBElement<WarningValidacionException>(_WarningValidacionException_QNAME, WarningValidacionException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfirmarReserva }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "confirmarReserva")
    public JAXBElement<ConfirmarReserva> createConfirmarReserva(ConfirmarReserva value) {
        return new JAXBElement<ConfirmarReserva>(_ConfirmarReserva_QNAME, ConfirmarReserva.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarRecursosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarRecursosResponse")
    public JAXBElement<ConsultarRecursosResponse> createConsultarRecursosResponse(ConsultarRecursosResponse value) {
        return new JAXBElement<ConsultarRecursosResponse>(_ConsultarRecursosResponse_QNAME, ConsultarRecursosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarRecursos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarRecursos")
    public JAXBElement<ConsultarRecursos> createConsultarRecursos(ConsultarRecursos value) {
        return new JAXBElement<ConsultarRecursos>(_ConsultarRecursos_QNAME, ConsultarRecursos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarReservaPorDatosClaveResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarReservaPorDatosClaveResponse")
    public JAXBElement<ConsultarReservaPorDatosClaveResponse> createConsultarReservaPorDatosClaveResponse(ConsultarReservaPorDatosClaveResponse value) {
        return new JAXBElement<ConsultarReservaPorDatosClaveResponse>(_ConsultarReservaPorDatosClaveResponse_QNAME, ConsultarReservaPorDatosClaveResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DesmarcarReservaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "desmarcarReservaResponse")
    public JAXBElement<DesmarcarReservaResponse> createDesmarcarReservaResponse(DesmarcarReservaResponse value) {
        return new JAXBElement<DesmarcarReservaResponse>(_DesmarcarReservaResponse_QNAME, DesmarcarReservaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerVentanaCalendarioInternetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "obtenerVentanaCalendarioInternetResponse")
    public JAXBElement<ObtenerVentanaCalendarioInternetResponse> createObtenerVentanaCalendarioInternetResponse(ObtenerVentanaCalendarioInternetResponse value) {
        return new JAXBElement<ObtenerVentanaCalendarioInternetResponse>(_ObtenerVentanaCalendarioInternetResponse_QNAME, ObtenerVentanaCalendarioInternetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerVentanaCalendarioInternet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "obtenerVentanaCalendarioInternet")
    public JAXBElement<ObtenerVentanaCalendarioInternet> createObtenerVentanaCalendarioInternet(ObtenerVentanaCalendarioInternet value) {
        return new JAXBElement<ObtenerVentanaCalendarioInternet>(_ObtenerVentanaCalendarioInternet_QNAME, ObtenerVentanaCalendarioInternet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorValidacionCommitException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "ErrorValidacionCommitException")
    public JAXBElement<ErrorValidacionCommitException> createErrorValidacionCommitException(ErrorValidacionCommitException value) {
        return new JAXBElement<ErrorValidacionCommitException>(_ErrorValidacionCommitException_QNAME, ErrorValidacionCommitException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultarAgendaPorNombre }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "consultarAgendaPorNombre")
    public JAXBElement<ConsultarAgendaPorNombre> createConsultarAgendaPorNombre(ConsultarAgendaPorNombre value) {
        return new JAXBElement<ConsultarAgendaPorNombre>(_ConsultarAgendaPorNombre_QNAME, ConsultarAgendaPorNombre.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DesmarcarReserva }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "desmarcarReserva")
    public JAXBElement<DesmarcarReserva> createDesmarcarReserva(DesmarcarReserva value) {
        return new JAXBElement<DesmarcarReserva>(_DesmarcarReserva_QNAME, DesmarcarReserva.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "pingResponse")
    public JAXBElement<PingResponse> createPingResponse(PingResponse value) {
        return new JAXBElement<PingResponse>(_PingResponse_QNAME, PingResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WarningValidacionCommitException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "WarningValidacionCommitException")
    public JAXBElement<WarningValidacionCommitException> createWarningValidacionCommitException(WarningValidacionCommitException value) {
        return new JAXBElement<WarningValidacionCommitException>(_WarningValidacionCommitException_QNAME, WarningValidacionCommitException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerDisponibilidadesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "obtenerDisponibilidadesResponse")
    public JAXBElement<ObtenerDisponibilidadesResponse> createObtenerDisponibilidadesResponse(ObtenerDisponibilidadesResponse value) {
        return new JAXBElement<ObtenerDisponibilidadesResponse>(_ObtenerDisponibilidadesResponse_QNAME, ObtenerDisponibilidadesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidacionException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://montevideo.gub.uy/schema/sae/1.0/", name = "ValidacionException")
    public JAXBElement<ValidacionException> createValidacionException(ValidacionException value) {
        return new JAXBElement<ValidacionException>(_ValidacionException_QNAME, ValidacionException.class, null, value);
    }

}
