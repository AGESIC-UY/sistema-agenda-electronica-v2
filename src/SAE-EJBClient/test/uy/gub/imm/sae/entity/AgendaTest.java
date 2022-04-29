package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgendaTest {

    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue(){
        Agenda agenda1 = new Agenda(1);
        Agenda agenda2 = new Agenda(1);

        assertTrue(agenda1.equals(agenda2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse(){
        Agenda agenda1 = new Agenda(1);
        Agenda agenda2 = new Agenda();
        agenda2.setId(null);

        assertFalse(agenda1.equals(agenda2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber(){
        int hashCode1 = new Agenda(1).hashCode();
        int hashCode2 = new Agenda(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}