package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AgrupacionDatoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        AgrupacionDato agrupacionDato1 = new AgrupacionDato(1);
        AgrupacionDato agrupacionDato2 = new AgrupacionDato(1);

        assertTrue(agrupacionDato1.equals(agrupacionDato2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        AgrupacionDato agrupacionDato1 = new AgrupacionDato(1);
        AgrupacionDato agrupacionDato2 = new AgrupacionDato();
        agrupacionDato2.setId(null);

        assertFalse(agrupacionDato1.equals(agrupacionDato2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new AgrupacionDato(1).hashCode();
        int hashCode2 = new AgrupacionDato(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}