/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sofis.agesic.sae.cda;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.valves.ValveBase;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.common.impl.SecureRandomIdentifierGenerator;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml1.core.NameIdentifier;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.SessionIndex;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml2.core.impl.SessionIndexBuilder;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.security.x509.KeyStoreX509CredentialAdapter;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author spio
 */
@SuppressWarnings({"rawtypes","unused", "unchecked"})
public class CDAServiceProviderValve extends ValveBase {

	static final Logger logger = Logger.getLogger("com.sofis.agesic.sae.cda");

	private static final String SAML_REQUEST = "SAMLRequest";
	private static final String SAML_RESPONSE = "SAMLResponse";
	

	private static final String REQUEST_DATA_CACHE = "RequestDataCache";
	private static final String USER_DATA_CACHE = "UserDataCache";

	private SecureRandomIdentifierGenerator idGenerator;

	// Estos son atributos de configuracion por lo que no hay problema de
	// sincronizacion (solo se modifican al levantar el Tomcat)
	private String idpUrlLogin = "";
	private String idpUrlLogout = "";
	private String providerId = "";
	private String spReturnUrl = "";
	private String returnPath = "";
	private String logoutPath = "";
	private String keystorePath = "";
	private String keystorePass = "";
	private String certAlias = "";
	private String truststorePath = "";
	private String truststorePass = "";
	private boolean validarFirma = true;
	private String restrictedUrls = "";
	private String excludedUrls = "";
	private String attributeMappings = "";
	private String defaultRoles = "";
	private String privisioningConfigs = "";
	private String usernameTransformations = "";
	private String getParameterMethod = ""; // GETPARAMETER o GETINPUTSTREAM
	private boolean debug = false;
	private boolean relaxValidityPeriod = false;
	private boolean setPrincipalEnable = true;
	private boolean setContextEnable = true;
	private String contextAttribute = "";

	private String sqlDatasource = "";
	private String sqlSchemaResolverQuery = "";
	private String sqlCDARequiredQuery = "";

	CDAServiceProviderData programData = null;

	// Esta variable se modifica solo en el constructor por lo que tampoco hay
	// problema de sinc.
	private XMLObjectBuilderFactory builderFactory;

	public CDAServiceProviderValve() {

		try {
			logger.info("==============================================");
			logger.info("=====  SOFIS-SERVICE-PROVIDER-VALVE v.2.1  ===");
			logger.info("==============================================");

			DefaultBootstrap.bootstrap();
			builderFactory = Configuration.getBuilderFactory();
			idGenerator = new SecureRandomIdentifierGenerator();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void invoke(Request httpRequest, Response httpResponse) throws IOException, ServletException {

		logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Comenzando a procesar request...");

		if (programData == null) {
			synchronized (this) {
				programData = new CDAServiceProviderData(keystorePath, keystorePass, truststorePath, truststorePass, restrictedUrls,
				    excludedUrls, attributeMappings, privisioningConfigs, defaultRoles, usernameTransformations);
			}
		}
		if (!programData.isConfigured()) {
			try {
				httpResponse.getWriter().write(
				    "No se pudo inicializar la configuracion de la valvula. " + "Puede obtener mas informacion en el log del servidor");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return;
		}

		HttpSession httpSession = httpRequest.getSession(true);

		logger.log(Level.FINE, "[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Procesando acceso a [{0}]", httpRequest.getServletPath());

		// Si el servletPath es la URL de retorno registrada en Agesic (en desarrollo, se registro
		// como URL de retorno "http://sofis.com.uy:8504/sso", por lo que el servletPath debe ser "/sso")
		// hay que ver si viene un SAMLResponse y ademas es valido y esta firmado.
		// Incluso si la firma es válida hay que ver si el registro del usuario en CDA es "Certificado" (usó la
		//cédula electrónica) o "Presencial" (demostró ser el dueño del número de cédula)
		String servletPath = httpRequest.getServletPath();
		if (returnPath.equals(servletPath)) {

			// =================================================================
			// Es un acceso a la URL de retorno luego de un login exitoso
			// =================================================================

			logger.log(Level.FINE, "[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Se detecta un acceso a la URL de retorno ({0})", returnPath);

			// No permitir acceder a /sso si no es una invocacion del IdP (debe contener un mensaje SAMLResponse del IdP)
			String samlResponse = getParameter(httpRequest, SAML_RESPONSE);
			if (samlResponse == null) {
				logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] El acceso a la URL de retorno no contiene una respuesta SAML. Se muestra error y termina la ejecucion.");
				
				//Es posible que contenga un LogoutRequest del CDA
				String samlRequest = getParameter(httpRequest, SAML_REQUEST);
				if(samlRequest != null) {
					try {
						processSAMLRequest(samlRequest, httpRequest, httpResponse);
					}catch(CDAServiceProviderException ex) {
						ex.printStackTrace();
						httpResponse.getWriter().write(ex.getMessage());
					}
					return;
				}
				
				try {
					httpResponse.getWriter().write("Esta URL solo debe ser invocada por el Proveedor de Identidades de su Organización");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return;
			}
			logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] El acceso a la URL de retorno contiene una respuesta SAML. Se continúa");

			// Tomar la respuesta y validarla
			String redirectUrl;
			try {
				logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Procesando la respuesta...");
				redirectUrl = processSAMLResponse(samlResponse, programData, httpRequest, httpResponse);
				if (redirectUrl == null) {
					logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] No se encontró la URL solicitada originalmente por el usuario");
					throw new CDAServiceProviderException("No se encontró la URL solicitada originalmente por el usuario");
				}
			} catch (CDAServiceProviderException ex) {
				logger.log(Level.FINE, "[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Error procesando la respuesta: {0}", ex.getMessage());
				sendErrorMessage(httpResponse, ex);
				return;
			}

			if("ok".equalsIgnoreCase(redirectUrl)) {
				//El método que se encarga de procesar el mensaje SAML ya se encargó de enviar la respuesta, 
				//no hay nada más que hacer
				return;
			}
			
			logger.log(Level.FINE, "[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Redirigiendo al usuario al recurso original: {0}", redirectUrl);

			// Reenviar al usuario a la pagina que solicito originalmente
			httpResponse.sendRedirect(redirectUrl);

		} else if (logoutPath.equals(servletPath)) {

			// =================================================================
			// Es un acceso a la URL de logout
			// =================================================================
			logger.log(Level.FINE, "[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Se detecta un acceso a la URL de logout ({0})", logoutPath);
			// Armar el SAML Request de logout y enviarlo
			initLogoutProcess(programData, httpRequest, httpResponse);
			// Destruir la sesión (no importa el resultado de la solicitud de logout)
			httpRequest.getSession().invalidate();
		} else {

			// =================================================================
			// Es un acceso a una URL comun
			// =================================================================

			logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] No es un acceso a la URL de retorno ni de logout.");
			
			// Si ya hay datos de login cargados en la sesion no se verifica
			// nada, solo se cargan esos datos y se continua
			UserData userData = (UserData) httpSession.getAttribute(USER_DATA_CACHE);
			if (userData != null) {
				logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Cargando datos de usuario en la sesión.");
				// Poner los datos del usuario en la sesion
				loadUserDataToSession(httpRequest, userData, programData);
				getNext().invoke(httpRequest, httpResponse);
				return;
			}

			// Verificar si hay que redirigir al CDA; hay 3 cosas a considerar:
			// 1 - Si la URL coincide con algún ptrón de páginas restringidas
			// 2 - Si la URL no coincide con algún patrón de páginas excluidas
			// 3 - Si mediante consultas SQL se requiere CDA
			String requestedResource = httpRequest.getContextPath() + httpRequest.getServletPath();
			boolean isRestricted = false;
			// Verificar si es una URL restringida
			for (String restrictedUrl : programData.getRestrictedUrlList()) {
				logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Verificando si es restringida -- comparando con "
				    + restrictedUrl);
				if (requestedResource.matches(restrictedUrl)) {
					isRestricted = true;
					logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Es restringida");
					break;
				}
			}
			// Si es restringida verificar si no es una URL excluida
			if (isRestricted) {
				for (String excludedUrl : programData.getExcludedUrlList()) {
					logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Verificando si está excluida -- comparando con "
					    + excludedUrl);
					if (requestedResource.matches(excludedUrl)) {
						logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Está excluida");
						isRestricted = false;
						break;
					}
				}
			}
			// Si continúa siendo restringida hay que ejecutar las consultas SQL
			// para determinar si se requiere CDA o no
			if (isRestricted) {
				logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] La URL es restringida, aplicando consultas SQL");

				if (sqlDatasource != null && !sqlDatasource.trim().isEmpty() && sqlSchemaResolverQuery != null
				    && !sqlSchemaResolverQuery.trim().isEmpty() && sqlCDARequiredQuery != null && !sqlCDARequiredQuery.trim().isEmpty()) {
					logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Hay consultas SQL para aplicar");
					try {
						Context ctx = new InitialContext();
						logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Origen de datos a utilizar: " + sqlDatasource);
						DataSource ds = (DataSource) ctx.lookup(sqlDatasource);
						if (ds != null) {
							logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Se obtuvo un datasource");
							Connection conn = ds.getConnection();
							Statement st1 = conn.createStatement();
							Map<String, String[]> params = httpRequest.getParameterMap();
							String sqlQuery = sqlSchemaResolverQuery;
							sqlQuery = remplazarParametros(sqlQuery, httpRequest);
							logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Consulta para determinar esquema: " + sqlQuery);
							ResultSet rs1 = st1.executeQuery(sqlQuery);
							String schema = null;
							if (rs1.next()) {
								schema = rs1.getString(1);
							}
							rs1.close();
							st1.close();
							logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Esquema obtenido: " + schema);
							Boolean cdaRequired = null;
							if (schema != null) {
								st1 = conn.createStatement();
								sqlQuery = sqlCDARequiredQuery;
								sqlQuery = sqlQuery.replace("{esquema}", schema);
								sqlQuery = remplazarParametros(sqlQuery, httpRequest);
								logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Consulta para determinar si se requiere CDA: "
								    + sqlQuery);
								rs1 = st1.executeQuery(sqlQuery);
								if (rs1.next()) {
									cdaRequired = rs1.getBoolean(1);
								}
								rs1.close();
								st1.close();
								logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Se requiere CDA: " + cdaRequired);
							}
							conn.close();
							if (cdaRequired != null) {
								isRestricted = cdaRequired.booleanValue();
							}
						} else {
							logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] No se obtuvo un datasource");
						}
					} catch (SQLException | NamingException ex) {
						logger.severe("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] " + ex.getMessage());
						ex.printStackTrace();
						isRestricted = false;
					}
				} else {
					logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] No hay consultas SQL para aplicar");
				}
			}

			logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Es una URL restringida final: " + isRestricted);
			if (isRestricted) {
				logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Hay que iniciar el proceso de login");
				initLoginProcess(programData, httpRequest, httpResponse);
				return;
			}

			getNext().invoke(httpRequest, httpResponse);
		}
	}

	// ===============================================================
	// PARAMETROS DE CONFIGURACION
	public String getIdpUrlLogin() {
		return idpUrlLogin;
	}

	public void setIdpUrlLogin(String idpUrlLogin) {
		this.idpUrlLogin = idpUrlLogin;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public void setCertAlias(String certAlias) {
		this.certAlias = certAlias;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getSpReturnUrl() {
		return spReturnUrl;
	}

	public void setSpReturnUrl(String spReturnUrl) {
		this.spReturnUrl = spReturnUrl;
	}

	public String getReturnPath() {
		return returnPath;
	}

	public void setReturnPath(String returnPath) {
		this.returnPath = returnPath;
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath;
	}

	public String getKeystorePass() {
		return keystorePass;
	}

	public void setKeystorePass(String keystorePass) {
		this.keystorePass = keystorePass;
	}

	public String getTruststorePath() {
		return truststorePath;
	}

	public void setTruststorePath(String truststorePath) {
		this.truststorePath = truststorePath;
	}

	public String getTruststorePass() {
		return truststorePass;
	}

	public void setTruststorePass(String truststorePass) {
		this.truststorePass = truststorePass;
	}

	public boolean isRelaxValidityPeriod() {
		return relaxValidityPeriod;
	}

	public void setRelaxValidityPeriod(boolean relaxValidityPeriod) {
		this.relaxValidityPeriod = relaxValidityPeriod;
	}

	public String getRestrictedUrls() {
		return restrictedUrls;
	}

	public void setRestrictedUrls(String restrictedUrls) {
		this.restrictedUrls = restrictedUrls;
	}

	public String getDefaultRoles() {
		return defaultRoles;
	}

	public void setDefaultRoles(String defaultRoles) {
		this.defaultRoles = defaultRoles;
	}

	public String getIdpUrlLogout() {
		return idpUrlLogout;
	}

	public void setIdpUrlLogout(String idpUrlLogout) {
		this.idpUrlLogout = idpUrlLogout;
	}

	public String getLogoutPath() {
		return logoutPath;
	}

	public void setLogoutPath(String logoutPath) {
		this.logoutPath = logoutPath;
	}

	public String getAttributeMappings() {
		return attributeMappings;
	}

	public void setAttributeMappings(String attributeMappings) {
		this.attributeMappings = attributeMappings;
	}

	public String getPrivisioningConfigs() {
		return privisioningConfigs;
	}

	public void setPrivisioningConfigs(String privisioningConfigs) {
		this.privisioningConfigs = privisioningConfigs;
	}

	public String getExcludedUrls() {
		return excludedUrls;
	}

	public void setExcludedUrls(String excludedUrls) {
		this.excludedUrls = excludedUrls;
	}

	public String getUsernameTransformations() {
		return usernameTransformations;
	}

	public void setUsernameTransformations(String usernameTransformations) {
		this.usernameTransformations = usernameTransformations;
	}

	public String getSqlDatasource() {
		return sqlDatasource;
	}

	public void setSqlDatasource(String sqlDatasource) {
		this.sqlDatasource = sqlDatasource;
	}

	public String getSqlSchemaResolverQuery() {
		return sqlSchemaResolverQuery;
	}

	public void setSqlSchemaResolverQuery(String sqlSchemaResolverQuery) {
		this.sqlSchemaResolverQuery = sqlSchemaResolverQuery;
	}

	public String getSqlCDARequiredQuery() {
		return sqlCDARequiredQuery;
	}

	public void setSqlCDARequiredQuery(String sqlCDARequiredQuery) {
		this.sqlCDARequiredQuery = sqlCDARequiredQuery;
	}

	// METODOS AUXILIARES
	private void logRequest(Request httpRequest) {
		logger.log(Level.FINEST, "AuthType: {0}", httpRequest.getAuthType());
		logger.log(Level.FINEST, "CharacterEncoding: {0}", httpRequest.getCharacterEncoding());
		logger.log(Level.FINEST, "ContentType: {0}", httpRequest.getContentType());
		logger.log(Level.FINEST, "ContextPath: {0}", httpRequest.getContextPath());
		logger.log(Level.FINEST, "DecodedRequestURI: {0}", httpRequest.getDecodedRequestURI());
		logger.log(Level.FINEST, "Info: {0}", httpRequest.getInfo());
		logger.log(Level.FINEST, "LocalAddr: {0}", httpRequest.getLocalAddr());
		logger.log(Level.FINEST, "LocalName: {0}", httpRequest.getLocalName());
		logger.log(Level.FINEST, "Method: {0}", httpRequest.getMethod());
		logger.log(Level.FINEST, "PathInfo: {0}", httpRequest.getPathInfo());
		logger.log(Level.FINEST, "PathTranslated: {0}", httpRequest.getPathTranslated());
		logger.log(Level.FINEST, "Protocol: {0}", httpRequest.getProtocol());
		logger.log(Level.FINEST, "QueryString: {0}", httpRequest.getQueryString());
		logger.log(Level.FINEST, "RemoteAddr: {0}", httpRequest.getRemoteAddr());
		logger.log(Level.FINEST, "RemoteHost: {0}", httpRequest.getRemoteHost());
		logger.log(Level.FINEST, "RemoteUser: {0}", httpRequest.getRemoteUser());
		logger.log(Level.FINEST, "RequestURI: {0}", httpRequest.getRequestURI());
		logger.log(Level.FINEST, "RequestedSessionId: {0}", httpRequest.getRequestedSessionId());
		logger.log(Level.FINEST, "Scheme: {0}", httpRequest.getScheme());
		logger.log(Level.FINEST, "ServerName: {0}", httpRequest.getServerName());
		logger.log(Level.FINEST, "ServletPath: {0}", httpRequest.getServletPath());
		logger.log(Level.FINEST, "UserPrincipal: {0}", httpRequest.getUserPrincipal());
	}

	private String initLoginProcess(CDAServiceProviderData programData, Request httpRequest, Response httpResponse) {
		String requestId = sendRedirectPostFormLogin(programData, httpRequest, httpResponse);
		logger.log(Level.FINE, "[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Proceso de login iniciado; requestId: {0}", requestId);
		RequestData requestData = new RequestData(requestId, buildOriginalUrl(httpRequest));
		httpRequest.getSession().setAttribute(REQUEST_DATA_CACHE, requestData);
		return requestId;
	}

	private String sendRedirectPostFormLogin(CDAServiceProviderData programData, Request httpRequest, Response httpResponse) {
		try {
			AuthnRequest authnRequest = buildSAMLAuthnRequest(programData, providerId);

			printXMLObject("AUTHNREQUEST", authnRequest);

			Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(authnRequest);
			org.w3c.dom.Element authDOM = marshaller.marshall(authnRequest);
			StringWriter rspWrt = new StringWriter();
			XMLHelper.writeNode(authDOM, rspWrt);

			String samlRequest = Base64.encodeBytes(rspWrt.toString().getBytes(), Base64.DONT_BREAK_LINES);

			// Nota: tal vez sea necesario URLEncode a samlRequest
			// (URLEncoder.encode(samlRequest, "UTF-8"))
			String html = "<html><form id='form1' action='" + idpUrlLogin + "' method='POST'>" + "<input type='hidden' name='"
			    + SAML_REQUEST + "' value='" + samlRequest + "' />";
			if (debug) {
				html += "<input type='submit' value='Login'/></form></html>";
			} else {
				html += "</form><script>document.getElementById('form1').submit();</script></html>";
			}

			httpResponse.getWriter().write(html);

			return authnRequest.getID();
		} catch (IOException | MarshallingException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void initLogoutProcess(CDAServiceProviderData programData, Request httpRequest, Response httpResponse) {

		UserData userData = (UserData) httpRequest.getSession().getAttribute(USER_DATA_CACHE);
		if (userData == null) {
			logger.fine("No se encontraron datos para el usuario.");
			return;
		}

		sendRedirectPostFormLogout(programData, httpRequest, httpResponse, userData);

		httpRequest.getSession().removeAttribute(USER_DATA_CACHE);
	}

	/**
	 * En el nuevo CDA el logout se hace mediante un REDIRECT (302 FOUND) y no
	 * mediante POST. Según el documento
	 * "Bindings for the OASIS Security Assertion Markup Language (SAML)" la
	 * aplicación debe enviar al usuario una instrucción redirect a la URL de
	 * logout del CDA (por ejemplo,
	 * https://test-eid.portal.gub.uy/idp/profile/SAML2/Redirect/SLO) con los
	 * siguientes parámetros: - SAMLRequest: solicitud de logout; debe ser de tipo
	 * LogoutRequest y NO debe estar firmada. Al momento de ponerlo como parámetro
	 * debe ser comprimido (deflated) y codificado en base64. - SigAlg: URI del
	 * algoritmo de firma (http://www.w3.org/2000/09/xmldsig#rsa-sha1) -
	 * Signature: firma de los dos parámetros anteriores; para esto deben
	 * concatenarse ambos parámetros de la siguiente manera:
	 * SAMLRequest=<valor>&SigAlg=<valor> y luego firmarse usando SHA1 y RSA o
	 * DSA.
	 * 
	 * @param programData
	 * @param httpRequest
	 * @param httpResponse
	 * @param userData
	 * @return
	 */
	private void sendRedirectPostFormLogout(CDAServiceProviderData programData, Request httpRequest, Response httpResponse,
	    UserData userData) {
		try {

			// Armar la solicitud de logout
			LogoutRequest logoutRequest = buildSAMLLogoutRequest(programData, providerId, userData);

			Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(logoutRequest);
			org.w3c.dom.Element authDOM = marshaller.marshall(logoutRequest);

			// Comprimir el token SAML usando DEFLATE y codificarlo en base 64
			StringWriter rspWrt = new StringWriter();
			XMLHelper.writeNode(authDOM, rspWrt);
			Deflater deflater = new Deflater(9, true);
			deflater.setInput(rspWrt.toString().getBytes());
			deflater.finish();
			byte[] buf = new byte[rspWrt.toString().getBytes().length];
			int size = deflater.deflate(buf);
			deflater.end();
			String samlRequest = Base64.encodeBytes(buf, 0, size, Base64.DONT_BREAK_LINES);

			// Determinar la firma de los parámetros
			String signatureAlg = SignatureMethod.RSA_SHA1; // "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
			String queryParametersToSign = "SAMLRequest=" + URLEncoder.encode(samlRequest, "UTF-8");
			queryParametersToSign = queryParametersToSign + "&SigAlg=" + URLEncoder.encode(signatureAlg, "UTF-8");
			PrivateKeyEntry pkEntry = (PrivateKeyEntry) programData.getKeyStore().getEntry(certAlias,
			    new PasswordProtection(keystorePass.toCharArray()));
			PrivateKey pk = pkEntry.getPrivateKey();
			java.security.Signature rsa = java.security.Signature.getInstance("SHA1withRSA");
			rsa.initSign(pk);
			rsa.update(queryParametersToSign.getBytes("UTF-8"));
			String sigStringVal = Base64.encodeBytes(rsa.sign(), Base64.DONT_BREAK_LINES);

			// Armar la URL para la redirección
			String redirectUrl = idpUrlLogout;
			redirectUrl = redirectUrl + (redirectUrl.indexOf("?") < 0 ? "?" : "&");
			redirectUrl = redirectUrl + queryParametersToSign;
			redirectUrl = redirectUrl + "&Signature=" + URLEncoder.encode(sigStringVal, "UTF-8");

			// Enviar la orden de redicción
			httpResponse.sendRedirect(redirectUrl);

		} catch (IOException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | SignatureException
		    | UnrecoverableEntryException | MarshallingException ex) {
			ex.printStackTrace();
		}
	}

	private AuthnRequest buildSAMLAuthnRequest(CDAServiceProviderData programData, String sIssuer) {
		try {
			DateTime ahora = new DateTime();

      SAMLObjectBuilder authnRequestBuilder = (SAMLObjectBuilder) builderFactory.getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);

			SAMLObjectBuilder issuerBuilder = (SAMLObjectBuilder) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
			Issuer issuer = (Issuer) issuerBuilder.buildObject();
			issuer.setValue(sIssuer);

			String sAssertionId = idGenerator.generateIdentifier();
			if (!sAssertionId.startsWith("_")) {
				sAssertionId = "_" + sAssertionId;
			}

			AuthnRequest authnRequest = (AuthnRequest) authnRequestBuilder.buildObject();
			authnRequest.setID("AuthnRequest" + sAssertionId + "-" + (new Date()).getTime());
			authnRequest.setVersion(SAMLVersion.VERSION_20);
			authnRequest.setIssueInstant(ahora);
			authnRequest.setIssuer(issuer);
			authnRequest.setAssertionConsumerServiceURL(spReturnUrl);
			authnRequest.setAttributeConsumingServiceIndex(0);
			authnRequest.setConsent(RequestAbstractType.OBTAINED_CONSENT);
			authnRequest.setDestination(idpUrlLogin);
			authnRequest.setForceAuthn(Boolean.FALSE);
			authnRequest.setProviderName(sIssuer);

			AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();
			Audience issuerAudience = new AudienceBuilder().buildObject();
			issuerAudience.setAudienceURI(sIssuer);
			audienceRestriction.getAudiences().add(issuerAudience);
			Conditions conditions = new ConditionsBuilder().buildObject();
			conditions.getAudienceRestrictions().add(audienceRestriction);
			authnRequest.setConditions(conditions);

			AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject(SAMLConstants.SAML20_NS,
			    AuthnContextClassRef.DEFAULT_ELEMENT_LOCAL_NAME, "saml2");
			RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder().buildObject();
			requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
			authnRequest.setRequestedAuthnContext(requestedAuthnContext);

			// Firma
			PrivateKeyEntry pkEntry = (PrivateKeyEntry) programData.getKeyStore().getEntry(certAlias, new PasswordProtection(keystorePass.toCharArray()));
			PrivateKey pk = pkEntry.getPrivateKey();
			X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
			BasicX509Credential credential = new BasicX509Credential();
			credential.setEntityCertificate(certificate);
			credential.setPrivateKey(pk);

			SignatureBuilder signatureBuilder = (SignatureBuilder) builderFactory.getBuilder(Signature.DEFAULT_ELEMENT_NAME);
			Signature signature = (Signature) signatureBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
			signature.setSigningCredential(credential);

			SecurityConfiguration secConfig = Configuration.getGlobalSecurityConfiguration();
			SecurityHelper.prepareSignatureParams(signature, credential, secConfig, null);
			authnRequest.setSignature(signature);
			Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(authnRequest);
			marshaller.marshall(authnRequest);
			Signer.signObject(signature);

			return authnRequest;
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | MarshallingException
		    | SecurityException | org.opensaml.xml.signature.SignatureException e) {
			e.printStackTrace();
		}
		return null;
	}

	// http://sureshatt.blogspot.com/2012/11/how-to-read-saml-20-response-with.html
	private String processSAMLResponse(String sSamlResponse, CDAServiceProviderData programData, Request httpRequest,
	    Response httpResponse) throws CDAServiceProviderException {
		try {
			org.opensaml.saml2.core.Response samlResponse;
			Element element;
			try {
				byte[] bSamlResponse = Base64.decode(sSamlResponse);
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentBuilderFactory.setNamespaceAware(true);
				DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = null;
				//Intentar parsear la respuesta tal cual, si no se puede es probable que esté comprimida
				//Cuando es una respuesta POST (request response) viene sin comprimir, 
				//cuando es una respuesta GET (logout response) viene comprimida
				try {
					document = docBuilder.parse(new ByteArrayInputStream(bSamlResponse));
				}catch(SAXParseException saxEx) {
					try {
							document = docBuilder.parse(new ByteArrayInputStream(inflate(bSamlResponse, true)));
						}catch(Exception ex) {
							//No se comprende la respuesta
							throw new CDAServiceProviderException("No se comprende la respuesta del Proveedor de Identidades", ex);
						}
				}

				element = document.getDocumentElement();
				UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
				Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
				XMLObject responseXmlObj = unmarshaller.unmarshall(element);
				String responseType = element.getLocalName();
				if("LogoutResponse".equals(responseType)) {
					//Es una respuesta de logout, simplemente terminar la invocación
					httpResponse.setContentType("application/xml");
					ServletOutputStream soStream = httpResponse.getOutputStream();
					soStream.flush();
					soStream.close();
					return "ok";
				}else if("Response".equals(responseType)) {
					samlResponse = (org.opensaml.saml2.core.Response) responseXmlObj;
				}else {
					throw new CDAServiceProviderException("No se acepta el tipo de respuesta del Proveedor de Identidades: "+responseType+" (solo se acepta Response y LogoutResponse)");
				}
			} catch (IOException | ParserConfigurationException | UnmarshallingException | SAXException ex) {
				ex.printStackTrace();
				throw new CDAServiceProviderException("Error obteniendo la respuesta del Proveedor de Identidades", ex);
			}

			printXMLObject("AUTHRESPONSE", samlResponse);

			// Validar la firma
			if (samlResponse.getAssertions() == null || samlResponse.getAssertions().isEmpty()) {
				throw new CDAServiceProviderException("La respuesta del Proveedor de Identidades no contiene informaciÃ³n sobre el usuario");
			}
			// Obtener la informacion del usuario (solo deberia venir una assertion, no mas)
			Assertion assertion = samlResponse.getAssertions().get(0);
			if (!assertion.isSigned()) {
				throw new CDAServiceProviderException("La respuesta del Proveedor de Identidades no estÃ¡ firmada digitalmente, no se acepta");
			}
			// Esto es necesario porque el atributo "ID" (con mayusculas) no es igual al atributo "Id"
			// y por lo tanto al validar la firma no encuentra ningun elemento con el Id buscado
			// Lo que hace es registrar el atributo "ID" como un atributo id valido
			NodeList assertionElements = element.getElementsByTagNameNS(SAMLConstants.SAML20_NS, Assertion.DEFAULT_ELEMENT_LOCAL_NAME);
			for (int i = 0; i < assertionElements.getLength(); i++) {
				Element assertionElement = (Element) assertionElements.item(i);
				assertionElement.setIdAttributeNS(null, "ID", true);
			}
			// Ver si la firma cumple el estandar SAML
			Signature signature = assertion.getSignature();
			try {
				SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
				profileValidator.validate(signature);
			} catch (ValidationException ex) {
				logger.log(Level.SEVERE, "firma del token saml no cumple con el estandar SAML", ex);
				throw new CDAServiceProviderException("La firma del mensaje enviado por el Proveedor de Identidades no cumple el estÃ¡ndar SAML 2.0, no se acepta");
			}
			// La firma cumple el estandar, hay que validarla, validarla contra todos los certificados del truststore
			Enumeration<String> aliases = programData.getTrustStore().aliases();
			KeyStoreX509CredentialAdapter credential;
			SignatureValidator signatureValidator;
			String alias;
			boolean firmaValida = false;
			while (aliases.hasMoreElements()) {
				alias = aliases.nextElement();
				credential = new KeyStoreX509CredentialAdapter(programData.getTrustStore(), alias, truststorePass.toCharArray());
				signatureValidator = new SignatureValidator(credential);
				try {
					signatureValidator.validate(signature);
					firmaValida = true;
					logger.log(Level.FINEST, "firma del token saml ok");
					break;
				} catch (ValidationException ex) {
					logger.log(Level.FINEST, "firma del token saml invalida", ex);
					// La firma no es criptograficamente valida con este
					// certificado (probar con otro)
					// ex.printStackTrace();
				}
			}

			if (!firmaValida) {
				if (validarFirma) {
					throw new CDAServiceProviderException("La firma del mensaje enviado por el Proveedor de Identidades no pudo ser validada con ninguno de los certificados del TrustStore, no se acepta");
				} else {
					logger.warning("La firma del mensaje enviado por el Proveedor de Identidades no pudo ser validada con ninguno de los certificados del TrustStore");
				}
			}
			
			//La firma es válida, ver si tiene un nivel de seguridad aceptable
			//(Debe tener uno de los atributos "Certificado" o "Presencial" en true)
			assertion.getAttributeStatements();
			boolean certificado = false;
			boolean presencial = false;
			for (AttributeStatement attSt : assertion.getAttributeStatements()) {
				for (Attribute att : attSt.getAttributes()) {
					if ("Certificado".equalsIgnoreCase(att.getName())) {
						XSString attVal = (XSString) att.getAttributeValues().get(0);
						String sCertificado = attVal.getValue();
						if("true".equalsIgnoreCase(sCertificado)) {
							certificado = true;
						}
					}
					if ("Presencial".equalsIgnoreCase(att.getName())) {
						XSString attVal = (XSString) att.getAttributeValues().get(0);
						String sPresencial = attVal.getValue();
						if("true".equalsIgnoreCase(sPresencial)) {
							presencial = true;
						}
					}
				}
			}
			if (!certificado && !presencial) {
				throw new CDAServiceProviderException("La cuenta del usuario en CDA no está autenticada, no se acepta");
			} 
			
			
			// Verificar el status
			Status status = samlResponse.getStatus();
			String statusCode = status.getStatusCode().getValue();

			if (!StatusCode.SUCCESS_URI.equals(statusCode)) {
				throw new CDAServiceProviderException("La respuesta enviada por el Proveedor de Identidades no es afirmativa (" + statusCode + "), no se acepta");
			}

			// Verificar la vigencia del token
			Conditions conditions = assertion.getConditions();
			if (!relaxValidityPeriod) {
				if (conditions == null || conditions.getNotBefore() == null || conditions.getNotOnOrAfter() == null) {
					throw new CDAServiceProviderException("La respuesta enviada por el Proveedor de Identidades no especifica su periodo de vigencia, no se acepta");
				}
				DateTime ahora = new DateTime();
				if (conditions.getNotBefore().isBefore(ahora) || conditions.getNotOnOrAfter().isAfter(ahora)) {
					throw new CDAServiceProviderException("La respuesta enviada por el Proveedor de Identidades esta fuera de su periodo de vigencia, no se acepta");
				}
			}

			// Verificar que el token este dirigido a este SP
			boolean isAudienceOk = false;
			if (conditions.getAudienceRestrictions() != null) {
				for (AudienceRestriction audienceRestriction : conditions.getAudienceRestrictions()) {
					List<Audience> audiences = audienceRestriction.getAudiences();
					if (audiences != null) {
						for (Audience audience : audiences) {
							if (audience.getAudienceURI() != null && audience.getAudienceURI().equals(providerId)) {
								isAudienceOk = true;
							}
						}
					}
				}
			}
			if (!isAudienceOk) {
				throw new CDAServiceProviderException("La respuesta enviada por el Proveedor de Identidades no está dirigida a este servidor, no se acepta");
			}

			// Si llego hasta aca, el token es valido
			// Obtener los datos asociados al usuario
			RequestData requestData = (RequestData) httpRequest.getSession().getAttribute(REQUEST_DATA_CACHE);

			if (requestData == null) {
				throw new CDAServiceProviderException("La respuesta enviada por el Proveedor de Identidades no está dirigida a este servidor, no se acepta");
			}

			// ToDo: asegurar el identificador para el usuario
			// Nota: se usa el remoteHost ya que todas las invocaciones a este
			// servidor provenientes del
			// mismo host se consideran que estan incluidas en el SSO
			String userDataId = "SSO_" + idGenerator.generateIdentifier() + "_" + ((new Date()).getTime());

			Subject subject = assertion.getSubject();

			if (subject == null || subject.getNameID() == null || subject.getNameID().getValue() == null) {
				throw new CDAServiceProviderException(
				    "El mensaje enviado por el Proveedor de Identidades no contiene informaciÃ³n sobre el usuario autenticado");
			}

			// String userName = subject.getNameID().getValue();

			String userId = null;
			String firstName = null;
			String lastName1 = null;
			String lastname2 = null;
			for (AttributeStatement attSt : assertion.getAttributeStatements()) {
				for (Attribute att : attSt.getAttributes()) {
					if ("uid".equalsIgnoreCase(att.getName())) {
						XSString attVal = (XSString) att.getAttributeValues().get(0);
						userId = attVal.getValue();
					}
					if ("PrimerNombre".equalsIgnoreCase(att.getName())) {
						XSString attVal = (XSString) att.getAttributeValues().get(0);
						firstName = attVal.getValue();
					}
					if ("PrimerApellido".equalsIgnoreCase(att.getName())) {
						XSString attVal = (XSString) att.getAttributeValues().get(0);
						lastName1 = attVal.getValue();
					}
					if ("SegundoApellido".equalsIgnoreCase(att.getName())) {
						XSString attVal = (XSString) att.getAttributeValues().get(0);
						lastname2 = attVal.getValue();
					}
				}
			}

			String userName = " ";
			if (firstName != null) {
				userName = userName + firstName + " ";
			}
			if (lastName1 != null) {
				userName = userName + lastName1 + " ";
			}
			if (lastname2 != null) {
				userName = userName + lastname2 + " ";
			}
			if (userId != null) {
				userName = userName + "(" + userId + ")";
			}
			userName = userName.trim();

			if (userName.isEmpty()) {
				subject.getNameID().getValue();
				userName = applyTransformations(userName, programData);
			}

			//userName = "[CDA]"+subject.getNameID().getValue() + "/" + userName;
			
			List<String> sessionIndexes = new ArrayList<String>();
			if (assertion.getAuthnStatements() != null && !assertion.getAuthnStatements().isEmpty()) {
				AuthnStatement authStatement = assertion.getAuthnStatements().get(0);
				if (authStatement.getSessionIndex() != null) {
					sessionIndexes.add(authStatement.getSessionIndex());
				}
			}

			UserData userData = new UserData(userDataId, userName, httpRequest.getRemoteHost(), sessionIndexes);

			if (assertion.getAttributeStatements() != null) {
				for (AttributeStatement attStatement : assertion.getAttributeStatements()) {
					if (attStatement.getAttributes() != null) {
						for (Attribute attribute : attStatement.getAttributes()) {
							if (attribute.getNameFormat() != null && attribute.getNameFormat().equals(Attribute.URI_REFERENCE)) {
								for (XMLObject xmlo : attribute.getAttributeValues()) {
									userData.getAttributes().put(attribute.getName(), getStringValueFromXMLObject(xmlo));
								}
							}
						}
					}
				}
			}

			logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] La respuesta se proceso correctamente.");

			httpRequest.getSession(true).setAttribute(USER_DATA_CACHE, userData);

			httpRequest.getSession().removeAttribute(REQUEST_DATA_CACHE);

			// Devolver la URL original que habia pedido el usuario para
			// redirigirlo
			return requestData.getOriginalUrl();
		} catch (CDAServiceProviderException sspEx) {
			throw sspEx;
		} catch (IllegalArgumentException | IllegalStateException | KeyStoreException | DOMException ex) {
			throw new CDAServiceProviderException("Error grave procesando la solicitud: " + ex.getMessage(), ex);
		}
	}

	private LogoutRequest buildSAMLLogoutRequest(CDAServiceProviderData programData, String sIssuer, UserData userData) {
		try {
			String sNameId = userData.getUserName();

			DateTime ahora = new DateTime();

			SAMLObjectBuilder authnRequestBuilder = (SAMLObjectBuilder) builderFactory.getBuilder(LogoutRequest.DEFAULT_ELEMENT_NAME);

			SAMLObjectBuilder issuerBuilder = (SAMLObjectBuilder) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
			Issuer issuer = (Issuer) issuerBuilder.buildObject();
			issuer.setValue(sIssuer);

			String sAssertionId = idGenerator.generateIdentifier();
			if (!sAssertionId.startsWith("_")) {
				sAssertionId = "_" + sAssertionId;
			}

			SAMLObjectBuilder nameIdBuilder = (SAMLObjectBuilder) builderFactory.getBuilder(NameID.DEFAULT_ELEMENT_NAME);
			NameID nameId = (NameID) nameIdBuilder.buildObject();
			nameId.setFormat(NameIdentifier.EMAIL);
			nameId.setValue(sNameId);

			LogoutRequest logoutRequest = (LogoutRequest) authnRequestBuilder.buildObject();
			logoutRequest.setDestination(idpUrlLogout);
			logoutRequest
			    .setID("LogoutRequest" + sAssertionId + "-" + (new Date()).getTime() /* "Assertion-uuid7989a210-0141-1ae2-b4c4-c02d0ee4a0a3" */);
			logoutRequest.setVersion(SAMLVersion.VERSION_20);
			logoutRequest.setIssueInstant(ahora);
			logoutRequest.setIssuer(issuer);
			logoutRequest.setNameID(nameId);

			if (userData.getSessionIndexes() != null) {
				for (String sSessionIndex : userData.getSessionIndexes()) {
					SessionIndex sessionIndex = new SessionIndexBuilder().buildObject();
					sessionIndex.setSessionIndex(sSessionIndex);
					logoutRequest.getSessionIndexes().add(sessionIndex);
				}
			}

			return logoutRequest;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// =======================================================================

	/**
	 * Transforma el nombre de usuario dado segÃ±n las transformaciones definidas
	 * (esto es porque el TFIM de agesic devuelve los nombres de los usuarios en
	 * formato estandar pais-tipodocumento-numerodocumento, donde tipodocumento es
	 * un codigo estandar numerico en lugar del nombre (ci, pasaporte, etc).
	 *
	 * @param userName
	 * @return
	 */
	private String applyTransformations(String userName, CDAServiceProviderData programData) {
		if (userName == null || programData == null || programData.getUsernameTransformationsMap() == null) {
			return "";
		}

		logger.log(Level.FINE, "Nombre de usuario antes: {0}", userName);

		String repl;
		for (String orig : programData.getUsernameTransformationsMap().keySet()) {
			repl = programData.getUsernameTransformationsMap().get(orig);
			userName = userName.replace(orig, repl);
		}

		logger.log(Level.FINE, "Nombre de usuario despues: {0}", userName);

		return userName;
	}

	private void loadUserDataToSession(Request httpRequest, UserData userData, CDAServiceProviderData programData) {

		//Si los datos ya se cargaron anteriormente no se vuelven a cargar
		HttpSession session = httpRequest.getSession(true);
		if(session.getAttribute("CDAServiceProviderValve")!=null) {
			logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Los datos del usuario ya fueron cargados previamente, no se vuelven a cargar");
			return;
		}
		
		if (userData == null) {
			logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] No hay datos de usuario para cargar");
			return;
		}

		String context = httpRequest.getContextPath();
		if (context != null && context.startsWith("/")) {
			context = context.substring(1);
		}

		// 12/11/2014 - Bug que mantiene los roles
		programData.initRoles(defaultRoles);

		Principal principal = userData.getPrincipals().get(context);

		if ((setPrincipalEnable && principal == null) || (setContextEnable && session.getAttribute(this.contextAttribute) == null)) {

			List<String> roles = programData.getDefaultRolesList();

			if (setPrincipalEnable) {
				// Generar el principal
				principal = new GenericPrincipal(null, userData.getUserName(), null, roles);
				logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Guardado usuario en el principal");
			}

			if (setContextEnable) {

				ContextValvula contextValvula = new ContextValvula();
				ContextValvula.Roles contextValvulaRoles = new ContextValvula.Roles();
				contextValvula.setUsuario(userData.getUserName());
				contextValvulaRoles.getItem().addAll(roles);
				contextValvula.setRoles(contextValvulaRoles);
				JAXBContext jaxbContext;
				String value = null;
				try {

					jaxbContext = JAXBContext.newInstance(ContextValvula.class);
					javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
					marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
					StringWriter writer = new StringWriter();
					marshaller.marshal(contextValvula, writer);
					value = writer.toString();
				} catch (JAXBException ex) {
					Logger.getLogger(CDAServiceProviderValve.class.getName()).log(Level.SEVERE, null, ex);
				}

				session.setAttribute(this.contextAttribute, value);
			}

			// Almacenar el principal en los datos del usuario
			userData.getPrincipals().put(context, principal);
		}
		httpRequest.setUserPrincipal(principal);
		
		//Poner una variable en la sesión indicando que los datos ya fueron cargados
		session.setAttribute("CDAServiceProviderValve", true);

		//Poner en la sesión los datos configurados en la variable attributeMappings
		//Por cada mapping de la forma contexto.nombrecda=nombreapp pone una variable en la sesión
		//  llamada "nombreapp" cuyo valor es el de la propiedad llamada "nombrecda" en el SAML de respuesta de CDA
		//  Ejemplo: sae-admin.uid=codigocda --> codigocda=uy-ci-42502648
		//  <saml2:Attribute FriendlyName="UID" Name="uid" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
    //    <saml2:AttributeValue xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">uy-ci-42502648</saml2:AttributeValue>
    //  </saml2:Attribute>
		//Además también se pone el mismo atributo encriptado con la clave privada del certificado usado para conectarse
		//a CDA para que la aplicación pueda utilizarla para loguear al usuario con el módulo SACDALoginModule
		
		try {
			//También generar una contraseña temporal para este usuario
			FileInputStream is = new FileInputStream(keystorePath);
      KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      char[] passwd = keystorePass.toCharArray();
      keystore.load(is, passwd);
      Key privateKey = keystore.getKey(certAlias, passwd);
  	  Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
  	  Key privKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKey.getEncoded()));
  	  cipher.init(Cipher.ENCRYPT_MODE, privKey);
		
			String key;
			String clave;
			String valor;
			Map<String, String> attributes = userData.getAttributes();
			Map<String, String> mappings = programData.getAttributeMappingsMap();
			for (String att : attributes.keySet()) {
				key = context + "." + att;
				clave = mappings.get(key);
				if (clave != null) {
					valor = attributes.get(att);
					//Valor sin encriptar
					session.setAttribute(clave, valor);
					//Valor encriptado
					byte[] bytes = cipher.doFinal(valor.getBytes());
					session.setAttribute(clave+"_encriptado", bytes);
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		logger.fine("[" + httpRequest.getContextPath()+"/"+httpRequest.getServletPath() + "] Los datos del usuario fueron cargados en la sesión");
		
	}

	private String buildOriginalUrl(Request httpRequest) {
		String origUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName();
		if (httpRequest.getServerPort() > 0) {
			origUrl = origUrl + ":" + httpRequest.getServerPort();
		}
		origUrl = origUrl + httpRequest.getContextPath() + httpRequest.getServletPath();
		if (httpRequest.getQueryString() != null && !httpRequest.getQueryString().trim().isEmpty()) {
			origUrl = origUrl + "?" + httpRequest.getQueryString();
		}
		return origUrl;
	}

	private void sendErrorMessage(Response httpRequest, CDAServiceProviderException sspEx) {
		try {
			httpRequest.getWriter().write(sspEx.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void printXMLObject(String titulo, XMLObject obj) {
		try {
			Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(obj);
			org.w3c.dom.Element authDOM = marshaller.marshall(obj);
			StringWriter rspWrt = new StringWriter();
			XMLHelper.writeNode(authDOM, rspWrt);
			logger.log(Level.FINE, "================== {0} ========================", titulo);
			logger.fine(rspWrt.toString());
			logger.fine("======================================================");
		} catch (MarshallingException ex) {
			ex.printStackTrace();
		}
	}

	public static String getStringValueFromXMLObject(XMLObject xmlObj) {
		if (xmlObj instanceof XSString) {
			return ((XSString) xmlObj).getValue();
		} else if (xmlObj instanceof XSAny) {
			return ((XSAny) xmlObj).getTextContent();
		}
		return null;
	}

	/**
	 * @return the getParameterMethod
	 */
	public String getGetParameterMethod() {
		return getParameterMethod;
	}

	/**
	 * @param getParameterMethod
	 *          the getParameterMethod to set
	 */
	public void setGetParameterMethod(String getParameterMethod) {
		this.getParameterMethod = getParameterMethod;
	}

	/**
	 * @return the usernameAttribute
	 */
	public String getUsernameAttribute() {
		return contextAttribute;
	}

	/**
	 * @param usernameAttribute
	 *          the usernameAttribute to set
	 */
	public void setUsernameAttribute(String usernameAttribute) {
		this.contextAttribute = usernameAttribute;
	}

	/**
	 * @return the setPrincipalEnable
	 */
	public boolean isSetPrincipalEnable() {
		return setPrincipalEnable;
	}

	/**
	 * @param setPrincipalEnable
	 *          the setPrincipalEnable to set
	 */
	public void setSetPrincipalEnable(boolean setPrincipalEnable) {
		this.setPrincipalEnable = setPrincipalEnable;
	}

	/**
	 * @return the setContextEnable
	 */
	public boolean isSetContextEnable() {
		return setContextEnable;
	}

	/**
	 * @param setContextEnable
	 *          the setContextEnable to set
	 */
	public void setSetContextEnable(boolean setContextEnable) {
		this.setContextEnable = setContextEnable;
	}

	/**
	 * @return the validarFirma
	 */
	public boolean isValidarFirma() {
		return validarFirma;
	}

	/**
	 * @param validarFirma
	 *          the validarFirma to set
	 */
	public void setValidarFirma(boolean validarFirma) {
		this.validarFirma = validarFirma;
	}

	/**
	 * @return the contextAttribute
	 */
	public String getContextAttribute() {
		return contextAttribute;
	}

	/**
	 * @param contextAttribute
	 *          the contextAttribute to set
	 */
	public void setContextAttribute(String contextAttribute) {
		this.contextAttribute = contextAttribute;
	}

	private class RequestData {

		private final String requestId;
		private final String originalUrl;

		public RequestData(String requestId, String originalUrl) {
			this.requestId = requestId;
			this.originalUrl = originalUrl;
		}

    public String getRequestId() {
			return requestId;
		}

		public String getOriginalUrl() {
			return originalUrl;
		}
	}

	private class UserData {

		private final String userDataId;
		private final String userName;
		private final String remoteHost;
		private final Map<String, String> attributes = new HashMap<>();
		private final Map<String, Principal> principals = new HashMap<>(); // Un
		// Principal por cada aplicacion

		// Para el CDA (esto se obtiene de la response inicial)
		private final List<String> sessionIndexes;

		private ContextValvula contextValvula;

		public UserData(String userDataId, String userName, String remoteHost, List<String> sessionIndexes) {
			this.userDataId = userDataId;
			this.userName = userName;
			this.remoteHost = remoteHost;
			this.sessionIndexes = sessionIndexes;
		}

		public String getUserDataId() {
			return userDataId;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public String getRemoteHost() {
			return remoteHost;
		}

		public String getUserName() {
			return userName;
		}

		public Map<String, Principal> getPrincipals() {
			return principals;
		}

		public List<String> getSessionIndexes() {
			return sessionIndexes;
		}

		/**
		 * @return the contextValvula
		 */
		public ContextValvula getContextValvula() {
			return contextValvula;
		}

		/**
		 * @param contextValvula
		 *          the contextValvula to set
		 */
		public void setContextValvula(ContextValvula contextValvula) {
			this.contextValvula = contextValvula;
		}

	}

	private String getParameter(Request request, String paramName) throws IOException {
		if ("GETPARAMETER".equals(getParameterMethod)) {
			return request.getParameter(paramName);
		} else if ("GETINPUTSTREAM".equals(getParameterMethod)) {
			if (request instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) request;
				ServletInputStream sis = null;
				try {
					sis = httpServletRequest.getInputStream();
					Map<String, String[]> tableParam = HttpUtils.parsePostData(request.getContentLength(), sis);
					String[] values = tableParam.get(paramName);
					if (values != null && values.length > 0) {
						String ret = values[0];
						return ret;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					if (sis != null) {
						sis.close();
					}
				}
			}
		}
		return null;
	}

	private String remplazarParametros(String sqlQuery, Request httpRequest) {
		// Primero remplazar los valores del request
		Map<String, String[]> params = httpRequest.getParameterMap();
		for (String param : params.keySet()) {
			if (params.get(param) != null && ((String[]) params.get(param)).length > 0) {
				if (sqlQuery.contains("{" + param + "}")) {
					String paramv = ((String[]) params.get(param))[0];
					sqlQuery = sqlQuery.replace("{" + param + "}", paramv);
				}
			}
		}
		// Si quedó algo intentar remplazarlo con valores de la sesion
		HttpSession httpSession = httpRequest.getSession();
		Enumeration<String> attNames = httpSession.getAttributeNames();
		while (attNames.hasMoreElements()) {
			String attName = attNames.nextElement();
			Object attValue = httpSession.getAttribute(attName);
			if (sqlQuery.contains("{" + attName + "}")) {
				sqlQuery = sqlQuery.replace("{" + attName + "}", attValue.toString());
			}
		}
		return sqlQuery;
	}

	
	private static byte[] inflate(byte[] bytes, boolean nowrap) throws Exception {

    Inflater decompressor = null;
    InflaterInputStream decompressorStream = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
        decompressor = new Inflater(nowrap);
        decompressorStream = new InflaterInputStream(new ByteArrayInputStream(bytes), 
          decompressor);
        byte[] buf = new byte[1024];
        int count;
        while ((count = decompressorStream.read(buf)) != -1) {
            out.write(buf, 0, count);
        }
        return out.toByteArray();
    } finally {
        if (decompressor != null) {
            decompressor.end();
        }
        try {
            if (decompressorStream != null) {
                decompressorStream.close();
            }
         } catch (IOException ioe) {
             /*ignore*/
         }
         try {
             if (out != null) {
                 out.close();
             }
         } catch (IOException ioe) {
             /*ignore*/
         }
      }
   }	
	
	
	/**
	 * Este método solo debería invocarse para procesar los LogoutRequest enviados por CDA cuando otra
	 * aplicación desea hacer logout; 
	 * @param sSamlResponse
	 * @param programData
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 * @throws CDAServiceProviderException
	 */
	private void processSAMLRequest(String sSamlRequest, Request httpRequest,
    Response httpResponse) throws CDAServiceProviderException {
		Element element;
		try {
			byte[] bSamlRequest = Base64.decode(sSamlRequest);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = null;
			//Intentar parsear la solicitud tal cual, si no se puede es probable que esté comprimida
			try {
				document = docBuilder.parse(new ByteArrayInputStream(bSamlRequest));
			}catch(SAXParseException saxEx) {
				try {
						document = docBuilder.parse(new ByteArrayInputStream(inflate(bSamlRequest, true)));
					}catch(Exception ex) {
						//No se comprende la respuesta
						throw new CDAServiceProviderException("No se comprende la solicitud del Proveedor de Identidades", ex);
					}
			}
			element = document.getDocumentElement();
			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
			XMLObject requestXmlObj = unmarshaller.unmarshall(element);
			String responseType = element.getLocalName();
			if("LogoutRequest".equals(responseType)) {
				
				LogoutRequest logoutRequest = (LogoutRequest)requestXmlObj;
				XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
				SAMLObjectBuilder<LogoutResponse> logoutResponseBuilder = (SAMLObjectBuilder<LogoutResponse>) builderFactory.getBuilder(LogoutResponse.DEFAULT_ELEMENT_NAME);
				LogoutResponse logoutResponse = logoutResponseBuilder.buildObject();
				//Destination
				logoutResponse.setDestination(idpUrlLogout);
				//ID
				String sAssertionId = idGenerator.generateIdentifier();
				if (!sAssertionId.startsWith("_")) {
					sAssertionId = "_" + sAssertionId;
				}
				logoutResponse.setID("AuthnRequest" + sAssertionId+ "-" + (new Date()).getTime());
				//Version
				logoutResponse.setVersion(SAMLVersion.VERSION_20);
				//IssueInstant
				logoutResponse.setIssueInstant(new DateTime());
				//InResponseTo
				logoutResponse.setInResponseTo(logoutRequest.getID());
				//Issuer
				SAMLObjectBuilder issuerBuilder = (SAMLObjectBuilder) builderFactory.getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
				Issuer issuer = (Issuer) issuerBuilder.buildObject();
				issuer.setValue(providerId);
				logoutResponse.setIssuer(issuer);
				//Status
				SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>) builderFactory.getBuilder(Status.DEFAULT_ELEMENT_NAME);
				Status status = statusBuilder.buildObject();
				SAMLObjectBuilder<StatusCode> statusCodeBuilder = (SAMLObjectBuilder<StatusCode>) builderFactory.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
				StatusCode statusCode = statusCodeBuilder.buildObject();
				statusCode.setValue(StatusCode.SUCCESS_URI);
				status.setStatusCode(statusCode);
				logoutResponse.setStatus(status);
				
				printXMLObject("LOGOUT RESPONSE OK", logoutResponse);

				Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(logoutResponse);
				org.w3c.dom.Element node = marshaller.marshall(logoutResponse);
				StringWriter rspWrt = new StringWriter();
				XMLHelper.writeNode(node, rspWrt);
				Deflater deflater = new Deflater(9, true);
				deflater.setInput(rspWrt.toString().getBytes());
				deflater.finish();
				byte[] buf = new byte[rspWrt.toString().getBytes().length];
				int size = deflater.deflate(buf);
				deflater.end();
				String samlRequest = Base64.encodeBytes(buf, 0, size, Base64.DONT_BREAK_LINES);
				// Determinar la firma de los parámetros
				String signatureAlg = SignatureMethod.RSA_SHA1; // "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
				String queryParametersToSign = "SAMLResponse=" + URLEncoder.encode(samlRequest, "UTF-8");
				queryParametersToSign = queryParametersToSign + "&SigAlg=" + URLEncoder.encode(signatureAlg, "UTF-8");
				PrivateKeyEntry pkEntry = (PrivateKeyEntry) programData.getKeyStore().getEntry(certAlias, new PasswordProtection(keystorePass.toCharArray()));
				PrivateKey pk = pkEntry.getPrivateKey();
				java.security.Signature rsa = java.security.Signature.getInstance("SHA1withRSA");
				rsa.initSign(pk);
				rsa.update(queryParametersToSign.getBytes("UTF-8"));
				String sigStringVal = Base64.encodeBytes(rsa.sign(), Base64.DONT_BREAK_LINES);
				// Armar la URL para la redirección
				String redirectUrl = idpUrlLogout;
				redirectUrl = redirectUrl + (redirectUrl.indexOf("?") < 0 ? "?" : "&");
				redirectUrl = redirectUrl + queryParametersToSign;
				redirectUrl = redirectUrl + "&Signature=" + URLEncoder.encode(sigStringVal, "UTF-8");
				//Invalidar la sesión
				httpRequest.getSession().invalidate();
				// Enviar la orden de redicción que incluye la respuesta y la firma
				httpResponse.sendRedirect(redirectUrl);
			}else {
				throw new CDAServiceProviderException("No se acepta el tipo de respuesta del Proveedor de Identidades: "+responseType+" (solo se acepta Response y LogoutResponse)");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CDAServiceProviderException("Error obteniendo la respuesta del Proveedor de Identidades", ex);
		}
	}
	
	
}
