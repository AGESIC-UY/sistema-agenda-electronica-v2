package uy.gub.imm.sae.web.common;

import java.util.Comparator;

import uy.gub.imm.sae.entity.Agenda;

public class AgendaComparatorNombre implements Comparator<Agenda> {

  @Override
  public int compare(Agenda agenda1, Agenda agenda2) {

    if(agenda1 == null || agenda1.getNombre()==null) {
      if(agenda2 == null || agenda2.getNombre()==null) {
        return 0;
      }
      return 1;
    }else {
      if(agenda2 == null  || agenda2.getNombre()==null) {
        return -1;
      }
      return agenda1.getNombre().compareTo(agenda2.getNombre());
    }
  }

}
