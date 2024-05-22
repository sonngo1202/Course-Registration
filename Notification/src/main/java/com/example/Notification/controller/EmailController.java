package com.example.Notification.controller;

import org.openapitools.api.NotificationApi;
import org.openapitools.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.Notification.service.EmailService;

@RestController
@CrossOrigin
public class EmailController implements NotificationApi{
	@Autowired
	private EmailService emailService;
	
	public ResponseEntity<String> send(EmailMessage emailMessage){
		try {
			emailService.sendSimpleMessage(emailMessage);
			return ResponseEntity.ok("Email sent successfully");
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
}
