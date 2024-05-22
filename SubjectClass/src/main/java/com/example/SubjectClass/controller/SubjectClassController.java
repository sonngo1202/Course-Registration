package com.example.SubjectClass.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.openapitools.api.SubjectClassApi;
import org.openapitools.model.Schedule;
import org.openapitools.model.SubjectClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.SubjectClass.dao.ScheduleDAO;
import com.example.SubjectClass.dao.SubjectClassDAO;

@RestController
@CrossOrigin
public class SubjectClassController implements SubjectClassApi{
	@Autowired
	private SubjectClassDAO subjectClassDAO;
	@Autowired
	private ScheduleDAO scheduleDAO;
	
	@Override
	public ResponseEntity<SubjectClass> getDetail(String id) {
		Optional<SubjectClass> subjectClassOpt = subjectClassDAO.findById(id);
        if (subjectClassOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subjectClassOpt.get());
	}
	@Override
	public ResponseEntity<List<Schedule>> getSchedule(String id) {
		if (!subjectClassDAO.existById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(scheduleDAO.findAllBySubjectClassCode(id));
	}
	@Override
	public ResponseEntity<String> updateNumber(String id, String action) {
		if (subjectClassDAO.updateNumber(id, action)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
	}
	@Override
	public ResponseEntity<List<SubjectClass>> getAll() {
		return ResponseEntity.ok(StreamSupport.stream(subjectClassDAO.findAll().spliterator(), false).collect(Collectors.toList()));
	}
	
}
