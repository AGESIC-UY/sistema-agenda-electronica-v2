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

package uy.gub.imm.sae.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Utiles {

  //Se inicializa en el bloque estático debajo
  public static final Calendar FECHA_INVALIDA;

  //Formato de la fecha generada por Date.toString() para poder volver a comvertirlo
  private static final DateFormat FORMATEADOR_BASICO_FECHA = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
  private static final DateFormat FORMATEADOR_BASICO_FECHA_INGLES = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
  
  
	public static Integer DIA = 1;
	public static Integer DIA_HORA = 2;
	public static Integer HORA = 3;
	public static Integer HORA_SEGUNDOS = 4;
	public static Integer DIA_HORA_SEGUNDOS = 5;
	
  static{
    Calendar gc = new GregorianCalendar();
    gc.setTimeZone(TimeZone.getTimeZone("GMT"));
    gc.set(Calendar.YEAR, 1);
    gc.set(Calendar.DAY_OF_YEAR, 1);
    gc.set(Calendar.HOUR_OF_DAY, 0);
    gc.set(Calendar.MINUTE, 0);
    gc.set(Calendar.SECOND, 0);
    gc.set(Calendar.MILLISECOND, 0);
    FECHA_INVALIDA = gc;
  }
  
	public static Date time2InicioDelDia (Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static void time2InicioDelDia (Calendar fecha) {
		fecha.set(Calendar.HOUR_OF_DAY, 0);
		fecha.set(Calendar.MINUTE, 0);
		fecha.set(Calendar.SECOND, 0);
		fecha.set(Calendar.MILLISECOND, 0);
	}

	public static Date time2FinDelDia (Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}
	
	public static Date stringToDate(String sFecha) throws ParseException {
	  try {
	    //Se intenta parsear la fecha en el idioma por defecto de la instalación
	    return FORMATEADOR_BASICO_FECHA.parse(sFecha);
	  }catch(Exception ex) {
	    //Si no se puede, se intenta parsearla en inglés (el calendar siempre la devuelve en inglés)
	    return FORMATEADOR_BASICO_FECHA_INGLES.parse(sFecha);
	  }
	}
	
	public static String date2string(Date fecha, Integer formato) {
		Calendar fecha2 = Calendar.getInstance();
		String resp = null;
		fecha2.setTime(fecha);

		String hora = Integer.toString(fecha2.get(Calendar.HOUR_OF_DAY));
		if (hora.length()==1){
			hora = "0" + hora;
		}
		String minutos = Integer.toString(fecha2.get(Calendar.MINUTE));
		if (minutos.length()==1){
			minutos = "0" + minutos;
		}
		String dia = Integer.toString(fecha2.get(Calendar.DATE));
		if (dia.length()==1){
			dia = "0" + dia;
		}
		String mes = Integer.toString(fecha2.get(Calendar.MONTH)+1);
		if (mes.length()==1){
			mes = "0" + mes;
		}
		String anio = Integer.toString(fecha2.get(Calendar.YEAR));
		String segundos = Integer.toString(fecha2.get(Calendar.SECOND));
		if (segundos.length()==1){
			segundos = "0" + segundos;
		}

		if (DIA.equals(formato)){
			resp = dia +"/"+mes+"/"+anio;
		}
		if (DIA_HORA.equals(formato)){
			resp = dia +"/"+mes+"/"+anio+" "+hora+":"+minutos;
		}
		if (DIA_HORA_SEGUNDOS.equals(formato)){
			resp = dia +"/"+mes+"/"+anio+" "+hora+":"+minutos+":"+segundos;
		}
		if (HORA.equals(formato)){
			resp = hora+":"+minutos;
		}
		if (HORA_SEGUNDOS.equals(formato)){
			resp = hora+":"+minutos+":"+segundos;
		}
		return resp;
	}
	
	/**
	 * Devuelve un objeto Date que representa una fecha inválida (con año = 1)
	 * @return
	 */
  public static final Date getFechaInvalida() {
    return FECHA_INVALIDA.getTime();
  }
  
  /**
   * Determina si la fecha indicada es inválida (por convención una fecha no nula pero inválida 
   * se representa con año = 1)
   * @param fecha
   * @return
   */
  public static final boolean esFechaInvalida(Date fecha) {
    if(fecha==null) {
      return false;
    }
    Calendar cFecha = new GregorianCalendar();
    cFecha.setTime(fecha);
    return cFecha.get(Calendar.YEAR) == FECHA_INVALIDA.get(Calendar.YEAR);
  }
  

}
