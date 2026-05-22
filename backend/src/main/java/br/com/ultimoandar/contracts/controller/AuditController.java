package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.AuditEventDto;
import br.com.ultimoandar.contracts.service.AuditService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditEventDto> latest(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resourceType
    ) {
        return service.latest(limit, actor, action, resourceType);
    }
}
