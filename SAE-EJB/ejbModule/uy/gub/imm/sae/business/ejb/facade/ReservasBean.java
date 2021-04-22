package uy.gub.imm.sae.business.ejb.facade;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.exception.AccesoMultipleException;

@Stateless
@RolesAllowed({"RA_AE_FCALL_CENTER","RA_AE_PLANIFICADOR", "RA_AE_ADMINISTRADOR","RA_AE_ANONIMO","RA_AE_FATENCION", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
public class ReservasBean implements Reservas {

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public void modificarEstadoReserva(ReservaDTO reserva) throws AccesoMultipleException {

		List<Disponibilidad>  disponibilidades = (List<Disponibilidad>) em.createQuery(
				"select d from  Disponibilidad d join d.reservas reserva where reserva.id = :r")
				.setParameter("r", reserva.getId().intValue()).getResultList();;
		
		Disponibilidad disponibilidad = disponibilidades.get(0);
		
		disponibilidad = em.find(Disponibilidad.class, disponibilidad.getId());
		
		try {
			disponibilidad.setNumerador(disponibilidad.getNumerador()+1);
			em.flush();
		} catch(OptimisticLockException e){
			throw new AccesoMultipleException("error_de_acceso_concurrente");
		}
		
		Reserva r = em.find(Reserva.class, reserva.getId());
		r.setEstado(Estado.U);
		r.setOrigen(reserva.getOrigen());
		
		em.flush();
		
	}
}
