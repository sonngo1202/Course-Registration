package com.example.Student.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openapitools.model.Progress;
import org.openapitools.model.ProgressDetail;
import org.springframework.stereotype.Component;

@Component
public class ProgressDAO extends DAO{
	public Progress getProgress(String studentCode) {
		Progress progress = null;
		String sql_progress = "SELECT * FROM progress WHERE Studentcode = ?";
		String sql_detail = "SELECT * FROM progressdetail WHERE Progressid = ?";
		try {
			con.setAutoCommit(false);
			PreparedStatement ps = con.prepareStatement(sql_progress);
			ps.setString(1, studentCode);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				progress = new Progress();
				progress.setId(rs.getInt("id"));
				progress.setTotalCredits(rs.getInt("total_credits"));
				progress.setPoint(rs.getBigDecimal("point"));
				progress.setGrade(rs.getString("grade"));
			}
			if(progress != null) {
				List<ProgressDetail> details = new ArrayList<>();
				PreparedStatement psDetail = con.prepareStatement(sql_detail);
				psDetail.setInt(1, progress.getId());
				ResultSet rsDetail = psDetail.executeQuery();
				while(rsDetail.next()) {
					ProgressDetail detail = new ProgressDetail();
					//True is obtain and False is fail subject
					detail.setStatus(rsDetail.getInt("status")==1?true:false);;
					detail.setGrade(rsDetail.getString("grade"));
					detail.setPoint(rsDetail.getBigDecimal("point"));
					detail.setSubjectCode(rsDetail.getString("Subjectcode"));
					details.add(detail);
				}
				progress.setDetails(details);
			}
			con.commit();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return progress;
	}
}