package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.AddendumDto;
import br.com.ultimoandar.contracts.dto.AddendumRequest;
import br.com.ultimoandar.contracts.service.AddendumService;
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
@RequestMapping("/api/addendums")
public class AddendumController {

    private final AddendumService service;

    public AddendumController(AddendumService service) {
        this.service = service;
    }

    @GetMapping
    public List<AddendumDto> list(@RequestParam(required = false) UUID contractId) {
        return service.list(contractId);
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public AddendumDto generate(@Valid @RequestBody AddendumRequest request) {
        return service.generate(request);
    }
}
