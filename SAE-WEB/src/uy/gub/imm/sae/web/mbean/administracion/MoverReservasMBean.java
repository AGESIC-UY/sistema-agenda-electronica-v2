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

package uy.gub.imm.sae.web.mbean.administracion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import uy.gub.imm.sae.business.dto.ResultadoEjecucion;
import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.web.common.BaseMBean;

public class MoverReservasMBean extends BaseMBean {

	public static final String MSG_ID = "pantalla";
	
	static Logger logger = Logger.getLogger(MoverReservasMBean.class);

	@EJB(mappedName = "java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;
	
	@Resource(lookup = "java:jboss/infinispan/container/sae")
    private EmbeddedCacheManager saeCacheManager;
	
	private SessionMBean sessionMBean;
	
	private String moverReservasAgendaDestinoId;
	private String moverReservasRecursoDestinoId;
	private Date moverReservasFecha;
	private Date moverReservasFechaDestino;
	private Boolean moverReservasEnviarCorreo;
	private Boolean moverReservasGenerarNovedades;

	private List<SelectItem> moverReservasAgendasDisponibles;
	private Map<String, List<SelectItem>> moverReservasRecursosDisponiblesPorAgenda;
	
	private List<SelectItem> horas =  new ArrayList<SelectItem>();
	private List<SelectItem> minutos =  new ArrayList<SelectItem>();
	
	private Integer paso = 1;
	private boolean permitirMoverReservas = false;
	private int cantidadReservasMover = 0;
	private String uuid;
	
	private Integer horaD;
	private Integer minD;
	private Integer horaH;
	private Integer minH;
	private Integer horaInicioDestino;
	private Integer minInicioDestino;
	
	private Boolean verPaso2 = Boolean.FALSE;
	private Boolean disablePaso1 = Boolean.FALSE;
	private Boolean reservasMovidas = Boolean.FALSE;
	private Calendar c0 = new GregorianCalendar();
	private Calendar c1 = new GregorianCalendar();
	private Calendar c2 = new GregorianCalendar();
	private Calendar c3 = new GregorianCalendar();
			
	
	@PostConstruct
	public void initAgendaRecurso() {

		boolean hayError = false;
		// Se controla que estén marcados la agenda y el recurso origen
		if (sessionMBean.getAgendaMarcada() == null) {
			hayError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_una_agenda_seleccionada"), MSG_ID);
		}
		// Se controla que se haya Marcado un recurso
		if (sessionMBean.getRecursoMarcado() == null) {
			hayError = true;
			addErrorMessage(sessionMBean.getTextos().get("debe_haber_un_recurso_seleccionado"), MSG_ID);
		}
		if (hayError) {
			return;
		}

		//Cargar la lista de agendas destino
		moverReservasAgendasDisponibles = new ArrayList<>();
		moverReservasRecursosDisponiblesPorAgenda = new HashMap<>();
		moverReservasAgendaDestinoId = "";
		moverReservasRecursoDestinoId = "";		
		try {
			moverReservasAgendasDisponibles.add(new SelectItem("", sessionMBean.getTextos().get("seleccionar")));
			moverReservasRecursosDisponiblesPorAgenda.put("", new ArrayList<SelectItem>());
			moverReservasRecursosDisponiblesPorAgenda.get("").add(new SelectItem("", sessionMBean.getTextos().get("seleccionar")));
			for(Agenda agenda : agendarReservasEJB.consultarAgendas()) {
				moverReservasAgendasDisponibles.add(new SelectItem(agenda.getId().toString(), agenda.getNombre()));
				moverReservasRecursosDisponiblesPorAgenda.put(agenda.getId().toString(), new ArrayList<SelectItem>());
				moverReservasRecursosDisponiblesPorAgenda.get(agenda.getId().toString()).add(new SelectItem("", sessionMBean.getTextos().get("seleccionar")));
				for(Recurso recurso : agendarReservasEJB.consultarRecursos(agenda)) {
						moverReservasRecursosDisponiblesPorAgenda.get(agenda.getId().toString()).add(new SelectItem(recurso.getId().toString(), recurso.getNombre()));
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			addErrorMessage(sessionMBean.getTextos().get("no_se_pudo_cargar_lista_de_agendas"), MSG_ID);
		}
		
		this.cargarListaHoras();
		this.cargarListaMinutos();
	}
	
	
	
	private void cargarListaHoras(){
		horas =  new ArrayList<SelectItem>();
    Integer h = 0;
    String labelH;
    while (h < 24){
			SelectItem s = new SelectItem();
			s.setValue(h);
			labelH = Integer.toString(h);
			if (labelH.length()<2){
				labelH = "0"+labelH;
			}
			s.setLabel(labelH);
			horas.add(s);
			h = h + 1;
		}
	}

	private void cargarListaMinutos(){
		minutos =  new ArrayList<SelectItem>();
    Integer h = 0;
    String labelH;
    while (h < 60){
			SelectItem s = new SelectItem();
			s.setValue(h);
			labelH = Integer.toString(h);
			if (labelH.length()<2){
				labelH = "0"+labelH;
			}
			s.setLabel(labelH);
			minutos.add(s);
			h = h + 1;
		}
	}

	public void beforePhaseMoverReservas(PhaseEvent event) {
		// Verificar que el usuario tiene permisos para acceder a esta página
	    if(!BooleanUtils.isTrue(sessionMBean.getUsuarioActual().isSuperadmin())) {
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        ctx.getApplication().getNavigationHandler().handleNavigation(ctx, "", "noAutorizado");
	    }	    
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("mover_reservas"));
		}
	}

	public void moverReservasAgendaSeleccionada() {
		//Solo deseleccionar el recurso que pudiera estar seleccionado
		moverReservasRecursoDestinoId = "";
	}
	
	
	public void validarRecursoOrigen() {
		boolean hayError = false;
		if(moverReservasFecha==null) {
			addErrorMessage(sessionMBean.getTextos().get("seleccione_un_dia"), MSG_ID);
			hayError = true;
		}
		
		try {
			
			Recurso recursoOrigen = sessionMBean.getRecursoMarcado();
			Empresa empresa = sessionMBean.getEmpresaActual();
			
			//Determinar el timezone del recurso 
			TimeZone timezoneOrigen = TimeZone.getDefault();
			if(recursoOrigen.getAgenda().getTimezone()!=null && !recursoOrigen.getAgenda().getTimezone().isEmpty()) {
				timezoneOrigen = TimeZone.getTimeZone(recursoOrigen.getAgenda().getTimezone());
			}else {
				if(empresa.getTimezone()!=null && !empresa.getTimezone().isEmpty()) {
					timezoneOrigen = TimeZone.getTimeZone(empresa.getTimezone());
				}
			}
			
			c0.setTime(moverReservasFecha); //Debe estar en GMT0
			
			c1.set(Calendar.YEAR, c0.get(Calendar.YEAR));
			c1.set(Calendar.MONTH, c0.get(Calendar.MONTH));
			c1.set(Calendar.DAY_OF_MONTH, c0.get(Calendar.DAY_OF_MONTH));
			c1.set(Calendar.HOUR_OF_DAY, this.horaD);
			c1.set(Calendar.MINUTE, this.minD);
			c1.set(Calendar.SECOND, 0);
			c1.set(Calendar.MILLISECOND, 0);
			
			c2.set(Calendar.YEAR, c0.get(Calendar.YEAR));
			c2.set(Calendar.MONTH, c0.get(Calendar.MONTH));
			c2.set(Calendar.DAY_OF_MONTH, c0.get(Calendar.DAY_OF_MONTH));
			c2.set(Calendar.HOUR_OF_DAY, this.horaH);
			c2.set(Calendar.MINUTE, this.minH);
			c2.set(Calendar.SECOND, 0);
			c2.set(Calendar.MILLISECOND, 0);
			
			if (c1.getTime().compareTo(c2.getTime()) >= 0){
				addErrorMessage(sessionMBean.getTextos().get("la_hora_de_fin_debe_ser_posterior_a_la_hora_de_inicio"), MSG_ID);
				hayError = true;
			}
			
			//Buscar las reservas en estado R
			VentanaDeTiempo ventana = new VentanaDeTiempo();
			ventana.setFechaInicial(c1.getTime());
			ventana.setFechaFinal(c2.getTime());
			
			//logger.info("RECURSO ORIGEN ID: " + recursoOrigen.getId() );
			Long cantReservas = agendarReservasEJB.obtenerReservasConfirmadasRecursoOrigen(recursoOrigen,ventana);
			logger.info("CANTIDAD DE RESERVAS " + cantReservas + " EN LA VENTANA :" +  ventana.getFechaInicial().toString() + " y " + ventana.getFechaFinal().toString());
			if(cantReservas<=0){
				addErrorMessage(sessionMBean.getTextos().get("no_existen_reservas_recurso_origen"), MSG_ID);
				hayError = true;
			}
			
			if (hayError) {
				return;
			}
			
			disablePaso1 = Boolean.TRUE;
			verPaso2 = Boolean.TRUE;
		}catch(Exception ex) {
			ex.printStackTrace();
			//addErrorMessage(sessionMBean.getTextos().get("no_se_validar_las_reservas"), MSG_ID);
		}
	}
	
	
	public void moverReservasValidar() {
		boolean hayError = false;
		if(StringUtils.isBlank(moverReservasAgendaDestinoId) || StringUtils.isBlank(moverReservasRecursoDestinoId)) {
			addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_agenda_recurso_destino"), MSG_ID);
			hayError = true;
		}
		if(moverReservasFechaDestino==null) {
			addErrorMessage(sessionMBean.getTextos().get("fecha_destino_vacia"), MSG_ID);
			hayError = true;
		}
		
		if (hayError) {
			return;
		}
		
		try {
			Recurso recursoOrigen = sessionMBean.getRecursoMarcado();
			Agenda agendaDestino = agendarReservasEJB.consultarAgendaPorId(Integer.valueOf(moverReservasAgendaDestinoId));
			Recurso recursoDestino = agendarReservasEJB.consultarRecursoPorId(agendaDestino, Integer.valueOf(moverReservasRecursoDestinoId));
			
			c0.setTime(moverReservasFechaDestino); //Debe estar en GMT0
			
			//Hora Inicio Destino
			c3.set(Calendar.YEAR, c0.get(Calendar.YEAR));
			c3.set(Calendar.MONTH, c0.get(Calendar.MONTH));
			c3.set(Calendar.DAY_OF_MONTH, c0.get(Calendar.DAY_OF_MONTH));
			c3.set(Calendar.HOUR_OF_DAY, this.horaInicioDestino);
			c3.set(Calendar.MINUTE, this.minInicioDestino);
			c3.set(Calendar.SECOND, 0);
			c3.set(Calendar.MILLISECOND, 0);
			
			if(recursoOrigen.equals(recursoDestino)){
				if(c1.getTime().compareTo(c3.getTime())==0){
					addErrorMessage(sessionMBean.getTextos().get("recurso_origen_recurso_destino_hora_inicio_distintas"), MSG_ID);
					hayError = true;
				}
			}
			
			if (hayError) {
				return;
			}
			
			Empresa empresa = sessionMBean.getEmpresaActual();
			
			VentanaDeTiempo ventanaOrigen = new VentanaDeTiempo();
			ventanaOrigen.setFechaInicial(c1.getTime());
			ventanaOrigen.setFechaFinal(c2.getTime());
			
			VentanaDeTiempo ventanaDestino = new VentanaDeTiempo();
			ventanaDestino.setFechaInicial(c3.getTime());
			ventanaDestino.setFechaFinal(Utiles.time2FinDelDia(c0.getTime()));
			
			ResultadoEjecucion resultado = agendarReservasEJB.validarMoverReservas(empresa, recursoOrigen, recursoDestino,ventanaOrigen, ventanaDestino);
			
			permitirMoverReservas = resultado.getErrores().isEmpty();
			if(permitirMoverReservas) {
				for(String mensaje : resultado.getMensajes()) {
					addAdvertenciaMessage(mensaje, MSG_ID);
				}
				
				if(!resultado.getMensajes().isEmpty()){
					addAdvertenciaMessage("Presione el botón Ejecutar para proceder con el movimiento", MSG_ID);
				}
				
				for(String warning : resultado.getWarnings()) {
					addAdvertenciaMessage(warning, MSG_ID);
				}
				paso = 2;
			}else {
				for(String error : resultado.getErrores()) {
					addErrorMessage(error, MSG_ID);
				}
			}
			cantidadReservasMover = resultado.getElementos();
			Cache saeCache = saeCacheManager.getCache("sae");
			uuid = UUID.randomUUID().toString();
			saeCache.put(uuid, 0);
		}catch(Exception ex) {
			ex.printStackTrace();
			addErrorMessage(sessionMBean.getTextos().get("no_se_validar_las_reservas"), MSG_ID);
		}
	}
	
	public void moverReservasCancelar() {
		paso = 1;
	}
	
	public String cancelar() {
		return "moverReservas";
	}
	
	public void moverReservasEjecutar() {
		try {
			String linkBase = null;
			if(moverReservasEnviarCorreo) {
				HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
				linkBase = request.getScheme()+"://"+request.getServerName();
			    if("http".equals(request.getScheme()) && request.getServerPort()!=80 || "https".equals(request.getScheme()) && request.getServerPort()!=443) {
			        linkBase = linkBase + ":" + request.getServerPort();
			    }
			}			
			Recurso recursoOrigen = sessionMBean.getRecursoMarcado();
			//Obtener la agenda y recurso destino
			Agenda agendaDestino = agendarReservasEJB.consultarAgendaPorId(Integer.valueOf(moverReservasAgendaDestinoId));
			Recurso recursoDestino = agendarReservasEJB.consultarRecursoPorId(agendaDestino, Integer.valueOf(moverReservasRecursoDestinoId));
			Empresa empresa = sessionMBean.getEmpresaActual();
			
			VentanaDeTiempo ventanaOrigen = new VentanaDeTiempo();
			ventanaOrigen.setFechaInicial(c1.getTime());
			ventanaOrigen.setFechaFinal(c2.getTime());
			
			VentanaDeTiempo ventanaDestino = new VentanaDeTiempo();
			ventanaDestino.setFechaInicial(c3.getTime());
			ventanaDestino.setFechaFinal(Utiles.time2FinDelDia(c0.getTime()));
			
			//Ejecutar la movida de reservas
			ResultadoEjecucion resultado = agendarReservasEJB.ejecutarMoverReservas(empresa, recursoOrigen, recursoDestino, ventanaOrigen,ventanaDestino,
					moverReservasEnviarCorreo, linkBase, moverReservasGenerarNovedades, uuid);
			for(String mensaje : resultado.getMensajes()) {
				addInfoMessage(mensaje, MSG_ID);
			}
			for(String warning : resultado.getWarnings()) {
				addAdvertenciaMessage(warning, MSG_ID);
			}
			for(String error : resultado.getErrores()) {
				addErrorMessage(error, MSG_ID);
			}
			if(resultado.getErrores().isEmpty()) {
				reservasMovidas = Boolean.TRUE;
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			addErrorMessage("Ha ocurrido un error en el movimiento de reservas, si el error persiste contacte al administrador", MSG_ID);
		} finally {
		    uuid = null;
		}
	}
	
	public String volverPagInicio() {
		return "volver";
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public String getMoverReservasAgendaDestinoId() {
		return moverReservasAgendaDestinoId;
	}

	public void setMoverReservasAgendaDestinoId(String moverReservasAgendaDestinoId) {
		this.moverReservasAgendaDestinoId = moverReservasAgendaDestinoId;
	}

	public String getMoverReservasRecursoDestinoId() {
		return moverReservasRecursoDestinoId;
	}

	public void setMoverReservasRecursoDestinoId(String moverReservasRecursoDestinoId) {
		this.moverReservasRecursoDestinoId = moverReservasRecursoDestinoId;
	}

	public List<SelectItem> getMoverReservasAgendasDisponibles() {
		return moverReservasAgendasDisponibles;
	}

	public List<SelectItem> getMoverReservasRecursosDisponibles() {
		if(moverReservasAgendaDestinoId==null) {
			moverReservasAgendaDestinoId = "";
		}
		return moverReservasRecursosDisponiblesPorAgenda.get(moverReservasAgendaDestinoId);
	}

	public Date getMoverReservasFecha() {
		return moverReservasFecha;
	}

	public void setMoverReservasFecha(Date moverReservasFecha) {
		this.moverReservasFecha = moverReservasFecha;
	}

	public Boolean getMoverReservasEnviarCorreo() {
		return moverReservasEnviarCorreo;
	}

	public void setMoverReservasEnviarCorreo(Boolean moverReservasEnviarCorreo) {
		this.moverReservasEnviarCorreo = moverReservasEnviarCorreo;
	}

	public Boolean getMoverReservasGenerarNovedades() {
		return moverReservasGenerarNovedades;
	}

	public void setMoverReservasGenerarNovedades(
			Boolean moverReservasGenerarNovedades) {
		this.moverReservasGenerarNovedades = moverReservasGenerarNovedades;
	}

	public boolean isPermitirMoverReservas() {
		return permitirMoverReservas;
	}

	public Integer getPaso() {
		return paso;
	}

    public int getProgreso() {
        if(uuid == null || cantidadReservasMover==0) {
            return 0;
        }
        Cache saeCache = saeCacheManager.getCache("sae");
        if(saeCache == null || !saeCache.containsKey(uuid)) {
            return 0;
        }
        int elementosProcesados = (Integer) saeCache.get(uuid);
        int progreso = Math.round(elementosProcesados * 100 / cantidadReservasMover);
        return progreso<=100?progreso:100;
    }



	public List<SelectItem> getHoras() {
		return horas;
	}



	public void setHoras(List<SelectItem> horas) {
		this.horas = horas;
	}



	public List<SelectItem> getMinutos() {
		return minutos;
	}



	public void setMinutos(List<SelectItem> minutos) {
		this.minutos = minutos;
	}



	public Integer getHoraD() {
		return horaD;
	}



	public void setHoraD(Integer horaD) {
		this.horaD = horaD;
	}



	public Integer getMinD() {
		return minD;
	}



	public void setMinD(Integer minD) {
		this.minD = minD;
	}



	public Integer getHoraH() {
		return horaH;
	}



	public void setHoraH(Integer horaH) {
		this.horaH = horaH;
	}



	public Integer getMinH() {
		return minH;
	}



	public void setMinH(Integer minH) {
		this.minH = minH;
	}



	public Boolean getVerPaso2() {
		return verPaso2;
	}



	public void setVerPaso2(Boolean verPaso2) {
		this.verPaso2 = verPaso2;
	}



	public Boolean getDisablePaso1() {
		return disablePaso1;
	}



	public void setDisablePaso1(Boolean disablePaso1) {
		this.disablePaso1 = disablePaso1;
	}



	public Integer getHoraInicioDestino() {
		return horaInicioDestino;
	}



	public void setHoraInicioDestino(Integer horaInicioDestino) {
		this.horaInicioDestino = horaInicioDestino;
	}



	public Integer getMinInicioDestino() {
		return minInicioDestino;
	}



	public void setMinInicioDestino(Integer minInicioDestino) {
		this.minInicioDestino = minInicioDestino;
	}



	public Date getMoverReservasFechaDestino() {
		return moverReservasFechaDestino;
	}



	public void setMoverReservasFechaDestino(Date moverReservasFechaDestino) {
		this.moverReservasFechaDestino = moverReservasFechaDestino;
	}



	public Boolean getReservasMovidas() {
		return reservasMovidas;
	}



	public void setReservasMovidas(Boolean reservasMovidas) {
		this.reservasMovidas = reservasMovidas;
	}
    
    
    
	
}
