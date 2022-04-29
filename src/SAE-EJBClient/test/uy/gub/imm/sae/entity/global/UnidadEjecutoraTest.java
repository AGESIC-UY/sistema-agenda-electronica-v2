package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class UnidadEjecutoraTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        UnidadEjecutora unidadEjecutora1 = new UnidadEjecutora(1);
        UnidadEjecutora unidadEjecutora2 = new UnidadEjecutora(1);

        assertTrue(unidadEjecutora1.equals(unidadEjecutora2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        UnidadEjecutora unidadEjecutora1 = new UnidadEjecutora(1);
        UnidadEjecutora unidadEjecutora2 = new UnidadEjecutora();
        unidadEjecutora2.setId(null);

        assertFalse(unidadEjecutora1.equals(unidadEjecutora2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new UnidadEjecutora(1).hashCode();
        int hashCode2 = new UnidadEjecutora(1).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new UnidadEjecutora(1).hashCode();
        int hashCode2 = new UnidadEjecutora().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}