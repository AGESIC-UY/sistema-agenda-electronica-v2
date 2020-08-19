package uy.gub.imm.sae.web.common;

public class Validadores {

  private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTVUWXYZ0123456789_";
  private static final String NUMEROS="1234567890";
  
  public static boolean validarNombreIdentificador(String nombre) {
    if(nombre==null || nombre.isEmpty()) {
      return false;
    }
    nombre = nombre.toUpperCase();
    // Se chequea que el primer caracter no sea un numero
    char caracter = nombre.charAt(0);
    if (NUMEROS.indexOf(caracter) != -1){
      return false;
    }
    // Se chequea que todos los caracteres sean válidos
    for (int i = 0; i < nombre.length(); i++) {
      caracter = nombre.charAt(i);
      if (CARACTERES.indexOf(caracter) == -1) {
        return false;
      }
    }
    return true;
  }
  
  
}
