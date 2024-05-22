package com.example.StudentRegisterSubject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageRequest {
	private String email;
	private String title;
	private String text;
}
