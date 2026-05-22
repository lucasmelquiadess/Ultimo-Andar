package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.exception.BusinessException;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {

    public void validate(String password, String username) {
        if (password == null || password.length() < 10) {
            throw new BusinessException("A senha deve ter pelo menos 10 caracteres.");
        }
        if (password.length() > 120) {
            throw new BusinessException("A senha deve ter no máximo 120 caracteres.");
        }
        if (username != null && !username.isBlank() && password.toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT))) {
            throw new BusinessException("A senha não deve conter o nome de usuário.");
        }
        if (!password.matches(".*[A-ZÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ].*")) {
            throw new BusinessException("A senha deve conter ao menos uma letra maiúscula.");
        }
        if (!password.matches(".*[a-záàâãéèêíïóôõöúçñ].*")) {
            throw new BusinessException("A senha deve conter ao menos uma letra minúscula.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException("A senha deve conter ao menos um número.");
        }
        if (!password.matches(".*[^A-Za-z0-9ÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑáàâãéèêíïóôõöúçñ].*")) {
            throw new BusinessException("A senha deve conter ao menos um símbolo.");
        }
    }
}
