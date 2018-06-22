package uy.gub.imm.sae.common.factories.ws;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.factories.ws.client.agendar.AgendarReservasWS;
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
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exception.ValidacionException;

public class AgendarReservasPort implements AgendarReservas {
	
	private AgendarReservasWS reservas;
	
	public AgendarReservasPort (AgendarReservasWS reservas){
		this.reservas = reservas;
	}
	
	public Boolean agendaActiva(Agenda a) {
		throw new UnsupportedOperationException();
	}

	public void cancelarReserva(Empresa empresa, Recurso recurso, Reserva reserva) throws UserException {
		throw new UnsupportedOperationException();
	}

	public List<Integer> cancelarReservasPeriodo(Empresa empresa, Recurso recurso, VentanaDeTiempo ventana, String idioma, String formatoFecha, String formatoHora, String asunto, String cuerpo) throws UserException {
    throw new UnsupportedOperationException();
	}
	
	public Reserva confirmarReserva(Empresa empresa, Reserva reserva, String transaccionPadreId, Long pasoPadre, boolean inicioAsistido) throws ApplicationException,
			BusinessException, ValidacionException, AccesoMultipleException, UserException {
    throw new UnsupportedOperationException();
	}

	public Agenda consultarAgendaPorId(Integer id)
			throws ApplicationException, BusinessException {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
	}

	public List<Reserva> consultarReservaPorDatos(Recurso r,
			Map<DatoASolicitar, DatoReserva> datos) {
		throw new UnsupportedOperationException();
	}

	public Reserva consultarReservaPorDatosClave(Recurso r,
			Map<DatoASolicitar, DatoReserva> datos) throws ApplicationException {
    throw new UnsupportedOperationException();
	}

	public Reserva consultarReservaPorId(Integer idRecurso, Integer idReserva)
			throws UserException {
		throw new UnsupportedOperationException();
	}

	public void desmarcarReserva(Reserva r) throws BusinessException {
    throw new UnsupportedOperationException();
	}

	public Reserva marcarReserva(Disponibilidad d) throws UserException{
    throw new UnsupportedOperationException();
	}

	public Reserva marcarReserva(List<Disponibilidad> disps) {
		throw new UnsupportedOperationException();
	}

	public List<Integer> obtenerCuposPorDia(Recurso r, VentanaDeTiempo v, TimeZone t) throws UserException {
    throw new UnsupportedOperationException();
	}

	public List<Disponibilidad> obtenerDisponibilidades(Recurso r, VentanaDeTiempo v, TimeZone tz) throws UserException {
    throw new UnsupportedOperationException();
	}

	public VentanaDeTiempo obtenerVentanaCalendarioIntranet(Recurso r) 	throws UserException {
    throw new UnsupportedOperationException();
	}

	public VentanaDeTiempo obtenerVentanaCalendarioInternet(Recurso r) throws UserException {
    throw new UnsupportedOperationException();
	}
	
	public void reagendarReservas(List<Reserva> reservas, Date fechaHora) {
		throw new UnsupportedOperationException();
	}

	public void validarDatosReserva(Recurso recurso, List<DatoReserva> datos) throws BusinessException, UserException, ApplicationException {
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
	public Recurso consultarRecursoPorReservaId(Integer reservId) throws ApplicationException, BusinessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> consultarTextos(String idioma) throws ApplicationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String> consultarPreguntasCaptcha(String idioma) throws ApplicationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void validarDatosReserva(Empresa e, Reserva r) throws BusinessException, ValidacionException, ApplicationException {
		throw new UnsupportedOperationException();
	}
	
	public void limpiarTrazas() {
		throw new UnsupportedOperationException();
	}

  @Override
  public List<TramiteAgenda> consultarTramites(Agenda a) throws ApplicationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Reserva confirmarReservaPresencial(Empresa empresa, Reserva reserva)
      throws ApplicationException, BusinessException, ValidacionException, AccesoMultipleException, UserException {
    throw new UnsupportedOperationException();
  }
  
  
  @Override
  public Reserva generarYConfirmarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idDisponibilidad, String valoresCampos, 
      String idTransaccionPadre, String pasoTransaccionPadre, String idioma) throws UserException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hayCupoPresencial(Disponibilidad disponibilidad) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void cancelarReserva(Integer idEmpresa, Integer idAgenda, Integer idRecurso, Integer idReserva) throws UserException {
    throw new UnsupportedOperationException();
  }

}
