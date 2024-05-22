package com.example.StudentRegisterSubject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressDetailResponse {
	private float point;
	private String grade;
	private String subjectCode;
	private boolean status;
}
