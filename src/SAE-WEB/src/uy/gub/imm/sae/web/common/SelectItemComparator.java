package uy.gub.imm.sae.web.common;

import java.util.Comparator;
import javax.faces.model.SelectItem;

public class SelectItemComparator implements Comparator<SelectItem> {  
  @Override  
  public int compare(SelectItem s1, SelectItem s2) {  
      return s1.getLabel().compareTo(s2.getLabel());  
  }  
}