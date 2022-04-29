package uy.gub.imm.sae.web.common;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.global.Empresa;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

public class TicketUtiles {

  public List<String> obtenerFuentesDisponibles() {
    List<String> fuentes = new ArrayList<String>();
    fuentes.addAll(FontFactory.getRegisteredFonts());
    Collections.sort(fuentes);
    return fuentes;
  }
  
  public String generarTicket(Empresa empresa, Agenda agenda, Recurso recurso, TimeZone timeZone, Reserva reserva, String formatoFecha, String formatoHora, Map<String, String> textos, boolean imprimir) {
    
    try {
      String fuenteNombre = recurso.getFuenteTicket()!=null?recurso.getFuenteTicket():BaseFont.HELVETICA_BOLD;
      Integer iFuenteChica = recurso.getTamanioFuenteChica()!=null?recurso.getTamanioFuenteChica():8;
      Integer iFuenteNormal = recurso.getTamanioFuenteNormal()!=null?recurso.getTamanioFuenteNormal():10;
      Integer iFuenteGrande = recurso.getTamanioFuenteGrande()!=null?recurso.getTamanioFuenteGrande():12;

      Font fuenteGrande = FontFactory.getFont(fuenteNombre, BaseFont.CP1250, BaseFont.EMBEDDED, iFuenteGrande, Font.NORMAL);
      Font fuenteNormal = FontFactory.getFont(fuenteNombre, BaseFont.CP1250, BaseFont.EMBEDDED, iFuenteNormal, Font.NORMAL);
      Font fuenteChica = FontFactory.getFont(fuenteNombre, BaseFont.CP1250, BaseFont.EMBEDDED, iFuenteChica, Font.NORMAL);
      
      LineSeparator line = new LineSeparator();
      line.setLineWidth(0.5f);
      Chunk separador = new Chunk(line);

      SimpleDateFormat sdfFecha = new SimpleDateFormat (formatoFecha);
      SimpleDateFormat sdfHora = new SimpleDateFormat (formatoHora);
      
      Document document = new Document(new Rectangle(210,225), 5, 5, 5, 5);
  
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PdfWriter pdfWriter = PdfWriter.getInstance(document, os);

      document.addTitle(textos.get("ticket_de_reserva"));
      document.addTitle(textos.get("confirmacion"));
      document.addSubject("SAE");
      document.addAuthor("SAE");
      document.addKeywords("SAE,ticket,"+textos.get("confirmacion")+","+textos.get("reserva"));
      document.addProducer();
      document.addCreator("SAE");
      document.addHeader("Producer", "SAE");
      document.open();
      
      if(empresa != null && empresa.getLogo()!=null) {
        Image logo = Image.getInstance(empresa.getLogo());
        logo.scaleToFit(100, 30);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);
      }

      Paragraph parrafo = null;
      
      document.add(separador);
      
      parrafo = new Paragraph(new Phrase(agenda.getNombre(), fuenteGrande));
      document.add(parrafo);

      parrafo = new Paragraph(new Phrase(recurso.getNombre(), fuenteGrande));
      document.add(parrafo);

      parrafo = new Paragraph(new Phrase(recurso.getDireccion(), fuenteNormal));
      document.add(parrafo);

      document.add(separador);
      
      Chunk glue = new Chunk(new VerticalPositionMark());
      
      parrafo = new Paragraph(textos.get("fecha")+":", fuenteNormal);
      parrafo.add(new Chunk(glue));
      parrafo.add(sdfFecha.format(reserva.getDisponibilidades().get(0).getFecha()));
      document.add(parrafo);      

      parrafo = new Paragraph(textos.get("hora")+":", fuenteNormal);
      parrafo.add(new Chunk(glue));
      parrafo.add(sdfHora.format(reserva.getDisponibilidades().get(0).getHoraInicio()));
      document.add(parrafo);
      
      if (recurso.getMostrarNumeroEnTicket()){
        String serieNumeroLabel = "";
        String serieNumeroValue = "";
        String serie = recurso.getSerie();
        if (serie != null && !serie.trim().isEmpty()) {
          serieNumeroLabel = textos.get("serie") + "/";
          serieNumeroValue = serie + "/";
        }
        serieNumeroLabel = serieNumeroLabel + textos.get("numero");
        serieNumeroValue = serieNumeroValue + reserva.getNumero().toString();
        
        parrafo = new Paragraph(serieNumeroLabel, fuenteNormal);
        parrafo.add(new Chunk(glue));
        parrafo.add(serieNumeroValue);
        document.add(parrafo);
      }
      
      if(agenda.getConTrazabilidad()!=null && agenda.getConTrazabilidad().booleanValue()) {
        parrafo = new Paragraph(textos.get("codigo_de_trazabilidad")+":", fuenteNormal);
        parrafo.add(new Chunk(glue));
        parrafo.add(reserva.getTrazabilidadGuid());
        document.add(parrafo);
      }

      parrafo = new Paragraph(textos.get("codigo_de_seguridad")+":", fuenteNormal);
      parrafo.add(new Chunk(glue));
      parrafo.add(reserva.getCodigoSeguridad());
      document.add(parrafo);
      
      document.add(separador);
      
      SimpleDateFormat sdfFechaHora = new SimpleDateFormat (formatoFecha + " " + formatoHora);
      sdfFechaHora.setTimeZone(timeZone);
      parrafo = new Paragraph(textos.get("reserva_realizada_el")+" "+sdfFechaHora.format(new Date()), fuenteChica);
      document.add(parrafo);

      if (recurso.getMostrarIdEnTicket()!=null && recurso.getMostrarIdEnTicket().booleanValue()){
        parrafo = new Paragraph(textos.get("id_de_la_reserva")+": "+reserva.getId().toString(), fuenteChica);
        document.add(parrafo);
      }
      
      if(imprimir) {
        pdfWriter.addJavaScript("this.print({bUI: true, bSilent: true, bShrinkToFit: true});",false); 
        pdfWriter.addJavaScript("this.closeDoc(true);");   
      }
      
      document.close();
      
      FacesContext facesContext = FacesContext.getCurrentInstance();
      HttpServletResponse response =  (HttpServletResponse)facesContext.getExternalContext().getResponse();
      response.setContentType("application/pdf");  
      if(!imprimir) {
        response.setHeader("Content-disposition", "attachment; filename=ticket.pdf");
      }
      os.writeTo(response.getOutputStream());
      response.getOutputStream().flush();
      response.getOutputStream().close();
      facesContext.responseComplete();
      
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    
    
    return null;
  }
  
  public String generarTicketPresencial(Empresa empresa, Agenda agenda, Recurso recurso, TimeZone timeZone, Reserva reserva, String formatoFecha, String formatoHora, Map<String, String> textos, boolean imprimir) {
    
    try {
      String fuenteNombre = recurso.getFuenteTicket()!=null?recurso.getFuenteTicket():BaseFont.HELVETICA_BOLD;
      Integer iFuenteChica = recurso.getTamanioFuenteChica()!=null?recurso.getTamanioFuenteChica():8;
      Integer iFuenteNormal = recurso.getTamanioFuenteNormal()!=null?recurso.getTamanioFuenteNormal():10;
      Integer iFuenteGrande = recurso.getTamanioFuenteGrande()!=null?recurso.getTamanioFuenteGrande():12;

      Font fuenteGrande = FontFactory.getFont(fuenteNombre, BaseFont.CP1250, BaseFont.EMBEDDED, iFuenteGrande, Font.NORMAL);
      Font fuenteNormal = FontFactory.getFont(fuenteNombre, BaseFont.CP1250, BaseFont.EMBEDDED, iFuenteNormal, Font.NORMAL);
      Font fuenteChica = FontFactory.getFont(fuenteNombre, BaseFont.CP1250, BaseFont.EMBEDDED, iFuenteChica, Font.NORMAL);
      
      LineSeparator line = new LineSeparator();
      line.setLineWidth(0.5f);
      Chunk separador = new Chunk(line);

      SimpleDateFormat sdfFecha = new SimpleDateFormat (formatoFecha);
      
      Document document = new Document(new Rectangle(210,225), 5, 5, 5, 5);
  
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PdfWriter pdfWriter = PdfWriter.getInstance(document, os);

      document.addTitle(textos.get("ticket_de_reserva"));
      document.addTitle(textos.get("confirmacion"));
      document.addSubject("SAE");
      document.addAuthor("SAE");
      document.addKeywords("SAE,ticket,"+textos.get("confirmacion")+","+textos.get("reserva"));
      document.addProducer();
      document.addCreator("SAE");
      document.addHeader("Producer", "SAE");
      document.open();
      
      if(empresa != null && empresa.getLogo()!=null) {
        Image logo = Image.getInstance(empresa.getLogo());
        logo.scaleToFit(100, 30);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);
      }

      Paragraph parrafo = null;
      
      document.add(separador);
      
      parrafo = new Paragraph(new Phrase(agenda.getNombre(), fuenteGrande));
      document.add(parrafo);

      parrafo = new Paragraph(new Phrase(recurso.getNombre(), fuenteGrande));
      document.add(parrafo);

      document.add(separador);
      
      Chunk glue = new Chunk(new VerticalPositionMark());
      
      parrafo = new Paragraph(textos.get("fecha")+":", fuenteNormal);
      parrafo.add(new Chunk(glue));
      parrafo.add(sdfFecha.format(reserva.getDisponibilidades().get(0).getFecha()));
      document.add(parrafo);      

      String serieNumeroLabel = "";
      String serieNumeroValue = "";
      String serie = reserva.getSerie();
      if (serie != null && !serie.trim().isEmpty()) {
        serieNumeroLabel = textos.get("serie") + "/";
        serieNumeroValue = serie + "/";
      }
      serieNumeroLabel = serieNumeroLabel + textos.get("numero");
      serieNumeroValue = serieNumeroValue + reserva.getNumero().toString();
      parrafo = new Paragraph(serieNumeroLabel, fuenteNormal);
      parrafo.add(new Chunk(glue));
      parrafo.add(serieNumeroValue);
      document.add(parrafo);
      
      if(agenda.getConTrazabilidad()!=null && agenda.getConTrazabilidad().booleanValue()) {
        parrafo = new Paragraph(textos.get("codigo_de_trazabilidad")+":", fuenteNormal);
        parrafo.add(new Chunk(glue));
        parrafo.add(reserva.getTrazabilidadGuid());
        document.add(parrafo);
      }

      document.add(separador);
      
      SimpleDateFormat sdfFechaHora = new SimpleDateFormat (formatoFecha + " " + formatoHora);
      sdfFechaHora.setTimeZone(timeZone);
      parrafo = new Paragraph(textos.get("reserva_realizada_el")+" "+sdfFechaHora.format(new Date()), fuenteChica);
      document.add(parrafo);

      if (recurso.getMostrarIdEnTicket()!=null && recurso.getMostrarIdEnTicket().booleanValue()){
        parrafo = new Paragraph(textos.get("id_de_la_reserva")+": "+reserva.getId().toString(), fuenteChica);
        document.add(parrafo);
      }
      
      if(imprimir) {
        pdfWriter.addJavaScript("this.print({bUI: true, bSilent: true, bShrinkToFit: true});",false); 
        pdfWriter.addJavaScript("this.closeDoc(true);");   
      }
      
      document.close();
      
      FacesContext facesContext = FacesContext.getCurrentInstance();
      HttpServletResponse response =  (HttpServletResponse)facesContext.getExternalContext().getResponse();
      response.setContentType("application/pdf");  
      if(!imprimir) {
        response.setHeader("Content-disposition", "attachment; filename=ticket.pdf");
      }
      os.writeTo(response.getOutputStream());
      response.getOutputStream().flush();
      response.getOutputStream().close();
      facesContext.responseComplete();
      
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    
    
    return null;
  }
}
