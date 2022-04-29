package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TramiteTest {

    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Tramite tramite1 = new Tramite("1");
        Tramite tramite2 = new Tramite("1");

        assertTrue(tramite1.equals(tramite2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Tramite tramite1 = new Tramite("1");
        Tramite tramite2 = new Tramite();
        tramite2.setId(null);

        assertFalse(tramite1.equals(tramite2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Tramite("1").hashCode();
        int hashCode2 = new Tramite("1").hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new Tramite("1").hashCode();
        int hashCode2 = new Tramite().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}