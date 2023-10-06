package com.wmlongandassociates.automail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class AutomatedMailerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomatedMailerApplication.class, args);
	}

}
