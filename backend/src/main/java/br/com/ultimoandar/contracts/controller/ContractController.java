package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.ContractDto;
import br.com.ultimoandar.contracts.dto.ContractRequest;
import br.com.ultimoandar.contracts.entity.enums.ContractStatus;
import br.com.ultimoandar.contracts.service.ContractService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService service;

    public ContractController(ContractService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContractDto> list(@RequestParam(required = false) String search, @RequestParam(required = false) ContractStatus status) {
        return service.list(search, status);
    }

    @GetMapping("/{id}")
    public ContractDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public ContractDto generate(@Valid @RequestBody ContractRequest request) {
        return service.generate(request);
    }

    @PostMapping("/{id}/reissue")
    public ContractDto reissue(@PathVariable UUID id) {
        return service.reissue(id);
    }

    @PatchMapping("/{id}/status")
    public ContractDto updateStatus(@PathVariable UUID id, @RequestParam ContractStatus status) {
        return service.updateStatus(id, status);
    }
}
