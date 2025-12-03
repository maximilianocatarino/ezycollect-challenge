package io.ezycollect.shared.infra.crypto;

import io.ezycollect.payment.application.port.command.EncryptionCommandPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesEncryption implements EncryptionCommandPort {

    private static final Logger log = LoggerFactory.getLogger(AesEncryption.class);

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16;
    private static final int GCM_IV_LENGTH = 12; // 96 bits

    private final SecretKeySpec secretKey;

    public AesEncryption(String secretKeyBase64) {
        // Decode the base64 key provided in application.yaml
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyBase64);
        // Ensure key is 256 bits (32 bytes)
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Encryption key must be 32 bytes (256 bits) after base64 decoding.");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv); // Generate a fresh IV for each encryption

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] output = new byte[GCM_IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, output, 0, GCM_IV_LENGTH);
            System.arraycopy(cipherText, 0, output, GCM_IV_LENGTH, cipherText.length);

            return Base64.getEncoder().encodeToString(output);
        } catch (Exception e) {
            throw new RuntimeException("Error during encryption", e);
        }
    }

    @Override
    public String decrypt(String ciphertextBase64) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(ciphertextBase64);

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(encryptedData, 0, iv, 0, GCM_IV_LENGTH);

            byte[] cipherText = new byte[encryptedData.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedData, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] plaintext = cipher.doFinal(cipherText);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption error", e);
            throw new RuntimeException("Error during decryption. Data corruption or invalid key.", e);
        }
    }
}
