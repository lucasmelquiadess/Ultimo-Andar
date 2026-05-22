package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.ResetPasswordRequest;
import br.com.ultimoandar.contracts.dto.UserDto;
import br.com.ultimoandar.contracts.dto.UserRequest;
import br.com.ultimoandar.contracts.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserRequest request) {
        return service.create(request);
    }

    @DeleteMapping("/{id}")
    public UserDto deactivate(@PathVariable UUID id) {
        return service.deactivate(id);
    }

    @PostMapping("/{id}/reset-password")
    public UserDto resetPassword(@PathVariable UUID id, @Valid @RequestBody ResetPasswordRequest request) {
        return service.resetPassword(id, request);
    }
}
