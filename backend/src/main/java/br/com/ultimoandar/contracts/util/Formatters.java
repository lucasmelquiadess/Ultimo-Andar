package br.com.ultimoandar.contracts.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formatters {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatters() {
    }

    public static String money(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(value == null ? BigDecimal.ZERO : value);
    }

    public static String date(LocalDate value) {
        return value == null ? "não aplicável" : DATE.format(value);
    }

    public static String blank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
