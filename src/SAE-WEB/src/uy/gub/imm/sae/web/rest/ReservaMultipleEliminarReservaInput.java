package uy.gub.imm.sae.web.rest;

public class ReservaMultipleEliminarReservaInput {

  private String token;
  private String tokenReserva;
  private Integer idEmpresa;
  private Integer idAgenda;
  private Integer idRecurso;
  private Integer idReserva;
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
  public String getTokenReserva() {
    return tokenReserva;
  }
  public void setTokenReserva(String tokenReserva) {
    this.tokenReserva = tokenReserva;
  }
  public Integer getIdReserva() {
    return idReserva;
  }
  public void setIdReserva(Integer idReserva) {
    this.idReserva = idReserva;
  }
  public String getIdioma() {
    return idioma;
  }
  public void setIdioma(String idioma) {
    this.idioma = idioma;
  }
  
}
