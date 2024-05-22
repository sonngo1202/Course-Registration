package com.example.StudentRegisterSubject.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
	private String subjectClassCode;
	private List<ScheduleDetailResponse> details;
}
