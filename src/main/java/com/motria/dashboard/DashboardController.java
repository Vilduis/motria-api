package com.motria.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public AdminDashboardResponse getAdminDashboard() {
        return dashboardService.adminDashboard();
    }

    @GetMapping("/tecnico/{technicalId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICAL')")
    public TechnicalDashboardResponse getTechnicalDashboard(@PathVariable Long technicalId) {
        return dashboardService.technicalDashboard(technicalId);
    }
}
