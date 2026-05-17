package com.urlshortener.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62ServiceTest {

    private final Base62Service base62Service = new Base62Service();

    @Test
    void testEncodeDecode() {
        long id = 12345L;
        String encoded = base62Service.encode(id);
        long decoded = base62Service.decode(encoded);

        assertEquals(id, decoded);
    }

    @Test
    void testEncodeZero() {
        long id = 0L;
        String encoded = base62Service.encode(id);
        assertEquals("000000", encoded);
        
        long decoded = base62Service.decode(encoded);
        assertEquals(id, decoded);
    }

    @Test
    void testLargeId() {
        long id = 9876543210L;
        String encoded = base62Service.encode(id);
        long decoded = base62Service.decode(encoded);

        assertEquals(id, decoded);
    }
}
