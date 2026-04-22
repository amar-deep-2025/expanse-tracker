package com.amar.fullstack.expanse_tracker_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpanseTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpanseTrackerBackendApplication.class, args);
	}

}
