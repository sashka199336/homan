package com.globus.biometric_auth_service.util;

import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class Base64Service {

    public static String encode(String plainText) {
        if (plainText == null) {
            throw new IllegalArgumentException("Cannot encode null string");
        }
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    public static String decode(String encodedText) {
        if (encodedText == null) {
            throw new IllegalArgumentException("Cannot decode null string");
        }
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        return new String(decodedBytes);
    }
}