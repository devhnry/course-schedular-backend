package com.henry.universitycourseschedular.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class IdUtils {
    public static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bb.array()).substring(0, 12); // ~12 chars
    }
}
