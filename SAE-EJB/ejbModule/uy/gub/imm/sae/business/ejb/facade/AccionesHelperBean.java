package uy.gub.imm.sae.business.ejb.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.enumerados.Evento;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.Accion;
import uy.gub.imm.sae.entity.AccionPorDato;
import uy.gub.imm.sae.entity.AccionPorRecurso;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAccionException;
import uy.gub.sae.acciones.business.dto.RecursoDTO;
import uy.gub.sae.acciones.business.ejb.EjecutorAccion;
import uy.gub.sae.acciones.business.ejb.ErrorAccion;
import uy.gub.sae.acciones.business.ejb.ResultadoAccion;
import uy.gub.sae.acciones.business.ejb.exception.InvalidParametersException;
import uy.gub.sae.acciones.business.ejb.exception.UnexpectedAccionException;

/**
 * Session Bean implementation class AccionesHelperBean
 */
@Stateless
public class AccionesHelperBean implements AccionesHelperLocal{

	//Parametro fijo que se pasa en todas las invocaciones a validaciones.
	private final String PARAMETRO_RECURSO = "RECURSO";
	private final String PARAMETRO_RESERVA = "RESERVA";
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager em;
	
  @SuppressWarnings("unchecked")
	public List<AccionPorRecurso> obtenerAccionesPorRecurso(Recurso r, Evento e) {
		List<AccionPorRecurso> acciones = (List<AccionPorRecurso>) em.createQuery(
  		"SELECT axr " +
  		"FROM  AccionPorRecurso axr " +
  		"WHERE axr.recurso = :r " +
  		"	 AND axr.evento = :e " +
  		"  AND axr.fechaBaja = null " +
  		"ORDER BY axr.ordenEjecucion ASC "
  		)
		.setParameter("r", r)
		.setParameter("e", e)
		.getResultList();
		return acciones;
	}

	public void ejecutarAccionesPorEvento(Map<String, DatoReserva> valores, ReservaDTO reserva, Recurso recurso, Evento evento) throws ApplicationException, BusinessException, ErrorAccionException{
		List<AccionPorRecurso> acciones = this.obtenerAccionesPorRecurso(recurso, evento);
		for(AccionPorRecurso aXr : acciones) {
			Accion a = aXr.getAccion();
			if (a.getFechaBaja() == null) {
				List<AccionPorDato> camposDeLaAccion = aXr.getAccionesPorDato();
				Map<String, Object> parametros = new HashMap<String, Object>();
				List<String> nombreCampos = new ArrayList<String>();
				for (AccionPorDato accionPorDato : camposDeLaAccion) {
					if (accionPorDato.getFechaDesasociacion() == null) {
						String nombreParametro = accionPorDato.getNombreParametro();
						DatoASolicitar campo = accionPorDato.getDatoASolicitar();
						DatoReserva dato = valores.get(campo.getNombre());
						if (dato != null) {
							if (campo.getTipo() == Tipo.NUMBER){
								parametros.put(nombreParametro, Integer.valueOf(dato.getValor()));
							} else if (campo.getTipo() == Tipo.BOOLEAN) {
								parametros.put(nombreParametro, Boolean.valueOf(dato.getValor()));
							} else if (campo.getTipo() == Tipo.DATE) {
							  try {
							    parametros.put(nombreParametro, Utiles.stringToDate(dato.getValor()));
							  }catch(Exception ex) {
                  parametros.put(nombreParametro, dato.getValor());
							  }
              } else {
								parametros.put(nombreParametro, dato.getValor());
							}
						} else {
							parametros.put(nombreParametro, null);
						}
						nombreCampos.add(campo.getNombre());
					}
				}
			
				parametros.put(PARAMETRO_RECURSO, copiarRecurso(aXr.getRecurso()));
				parametros.put(PARAMETRO_RESERVA, reserva);
				
				EjecutorAccion ejecutor = getEjecutor(a.getHost(), a.getServicio());
				try {
					//Ejecuto la accion
					ResultadoAccion resultado =  ejecutor.ejecutar(a.getNombre(), parametros);
					//Hay errores
					if (resultado.getErrores().size() > 0) {
						List<String> mensajes = new ArrayList<String>();
						List<String> codigosErrorMensajes = new ArrayList<String>();
						for (ErrorAccion error : resultado.getErrores()) {
							mensajes.add(error.getMensaje());
							codigosErrorMensajes.add(error.getCodigo());
						}
						throw new ErrorAccionException("-1", mensajes, codigosErrorMensajes, a.getNombre());
					}
				} catch (UnexpectedAccionException e) {
					throw new ApplicationException(e);
				} catch (InvalidParametersException e) {
					List<String> mensajes = new ArrayList<String>();
					mensajes.add(e.getMessage());
					throw new ErrorAccionException("-1", mensajes);
				}
			}
		}
	}
	
	private EjecutorAccion getEjecutor(String host, String jndiName) throws ApplicationException {

		Object ejb = null;
		try {
			InitialContext ctx; 
			if (host != null && !host.trim().isEmpty() && !"localhost".endsWith(host.trim()) ) {
				Properties props = new Properties();
				props.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
				props.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
				props.put("java.naming.provider.url", host);
				ctx = new InitialContext(props);
			} else {
				ctx = new InitialContext();
			}
			
			ejb = ctx.lookup(jndiName);
		} catch (NamingException nEx) {
			throw new ApplicationException("No se pudo acceder a un EJB de tipo EjecutorAccion (jndiName: "+jndiName+")", nEx);
	  }
		
		EjecutorAccion ejecutor = null;
		if (ejb instanceof EjecutorAccion) {
			ejecutor = (EjecutorAccion) ejb;
		} else {
			throw new ApplicationException("Se esperaba un EJB de tipo EjecutorAccion y se encontró uno del tipo " + ejb.getClass());
		}
		
		return ejecutor;
	}
	
	private RecursoDTO copiarRecurso(Recurso recurso) {
		RecursoDTO recursoDTO = new RecursoDTO();
		recursoDTO.setId(recurso.getId());
		recursoDTO.setNombre(recurso.getNombre());
		recursoDTO.setDescripcion(recurso.getDescripcion());
		recursoDTO.setCantDiasAGenerar(recurso.getCantDiasAGenerar());
		recursoDTO.setFechaBaja(recurso.getFechaBaja());
		recursoDTO.setFechaFin(recurso.getFechaFin());
		recursoDTO.setFechaFinDisp(recurso.getFechaFinDisp());
		recursoDTO.setFechaInicio(recurso.getFechaInicio());
		recursoDTO.setFechaInicioDisp(recurso.getFechaInicioDisp());
		recursoDTO.setMostrarNumeroEnLlamador(recurso.getMostrarNumeroEnLlamador());
		recursoDTO.setReservaMultiple(recurso.getReservaMultiple());
		recursoDTO.setVentanaCuposMinimos(recurso.getVentanaCuposMinimos());
		recursoDTO.setDiasInicioVentanaIntranet(recurso.getDiasInicioVentanaIntranet());
		recursoDTO.setDiasVentanaIntranet(recurso.getDiasVentanaIntranet());
		recursoDTO.setDiasInicioVentanaInternet(recurso.getDiasInicioVentanaInternet());
		recursoDTO.setDiasVentanaInternet(recurso.getDiasVentanaInternet());
		recursoDTO.setSerie(recurso.getSerie());
		recursoDTO.setAgendaDescripcion(recurso.getAgenda().getDescripcion());
		return recursoDTO;
	}

}
