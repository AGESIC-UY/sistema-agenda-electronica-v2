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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;


public class JRDinamicBeanCollectionDataSource implements JRDataSource {

   private static final String EXCEPCION_ROWS_NULL = "La lista de objetos pasada al constructor es nula.";
   private static final String EXCEPCION_MENSAJE_NO_HAY_FILA_SELECCIONADA = "No hay fila seleccionada, debe ejecutar next() antes de tratar de acceder a una fila";
   private static final String EXCEPCION_SE_ESPERABA_DATA_SOURCE = "Se esperaba un campo de tipo List<Object> para ser utilizada como DataSource en un subreporte";

   private String nombreAtributoDeDatosDinamicos;
   private List<Object> rows;
   private int currentRowIndex = -1; //Aun no se ha leido nada.
   
   
   /**
    * Crea un data source para jasperReport que obtiene los registros
    * de la lista que recibe por parametro.
    * 
    * @param registros: Fuente de datos que sera procesada (iterada) por el reporte.
    * @param nombreAtributoDeDatosDinamicos: Nombre del atributo que tendra un Map con los <campo,valor> para los campos dinamicos del registro.
    */
   @SuppressWarnings("unchecked")
   public JRDinamicBeanCollectionDataSource(List<?> registros, String nombreAtributoDeDatosDinamicos) {
	  
      rows = (List<Object>)registros;
      this.nombreAtributoDeDatosDinamicos = nombreAtributoDeDatosDinamicos;
   }

   public boolean next() {
      
      //Si aun quedan registros por leer.
      if (currentRowIndex + 1  < rows.size() ) {

    	  currentRowIndex++;
    	  return true;
      }
      else {
    	 
    	  return false;
      }
   }

   
   @SuppressWarnings("unchecked")
   public Object getFieldValue(JRField jrField) throws JRException {
      
		//Se debe ejecutar next() antes de tratar de acceder a la fila.
		if (currentRowIndex < 0) {
		   throw new JRException(EXCEPCION_MENSAJE_NO_HAY_FILA_SELECCIONADA);
		}
		  
		//Obtengo el valor del atributo indicado por reflexion sobre el objeto de la lista.
		Object value;
		try {
			value = getAttributeValueByReflexion(rows.get(currentRowIndex), jrField.getName(), jrField.getValueClassName());
		} catch (NoSuchMethodException e) {
			//Se trata de un campo dinamico
			Map<String, Object> values;
			try {
				values = (Map<String, Object>)getAttributeValueByReflexion(rows.get(currentRowIndex), nombreAtributoDeDatosDinamicos, "java.util.Map");
			} catch (NoSuchMethodException e1) {
				throw new JRException(e1);
			}
			value = values.get(jrField.getName());
		}

		//CASO PARTICULAR: Obtener un DataSource
		//Es la forma de pasar data sources a los subreportes.
		if (jrField.getValueClassName().equals("net.sf.jasperreports.engine.JRDataSource")) {
			if (value instanceof List ){
				value = new JRDinamicBeanCollectionDataSource((List<Object>)value, nombreAtributoDeDatosDinamicos);
			}
			else {
				throw new JRException(EXCEPCION_SE_ESPERABA_DATA_SOURCE);
			}
		}
      
		return value;
   }
   
   private Object getAttributeValueByReflexion(Object row, String atributeName, String atributeClazz) throws JRException, NoSuchMethodException {

	   
	   Object value = null;
	      
	   String prefijo = "get";
	   
	   if (atributeClazz.equals("java.lang.Boolean")) {
		   prefijo = "is";
	   }

	   try {
		   Method metodo;
		   String nombreMetodo = prefijo + atributeName.substring(0, 1).toUpperCase() + atributeName.substring(1);
		   metodo = row.getClass().getMethod(nombreMetodo, new Class [0]);
		   value = metodo.invoke(row, new Object[0]);
       } catch (IllegalAccessException e) {
    	   throw new JRException(e);
       } catch (InvocationTargetException e) {
    	   throw new JRException(e);
       }

       return value;
   }
   
}
