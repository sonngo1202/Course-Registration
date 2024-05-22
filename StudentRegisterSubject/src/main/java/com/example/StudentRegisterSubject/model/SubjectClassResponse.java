package com.example.StudentRegisterSubject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectClassResponse {
	private String code;
    private int maxNumber;
    private int number;
    private boolean isActive;
    private String des;
    private String subjectCode;
}
