package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidacionPorDatoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ValidacionPorDato validacionPorDato1 = new ValidacionPorDato(1);
        ValidacionPorDato validacionPorDato2 = new ValidacionPorDato(1);

        assertTrue(validacionPorDato1.equals(validacionPorDato2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ValidacionPorDato validacionPorDato1 = new ValidacionPorDato(1);
        ValidacionPorDato validacionPorDato2 = new ValidacionPorDato();
        validacionPorDato2.setId(null);

        assertFalse(validacionPorDato1.equals(validacionPorDato2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ValidacionPorDato(1).hashCode();
        int hashCode2 = new ValidacionPorDato(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

}