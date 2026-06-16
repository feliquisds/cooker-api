package com.expsn.cooker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class CookerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookerApplication.class, args);
	}
}
