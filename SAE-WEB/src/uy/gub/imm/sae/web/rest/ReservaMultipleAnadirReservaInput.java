package uy.gub.imm.sae.web.rest;

public class ReservaMultipleAnadirReservaInput {

  private String token;
  private String tokenReserva;
  private Integer idEmpresa;
  private Integer idAgenda;
  private Integer idRecurso;
  private Integer idDisponibilidad;
  private String datosReserva;
  private String idioma;

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
  public String getDatosReserva() {
    return datosReserva;
  }
  public void setDatosReserva(String datosReserva) {
    this.datosReserva = datosReserva;
  }
  public String getTokenReserva() {
    return tokenReserva;
  }
  public void setTokenReserva(String tokenReserva) {
    this.tokenReserva = tokenReserva;
  }
  public String getIdioma() {
    return idioma;
  }
  public void setIdioma(String idioma) {
    this.idioma = idioma;
  }
  
}
