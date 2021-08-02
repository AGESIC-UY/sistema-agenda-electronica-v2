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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;

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
    
    @EJB(mappedName="java:global/sae-1-service/sae-ejb/DepuracionReservasBean!uy.gub.imm.sae.business.ejb.facade.DepuracionReservasRemote")
    private DepuracionReservas depuracionReservasEJB;
    
    
    private static final long LOCK_ID_REINTENTAR = 1818181818;
    
    /*
     * Elimina todas las Reservas que estan en estado Pendiente desde un periodo
     * de tiempo ya transcurrido
     */

    @SuppressWarnings("unchecked")
    @Schedule(second = "0", minute = "*/5", hour = "*", persistent = false)
    @TransactionTimeout(value=60, unit=TimeUnit.MINUTES)
    public void eliminarReservasPendientes() {
    	
    	try{
    	
    	  //Intentar liberar el lock por si lo tiene esta instancia
	      boolean lockOk = (boolean)globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+")").getSingleResult();
	      //Intentar obtener el lock
	      lockOk = (boolean)globalEntityManager.createNativeQuery("SELECT pg_try_advisory_lock("+LOCK_ID_REINTENTAR+")").getSingleResult();
	      if(!lockOk) {
	        //Otra instancia tiene el lock
	        logger.info("No se ejecuta el reintento de depuración de reservas porque hay otra instancia haciéndolo.");
	        return;
	      }
	      //No hay otra instancia con el lock, se continúa
	      
	      logger.info("Se ejecuta el reintento de depuración de reservas.");
	      
    		
	        // Obtener los identificadores de todas las empresas
	        Query query = globalEntityManager.createQuery("SELECT e FROM Empresa e WHERE fecha_baja IS NULL ");
	        List<Empresa> empresas = query.getResultList();
	        
	    	String idsRecursosIndividuales = "SELECT r.id FROM {esquema}.ae_reservas r JOIN {esquema}.ae_reservas_disponibilidades rd ON rd.aers_id=r.id "
	                + "JOIN {esquema}.ae_disponibilidades d ON d.id=rd.aedi_id  WHERE d.aere_id=:idRecurso AND r.estado='P' AND r.aetr_id IS NULL AND r.fcrea<:fechaLimite ";
	
	    	String idsRecursosMultiples = "SELECT r.id FROM {esquema}.ae_reservas r JOIN {esquema}.ae_tokens_reservas t ON t.id=r.aetr_id "
	                + " WHERE t.aere_id=:idRecurso "
	                + " AND t.estado = 'P' AND ((t.ultima_reserva IS NULL AND t.fecha_inicio<:fechaLimite) or (t.ultima_reserva IS NOT NULL AND t.ultima_reserva<:fechaLimite))";

	        for (Empresa empresa : empresas) {
	            if (empresa.getDatasource() != null) {
	                // Obtener los recursos por empresa
	                Query queryRecursos = globalEntityManager.createNativeQuery("SELECT id,reserva_pen_tiempo_max,reserva_multiple_pend_tiempo_max FROM  "
	                                + empresa.getDatasource() + ".ae_recursos WHERE fecha_baja IS NULL ");
	                List<Object[]> recursos = queryRecursos.getResultList();
	                if (recursos != null && !recursos.isEmpty()) {
	                    for (Object[] recurso : recursos) {
	                        Integer idRecurso = (Integer) recurso[0];
	                        Integer tiempoMaxIndividual = null;
	                        if (recurso[1] != null) {
	                            tiempoMaxIndividual = (Integer) recurso[1];
	                        }
	
	                        Integer tiempoMaxMultiple = null;
	                        if (recurso[2] != null) {
	                            tiempoMaxMultiple = (Integer) recurso[2];
	                        }
	
	                        logger.debug("EMPRESA ID: " + empresa.getId() + "..." + "RECURSO ID: " + idRecurso);
	                        int tiempoMaximoIndividual = 10;
	                        int tiempoMaximoMultiple = 2880;
	
	                        try {
	                            // Determinar el tiempo máximo que puede estar una
	                            // reserva pendiente sin confirmación
	                            query = globalEntityManager.createNativeQuery("SELECT clave, valor FROM global.ae_configuracion c WHERE c.clave IN "
	                                            + "('RESERVA_PENDIENTE_TIEMPO_MAXIMO','RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO')");
	                            List<Object[]> valores = query.getResultList();
	                            for (Object[] valor : valores) {
	                                if ("RESERVA_PENDIENTE_TIEMPO_MAXIMO".equals(valor[0])) {
	                                    try {
	                                        tiempoMaximoIndividual = Integer.valueOf(valor[1].toString());
	                                    } catch (Exception ex) {
	                                        // Nada para hacer, valor por defecto
	                                    }
	                                } else if ("RESERVA_MULTIPLE_PENDIENTE_TIEMPO_MAXIMO".equals(valor[0])) {
	                                    try {
	                                        tiempoMaximoMultiple = Integer.valueOf(valor[1].toString());
	                                    } catch (Exception ex) {
	                                        // Nada para hacer, valor por defecto
	                                    }
	                                }
	                            }
	                        } catch (Exception ex) {
	                            logger.error("No se pudo eliminar reservas pendientes.", ex);
	                        }
	
	                        if (tiempoMaxIndividual != null && tiempoMaxIndividual > 0) {
	                            logger.debug("RECURSO ID: " + idRecurso + " TIEMPO MAX INDIVIDUAL " + tiempoMaxIndividual + " .... ");
	                            tiempoMaximoIndividual = tiempoMaxIndividual;
	
	                        }
	
	                        if (tiempoMaxMultiple != null && tiempoMaxMultiple > 0) {
	                            logger.debug("RECURSO ID: " + idRecurso + " TIEMPO MAX MULTIPLE " + tiempoMaxMultiple + " .... ");
	                            tiempoMaximoMultiple = tiempoMaxMultiple;
	                        }
	
	                        Calendar cFechaLimiteIndividual = new GregorianCalendar();
	                        cFechaLimiteIndividual.add(Calendar.MINUTE, tiempoMaximoIndividual * -1);
	
	                        Calendar cFechaLimiteMultiple = new GregorianCalendar();
	                        cFechaLimiteMultiple.add(Calendar.MINUTE, tiempoMaximoMultiple * -1);
	
	                        try {
	                        	
	                        	
	                	        // Consulta de ids de reservas individuales a
	                	        // eliminar
	                	        List<Integer> idsReservaInd = new ArrayList<>();
	                	        String sql = idsRecursosIndividuales.replace("{esquema}", empresa.getDatasource());
	                	        Query query1 = entityManager.createNativeQuery(sql);
	                	        query1.setParameter("fechaLimite", cFechaLimiteIndividual.getTime(), TemporalType.TIMESTAMP);
	                	        query1.setParameter("idRecurso", idRecurso);
	                	        idsReservaInd = (List<Integer>) query1.getResultList();
	                	        
	                	        
	                	        // Consulta de ids de reservas multiples a
	                	        // eliminar
	                	        List<Integer> idsReservaMul = new ArrayList<>();
	                	        sql = idsRecursosMultiples.replace("{esquema}", empresa.getDatasource());
	                	        query1 = entityManager.createNativeQuery(sql);
	                	        query1.setParameter("fechaLimite", cFechaLimiteMultiple.getTime(), TemporalType.TIMESTAMP);
	                	        query1.setParameter("idRecurso", idRecurso);
	                	        idsReservaMul = (List<Integer>) query1.getResultList();
	                        	
	                	        
	                        	depuracionReservasEJB.eliminarReservas(empresa, cFechaLimiteIndividual,cFechaLimiteMultiple, idRecurso, idsReservaInd,idsReservaMul);
	                           
	                        } catch (Exception ex) {
	                            logger.error("No se pudo eliminar reservas pendientes para la empresa " + empresa.getNombre() + "(esquema '"
	                                            + empresa.getDatasource() + "')", ex);
	                        }
	                    }
	                }
	
	            }
	        }
	        
	        logger.info("Finalizo la depuración de reservas.");
	        
    	}finally {
	        //Intentar liberar el lock (si lo tiene esta instancia)
	        globalEntityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID_REINTENTAR+")").getSingleResult();
	        logger.info("Ejecución del reintento de depuración de reservas finalizada.");
      }

    }

}
