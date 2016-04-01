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

package uy.gub.imm.sae.business.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.web.common.reporte.JRDinamicBeanCollectionDataSource;

public class HelperTest {

	static Logger logger = Logger.getLogger(HelperTest.class);
	
	
	public static List<Object> createBeanCollection(){
		
		List<Object> beanColl = new ArrayList<Object>();
		ReservaYDato b;
		
		Calendar cal = new GregorianCalendar();
		Date ahora = cal.getTime();
		
		b = new ReservaYDato();
		b.setNombreAgenda("Agenda1");
		b.setNombreRecurso("Recurso1");
		b.setIdReserva(BigDecimal.valueOf(1));
		b.setFechaDisp(new Timestamp(ahora.getTime()));
		cal.add(Calendar.HOUR, 1);
		b.setHoraInicioDisp(new Timestamp(cal.getTimeInMillis()));
		b.setIdAgrupDatos(BigDecimal.valueOf(1));
		b.setEstadoReserva("C");
		b.setNombreAgrupDatos("agrupacion1");
		b.setDatosComoHtml(
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo1:</style>valor1      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo2:</style>valor2      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo3:</style>valor3      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo4:</style>valor4      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo5:</style>valor5      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo6:</style>valor6      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo7:</style>valor7      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo8:</style>valor8      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo9:</style>valor9      "
		);
		beanColl.add(b);

		b = new ReservaYDato();
		b.setNombreAgenda("Agenda1");
		b.setNombreRecurso("Recurso1");
		b.setIdReserva(BigDecimal.valueOf(1));
		b.setFechaDisp(new Timestamp(ahora.getTime()));
		b.setHoraInicioDisp(new Timestamp(cal.getTimeInMillis()));
		b.setIdAgrupDatos(BigDecimal.valueOf(2));
		b.setEstadoReserva("C");
		b.setNombreAgrupDatos("agrupacion2");
		
		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		
		b.setDatosComoHtml(
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo1:</style>valor1      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo2:</style>valor2      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo3:</style>valor3      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo4:</style>valor4      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo5:</style>valor5      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo6:</style>valor6      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo7:</style>valor7      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo8:</style>valor8      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo9:</style>valor9      "
		);
		beanColl.add(b);

		b = new ReservaYDato();
		b.setNombreAgenda("Agenda1");
		b.setNombreRecurso("Recurso1");
		b.setIdReserva(BigDecimal.valueOf(2));
		b.setIdAgrupDatos(BigDecimal.valueOf(21));
		b.setFechaDisp(new Timestamp(System.currentTimeMillis()));
		b.setHoraInicioDisp(new Timestamp(System.currentTimeMillis()));
		b.setEstadoReserva("C");
		b.setNombreAgrupDatos("agrupacion1");

		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		
		b.setDatosComoHtml(
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo1:</style>valor1      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo2:</style>valor2      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo3:</style>valor3      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo4:</style>valor4      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo5:</style>valor5      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo6:</style>valor6      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo7:</style>valor7      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo8:</style>valor8      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo9:</style>valor9      "
		);
		beanColl.add(b);
		

		b = new ReservaYDato();
		b.setNombreAgenda("Agenda1");
		b.setNombreRecurso("Recurso1");
		b.setIdReserva(BigDecimal.valueOf(2));
		b.setIdAgrupDatos(BigDecimal.valueOf(22));
		b.setFechaDisp(new Timestamp(System.currentTimeMillis()));
		b.setHoraInicioDisp(new Timestamp(System.currentTimeMillis()));
		b.setEstadoReserva("C");
		b.setNombreAgrupDatos("agrupacion1");

		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		
		b.setDatosComoHtml(
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo1:</style>valor1      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo2:</style>valor2      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo3:</style>valor3      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo4:</style>valor4      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo5:</style>valor5      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo6:</style>valor6      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo7:</style>valor7      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo8:</style>valor8      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo9:</style>valor9      "
		);
		beanColl.add(b);

		b = new ReservaYDato();
		b.setNombreAgenda("Agenda1");
		b.setNombreRecurso("Recurso1");
		b.setIdReserva(BigDecimal.valueOf(2));
		b.setIdAgrupDatos(BigDecimal.valueOf(23));
		b.setFechaDisp(new Timestamp(System.currentTimeMillis()));
		b.setHoraInicioDisp(new Timestamp(System.currentTimeMillis()));
		b.setEstadoReserva("C");
		b.setNombreAgrupDatos("agrupacion1");

		b.setDatosComoHtml(
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo1:</style>valor1      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo2:</style>valor2      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo3:</style>valor3      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo4:</style>valor4      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo5:</style>valor5      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo6:</style>valor6      " +
				"<br/>" +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo7:</style>valor7      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo8:</style>valor8      " +
				"<style isBold=\"true\" pdfFontName=\"Helvetica-Bold\">campo9:</style>valor9      "
		);
		beanColl.add(b);
		
		return beanColl;
	}
	
	public static JRDinamicBeanCollectionDataSource createDataSourceReservaDTO(){
		
		List<ReservaDTO> beanColl = new ArrayList<ReservaDTO>();
		ReservaDTO b;
		
		Calendar cal = new GregorianCalendar();
		Date ahora = cal.getTime();
		
		cal.add(Calendar.HOUR, 1);
		b = new ReservaDTO();
		b.setId(Integer.valueOf(1));
		b.setFecha(ahora);
		b.setHoraInicio(cal.getTime());
		b.setEstado("C");
		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		beanColl.add(b);

		b = new ReservaDTO();
		b.setId(Integer.valueOf(2));
		b.setFecha(ahora);
		b.setHoraInicio(cal.getTime());
		b.setEstado("C");
		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		beanColl.add(b);

		b = new ReservaDTO();
		b.setId(Integer.valueOf(3));
		b.setFecha(ahora);
		b.setHoraInicio(cal.getTime());
		b.setEstado("C");
		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		beanColl.add(b);

		b = new ReservaDTO();
		b.setId(Integer.valueOf(4));
		b.setFecha(ahora);
		b.setHoraInicio(cal.getTime());
		b.setEstado("C");
		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		beanColl.add(b);

		cal.add(Calendar.HOUR, 1);
		b = new ReservaDTO();
		b.setId(Integer.valueOf(5));
		b.setFecha(ahora);
		b.setHoraInicio(cal.getTime());
		b.setEstado("C");
		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		beanColl.add(b);

		b = new ReservaDTO();
		b.setId(Integer.valueOf(6));
		b.setFecha(ahora);
		b.setHoraInicio(cal.getTime());
		b.setEstado("C");
		b.getDatos().put("string", "valor");
		b.getDatos().put("date", new Date());
		b.getDatos().put("integer", new Integer(111));
		beanColl.add(b);

//		try {
			return new JRDinamicBeanCollectionDataSource(beanColl, "datos");
/*		} catch (JRException e) {
			throw new RuntimeException(e);
		}*/
	}
	
	public static void main(String[] args) {
		logger.debug(HelperTest.createDataSourceReservaDTO().next());
	}
	
}
