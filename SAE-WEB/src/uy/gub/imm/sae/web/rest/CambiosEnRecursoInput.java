package uy.gub.imm.sae.web.rest;

public class CambiosEnRecursoInput {

	private String token;
	private Integer idEmpresa;
	private Integer idAgenda;
	private Integer idRecurso;
	//Las fechas deben ser en formato "yyyyMMdd"
	private String fechaDesde;
	private String fechaHasta;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}	
	public Integer getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public Integer getIdAgenda() {
		return idAgenda;
	}
	public void setIdAgenda(Integer idAgenda) {
		this.idAgenda = idAgenda;
	}
	public Integer getIdRecurso() {
		return idRecurso;
	}
	public void setIdRecurso(Integer idRecurso) {
		this.idRecurso = idRecurso;
	}
	public String getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(String fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public String getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(String fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	@Override
	public String toString() {
		return "CambiosEnRecursoInput [token=" + token + ", idEmpresa="
				+ idEmpresa + ", idAgenda=" + idAgenda + ", idRecurso="
				+ idRecurso + ", fechaDesde=" + fechaDesde + ", fechaHasta="
				+ fechaHasta + "]";
	}
	
	
	
}