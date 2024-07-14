package com.project.consumer.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashGenerator {
    public static String generateHash(String timestamp, String description) {
        try{
            String input = timestamp + description;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHes(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHes(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
