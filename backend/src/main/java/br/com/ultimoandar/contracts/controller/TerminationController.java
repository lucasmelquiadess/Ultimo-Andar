package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.TerminationDto;
import br.com.ultimoandar.contracts.dto.TerminationRequest;
import br.com.ultimoandar.contracts.service.TerminationService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/terminations")
public class TerminationController {

    private final TerminationService service;

    public TerminationController(TerminationService service) {
        this.service = service;
    }

    @GetMapping
    public List<TerminationDto> list(@RequestParam(required = false) UUID contractId) {
        return service.list(contractId);
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public TerminationDto generate(@Valid @RequestBody TerminationRequest request) {
        return service.generate(request);
    }
}
