package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecursoAudTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        RecursoAud recursoAud1 = new RecursoAud(1);
        RecursoAud recursoAud2 = new RecursoAud(1);

        assertTrue(recursoAud1.equals(recursoAud2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        RecursoAud recursoAud1 = new RecursoAud(1);
        RecursoAud recursoAud2 = new RecursoAud();
        recursoAud2.setId(null);

        assertFalse(recursoAud1.equals(recursoAud2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new RecursoAud(1).hashCode();
        int hashCode2 = new RecursoAud(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}