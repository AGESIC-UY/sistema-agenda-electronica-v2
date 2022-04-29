package uy.gub.imm.sae.entity.global;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TokenTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        Token token1 = new Token("token");
        Token token2 = new Token("token");

        assertTrue(token1.equals(token2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        Token token1 = new Token("token");
        Token token2 = new Token("token1");

        assertFalse(token1.equals(token2));
    }

    @Test
    public void givenObjectsWithSameClave_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new Token("1").hashCode();
        int hashCode2 = new Token("1").hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void givenObjectsWithDifferentClave_whenHashcode_thenReturnDifferentNumber() {
        int hashCode1 = new Token("1").hashCode();
        int hashCode2 = new Token().hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }
}