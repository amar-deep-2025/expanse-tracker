package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.*;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
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

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary(
            Authentication auth) {

        User user = getCurrentUser(auth);

        logger.info("Dashboard summary requested for userId={}",
                user.getId());

        return ResponseEntity.ok(
                dashboardService.getSummary(user)
        );
    }

    @GetMapping("/summary/date")
    public ResponseEntity<DashboardSummaryDto> getSummaryByDate(
            Authentication auth,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        User user = getCurrentUser(auth);

        logger.info(
                "Dashboard summary by date requested for userId={} from {} to {}",
                user.getId(), start, end
        );

        return ResponseEntity.ok(
                dashboardService.getSummaryByDate(
                        user, start, end
                )
        );
    }

    @GetMapping("/category")
    public ResponseEntity<List<CategoryDto>> getCategory(
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
                dashboardService.getCategorySummary(
                        user, start, end
                )
        );
    }
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

    @GetMapping("/recent")
    public List<RecentExpanseDto> getRecent(Authentication auth) {
        System.out.println("hello");
        User user=getCurrentUser(auth);
        logger.info("Entered in Recent expenses userId={}", user.getId());
        return dashboardService.getRecentExpenses(user);
    }

    @GetMapping("/compare-month")
    public ResponseEntity<ComparisonDto> compareMonth(
            Authentication auth) {

        User user = getCurrentUser(auth);

        logger.info(
                "Month comparison requested for userId={}",
                user.getId()
        );

        return ResponseEntity.ok(
                dashboardService.compareCurrentMonth(
                        user.getId()
                )
        );
    }

    @GetMapping("/top-category")
    public ResponseEntity<CategoryDto> getTopCategory(
            Authentication auth) {

        User user = getCurrentUser(auth);

        logger.info(
                "Top category requested for userId={}",
                user.getId()
        );
        return ResponseEntity.ok(
                dashboardService.getTopCategory(
                        user.getId()
                )
        );
    }
    private User getCurrentUser(Authentication auth) {
        return (User) auth.getPrincipal();
    }
}