package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.OwnerDto;
import br.com.ultimoandar.contracts.service.OwnerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService service;

    public OwnerController(OwnerService service) {
        this.service = service;
    }

    @GetMapping
    public List<OwnerDto> list(@RequestParam(required = false) String search, @RequestParam(required = false) Boolean active) {
        return service.list(search, active);
    }

    @GetMapping("/{id}")
    public OwnerDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerDto create(@Valid @RequestBody OwnerDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public OwnerDto update(@PathVariable UUID id, @Valid @RequestBody OwnerDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        service.deactivate(id);
    }
}
