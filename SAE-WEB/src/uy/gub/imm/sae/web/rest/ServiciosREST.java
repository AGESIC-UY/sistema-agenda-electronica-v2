package uy.gub.imm.sae.web.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.factories.BusinessLocator;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionPorCampoException;
import uy.gub.imm.sae.login.Utilidades;

@Path("/consultas")
public class ServiciosREST {

	private final static SimpleDateFormat FORMATOFECHA_CONHORA = new SimpleDateFormat("yyyyMMdd HH:mm"); 	
  private final static SimpleDateFormat FORMATOFECHA_SINHORA = new SimpleDateFormat("yyyyMMdd");   
	
  @Context
  private HttpServletRequest httpRequest;

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
          input.getDatosReserva(), input.getIdTransaccionPadre(), input.getPasoTransaccionPadre(), input.getIdioma());
      Agenda agenda = agendarReservas.consultarAgendaPorId(input.getIdAgenda());
      //Enviar el mail de confirmacion
      String linkCancelacion = httpRequest.getScheme()+"://"+httpRequest.getServerName();
      if("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=80 || "https".equals(httpRequest.getScheme()) && httpRequest.getServerPort()!=443) {
        linkCancelacion = linkCancelacion + ":" + httpRequest.getServerPort();
      }
      linkCancelacion = linkCancelacion + "/sae/cancelarReserva/Paso1.xhtml?e="+input.getIdEmpresa()+"&a="+agenda.getId()+"&ri="+reserva.getId();
      Map<String, Object> datosEmpresa = consultas.consultarDatosEmpresa(input.getIdEmpresa());
      String formatoFecha = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_FECHA")?(String)datosEmpresa.get("FORMATO_FECHA"):"dd/MM/yyyy";
      String formatoHora = datosEmpresa!=null && datosEmpresa.containsKey("FORMATO_HORA")?(String)datosEmpresa.get("FORMATO_HORA"):"HH:mm";
      agendarReservas.enviarComunicacionesConfirmacion(linkCancelacion, reserva, input.getIdioma(), formatoFecha, formatoHora);
      //Enviar la respuesta
      JsonObject jReserva = new JsonObject();
      jReserva.addProperty("resultado", "1");
      jReserva.addProperty("id", reserva.getId());
      jReserva.addProperty("serieNumero", reserva.getSerie()+"-"+reserva.getNumero());
      jReserva.addProperty("codigoCancelacion", reserva.getCodigoSeguridad());
      jReserva.addProperty("codigoTrazabilidad", reserva.getTrazabilidadGuid());
      String idioma = input.getIdioma();
      if(idioma!=null && !idioma.isEmpty() && agenda.getTextosAgenda().containsKey(idioma)) {
        jReserva.addProperty("textoTicket", agenda.getTextosAgenda().get(idioma).getTextoTicketConf());
      }else {
        if(!agenda.getTextosAgenda().isEmpty()) {
          jReserva.addProperty("textoTicket", agenda.getTextosAgenda().values().toArray(new TextoAgenda[0])[0].getTextoTicketConf());
        }
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
  

}
