package com.kieutrinh.project.covid19invietnam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Covid19InVietnamApplication {

	public static void main(String[] args) {
		SpringApplication.run(Covid19InVietnamApplication.class, args);

	}

}
