package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class AniosTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue(){
        Anios anios1 = new Anios(1);
        Anios anios2 = new Anios(1);

        assertTrue(anios1.equals(anios2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse(){
        Anios anios1 = new Anios(1);
        Anios anios2 = new Anios();
        anios2.setId(null);

        assertFalse(anios1.equals(anios2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber(){
        int hashCode1 = new Anios(1).hashCode();
        int hashCode2 = new Anios(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}