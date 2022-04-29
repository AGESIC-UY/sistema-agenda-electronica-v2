package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidacionTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Validacion validacion1 = new Validacion(1);
        Validacion validacion2 = new Validacion(1);

        assertTrue(validacion1.equals(validacion2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Validacion validacion1 = new Validacion(1);
        Validacion validacion2 = new Validacion();
        validacion2.setId(null);

        assertFalse(validacion1.equals(validacion2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Validacion(1).hashCode();
        int hashCode2 = new Validacion(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

}