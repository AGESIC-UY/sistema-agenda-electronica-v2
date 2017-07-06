package uy.gub.imm.sae.web.common;

import java.util.Comparator;

import uy.gub.imm.sae.entity.Recurso;

public class RecursoComparatorNombre implements Comparator<Recurso> {

  @Override
  public int compare(Recurso recurso1, Recurso recurso2) {

    if(recurso1 == null || recurso1.getNombre()==null) {
      if(recurso2 == null || recurso2.getNombre()==null) {
        return 0;
      }
      return 1;
    }else {
      if(recurso2 == null  || recurso2.getNombre()==null) {
        return -1;
      }
      return recurso1.getNombre().compareTo(recurso2.getNombre());
    }
  }

}
