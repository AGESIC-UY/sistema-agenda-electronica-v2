package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class DatoDelRecursoTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue(){
        DatoDelRecurso datoDelRecurso1 = new DatoDelRecurso(1);
        DatoDelRecurso datoDelRecurso2 = new DatoDelRecurso(1);

        assertTrue(datoDelRecurso1.equals(datoDelRecurso2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse(){
        DatoDelRecurso datoDelRecurso1 = new DatoDelRecurso(1);
        DatoDelRecurso datoDelRecurso2 = new DatoDelRecurso();
        datoDelRecurso2.setId(null);

        assertFalse(datoDelRecurso1.equals(datoDelRecurso2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber(){
        int hashCode1 = new DatoDelRecurso(1).hashCode();
        int hashCode2 = new DatoDelRecurso(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}