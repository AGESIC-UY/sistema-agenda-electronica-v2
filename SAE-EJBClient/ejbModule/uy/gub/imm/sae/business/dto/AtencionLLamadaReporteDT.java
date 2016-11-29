package uy.gub.imm.sae.business.dto;

import java.io.Serializable;
import java.util.Date;

public class AtencionLLamadaReporteDT implements Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 7578316670466636181L;
  private String nombFuncionario;
  private String nombAgenda;
  private String nombRecurso;
  private String nombEmpresa;
  private Integer puesto;
  private Date fechaHoraLlamada;
  private Date fechaHoraAtencion;
  private Date fechaHoraReserva;
  private String atencion;
  private Integer reservaId;
  private String tramiteCodigo;
  private String tramiteNombre;

  public AtencionLLamadaReporteDT(String nombFun, String nomAgenda,
      String nomRecurso, Integer nroPuesto, Date fechaHoraLlam,
      Date fechaHoraAten, String resolucionAtencion, Integer reservId,
      Date fechaHoraReserv, String tramiteCodigo, String tramiteNombre) {
    this.nombFuncionario = nombFun;
    this.nombAgenda = nomAgenda;
    this.nombRecurso = nomRecurso;
    this.fechaHoraAtencion = fechaHoraAten;
    this.fechaHoraLlamada = fechaHoraLlam;
    this.puesto = nroPuesto;
    this.atencion = resolucionAtencion;
    this.reservaId = reservId;
    this.fechaHoraReserva = fechaHoraReserv;
    this.tramiteCodigo = tramiteCodigo;
    this.tramiteNombre = tramiteNombre;
  }

  public String getNombFuncionario() {
    return nombFuncionario;
  }

  public void setNombFuncionario(String nombFuncionario) {
    this.nombFuncionario = nombFuncionario;
  }

  public String getNombAgenda() {
    return nombAgenda;
  }

  public void setNombAgenda(String nombAgenda) {
    this.nombAgenda = nombAgenda;
  }

  public String getNombEmpresa() {
    return nombEmpresa;
  }

  public void setNombEmpresa(String nombEmpresa) {
    this.nombEmpresa = nombEmpresa;
  }

  public Date getFechaHoraLlamada() {
    return fechaHoraLlamada;
  }

  public void setFechaHoraLlamada(Date fechaHoraLlamada) {
    this.fechaHoraLlamada = fechaHoraLlamada;
  }

  public Date getFechaHoraAtencion() {
    return fechaHoraAtencion;
  }

  public void setFechaHoraAtencion(Date fechaHoraAtencion) {
    this.fechaHoraAtencion = fechaHoraAtencion;
  }

  public String getNombRecurso() {
    return nombRecurso;
  }

  public void setNombRecurso(String nombRecurso) {
    this.nombRecurso = nombRecurso;
  }

  public Integer getPuesto() {
    return puesto;
  }

  public void setPuesto(Integer puesto) {
    this.puesto = puesto;
  }

  public String getAtencion() {
    return atencion;
  }

  public void setAtencion(String atencion) {
    this.atencion = atencion;
  }

  public Integer getReservaId() {
    return reservaId;
  }

  public void setReservaId(Integer reservaId) {
    this.reservaId = reservaId;
  }

  public Date getFechaHoraReserva() {
    return fechaHoraReserva;
  }

  public void setFechaHoraReserva(Date fechaHoraReserva) {
    this.fechaHoraReserva = fechaHoraReserva;
  }

  public String getTramiteCodigo() {
    return tramiteCodigo;
  }

  public void setTramiteCodigo(String tramiteCodigo) {
    this.tramiteCodigo = tramiteCodigo;
  }

  public String getTramiteNombre() {
    return tramiteNombre;
  }

  public void setTramiteNombre(String tramiteNombre) {
    this.tramiteNombre = tramiteNombre;
  }

}
