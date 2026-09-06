package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.DashboardResponse;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.AiFacadeService;
import com.amar.fullstack.expanse_tracker_backend.service.AiService;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final AiFacadeService aiFacadeService;
    private final DashboardService dashboardService;

    public AiController(
            AiService aiService,
            AiFacadeService aiFacadeService,
            DashboardService dashboardService
    ) {
        this.aiService = aiService;
        this.aiFacadeService = aiFacadeService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/test")
    public String test() {
        return aiService.ask("Explain Expanse Tracker in one short line.");
    }

    @GetMapping("/ask")
    public String ask(
            @RequestParam String prompt,
            Authentication auth
    ) {
        return aiService.ask(prompt);
    }


    @GetMapping("/insight")
    public String generateInsight(Authentication auth) {

        User user = (User) auth.getPrincipal();

        DashboardResponse dashboardData =
                dashboardService.getSummaryByUserId(user.getId());

        return aiFacadeService.generateInsight(dashboardData);
    }

    @GetMapping("/custom")
    public String customQuestion(
            @RequestParam String prompt,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        DashboardResponse dashboardData =
                dashboardService.getSummaryByUserId(user.getId());

        return aiFacadeService.generateCustomAnswer(
                prompt,
                dashboardData
        );
    }
}