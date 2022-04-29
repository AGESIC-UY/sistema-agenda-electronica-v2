# SAE

## Compilación

En el directorio `SAE-Config` ejecutar `ant build`. 
Se requiere una variable de entorno `SAE2_DEPLOYMENT_FOLDER` que apunte a la carpeta de deployments del servidor, por ejemplo `D:/Servers/jboss-as-7.1.1.Final/standalone/deployments`, pues allí se van a copiar los .ear resultantes de la compilación.

## Testing unitario

En el directorio `SAE-Config` ejecutar `ant test`

### Test coverage

En el directorio `SAE-Config` ejecutar `ant report`.

El reporte de cobertura de código queda en la carpeta `SAE-Config/jacoco`.

