package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class OficinaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Oficina oficina1 = new Oficina("1");
        Oficina oficina2 = new Oficina("1");

        assertTrue(oficina1.equals(oficina2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Oficina oficina1 = new Oficina("1");
        Oficina oficina2 = new Oficina();
        oficina2.setId(null);

        assertFalse(oficina1.equals(oficina2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Oficina("1").hashCode();
        int hashCode2 = new Oficina("1").hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new Oficina("1").hashCode();
        int hashCode2 = new Oficina().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}