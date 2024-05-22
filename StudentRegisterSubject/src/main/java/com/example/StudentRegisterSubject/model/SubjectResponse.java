package com.example.StudentRegisterSubject.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponse {
	private String code;
    private String name;
    private List<SubjectResponse> prerequisites;
    private int credits;
    private boolean isActive;
    private String des;
}
