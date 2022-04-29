package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NovedadTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Novedad novedad1 = new Novedad(1);
        Novedad novedad2 = new Novedad(1);

        assertTrue(novedad1.equals(novedad2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Novedad novedad1 = new Novedad(1);
        Novedad novedad2 = new Novedad();
        novedad2.setId(null);

        assertFalse(novedad1.equals(novedad2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Novedad(1).hashCode();
        int hashCode2 = new Novedad().hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}