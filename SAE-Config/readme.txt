
COMPILACION
  Los archivos generados quedan en SAE-EAR/build/jar/

Opción 1 - LINEA DE COMANDOS
  Nota: requiere ant versión 1.9 o superior

  export JAVA_HOME=<ruta a directorio de instalación de jre7>
  export ANT_HOME=<ruta a directorio de instalación de ant>
  cd SAE-Config
  ant clean
  ant build
  
Nota: si al compilar se observa el error "/build.xml:51: srcdir "{...}/SAE-Config/ejbModule" does not exist!" seguramente se está utilizando una versión de ant anterior a 1.9.

Opción 2 - DESDE ECLIPSE

  Click derecho sobre SAE-Config/build.xml y elegir "Run"

INSTALACION

1 - Crear la base de datos
  1 - Crear una base de datos en el servidor Postgres
  2 - Crear el esquema global ejecutando el script esquema-global.sql
  3 - Para cada empresa a utilizar crear un esquema ejecutando el script esquema-template.sql (remplazar NombreEsquema)
2 - Configurar el servidor de aplicaciones (standalone.xml)
  1 - Crear los módulos necesarios
  2 - Añadir usuario anonimo/anonimo1 (anonimo=325621c25511c66dfe9580652de9291b) al archivo application-users.properties

3 - Deployar la aplicación
  1 - Invocar el script build.xml del proyecto SAE-Config
  2 - Copiar los cuatro archivos que están en la carpeta build/jar del proyecto SAE-EAR a la carpeta deployments
  
ACCESO

Administracion: http://host:port/sae-admin (ejemplo: http://localhost:8080/sae-admin/)
Reserva pública: http://host:port/sae/agendarReserva/Paso1.xhtml?empresa=<empresa>&agenda=<agenda>&recurso=<recurso>
Cancelar reserva pública: http://host:port/sae/cancelarReserva/Paso1.xhtml?empresa=<empresa>&agenda=<agenda>&recurso=<recurso>  

PRIMEFACES

Se tocaron los siguientes archivos de Primefaces:
  /org/primefaces/component/datatable/DataTable.java
  /org/primefaces/component/datatable/DataTableRenderer.java
  /org/primefaces/component/menu/BaseMenuRenderer.java
  /org/primefaces/component/messages/Messages.java
  /org/primefaces/component/messages/MessagesRenderer.java
  /org/primefaces/component/panelmenu/PanelMenuRenderer.java
  /org/primefaces/component/selectbooleancheckbox/SelectBooleanCheckboxRenderer.java
  /org/primefaces/component/selectonemenu/SelectOneMenuRenderer.java
  /org/primefaces/component/selectoneradio/SelectOneRadioRenderer.java
  /org/primefaces/component/tabview/TabViewRenderer.java
  /org/primefaces/component/api/UIData.java
  /org/primefaces/component/paginator/PageLinkRenderer.java
  /org/primefaces/component/paginator/FirstPageLinkRenderer.java
  /org/primefaces/component/paginator/PrevPageLinkRenderer.java
  /org/primefaces/component/paginator/NextPageLinkRenderer.java
  /org/primefaces/component/paginator/LastPageLinkRenderer.java
  
Cada vez que se modifica un archivo de Primefaces hay que compilarlo en un proyecto separado e incluirlo en el archivo primefaces-5.3-modificado.jar que está en /SAE-WEB/WebContent/WEB-INF/lib  
  
SERVICIOS WEB  
  
Para crear las clases de los servicios web, hay que utilizar directamente el comando wsimport de Java 7:
/usr/lib/jvm/jdk1.7.0_60/bin/wsimport -s . http://localhost:8080/SAE-StubsServiciosWebAgesic/wsTramite?wsdl
Luego, hay que modificar el archivo Persist.java para cambiar la parte que dice 
  @XmlType(name = "persist" 
por 
  @XmlType(name = "persist1"
y el archivo PersistResponse.java para cambiar la parte que 
  dice @XmlType(name = "persistResponse" 
por 
  @XmlType(name = "persistResponse1"
Esto es para evitar el problema "Two classes have the same XML type name"

En el caso de Trazabilidad, también hay que añadir la anotación @XmlRootElement a las clases CabezalDTO y LineaDTO
Esto es para poder generar el XML en el caso de que haya que almacenarlo para volver a intentar más tarde

