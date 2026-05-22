package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.dto.ResetPasswordRequest;
import br.com.ultimoandar.contracts.dto.UserDto;
import br.com.ultimoandar.contracts.dto.UserRequest;
import br.com.ultimoandar.contracts.entity.AppUser;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.exception.ResourceNotFoundException;
import br.com.ultimoandar.contracts.repository.AppUserRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;
    private final AuditService auditService;
    private final PasswordPolicy passwordPolicy;

    public UserService(AppUserRepository repository, PasswordEncoder encoder, AuditService auditService, PasswordPolicy passwordPolicy) {
        this.repository = repository;
        this.encoder = encoder;
        this.auditService = auditService;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional(readOnly = true)
    public List<UserDto> list() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    @Transactional
    public UserDto create(UserRequest request) {
        String username = normalizeUsername(request.username());
        if (repository.existsByUsernameIgnoreCase(username)) {
            throw new BusinessException("Já existe um usuário com este login.");
        }
        passwordPolicy.validate(request.password(), username);

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(request.active() == null || request.active());
        user.setMustChangePassword(true);
        AppUser saved = repository.save(user);
        auditService.record("USER_CREATED", "USER", saved.getId(), "Usuário " + saved.getUsername());
        return toDto(saved);
    }

    @Transactional
    public UserDto deactivate(UUID id) {
        AppUser user = getEntity(id);
        if (user.getUsername().equalsIgnoreCase(auditService.currentActor())) {
            throw new BusinessException("Não é possível inativar o próprio usuário logado.");
        }
        user.setActive(false);
        AppUser saved = repository.save(user);
        auditService.record("USER_DEACTIVATED", "USER", saved.getId(), "Usuário " + saved.getUsername());
        return toDto(saved);
    }

    @Transactional
    public UserDto resetPassword(UUID id, ResetPasswordRequest request) {
        AppUser user = getEntity(id);
        if (!user.isActive()) {
            throw new BusinessException("Não é possível resetar a senha de um usuário inativo.");
        }
        passwordPolicy.validate(request.newPassword(), user.getUsername());
        user.setPasswordHash(encoder.encode(request.newPassword()));
        user.setMustChangePassword(true);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        AppUser saved = repository.save(user);
        auditService.record("USER_PASSWORD_RESET", "USER", saved.getId(), "Senha temporária gerada para " + saved.getUsername());
        return toDto(saved);
    }

    private AppUser getEntity(UUID id) {
        return repository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private UserDto toDto(AppUser user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                user.isActive(),
                user.isMustChangePassword(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
