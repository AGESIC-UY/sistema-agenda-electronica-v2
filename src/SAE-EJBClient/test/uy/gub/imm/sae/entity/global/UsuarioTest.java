package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsuarioTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Usuario usuario1 = new Usuario(1);
        Usuario usuario2 = new Usuario(1);

        assertTrue(usuario1.equals(usuario2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Usuario usuario1 = new Usuario(1);
        Usuario usuario2 = new Usuario();
        usuario2.setId(null);

        assertFalse(usuario1.equals(usuario2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Usuario(1).hashCode();
        int hashCode2 = new Usuario(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}