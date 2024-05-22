package com.example.Registration.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.openapitools.model.Registration;
import org.openapitools.model.RegistrationDTO;
import org.openapitools.model.RegistrationDetail;
import org.springframework.stereotype.Component;

@Component
public class RegistrationDAO extends DAO{
	public boolean saveRegistration(RegistrationDTO registration) {
		boolean kq = false;
		String sql_registration = "INSERT INTO registration(created_at, note, is_active, Studentcode) VALUES(?, ?, ?, ?)";
		String sql_detail = "INSERT INTO registrationdetail(Registrationid, SubjectClasscode) VALUES(?, ?)";
		try {
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(sql_registration, Statement.RETURN_GENERATED_KEYS);
			ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
			ps.setString(2, registration.getNote());
			ps.setInt(3, 1);
			ps.setString(4, registration.getStudentCode());
			ps.executeUpdate();
			int id = 0;
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if(generatedKeys.next()) {
				id = generatedKeys.getInt(1);
			}
			PreparedStatement psDetail = con.prepareStatement(sql_detail);
			for(RegistrationDetail rd : registration.getDetails()) {
				psDetail.setInt(1, id);
				psDetail.setString(2, rd.getSubjectClassCode());
				psDetail.addBatch();
			}
			psDetail.executeBatch();
			con.commit();
			kq = true;
		}catch(SQLException e) {
			e.printStackTrace();
			kq = false;
		}
		return kq;
	}
	public boolean updateRegistration(int id ,RegistrationDTO registration) {
		boolean kq = false;
		String sql_delete = "DELETE FROM registrationdetail WHERE Registrationid = ?";
		String sql_detail = "INSERT INTO registrationdetail(Registrationid, SubjectClasscode) VALUES(?, ?)";
		try {
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(sql_delete);
			ps.setInt(1, id);
			ps.executeUpdate();
			PreparedStatement psDetail = con.prepareStatement(sql_detail);
			for(RegistrationDetail rd : registration.getDetails()) {
				psDetail.setInt(1, id);
				psDetail.setString(2, rd.getSubjectClassCode());
				psDetail.addBatch();
			}
			psDetail.executeBatch();
			con.commit();
			kq = true;
		}catch(SQLException e) {
			e.printStackTrace();
			kq = false;
		}
		return kq;
	}
	public Registration getRegistration(String studentCode) {
		Registration registration = null;
		String sql_registration = "SELECT * FROM registration WHERE Studentcode = ? AND is_active = 1";
		String sql_detail = "SELECT * FROM registrationdetail WHERE Registrationid = ?";
		try {
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(sql_registration);
			ps.setString(1, studentCode);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				registration = new Registration();
				registration.setId(rs.getInt("id"));
				registration.setNote(rs.getString("note"));
				registration.setCreatedAt(LocalDate.parse(rs.getString("created_at")));
				registration.setStudentCode(studentCode);
			}
			if(registration != null) {
				List<RegistrationDetail> details = new ArrayList<>();
				PreparedStatement psDetail = con.prepareStatement(sql_detail);
				psDetail.setInt(1, registration.getId());
				ResultSet rsDetail = psDetail.executeQuery();
				while(rsDetail.next()) {
					RegistrationDetail registrationDetail = new RegistrationDetail();
					registrationDetail.setSubjectClassCode(rsDetail.getString("SubjectClasscode"));
					details.add(registrationDetail);
				}
				registration.setDetails(details);
			}
			con.commit();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return registration;
	}
}