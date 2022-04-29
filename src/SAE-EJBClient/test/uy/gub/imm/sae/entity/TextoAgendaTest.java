package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextoAgendaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        TextoAgenda textoAgenda1 = new TextoAgenda(1);
        TextoAgenda textoAgenda2 = new TextoAgenda(1);

        assertTrue(textoAgenda1.equals(textoAgenda2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        TextoAgenda textoAgenda1 = new TextoAgenda(1);
        TextoAgenda textoAgenda2 = new TextoAgenda();
        textoAgenda2.setId(null);

        assertFalse(textoAgenda1.equals(textoAgenda2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new TextoAgenda(1).hashCode();
        int hashCode2 = new TextoAgenda(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}