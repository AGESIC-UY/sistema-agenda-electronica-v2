package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RolesUsuarioRecursoIdTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        RolesUsuarioRecursoId rolesUsuarioRecursoId1 = new RolesUsuarioRecursoId(1, 1);
        RolesUsuarioRecursoId rolesUsuarioRecursoId2 = new RolesUsuarioRecursoId(1, 1);

        assertTrue(rolesUsuarioRecursoId1.equals(rolesUsuarioRecursoId2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        RolesUsuarioRecursoId rolesUsuarioRecursoId1 = new RolesUsuarioRecursoId(1, 1);
        RolesUsuarioRecursoId rolesUsuarioRecursoId2 = new RolesUsuarioRecursoId();

        assertFalse(rolesUsuarioRecursoId1.equals(rolesUsuarioRecursoId2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new RolesUsuarioRecursoId(1, 1).hashCode();
        int hashCode2 = new RolesUsuarioRecursoId(1, 1).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new RolesUsuarioRecursoId(1, 1).hashCode();
        int hashCode2 = new RolesUsuarioRecursoId().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}