package com.amar.fullstack.expanse_tracker_backend.controller;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // ✅ Summary
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(dashboardService.getSummary(user));
    }

    @GetMapping("/summary/date")
    public ResponseEntity<?> getSummaryByDate(
            Authentication auth,
            @RequestParam String start,
            @RequestParam String end) {

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                dashboardService.getSummaryByDate(
                        user,
                        LocalDateTime.parse(start),
                        LocalDateTime.parse(end)
                )
        );
    }
    @GetMapping("/category")
    public ResponseEntity<?> getCategory(
            Authentication auth,
            @RequestParam String start,
            @RequestParam String end) {

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                dashboardService.getCategorySummary(
                        user,
                        LocalDateTime.parse(start),
                        LocalDateTime.parse(end)
                )
        );
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthly(
            Authentication auth,
            @RequestParam int year) {

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                dashboardService.getMonthly(user, year)
        );
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecent(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(
                dashboardService.getRecentExpenses(user)
        );
    }
}