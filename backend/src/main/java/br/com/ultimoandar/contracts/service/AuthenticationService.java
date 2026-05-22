package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.config.SecurityProperties;
import br.com.ultimoandar.contracts.dto.ChangePasswordRequest;
import br.com.ultimoandar.contracts.dto.CurrentUserDto;
import br.com.ultimoandar.contracts.dto.LoginRequest;
import br.com.ultimoandar.contracts.dto.LoginResponse;
import br.com.ultimoandar.contracts.entity.AppUser;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.repository.AppUserRepository;
import br.com.ultimoandar.contracts.security.JwtService;
import java.time.Instant;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final SecurityProperties properties;
    private final AuditService auditService;
    private final PasswordPolicy passwordPolicy;

    public AuthenticationService(
            AppUserRepository repository,
            PasswordEncoder encoder,
            JwtService jwtService,
            SecurityProperties properties,
            AuditService auditService,
            PasswordPolicy passwordPolicy
    ) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.properties = properties;
        this.auditService = auditService;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional(noRollbackFor = BusinessException.class)
    public LoginResponse login(LoginRequest request) {
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        AppUser user = repository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        if (!user.isActive()) {
            throw new BusinessException("Usuário inativo.");
        }
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new BusinessException("Usuário temporariamente bloqueado. Tente novamente mais tarde.");
        }
        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            registerFailedLogin(user);
            auditService.recordAs(username, "LOGIN_FAILED", "AUTH", user.getId(), "Falha de autenticação");
            throw new BusinessException("Credenciais inválidas.");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        repository.save(user);
        JwtService.TokenIssue token = jwtService.issue(user.getUsername());
        auditService.recordAs(user.getUsername(), "LOGIN_SUCCESS", "AUTH", user.getId(), "Login realizado");
        return new LoginResponse(token.token(), token.expiresAt(), toCurrentUser(user));
    }

    @Transactional(readOnly = true)
    public CurrentUserDto current(Authentication authentication) {
        AppUser user = repository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));
        return toCurrentUser(user);
    }

    @Transactional
    public CurrentUserDto changePassword(Authentication authentication, ChangePasswordRequest request) {
        AppUser user = repository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));
        if (!encoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Senha atual inválida.");
        }
        passwordPolicy.validate(request.newPassword(), user.getUsername());
        user.setPasswordHash(encoder.encode(request.newPassword()));
        user.setMustChangePassword(false);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        AppUser saved = repository.save(user);
        auditService.record("PASSWORD_CHANGED", "USER", saved.getId(), "Senha alterada");
        return toCurrentUser(saved);
    }

    private void registerFailedLogin(AppUser user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= properties.maxLoginAttempts()) {
            user.setLockedUntil(Instant.now().plusSeconds(properties.lockMinutes() * 60));
            user.setFailedLoginAttempts(0);
        }
        repository.save(user);
    }

    private CurrentUserDto toCurrentUser(AppUser user) {
        return new CurrentUserDto(user.getUsername(), user.getDisplayName(), user.getRole(), user.isMustChangePassword());
    }
}
