package com.mis.eoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
public class MisSecretApplication1 {

	public static void main(String[] args) {
		SpringApplication.run(MisSecretApplication1.class, args);
	}

}
