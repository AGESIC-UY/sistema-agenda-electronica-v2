package uy.gub.imm.sae.web.rest;

public class ModificarReservaInput {

  private String token;
  private Integer idEmpresa;
  private Integer idAgenda;
  private Integer idRecurso;
  private Integer idReserva;
  private Integer idDisponibilidad;
  private String codigoSeguridad;
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
  public String getIdioma() {
    return idioma;
  }
  public void setIdioma(String idioma) {
    this.idioma = idioma;
  }
  public Integer getIdReserva() {
    return idReserva;
  }
  public void setIdReserva(Integer idReserva) {
    this.idReserva = idReserva;
  }
  public String getCodigoSeguridad() {
    return codigoSeguridad;
  }
  public void setCodigoSeguridad(String codigoSeguridad) {
    this.codigoSeguridad = codigoSeguridad;
  }  
}
