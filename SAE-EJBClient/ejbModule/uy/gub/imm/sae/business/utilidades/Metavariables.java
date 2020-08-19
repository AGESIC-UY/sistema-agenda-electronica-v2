package uy.gub.imm.sae.business.utilidades;

import java.text.SimpleDateFormat;
import java.util.Date;

import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;

public class Metavariables {
  
  public static String remplazarMetavariables(String texto, Reserva reserva, String formatoFecha, String formatoHora, String linkCancelacion, String linkModificacion) {
    if(reserva == null) {
      return texto;
    }
    Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
    if(recurso.getCambiosAdmite()==null || !recurso.getCambiosAdmite().booleanValue()) {
      linkModificacion = "";
    }
    return remplazarMetavariables(texto, reserva.getId(), recurso.getAgenda().getNombre(), recurso.getNombre(), recurso.getDireccion(), reserva.getTramiteNombre(),
        reserva.getDisponibilidades().get(0).getHoraInicio(), recurso.getSerie(), reserva.getNumero(), reserva.getCodigoSeguridad(), 
        reserva.getTrazabilidadGuid(), formatoFecha, formatoHora, linkCancelacion, linkModificacion);
  }

  public static String remplazarMetavariables(String texto, Integer idReserva, String nombreAgenda, String nombreRecurso, String dirRecurso, String nombreTramite, 
      Date fecha, String serie, Integer numero, String codigoSeguridad, String guidTrazabilidad, String formatoFecha, String formatoHora, 
      String linkCancelacion, String linkModificacion) {
    if(texto==null) {
      return "";
    }
    if(formatoFecha == null) {
      formatoFecha = "yyyy-MM-dd";
    }
    if(formatoHora == null) {
      formatoHora = "HH:mm";
    }
    SimpleDateFormat sdfFecha = new SimpleDateFormat (formatoFecha);
    SimpleDateFormat sdfHora = new SimpleDateFormat (formatoHora);
    texto = texto.replace("{{AGENDA}}", nombreAgenda);
    texto = texto.replace("{{RECURSO}}", nombreRecurso);
    texto = texto.replace("{{TRAMITE}}", nombreTramite);
    texto = texto.replace("{{DIRECCION}}", dirRecurso);
    texto = texto.replace("{{FECHA}}", sdfFecha.format(fecha));
    texto = texto.replace("{{HORA}}", sdfHora.format(fecha));
    texto = texto.replace("{{SERIE}}", serie!=null?serie:"---");
    texto = texto.replace("{{NUMERO}}", numero!=null?numero.toString():"---");
    texto = texto.replace("{{CODIGOSEGURIDAD}}", codigoSeguridad);
    texto = texto.replace("{{CODIGOTRAZABILIDAD}}", guidTrazabilidad!=null?guidTrazabilidad:"---");
    texto = texto.replace("{{CANCELACION}}", linkCancelacion!=null?linkCancelacion:"");
    texto = texto.replace("{{MODIFICACION}}", linkModificacion!=null?linkModificacion:"");
    texto = texto.replace("{{IDRESERVA}}", idReserva.toString());
    
    return texto;
  }

}
