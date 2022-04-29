package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class AtencionTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue(){
        Atencion atencion1 = new Atencion(1);
        Atencion atencion2 = new Atencion(1);

        assertTrue(atencion1.equals(atencion2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse(){
        Atencion atencion1 = new Atencion(1);
        Atencion atencion2 = new Atencion();
        atencion2.setId(null);

        assertFalse(atencion1.equals(atencion2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber(){
        int hashCode1 = new Atencion(1).hashCode();
        int hashCode2 = new Atencion(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}