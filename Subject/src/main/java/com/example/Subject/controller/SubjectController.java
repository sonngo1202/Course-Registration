package com.example.Subject.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.openapitools.api.SubjectApi;
import org.openapitools.model.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.Subject.dao.SubjectDAO;

@RestController
@CrossOrigin
public class SubjectController implements SubjectApi{
	@Autowired
	private SubjectDAO subjectDAO;

	@Override
	public ResponseEntity<Subject> getDetail(String id) {
		Optional<Subject> subjectOpt = subjectDAO.findById(id);
    	if(subjectOpt.isEmpty()) {
    		return ResponseEntity.notFound().build();
    	}
        return ResponseEntity.ok(subjectOpt.get());
	}

	@Override
	public ResponseEntity<List<Subject>> getAll() {
		return ResponseEntity.ok(StreamSupport.stream(subjectDAO.findAll().spliterator(), false).collect(Collectors.toList()));
	}
	
}
