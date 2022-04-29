package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class DiasDeLaSemanaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue(){
        DiasDeLaSemana diasDeLaSemana1 = new DiasDeLaSemana(1);
        DiasDeLaSemana diasDeLaSemana2 = new DiasDeLaSemana(1);

        assertTrue(diasDeLaSemana1.equals(diasDeLaSemana2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse(){
        DiasDeLaSemana diasDeLaSemana1 = new DiasDeLaSemana(1);
        DiasDeLaSemana diasDeLaSemana2 = new DiasDeLaSemana();
        diasDeLaSemana2.setId(null);

        assertFalse(diasDeLaSemana1.equals(diasDeLaSemana2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber(){
        int hashCode1 = new DiasDeLaSemana(1).hashCode();
        int hashCode2 = new DiasDeLaSemana(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}