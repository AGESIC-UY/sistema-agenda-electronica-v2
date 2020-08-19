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

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Llamada;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;

@Stateless
@RolesAllowed({"RA_AE_ADMINISTRADOR","RA_AE_PLANIFICADOR","RA_AE_FATENCION"})
public class LlamadasHelperBean implements LlamadasHelperLocal {

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager em;

	static Logger logger = Logger.getLogger(LlamadasHelperBean.class);
	
	
	/**
	 * Persiste una nueva llamdada y marca la reserva como utilizada,
	 * realiza mutua exclusion sobre la reserva utilizando bloqueo optimista con @ Version, 
	 * Se asume que no existe otra llamada para la misma reserva
	 * (unicidad sobre la clave foranea de Reserva).
	 * Realiza todo en una transacción independiente. 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Reserva hacerLlamadaMutex(Integer recursoId, Integer reservaId, Integer puesto) {
		Recurso recurso = em.find(Recurso.class, recursoId);
		Reserva reserva = em.find(Reserva.class, reservaId);

		if (reserva.getEstado().equals(Estado.R)) {
			
			Llamada ll = buildLlamada(recurso, reserva, puesto);
	
			reserva.setEstado(Estado.U);
			em.flush();
			em.persist(ll); //Despues del flush pues si no salta unicidad en lugar de lockeo optimista

			reserva.getDatosReserva().size(); //Lazy initialization
			reserva.getDisponibilidades().size(); //Lazy initialization
			return reserva;
		} 
		else {
			return null;
		}
	}

	
	/**
	 * Persiste una llamada para una reserva que ya fue llamada, es decir, es un reintento.
	 * Realiza mutua exclusion sobre la llamada en el sentido que si se realiza la misma llamada dara violacion de integridad referencial
	 * pues no puden haber 2 llamadas para una misma reserva. 
	 * Por lo que asume que puede existir una llamada para esta reserva, la que debe ser borrada antes de persistir la nueva llamada.
	 * Realiza todo en una transacción independiente. 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Reserva reintentarLlamadaMutex(Integer recursoId, Integer reservaId, Integer puesto) {

		Recurso recurso = em.find(Recurso.class, recursoId);
		Reserva reserva = em.find(Reserva.class, reservaId);

		//Como la reserva tiene @Version, utilizara bloqueo optimista.
		//Debido a que en realidad no modifico la reserva, necesito hacer un lock explicito para asegurar mutua exlusion en la llamada.
		//es decir, para forzar al control de optimistic lockin sobre la reserva.
		//TODO se comenta porque explota queda pendiente de revisión, excepcion: entity not in the persistence context
		em.lock(reserva, LockModeType.WRITE);
		
		if (reserva.getEstado().equals(Estado.U)) {
			
			//Busco y borro la llamada previa, si existe aún.
			Llamada anteriorLlamada = (Llamada)em.createQuery(
					"select ll " +
					"from   Llamada ll " +
					"where   " +
					"       ll.reserva.id = :reserva "
					)
					.setParameter("reserva", reservaId)
					.getSingleResult();		
			
			if (anteriorLlamada != null) {
				Llamada llamadaABorrar = null;
				if (!em.contains(anteriorLlamada)) {
					llamadaABorrar = em.merge(anteriorLlamada);
				}else {
					llamadaABorrar = anteriorLlamada;
				}
				em.remove(llamadaABorrar);		
				em.flush();
			}
			
			Llamada ll = buildLlamada(recurso, reserva, puesto);
			em.persist(ll);
			em.flush();

			reserva.getDatosReserva().size(); //Lazy initialization
			reserva.getDisponibilidades().size(); //Lazy initialization
			return reserva;
		} 
		else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Llamada buildLlamada(Recurso recurso, Reserva reserva, Integer puesto) {
	
		Llamada ll = new Llamada();
		ll.setReserva(reserva);
		ll.setRecurso(recurso);
		ll.setFecha(new Date());
		ll.setHora(new Date());
		ll.setNumero(reserva.getNumero());
		ll.setPuesto(puesto);
		//ObtenER los datos solicitados para la reserva que deben desplegarse en la pantalla llamadora
		List<Object[]> datos = (List<Object[]>) em.createQuery("SELECT dr.datoASolicitar.incluirEnLlamador, dr.datoASolicitar.largoEnLlamador, dr.valor " +
			"FROM DatoReserva dr " +
			"WHERE dr.reserva = :reserva " +
			"ORDER BY dr.datoASolicitar.ordenEnLlamador ")
			.setParameter("reserva", reserva)
			.getResultList();
		String etiqueta = "";
		for (Object[] row : datos) {
			Boolean incluir = (Boolean)row[0];
			Integer largo = (Integer)row[1];
			String valor = (String)row[2];
			if (incluir) {
				if (valor.length() <= largo) {
					etiqueta += valor;
					etiqueta += " ";
				} else {
					etiqueta += valor.substring(0,largo);
					etiqueta += ". ";
				}
			}
		}
		if (etiqueta.equals("")) {
			ll.setEtiqueta("---");
		} else {
			ll.setEtiqueta(etiqueta);
		}
		return ll;
	}
	
}
