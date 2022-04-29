package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidacionPorRecursoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ValidacionPorRecurso validacionPorRecurso1 = new ValidacionPorRecurso(1);
        ValidacionPorRecurso validacionPorRecurso2 = new ValidacionPorRecurso(1);

        assertTrue(validacionPorRecurso1.equals(validacionPorRecurso2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ValidacionPorRecurso validacionPorRecurso1 = new ValidacionPorRecurso(1);
        ValidacionPorRecurso validacionPorRecurso2 = new ValidacionPorRecurso();
        validacionPorRecurso2.setId(null);

        assertFalse(validacionPorRecurso1.equals(validacionPorRecurso2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ValidacionPorRecurso(1).hashCode();
        int hashCode2 = new ValidacionPorRecurso(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

}