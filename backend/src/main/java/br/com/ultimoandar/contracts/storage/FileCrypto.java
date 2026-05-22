package br.com.ultimoandar.contracts.storage;

import br.com.ultimoandar.contracts.security.CryptoKeys;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;

final class FileCrypto {

    private static final byte[] MAGIC = "UAENC1\n".getBytes(StandardCharsets.UTF_8);
    private static final String CRYPTO_KEY_ENV = "APP_CRYPTO_KEY";
    private static final String DEFAULT_CRYPTO_KEY = "ultimo-andar-local-crypto-key-change-me";
    private static final SecureRandom RANDOM = new SecureRandom();

    private FileCrypto() {
    }

    static byte[] encrypt(byte[] plain) {
        try {
            byte[] iv = new byte[12];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    CryptoKeys.aesKey(CRYPTO_KEY_ENV, DEFAULT_CRYPTO_KEY),
                    new GCMParameterSpec(128, iv)
            );
            byte[] encrypted = cipher.doFinal(plain);
            byte[] output = new byte[MAGIC.length + iv.length + encrypted.length];
            System.arraycopy(MAGIC, 0, output, 0, MAGIC.length);
            System.arraycopy(iv, 0, output, MAGIC.length, iv.length);
            System.arraycopy(encrypted, 0, output, MAGIC.length + iv.length, encrypted.length);
            return output;
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível criptografar arquivo.", exception);
        }
    }

    static byte[] decryptIfNeeded(byte[] bytes) {
        if (!isEncrypted(bytes)) {
            return bytes;
        }
        try {
            byte[] iv = Arrays.copyOfRange(bytes, MAGIC.length, MAGIC.length + 12);
            byte[] encrypted = Arrays.copyOfRange(bytes, MAGIC.length + 12, bytes.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    CryptoKeys.aesKey(CRYPTO_KEY_ENV, DEFAULT_CRYPTO_KEY),
                    new GCMParameterSpec(128, iv)
            );
            return cipher.doFinal(encrypted);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível descriptografar arquivo.", exception);
        }
    }

    private static boolean isEncrypted(byte[] bytes) {
        if (bytes.length < MAGIC.length + 12) {
            return false;
        }
        return Arrays.equals(bytes, 0, MAGIC.length, MAGIC, 0, MAGIC.length);
    }
}
