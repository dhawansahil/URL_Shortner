package com.systemdesign.urlshortener.model;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class UrlMappingTest {

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        UrlMapping original = new UrlMapping();
        original.setShortId("abc");
        original.setLongUrl("https://google.com");
        original.setCreatedAt(new Date());
        original.setExpiresAt(new Date());

        // Serialize
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(original);
        out.close();

        // Deserialize
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        UrlMapping deserialized = (UrlMapping) in.readObject();

        // Verify
        assertEquals(original.getShortId(), deserialized.getShortId());
        assertEquals(original.getLongUrl(), deserialized.getLongUrl());
        assertEquals(original.getCreatedAt(), deserialized.getCreatedAt());
        // Date equals might fail if precision is lost, but usually fine for simple
        // check
        assertEquals(original.getExpiresAt(), deserialized.getExpiresAt());
    }
}
