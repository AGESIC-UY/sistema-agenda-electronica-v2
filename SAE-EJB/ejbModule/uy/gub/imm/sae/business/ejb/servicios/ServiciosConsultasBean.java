package uy.gub.imm.sae.business.ejb.servicios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.ejb.facade.ConsultasLocal;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.exception.BusinessException;


@Stateless
@RolesAllowed({"RA_AE_FATENCION","RA_AE_PLANIFICADOR", "RA_AE_ADMINISTRADOR","RA_AE_ANONIMO"})
public class ServiciosConsultasBean implements ServiciosConsultasLocal, ServiciosConsultasRemote {

	@EJB
	private ConsultasLocal consultaEJB;

	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;

	
	public List<ReservaDTO> consultarReservasPorClave(Integer recId, Map<String, String> datosClave) throws BusinessException {
		ReservaDTO auxiliar;
		if (recId == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}
		
		Recurso recurso = entityManager.find(Recurso.class, recId);
		if (recurso == null) {
			throw new BusinessException("-1", "No se encuentra el recurso indicado");
		}		

		List<DatoReserva> datosConsultar = new ArrayList<DatoReserva>();
//		Map<String,Object> datos = new HashMap<String, Object>();
		List<DatoASolicitar> datosRecurso = recurso.getDatoASolicitar();
		Iterator<DatoASolicitar> iterador = datosRecurso.iterator();
		List<DatoASolicitar> datosOpcionales = new ArrayList<DatoASolicitar>();
		while (iterador.hasNext()){
			DatoASolicitar dato = iterador.next();
			if (dato.getEsClave()){
				if (datosClave.get(dato.getNombre())!= null){
					DatoReserva datoR=new DatoReserva();
					datoR.setDatoASolicitar(dato);
					datoR.setValor(datosClave.get(dato.getNombre()));
					datosConsultar.add(datoR);
				}
				else {
					datosOpcionales.add(dato);
				}
			}
		}
		List<Reserva> reservas=consultaEJB.consultarReservaDatos(datosConsultar, recurso);
		
		// Elimino aquellas reservas que tengan más datos de la clave
		// que los que se pasaron.
		if (datosOpcionales.size()> 0){
			Iterator<DatoASolicitar> itD = datosOpcionales.iterator();
			while (itD.hasNext()) {
				DatoASolicitar dato1 = itD.next();
				List<Reserva> reservasAux = new ArrayList<Reserva>();
				Iterator<Reserva> itR = reservas.iterator();
				while (itR.hasNext()){
					Reserva resAux = itR.next();
					Set<DatoReserva> datosResAux = resAux.getDatosReserva();
					Iterator<DatoReserva> itDR = datosResAux.iterator();
					Boolean queda = true;
					while (itDR.hasNext()){
						DatoReserva datoResAux = itDR.next();
						if (datoResAux.getDatoASolicitar().getId() == dato1.getId()){
							queda = false;
						}
					}
					if (queda){
						reservasAux.add(resAux);
					}
				}
				reservas = reservasAux;
			}
		}
		Iterator<Reserva> it = reservas.iterator();
		Reserva reserva = null;
		List<ReservaDTO> resultado = new ArrayList<ReservaDTO>();
		while (it.hasNext()){
			reserva=it.next();
			auxiliar = new ReservaDTO();
			auxiliar.setId(reserva.getId());
			auxiliar.setNumero(reserva.getNumero());
			auxiliar.setEstado(reserva.getEstadoDescripcion());
			auxiliar.setFecha(reserva.getDisponibilidades().get(0).getFecha());
			auxiliar.setHoraInicio(reserva.getDisponibilidades().get(0).getHoraInicio());
			resultado.add(auxiliar);
		}
		return resultado;
	}
	


}
