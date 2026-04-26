package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.PaymentVerifyRequest;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public String createOrder(@RequestParam int amount, Authentication auth) throws Exception {

        User user = (User) auth.getPrincipal();
        return paymentService.createOrder(amount, user);
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody @Valid PaymentVerifyRequest request,
            Authentication auth) throws Exception {

        User user = (User) auth.getPrincipal();
        return paymentService.verifyPayment(request, user);
    }
}