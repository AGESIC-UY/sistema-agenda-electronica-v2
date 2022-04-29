package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReservaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Reserva reserva1 = new Reserva(1);
        Reserva reserva2 = new Reserva(1);

        assertTrue(reserva1.equals(reserva2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Reserva reserva1 = new Reserva(1);
        Reserva reserva2 = new Reserva();
        reserva2.setId(null);

        assertFalse(reserva1.equals(reserva2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Reserva(1).hashCode();
        int hashCode2 = new Reserva(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}