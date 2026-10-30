package io.github.thelinuxseal.sealbot.rule;

import io.github.thelinuxseal.sealbot.MessageData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ChatPattern {
    Pattern getPattern();
    long getCooldown();
    MessageData getData(Matcher matcher);
    String format(MessageData data, String output);

    static String shortHash(String input, int bytes) {   // bytes=2 -> 4 hex chars, bytes=4 -> 8
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d, 0, bytes);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);   // SHA-256 is always available
        }
    }
}
