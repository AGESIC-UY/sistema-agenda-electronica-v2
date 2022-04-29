package uy.gub.imm.sae.web.rest;

import java.util.ArrayList;
import java.util.List;

public class ReservasPorEmpresaInput {

	private String token;
	private String idEmpresa;
	private String codigoTramite;
	private List<Persona> personas = new ArrayList<Persona>();

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(String idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getCodigoTramite() {
		return codigoTramite;
	}

	public void setCodigoTramite(String codigoTramite) {
		this.codigoTramite = codigoTramite;
	}

	public List<Persona> getPersonas() {
		return personas;
	}

	public void setPersonas(List<Persona> personas) {
		this.personas = personas;
	}

}
