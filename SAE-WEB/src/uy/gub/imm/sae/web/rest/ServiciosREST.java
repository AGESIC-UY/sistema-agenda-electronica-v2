package uy.gub.imm.sae.web.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.business.utilidades.Metavariables;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.factories.BusinessLocator;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionPorCampoException;
import uy.gub.imm.sae.login.Utilidades;

@Path("/consultas")
public class ServiciosREST {

  static Logger logger = Logger.getLogger(ServiciosREST.class);
  
	private final static SimpleDateFormat FORMATOFECHA_CONHORA = new SimpleDateFormat("yyyyMMdd HH:mm"); 	
  private final static SimpleDateFormat FORMATOFECHA_SINHORA = new SimpleDateFormat("yyyyMMdd");   
  private final static SimpleDateFormat FORMATOFECHA_HORA = new SimpleDateFormat("HHmm");  
	
  @Context
  private HttpServletRequest httpRequest;

  private String jError(String mensaje) {
    JsonObject jError = new JsonObject();
    jError.addProperty("resultado", "0");
    jError.addProperty("error", mensaje);
    return jError.toString();
  }
  
  // ======================================================================================================================================
  // ======================================================================================================================================
  
	@POST
  @Path("/reservas-por-agenda-y-documento")
  @Consumes("application/json")
  @Produces("application/json")
	public String getReservasDocumentoPost(ReservasPorDocumentoInput input) {
	  return getReservasDocumento(input);
	}
	
  @GET
  @Path("/reservas-por-agenda-y-documento")
  @Produces("application/json")
  public String getReservasDocumentoGet(@QueryParam("token") String token, @QueryParam("idAgenda") Integer idAgenda, @QueryParam("idRecurso") Integer idRecurso, 
      @QueryParam("tipoDocumento") String tipoDocumento, @QueryParam("numeroDocumento") String numeroDocumento, @QueryParam("codigoTramite") String codigoTramite, 
      @QueryParam("fechaDesde") String fechaDesde, @QueryParam("fechaHasta") String fechaHasta) {
    ReservasPorDocumentoInput input = new ReservasPorDocumentoInput();
    input.setCodigoTramite(codigoTramite);
    input.setFechaDesde(fechaDesde);
    input.setFechaHasta(fechaHasta);
    input.setIdAgenda(idAgenda);
    input.setIdRecurso(idRecurso);
    input.setNumeroDocumento(numeroDocumento);
    input.setTipoDocumento(tipoDocumento);
    input.setToken(token);
    return getReservasDocumento(input);
  }
	
	private String getReservasDocumento(ReservasPorDocumentoInput input) {
		try {
			BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
			Consultas consultas = bl.getConsultas();
			List<Map<String, Object>> reservas = consultas.consultarReservasPorTokenYDocumento(input.getToken(), input.getIdAgenda(), 
					input.getIdRecurso(), input.getTipoDocumento(), input.getNumeroDocumento(), input.getCodigoTramite());
			JsonArray jReservas = new JsonArray();
			for(Map<String, Object> reserva : reservas) {
        JsonObject jReserva = new JsonObject();
        jReserva.addProperty("fecha_reserva", FORMATOFECHA_CONHORA.format((Date) reserva.get("fechaHora")));
        jReserva.addProperty("nombre_recurso", (String) reserva.get("nombreRecurso"));
        jReserva.addProperty("codigo_cancelacion", (String) reserva.get("codigoCancelacion"));
        jReservas.add(jReserva);
			}
			return jReservas.toString();
		}catch(UserException uEx) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", uEx.getCodigoError());
      return jError.toString();
    }catch(Exception ex) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", ex.getMessage());
      return jError.toString();
		}
  }	
	
	// ======================================================================================================================================
  // ======================================================================================================================================
	
  @POST
  @Path("/reservas-por-agenda-y-documento-full")
  @Consumes("application/json")
  @Produces("application/json")
  public String getReservasDocumentoFullPost(ReservasPorDocumentoInput input) {
    return getReservasDocumentoFull(input);
  }
  
  @GET
  @Path("/reservas-por-agenda-y-documento-full")
  @Produces("application/json")
  public String getReservasDocumentoFullGet(@QueryParam("token") String token, @QueryParam("idAgenda") Integer idAgenda, @QueryParam("idRecurso") Integer idRecurso, 
      @QueryParam("tipoDocumento") String tipoDocumento, @QueryParam("numeroDocumento") String numeroDocumento, @QueryParam("codigoTramite") String codigoTramite, 
      @QueryParam("fechaDesde") String fechaDesde, @QueryParam("fechaHasta") String fechaHasta) {
    ReservasPorDocumentoInput input = new ReservasPorDocumentoInput();
    input.setCodigoTramite(codigoTramite);
    input.setFechaDesde(fechaDesde);
    input.setFechaHasta(fechaHasta);
    input.setIdAgenda(idAgenda);
    input.setIdRecurso(idRecurso);
    input.setNumeroDocumento(numeroDocumento);
    input.setTipoDocumento(tipoDocumento);
    input.setToken(token);
    return getReservasDocumentoFull(input);
  }
  
  private String getReservasDocumentoFull(ReservasPorDocumentoInput input) {
    try {
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      Date fechaDesde = null;
      Date fechaHasta = null;
      if(input.getFechaDesde()!=null && !input.getFechaDesde().trim().isEmpty()) {
        try {
          fechaDesde = FORMATOFECHA_SINHORA.parse(input.getFechaDesde());
        }catch(Exception ex) {
          fechaDesde = new Date();
        }
      }
      if(input.getFechaHasta()!=null && !input.getFechaHasta().trim().isEmpty()) {
        try {
          fechaHasta = FORMATOFECHA_SINHORA.parse(input.getFechaHasta());
        }catch(Exception ex) {
          fechaHasta = new Date();
        }
      }
      List<Map<String, Object>> reservas0 = consultas.consultarReservasPorTokenYAgendaTramiteDocumento(input.getToken(), input.getIdAgenda(), 
          input.getIdRecurso(), input.getTipoDocumento(), input.getNumeroDocumento(), input.getCodigoTramite(), fechaDesde, fechaHasta);
      JsonArray reservas1 = new JsonArray();
      JsonObject reserva1 = null;
      for(Map<String, Object> reserva0 : reservas0) {
        reserva1 = new JsonObject();
        reserva1.addProperty("reservaId", reserva0.get("reservaId")!=null?reserva0.get("reservaId").toString():"");
        String estado = (String)reserva0.get("estado");
        if(estado == null) {
          reserva1.addProperty("estado", "");
        }else {
          try {
            reserva1.addProperty("estado", Estado.valueOf(estado).getDescripcion());
          }catch(Exception ex) {
            reserva1.addProperty("estado", estado);
            ex.printStackTrace();
          }
        }
        reserva1.addProperty("serie", reserva0.get("serie")!=null?reserva0.get("serie").toString():"");
        reserva1.addProperty("numero", reserva0.get("numero")!=null?reserva0.get("numero").toString():"");
        reserva1.addProperty("codigoCancelacion", reserva0.get("codigoCancelacion")!=null?reserva0.get("codigoCancelacion").toString():"");
        reserva1.addProperty("codigoTrazabilidad", reserva0.get("codigoTrazabilidad")!=null?reserva0.get("codigoTrazabilidad").toString():"");
        reserva1.addProperty("codigoTramite", reserva0.get("codigoTramite")!=null?reserva0.get("codigoTramite").toString():"");
        reserva1.addProperty("nombreTramite", reserva0.get("nombreTramite")!=null?reserva0.get("nombreTramite").toString():"");
        reserva1.addProperty("fechaHora", reserva0.get("fechaHora")!=null?FORMATOFECHA_CONHORA.format(reserva0.get("fechaHora")):"");
        reserva1.addProperty("presencial", reserva0.get("reservaId")!=null?reserva0.get("reservaId").toString().equalsIgnoreCase("true")?"si":"no":"");
        reserva1.addProperty("recurso", reserva0.get("recurso")!=null?reserva0.get("recurso").toString():"");
        reserva1.addProperty("agenda", reserva0.get("agenda")!=null?reserva0.get("agenda").toString():"");
        reserva1.addProperty("funcionario", reserva0.get("funcionario")!=null?reserva0.get("funcionario").toString():"");
        reserva1.addProperty("asistio", reserva0.get("asistio")!=null?reserva0.get("asistio").toString().equalsIgnoreCase("true")?"si":"no":"");
        JsonObject datos1 = new JsonObject();
        if(reserva0.get("datos") != null) {
          @SuppressWarnings("unchecked")
          Map<String,String> datos = (Map<String, String>) reserva0.get("datos");
          for(String dato : datos.keySet()) {
            datos1.addProperty(dato, datos.get(dato));
          }
        }
        reserva1.add("datos", datos1);
        reservas1.add(reserva1);
      }
      JsonObject jReservas = new JsonObject();
      jReservas.add("reservas", reservas1);
      jReservas.addProperty("cantidad", reservas1.size());
      return jReservas.toString();
    }catch(UserException uEx) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", uEx.getCodigoError());
      return jError.toString();
    }catch(Exception ex) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", ex.getMessage());
      return jError.toString();
    }
  } 

  // ======================================================================================================================================
  // ======================================================================================================================================
  
  @POST
  @Path("/reservas-vigentes-por-agenda-y-documento")
  @Consumes("application/json")
  @Produces("application/json")
  public String getReservasVigentesDocumentoPost(ReservasPorDocumentoInput input) {
    return getReservasVigentesDocumento(input);
  }
  
  @GET
  @Path("/reservas-vigentes-por-agenda-y-documento")
  @Produces("application/json")
  public String getReservasVigentesDocumentoGet(@QueryParam("token") String token, @QueryParam("idAgenda") Integer idAgenda, @QueryParam("idRecurso") Integer idRecurso, 
      @QueryParam("tipoDocumento") String tipoDocumento, @QueryParam("numeroDocumento") String numeroDocumento, @QueryParam("codigoTramite") String codigoTramite) {
    ReservasPorDocumentoInput input = new ReservasPorDocumentoInput();
    input.setCodigoTramite(codigoTramite);
    input.setIdAgenda(idAgenda);
    input.setIdRecurso(idRecurso);
    input.setNumeroDocumento(numeroDocumento);
    input.setTipoDocumento(tipoDocumento);
    input.setToken(token);
    return getReservasVigentesDocumento(input);
  }
  
  private String getReservasVigentesDocumento(ReservasPorDocumentoInput input) {
    try {
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      List<Map<String, Object>> reservas0 = consultas.consultarReservasVigentesPorTokenYAgendaTramiteDocumento(input.getToken(), input.getIdAgenda(), 
          input.getIdRecurso(), input.getTipoDocumento(), input.getNumeroDocumento(), input.getCodigoTramite());
      JsonArray reservas1 = new JsonArray();
      JsonObject reserva1 = null;
      for(Map<String, Object> reserva0 : reservas0) {
        reserva1 = new JsonObject();
        reserva1.addProperty("reservaId", reserva0.get("reservaId")!=null?reserva0.get("reservaId").toString():"");
        String estado = (String)reserva0.get("estado");
        if(estado == null) {
          reserva1.addProperty("estado", "");
        }else {
          try {
            reserva1.addProperty("estado", Estado.valueOf(estado).getDescripcion());
          }catch(Exception ex) {
            reserva1.addProperty("estado", estado);
            ex.printStackTrace();
          }
        }
        reserva1.addProperty("serie", reserva0.get("serie")!=null?reserva0.get("serie").toString():"");
        reserva1.addProperty("numero", reserva0.get("numero")!=null?reserva0.get("numero").toString():"");
        reserva1.addProperty("codigoCancelacion", reserva0.get("codigoCancelacion")!=null?reserva0.get("codigoCancelacion").toString():"");
        reserva1.addProperty("codigoTrazabilidad", reserva0.get("codigoTrazabilidad")!=null?reserva0.get("codigoTrazabilidad").toString():"");
        reserva1.addProperty("codigoTramite", reserva0.get("codigoTramite")!=null?reserva0.get("codigoTramite").toString():"");
        reserva1.addProperty("nombreTramite", reserva0.get("nombreTramite")!=null?reserva0.get("nombreTramite").toString():"");
        reserva1.addProperty("fechaHora", reserva0.get("fechaHora")!=null?FORMATOFECHA_CONHORA.format(reserva0.get("fechaHora")):"");
        reserva1.addProperty("presencial", reserva0.get("reservaId")!=null?reserva0.get("reservaId").toString().equalsIgnoreCase("true")?"si":"no":"");
        reserva1.addProperty("recurso", reserva0.get("recurso")!=null?reserva0.get("recurso").toString():"");
        reserva1.addProperty("agenda", reserva0.get("agenda")!=null?reserva0.get("agenda").toString():"");
        reserva1.addProperty("funcionario", reserva0.get("funcionario")!=null?reserva0.get("funcionario").toString():"");
        reserva1.addProperty("asistio", reserva0.get("asistio")!=null?reserva0.get("asistio").toString().equalsIgnoreCase("true")?"si":"no":"");
        JsonObject datos1 = new JsonObject();
        if(reserva0.get("datos") != null) {
          @SuppressWarnings("unchecked")
          Map<String,String> datos = (Map<String, String>) reserva0.get("datos");
          for(String dato : datos.keySet()) {
            datos1.addProperty(dato, datos.get(dato));
          }
        }
        reserva1.add("datos", datos1);
        reservas1.add(reserva1);
      }
      JsonObject jReservas = new JsonObject();
      jReservas.add("reservas", reservas1);
      jReservas.addProperty("cantidad", reservas1.size());
      return jReservas.toString();
    }catch(UserException uEx) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", uEx.getCodigoError());
      return jError.toString();
    }catch(Exception ex) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", ex.getMessage());
      return jError.toString();
    }
  } 

  // ======================================================================================================================================
  // ======================================================================================================================================
  
  @POST
  @Path("/recursos_por_agenda")
  @Consumes("application/json")
  @Produces("application/json")
  public String getRecursosPorAgendaPost(RecursosPorAgendaInput input) {
    return getRecursosPorAgenda(input);
  }

  @GET
  @Path("/recursos_por_agenda")
  @Produces("application/json")
  public String getRecursosPorAgendaGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idioma") String idioma) {
    RecursosPorAgendaInput input = new RecursosPorAgendaInput();
    input.setIdAgenda(idAgenda);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setToken(token);
    return getRecursosPorAgenda(input);
  }

  private String getRecursosPorAgenda(RecursosPorAgendaInput input) {
    try {
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.addProperty("mensaje", "no_se_encuentra_el_token_especificado");
        return jError.toString();
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      //Obtener los recursos
      Map<String, Object> agenda = consultas.consultarRecursosPorAgenda(input.getIdEmpresa(), input.getIdAgenda(), input.getIdioma());
      //Transformar el resultado a un objeto Json para devolver
      JsonObject jAgenda = new JsonObject();
      jAgenda.addProperty("textoRecurso", (String)agenda.get("textoRecurso"));
      jAgenda.addProperty("textoPaso1", (String)agenda.get("textoPaso1"));
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> recursos = (List<Map<String, Object>>)agenda.get("recursos");
      JsonArray jRecursos = new JsonArray();
      for(Map<String, Object> recurso : recursos) {
        JsonObject jRecurso = new JsonObject();
        jRecurso.addProperty("id", (Integer)recurso.get("id"));
        jRecurso.addProperty("nombre", (String)recurso.get("nombre"));
        jRecurso.addProperty("direccion", (String)recurso.get("direccion"));
        jRecurso.addProperty("telefono", (String)recurso.get("telefono"));
        jRecurso.addProperty("latitud", (String)recurso.get("latitud"));
        jRecurso.addProperty("longitud", (String)recurso.get("longitud"));
        jRecurso.addProperty("multiple", (String)recurso.get("multiple"));
        jRecurso.addProperty("cambios", (String)recurso.get("cambios"));
        if(recurso.containsKey("cambios_limite")) {
          jRecurso.addProperty("cambios_limite", (String)recurso.get("cambios_limite"));
        }
        jRecursos.add(jRecurso);
      }
      jAgenda.add("recursos", jRecursos);
      //Devolver el resultado en formato string
      return jAgenda.toString();
    }catch(UserException uEx) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", uEx.getCodigoError());
      return jError.toString();
    }catch(Exception ex) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", ex.getMessage());
      return jError.toString();
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  } 
  
  // ======================================================================================================================================
  // ======================================================================================================================================
  
  @POST
  @Path("/disponibilidades_por_recurso")
  @Consumes("application/json")
  @Produces("application/json")
  public String getDisponibilidadesPorRecursoPost(DisponibilidadesPorRecursoInput input) {
    return getDisponibilidadesPorRecurso(input);
  }
  
  @GET
  @Path("/disponibilidades_por_recurso")
  @Produces("application/json")
  public String getDisponibilidadesPorRecursoGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("idioma") String idioma) {
    DisponibilidadesPorRecursoInput input = new DisponibilidadesPorRecursoInput();
    input.setIdAgenda(idAgenda);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setToken(token);    
    return getDisponibilidadesPorRecurso(input);
  }
  
  private String getDisponibilidadesPorRecurso(DisponibilidadesPorRecursoInput input) {
    try {
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.addProperty("error", "no_se_encuentra_el_token_especificado");
        return jError.toString();
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      //Obtener las disponibilidades
      Map<String, Object> resp = consultas.consultarDisponibilidadesPorRecurso(input.getIdEmpresa(),  input.getIdAgenda(), input.getIdRecurso(), 
          input.getIdioma());
      //Transformar el resultado a un objeto JSon para devolver
      JsonObject jDisp = new JsonObject();
      jDisp.addProperty("textoAgendaPaso2", (String)resp.get("textoAgendaPaso2"));
      jDisp.addProperty("textoRecursoPaso2", (String)resp.get("textoRecursoPaso2"));
      @SuppressWarnings("unchecked")
      Map<String, Integer> disps = (Map<String, Integer>)resp.get("disponibilidades");
      List<String> claves = new ArrayList<String>(disps.keySet());
      Collections.sort(claves);
      String fechaActual = null;
      JsonArray jDisps = new JsonArray();
      jDisp.add("disponibilidades", jDisps);
      JsonObject porFecha = null;
      JsonObject porHora = null;
      for(String clave : claves) {
        String[] fechaHoraId = clave.split(" ");
        String fecha = fechaHoraId[0];
        String hora = fechaHoraId[1];
        String id = fechaHoraId[2];
        if(fechaActual==null || !fechaActual.equals(fecha)) {
          porFecha = new JsonObject();
          porHora = new JsonObject();
          porFecha.add(fecha, porHora);
          jDisps.add(porFecha);
          fechaActual = fecha;
        }
        JsonObject dispPorHora = new JsonObject();
        dispPorHora.addProperty("id", id);
        dispPorHora.addProperty("cupo", disps.get(clave));
        porHora.add(hora, dispPorHora);
      }
      //Devolver el resultado en formato string
      return jDisp.toString();
    }catch(UserException uEx) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", uEx.getCodigoError());
      return jError.toString();
    }catch(Exception ex) {
      JsonObject jError = new JsonObject();
      jError.addProperty("error", ex.getMessage());
      return jError.toString();
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }

  // ======================================================================================================================================
  // ======================================================================================================================================
  
  @POST
  @Path("/confirmar_reserva")
  @Consumes("application/json")
  @Produces("application/json")
  public String confirmarReservaPost(ConfirmarReservaInput input) {
    return confirmarReserva(input);
  }
  
  @GET
  @Path("/confirmar_reserva")
  @Produces("application/json")
  public String confirmarReservaGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("idDisponibilidad") Integer idDisponibilidad, @QueryParam("idTransaccionPadre") String idTransaccionPadre, 
      @QueryParam("pasoTransaccionPadre") String pasoTransaccionPadre, @QueryParam("idioma") String idioma, @QueryParam("datosReserva") String datosReserva) {
    ConfirmarReservaInput input = new ConfirmarReservaInput();
    input.setDatosReserva(datosReserva);
    input.setIdAgenda(idAgenda);
    input.setIdDisponibilidad(idDisponibilidad);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setIdTransaccionPadre(idTransaccionPadre);
    input.setPasoTransaccionPadre(pasoTransaccionPadre);
    input.setToken(token);
    return confirmarReserva(input);
  }
  
  private String confirmarReserva(ConfirmarReservaInput input) {
    try {
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.addProperty("error", "no_se_encuentra_el_token_especificado");
        return jError.toString();
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      //Generar y confirmar la reserva
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      Reserva reserva = agendarReservas.generarYConfirmarReserva(input.getIdEmpresa(), input.getIdAgenda(), input.getIdRecurso(), input.getIdDisponibilidad(),
          input.getDatosReserva(), input.getIdTransaccionPadre(), input.getPasoTransaccionPadre(), null, input.getIdioma());
      Agenda agenda = agendarReservas.consultarAgendaPorId(input.getIdAgenda());
      Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
      //Enviar el mail de confirmacion
      String linkBase = httpRequest.getScheme()+"://"+httpRequest.getServerName();
      if("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=80 || "https".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=443) {
        linkBase = linkBase + ":" + httpRequest.getServerPort();
      }
      String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+input.getIdEmpresa()+"&a="+agenda.getId()+"&ri="+reserva.getId();
      String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+input.getIdEmpresa()+"&a="+agenda.getId()+"&r="+recurso.getId()+"&ri="+reserva.getId();
      Map<String, Object> datosEmpresa = consultas.consultarDatosEmpresa(input.getIdEmpresa());
      String formatoFecha = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_FECHA")?(String)datosEmpresa.get("FORMATO_FECHA"):"dd/MM/yyyy";
      String formatoHora = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_HORA")?(String)datosEmpresa.get("FORMATO_HORA"):"HH:mm";
      agendarReservas.enviarComunicacionesConfirmacion(linkCancelacion, linkModificacion, reserva, input.getIdioma(), formatoFecha, formatoHora);
      //Enviar la respuesta
      JsonObject jReserva = new JsonObject();
      jReserva.addProperty("resultado", "1");
      jReserva.addProperty("id", reserva.getId());
      jReserva.addProperty("serieNumero", reserva.getSerie()+"-"+reserva.getNumero());
      jReserva.addProperty("codigoCancelacion", reserva.getCodigoSeguridad());
      jReserva.addProperty("codigoTrazabilidad", reserva.getTrazabilidadGuid());
      
      Disponibilidad disp = reserva.getDisponibilidades().get(0); 
      jReserva.addProperty("fechaHora", FORMATOFECHA_SINHORA.format(disp.getFecha())+FORMATOFECHA_HORA.format(disp.getHoraInicio()));
      String idioma = input.getIdioma();
      String textoTicket = null;
      if(idioma!=null && !idioma.isEmpty() && agenda.getTextosAgenda().containsKey(idioma)) {
        //jReserva.addProperty("textoTicket", agenda.getTextosAgenda().get(idioma).getTextoTicketConf());
        textoTicket = agenda.getTextosAgenda().get(idioma).getTextoTicketConf();
      }else {
        if(!agenda.getTextosAgenda().isEmpty()) {
          //jReserva.addProperty("textoTicket", agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoTicketConf());
          textoTicket = agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoTicketConf();
        }
      }
      if(textoTicket != null) {
        jReserva.addProperty("textoTicket", Metavariables.remplazarMetavariables(textoTicket, reserva, formatoFecha, formatoHora, linkCancelacion, linkModificacion));
      }
      
      return jReserva.toString();
    }catch(UserException uEx) {
      if(uEx instanceof ValidacionClaveUnicaException) {
        //Ya existe una reserva para la clave
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.addProperty("error", "ya_existe_una_reserva_para_el_dia_especificado_con_los_datos_proporcionados");
        return jError.toString();
      }
      if(uEx instanceof ValidacionPorCampoException) {
        //Hay al menos un campo que tiene problemas
        JsonArray jMensajes = new JsonArray();
        ValidacionPorCampoException vpcEx = (ValidacionPorCampoException)uEx;
        for(int i=0; i<vpcEx.getCantCampos(); i++) {
          jMensajes.add(vpcEx.getMensaje(i)+"/"+vpcEx.getNombreCampo(i));
        }
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.add("errores", jMensajes);
        return jError.toString();
      }
      if("el_horario_acaba_de_quedar_sin_cupos".equals(uEx.getCodigoError())) {
        //No hay cupos para la disponibilidad seleccionada
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "2");
        jError.addProperty("error", uEx.getCodigoError());
        return jError.toString();
      }
      //Otro error no manejado específicamente
      uEx.printStackTrace();
      JsonObject jError = new JsonObject();
      jError.addProperty("resultado", "0");
      jError.addProperty("error", uEx.getCodigoError());
      return jError.toString();
    }catch(Exception ex) {
      //Otro error no manejado específicamente
      ex.printStackTrace();
      JsonObject jError = new JsonObject();
      jError.addProperty("resultado", "0");
      jError.addProperty("error", ex.getMessage());
      return jError.toString();
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }

  // ======================================================================================================================================
  // ======================================================================================================================================
  
  @POST
  @Path("/cancelar_reserva")
  @Consumes("application/json")
  @Produces("application/json")
  public String cancelarReservaPost(CancelarReservaInput input) {
    return cancelarReserva(input);
  }
  
  @GET
  @Path("/cancelar_reserva")
  @Produces("application/json")
  public String cancelarReservaGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("idReserva") Integer idReserva, @QueryParam("codigoCancelacion") String codigoCancelacion, 
      @QueryParam("idioma") String idioma) {
    CancelarReservaInput input = new CancelarReservaInput();
    input.setIdAgenda(idAgenda);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setToken(token);
    input.setIdReserva(idReserva);
    input.setCodigoCancelacion(codigoCancelacion);
    return cancelarReserva(input);
  }
  
  private String cancelarReserva(CancelarReservaInput input) {
    try {
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.addProperty("error", "no_se_encuentra_el_token_especificado");
        return jError.toString();
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      //Obtener la reserva
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      Reserva reserva = agendarReservas.consultarReservaPorId(input.getIdReserva());
      if(reserva==null) {
        throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
      }
      //Verificar el código de cancelación
      if(reserva.getCodigoSeguridad()!=null && !reserva.getCodigoSeguridad().equals(input.getCodigoCancelacion())) {
        throw new UserException("no_se_encuentra_la_reserva_o_ya_fue_cancelada");
      }
      //Cancelar la reserva
      Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
      agendarReservas.cancelarReserva(input.getIdEmpresa(), recurso.getAgenda().getId(), recurso.getId(), input.getIdReserva());
      //Enviar el mail de confirmacion
      try {
        Map<String, Object> datosEmpresa = consultas.consultarDatosEmpresa(input.getIdEmpresa());
        String formatoFecha = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_FECHA")?(String)datosEmpresa.get("FORMATO_FECHA"):"dd/MM/yyyy";
        String formatoHora = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_HORA")?(String)datosEmpresa.get("FORMATO_HORA"):"HH:mm";
        agendarReservas.enviarComunicacionesCancelacion(reserva, input.getIdioma(), formatoFecha, formatoHora);
      }catch(Exception ex) {
        //Nada para hacer, no se envía mensaje
      }
      //Enviar la respuesta
      JsonObject jRespuesta = new JsonObject();
      jRespuesta.addProperty("resultado", "1");
      return jRespuesta.toString();
    }catch(UserException uEx) {
      JsonObject jError = new JsonObject();
      jError.addProperty("resultado", "0");
      jError.addProperty("error", uEx.getCodigoError());
      return jError.toString();
    }catch(Exception ex) {
      JsonObject jError = new JsonObject();
      jError.addProperty("resultado", "0");
      jError.addProperty("error", ex.getMessage());
      return jError.toString();
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  } 

  // ======================================================================================================================================
  // ======================================================================================================================================
  
  @POST
  @Path("/reserva_multiple_obtener_token")
  @Consumes("application/json")
  @Produces("application/json")
  public String reservaMultipleObtenerTokenPost(ReservaMultipleObtenerTokenInput input) {
    return reservaMultipleObtenerToken(input);
  }
  
  @GET
  @Path("/reserva_multiple_obtener_token")
  @Produces("application/json")
  public String reservaMultipleObtenerToken(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("documento") String documento, @QueryParam("nombre") String nombre, @QueryParam("email") String email,  
      @QueryParam("codigoTramite") String codigoTramite, @QueryParam("idioma") String idioma) {
    ReservaMultipleObtenerTokenInput input = new ReservaMultipleObtenerTokenInput();
    input.setToken(token);
    input.setIdEmpresa(idEmpresa);
    input.setIdAgenda(idAgenda);
    input.setIdRecurso(idRecurso);
    input.setIdioma(idioma);
    input.setDocumento(documento);
    input.setNombre(nombre);
    input.setEmail(email);
    input.setCodigoTramite(codigoTramite);    
    return reservaMultipleObtenerToken(input);
  }
  
  private String reservaMultipleObtenerToken(ReservaMultipleObtenerTokenInput input) {
    try {
      if(input.getIdEmpresa()==null) {
        return jError("debe_especificar_la_empresa");
      }
      if(input.getIdAgenda()==null) {
        return jError("debe_especificar_la_agenda");
      }
      if(input.getIdRecurso()==null) {
        return jError("debe_especificar_el_recurso");
      }
      if(input.getDocumento()==null || input.getNombre()==null || input.getEmail()==null) {
        return jError("debe_especificar_los_datos_personales");
      }
      if(input.getCodigoTramite()==null) {
        return jError("debe_especificar_el_tramite");
      }
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      //Verificar la agenda
      Agenda agenda = agendarReservas.consultarAgendaPorId(input.getIdAgenda());
      if(agenda == null) {
        return jError("no_se_encuentra_la_agenda_especificada");
      }
      //Verificar el recurso
      Recurso recurso = agendarReservas.consultarRecursoPorId(agenda, input.getIdRecurso());
      if(recurso == null) {
        return jError("no_se_encuentra_el_recurso_especificado");
      }
      //Verificar que el recurso admita reservas múltiples
      if(recurso.getMultipleAdmite()==null || !recurso.getMultipleAdmite()) {
        return jError("el_recurso_no_admite_reservas_multiples");
      }
      //Generar un token de reserva
      TokenReserva tokenReserva = agendarReservas.generarTokenReserva(input.getIdRecurso(), input.getDocumento(), input.getNombre(), input.getEmail(), 
          input.getCodigoTramite(), null);
      //Enviar la respuesta
      JsonObject jRespuesta = new JsonObject();
      jRespuesta.addProperty("resultado", "1");
      jRespuesta.addProperty("token_reservas", tokenReserva.getToken());
      return jRespuesta.toString();
    }catch(UserException uEx) {
      return jError(uEx.getCodigoError());
    }catch(Exception ex) {
      return jError(ex.getMessage());
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  } 

  // ======================================================================================================================================
  // ======================================================================================================================================

  @POST
  @Path("/reserva_multiple_anadir_reserva")
  @Consumes("application/json")
  @Produces("application/json")
  public String reservaMultipleAnadirReservaPost(ReservaMultipleAnadirReservaInput input) {
    return reservaMultipleAnadirReserva(input);
  }
  
  @GET
  @Path("/reserva_multiple_anadir_reserva")
  @Produces("application/json")
  public String reservaMultipleAnadirReservaGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("idDisponibilidad") Integer idDisponibilidad, @QueryParam("idioma") String idioma, 
      @QueryParam("datosReserva") String datosReserva, @QueryParam("tokenReserva") String tokenReserva) {
    ReservaMultipleAnadirReservaInput input = new ReservaMultipleAnadirReservaInput();
    input.setDatosReserva(datosReserva);
    input.setIdAgenda(idAgenda);
    input.setIdDisponibilidad(idDisponibilidad);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setToken(token);
    input.setTokenReserva(tokenReserva);
    return reservaMultipleAnadirReserva(input);
  }
  
  private String reservaMultipleAnadirReserva(ReservaMultipleAnadirReservaInput input) {
    try {
      if(input.getIdEmpresa()==null) {
        return jError("debe_especificar_la_empresa");
      }
      if(input.getIdAgenda()==null) {
        return jError("debe_especificar_la_agenda");
      }
      if(input.getIdRecurso()==null) {
        return jError("debe_especificar_el_recurso");
      }
      if(input.getIdDisponibilidad()==null) {
        return jError("debe_especificar_la_disponibilidad");
      }
      if(input.getTokenReserva()==null || input.getTokenReserva().trim().isEmpty()) {
        return jError("debe_especificar_el_token_de_reservas");
      }
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      //Verificar el token de reserva
      if(input.getTokenReserva() == null || input.getTokenReserva().trim().isEmpty()) {
        return jError("debe_especificar_el_token_de_reservas");
      }
      TokenReserva tokenReserva = agendarReservas.obtenerTokenReserva(input.getTokenReserva());
      if(tokenReserva == null || tokenReserva.getRecurso()==null || !tokenReserva.getRecurso().getId().equals(input.getIdRecurso().intValue())) {
        return jError("no_se_encuentra_el_token_de_reservas_especificado");
      }
      if(tokenReserva.getEstado() == Estado.R) {
        return jError("el_token_esta_confirmado");
      }
      if(tokenReserva.getEstado() == Estado.C) {
        return jError("el_token_esta_cancelado");
      }
      Long tiempoMaximo;
      String sTiempoMaximo = consultas.consultarConfiguracion("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO");
      try {
        tiempoMaximo = Long.valueOf(sTiempoMaximo);
      }catch(Exception ex) {
        tiempoMaximo = 10L;
      }
      Date fechaToken = tokenReserva.getUltimaReserva()!=null?tokenReserva.getUltimaReserva():tokenReserva.getFechaInicio();
      long diferenciaMinutos = (((new Date()).getTime() - fechaToken.getTime()) / 60000);
      if(diferenciaMinutos > tiempoMaximo) {
        return jError("el_token_ha_expirado");
      }
      //Generar y confirmar la reserva
      Reserva reserva = agendarReservas.generarYConfirmarReserva(input.getIdEmpresa(), input.getIdAgenda(), input.getIdRecurso(), input.getIdDisponibilidad(),
          input.getDatosReserva(), null, null, tokenReserva, input.getIdioma());
      //Enviar la respuesta
      JsonObject jReserva = new JsonObject();
      jReserva.addProperty("resultado", "1");
      jReserva.addProperty("id", reserva.getId());
      jReserva.addProperty("serieNumero", reserva.getSerie()+"-"+reserva.getNumero());
      jReserva.addProperty("codigoCancelacion", reserva.getCodigoSeguridad());
      jReserva.addProperty("codigoTrazabilidad", reserva.getTrazabilidadGuid());
      Disponibilidad disp = reserva.getDisponibilidades().get(0); 
      jReserva.addProperty("fechaHora", FORMATOFECHA_SINHORA.format(disp.getFecha())+FORMATOFECHA_HORA.format(disp.getHoraInicio()));
      return jReserva.toString();
    }catch(UserException uEx) {
      if(uEx instanceof ValidacionClaveUnicaException) {
        //Ya existe una reserva para la clave
        return jError("ya_existe_una_reserva_para_el_dia_especificado_con_los_datos_proporcionados");
      }
      if(uEx instanceof ValidacionPorCampoException) {
        //Hay al menos un campo que tiene problemas
        JsonArray jMensajes = new JsonArray();
        ValidacionPorCampoException vpcEx = (ValidacionPorCampoException)uEx;
        for(int i=0; i<vpcEx.getCantCampos(); i++) {
          jMensajes.add(vpcEx.getMensaje(i)+"/"+vpcEx.getNombreCampo(i));
        }
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.add("errores", jMensajes);
        return jError.toString();
      }
      if("el_horario_acaba_de_quedar_sin_cupos".equals(uEx.getCodigoError())) {
        //No hay cupos para la disponibilidad seleccionada
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "2");
        jError.addProperty("error", uEx.getCodigoError());
        return jError.toString();
      }
      //Otro error no manejado específicamente
      return jError(uEx.getCodigoError());
    }catch(Exception ex) {
      //Otro error no manejado específicamente
      ex.printStackTrace();
      return jError(ex.getMessage());
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }

  // ======================================================================================================================================
  // ======================================================================================================================================

  @POST
  @Path("/reserva_multiple_eliminar_reserva")
  @Consumes("application/json")
  @Produces("application/json")
  public String reservaMultipleEliminarReservaPost(ReservaMultipleEliminarReservaInput input) {
    return reservaMultipleEliminarReserva(input);
  }
  
  @GET
  @Path("/reserva_multiple_eliminar_reserva")
  @Produces("application/json")
  public String reservaMultipleEliminarReservaGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("idReserva") Integer idReserva, @QueryParam("tokenReserva") String tokenReserva, @QueryParam("idioma") String idioma) {
    ReservaMultipleEliminarReservaInput input = new ReservaMultipleEliminarReservaInput();
    input.setIdAgenda(idAgenda);
    input.setIdReserva(idReserva);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setToken(token);
    input.setTokenReserva(tokenReserva);
    return reservaMultipleEliminarReserva(input);
  }
  
  private String reservaMultipleEliminarReserva(ReservaMultipleEliminarReservaInput input) {
    try {
      if(input.getIdEmpresa()==null) {
        return jError("debe_especificar_la_empresa");
      }
      if(input.getIdAgenda()==null) {
        return jError("debe_especificar_la_agenda");
      }
      if(input.getIdRecurso()==null) {
        return jError("debe_especificar_el_recurso");
      }
      if(input.getIdReserva()==null) {
        return jError("debe_especificar_la_reserva");
      }
      if(input.getTokenReserva()==null || input.getTokenReserva().trim().isEmpty()) {
        return jError("debe_especificar_el_token_de_reservas");
      }
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      //Validar el token de reservas
      TokenReserva tokenReserva = agendarReservas.obtenerTokenReserva(input.getTokenReserva());
      if(tokenReserva == null || tokenReserva.getRecurso()==null || tokenReserva.getRecurso().getId().intValue() != input.getIdRecurso().intValue()) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      if(tokenReserva.getEstado() == Estado.R) {
        return jError("el_token_esta_confirmado");
      }
      if(tokenReserva.getEstado() == Estado.C) {
        return jError("el_token_esta_cancelado");
      }
      Long tiempoMaximo;
      String sTiempoMaximo = consultas.consultarConfiguracion("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO");
      try {
        tiempoMaximo = Long.valueOf(sTiempoMaximo);
      }catch(Exception ex) {
        tiempoMaximo = 10L;
      }
      Date fechaToken = tokenReserva.getUltimaReserva()!=null?tokenReserva.getUltimaReserva():tokenReserva.getFechaInicio();
      long diferenciaMinutos = (((new Date()).getTime() - fechaToken.getTime()) / 60000);
      if(diferenciaMinutos > tiempoMaximo) {
        return jError("el_token_ha_expirado");
      }
      //Eliminar la reserva
      agendarReservas.cancelarReservaMultiple(tokenReserva.getId(), input.getIdReserva());
      //Enviar la respuesta
      JsonObject jReserva = new JsonObject();
      jReserva.addProperty("resultado", "1");
      return jReserva.toString();
    }catch(UserException uEx) {
      return jError(uEx.getCodigoError());
    }catch(Exception ex) {
      //Otro error no manejado específicamente
      ex.printStackTrace();
      return jError(ex.getMessage());
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }
  // ======================================================================================================================================
  // ======================================================================================================================================

  @POST
  @Path("/reserva_multiple_confirmar_reservas")
  @Consumes("application/json")
  @Produces("application/json")
  public String reservaMultipleConfirmarReservasPost(ReservaMultipleConfirmarReservasInput input) {
    return reservaMultipleConfirmarReservas(input);
  }
  
  @GET
  @Path("/reserva_multiple_confirmar_reservas")
  @Produces("application/json")
  public String reservaMultipleConfirmarReservasGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("tokenReserva") String tokenReserva, 
      @QueryParam("idTransaccionPadre") String idTransaccionPadre, @QueryParam("pasoTransaccionPadre") Long pasoTransaccionPadre, 
      @QueryParam("idioma") String idioma) {
    ReservaMultipleConfirmarReservasInput input = new ReservaMultipleConfirmarReservasInput();
    input.setIdAgenda(idAgenda);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setToken(token);
    input.setTokenReserva(tokenReserva);
    input.setIdTransaccionPadre(idTransaccionPadre);
    input.setPasoTransaccionPadre(pasoTransaccionPadre);
    return reservaMultipleConfirmarReservas(input);
  }
  
  private String reservaMultipleConfirmarReservas(ReservaMultipleConfirmarReservasInput input) {
    try {
      if(input.getIdEmpresa()==null) {
        return jError("debe_especificar_la_empresa");
      }
      if(input.getIdAgenda()==null) {
        return jError("debe_especificar_la_agenda");
      }
      if(input.getIdRecurso()==null) {
        return jError("debe_especificar_el_recurso");
      }
      if(input.getTokenReserva()==null || input.getTokenReserva().trim().isEmpty()) {
        return jError("debe_especificar_el_token_de_reservas");
      }
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      //Validar el token de reservas
      TokenReserva tokenReserva = agendarReservas.obtenerTokenReserva(input.getTokenReserva());
      if(tokenReserva == null || tokenReserva.getRecurso()==null || !tokenReserva.getRecurso().getId().equals(input.getIdRecurso())) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      if(tokenReserva.getEstado() == Estado.R) {
        return jError("el_token_esta_confirmado");
      }
      if(tokenReserva.getEstado() == Estado.C) {
        return jError("el_token_esta_cancelado");
      }
      Long tiempoMaximo;
      String sTiempoMaximo = consultas.consultarConfiguracion("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO");
      try {
        tiempoMaximo = Long.valueOf(sTiempoMaximo);
      }catch(Exception ex) {
        tiempoMaximo = 10L;
      }
      Date fechaToken = tokenReserva.getUltimaReserva()!=null?tokenReserva.getUltimaReserva():tokenReserva.getFechaInicio();
      long diferenciaMinutos = (((new Date()).getTime() - fechaToken.getTime()) / 60000);
      if(diferenciaMinutos > tiempoMaximo) {
        return jError("el_token_ha_expirado");
      }
      //Confirmar todas las reservas pendientes
      tokenReserva = agendarReservas.confirmarReservasMultiples(input.getIdEmpresa(), tokenReserva.getId(), input.getIdTransaccionPadre(), input.getPasoTransaccionPadre(), false);
      //Armar el texto del ticket
      String textoTicket = null;
      Agenda agenda = tokenReserva.getRecurso().getAgenda();
      if(input.getIdioma()!=null && !input.getIdioma().isEmpty() && agenda.getTextosAgenda().containsKey(input.getIdioma())) {
        textoTicket = agenda.getTextosAgenda().get(input.getIdioma()).getTextoTicketConf();
      }else {
        if(!agenda.getTextosAgenda().isEmpty()) {
          textoTicket = agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoTicketConf();
        }
      }
      //Armar el texto del link de cancelación (template)
      String linkBase = httpRequest.getScheme()+"://"+httpRequest.getServerName();
      if("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=80 || "https".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=443) {
        linkBase = linkBase + ":" + httpRequest.getServerPort();
      }
      String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+input.getIdEmpresa()+"&a="+agenda.getId()+"&ri={idReserva}";
      String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+input.getIdEmpresa()+"&a="+input.getIdAgenda()+"&r="+input.getIdRecurso()+"&ri={idReserva}";
      
      Map<String, Object> datosEmpresa = consultas.consultarDatosEmpresa(input.getIdEmpresa());
      String formatoFecha = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_FECHA")?(String)datosEmpresa.get("FORMATO_FECHA"):"dd/MM/yyyy";
      String formatoHora = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_HORA")?(String)datosEmpresa.get("FORMATO_HORA"):"HH:mm";
      //Enviar las comunicaciones
      agendarReservas.enviarComunicacionesConfirmacion(linkCancelacion, linkModificacion, tokenReserva, input.getIdioma(), formatoFecha, formatoHora);
      //Armar la respuesta
      JsonArray jReservas = new JsonArray();
      List<Reserva> reservas = agendarReservas.obtenerReservasMultiples(tokenReserva.getId(), false);
      for(Reserva reserva : reservas) {
        if(reserva.getEstado().equals(Estado.R)) {
          JsonObject jReserva = new JsonObject();
          jReserva.addProperty("id", reserva.getId());
          jReserva.addProperty("serieNumero", reserva.getSerie()+"-"+reserva.getNumero());
          jReserva.addProperty("codigoCancelacion", reserva.getCodigoSeguridad());
          jReserva.addProperty("codigoTrazabilidad", reserva.getTrazabilidadGuid());
          String linkCancelacionReserva = linkCancelacion.replace("{idReserva}", reserva.getId().toString());
          String linkModificacionReserva = linkModificacion.replace("{idReserva}", reserva.getId().toString());
          jReserva.addProperty("linkCancelacion", linkCancelacionReserva);
          Disponibilidad disp = reserva.getDisponibilidades().get(0); 
          jReserva.addProperty("fechaHora", FORMATOFECHA_SINHORA.format(disp.getFecha())+FORMATOFECHA_HORA.format(disp.getHoraInicio()));
          jReservas.add(jReserva);
          if(textoTicket != null) {
            jReserva.addProperty("textoTicket", Metavariables.remplazarMetavariables(textoTicket, reserva, formatoFecha, formatoHora, linkCancelacionReserva, linkModificacionReserva));
          }
        }
      }
      JsonObject jResultado = new JsonObject();
      jResultado.addProperty("resultado", "1");
      jResultado.add("reservas", jReservas);
      return jResultado.toString();
    }catch(UserException uEx) {
      return jError(uEx.getCodigoError());
    }catch(Exception ex) {
      //Otro error no manejado específicamente
      ex.printStackTrace();
      return jError(ex.getMessage());
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }

  // ======================================================================================================================================
  // ======================================================================================================================================

  @POST
  @Path("/reserva_multiple_cancelar_reservas")
  @Consumes("application/json")
  @Produces("application/json")
  public String reservaMultipleCancelarReservasPost(ReservaMultipleCancelarReservasInput input) {
    return reservaMultipleCancelarReservas(input);
  }
  
  @GET
  @Path("/reserva_multiple_cancelar_reservas")
  @Produces("application/json")
  public String reservaMultipleCancelarReservasGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("tokenReserva") String tokenReserva, @QueryParam("idioma") String idioma) {
    ReservaMultipleCancelarReservasInput input = new ReservaMultipleCancelarReservasInput();
    input.setIdAgenda(idAgenda);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setToken(token);
    input.setTokenReserva(tokenReserva);
    return reservaMultipleCancelarReservas(input);
  }
  
  private String reservaMultipleCancelarReservas(ReservaMultipleCancelarReservasInput input) {
    try {
      if(input.getIdEmpresa()==null) {
        return jError("debe_especificar_la_empresa");
      }
      if(input.getIdAgenda()==null) {
        return jError("debe_especificar_la_agenda");
      }
      if(input.getIdRecurso()==null) {
        return jError("debe_especificar_el_recurso");
      }
      if(input.getTokenReserva()==null || input.getTokenReserva().trim().isEmpty()) {
        return jError("debe_especificar_el_token_de_reservas");
      }
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      //Validar el token de reservas
      TokenReserva tokenReserva = agendarReservas.obtenerTokenReserva(input.getTokenReserva());
      if(tokenReserva == null || tokenReserva.getRecurso()==null || !tokenReserva.getRecurso().getId().equals(input.getIdRecurso())) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      if(tokenReserva.getEstado() == Estado.R) {
        return jError("el_token_esta_confirmado");
      }
      if(tokenReserva.getEstado() == Estado.C) {
        return jError("el_token_esta_cancelado");
      }
      Long tiempoMaximo;
      String sTiempoMaximo = consultas.consultarConfiguracion("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO");
      try {
        tiempoMaximo = Long.valueOf(sTiempoMaximo);
      }catch(Exception ex) {
        tiempoMaximo = 10L;
      }
      Date fechaToken = tokenReserva.getUltimaReserva()!=null?tokenReserva.getUltimaReserva():tokenReserva.getFechaInicio();
      long diferenciaMinutos = (((new Date()).getTime() - fechaToken.getTime()) / 60000);
      if(diferenciaMinutos > tiempoMaximo) {
        return jError("el_token_ha_expirado");
      }
      //Cancelar todas las reservas pendientes
      tokenReserva = agendarReservas.cancelarReservasMultiples(tokenReserva.getId());
      JsonObject jResultado = new JsonObject();
      jResultado.addProperty("resultado", "1");
      return jResultado.toString();
    }catch(UserException uEx) {
      return jError(uEx.getCodigoError());
    }catch(Exception ex) {
      //Otro error no manejado específicamente
      ex.printStackTrace();
      return jError(ex.getMessage());
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }

  // ======================================================================================================================================
  // ======================================================================================================================================

  @POST
  @Path("/reserva_multiple_validar_token")
  @Consumes("application/json")
  @Produces("application/json")
  public String reservaMultipleValidarTokenPost(ReservaMultipleValidarTokenInput input) {
    return reservaMultipleValidarToken(input);
  }
  
  @GET
  @Path("/reserva_multiple_validar_token")
  @Produces("application/json")
  public String reservaMultipleValidarTokenGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("tokenReserva") String tokenReserva, @QueryParam("idioma") String idioma) {
    ReservaMultipleValidarTokenInput input = new ReservaMultipleValidarTokenInput();
    input.setIdAgenda(idAgenda);
    input.setIdEmpresa(idEmpresa);
    input.setIdioma(idioma);
    input.setIdRecurso(idRecurso);
    input.setToken(token);
    input.setTokenReserva(tokenReserva);
    return reservaMultipleValidarToken(input);
  }
  
  private String reservaMultipleValidarToken(ReservaMultipleValidarTokenInput input) {
    try {
      if(input.getIdEmpresa()==null) {
        return jError("debe_especificar_la_empresa");
      }
      if(input.getIdAgenda()==null) {
        return jError("debe_especificar_la_agenda");
      }
      if(input.getIdRecurso()==null) {
        return jError("debe_especificar_el_recurso");
      }
      if(input.getTokenReserva()==null || input.getTokenReserva().trim().isEmpty()) {
        return jError("debe_especificar_el_token_de_reservas");
      }
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      //Validar el token de reservas
      TokenReserva tokenReserva = agendarReservas.obtenerTokenReserva(input.getTokenReserva());
      if(tokenReserva == null || tokenReserva.getRecurso()==null || !tokenReserva.getRecurso().getId().equals(input.getIdRecurso())) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      Date fechaToken = tokenReserva.getUltimaReserva()!=null?tokenReserva.getUltimaReserva():tokenReserva.getFechaInicio();
      String estado = null;
      switch (tokenReserva.getEstado()) {
        case C:
          estado = "cancelado";
          break;
        case P:
          Long tiempoMaximo;
          String sTiempoMaximo = consultas.consultarConfiguracion("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO");
          try {
            tiempoMaximo = Long.valueOf(sTiempoMaximo);
          }catch(Exception ex) {
            tiempoMaximo = 10L;
          }
          long diferenciaMinutos = (((new Date()).getTime() - fechaToken.getTime()) / 60000);
          if(diferenciaMinutos > tiempoMaximo) {
            estado = "expirado";
          }else {
            estado = "abierto";
          }
          break;
        case R:
          estado = "confirmado";
          break;
        default:
          estado = "desconocido";
          break;
      }
      JsonObject jResultado = new JsonObject();
      jResultado.addProperty("resultado", "1");
      jResultado.addProperty("estado", estado);
      //Para mostrar la fecha hay que ajustarla según el timezone de la agenda
      Agenda agenda = tokenReserva.getRecurso().getAgenda();
      Empresa empresa = agendarReservas.obtenerEmpresaPorId(input.getIdEmpresa());
      TimeZone timezone = TimeZone.getDefault();
      if(agenda.getTimezone()!=null && !agenda.getTimezone().isEmpty()) {
        timezone = TimeZone.getTimeZone(agenda.getTimezone());
      }else {
        if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
          timezone = TimeZone.getTimeZone(empresa.getTimezone());
        }
      }
      Calendar cal = new GregorianCalendar();
      cal.setTime(fechaToken);
      cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTime().getTime()));
      fechaToken = cal.getTime();      
      jResultado.addProperty("ultimaModificacion", FORMATOFECHA_SINHORA.format(fechaToken)+FORMATOFECHA_HORA.format(fechaToken));
      return jResultado.toString();
    }catch(Exception ex) {
      //Otro error no manejado específicamente
      ex.printStackTrace();
      return jError(ex.getMessage());
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }

  // ======================================================================================================================================
  // ======================================================================================================================================

  @POST
  @Path("/modificar_reserva")
  @Consumes("application/json")
  @Produces("application/json")
  public String modificarReservaPost(ModificarReservaInput input) {
    return modificarReserva(input);
  }
  
  @GET
  @Path("/confirmar_reserva")
  @Produces("application/json")
  public String modificarReservaGet(@QueryParam("token") String token, @QueryParam("idEmpresa") Integer idEmpresa, @QueryParam("idAgenda") Integer idAgenda, 
      @QueryParam("idRecurso") Integer idRecurso, @QueryParam("idDisponibilidad") Integer idDisponibilidad, @QueryParam("idReserva") Integer idReserva, 
      @QueryParam("codigoSeguridad") String codigoSeguridad, @QueryParam("idioma") String idioma) {
    ModificarReservaInput input = new ModificarReservaInput();
    input.setToken(token);
    input.setIdEmpresa(idEmpresa);
    input.setIdAgenda(idAgenda);
    input.setIdRecurso(idRecurso);
    input.setIdReserva(idReserva);
    input.setIdDisponibilidad(idDisponibilidad);
    input.setCodigoSeguridad(codigoSeguridad);
    input.setIdioma(idioma);
    return modificarReserva(input);
  }
  
  private String modificarReserva(ModificarReservaInput input) {
    try {
      if(input.getIdEmpresa()==null) {
        return jError("debe_especificar_la_empresa");
      }
      if(input.getIdAgenda()==null) {
        return jError("debe_especificar_la_agenda");
      }
      if(input.getIdRecurso()==null) {
        return jError("debe_especificar_el_recurso");
      }
      if(input.getIdReserva()==null) {
        return jError("debe_especificar_la_reserva");
      }
      if(input.getIdDisponibilidad()==null) {
        return jError("debe_especificar_la_disponibilidad");
      }
      if(input.getCodigoSeguridad()==null) {
        return jError("el_codigo_de_seguridad_es_obligatorio");
      }
      //Validar el token y la empresa
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      boolean tokenValido = consultas.validarTokenEmpresa(input.getToken(), input.getIdEmpresa());
      if(!tokenValido) {
        return jError("no_se_encuentra_el_token_especificado");
      }
      //Hacer un login falso para que cambie el esquema
      String falsoUsuario = "sae" + input.getIdEmpresa()+ "-" + ((new Date()).getTime()+(new Random()).nextInt(1000)) + "/" + input.getIdEmpresa();
      String password = Utilidades.encriptarPassword(falsoUsuario);
      httpRequest.login(falsoUsuario, password);
      AgendarReservas agendarReservas = bl.getAgendarReservas();
      
      //Obtener la reserva original sin modificar
      Reserva reservaOriginal = agendarReservas.consultarReservaPorId(input.getIdReserva());
      if(reservaOriginal == null) {
        return jError("no_se_encuentra_la_reserva");
      }
      if(!input.getCodigoSeguridad().equals(reservaOriginal.getCodigoSeguridad())) {
        return jError("los_datos_ingresados_no_son_correctos");
      }
      Recurso recurso = reservaOriginal.getDisponibilidades().get(0).getRecurso();
      if(recurso.getCambiosAdmite()==null || !recurso.getCambiosAdmite().booleanValue()) {
        return jError("el_recurso_no_admite_cambios_de_reservas");
      }
      //Modificar la reserva
      Reserva reservaNueva = agendarReservas.modificarReserva(input.getIdEmpresa(), input.getIdAgenda(), input.getIdRecurso(), input.getIdReserva(), 
          input.getIdDisponibilidad(), null, input.getIdioma(), null);
      //Enviar las comunicaciones (cancelación de la original y confirmación de la nueva)
      Map<String, Object> datosEmpresa = consultas.consultarDatosEmpresa(input.getIdEmpresa());
      String formatoFecha = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_FECHA")?(String)datosEmpresa.get("FORMATO_FECHA"):"dd/MM/yyyy";
      String formatoHora = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_HORA")?(String)datosEmpresa.get("FORMATO_HORA"):"HH:mm";
      agendarReservas.enviarComunicacionesCancelacion(reservaOriginal, input.getIdioma(), formatoFecha, formatoHora);
      String linkBase = httpRequest.getScheme()+"://"+httpRequest.getServerName();
      if("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=80 || "https".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=443) {
        linkBase = linkBase + ":" + httpRequest.getServerPort();
      }
      String linkCancelacion = linkBase + "/sae/cancelarReserva/Paso1.xhtml?e="+input.getIdEmpresa()+"&a="+input.getIdAgenda()+"&ri="+reservaNueva.getId();
      String linkModificacion = linkBase + "/sae/modificarReserva/Paso1.xhtml?e="+input.getIdEmpresa()+"&a="+input.getIdAgenda()+"&r="+input.getIdRecurso()+"&ri="+reservaNueva.getId();
      agendarReservas.enviarComunicacionesConfirmacion(linkCancelacion, linkModificacion, reservaNueva, input.getIdioma(), formatoFecha, formatoHora);
      //Enviar la respuesta
      JsonObject jReserva = new JsonObject();
      jReserva.addProperty("resultado", "1");
      jReserva.addProperty("id", reservaNueva.getId());
      jReserva.addProperty("serieNumero", reservaNueva.getSerie()+"-"+reservaNueva.getNumero());
      jReserva.addProperty("codigoCancelacion", reservaNueva.getCodigoSeguridad());
      jReserva.addProperty("codigoTrazabilidad", reservaNueva.getTrazabilidadGuid());
      Disponibilidad disp = reservaNueva.getDisponibilidades().get(0); 
      jReserva.addProperty("fechaHora", FORMATOFECHA_SINHORA.format(disp.getFecha())+FORMATOFECHA_HORA.format(disp.getHoraInicio()));
      String idioma = input.getIdioma();
      String textoTicket = null;
      Agenda agenda = agendarReservas.consultarAgendaPorId(input.getIdAgenda());
      if(idioma!=null && !idioma.isEmpty() && agenda.getTextosAgenda().containsKey(idioma)) {
        textoTicket = agenda.getTextosAgenda().get(idioma).getTextoTicketConf();
      }else {
        if(!agenda.getTextosAgenda().isEmpty()) {
          textoTicket = agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoTicketConf();
        }
      }
      if(textoTicket != null) {
        jReserva.addProperty("textoTicket", Metavariables.remplazarMetavariables(textoTicket, reservaNueva, formatoFecha, formatoHora, linkCancelacion, linkModificacion));
      }
      return jReserva.toString();
    }catch(UserException uEx) {
      if(uEx instanceof ValidacionClaveUnicaException) {
        //Ya existe una reserva para la clave
        return jError("ya_existe_una_reserva_para_el_dia_especificado_con_los_datos_proporcionados");
      }
      if(uEx instanceof ValidacionPorCampoException) {
        //Hay al menos un campo que tiene problemas
        JsonArray jMensajes = new JsonArray();
        ValidacionPorCampoException vpcEx = (ValidacionPorCampoException)uEx;
        for(int i=0; i<vpcEx.getCantCampos(); i++) {
          jMensajes.add(vpcEx.getMensaje(i)+"/"+vpcEx.getNombreCampo(i));
        }
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "0");
        jError.add("errores", jMensajes);
        return jError.toString();
      }
      if("el_horario_acaba_de_quedar_sin_cupos".equals(uEx.getCodigoError())) {
        //No hay cupos para la disponibilidad seleccionada
        JsonObject jError = new JsonObject();
        jError.addProperty("resultado", "2");
        jError.addProperty("error", uEx.getCodigoError());
        return jError.toString();
      }
      //Otro error no manejado específicamente
      return jError(uEx.getCodigoError());
    }catch(Exception ex) {
      //Otro error no manejado específicamente
      ex.printStackTrace();
      return jError(ex.getMessage());
    } finally {
      try {
        httpRequest.logout();
      }catch(Exception ex) {
        //Nada para hacer
      }
    }
  }

  // ======================================================================================================================================
  // ======================================================================================================================================
  
}

