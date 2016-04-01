
package uy.gub.agesic.itramites.bruto.web.ws.linea;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for estadoRespuestaEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="estadoRespuestaEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK"/>
 *     &lt;enumeration value="ERROR"/>
 *     &lt;enumeration value="EXCEPTION"/>
 *     &lt;enumeration value="EXCEPTION_MENSAJE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "estadoRespuestaEnum")
@XmlEnum
public enum EstadoRespuestaEnum {

    OK,
    ERROR,
    EXCEPTION,
    EXCEPTION_MENSAJE;

    public String value() {
        return name();
    }

    public static EstadoRespuestaEnum fromValue(String v) {
        return valueOf(v);
    }

}
