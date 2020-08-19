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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;

import uy.gub.imm.sae.business.dto.ReservaDTO;
import uy.gub.imm.sae.business.utilidades.SimpleCalendario;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.ModoAutocompletado;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoReserva;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.ParametrosAutocompletar;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.ServicioAutocompletarPorDato;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.Validacion;
import uy.gub.imm.sae.entity.ValidacionPorDato;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.entity.ValorConstanteValidacionRecurso;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.ErrorAutocompletarException;
import uy.gub.imm.sae.exception.ErrorValidacionCommitException;
import uy.gub.imm.sae.exception.ErrorValidacionException;
import uy.gub.imm.sae.exception.ValidacionException;
import uy.gub.imm.sae.exception.ValidacionPorCampoException;
import uy.gub.imm.sae.exception.WarningAutocompletarException;
import uy.gub.imm.sae.validaciones.business.dto.RecursoDTO;
import uy.gub.imm.sae.validaciones.business.ejb.ErrorValidacion;
import uy.gub.imm.sae.validaciones.business.ejb.ResultadoValidacion;
import uy.gub.imm.sae.validaciones.business.ejb.ValidadorReserva;
import uy.gub.imm.sae.validaciones.business.ejb.exception.InvalidParametersException;
import uy.gub.imm.sae.validaciones.business.ejb.exception.UnexpectedValidationException;
import uy.gub.sae.autocompletados.business.ejb.AutocompletadorReserva;
import uy.gub.sae.autocompletados.business.ejb.ErrorAutocompletado;
import uy.gub.sae.autocompletados.business.ejb.ResultadoAutocompletado;
import uy.gub.sae.autocompletados.business.ejb.WarningAutocompletado;
import uy.gub.sae.autocompletados.business.ejb.exception.UnexpectedAutocompletadoException;

@Stateless
@PermitAll
public class AgendarReservasHelperBean implements AgendarReservasHelperLocal{

	//Parametro fijo que se pasa en todas las invocaciones a validaciones.
	private final String PARAMETRO_RECURSO = "RECURSO";
	private final String PARAMETRO_RESERVA = "RESERVA";
	private final String PARAMETRO_DATOS_CLAVE = "DATOS_CLAVE";
	
	@PersistenceContext(unitName = "SAE-EJB")
	private EntityManager entityManager;

	@EJB
	private ConsultasLocal consultaEJB;
	
  static Logger logger = Logger.getLogger(AgendarReservasHelperBean.class);
	
	/**
	 * Obtiene la ventana del calendario estatica, es decir sin verificar 
	 * los cupos que realmente existen en la ventana.
	 * @param recurso
	 * @return
	 */
	public VentanaDeTiempo obtenerVentanaCalendarioEstaticaIntranet (Recurso recurso) {
		recurso = entityManager.find(Recurso.class, recurso.getId());
		VentanaDeTiempo ventana = new VentanaDeTiempo();
    Calendario calendario = new SimpleCalendario(null);
		//Calcular la fecha inicial: hoy + diasInicioVentanaIntranet (tener en cuenta los días no hábiles)
		Date hoy = Utiles.time2InicioDelDia(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(hoy);
    try {
      for(int i=0; i<recurso.getDiasInicioVentanaIntranet().intValue(); ) {
        if(calendario.esDiaHabil(cal.getTime(), recurso)) {
          i++;
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
      }
    }catch(Exception ex) {
      cal.add(Calendar.DAY_OF_MONTH, recurso.getDiasInicioVentanaIntranet());
    }
    //Fecha inicial
		Date fechaInicial = cal.getTime();
		ventana.setFechaInicial(recurso.getFechaInicioDisp());
		if (ventana.getFechaInicial().before(fechaInicial)) {
			ventana.setFechaInicial(fechaInicial);
		}
    //Calcular la fecha final: fecha inicial + getDiasVentanaInternet (tener en cuenta los días no hábiles)
    try {
      for(int i=0; i<recurso.getDiasVentanaIntranet().intValue()-1; ) {
        if(calendario.esDiaHabil(cal.getTime(), recurso)) {
          i++;
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
      }
    }catch(Exception ex) {
      cal.add(Calendar.DAY_OF_MONTH, recurso.getDiasVentanaIntranet());
    }
    Date fechaFinal = cal.getTime();
    ventana.setFechaFinal(fechaFinal);
    if (recurso.getFechaFinDisp() != null && recurso.getFechaFinDisp().before(ventana.getFechaFinal())) {
      ventana.setFechaFinal(recurso.getFechaFinDisp());
    }
		return ventana;
	}

	/**
	 * Obtiene la ventana del calendario estatica, es decir sin verificar 
	 * los cupos que realmente existen en la ventana.
	 * @param recurso
	 * @return
	 */
	public VentanaDeTiempo obtenerVentanaCalendarioEstaticaInternet (Recurso recurso) {
		recurso = entityManager.find(Recurso.class, recurso.getId());
		VentanaDeTiempo ventana = new VentanaDeTiempo();
    Calendario calendario = new SimpleCalendario(null);
		//Calcular la fecha inicial: hoy + diasInicioVentanaInternet (tener en cuenta los días no hábiles)
		Date hoy = Utiles.time2InicioDelDia(new Date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(hoy);
		try {
      for(int i=0; i<recurso.getDiasInicioVentanaInternet().intValue(); ) {
        if(calendario.esDiaHabil(cal.getTime(), recurso)) {
          i++;
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
      }
		}catch(Exception ex) {
		  cal.add(Calendar.DAY_OF_MONTH, recurso.getDiasInicioVentanaInternet());
		}
		//Fecha inicial
		Date fechaInicial = cal.getTime();
		ventana.setFechaInicial(recurso.getFechaInicioDisp());
		if (ventana.getFechaInicial().before(fechaInicial)) {
			ventana.setFechaInicial(fechaInicial);
		}
    //Calcular la fecha final: fecha inicial + getDiasVentanaInternet (tener en cuenta los días no hábiles)
    try {
      for(int i=0; i<recurso.getDiasVentanaInternet().intValue()-1; ) {
        if(calendario.esDiaHabil(cal.getTime(), recurso)) {
          i++;
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
      }
    }catch(Exception ex) {
      cal.add(Calendar.DAY_OF_MONTH, recurso.getDiasVentanaInternet());
    }
    Date fechaFinal = cal.getTime();
    ventana.setFechaFinal(fechaFinal);
		if (recurso.getFechaFinDisp() != null && recurso.getFechaFinDisp().before(ventana.getFechaFinal())) {
			ventana.setFechaFinal(recurso.getFechaFinDisp());
		}
		return ventana;
	}
	
	/**
	 * Obtiene los cupos asignados, es decir la suma de los cupos de las disponibilidades existentes no nulas
	 * dentro de la ventana indicada. Para el rango de la ventana que caiga en el pasado o fuera 
	 * del inicio de disponibilidad indicado en el recurso, no se devuelve cupos.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> obtenerCuposAsignados(Recurso recurso, VentanaDeTiempo ventana, TimeZone timezone) {
		//La clono pues la voy a modificar.
		ventana = new VentanaDeTiempo(ventana);
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.MILLISECOND, timezone.getOffset((new Date()).getTime()));
    Date ahora = cal.getTime();
		//Elimino el pasado
		if (ventana.getFechaInicial().before(ahora)) {
			ventana.setFechaInicial(ahora);
		}
		if (ventana.getFechaInicial().before(recurso.getFechaInicioDisp())) {
			ventana.setFechaInicial(recurso.getFechaInicioDisp());
		}
		//Cupos asignados por dia.
		//NO se debe considerar las disponibilidades presenciales
		String eql = "SELECT d.fecha, SUM(d.cupo) " + 
	      "FROM Disponibilidad d " +
	      "WHERE d.recurso = :rec " +
        "  AND d.presencial = false " +
	      "  AND d.fechaBaja is null " +
	      "  AND d.fecha BETWEEN :fi AND :ff " +
	      "  AND (d.fecha <> :hoy OR d.horaInicio >= :ahora) " +
	      "GROUP BY d.fecha " +
	      "ORDER BY d.fecha asc ";
		List<Object[]> cuposAsignados = entityManager.createQuery(eql)
			.setParameter("rec", recurso)
			.setParameter("fi", ventana.getFechaInicial(), TemporalType.DATE)
			.setParameter("ff", ventana.getFechaFinal(), TemporalType.DATE)
			.setParameter("hoy", ahora, TemporalType.DATE)
			.setParameter("ahora", ahora, TemporalType.TIMESTAMP)
			.getResultList();
		return cuposAsignados;
	}

	/**
	 * Obtiene los cupos consumidos, es decir la suma de las reservas no canceladas dentro de la ventana indicada. 
	 * Para el rango de la ventana que caiga en el pasado o fuera del inicio de disponibilidad indicado en el recurso, no se devuelve cupos.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> obtenerCuposConsumidos(Recurso recurso, VentanaDeTiempo ventana, TimeZone timezone) {
		//La clono pues la voy a modificar.
		ventana = new VentanaDeTiempo(ventana);
    //Date ahora = new Date();
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.MILLISECOND, timezone.getOffset((new Date()).getTime()));
    Date ahora = cal.getTime();
		//Elimino el PASADO
		if (ventana.getFechaInicial().before(ahora)) {
			ventana.setFechaInicial(ahora);
		}
		if (ventana.getFechaInicial().before(recurso.getFechaInicioDisp())) {
			ventana.setFechaInicial(recurso.getFechaInicioDisp());
		}
		//Cupos consumidos, es decir, cantidad de reservas por dia no canceladas.
		//No se debe considerar las disponibilidades presenciales
		//Si la reserva está cancelada hay que tomar en cuenta el campo fechaLiberacion
		List<Object[]> cuposConsumidos = entityManager.createQuery(
			"SELECT d.fecha, COUNT(r) " + 
			"FROM Disponibilidad d " +
			"LEFT JOIN d.reservas r " +
			"WHERE d.recurso = :recurso " +
      "  AND d.presencial = false " +
			"  AND d.fechaBaja is null " +
			"  AND d.fecha BETWEEN :finicio AND :ffin " +
    	"  AND (d.fecha <> :hoy OR d.horaInicio >= :ahora) " +
			"  AND (r is null OR r.estado <> :cancelado OR r.fechaLiberacion>=:ahora) " +
			"GROUP BY d.fecha " +
			"ORDER BY d.fecha asc ")
			.setParameter("recurso", recurso)
			.setParameter("finicio", ventana.getFechaInicial(), TemporalType.DATE)
			.setParameter("ffin", ventana.getFechaFinal(), TemporalType.DATE)
			.setParameter("hoy", ahora, TemporalType.DATE)
			.setParameter("ahora", ahora, TemporalType.TIMESTAMP)
			.setParameter("cancelado", Estado.C)
      .setParameter("ahora", new Date())
			.getResultList();
		return cuposConsumidos;
	}
	
	//Armo la lista de resultados, indicando los cupos para todos los dias solicitados (parametro ventana)
	public List<Integer> obtenerCuposXDia(VentanaDeTiempo v, List<Object[]> cuposAsignados, List<Object[]> cuposConsumidos) {
		Iterator<Object[]> iterCuposAsignados  = cuposAsignados.iterator();
		Iterator<Object[]> iterCuposConsumidos = cuposConsumidos.iterator();
		List<Integer> cuposXdia = new ArrayList<Integer>();
		Calendar cont = Calendar.getInstance();
		cont.setTime(Utiles.time2InicioDelDia(v.getFechaInicial()));
		Object[] cupoAsignado  = null;
		Object[] cupoConsumido = null;
		if (iterCuposAsignados.hasNext()) {
			cupoAsignado = iterCuposAsignados.next();
		}
		if (iterCuposConsumidos.hasNext()) {
			cupoConsumido = iterCuposConsumidos.next();
		}
		//Recorro la ventana dia a dia y voy generando la lista completa de cupos x dia con -1, 0, >0 segun corresponda.
		while (!cont.getTime().after(v.getFechaFinal())) {
			Integer cantidadDeCupos = -1;
			//avanzo un lugar en la lista de cupos x dia si la fecha del cupo es igual a la fecha del contador.
			if (cupoAsignado != null) {
				Date fechaDelCupoA = (Date)cupoAsignado[0];
				if (fechaDelCupoA.equals(cont.getTime())) {
					//Nunca deberia ser mas grande que un Entero.	
					cantidadDeCupos = ((Long)cupoAsignado[1]).intValue();
					if (iterCuposAsignados.hasNext()) {
						cupoAsignado = iterCuposAsignados.next();
					} else {
						cupoAsignado = null;
					}
					if (cupoConsumido != null) {
						Date fechaDelCupoC = (Date)cupoConsumido[0];
						if (fechaDelCupoC.equals(cont.getTime())) {
							//Nunca deberia ser mas grande que un Entero.	
							cantidadDeCupos -= ((Long)cupoConsumido[1]).intValue();
							if (cantidadDeCupos < -1) {
								//Solo se da en el caso de que mas de uno hayan querido reservar a la vez cuando quedaba solo un cupo
								cantidadDeCupos = -1;
							}
							if (iterCuposConsumidos.hasNext()) {
								cupoConsumido = iterCuposConsumidos.next();
							} else {
								cupoConsumido = null;
							}
						}
					}
				}
			}
			cuposXdia.add(cantidadDeCupos);
			cont.add(Calendar.DAY_OF_MONTH, 1);
		}
		return cuposXdia;
	}
	
	
	/**
	 * Crea la reserva como pendiente, realiza todo en una transaccion independiente
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Reserva crearReservaPendiente(Disponibilidad disp, TokenReserva token, String ipOrigen) {
	  disp = entityManager.find(Disponibilidad.class, disp.getId());
	  if(token!=null) {
	    token = entityManager.find(TokenReserva.class, token.getId());
	    token.setUltimaReserva(new Date());
	    token.setIpOrigen(ipOrigen);
	  }
	  //Creo y seteo atributos
		Reserva reserva = new Reserva();
		reserva.setEstado(Estado.P);
		reserva.getDisponibilidades().add(disp);
		reserva.setFechaCreacion(new Date());
		reserva.setToken(token);
		reserva.setIpOrigen(ipOrigen);
		entityManager.persist(reserva);
		return reserva;
	}

	/**
	 * Verifica que existan cupos disponibles para la reserva.
	 * Si el parámetro reservaTomada es true asume que pueden quedar 0 reservas porque el llamador ya tomó la reserva (la última)
	 * Si el parámetro reservaTomada es false deben quedar al menos 1 cupo para que el llamador pueda tomarlo
	 * Si la reserva está cancelada hay que tomar en cuenta si ya se liberó el cupo
	 */
	public boolean chequeoCupoDisponible (Disponibilidad disponibilidad, boolean reservaTomada) {
		int cantReservas = ((Long) entityManager.createQuery(
  		"SELECT COUNT(*) FROM Disponibilidad d JOIN d.reservas r " +
  		"WHERE d = :disp AND (r.estado <> :cancelado OR r.fechaLiberacion>=:ahora)")
		.setParameter("disp", disponibilidad)
		.setParameter("cancelado", Estado.C)
    .setParameter("ahora", new Date())
		.getSingleResult()).intValue();
		int minimo = reservaTomada?0:1;
		return (disponibilidad.getCupo() - cantReservas) < minimo;
	}

	/**
	 * Retorna los datos a solicitar vivos del recurso 
	 */
	public List<DatoASolicitar> obtenerDatosASolicitar(Recurso r) {
		@SuppressWarnings("unchecked")
		List<DatoASolicitar> campos = (List<DatoASolicitar>) entityManager.createQuery(
  		"SELECT c " +
  		"FROM DatoASolicitar c " +
  		"WHERE c.agrupacionDato.recurso = :r " +
  		"  AND c.fechaBaja = null " +
  		"ORDER BY c.agrupacionDato.orden, c.fila, c.columna")
		.setParameter("r", r)
		.getResultList();
		return campos;
	}

	public List<ValidacionPorRecurso> obtenerValidacionesPorRecurso(Recurso r) {
		@SuppressWarnings("unchecked")
		List<ValidacionPorRecurso> validaciones = (List<ValidacionPorRecurso>) entityManager.createQuery(
  		"SELECT vxr " +
  		"FROM ValidacionPorRecurso vxr " +
  		"WHERE vxr.recurso = :r " +
  		"  AND vxr.fechaBaja = null " +
  		"ORDER BY vxr.ordenEjecucion ASC")
		.setParameter("r", r)
		.getResultList();
		return validaciones;
	}
	
	public void validarDatosReservaBasico(List<DatoASolicitar> campos, Map<String, DatoReserva> valores) throws ValidacionException {
		Map<String, DatoASolicitar> camposMap = new HashMap<String, DatoASolicitar>();
		for (DatoASolicitar datoASolicitar : campos) {
			camposMap.put(datoASolicitar.getNombre(), datoASolicitar);
		}
		List<String> camposInvalidos  = new ArrayList<String>();
		List<String> mensajes = new ArrayList<String>();
		//Chequeo formato
		for (DatoASolicitar campo : campos) {
			DatoReserva dato = valores.get(campo.getNombre());
			if (campo.getRequerido() && (dato==null || dato.getValor()==null || dato.getValor().trim().isEmpty())) {
				camposInvalidos.add(campo.getNombre());
				mensajes.add("debe_completar_el_campo_campo");
			} else if(dato != null) {
			  boolean campoInvalido = false;
				if (campo.getTipo() == Tipo.NUMBER) {
				  //El tipo del dato es numérico, tratar de validarlo
					try {
						Integer.parseInt(dato.getValor());
					} catch (NumberFormatException e) {
					  campoInvalido = true;
						camposInvalidos.add(campo.getNombre());
						mensajes.add("el_campo_campo_solo_puede_contener_digitos");
					}
				}else if (campo.getTipo() == Tipo.DATE) {
				  if(dato.getValor()!=null && !dato.getValor().trim().isEmpty()) {
  				  //El tipo del dato es fecha y no es vacía, tratar de validarlo
            try {
              Date fecha = Utiles.stringToDate(dato.getValor());
              if(Utiles.esFechaInvalida(fecha)) {
                //El formato es correcto pero la fecha es inválida
                campoInvalido = true;
                camposInvalidos.add(campo.getNombre());
                mensajes.add("el_campo_campo_no_tiene_una_fecha_valida");
              }
            }catch(Exception ex) {
              //No se puede parsear la fecha
              campoInvalido = true;
              camposInvalidos.add(campo.getNombre());
              mensajes.add("el_campo_campo_no_tiene_una_fecha_valida");
              ex.printStackTrace();
            }
				  }
        }else if (campo.getTipo() == Tipo.BOOLEAN) {
          if(dato.getValor()!=null && !dato.getValor().trim().isEmpty()) {
            //El tipo del dato es booleano y no es vacío, tratar de validarlo
            try {
              Boolean.valueOf(dato.getValor());
            }catch(Exception ex) {
              //No se puede parsear el booleano
              campoInvalido = true;
              camposInvalidos.add(campo.getNombre());
              mensajes.add("el_valor_ingresado_no_es_aceptable");
              ex.printStackTrace();
            }
          }
        }else if (campo.getTipo() == Tipo.LIST) {
          if(dato.getValor()!=null && !dato.getValor().trim().isEmpty()) {
            //El tipo del dato es lista y no es vacío, verificar que sea una de las opciones admitidas
            campoInvalido = true;
            for(ValorPosible valorPosible : campo.getValoresPosibles()) {
              if(dato.getValor().equals(valorPosible.getValor())) {
                campoInvalido = false;
              }
            }
            if(campoInvalido) {
              //No es un valor aceptable
              campoInvalido = true;
              camposInvalidos.add(campo.getNombre());
              mensajes.add("el_valor_ingresado_no_es_aceptable");
            }
          }
        }
				//Si el campo no está marcado como inválido, aplicar validaciones específicas
				if(!campoInvalido) {
					if(!campo.getAgrupacionDato().getBorrarFlag()) {
					  //Si es un correo electrónico validar el formato
						if(DatoASolicitar.CORREO_ELECTRONICO.equalsIgnoreCase(campo.getNombre()))	{
						  Pattern pat = Pattern.compile(Utiles.EMAIL_PATTERN);
							Matcher mat = pat.matcher(dato.getValor());
							if(!mat.find()){
								camposInvalidos.add(campo.getNombre());
								mensajes.add("no_es_una_direccion_de_correo_electronico_valida");
							}
						}
						//Si es una cédula validar el dígito
						if(DatoASolicitar.NUMERO_DOCUMENTO.equalsIgnoreCase(campo.getNombre()))	{
							//Ver si el tipo de documento es CI, si es así hay que validar la cédula uruguaya
							DatoReserva tipoDoc = valores.get(DatoASolicitar.TIPO_DOCUMENTO);
							if(tipoDoc!=null && tipoDoc.getValor().equals("CI")) {
								boolean ciOk = Utiles.validarDigitoVerificadorCI(dato.getValor());
								if(!ciOk) {
									camposInvalidos.add(campo.getNombre());
									mensajes.add("cedula_de_identidad_invalida");
								}
							}
						}
					}
				}
			}
		}
		//Si hay campos invalidos
		if (camposInvalidos.size() > 0) {
			throw new ValidacionPorCampoException("-1", camposInvalidos, mensajes);
		}
	}

	public List<Reserva> validarDatosReservaPorClave(Recurso recurso, Disponibilidad disponibilidad, 
	    List<DatoASolicitar> campos, Map<String, DatoReserva> valores, String codigoTramite) {
  	//Se supone que si un campo es clave tiene que ser requerido.
  	//Si cambia este supuesto, se deberá revisar este procedimiento.
  	List<Reserva> listaReserva = new ArrayList<Reserva>();
  	Map<String, DatoASolicitar> camposMap = new HashMap<String, DatoASolicitar>();
  	for (DatoASolicitar datoASolicitar : campos) {
  		camposMap.put(datoASolicitar.getNombre(), datoASolicitar);
  	}
  	List<DatoASolicitar> camposClave = new ArrayList<DatoASolicitar>();
  	//Se carga lista de camposClave
  	for (DatoASolicitar campo : campos) {
  		//Chequeo si el campo es clave
  		if (campo.getEsClave() ) {
  			camposClave.add(campo);
  		}
  	}
  	//Se controla si existen campos clave
  	if (!camposClave.isEmpty()) {
  		//Se controla que no exista en la base otra reserva con la misma clave.
      List<DatoReserva> datoReservaLista = new ArrayList<DatoReserva>();
  	  Iterator<DatoASolicitar> iCampo = camposClave.iterator();
  		while (iCampo.hasNext()){
  			DatoASolicitar datoASolicitar = iCampo.next();
  			DatoReserva datoReserva = valores.get(datoASolicitar.getNombre());
  			datoReservaLista.add(datoReserva);
  		}
  		//Determinar el período en el cual no pueden haber otras reservas (por defecto es solo la misma fecha)
  		Date fecha = disponibilidad.getFecha();
  		Date fechaDesde = fecha;
  		Date fechaHasta = fecha;
  		Integer periodoValidacion = recurso.getPeriodoValidacion();
  		if(periodoValidacion==null) {
  		  periodoValidacion=0;
  		}
		  Calendar cal = new GregorianCalendar();
		  cal.setTime(fecha);
		  cal.add(Calendar.DATE, -1*periodoValidacion);
		  fechaDesde = Utiles.time2InicioDelDia(cal.getTime());
		  cal.setTime(fecha);
		  cal.add(Calendar.DATE, 1*periodoValidacion);
		  fechaHasta = Utiles.time2FinDelDia(cal.getTime());
  		//Consultar las reservas por dato de reserva (solo para los campos clave)
  		listaReserva = consultaEJB.consultarReservaDatosPeriodo(datoReservaLista, recurso, fechaDesde, fechaHasta, codigoTramite);
  	}
		return listaReserva;
	}

	public void validarDatosReservaExtendido(List<ValidacionPorRecurso> validaciones, List<DatoASolicitar> campos, 
		Map<String, DatoReserva> valores, ReservaDTO reserva) throws ApplicationException, BusinessException, 
		ErrorValidacionException, ErrorValidacionCommitException {
	 	Map<String, String> datosClave = new HashMap<String, String>();
	 	// Si no hay ninguna validación no necesito armar el Map, de modo que no
	 	// busco una forma alternativa de obtener el recurso.
    if (!validaciones.isEmpty()){
    	datosClave = copiarDatos(valores, validaciones.get(0).getRecurso());
    }
		for(ValidacionPorRecurso vXr : validaciones) {
			Validacion v = vXr.getValidacion();
			if (v.getFechaBaja() == null) {
				List<ValidacionPorDato> camposDeLaValidacion = vXr.getValidacionesPorDato();
				Map<String, Object> parametros = new HashMap<String, Object>();
				List<String> nombreCampos = new ArrayList<String>();
				for (ValidacionPorDato validacionPorDato : camposDeLaValidacion) {
					if (validacionPorDato.getFechaDesasociacion() == null) {
						String nombreParametro = validacionPorDato.getNombreParametro();
						DatoASolicitar campo = validacionPorDato.getDatoASolicitar();
						DatoReserva dato = valores.get(campo.getNombre());
						if (dato != null) {
							if (campo.getTipo() == Tipo.NUMBER){
								parametros.put(nombreParametro, Integer.valueOf(dato.getValor()));
							}	else if (campo.getTipo() == Tipo.BOOLEAN) {
								parametros.put(nombreParametro, Boolean.valueOf(dato.getValor()));
							}	else {
								parametros.put(nombreParametro, dato.getValor());
							}
						} else {
							//parametros.put(nombreParametro, null);
							//Este codigo esta en las acciones, pero aca no puedo ponerlo mientras tenga mas adelante
							//el chequeo isEmpty para no llamar a una validacion si todos los campos de la misma son opcionales
							//y estan en null. No se hay que analizarlo mas.
						}
						nombreCampos.add(campo.getNombre());
					}
				}
				List<ValorConstanteValidacionRecurso> constantesDeLaValidacion = vXr.getConstantesValidacion();
				for (ValorConstanteValidacionRecurso valorConstante: constantesDeLaValidacion){
					if (valorConstante.getFechaDesasociacion() == null){
						parametros.put(valorConstante.getNombreConstante(), valorConstante.getValor());
					}
				}
        // Si parametros.isEmpty no llamo a la validacion, para que funcione bien con campos que
        // permiten valores nulos
  			if (!parametros.isEmpty() || nombreCampos.isEmpty()) {
  				parametros.put(PARAMETRO_RECURSO, copiarRecurso(vXr.getRecurso()));
  				parametros.put(PARAMETRO_RESERVA, reserva);
  				parametros.put(PARAMETRO_DATOS_CLAVE, datosClave);
  				ValidadorReserva validador = getValidador(v.getHost(), v.getServicio());
  				// Para que no reviente cuando la validación de reagenda devuelve false
  				if (nombreCampos.isEmpty()) {
  					nombreCampos.add("DUMMY");
  				}
  				try {
  					//Ejecuto la validacion
  					ResultadoValidacion resultado =  validador.validarDatosReserva(v.getNombre(), parametros);
  					//Hay errores
  					if (resultado.getErrores().size() > 0) {
  						List<String> mensajes = new ArrayList<String>();
  						List<String> codigosErrorMensajes = new ArrayList<String>();
  						for (ErrorValidacion error : resultado.getErrores()) {
  							mensajes.add(error.getMensaje());
  							codigosErrorMensajes.add(error.getCodigo());
  						}
  						throw new ErrorValidacionException("-1", nombreCampos, mensajes, codigosErrorMensajes, v.getNombre());
  					}
  					//Hay errores con commit
  					if (resultado.getErroresConCommit().size() > 0) {
  						List<String> mensajes = new ArrayList<String>();
  						List<String> codigosErrorMensajes = new ArrayList<String>();
  						for (ErrorValidacion error : resultado.getErroresConCommit()) {
  							mensajes.add(error.getMensaje());
  							codigosErrorMensajes.add(error.getCodigo());
  						}
  						throw new ErrorValidacionCommitException("-1", nombreCampos, mensajes, codigosErrorMensajes, v.getNombre());
  					}
  				} catch (UnexpectedValidationException e) {
  					throw new ApplicationException(e);
  				} catch (InvalidParametersException e) {
  					List<String> mensajes = new ArrayList<String>();
  					mensajes.add(e.getMessage());
  					throw new ErrorValidacionException("-1", nombreCampos, mensajes);
  				}
  			}
			} 
		}
	}
	
	public int cantidadReservasPorIp(Recurso recurso, String ipOrigen, Integer reservaId) {
	  
    Date fecha = new Date();
    Date fechaDesde = fecha;
    Date fechaHasta = fecha;
    Integer periodoValidacion = recurso.getPeriodoPorIP();
    if(periodoValidacion==null) {
      periodoValidacion=0;
    }
    Calendar cal = new GregorianCalendar();
    cal.setTime(fecha);
    cal.add(Calendar.DATE, -1*periodoValidacion);
    fechaDesde = Utiles.time2InicioDelDia(cal.getTime());
    cal.setTime(fecha);
    cal.add(Calendar.DATE, 1*periodoValidacion);
    fechaHasta = Utiles.time2FinDelDia(cal.getTime());
    //Solo se consideran las reservas simples, no las reservas múltiples
    String eql = "SELECT COUNT(*) FROM Reserva r JOIN r.disponibilidades d WHERE d.recurso.id=:idRecurso AND r.estado NOT IN (:cancelada,:pendiente) AND r.token IS NULL AND "
        + "r.ipOrigen=:ipOrigen AND r.fechaCreacion>=:fechaDesde AND r.fechaCreacion<=:fechaHasta";
    if(reservaId!=null) {
      eql = eql + " AND r.id<>:reservaId";
    }
    Query query = entityManager.createQuery(eql);
    query.setParameter("cancelada", Estado.C);
    query.setParameter("pendiente", Estado.P);
    query.setParameter("ipOrigen", ipOrigen);
    query.setParameter("fechaDesde", fechaDesde);
    query.setParameter("fechaHasta", fechaHasta);
    query.setParameter("idRecurso", recurso.getId());
    if(reservaId!=null) {
      query.setParameter("reservaId", reservaId);
    }
	  int cantReservasIP = ((Long) query.getSingleResult()).intValue();
	  return cantReservasIP;
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
		recursoDTO.setVentanaCuposMinimos(recurso.getVentanaCuposMinimos());
		recursoDTO.setDiasInicioVentanaIntranet(recurso.getDiasInicioVentanaIntranet());
		recursoDTO.setDiasVentanaIntranet(recurso.getDiasVentanaIntranet());
		recursoDTO.setDiasInicioVentanaInternet(recurso.getDiasInicioVentanaInternet());
		recursoDTO.setDiasVentanaInternet(recurso.getDiasVentanaInternet());
		recursoDTO.setPresencialAdmite(recurso.getPresencialAdmite());
		recursoDTO.setPresencialCupos(recurso.getPresencialCupos());
		recursoDTO.setPresencialLunes(recurso.getPresencialLunes());
		recursoDTO.setPresencialMartes(recurso.getPresencialMartes());
		recursoDTO.setPresencialMiercoles(recurso.getPresencialMiercoles());
		recursoDTO.setPresencialJueves(recurso.getPresencialJueves());
		recursoDTO.setPresencialViernes(recurso.getPresencialViernes());
		recursoDTO.setPresencialSabado(recurso.getPresencialSabado());
    recursoDTO.setMultipleAdmite(recurso.getMultipleAdmite());
    recursoDTO.setCambiosAdmite(recurso.getCambiosAdmite());
    recursoDTO.setCambiosTiempo(recurso.getCambiosTiempo()); 
    recursoDTO.setCambiosUnidad(recurso.getCambiosUnidad());
    recursoDTO.setPeriodoValidacion(recurso.getPeriodoValidacion()); 
    recursoDTO.setValidarPorIP(recurso.getValidarPorIP()); 
    recursoDTO.setCantidadPorIP(recurso.getCantidadPorIP()); 
    recursoDTO.setPeriodoPorIP(recurso.getPeriodoPorIP()); 
    recursoDTO.setIpsSinValidacion(recurso.getIpsSinValidacion()); 
    recursoDTO.setCancelacionTiempo(recurso.getCancelacionTiempo()); 
    recursoDTO.setCancelacionUnidad(recurso.getCancelacionUnidad()); 
    recursoDTO.setCancelacionTipo(recurso.getCancelacionTipo()!=null?recurso.getCancelacionTipo().name(): null); 
    recursoDTO.setMiPerfilConHab(recurso.getMiPerfilConHab());
    recursoDTO.setMiPerfilConTitulo(recurso.getMiPerfilConTitulo());
    recursoDTO.setMiPerfilConCorto(recurso.getMiPerfilConCorto());
    recursoDTO.setMiPerfilConLargo(recurso.getMiPerfilConLargo());
    recursoDTO.setMiPerfilConVencim(recurso.getMiPerfilConVencim());
    recursoDTO.setMiPerfilCanHab(recurso.getMiPerfilCanHab());
    recursoDTO.setMiPerfilCanTitulo(recurso.getMiPerfilCanTitulo());
    recursoDTO.setMiPerfilCanCorto(recurso.getMiPerfilCanCorto());
    recursoDTO.setMiPerfilCanLargo(recurso.getMiPerfilCanLargo());
    recursoDTO.setMiPerfilCanVencim(recurso.getMiPerfilCanVencim());
    recursoDTO.setMiPerfilRecHab(recurso.getMiPerfilRecHab());
    recursoDTO.setMiPerfilRecTitulo(recurso.getMiPerfilRecTitulo());
    recursoDTO.setMiPerfilRecCorto(recurso.getMiPerfilRecCorto());
    recursoDTO.setMiPerfilRecLargo(recurso.getMiPerfilRecLargo());
    recursoDTO.setMiPerfilRecVencim(recurso.getMiPerfilRecVencim());
    recursoDTO.setMiPerfilRecHora(recurso.getMiPerfilRecHora());
    recursoDTO.setMiPerfilRecDias(recurso.getMiPerfilRecDias());
    
    return recursoDTO;
	}
	
	private Map<String,String> copiarDatos(Map<String, DatoReserva> valores, Recurso rec){
		Map<String, String> retorno = new HashMap<String, String>();
		List<DatoASolicitar> datosSol = rec.getDatoASolicitar();
		Iterator<DatoASolicitar> it = datosSol.iterator();
		while (it.hasNext()){
			DatoASolicitar dato = (DatoASolicitar)it.next();
			if (dato.getEsClave() && (dato.getFechaBaja()==null)){
				String valor = (String)valores.get(dato.getNombre()).getValor();
				retorno.put(dato.getNombre(), valor);
			}
		}
		return retorno;
	}

	private ValidadorReserva getValidador(String host, String jndiName) throws ApplicationException {
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
		} catch (NamingException e) {
			throw new ApplicationException("No se pudo acceder a un EJB de tipo ValidadorReserva (jndiName: "+jndiName+")", e);
    }
		ValidadorReserva validador = null;
		if (ejb instanceof ValidadorReserva) {
			validador = (ValidadorReserva) ejb;
		} else {
			throw new ApplicationException("Se esperaba un EJB de tipo ValidadorReserva y se encontró uno del tipo " + ejb.getClass());
		}
		return validador;
	}
	
	public Map<String, Object> autocompletarCampo(ServicioPorRecurso s, Map<String, Object> datosParam) throws ApplicationException, BusinessException, ErrorAutocompletarException, WarningAutocompletarException{
		Map<String, Object> campos = new HashMap<String, Object>();
		List<ServicioAutocompletarPorDato> lstDatos = s.getAutocompletadosPorDato();
		List<ParametrosAutocompletar> parametros = s.getAutocompletado().getParametrosAutocompletados();
		Map<String,Object> paramEntradaMap = new HashMap<String, Object>();
		Map<String,Object> paramSalidaMap = new HashMap<String, Object>();
		List<String> nombreCampos = new ArrayList<String>();
		//Se crean las estructuras para los parametros de entrada y salida, mapeando los parametros con los datos a solicitar
		for (ParametrosAutocompletar param : parametros){
			for (ServicioAutocompletarPorDato sDato : lstDatos){
				if ( (sDato.getFechaDesasociacion()) == null && (sDato.getNombreParametro().equals(param.getNombre())) ){
					if (ModoAutocompletado.ENTRADA.equals(param.getModo())){
						nombreCampos.add(sDato.getDatoASolicitar().getNombre());
						paramEntradaMap.put(param.getNombre(), datosParam.get(sDato.getDatoASolicitar().getNombre()));
					}else if (ModoAutocompletado.SALIDA.equals(param.getModo())){
						paramSalidaMap.put(param.getNombre(), sDato.getDatoASolicitar().getNombre());
					}
				}
			}
		}
		//Se obtiene el servicio de autocompletar que se va a ejecutar
		AutocompletadorReserva servicioAutocompletador = this.getAutocompletador(s.getAutocompletado().getHost(), s.getAutocompletado().getServicio());
		try {
			//Se invoca el servicio de autocompletar
			ResultadoAutocompletado resultado = servicioAutocompletador.autocompletarDatosReserva(s.getAutocompletado().getNombre(), paramEntradaMap);
			for (String resultKey : resultado.getResultados().keySet()){
				//Mapeo los valores devueltos para los parametros de salida con los datos a solicitar que se van a completar
				if (paramSalidaMap.containsKey(resultKey)){
					campos.put(paramSalidaMap.get(resultKey).toString(), resultado.getResultados().get(resultKey));
				}
			}
			if (resultado.getErrores().size() > 0) {
				List<String> mensajes = new ArrayList<String>();
				List<String> codigosErrorMensajes = new ArrayList<String>();
				for (ErrorAutocompletado error : resultado.getErrores()) {
					mensajes.add(error.getMensaje());
					codigosErrorMensajes.add(error.getCodigo());
				}
				throw new ErrorAutocompletarException("-1", nombreCampos, mensajes, codigosErrorMensajes, s.getAutocompletado().getNombre());
			}
			if (resultado.getErrores().size() > 0) {
				List<String> mensajes = new ArrayList<String>();
				List<String> codigosErrorMensajes = new ArrayList<String>();
				for (ErrorAutocompletado error : resultado.getErrores()) {
					mensajes.add(error.getMensaje());
					codigosErrorMensajes.add(error.getCodigo());
				}
				throw new ErrorAutocompletarException("-1", nombreCampos, mensajes, codigosErrorMensajes, s.getAutocompletado().getNombre());
			}
			if (resultado.getWarnings().size() > 0) {
				List<String> mensajes = new ArrayList<String>();
				List<String> codigosWarningMensajes = new ArrayList<String>();
				for (WarningAutocompletado warning : resultado.getWarnings()) {
					mensajes.add(warning.getMensaje());
					codigosWarningMensajes.add(warning.getCodigo());
				}
				throw new WarningAutocompletarException("-1", nombreCampos, mensajes, codigosWarningMensajes, s.getAutocompletado().getNombre());
			}
		} catch (UnexpectedAutocompletadoException e) {
			throw new ApplicationException(e);
		} catch (uy.gub.sae.autocompletados.business.ejb.exception.InvalidParametersException e) {
			List<String> mensajes = new ArrayList<String>();
			mensajes.add(e.getMessage());
			throw new ErrorAutocompletarException("-1", nombreCampos, mensajes);
		}
		return campos;
	}
	
	private AutocompletadorReserva getAutocompletador(String host, String jndiName) throws ApplicationException {
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
		} catch (NamingException e) {
			throw new ApplicationException("No se pudo acceder a un EJB de tipo AutocompletadorReserva (jndiName: "+jndiName+")", e);
    }
		AutocompletadorReserva autocompletador = null;
		if (ejb instanceof AutocompletadorReserva) {
			autocompletador = (AutocompletadorReserva) ejb;
		} else {
			throw new ApplicationException("Se esperaba un EJB de tipo AutocompletadorReserva y se encontró uno del tipo " + ejb.getClass());
		}
		return autocompletador;
	}
	
}


