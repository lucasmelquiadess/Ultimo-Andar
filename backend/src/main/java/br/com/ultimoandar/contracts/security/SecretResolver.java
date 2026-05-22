package br.com.ultimoandar.contracts.security;

import java.nio.file.Files;
import java.nio.file.Path;

public final class SecretResolver {

    private SecretResolver() {
    }

    public static String resolve(String environmentName, String fallback) {
        String filePath = System.getenv(environmentName + "_FILE");
        if (filePath != null && !filePath.isBlank()) {
            try {
                return Files.readString(Path.of(filePath).toAbsolutePath().normalize()).trim();
            } catch (Exception exception) {
                throw new IllegalStateException("Nao foi possivel ler o segredo em " + environmentName + "_FILE.", exception);
            }
        }
        return System.getenv().getOrDefault(environmentName, fallback);
    }
}
