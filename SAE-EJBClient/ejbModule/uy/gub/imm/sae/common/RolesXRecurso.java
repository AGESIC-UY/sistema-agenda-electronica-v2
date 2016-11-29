package uy.gub.imm.sae.common;

public enum RolesXRecurso {

  RA_AE_PLANIFICADOR_X_RECURSO("planificador"),
  AE_R_GENERADORREPORTES_X_RECURSO("generador_de_reportes");

  String clave;
  
  RolesXRecurso(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  };
  
  
}
