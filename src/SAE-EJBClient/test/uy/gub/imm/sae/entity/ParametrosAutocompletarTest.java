package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParametrosAutocompletarTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ParametrosAutocompletar parametrosAutocompletar1 = new ParametrosAutocompletar(1);
        ParametrosAutocompletar parametrosAutocompletar2 = new ParametrosAutocompletar(1);

        assertTrue(parametrosAutocompletar1.equals(parametrosAutocompletar2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ParametrosAutocompletar parametrosAutocompletar1 = new ParametrosAutocompletar(1);
        ParametrosAutocompletar parametrosAutocompletar2 = new ParametrosAutocompletar();
        parametrosAutocompletar2.setId(null);

        assertFalse(parametrosAutocompletar1.equals(parametrosAutocompletar2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ParametrosAutocompletar(1).hashCode();
        int hashCode2 = new ParametrosAutocompletar(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}