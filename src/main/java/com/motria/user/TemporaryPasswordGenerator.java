package com.motria.user;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Genera contraseñas temporales seguras y fáciles de leer
 * (sin caracteres ambiguos como 0/O o 1/l/I).
 */
@Component
public class TemporaryPasswordGenerator {

    static final int LENGTH = 12;

    private static final String LOWER = "abcdefghijkmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String DIGITS = "23456789";
    private static final String SYMBOLS = "!@#$%&*";
    private static final String ALL = LOWER + UPPER + DIGITS + SYMBOLS;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder password = new StringBuilder(LENGTH);
        // Garantiza al menos un carácter de cada tipo.
        password.append(randomChar(LOWER))
                .append(randomChar(UPPER))
                .append(randomChar(DIGITS))
                .append(randomChar(SYMBOLS));
        while (password.length() < LENGTH) {
            password.append(randomChar(ALL));
        }
        return shuffle(password);
    }

    private char randomChar(String alphabet) {
        return alphabet.charAt(random.nextInt(alphabet.length()));
    }

    private String shuffle(CharSequence text) {
        char[] chars = text.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }
}
