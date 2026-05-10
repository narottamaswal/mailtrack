package com.mailtrack.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public final class IdGenerator {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RNG = new SecureRandom();
    private IdGenerator() {}

    public static String generate(int len) {
        return RNG.ints(len, 0, CHARS.length())
                  .mapToObj(i -> String.valueOf(CHARS.charAt(i)))
                  .collect(Collectors.joining());
    }

    public static String campaignId() { return generate(10); }
    public static String itemId()     { return generate(8); }
    public static String linkHash()   { return generate(6); }
    public static String sessionId()  { return java.util.UUID.randomUUID().toString(); }
}
