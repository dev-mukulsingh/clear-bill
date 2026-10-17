package com.clearbillsolutions.controller;

import com.clearbillsolutions.dto.DashboardChartsDTO;
import com.clearbillsolutions.dto.DashboardStatsDTO;
import com.clearbillsolutions.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/charts")
    public ResponseEntity<DashboardChartsDTO> getDashboardCharts() {
        return ResponseEntity.ok(dashboardService.getDashboardCharts());
    }
}
