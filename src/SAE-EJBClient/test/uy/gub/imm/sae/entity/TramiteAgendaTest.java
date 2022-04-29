package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TramiteAgendaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        TramiteAgenda tramiteAgenda1 = new TramiteAgenda(1);
        TramiteAgenda tramiteAgenda2 = new TramiteAgenda(1);

        assertTrue(tramiteAgenda1.equals(tramiteAgenda2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        TramiteAgenda tramiteAgenda1 = new TramiteAgenda(1);
        TramiteAgenda tramiteAgenda2 = new TramiteAgenda();
        tramiteAgenda2.setId(null);

        assertFalse(tramiteAgenda1.equals(tramiteAgenda2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new TramiteAgenda(1).hashCode();
        int hashCode2 = new TramiteAgenda(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

}