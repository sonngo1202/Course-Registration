package com.example.StudentRegisterSubject.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationRequest {
	private String note;
	private String studentCode;
	private List<RegistrationDetailRequest> details;
}
