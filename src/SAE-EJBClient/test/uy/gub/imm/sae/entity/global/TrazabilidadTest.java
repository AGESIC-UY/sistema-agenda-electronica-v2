package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TrazabilidadTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Trazabilidad trazabilidad1 = new Trazabilidad(1);
        Trazabilidad trazabilidad2 = new Trazabilidad(1);

        assertTrue(trazabilidad1.equals(trazabilidad2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Trazabilidad trazabilidad1 = new Trazabilidad(1);
        Trazabilidad trazabilidad2 = new Trazabilidad();
        trazabilidad2.setId(null);

        assertFalse(trazabilidad1.equals(trazabilidad2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Trazabilidad(1).hashCode();
        int hashCode2 = new Trazabilidad(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}