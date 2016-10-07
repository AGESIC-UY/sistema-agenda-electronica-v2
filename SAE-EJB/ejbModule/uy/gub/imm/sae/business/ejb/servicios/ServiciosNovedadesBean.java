package uy.gub.imm.sae.business.ejb.servicios;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
/* */
import javax.xml.ws.BindingProvider;
import javax.xml.ws.addressing.AddressingBuilder;
import javax.xml.ws.addressing.JAXWSAConstants;
import javax.xml.ws.addressing.soap.SOAPAddressingBuilder;
import javax.xml.ws.addressing.soap.SOAPAddressingProperties;
import javax.xml.ws.handler.Handler;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;
import org.jboss.ws.extensions.addressing.AttributedURIImpl;

import uy.gub.agesic.AgesicConstants;
import uy.gub.agesic.beans.RSTBean;
import uy.gub.agesic.beans.SAMLAssertion;
import uy.gub.agesic.beans.StoreBean;
import uy.gub.agesic.jbossws.SAMLHandler;
import uy.gub.agesic.jbossws.WSAddressingHandler;
import uy.gub.agesic.novedades.Acciones;
import uy.gub.agesic.novedades.NuevaNovedadService;
import uy.gub.agesic.novedades.NuevaNovedadService_Service;
import uy.gub.agesic.novedades.ObjectFactory;
import uy.gub.agesic.novedades.Publicar;
import uy.gub.agesic.novedades.PublishSubscribeHeadersHandler;
import uy.gub.agesic.sts.client.PGEClient;
import uy.gub.imm.sae.business.ejb.facade.ConfiguracionBean;
import uy.gub.imm.sae.business.ws.SoapHandler;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Novedad;

@Stateless
public class ServiciosNovedadesBean {

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;

	@EJB
	private ConfiguracionBean confBean;
	
	private static Logger logger = Logger.getLogger(ServiciosNovedadesBean.class);


	/**
	 * @param empresa
	 * @param reserva
	 * @param transaccionId
	 * @param idOficina
	 * @param paso
	 */
	public void publicarNovedad(Empresa empresa, Reserva reserva, Acciones accion) {
		
		Disponibilidad dispon = reserva.getDisponibilidades().get(0);
		Recurso recurso = dispon.getRecurso();
		Agenda agenda = recurso.getAgenda();
		
		boolean habilitado = false;
		if(agenda.getPublicarNovedades()!=null) {
			habilitado = agenda.getPublicarNovedades().booleanValue();
		}
		if(!habilitado) {
			return;
		}
		try {
			habilitado = confBean.getBoolean("WS_NOVEDADES_HABILITADO");
		} catch (NumberFormatException nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
			return;
		}

		ObjectFactory objFactory = new ObjectFactory();
		Publicar novedad = objFactory.createPublicar();

		try {
			novedad.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		}catch(Exception ex) {
			novedad.setTimestamp(null);
		}
		novedad.setTipoDocumento(reserva.getTipoDocumento());
		novedad.setPaisDocumento("");
		novedad.setNumeroDocumento(reserva.getNumeroDocumento());
		
		DateFormat df1 = new SimpleDateFormat("yyyyMMdd HHmm ZZZ");
		novedad.setFechaHoraReserva(df1.format(dispon.getHoraInicio()));
		
		novedad.setNumeroReserva(reserva.getNumero()==null?"":reserva.getNumero().toString());
		
		novedad.setOidOrganismo(empresa.getOid());
		novedad.setNombreOrganismo(empresa.getNombre());

		novedad.setCodigoAgenda(reserva.getTramiteCodigo());
		novedad.setNombreAgenda(agenda.getNombre());
		
		novedad.setCodigoRecurso(recurso.getOficinaId());
		novedad.setNombreRecurso(recurso.getNombre());
		
		novedad.setAccion(accion);
		
		registrarNovedad(empresa, reserva, novedadToXml(novedad));
	}

	private String novedadToXml(Publicar novedad) {
		if (novedad == null) {
			return null;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Publicar.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(novedad, sw);
			String xmlString = sw.toString();
			return xmlString;
		} catch (Exception ex) {
			logger.warn("No se pudo convertir la novedad a XML: " + ex.getMessage(), ex);
		}
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void registrarNovedad(Empresa empresa, Reserva reserva, String datos) {
		if (datos == null) {
			return;
		}
		Novedad novedad = new Novedad();
		novedad.setDatos(datos);
		novedad.setEnviado(false);
		novedad.setFechaCreacion(new Date());
		novedad.setFechaUltIntento(new Date());
		novedad.setIntentos(0);
		novedad.setEmpresa(empresa);
		novedad.setReservaId(reserva.getId());
		globalEntityManager.persist(novedad);
	}

	private Publicar xmlToNovedad(String xml) {
		if (xml == null) {
			return null;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Publicar.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			Publicar novedad = (Publicar) jaxbUnmarshaller.unmarshal(reader);
			return novedad;
		} catch (Exception ex) {
			logger.warn("No se pudo convertir el XML a la novedad: " + ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Schedule(second = "0", minute = "*/2", hour = "*", persistent = false)
	public void reintentarTrazas() {

		boolean habilitado = false;
		try {
			habilitado = confBean.getBoolean("WS_NOVEDADES_HABILITADO");
		} catch (NumberFormatException nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
			return;
		}
		
		int maxIntentos = 15;
		try {
			maxIntentos = confBean.getLong("WS_NOVEDADES_MAXINTENTOS").intValue();
		} catch (Exception nfEx) {
			maxIntentos = 15;
		}
		
		String eql = "SELECT n FROM Novedad n WHERE n.enviado=FALSE AND n.intentos<:maxIntentos ORDER BY id";
		Query query = globalEntityManager.createQuery(eql);
		query.setParameter("maxIntentos", maxIntentos);
		List<Novedad> novedades = (List<Novedad>) query.getResultList();
		if (!novedades.isEmpty()) {

			NuevaNovedadService_Service novedadService = new NuevaNovedadService_Service(NuevaNovedadService_Service.class.getResource("PublicacionTopico-SAENovedades.wsdl"));
			NuevaNovedadService novedadPort = novedadService.getNuevaNovedadPort();
			String wsaToNovedad = confBean.getString("WS_NOVEDADES_WSATO");
			String wsaActionNovedad = confBean.getString("WS_NOVEDADES_WSAACTION");

			int timeout = 5000;
			try {
				timeout = confBean.getLong("WS_NOVEDADES_TIMEOUT").intValue();
			} catch (Exception nfEx) {
				timeout = 5000;
			}

			try {
				configurarSeguridad(novedadPort, wsaToNovedad, wsaActionNovedad, timeout);
			}catch(Exception ex) {
				throw new RuntimeException("No se pudo configurar la seguridad para invocar los servicios web de Publicación de Novedades: "+ex.getMessage(), ex);
			}
			
			for (Novedad novedad0 : novedades) {
				Publicar novedad = xmlToNovedad(novedad0.getDatos());
				novedad0.setFechaUltIntento(new Date());
				novedad0.setIntentos(novedad0.getIntentos() + 1);
				try {
					novedadPort.nuevaNovedad(novedad);;
					novedad0.setEnviado(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						globalEntityManager.merge(novedad0);
					}catch(Exception ex) {
						//
					}
				}
			}
		}

	}

	/*
	 * Atención que no es exactamente igual al método del mismo nombre de otros servicios (ServiciosTrazabilidadBean) porque
	 * este incluye un handler exclusivo para novedades!!
	 */
	@SuppressWarnings("rawtypes")
	private void configurarSeguridad(Object port, String wsaTo, String wsaAction, int timeout) throws Exception {
		/* */
		String urlSts = confBean.getString("WS_NOVEDADES_URLSTS");
		String rol = confBean.getString("WS_NOVEDADES_ROL");
		String policy = confBean.getString("WS_NOVEDADES_POLICY");

		String orgKsPath = confBean.getString("WS_NOVEDADES_ORG_KS_PATH");
		String orgKsPass = confBean.getString("WS_NOVEDADES_ORG_KS_PASS");
		String orgKsAlias = confBean.getString("WS_NOVEDADES_ORG_KS_ALIAS");

		String sslKsPath = confBean.getString("WS_NOVEDADES_SSL_KS_PATH");
		String sslKsPass = confBean.getString("WS_NOVEDADES_SSL_KS_PASS");
		String sslKsAlias = confBean.getString("WS_NOVEDADES_SSL_KS_ALIAS");

		String sslTsPath = confBean.getString("WS_NOVEDADES_SSL_TS_PATH");
		String sslTsPass = confBean.getString("WS_NOVEDADES_SSL_TS_PASS");

		String productor = confBean.getString("WS_NOVEDADES_PRODUCTOR");
		String topico = confBean.getString("WS_NOVEDADES_TOPICO");
		
		SAMLAssertion tokenSTS = obtenerTokenSTS(urlSts, rol, wsaTo, policy, orgKsPath, orgKsPass, orgKsAlias, sslKsPath, sslKsPass, sslKsAlias,
				sslTsPath, sslTsPass, timeout);

		AddressingBuilder addrBuilder = SOAPAddressingBuilder.getAddressingBuilder();
		SOAPAddressingProperties addrProps = (SOAPAddressingProperties) addrBuilder.newAddressingProperties();

		addrProps.setTo(new AttributedURIImpl(wsaTo));
		addrProps.setAction(new AttributedURIImpl(wsaAction));

		// Build handler chain
		List<Handler> customHandlerChain = new ArrayList<Handler>();
		customHandlerChain.add(new WSAddressingHandler());
		customHandlerChain.add(new SAMLHandler());
		customHandlerChain.add(new PublishSubscribeHeadersHandler(productor, topico)); //Para P&S
		customHandlerChain.add(new SoapHandler());

		BindingProvider bindingProvider = (BindingProvider) port;

		Map<String, Object> reqContext = bindingProvider.getRequestContext();
		reqContext.put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES, addrProps);
		reqContext.put(AgesicConstants.SAML1_PROPERTY, tokenSTS);
		reqContext.put("javax.xml.ws.client.connectionTimeout", timeout);
		reqContext.put("javax.xml.ws.client.receiveTimeout", timeout);

		// Para configurar el keystore y el truststore
		Client client = ClientProxy.getClient(port);
		
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		TLSClientParameters tlsParams = new TLSClientParameters();
		tlsParams.setSSLSocketFactory(createSSLContext(sslKsPath, sslKsPass, sslKsAlias, sslTsPath, sslTsPass).getSocketFactory());
		conduit.setTlsClientParameters(tlsParams);
		bindingProvider.getBinding().setHandlerChain(customHandlerChain);

	}

	private SAMLAssertion obtenerTokenSTS(String urlSts, String rol, String wsaTo, String policy, String orgKsPath, String orgKsPass,
			String orgKsAlias, String sslKsPath, String sslKsPass, String sslKsAlias, String sslTsPath, String sslTsPass, int timeout) throws Exception {
		RSTBean bean = new RSTBean();
		bean.setUserName("SAE");
		bean.setRole(rol);
		bean.setService(wsaTo);
		bean.setPolicyName(policy);
		bean.setIssuer("SAE");

		StoreBean orgKeyStore = new StoreBean();
		orgKeyStore.setStoreFilePath(orgKsPath);
		orgKeyStore.setStorePwd(orgKsPass);
		orgKeyStore.setAlias(orgKsAlias);

		StoreBean sslKeyStore = new StoreBean();
		sslKeyStore.setStoreFilePath(sslKsPath);
		sslKeyStore.setStorePwd(sslKsPass);
		sslKeyStore.setAlias(sslKsAlias);

		StoreBean sslTrustStore = new StoreBean();
		sslTrustStore.setStoreFilePath(sslTsPath);
		sslTrustStore.setStorePwd(sslTsPass);

		PGEClient client = new PGEClient();
		client.setTimeout(timeout);
		SAMLAssertion assertion = null;
		try {
			assertion = client.requestSecurityToken(bean, orgKeyStore, sslKeyStore, sslTrustStore, urlSts);
		} catch (Exception ex) {
			throw new Exception("No se pudo obtener el token STS", ex);
		}

		return assertion;
	}

	private SSLContext createSSLContext(String sslKsPath, String sslKsPass, String sslKsAlias, String sslTsPath, String sslTsPass) throws Exception {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(new FileInputStream(sslTsPath), sslTsPass.toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(trustStore);

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(new FileInputStream(sslKsPath), sslKsPass.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keyStore, sslKsPass.toCharArray());

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		return sslContext;
	}

}
