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

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import uy.gub.imm.sae.business.dto.ResultadoEjecucion;
import uy.gub.imm.sae.business.utilidades.CamposCSV;
import uy.gub.imm.sae.business.utilidades.ConstantesDisponibilidad;
import uy.gub.imm.sae.business.utilidades.ConstantesRecurso;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.common.enumerados.FormaCancelacion;
import uy.gub.imm.sae.common.enumerados.Tipo;
import uy.gub.imm.sae.entity.AccionMiPerfil;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.DatoDelRecurso;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.RecursoAud;
import uy.gub.imm.sae.entity.RolesUsuarioRecurso;
import uy.gub.imm.sae.entity.RolesUsuarioRecursoId;
import uy.gub.imm.sae.entity.ServicioPorRecurso;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.entity.ValidacionPorDato;
import uy.gub.imm.sae.entity.ValidacionPorRecurso;
import uy.gub.imm.sae.entity.ValorPosible;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.exportar.AgrupacionDatoExport;
import uy.gub.imm.sae.exportar.DatoASolicitarExportar;
import uy.gub.imm.sae.exportar.ExportarHelper;
import uy.gub.imm.sae.exportar.RecursoExportar;
import uy.gub.imm.sae.exportar.ValorPosibleExport;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Stateless
@RolesAllowed({ "RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_ANONIMO", "RA_AE_LLAMADOR", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
public class RecursosBean implements RecursosLocal, RecursosRemote {
	
	static Logger logger = Logger.getLogger(RecursosBean.class);

	private EntityManager entityManager;
	
	@EJB
	private DisponibilidadesLocal disponibilidadEJB;
	
	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionLocal")
	private Configuracion confBean;
	
	@Resource
	private SessionContext ctx;
	
	private static final long LOCK_ID = 1616161616;
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    private Boolean recursoEsNuevo = Boolean.FALSE;
    private Integer nroLinea = 0;
    private Integer totalLineas = 0;
    private Integer cantLineasError = 0;
    private Integer cantLineasOK = 0;
    private Boolean errorLinea = null;
    
    private ResultadoEjecucion ret = new ResultadoEjecucion();

	/**
	 * Crea el recurso <b>r</b> asociándolo a la agenda <b>a</b>. Controla la
	 * unicidad del nombre del recurso entre todos los recursos vivos (fechaBaja
	 * == null). Se controla que la agenda se encuentre viva. Se permite setear
	 * todos sus atributos menos la fechaBaja. Controla: fechaInicio <> NULL
	 * fechaInicio <= fechaFin o fechaFin == NULL fechaInicioDisp <> NULL
	 * fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL fechaInicio <=
	 * fechaInicioDisp ventanaDiasMinimos > 0 ventanaCuposMinimos > 0
	 * cantDiasAGenerar > 0 largoListaEspera > 0 largo del campo Serie menor o
	 * igual a 3 cantDiasAGenerar >= ventanaDiasMinimos Retorna el recurso con
	 * su identificador interno. Controla que el usuario tenga rol Planificador
	 * sobre la agenda <b>a</b> Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 * @throws ApplicationException
	 * @throws BusinessException
	 */
	public Recurso crearRecurso(Agenda a, Recurso r, String codigoUsuario) throws UserException,
			ApplicationException, BusinessException {
		if (a == null) {
			throw new UserException("debe_haber_una_agenda_seleccionada");
		}
		Agenda aManejada = (Agenda) entityManager.find(Agenda.class, a.getId());
		if (aManejada == null) {
			throw new UserException("no_se_encuentra_la_agenda_especificada");
		}
		// No se puede dar de alta un recurso para una agenda cerrada
		if (aManejada.getFechaBaja() != null) {
			throw new UserException("la_agenda_especificada_no_es_valida");
		}
		a = aManejada;
		aManejada = null; // De aquí en mas utilizo "a".
		r.setAgenda(a);

		// Controla la unicidad del nombre del recurso entre todos los recursos
		// vivos (fechaBaja == null)
		// para la misma agenda.
		if (existeRecursoPorNombre(r)) {
			throw new UserException("ya_existe_un_recurso_con_el_nombre_especificado");
		}
		// fechaInicio <> NULL
		if (r.getFechaInicio() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}
		// Se setea hora en fecha de inicio 00:00:00
		r.setFechaInicio(Utiles.time2InicioDelDia(r.getFechaInicio()));
		// Si la fecha de Fin no es nula, se setea la hora al final del Día.
		if (r.getFechaFin() != null) {
			r.setFechaFin(Utiles.time2FinDelDia(r.getFechaFin()));
		}
		// fechaInicio <= fechaFin o fechaFin == NULL
		if ((r.getFechaFin() != null)
				&& (r.getFechaInicio().compareTo(r.getFechaFin()) > 0)) {
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}
		// fechaInicioDisp <> NULL
		if (r.getFechaInicioDisp() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}
		if (r.getSabadoEsHabil() == null) {
			r.setSabadoEsHabil(false);
		}
    if (r.getDomingoEsHabil() == null) {
      r.setDomingoEsHabil(false);
    }
		// Se setea hora en fecha de inicio disp. 00:00:00
		r.setFechaInicioDisp(Utiles.time2InicioDelDia(r.getFechaInicioDisp()));
		// Si la fecha de Fin de disponibilidad no es nula, se setea la hora al
		// final del Día.
		if (r.getFechaFinDisp() != null) {
			r.setFechaFinDisp(Utiles.time2FinDelDia(r.getFechaFinDisp()));
		}
		// fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL
		if ((r.getFechaFinDisp() != null)
				&& (r.getFechaInicioDisp().compareTo(r.getFechaFinDisp()) > 0)) {
			throw new UserException(
					"la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}
		// fechaInicio <= fechaInicioDisp
		if (r.getFechaInicio().compareTo(r.getFechaInicioDisp()) > 0) {
			throw new UserException(
					"la_fecha_de_inicio_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso");
		}
		// diasInicioVentanaIntranet >= 0
		if (r.getDiasInicioVentanaIntranet() == null) {
			throw new UserException(
					"los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (r.getDiasInicioVentanaIntranet() < 0) {
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_o_igual_a_cero");
		}
		// diasVentanaIntranet > 0
		if (r.getDiasVentanaIntranet() == null) {
			throw new UserException(
					"los_dias_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (r.getDiasVentanaIntranet() <= 0) {
			throw new UserException(
					"los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");
		}
		// diasInicioVentanaInternet >= 0
		if (r.getDiasInicioVentanaInternet() == null) {
			throw new UserException(
					"los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (r.getDiasInicioVentanaInternet() < 0) {
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_o_igual_a_cero");
		}
		// diasVentanaInternet > 0
		if (r.getDiasVentanaInternet() == null) {
			throw new UserException(
					"los_dias_de_la_ventana_de_internet_es_obligatorio");
		}
		if (r.getDiasVentanaInternet() <= 0) {
			throw new UserException(
					"los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero");
		}
		// ventanaCuposMinimos >= 0
		if (r.getVentanaCuposMinimos() == null) {
			throw new UserException(
					"la_cantidad_de_cupos_minimos_es_obligatoria");
		}
		if (r.getVentanaCuposMinimos() < 0) {
			throw new UserException(
					"la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero");
		}
		// cantDiasAGenerar > 0
		if (r.getCantDiasAGenerar() == null) {
			throw new UserException(
					"la_cantidad_de_dias_a_generar_es_obligatoria");
		}
		if (r.getCantDiasAGenerar() <= 0) {
			throw new UserException(
					"la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero");
		}
		// cantDiasAGenerar >= diasInicioVentanaIntranet + diasVentanaIntranet
		if (r.getCantDiasAGenerar().compareTo(
				r.getDiasInicioVentanaIntranet() + r.getDiasVentanaIntranet()) < 0) {
			throw new UserException(
					"la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet");
		}
		// cantDiasAGenerar >= diasInicioVentanaInternet + diasVentanaInternet
		if (r.getCantDiasAGenerar().compareTo(
				r.getDiasInicioVentanaInternet() + r.getDiasVentanaInternet()) < 0) {
			throw new UserException(
					"la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet");
		}
		// largoListaEspera > 0
		if ((r.getLargoListaEspera() != null) && (r.getLargoListaEspera() <= 0)) {
			throw new UserException(
					"el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero");
		}
		// se controla que el campo "usaLlamador" no sea null
		if (r.getUsarLlamador() == null) {
			r.setUsarLlamador(true);
		}
		
		
		//Controles de la acción MiPerfil
		//--------------------------------
		if (r.getAccionMiPerfil() != null) {
			
			//Controlo que haya una sola accion de confirmacion destacada
			if (BooleanUtils.isTrue(r.getMiPerfilConHab()) && getCantAccionMiPerfilDestacadasConfirmacion(r.getAccionMiPerfil()) != 1) {
				throw new UserException("debe_haber_una_unica_accion_de_confirmacion_destacada");
			}
			
			//Controlo que haya una sola accion de cancelacion destacada
			if (BooleanUtils.isTrue(r.getMiPerfilCanHab()) && getCantAccionMiPerfilDestacadasCancelacion(r.getAccionMiPerfil()) != 1) {
				throw new UserException("debe_haber_una_unica_accion_de_cancelacion_destacada");
			}
			
			//Controlo que haya una sola accion de recordatorio destacada
			if (BooleanUtils.isTrue(r.getMiPerfilRecHab()) && getCantAccionMiPerfilDestacadasRecordatorio(r.getAccionMiPerfil()) != 1) {
				throw new UserException("debe_haber_una_unica_accion_de_recordatorio_destacada");
			}
		
		}
		
		
		if (r.getReservaPendienteTiempoMax()!=null && r.getReservaPendienteTiempoMax() < 0 ){
			throw new UserException("reserva_pendiente_tiempo_max_debe_ser_mayor_a_cero");
			
		}
	    
	    
	    if (r.getReservaMultiplePendienteTiempoMax()!=null && r.getReservaMultiplePendienteTiempoMax() < 0 ){
	    	throw new UserException("reserva_multiple_pendiente_tiempo_max_debe_ser_mayor_a_cero");
		}
		
		
		entityManager.persist(r);
		
		//Guardar registro en histórico (crear Recurso)
		this.guardarHistoricoRecurso(r, codigoUsuario, 0);
		
		// paso a agregar agrupacion
		AgrupacionDato agrupDato = new AgrupacionDato();
		agrupDato.setNombre("datos_personales");
		agrupDato.setEtiqueta("Datos personales");
		agrupDato.setOrden(1);
		agrupDato.setBorrarFlag(false);
		agregarAgrupacionDato(r, agrupDato);

		// agrego datos a solicitar tipo documento
		DatoASolicitar d1 = new DatoASolicitar();
		d1.setNombre(DatoASolicitar.TIPO_DOCUMENTO);
		d1.setRequerido(true);
		d1.setFila(1);
		d1.setColumna(1);
		d1.setIncluirEnReporte(true);
		d1.setAgrupacionDato(agrupDato);
		d1.setAnchoDespliegue(100);
		d1.setEsClave(true);
		d1.setSoloLectura(false);
		d1.setEtiqueta("Tipo de documento");
		d1.setIncluirEnLlamador(true);
		d1.setLargo(20);
		d1.setLargoEnLlamador(20);
		d1.setOrdenEnLlamador(1);
		d1.setRecurso(r);
		d1.setRequerido(true);
		d1.setTipo(Tipo.LIST);
		d1.setBorrarFlag(false);
		// persisto dato a solicitar
		agregarDatoASolicitar(r, agrupDato, d1);
		// Ingreso valores posibles
		// creo valor cédula
		ValorPosible vp1 = new ValorPosible();
		vp1.setDato(d1);
		vp1.setEtiqueta("Cédula de Identidad");
		vp1.setFechaDesde(r.getFechaInicio());
		vp1.setFechaHasta(r.getFechaFin());
		vp1.setOrden(1);
		vp1.setValor("CI");
		vp1.setBorrarFlag(false);
		agregarValorPosible(d1, vp1);

		// creo valor pasaporte
		ValorPosible vp2 = new ValorPosible();
		vp2.setDato(d1);
		vp2.setEtiqueta("Pasaporte");
		vp2.setFechaDesde(r.getFechaInicio());
		vp2.setFechaHasta(r.getFechaFin());
		vp2.setOrden(2);
		vp2.setValor("P");
		vp2.setBorrarFlag(false);
		agregarValorPosible(d1, vp2);

		// creo valor pasaporte
		ValorPosible vp3 = new ValorPosible();
		vp3.setDato(d1);
		vp3.setEtiqueta("Otro");
		vp3.setFechaDesde(r.getFechaInicio());
		vp3.setFechaHasta(r.getFechaFin());
		vp3.setOrden(3);
		vp3.setValor("O");
		vp3.setBorrarFlag(false);
		agregarValorPosible(d1, vp3);

		// agrego datos a solicitar Nro. documento
		DatoASolicitar d2 = new DatoASolicitar();
		d2.setNombre(DatoASolicitar.NUMERO_DOCUMENTO);
		d2.setRequerido(true);
		d2.setFila(2);
		d2.setColumna(1);
		d2.setIncluirEnReporte(true);
		d2.setAgrupacionDato(agrupDato);
		d2.setAnchoDespliegue(120);
		d2.setEsClave(true);
		d2.setEtiqueta("Número de documento");
		d2.setIncluirEnLlamador(true);
		d2.setLargo(10);
		d2.setLargoEnLlamador(10);
		d2.setOrdenEnLlamador(2);
		d2.setRecurso(r);
		d2.setRequerido(true);
		d2.setTipo(Tipo.STRING);
		d2.setBorrarFlag(false);
		// persisto dato a solicitar
		agregarDatoASolicitar(r, agrupDato, d2);

		// agrego datos a solicitar Correo electrónico
		DatoASolicitar d3 = new DatoASolicitar();
		d3.setNombre(DatoASolicitar.CORREO_ELECTRONICO);
		d3.setRequerido(true);
		d3.setFila(3);
		d3.setColumna(1);
		d3.setIncluirEnReporte(true);
		d3.setAgrupacionDato(agrupDato);
		d3.setAnchoDespliegue(100);
		d3.setEsClave(false);
		d3.setEtiqueta("Correo electrónico");
		d3.setIncluirEnLlamador(false);
		d3.setLargo(100);
		d3.setLargoEnLlamador(150);
		d3.setOrdenEnLlamador(3);
		d3.setRecurso(r);
		d3.setRequerido(true);
		d3.setTipo(Tipo.STRING);
		d3.setBorrarFlag(false);
		// persisto dato a solicitar
		agregarDatoASolicitar(r, agrupDato, d3);
		
		
		//Si el recurso tiene una accion, debo guardarla (crearla)
		if (r.getAccionMiPerfil() != null) {
		
			//Creo la acción
			AccionMiPerfil nuevaAccion = r.getAccionMiPerfil(); 
			entityManager.persist(nuevaAccion);
		}

		
		return r;

	}

	public void guardarHistoricoRecurso(Recurso r, String codigoUsuario, Integer tipoOperacion) {
		RecursoAud recursoAud = new RecursoAud(r);
		recursoAud.setUsuario(codigoUsuario);
		recursoAud.setFechaModificacion(new Date());
		recursoAud.setTipoOperacion(tipoOperacion);
		
		entityManager.persist(recursoAud);
	}
	
	public Recurso crearRecursoImportado(Agenda agenda, Recurso recurso, String codigoUsuario) throws UserException,
			ApplicationException, BusinessException {
		if (agenda == null) {
			throw new UserException("debe_haber_una_agenda_seleccionada");
		}
		Agenda aManejada = (Agenda) entityManager.find(Agenda.class, agenda.getId());
		if (aManejada == null) {
			throw new UserException("no_se_encuentra_la_agenda_especificada");
		}
		// No se puede dar de alta un recurso para una agenda cerrada
		if (aManejada.getFechaBaja() != null) {
			throw new UserException("la_agenda_especificada_no_es_valida");
		}
		agenda = aManejada;
		aManejada = null;
		recurso.setAgenda(agenda);

		// Controla la unicidad del nombre del recurso entre todos los recursos
		// vivos (fechaBaja == null)
		// para la misma agenda.
		if (existeRecursoPorNombre(recurso)) {
			throw new UserException("ya_existe_un_recurso_con_el_nombre_especificado");
		}
		// fechaInicio <> NULL
		if (recurso.getFechaInicio() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}
		// Se setea hora en fecha de inicio 00:00:00
		recurso.setFechaInicio(Utiles.time2InicioDelDia(recurso.getFechaInicio()));
		// Si la fecha de Fin no es nula, se setea la hora al final del Día.
		if (recurso.getFechaFin() != null) {
		  recurso.setFechaFin(Utiles.time2FinDelDia(recurso.getFechaFin()));
		}
		// fechaInicio <= fechaFin o fechaFin == NULL
		if ((recurso.getFechaFin() != null) && (recurso.getFechaInicio().compareTo(recurso.getFechaFin()) > 0)) {
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}
		// fechaInicioDisp <> NULL
		if (recurso.getFechaInicioDisp() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}
		if (recurso.getSabadoEsHabil() == null) {
		  recurso.setSabadoEsHabil(false);
		}
    if (recurso.getDomingoEsHabil() == null) {
      recurso.setDomingoEsHabil(false);
    }
		// Se setea hora en fecha de inicio disp. 00:00:00
    recurso.setFechaInicioDisp(Utiles.time2InicioDelDia(recurso.getFechaInicioDisp()));
		// Si la fecha de Fin de disponibilidad no es nula, se setea la hora al
		// final del Día.
		if (recurso.getFechaFinDisp() != null) {
		  recurso.setFechaFinDisp(Utiles.time2FinDelDia(recurso.getFechaFinDisp()));
		}
		// fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL
		if ((recurso.getFechaFinDisp() != null)	&& (recurso.getFechaInicioDisp().compareTo(recurso.getFechaFinDisp()) > 0)) {
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}
		// fechaInicio <= fechaInicioDisp
		if (recurso.getFechaInicio().compareTo(recurso.getFechaInicioDisp()) > 0) {
			throw new UserException("la_fecha_de_inicio_debe_ser_igual_o_posterior_a_la_fecha_de_inicio_de_la_disponibilidad_del_recurso");
		}
		// diasInicioVentanaIntranet >= 0
		if (recurso.getDiasInicioVentanaIntranet() == null) {
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (recurso.getDiasInicioVentanaIntranet() < 0) {
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_intranet_debe_ser_mayor_o_igual_a_cero");
		}
		// diasVentanaIntranet > 0
		if (recurso.getDiasVentanaIntranet() == null) {
			throw new UserException("los_dias_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (recurso.getDiasVentanaIntranet() <= 0) {
			throw new UserException("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");
		}
		// diasInicioVentanaInternet >= 0
		if (recurso.getDiasInicioVentanaInternet() == null) {
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (recurso.getDiasInicioVentanaInternet() < 0) {
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_internet_debe_ser_mayor_o_igual_a_cero");
		}
		// diasVentanaInternet > 0
		if (recurso.getDiasVentanaInternet() == null) {
			throw new UserException("los_dias_de_la_ventana_de_internet_es_obligatorio");
		}
		if (recurso.getDiasVentanaInternet() <= 0) {
			throw new UserException("los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero");
		}
		// ventanaCuposMinimos >= 0
		if (recurso.getVentanaCuposMinimos() == null) {
			throw new UserException("la_cantidad_de_cupos_minimos_es_obligatoria");
		}
		if (recurso.getVentanaCuposMinimos() < 0) {
			throw new UserException("la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero");
		}
		// cantDiasAGenerar > 0
		if (recurso.getCantDiasAGenerar() == null) {
			throw new UserException("la_cantidad_de_dias_a_generar_es_obligatoria");
		}
		if (recurso.getCantDiasAGenerar() <= 0) {
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero");
		}
		// cantDiasAGenerar >= diasInicioVentanaIntranet + diasVentanaIntranet
		if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaIntranet() + recurso.getDiasVentanaIntranet()) < 0) {
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet");
		}
		// cantDiasAGenerar >= diasInicioVentanaInternet + diasVentanaInternet
		if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaInternet() + recurso.getDiasVentanaInternet()) < 0) {
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet");
		}
		// largoListaEspera > 0
		if ((recurso.getLargoListaEspera() != null) && (recurso.getLargoListaEspera() <= 0)) {
			throw new UserException("el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero");
		}
		// se controla que el campo "usaLlamador" no sea null
		if (recurso.getUsarLlamador() == null) {
		  recurso.setUsarLlamador(true);
		}
		
		
		if (recurso.getReservaPendienteTiempoMax()!=null && recurso.getReservaPendienteTiempoMax() < 0 ){
			throw new UserException("reserva_pendiente_tiempo_max_debe_ser_mayor_a_cero");
		}
	    
	    
	    if (recurso.getReservaMultiplePendienteTiempoMax()!=null && recurso.getReservaMultiplePendienteTiempoMax() < 0 ){
	    	throw new UserException("reserva_multiple_pendiente_tiempo_max_debe_ser_mayor_a_cero");
		}
		
		
		entityManager.persist(recurso);
		
		
		//Creo una accionMiPerfil por defecto para el recurso importado. Esto es para evitar errores, ya que al importar 
		//no se tienen en cuenta las acciones de MiPerfil (si algún dia se agrega lo de acciones MiPerfil al importar/exportar, esto se saca)
		AccionMiPerfil accionMiPerfil = obtenerAccionMiPerfilPorDefecto(recurso);
		entityManager.persist(accionMiPerfil);
		
		//Guardar registro en histórico (crea Recurso importado)
		this.guardarHistoricoRecurso(recurso, codigoUsuario, 0);
		
		return recurso;	

	}

	/**
	 * Modifica los datos del recurso <b>r</b>. Controla la unicidad del nombre
	 * del recurso entre todos los recursos vivos (fechaBaja == null). Se
	 * permite setear todos sus atributos menos la fechaBaja. Controla:
	 * fechaInicio <> NULL fechaInicio <= fechaFin o fechaFin == NULL
	 * fechaInicioDisp <> NULL fechaInicioDisp <= fechaFinDisp o fechaFinDisp ==
	 * NULL fechaInicio <= fechaInicioDisp ventanaDiasMinimos > 0
	 * ventanaCuposMinimos > 0 cantDiasAGenerar > 0 largoListaEspera > 0 El
	 * largo del campo Serie no puede ser mayor a 3 cantDiasAGenerar >=
	 * ventanaDiasMinimos Si reservaMultiple = True => se podrá cambiar su valor
	 * a reservaMultiple = FALSE solo si no existe reserva viva con más de una
	 * disponibilidad para ese recurso. No pueden quedar disponibilidades vivas
	 * fuera del período fechaInicioDisp y fechaFinDisp. No pueden quedar
	 * reservas vivas fuera del período fechaInicioDisp y fechaFinDisp. Retorna
	 * el recurso con su identificador interno. Controla que el usuario tenga
	 * rol Planificador sobre la agenda <b>a</b> Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 * @throws BusinessException
	 * @throws ApplicationException
	 */
	public void modificarRecurso(Recurso recurso, String codigoUsuario) throws UserException,
			BusinessException, ApplicationException {

		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class,	recurso.getId());
		boolean guardarHistorico = !recurso.equals(recursoActual);

		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}

		// Controla la unicidad del nombre del recurso entre todos los recursos
		// vivos (fechaBaja == null)
		// para la misma agenda.
		if (existeRecursoPorNombre(recurso)) {
			throw new UserException("ya_existe_un_recurso_con_el_nombre_especificado");
		}

		// fechaInicio <> NULL
		if (recurso.getFechaInicio() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}

		// Se setea hora en fecha de inicio 00:00:00
		recurso.setFechaInicio(Utiles.time2InicioDelDia(recurso.getFechaInicio()));

		// Si la fecha de Fin de disponibilidad no es nula, se setea la hora al
		// final del Día.
		if (recurso.getFechaFin() != null) {
			recurso.setFechaFin(Utiles.time2FinDelDia(recurso.getFechaFin()));
		}

		// fechaInicio <= fechaFin o fechaFin == NULL
		if (recurso.getFechaFin() == null) {
			throw new UserException("la_fecha_de_fin_es_obligatoria");
		}
		if (recurso.getFechaInicio().compareTo(recurso.getFechaFin()) > 0) {
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}

		// fechaInicioDisp <> NULL
		if (recurso.getFechaInicioDisp() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}

		// Se setea hora en fecha de inicio disp. 00:00:00
		recurso.setFechaInicioDisp(Utiles.time2InicioDelDia(recurso.getFechaInicioDisp()));
		// Si la fecha de Fin de disponibilidad no es nula, se setea la hora al final del Día.
		if (recurso.getFechaFinDisp() != null) {
			recurso.setFechaFinDisp(Utiles.time2FinDelDia(recurso.getFechaFinDisp()));
		}

		// fechaInicioDisp <= fechaFinDisp o fechaFinDisp == NULL
		if (recurso.getFechaFinDisp() == null) {
			throw new UserException("la_fecha_de_fin_es_obligatoria");
		}
		if (recurso.getFechaInicioDisp().compareTo(recurso.getFechaFinDisp()) > 0) {
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}

		// fechaInicio <= fechaInicioDisp
		if (recurso.getFechaInicio().compareTo(recurso.getFechaInicioDisp()) > 0) {
			throw new UserException("la_fecha_de_inicio_de_disponibilidad_debe_ser_posterior_a_la_fecha_de_inicio");
		}

		// diasInicioVentanaIntranet >= 0
		if (recurso.getDiasInicioVentanaIntranet() == null) {
			throw new UserException("los_dias_de_inicio_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (recurso.getDiasInicioVentanaIntranet() < 0) {
			throw new UserException("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");
		}

		// diasVentanaIntranet > 0
		if (recurso.getDiasVentanaIntranet() == null) {
			throw new UserException("los_dias_de_la_ventana_de_intranet_es_obligatorio");
		}
		if (recurso.getDiasVentanaIntranet() <= 0) {
			throw new UserException("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");
		}

		// diasInicioVentanaInternet >= 0
		if (recurso.getDiasInicioVentanaInternet() == null) {
			throw new UserException("los_dias_de_la_ventana_de_internet_es_obligatorio");
		}
		if (recurso.getDiasInicioVentanaInternet() < 0) {
			throw new UserException("los_dias_de_la_ventana_de_internet_debe_ser_mayor_a_cero");
		}

		// diasVentanaInternet > 0
		if (recurso.getDiasVentanaInternet() == null) {
			throw new UserException("los_dias_de_la_ventana_de_internet_es_obligatorio");
		}
		if (recurso.getDiasVentanaInternet() <= 0) {
			throw new UserException("los_dias_de_la_ventana_de_intranet_debe_ser_mayor_a_cero");
		}

		// ventanaCuposMinimos >= 0
		if ((recurso.getVentanaCuposMinimos() == null)
				|| (recurso.getVentanaCuposMinimos() < 0)) {
			throw new UserException("la_cantidad_de_cupos_minimos_debe_ser_mayor_o_igual_a_cero");
		}

		// cantDiasAGenerar > 0
		if ((recurso.getCantDiasAGenerar() == null) || (recurso.getCantDiasAGenerar() <= 0)) {
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_a_cero");
		}

		// cantDiasAGenerar >= diasInicioVentanaIntranet + diasVentanaIntranet
		if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaIntranet() + recurso.getDiasVentanaIntranet()) < 0) {
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_intranet");
		}

		// cantDiasAGenerar >= diasInicioVentanaInternet + diasVentanaInternet
		if (recurso.getCantDiasAGenerar().compareTo(recurso.getDiasInicioVentanaInternet() + recurso.getDiasVentanaInternet()) < 0) {
			throw new UserException("la_cantidad_de_dias_a_generar_debe_ser_mayor_o_igual_que_la_suma_internet");
		}

		// largoListaEspera > 0
		if ((recurso.getLargoListaEspera() != null) && (recurso.getLargoListaEspera() <= 0)) {
			throw new UserException("el_largo_de_la_lista_de_espera_debe_ser_mayor_que_cero");
		}

		// No pueden quedar disponibilidades vivas fuera del período fechaInicioDisp y fechaFinDisp.
		if (hayDisponibilidadesVivasFueraDelPeriodo(recurso.getId(), recurso.getFechaInicioDisp(),	recurso.getFechaFinDisp())) {
			throw new UserException("no_se_puede_modificar_las_fechas_porque_quedarian_disponibilidades_fuera_del_periodo_especificado");
		}

		// No pueden quedar reservas vivas fuera del período fechaInicioDisp y fechaFinDisp.
		if (hayReservasVivasFueraDelPeriodo(recurso.getId(), recurso.getFechaInicioDisp(),	recurso.getFechaFinDisp())) {
			throw new UserException("no_se_puede_modificar_las_fechas_porque_quedarian_reservas_fuera_del_periodo_especificado");
		}

		// Se controla que la serie no tenga largo mayor a 3
		if ((recurso.getSerie() != null) && (recurso.getSerie().length() > 3)) {
			throw new UserException("AE10028", "El largo del campo serie no puede ser mayor a 3");
		}
		
		//Controles de la acción MiPerfil
		//--------------------------------
		if (recurso.getAccionMiPerfil() != null) {
			
			//Controlo que haya una sola accion de confirmacion destacada
			if (BooleanUtils.isTrue(recurso.getMiPerfilConHab()) && getCantAccionMiPerfilDestacadasConfirmacion(recurso.getAccionMiPerfil()) != 1) {
				throw new UserException("debe_haber_una_unica_accion_de_confirmacion_destacada");
			}
			
			//Controlo que haya una sola accion de cancelacion destacada
			if (BooleanUtils.isTrue(recurso.getMiPerfilCanHab()) && getCantAccionMiPerfilDestacadasCancelacion(recurso.getAccionMiPerfil()) != 1) {
				throw new UserException("debe_haber_una_unica_accion_de_cancelacion_destacada");
			}
			
			//Controlo que haya una sola accion de recordatorio destacada
			if (BooleanUtils.isTrue(recurso.getMiPerfilRecHab()) && getCantAccionMiPerfilDestacadasRecordatorio(recurso.getAccionMiPerfil()) != 1) {
				throw new UserException("debe_haber_una_unica_accion_de_recordatorio_destacada");
			}
		
		}
		
		
		if (recurso.getReservaPendienteTiempoMax()!=null && recurso.getReservaPendienteTiempoMax() < 0 ){
			throw new UserException("reserva_pendiente_tiempo_max_debe_ser_mayor_a_cero");
			
		}
	    
	    
	    if (recurso.getReservaMultiplePendienteTiempoMax()!=null && recurso.getReservaMultiplePendienteTiempoMax() < 0 ){
	    	throw new UserException("reserva_multiple_pendiente_tiempo_max_debe_ser_mayor_a_cero");
		}
		

		recursoActual.setNombre(recurso.getNombre());
		recursoActual.setDescripcion(recurso.getDescripcion());
		recursoActual.setFechaInicio(recurso.getFechaInicio());
		recursoActual.setFechaFin(recurso.getFechaFin());
		recursoActual.setFechaInicioDisp(recurso.getFechaInicioDisp());
		recursoActual.setFechaFinDisp(recurso.getFechaFinDisp());
		recursoActual.setDiasInicioVentanaIntranet(recurso.getDiasInicioVentanaIntranet());
		recursoActual.setDiasVentanaIntranet(recurso.getDiasVentanaIntranet());
		recursoActual.setDiasInicioVentanaInternet(recurso.getDiasInicioVentanaInternet());
		recursoActual.setDiasVentanaInternet(recurso.getDiasVentanaInternet());
		recursoActual.setVentanaCuposMinimos(recurso.getVentanaCuposMinimos());
		recursoActual.setCantDiasAGenerar(recurso.getCantDiasAGenerar());
		recursoActual.setLargoListaEspera(recurso.getLargoListaEspera());
		recursoActual.setSerie(recurso.getSerie());
		recursoActual.setVisibleInternet(recurso.getVisibleInternet());
		recursoActual.setMostrarNumeroEnLlamador(recurso.getMostrarNumeroEnLlamador());
    recursoActual.setMostrarIdEnTicket(recurso.getMostrarIdEnTicket());
		recursoActual.setMostrarNumeroEnTicket(recurso.getMostrarNumeroEnTicket());
		recursoActual.setFuenteTicket(recurso.getFuenteTicket());
		recursoActual.setTamanioFuenteChica(recurso.getTamanioFuenteChica());
    recursoActual.setTamanioFuenteNormal(recurso.getTamanioFuenteNormal());
    recursoActual.setTamanioFuenteGrande(recurso.getTamanioFuenteGrande());
		recursoActual.setSabadoEsHabil(recurso.getSabadoEsHabil());
    recursoActual.setDomingoEsHabil(recurso.getDomingoEsHabil());

		recursoActual.setLocalidad(recurso.getLocalidad());
		recursoActual.setDepartamento(recurso.getDepartamento());
		recursoActual.setDireccion(recurso.getDireccion());
		recursoActual.setTelefonos(recurso.getTelefonos());
		recursoActual.setHorarios(recurso.getHorarios());
		recursoActual.setLatitud(recurso.getLatitud());
		recursoActual.setLongitud(recurso.getLongitud());
		
		recursoActual.setPresencialAdmite(recurso.getPresencialAdmite());
    recursoActual.setPresencialCupos(recurso.getPresencialCupos());
    recursoActual.setPresencialLunes(recurso.getPresencialLunes());
    recursoActual.setPresencialMartes(recurso.getPresencialMartes());
    recursoActual.setPresencialMiercoles(recurso.getPresencialMiercoles());
    recursoActual.setPresencialJueves(recurso.getPresencialJueves());
    recursoActual.setPresencialViernes(recurso.getPresencialViernes());
    recursoActual.setPresencialSabado(recurso.getPresencialSabado());

    recursoActual.setMultipleAdmite(recurso.getMultipleAdmite());
    recursoActual.setCambiosAdmite(recurso.getCambiosAdmite());
    recursoActual.setCambiosTiempo(recurso.getCambiosTiempo());
    recursoActual.setCambiosUnidad(recurso.getCambiosUnidad());
    recursoActual.setPeriodoValidacion(recurso.getPeriodoValidacion());
    
    recursoActual.setValidarPorIP(recurso.getValidarPorIP());
    recursoActual.setCantidadPorIP(recurso.getCantidadPorIP());
    recursoActual.setPeriodoPorIP(recurso.getPeriodoPorIP());
    recursoActual.setIpsSinValidacion(recurso.getIpsSinValidacion());
    
    recursoActual.setCancelacionTiempo(recurso.getCancelacionTiempo()); 
    recursoActual.setCancelacionUnidad(recurso.getCancelacionUnidad()); 
    recursoActual.setCancelacionTipo(recurso.getCancelacionTipo()); 
    
    recursoActual.setMiPerfilConHab(recurso.getMiPerfilConHab());
    recursoActual.setMiPerfilConTitulo(recurso.getMiPerfilConTitulo());
    recursoActual.setMiPerfilConCorto(recurso.getMiPerfilConCorto());
    recursoActual.setMiPerfilConLargo(recurso.getMiPerfilConLargo());
    recursoActual.setMiPerfilConVencim(recurso.getMiPerfilConVencim());
    recursoActual.setMiPerfilCanHab(recurso.getMiPerfilCanHab());
    recursoActual.setMiPerfilCanTitulo(recurso.getMiPerfilCanTitulo());
    recursoActual.setMiPerfilCanCorto(recurso.getMiPerfilCanCorto());
    recursoActual.setMiPerfilCanLargo(recurso.getMiPerfilCanLargo());
    recursoActual.setMiPerfilCanVencim(recurso.getMiPerfilCanVencim());
    recursoActual.setMiPerfilRecHab(recurso.getMiPerfilRecHab());
    recursoActual.setMiPerfilRecTitulo(recurso.getMiPerfilRecTitulo());
    recursoActual.setMiPerfilRecCorto(recurso.getMiPerfilRecCorto());
    recursoActual.setMiPerfilRecLargo(recurso.getMiPerfilRecLargo());
    recursoActual.setMiPerfilRecVencim(recurso.getMiPerfilRecVencim());
    recursoActual.setMiPerfilRecHora(recurso.getMiPerfilRecHora());
    recursoActual.setMiPerfilRecDias(recurso.getMiPerfilRecDias());
    
    recursoActual.setReservaPendienteTiempoMax(recurso.getReservaPendienteTiempoMax());
    recursoActual.setReservaMultiplePendienteTiempoMax(recurso.getReservaMultiplePendienteTiempoMax());
    
		for (TextoRecurso viejo : recursoActual.getTextosRecurso().values()) {
			entityManager.remove(viejo);
		}
		recursoActual.setTextosRecurso(new HashMap<String, TextoRecurso>());
		if (recurso.getTextosRecurso() != null) {
			for (String idioma : recurso.getTextosRecurso().keySet()) {
				TextoRecurso viejo = recurso.getTextosRecurso().get(idioma);
				TextoRecurso nuevo = new TextoRecurso();

				nuevo.setRecurso(viejo.getRecurso());
				nuevo.setIdioma(viejo.getIdioma());
				nuevo.setTextoPaso2(viejo.getTextoPaso2());
				nuevo.setTextoPaso3(viejo.getTextoPaso3());
				nuevo.setTituloCiudadanoEnLlamador(viejo.getTituloCiudadanoEnLlamador());
				nuevo.setTituloPuestoEnLlamador(viejo.getTituloPuestoEnLlamador());
				nuevo.setTicketEtiquetaUno(viejo.getTicketEtiquetaUno());
				nuevo.setTicketEtiquetaDos(viejo.getTicketEtiquetaDos());
				nuevo.setValorEtiquetaUno(viejo.getValorEtiquetaUno());
				nuevo.setValorEtiquetaDos(viejo.getValorEtiquetaDos());
				recursoActual.getTextosRecurso().put(idioma, nuevo);
				entityManager.persist(nuevo);
			}
		}

		entityManager.merge(recursoActual);
		
		
		//Si el recurso tiene una accion, debo guardarla
		if (recurso.getAccionMiPerfil() != null) {
			AccionMiPerfil accionActual = null;
    		if(recurso.getAccionMiPerfil().getId() != null) {
    		    accionActual = (AccionMiPerfil) entityManager.find(AccionMiPerfil.class, recurso.getAccionMiPerfil().getId());
                //Actualizo la acción
                accionActual.setDestacada_con_1(recurso.getAccionMiPerfil().getDestacada_con_1());
                accionActual.setDestacada_con_2(recurso.getAccionMiPerfil().getDestacada_con_2());
                accionActual.setDestacada_con_3(recurso.getAccionMiPerfil().getDestacada_con_3());
                accionActual.setDestacada_con_4(recurso.getAccionMiPerfil().getDestacada_con_4());
                accionActual.setDestacada_con_5(recurso.getAccionMiPerfil().getDestacada_con_5());
                accionActual.setDestacada_can_1(recurso.getAccionMiPerfil().getDestacada_can_1());
                accionActual.setDestacada_can_2(recurso.getAccionMiPerfil().getDestacada_can_2());
                accionActual.setDestacada_can_3(recurso.getAccionMiPerfil().getDestacada_can_3());
                accionActual.setDestacada_can_4(recurso.getAccionMiPerfil().getDestacada_can_4());
                accionActual.setDestacada_can_5(recurso.getAccionMiPerfil().getDestacada_can_5());
                accionActual.setDestacada_rec_1(recurso.getAccionMiPerfil().getDestacada_rec_1());
                accionActual.setDestacada_rec_2(recurso.getAccionMiPerfil().getDestacada_rec_2());
                accionActual.setDestacada_rec_3(recurso.getAccionMiPerfil().getDestacada_rec_3());
                accionActual.setDestacada_rec_4(recurso.getAccionMiPerfil().getDestacada_rec_4());
                accionActual.setDestacada_rec_5(recurso.getAccionMiPerfil().getDestacada_rec_5());
                
                accionActual.setTitulo_con_1(recurso.getAccionMiPerfil().getTitulo_con_1());
                accionActual.setTitulo_con_2(recurso.getAccionMiPerfil().getTitulo_con_2());
                accionActual.setTitulo_con_3(recurso.getAccionMiPerfil().getTitulo_con_3());
                accionActual.setTitulo_con_4(recurso.getAccionMiPerfil().getTitulo_con_4());
                accionActual.setTitulo_con_5(recurso.getAccionMiPerfil().getTitulo_con_5());
                accionActual.setTitulo_can_1(recurso.getAccionMiPerfil().getTitulo_can_1());
                accionActual.setTitulo_can_2(recurso.getAccionMiPerfil().getTitulo_can_2());
                accionActual.setTitulo_can_3(recurso.getAccionMiPerfil().getTitulo_can_3());
                accionActual.setTitulo_can_4(recurso.getAccionMiPerfil().getTitulo_can_4());
                accionActual.setTitulo_can_5(recurso.getAccionMiPerfil().getTitulo_can_5());
                accionActual.setTitulo_rec_1(recurso.getAccionMiPerfil().getTitulo_rec_1());
                accionActual.setTitulo_rec_2(recurso.getAccionMiPerfil().getTitulo_rec_2());
                accionActual.setTitulo_rec_3(recurso.getAccionMiPerfil().getTitulo_rec_3());
                accionActual.setTitulo_rec_4(recurso.getAccionMiPerfil().getTitulo_rec_4());
                accionActual.setTitulo_rec_5(recurso.getAccionMiPerfil().getTitulo_rec_5());
                
                accionActual.setUrl_con_1(recurso.getAccionMiPerfil().getUrl_con_1());
                accionActual.setUrl_con_2(recurso.getAccionMiPerfil().getUrl_con_2());
                accionActual.setUrl_con_3(recurso.getAccionMiPerfil().getUrl_con_3());
                accionActual.setUrl_con_4(recurso.getAccionMiPerfil().getUrl_con_4());
                accionActual.setUrl_con_5(recurso.getAccionMiPerfil().getUrl_con_5());
                accionActual.setUrl_can_1(recurso.getAccionMiPerfil().getUrl_can_1());
                accionActual.setUrl_can_2(recurso.getAccionMiPerfil().getUrl_can_2());
                accionActual.setUrl_can_3(recurso.getAccionMiPerfil().getUrl_can_3());
                accionActual.setUrl_can_4(recurso.getAccionMiPerfil().getUrl_can_4());
                accionActual.setUrl_can_5(recurso.getAccionMiPerfil().getUrl_can_5());
                accionActual.setUrl_rec_1(recurso.getAccionMiPerfil().getUrl_rec_1());
                accionActual.setUrl_rec_2(recurso.getAccionMiPerfil().getUrl_rec_2());
                accionActual.setUrl_rec_3(recurso.getAccionMiPerfil().getUrl_rec_3());
                accionActual.setUrl_rec_4(recurso.getAccionMiPerfil().getUrl_rec_4());
                accionActual.setUrl_rec_5(recurso.getAccionMiPerfil().getUrl_rec_5());
                    
                entityManager.merge(accionActual);
    		}else {
				//Creo la acción 
				accionActual = new AccionMiPerfil(); 
				accionActual.setRecurso(recursoActual);
				entityManager.persist(accionActual);
			}

			
		}
		
		//Guardar registro en histórico (modificar Recurso)
		if (guardarHistorico) {
			this.guardarHistoricoRecurso(recursoActual, codigoUsuario, 1);
		}

	}

	/**
	 * Realiza una baja lógica del recurso <b>r</b> (se setea fechaBaja con la
	 * fecha actual del sistema). Controla que no existan disponibilidades vivas
	 * (fechaBaja == null) y futuras (fecha > fecha actual del sistema).
	 * Controla que no existan reservas vivas (estado R o P). Controla que no
	 * existan agrupaciones de datos en el recurso Controla que no existan
	 * Validaciones aplicadas en el recurso Controla que el usuario tenga rol
	 * Planificador sobre la agenda asociada. Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public void eliminarRecurso(Recurso recurso, TimeZone timezone, String codigoUsuario) throws UserException, ApplicationException {
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class,	recurso.getId());
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		//Se controla que no existan reservas vivas para el recurso.
		if (hayReservasVivas(recursoActual, timezone)) {
			throw new UserException("no_se_puede_eliminar_el_recurso_porque_hay_reservas_vivas");
		}
		//Se eliminan disponibilidades.
		List<Disponibilidad> disponibilades = (List<Disponibilidad>) entityManager
			.createQuery(
				"SELECT d FROM Disponibilidad d "
						+ "WHERE d.recurso = :recurso "
						+ "  AND d.fecha >= :fecha"
						+ "  AND d.horaFin >= :hora"
						+ "  AND d.fechaBaja is null")
			.setParameter("recurso", recursoActual)
			.setParameter("fecha", new Date())
			.setParameter("hora", new Date()).getResultList();
		for (Disponibilidad disponibilidad : disponibilades) {
			disponibilidad.setFechaBaja(new Date());
		}
		//Se eliminan agrupaciones de datos
		List<AgrupacionDato> listaAgrupacion = (List<AgrupacionDato>) entityManager
				.createQuery("SELECT ad FROM AgrupacionDato ad "
					+ "WHERE ad.recurso = :recurso AND ad.fechaBaja is null")
				.setParameter("recurso", recursoActual).getResultList();
		for (AgrupacionDato agrupacionDato : listaAgrupacion) {
			List<DatoASolicitar> lisrDatoAsolicitar = agrupacionDato.getDatosASolicitar();
			for (DatoASolicitar datoASolicitar : lisrDatoAsolicitar) {
				datoASolicitar.setFechaBaja(new Date());
			}
			agrupacionDato.setFechaBaja(new Date());
		}

		//Se eliminan las validaciones asociadas
		List<ValidacionPorRecurso> listValidacionRecurso = (List<ValidacionPorRecurso>) entityManager
				.createQuery("SELECT vxr FROM ValidacionPorRecurso vxr "
					+ "WHERE vxr.recurso = :recurso AND vxr.fechaBaja is null")
				.setParameter("recurso", recursoActual).getResultList();
		for (ValidacionPorRecurso validacionPorRecurso : listValidacionRecurso) {
			validacionPorRecurso.setFechaBaja(new Date());
		}
		recursoActual.setFechaBaja(new Date());
		
		//Guardar registro en histórico (eliminar Recurso)
		this.guardarHistoricoRecurso(recursoActual, codigoUsuario, 2);
	}

	public Recurso consultarRecurso(Recurso r) throws UserException {
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class,
				r.getId());
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		return recursoActual;

	}

	public List<Recurso> consultarRecursoByAgendaId(int agendaId) throws UserException {
		List<Recurso> recursos = entityManager.createQuery("SELECT r FROM Recurso r "
						+ "WHERE r.agenda.id = :agendaId and r.fechaBaja is null", Recurso.class)
				.setParameter("agendaId", agendaId).getResultList();
		if (recursos == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		return recursos;
	}

	/**
	 * Agrega un DatoDelRecurso <b>d</b> asociándolo al recurso <b>r</b>.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del
	 * recurso <b>r</b> Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 */
	public DatoDelRecurso agregarDatoDelRecurso(Recurso r, DatoDelRecurso d) throws UserException {
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		d.setRecurso(recursoActual);
		entityManager.persist(d);
		return d;
	}

	/**
	 * Modifica los datos del DatoDelRecurso <b>d</b>. Controla que el usuario
	 * tenga rol Planificador sobre la agenda del recurso asociado a <b>d</b>
	 * Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 */
	public void modificarDatoDelRecurso(DatoDelRecurso d) throws UserException {

		DatoDelRecurso datoActual = (DatoDelRecurso) entityManager.find(
				DatoDelRecurso.class, d.getId());

		if (datoActual == null) {
			throw new UserException("AE10033",
					"No existe la información del recurso que se quiere modificar: "
							+ d.getId().toString());
		}

		// No se puede modificar un dato de un recurso con fecha de baja
		if (datoActual.getRecurso().getFechaBaja() != null) {
			throw new UserException("AE10024",
					"No se puede modificar un recurso con fecha de baja");
		}

		if (d.getEtiqueta() == null) {
			throw new UserException("AE10034",
					"La etiqueta de la información del recurso no puede ser nula");
		}

		if (d.getOrden() == null || d.getOrden().intValue() < 0) {
			throw new UserException("AE10035",
					"El orden de la información del recurso debe ser mayor o igual a cero");
		}

		if (d.getValor() == null || d.getValor().compareTo("") == 0) {
			throw new UserException("AE10036",
					"El valor de la información del recurso no puede ser nulo");
		}

		datoActual.setEtiqueta(d.getEtiqueta());
		datoActual.setOrden(d.getOrden());
		datoActual.setValor(d.getValor());
	}

	/**
	 * Realiza una baja física del DatoDelRecurso <b>d</b> Controla que el
	 * usuario tenga rol Planificador sobre la agenda del recurso asociado a
	 * <b>d</b>. Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 */
	public void eliminarDatoDelRecurso(DatoDelRecurso d) throws UserException {

		DatoDelRecurso datoActual = (DatoDelRecurso) entityManager.find(
				DatoDelRecurso.class, d.getId());

		if (datoActual == null) {
			throw new UserException("AE10033",
					"No existe la información del recurso: "
							+ d.getId().toString());
		}
		entityManager.remove(datoActual);
	}

	/**
	 * Retorna una lista de datos del recurso Controla que el usuario tenga rol
	 * Administrador/Planificador sobre la agenda <b>a</b> del recurso Roles
	 * permitidos: Administrador, Planificador
	 */
	@SuppressWarnings("unchecked")
	@RolesAllowed({ "RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_ANONIMO", "RA_AE_FCALL_CENTER", "RA_AE_FATENCION", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
	public List<DatoDelRecurso> consultarDatosDelRecurso(Recurso r)
			throws ApplicationException, BusinessException {

		if (r == null) {
			throw new BusinessException("-1", "Parametro nulo");
		}

		r = entityManager.find(Recurso.class, r.getId());
		if (r == null) {
			throw new BusinessException("-1",
					"No se encuentra la agenda indicada");
		}

		try {
			List<DatoDelRecurso> datosDelRecurso = (List<DatoDelRecurso>) entityManager
					.createQuery(
							"SELECT d from DatoDelRecurso d "
									+ "WHERE d.recurso = :r "
									+ "ORDER BY d.orden").setParameter("r", r)
					.getResultList();
			return datosDelRecurso;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * Agrega una AgrupacionDato <b>d</b>. Controla que el usuario tenga rol
	 * Planificador sobre la agenda del recurso <b>r</b> Roles permitidos:
	 * Planificador
	 * 
	 * @throws UserException
	 * @throws ApplicationException
	 */
	public AgrupacionDato agregarAgrupacionDato(Recurso r, AgrupacionDato a) throws UserException, ApplicationException {
		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		if (recursoActual.getFechaBaja() != null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		a.setRecurso(r);
    if (a.getNombre() == null || a.getNombre().isEmpty()) {
      throw new UserException("el_nombre_de_la_agrupacion_es_obligatorio");
    }else {
  		if (existeAgrupacionPorNombre(a)) {
  			throw new UserException("ya_existe_una_agrupacion_con_el_nombre_especificado");
  		}
    }
    if(a.getEtiqueta() == null || a.getEtiqueta().isEmpty()) {
      throw new UserException("la_etiqueta_de_la_agrupacion_es_obligatoria");
    }
		if (a.getOrden() == null) {
			throw new UserException("el_orden_de_la_agrupacion_es_obligatorio");
		} else {
			if (a.getOrden() < 1) {
				throw new UserException("el_orden_de_la_agrupacion_debe_ser_mayor_a_cero");
			}
		}
		entityManager.persist(a);
		return a;
	}
	
	public AgrupacionDato agregarAgrupacionDatoImportar(Recurso recursoActual, AgrupacionDato a) throws UserException, ApplicationException {
		if (recursoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		if (recursoActual.getFechaBaja() != null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		a.setRecurso(recursoActual);
    if (a.getNombre() == null || a.getNombre().isEmpty()) {
      throw new UserException("el_nombre_de_la_agrupacion_es_obligatorio");
    }else {
  		if (existeAgrupacionPorNombre(a)) {
  			throw new UserException("ya_existe_una_agrupacion_con_el_nombre_especificado");
  		}
    }
    if(a.getEtiqueta() == null || a.getEtiqueta().isEmpty()) {
      throw new UserException("la_etiqueta_de_la_agrupacion_es_obligatoria");
    }
		
		if (a.getOrden() == null) {
			throw new UserException("el_orden_de_la_agrupacion_es_obligatorio");
		} else {
			if (a.getOrden() < 1) {
				throw new UserException(
						"el_orden_de_la_agrupacion_debe_ser_mayor_a_cero");
			}
		}
		entityManager.persist(a);
		return a;
	}
	
	/**
	 * Modifica Agrupaciones del dato <b>d</b>. Si tiene fecha baja no se puede
	 * modificar. Controla que el usuario tenga rol Planificador sobre la agenda
	 * del recurso asociado a <b>d</b> Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 */
	public void modificarAgrupacionDato(AgrupacionDato a) throws UserException, ApplicationException {

		AgrupacionDato agrupacionActual = (AgrupacionDato) entityManager.find(AgrupacionDato.class, a.getId());

		if (agrupacionActual == null) {
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}

		if (a.getNombre() == null || a.getNombre().isEmpty()) {
			throw new UserException("el_nombre_de_la_agrupacion_es_obligatorio");
		}else {
		  if(existeAgrupacionPorNombre(a)) {
		    throw new UserException("ya_existe_una_agrupacion_con_el_nombre_especificado");
		  }
		}
    if (a.getEtiqueta() == null || a.getEtiqueta().isEmpty()) {
      throw new UserException("la_etiqueta_de_la_agrupacion_es_obligatoria");
    }

		if (a.getOrden() == null) {
			throw new UserException("el_orden_de_la_agrupacion_es_obligatorio");
		} else {
			if (a.getOrden().intValue() < 0) {
				throw new UserException("el_orden_de_la_agrupacion_debe_ser_mayor_a_cero");
			}
		}
		if (agrupacionActual.getFechaBaja() != null) {
			throw new UserException("no_se_puede_modifcar_una_agrupacion_eliminada");
		}

		agrupacionActual.setNombre(a.getNombre());
    agrupacionActual.setEtiqueta(a.getEtiqueta());
		agrupacionActual.setOrden(a.getOrden());

	}

	/**
	 * Realiza una baja logica de agrupaciones del dato <b>d</b>. Controla que
	 * el usuario tenga rol Planificador sobre la agenda del recurso asociado a
	 * <b>d</b>. Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 */
	public void eliminarAgrupacionDato(AgrupacionDato a, boolean controlarDatos)
			throws UserException, ApplicationException {

		AgrupacionDato agrupacionActual = (AgrupacionDato) entityManager.find(AgrupacionDato.class, a.getId());

		if (agrupacionActual == null) {
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}
		if (agrupacionActual.getFechaBaja() != null) {
			throw new UserException("la_agrupacion_ya_esta_eliminada");
		}
		if (controlarDatos) {
			if (existeDatoASolicPorAgrupacion(a.getId())) {
				throw new UserException("no_se_puede_eliminar_la_agrupación_porque_tiene_datos_asociados");
			}
		} else {
			if (agrupacionActual.getDatosASolicitar() != null) {
				for (DatoASolicitar dato : agrupacionActual.getDatosASolicitar()) {
					eliminarDatoASolicitar(dato);
				}
			}
		}

		agrupacionActual.setFechaBaja(new Date());
	}

	/**
	 * Retorna una lista de agrupaciones de datos del recurso Controla que el
	 * usuario tenga rol Administrador/Planificador sobre la agenda <b>a</b> del
	 * recurso Roles permitidos: Administrador, Planificador
	 * 
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<AgrupacionDato> consultarAgrupacionesDatos(Recurso r)
			throws ApplicationException {
		try {
			List<AgrupacionDato> agrupacionDato = (List<AgrupacionDato>) entityManager
				.createQuery("SELECT a from AgrupacionDato a "
					+ "WHERE a.recurso = :r "
					+ "AND a.fechaBaja is null "
					+ "ORDER BY a.orden").setParameter("r", r)
				.getResultList();
			return agrupacionDato;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	/**
	 * Agrega un DatoASolicitar <b>d</b> asociándolo al recurso <b>r</b>.
	 * Controla que no exista otro dato vivo (fechaBaja == null) con el mismo
	 * nombre. Se permite setear todos sus atributos menos la fechaBaja.
	 * Controla que el usuario tenga rol Planificador sobre la agenda del
	 * recurso <b>r</b> Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 * @throws ApplicationException
	 */
	public DatoASolicitar agregarDatoASolicitar(Recurso r, AgrupacionDato a,
			DatoASolicitar d) throws UserException, ApplicationException,
			BusinessException {

		Recurso recursoActual = (Recurso) entityManager.find(Recurso.class, r.getId());

		if (recursoActual == null) {
			throw new BusinessException("no_se_encuentra_el_recurso_especificado");
		}

		recursoActual.getAgrupacionDatos().size();

		if (a == null) {
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}
		AgrupacionDato agrupacionActual = (AgrupacionDato) entityManager.find(AgrupacionDato.class, a.getId());
		if (agrupacionActual == null) {
			throw new UserException("no_se_encuentra_la_agrupacion_especificada");
		}

		if (agrupacionActual.getFechaBaja() != null) {
			throw new UserException("no_se_puede_modifcar_una_agrupacion_eliminada");
		}

		d.setRecurso(recursoActual);
		d.setAgrupacionDato(agrupacionActual);

		if (d.getNombre() == null || d.getNombre().equals("")) {
			throw new UserException("el_nombre_del_dato_es_obligatorio");
		}

		if (existeDatoASolicPorNombre(d.getNombre(), recursoActual.getId(),	null)) {
			throw new UserException("Ya existe un dato con el nombre especificado");
		}

		if (d.getEtiqueta() == null || d.getEtiqueta().equals("")) {
			throw new UserException("la_etiqueta_del_dato_es_obligatoria");
		}

		if (d.getTipo() == null || d.getTipo().equals("")) {
			throw new UserException("el_tipo_del_dato_es_obligatorio");
		}

		if (d.getFila() == null) {
			throw new UserException("la_fila_del_dato_es_obligatoria");
		}

		if (d.getFila().intValue() < 1) {
			throw new UserException("la_fila_del_dato_debe_ser_mayor_a_cero");
		}

		if (d.getColumna() == null) {
			throw new UserException("la_columna_del_dato_es_obligatoria");
		}

		if (d.getLargo() == null) {
			throw new UserException("el_largo_del_dato_es_obligatorio");
		}

		if (d.getAnchoDespliegue() == null) {
			throw new UserException("el_ancho_de_despliegue_es_obligatorio");
		}

		if (d.getIncluirEnReporte() == true && d.getAnchoDespliegue().intValue() <= 0) {
			throw new UserException("el_ancho_de_despliegue_debe_ser_mayor_a_cero");
		}

		if (d.getOrdenEnLlamador() == null && d.getIncluirEnReporte()) {
			throw new UserException("el_orden_en_el_llamador_es_obligatorio");
		}

		else if (d.getIncluirEnLlamador() && d.getOrdenEnLlamador() == null) {
			d.setOrdenEnLlamador(1);
		}

		if (d.getIncluirEnLlamador() && d.getLargoEnLlamador() == null) {
			d.setLargoEnLlamador(d.getLargo());
		}

		entityManager.persist(d);
		return d;
	}
	
	public DatoASolicitar agregarDatoASolicitarImportar(Recurso recursoActual, AgrupacionDato agrupacionActual,
			DatoASolicitar d) throws UserException, ApplicationException,
			BusinessException {

		

		if (recursoActual == null) {
			throw new BusinessException(
					"no_se_encuentra_el_recurso_especificado");
		}

		

		if (agrupacionActual == null) {
			throw new UserException(
					"no_se_encuentra_la_agrupacion_especificada");
		}
		
		

		if (agrupacionActual.getFechaBaja() != null) {
			throw new UserException(
					"no_se_puede_modifcar_una_agrupacion_eliminada");
		}

		d.setRecurso(recursoActual);
		d.setAgrupacionDato(agrupacionActual);

		if (d.getNombre() == null || d.getNombre().equals("")) {
			throw new UserException("el_nombre_del_dato_es_obligatorio");
		}

		if (existeDatoASolicPorNombre(d.getNombre(), recursoActual.getId(),
				null)) {
			throw new UserException(
					"Ya existe un dato con el nombre especificado");
		}

		if (d.getEtiqueta() == null || d.getEtiqueta().equals("")) {
			throw new UserException("la_etiqueta_del_dato_es_obligatoria");
		}

		if (d.getTipo() == null || d.getTipo().equals("")) {
			throw new UserException("el_tipo_del_dato_es_obligatorio");
		}

		if (d.getFila() == null) {
			throw new UserException("la_fila_del_dato_es_obligatoria");
		}

		if (d.getFila().intValue() < 1) {
			throw new UserException("la_fila_del_dato_debe_ser_mayor_a_cero");
		}

		if (d.getColumna() == null) {
			throw new UserException("la_columna_del_dato_es_obligatoria");
		}

		if (d.getLargo() == null) {
			throw new UserException("el_largo_del_dato_es_obligatorio");
		}

		if (d.getAnchoDespliegue() == null) {
			throw new UserException("el_ancho_de_despliegue_es_obligatorio");
		}

		if (d.getIncluirEnReporte() == true
				&& d.getAnchoDespliegue().intValue() <= 0) {
			throw new UserException(
					"el_ancho_de_despliegue_debe_ser_mayor_a_cero");
		}

		if (d.getOrdenEnLlamador() == null && d.getIncluirEnReporte()) {
			throw new UserException("el_orden_en_el_llamador_es_obligatorio");
		}

		else if (d.getIncluirEnLlamador() && d.getOrdenEnLlamador() == null) {
			d.setOrdenEnLlamador(1);
		}

		if (d.getIncluirEnLlamador() && d.getLargoEnLlamador() == null) {
			d.setLargoEnLlamador(d.getLargo());
		}

		entityManager.persist(d);
		return d;
	}


	/**
	 * Modifica los datos de un DatoASolicitar <b>d</b>. Controla que no exista
	 * otro dato vivo (fechaBaja == null) con el mismo nombre. Controla que no
	 * existan datos de reserva asociados al DatoASolicitar. Se permite setear
	 * todos sus atributos menos la fechaBaja. Controla que el usuario tenga rol
	 * Planificador sobre la agenda del recurso <b>r</b> Roles permitidos:
	 * Planificador
	 * 
	 * @throws UserException
	 * @throws ApplicationException
	 */
	public void modificarDatoASolicitar(DatoASolicitar d) throws UserException, ApplicationException {

		DatoASolicitar datoActual = (DatoASolicitar) entityManager.find(DatoASolicitar.class, d.getId());

		if (datoActual == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}

		if (d.getNombre() == null) {
      throw new UserException("el_nombre_del_dato_es_obligatorio");
    }

		if (existeDatoASolicPorNombre(d.getNombre(), datoActual.getRecurso().getId(), d.getId())) {
			throw new UserException("ya_existe_un_dato_con_el_nombre_especificado");
		}

		if (d.getEtiqueta() == null) {
			throw new UserException("la_etiqueta_del_dato_es_obligatoria");
		}

		if (d.getTipo() == null) {
			throw new UserException("el_tipo_del_dato_es_obligatorio");
		}

		if (d.getFila() == null) {
			throw new UserException("la_fila_del_dato_es_obligatoria");
		}

		if (d.getColumna() == null) {
			throw new UserException("la_columna_del_dato_es_obligatoria");
		}

		if (d.getLargo() == null) {
			throw new UserException("el_largo_del_dato_es_obligatorio");
		}

		if (d.getAnchoDespliegue() == null) {
			throw new UserException("el_ancho_de_despliegue_es_obligatorio");
		}

		if (d.getLargoEnLlamador() == null) {
			d.setLargoEnLlamador(d.getLargo());
		}

		datoActual.setNombre(d.getNombre());
		datoActual.setEtiqueta(d.getEtiqueta());
		datoActual.setTipo(d.getTipo());
		datoActual.setRequerido(d.getRequerido()!=null?d.getRequerido():false);
		datoActual.setEsClave(d.getEsClave()!=null?d.getEsClave():false);
    datoActual.setSoloLectura(d.getSoloLectura()!=null?d.getSoloLectura():false);
		datoActual.setFila(d.getFila());
		datoActual.setColumna(d.getColumna());
		datoActual.setLargo(d.getLargo());
		datoActual.setTextoAyuda(d.getTextoAyuda());
		datoActual.setAgrupacionDato(d.getAgrupacionDato());
		datoActual.setIncluirEnReporte(d.getIncluirEnReporte()!=null?d.getIncluirEnReporte():false);
		datoActual.setAnchoDespliegue(d.getAnchoDespliegue());
		datoActual.setIncluirEnLlamador(d.getIncluirEnLlamador()!=null?d.getIncluirEnLlamador():false);
		datoActual.setLargoEnLlamador(d.getLargoEnLlamador()!=null?d.getLargoEnLlamador():d.getLargo());
		datoActual.setOrdenEnLlamador(d.getOrdenEnLlamador()!=null?d.getOrdenEnLlamador():1);
		datoActual.setIncluirEnNovedades(d.getIncluirEnNovedades()!=null?d.getIncluirEnNovedades():false);
	}

	/**
	 * Realiza una baja lógica del dato a solicitar <b>d</b> (se setea fechaBaja
	 * con la fecha actual del sistema). Controla que el usuario tenga rol
	 * Planificador sobre la agenda asociada. Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 */
	public void eliminarDatoASolicitar(DatoASolicitar d) throws UserException {
		DatoASolicitar datoActual = (DatoASolicitar) entityManager.find(DatoASolicitar.class, d.getId());
		if (datoActual == null) {
			throw new UserException("no_se_encuentra_el_dato_especificado");
		}
		datoActual.setFechaBaja(new Date());
	}

	/**
	 * Agrega un ValorPosible asociándolo al DatoASolicitar. Controla que no
	 * exista otro valor posible con la misma etiqueta y/o valor que se solapen
	 * en el período (fechaDesde, fechaHasta). Controla que el usuario tenga rol
	 * Planificador sobre la agenda del recurso <b>r</b> Roles permitidos:
	 * Planificador
	 * 
	 * @throws UserException
	 * @throws ApplicationException
	 */
	public ValorPosible agregarValorPosible(DatoASolicitar d, ValorPosible vp) throws UserException, ApplicationException {

		DatoASolicitar datoActual = (DatoASolicitar) entityManager.find(DatoASolicitar.class, d.getId());

		if (datoActual == null) {
			throw new UserException("no_se_encuentra_el_dato_especificado");
		}

		vp.setDato(d);

		if (vp.getEtiqueta() == null) {
			throw new UserException("la_etiqueta_del_dato_es_obligatoria");
		}

		if (vp.getValor() == null) {
			throw new UserException("el_valor_del_dato_es_obligatorio");
		}

		if (vp.getOrden() == null) {
			throw new UserException("el_orden_del_dato_es_obligatorio");
		}

		if (vp.getOrden().intValue() < 1) {
			throw new UserException("el_orden_del_dato_debe_ser_mayor_a_cero");
		}

		if (vp.getFechaDesde() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}

		if ((vp.getFechaHasta() != null) && (vp.getFechaDesde().compareTo(vp.getFechaHasta()) > 0)) {
			throw new UserException("la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}
		if (existeValorPosiblePeriodo(vp)) {
			throw new UserException("ya_existe_otro_valor_con_la_misma_etiqueta_y_valor");
		}

		entityManager.persist(vp);
		return vp;
	}
	
	public ValorPosible agregarValorPosibleImportar(DatoASolicitar datoActual, ValorPosible vp)
			throws UserException, ApplicationException {

		if (datoActual == null) {
			throw new UserException("no_se_encuentra_el_dato_especificado");
		}

		vp.setDato(datoActual);

		if (vp.getEtiqueta() == null) {
			throw new UserException("la_etiqueta_del_dato_es_obligatoria");
		}

		if (vp.getValor() == null) {
			throw new UserException("el_valor_del_dato_es_obligatorio");
		}

		if (vp.getOrden() == null) {
			throw new UserException("el_orden_del_dato_es_obligatorio");
		}

		if (vp.getOrden().intValue() < 1) {
			throw new UserException("el_orden_del_dato_debe_ser_mayor_a_cero");
		}

		if (vp.getFechaDesde() == null) {
			throw new UserException("la_fecha_de_inicio_es_obligatoria");
		}

		// fechaDesde <= fechaHasta o fechaHasta == NULL
		if ((vp.getFechaHasta() != null)
				&& (vp.getFechaDesde().compareTo(vp.getFechaHasta()) > 0)) {
			throw new UserException(
					"la_fecha_de_fin_debe_ser_posterior_a_la_fecha_de_inicio");
		}
		if (existeValorPosiblePeriodo(vp)) {
			throw new UserException(
					"ya_existe_otro_valor_con_la_misma_etiqueta_y_valor");
		}

		entityManager.persist(vp);
		return vp;
	}

	/**
	 * Modifica el ValorPosible. Controla que no exista otro valor posible con
	 * la misma etiqueta y/o valor que se solapen en el período (fechaDesde,
	 * fechaHasta). Permite modificar fecha hasta , ya que significa la vigencia
	 * del valor Controla que el usuario tenga rol Planificador sobre la agenda
	 * del recurso <b>r</b> Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 * @throws ApplicationException
	 */
	public void modificarValorPosible(ValorPosible v) throws UserException,
			ApplicationException {

		ValorPosible valorActual = (ValorPosible) entityManager.find(
				ValorPosible.class, v.getId());

		if (valorActual == null) {
			throw new UserException("AE10062", "No existe el valor posible "
					+ v.getId().toString());
		}

		// No se puede modificar un dato con fecha de baja
		if (valorActual.getDato().getFechaBaja() != null) {
			throw new UserException("AE10056",
					"No se puede modificar un dato con fecha de baja");
		}

		v.setDato(valorActual.getDato());

		if (v.getEtiqueta() == null) {
			throw new UserException("AE10057", "La etiqueta no puede ser nula");
		}

		if (v.getValor() == null) {
			throw new UserException("AE10058", "El valor no puede ser nulo");
		}

		if (v.getOrden() == null) {
			throw new UserException("AE10059", "El orden no puede ser nulo");
		}

		if (v.getFechaDesde() == null) {
			throw new UserException("AE10060",
					"La fecha desde no puede ser nula");
		}

		// fechaDesde <= fechaHasta o fechaHasta == NULL
		if ((v.getFechaHasta() != null)
				&& (v.getFechaDesde().compareTo(v.getFechaHasta()) > 0)) {
			throw new UserException("AE10061",
					"Fecha desde debe ser menor o igual a fecha hasta o fecha hasta es nula");
		}

		// Controla que no exista otro valor posible con la misma etiqueta y/o
		// valor que se solapen en el
		// período (fechaDesde, fechaHasta).
		if (existeValorPosiblePeriodo(v)) {
			throw new UserException("AE10069",
					"No puede existir otra etiqueta/valor posible que se solape en el período");
		}

		valorActual.setEtiqueta(v.getEtiqueta());
		valorActual.setOrden(v.getOrden());
		valorActual.setValor(v.getValor());
		valorActual.setFechaDesde(v.getFechaDesde());
		valorActual.setFechaHasta(v.getFechaHasta());

	}

	/**
	 * Realiza una baja física del ValorPosible Controla que el usuario tenga
	 * rol Planificador sobre la agenda asociada Roles permitidos: Planificador
	 * 
	 * @throws UserException
	 */
	public void eliminarValorPosible(ValorPosible v) throws UserException {

		ValorPosible valorActual = (ValorPosible) entityManager.find(
				ValorPosible.class, v.getId());

		if (valorActual == null) {
			throw new UserException("AE10062", "No existe el valor posible "
					+ v.getId().toString());
		}
		entityManager.remove(valorActual);

	}

	public Boolean existeRecursoPorNombre(Recurso r) throws ApplicationException {
		try {
			Long cant = (Long) entityManager
					.createQuery(
							"SELECT count(r) from Recurso r "
									+ "WHERE UPPER(r.nombre) = UPPER(:nombre) "
									+ "AND (r.id <> :id OR :id is null)"
									+ "AND r.agenda = :agenda "
									+ "AND r.fechaBaja IS NULL")
					.setParameter("nombre", r.getNombre().toUpperCase())
					.setParameter("id", r.getId())
					.setParameter("agenda", r.getAgenda()).getSingleResult();
			return (cant > 0);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}

	}

	private Boolean hayReservasVivasFueraDelPeriodo(Integer recursoId, Date desde, Date hasta) throws ApplicationException {
		try {
			Long cant = (Long) entityManager
					.createQuery("SELECT count(r) FROM Disponibilidad d JOIN d.reservas r "
						+ "WHERE d.recurso.id = :recursoId "
						+ "  AND (d.fecha < :fecha_desde OR d.fecha > :fecha_hasta )"
            + "  AND d.presencial=false "
						+ "  AND (r.estado = :reservado OR r.estado = :pendiente) ")
					.setParameter("recursoId", recursoId)
					.setParameter("fecha_desde", desde, TemporalType.DATE)
					.setParameter("fecha_hasta", hasta, TemporalType.DATE)
					.setParameter("reservado", Estado.R)
					.setParameter("pendiente", Estado.P)
					.getSingleResult();
			return (cant > 0);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private Boolean hayDisponibilidadesVivasFueraDelPeriodo(Integer recursoId, Date desde, Date hasta) throws ApplicationException {
		try {
			Long cant = (Long) entityManager
					.createQuery("SELECT count(d) FROM Disponibilidad d "
						+ "WHERE d.recurso.id = :recursoId "
						+ "  AND (d.fecha < :fecha_desde OR d.fecha > :fecha_hasta)"
						+ "  AND d.presencial=false "
						+ "  AND d.fechaBaja IS NULL")
					.setParameter("recursoId", recursoId)
					.setParameter("fecha_desde", desde, TemporalType.DATE)
					.setParameter("fecha_hasta", hasta, TemporalType.DATE)
					.getSingleResult();
			return (cant > 0);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private Boolean existeAgrupacionPorNombre(AgrupacionDato a) throws ApplicationException {
		try {
			Long cant = (Long) entityManager
					.createQuery("SELECT COUNT(a) FROM AgrupacionDato a "
									+ "WHERE a.nombre = :nombre "
									+ "AND (a.id <> :id OR :id is null)"
									+ "AND  a.recurso = :recurso "
									+ "AND  a.fechaBaja IS NULL")
					.setParameter("nombre", a.getNombre())
					.setParameter("id", a.getId())
					.setParameter("recurso", a.getRecurso()).getSingleResult();

			return (cant > 0);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}

	}

	// Controla que no exista un dato a solicitar con el mismo nombre en la
	// base.
	public boolean existeDatoASolicPorNombre(String n, Integer idRecurso,
			Integer idDatoSolicitar) throws ApplicationException {
		try {
			Long cant = (Long) entityManager
					.createQuery(
							"SELECT count(d) "
									+ "from DatoASolicitar d "
									+ "WHERE upper(d.nombre) = upper(:n) "
									+ "AND d.recurso.id = :recurso "
									+ "AND d.fechaBaja IS NULL "
									+ "AND (d.id <> :idDatoSolicitar or :idDatoSolicitar is null)")
					.setParameter("n", n).setParameter("recurso", idRecurso)
					.setParameter("idDatoSolicitar", idDatoSolicitar)
					.getSingleResult();

			return (cant > 0);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}

	}

	// Controla que no existan datos a solicitar vivos para la agrupacion
	// ya que si existen esta no se puede eliminar
	private Boolean existeDatoASolicPorAgrupacion(Integer a)
			throws ApplicationException {
		try {
			Long cant = (Long) entityManager
					.createQuery(
							"SELECT count(d) " + "from DatoASolicitar d "
									+ "WHERE d.agrupacionDato.id = :a "
									+ "AND d.fechaBaja IS NULL")
					.setParameter("a", a).getSingleResult();

			return (cant > 0);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}

	}

	// Controla que no exista otro valor posible con el mismo valor/etiqueta que
	// se solapen en el período (fechaDesde, fechaHasta).
	public boolean existeValorPosiblePeriodo(ValorPosible v)
			throws ApplicationException {

		String queryString = "SELECT count(v) from ValorPosible v "
				+ "WHERE (v.valor = :valor OR v.etiqueta = :etiqueta ) "
				+ (v.getId() != null ? " AND v.id <> :id " : "")
				+ // Si estoy modifiando un valorPosible, no lo comparo consigo
					// mismo.
				"AND  v.dato = :dato "
				+ "AND ("
				+ (v.getFechaHasta() == null ? " (v.fechaHasta is null) OR " + // Si
																				// el
																				// nuevo
																				// valorPosible
																				// no
																				// tiene
																				// fechaHasta
																				// y
																				// en
																				// la
																				// DB
																				// hay
																				// otro
																				// sin
																				// fechaHasta
																				// =>
																				// se
																				// solapan
						" (v.fechaHasta is not null and :desde <= v.fechaHasta) " // Si
																					// el
																					// nuevo
																					// valorPosible
																					// no
																					// tiene
																					// fechaHasta
																					// y
																					// comienza
																					// antes
																					// del
																					// fin
																					// del
																					// que
																					// existe
																					// en
																					// la
																					// DB
																					// =>
																					// se
																					// solapan
						: " (v.fechaHasta is null and :hasta >= v.fechaDesde) OR "
								+ // Si el nuevo valorPosible termina dentro del
									// período del que esta en la DB => se
									// solapan
								" (v.fechaHasta is not null and :hasta >= v.fechaDesde and :desde <= v.fechaHasta) ")
				+ // Si el nuevo valorPosible termina dentro del período del que
					// esta en la DB => se solapan
				")";

		try {
			Query query = entityManager.createQuery(queryString);
			query.setParameter("valor", v.getValor())
					.setParameter("etiqueta", v.getEtiqueta())
					.setParameter("dato", v.getDato())
					.setParameter("desde", v.getFechaDesde(),
							TemporalType.TIMESTAMP);

			if (v.getId() != null) {
				query.setParameter("id", v.getId());
			}
			if (v.getFechaHasta() != null) {
				query.setParameter("hasta", v.getFechaHasta(),
						TemporalType.TIMESTAMP);
			}

			Long cant = (Long) query.getSingleResult();

			return (cant > 0);

		} catch (Exception e) {
			throw new ApplicationException(e);
		}

	}

	/**
	 * Retorna el arbol de AgrupacionDato/DatoASolicitar/ValorPosible vivos. La
	 * lista de objetos AgrupacionDato ordenada por el atributo orden. La lista
	 * de objetos DatoASolicitar ordenada por (fila,columna)
	 * 
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	@RolesAllowed({ "RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_ANONIMO", "RA_AE_FATENCION", "RA_AE_FCALL_CENTER", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
	public List<AgrupacionDato> consultarDefinicionDeCampos(Recurso recurso, TimeZone timezone) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}

		List<AgrupacionDato> agrupacionesDTO = new ArrayList<AgrupacionDato>();
		List<AgrupacionDato> agrupaciones = (List<AgrupacionDato>) entityManager
				.createQuery("FROM AgrupacionDato a "
					+ "WHERE a.recurso = :r "
					+ "AND a.fechaBaja IS NULL "
					+ "ORDER BY a.orden ")
				.setParameter("r", recurso).getResultList();
		//Para cada agrupacion obtener los datos a solicitar
		for (AgrupacionDato agrupacion : agrupaciones) {
			AgrupacionDato agrupacionDTO = new AgrupacionDato(agrupacion);
			agrupacionDTO.setRecurso(recurso);
			agrupacionesDTO.add(agrupacionDTO);
			List<DatoASolicitar> datosDTO = obtenerDatosASolicitar(agrupacion, timezone);
			agrupacionDTO.setDatosASolicitar(datosDTO);
		}

		return agrupacionesDTO;
	}

	/**
	 * Retorna el arbol de AgrupacionDato/DatoASolicitar/ValorPosible. La lista
	 * de objetos AgrupacionDato ordenada por el atributo orden. La lista de
	 * objetos DatoASolicitar ordenada por (fila,columna) Se usa para la
	 * consulta de Reservas por datos a Solicitar y actualmente solo permite
	 * traer todos los valores posibles, no los datos a solicitar ya que esto
	 * podría provocar solapamientos en el formulario
	 * 
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public List<AgrupacionDato> consultarDefCamposTodos(Recurso recurso) throws UserException {
		if (recurso == null) {
			throw new UserException("debe_especificar_el_recurso");
		}
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}
		List<AgrupacionDato> agrupacionesDTO = new ArrayList<AgrupacionDato>();
		//Obtener las agrupaciones de datos
		List<AgrupacionDato> agrupaciones = (List<AgrupacionDato>) entityManager.createQuery(
				"FROM AgrupacionDato a "
				+ "WHERE a.recurso = :r " 
				+ "  AND a.fechaBaja IS NULL "
				+ "ORDER BY a.orden")
				.setParameter("r", recurso).getResultList();
		//Para cada agrupación obtener sus datos
		for (AgrupacionDato agrupacion : agrupaciones) {
			AgrupacionDato agrupacionDTO = new AgrupacionDato(agrupacion);
			agrupacionesDTO.add(agrupacionDTO);
			List<DatoASolicitar> datosDTO = obtenerTodosDatosASolicitar(agrupacion);
			agrupacionDTO.setDatosASolicitar(datosDTO);
		}
		return agrupacionesDTO;
	}

	public Boolean mostrarDatosASolicitarEnLlamador(Recurso recurso)
			throws BusinessException {

		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new BusinessException("-1",
					"No se encuentra el recurso indicado");
		}

		Long cantAMostrar = (Long) entityManager
				.createQuery(
						"select count(dato) from DatoASolicitar dato "
								+ "where dato.recurso = :recurso and "
								+ "      dato.fechaBaja is null and "
								+ "      dato.incluirEnLlamador = :incluir ")
				.setParameter("recurso", recurso).setParameter("incluir", true)
				.getSingleResult();

		if (cantAMostrar > 0) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private List<DatoASolicitar> obtenerDatosASolicitar(AgrupacionDato agrupacion, TimeZone timezone) {
		List<DatoASolicitar> datosDTO = new ArrayList<DatoASolicitar>();
		List<DatoASolicitar> datos = (List<DatoASolicitar>) entityManager
				.createQuery("FROM DatoASolicitar d "
					+ "WHERE d.agrupacionDato = :agrupacion "
					+ "  AND d.fechaBaja IS NULL "
					+ "ORDER BY d.fila, d.columna ")
				.setParameter("agrupacion", agrupacion).getResultList();
		//Para cada dato a solicitar que sea del tipo Lista obtener los valores posibles.
		for (DatoASolicitar datoASolicitar : datos) {
			DatoASolicitar datoASolicitarDTO = new DatoASolicitar(datoASolicitar);
			datosDTO.add(datoASolicitarDTO);
			if (datoASolicitarDTO.getTipo() == Tipo.LIST) {
				List<ValorPosible> valoresDTO = obtenerValoresPosibles(datoASolicitar, timezone);
				datoASolicitarDTO.setValoresPosibles(valoresDTO);
			}
		}

		return datosDTO;
	}

	/**
	 * Obtiene la lista de Datos a Solicitar para armar la consulta de Reserva
	 * por datos de la reserva. No trae los datos a solicitar dados de baja,
	 * debido a que esto podría provocar solapamientos en el formulario dinámico
	 * si recupera todos los valores posibles de los datos a solicitar de tipo
	 * lista aunque ya hayan sido dados de baja.
	 * 
	 * @param agrupacion
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private List<DatoASolicitar> obtenerTodosDatosASolicitar(AgrupacionDato agrupacion) {
		List<DatoASolicitar> datosDTO = new ArrayList<DatoASolicitar>();
		//Obtener los datos a solicitar de la agrupación
		List<DatoASolicitar> datos = (List<DatoASolicitar>) entityManager.createQuery(
				  "FROM DatoASolicitar dato "
				+ "WHERE dato.agrupacionDato = :agrupacion "
				+ "  AND dato.fechaBaja IS NULL "
				+ "ORDER BY dato.fila, dato.columna ")
				.setParameter("agrupacion", agrupacion).getResultList();
		//Para cada dato, si es de tipo lista obtener los valores posibles
		for (DatoASolicitar datoASolicitar : datos) {
			DatoASolicitar datoASolicitarDTO = new DatoASolicitar(datoASolicitar);
			datosDTO.add(datoASolicitarDTO);
			if (datoASolicitarDTO.getTipo() == Tipo.LIST) {
				List<ValorPosible> valoresDTO = obtenerTodosValoresPosibles(datoASolicitar);
				datoASolicitarDTO.setValoresPosibles(valoresDTO);
			}
		}
		return datosDTO;
	}

	@SuppressWarnings("unchecked")
	private List<ValorPosible> obtenerValoresPosibles(DatoASolicitar dato, TimeZone timezone) {
		List<ValorPosible> valoresDTO = new ArrayList<ValorPosible>();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTimeInMillis()));
		Date hoy = cal.getTime();
		List<ValorPosible> valores = (List<ValorPosible>) entityManager
			.createQuery("FROM ValorPosible valor "
				+ "WHERE valor.dato = :dato "
				+ "  AND :hoy >= valor.fechaDesde "
				+ "  AND (valor.fechaHasta IS NULL OR :hoy <= valor.fechaHasta) "
				+ "ORDER BY valor.orden ")
			.setParameter("dato", dato)
			.setParameter("hoy", hoy, TemporalType.DATE).getResultList();
		for (ValorPosible valorPosible : valores) {
			valoresDTO.add(new ValorPosible(valorPosible));
		}
		return valoresDTO;
	}

	/**
	 * Obtiene la lista de valores posibles para un dato a solicitar de tipo
	 * lista sin filtrar por la vigencia, se usa para la consulta de Reserva por
	 * datos de la reserva.
	 * 
	 * @param dato
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ValorPosible> obtenerTodosValoresPosibles(DatoASolicitar dato) {

		List<ValorPosible> valoresDTO = new ArrayList<ValorPosible>();

		// Obtengo los valores posibles vivos del dato
		List<ValorPosible> valores = (List<ValorPosible>) entityManager
				.createQuery(
						"from ValorPosible valor " + "where valor.dato = :d  "
								+ "order by valor.orden, valor.fechaDesde ")
				.setParameter("d", dato).getResultList();

		for (ValorPosible valorPosible : valores) {
			valoresDTO.add(new ValorPosible(valorPosible));
		}
		return valoresDTO;
	}

	@SuppressWarnings("unchecked")
	public List<DatoASolicitar> consultarDatosSolicitar(Recurso recurso)  {

		List<DatoASolicitar> datosDTO = new ArrayList<DatoASolicitar>();

		//Obtener los datos a solicitar vivos de la agrupación
		List<DatoASolicitar> datos = (List<DatoASolicitar>) entityManager.createQuery(
		    "FROM DatoASolicitar dato "
			+ "WHERE dato.recurso = :recurso "
			+ "  AND dato.fechaBaja IS NULL "
			+ "ORDER BY dato.agrupacionDato.orden, dato.fila, dato.nombre")
		.setParameter("recurso", recurso).getResultList();

		//Para cada dato a solicitar que sea del tipo Lista, obtener los valores posibles.
		for (DatoASolicitar datoASolicitar : datos) {
			DatoASolicitar datoASolicitarDTO = new DatoASolicitar(datoASolicitar);
			datosDTO.add(datoASolicitarDTO);
			if (datoASolicitar.getTipo() == Tipo.LIST) {
				List<ValorPosible> valoresDTO = obtenerTodosValoresPosibles(datoASolicitar);
				datoASolicitarDTO.setValoresPosibles(valoresDTO);
			}
		}
		return datosDTO;
	}

	/**
	 * Retorna todos los valores posibles aunque no esten en el rango
	 * desde,hasta.
	 */
	public List<ValorPosible> consultarValoresPosibles(DatoASolicitar d)
			throws ApplicationException {
		return this.obtenerTodosValoresPosibles(d);
	}

	private Boolean hayReservasVivas(Recurso recurso, TimeZone timezone) throws ApplicationException {
		try {
	    Calendar cal = new GregorianCalendar();
	    cal.add(Calendar.MILLISECOND, timezone.getOffset(cal.getTimeInMillis()));
			Long cant = (Long) entityManager
				.createQuery(
  				"SELECT count(r) FROM Disponibilidad d JOIN d.reservas r "
  						+ "WHERE d.recurso = :recurso "
  						+ "  AND d.fecha >= :fecha"
  						+ "  AND d.horaFin >= :hora"
  						+ "  AND r.estado IN ('R','P')")
				.setParameter("recurso", recurso)
				.setParameter("fecha", cal.getTime())
				.setParameter("hora", cal.getTime()).getSingleResult();
			return (cant > 0);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	public Recurso copiarRecurso(Recurso recurso, String codigoUsuario) throws BusinessException,
			ApplicationException, UserException {

		//Antes de que haga el entityManager.find(recurso), me guardo la accionMiPerfil que viene en el recurso como transient para no perderla
		AccionMiPerfil auxAccionMiPerfil = recurso.getAccionMiPerfil();
		
		recurso = entityManager.find(Recurso.class, recurso.getId());
		if (recurso == null) {
			throw new UserException("no_se_encuentra_el_recurso_especificado");
		}

		// 1- Creo un nuevo Recurso y copio los atributos de r.
		// 2- Creo un nuevo TextoRecurso y copio los atributos de
		// r.textoRecurso.
		// 3- Para cada ValidacionPorRecurso de r, creo una copia y la asigno al
		// nuevo recurso
		// 4- Para cada DatoDelRecurso de r, creo una copia y la asigno al nuevo
		// recurso.
		// 5- Para cada AgrupacionDato de r:
		// creo una copia y la asigno al recurso nuevo.
		//
		// 5.1 Para cada DatoASolicitar de la AgrupacionDato:
		// creo una copia y la asigno al recurso nuevo y a la AgrupacionDato
		// nueva.
		//
		// 5.2 Para cada ValorPosible del DatoASolicitar:
		// creo una copia y la asigno al datoASolicitar nuevo.
		//
		// 5.3 Para cada ValidacionPorDato del DatoASolicitar:
		// Creo una ValidacionPorDato nueva y le asigno:
		// el DatoASolicitar nuevo y
		// la ValidacionPorRecurso nueva que se corresponde a la
		// ValidacionPorRecurso original
		// (Para esto usar un Map<ValidacionPorRecursoOrig,
		// ValidacionPorRecursoCopia>
		// 6- Se copian disponibilidades

		// 1 - Esto ya copia las propiedades básicas del recurso
		Recurso rCopia = new Recurso(recurso);
		rCopia.setId(null);
		rCopia.setAgenda(recurso.getAgenda());

		int contador = 0;
		do {
			contador++;
			rCopia.setNombre("Copia " + contador + " de " + recurso.getNombre());
			rCopia.setDescripcion("Copia " + contador + " de " + recurso.getDescripcion());
		} while (existeRecursoPorNombre(rCopia));

		entityManager.persist(rCopia);

		//Guardar registro en histórico (crear Recurso)
		this.guardarHistoricoRecurso(rCopia, codigoUsuario, 0);
		
		// 2
		rCopia.setTextosRecurso(new HashMap<String, TextoRecurso>());
		if (recurso.getTextosRecurso() != null) {
			for (String idioma : recurso.getTextosRecurso().keySet()) {
				TextoRecurso viejo = recurso.getTextosRecurso().get(idioma);
				TextoRecurso trCopia = new TextoRecurso();

				trCopia.setRecurso(viejo.getRecurso());
				trCopia.setIdioma(viejo.getIdioma());
				trCopia.setTextoPaso2(viejo.getTextoPaso2());
				trCopia.setTextoPaso3(viejo.getTextoPaso3());
				trCopia.setTituloCiudadanoEnLlamador(viejo.getTituloCiudadanoEnLlamador());
				trCopia.setTituloPuestoEnLlamador(viejo.getTituloPuestoEnLlamador());
				trCopia.setTicketEtiquetaUno(viejo.getTicketEtiquetaUno());
				trCopia.setTicketEtiquetaDos(viejo.getTicketEtiquetaDos());
				trCopia.setValorEtiquetaUno(viejo.getValorEtiquetaUno());
				trCopia.setValorEtiquetaDos(viejo.getValorEtiquetaDos());
				rCopia.getTextosRecurso().put(idioma, trCopia);
				entityManager.persist(trCopia);
			}
		}

		// 3
		Map<ValidacionPorRecurso, ValidacionPorRecurso> validacionesDelRecurso = new HashMap<ValidacionPorRecurso, ValidacionPorRecurso>();
		for (ValidacionPorRecurso vxr : recurso.getValidacionesPorRecurso()) {
			if (vxr.getFechaBaja() == null) {
				ValidacionPorRecurso vxrCopia = new ValidacionPorRecurso();
				vxrCopia.setId(null);
				vxrCopia.setOrdenEjecucion(vxr.getOrdenEjecucion());
				vxrCopia.setRecurso(rCopia);
				vxrCopia.setValidacion(vxr.getValidacion());
				validacionesDelRecurso.put(vxr, vxrCopia);
				entityManager.persist(vxrCopia);
			}
		}

		// 4
		for (DatoDelRecurso ddr : recurso.getDatoDelRecurso()) {

			DatoDelRecurso ddrCopia = new DatoDelRecurso();
			ddrCopia.setOrden(ddr.getOrden());
			ddrCopia.setEtiqueta(ddr.getEtiqueta());
			ddrCopia.setValor(ddr.getValor());
			ddrCopia.setRecurso(rCopia);
			rCopia.getDatoDelRecurso().add(ddrCopia);
			entityManager.persist(ddrCopia);
		}

		// 5
		for (AgrupacionDato agrup : recurso.getAgrupacionDatos()) {

			if (agrup.getFechaBaja() == null) {
				AgrupacionDato agrupCopia = new AgrupacionDato(agrup);
				agrupCopia.setId(null);
				agrupCopia.setRecurso(rCopia);
				entityManager.persist(agrupCopia);

				// 5.1
				for (DatoASolicitar campo : agrup.getDatosASolicitar()) {

					if (campo.getFechaBaja() == null) {
						DatoASolicitar campoCopia = new DatoASolicitar(campo);
						campoCopia.setId(null);
						campoCopia.setRecurso(rCopia);
						campoCopia.setAgrupacionDato(agrupCopia);

						agrupCopia.getDatosASolicitar().add(campoCopia);
						rCopia.getDatoASolicitar().add(campoCopia);
						entityManager.persist(campoCopia);

						// 5.2
						for (ValorPosible vp : campo.getValoresPosibles()) {
							if (vp.getFechaHasta() == null || vp.getFechaHasta().after(new Date())) {
								ValorPosible vpCopia = new ValorPosible(vp);
								vpCopia.setId(null);
								vpCopia.setDato(campoCopia);
								entityManager.persist(vpCopia);
							}
						}

						// 5.3
						for (ValidacionPorDato vxd : campo.getValidacionesPorDato()) {
							if (vxd.getFechaDesasociacion() == null) {
								ValidacionPorDato vxdCopia = new ValidacionPorDato();
								vxdCopia.setNombreParametro(vxd.getNombreParametro());
								vxdCopia.setDatoASolicitar(campoCopia);
								ValidacionPorRecurso vxrCopia = validacionesDelRecurso.get(vxd.getValidacionPorRecurso());
								vxdCopia.setValidacionPorRecurso(vxrCopia);
								entityManager.persist(vxdCopia);
							}
						}
					}

				}// Fin 5.1

			}

		}// Fin 5
		// 6
		List<Disponibilidad> disponibilidades = recurso.getDisponibilidades();
		for (Disponibilidad disponibilidad : disponibilidades) {
			Disponibilidad disponibilidadCopia = new Disponibilidad();
			disponibilidadCopia.setCupo(disponibilidad.getCupo());
			disponibilidadCopia.setFecha(disponibilidad.getFecha());
			disponibilidadCopia.setFechaBaja(disponibilidad.getFechaBaja());
			disponibilidadCopia.setHoraFin(disponibilidad.getHoraFin());
			disponibilidadCopia.setHoraInicio(disponibilidad.getHoraInicio());
			disponibilidadCopia.setId(null);
			disponibilidadCopia.setNumerador(disponibilidad.getNumerador());
			disponibilidadCopia.setPlantilla(disponibilidad.getPlantilla());
			disponibilidadCopia.setRecurso(rCopia);
			disponibilidadCopia.setVersion(1);
			entityManager.persist(disponibilidadCopia);
		}
		
		// 7 (copio la accionMiPerfil del recurso, si es que tiene)
		if (auxAccionMiPerfil != null){
			//Hago la copia con un constructor hecho para eso
			AccionMiPerfil aCopia = new AccionMiPerfil(auxAccionMiPerfil);
			aCopia.setId(null);
			aCopia.setRecurso(rCopia);
			entityManager.persist(aCopia);
		}
		
		return rCopia;
	}

	/**
	 * Retorna los servicios de autocompletar asociados al recurso.
	 * 
	 * @param recurso
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	@RolesAllowed({ "RA_AE_ADMINISTRADOR", "RA_AE_PLANIFICADOR", "RA_AE_ANONIMO", "RA_AE_FCALL_CENTER", "RA_AE_FATENCION", "RA_AE_ADMINISTRADOR_DE_RECURSOS"})
	public List<ServicioPorRecurso> consultarServicioAutocompletar(Recurso r)
			throws BusinessException {

		List<ServicioPorRecurso> servicios = (List<ServicioPorRecurso>) entityManager
				.createQuery(
						"from ServicioPorRecurso servicio "
								+ "where servicio.recurso = :recurso and "
								+ "      servicio.fechaBaja is null ")
				.setParameter("recurso", r).getResultList();

		for (int i = 0; i < servicios.size(); i++) {
			servicios.get(i).getAutocompletado().getParametrosAutocompletados()
					.size();
			servicios.get(i).getAutocompletadosPorDato().size();
		}

		return servicios;
	}

	public byte[] exportarRecurso(Recurso recurso, String versionSAE) throws UserException {
		if (recurso == null) {
			return null;
		}
		byte[] bytes = null;
		try {
		  Query query = entityManager.createQuery("SELECT r FROM Recurso r WHERE r.id=:idRecurso");
		  query = query.setParameter("idRecurso", recurso.getId());
		  recurso = (Recurso) query.getSingleResult();
			for (AgrupacionDato agrupacion : recurso.getAgrupacionDatos()) {
				for (DatoASolicitar datoAsolicitar : agrupacion.getDatosASolicitar()) {
					datoAsolicitar.getValoresPosibles().isEmpty();
				}
			}
			RecursoExportar recursoExp = ExportarHelper.exportarRecurso(recurso, versionSAE);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			JAXBContext jaxbContext = JAXBContext.newInstance(RecursoExportar.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(recursoExp, output);
			bytes = output.toByteArray();
			return bytes;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new UserException("no_se_pudo_realizar_la_exportacion");
		}
	}

	public Recurso importarRecurso(Agenda a, byte[] b, String versionSAE, String codigoUsuario) throws UserException {
		if (b == null) {
			return null;
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(RecursoExportar.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ByteArrayInputStream input = new ByteArrayInputStream(b);
			RecursoExportar recursoExp = (RecursoExportar) jaxbUnmarshaller.unmarshal(input);
			Recurso recurso = ExportarHelper.importarRecurso(recursoExp, versionSAE);
			recurso = crearRecursoImportado(a, recurso, codigoUsuario);
			recurso.setAgrupacionDatos(new ArrayList<AgrupacionDato>());
			for(AgrupacionDatoExport agdExp : recursoExp.getAgrupaciones()){
				AgrupacionDato agd = ExportarHelper.importarAgrupacionDato(agdExp);
				agregarAgrupacionDatoImportar(recurso, agd);
				agd.setDatosASolicitar(new ArrayList<DatoASolicitar>());
				for(DatoASolicitarExportar dasExp : agdExp.getDatosAsolicitar()){
					DatoASolicitar das = ExportarHelper.importarDatoASolicitar(dasExp);
					das = agregarDatoASolicitarImportar(recurso, agd, das);
					das.setValoresPosibles(new ArrayList<ValorPosible>());
					for(ValorPosibleExport vpExp :dasExp.getValoresPosibles()){
						ValorPosible vp =  ExportarHelper.importarValorPosible(vpExp);
						vp = agregarValorPosibleImportar(das,vp);
						das.getValoresPosibles().add(vp);
					}
					agd.getDatosASolicitar().add(das);
				}
				recurso.getAgrupacionDatos().add(agd);
			}
			
			if(recursoExp.getTextosRecurso()!=null) {
				recurso.setTextosRecurso(new HashMap<String, TextoRecurso>());
				for(String idioma : recursoExp.getTextosRecurso().keySet()) {
					TextoRecurso tr = ExportarHelper.importarTextoRecurso(recursoExp.getTextosRecurso().get(idioma));
					tr.setRecurso(recurso);
					entityManager.persist(tr);
					if(tr!=null) {
						recurso.getTextosRecurso().put(idioma, tr);
					}
				}
			}
			return recurso;
		} catch (UserException uEx) {
      uEx.printStackTrace();
			throw uEx;
		} catch (Exception ex) {
      ex.printStackTrace();
			throw new UserException("no_se_pudo_realizar_la_importacion");
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value=30, unit=TimeUnit.MINUTES)
	public String cargaMasiva(Agenda a, byte[] b, String codigoUsuario, TimeZone timezone) throws UserException {
		ret = new ResultadoEjecucion();
		Boolean errorLinea = null;
		
		Integer contador = 0;
        nroLinea = 1;
        
        totalLineas = 0;
        cantLineasError = 0;
        cantLineasOK = 0;
        
        List<Integer> lineasConError = new ArrayList<Integer>();
        List<Recurso> recursosImportados = new ArrayList<Recurso>();
        
		if (b == null) {
			return null;
		}
		try {
			logger.info("Ejecución iniciada  carga masiva");
            
			 //Intentar liberar el lock por si lo tiene esta instancia
		      //boolean lockOk = (boolean)entityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID+")").getSingleResult();
		      //Intentar obtener el lock
			boolean lockOk = (boolean)entityManager.createNativeQuery("SELECT pg_try_advisory_xact_lock("+LOCK_ID+")").getSingleResult();
		      if(!lockOk) {
		        //Otra instancia tiene el lock
		        logger.info("No se ejecuta el reintento de carga masiva porque hay otra instancia haciéndolo.");
		        return null;
		      }
		      //No hay otra instancia con el lock, se continúa
           
			
			Reader reader = new StringReader(new String(b));
			
			ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
            strategy.setType(CamposCSV.class);
            String[] memberFieldsToBindTo = {"nombreRecurso", "Direccion", "Latitud", "Longitud", "Telefonos", "Horarios", 
                "fechaInicio", "fechaFin", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            CsvToBean<CamposCSV> csvToBean = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(';')
                    .build();

            Iterator<CamposCSV> cargaMasivaIterator = csvToBean.iterator();
			
            
            
            
            while (cargaMasivaIterator.hasNext()) {
                nroLinea += 1;
                contador += 1;
                totalLineas += 1;
                CamposCSV cargaMasivaLinea = cargaMasivaIterator.next();
                Boolean recursoDuplicado = Boolean.FALSE;
                if(!recursosImportados.isEmpty()){
                	for(Recurso r : recursosImportados){
                		if(r.getNombre().equals(cargaMasivaLinea.getNombreRecurso())){
                			//logger.error("El recurso duplicado es.. " + cargaMasivaLinea.getNombreRecurso());
                			//recurso duplicado en el archivo csv
                			recursoDuplicado = Boolean.TRUE;
                			break;
                		}
                	}
                }
                
                //SI EL RECURSO NO ES DUPLICADO PROCESAR
                if(!recursoDuplicado){
                	try{
                		Recurso recurso = this.guardarRecurso(cargaMasivaLinea,a,codigoUsuario);
                		if (recurso!=null) {
    	                	recursosImportados.add(recurso);
    	                	try{
	    	                    errorLinea = this.guardarDisponibilidades(cargaMasivaLinea,recurso);
	    	                    
	    	                    if (errorLinea && recursoEsNuevo) {
	    	                    	//logger.error("El recurso eliminar es.. " + cargaMasivaLinea.getNombreRecurso());
	    	                    	eliminarRecurso(recurso, timezone, codigoUsuario);
	    	                    	throw new UserException("Error creando las disponibilidades  del recurso " + cargaMasivaLinea.getNombreRecurso());
	    	                    }
	    	                    else if(errorLinea){
	    	                    	lineasConError.add(nroLinea);
	    	                        cantLineasError += 1;
	    	                    }
	    	                    else {
	    	                        cantLineasOK += 1;
	    	                    }
    	                	}catch (Exception e) {
    	                		if(recursoEsNuevo){
    	                			//logger.error("El recurso eliminar es.. " + cargaMasivaLinea.getNombreRecurso());
    	                			eliminarRecurso(recurso, timezone, codigoUsuario);
    	                		}
    	                		
    	                		e.printStackTrace();
    	                		lineasConError.add(nroLinea);
    		                    cantLineasError += 1;
    	                	}
    	                } else {
    	                    lineasConError.add(nroLinea);
    	                    cantLineasError += 1;
    	                }
                	}
                	catch (Exception e) {
                		e.printStackTrace();
                		lineasConError.add(nroLinea);
	                    cantLineasError += 1;
                	}
                	
                }
                else{
                	lineasConError.add(nroLinea);
                    cantLineasError += 1;
                }
                
            }
            
            logger.info("Ejecución finalizada");
            
            
		} catch (Exception e) {
			logger.error("Error: No se ha podido realizar la carga masiva");
            e.printStackTrace();
            throw new UserException("no_se_pudo_realizar_la_importacion");
            //return null;
        } finally {
            logger.info("Desbloqueando base de datos..");
	        //Intentar liberar el lock (si lo tiene esta instancia)
            //entityManager.createNativeQuery("SELECT pg_advisory_unlock("+LOCK_ID+")").getSingleResult();
	        logger.info("Ejecución del reintento de carga masiva finalizada.");

        }
		
		return StringUtils.join(lineasConError, ",") + ";" + totalLineas + ";" + cantLineasError + ";" + cantLineasOK;
	}
	
	
	
	private Recurso guardarRecurso(CamposCSV cargaMasivaLinea,Agenda a,String codigoUsuario) throws UserException {
        
		Recurso recurso = null;
		List<Recurso> recs = new ArrayList();
		
        if(StringUtils.isBlank(cargaMasivaLinea.getNombreRecurso())) {
            //logger.info("El recurso no tiene nombre");
            return null;
        }
        
        //logger.info("Nombre del recurso " + cargaMasivaLinea.getNombreRecurso());
        dateFormat.setLenient(false);
        Date fechaInicio = null;
    	Date fechaFin = null;
    	
    	 if(cargaMasivaLinea.getFechaInicio()!=null && !cargaMasivaLinea.getFechaInicio().trim().isEmpty()) {
    		 //logger.info("Fecha inicio " + cargaMasivaLinea.getFechaInicio());
             try {
            	 fechaInicio = dateFormat.parse(cargaMasivaLinea.getFechaInicio());
                 //Validación de fechas 
                 if(fechaInicio!=null && Utiles.esFechaInvalida(fechaInicio)){
                	 return null;  
            	 }
             }catch(Exception ex) {
            	 return null;
             }
         }
    	 else{
    		 return null;
    	 }
    	 
         if(cargaMasivaLinea.getFechaFin()!=null && !cargaMasivaLinea.getFechaFin().trim().isEmpty()) {
        	//logger.info("Fecha fin " + cargaMasivaLinea.getFechaFin());
            try {
            	fechaFin = dateFormat.parse(cargaMasivaLinea.getFechaFin());
                if(fechaFin!=null && Utiles.esFechaInvalida(fechaFin)){
                	return null;  
           	 	}
            }catch(Exception ex) {
            	return null;
            }
         }
         else{
    		 return null;
    	 }
         
         //Comparar que la fecha Fin sea posterior a la fecha inicio
         if (fechaInicio.compareTo(fechaFin) > 0) {
 			return null;
 		}
         
    	
         
        Integer diasPeriodo = Utiles.daysBetweenDates(fechaInicio, fechaFin);
        if(diasPeriodo==null){
        	return null;  
        } 

        //logger.info("Días período " + diasPeriodo);
        
        //traer los dos parámetros para recursos nuevos y existentes
        Integer diasRecursosNuevos = Utiles.DIAS_RECURSOS_NUEVOS;
        Integer diasRecursosExistentes = Utiles.DIAS_RECURSOS_EXISTENTES;
        
        
  		try {
  			diasRecursosNuevos = Integer.valueOf(confBean.getString("CARGA_MASIVA_DIAS_RECURSOS_NUEVOS"));
  		} catch (Exception nfEx) {
  			//logger.error("Error al obtener las configuraciones para los días de los recursos");
  		}
  		
  		try {
  			diasRecursosExistentes = Integer.valueOf(confBean.getString("CARGA_MASIVA_DIAS_RECURSOS_EXISTENTES"));
  		} catch (Exception nfEx) {
  			//logger.error("Error al obtener las configuraciones para los días de los recursos");
  		}
        
  		try{
			String query = "select r from Recurso r where r.nombre = ? and r.agenda.id = ? AND r.fechaBaja IS NULL";
	        Query q = entityManager.createQuery(query);
	        q.setParameter(1, cargaMasivaLinea.getNombreRecurso());
	        q.setParameter(2, a.getId());
	        recs = (List<Recurso>) q.getResultList();
  		}
  		catch (Exception e) {
			return null;
  		}
        
        if(!recs.isEmpty()) {
        	try{
	        	recurso = recs.get(0);
	        	//logger.info("Ya existe un recurso llamado "+recurso.getNombre()+" en la agenda "+a.getNombre());
	        	Boolean fechaDentroRango = Boolean.FALSE;
	        	//comparar que las fechas esten dentro de las fechas de atención al cliente
	        	//la fecha inicio esta despues de la fecha inicio disp y antes de la fecha fin disp 
	        	if (fechaInicio.compareTo(recurso.getFechaInicioDisp())==0 || fechaInicio.after(recurso.getFechaInicioDisp())){
	        		fechaDentroRango = Boolean.TRUE;
				}
	        	
	        	if(fechaInicio.compareTo(recurso.getFechaFinDisp())==0 || fechaInicio.before(recurso.getFechaFinDisp())){
	        		fechaDentroRango = Boolean.TRUE;
	        	}
	        	else{
	        		fechaDentroRango = Boolean.FALSE;
	        	}
	        	
	        	if(fechaFin.compareTo(recurso.getFechaFinDisp())==0 || fechaFin.before(recurso.getFechaFinDisp())){
	        		fechaDentroRango = Boolean.TRUE;
	        	}
	
	        	if(!fechaDentroRango){
	        		return null;
	        	}
	        	else{
	        		//logger.info("LAS FECHAS ESTAN DENTRO EL RANGO");
	        	}
	        	
	        	//Comparar los días antes de continuar
	        	if(!(diasPeriodo<=diasRecursosExistentes)){
	        		return null;
	        	}
	
	        	
	        	this.setRecursoEsNuevo(Boolean.FALSE);

	            
	            entityManager.merge(recurso);
	            //entityManager.flush();
	            
	    		//Guardar registro en histórico (crear Recurso)
	    		this.guardarHistoricoRecurso(recurso, codigoUsuario, 1);
	           
	            return recurso;
	            
	        } catch (Exception e) {
	        		logger.error("Error actualizando el recurso " + cargaMasivaLinea.getNombreRecurso());
	        		throw new UserException(e.getMessage());
		            //return null;
	        }
            
        }
        else{
        	
        	//Comparar los días antes de continuar
        	if(!(diasPeriodo<=diasRecursosNuevos)){
        		return null;
        	}
        	
	        //logger.info("Creando recurso: "+cargaMasivaLinea.getNombreRecurso());
	        
	        
	        this.setRecursoEsNuevo(Boolean.TRUE);
	        Calendar hoy00 = new GregorianCalendar();
	        hoy00.set(Calendar.HOUR_OF_DAY, 0);
	        hoy00.set(Calendar.MINUTE, 0);
	        hoy00.set(Calendar.SECOND, 0);
	        hoy00.set(Calendar.MILLISECOND, 0);
	        
	        try {
	        	recurso = new Recurso();
	        	
	            recurso.setNombre(cargaMasivaLinea.getNombreRecurso());
	            recurso.setDescripcion(cargaMasivaLinea.getNombreRecurso());
	            recurso.setFechaInicio(new Date());
	            //recurso.setFechaInicio(dateFormat.parse(cargaMasivaLinea.getFechaInicio()));
	            recurso.setFechaFin(dateFormat.parse(cargaMasivaLinea.getFechaFin()));
	            recurso.setFechaInicioDisp(dateFormat.parse(cargaMasivaLinea.getFechaInicio()));
	            recurso.setFechaFinDisp(dateFormat.parse(cargaMasivaLinea.getFechaFin()));
	            recurso.setDiasInicioVentanaIntranet(ConstantesRecurso.diasInicioVentanaIntranet);
	            recurso.setDiasVentanaIntranet(ConstantesRecurso.diasVentanaIntranet);
	            recurso.setDiasInicioVentanaInternet(ConstantesRecurso.diasInicioVentanaInternet);
	            recurso.setDiasVentanaInternet(ConstantesRecurso.diasVentanaInternet);
	            recurso.setVentanaCuposMinimos(ConstantesRecurso.ventanaCuposMinimos);
	            recurso.setCantDiasAGenerar(ConstantesRecurso.cantDiasAGenerar);
	            recurso.setMostrarNumeroEnTicket(ConstantesRecurso.mostrarNumeroEnTicket);
	            recurso.setLargoListaEspera(ConstantesRecurso.largoListaEspera);
	            recurso.setVisibleInternet(ConstantesRecurso.visibleInternet);

	            recurso.setAgenda(a);
	            recurso.setDireccion(cargaMasivaLinea.getDireccion());
	            recurso.setLocalidad(ConstantesRecurso.localidad);
	            recurso.setDepartamento(ConstantesRecurso.departamento);
	            recurso.setTelefonos(cargaMasivaLinea.getTelefonos());
	            recurso.setHorarios(cargaMasivaLinea.getHorarios());
	
	            if(!StringUtils.isBlank(cargaMasivaLinea.getLatitud()) && !StringUtils.isBlank(cargaMasivaLinea.getLongitud())) {
	                recurso.setLatitud(new BigDecimal(cargaMasivaLinea.getLatitud().replace(",", ".").trim()));
	                recurso.setLongitud(new BigDecimal(cargaMasivaLinea.getLongitud().replace(",", ".").trim()));
	            }

	            recurso.setFuenteTicket(ConstantesRecurso.fuenteTicket);
	            recurso.setTamanioFuenteGrande(ConstantesRecurso.tamanioFuenteGrande);
	            recurso.setTamanioFuenteNormal(ConstantesRecurso.tamanioFuenteNormal);
	            recurso.setTamanioFuenteChica(ConstantesRecurso.tamanioFuenteChica);
	            recurso.setPeriodoValidacion(ConstantesRecurso.periodoValidacion);
	            recurso.setValidarPorIP(ConstantesRecurso.validarPorIP);
	            recurso.setCancelacionTiempo(ConstantesRecurso.cancelacionTiempo);
	            recurso.setCancelacionUnidad(ConstantesRecurso.cancelacionUnidad);
	            recurso.setCancelacionTipo(FormaCancelacion.valueOf(ConstantesRecurso.cancelacionTipo));
//	
	            entityManager.persist(recurso);
	            entityManager.flush();
	            
	    		//Guardar registro en histórico (crear Recurso)
	    		this.guardarHistoricoRecurso(recurso, codigoUsuario, 0);
	    		
	    		// paso a agregar agrupacion
	    		AgrupacionDato agrupDato = new AgrupacionDato();
	    		agrupDato.setNombre("datos_personales");
	    		agrupDato.setEtiqueta("Datos personales");
	    		agrupDato.setOrden(1);
	    		agrupDato.setBorrarFlag(false);
	    		agregarAgrupacionDato(recurso, agrupDato);

	    		// agrego datos a solicitar tipo documento
	    		DatoASolicitar d1 = new DatoASolicitar();
	    		d1.setNombre(DatoASolicitar.TIPO_DOCUMENTO);
	    		d1.setRequerido(true);
	    		d1.setFila(1);
	    		d1.setColumna(1);
	    		d1.setIncluirEnReporte(true);
	    		d1.setAgrupacionDato(agrupDato);
	    		d1.setAnchoDespliegue(100);
	    		d1.setEsClave(true);
	    		d1.setSoloLectura(false);
	    		d1.setEtiqueta("Tipo de documento");
	    		d1.setIncluirEnLlamador(true);
	    		d1.setLargo(20);
	    		d1.setLargoEnLlamador(20);
	    		d1.setOrdenEnLlamador(1);
	    		d1.setRecurso(recurso);
	    		d1.setRequerido(true);
	    		d1.setTipo(Tipo.LIST);
	    		d1.setBorrarFlag(false);
	    		// persisto dato a solicitar
	    		agregarDatoASolicitar(recurso, agrupDato, d1);
	    		// Ingreso valores posibles
	    		// creo valor cédula
	    		ValorPosible vp1 = new ValorPosible();
	    		vp1.setDato(d1);
	    		vp1.setEtiqueta("Cédula de Identidad");
	    		vp1.setFechaDesde(recurso.getFechaInicio());
	    		vp1.setFechaHasta(recurso.getFechaFin());
	    		vp1.setOrden(1);
	    		vp1.setValor("CI");
	    		vp1.setBorrarFlag(false);
	    		agregarValorPosible(d1, vp1);

	    		// creo valor pasaporte
	    		ValorPosible vp2 = new ValorPosible();
	    		vp2.setDato(d1);
	    		vp2.setEtiqueta("Pasaporte");
	    		vp2.setFechaDesde(recurso.getFechaInicio());
	    		vp2.setFechaHasta(recurso.getFechaFin());
	    		vp2.setOrden(2);
	    		vp2.setValor("P");
	    		vp2.setBorrarFlag(false);
	    		agregarValorPosible(d1, vp2);

	    		// creo valor pasaporte
	    		ValorPosible vp3 = new ValorPosible();
	    		vp3.setDato(d1);
	    		vp3.setEtiqueta("Otro");
	    		vp3.setFechaDesde(recurso.getFechaInicio());
	    		vp3.setFechaHasta(recurso.getFechaFin());
	    		vp3.setOrden(3);
	    		vp3.setValor("O");
	    		vp3.setBorrarFlag(false);
	    		agregarValorPosible(d1, vp3);

	    		// agrego datos a solicitar Nro. documento
	    		DatoASolicitar d2 = new DatoASolicitar();
	    		d2.setNombre(DatoASolicitar.NUMERO_DOCUMENTO);
	    		d2.setRequerido(true);
	    		d2.setFila(2);
	    		d2.setColumna(1);
	    		d2.setIncluirEnReporte(true);
	    		d2.setAgrupacionDato(agrupDato);
	    		d2.setAnchoDespliegue(120);
	    		d2.setEsClave(true);
	    		d2.setEtiqueta("Número de documento");
	    		d2.setIncluirEnLlamador(true);
	    		d2.setLargo(10);
	    		d2.setLargoEnLlamador(10);
	    		d2.setOrdenEnLlamador(2);
	    		d2.setRecurso(recurso);
	    		d2.setRequerido(true);
	    		d2.setTipo(Tipo.STRING);
	    		d2.setBorrarFlag(false);
	    		// persisto dato a solicitar
	    		agregarDatoASolicitar(recurso, agrupDato, d2);

	    		// agrego datos a solicitar Correo electrónico
	    		DatoASolicitar d3 = new DatoASolicitar();
	    		d3.setNombre(DatoASolicitar.CORREO_ELECTRONICO);
	    		d3.setRequerido(true);
	    		d3.setFila(3);
	    		d3.setColumna(1);
	    		d3.setIncluirEnReporte(true);
	    		d3.setAgrupacionDato(agrupDato);
	    		d3.setAnchoDespliegue(100);
	    		d3.setEsClave(false);
	    		d3.setEtiqueta("Correo electrónico");
	    		d3.setIncluirEnLlamador(false);
	    		d3.setLargo(100);
	    		d3.setLargoEnLlamador(150);
	    		d3.setOrdenEnLlamador(3);
	    		d3.setRecurso(recurso);
	    		d3.setRequerido(true);
	    		d3.setTipo(Tipo.STRING);
	    		d3.setBorrarFlag(false);
	    		// persisto dato a solicitar
	    		agregarDatoASolicitar(recurso, agrupDato, d3);
	    		
	    		
	    		
	    		
	            return recurso;
	            
        	} catch (Exception e) {
        		logger.error("Error crear el recurso " + cargaMasivaLinea.getNombreRecurso());
        		throw new UserException(e.getMessage());
	            //return null;
	        }
    	}
    }
	
	@TransactionTimeout(value=15, unit=TimeUnit.MINUTES)
	private Boolean guardarDisponibilidades(CamposCSV cargaMasivaLinea,Recurso recurso) throws UserException {
        try {
        	
        	//logger.info("Creando las disponibilidades, recurso: " + recurso.getId());
            
        	Date fechaInicio = null;
        	Date fechaFin = null;
        	dateFormat.setLenient(false);
        	 if(cargaMasivaLinea.getFechaInicio()!=null && !cargaMasivaLinea.getFechaInicio().trim().isEmpty()) {
                 try {
                	 fechaInicio = dateFormat.parse(cargaMasivaLinea.getFechaInicio());
                 }catch(Exception ex) {
                	 return true;
                 }
             }
        	 
             if(cargaMasivaLinea.getFechaFin()!=null && !cargaMasivaLinea.getFechaFin().isEmpty()) {
 	            try {
 	            	fechaFin = dateFormat.parse(cargaMasivaLinea.getFechaFin());
 	            }catch(Exception ex) {
 	            	return true;
 	            }
             }
        	
             
             //Validación de fechas 
             if(Utiles.esFechaInvalida(fechaInicio)){
            	 return true;  
	    	 }
            
             if(Utiles.esFechaInvalida(fechaFin)){
            	 return true;  
	    	 }
           
            //Preguntar si la fechaInicio es menor que fechaFin
            if(!fechaInicio.before(fechaFin)){
            	return true;
            }
        	
        	if(recursoEsNuevo && fechaInicio!=null && fechaFin!=null){
        		
        		Boolean sabadoHabil = Boolean.FALSE;
        		Boolean domingoHabil = Boolean.FALSE;
        		//logger.info("Recurso nuevo, se crean las disponibilidades");
	            String[] horaCupos = new String[]{};

	            Calendar start = Calendar.getInstance();
	            start.setTime(fechaInicio);
	            Calendar end = Calendar.getInstance();
	            end.setTime(fechaFin);
	            end.add(Calendar.DATE, 1);
	            
	            Calendar inicioTurnos = Calendar.getInstance();
	            Calendar finTurnos = Calendar.getInstance();
	            Calendar horaFin = Calendar.getInstance();
	            Boolean crearCupos = false;
	            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
	                crearCupos = false;
	                if ((start.get(Calendar.DAY_OF_WEEK) == 1 && !StringUtils.isBlank(cargaMasivaLinea.getDomingo()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getDomingo().split("-");
	                    domingoHabil = Boolean.TRUE;
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 2 && !StringUtils.isBlank(cargaMasivaLinea.getLunes()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getLunes().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 3 && !StringUtils.isBlank(cargaMasivaLinea.getMartes()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getMartes().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 4 && !StringUtils.isBlank(cargaMasivaLinea.getMiercoles()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getMiercoles().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 5 && !StringUtils.isBlank(cargaMasivaLinea.getJueves()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getJueves().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 6 && !StringUtils.isBlank(cargaMasivaLinea.getViernes()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getViernes().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 7 && !StringUtils.isBlank(cargaMasivaLinea.getSabado()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getSabado().split("-");
	                    sabadoHabil = Boolean.TRUE;
	                }
	                if (crearCupos) {
	                	
	                		
	                	
		                    Integer duracion = Integer.valueOf(horaCupos[2]);
		                    String inicioStr = dateFormat.format(date) + " " + horaCupos[0];
		                    inicioTurnos.setTime(timeFormat.parse(inicioStr));
		                    String finStr = dateFormat.format(date) + " " + horaCupos[1];
		                    finTurnos.setTime(timeFormat.parse(finStr));

		                    for (Date turnos = inicioTurnos.getTime(); inicioTurnos.getTime().before(finTurnos.getTime()); inicioTurnos.add(Calendar.MINUTE, duracion), turnos = inicioTurnos.getTime()) {
	                    	
	                			
		                        horaFin.setTime(turnos);
		                        horaFin.add(Calendar.MINUTE, duracion);
		                        Disponibilidad disponibilidad = new Disponibilidad();
		                        disponibilidad.setFecha(date);
		                        disponibilidad.setHoraInicio(turnos);
		                        disponibilidad.setHoraFin(horaFin.getTime());
		                        disponibilidad.setCupo(Integer.valueOf(horaCupos[3]));
		                        disponibilidad.setNumerador(ConstantesDisponibilidad.numerador);
		                        disponibilidad.setVersion(ConstantesDisponibilidad.version);
		                        disponibilidad.setPresencial(ConstantesDisponibilidad.presencial);
		                        disponibilidad.setRecurso(recurso);
		                        entityManager.persist(disponibilidad);
		                        //ema.flush();
		                        
		                    }
		                    
		                    
	                    if(sabadoHabil || domingoHabil){
	                    	if(sabadoHabil){
	                    		recurso.setSabadoEsHabil(true);
	                    	}
	                    	
	                    	if(domingoHabil){
	                    		recurso.setDomingoEsHabil(true);
	                    	}
	                    	
	                    	entityManager.merge(recurso);
	                    	
	                    }

	                }
	                
	            }
	            

	            
	            
        	}
        	else if(fechaInicio!=null && fechaFin!=null){
        		Boolean sabadoHabil = Boolean.FALSE;
        		Boolean domingoHabil = Boolean.FALSE;
        		//1. Preguntar si hay disponibilidad en ese período
        		//logger.info("Recurso no es nuevo, se crean las disponibilidades");
        		
        		String[] horaCupos = new String[]{};

	            Calendar start = Calendar.getInstance();
	            start.setTime(fechaInicio);
	            Calendar end = Calendar.getInstance();
	            end.setTime(fechaFin);
	            end.add(Calendar.DATE, 1);
	            Calendar inicioTurnos = Calendar.getInstance();
	            Calendar finTurnos = Calendar.getInstance();
	            Calendar horaFin = Calendar.getInstance();
	            Boolean crearCupos = false;
	            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
	                crearCupos = false;
	                if ((start.get(Calendar.DAY_OF_WEEK) == 1 && !StringUtils.isBlank(cargaMasivaLinea.getDomingo()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getDomingo().split("-");
	                    domingoHabil = Boolean.TRUE;
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 2 && !StringUtils.isBlank(cargaMasivaLinea.getLunes()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getLunes().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 3 && !StringUtils.isBlank(cargaMasivaLinea.getMartes()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getMartes().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 4 && !StringUtils.isBlank(cargaMasivaLinea.getMiercoles()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getMiercoles().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 5 && !StringUtils.isBlank(cargaMasivaLinea.getJueves()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getJueves().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 6 && !StringUtils.isBlank(cargaMasivaLinea.getViernes()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getViernes().split("-");
	                }
	                if ((start.get(Calendar.DAY_OF_WEEK) == 7 && !StringUtils.isBlank(cargaMasivaLinea.getSabado()))) {
	                    crearCupos = true;
	                    horaCupos = cargaMasivaLinea.getSabado().split("-");
	                    sabadoHabil = Boolean.TRUE;
	                }

	                Boolean result = disponibilidadEJB.existeDisponibilidadFechaRecurso(date,recurso);
	                //si existen disponibilidades en esta fecha
		                if(result && crearCupos){
		                	
		                	Integer duracion = Integer.valueOf(horaCupos[2]);
		                    String inicioStr = dateFormat.format(date) + " " + horaCupos[0];
		                    inicioTurnos.setTime(timeFormat.parse(inicioStr));
		                    String finStr = dateFormat.format(date) + " " + horaCupos[1];
		                    finTurnos.setTime(timeFormat.parse(finStr));
		                    
		                	Date dispInicio = timeFormat.parse(dateFormat.format(date) + " " + horaCupos[0]);
		                	Date dispFin = timeFormat.parse(dateFormat.format(date) + " " + horaCupos[1]);
		                	
		                	//Se pregunta si existen disponibilidades para una fecha en especifico con el mismo rango de horas
		                	List<Disponibilidad> disponibilidades = disponibilidadEJB.obtenerDisponibilidadesRangoHoraInicio(date,dispInicio,dispFin,recurso);
		                	
		                	if(!disponibilidades.isEmpty()){
		                			
				                	//Disponibilidad dispIni = disponibilidadEJB.obtenerDisponibilidadEnHoraInicioMin(recurso,date);
				                	//Disponibilidad dispF = disponibilidadEJB.obtenerDisponibilidadEnHoraInicioMax(recurso,date);
		                			Disponibilidad dispIni = disponibilidades.get(0);
		                			Disponibilidad dispF = disponibilidades.get(disponibilidades.size()-1);
				                	
		                			for (Date turnos = inicioTurnos.getTime(); inicioTurnos.before(finTurnos); inicioTurnos.add(Calendar.MINUTE, duracion), turnos = inicioTurnos.getTime()) {
				                        horaFin.setTime(turnos);
				                        horaFin.add(Calendar.MINUTE, duracion);
		                			}
		                			
				                	if(dispInicio.compareTo(dispIni.getHoraInicio())==0 && horaFin.getTime().compareTo(dispF.getHoraFin())==0){
				                		//las horas son iguales

				                		//logger.info("las horas son iguales actualiza las disponibilidades en los cupos");
				                		for(Disponibilidad d : disponibilidades){
				                			Integer cupoActual = d.getCupo();
		                					Integer nuevoCupo = Integer.valueOf(horaCupos[3]) + cupoActual;
				                			d.setCupo(nuevoCupo);
				                			entityManager.merge(d);
			                			}
				                		
				                	}
				                	else{
				                		//logger.info("se traslapan");
				                		return true;
				                	}
		                	}
		                	else{
			                		//logger.info("hay disponibilidades, pero no en el nuevo rango de horas");
				                    for (Date turnos = inicioTurnos.getTime(); inicioTurnos.before(finTurnos); inicioTurnos.add(Calendar.MINUTE, duracion), turnos = inicioTurnos.getTime()) {
				                        horaFin.setTime(turnos);
				                        horaFin.add(Calendar.MINUTE, duracion);
				                        Disponibilidad disponibilidad = new Disponibilidad();
				                        disponibilidad.setFecha(date);
				                        disponibilidad.setHoraInicio(turnos);
				                        disponibilidad.setHoraFin(horaFin.getTime());
				                        disponibilidad.setCupo(Integer.valueOf(horaCupos[3]));
				                        disponibilidad.setNumerador(ConstantesDisponibilidad.numerador);
				                        disponibilidad.setVersion(ConstantesDisponibilidad.version);
				                        disponibilidad.setPresencial(ConstantesDisponibilidad.presencial);
				                        disponibilidad.setRecurso(recurso);
				                        entityManager.persist(disponibilidad);
				                    }
		                		
		                	}
		                
		                	if(sabadoHabil || domingoHabil){
		                    	if(sabadoHabil){
		                    		recurso.setSabadoEsHabil(true);
		                    	}
		                    	
		                    	if(domingoHabil){
		                    		recurso.setDomingoEsHabil(true);
		                    	}
		                    	
		                    	entityManager.merge(recurso);
		                    	
		                    }
			                
		                }
		                else if(crearCupos){
		                	
			                	//Si no existe, crea el cupo
			                	//logger.info("no hay disponibilidades en la fecha consultada");
			                	Integer duracion = Integer.valueOf(horaCupos[2]);
			                    String inicioStr = dateFormat.format(date) + " " + horaCupos[0];
			                    inicioTurnos.setTime(timeFormat.parse(inicioStr));
			                    String finStr = dateFormat.format(date) + " " + horaCupos[1];
			                    finTurnos.setTime(timeFormat.parse(finStr));
			                    
			                    for (Date turnos = inicioTurnos.getTime(); inicioTurnos.before(finTurnos); inicioTurnos.add(Calendar.MINUTE, duracion), turnos = inicioTurnos.getTime()) {
			                        horaFin.setTime(turnos);
			                        horaFin.add(Calendar.MINUTE, duracion);
			                        Disponibilidad disponibilidad = new Disponibilidad();
			                        disponibilidad.setFecha(date);
			                        disponibilidad.setHoraInicio(turnos);
			                        disponibilidad.setHoraFin(horaFin.getTime());
			                        disponibilidad.setCupo(Integer.valueOf(horaCupos[3]));
			                        disponibilidad.setNumerador(ConstantesDisponibilidad.numerador);
			                        disponibilidad.setVersion(ConstantesDisponibilidad.version);
			                        disponibilidad.setPresencial(ConstantesDisponibilidad.presencial);
			                        disponibilidad.setRecurso(recurso);
			                        entityManager.persist(disponibilidad);
			                    }
			                    
			                    if(sabadoHabil || domingoHabil){
			                    	if(sabadoHabil){
			                    		recurso.setSabadoEsHabil(true);
			                    	}
			                    	
			                    	if(domingoHabil){
			                    		recurso.setDomingoEsHabil(true);
			                    	}
			                    	
			                    	entityManager.merge(recurso);
			                    	
			                    }
		                }
	            }
	            
	            
	            //Preguntar acá si es sábado o domingo
        		
        	}
        	else{
        		return true;
        	}
        	

        	
            return false;
        } catch (Exception e) {
        	logger.error("Error creando las disponibilidades  del recurso " + cargaMasivaLinea.getNombreRecurso());
        	throw new UserException(e.getMessage());
        	
        }
    }
	
	public List<RolesUsuarioRecurso> asociarRolesUsuarioRecurso(Integer usuarioId, Map<Integer, String[]> rolesRecurso) {
	  
	  //Borrar las asociaciones actuales
	  Query query = entityManager.createQuery("DELETE FROM RolesUsuarioRecurso r WHERE r.id.usuarioId=:usuarioId");
	  query = query.setParameter("usuarioId", usuarioId);
	  query.executeUpdate();
	  //Crear las nuevas asociaciones
	  List<RolesUsuarioRecurso> ret = new ArrayList<RolesUsuarioRecurso>();
	  for(Integer recursoId : rolesRecurso.keySet()) {
	    String[] roles = rolesRecurso.get(recursoId);
	    if(roles!=null && roles.length>0) {
	      String sRoles = Arrays.toString(roles).replace("[", "").replace("]", "");
        RolesUsuarioRecursoId rurId = new RolesUsuarioRecursoId();
        rurId.setRecursoId(recursoId);
        rurId.setUsuarioId(usuarioId);
	      RolesUsuarioRecurso rur = new RolesUsuarioRecurso();
	      rur.setId(rurId);
	      rur.setRoles(sRoles);
	      entityManager.persist(rur);
	      ret.add(rur);
	    }
	  }
	  return ret;
	}
	
	public List<RolesUsuarioRecurso> getRolesUsuarioRecurso(Integer usuarioId) {
    try {
      Query query = entityManager.createQuery("SELECT r FROM RolesUsuarioRecurso r WHERE r.id.usuarioId=:usuarioId");
      query = query.setParameter("usuarioId", usuarioId);
      @SuppressWarnings("unchecked")
      List<RolesUsuarioRecurso> ret = query.getResultList();
  	  return ret;
    }catch(NoResultException nrEx) {
      return new ArrayList<RolesUsuarioRecurso>();
    }
	}

  public RolesUsuarioRecurso getRolesUsuarioRecurso(Integer usuarioId, Integer recursoId) {
    try {
      Query query = entityManager.createQuery("SELECT r FROM RolesUsuarioRecurso r WHERE r.id.usuarioId=:usuarioId AND r.id.recursoId=:recursoId");
      RolesUsuarioRecurso ret = (RolesUsuarioRecurso) query.setParameter("usuarioId", usuarioId).setParameter("recursoId", recursoId).getSingleResult();
      return ret;
    }catch(Exception ex) {
      return null;
    }
  }
  
  public AccionMiPerfil obtenerAccionMiPerfilDeRecurso(Integer recursoId){
	  try {
		  AccionMiPerfil acc = new AccionMiPerfil();
		  Query query = entityManager.createQuery("SELECT a FROM AccionMiPerfil a WHERE a.recurso.id=:recursoId order by a.id desc");
	      query.setMaxResults(1);
	      List<AccionMiPerfil> list = (List<AccionMiPerfil>) query.setParameter("recursoId", recursoId).getResultList();
	      if(!list.isEmpty()){
	    	acc = list.get(0);  
	      }
	      return acc;
	    }catch(Exception ex) {
	      ex.printStackTrace();
	      return null;
	    }
  }
  
  public AccionMiPerfil obtenerAccionMiPerfilPorDefecto(Recurso recurso){
	  
	  //Creo la accion con valores por defecto
      AccionMiPerfil accionMiPerfil = new AccionMiPerfil();
      
      accionMiPerfil.setRecurso(recurso);
      
      accionMiPerfil.setDestacada_con_1(true);
      accionMiPerfil.setTitulo_con_1("Ir a ubicacion");
      accionMiPerfil.setUrl_con_1("https://www.google.com.uy/maps/@{latitud},{longitud},15z");
      
      accionMiPerfil.setTitulo_con_2("Cancelar reserva");
      accionMiPerfil.setUrl_con_2("{linkBase}/sae/cancelarReserva/Paso1.xhtml?e={empresa}&a={agenda}&ri={reserva}");
      
      accionMiPerfil.setDestacada_can_1(true);
      accionMiPerfil.setTitulo_can_1("Ir a ubicacion");
      accionMiPerfil.setUrl_can_1("https://www.google.com.uy/maps/@{latitud},{longitud},15z");
      
      accionMiPerfil.setDestacada_rec_1(true);
      accionMiPerfil.setTitulo_rec_1("Ir a ubicacion");
      accionMiPerfil.setUrl_rec_1("https://www.google.com.uy/maps/@{latitud},{longitud},15z");
      
      accionMiPerfil.setTitulo_rec_2("Cancelar reserva");
      accionMiPerfil.setUrl_rec_2("{linkBase}/sae/cancelarReserva/Paso1.xhtml?e={empresa}&a={agenda}&ri={reserva}");
       
      return accionMiPerfil;
  }
  
  public Integer getCantAccionMiPerfilDestacadasConfirmacion (AccionMiPerfil accionMiPerfil) {
	  Integer Cant = 0;
	  
	  if ( accionMiPerfil.getDestacada_con_1() && !accionMiPerfil.getUrl_con_1().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_con_2() && !accionMiPerfil.getUrl_con_2().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_con_3() && !accionMiPerfil.getUrl_con_3().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_con_4() && !accionMiPerfil.getUrl_con_4().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_con_5() && !accionMiPerfil.getUrl_con_5().isEmpty() ) {
		  Cant += 1;
	  }
	  
	  return Cant;
  }
  
  public Integer getCantAccionMiPerfilDestacadasCancelacion (AccionMiPerfil accionMiPerfil) {
	  Integer Cant = 0;
	  
	  if ( accionMiPerfil.getDestacada_can_1() && !accionMiPerfil.getUrl_can_1().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_can_2() && !accionMiPerfil.getUrl_can_2().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_can_3() && !accionMiPerfil.getUrl_can_3().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_can_4() && !accionMiPerfil.getUrl_can_4().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_can_5() && !accionMiPerfil.getUrl_can_5().isEmpty() ) {
		  Cant += 1;
	  }
	  
	  return Cant;
  }
  
  public Integer getCantAccionMiPerfilDestacadasRecordatorio (AccionMiPerfil accionMiPerfil) {
	  Integer Cant = 0;
	  
	  if ( accionMiPerfil.getDestacada_rec_1() && !accionMiPerfil.getUrl_rec_1().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_rec_2() && !accionMiPerfil.getUrl_rec_2().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_rec_3() && !accionMiPerfil.getUrl_rec_3().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_rec_4() && !accionMiPerfil.getUrl_rec_4().isEmpty() ) {
		  Cant += 1;
	  }
	  if ( accionMiPerfil.getDestacada_rec_5() && !accionMiPerfil.getUrl_rec_5().isEmpty() ) {
		  Cant += 1;
	  }
	  
	  return Cant;
  }

	
  
  public Boolean getRecursoEsNuevo() {
		return recursoEsNuevo;
  }
	
	
  public void setRecursoEsNuevo(Boolean recursoEsNuevo) {
		this.recursoEsNuevo = recursoEsNuevo;
  }

	@PersistenceContext(unitName = "SAE-EJB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
