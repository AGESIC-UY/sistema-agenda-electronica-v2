package uy.gub.imm.sae.entity;

import org.junit.Test;
import uy.gub.imm.sae.entity.global.Oficina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class PreguntaCaptchaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        PreguntaCaptcha preguntaCaptcha1 = new PreguntaCaptcha("1");
        PreguntaCaptcha preguntaCaptcha2 = new PreguntaCaptcha("1");

        assertTrue(preguntaCaptcha1.equals(preguntaCaptcha2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        PreguntaCaptcha preguntaCaptcha1 = new PreguntaCaptcha("1");
        PreguntaCaptcha preguntaCaptcha2 = new PreguntaCaptcha(null);

        assertFalse(preguntaCaptcha1.equals(preguntaCaptcha2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new PreguntaCaptcha("1").hashCode();
        int hashCode2 = new PreguntaCaptcha("1").hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new PreguntaCaptcha("1").hashCode();
        int hashCode2 = new PreguntaCaptcha().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}