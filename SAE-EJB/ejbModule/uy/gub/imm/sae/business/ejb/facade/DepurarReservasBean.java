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

import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.entity.global.Empresa;

@Stateless
@RolesAllowed("RA_AE_ANONIMO")
public class DepurarReservasBean  {
	private static Logger logger = Logger.getLogger(DepurarReservasBean.class);
	private static final int MINUTOS_BORRAR_RESERVAS_PENDIENTES = 10; 

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
	@Schedule(second="0", minute="*/30", hour="*", persistent=false)
	public void eliminarReservasPendientes(){
		
		try {
			//Obtener los identificadores de todas las empresas
			Query query =  globalEntityManager.createQuery("SELECT e FROM Empresa e WHERE fecha_baja IS NULL ");
			List<Empresa> empresas = query.getResultList();
			//Consulta para eliminar las disponibilidades ocupadas (sino no se puede borrar las reservas)
			String sql1 = "DELETE FROM {esquema}.ae_reservas_disponibilidades rd WHERE EXISTS (SELECT 1 FROM {esquema}.ae_reservas r "
					+ "WHERE r.id=rd.aers_id AND r.estado='P' AND "
					+ "(EXTRACT(DAY FROM (CURRENT_TIMESTAMP - r.fcrea))*24*60 + EXTRACT(HOUR FROM (CURRENT_TIMESTAMP - r.fcrea))*60 + EXTRACT(MINUTE FROM (CURRENT_TIMESTAMP - r.fcrea)))> :periodoBorrado)";
			//Consulta para eliminar las reservas
			String sql2 = "delete from {esquema}.ae_reservas r where r.estado='P' and "
					+ "(EXTRACT(DAY FROM (CURRENT_TIMESTAMP - r.fcrea))*24*60 + EXTRACT(HOUR FROM (CURRENT_TIMESTAMP - r.fcrea))*60 + EXTRACT(MINUTE FROM (CURRENT_TIMESTAMP - r.fcrea)))> :periodoBorrado";
			for(Empresa empresa : empresas) {
				if(empresa.getDatasource() != null) {
					try {
						String sql = sql1.replace("{esquema}", empresa.getDatasource());
						Query query1 = entityManager.createNativeQuery(sql);
						query1.setParameter("periodoBorrado", MINUTOS_BORRAR_RESERVAS_PENDIENTES);
						int cant = query1.executeUpdate();

						sql = sql2.replace("{esquema}", empresa.getDatasource());
						query1 = entityManager.createNativeQuery(sql);
						query1.setParameter("periodoBorrado", MINUTOS_BORRAR_RESERVAS_PENDIENTES);
						cant = query1.executeUpdate();
						
						logger.info("Se eliminaron " + cant + " reservas pendientes para la empresa "+empresa.getNombre()+" .... ");
					}catch(Exception ex) {
						//ex.printStackTrace();
						logger.error("No se pudo eliminar reservas pendientes para la empresa "+empresa.getNombre()+"(esquema '"+empresa.getDatasource()+"')");
					}
				}
			}
			
		} catch (Exception ex) {
		}
	}
	

}
