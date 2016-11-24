package uy.gub.imm.sae.common;

import java.util.HashMap;

public class SofisHashMap extends HashMap<String, String> {

  /**
   * 
   */
  private static final long serialVersionUID = -6772446218665849664L;

  @Override
  public String get(Object key) {
    if(key == null) {
      return null;
    }
    if(super.containsKey(key)) {
      return super.get(key);
    }else {
      return key.toString();
    }
  }
  
  

}
