package uy.gub.imm.sae.web.mbean.administracion;

import java.util.List;

import uy.gub.imm.sae.common.enumerados.Estado;
import uy.gub.imm.sae.entity.AgrupacionDato;
import uy.gub.imm.sae.entity.Reserva;
import uy.gub.imm.sae.web.common.RemovableFromSession;
import uy.gub.imm.sae.web.common.SessionCleanerMBean;
import uy.gub.imm.sae.web.mbean.administracion.ListaDeEsperaMBean.Horario;

public class ListaDeEsperaSessionMBean extends SessionCleanerMBean implements RemovableFromSession {

	private List<Estado> estadosSeleccionado;
	private Boolean refrescarListaDeEspera = true;
	
	private List<AgrupacionDato> agrupaciones;
	
	private List<Horario> horarios;
	
	private Boolean mostrarDatosSiguiente;
	
	private Reserva siguienteReserva;
	
	public List<Estado> getEstadosSeleccionado() {
		return estadosSeleccionado;
	}
	public void setEstadosSeleccionado(List<Estado> estadosSeleccionado) {
		this.estadosSeleccionado = estadosSeleccionado;
	}
	public Boolean getRefrescarListaDeEspera() {
		return refrescarListaDeEspera;
	}
	public void setRefrescarListaDeEspera(Boolean refrescarListaDeEspera) {
		this.refrescarListaDeEspera = refrescarListaDeEspera;
	}
	public List<AgrupacionDato> getAgrupaciones() {
		return agrupaciones;
	}
	public void setAgrupaciones(List<AgrupacionDato> agrupaciones) {
		this.agrupaciones = agrupaciones;
	}
	public List<Horario> getHorarios() {
		return horarios;
	}
	public void setHorarios(List<Horario> horarios) {
		this.horarios = horarios;
	}
	public Boolean getMostrarDatosSiguiente() {
		return mostrarDatosSiguiente;
	}
	public void setMostrarDatosSiguiente(Boolean mostrarDatosSiguiente) {
		this.mostrarDatosSiguiente = mostrarDatosSiguiente;
	}

	public Reserva getSiguienteReserva() {
		return siguienteReserva;
	}
	public void setSiguienteReserva(Reserva siguienteReserva) {
		this.siguienteReserva = siguienteReserva;
	}
	
}
