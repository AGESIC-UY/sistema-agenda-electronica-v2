package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValorPosibleTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ValorPosible valorPosible1 = new ValorPosible(1);
        ValorPosible valorPosible2 = new ValorPosible(1);

        assertTrue(valorPosible1.equals(valorPosible2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ValorPosible valorPosible1 = new ValorPosible(1);
        ValorPosible valorPosible2 = new ValorPosible();
        valorPosible2.setId(null);

        assertFalse(valorPosible1.equals(valorPosible2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ValorPosible(1).hashCode();
        int hashCode2 = new ValorPosible(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

}