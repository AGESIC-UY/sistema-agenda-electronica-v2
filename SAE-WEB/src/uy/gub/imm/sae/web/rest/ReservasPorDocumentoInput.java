package uy.gub.imm.sae.web.rest;

public class ReservasPorDocumentoInput {

	private String token;
	private Integer idAgenda;
	private Integer idRecurso;
	private String tipoDocumento;
	private String numeroDocumento;
  private String codigoTramite;
  //Las fechas deben ser en formato "yyyyMMdd"
  private String fechaDesde;
  private String fechaHasta;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
  public String getCodigoTramite() {
    return codigoTramite;
  }
  public void setCodigoTramite(String codigoTramite) {
    this.codigoTramite = codigoTramite;
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
	
}
