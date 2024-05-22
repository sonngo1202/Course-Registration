package com.example.StudentRegisterSubject.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openapitools.api.TaskApi;
import org.openapitools.model.Process;
import org.openapitools.model.StudentRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.StudentRegisterSubject.service.StudentRegistrationService;

@RestController
@CrossOrigin
public class StudentRegistrationController implements TaskApi{
	@Autowired
	private StudentRegistrationService studentRegistrationService;

	@Override
	public ResponseEntity<Void> delete(String id) {
		if(studentRegistrationService.delete(id)) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
	
	
	@Override
	public ResponseEntity<SseEmitter> getDetail(String id) {
	    List<Process> listProcess = studentRegistrationService.fetchData(id);
	    if(listProcess == null) {
	        return ResponseEntity.notFound().build();
	    }
	    SseEmitter sseEmitter = new SseEmitter(0L);
	    Thread thread = new Thread(() -> {
	        try {
	            while (true) {
	                List<Process> data = studentRegistrationService.fetchData(id);
	                if (data != null && !data.isEmpty()) {
	                    sseEmitter.send(data);
	                } else {
	                    sseEmitter.complete();
	                    break;
	                }

	                TimeUnit.SECONDS.sleep(1);
	            }
	        } catch (IOException | InterruptedException e) {
	            sseEmitter.completeWithError(e);
	        }
	    });
	    thread.start();
	    return ResponseEntity.ok(sseEmitter);
	}


	@Override
	public ResponseEntity<String> start(StudentRegistration studentRegistration) {
		if(!studentRegistrationService.start(studentRegistration)) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok().build();
	}
	
}
