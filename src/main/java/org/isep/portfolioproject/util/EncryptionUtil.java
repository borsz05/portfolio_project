package org.isep.portfolioproject.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String PREFIX = "ENC1:";
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 12;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LEN = 256;

    private EncryptionUtil() {
    }

    public static byte[] encrypt(byte[] plain, String passphrase) {
        try {
            byte[] salt = new byte[SALT_LEN];
            byte[] iv = new byte[IV_LEN];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            random.nextBytes(iv);

            SecretKey key = deriveKey(passphrase, salt);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] cipherText = cipher.doFinal(plain);

            byte[] payload = new byte[salt.length + iv.length + cipherText.length];
            System.arraycopy(salt, 0, payload, 0, salt.length);
            System.arraycopy(iv, 0, payload, salt.length, iv.length);
            System.arraycopy(cipherText, 0, payload, salt.length + iv.length, cipherText.length);

            String encoded = PREFIX + Base64.getEncoder().encodeToString(payload);
            return encoded.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static byte[] decrypt(byte[] raw, String passphrase) {
        try {
            String text = new String(raw, StandardCharsets.UTF_8);
            if (!text.startsWith(PREFIX)) {
                return raw;
            }

            byte[] payload = Base64.getDecoder().decode(text.substring(PREFIX.length()));
            byte[] salt = new byte[SALT_LEN];
            byte[] iv = new byte[IV_LEN];
            byte[] cipherText = new byte[payload.length - SALT_LEN - IV_LEN];

            System.arraycopy(payload, 0, salt, 0, SALT_LEN);
            System.arraycopy(payload, SALT_LEN, iv, 0, IV_LEN);
            System.arraycopy(payload, SALT_LEN + IV_LEN, cipherText, 0, cipherText.length);

            SecretKey key = deriveKey(passphrase, salt);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public static boolean isEncrypted(byte[] raw) {
        if (raw == null || raw.length == 0) return false;
        String text = new String(raw, StandardCharsets.UTF_8);
        return text.startsWith(PREFIX);
    }

    private static SecretKey deriveKey(String passphrase, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONS, KEY_LEN);
        byte[] bytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(bytes, "AES");
    }
}
