package br.com.ultimoandar.contracts.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;

@Converter
public class SensitiveStringConverter implements AttributeConverter<String, String> {

    private static final String PREFIX = "v1:";
    private static final String CRYPTO_KEY_ENV = "APP_CRYPTO_KEY";
    private static final String DEFAULT_CRYPTO_KEY = "ultimo-andar-local-crypto-key-change-me";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank() || attribute.startsWith(PREFIX)) {
            return attribute;
        }
        try {
            byte[] iv = new byte[12];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    CryptoKeys.aesKey(CRYPTO_KEY_ENV, DEFAULT_CRYPTO_KEY),
                    new GCMParameterSpec(128, iv)
            );
            byte[] encrypted = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            return PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(iv)
                    + ":"
                    + Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível criptografar dado sensível.", exception);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank() || !dbData.startsWith(PREFIX)) {
            return dbData;
        }
        try {
            String[] parts = dbData.split(":");
            byte[] iv = Base64.getUrlDecoder().decode(parts[1]);
            byte[] encrypted = Base64.getUrlDecoder().decode(parts[2]);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    CryptoKeys.aesKey(CRYPTO_KEY_ENV, DEFAULT_CRYPTO_KEY),
                    new GCMParameterSpec(128, iv)
            );
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível descriptografar dado sensível.", exception);
        }
    }
}
