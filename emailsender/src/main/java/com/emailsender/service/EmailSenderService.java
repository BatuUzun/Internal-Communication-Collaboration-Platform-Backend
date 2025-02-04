package com.emailsender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderService {
	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String SEND_EMAIL_FROM;

	@Async
	public String verificationCodeEmailSender(String email, String code) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = null;

		final String VERIFY_EMAIL_TEXT = "Hello," + "\nThank you for signing up with emailsender."
				+ " To complete the registration process, please use the following verification code:"
				+ "\nVerification Code: " + code + "\n\nEmailsender Team.";

		final String VERIFY_EMAIL_SUBJECT = "Verify your email";

		prepareEmailContent(mimeMessageHelper, mimeMessage, email, VERIFY_EMAIL_TEXT, VERIFY_EMAIL_SUBJECT);
		javaMailSender.send(mimeMessage);

		return code;
	}

	@Async
	public String changePasswordCodeEmailSender(String email, String code) {

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = null;

		final String EMAIL_SUBJECT = "Reset Your Emailsender Password";
		final String EMAIL_BODY = "Dear User,\n\n"
				+ "We received a request to reset the password for your Emailsender account. "
				+ "Please use the verification code below to complete the password reset process:\n\n"
				+ "Verification Code: " + code + "\n\n" + "Best regards,\n" + "The Emailsender Team";

		prepareEmailContent(mimeMessageHelper, mimeMessage, email, EMAIL_BODY, EMAIL_SUBJECT);
		javaMailSender.send(mimeMessage);

		return code;
	}

	public String generateVerificationCode(String email, String code) {
		verificationCodeEmailSender(email, code);
		return code;
	}

	public String generateChangePasswordCode(String email, String code) {
		changePasswordCodeEmailSender(email, code);
		return code;
	}

	private void prepareEmailContent(MimeMessageHelper mimeMessageHelper, MimeMessage mimeMessage, String email,
			String VERIFY_EMAIL_TEXT, String VERIFY_EMAIL_SUBJECT) {

		try {
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(SEND_EMAIL_FROM);
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setText(VERIFY_EMAIL_TEXT);
			mimeMessageHelper.setSubject(VERIFY_EMAIL_SUBJECT);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

}