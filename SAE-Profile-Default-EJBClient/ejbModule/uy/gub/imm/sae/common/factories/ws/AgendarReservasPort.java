package uy.gub.imm.sae.common.factories.ws;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.factories.ws.client.agendar.AccesoMultipleException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.AgendarReservasWS;
import uy.gub.imm.sae.common.factories.ws.client.agendar.ApplicationException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.BusinessException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.ErrorValidacionCommitException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.ErrorValidacionException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.UserCommitException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.UserException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.ValidacionClaveUnicaException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.ValidacionException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.ValidacionPorCampoException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.WarningValidacionCommitException_Exception;
import uy.gub.imm.sae.common.factories.ws.client.agendar.WarningValidacionException_Exception;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TramiteAgenda;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.AccesoMultipleException;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.AutocompletarException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorValidacionCommitException;
import uy.gub.imm.sae.exception.ErrorValidacionException;
import uy.gub.imm.sae.exception.UserCommitException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionClaveUnicaException;
import uy.gub.imm.sae.exception.ValidacionException;
import uy.gub.imm.sae.exception.ValidacionPorCampoException;

public class AgendarReservasPort implements AgendarReservas {
	
	private AgendarReservasWS reservas;
	
	public AgendarReservasPort (AgendarReservasWS reservas){
		this.reservas = reservas;
	}
	
	public Boolean agendaActiva(Agenda a) {
		throw new UnsupportedOperationException();
	}

	public void cancelarReserva(Empresa empresa, Recurso recurso, Reserva reserva)
			throws BusinessException {
		throw new UnsupportedOperationException();
	}

	public Reserva confirmarReserva(Empresa empresa, Reserva reserva, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) throws ApplicationException,
			BusinessException, ValidacionException, AccesoMultipleException, UserException {
		
		try {
			return this.reservas.confirmarReserva(reserva);
		} catch (AccesoMultipleException_Exception e) {
			throw new AccesoMultipleException(e.getFaultInfo().getCodigoError());
		} catch (ApplicationException_Exception e) {
			throw new ApplicationException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		} catch (UserException_Exception e) {
			throw new UserException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		} catch (ValidacionException_Exception e) {
			throw new ValidacionException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getNombresCampos(), e.getFaultInfo().getMensajes());
		} catch (ErrorValidacionException_Exception e) {
			throw new ErrorValidacionException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getNombresCampos(),e.getFaultInfo().getMensajes());
		} catch (ErrorValidacionCommitException_Exception e) {
			throw new ErrorValidacionCommitException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getNombresCampos(),e.getFaultInfo().getMensajes());
		} catch (ValidacionClaveUnicaException_Exception e) {
			throw new ValidacionClaveUnicaException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getNombresCampos());
		} catch (ValidacionPorCampoException_Exception e) {
			throw new ValidacionPorCampoException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getNombresCampos(),e.getFaultInfo().getMensajes());
		}
	}

	public Agenda consultarAgendaPorId(Integer id)
			throws ApplicationException, BusinessException {
		try {
			return this.reservas.consultarAgendaPorId(id);
		} catch (ApplicationException_Exception e) {
			throw new ApplicationException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
	}
	
	public List<Agenda> consultarAgendas() throws ApplicationException,
			BusinessException {
		throw new UnsupportedOperationException();
	}

	public Recurso consultarRecursoPorId(Agenda a, Integer id)
			throws ApplicationException, BusinessException {
		throw new UnsupportedOperationException();
	}

	public Recurso consultarRecursoPorNombre(Agenda a, String nombre)
			throws ApplicationException, BusinessException {
		throw new UnsupportedOperationException();
	}

	public List<Recurso> consultarRecursos(Agenda a)
			throws ApplicationException, BusinessException {
		
		try {
			return this.reservas.consultarRecursos(a);
		} catch (ApplicationException_Exception e) {
			throw new ApplicationException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
	}

	public List<Reserva> consultarReservaPorDatos(Recurso r,
			Map<DatoASolicitar, DatoReserva> datos) {
		throw new UnsupportedOperationException();
	}

	public Reserva consultarReservaPorDatosClave(Recurso r,
			Map<DatoASolicitar, DatoReserva> datos) throws ApplicationException {
		
		HashMap<DatoASolicitar, DatoReserva> map = null;
		if (datos instanceof HashMap) {
			map = (HashMap<DatoASolicitar, DatoReserva>) datos;
		} else {
			map = new HashMap<DatoASolicitar, DatoReserva>(datos);
		}
		
		try {
			return this.reservas.consultarReservaPorDatosClave(r, map);
		} catch (ApplicationException_Exception e) {
			throw new ApplicationException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
	}

	public Reserva consultarReservaPorNumero(Recurso r, Integer numero)
			throws BusinessException {
		throw new UnsupportedOperationException();
	}

	public List<Reserva> consultarReservasEnPeriodo(Recurso r, VentanaDeTiempo v) {
		throw new UnsupportedOperationException();
	}

	public void desmarcarReserva(Reserva r) throws BusinessException {
		try {
			this.reservas.desmarcarReserva(r);
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
	}

	public Reserva marcarReserva(Disponibilidad d) throws BusinessException,
			UserException{

		try {
			return this.reservas.marcarReservaDisponible(d);
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		} catch (UserException_Exception e) {
			throw new UserException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		} catch (UserCommitException_Exception e) {
			throw new UserCommitException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
		
	}

	public Reserva marcarReserva(List<Disponibilidad> disps) {
		throw new UnsupportedOperationException();
	}

	public List<Integer> obtenerCuposPorDia(Recurso r, VentanaDeTiempo v)
			throws BusinessException {

		try {
			return this.reservas.obtenerCuposPorDia(r, v);
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
	}

	public List<Disponibilidad> obtenerDisponibilidades(Recurso r, VentanaDeTiempo v, TimeZone tz) throws BusinessException {

		try {
			return this.reservas.obtenerDisponibilidades(r, v);
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
	}

	public VentanaDeTiempo obtenerVentanaCalendarioIntranet(Recurso r)
			throws BusinessException {

		//Se da una implementacion para cumplir con la interfaz pero nunca se ejecuta este método
		return null;
	}

	public VentanaDeTiempo obtenerVentanaCalendarioInternet(Recurso r)
			throws BusinessException {

		try {
			return this.reservas.obtenerVentanaCalendarioInternet(r);
		} catch (BusinessException_Exception e) {
			throw new BusinessException(e.getFaultInfo().getCodigoError(),e.getFaultInfo().getMessage());
		}
	}
	
	public void reagendarReservas(List<Reserva> reservas, Date fechaHora) {
		throw new UnsupportedOperationException();
	}

	public void validarDatosReserva(Recurso recurso, List<DatoReserva> datos)
			throws BusinessException, UserException, ApplicationException {
		throw new UnsupportedOperationException();
	}

	public List<ServicioPorRecurso> consultarServicioAutocompletar (Recurso r) throws BusinessException {
		throw new UnsupportedOperationException();
	}
	
	public Map<String, Object> autocompletarCampo(ServicioPorRecurso r, Map<String, Object> datosParam) throws ApplicationException, BusinessException, AutocompletarException, UserException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Empresa obtenerEmpresaPorId(Integer empresaId) throws ApplicationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void enviarComunicacionesConfirmacion(String linkCancelacion, Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void enviarComunicacionesCancelacion(Reserva reserva, String idioma, String formatoFecha, String formatoHora) throws ApplicationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Recurso consultarRecursoPorReservaId(Integer reservId)
			throws ApplicationException, BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> consultarTextos(String idioma) throws ApplicationException {
		throw new UnsupportedOperationException();
	}

//	@Override
//	public List<String> consultarFrasesCaptcha(String idioma) throws ApplicationException {
//		throw new UnsupportedOperationException();
//	}

	@Override
	public Map<String, String> consultarPreguntasCaptcha(String idioma) throws ApplicationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void validarDatosReserva(Empresa e, Reserva r) throws BusinessException, ValidacionException,
			ApplicationException {
		throw new UnsupportedOperationException();
	}
	
	public void limpiarTrazas() {
		throw new UnsupportedOperationException();
	}

  @Override
  public List<TramiteAgenda> consultarTramites(Agenda a) throws ApplicationException {
    throw new UnsupportedOperationException();
  }
}
