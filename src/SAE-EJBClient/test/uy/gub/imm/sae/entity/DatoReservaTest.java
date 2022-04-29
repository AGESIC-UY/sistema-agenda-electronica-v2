package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class DatoReservaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue(){
        DatoReserva datoReserva1 = new DatoReserva(1);
        DatoReserva datoReserva2 = new DatoReserva(1);

        assertTrue(datoReserva1.equals(datoReserva2));
    }

    @Test
    public void givenObjectsWithSameIdAndName_whenEqual_thenReturnTrue(){
        DatoReserva datoReserva1 = new DatoReserva(1, "valor");
        DatoReserva datoReserva2 = new DatoReserva(1, "valor");

        assertTrue(datoReserva1.equals(datoReserva2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse(){
        DatoReserva datoReserva1 = new DatoReserva(1);
        DatoReserva datoReserva2 = new DatoReserva();
        datoReserva2.setId(null);

        assertFalse(datoReserva1.equals(datoReserva2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber(){
        int hashCode1 = new DatoReserva(1).hashCode();
        int hashCode2 = new DatoReserva(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}