package io.ezycollect.payment.application.port.command;

public interface EncryptionCommandPort {
    String encrypt(String plaintext);
    String decrypt(String ciphertextBase64);
}
