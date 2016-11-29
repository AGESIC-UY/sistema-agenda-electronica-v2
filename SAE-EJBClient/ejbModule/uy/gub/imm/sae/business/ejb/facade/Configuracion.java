package uy.gub.imm.sae.business.ejb.facade;

public interface Configuracion {

  public String getString(String clave);
  public Boolean getBoolean(String clave);
  public Long getLong(String clave);
  
}
