package uy.gub.imm.sae.web.common;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class SofisJSFUtils {

  public static String obtenerDireccionIPCliente(FacesContext cxt) {
    HttpServletRequest request = (HttpServletRequest) cxt.getExternalContext().getRequest();
    String dirIp = request.getHeader("X-FORWARDED-FOR");
    if (dirIp == null) {
      dirIp = request.getRemoteAddr();
    }
    return dirIp;
  }
  
  public static void printComponentTree(UIComponent comp){
    printComponentTree(comp, 0);
  }

  
  private static void printComponentTree(UIComponent comp, int indent){
    printComponentInfo(comp, indent);
    
    List<UIComponent> complist = comp.getChildren();
    if (complist.size()>0)
      indent++;
    for   (int i = 0; i < complist.size(); i++) {
      UIComponent uicom = complist.get(i);
      printComponentTree(uicom, indent);
      if (i+1 == complist.size())
        indent--;
    }
    
  }

  private static void printComponentInfo(UIComponent comp, int indent){
  
   if (comp.getId() == null){
     System.out.println("UIViewRoot" + " " + "(" + comp.getClass().getName() + ")");
   } else {
       printIndent(indent);
       System.out.println("|");
       printIndent(indent);
       System.out.println(comp.getId() + " " + comp.getClientId() + " " + "(" + comp.getClass().getName() + ")");
     }  
  }  
  
  private static void printIndent(int indent){
    for (int i=0; i<indent; i++ )  
      for (int j=0;j<2; j++)  
        System.out.print(" ");          
  } 
  
  
  
}
