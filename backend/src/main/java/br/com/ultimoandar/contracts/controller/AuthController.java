package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.ChangePasswordRequest;
import br.com.ultimoandar.contracts.dto.CurrentUserDto;
import br.com.ultimoandar.contracts.dto.LoginRequest;
import br.com.ultimoandar.contracts.dto.LoginResponse;
import br.com.ultimoandar.contracts.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService service;

    public AuthController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/me")
    public CurrentUserDto me(Authentication authentication) {
        return service.current(authentication);
    }

    @PostMapping("/change-password")
    public CurrentUserDto changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        return service.changePassword(authentication, request);
    }
}
