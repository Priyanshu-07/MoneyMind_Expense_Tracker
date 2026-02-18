package com.moneymind.Backend.controller;

import com.moneymind.Backend.dto.DashboardResponse;
import com.moneymind.Backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "") String month) {
        if (month.isEmpty()) {
            month = java.time.YearMonth.now().toString();
        }
        return ResponseEntity.ok(dashboardService.getDashboard(user.getUsername(), month));
    }
}