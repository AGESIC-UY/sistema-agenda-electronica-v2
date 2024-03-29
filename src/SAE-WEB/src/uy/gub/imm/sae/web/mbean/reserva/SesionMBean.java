package uy.gub.imm.sae.web.mbean.reserva;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.business.ejb.facade.Configuracion;
import uy.gub.imm.sae.common.SofisHashMap;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Agenda;
import uy.gub.imm.sae.entity.DatoASolicitar;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TokenReserva;
import uy.gub.imm.sae.entity.global.Empresa;
import uy.gub.imm.sae.exception.ApplicationException;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.RowList;
import uy.gub.imm.sae.web.common.SelectItemComparator;

public class SesionMBean	extends BaseMBean {
	
	private static Logger LOGGER = Logger.getLogger(SesionMBean.class);
	
	private String paginaDeRetorno;
	
	private Agenda agenda;
	private Recurso recurso;
	private Map<String, DatoASolicitar> datosASolicitar;
	
	private VentanaDeTiempo ventanaCalendario;
	private VentanaDeTiempo ventanaMesSeleccionado;
	private List<Integer> cuposXdiaMesSeleccionado;
  private Date currentDate;
	 
	private Date diaSeleccionado;
	private RowList<Disponibilidad> disponibilidadesDelDiaMatutina;
	private RowList<Disponibilidad> disponibilidadesDelDiaVespertina;

	private Disponibilidad disponibilidad;
	private Reserva reserva;
	private Reserva reservaConfirmada;
	private Reserva reservaCancelar;
	private Boolean envioCorreoReserva = Boolean.TRUE;

  
  private Reserva reservaModificar1; //Reserva que se desea modificar
  private Reserva reservaModificar2; //Reserva nueva elegida por el ciudadano
  
  
	private String urlPaso1Reserva;
	private String urlTramite;
	private String parmsDatosCiudadano;
	private String codigoTrazabilidadPadre;
	private String codigoTramite;
	
	private String paso3Captcha;

	private Empresa empresaActual;

	private String idiomaActual = Locale.getDefault().getLanguage();
	
	private boolean mostrarFechaActual = false;

  private AgendarReservas agendarReservasEJB;
	
  @EJB(mappedName = "java:global/sae-1-service/sae-ejb/ConfiguracionBean!uy.gub.imm.sae.business.ejb.facade.ConfiguracionRemote")
  private Configuracion configuracionEJB;
	
  private String googleAnalytics;
  
  //Para la reserva múltiple
  private TokenReserva tokenReserva;
  
  @PostConstruct
  public void init() {
    try {
      agendarReservasEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getAgendarReservas();
      //Cargar los textos dependientes del idioma
      cargarTextos();
      //Cargar las propiedades de configuracion
      Boolean bMostrarFechaActual = configuracionEJB.getBoolean("MOSTRAR_FECHA_ACTUAL");
      if(bMostrarFechaActual!=null) {
        mostrarFechaActual = bMostrarFechaActual.booleanValue();
      }
      //Cargar el código de Google Analytics, si existe
      googleAnalytics = configuracionEJB.getString("GOOGLE_ANALYTICS");
      if(googleAnalytics!=null && googleAnalytics.trim().isEmpty()) {
        googleAnalytics = null;
      }
    } catch (ApplicationException e) {
      LOGGER.error(e);
      redirect(ERROR_PAGE_OUTCOME);     
    }
  } 
	
	public TimeZone getTimeZone() {
		//Primero se devuelve la de la Agenda, si tiene
		if(agenda != null && agenda.getTimezone() != null  && !agenda.getTimezone().trim().isEmpty()) {
			return TimeZone.getTimeZone(agenda.getTimezone());
		}
		//Si no tiene la Agenda, se devuelve la de la Empresa
		if(empresaActual != null && empresaActual.getTimezone() != null) {
			return TimeZone.getTimeZone(empresaActual.getTimezone());
		}
		//En otro caso se devuelve uno por defecto
		return TimeZone.getDefault();
	}
	
	public String getFormatoFecha() {
		if(empresaActual != null && empresaActual.getFormatoFecha() != null) {
			return empresaActual.getFormatoFecha();
		}
		return "dd/MM/yyyy";
	}
	
	public String getFormatoHora() {
		if(empresaActual != null && empresaActual.getFormatoHora() != null) {
			return empresaActual.getFormatoHora();
		}
		return "HH:mm";
	}
	
	public String getLocale() {
		return idiomaActual;
	}
	
	/*
	 * En el DatePicker el formato usa lo siguiente:
	 *   dd: día en dos dígitos
	 *   mm: mes en dos dígitos
	 *   yy: año en cuatro dígitos
	 */
	public String getFormatoFechaDatepicker() {
		String formatoJava = "dd/MM/yyyy";
		if(empresaActual != null && empresaActual.getFormatoFecha() != null) {
			formatoJava = empresaActual.getFormatoFecha();
		}
		return formatoJava.replace("yyyy", "yy").replace("MM", "mm");
	}
	
  public boolean isMostrarFechaActual() {
    return this.mostrarFechaActual;
  }
  
  public String getFechaActual() {
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.MILLISECOND, getTimeZone().getOffset((new Date()).getTime()));
    DateFormat df = new SimpleDateFormat(getFormatoFecha()+" "+getFormatoHora());
    return df.format(cal.getTime());
  }
  
	
	//**************************************************************************************************************************************************************
	//***************************************** CDA *****************************************************************************************************
	//**************************************************************************************************************************************************************
	private String usuarioCda = null;
	
	public String getUsuarioCda() {
		return usuarioCda;
	}

	public void setUsuarioCda(String usuarioCda) {
		this.usuarioCda = usuarioCda;
	}


	//**************************************************************************************************************************************************************
	//*****************************************CANCELAR RESERVA*****************************************************************************************************
	//**************************************************************************************************************************************************************
	private List<Reserva> listaReservas = new ArrayList<Reserva>();
	private Reserva reservaDatos;
	private Disponibilidad disponibilidadCancelarReserva;
	private String urlCancelarReserva;
	private Integer empresaId;
	private Integer agendaId;
	private Integer recursoId;
	private Integer reservaId;
	private String codigoSeguridadReserva;
	private boolean renderedVolverBotom;
	private boolean SeHizoSeleccion;
	
	public void seleccionarAgenda(Integer agendaId) throws BusinessException, RolException, ApplicationException {
		limpiarSesion();
		this.agenda = null;
		this.agenda = agendarReservasEJB.consultarAgendaPorId(agendaId);
	}
	
	public void seleccionarAgendaPorReserva(Integer reservId) throws BusinessException, RolException, ApplicationException {
		limpiarSesion();
		this.agenda = null;
		this.agenda = agendarReservasEJB.consultarAgendaPorId(agendaId);
	}

	public String getPaginaDeRetorno() {
		return paginaDeRetorno;
	}
	
	public void setPaginaDeRetorno(String paginaDeRetorno) {
		this.paginaDeRetorno = paginaDeRetorno;
	}
	
	public Agenda getAgenda() {
		return agenda;
	}
	
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	} 

	public Recurso getRecurso() {
		return recurso;
	}

	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}

	public Date getDiaSeleccionado() {
		return diaSeleccionado;
	}

	public void setDiaSeleccionado(Date diaSeleccionado) {
		if (diaSeleccionado == null) {
			this.diaSeleccionado = null;
			this.disponibilidadesDelDiaMatutina = null;
			this.disponibilidadesDelDiaVespertina = null;
			this.disponibilidad = null;
		} else {
			if(this.diaSeleccionado == null || !diaSeleccionado.equals(this.diaSeleccionado)) {
				this.diaSeleccionado = diaSeleccionado;
				this.disponibilidadesDelDiaMatutina = null;
				this.disponibilidadesDelDiaVespertina = null;
				this.disponibilidad = null;
			}
		}
	}

	public RowList<Disponibilidad> getDisponibilidadesDelDiaMatutina() {
		return disponibilidadesDelDiaMatutina;
	}

	public void setDisponibilidadesDelDiaMatutina(RowList<Disponibilidad> disponibilidadesDelDiaRows) {
		this.disponibilidadesDelDiaMatutina = disponibilidadesDelDiaRows;
	}

	public RowList<Disponibilidad> getDisponibilidadesDelDiaVespertina() {
		return disponibilidadesDelDiaVespertina;
	}

	public void setDisponibilidadesDelDiaVespertina(RowList<Disponibilidad> disponibilidadesDelDiaRows) {
		this.disponibilidadesDelDiaVespertina = disponibilidadesDelDiaRows;
	}
	
	public Disponibilidad getDisponibilidad() {
		return disponibilidad;
	}

	public void setDisponibilidad(Disponibilidad disponibilidad) {
		this.disponibilidad = disponibilidad;
	}
	
	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}
	
	public Reserva getReservaConfirmada() {
		return reservaConfirmada;
	}

	public void setReservaConfirmada(Reserva reservaConfirmada) {
		this.reservaConfirmada = reservaConfirmada;
	}

	public Reserva getReservaCancelar() {
    return reservaCancelar;
  }

  public void setReservaCancelar(Reserva reservaCancelar) {
    this.reservaCancelar = reservaCancelar;
  }

  /**
	 * El calendario usa este atributo para saber que en mes posicionarse, 
	 * inicialmente se posiciona en el mes correspondiente a la fecha inicial de la
	 * la ventana del calendario de la agenda, pero luego mediante el setCurrentDate
	 * el componente grafico actualiza esta fecha según nos movamos en los meses/años
	 * del calendario. No confundir con el dia seleccionado en el calendario. 
	 */
	public Date getCurrentDate() {
		return currentDate;
	}	
	public void setCurrentDate(Date date) {
		currentDate = date;
	}

	public VentanaDeTiempo getVentanaCalendario() {
		return ventanaCalendario;
	}

	public void setVentanaCalendario(VentanaDeTiempo ventanaCalendario) {
		this.ventanaCalendario = ventanaCalendario;
	}

	public VentanaDeTiempo getVentanaMesSeleccionado() {
		return ventanaMesSeleccionado;
	}

	public void setVentanaMesSeleccionado(VentanaDeTiempo ventanaMesSeleccionado) {
		this.ventanaMesSeleccionado = ventanaMesSeleccionado;
	}

	public List<Integer> getCuposXdiaMesSeleccionado() {
		return cuposXdiaMesSeleccionado;
	}

	public void setCuposXdiaMesSeleccionado(List<Integer> cuposXdiaMesSeleccionado) {
		this.cuposXdiaMesSeleccionado = cuposXdiaMesSeleccionado;
	}

	public void setDatosASolicitar(Map<String, DatoASolicitar> datos) {
		this.datosASolicitar = datos;
	}

	public Map<String, DatoASolicitar> getDatosASolicitar() {
		return this.datosASolicitar;
	}
	
	private void limpiarSesion() {
		agenda = null;
		recurso = null;
		datosASolicitar = null;
		ventanaCalendario = null;
		ventanaMesSeleccionado = null;
		cuposXdiaMesSeleccionado = null;
    currentDate = null;
		diaSeleccionado = null;
		limpiarPaso2();
	}

	public void limpiarPaso2() {
		disponibilidadesDelDiaMatutina = null;
		disponibilidadesDelDiaVespertina = null;
		disponibilidad = null;
		reserva = null;
		limpiarPaso3();
	}

	public void limpiarPaso3() {
	}

	public String getUrlPaso1Reserva() {
		return urlPaso1Reserva;
	}

	public void setUrlPaso1Reserva(String urlPaso1Reserva) {
		this.urlPaso1Reserva = urlPaso1Reserva;
	}
	
	//**************************************************************************************************************************************************************
	//*****************************************CANCELAR RESERVA*****************************************************************************************************
	//**************************************************************************************************************************************************************
	public List<Reserva> getListaReservas() {
		return listaReservas;
	}

	public void setListaReservas(List<Reserva> listaReservas) {
		this.listaReservas = listaReservas;
	}
	
	public Reserva getReservaDatos() {
		return reservaDatos;
	}

	public void setReservaDatos(Reserva reservaDatos) {
		this.reservaDatos = reservaDatos;
	}
	
	public Disponibilidad getDisponibilidadCancelarReserva() {
		return disponibilidadCancelarReserva;
	}

	public void setDisponibilidadCancelarReserva(
			Disponibilidad disponibilidadCancelarReserva) {
		this.disponibilidadCancelarReserva = disponibilidadCancelarReserva;
	}
	
	public String getUrlCancelarReserva() {
		return urlCancelarReserva;
	}

	public void setUrlCancelarReserva(String urlCancelarReserva) {
		this.urlCancelarReserva = urlCancelarReserva;
	}
		
	public Empresa getEmpresaActual() {
		return empresaActual;
	}

	public void setEmpresaActual(Empresa empresaActual) {
		this.empresaActual = empresaActual;
	}

	public StreamedContent getEmpresaActualLogo() {
		//No se puede cachear porque un stream y la segunda vez que el cliente lo pide está cerrado
		if(empresaActual !=null && empresaActual.getLogo() !=null) {
			return new DefaultStreamedContent(new ByteArrayInputStream(empresaActual.getLogo()));
		}
		return null;
	}

	public String getEmpresaActualLogoTexto() {
		//No se puede cachear porque un stream y la segunda vez que el cliente lo pide está cerrado
		if(empresaActual !=null) {
			if(empresaActual.getLogoTexto() != null && !empresaActual.getLogoTexto().trim().isEmpty()) {
				return empresaActual.getLogoTexto();
			}else {
				return empresaActual.getNombre();
			}
		}
		return "Logo de la empresa";
	}
	
	public String getEmpresaActualNombre() {
		if (empresaActual != null) {
			return empresaActual.getNombre();
		} else {
			return "";
		}
	}
	
	public Integer getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Integer empresaId) {
		this.empresaId = empresaId;
	}

	public Integer getAgendaId() {
		return agendaId;
	}

	public void setAgendaId(Integer agendaId) {
		this.agendaId = agendaId;
	}

	public Integer getRecursoId() {
		return recursoId;
	}

	public void setRecursoId(Integer recursoId) {
		this.recursoId = recursoId;
	}

	public String getCodigoSeguridadReserva() {
		return codigoSeguridadReserva;
	}

	public void setCodigoSeguridadReserva(String codigoSeguridadReserva) {
		this.codigoSeguridadReserva = codigoSeguridadReserva;
	}

	public Integer getReservaId() {
		return reservaId;
	}

	public void setReservaId(Integer reservaId) {
		this.reservaId = reservaId;
	}

	public boolean isRenderedVolverBotom() {
		return renderedVolverBotom;
	}

	public void setRenderedVolverBotom(boolean renderedVolverBotom) {
		this.renderedVolverBotom = renderedVolverBotom;
	}

	public boolean isSeHizoSeleccion() {
		return SeHizoSeleccion;
	}

	public void setSeHizoSeleccion(boolean seHizoSeleccion) {
		SeHizoSeleccion = seHizoSeleccion;
	}

  public String getGoogleAnalytics() {
    return googleAnalytics;
  }

	//*******************************************************************
	// PARA LOS TEXTOS DEPENDIENTES DEL IDIOMA
	//*******************************************************************
	
  public String getIdiomaActual() {
		return idiomaActual;
	}

	public void setIdiomaActual(String idiomaActual) {
		this.idiomaActual = idiomaActual;
	}

	private Map<String, String> textos = new SofisHashMap();
	private Map<String, String> preguntasCaptcha = new HashMap<String, String>();

	public void cargarTextos() {
		try {
			textos = agendarReservasEJB.consultarTextos(idiomaActual);
		} catch (ApplicationException e) {
			textos = new SofisHashMap();
			e.printStackTrace();
		}
		try {
			preguntasCaptcha = agendarReservasEJB.consultarPreguntasCaptcha(idiomaActual);
		} catch (ApplicationException e) {
			preguntasCaptcha = new HashMap<String, String>();
			e.printStackTrace();
		}
	}
	
  public Map<String, String> getTextos() {
    return textos;
  }
  
	public Map<String, String> getPreguntasCaptcha() {
		return preguntasCaptcha;
	}
	
	public List<SelectItem> getIdiomasSoportados() {
		
		List<SelectItem> idiomas = new ArrayList<SelectItem>();
		if(agenda != null) {
			String sIdiomasSoportados = agenda.getIdiomas();
			if(sIdiomasSoportados != null) {
				String[] idiomasSoportados = sIdiomasSoportados.split(",");
				for(String idiomaSoportado : idiomasSoportados) {
					if(!idiomaSoportado.trim().isEmpty()) {
						Locale locale = new Locale(idiomaSoportado);
						idiomas.add(new SelectItem(locale.getLanguage(), locale.getDisplayLanguage(locale)));
					}
				}
			}
			Collections.sort(idiomas, new SelectItemComparator());
		}
		return idiomas;
	}
  
	public void cambioIdiomaActual(ValueChangeEvent event) {
	  limpiarMensajesError();
    String idiomaSeleccionado = (String) event.getNewValue();
    cambioIdiomaActual(idiomaSeleccionado);
	}

  public void cambioIdiomaActual(String idiomaSeleccionado) {
    try {
      LOGGER.debug("Cambiando idoma de la interfaz...");
      List<String> idiomasDisponibles = Arrays.asList(agenda.getIdiomas().split(","));
      //Se verifica que el idioma indicado sea uno de los disponibles, sino no se permite el cambio
      LOGGER.debug("Verificando que el idioma seleccionado ("+idiomaSeleccionado+") sea uno de los soportados ("+idiomasDisponibles+")");
      if(!idiomasDisponibles.contains(idiomaSeleccionado)) {
        LOGGER.warn("No se pudo cambiar el idioma porque "+idiomaSeleccionado+" no es un idioma soportado");
        return;
      }
      idiomaActual = idiomaSeleccionado;
      //Este javascript es para cambiar el atributo lang del elemento <html> en la página actual
      //(sin esto ese atributo no cambia en la página actual y UNIT lo marca como un error)
      RequestContext.getCurrentInstance().execute("document.documentElement.lang='"+idiomaActual+"'");
      LOGGER.debug("Idioma cambiado, cargando textos...");
      cargarTextos();
      LOGGER.debug("Textos cargados.");
    }catch(Exception ex) {
      LOGGER.error("No se pudo cambiar el idioma", ex);
    }
  }

	public String getUrlTramite() {
		return urlTramite;
	}

	public void setUrlTramite(String urlTramite) {
		this.urlTramite = urlTramite;
	}

	public String getParmsDatosCiudadano() {
		return parmsDatosCiudadano;
	}

	public void setParmsDatosCiudadano(String parmsDatosCiudadano) {
		this.parmsDatosCiudadano = parmsDatosCiudadano;
	}

	public String getCodigoTrazabilidadPadre() {
		return codigoTrazabilidadPadre;
	}

	public void setCodigoTrazabilidadPadre(String codigoTrazabilidadPadre) {
		this.codigoTrazabilidadPadre = codigoTrazabilidadPadre;
	}
	
  public String getCodigoTramite() {
    return codigoTramite;
  }

  public void setCodigoTramite(String codigoTramite) {
    this.codigoTramite = codigoTramite;
  }
	
	public String getPaso3Captcha() {
		return paso3Captcha;
	}

	public void setPaso3Captcha(String paso3Captcha) {
		this.paso3Captcha = paso3Captcha;
	}
	
  @PreDestroy
  public void preDestroy() {
    try {
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      this.agenda = null;
      this.agendarReservasEJB = null;
      this.configuracionEJB = null;
      if(this.cuposXdiaMesSeleccionado!=null) {
        this.cuposXdiaMesSeleccionado.clear();
      }
      this.cuposXdiaMesSeleccionado = null;
      if(this.datosASolicitar!=null) {
        this.datosASolicitar.clear();
      }
      this.datosASolicitar = null;
      this.disponibilidad = null;
      this.disponibilidadCancelarReserva = null;
      this.disponibilidadesDelDiaMatutina = null;
      this.disponibilidadesDelDiaVespertina = null;
      this.empresaActual = null;
      if(this.listaReservas!=null) {
        this.listaReservas.clear();
      }
      this.listaReservas = null;
      if(this.preguntasCaptcha!=null) {
        this.preguntasCaptcha.clear();
      }
      this.preguntasCaptcha = null;
      this.recurso = null;
      this.reserva = null;
      this.reservaConfirmada = null;
      this.reservaDatos = null;
      if(this.textos!=null) {
        this.textos.clear();
      }
      this.textos = null;
      this.ventanaCalendario = null;
      this.ventanaMesSeleccionado = null;
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      LOGGER.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
    }
  }

  public TokenReserva getTokenReserva() {
    return tokenReserva;
  }

  public void setTokenReserva(TokenReserva tokenReserva) {
    this.tokenReserva = tokenReserva;
  }

  public Reserva getReservaModificar1() {
    return reservaModificar1;
  }

  public void setReservaModificar1(Reserva reservaModificar1) {
    this.reservaModificar1 = reservaModificar1;
  }

  public Reserva getReservaModificar2() {
    return reservaModificar2;
  }

  public void setReservaModificar2(Reserva reservaModificar2) {
    this.reservaModificar2 = reservaModificar2;
  }
	
  public String getMensajeReservaExistente() {
    if(this.recurso==null || this.recurso.getId()==null) {
      return "";
    }
    if(this.recurso.getPeriodoValidacion()==null || this.getRecurso().getPeriodoValidacion()==0) {
      return textos.get("solo_se_permite_una_reserva_diaria");
    }
    return textos.get("solo_se_permite_una_reserva_en_un_periodo_de_dias").replace("{dias}", ""+(this.recurso.getPeriodoValidacion()*2));
  }

  public Boolean getEnvioCorreoReserva() {
	return envioCorreoReserva;
  }

  public void setEnvioCorreoReserva(Boolean envioCorreoReserva) {
	this.envioCorreoReserva = envioCorreoReserva;
  }
  
  
  
  
  
}


