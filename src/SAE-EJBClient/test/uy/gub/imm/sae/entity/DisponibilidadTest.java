package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DisponibilidadTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Disponibilidad disponibilidad1 = new Disponibilidad(1);
        Disponibilidad disponibilidad2 = new Disponibilidad(1);

        assertTrue(disponibilidad1.equals(disponibilidad2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Disponibilidad disponibilidad1 = new Disponibilidad(1);
        Disponibilidad disponibilidad2 = new Disponibilidad();
        disponibilidad2.setId(null);

        assertFalse(disponibilidad1.equals(disponibilidad2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Disponibilidad(1).hashCode();
        int hashCode2 = new Disponibilidad(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}