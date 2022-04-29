package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ConfiguracionTest {

    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Configuracion config1 = new Configuracion("clave");
        Configuracion config2 = new Configuracion("clave");

        assertTrue(config1.equals(config2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Configuracion config1 = new Configuracion("clave");
        Configuracion config2 = new Configuracion("clave1");

        assertFalse(config1.equals(config2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Configuracion("clave").hashCode();
        int hashCode2 = new Configuracion("clave").hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new Configuracion("clave").hashCode();
        int hashCode2 = new Configuracion().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}