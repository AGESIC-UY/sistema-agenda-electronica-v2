package uy.gub.imm.sae.business.ejb.servicios;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
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
import javax.persistence.TemporalType;
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
import uy.gub.imm.sae.entity.Agenda;
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
	 * 	Estados: 1=Inicio, 2=En ejecución, 3=Finalizado, 4=Cancelado
	 * */
	public static enum Paso {
		RESERVA(1L, 2, "Reserva"), 
		ASISTENCIA(2L, 2, "Atención"), 
		INASISTENCIA(3L, 2, "No asistencia"), 
		CANCELACION(4L, 4, "Cancelación"),
		FINALIZACION(5L, 3, "Finalización");

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

	public String registrarCabezal(Empresa empresa, Reserva reserva, String transaccionId, String procesoId, boolean inicioAsistido, 
			String transaccionPadreId, Long pasoPadre) {


		boolean habilitado = false;
		//Primero ver si la agenda soporta trazabilidad
		Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
		if(agenda.getConTrazabilidad()!=null) {
			habilitado = agenda.getConTrazabilidad().booleanValue();
		}
		if (!habilitado) {
			return null;
		}
		//Después ver si la instalación soporta trazabilidad 
		try {
			habilitado = confBean.getBoolean("WS_TRAZABILIDAD_HABILITADO");
		} catch (Exception nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
			return null;
		}
		
		int timeout = 5000;
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
			//Hay 3 pasos: 1=Reserva, 2=Atención (asiste o no), 3=Finalización (al final del día)
			traza.setCantidadPasosProceso(3L);
			//Si se hace desde la web pública es 0 en otro caso es 1
			if(inicioAsistido) {
				traza.setInicioAsistidoProceso(1);
			}	else {
				traza.setInicioAsistidoProceso(0);
			}
			//1=Web PC, 2=Web Móvil, 3=Presencial, 4=Redes de Cobranza, 5=PAC, 6=Telefónico, 7=Correo electrónico
			//ToDo: si es una reserva pública, habría que ver si es 1 o 2; si es privada habría que ver si es 3, 5 o 6
			//traza.setCanalDeInicio(1);
			//Ejemplo: 2015-11-16, 17:26:32 +03:00
			traza.setFechaHoraOrganismo(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

			String wsaTo = confBean.getString("WS_TRAZABILIDAD_WSATO_CABEZAL");
			String wsaAction = confBean.getString("WS_TRAZABILIDAD_WSAACTION_CABEZAL");
			configurarSeguridad(cabezalPort, wsaTo, wsaAction, timeout);

			CabezalResponseDTO cabezalResp = cabezalPort.persist(traza);
			if (cabezalResp.getEstado().equals(uy.gub.agesic.itramites.bruto.web.ws.cabezal.EstadoRespuestaEnum.OK)) {
				registrarTraza(empresa, reserva.getId(), transaccionId, cabezalToXml(traza), true, false, true);
				return cabezalResp.getGuid();
			} else {
				// No se pudo registrar la traza pero no se puede cancelar la reserva
				logger.warn("No se pudo registrar el cabezal de la traza para la reserva " + transaccionId + ": " + cabezalResp.getMensaje());
				registrarTraza(empresa, reserva.getId(), transaccionId, cabezalToXml(traza), true, false, false);
			}
		} catch (Exception ex) {
			logger.warn("No se pudo registrar el cabezal de la traza para la reserva " + transaccionId + ": " + ex.getMessage(), ex);
			registrarTraza(empresa, reserva.getId(), transaccionId, cabezalToXml(traza), true, false, false);
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
	public void registrarLinea(Empresa empresa, Reserva reserva, String transaccionId, String oficina, Paso paso) {
		
		boolean habilitado = false;
		
		//Primero ver si la agenda soporta trazabilidad
		Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
		if(agenda.getConTrazabilidad()!=null) {
			habilitado = agenda.getConTrazabilidad().booleanValue();
		}
		if (!habilitado) {
			return;
		}
		//Después ver si la instalación soporta trazabilidad 
		try {
			habilitado = confBean.getBoolean("WS_TRAZABILIDAD_HABILITADO");
		} catch (NumberFormatException nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
			return;
		}

		//Determinar el número de pasoProceso que le corresponde
		String eql = "SELECT count(t.id) FROM Trazabilidad t WHERE t.transaccionId=:transaccionId AND t.esCabezal=FALSE";
		Query query = globalEntityManager.createQuery(eql);
		query.setParameter("transaccionId", transaccionId);
		long pasoProceso = (Long) query.getSingleResult();
		pasoProceso++; //Este pasoProceso es uno más que la cantidad de lineas de la transacción
		
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
		traza.setOficina(oficina);
		try {
			traza.setFechaHoraOrganismo(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		}catch(Exception ex) {
			traza.setFechaHoraOrganismo(null);
		}
		traza.setPaso(paso.getLPaso());
		traza.setDescripcionDelPaso(paso.getAsunto());
		traza.setEstadoProceso(paso.estado);
		traza.setTipoRegistroTrazabilidad(3); //Comun
		traza.setPasoDelProceso(pasoProceso);

		registrarTraza(empresa, reserva.getId(), transaccionId, lineaToXml(traza), false, false, false);
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
	public void registrarTraza(Empresa empresa, Integer reservaId, String transaccionId, String datos, 
			boolean esCabezal, boolean esFinal, boolean enviado) {
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
		traza.setReservaId(reservaId);
		traza.setEsFinal(esFinal);
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

		boolean habilitado = false;
		try {
			habilitado = confBean.getBoolean("WS_TRAZABILIDAD_HABILITADO");
		} catch (NumberFormatException nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
			return;
		}
		
		int maxIntentos = 15;
		try {
			maxIntentos = confBean.getLong("WS_TRAZABILIDAD_MAXINTENTOS").intValue();
		} catch (Exception nfEx) {
			maxIntentos = 15;
		}
		
		String eql = "SELECT t FROM Trazabilidad t WHERE t.enviado=FALSE AND t.intentos<:maxIntentos ORDER BY id";
		Query query = globalEntityManager.createQuery(eql);
		query.setParameter("maxIntentos", maxIntentos);
		List<Trazabilidad> trazas = (List<Trazabilidad>) query.getResultList();
		if (!trazas.isEmpty()) {

			CabezalService cabezalService = new CabezalService(CabezalService.class.getResource("CabezalService.wsdl"));
			CabezalWS cabezalPort = cabezalService.getCabezalWSPort();
			String wsaToCabezal = confBean.getString("WS_TRAZABILIDAD_WSATO_CABEZAL");
			String wsaActionCabezal = confBean.getString("WS_TRAZABILIDAD_WSAACTION_CABEZAL");

			LineaService lineaService = new LineaService(LineaService.class.getResource("LineaService.wsdl"));
			LineaWS lineaPort = lineaService.getLineaWSPort();
			String wsaToLinea = confBean.getString("WS_TRAZABILIDAD_WSATO_LINEA");
			String wsaActionLinea = confBean.getString("WS_TRAZABILIDAD_WSAACTION_LINEA");
			
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
				traza.setFechaUltIntento(new Date());
				traza.setIntentos(traza.getIntentos() + 1);
				
				try {
					if (traza.getEsCabezal()) {
						CabezalDTO cabezal = xmlToCabezal(traza.getDatos());
						configurarSeguridad(cabezalPort, wsaToCabezal, wsaActionCabezal, timeout);
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
									ex.printStackTrace();
								}
							}
							traza.setEnviado(true);
						} else {
							traza.setEnviado(false);
						}
					} else {
						LineaDTO linea = xmlToLinea(traza.getDatos());
						configurarSeguridad(lineaPort, wsaToLinea, wsaActionLinea, timeout);
						ResponseDTO lineaResp = lineaPort.persist(linea);
						if (lineaResp.getEstado().equals(uy.gub.agesic.itramites.bruto.web.ws.linea.EstadoRespuestaEnum.OK)) {
							traza.setEnviado(true);
						} else {
							traza.setEnviado(false);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						globalEntityManager.merge(traza);
					}catch(Exception ex) {
						//
					}
				}
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void configurarSeguridad(Object port, String wsaTo, String wsaAction, int timeout) throws Exception {
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

	
	
	@SuppressWarnings("unchecked")
	@Schedule(second = "0", minute = "0", hour = "1", persistent = false)
	public void finalizarTrazas() {
		//Template para la consulta de reservas vencidas con trazabilidad habilitada
		String sql = "SELECT r.id, t.transaccion_id, s.nombre, count(*) AS lineas "
				+ "FROM {empresa}.ae_disponibilidades d "
				+ "JOIN {empresa}.ae_reservas_disponibilidades rd ON rd.aedi_id=d.id "
				+ "JOIN {empresa}.ae_reservas r ON r.id=rd.aers_id "
				+ "JOIN {empresa}.ae_recursos s ON s.id=d.aere_id "
				+ "JOIN global.ae_trazabilidad t ON t.reserva_id=r.id AND t.empresa_id={empresaId} "
				+ "WHERE d.fecha < :ahora "
				+ "GROUP BY r.id, t.transaccion_id, s.nombre";
		
		//Armar y configurar el invocador del servicio web
		LineaService lineaService = new LineaService(LineaService.class.getResource("LineaService.wsdl"));
		LineaWS lineaPort = lineaService.getLineaWSPort();
		String wsaToLinea = confBean.getString("WS_TRAZABILIDAD_WSATO_LINEA");
		String wsaActionLinea = confBean.getString("WS_TRAZABILIDAD_WSAACTION_LINEA");
		int timeout = 5000;
		try {
			timeout = confBean.getLong("WS_TRAZABILIDAD_TIMEOUT").intValue();
		} catch (Exception nfEx) {
			timeout = 5000;
		}		
		long version = 0;
		try {
			version = confBean.getLong("WS_TRAZABILIDAD_VERSION");
		} catch (NumberFormatException nfEx) {
			version = 100;
		}
		//Cargar todas las empresas
		List<Empresa> empresas = globalEntityManager.createQuery("SELECT e FROM Empresa e WHERE e.fechaBaja IS NULL").getResultList();
		//Para cada empresa determinar sus trazas vencidas y abiertas y tratar de cerrarlas
		Integer reservaId=null;
		String transaccionId=null;
		String oficina=null;
		BigInteger lineas=null;
		for(Empresa empresa : empresas) {
			try {
				String sql1 = sql.replace("{empresa}", empresa.getDatasource()).replace("{empresaId}", empresa.getId().toString());
				Query query = globalEntityManager.createNativeQuery(sql1);
				query.setParameter("ahora", new Date(), TemporalType.DATE);
				List<Object[]> aTrazas = (List<Object[]>) query.getResultList();
				
				for(Object[] aTraza : aTrazas) {
					
					try {
						reservaId = (Integer) aTraza[0];
						transaccionId = (String)aTraza[1];
						oficina = (String)aTraza[2];
						lineas = (BigInteger)aTraza[3];
						
						LineaDTO linea = null;
						linea = new LineaDTO();
						linea.setIdTransaccion(transaccionId);
						linea.setEdicionModelo(version);
						linea.setOficina(oficina);
						try {
							linea.setFechaHoraOrganismo(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
						}catch(Exception ex) {
							linea.setFechaHoraOrganismo(null);
						}
						linea.setPaso(Paso.FINALIZACION.getLPaso());
						linea.setDescripcionDelPaso(Paso.FINALIZACION.getAsunto());
						linea.setEstadoProceso(Paso.FINALIZACION.getEstado());
						linea.setTipoRegistroTrazabilidad(3); //Comun
						linea.setPasoDelProceso(lineas.longValue()+1);
			
						configurarSeguridad(lineaPort, wsaToLinea, wsaActionLinea, timeout);
						ResponseDTO lineaResp = lineaPort.persist(linea);
						
						//Solo se registra la traza final si se pudo enviar correctamente, ya que en otro caso se requiere
						//volvera generar una traza con la fecha del momento
						if (lineaResp.getEstado().equals(uy.gub.agesic.itramites.bruto.web.ws.linea.EstadoRespuestaEnum.OK)) {
							registrarTraza(empresa, reservaId, transaccionId, lineaToXml(linea), false, true, true);
							logger.debug("La traza "+transaccionId+" de la empresa "+empresa.getId()+" ("+empresa.getNombre()+") fue cerrada.");
						}else {
							logger.debug("La traza "+transaccionId+" de la empresa "+empresa.getId()+" ("+empresa.getNombre()+") no fue cerrada: "+lineaResp.getMensaje());
						}
					} catch (Exception ex) {
						logger.error("No se pudo cerrar la traza "+transaccionId+" de la empresa "+empresa.getId()+" ("+empresa.getNombre()+")", ex);
						ex.printStackTrace();
					}
				}
			}catch(Exception ex) {
				logger.error("No se pudo cerrar las trazas de la empresa "+empresa.getId()+" ("+empresa.getNombre()+")", ex);
			}
		}
	}
	
	
}
