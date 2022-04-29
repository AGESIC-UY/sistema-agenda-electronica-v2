package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServicioPorRecursoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ServicioPorRecurso servicioPorRecurso1 = new ServicioPorRecurso(1);
        ServicioPorRecurso servicioPorRecurso2 = new ServicioPorRecurso(1);

        assertTrue(servicioPorRecurso1.equals(servicioPorRecurso2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ServicioPorRecurso servicioPorRecurso1 = new ServicioPorRecurso(1);
        ServicioPorRecurso servicioPorRecurso2 = new ServicioPorRecurso();
        servicioPorRecurso2.setId(null);

        assertFalse(servicioPorRecurso1.equals(servicioPorRecurso2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ServicioPorRecurso(1).hashCode();
        int hashCode2 = new ServicioPorRecurso(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}