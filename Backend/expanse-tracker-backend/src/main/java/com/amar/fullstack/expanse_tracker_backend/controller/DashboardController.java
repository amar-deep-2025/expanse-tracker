package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.*;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger =
            LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // 🔥 DEFAULT DASHBOARD (PAGE LOAD)
    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getSummary(Authentication auth) {

        User user = getCurrentUser(auth);

        logger.info("Dashboard summary requested for userId={}", user.getId());

        return ResponseEntity.ok(
                dashboardService.getSummary(user)
        );
    }

    // 🔥 DATE FILTER DASHBOARD
    @GetMapping("/summary-by-date")
    public ResponseEntity<DashboardResponse> getSummaryByDate(
            Authentication auth,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        User user = getCurrentUser(auth);

        logger.info(
                "Dashboard summary by date requested for userId={} from {} to {}",
                user.getId(), start, end
        );

        return ResponseEntity.ok(
                dashboardService.getSummaryByDate(user, start, end)
        );
    }

    @GetMapping("/category-summary")
    public ResponseEntity<List<CategoryDto>> getCategorySummary(
            Authentication auth,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        User user = getCurrentUser(auth);

        logger.info(
                "Category summary requested for userId={} from {} to {}",
                user.getId(), start, end
        );

        return ResponseEntity.ok(
                dashboardService.getCategorySummary(user, start, end)
        );
    }

    // 🔹 MONTHLY DATA (FOR CHARTS)
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyDto>> getMonthly(
            Authentication auth,

            @RequestParam
            @Min(2020)
            @Max(2100)
            int year) {

        User user = getCurrentUser(auth);

        logger.info(
                "Monthly summary requested for userId={} year={}",
                user.getId(), year
        );

        return ResponseEntity.ok(
                dashboardService.getMonthly(user, year)
        );
    }

    // 🔹 RECENT EXPENSES
    @GetMapping("/recent")
    public ResponseEntity<List<RecentExpanseDto>> getRecent(Authentication auth) {

        User user = getCurrentUser(auth);

        logger.info("Recent expenses requested for userId={}", user.getId());

        return ResponseEntity.ok(
                dashboardService.getRecentExpenses(user)
        );
    }

    // 🔹 MONTH COMPARISON
    @GetMapping("/compare")
    public ResponseEntity<ComparisonDto> compareMonth(Authentication auth) {

        User user = getCurrentUser(auth);

        logger.info("Month comparison requested for userId={}", user.getId());

        return ResponseEntity.ok(
                dashboardService.compareCurrentMonth(user.getId())
        );
    }

    // 🔹 TOP CATEGORY
    @GetMapping("/top-category")
    public ResponseEntity<CategoryDto> getTopCategory(Authentication auth) {

        User user = getCurrentUser(auth);

        logger.info("Top category requested for userId={}", user.getId());

        return ResponseEntity.ok(
                dashboardService.getTopCategory(user.getId())
        );
    }

    // 🔐 GET LOGGED-IN USER
    private User getCurrentUser(Authentication auth) {
        return (User) auth.getPrincipal();
    }
}