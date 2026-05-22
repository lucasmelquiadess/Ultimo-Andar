package br.com.ultimoandar.contracts.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoKeys {

    private CryptoKeys() {
    }

    public static SecretKeySpec aesKey(String environmentName, String fallback) {
        try {
            String configured = SecretResolver.resolve(environmentName, fallback);
            byte[] configuredBytes = configured.getBytes(StandardCharsets.UTF_8);
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(configuredBytes);
            return new SecretKeySpec(digest, "AES");
        } catch (Exception exception) {
            throw new IllegalStateException("Nao foi possivel preparar a chave de criptografia.", exception);
        }
    }
}
