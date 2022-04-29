package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextoRecursoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        TextoRecurso textoRecurso1 = new TextoRecurso(1);
        TextoRecurso textoRecurso2 = new TextoRecurso(1);

        assertTrue(textoRecurso1.equals(textoRecurso2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        TextoRecurso textoRecurso1 = new TextoRecurso(1);
        TextoRecurso textoRecurso2 = new TextoRecurso();
        textoRecurso2.setId(null);

        assertFalse(textoRecurso1.equals(textoRecurso2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new TextoRecurso(1).hashCode();
        int hashCode2 = new TextoRecurso(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}