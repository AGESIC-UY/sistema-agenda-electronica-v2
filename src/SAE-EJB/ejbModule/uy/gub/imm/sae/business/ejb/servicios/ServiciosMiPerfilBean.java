package uy.gub.imm.sae.business.ejb.servicios;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uy.gub.imm.sae.business.ejb.facade.Configuracion;
import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.business.utilidades.Metavariables;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.AccionMiPerfil;
import uy.gub.imm.sae.entity.Comunicacion;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;
/* */
/* */

@Stateless
public class ServiciosMiPerfilBean {

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
	private EntityManager globalEntityManager;

	@PersistenceContext(unitName = "SAE-EJB")
    private EntityManager entityManager;

    @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionLocal")
	private Configuracion confBean;

	private static final Logger logger = Logger.getLogger(ServiciosMiPerfilBean.class);

	private static final DateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd");
	
	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/RecursosBean!uy.gub.imm.sae.business.ejb.facade.RecursosLocal")
	private Recursos recursosEJB;



	public boolean enviarAviso(Empresa empresa, Reserva reserva, Comunicacion.Tipo2 tipoAviso) {

    logger.debug("Comenzando a enviar un aviso a MiPerfil");

    String tituloAviso = null;
    String textoCorto = null;
    String textoAviso = null;
    Integer vencim = null;
    JsonArray jAcciones = new JsonArray();
    AccionMiPerfil accionMiPerfil = null;

		//Primero ver si la agenda soporta el envío de avisos
		Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
    boolean habilitado = false;
   
    switch (tipoAviso) {
      case RESERVA:
      case RESERVAMULTIPLE:
        if(recurso.getMiPerfilConHab()!=null) {
          habilitado = recurso.getMiPerfilConHab();
        }
        tituloAviso = recurso.getMiPerfilConTitulo();
        textoCorto = recurso.getMiPerfilConCorto();
        textoAviso = recurso.getMiPerfilConLargo();
        vencim = recurso.getMiPerfilConVencim();
        
        //Cargo acciones
        accionMiPerfil= recursosEJB.obtenerAccionMiPerfilDeRecurso(recurso.getId());
        if (accionMiPerfil != null){
        	if (!accionMiPerfil.getUrl_con_1().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_con_1());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_con_1().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}", ""+confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_con_1().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_con_2().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_con_2());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_con_2().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_con_2().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_con_3().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_con_3());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_con_3().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_con_3().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_con_4().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_con_4());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_con_4().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_con_4().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_con_5().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_con_5());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_con_5().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_con_5().toString());
        		jAcciones.add(jObject);
        	}
        }
        
        break;
      case CANCELA:
      case CANCELAMULTIPLE:
        if(recurso.getMiPerfilCanHab()!=null) {
          habilitado = recurso.getMiPerfilCanHab();
        }
        tituloAviso = recurso.getMiPerfilCanTitulo();
        textoCorto = recurso.getMiPerfilCanCorto();
        textoAviso = recurso.getMiPerfilCanLargo();
        vencim = recurso.getMiPerfilCanVencim();
        
        //Cargo acciones
        accionMiPerfil = recursosEJB.obtenerAccionMiPerfilDeRecurso(recurso.getId());
        if (accionMiPerfil != null){
        	if (!accionMiPerfil.getUrl_can_1().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_can_1());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_can_1().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_can_1().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_can_2().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_can_2());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_can_2().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_can_2().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_can_3().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_can_3());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_can_3().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_can_3().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_can_4().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_can_4());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_can_4().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_can_4().toString());
        		jAcciones.add(jObject);
        	}
        	if (!accionMiPerfil.getUrl_can_5().isEmpty()){
        		JsonObject jObject = new JsonObject();
        	    jObject.addProperty("nombreBoton", accionMiPerfil.getTitulo_can_5());
        	    jObject.addProperty("linkBoton", accionMiPerfil.getUrl_can_5().replace("{latitud}", ""+recurso.getLatitud()).replace("{longitud}", ""+recurso.getLongitud()).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+recurso.getAgenda().getId()).replace("{reserva}", ""+reserva.getId()));
        	    jObject.addProperty("destacada", accionMiPerfil.getDestacada_can_5().toString());
        		jAcciones.add(jObject);
        	}
        }
        
      default:
        break;
    }
		if (!habilitado) {
	    logger.debug("Se ignora la solicitud porque los avisos de confirmación de MiPerfil no están habilitados para el recurso.");
			return false;
		}
		//Después ver si la instalación soporta la integración con MI PERFIL
		try {
			habilitado = confBean.getBoolean("WS_MIPERFIL_HABILITADO");
		} catch (Exception nfEx) {
			habilitado = false;
		}
		if (!habilitado) {
      logger.debug("Se ignora la solicitud porque MiPerfil no está habilitado para la instalación.");
			return false;
		}

    String numeroDocumento = reserva.getNumeroDocumento();
    if(numeroDocumento==null || numeroDocumento.trim().isEmpty()) {
      logger.debug("Se ignora la solicitud porque no se tiene el número de documento del ciudadano.");
      return false;
    }

    String paisDocumento = reserva.getPaisDocumento();
    if(paisDocumento==null || paisDocumento.trim().isEmpty()) {
      paisDocumento = "uy";
    }

    String tipoDocumento = reserva.getTipoDocumento();
    if(tipoDocumento==null || tipoDocumento.trim().isEmpty()) {
      tipoDocumento = "ci";
    }

    String codigoUsuario = "SAE-e"+empresa.getId()+"&a"+recurso.getAgenda().getId()+"&r"+recurso.getId();

		String formatoFecha = (empresa != null && empresa.getFormatoFecha() != null? empresa.getFormatoFecha(): "dd/MM/yyyy");
    String formatoHora = (empresa != null && empresa.getFormatoHora() != null? empresa.getFormatoHora(): "HH:mm");

		JsonArray jDests = new JsonArray();
		jDests.add(paisDocumento.toLowerCase()+"-"+tipoDocumento.toLowerCase()+"-"+numeroDocumento);


	//G.R. - 21/10/19 - En los textos, remplazo las comillas dobles por \" para evitar mal formación en el Json (hago un escapeo).
	//Luego hago un segundo remplazo de caracteres, para cubrirme del caso en el que el usuario haya guardado el texto con el escapeo incluido.		
    String jsonEscapeValor = confBean.getString("JSON_ESCAPE");
    if (jsonEscapeValor != null && jsonEscapeValor.equalsIgnoreCase("true")){
    	tituloAviso = tituloAviso.replace("\"", "\\\"");
    	textoCorto = textoCorto.replace("\"", "\\\"");
    	textoAviso = textoAviso.replace("\"", "\\\"");
    	tituloAviso = tituloAviso.replace("\\\\\"", "\\\"");
    	textoCorto = textoCorto.replace("\\\\\"", "\\\"");
    	textoAviso = textoAviso.replace("\\\\\"", "\\\"");
    }
		
    //No se admiten las metavariables para el link de cancelación y modificación porque se envían como acciones
    JsonObject jObject = new JsonObject();
    String miPerfilOId = confBean.getString("MIPERFIL_OID");
    if (miPerfilOId != null && !miPerfilOId.isEmpty()){
    	jObject.addProperty("organismoOID", miPerfilOId);
    }else{
    	jObject.addProperty("organismoOID", empresa.getOid());
    }
    jObject.addProperty("usuario", codigoUsuario);
    jObject.addProperty("tipoAviso", 1);
    jObject.addProperty("tituloAviso", Metavariables.remplazarMetavariables(tituloAviso, reserva, formatoFecha, formatoHora, null, null));
    jObject.addProperty("textoCorto", Metavariables.remplazarMetavariables(textoCorto, reserva, formatoFecha, formatoHora, null, null));
    jObject.addProperty("textoAviso", Metavariables.remplazarMetavariables(textoAviso, reserva, formatoFecha, formatoHora, null, null));
    jObject.addProperty("fechaEnvio", FORMATO_FECHA.format(new Date()));
    if(vencim!=null && vencim>0) {
      Calendar fVen = new GregorianCalendar();
      fVen.add(Calendar.DATE, vencim);
      jObject.addProperty("fechaVencimiento", FORMATO_FECHA.format(fVen.getTime()));
//    }else {
//      jObject.addProperty("fechaVencimiento", "");
    }
    jObject.addProperty("todosDestinatarios", false);
    jObject.add("listaDestinatarios", jDests);
    jObject.add("acciones", jAcciones);

    invocarServicioRESTMiPerfil(jObject);

		return true;
	}

  private boolean invocarServicioRESTMiPerfil(JsonObject jObject) {
    try {

      String sUrl = confBean.getString("WS_MIPERFIL_URL");
      if (sUrl == null || sUrl.equalsIgnoreCase("")){
    	  logger.error("WS_MIPERFIL_URL no configurado en conf global "+ sUrl);
    	  return false;
      }
      URL url = new URL(sUrl);
      HttpURLConnection conn;
      boolean secure = sUrl.startsWith("https://");
      if (secure) {
        String sKsPath = confBean.getString("WS_MIPERFIL_KS_PATH");
        String sKsPass = confBean.getString("WS_MIPERFIL_KS_PASS");

        String sTsPath = confBean.getString("WS_MIPERFIL_TS_PATH");
        String sTsPass = confBean.getString("WS_MIPERFIL_TS_PASS");

        //Inicializar el contexto SSL
        //Crear el TrustManager para confiar en los certificados contenidos en el keystore
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(sTsPath), sTsPass.toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(sKsPath), sKsPass.toCharArray());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, sKsPass.toCharArray());

        //Crear un contexto SSL que utilice el TrustManager creado
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        //Abrir la conexión
        conn = (HttpsURLConnection) url.openConnection();
        ((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
      } else {
        conn = (HttpURLConnection) url.openConnection();
      }
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setConnectTimeout(60000);
      conn.setReadTimeout(300000);

      logger.debug("Enviando mensaje: "+jObject.toString());

      OutputStream os = conn.getOutputStream();
      os.write(jObject.toString().getBytes());
      os.flush();

      if(conn.getResponseCode()==HttpURLConnection.HTTP_CREATED) {
        logger.info("Mensaje enviado correctamente");
      }else {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
        StringBuffer response = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
          response.append(line);
        }
        jObject = new JsonParser().parse(response.toString()).getAsJsonObject();
        logger.warn("No se pudo enviar el mensaje: "+jObject.get("mensaje").toString());
      }
      conn.disconnect();
      return (conn.getResponseCode() == HttpURLConnection.HTTP_CREATED);
    } catch (Exception ex) {
      logger.error("No se pudo invocar un servicio de MiPerfil", ex);
      return false;
    }

  }
  
  
  private boolean invocarServicioRESTMiPerfilRecordatorio(JsonObject jObject) {
	    try {

	    	
	    	 //String sql6 = "select valor ae_configuracion where clave = :conf";
	        Query query1 =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('WS_MIPERFIL_URL')");
	        List<Object[]> valores1 = query1.getResultList();
	        Query query2 =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('WS_MIPERFIL_KS_PATH')");
	        List<Object[]> valores2 = query2.getResultList();
	        
	        Query query3 =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('WS_MIPERFIL_KS_PASS')");
	        List<Object[]> valores3 = query3.getResultList();
	        
	        Query query4 =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('WS_MIPERFIL_TS_PATH')");
	        List<Object[]> valores4 = query4.getResultList();
	        
	        
	        Query query5 =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('WS_MIPERFIL_TS_PASS')");
	        List<Object[]> valores5 = query5.getResultList();
	        
	        String sUrl = null;
	        for(Object[] valor : valores1) {
	        	sUrl = valor[1] + "";
	        }
	        
	     
	      if (sUrl == null || sUrl.equalsIgnoreCase("")){
	    	  logger.error("WS_MIPERFIL_URL no configurado en conf global "+ sUrl);
	    	  return false;
	      }
	      URL url = new URL(sUrl);
	      HttpURLConnection conn;
	      boolean secure = sUrl.startsWith("https://");
	      if (secure) {
	        String sKsPath = null;
	        for(Object[] valor : valores2) {
	        	sKsPath = valor[1] + "";
	        }
	        String sKsPass = null;
	        for(Object[] valor : valores3) {
	        	sKsPass = valor[1] + "";
	        }
	        String sTsPath = null;
	        for(Object[] valor : valores4) {
	        	sTsPath = valor[1] + "";
	        }
	        String sTsPass = null;
	        for(Object[] valor : valores5) {
	        	sTsPass = valor[1] + "";
	        }

	        //Inicializar el contexto SSL
	        //Crear el TrustManager para confiar en los certificados contenidos en el keystore
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(new FileInputStream(sTsPath), sTsPass.toCharArray());
	        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	        trustManagerFactory.init(trustStore);

	        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        keyStore.load(new FileInputStream(sKsPath), sKsPass.toCharArray());
	        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	        keyManagerFactory.init(keyStore, sKsPass.toCharArray());

	        //Crear un contexto SSL que utilice el TrustManager creado
	        SSLContext sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
	        //Abrir la conexión
	        conn = (HttpsURLConnection) url.openConnection();
	        ((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
	      } else {
	        conn = (HttpURLConnection) url.openConnection();
	      }
	      conn.setDoOutput(true);
	      conn.setRequestMethod("POST");
	      conn.setRequestProperty("Content-Type", "application/json");
	      conn.setConnectTimeout(60000);
	      conn.setReadTimeout(300000);

	      logger.debug("Enviando mensaje: "+jObject.toString());

	      OutputStream os = conn.getOutputStream();
	      os.write(jObject.toString().getBytes());
	      os.flush();

	      if(conn.getResponseCode()==HttpURLConnection.HTTP_CREATED) {
	        logger.info("Mensaje enviado correctamente");
	      }else {
	        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
	        StringBuffer response = new StringBuffer();
	        String line;
	        while ((line = br.readLine()) != null) {
	          response.append(line);
	        }
	        jObject = new JsonParser().parse(response.toString()).getAsJsonObject();
	        logger.warn("No se pudo enviar el mensaje: "+jObject.get("mensaje").toString());
	      }
	      conn.disconnect();
	      return (conn.getResponseCode() == HttpURLConnection.HTTP_CREATED);
	    } catch (Exception ex) {
	      logger.error("No se pudo invocar un servicio de MiPerfil", ex);
	      return false;
	    }

	  }

  /* Envía los recordatorios pendientes */

  private static final long LOCK_ID_REINTENTAR = 1717171717;

  @SuppressWarnings("unchecked")
  @Schedule(second="0", minute="*/30", hour="*", persistent=false)
  public void enviarRecordatorios(){
	logger.info("COMIENZA enviarRecordatorios*************************************");
    //Intentar liberar el lock por si lo tiene esta instancia
    boolean lockOk = (boolean)globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+")").getSingleResult();
    logger.info("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+") : " + lockOk);
    //Intentar obtener el lock
    lockOk = (boolean)globalEntityManager.createNativeQuery("SELECT pg_try_advisory_lock("+LOCK_ID_REINTENTAR+")").getSingleResult();
    logger.info("SELECT pg_try_advisory_lock("+LOCK_ID_REINTENTAR+") " + lockOk);
    if(!lockOk) {
      //Otra instancia tiene el lock
      logger.info("No se ejecuta el reintento de envio de recordatorios porque hay otra instancia haciéndolo.");
      return;
    }
    //No hay otra instancia con el lock, se continúa

    try {
      //Determinar el tiempo máximo que puede estar una reserva pendiente sin confirmación
      Query query =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('WS_MIPERFIL_HABILITADO')");
      List<Object[]> valores = query.getResultList();
      boolean miPerfilHabilitado = false;
      for(Object[] valor : valores) {
        if("WS_MIPERFIL_HABILITADO".equals(valor[0])) {
          try {
            miPerfilHabilitado = Boolean.valueOf(valor[1].toString());
          }catch(Exception ex) {
            //Nada para hacer, valor por defecto
          }
        }
      }
      logger.info("miPerfilHabilitado: "  +miPerfilHabilitado);
      if(!miPerfilHabilitado) {
        //MiPerfil no está habilitado
        return;
      }

      //Obtener los identificadores de todas las empresas
      query =  globalEntityManager.createQuery("SELECT e FROM Empresa e WHERE fecha_baja IS NULL ");
      List<Empresa> empresas = query.getResultList();
//      logger.info("--> QUERY 0: obtener identificadores de todas las empresas --> PARAMETROS: (Ninguno) --> CANT RESULTADOS:" + String.valueOf(empresas.size()));

      //Consulta para obtener los datos de las agendas
      String sql1 = "SELECT a.id, a.nombre, a.timezone FROM {esquema}.ae_agendas a WHERE a.fecha_baja IS NULL";

      //Consulta para obtener los datos de los recursos
      String sql2 = "SELECT r.id, r.nombre, r.direccion, r.mi_perfil_rec_hora, r.mi_perfil_rec_dias, r.mi_perfil_rec_tit, "
          + "r.mi_perfil_rec_cor, r.mi_perfil_rec_lar, r.mi_perfil_rec_ven, r.latitud, r.longitud  "
          + "FROM {esquema}.ae_recursos r WHERE r.aeag_id=:idAgenda and r.fecha_baja IS NULL AND r.mi_perfil_rec_hab=true AND "
          + "r.mi_perfil_rec_dias IS NOT NULL AND r.mi_perfil_rec_hora<=:horaActual";

      //Consulta para obtener las reservas que hay que notificar
      String sql3 = "SELECT r.id, r.tramite_nombre, d.hora_inicio, r.serie, r.numero, r.codigo_seguridad, r.trazabilidad_guid, "
          + "dr1.valor as tipodoc, dr2.valor as numdoc, dr3.valor as paisdoc "
          + "FROM {esquema}.ae_reservas r JOIN {esquema}.ae_reservas_disponibilidades rd on rd.aers_id=r.id "
          + "JOIN {esquema}.ae_disponibilidades d on d.id=rd.aedi_id "
          + "LEFT JOIN {esquema}.ae_datos_a_solicitar ds1 on ds1.aere_id=d.aere_id and ds1.nombre='TipoDocumento' "
          + "LEFT JOIN {esquema}.ae_datos_reserva dr1 on dr1.aeds_id=ds1.id and dr1.aers_id=r.id "
          + "LEFT JOIN {esquema}.ae_datos_a_solicitar ds2 on ds2.aere_id=d.aere_id and ds2.nombre='NroDocumento' "
          + "LEFT JOIN {esquema}.ae_datos_reserva dr2 on dr2.aeds_id=ds2.id and dr2.aers_id=r.id "
          + "LEFT JOIN {esquema}.ae_datos_a_solicitar ds3 on ds3.aere_id=d.aere_id and ds3.nombre='PaisDocumento' "
          + "LEFT JOIN {esquema}.ae_datos_reserva dr3 on dr3.aeds_id=ds3.id and dr3.aers_id=r.id "
          + "WHERE d.aere_id=:idRecurso AND r.estado=:eReservada AND d.fecha>=current_date and r.mi_perfil_notif=false "
          + "AND DATE_PART('day', cast(d.fecha as timestamp)-current_date)<=:dias";

      String sql4 = "UPDATE {esquema}.ae_reservas set mi_perfil_notif=true WHERE id=:idReserva";
      
      String sql5 = "SELECT  titulo_con_1, url_con_1, destacada_con_1, titulo_con_2,url_con_2, destacada_con_2, titulo_con_3, url_con_3, destacada_con_3, "
      		+ "  titulo_con_4, url_con_4, destacada_con_4, titulo_con_5, url_con_5, "
      		+ " destacada_con_5, titulo_can_1, url_can_1, destacada_can_1, titulo_can_2, "
      		+ " url_can_2, destacada_can_2, titulo_can_3, url_can_3, destacada_can_3, "
      		+ " titulo_can_4, url_can_4, destacada_can_4, titulo_can_5, url_can_5, "
      		+ " destacada_can_5, titulo_rec_1, url_rec_1, destacada_rec_1, titulo_rec_2, "
      		+ " url_rec_2, destacada_rec_2, titulo_rec_3, url_rec_3, destacada_rec_3, "
      		+ " titulo_rec_4, url_rec_4, destacada_rec_4, titulo_rec_5, url_rec_5, "
      		+ " destacada_rec_5"
      		+ "  FROM {esquema}.ae_acciones_miperfil_recurso WHERE recurso_id=:idRecurso";

      
      //String sql6 = "select valor ae_configuracion where clave = :conf";
      Query queryOid =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('MIPERFIL_OID')");
      List<Object[]> valoresOid = queryOid.getResultList();
      Query queryJson =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN ('JSON_ESCAPE')");
      List<Object[]> valoresJson = queryJson.getResultList();
      
      
      //Determinar la hora actual
      Calendar ahora = new GregorianCalendar();

      for(Empresa empresa : empresas) {
        if(empresa.getDatasource() != null) {
          try {
            String formatoFecha = empresa.getFormatoFecha()!=null?empresa.getFormatoFecha():"dd/MM/yyyy";
            String formatoHora = empresa.getFormatoHora()!=null?empresa.getFormatoHora():"HH:mm";

            String sql = sql1.replace("{esquema}", empresa.getDatasource());
            Query query1 = entityManager.createNativeQuery(sql);
            List<Object[]> agendas = query1.getResultList();
            //logger.info("--> QUERY 1: obtener los datos de las agendas --> PARAMETROS: {esquema}="+empresa.getDatasource()+"  --> CANT RESULTADOS:" + String.valueOf(agendas.size()));           
           
            TimeZone timeZone = null;
            
//          ////////////////////////////////////////////////////////////////////////////////////////////////  
//          /////////////PRUEBA metavariables fecha y hora//////////////////////////////////////////////////
//          ////////////////////////////////////////////////////////////////////////////////////////////////  
//            Date fechaPrueba = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("10-06-2018 02:01:15");
//            String strPRUEBA = "/////HORA: {{HORA}} //// FECHA: {{FECHA}}";
//            strPRUEBA = Metavariables.remplazarMetavariables(strPRUEBA, 1, "","",
//            		"", "", fechaPrueba, "", 1, "", "",
//                    (String)formatoFecha, (String)formatoHora, "", "");
//          ////////////////////////////////////////////////////////////////////////////////////////////////
//          ////////////////////////////////////////////////////////////////////////////////////////////////
            
            for(Object[] agenda : agendas) {
              //Determinar el timezone según la agenda o la empresa
              if(agenda[2]!=null && !agenda[2].toString().isEmpty()) {
                timeZone = TimeZone.getTimeZone(agenda[2].toString());
              }else {
                timeZone = TimeZone.getTimeZone(empresa.getTimezone());
              }
              //Ajustar la hora actual según el timezone
              Calendar ahoraTZ = (Calendar) ahora.clone();
              ahoraTZ.add(Calendar.MILLISECOND, timeZone.getOffset(ahoraTZ.getTimeInMillis()));
              
              logger.info("ahoraTZ HOUR OF DAY: "  +ahoraTZ.get(Calendar.HOUR_OF_DAY));
              if (ahoraTZ.get(Calendar.HOUR_OF_DAY) < 8 || ahoraTZ.get(Calendar.HOUR_OF_DAY) > 20){
            	  logger.error("No se pudo enviar recordatorios para la empresa "+empresa.getNombre()+"(esquema '"+empresa.getDatasource()+"') dado que se encuentra fuera de horario de envio 8 a 20 "+ahoraTZ.get(Calendar.HOUR_OF_DAY));
            	  continue;
              }else{
            	  logger.error("Hora correcto para envio en la  "+empresa.getNombre()+"(esquema '"+empresa.getDatasource()+"') " +timeZone +" - Hora Actual: "+ahoraTZ.get(Calendar.HOUR_OF_DAY));
              } 
              
              sql = sql2.replace("{esquema}", empresa.getDatasource());
              Query query2 = entityManager.createNativeQuery(sql);
              List<Object[]> recursos = query2.setParameter("idAgenda", agenda[0]).setParameter("horaActual", ahoraTZ.get(Calendar.HOUR_OF_DAY)).getResultList();
//              logger.info("--> QUERY 2: obtener los datos de los recursos --> PARAMETROS: {esquema}="+empresa.getDatasource()+"  --> CANT RESULTADOS:" + String.valueOf(recursos.size()));
              
              logger.info("recursos tamano : "  +recursos.size());
              for(Object[] recurso : recursos) {
                //Si no están definidos los tres textos (título, corto, largo) no se envía recordatorio
            	  
            	logger.info("recursos definido los tres textos? : ID recurso "+(Integer)recurso[0] + " TEXTOS definidos: "  + (recurso[5]!=null && recurso[6]!=null && recurso[7]!=null));
                if(recurso[5]!=null && recurso[6]!=null && recurso[7]!=null) {
                  try {
                    //Buscar las reservas que hay que notificar
                	  
                	  sql = sql3.replace("{esquema}", empresa.getDatasource());
                      Query query3 = entityManager.createNativeQuery(sql);
                	  	List<Object[]> reservas = query3.setParameter("idRecurso", (Integer)recurso[0]).setParameter("eReservada", Estado.R.toString())
                        .setParameter("dias", (Integer)recurso[4]).getResultList();
//                    logger.info("--> QUERY 3: obtener los datos de las reservas a notificar --> PARAMETROS: idrecurso="+recurso[0]+ " eReservada="+Estado.R.toString()+"  --> CANT RESULTADOS:" + String.valueOf(reservas.size()));

                    String codigoUsuario = "SAE-e"+empresa.getId()+"&a"+agenda[0]+"&r"+recurso[0];
                    
                    logger.info("reservas tamano : "  +reservas.size());
                    for(Object[] reserva : reservas) {
                      logger.info("comienza a procesar reserva id : "  +reserva[0]);
                      String numeroDocumento = (String)reserva[8];
                      if(numeroDocumento==null || numeroDocumento.trim().isEmpty()) {
                        logger.debug("Se ignora la solicitud porque no se tiene el número de documento del ciudadano.");
                      }else {

                        String paisDocumento = (String)reserva[9];
                        if(paisDocumento==null || paisDocumento.trim().isEmpty()) {
                          paisDocumento = "uy";
                        }

                        String tipoDocumento = (String)reserva[7];
                        if(tipoDocumento==null || tipoDocumento.trim().isEmpty()) {
                          tipoDocumento = "ci";
                        }

                        logger.info("reserva id : "  +numeroDocumento + "-" + paisDocumento + "-" +tipoDocumento );
                        
                        String tituloAviso = (String)recurso[5];
                        String textoCorto = (String)recurso[6];
                        String textoAviso = (String)recurso[7];
                    	
                        //G.R. - 21/10/19 - En los textos, remplazo las comillas dobles por \" para evitar mal formación en el Json (hago un escapeo).
                        //Luego hago un segundo remplazo de caracteres, para cubrirme del caso en el que el usuario haya guardado el texto con el escapeo incluido.
                        String jsonEscapeValor =  null;
                        for(Object[] valor : valoresJson) {
                        	if (valor[1] != null){
                        		jsonEscapeValor = valor[1] + "";	
                        	}
                            
                         }
                       
                        logger.info("jsonEscapeValor: "  +jsonEscapeValor );
                        if (jsonEscapeValor != null && jsonEscapeValor.equalsIgnoreCase("true")){
                        	tituloAviso = tituloAviso.replace("\"", "\\\"");
                        	textoCorto = textoCorto.replace("\"", "\\\"");
                        	textoAviso = textoAviso.replace("\"", "\\\"");
                        	tituloAviso = tituloAviso.replace("\\\\\"", "\\\"");
                        	textoCorto = textoCorto.replace("\\\\\"", "\\\"");
                        	textoAviso = textoAviso.replace("\\\\\"", "\\\"");	
                        }
                        

                        logger.info("tituloAviso: "  +tituloAviso );
                        logger.info("textoCorto: "  +textoCorto );
                        
                        JsonArray jDests = new JsonArray();
                        jDests.add(paisDocumento.toLowerCase()+"-"+tipoDocumento.toLowerCase()+"-"+numeroDocumento);

                        JsonArray jAcciones = new JsonArray();
                        
                        //Cargo acciones
                        //AccionMiPerfil accionMiPerfil = recursosEJB.obtenerAccionMiPerfilDeRecurso((Integer)recurso[0]);
                        sql = sql5.replace("{esquema}", empresa.getDatasource());
                        Query query5 = entityManager.createNativeQuery(sql);
                        List<Object[]> accionMiPerfilList = query5.setParameter("idRecurso", recurso[0]).getResultList();
                        if (accionMiPerfilList != null && accionMiPerfilList.size() > 0){
                        	Object[] accionMiPerfil = accionMiPerfilList.get(0);
                        	if (accionMiPerfil[1] != null && !accionMiPerfil[1].toString().isEmpty() ){
                        		JsonObject jObject = new JsonObject();
                        	    jObject.addProperty("nombreBoton", accionMiPerfil[0] + "");
                        	    
                        	    jObject.addProperty("linkBoton", (""+accionMiPerfil[1]).replace("{latitud}", ""+recurso[9]).replace("{longitud}", ""+recurso[10]).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+agenda[0]).replace("{reserva}", ""+reserva[0]));
                        	    jObject.addProperty("destacada", ""+accionMiPerfil[2]);
                        		jAcciones.add(jObject);
                        	}
                        	if (accionMiPerfil[4] != null && !accionMiPerfil[4].toString().isEmpty() ){
                        		JsonObject jObject = new JsonObject();
                        	    jObject.addProperty("nombreBoton", accionMiPerfil[3] + "");
                        	    
                        	    jObject.addProperty("linkBoton", (""+accionMiPerfil[4]).replace("{latitud}", ""+recurso[9]).replace("{longitud}", ""+recurso[10]).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+agenda[0]).replace("{reserva}", ""+reserva[0]));
                        	    jObject.addProperty("destacada", ""+accionMiPerfil[5]);
                        		jAcciones.add(jObject);
                        	}
                        	if (accionMiPerfil[7] != null && !accionMiPerfil[7].toString().isEmpty() ){
                        		JsonObject jObject = new JsonObject();
                        	    jObject.addProperty("nombreBoton", accionMiPerfil[6] + "");
                        	    
                        	    jObject.addProperty("linkBoton", (""+accionMiPerfil[7]).replace("{latitud}", ""+recurso[9]).replace("{longitud}", ""+recurso[10]).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+agenda[0]).replace("{reserva}", ""+reserva[0]));
                        	    jObject.addProperty("destacada", ""+accionMiPerfil[8]);
                        		jAcciones.add(jObject);
                        	}
                        	if (accionMiPerfil[10] != null && !accionMiPerfil[10].toString().isEmpty() ){
                        		JsonObject jObject = new JsonObject();
                        	    jObject.addProperty("nombreBoton", accionMiPerfil[9] + "");
                        	    
                        	    jObject.addProperty("linkBoton", (""+accionMiPerfil[10]).replace("{latitud}", ""+recurso[9]).replace("{longitud}", ""+recurso[10]).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+agenda[0]).replace("{reserva}", ""+reserva[0]));
                        	    jObject.addProperty("destacada", ""+accionMiPerfil[11]);
                        		jAcciones.add(jObject);
                        	}
                        	if (accionMiPerfil[13] != null && !accionMiPerfil[13].toString().isEmpty() ){
                        		JsonObject jObject = new JsonObject();
                        	    jObject.addProperty("nombreBoton", accionMiPerfil[12] + "");
                        	    
                        	    jObject.addProperty("linkBoton", (""+accionMiPerfil[13]).replace("{latitud}", ""+recurso[9]).replace("{longitud}", ""+recurso[10]).replace("{linkBase}",""+ confBean.getString("DOMINIO")).replace("{empresa}", ""+empresa.getId()).replace("{agenda}", ""+agenda[0]).replace("{reserva}", ""+reserva[0]));
                        	    jObject.addProperty("destacada", ""+accionMiPerfil[14]);
                        		jAcciones.add(jObject);
                        	}
                        }

                        JsonObject jObject = new JsonObject();
                        
                        String miPerfilOId =  null;
                        for(Object[] valor : valoresOid) {
                        	if (valor[1] != null){
                        		miPerfilOId = valor[1] + "";	
                        	}
                            
                         }
                        
                        logger.info("miPerfilOId: "  +miPerfilOId );
                       // String miPerfilOId = confBean.getString("MIPERFIL_OID");
                        if (miPerfilOId != null && !miPerfilOId.isEmpty()){
                        	jObject.addProperty("organismoOID", miPerfilOId);
                        }else{
                        	jObject.addProperty("organismoOID", empresa.getOid());
                        }
                        //logger.info("Fecha/Hora: "  +reserva[2] + "  formatoFecha:"+formatoFecha+ "  formatoHora:"+formatoHora);
                        jObject.addProperty("usuario", codigoUsuario);
                        jObject.addProperty("tipoAviso", 1);
                        jObject.addProperty("tituloAviso", Metavariables.remplazarMetavariables(tituloAviso, (Integer)reserva[0], (String)agenda[1], (String)recurso[1],
                            (String)recurso[2], (String)reserva[1], (Date)reserva[2], (String)reserva[3], (Integer)reserva[4], (String)reserva[5], (String)reserva[6],
                            (String)formatoFecha, (String)formatoHora, null, null));
                        jObject.addProperty("textoCorto", Metavariables.remplazarMetavariables(textoCorto, (Integer)reserva[0], (String)agenda[1], (String)recurso[1],
                            (String)recurso[2], (String)reserva[1], (Date)reserva[2], (String)reserva[3], (Integer)reserva[4], (String)reserva[5], (String)reserva[6],
                            (String)formatoFecha, (String)formatoHora, null, null));
                        jObject.addProperty("textoAviso", Metavariables.remplazarMetavariables(textoAviso, (Integer)reserva[0], (String)agenda[1], (String)recurso[1],
                            (String)recurso[2], (String)reserva[1], (Date)reserva[2], (String)reserva[3], (Integer)reserva[4], (String)reserva[5], (String)reserva[6],
                            (String)formatoFecha, (String)formatoHora, null, null));
                        jObject.addProperty("fechaEnvio", FORMATO_FECHA.format(new Date()));
                        if(recurso[8]!=null && (Integer)recurso[8]>0) {
                          Calendar fVen = new GregorianCalendar();
                          fVen.add(Calendar.DATE, (Integer)recurso[8]);
                          jObject.addProperty("fechaVencimiento", FORMATO_FECHA.format(fVen.getTime()));
//                        }else {
//                          jObject.addProperty("fechaVencimiento", "");
                        }
                        jObject.addProperty("todosDestinatarios", false);
                        jObject.add("listaDestinatarios", jDests);
                        jObject.add("acciones", jAcciones);
                        
                        logger.info("comienza a invocacion Mi perfil para " + jObject); 
                        boolean ok = invocarServicioRESTMiPerfilRecordatorio(jObject);
                        logger.info("finaliza a invocacion Mi perfil estado " + ok);
                        if(ok) {
                          sql = sql4.replace("{esquema}", empresa.getDatasource());
                          Query query4 = entityManager.createNativeQuery(sql).setParameter("idReserva", (Integer)reserva[0]);
                          query4.executeUpdate();
//                          logger.info("--> QUERY 4: marcar la reserva como notificada --> PARAMETROS: {esquema}="+empresa.getDatasource() +"  --> CANT RESULTADOS: no aplica (es un update)");
                        }
                      }
                    }

                  }catch(Exception ex) {
                    ex.printStackTrace();
                    try{
                    	globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+")").getSingleResult();	
                    }catch(Exception ex1) {
                    }  
                    logger.warn("No se pudo enviar recordatorios: empresa: "+empresa.getNombre()+", agenda: "+agenda[1]+", recurso: "+recurso[1]);
                  }
                }
              }
            }
          }catch(Exception ex) {
        	  try{
              	globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+")").getSingleResult();	
              }catch(Exception ex1) {
              }
            logger.error("No se pudo enviar recordatorios para la empresa "+empresa.getNombre()+"(esquema '"+empresa.getDatasource()+"')", ex);
          }
        }
      }

    } catch (Exception ex) {
    	try{
        	globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+")").getSingleResult();	
        }catch(Exception ex1) {
        }
      logger.error("No se pudo enviar recordatorios.", ex);
    }finally {
      //Intentar liberar el lock (si lo tiene esta instancia)
      globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+")").getSingleResult();
      logger.info("Ejecución del reintento de envio de recordatorios finalizado. *********************************************");
    }
  }

}
