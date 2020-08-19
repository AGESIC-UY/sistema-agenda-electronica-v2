package uy.gub.imm.sae.web.rest;

public class ConfirmarReservaInput {

  private String token;
  private Integer idEmpresa;
  private Integer idAgenda;
  private Integer idRecurso;
  private Integer idDisponibilidad;
  private String idTransaccionPadre;
  private String pasoTransaccionPadre;
  private String idioma;
  private String datosReserva;
  private Boolean notificar; 
  
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
  public Integer getIdDisponibilidad() {
    return idDisponibilidad;
  }
  public void setIdDisponibilidad(Integer idDisponibilidad) {
    this.idDisponibilidad = idDisponibilidad;
  }
  public String getIdTransaccionPadre() {
    return idTransaccionPadre;
  }
  public void setIdTransaccionPadre(String idTransaccionPadre) {
    this.idTransaccionPadre = idTransaccionPadre;
  }
  public String getPasoTransaccionPadre() {
    return pasoTransaccionPadre;
  }
  public void setPasoTransaccionPadre(String pasoTransaccionPadre) {
    this.pasoTransaccionPadre = pasoTransaccionPadre;
  }
  public String getIdioma() {
    return idioma;
  }
  public void setIdioma(String idioma) {
    this.idioma = idioma;
  }
  public String getDatosReserva() {
    return datosReserva;
  }
  public void setDatosReserva(String datosReserva) {
    this.datosReserva = datosReserva;
  }
  
	public Boolean getNotificar() {
		return notificar;
	}
	public void setNotificar(Boolean notificar) {
		this.notificar = notificar;
	}
    
  
}
