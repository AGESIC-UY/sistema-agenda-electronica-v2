Este proyecto es el módulo de autenticación que usa la agenda (SAE).

COMPILACION

Opción 1 - LINEA DE COMANDOS

  Editar el archivo build.xml y configurar la propiedad "jboss.home" apuntando al servidor JBoss 7.1
  export JAVA_HOME=<ruta a directorio de instalación de jre7>
  export ANT_HOME=<ruta a directorio de instalación de ant>
  cd SAE-LoginModule
  ant clean
  ant build
  El archivo generado queda en el directorio dist

Opción 2 - DESDE ECLIPSE

  Click derecho sobre el proyecto, seleccionar "Export..." -> "Java/Jar File" y seleccionar la ubicación
  
Luego de generar el jar, copiarlo a:
  * SAE-Config/libs/ (para compilar SAE)
  * <jboss>/modules/saelogin/main (para ejecutar SAE)

    