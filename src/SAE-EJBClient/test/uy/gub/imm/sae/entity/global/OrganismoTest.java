package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class OrganismoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Organismo organismo1 = new Organismo(1);
        Organismo organismo2 = new Organismo(1);

        assertTrue(organismo1.equals(organismo2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Organismo organismo1 = new Organismo(1);
        Organismo organismo2 = new Organismo();
        organismo2.setId(null);

        assertFalse(organismo1.equals(organismo2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Organismo(1).hashCode();
        int hashCode2 = new Organismo(1).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new Organismo(1).hashCode();
        int hashCode2 = new Organismo().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}