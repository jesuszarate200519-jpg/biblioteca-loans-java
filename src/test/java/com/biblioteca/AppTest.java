package com.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void testTrueIsTrue() {
        assertTrue(true, "true debería ser true");
    }

    @Test
    void testSum() {
        int a = 2;
        int b = 3;
        int result = a + b;
        assertEquals(5, result, "2 + 3 debería ser 5");
    }

    @Test
    void testStringNotNull() {
        String str = "Biblioteca";
        assertNotNull(str, "La cadena no debería ser null");
        assertEquals("Biblioteca", str, "La cadena debería ser 'Biblioteca'");
    }

    @Test
    void testException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> { throw new IllegalArgumentException("Error de prueba"); }
        );
        assertEquals("Error de prueba", exception.getMessage());
    }
}
