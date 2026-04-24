package com.amar.fullstack.expanse_tracker_backend.config;


import com.razorpay.RazorpayClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Bean
    public RazorpayClient razorpayClient() throws Exception{
        return new RazorpayClient(keyId,keySecret);

    }
    @PostConstruct
    public void testKeys() {
        System.out.println("KEY_ID = " +keyId);
        System.out.println("KEY_SECRET = " +keySecret);
    }
}
