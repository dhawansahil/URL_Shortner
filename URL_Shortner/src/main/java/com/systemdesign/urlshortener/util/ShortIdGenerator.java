package com.systemdesign.urlshortener.util;

import java.security.SecureRandom;
import java.util.Random;

public class ShortIdGenerator {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();
    private static final Random RANDOM = new SecureRandom();
    private static final int DEFAULT_LENGTH = 7;

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(BASE)));
        }
        return sb.toString();
    }
    
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }
}
