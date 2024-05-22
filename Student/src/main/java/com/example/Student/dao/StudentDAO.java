package com.example.Student.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.openapitools.model.Student;
import org.springframework.stereotype.Component;


@Component
public class StudentDAO extends DAO{
	public List<Student> getAll() {
		String sql = "SELECT * FROM student";
		List<Student> listStudent = new ArrayList<>();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Student student = new Student();
				student.setCode(rs.getString("code"));
				student.setFirstname(rs.getString("firstname"));
				student.setLastname(rs.getString("lastname"));
				student.setDob(LocalDate.parse(rs.getString("dob")));
				student.setEmail(rs.getString("email"));
				student.setPhoneNumber(rs.getString("phone_number"));
				student.setDes(rs.getString("des"));
				listStudent.add(student);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return listStudent;
	}
	
	public Student getDetail(String code) {
		Student student = null;
		String sql = "SELECT * FROM student WHERE code = ?";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, code);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				student = new Student();
				student.setCode(code);
				student.setFirstname(rs.getString("firstname"));
				student.setLastname(rs.getString("lastname"));
				student.setDob(LocalDate.parse(rs.getString("dob")));
				student.setEmail(rs.getString("email"));
				student.setPhoneNumber(rs.getString("phone_number"));
				student.setDes(rs.getString("des"));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return student;
	}
	
	public boolean existById(String id) {
        String sql = "SELECT * FROM student WHERE code = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}