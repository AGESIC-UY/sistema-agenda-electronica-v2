package uy.gub.imm.sae.business.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UsuarioEmpresaRoles implements Serializable {

	private static final long serialVersionUID = 8269486447390948269L;
	
	private Integer idUsuario;
	private Integer idEmpresa;
	
	private boolean administrador;
	private boolean planificador;
	private boolean fCallCenter;
	private boolean fAtencion;
	private boolean llamador;
	
	public UsuarioEmpresaRoles(Integer idUsuario, Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
		this.idUsuario = idUsuario;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public boolean isPlanificador() {
		return planificador;
	}

	public void setPlanificador(boolean planificador) {
		this.planificador = planificador;
	}

	public boolean isfCallCenter() {
		return fCallCenter;
	}

	public void setfCallCenter(boolean fCallCenter) {
		this.fCallCenter = fCallCenter;
	}

	public boolean isfAtencion() {
		return fAtencion;
	}

	public void setfAtencion(boolean fAtencion) {
		this.fAtencion = fAtencion;
	}

	public boolean isLlamador() {
		return llamador;
	}

	public void setLlamador(boolean llamador) {
		this.llamador = llamador;
	}
	
	public void marcarRol(String nombreRol, boolean valor) {
		if("RA_AE_ADMINISTRADOR".equals(nombreRol)) {
			this.administrador = valor;
		}else if("RA_AE_PLANIFICADOR".equals(nombreRol)) {
			this.planificador = valor;
		}else if("RA_AE_FCALL_CENTER".equals(nombreRol)) {
			this.fCallCenter = valor;
		}else if("RA_AE_FATENCION".equals(nombreRol)) {
			this.fAtencion = valor;
		}else if("RA_AE_LLAMADOR".equals(nombreRol)) {
			this.llamador = valor;
		} 
	}
	
	public List<String> getRoles() {
		List<String> roles = new ArrayList<String>();
		if(this.administrador) {
			roles.add("RA_AE_ADMINISTRADOR");
		}
		if(this.planificador) {
			roles.add("RA_AE_PLANIFICADOR");
		}
		if(this.fCallCenter) {
			roles.add("RA_AE_FCALL_CENTER");
		}
		if(this.fAtencion) {
			roles.add("RA_AE_FATENCION");
		}
		if(this.llamador) {
			roles.add("RA_AE_LLAMADOR");
		}
		return roles;
	}
	
	
	
}
