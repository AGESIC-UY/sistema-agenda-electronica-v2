package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccionMiPerfilTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        AccionMiPerfil accionMiPerfil1 = new AccionMiPerfil(1);
        AccionMiPerfil accionMiPerfil2 = new AccionMiPerfil(1);

        assertTrue(accionMiPerfil1.equals(accionMiPerfil2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        AccionMiPerfil accionMiPerfil1 = new AccionMiPerfil(1);
        AccionMiPerfil accionMiPerfil2 = new AccionMiPerfil();
        accionMiPerfil2.setId(null);

        assertFalse(accionMiPerfil1.equals(accionMiPerfil2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new AccionMiPerfil(1).hashCode();
        int hashCode2 = new AccionMiPerfil(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}