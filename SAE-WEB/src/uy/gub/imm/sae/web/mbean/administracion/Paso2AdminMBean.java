package uy.gub.imm.sae.web.mbean.administracion;

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
import javax.ejb.EJB;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONArray;

import uy.gub.imm.sae.business.ejb.facade.AgendarReservas;
import uy.gub.imm.sae.common.Utiles;
import uy.gub.imm.sae.common.VentanaDeTiempo;
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

public class Paso2AdminMBean extends BaseMBean {

	static Logger logger = Logger.getLogger(Paso2AdminMBean.class);
	public static final String MSG_ID = "pantalla";

	@EJB(mappedName="java:global/sae-1-service/sae-ejb/AgendarReservasBean!uy.gub.imm.sae.business.ejb.facade.AgendarReservasRemote")
	private AgendarReservas agendarReservasEJB;

	private SessionMBean sessionMBean;

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
	
	@PostConstruct
	public void init() {
		try {
			if (sessionMBean.getAgenda() == null || sessionMBean.getRecurso() == null) {
        redirect("inicio");
				return;
			}
			if (sessionMBean.getReserva() != null) {
				agendarReservasEJB.desmarcarReserva(sessionMBean.getReserva());
				sessionMBean.setReserva(null);
			}
			configurarCalendario();
			configurarDisponibilidadesDelDia();
			
		}catch (Exception e) {
			addErrorMessage(e);
		}

	}

	public String getAgendaNombre() {
		if (sessionMBean.getAgenda() != null) {
			return sessionMBean.getAgenda().getNombre();
		} else {
			return null;
		}
	}

	public String getRecursoDescripcion() {
		Recurso recurso = sessionMBean.getRecurso();
		if (recurso != null) {
			String descripcion = recurso.getNombre();
			if(descripcion != null && !descripcion.equals(recurso.getDireccion())) {
				descripcion = descripcion + " - " + recurso.getDireccion();
			}
			return  descripcion;
		} else {
			return null;
		}
	}

	public SessionMBean getSessionMBean() {
		return sessionMBean;
	}

	public void setSessionMBean(SessionMBean sessionMBean) {
		this.sessionMBean = sessionMBean;
	}

	public String getDescripcion() {
		TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgenda(), sessionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			String str = textoAgenda.getTextoPaso2();
			if (str != null)
				return str;
			else
				return "";
		} else {
			return "";
		}
	}

	public String getDescripcionRecurso() {
		TextoRecurso textoRecurso = getTextoRecurso(sessionMBean.getRecursoMarcado(), sessionMBean.getIdiomaActual());
		if (textoRecurso != null) {
			return textoRecurso.getTextoPaso2();
		} else {
			return null;
		}
	}

	public String getEtiquetaDelRecurso() {
		TextoAgenda textoAgenda = getTextoAgenda(sessionMBean.getAgendaMarcada(), sessionMBean.getIdiomaActual());
		if (textoAgenda != null) {
			return textoAgenda.getTextoSelecRecurso();
		} else {
			return null;
		}
	}

	public Date getDiaSeleccionado() {
		return sessionMBean.getDiaSeleccionado();
	}

	public Boolean getHayDisponibilidadesMatutina() {
		return sessionMBean.getDisponibilidadesDelDiaMatutina().size() != 0;
	}

	public RowList<Disponibilidad> getDisponibilidadesMatutina() {
		this.disponibilidadesMatutina = sessionMBean
				.getDisponibilidadesDelDiaMatutina();
		return this.disponibilidadesMatutina;
	}

	public Boolean getHayDisponibilidadesVespertina() {
		return sessionMBean.getDisponibilidadesDelDiaVespertina().size() != 0;
	}

	public RowList<Disponibilidad> getDisponibilidadesVespertina() {
		this.disponibilidadesVespertina = sessionMBean
				.getDisponibilidadesDelDiaVespertina();
		return this.disponibilidadesVespertina;
	}
	
	private void marcarReserva(Disponibilidad disponibilidad) throws RolException, BusinessException, UserException {
		Reserva r = agendarReservasEJB.marcarReserva(disponibilidad, null);
		sessionMBean.setReserva(r);
		sessionMBean.setDisponibilidad(disponibilidad);
	}

	private void configurarDisponibilidadesDelDia() {
		List<Disponibilidad> dispMatutinas = new ArrayList<Disponibilidad>();
		List<Disponibilidad> dispVespertinas = new ArrayList<Disponibilidad>();
		
		if (sessionMBean.getDiaSeleccionado() != null) {
			
			VentanaDeTiempo ventana = new VentanaDeTiempo();
			ventana.setFechaInicial(Utiles.time2InicioDelDia(sessionMBean.getDiaSeleccionado()));
			ventana.setFechaFinal(Utiles.time2FinDelDia(sessionMBean.getDiaSeleccionado()));
			
			try {
				List<Disponibilidad> lista = agendarReservasEJB.obtenerDisponibilidades(sessionMBean.getRecurso(), ventana, sessionMBean.getTimeZone());
				
				for (Disponibilidad d : lista) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(d.getHoraInicio());
          if(d.getCupo()<0) {
            d.setCupo(0);
          }

					if (cal.get(Calendar.AM_PM) == Calendar.AM) {
						// Matutino
						dispMatutinas.add(d);
					} else {
						// Vespertino
						dispVespertinas.add(d);
					}
				}
				
			} catch (Exception e) {
				addErrorMessage(e);
			}
		}
		
		sessionMBean.setDisponibilidadesDelDiaMatutina(new RowList<Disponibilidad>(dispMatutinas));
		sessionMBean.setDisponibilidadesDelDiaVespertina(new RowList<Disponibilidad>(dispVespertinas));
	}

	
	private void configurarCalendario() throws RolException, UserException {

		Recurso recurso = sessionMBean.getRecurso();

		VentanaDeTiempo ventanaCalendario;
		ventanaCalendario = agendarReservasEJB.obtenerVentanaCalendarioIntranet(recurso);

		sessionMBean.setVentanaCalendario(ventanaCalendario);
		
		VentanaDeTiempo ventanaMesSeleccionado = new VentanaDeTiempo();
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(ventanaCalendario.getFechaInicial());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		ventanaMesSeleccionado.setFechaInicial(Utiles.time2InicioDelDia(cal.getTime()));
		
    cal.setTime(ventanaCalendario.getFechaFinal());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		ventanaMesSeleccionado.setFechaFinal(Utiles.time2FinDelDia(cal.getTime()));
		sessionMBean.setVentanaMesSeleccionado(ventanaMesSeleccionado);
	
		cargarCuposADesplegar(recurso, ventanaMesSeleccionado);

		sessionMBean.setCurrentDate(ventanaCalendario.getFechaInicial());
		sessionMBean.setDiaSeleccionado(null);
	}
	
	private void cargarCuposADesplegar(Recurso recurso, VentanaDeTiempo ventana) {
		try {
		  List<Integer> listaCupos = agendarReservasEJB.obtenerCuposPorDia(recurso, ventana, sessionMBean.getTimeZone());
			// Se carga la fecha inicial
			Calendar cont = Calendar.getInstance();
			cont.setTime(Utiles.time2InicioDelDia(sessionMBean.getVentanaMesSeleccionado().getFechaInicial()));

			Integer i = 0;

			Date inicioDisp = sessionMBean.getVentanaCalendario().getFechaInicial();
			Date finDisp = sessionMBean.getVentanaCalendario().getFechaFinal();

			jsonArrayFchDisp = new JSONArray();
			// Recorro la ventana dia a dia y voy generando la lista completa de
			// cupos x dia con -1, 0, >0 según corresponda.
			while (!cont.getTime().after(sessionMBean.getVentanaMesSeleccionado().getFechaFinal())) {
				if (cont.getTime().before(inicioDisp) || cont.getTime().after(finDisp)) {
					listaCupos.set(i, -1);
				}else {
					if (listaCupos.get(i) > 0) {
						String dateStr = String.valueOf(cont.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(cont.get(Calendar.MONTH) + 1) + "/" + String.valueOf(cont.get(Calendar.YEAR));
						jsonArrayFchDisp.put(dateStr);
					}
				}
				cont.add(Calendar.DAY_OF_MONTH, 1);
				i++;
			}
			sessionMBean.setCuposXdiaMesSeleccionado(listaCupos);
		} catch (Exception e) {
			addErrorMessage(e);
		}
	}
	
	public void seleccionarFecha() {
		DateFormat format = new SimpleDateFormat(sessionMBean.getFormatoFecha());
		Date date = null;	
		try {
			date = format.parse(this.diaSeleccionadoStr);
			sessionMBean.setDiaSeleccionado(date);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			fechaFormatSelect = cal.get(Calendar.DAY_OF_MONTH)+" de "+deduccionMes(cal.get(Calendar.MONTH))+" "+cal.get(Calendar.YEAR);
			configurarDisponibilidadesDelDia();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}catch (RolException e) {
			e.printStackTrace();
		}
	}
	
	public void seleccionarHorarioMatutino(SelectEvent event) {
		//al seleccionar un horario matutino se debe deseleccionar horario vespertino
		if(this.rowSelectMatutina!=null) {
			setRowSelectVespertina(null);
		}
	}
	
	public void seleccionarHorarioVespertino(SelectEvent event) {
		//al seleccionar un horario vespertino se debe deseleccionar horario matutino
		if(this.rowSelectVespertina!=null) {
			setRowSelectMatutina(null);
		}
	}
	
	public String siguientePaso() {
		if(!(rowSelectMatutina==null) || !(rowSelectVespertina==null) )	{
			Row<Disponibilidad> row = null;
			if(!(rowSelectMatutina==null)) {
				row = rowSelectMatutina;
				if (row.getData().getCupo()>0) {
					sessionMBean.getDisponibilidadesDelDiaMatutina().setSelectedRow(row);
					sessionMBean.getDisponibilidadesDelDiaVespertina().setSelectedRow(null);
				}else {
					addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_un_horario_con_disponibilidades"), MSG_ID);
					return null;
				}
			} else {
				row = rowSelectVespertina;
				if (row.getData().getCupo()>0) {
					sessionMBean.getDisponibilidadesDelDiaMatutina().setSelectedRow(null);
					sessionMBean.getDisponibilidadesDelDiaVespertina().setSelectedRow(row);
				}else {
					addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_un_horario_con_disponibilidades"), MSG_ID);
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
			addErrorMessage(sessionMBean.getTextos().get("debe_seleccionar_un_horario_con_disponibilidades"), MSG_ID);
			return null;
		}
	}
	
	public void beforePhase(PhaseEvent event) {
		disableBrowserCache(event);
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			sessionMBean.setPantallaTitulo(sessionMBean.getTextos().get("realizar_reserva"));
			sessionMBean.limpiarPaso3();
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

	public void setSelectItemsDispVespertina(List<SelectItem> selectItemsDispVespertina) {
		this.selectItemsDispVespertina = selectItemsDispVespertina;
	}

	public List<SelectItem> getSelectItemsDispMatutina() {
		return selectItemsDispMatutina;
	}
	
	public void setSelectItemsDispMatutina(List<SelectItem> selectItemsDispVespertina) {
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

  public String claseSegunCupo(Disponibilidad disponibilidad) {
    if(disponibilidad.getCupo()==null || disponibilidad.getCupo()<1) {
      return "cupoNoSeleccionable";
    }
    return "";
  }
	
}
