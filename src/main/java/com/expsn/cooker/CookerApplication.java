package com.expsn.cooker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CookerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookerApplication.class, args);
	}
}
