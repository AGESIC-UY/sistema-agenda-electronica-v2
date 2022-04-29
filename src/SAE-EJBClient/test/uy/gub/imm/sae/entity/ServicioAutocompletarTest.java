package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServicioAutocompletarTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ServicioAutocompletar servicioAutoCompletar1 = new ServicioAutocompletar(1);
        ServicioAutocompletar servicioAutoCompletar2 = new ServicioAutocompletar(1);

        assertTrue(servicioAutoCompletar1.equals(servicioAutoCompletar2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ServicioAutocompletar servicioAutoCompletar1 = new ServicioAutocompletar(1);
        ServicioAutocompletar servicioAutoCompletar2 = new ServicioAutocompletar();
        servicioAutoCompletar2.setId(null);

        assertFalse(servicioAutoCompletar1.equals(servicioAutoCompletar2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ServicioAutocompletar(1).hashCode();
        int hashCode2 = new ServicioAutocompletar(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}