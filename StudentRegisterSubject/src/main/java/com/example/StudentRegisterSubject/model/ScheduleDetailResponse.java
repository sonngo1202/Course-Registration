package com.example.StudentRegisterSubject.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetailResponse {
	private Long id;
    private List<Integer> weeks;
    private int sessionStart;
    private int sessionEnd;
    private String dayOfWeek;
    private String des;
}
