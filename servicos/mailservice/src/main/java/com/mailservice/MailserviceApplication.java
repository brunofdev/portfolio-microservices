package com.mailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MailserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailserviceApplication.class, args);
	}

}
