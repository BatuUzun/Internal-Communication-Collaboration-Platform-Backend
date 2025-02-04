package com.emailsender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emailsender.service.EmailSenderService;

@RestController
@RequestMapping("/emailsender")
public class EmailsenderController {

	@Autowired
	private EmailSenderService emailService;

	@GetMapping("/send-verification-code/")
	private String sendVerificationCode(@RequestParam String email, @RequestParam String code) {
		return emailService.generateVerificationCode(email, code);
	}

	@GetMapping("/send-change-password-code/")
	private String sendChangePasswordCode(@RequestParam String email, @RequestParam String code) {
		return emailService.generateChangePasswordCode(email, code);
	}

}