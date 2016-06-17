
package uy.gub.agesic.novedades;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for acciones.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="acciones">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RESERVA"/>
 *     &lt;enumeration value="CANCELACION"/>
 *     &lt;enumeration value="ASISTENCIA"/>
 *     &lt;enumeration value="INASISTENCIA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "acciones")
@XmlEnum
public enum Acciones {

    RESERVA,
    CANCELACION,
    ASISTENCIA,
    INASISTENCIA;

    public String value() {
        return name();
    }

    public static Acciones fromValue(String v) {
        return valueOf(v);
    }

}
