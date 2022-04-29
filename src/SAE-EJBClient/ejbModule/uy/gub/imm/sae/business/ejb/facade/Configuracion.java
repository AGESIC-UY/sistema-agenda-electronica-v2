package uy.gub.imm.sae.business.ejb.facade;

import java.util.List;

public interface Configuracion {

  public String getString(String clave);
  public Boolean getBoolean(String clave);
  public Long getLong(String clave);

  public List<uy.gub.imm.sae.entity.global.Configuracion> getAll();
  public void guardar(uy.gub.imm.sae.entity.global.Configuracion conf);
}
