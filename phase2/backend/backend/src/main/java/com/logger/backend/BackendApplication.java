package com.logger.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		System.out.println("🔥 MAIN CLASS STARTED");
		SpringApplication.run(BackendApplication.class, args);
	}
}