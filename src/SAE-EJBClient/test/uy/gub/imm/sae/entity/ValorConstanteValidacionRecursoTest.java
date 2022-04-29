package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValorConstanteValidacionRecursoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        ValorConstanteValidacionRecurso valorConstanteValidacionRecurso1 = new ValorConstanteValidacionRecurso(1);
        ValorConstanteValidacionRecurso valorConstanteValidacionRecurso2 = new ValorConstanteValidacionRecurso(1);

        assertTrue(valorConstanteValidacionRecurso1.equals(valorConstanteValidacionRecurso2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        ValorConstanteValidacionRecurso valorConstanteValidacionRecurso1 = new ValorConstanteValidacionRecurso(1);
        ValorConstanteValidacionRecurso valorConstanteValidacionRecurso2 = new ValorConstanteValidacionRecurso();
        valorConstanteValidacionRecurso2.setId(null);

        assertFalse(valorConstanteValidacionRecurso1.equals(valorConstanteValidacionRecurso2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new ValorConstanteValidacionRecurso(1).hashCode();
        int hashCode2 = new ValorConstanteValidacionRecurso(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

}