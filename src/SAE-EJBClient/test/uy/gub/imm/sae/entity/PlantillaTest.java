package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlantillaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Plantilla plantilla1 = new Plantilla(1);
        Plantilla plantilla2 = new Plantilla(1);

        assertTrue(plantilla1.equals(plantilla2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Plantilla plantilla1 = new Plantilla(1);
        Plantilla plantilla2 = new Plantilla();
        plantilla2.setId(null);

        assertFalse(plantilla1.equals(plantilla2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Plantilla(1).hashCode();
        int hashCode2 = new Plantilla(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}