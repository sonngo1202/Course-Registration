package com.example.Student.controller;

import java.util.List;

import org.openapitools.api.StudentApi;
import org.openapitools.model.Progress;
import org.openapitools.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.example.Student.dao.ProgressDAO;
import com.example.Student.dao.StudentDAO;

@RestController
@CrossOrigin
public class StudentController implements StudentApi{
	@Autowired
	private StudentDAO studentDAO;
	@Autowired
	private ProgressDAO progressDAO;
	
	@Override
	public ResponseEntity<Student> getDetail(String id){
		Student student = studentDAO.getDetail(id);
		if(student != null) {
			return ResponseEntity.ok(student);
		}
		return ResponseEntity.notFound().build();
	}
	
	
	@Override
	public ResponseEntity<List<Student>> getAll() {
		return ResponseEntity.ok(studentDAO.getAll());
	}


	@Override
    public ResponseEntity<Progress> getProgress(String id) {
		if(!studentDAO.existById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(progressDAO.getProgress(id));
    }
}
