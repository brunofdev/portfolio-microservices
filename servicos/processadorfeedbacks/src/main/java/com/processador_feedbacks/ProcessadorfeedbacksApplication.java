package com.processador_feedbacks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProcessadorfeedbacksApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProcessadorfeedbacksApplication.class, args);
	}

}
