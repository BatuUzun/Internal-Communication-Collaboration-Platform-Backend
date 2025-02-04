package com.verification_code_manager.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/*
 * 
 * @GetMapping("/send-verification-code/")
	private String sendVerificationCode(@RequestParam String email, @RequestParam String code) {
		return emailService.generateVerificationCode(email, code);
	}
 * 
 * */

@FeignClient(name = "emailsender")
public interface EmailSenderProxy {
	
	@GetMapping("/emailsender/send-verification-code/")
	String sendVerificationCode(@RequestParam String email, @RequestParam String code);
	
	@GetMapping("/emailsender/send-change-password-code/")
	String sendChangePasswordCode(@RequestParam String email, @RequestParam String code);
    
}