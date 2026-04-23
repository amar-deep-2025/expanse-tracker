package com.amar.fullstack.expanse_tracker_backend;

import com.amar.fullstack.expanse_tracker_backend.notification.strategy.EmailNotificationStrategy;
import com.amar.fullstack.expanse_tracker_backend.notification.strategy.SmsNotificationStrategy;
import com.amar.fullstack.expanse_tracker_backend.service.BudgetService;
import com.amar.fullstack.expanse_tracker_backend.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExpanseTrackerBackendApplicationTests {

	@Test
	void contextLoads() {
	}
}