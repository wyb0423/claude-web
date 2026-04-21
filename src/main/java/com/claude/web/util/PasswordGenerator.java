package com.claude.web.util;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordGenerator {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public static String generatePassword() {
        byte[] bytes = new byte[16];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    public static void main(String[] args) {
        System.out.println("Generated password: " + generatePassword());
    }
}
