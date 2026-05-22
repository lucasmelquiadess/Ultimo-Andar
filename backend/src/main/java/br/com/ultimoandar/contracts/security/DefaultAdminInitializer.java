package br.com.ultimoandar.contracts.security;

import br.com.ultimoandar.contracts.config.SecurityProperties;
import br.com.ultimoandar.contracts.entity.AppUser;
import br.com.ultimoandar.contracts.entity.enums.UserRole;
import br.com.ultimoandar.contracts.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;
    private final SecurityProperties properties;

    public DefaultAdminInitializer(AppUserRepository repository, PasswordEncoder encoder, SecurityProperties properties) {
        this.repository = repository;
        this.encoder = encoder;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        AppUser user = new AppUser();
        user.setUsername(properties.username());
        user.setDisplayName("Administrador");
        user.setPasswordHash(encoder.encode(properties.password()));
        user.setRole(UserRole.ADMIN);
        user.setActive(true);
        user.setMustChangePassword(true);
        repository.save(user);
    }
}
