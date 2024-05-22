package com.example.StudentRegisterSubject.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
	private String code;
	private String firstname;
	private String lastname;
	private Date dob;
	private String email;
	private String phoneNumber;
	private String des;
}
