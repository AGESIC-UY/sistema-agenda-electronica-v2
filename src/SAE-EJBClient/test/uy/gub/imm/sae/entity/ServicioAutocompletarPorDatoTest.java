package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServicioAutocompletarPorDatoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ServicioAutocompletar servicioAutocompletar1 = new ServicioAutocompletar(1);
        ServicioAutocompletar servicioAutocompletar2 = new ServicioAutocompletar(1);

        assertTrue(servicioAutocompletar1.equals(servicioAutocompletar2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ServicioAutocompletar servicioAutocompletar1 = new ServicioAutocompletar(1);
        ServicioAutocompletar servicioAutocompletar2 = new ServicioAutocompletar();
        servicioAutocompletar2.setId(null);

        assertFalse(servicioAutocompletar1.equals(servicioAutocompletar2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ServicioAutocompletar(1).hashCode();
        int hashCode2 = new ServicioAutocompletar(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}