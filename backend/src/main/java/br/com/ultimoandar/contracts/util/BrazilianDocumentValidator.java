package br.com.ultimoandar.contracts.util;

public final class BrazilianDocumentValidator {

    private BrazilianDocumentValidator() {
    }

    public static boolean isCpfOrCnpj(String value) {
        String digits = onlyDigits(value);
        return isCpf(digits) || isCnpj(digits);
    }

    public static String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private static boolean isCpf(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        int first = digit(cpf, 9, 10);
        int second = digit(cpf, 10, 11);
        return first == cpf.charAt(9) - '0' && second == cpf.charAt(10) - '0';
    }

    private static int digit(String value, int length, int weight) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (value.charAt(i) - '0') * (weight - i);
        }
        int result = 11 - (sum % 11);
        return result >= 10 ? 0 : result;
    }

    private static boolean isCnpj(String cnpj) {
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) {
            return false;
        }
        return cnpjDigit(cnpj, 12) == cnpj.charAt(12) - '0'
                && cnpjDigit(cnpj, 13) == cnpj.charAt(13) - '0';
    }

    private static int cnpjDigit(String cnpj, int length) {
        int[] weights = length == 12
                ? new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
                : new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }
        int result = sum % 11;
        return result < 2 ? 0 : 11 - result;
    }
}
