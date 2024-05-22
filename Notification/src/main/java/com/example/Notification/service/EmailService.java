package com.example.Notification.service;

import org.openapitools.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender emailSender;
	
	public void sendSimpleMessage(EmailMessage emailMessage) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(emailMessage.getEmail());
		message.setSubject(emailMessage.getTitle());
		message.setText(emailMessage.getText());
		emailSender.send(message);
	}
}