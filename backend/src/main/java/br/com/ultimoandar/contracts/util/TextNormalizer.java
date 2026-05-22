package br.com.ultimoandar.contracts.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizer {

    private TextNormalizer() {
    }

    public static String search(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value.toLowerCase(Locale.ROOT).trim() + "%";
    }

    public static String safeFileName(String value) {
        String normalized = Normalizer.normalize(value == null ? "arquivo" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^a-zA-Z0-9._-]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("(^-|-$)", "")
                .toLowerCase(Locale.ROOT);
        return normalized.isBlank() ? "arquivo" : normalized;
    }
}
