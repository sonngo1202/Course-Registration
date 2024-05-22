package com.example.Registration.controller;

import org.openapitools.api.RegistrationApi;
import org.openapitools.model.Registration;
import org.openapitools.model.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.Registration.dao.RegistrationDAO;

@RestController
@CrossOrigin
public class RegistrationController implements RegistrationApi{
	@Autowired
	private RegistrationDAO registrationDAO;

	@Override
	public ResponseEntity<Registration> getDetail(String studentCode) {
		Registration registration = registrationDAO.getRegistration(studentCode);
		if(registration == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(registration);
	}

	@Override
	public ResponseEntity<String> register(RegistrationDTO registrationDTO) {
		if(registrationDAO.saveRegistration(registrationDTO)) {
			return ResponseEntity.status(201).build();
		}
		return ResponseEntity.badRequest().build();
	}

	@Override
	public ResponseEntity<String> update(Integer id, RegistrationDTO registrationDTO) {
		if(registrationDAO.updateRegistration(id, registrationDTO)) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.badRequest().build();

	}
	
}
