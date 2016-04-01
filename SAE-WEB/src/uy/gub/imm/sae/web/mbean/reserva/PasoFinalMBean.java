/*
 * SAE - Sistema de Agenda Electronica
 * Copyright (C) 2009  IMM - Intendencia Municipal de Montevideo
 *
 * This file is part of SAE.

 * SAE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uy.gub.imm.sae.web.mbean.reserva;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.ejb.facade.Recursos;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.web.common.FormularioDinamicoReserva;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
/**
 * Presenta todos los datos de la reserva y da la opción de imprimir un recibo.
 * @author im2716295
 *
 */
public class PasoFinalMBean extends PasoMBean {

	private Recursos recursosEJB;
	
	private SesionMBean sesionMBean;
	
	private List<DatoDelRecurso> infoRecurso;
	
	private UIComponent campos;
	
	private Logger logger = Logger.getLogger(PasoFinalMBean.class);

	
	@PostConstruct
	public void init() {
		try {
			recursosEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getRecursos();
			if (sesionMBean.getAgenda() == null || sesionMBean.getRecurso() == null || sesionMBean.getReservaConfirmada() == null) {
				redirect(ESTADO_INVALIDO_PAGE_OUTCOME);
				return;
			}
		} catch (ApplicationException ex) {
			ex.printStackTrace();
			redirect(ERROR_PAGE_OUTCOME);
		}
	}	

	public SesionMBean getSesionMBean() {
		return sesionMBean;
	}

	public void setSesionMBean(SesionMBean sesionMBean) {
		this.sesionMBean = sesionMBean;
	}
	
	public String getAgendaNombre() {
		if (sesionMBean.getAgenda() != null) {
			return sesionMBean.getAgenda().getNombre();
		}
		else {
			return null;
		}
	}	
	
	public String getEtiquetaDelRecurso() {
		//TextoAgenda textoAgenda = sesionMBean.getAgenda().getTextosAgenda();
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		} else {
			return "";
		}
	}
	
	public List<DatoDelRecurso> getInfoRecurso() {

		if (infoRecurso == null) {
			if (sesionMBean.getRecurso() != null) {
				try {
					infoRecurso = recursosEJB.consultarDatosDelRecurso(sesionMBean.getRecurso());
					if (infoRecurso.isEmpty()) {
						infoRecurso = null;
					}
				} catch (Exception e) {
					addErrorMessage(e);
				}
			}
		}
		return infoRecurso;
	}	
	
	public UIComponent getCampos() {
		return campos;
	}
	
	public void setCampos(UIComponent campos) {
		
		this.campos = campos;
		
		String mensajeError = "";
		try {
			Recurso recurso = sesionMBean.getRecurso();

			
			//El chequeo de recurso != null es en caso de un acceso directo a la pagina, es solo
			//para que no salte la excepcion en el log, pues de todas formas sera redirigido a una pagina de error.
			if (campos.getChildCount() == 0 && recurso != null) {
				mensajeError = "RESERVA: ";
				List<AgrupacionDato> agrupaciones = recursosEJB.consultarDefinicionDeCampos(recurso);
				Reserva rtmp = sesionMBean.getReservaConfirmada();
				if (rtmp == null) {
					mensajeError += "nulo";
				} else {
					mensajeError += rtmp.getId() + ":" + rtmp.getFechaCreacion() + ":";
					if (rtmp.getDatosReserva()== null) {
						mensajeError += "DatosReserva nulo";
					}
				}
				
				Map<String, Object> valores = obtenerValores(sesionMBean.getReservaConfirmada().getDatosReserva());
				FormularioDinamicoReserva formularioDin = new FormularioDinamicoReserva(valores, sesionMBean.getFormatoFecha());
				formularioDin.armarFormulario(agrupaciones, null);
				UIComponent formulario = formularioDin.getComponenteFormulario();
				campos.getChildren().add(formulario);
			}
		} catch (Exception e) {
			logger.error(mensajeError, e);
			redirect(ERROR_PAGE_OUTCOME);
		}
	}

	public String getDescripcion() {
		//TextoAgenda textoAgenda = sesionMBean.getAgenda().getTextosAgenda();
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			String str = textoAgenda.getTextoTicketConf();
			if(str!=null) {
				return str;
			}	else {
				return "";
			}
		}
		else {
			return "";
		}
	}
	

	/**
	 * @param datos de alguna reserva
	 * @return retorna los valores de cada dato en un mapa cuya clave es el nombre del campo 
	 */
	private Map<String, Object> obtenerValores(Set<DatoReserva> datos) {
		Map<String, Object> valores = new HashMap<String, Object>();
		for (DatoReserva dato : datos) {
			valores.put(dato.getDatoASolicitar().getNombre(), dato.getValor());
		}
		return valores;
	}
	
	public void beforePhase (PhaseEvent event) {
		disableBrowserCache(event);
	}
	
	public Date getDiaSeleccionado() {
		return sesionMBean.getDiaSeleccionado();
	}

	public Date getHoraSeleccionada() {
		if (sesionMBean.getDisponibilidad() != null) {
			return sesionMBean.getDisponibilidad().getHoraInicio();
		} else {
			return null;
		}
	}

	public String getReservaNumero() {
		if(sesionMBean.getReservaConfirmada() != null && sesionMBean.getReservaConfirmada().getNumero()!=null) {
			return "No. " + sesionMBean.getReservaConfirmada().getNumero();
		}
		return "";
	}
	
	public String getRecursoDescripcion() {
		Recurso recurso = sesionMBean.getRecurso();
		if (recurso != null) {
			String descripcion = recurso.getNombre();
			if(descripcion != null && !descripcion.equals(recurso.getDireccion())) {
				descripcion = descripcion + " - " + recurso.getDireccion();
			}
			return  descripcion;
		} else {
			return null;
		}
	}
	
	public String generarTicket(boolean imprimir) {
		try {
			BaseFont helveticaBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			BaseFont helveticaOblique = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			
			SimpleDateFormat sdfFecha = new SimpleDateFormat (sesionMBean.getFormatoFecha());
			SimpleDateFormat sdfHora = new SimpleDateFormat (sesionMBean.getFormatoHora());
			
			Rectangle pageSize = new Rectangle(210,225);
			
			Document document = new Document(pageSize);
			document.addTitle(sesionMBean.getTextos().get("ticket_de_reserva"));
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
			document.open();
			
			PdfContentByte pdfContent = pdfWriter.getDirectContent();

			Empresa empresa = sesionMBean.getEmpresaActual();
			if(empresa != null && empresa.getLogo()!=null) {
				Image img = Image.getInstance(empresa.getLogo());
				img.scaleAbsolute(100,30);
				img.setAbsolutePosition(55, 170);
				document.add(img);
			}
			
			int posY = 170;
			
			//Dibujo primer línea
			LineSeparator line = new LineSeparator();
			line.setAlignment(LineSeparator.ALIGN_CENTER);
			line.setLineColor(BaseColor.BLACK);
			line.setLineWidth(0.5f);
			line.drawLine(pdfContent, 10, 200,  posY);
			posY = posY - 15;

			//Nombre de la agenda (trámite)
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 10);
			pdfContent.setTextMatrix(15, posY);
			pdfContent.showText(sesionMBean.getAgenda().getNombre());
			pdfContent.endText();
			posY = posY - 15;
			
			//Nombre del recurso (oficina)
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 10);
			pdfContent.setTextMatrix(15, posY);
			pdfContent.showText(sesionMBean.getRecurso().getNombre());
			pdfContent.endText();
			posY = posY - 15;
			
			//Dirección del recurso (oficina)
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 10);
			pdfContent.setTextMatrix(15, posY);
			pdfContent.showText(sesionMBean.getRecurso().getDireccion());
			pdfContent.endText();
			posY = posY - 15;
			
			//Dibujo segunda línea
			posY = posY + 10;
			line.setLineColor(BaseColor.BLACK);
			line.drawLine(pdfWriter.getDirectContent(), 10, 200,  posY);
			posY = posY - 15;

			//Fecha
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 12);
			pdfContent.setTextMatrix(15, posY);
			pdfContent.showText(sesionMBean.getTextos().get("fecha")+":");
			pdfContent.endText();

			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 12);
			pdfContent.setTextMatrix(130, posY);
			pdfContent.showText(sdfFecha.format(sesionMBean.getDisponibilidad().getFecha()));
			pdfContent.endText();
			posY = posY - 15;
			
			//Hora
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 12);
			pdfContent.setTextMatrix(15, posY);
			pdfContent.showText(sesionMBean.getTextos().get("hora")+":");
			pdfContent.endText();
			
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 12);
			pdfContent.setTextMatrix(130, posY);
			pdfContent.showText(sdfHora.format(sesionMBean.getDisponibilidad().getHoraInicio()));
			pdfContent.endText();
			posY = posY - 15;

			//Serie
			String serie = sesionMBean.getRecurso().getSerie();
			if (serie != null && !serie.trim().isEmpty()) {
				pdfContent.beginText();
				pdfContent.setFontAndSize(helveticaBold, 12);
				pdfContent.setTextMatrix(15, posY);
				pdfContent.showText(sesionMBean.getTextos().get("serie")+":");
				pdfContent.endText();

				pdfContent.beginText();
				pdfContent.setFontAndSize(helveticaBold, 12);
				pdfContent.setTextMatrix(130, posY);
				pdfContent.showText(serie);
				pdfContent.endText();
				posY = posY - 15;
			}
			
			if (sesionMBean.getRecurso().getMostrarNumeroEnTicket()){
				pdfContent.beginText();
				pdfContent.setFontAndSize(helveticaBold, 12);
				pdfContent.setTextMatrix(15, posY);
				pdfContent.showText(sesionMBean.getTextos().get("numero")+":");
				pdfContent.endText();

				pdfContent.beginText();
				pdfContent.setFontAndSize(helveticaBold, 12);
				pdfContent.setTextMatrix(130, posY);
				pdfContent.showText(sesionMBean.getReservaConfirmada().getNumero().toString());
				pdfContent.endText();
				posY = posY - 15;
			}

			//Dibujo tercera línea
			posY = posY + 10;
			line.setLineColor(BaseColor.BLACK);
			line.drawLine(pdfWriter.getDirectContent(), 10, 200,  posY);
			posY = posY - 15;
			
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 10);
			pdfContent.setTextMatrix(15, posY);
			pdfContent.showText(sesionMBean.getTextos().get("codigo_de_trazabilidad")+":");
			pdfContent.endText();

			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 12);
			pdfContent.setTextMatrix(130, posY);
			pdfContent.showText(sesionMBean.getReservaConfirmada().getTrazabilidadGuid());
			pdfContent.endText();
			posY = posY - 15;
			
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 10);
			pdfContent.setTextMatrix(15, posY);
			pdfContent.showText(sesionMBean.getTextos().get("codigo_de_seguridad")+":");
			pdfContent.endText();

			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaBold, 12);
			pdfContent.setTextMatrix(130, posY);
			pdfContent.showText(sesionMBean.getReservaConfirmada().getCodigoSeguridad());
			pdfContent.endText();
			posY = posY - 15;

			//Dibujo cuarta línea
			posY = posY + 10;
			line.setLineColor(BaseColor.BLACK);
			line.drawLine(pdfWriter.getDirectContent(), 10, 200,  posY);
			posY = posY - 15;
			
			pdfContent.beginText();
			pdfContent.setFontAndSize(helveticaOblique, 8);
			pdfContent.setTextMatrix(20, posY);
			Date ahora = new Date();
			pdfContent.showText(sesionMBean.getTextos().get("reserva_realizada_el")+" "+sdfFecha.format(ahora)+ " " + sdfHora.format(ahora));
			pdfContent.endText();
			posY = posY - 15;
			
			
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

	public String getUrlCancelacion() {
		Reserva reserva = sesionMBean.getReservaConfirmada();
		if (reserva != null) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String linkCancelacion = request.getScheme()+"://"+request.getServerName();
			if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
				linkCancelacion = linkCancelacion + ":" + request.getServerPort();
			}
			Agenda agenda = reserva.getDisponibilidades().get(0).getRecurso().getAgenda();
			
			linkCancelacion = linkCancelacion + "/sae/cancelarReserva/Paso1.xhtml?e="+sesionMBean.getEmpresaActual().getId()+"&a="+agenda.getId()+"&ri="+reserva.getId();
			return linkCancelacion;
		}
		return null;
	}
	
	public String getUrlTramite()
	{
		
		return sesionMBean.getUrlTramite();
	}
	
}
