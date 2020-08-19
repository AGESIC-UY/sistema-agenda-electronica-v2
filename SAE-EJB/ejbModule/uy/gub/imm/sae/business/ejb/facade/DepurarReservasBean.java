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

package uy.gub.imm.sae.business.ejb.facade;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.entity.global.Empresa;

@Stateless
@RolesAllowed("RA_AE_ANONIMO")
public class DepurarReservasBean  {
	private static Logger logger = Logger.getLogger(DepurarReservasBean.class);

	@PersistenceContext(unitName = "AGENDA-GLOBAL")
  private EntityManager globalEntityManager;
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;

	@Resource
	private SessionContext ctx;

	/* Elimina todas las Reservas que estan en estado
	 * Pendiente desde un periodo de tiempo ya transcurrido
	 */ 
	
	@SuppressWarnings("unchecked")
	@Schedule(second="0", minute="*/5", hour="*", persistent=false)
	public void eliminarReservasPendientes(){
		
		try {
		  //Determinar el tiempo máximo que puede estar una reserva pendiente sin confirmación
		  Query query =  globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN "
		      + "('RESERVA_PENDIENTE_TIEMPO_MAXIMO','RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO')");
		  List<Object[]> valores = query.getResultList();
		  int tiempoMaximoIndividual = 10;
      int tiempoMaximoMultiple = 2880;
      for(Object[] valor : valores) {
        if("RESERVA_PENDIENTE_TIEMPO_MAXIMO".equals(valor[0])) {
    		  try {
    		    tiempoMaximoIndividual = Integer.valueOf(valor[1].toString());
    		  }catch(Exception ex) {
            //Nada para hacer, valor por defecto
    		  }
        }else if("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO".equals(valor[0])) {
          try {
            tiempoMaximoMultiple = Integer.valueOf(valor[1].toString());
          }catch(Exception ex) {
            //Nada para hacer, valor por defecto
          }
        }
      }
		  
      Calendar cFechaLimiteIndividual = new GregorianCalendar();
      cFechaLimiteIndividual.add(Calendar.MINUTE, tiempoMaximoIndividual * -1);
      
      Calendar cFechaLimiteMultiple = new GregorianCalendar();
      cFechaLimiteMultiple.add(Calendar.MINUTE, tiempoMaximoMultiple * -1);
      
			//Obtener los identificadores de todas las empresas
			query =  globalEntityManager.createQuery("SELECT e FROM Empresa e WHERE fecha_baja IS NULL ");
			List<Empresa> empresas = query.getResultList();
			
			//Consulta para eliminar las disponibilidades ocupadas por reservas individuales
      String sql1 = "DELETE FROM {esquema}.ae_reservas_disponibilidades rd WHERE rd.aers_id IN ("
          + "SELECT r.id FROM {esquema}.ae_reservas r WHERE r.estado='P' AND r.aetr_id IS NULL AND r.fcrea<:fechaLimite)";
			
      //Consulta para eliminar los datos asociados a las reservas inviduales 
      String sql2 = "DELETE FROM {esquema}.ae_datos_reserva dr WHERE dr.aers_id IN ("
          + "SELECT r.id FROM {esquema}.ae_reservas r WHERE r.estado='P' AND r.aetr_id IS NULL AND r.fcrea<:fechaLimite)";
			
			//Consulta para eliminar las reservas individuales
			String sql3 = "DELETE FROM {esquema}.ae_reservas r WHERE r.estado='P' AND r.aetr_id IS NULL AND r.fcrea<:fechaLimite";
			
      //Consulta para eliminar las disponibilidades ocupadas por reservas múltiples
      String sql4 = "DELETE FROM {esquema}.ae_reservas_disponibilidades rd WHERE rd.aers_id IN ( "
        + " SELECT r.id FROM {esquema}.ae_reservas r JOIN {esquema}.ae_tokens_reservas t ON t.id=r.aetr_id " 
        + " WHERE t.estado = 'P' AND ((t.ultima_reserva IS NULL AND t.fecha_inicio<:fechaLimite) or (t.ultima_reserva IS NOT NULL AND t.ultima_reserva<:fechaLimite)))";
      
			//Consulta para eliminar los datos asociados a las reservas múltiples 
      String sql5 = "DELETE FROM {esquema}.ae_datos_reserva d WHERE d.aers_id IN ( "
        + " SELECT r.id FROM {esquema}.ae_reservas r JOIN {esquema}.ae_tokens_reservas t ON t.id=r.aetr_id " 
        + " WHERE t.estado = 'P' AND ((t.ultima_reserva IS NULL AND t.fecha_inicio<:fechaLimite) or (t.ultima_reserva IS NOT NULL AND t.ultima_reserva<:fechaLimite)))";
			
      //Consultas para eliminar las reservas múltiples pertenecientes a tokens vencidos
      String sql6 = "DELETE FROM {esquema}.ae_reservas r WHERE r.aetr_id IN ( "
        + "SELECT t.id FROM {esquema}.ae_tokens_reservas t WHERE t.estado = 'P' AND "
        + "((t.ultima_reserva IS NULL AND t.fecha_inicio<:fechaLimite) or (t.ultima_reserva IS NOT NULL AND t.ultima_reserva<:fechaLimite)))";
			
      //Consulta para eliminar los tokens de reserva múltiple
      String sql7 = "DELETE FROM {esquema}.ae_tokens_reservas t WHERE t.estado = 'P' AND "
          + "((t.ultima_reserva IS NULL AND t.fecha_inicio<:fechaLimite) or (t.ultima_reserva IS NOT NULL AND t.ultima_reserva<:fechaLimite))";
      
      for(Empresa empresa : empresas) {
        if(empresa.getDatasource() != null) {
          try {
            int cant = 0;
            
            // Eliminación de reservas individuales
            
            String sql = sql1.replace("{esquema}", empresa.getDatasource());
            Query query1 = entityManager.createNativeQuery(sql);
            query1.setParameter("fechaLimite", cFechaLimiteIndividual.getTime(), TemporalType.TIMESTAMP);
            query1.executeUpdate();

            sql = sql2.replace("{esquema}", empresa.getDatasource());
            query1 = entityManager.createNativeQuery(sql);
            query1.setParameter("fechaLimite", cFechaLimiteIndividual.getTime(), TemporalType.TIMESTAMP);
            query1.executeUpdate();

            sql = sql3.replace("{esquema}", empresa.getDatasource());
            query1 = entityManager.createNativeQuery(sql);
            query1.setParameter("fechaLimite", cFechaLimiteIndividual.getTime(), TemporalType.TIMESTAMP);
            cant = query1.executeUpdate();

            // Eliminación de reservas múltiples
            
            sql = sql4.replace("{esquema}", empresa.getDatasource());
            query1 = entityManager.createNativeQuery(sql);
            query1.setParameter("fechaLimite", cFechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
            query1.executeUpdate();

            sql = sql5.replace("{esquema}", empresa.getDatasource());
            query1 = entityManager.createNativeQuery(sql);
            query1.setParameter("fechaLimite", cFechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
            query1.executeUpdate();
            
            sql = sql6.replace("{esquema}", empresa.getDatasource());
            query1 = entityManager.createNativeQuery(sql);
            query1.setParameter("fechaLimite", cFechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
            cant = cant + query1.executeUpdate();
            
            sql = sql7.replace("{esquema}", empresa.getDatasource());
            query1 = entityManager.createNativeQuery(sql);
            query1.setParameter("fechaLimite", cFechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
            query1.executeUpdate();
            
            logger.info("Se eliminaron " + cant + " reservas pendientes para la empresa "+empresa.getNombre()+" .... ");
          }catch(Exception ex) {
            logger.error("No se pudo eliminar reservas pendientes para la empresa "+empresa.getNombre()+"(esquema '"+empresa.getDatasource()+"')", ex);
          }
        }
      }
      
		} catch (Exception ex) {
		  logger.error("No se pudo eliminar reservas pendientes.", ex);
		}
	}
	
}
