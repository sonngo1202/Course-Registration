package com.example.StudentRegisterSubject.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.openapitools.model.Process;
import org.openapitools.model.StudentRegistration;
import org.openapitools.model.StudentRegistrationDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.StudentRegisterSubject.model.EmailMessageRequest;
import com.example.StudentRegisterSubject.model.ProgressDetailResponse;
import com.example.StudentRegisterSubject.model.RegistrationDetailRequest;
import com.example.StudentRegisterSubject.model.RegistrationRequest;
import com.example.StudentRegisterSubject.model.ScheduleDetailResponse;
import com.example.StudentRegisterSubject.model.ScheduleResponse;
import com.example.StudentRegisterSubject.model.StudentProgressResponse;
import com.example.StudentRegisterSubject.model.StudentResponse;
import com.example.StudentRegisterSubject.model.SubjectResponse;

@Service
public class StudentRegistrationService {
	@Autowired
    private RestTemplate restTemplate;

    @Value("${student.url}")
    private String studentUrl;

    @Value("${subject.url}")
    private String subjectUrl;

    @Value("${subjectclass.url}")
    private String subjectclassUrl;

    @Value("${registration.url}")
    private String registrationUrl;

    @Value("${notification.url}")
    private String notificationUrl;
    
    private Map<String, Stack<List<Process>>> dataStacks = new HashMap<>(); 
    
    private Map<String, Boolean> checkStops = new HashMap<>();
    
    private Map<String, List<String>> subjectClassRegistrations = new HashMap<>();
    
    private String[] listNameProcess = {"Bắt đầu", "Nhận thông tin sinh viên",
    		"Nhận thông tin các môn học", "Xác minh số tín chỉ", "Nhận thông tin tiến trình học của sinh viên",
    		"Xác minh điều kiện tiên quyết", "Nhận thông tin lịch của lớp môn học",
    		"Xác minh trùng lịch học", "Xác minh sĩ số lớp học", "Ghi nhận đơn đăng ký"};
    
    private final int timeSleep = 5;
    
    public List<Process> fetchData(String id) {
    	Stack<List<Process>> stack = dataStacks.get(id);
    	if(stack == null || stack.isEmpty()) {
    		return null;
    	}
    	return stack.peek();
    }
    
    public boolean start(StudentRegistration studentRegistration) {
    	StringBuilder message = new StringBuilder();
    	checkStops.put(studentRegistration.getStudentCode(), false);
		
		Stack<List<Process>> data = new Stack<>();
		dataStacks.put(studentRegistration.getStudentCode(), data);
		List<Process> listProcess0 = generateProcess(0);
		data.push(listProcess0);
		try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Get Detail Student
		StudentResponse student =  callStudentService(studentRegistration.getStudentCode());
		if(student == null) {
			 dataStacks.remove(studentRegistration.getStudentCode());
			 checkStops.remove(studentRegistration.getStudentCode());
             return false;
		}
		List<Process> listProcess1 = generateProcess(1);
		data.push(listProcess1);
		try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
		
		//Get Detail Subject
        List<SubjectResponse> listSubject = callSubjectService(studentRegistration.getDetails());
        if(listSubject == null || listSubject.isEmpty()) {
        	dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess2 = generateProcess(2);
		data.push(listProcess2);
        try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
        
        //Verify credits
        if (!verifyCredits(listSubject, message)) {
            callNotification(new EmailMessageRequest(student.getEmail(), "Đăng ký học", message.toString()));
            dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess3 = generateProcess(3);
		data.push(listProcess3);
        try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
        
        //Get Progress of student
        StudentProgressResponse studentProgress = callStudentServiceToGetProgress(studentRegistration.getStudentCode());
        if(studentProgress == null) {
        	dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess4 = generateProcess(4);
		data.push(listProcess4);
        try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
        
        //Verify prerequisites
        if (!verifyPrerequisites(listSubject, studentProgress, message)) {
            callNotification(new EmailMessageRequest(student.getEmail(), "Đăng ký học", message.toString()));
            dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess5 = generateProcess(5);
		data.push(listProcess5);
        try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
        
        //Get Schedule of subject class
        List<ScheduleResponse> listScheduleClass = callSubjectClassServiceToGetSchedule(studentRegistration.getDetails());
        if(listScheduleClass == null) {
        	dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess6 = generateProcess(6);
		data.push(listProcess6);
        try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
        
        //Verify schedule
        if (!verifyDuplicateSchedule(listScheduleClass, message)) {
        	callNotification(new EmailMessageRequest(student.getEmail(), "Đăng ký học", message.toString()));
        	dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess7 = generateProcess(7);
		data.push(listProcess7);
        try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
        
        //Verify number
        if (!verifyNumber(studentRegistration, message)) {
            callNotification(new EmailMessageRequest(student.getEmail(), "Đăng ký học", message.toString()));
            rollBackNumberSubjectClass(studentRegistration.getStudentCode());
            dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess8 = generateProcess(8);
		data.push(listProcess8);
        try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if(checkStops.get(studentRegistration.getStudentCode())) {
			dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
		}
        
        //Register
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setStudentCode(studentRegistration.getStudentCode());
        List<RegistrationDetailRequest> details = new ArrayList<>();
        studentRegistration.getDetails().forEach(subjectclass -> details.add(new RegistrationDetailRequest(subjectclass.getSubjectClassCode())));
        registrationRequest.setDetails(details);
        if (!callRegistrationService(registrationRequest, studentRegistration.getId())) {
            message.append("Lỗi không thể đăng ký.");
            callNotification(new EmailMessageRequest(student.getEmail(), "Đăng ký học", message.toString()));
            dataStacks.remove(studentRegistration.getStudentCode());
			checkStops.remove(studentRegistration.getStudentCode());
            return false;
        }
        List<Process> listProcess9 = generateProcess(9);
		data.push(listProcess9);
		message.append("Hoàn thành đăng ký.");
		//Notification
        callNotification(new EmailMessageRequest(student.getEmail(), "Đăng ký học", message.toString()));
		
		dataStacks.remove(studentRegistration.getStudentCode());
        checkStops.remove(studentRegistration.getStudentCode());
    	return true;
    }
    
    public boolean delete(String id) {
    	boolean checkStop = checkStops.get(id);
    	if(!checkStop) {
    		checkStops.put(id, true);
    		Stack<List<Process>> stack = dataStacks.get(id);
    		if(stack != null && !stack.isEmpty()) {
    			List<Process> listProcess = stack.peek();
    			if(listProcess.get(8).getStatus()) {
    				rollBackNumberSubjectClass(id);
    			}
    		}
    		return true;
    	}
    	return false;
    }
    
    public List<Process> generateProcess(int idProcess) {
    	List<Process> listProcesses = new ArrayList<>();
    	for(int i = 0; i < 10; i++) {
    		if(i <= idProcess) {
    			listProcesses.add(new Process(i, listNameProcess[i], true));
    		}else {
    			listProcesses.add(new Process(i, listNameProcess[i], false));
    		}
    	}
    	return listProcesses;
    }
    
    //Call student service
    private StudentResponse callStudentService(String studentCode) {
    	try {
        	return restTemplate.getForEntity(studentUrl + studentCode, StudentResponse.class).getBody();
        }catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
    
    //Call subject service
    private List<SubjectResponse> callSubjectService(List<StudentRegistrationDetail> list) {
        List<SubjectResponse> listSubject = new ArrayList<>();
        for (StudentRegistrationDetail scd : list) {
            try {
            	SubjectResponse subject = restTemplate.getForEntity(subjectUrl + scd.getSubjectCode(), SubjectResponse.class).getBody();
                listSubject.add(subject);
            }catch(Exception e) {
            	e.printStackTrace();
            	return null;
            }
        }
        return listSubject;
    }
    
    //Call notification service
    private void callNotification(EmailMessageRequest emailMessageRequest) {
        try {
        	HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EmailMessageRequest> requestEntity = new HttpEntity<>(emailMessageRequest, header);
            int statusCode = restTemplate.postForEntity(notificationUrl, requestEntity, String.class).getStatusCodeValue();
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    //Call student service to get progress
    private StudentProgressResponse callStudentServiceToGetProgress(String studentCode) {
        try {
        	StudentProgressResponse studentProgress = restTemplate.getForEntity(studentUrl + studentCode + "/progress/", StudentProgressResponse.class).getBody();
            return studentProgress;
        }catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
    
    //Call subject class service to get schedule
    private List<ScheduleResponse> callSubjectClassServiceToGetSchedule(List<StudentRegistrationDetail> list) {
        List<ScheduleResponse> listScheduleClass = new ArrayList<>();
        for (StudentRegistrationDetail scd : list) {
            try {
            	ScheduleResponse scheduleClass = new ScheduleResponse();
                ResponseEntity<List<ScheduleDetailResponse>> responseEntity = restTemplate.exchange(
            	    subjectclassUrl + scd.getSubjectClassCode() + "/schedule/",
            	    HttpMethod.GET,
            	    null,
            	    new ParameterizedTypeReference<List<ScheduleDetailResponse>>() {}
            	);
            	List<ScheduleDetailResponse> scheduleDetails = responseEntity.getBody();

                scheduleClass.setSubjectClassCode(scd.getSubjectClassCode());
                scheduleClass.setDetails(scheduleDetails);
                listScheduleClass.add(scheduleClass);
            }catch(Exception e) {
            	e.printStackTrace();
            	return null;
            }
        }
        return listScheduleClass;
    }
    
    //Call subject class service to update number
    private int callSubjectClassServiceToUpdateNumber(String code, String action) {
    	try {
    		return restTemplate.exchange(subjectclassUrl + code+"/number/"+action+"/", HttpMethod.PUT, null,String.class).getStatusCodeValue();
    	}catch(Exception e) {
    		e.printStackTrace();
    		return 400;
    	}
    }
    
    //Call registration service
    private boolean callRegistrationService(RegistrationRequest registration, int id) {
        try {
        	HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RegistrationRequest> requestEntity = new HttpEntity<>(registration, header);
            if (id > 0) {
                int statusCode = restTemplate.exchange(registrationUrl + id, HttpMethod.PUT, requestEntity, String.class).getStatusCodeValue();
                if (statusCode == 200) return true;
            } 
            int statusCode = restTemplate.postForEntity(registrationUrl, requestEntity, String.class).getStatusCodeValue();
            if (statusCode == 201) {
            	return true;
            }
        }catch(Exception e) {
        	e.printStackTrace();
        }
		return false;
    }
    
    
    //Verify credits
    private boolean verifyCredits(List<SubjectResponse> listSubject, StringBuilder message) {
        int totalCredits = 0;
        for (SubjectResponse subject : listSubject) {
            totalCredits += subject.getCredits();
        }
        if (totalCredits < 12 || totalCredits > 28) {
            message.append("Số tín chỉ của bạn là " + totalCredits);
            message.append(".\r\n");
            message.append("Không hợp lệ với yêu cầu đăng ký. Số tín chỉ hợp lệ là từ 12 đến 28.");
            return false;
        }
        return true;
    }
    
    //Verify prerequisites
    private boolean verifyPrerequisites(List<SubjectResponse> listSubject, StudentProgressResponse studentProgress, StringBuilder message) {
        Set<String> listError = new HashSet<>();
        for (SubjectResponse subject : listSubject) {
            for (SubjectResponse prerequisites : subject.getPrerequisites()) {
                boolean check = false;
                for (ProgressDetailResponse pd : studentProgress.getDetails()) {
                    if (Objects.equals(prerequisites.getCode(), pd.getSubjectCode()) && pd.isStatus()) {
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    listError.add(subject.getCode());
                }
            }
        }
        if (!listError.isEmpty()) {
            message.append("Danh sách môn không đủ điều kiện tiên quyết: " + String.join(", ", listError));
            message.append(".\r\n");
        }
        return listError.isEmpty();
    }
    
    //Verify duplication schedule
    private boolean verifyDuplicateSchedule(List<ScheduleResponse> scheduleSubjects, StringBuilder message) {
        Map<String, Set<String>> duplicated = new HashMap<>(); 
        for (ScheduleResponse scheduleSubject : scheduleSubjects) {
            for (ScheduleResponse s : scheduleSubjects) {
                if (isDuplicateScheduleSubject(scheduleSubject, s)) {
                    if (duplicated.get(scheduleSubject.getSubjectClassCode()) == null) {
                    	duplicated.put(scheduleSubject.getSubjectClassCode(), new HashSet<>()); 
                    }
                    duplicated.get(scheduleSubject.getSubjectClassCode()).add(s.getSubjectClassCode());
                }
            }
        }
        if (!duplicated.isEmpty()) {
        	message.append("Danh sách lớp học có lịch trùng nhau:").append("\r\n");
        	duplicated.forEach((key, value) -> {
        		message.append(String.format("%s: %s", key, String.join(", ", value))).append("\r\n");
        		
        	});
        }
        return duplicated.isEmpty();
    }
    private boolean isDuplicateScheduleSubject(ScheduleResponse scheduleSubject1, ScheduleResponse scheduleSubject2) {
    	if (scheduleSubject1.getSubjectClassCode().equals(scheduleSubject2.getSubjectClassCode())) {
    		return false;
    	}
        for (ScheduleDetailResponse scheduleDetail1 : scheduleSubject1.getDetails()) {
            for (ScheduleDetailResponse scheduleDetail2 : scheduleSubject2.getDetails()) {
                if (isDuplicateScheduleDetail(scheduleDetail1, scheduleDetail2)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isDuplicateScheduleDetail(ScheduleDetailResponse schedule1, ScheduleDetailResponse schedule2) {
        // Không trùng thứ -> không thể trùng
        if (!schedule1.getDayOfWeek().equals(schedule2.getDayOfWeek())) {
            return false;
        }

        // Khong trung gio -> khong the trung
        if (!isDuplicateSession(schedule1, schedule2)) {
            return false;
        }

        // check trung tuan
        int i1 = 0, i2 = 0;
        List<Integer> weeks1 = schedule1.getWeeks();
        List<Integer> weeks2 = schedule2.getWeeks();
        while (i1 < weeks1.size() && i2 < weeks2.size()) {
            if (weeks1.get(i1).equals(weeks2.get(i2))) {
                return true;
            } else if (weeks1.get(i1).compareTo(weeks2.get(i2)) < 0) {
                i1 += 1;
            } else {
                i2 += 1;
            }
        }
        return false;
    }
    private boolean isDuplicateSession(ScheduleDetailResponse schedule1, ScheduleDetailResponse schedule2) {
        return (schedule1.getSessionStart() >= schedule2.getSessionStart() && schedule1.getSessionStart() <= schedule2.getSessionEnd())
                || (schedule2.getSessionStart() >= schedule1.getSessionStart() && schedule2.getSessionStart() <= schedule1.getSessionEnd());
    }
    
  //Verify Number
    private boolean verifyNumber(StudentRegistration studentRegistration, StringBuilder message) {
        Set<String> invalidSubjectClassCode = new HashSet<>();
        List<String> subjectClasCodes = new ArrayList<>();
        for (StudentRegistrationDetail subjectClass : studentRegistration.getDetails()) {
            if (callSubjectClassServiceToUpdateNumber(subjectClass.getSubjectClassCode(), "add") != 200) {
                invalidSubjectClassCode.add(subjectClass.getSubjectClassCode());
            }else {
            	subjectClasCodes.add(subjectClass.getSubjectClassCode());
            }
        }
        if (!invalidSubjectClassCode.isEmpty())
            message.append("Danh sách lớp đã full: " + String.join(", ", invalidSubjectClassCode) + ".\r\n");
        subjectClassRegistrations.put(studentRegistration.getStudentCode(), subjectClasCodes);
        return invalidSubjectClassCode.isEmpty();
    }
    
    //Roll back where process was deleted or stopped
    private void rollBackNumberSubjectClass(String studentCode) {
    	List<String> subjectClassCodes = subjectClassRegistrations.get(studentCode);
    	for(String code : subjectClassCodes) {
    		callSubjectClassServiceToUpdateNumber(code, "delete");
    	}
    }
}
