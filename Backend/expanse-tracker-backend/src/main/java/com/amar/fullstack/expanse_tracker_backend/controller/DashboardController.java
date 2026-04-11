package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(Authentication auth) {

        logger.info("Dashboard summary API called");
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(dashboardService.getSummary(user));
    }

    @GetMapping("/summary/date")
    public ResponseEntity<?> getSummaryByDate(
            Authentication auth,
            @RequestParam String start,
            @RequestParam String end) {
        logger.info("Dashboard summary by date API called");
        User user = (User) auth.getPrincipal();
        logger.debug("Date range: {} to {}", start, end);

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
        logger.info("Dashboard category summary API called");
        User user = (User) auth.getPrincipal();
        logger.debug("Category date range: {} to {}", start, end);
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

        logger.info("Dashboard monthly API called for year: {}", year);

        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(
                dashboardService.getMonthly(user, year)
        );
    }
    @GetMapping("/recent")
    public ResponseEntity<?> getRecent(Authentication auth) {

        logger.info("Dashboard recent expenses API called");

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                dashboardService.getRecentExpenses(user)
        );
    }
}