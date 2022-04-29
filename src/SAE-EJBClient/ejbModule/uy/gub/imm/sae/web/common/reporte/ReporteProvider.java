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

package uy.gub.imm.sae.web.common.reporte;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class ReporteProvider {

	private static final String CONTENT_TYPE_PDF = "application/pdf";
	
	
	public static JasperPrint generarReporteDinamico(
			InputStream reporteJrxml, 
			List<Columna> defColumnas, 
			String atributoCamposDinamicos,
			Map<String,?> params, 
			List<?> datos) 
			throws ReporteException {

		try{
			JasperDesign reportDesign = JRXmlLoader.load(reporteJrxml);
			
			agregarColumnas(reportDesign, defColumnas);
			JasperReport report = JasperCompileManager.compileReport(reportDesign);
			JRDinamicBeanCollectionDataSource dataSource = new JRDinamicBeanCollectionDataSource(datos, atributoCamposDinamicos);
			JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);
			
			//return JasperExportManager.exportReportToPdf(print);
			return print;
			
		} catch (Exception e) {
			throw new ReporteException("JasperReportsProvider_getReportAsByteArray_JR", e);
		}
	}
	
//	public static byte[] generarReporteDinamico(
//			InputStream reporteJrxml, 
//			List<Columna> defColumnas, 
//			String atributoCamposDinamicos,
//			Map<String,?> params, 
//			List<?> datos) 
//			throws ReporteException {
//
//		try{
//			JasperDesign reportDesign = JRXmlLoader.load(reporteJrxml);
//			
//			agregarColumnas(reportDesign, defColumnas);
//			JasperReport report = JasperCompileManager.compileReport(reportDesign);
//			JRDinamicBeanCollectionDataSource dataSource = new JRDinamicBeanCollectionDataSource(datos, atributoCamposDinamicos);
//			JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);
//			
//			return JasperExportManager.exportReportToPdf(print);
//			
//		} catch (Exception e) {
//			throw new ReporteException("JasperReportsProvider_getReportAsByteArray_JR", e);
//		}
//	}

	/**
	 * Modifica el diseño del reporte agregando las columnas indicadas en el ultimo grupo que tenga el reporte.
	 * Asume que tiene definido almenos un grupo.
	 */
	private static void agregarColumnas(JasperDesign reportDesign, List<Columna> columnas) throws JRException {
		
		//Obtengo el ultimo grupo
		int cant = reportDesign.getGroupsList().size();
		JRDesignGroup grupo = (JRDesignGroup) reportDesign.getGroupsList().get(cant-1);
		//JRDesignBand grupoHeader = (JRDesignBand) grupo.getGroupHeaderSection();
		//JRDesignBand detail = (JRDesignBand) reportDesign.getDetailSection();
		JRDesignBand grupoHeader = (JRDesignBand) grupo.getGroupHeader();
		JRDesignBand detail = (JRDesignBand) reportDesign.getDetail();

		int x = 70;
		for (Columna col: columnas) {
		
			//Agrego el nombre de la columna en la banda cabezal del grupo
			JRDesignTextField designColNombre = new JRDesignTextField();
			JRDesignExpression exp = new JRDesignExpression();
			exp.setText("\""+col.getNombre()+"\"");
			exp.setValueClass(String.class);
			designColNombre.setKey("col_"+col.getId());
			designColNombre.setExpression(exp);
			designColNombre.setHeight(13);
			designColNombre.setWidth(col.getAncho());
			designColNombre.setX(x);
			designColNombre.setY(21);
			designColNombre.setStretchWithOverflow(true);
			grupoHeader.addElement(designColNombre);
			
			//Agrego la definicion del campo de donde se tomara el valor
			JRDesignField designField = new JRDesignField();
			
			String nombreCampo = null;
			
			//Esto es por si hay campos con el mismo nombre que los hardcode (por ejemplo, "numero")
			int cont = 0;
			while(cont>=0) {
				nombreCampo = col.getId();
				try {
					if(cont>0) {
						nombreCampo = nombreCampo+cont;
					}
					designField.setName(nombreCampo);
					designField.setValueClass(col.getClase());
					reportDesign.addField(designField);
					cont=-1;
				}catch(Exception ex) {
					cont++;
				}
			}
			
			//Agrego la celda de la columna (valor) en la banda DETALLE
			JRDesignTextField designColCelda = new JRDesignTextField();
			JRDesignExpression expCelda = new JRDesignExpression();
			//expCelda.setText("$F{"+col.getId()+"}");
			expCelda.setText("$F{"+nombreCampo+"}");
			expCelda.setValueClass(col.getClase());
			designColCelda.setExpression(expCelda);
			designColCelda.setHeight(12);
			designColCelda.setWidth(col.getAncho());
			designColCelda.setX(x);
			designColCelda.setY(1);
			designColCelda.setPrintWhenDetailOverflows(true);
			designColCelda.setStretchWithOverflow(true);
			designColCelda.setBlankWhenNull(true);
			designColCelda.setHorizontalAlignment(JRDesignTextField.HORIZONTAL_ALIGN_LEFT);
			designColCelda.setPositionType(JRDesignTextField.POSITION_TYPE_FLOAT);
			designColCelda.setFontSize(8);
			designColCelda.setKey("celda_"+col.getId());
			detail.addElement(designColCelda);
			
			
			x += col.getAncho();
		}	
		
	}
	
	
	public static void exportarReporteComoPdf(HttpServletResponse response, List<JasperPrint> reportes, String nombre){
		try {
			JRPdfExporter exp = new JRPdfExporter();
      exp.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, reportes);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      exp.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, bos);
      exp.exportReport();
      byte[] bytes = bos.toByteArray();
			
			response.setContentType(CONTENT_TYPE_PDF);
			response.setContentLength(bytes.length);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=\""+nombre+"\"");
			ServletOutputStream out = response.getOutputStream();
			out.write(bytes);
			out.flush();
			out.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}

}
