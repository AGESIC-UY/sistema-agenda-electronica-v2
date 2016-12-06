package uy.gub.imm.sae.business.ejb.facade;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;

/**
 * Session Bean implementation class DisponibilidadesSingleton
 */
@Singleton
@LocalBean
public class DisponibilidadesSingleton {

  public DisponibilidadesSingleton() {
  }

  /**
   * Obtiene de la base de datos la instancia de Disponibilidad presencial correspondiente al recurso para la fecha de hoy.
   * Si no existe, crea una y lo almacena (esta es la razón para hacerlo en un Singleton, de forma de que no se creen nunca dos para el mismo día).
   * @param entityManager
   * @param recurso
   * @param timezone
   * @return
   */
  public Disponibilidad obtenerDisponibilidadPresencial(EntityManager entityManager, Recurso recurso, TimeZone timezone) {
    if(recurso==null || recurso.getId()==null) {
      return null;
    }
    
    recurso = entityManager.find(Recurso.class, recurso.getId());
    
    if(recurso==null || recurso.getPresencialAdmite()==null || !recurso.getPresencialAdmite().booleanValue()) {
      return null;
    }
    
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.MILLISECOND, timezone.getOffset((new Date()).getTime()));
    Date ahora = cal.getTime();
    try {
      Query query = entityManager.createQuery(
          "SELECT d "
          + "FROM Disponibilidad d "
          + "WHERE d.presencial=true "
          + "AND d.recurso.id=:recursoId "
          + "AND d.fecha=:fecha");
      query = query.setParameter("recursoId", recurso.getId());
      query = query.setParameter("fecha", ahora, TemporalType.DATE);
      Disponibilidad disponibilidad = (Disponibilidad) query.getSingleResult();
      //Existe una disponibilidad para hoy
      return disponibilidad;
    }catch(NoResultException nrEx) {
      //No existe aún una disponibilidad presencial para hoy en el recurso indicado, se crea
      Disponibilidad disponibilidad = new Disponibilidad();
      disponibilidad.setFecha(ahora);
      disponibilidad.setCupo(recurso.getPresencialCupos());
      disponibilidad.setRecurso(recurso);
      disponibilidad.setHoraInicio(ahora);
      disponibilidad.setHoraFin(ahora);
      disponibilidad.setPresencial(true);
      entityManager.persist(disponibilidad);
      entityManager.flush();
      entityManager.refresh(disponibilidad);
      return disponibilidad;
    }
  }
  
}
