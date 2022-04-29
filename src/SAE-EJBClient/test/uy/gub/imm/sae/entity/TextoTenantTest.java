package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TextoTenantTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        TextoTenant textoTenant1 = new TextoTenant(new TextoGlobalId("1", "1"));
        TextoTenant textoTenant2 = new TextoTenant(new TextoGlobalId("1", "1"));

        assertTrue(textoTenant1.equals(textoTenant2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        TextoTenant textoTenant1 = new TextoTenant(new TextoGlobalId("1", "1"));
        TextoTenant textoTenant2 = new TextoTenant(null);

        assertFalse(textoTenant1.equals(textoTenant2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new TextoTenant(new TextoGlobalId("1", "1")).hashCode();
        int hashCode2 = new TextoTenant(new TextoGlobalId("1", "1")).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new TextoTenant(new TextoGlobalId("1", "1")).hashCode();
        int hashCode2 = new TextoTenant().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}