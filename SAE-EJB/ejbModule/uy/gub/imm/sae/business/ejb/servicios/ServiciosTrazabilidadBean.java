package uy.gub.imm.sae.business.ejb.servicios;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
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
import javax.persistence.Table;
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
import uy.gub.agesic.itramites.bruto.web.ws.cabezal.CabezalDTO;
import uy.gub.agesic.itramites.bruto.web.ws.cabezal.CabezalResponseDTO;
import uy.gub.agesic.itramites.bruto.web.ws.cabezal.CabezalService;
import uy.gub.agesic.itramites.bruto.web.ws.cabezal.CabezalWS;
import uy.gub.agesic.itramites.bruto.web.ws.linea.LineaDTO;
import uy.gub.agesic.itramites.bruto.web.ws.linea.LineaService;
import uy.gub.agesic.itramites.bruto.web.ws.linea.LineaWS;
import uy.gub.agesic.itramites.bruto.web.ws.linea.ResponseDTO;
import uy.gub.agesic.jbossws.SAMLHandler;
import uy.gub.agesic.jbossws.WSAddressingHandler;
import uy.gub.agesic.sts.client.PGEClient;
import uy.gub.imm.sae.business.ejb.facade.ConfiguracionBean;
import uy.gub.imm.sae.business.ws.SoapHandler;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
/* */
import uy.gub.imm.sae.entity.global.Trazabilidad;

@Stateless
public class ServiciosTrazabilidadBean {

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;

	@EJB
	private ConfiguracionBean confBean;
	
	private static Logger logger = Logger.getLogger(ServiciosTrazabilidadBean.class);

	/*
	 * Por documentación del campo estado ver el documento Metadato_Trazas_Edicion_01_SinIntro.pdf
	 * 	1=Inicio, 2=Ejecución, 3=Finalizado, 4=Cancelado
	 * */
	public static enum Paso {
		RESERVA(1L, 1, "Reserva"), 
		ASISTENCIA(2L, 3, "Atención"), 
		INASISTENCIA(3L, 3, "No asistencia"), 
		CANCELACION(4L, 4, "Cancelación");

		long lPaso;
		int estado;
		String asunto;

		Paso(long lPaso, int estado, String asunto) {
			this.lPaso = lPaso;
			this.estado = estado;
			this.asunto = asunto;
		}

		public long getLPaso() {
			return lPaso;
		}

		public int getEstado() {
			return estado;
		}

		public String getAsunto() {
			return asunto;
		}
	}

	public static enum Tipo {
		CABEZAL, LINEA
	}

	public String registrarCabezal(Empresa empresa, Reserva reserva, String transaccionId, String procesoId, 
			String transaccionPadreId, Long pasoPadre) {

		boolean habilitado = false;
		int timeout = 5000;
		try {
			habilitado = confBean.getBoolean("WS_TRAZABILIDAD_HABILITADO");
		} catch (Exception nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
			return null;
		}
		try {
			timeout = confBean.getLong("WS_TRAZABILIDAD_TIMEOUT").intValue();
		} catch (Exception nfEx) {
			timeout = 5000;
		}

		CabezalDTO traza = null;
		try {
			URL urlWsdl = CabezalService.class.getResource("CabezalService.wsdl");

			// Consultar el servicio web
			CabezalService cabezalService = new CabezalService(urlWsdl);
			
			CabezalWS cabezalPort = cabezalService.getCabezalWSPort();
			
			traza = new CabezalDTO();
			
			//I.Friedmann: El modelo de trazabilidad está pensado para que aplique a cualquier tipo de proceso: 
			//trámites, expedientes, compras, reclamos, etc... en el caso de Agenda se trata de Trámites, y el valor 
			//que corresponde a ese tipo de proceso es el 1
			traza.setTipoProceso(1);
			//En caso de que Tipo de proceso sea trámite utilizar la codiguera de la guía de trámites
			try {
				String[] partes = procesoId.split("-");
				if(partes.length>1) {
					procesoId = partes[1];
				}
				traza.setIdProceso(Integer.valueOf(procesoId));
			}catch(Exception ex) {
				traza.setIdProceso(0);
			}
      //OID organismo + ":" + Identificador del Proceso + ":" + Código interno del trámite
			traza.setIdTransaccion(transaccionId);
			//Versión (01.00) del Modelo de Metadatos de Referencia de Trazabilidad
			long version = 0;
			try {
				version = confBean.getLong("WS_TRAZABILIDAD_VERSION");
			} catch (NumberFormatException nfEx) {
				version = 100;
			}
			traza.setEdicionModelo(version);
			//Es obligatorio, si no hay, es el mismo que transaccionId
			if(transaccionPadreId!=null && !transaccionPadreId.trim().isEmpty()) {
				traza.setIdTransaccionPadre(transaccionPadreId);
			}
			//
			if(pasoPadre!=null) {
				traza.setPasoPadre(pasoPadre);
			}
			//I. Friedmann:  El número corresponde a la cantidad de pasos que el proceso lleva en un flujo normal. 
			//Entiendo que en un flujo normal en la agenda los pasos son 2, cuando la persona agenda y luego asiste.
			traza.setCantidadPasosProceso(2L);
			//I. Friedmann: En esta oportunidad debería mandar 0
			traza.setInicioAsistidoProceso(0);
			//1=Web PC, 2=Web Móvil, 3=Presencial, 4=Redes de Cobranza, 5=PAC, 6=Telefónico, 7=Correo electrónico
			//ToDo: si es una reserva pública, habría que ver si es 1 o 2; si es privada habría que ver si es 3, 5 o 6
			//traza.setCanalDeInicio(1);
			//Ejemplo: 2015-11-16, 17:26:32 +03:00
			traza.setFechaHoraOrganismo(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

			String service = confBean.getString("WS_TRAZABILIDAD_SERVICE_CABEZAL");
			configurarSeguridad(cabezalPort, service, timeout);

			CabezalResponseDTO cabezalResp = cabezalPort.persist(traza);
			if (cabezalResp.getEstado().equals(uy.gub.agesic.itramites.bruto.web.ws.cabezal.EstadoRespuestaEnum.OK)) {
				registrarTraza(empresa, reserva, transaccionId, cabezalToXml(traza), true, true);
				return cabezalResp.getGuid();
			} else {
				// No se pudo registrar la traza pero no se puede cancelar la reserva
				logger.warn("No se pudo registrar el cabezal de la traza para la reserva " + transaccionId + ": " + cabezalResp.getMensaje());
				registrarTraza(empresa, reserva, transaccionId, cabezalToXml(traza), true, false);
			}
		} catch (Exception ex) {
			logger.warn("No se pudo registrar el cabezal de la traza para la reserva " + transaccionId + ": " + ex.getMessage(), ex);
			registrarTraza(empresa, reserva, transaccionId, cabezalToXml(traza), true, false);
		}
		return null;
	}

	/**
	 * El registro de las líneas siempre se realiza posteriormente, para no trancar al usuario (solo es necesario hacerlo
	 * sincrónico en el cabezal para obtener el código de trazabilidad)	 * 
	 * @param empresa
	 * @param reserva
	 * @param transaccionId
	 * @param idOficina
	 * @param paso
	 */
	public void registrarLinea(Empresa empresa, Reserva reserva, String transaccionId, String idOficina, Paso paso) {
		boolean habilitado = false;
		try {
			habilitado = confBean.getBoolean("WS_TRAZABILIDAD_HABILITADO");
		} catch (NumberFormatException nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
			return;
		}

		LineaDTO traza = null;
		traza = new LineaDTO();
		traza.setIdTransaccion(transaccionId);
		long version = 0;
		try {
			version = confBean.getLong("WS_TRAZABILIDAD_VERSION");
		} catch (NumberFormatException nfEx) {
			version = 100;
		}
		traza.setEdicionModelo(version);
		traza.setIdOficina(idOficina);
		try {
			traza.setFechaHoraOrganismo(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		}catch(Exception ex) {
			traza.setFechaHoraOrganismo(null);
		}
		traza.setPaso(paso.getLPaso());
		traza.setDescripcionDelPaso(paso.getAsunto());
		traza.setEstadoProceso(paso.estado);

		registrarTraza(empresa, reserva, transaccionId, lineaToXml(traza), false, false);
	}

	private String cabezalToXml(CabezalDTO cabezal) {
		if (cabezal == null) {
			return null;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CabezalDTO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(cabezal, sw);
			String xmlString = sw.toString();
			return xmlString;
		} catch (Exception ex) {
			logger.warn("No se pudo convertir el cabezal de la traza para la reserva " + cabezal.getIdTransaccion() + " a XML: " + ex.getMessage(), ex);
		}
		return null;
	}

	private String lineaToXml(LineaDTO linea) {
		if (linea == null) {
			return null;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LineaDTO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(linea, sw);
			String xmlString = sw.toString();
			return xmlString;
		} catch (Exception ex) {
			logger.warn("No se pudo convertir la linea de la traza para la reserva " + linea.getIdTransaccion() + " a XML: " + ex.getMessage(), ex);
		}
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void registrarTraza(Empresa empresa, Reserva reserva, String transaccionId, String datos, boolean esCabezal, boolean enviado) {
		if (datos == null) {
			return;
		}
		Trazabilidad traza = new Trazabilidad();
		traza.setDatos(datos);
		traza.setEnviado(enviado);
		traza.setFechaCreacion(new Date());
		traza.setFechaUltIntento(new Date());
		traza.setIntentos(0);
		traza.setTransaccionId(transaccionId);
		traza.setEsCabezal(esCabezal);
		traza.setEmpresa(empresa);
		traza.setReservaId(reserva.getId());
		globalEntityManager.persist(traza);
	}

	private CabezalDTO xmlToCabezal(String xml) {
		if (xml == null) {
			return null;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CabezalDTO.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			CabezalDTO cabezal = (CabezalDTO) jaxbUnmarshaller.unmarshal(reader);
			return cabezal;
		} catch (Exception ex) {
			logger.warn("No se pudo convertir el XML al cabezal de la traza: " + ex.getMessage(), ex);
		}
		return null;
	}

	private LineaDTO xmlToLinea(String xml) {
		if (xml == null) {
			return null;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LineaDTO.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			LineaDTO linea = (LineaDTO) jaxbUnmarshaller.unmarshal(reader);
			return linea;
		} catch (Exception ex) {
			logger.warn("No se pudo convertir el XML a la linea de la traza: " + ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Schedule(second = "0", minute = "*/3", hour = "*", persistent = false)
	public void reintentarTrazas() {
		Query query = globalEntityManager.createQuery("SELECT t FROM Trazabilidad t WHERE t.enviado=FALSE ORDER BY id");
		List<Trazabilidad> trazas = (List<Trazabilidad>) query.getResultList();
		if (!trazas.isEmpty()) {

			CabezalService cabezalService = new CabezalService(CabezalService.class.getResource("CabezalService.wsdl"));
			CabezalWS cabezalPort = cabezalService.getCabezalWSPort();
			String serviceCabezal = confBean.getString("WS_TRAZABILIDAD_SERVICE_CABEZAL");

			LineaService lineaService = new LineaService(LineaService.class.getResource("LineaService.wsdl"));
			LineaWS lineaPort = lineaService.getLineaWSPort();
			String serviceLinea = confBean.getString("WS_TRAZABILIDAD_SERVICE_LINEA");
			
			int timeout = 5000;
			try {
				timeout = confBean.getLong("WS_TRAZABILIDAD_TIMEOUT").intValue();
			} catch (Exception nfEx) {
				timeout = 5000;
			}
			
			//Determinar el nombre de la tabla de reservas
			Table tabla = Reserva.class.getAnnotation(Table.class);
			String nombreTablaReservas = "reservas";
			if(tabla != null) {
				nombreTablaReservas = tabla.name();
			}
			
			for (Trazabilidad traza : trazas) {
				try {
					if (traza.getEsCabezal()) {
						CabezalDTO cabezal = xmlToCabezal(traza.getDatos());
						configurarSeguridad(cabezalPort, serviceCabezal, timeout);
						CabezalResponseDTO cabezalResp = cabezalPort.persist(cabezal);
						if (cabezalResp.getEstado().equals(uy.gub.agesic.itramites.bruto.web.ws.cabezal.EstadoRespuestaEnum.OK)) {
							//Actualizar el codigo de trazabilidad en la tabla de reservas
							//Y enviar el mail al usuario
							if(traza.getEmpresa()!=null && traza.getReservaId()!=null) {
								try {
									//
									String esquema = traza.getEmpresa().getDatasource();

									//Actualizar el codigo de trazabilidad en la tabla de reservas
									String sql = "UPDATE "+esquema+"."+nombreTablaReservas+
											" SET trazabilidad_guid=:trazabilidadGuid WHERE id=:reservaId";
									Query update = globalEntityManager.createNativeQuery(sql);
									update.setParameter("trazabilidadGuid", cabezalResp.getGuid());
									update.setParameter("reservaId", traza.getReservaId());
									update.executeUpdate();
									
									//Obtener el texto para el mail de notificación de código de trazabilidad
									
									//glabandera 04/02/2016 hasta no tener un texto configurable no se enviará mail
									//Enviar el código por mail al usuario
//									sql = "SELECT valor FROM "+esquema+".ae_datos_reserva dr JOIN "+esquema+".ae_datos_a_solicitar ds ON "
//											+ " ds.id=dr.aeds_id JOIN "+esquema+".ae_agrupaciones_datos ad ON ad.id=ds.aead_id WHERE "
//											+ "dr.aers_id=:reservaId AND UPPER(ds.nombre)='MAIL' AND UPPER(ad.nombre)='DATOS PERSONALES'";
//									update = globalEntityManager.createNativeQuery(sql);
//									update.setParameter("reservaId", traza.getReservaId());
//									List<String> emails = update.getResultList();
//									for(String email : emails) {
//										if(email != null && !email.trim().isEmpty()) {
//											//ToDo: crear un mensaje apropiado
//											MailUtiles.enviarMail(email, "Su código de trazabilidad", "El código de trazabilidad de su reserva es '"+cabezalResp.getGuid()+"'", MailUtiles.CONTENT_TYPE_PLAIN);
//										}
//									}
									
								}catch(Exception ex) {
									//
									ex.printStackTrace();
								}
								
							}

							
							
							traza.setEnviado(true);
							
							
							
						} else {
							traza.setEnviado(false);
						}
					} else {
						LineaDTO linea = xmlToLinea(traza.getDatos());
						configurarSeguridad(lineaPort, serviceLinea, timeout);
						ResponseDTO lineaResp = lineaPort.persist(linea);
						if (lineaResp.getEstado().equals(uy.gub.agesic.itramites.bruto.web.ws.linea.EstadoRespuestaEnum.OK)) {
							traza.setEnviado(true);
						} else {
							traza.setEnviado(false);
						}
					}
					traza.setFechaUltIntento(new Date());
					traza.setIntentos(traza.getIntentos() + 1);
					globalEntityManager.merge(traza);
				} catch (Exception ex) {
					ex.printStackTrace();
					//
				}
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void configurarSeguridad(Object port, String service, int timeout) throws Exception {

		/* */
		String urlSts = confBean.getString("WS_TRAZABILIDAD_URLSTS");
		String rol = confBean.getString("WS_TRAZABILIDAD_ROL");
		String policy = confBean.getString("WS_TRAZABILIDAD_POLICY");

		String orgKsPath = confBean.getString("WS_TRAZABILIDAD_ORG_KS_PATH");
		String orgKsPass = confBean.getString("WS_TRAZABILIDAD_ORG_KS_PASS");
		String orgKsAlias = confBean.getString("WS_TRAZABILIDAD_ORG_KS_ALIAS");

		String sslKsPath = confBean.getString("WS_TRAZABILIDAD_SSL_KS_PATH");
		String sslKsPass = confBean.getString("WS_TRAZABILIDAD_SSL_KS_PASS");
		String sslKsAlias = confBean.getString("WS_TRAZABILIDAD_SSL_KS_ALIAS");

		String sslTsPath = confBean.getString("WS_TRAZABILIDAD_SSL_TS_PATH");
		String sslTsPass = confBean.getString("WS_TRAZABILIDAD_SSL_TS_PASS");

		SAMLAssertion tokenSTS = obtenerTokenSTS(urlSts, rol, service, policy, orgKsPath, orgKsPass, orgKsAlias, sslKsPath, sslKsPass, sslKsAlias,
				sslTsPath, sslTsPass, timeout);

		AddressingBuilder addrBuilder = SOAPAddressingBuilder.getAddressingBuilder();
		SOAPAddressingProperties addrProps = (SOAPAddressingProperties) addrBuilder.newAddressingProperties();

		addrProps.setTo(new AttributedURIImpl(service));
		addrProps.setAction(new AttributedURIImpl(service + "/persist"));

		// Build handler chain
		List<Handler> customHandlerChain = new ArrayList<Handler>();
		customHandlerChain.add(new WSAddressingHandler());
		customHandlerChain.add(new SAMLHandler());
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

	private SAMLAssertion obtenerTokenSTS(String urlSts, String rol, String service, String policy, String orgKsPath, String orgKsPass,
			String orgKsAlias, String sslKsPath, String sslKsPass, String sslKsAlias, String sslTsPath, String sslTsPass, int timeout) throws Exception {
		RSTBean bean = new RSTBean();
		bean.setUserName("SAE");
		bean.setRole(rol);
		bean.setService(service);
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

	/**
	 * 
	 * @param organismoOid: OID del organismo
	 * @param tramiteId: id del trámite asociado a la agenda. El formato debe ser <empresa>-<idtramite>
	 * @param idInterno: id de la reserva
	 * @return
	 */
	public String armarTransaccionId(String organismoOid, String tramiteId, Integer idInterno) {
		if(organismoOid==null || tramiteId==null || idInterno==null) {
			return null;
		}
		//El id del trámite incluye al id de la empresa (ej: 13-1855 -> tramite = 1855)
		String[] partes = tramiteId.split("-");
		if(partes.length>1) {
			tramiteId = partes[1];
		}
		return organismoOid+":"+tramiteId+":"+idInterno;
	}
	
}
