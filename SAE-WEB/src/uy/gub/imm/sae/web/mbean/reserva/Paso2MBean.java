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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONArray;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
import uy.gub.imm.sae.common.factories.BusinessLocatorFactory;
import uy.gub.imm.sae.entity.Disponibilidad;
import uy.gub.imm.sae.entity.Recurso;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.entity.TextoAgenda;
import uy.gub.imm.sae.entity.TextoRecurso;
import uy.gub.imm.sae.exception.BusinessException;
import uy.gub.imm.sae.exception.RolException;
import uy.gub.imm.sae.exception.UserException;
import uy.gub.imm.sae.web.common.BaseMBean;
import uy.gub.imm.sae.web.common.Row;
import uy.gub.imm.sae.web.common.RowList;

public class Paso2MBean extends BaseMBean {

	static Logger logger = Logger.getLogger(Paso2MBean.class);

	private AgendarReservas agendarReservasEJB;

	private SesionMBean sesionMBean;

	private Row<Disponibilidad> rowSelectMatutina;
	private Row<Disponibilidad> rowSelectVespertina;
	private RowList<Disponibilidad> disponibilidadesMatutina;
	private RowList<Disponibilidad> disponibilidadesVespertina;
	private JSONArray jsonArrayFchDisp;
	private String diaSeleccionadoStr;
	private String fechaFormatSelect;
	private List<SelectItem> selectItemsDispMatutina;
	private List<SelectItem> selectItemsDispVespertina;
	private String selectIdMatutina;
	private String selectIdVespertina;

	private String filtroHorarios = "MV";
	
	private boolean errorInit;
	
	@PostConstruct
	public void init() {
		errorInit = false;
		try {
			agendarReservasEJB = BusinessLocatorFactory.getLocatorContextoNoAutenticado().getAgendarReservas();
			if (sesionMBean.getAgenda() == null || sesionMBean.getRecurso() == null) {
				addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
				errorInit = true;
				return;
			}
			if (sesionMBean.getReserva() != null) {
				agendarReservasEJB.desmarcarReserva(sesionMBean.getReserva());
				sesionMBean.setReserva(null);
			}
			configurarCalendario();
			configurarDisponibilidadesDelDia();
		} catch (Exception aEx) {
			addErrorMessage(sesionMBean.getTextos().get("la_combinacion_de_parametros_especificada_no_es_valida"));
			errorInit = true;
		}
	}

	public String getAgendaNombre() {
		if (sesionMBean.getAgenda() != null) {
			return sesionMBean.getAgenda().getNombre();
		} else {
			return null;
		}
	}

	public String getRecursoDescripcion() {
		Recurso recurso = sesionMBean.getRecurso();
		if (recurso != null) {
			String descripcion = recurso.getNombre();
			if (descripcion != null
					&& !descripcion.equals(recurso.getDireccion())) {
				descripcion = descripcion + " - " + recurso.getDireccion();
			}
			return descripcion;
		} else {
			return null;
		}
	}

	public SesionMBean getSesionMBean() {
		return sesionMBean;
	}

	public void setSesionMBean(SesionMBean sesionMBean) {
		this.sesionMBean = sesionMBean;
	}

	public String getDescripcion() {
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			String str = textoAgenda.getTextoPaso2();
			if (str != null)
				return str;
			else
				return "";
		} else {
			return null;
		}
	}

	public String getDescripcionRecurso() {
		TextoRecurso textoRecurso = getTextoRecurso(sesionMBean.getRecurso(), sesionMBean.getIdiomaActual());
		if (textoRecurso != null) {
			return textoRecurso.getTextoPaso2();
		} else {
			return null;
		}
	}

	public String getEtiquetaDelRecurso() {
		TextoAgenda textoAgenda = getTextoAgenda(sesionMBean.getAgenda(), sesionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		} else {
			return null;
		}
	}

	public Date getDiaSeleccionado() {
		return sesionMBean.getDiaSeleccionado();
	}

	public Boolean getHayDisponibilidadesMatutina() {
		if(sesionMBean.getDisponibilidadesDelDiaMatutina()==null) {
			return false;
		}
		return !sesionMBean.getDisponibilidadesDelDiaMatutina().isEmpty();
	}

	public RowList<Disponibilidad> getDisponibilidadesMatutina() {
		this.disponibilidadesMatutina = sesionMBean.getDisponibilidadesDelDiaMatutina();
		if(this.disponibilidadesMatutina==null) {
			this.disponibilidadesMatutina = new RowList<Disponibilidad>();
		}
		return this.disponibilidadesMatutina;
	}

	public Boolean getHayDisponibilidadesVespertina() {
		if(sesionMBean.getDisponibilidadesDelDiaVespertina()==null) {
			return false;
		}
		return !sesionMBean.getDisponibilidadesDelDiaVespertina().isEmpty();
	}

	public RowList<Disponibilidad> getDisponibilidadesVespertina() {
		this.disponibilidadesVespertina = sesionMBean.getDisponibilidadesDelDiaVespertina();
		if(this.disponibilidadesVespertina == null) {
			this.disponibilidadesVespertina = new RowList<Disponibilidad>();
		}
		return this.disponibilidadesVespertina;
	}

	private void marcarReserva(Disponibilidad disponibilidad) throws RolException, BusinessException, UserException {
		Reserva reserva = agendarReservasEJB.marcarReserva(disponibilidad, null);
		sesionMBean.setReserva(reserva);
		sesionMBean.setDisponibilidad(disponibilidad);
	}

	private void configurarDisponibilidadesDelDia() {
		List<Disponibilidad> dispMatutinas = new ArrayList<Disponibilidad>();
		List<Disponibilidad> dispVespertinas = new ArrayList<Disponibilidad>();
		if (sesionMBean.getDiaSeleccionado() != null) {
			VentanaDeTiempo ventana = new VentanaDeTiempo();
			ventana.setFechaInicial(Utiles.time2InicioDelDia(sesionMBean.getDiaSeleccionado()));
			ventana.setFechaFinal(Utiles.time2FinDelDia(sesionMBean.getDiaSeleccionado()));
			try {
				List<Disponibilidad> lista = agendarReservasEJB.obtenerDisponibilidades(sesionMBean.getRecurso(),	ventana, sesionMBean.getTimeZone());
				for (Disponibilidad d : lista) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(d.getHoraInicio());
					if(d.getCupo()<0) {
					  d.setCupo(0);
					}
					if (cal.get(Calendar.AM_PM) == Calendar.AM) {
						dispMatutinas.add(d);
					} else {
						dispVespertinas.add(d);
					}
				}
			} catch (Exception e) {
				addErrorMessage(e);
			}
		}

		sesionMBean.setDisponibilidadesDelDiaMatutina(new RowList<Disponibilidad>(dispMatutinas));
		sesionMBean.setDisponibilidadesDelDiaVespertina(new RowList<Disponibilidad>(dispVespertinas));
	}

	private void configurarCalendario() throws RolException, UserException {

		Recurso recurso = sesionMBean.getRecurso();

		VentanaDeTiempo ventanaCalendario = agendarReservasEJB.obtenerVentanaCalendarioInternet(recurso);
		
		sesionMBean.setVentanaCalendario(ventanaCalendario);

		VentanaDeTiempo ventanaMesSeleccionado = new VentanaDeTiempo();
		Calendar cal = Calendar.getInstance();
		cal.setTime(ventanaCalendario.getFechaInicial());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		ventanaMesSeleccionado.setFechaInicial(Utiles.time2InicioDelDia(cal.getTime()));
		
    cal.setTime(ventanaCalendario.getFechaFinal());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		ventanaMesSeleccionado.setFechaFinal(Utiles.time2FinDelDia(cal.getTime()));
		sesionMBean.setVentanaMesSeleccionado(ventanaMesSeleccionado);
		
		cargarCuposADesplegar(recurso, ventanaMesSeleccionado);

		sesionMBean.setCurrentDate(ventanaCalendario.getFechaInicial());
		sesionMBean.setDiaSeleccionado(null);
	}

	private void cargarCuposADesplegar(Recurso recurso, VentanaDeTiempo ventana) {

		List<Integer> listaCupos = null;
		try {
			listaCupos = agendarReservasEJB.obtenerCuposPorDia(recurso, ventana, sesionMBean.getTimeZone());
			// Se carga la fecha inicial
			Calendar cont = Calendar.getInstance();
			cont.setTime(Utiles.time2InicioDelDia(sesionMBean.getVentanaMesSeleccionado().getFechaInicial()));

			Integer i = 0;

			Date inicioDisp = sesionMBean.getVentanaCalendario().getFechaInicial();
			Date finDisp = sesionMBean.getVentanaCalendario().getFechaFinal();

			jsonArrayFchDisp = new JSONArray();
			// Recorro la ventana dia a dia y voy generando la lista completa de
			// cupos x dia con -1, 0, >0 según corresponda.
			while (!cont.getTime().after(sesionMBean.getVentanaMesSeleccionado().getFechaFinal())) {
				if (cont.getTime().before(inicioDisp) || cont.getTime().after(finDisp)) {
					listaCupos.set(i, -1);
				} else {
					if (listaCupos.get(i) > 0) {
						String dateStr = String.valueOf(cont.get(Calendar.DAY_OF_MONTH))+"/"+ String.valueOf(cont.get(Calendar.MONTH) + 1)+ "/" + String.valueOf(cont.get(Calendar.YEAR));
						jsonArrayFchDisp.put(dateStr);
					}
				}
				cont.add(Calendar.DAY_OF_MONTH, 1);
				i++;
			}

			sesionMBean.setCuposXdiaMesSeleccionado(listaCupos);
		} catch (Exception e) {
			addErrorMessage(e);
		}

	}

	public void seleccionarFecha() {
		DateFormat format = new SimpleDateFormat(sesionMBean.getFormatoFecha());
		Date date = null;
		try {
			date = format.parse(this.diaSeleccionadoStr);
			//Verificar que la fecha esté en el rango permitido (que no la modifiquen en el medio)
			Date inicio_disp = sesionMBean.getVentanaCalendario().getFechaInicial();
			Date fin_disp = sesionMBean.getVentanaCalendario().getFechaFinal();
			if(date.before(inicio_disp) || date.after(fin_disp)) {
				addErrorMessage(sesionMBean.getTextos().get("fecha_no_valida"));
				sesionMBean.setDiaSeleccionado(null);
				configurarDisponibilidadesDelDia();
				return;
			}
			
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			fechaFormatSelect = cal.get(Calendar.DAY_OF_MONTH)+" de "+deduccionMes(cal.get(Calendar.MONTH))+" "+cal.get(Calendar.YEAR);
			sesionMBean.setDiaSeleccionado(date);
			configurarDisponibilidadesDelDia();
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (RolException e) {
			e.printStackTrace();

		}

	}

	public void seleccionarHorarioMatutino(SelectEvent event) {
		if (this.rowSelectMatutina != null) {
			setRowSelectVespertina(null);
		}
	}

	public void seleccionarHorarioVespertino(SelectEvent event) {
		if (this.rowSelectVespertina != null) {
			setRowSelectMatutina(null);
		}
	}

	public String siguientePaso() {

		if (!(rowSelectMatutina == null) || !(rowSelectVespertina == null)) {
			Row<Disponibilidad> row = null;
			if (!(rowSelectMatutina == null)) {
				row = rowSelectMatutina;
				if (row.getData().getCupo() > 0) {
					sesionMBean.getDisponibilidadesDelDiaMatutina().setSelectedRow(row);
					sesionMBean.getDisponibilidadesDelDiaVespertina().setSelectedRow(null);
				} else {
					addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_un_horario"));
					return null;
				}
			} else {
				row = rowSelectVespertina;
				if (row.getData().getCupo() > 0) {
					sesionMBean.getDisponibilidadesDelDiaMatutina().setSelectedRow(null);
					sesionMBean.getDisponibilidadesDelDiaVespertina().setSelectedRow(row);
				} else {
					addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_un_horario"));
					return null;
				}
			}
			try {
				marcarReserva(row.getData());
				return "siguientePaso";
			} catch (Exception ex) {
			  addErrorMessage(ex);
			  configurarDisponibilidadesDelDia();
				return null;
			}
		} else {
			addErrorMessage(sesionMBean.getTextos().get("debe_seleccionar_un_horario"));
			return null;
		}
	}

	public void beforePhase(PhaseEvent event) {
		disableBrowserCache(event);

		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sesionMBean.limpiarPaso3();
		}
	}

	public JSONArray getJsonArrayFchDisp() {
		return jsonArrayFchDisp;
	}

	public void setJsonArrayFchDisp(JSONArray jsonArrayFchDisp) {
		this.jsonArrayFchDisp = jsonArrayFchDisp;
	}

	public String getDiaSeleccionadoStr() {
		return diaSeleccionadoStr;
	}

	public void setDiaSeleccionadoStr(String diaSeleccionadoStr) {
		this.diaSeleccionadoStr = diaSeleccionadoStr;
	}

	public String getSelectIdMatutina() {
		return selectIdMatutina;
	}

	public void setSelectIdMatutina(String selectIdMatutina) {
		this.selectIdMatutina = selectIdMatutina;
	}

	public List<SelectItem> getSelectItemsDispVespertina() {
		return selectItemsDispVespertina;
	}

	public void setSelectItemsDispVespertina(
			List<SelectItem> selectItemsDispVespertina) {
		this.selectItemsDispVespertina = selectItemsDispVespertina;
	}

	public List<SelectItem> getSelectItemsDispMatutina() {

		return selectItemsDispMatutina;

	}

	public void setSelectItemsDispMatutina(
			List<SelectItem> selectItemsDispVespertina) {
		this.selectItemsDispVespertina = selectItemsDispVespertina;
	}

	public String getSelectIdVespertina() {
		return selectIdVespertina;
	}

	public void setSelectIdVespertina(String selectIdVespertina) {
		this.selectIdVespertina = selectIdVespertina;
	}

	public Row<Disponibilidad> getRowSelectMatutina() {
		return rowSelectMatutina;
	}

	public void setRowSelectMatutina(Row<Disponibilidad> rowSelectMatutina) {

		this.rowSelectMatutina = rowSelectMatutina;
	}

	public Row<Disponibilidad> getRowSelectVespertina() {
		return rowSelectVespertina;
	}

	public void setRowSelectVespertina(Row<Disponibilidad> rowSelectVespertina) {
		this.rowSelectVespertina = rowSelectVespertina;
	}

	public String Paso1() {
		return sesionMBean.getUrlPaso1Reserva() + "&faces-redirect=true";
	}

	public String getFechaFormatSelect() {
		return fechaFormatSelect;
	}

	public void setFechaFormatSelect(String fechaFormatSelect) {
		this.fechaFormatSelect = fechaFormatSelect;
	}

	private String deduccionMes(int i) {
		
		String nombreMes = null;
		switch (i) {
		case Calendar.JANUARY:
			nombreMes = "Enero";
			break;
		case Calendar.FEBRUARY:
			nombreMes = "Febrero";
			break;
		case Calendar.MARCH:
			nombreMes = "Marzo";
			break;
		case Calendar.APRIL:
			nombreMes = "Abril";
			break;
		case Calendar.MAY:
			nombreMes = "Mayo";
			break;
		case Calendar.JUNE:
			nombreMes = "Junio";
			break;
		case Calendar.JULY:
			nombreMes = "Julio";
			break;
		case Calendar.AUGUST:
			nombreMes = "Agosto";
			break;
		case Calendar.SEPTEMBER:
			nombreMes = "Setiembre";
			break;
		case Calendar.OCTOBER:
			nombreMes = "Octubre";
			break;
		case Calendar.NOVEMBER:
			nombreMes = "Noviembre";
			break;
		default:
			nombreMes = "Diciembre";
			break;
		}
		return nombreMes;
	}

	public String getFiltroHorarios() {
		
		return filtroHorarios;
	}

	public void setFiltroHorarios(String filtroHorarios) {
		this.filtroHorarios = filtroHorarios;
	}

	public boolean isErrorInit() {
		return errorInit;
	}
	
	public String claseSegunCupo(Disponibilidad disponibilidad) {
	  if(disponibilidad.getCupo()==null || disponibilidad.getCupo()<1) {
	    return "cupoNoSeleccionable";
	  }
	  return "";
	}
	
  @PreDestroy
  public void preDestroy() {
    
    try {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", liberando objetos...");
      
      this.agendarReservasEJB = null;
      if(this.disponibilidadesMatutina!=null) {
        this.disponibilidadesMatutina.clear();
      }
      this.disponibilidadesMatutina = null;
      if(this.disponibilidadesVespertina!=null) {
        this.disponibilidadesVespertina.clear();
      }
      this.disponibilidadesVespertina = null;
      this.jsonArrayFchDisp = null;
      this.rowSelectMatutina = null;
      this.rowSelectVespertina = null;
      if(this.selectItemsDispMatutina!=null) {
        this.selectItemsDispMatutina.clear();
      }
      this.selectItemsDispMatutina = null;
      if(this.selectItemsDispVespertina!=null) {
        this.selectItemsDispVespertina.clear();
      }
      this.selectItemsDispVespertina = null;
      this.sesionMBean = null;
      
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", objetos liberados.");
    }catch(Exception ex) {
      logger.debug("Destruyendo una instancia de "+this.getClass().getName()+", error.", ex);
      
    }
  }
	
}
