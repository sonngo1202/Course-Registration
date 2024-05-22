package com.example.StudentRegisterSubject.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProgressResponse {
	private int id;
	private int total_credits;
	private String grade;
	private float point;
	private List<ProgressDetailResponse> details;
}
