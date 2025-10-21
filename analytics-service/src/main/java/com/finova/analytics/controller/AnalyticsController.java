package com.finova.analytics.controller;

import com.finova.analytics.dto.AnalyticsDashboardDTO;
import com.finova.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<AnalyticsDashboardDTO> getDashboard(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "12m") String period) {
        log.info("GET /api/analytics/dashboard/{} with period: {}", userId, period);
        AnalyticsDashboardDTO dashboard = analyticsService.getDashboard(userId, period);
        return ResponseEntity.ok(dashboard);
    }
}

