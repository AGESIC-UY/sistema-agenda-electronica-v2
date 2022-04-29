package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParametroValidacionTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ParametroValidacion parametroValidacion1 = new ParametroValidacion(1);
        ParametroValidacion parametroValidacion2 = new ParametroValidacion(1);

        assertTrue(parametroValidacion1.equals(parametroValidacion2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ParametroValidacion parametroValidacion1 = new ParametroValidacion(1);
        ParametroValidacion parametroValidacion2 = new ParametroValidacion();
        parametroValidacion2.setId(null);

        assertFalse(parametroValidacion1.equals(parametroValidacion2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ParametroValidacion(1).hashCode();
        int hashCode2 = new ParametroValidacion(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}