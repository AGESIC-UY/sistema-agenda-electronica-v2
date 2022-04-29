package uy.gub.imm.sae.entity;

import org.junit.Test;
import uy.gub.imm.sae.entity.global.Oficina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class FraseCaptchaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        FraseCaptcha fraseCaptcha1 = new FraseCaptcha("1");
        FraseCaptcha fraseCaptcha2 = new FraseCaptcha("1");

        assertTrue(fraseCaptcha1.equals(fraseCaptcha2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        FraseCaptcha fraseCaptcha1 = new FraseCaptcha("1");
        FraseCaptcha fraseCaptcha2 = new FraseCaptcha();
        fraseCaptcha2.setClave(null);

        assertFalse(fraseCaptcha1.equals(fraseCaptcha2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new FraseCaptcha("1").hashCode();
        int hashCode2 = new FraseCaptcha("1").hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new FraseCaptcha("1").hashCode();
        int hashCode2 = new FraseCaptcha().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}