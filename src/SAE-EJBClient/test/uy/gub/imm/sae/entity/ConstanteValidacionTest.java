package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstanteValidacionTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ConstanteValidacion constanteValidacion1 = new ConstanteValidacion(1);
        ConstanteValidacion constanteValidacion2 = new ConstanteValidacion(1);

        assertTrue(constanteValidacion1.equals(constanteValidacion2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ConstanteValidacion constanteValidacion1 = new ConstanteValidacion(1);
        ConstanteValidacion constanteValidacion2 = new ConstanteValidacion();
        constanteValidacion2.setId(null);

        assertFalse(constanteValidacion1.equals(constanteValidacion2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ConstanteValidacion(1).hashCode();
        int hashCode2 = new ConstanteValidacion(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}