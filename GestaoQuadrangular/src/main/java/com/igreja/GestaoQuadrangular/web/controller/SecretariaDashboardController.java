package com.igreja.GestaoQuadrangular.web.controller;


import com.igreja.GestaoQuadrangular.application.dto.DashboardSecretariaDTO;
import com.igreja.GestaoQuadrangular.servicce.SecretariaDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secretaria")
@PreAuthorize("hasRole('SECRETARIA') or hasRole('PASTOR')")
public class SecretariaDashboardController {

    private final SecretariaDashboardService secretariaDashboardService;

    public SecretariaDashboardController(SecretariaDashboardService secretariaDashboardService) {
        this.secretariaDashboardService = secretariaDashboardService;
    }


    @GetMapping("/dashboard")
    public DashboardSecretariaDTO getDashboard() {
        return secretariaDashboardService.getDashboardData();
    }
}