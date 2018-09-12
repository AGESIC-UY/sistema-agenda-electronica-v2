package uy.gub.imm.sae.business.ejb.servicios;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.ws.extensions.addressing.AttributedURIImpl;
import org.joda.time.DateTime;

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
import uy.gub.imm.sae.business.ejb.facade.Configuracion;
import uy.gub.imm.sae.business.ws.SoapHandler;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.entity.global.Novedad;

/**
 * Clase encargada del envío de notificaciones al Sistema de Novedades de AGESIC.
 * 
 * El envío se realiza en dos pasos:
 * 1 - Cuando otra clase invoca el método publicarNovedad(...) la novedad se registra en la base
 *    de datos pero no se envía en el momento (para liberar rápidamente la thread y no bloquear al
 *    usuario).
 * 2 - Cada x minutos (2 actualmente) el método enviarNovedadesPendientes(...) se encarga de enviar
 *    las novedades pendientes.
 * 
 * @author spio
 *
 */
@Stateless
public class ServiciosNovedadesBean {
 
	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;

  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionLocal")
	private Configuracion confBean;
	
	private static Logger logger = Logger.getLogger(ServiciosNovedadesBean.class);

  private static final DateFormat FULL_TIME_DF = new SimpleDateFormat("yyyyMMdd HHmm ZZZ");
	
	/**
	 * @param empresa
	 * @param reserva
	 * @param transaccionId
	 * @param idOficina
	 * @param paso
	 */
	public void publicarNovedad(Empresa empresa, Reserva reserva, Acciones accion) {
		
    logger.debug("Novedad recibida para publicar: reserva " + reserva.getId());
	  
		Disponibilidad dispon = reserva.getDisponibilidades().get(0);
		Recurso recurso = dispon.getRecurso();
		Agenda agenda = recurso.getAgenda();
		
		boolean habilitado = false;
		if(agenda.getPublicarNovedades()!=null) {
			habilitado = agenda.getPublicarNovedades().booleanValue();
		}
		if(!habilitado) {
	    logger.debug("La publicación de novedades no está habilitada en la agenda indicada, se ignora la solicitud.");
			return;
		}
		try {
			habilitado = confBean.getBoolean("WS_NOVEDADES_HABILITADO");
		} catch (NumberFormatException nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
      logger.debug("La publicación de novedades no está habilitada en la instalación, se ignora la solicitud.");
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
		novedad.setFechaHoraReserva(FULL_TIME_DF.format(dispon.getHoraInicio()));
		novedad.setNumeroReserva(reserva.getNumero()==null?"":reserva.getNumero().toString());
		novedad.setOidOrganismo(empresa.getOid());
		novedad.setNombreOrganismo(empresa.getNombre());
		novedad.setCodigoAgenda(reserva.getTramiteCodigo());
		novedad.setNombreAgenda(agenda.getNombre());
		novedad.setCodigoRecurso(recurso.getOficinaId());
		novedad.setNombreRecurso(recurso.getNombre());
		novedad.setAccion(accion);
		
    logger.debug("Registrando la novedad en la base de datos...");
		
		Integer novedadId = registrarNovedad(empresa, reserva, novedadToXml(novedad));
		
		if(novedadId != null) {
	    logger.debug("La novedad fue registrada en la base de datos (id="+novedadId+", no enviada aún).");
		}else {
      logger.debug("La novedad no fue registrada en la base de datos.");
		}
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
	private Integer registrarNovedad(Empresa empresa, Reserva reserva, String datos) {
		if (datos == null) {
			return null;
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
		return novedad.getId();
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

  //El id puede ser cualquier número que compartan todos los módulos y que no entre en conflicto con
  //otro lock
  private static final long LOCK_ID = 1515151515;
	@SuppressWarnings("unchecked")
  @TransactionTimeout(value=30, unit=TimeUnit.MINUTES)
	@Schedule(second = "30", minute = "*/5", hour = "*", persistent = false)
  //@Schedule(second = "30", minute = "*/1", hour = "*", persistent = false)
	public void enviarNovedadesPendientes() {
	  
    logger.info("Enviando novedades pendientes...");
    
    //Intentar liberar el lock por si lo tiene esta instancia
    boolean lockOk = (boolean)globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID+")").getSingleResult();
    //Intentar obtener el lock
    lockOk = (boolean)globalEntityManager.createNativeQuery("SELECT pg_try_advisory_lock("+LOCK_ID+")").getSingleResult();
    if(!lockOk) {
      //Otra instancia tiene el lock
      logger.info("No se ejecuta el envío de novedades porque hay otra instancia haciéndolo.");
      return;
    }
    //No hay otra instancia con el lock, se continúa
    
    try {
  		boolean habilitado = false;
  		try {
  			habilitado = confBean.getBoolean("WS_NOVEDADES_HABILITADO");
  		} catch (NumberFormatException nfEx) {
  			habilitado = false;
  		}
  		if (!habilitado) {
        logger.debug("La publicación de novedades no está habilitada en la instalación.");
  			return;
  		}
  		
  		int maxIntentos = 15;
  		try {
  			maxIntentos = confBean.getLong("WS_NOVEDADES_MAXINTENTOS").intValue();
  		} catch (Exception nfEx) {
  			maxIntentos = 15;
  		}
  
      logger.debug("Determinando novedades pendientes de envío...");
  		
  		String eql = "SELECT n FROM Novedad n WHERE n.enviado=FALSE AND n.intentos<:maxIntentos ORDER BY id";
  		
      logger.debug("Consulta para determinar las novedades pendientes: "+eql);
  		
  		Query query = globalEntityManager.createQuery(eql);
  		query.setParameter("maxIntentos", maxIntentos);
  		List<Novedad> novedades = (List<Novedad>) query.getResultList();
  		
      logger.debug("Se encontraron "+novedades.size()+" novedades pendientes de envío.");
  		
  		if (!novedades.isEmpty()) {
  		  URL urlWsdl = NuevaNovedadService_Service.class.getResource("PublicacionTopico-SAENovedades.wsdl");
  			NuevaNovedadService_Service novedadService = new NuevaNovedadService_Service(urlWsdl);
  			NuevaNovedadService novedadPort = novedadService.getNuevaNovedadPort();
  			try {
  			  DateTime tokenVence = null;
  	      for (Novedad novedad0 : novedades) {
            if(tokenVence==null || !tokenVence.isAfterNow()) {
              logger.debug("No hay token STS o está por vencerse, se pide uno... ");
              tokenVence = configurarWSPort(novedadPort);
            }
  	        Publicar novedad = xmlToNovedad(novedad0.getDatos());
  	        novedad0.setFechaUltIntento(new Date());
  	        novedad0.setIntentos(novedad0.getIntentos() + 1);
  	        try {
  	          novedadPort.nuevaNovedad(novedad);;
  	          novedad0.setEnviado(true);
  	        } catch (Exception ex) {
              logger.warn("No se pudo enviar una novedad!", ex);
  	        } finally {
  	          try {
  	            globalEntityManager.merge(novedad0);
  	          }catch(Exception ex) {
  	            //
  	          }
  	        }
  	      }
  			}catch(Exception ex) {
  			  ex.printStackTrace();
  			}
  			
  	    logger.debug(""+novedades.size()+" novedades procesadas.");
  		}
    }finally {
      //Intentar liberar el lock (si lo tiene esta instancia)
      globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID+")").getSingleResult();
      logger.info("Procesamiento de novedades pendientes finalizado.");
    }
	}

	/*
	 * Atención que no es exactamente igual al método del mismo nombre de otros servicios (ServiciosTrazabilidadBean) porque
	 * este incluye un handler exclusivo para novedades!!
	 */
	@SuppressWarnings("rawtypes")
	private DateTime configurarWSPort(Object port) throws Exception {
		
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
		
    String wsaTo = confBean.getString("WS_NOVEDADES_WSATO");
    String wsaAction = confBean.getString("WS_NOVEDADES_WSAACTION");
    String wsAddressLocation = confBean.getString("WS_NOVEDADES_LOCATION");
    int timeout = 5000;
    try {
      timeout = confBean.getLong("WS_NOVEDADES_TIMEOUT").intValue();
    } catch (Exception nfEx) {
      timeout = 5000;
    }
		
    logger.debug("Propiedades del servicio web: ");
    logger.debug("--- urlSts: "+urlSts);
    logger.debug("--- rol: "+rol);
    logger.debug("--- policy: "+policy);
    logger.debug("--- orgKsPath: "+orgKsPath);
    logger.debug("--- orgKsPass: ***");
    logger.debug("--- orgKsAlias: "+orgKsAlias);
    logger.debug("--- sslKsPath: "+sslKsPath);
    logger.debug("--- sslKsPass: ***");
    logger.debug("--- sslKsAlias: "+sslKsAlias);
    logger.debug("--- sslTsPath: "+sslTsPath);
    logger.debug("--- sslTsPass: ***");
    logger.debug("--- wsAddressLocation: "+wsAddressLocation);
    logger.debug("--- wsaTo: "+wsaTo);
    logger.debug("--- wsaAction: "+wsaAction);
    logger.debug("--- timeout: "+timeout);
    
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
    if(wsAddressLocation != null) {
      //Si no se define se deja lo que tenga el WSDL
      reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsAddressLocation);
    }

		// Para configurar el keystore y el truststore
		Client client = ClientProxy.getClient(port);
		
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		TLSClientParameters tlsParams = new TLSClientParameters();
		tlsParams.setSSLSocketFactory(createSSLContext(sslKsPath, sslKsPass, sslKsAlias, sslTsPath, sslTsPass).getSocketFactory());
		conduit.setTlsClientParameters(tlsParams);
		bindingProvider.getBinding().setHandlerChain(customHandlerChain);

    //Se le quita algunos segundos para prevenir que venza mientras se ejecuta alguna acción
    DateTime tokenVence = tokenSTS.getAssertion().getConditions().getNotOnOrAfter().minusSeconds(3);
    
    return tokenVence;
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
