package com.verification_code_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VerificationCodeManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerificationCodeManagerApplication.class, args);
	}

}
