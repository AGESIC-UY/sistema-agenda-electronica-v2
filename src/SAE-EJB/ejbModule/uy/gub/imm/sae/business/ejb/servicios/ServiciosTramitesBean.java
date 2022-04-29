package uy.gub.imm.sae.business.ejb.servicios;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
/* */
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import uy.gub.imm.sae.business.ejb.facade.Configuracion;
import uy.gub.imm.sae.business.ws.SoapHandler;
import uy.gub.imm.sae.business.ws.guiatramites.ArrayOfOfNacionalDatos;
import uy.gub.imm.sae.business.ws.guiatramites.ArrayOfResumenTramite;
import uy.gub.imm.sae.business.ws.guiatramites.ArrayOfString;
import uy.gub.imm.sae.business.ws.guiatramites.GuiaTramites;
import uy.gub.imm.sae.business.ws.guiatramites.GuiaTramitesSoap;
import uy.gub.imm.sae.business.ws.guiatramites.ObtTramitesEnOrdenAlfabeticoResponseType;
import uy.gub.imm.sae.business.ws.guiatramites.ObtTramitesPorId;
import uy.gub.imm.sae.business.ws.guiatramites.ObtTramitesPorIdResponse;
import uy.gub.imm.sae.business.ws.guiatramites.ObtTramitesPorOrgEnOrdenAlfabeticoType;
import uy.gub.imm.sae.business.ws.guiatramites.OfNacionalDatos;
import uy.gub.imm.sae.business.ws.guiatramites.ResumenTramite;
import uy.gub.imm.sae.business.ws.wstramite.WsTramite;
import uy.gub.imm.sae.business.ws.wstramite.WsTramiteSoap;
import uy.gub.imm.sae.entity.global.Oficina;
import uy.gub.imm.sae.entity.global.Organismo;
import uy.gub.imm.sae.entity.global.Tramite;
import uy.gub.imm.sae.entity.global.UnidadEjecutora;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;

/**
 * Clase encargada de las invocaciones a los servicios web de TramitesUy.
 * 
 * @author spio
 *
 */
@Stateless
public class ServiciosTramitesBean {

	//@PersistenceContext(unitName = "AGENDA-GLOBAL")
	//private EntityManager globalEntityManager;

  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionLocal")
	private Configuracion confBean;
	
	private static Logger logger = Logger.getLogger(ServiciosTramitesBean.class);

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<Organismo> obtenerOrganismos() throws ApplicationException {
    try {
      logger.info("Obteniendo organismos de TrámitesUy...");
      
      URL urlWsdl = WsTramite.class.getResource("WsTramite.wsdl");
      
      //Obtener el stub del servicio web
      WsTramite wsTramite = new WsTramite(urlWsdl);
      WsTramiteSoap port = wsTramite.getWsTramiteSoap();
      BindingProvider bindingProvider = (BindingProvider) port;

      //Configurar la URL del servicio web
      String wsAddressLocation = confBean.getString("WS_TRAMITE_LOCATION_INFO");
      if(wsAddressLocation != null) {
        //Si no se define se deja lo que tenga el WSDL
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsAddressLocation);
      }
      
      //Configurar el handler SOAP
      List<Handler> customHandlerChain = new ArrayList<Handler>();
      customHandlerChain.add(new SoapHandler());
      bindingProvider.getBinding().setHandlerChain(customHandlerChain);
      
      //Configurar el timeout
      Long timeout = confBean.getLong("WS_TRAMITE_TIMEOUT");
      if(timeout == null) {
        timeout = 5000L;
      }
      bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", timeout);
      bindingProvider.getRequestContext().put("javax.xml.ws.client.receiveTimeout", timeout);
      
      //Invocar la operación
      String wsUser = confBean.getString("WS_TRAMITE_USER");
      String wsPass = confBean.getString("WS_TRAMITE_PASS");
      String sOrganismos = port.obtenerOrganismos(wsUser, wsPass);

      //Parsear el string para obtener los organismos
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(sOrganismos));
      SAXBuilder builder = new SAXBuilder();
      Document document = builder.build(is);
      Element root = document.getRootElement();

      //Ver si la invocación no dio un error
      Filter erroresFilter = new ElementFilter("errores");
      if(erroresFilter!=null) {
        Iterator<Element> erroresIter = (Iterator<Element>)root.getDescendants(erroresFilter);
        if(erroresIter.hasNext()) {
          String error1 = erroresIter.next().getText();
          logger.warn("No se pudo obtener organismos de TrámitesUy: "+error1);
          return null;
        }
      }

      logger.debug("El servicio web se invocó correctamente, procesando los resultados...");
      
      //insertar los organismos nuevos
      Filter filter = new ElementFilter("organismo");
      Iterator<Element> iter = (Iterator<Element>)root.getDescendants(filter);
      List<Organismo> organismos = new ArrayList<Organismo>();
      while(iter.hasNext()) {
        Element element = (Element) iter.next();
        Organismo organismo = new Organismo();
        organismo.setId(Integer.valueOf(element.getAttributeValue("id")));
        organismo.setCodigo(element.getChild("id").getValue());
        organismo.setNombre(element.getChild("nombre").getValue());
        organismos.add(organismo);
      }

      logger.info("Se obtuvieron "+organismos.size()+" organismos.");
      
      return organismos;
    } catch (Exception ex) {
      logger.warn("No se pudo obtener los organismos de TrámitesUy: ", ex);
      return null;
    }
  }
	
  @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<UnidadEjecutora> obtenerUnidadesEjecutoras() throws ApplicationException {
    try {
      logger.info("Obteniendo unidades ejecutoras de TrámitesUy...");
	  
      URL urlWsdl = WsTramite.class.getResource("WsTramite.wsdl");
  
      String wsUser = confBean.getString("WS_TRAMITE_USER");
      String wsPass = confBean.getString("WS_TRAMITE_PASS");
      
      
      //Consultar el servicio web
      WsTramite wsTramite = new WsTramite(urlWsdl);
      WsTramiteSoap port = wsTramite.getWsTramiteSoap();
      BindingProvider bindingProvider = (BindingProvider) port;
      
      //Configurar la URL del servicio web
      String wsAddressLocation = confBean.getString("WS_TRAMITE_LOCATION_INFO");
      if(wsAddressLocation != null) {
        //Si no se define se deja lo que tenga el WSDL
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsAddressLocation);
      }
  
      //Configurar el handler SOAP
      List<Handler> customHandlerChain = new ArrayList<Handler>();
      customHandlerChain.add(new SoapHandler());
      bindingProvider.getBinding().setHandlerChain(customHandlerChain);
  
      //Configurar el timeout
      Long timeout = confBean.getLong("WS_TRAMITE_TIMEOUT");
      if(timeout == null) {
        timeout = 5000L;
      }
      bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", timeout);
      bindingProvider.getRequestContext().put("javax.xml.ws.client.receiveTimeout", timeout);
      
      //Invocar la operación
      String uEjecutoras = port.obtenerUnidadesEjecutoras(wsUser, wsPass);
      
      //Parsear el string para obtener los organismos
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(uEjecutoras));
      SAXBuilder builder = new SAXBuilder();
      Document document = builder.build(is);
      Element root = document.getRootElement();
      Filter erroresFilter = new ElementFilter("errores");
      if(erroresFilter!=null) {
        Iterator<Element> erroresIter = (Iterator<Element>)root.getDescendants(erroresFilter);
        if(erroresIter.hasNext()) {
          String error1 = erroresIter.next().getText();
          logger.warn("No se pudo obtener unidades ejecutoras de TrámitesUy: "+error1);
          return null;
        }
      }
      
      logger.debug("El servicio web se invocó correctamente, procesando los resultados...");
      
      Filter filter = new ElementFilter("ue");
      Iterator iter = (Iterator<Element>)root.getDescendants(filter);
      List<UnidadEjecutora> unidadesEjecutoras = new ArrayList<UnidadEjecutora>();
      while(iter.hasNext()) {
        Element element = (Element) iter.next();
        UnidadEjecutora uEjecutora = new UnidadEjecutora();
        uEjecutora.setId(Integer.valueOf(element.getAttributeValue("id")));
        uEjecutora.setCodigo(element.getChild("id").getValue());
        uEjecutora.setNombre(element.getChild("nombre").getValue());
        unidadesEjecutoras.add(uEjecutora);
      }
      
      logger.info("Se obtuvieron "+unidadesEjecutoras.size()+" unidades ejecutoras.");
      
      return unidadesEjecutoras;
    }catch(Exception ex) {
      logger.warn("No se pudo obtener las unidades ejecutoras de TrámitesUy: ", ex);
      return null;
    }
	}
	
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List<Tramite> obtenerTramitesOrganismoYUnidadEjecutora(Integer empresaId, Integer organismoCod, Integer unidadEjCod) throws ApplicationException, UserException {
    try {
      logger.info("Obteniendo trámites de TrámitesUy (organismoCod="+organismoCod+", unidadEjCod="+unidadEjCod+")...");
      
      //Consultar el servicio web
      URL urlWsdl = GuiaTramites.class.getResource("GuiaTramites.wsdl");
      GuiaTramites guiaTramites = new GuiaTramites(urlWsdl);
      GuiaTramitesSoap port = guiaTramites.getGuiaTramitesSoap();
      BindingProvider bindingProvider = (BindingProvider) port;
      
      //Configurar la URL del servicio web
      String wsAddressLocation = confBean.getString("WS_TRAMITE_LOCATION_GUIA");
      if(wsAddressLocation != null) {
        //Si no se define se deja lo que tenga el WSDL
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsAddressLocation);
      }
      
      List<Handler> customHandlerChain = new ArrayList<Handler>();
      customHandlerChain.add(new SoapHandler());
      bindingProvider.getBinding().setHandlerChain(customHandlerChain);
      
      ObtTramitesPorOrgEnOrdenAlfabeticoType entrada = new ObtTramitesPorOrgEnOrdenAlfabeticoType();
      entrada.setIdOrg(organismoCod);
      entrada.setIdUE(unidadEjCod);
      entrada.setPagina(0);
      entrada.setCantidadEltos(100);
      
      String[] letras0 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "Ñ", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
      List<String> letrasDeshabilitadas = new ArrayList<String>();
      
      List<Tramite> tramitesARetornar = new ArrayList();
      
      //Configurar el timeout desde la configuracion
      Long timeout = confBean.getLong("WS_TRAMITE_TIMEOUT");
      if(timeout == null) {
        timeout = 5000L;
      }
      bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", timeout);
      bindingProvider.getRequestContext().put("javax.xml.ws.client.receiveTimeout", timeout);
      
      for(String letra : letras0) {
        //Si es la primera letra hay que invocar si o si porque no se saben cuales estan deshabilitadas
        if("A".equals(letra) || (!letrasDeshabilitadas.contains(letra) && !letrasDeshabilitadas.contains(letra.toLowerCase()))) {
          entrada.setLetra(letra);
          ObtTramitesEnOrdenAlfabeticoResponseType resp = port.obtTramitesPorOrgEnOrdenAlfabetico(entrada);
          if(resp.getTotalResultados()>0 && resp.getErrores().getMensaje().isEmpty()) {
            ArrayOfResumenTramite tramites = resp.getColTramites();
            //insertar los tramites nuevos
            for(ResumenTramite tram : tramites.getResumenTramite()) {
              Tramite tramite = new Tramite();
              tramite.setEmpresaId(empresaId);
              tramite.setId(empresaId+"-"+tram.getId());
              tramite.setNombre(recortarString(tram.getNombre(), 100));
              tramite.setOnline(tram.isOnLine());
              tramite.setQuees(recortarString(tram.getQueEs(), 1000));
              tramite.setTemas(recortarString(tram.getTemas(), 1000));
              //globalEntityManager.persist(tramite);
              tramitesARetornar.add(tramite);
            }
          }
          //Además, si es la primera letra hay que ver cuáles otras letras están deshabilitadas
          if("A".equalsIgnoreCase(letra)) {
            ArrayOfString letrasDes = resp.getLetrasDeshabilitadas();
            letrasDeshabilitadas.addAll(letrasDes.getString());
          }
        }
      }
      
      logger.info("Se obtuvieron "+tramitesARetornar.size()+" trámites.");
      
      return tramitesARetornar;
      
    } catch (Exception ex) {
      logger.warn("No se pudo obtener los trámites de TrámitesUy: ", ex);
      return null;
    }
  }
	
  @SuppressWarnings({"rawtypes"})
  public List<Oficina> obtenerOficinasTramite(Tramite tramite) throws ApplicationException {
      try {
        logger.info("Obteniendo oficinas de TrámitesUy (tramite="+tramite.getId()+")...");
        
        URL urlWsdl = GuiaTramites.class.getResource("GuiaTramites.wsdl");
        //Consultar el servicio web
        GuiaTramites guiaTramites = new GuiaTramites(urlWsdl);
        GuiaTramitesSoap port = guiaTramites.getGuiaTramitesSoap();
        BindingProvider bindingProvider = (BindingProvider) port;

        //Configurar la URL del servicio web
        String wsAddressLocation = confBean.getString("WS_TRAMITE_LOCATION_GUIA");
        if(wsAddressLocation != null) {
          //Si no se define se deja lo que tenga el WSDL
          bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsAddressLocation);
        }
        
        List<Handler> customHandlerChain = new ArrayList<Handler>();
        customHandlerChain.add(new SoapHandler());
        bindingProvider.getBinding().setHandlerChain(customHandlerChain);
        
        //Obtener el timeout desde la configuracion
        Long timeout = confBean.getLong("WS_TRAMITE_TIMEOUT");
        if(timeout == null) {
          timeout = 5000L;
        }
        bindingProvider.getRequestContext().put("javax.xml.ws.client.connectionTimeout", timeout);
        bindingProvider.getRequestContext().put("javax.xml.ws.client.receiveTimeout", timeout);
        
        String tramiteEmpresaId = tramite.getId();
        String tramiteId = null;
        if(tramiteEmpresaId.indexOf("-")>0) {
          tramiteId = tramiteEmpresaId.substring(tramiteEmpresaId.indexOf("-")+1); 
        }else {
          tramiteId = tramiteEmpresaId;
        }
        
        //La lista de oficinas viene en el detalle del tramite
        ObtTramitesPorId entrada = new ObtTramitesPorId();
        entrada.setId(tramiteId);
        ObtTramitesPorIdResponse tramite0 = port.obtTramitePorId(entrada);
        List<Oficina> oficinas = new ArrayList<Oficina>();
        if(tramite0!=null && tramite0.getDatosTramite()!=null) {
          //insertar los tramites nuevos
          ArrayOfOfNacionalDatos ofNacionales = tramite0.getDatosTramite().getOfNacionales();
          if(ofNacionales != null) {
            int cont = 0;
            for(OfNacionalDatos ofNacional : ofNacionales.getOfNacionalDatos()) {
              Oficina oficina = new Oficina();
              oficina.setComentarios(ofNacional.getComentarios());
              oficina.setDepartamento(ofNacional.getDepartamento());
              oficina.setDireccion(ofNacional.getDireccion());
              oficina.setHorarios(ofNacional.getHorario());
              oficina.setId(tramiteEmpresaId+"-"+(cont++)); //Las oficinas no tienen id, se genera uno
              oficina.setLocalidad(ofNacional.getLocalidad());
              oficina.setNombre(ofNacional.getDireccion()); //Las oficinas no tienen nombre, se usa la dirección
              oficina.setTelefonos(ofNacional.getTelefonos());
              oficina.setTramite(tramite);
              oficinas.add(oficina);
            }
          }
        }
        
        logger.info("Se obtuvieron "+oficinas.size()+" oficinas.");
        
        return oficinas;
      } catch (Exception ex) {
        logger.warn("No se pudo obtener los oficinas de TrámitesUy: ", ex);
        return null;
      }
  }

  
  private String recortarString(String str, int max) {
    if(str == null) {
      return null;
    }
    if(str.length()<=max) {
      return str;
    }
    return str.substring(0,  max);
    
  }


}
