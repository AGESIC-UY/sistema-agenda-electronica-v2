package uy.gub.imm.sae.business.utilidades;

import java.text.SimpleDateFormat;

import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;

public class Metavariables {
  
  public static String remplazarMetavariables(String texto, Reserva reserva, String formatoFecha, String formatoHora, String linkCancelacion, String linkModificacion) {
    
    if(texto==null) {
      return null;
    }
    if(reserva == null) {
      return texto;
    }
    if(formatoFecha == null) {
      formatoFecha = "yyyy-MM-dd";
    }
    if(formatoHora == null) {
      formatoHora = "HH:mm";
    }
    
    SimpleDateFormat sdfFecha = new SimpleDateFormat (formatoFecha);
    SimpleDateFormat sdfHora = new SimpleDateFormat (formatoHora);
    
    Recurso recurso = reserva.getDisponibilidades().get(0).getRecurso();
    
    texto = texto.replace("{{AGENDA}}", recurso.getAgenda().getNombre());
    texto = texto.replace("{{RECURSO}}", recurso.getNombre());
    texto = texto.replace("{{TRAMITE}}", reserva.getTramiteNombre());
    texto = texto.replace("{{DIRECCION}}", recurso.getDireccion());
    texto = texto.replace("{{FECHA}}", sdfFecha.format(reserva.getDisponibilidades().get(0).getFecha()));
    texto = texto.replace("{{HORA}}", sdfHora.format(reserva.getDisponibilidades().get(0).getHoraInicio()));
    texto = texto.replace("{{SERIE}}", recurso.getSerie()!=null?recurso.getSerie():"---");
    texto = texto.replace("{{NUMERO}}", reserva.getNumero()!=null?reserva.getNumero().toString():"---");
    texto = texto.replace("{{CODIGOSEGURIDAD}}", reserva.getCodigoSeguridad());
    texto = texto.replace("{{CODIGOTRAZABILIDAD}}", reserva.getTrazabilidadGuid()!=null?reserva.getTrazabilidadGuid():"---");
    texto = texto.replace("{{CANCELACION}}", linkCancelacion);
    if(recurso.getCambiosAdmite()!=null && recurso.getCambiosAdmite().booleanValue()) {
      texto = texto.replace("{{MODIFICACION}}", linkModificacion);
    }else {
      texto = texto.replace("{{MODIFICACION}}", "");
    }
    texto = texto.replace("{{IDRESERVA}}", reserva.getId().toString());
    
    return texto;
  }

}
