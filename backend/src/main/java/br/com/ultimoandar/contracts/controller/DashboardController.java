package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.DashboardDto;
import br.com.ultimoandar.contracts.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping
    public DashboardDto get() {
        return service.get();
    }
}
