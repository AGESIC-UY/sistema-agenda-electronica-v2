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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;

import uy.gub.imm.sae.entity.Plantilla;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.UserException;

@Stateless
public class DepuracionReservasBean implements DepuracionReservas, DepuracionReservasRemote{
	
	
	private static Logger logger = Logger.getLogger(DepuracionReservasBean.class);
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;
	
	

	
	// Consulta para eliminar las disponibilidades ocupadas por reservas
    // individuales
    private static String sql1 = "DELETE FROM {esquema}.ae_reservas_disponibilidades rd WHERE rd.aedi_id IN ("
                    					+ "SELECT d.id FROM {esquema}.ae_disponibilidades d WHERE d.aere_id=:idRecurso) AND rd.aers_id IN ("
                						+ "SELECT r.id FROM {esquema}.ae_reservas r WHERE r.estado='P' AND r.aetr_id IS NULL AND r.fcrea<:fechaLimite)";

    // Consulta para eliminar los datos asociados a las reservas inviduales
    private static String sql2 = "DELETE FROM {esquema}.ae_datos_reserva dr WHERE dr.aers_id IN ( :idsReservas )";

    // Consulta para eliminar las reservas individuales
    private static String sql3 = "DELETE FROM {esquema}.ae_reservas r WHERE r.id IN ( :idsReservas )";

    // Consulta para eliminar las disponibilidades ocupadas por reservas
    // múltiples
    private static String sql4 = "DELETE FROM {esquema}.ae_reservas_disponibilidades rd WHERE rd.aers_id IN ( :idsReservasMultiples )";

    // Consulta para eliminar los datos asociados a las reservas múltiples
    private static String sql5 = "DELETE FROM {esquema}.ae_datos_reserva d WHERE d.aers_id IN ( :idsReservasMultiples )";

    // Consultas para eliminar las reservas múltiples pertenecientes a
    // tokens vencidos
    private static String sql6 = "DELETE FROM {esquema}.ae_reservas r WHERE r.id IN ( :idsReservasMultiples )";

    // Consulta para eliminar los tokens de reserva múltiple
    private static String sql7 = "DELETE FROM {esquema}.ae_tokens_reservas t WHERE t.aere_id=:idRecurso AND  t.estado = 'P' AND "
                    + "((t.ultima_reserva IS NULL AND t.fecha_inicio<:fechaLimite) or (t.ultima_reserva IS NOT NULL AND t.ultima_reserva<:fechaLimite))";
	
	
	
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value=15, unit=TimeUnit.MINUTES)
	public void eliminarReservas(Empresa empresa, Calendar fechaLimiteIndividual,Calendar fechaLimiteMultiple,Integer idRecurso,
								 List<Integer> idsReservaIndividuales, List<Integer> idsReservaMultiples)  {
		int cant = 0;
		
		
		try{
			// TODO Auto-generated method stub
			
			 // Eliminación de reservas individuales
	
			String sql = sql1.replace("{esquema}", empresa.getDatasource());
			Query query1 = entityManager.createNativeQuery(sql);
	        query1.setParameter("fechaLimite", fechaLimiteIndividual.getTime(), TemporalType.TIMESTAMP);
	        query1.setParameter("idRecurso", idRecurso);
	        query1.executeUpdate();
	
	        if (!idsReservaIndividuales.isEmpty()) {
	            sql = sql2.replace("{esquema}", empresa.getDatasource());
	            query1 = entityManager.createNativeQuery(sql);
	            query1.setParameter("idsReservas", idsReservaIndividuales);
	            query1.executeUpdate();
	
	            sql = sql3.replace("{esquema}", empresa.getDatasource());
	            query1 = entityManager.createNativeQuery(sql);
	            query1.setParameter("idsReservas", idsReservaIndividuales);
	            cant = query1.executeUpdate();
	        }
	
	        // Eliminación de reservas múltiples
	        if (!idsReservaMultiples.isEmpty()) {
		        sql = sql4.replace("{esquema}", empresa.getDatasource());
		        query1 = entityManager.createNativeQuery(sql);
		        //query1.setParameter("fechaLimite", fechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
		        //query1.setParameter("idRecurso", idRecurso);
		        query1.setParameter("idsReservasMultiples", idsReservaMultiples);
		        query1.executeUpdate();
		
		        sql = sql5.replace("{esquema}", empresa.getDatasource());
		        query1 = entityManager.createNativeQuery(sql);
		        //query1.setParameter("fechaLimite", fechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
		        //query1.setParameter("idRecurso", idRecurso);
		        query1.setParameter("idsReservasMultiples", idsReservaMultiples);
		        query1.executeUpdate();
		
		        sql = sql6.replace("{esquema}", empresa.getDatasource());
		        query1 = entityManager.createNativeQuery(sql);
		        //query1.setParameter("fechaLimite", fechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
		        //query1.setParameter("idRecurso", idRecurso);
		        query1.setParameter("idsReservasMultiples", idsReservaMultiples);
		        cant = cant + query1.executeUpdate();
		        
		        sql = sql7.replace("{esquema}", empresa.getDatasource());
		        query1 = entityManager.createNativeQuery(sql);
		        query1.setParameter("fechaLimite", fechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
		        query1.setParameter("idRecurso", idRecurso);
		        query1.executeUpdate();
	        
	        }
	        
	
	        logger.debug("Se eliminaron " + cant + " reservas pendientes para el recurso con ID: " + idRecurso.toString() + " la empresa " + empresa.getNombre() + " .... ");
		}
		catch(Exception aex){
			logger.info("No se pudo eliminar reservas pendientes para el recurso con ID:  " + idRecurso.toString() + " de la empresa " + empresa.getNombre() + "(esquema '"
                    + empresa.getDatasource() + "')", aex);
		}
		
		
	}






	

}
