package uy.gub.imm.sae.entity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TokenReservaTest {
    @Test
    public void givenObjectsWithSameId_whenEqual_thenReturnTrue() {
        TokenReserva tokenTokenReserva1 = new TokenReserva(1);
        TokenReserva tokenTokenReserva2 = new TokenReserva(1);

        assertTrue(tokenTokenReserva1.equals(tokenTokenReserva2));
    }

    @Test
    public void givenObjectsWithDifferentId_whenEqual_thenReturnFalse() {
        TokenReserva tokenTokenReserva1 = new TokenReserva(1);
        TokenReserva tokenTokenReserva2 = new TokenReserva();
        tokenTokenReserva2.setId(null);

        assertFalse(tokenTokenReserva1.equals(tokenTokenReserva2));
    }

    @Test
    public void givenObjectsFromSameClass_whenHashcode_thenReturnSameNumber() {
        int hashCode1 = new TokenReserva(1).hashCode();
        int hashCode2 = new TokenReserva(2).hashCode();

        assertEquals(hashCode1, hashCode2);
    }

}