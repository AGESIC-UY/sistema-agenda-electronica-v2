package uy.gub.imm.sae.web.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import uy.gub.imm.sae.business.ejb.facade.Consultas;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.factories.BusinessLocator;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.exception.UserException;

@Path("/consultas")
public class ServiciosREST {

	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm"); 	
	
	@POST
  @Path("/reservas-por-agenda-y-documento")
  @Consumes("application/json")
  @Produces("application/json")
	public String getReservasDocumento(ReservasPorDocumentoInput input) {
		try {
			
			BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
			Consultas consultas = bl.getConsultas();
			
			List<Map<String, Object>> reservas = consultas.consultarReservasPorTokenYDocumento(input.getToken(), input.getIdAgenda(), 
					input.getIdRecurso(), input.getTipoDocumento(), input.getNumeroDocumento());
			
			StringBuilder resp = new StringBuilder("[");
			for(Map<String, Object> reserva : reservas) {
				if(resp.length()>1) {
					resp.append(",");
				}
				Date fecha = (Date) reserva.get("fechaHora");
        String nombreRecurso = (String) reserva.get("nombreRecurso");
        String codigoCancelacion = (String) reserva.get("codigoCancelacion");
				resp.append("{'fecha_reserva': '"+sdf.format(fecha)+"', 'nombre_recurso': '"+nombreRecurso+"', 'codigo_cancelacion': '"+codigoCancelacion+"'}");
			}
			resp.append("]");
			
			return resp.toString();
		}catch(UserException uEx) {
      return "{'error':'"+uEx.getCodigoError()+"'}";
    }catch(Exception ex) {
			return "{'error':'"+ex.getMessage()+"'}";
		}
  }	
	
  @POST
  @Path("/reservas-por-agenda-y-documento-full")
  @Consumes("application/json")
  @Produces("application/json")
  public String getReservasDocumentoFull(ReservasPorDocumentoInput input) {
    try {
      BusinessLocator bl = BusinessLocatorFactory.getLocatorContextoNoAutenticado();
      Consultas consultas = bl.getConsultas();
      List<Map<String, Object>> reservas0 = consultas.consultarReservasPorTokenYDocumentoFull(input.getToken(), input.getIdAgenda(), 
          input.getIdRecurso(), input.getTipoDocumento(), input.getNumeroDocumento());
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
        reserva1.addProperty("fechaHora", reserva0.get("fechaHora")!=null?reserva0.get("fechaHora").toString():"");
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
      JsonObject ret = new JsonObject();
      ret.add("reservas", reservas1);
      ret.addProperty("cantidad", reservas1.size());
      return ret.toString();
    }catch(UserException uEx) {
      return "{'error':'"+uEx.getCodigoError()+"'}";
    }catch(Exception ex) {
      return "{'error':'"+ex.getMessage()+"'}";
    }
  } 
}
