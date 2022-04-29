package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TextoGlobalTest {

    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        TextoGlobal textoGlobal1 = new TextoGlobal("codigo");
        TextoGlobal textoGlobal2 = new TextoGlobal("codigo");

        assertTrue(textoGlobal1.equals(textoGlobal2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        TextoGlobal textoGlobal1 = new TextoGlobal("codigo");
        TextoGlobal textoGlobal2 = new TextoGlobal("codigo1");

        assertFalse(textoGlobal1.equals(textoGlobal2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new TextoGlobal("codigo").hashCode();
        int hashCode2 = new TextoGlobal("codigo").hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new TextoGlobal("codigo").hashCode();
        int hashCode2 = new TextoGlobal().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}