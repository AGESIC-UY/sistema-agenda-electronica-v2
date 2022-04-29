package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MesesTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Meses meses1 = new Meses(1);
        Meses meses2 = new Meses(1);

        assertTrue(meses1.equals(meses2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Meses meses1 = new Meses(1);
        Meses meses2 = new Meses();
        meses2.setId(null);

        assertFalse(meses1.equals(meses2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Meses(1).hashCode();
        int hashCode2 = new Meses(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }
}