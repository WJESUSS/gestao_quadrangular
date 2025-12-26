package com.igreja.GestaoQuadrangular.infrastructure.security;

import com.igreja.GestaoQuadrangular.servicce.LeaderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {

    private final LeaderService leaderService;

    public SecurityService(LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    public boolean isLeaderOfCell(Authentication authentication, Long celulaId) {
        String username = authentication.getName();
        return leaderService.isLeaderOfCell(username, celulaId);
    }
}