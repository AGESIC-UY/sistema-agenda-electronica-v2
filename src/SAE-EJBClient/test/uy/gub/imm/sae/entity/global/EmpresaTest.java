package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmpresaTest {

    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Empresa empresa1 = new Empresa(1);
        Empresa empresa2 = new Empresa(1);

        assertTrue(empresa1.equals(empresa2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Empresa empresa1 = new Empresa(1);
        Empresa empresa2 = new Empresa();
        empresa2.setId(null);

        assertFalse(empresa1.equals(empresa2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Empresa(1).hashCode();
        int hashCode2 = new Empresa().hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}