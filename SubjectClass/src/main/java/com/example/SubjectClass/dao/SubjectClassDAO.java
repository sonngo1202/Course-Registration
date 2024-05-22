package com.example.SubjectClass.dao;

import org.openapitools.model.Schedule;
import org.openapitools.model.SubjectClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class SubjectClassDAO extends DAO<SubjectClass, String> {
    public SubjectClassDAO() throws ClassNotFoundException, SQLException {
        super();
    }
    
    @Autowired
	private ScheduleDAO scheduleDAO; 

    @Override
    public Iterable<SubjectClass> findAll() {
        List<SubjectClass> subjectClasses = new ArrayList<>();
        String sql = "SELECT * FROM subjectclass WHERE is_active = true";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	SubjectClass subjectClass = resultSetToModel(rs);
            	subjectClass.setSchedules(scheduleDAO.findAllBySubjectClassCode(subjectClass.getCode()));
                subjectClasses.add(subjectClass);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subjectClasses;
    }

    public List<SubjectClass> findAllBySubjectCode(String code) {
        List<SubjectClass> subjectClasses = new ArrayList<>();
        String sql = "SELECT * FROM subjectclass WHERE Subjectcode = ? AND is_active = true";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                subjectClasses.add(resultSetToModel(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subjectClasses;
    }

    @Override
    public Optional<SubjectClass> findById(String id) {
        SubjectClass subjectClass = null;
        String sql = "SELECT * FROM subjectclass WHERE code = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                subjectClass = resultSetToModel(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(subjectClass);
    }

    public boolean existById(String id) {
        String sql = "SELECT * FROM subjectclass WHERE code = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    SubjectClass resultSetToModel(ResultSet rs) throws SQLException {
        return SubjectClass.builder()
                .code(rs.getString("code"))
                .maxNumber(rs.getInt("max_number"))
                .number(rs.getInt("number"))
                .isActive(rs.getBoolean("is_active"))
                .des(rs.getString("des"))
                .subjectCode(rs.getString("Subjectcode"))
                .build();
    }

    @Override
    public boolean save(SubjectClass s) {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO subjectclass(code, max_number, number, is_active, des, Subjectcode) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getCode());
            ps.setInt(2, s.getMaxNumber());
            ps.setInt(3, s.getNumber());
            ps.setBoolean(4, s.getIsActive());
            ps.setString(5, s.getDes());
            ps.setString(6, s.getSubjectCode());

            if (ps.executeUpdate() < 1) {
                conn.rollback();
                return false;
            }

            sql = "INSERT INTO schedule(weeks, session_start, session_end, dayofweek, des, SubjectClasscode) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            for (Schedule schedule : s.getSchedules()) {
                ps.setString(1, schedule.getWeeks().stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "))
                );
                ps.setInt(2, schedule.getSessionStart());
                ps.setInt(3, schedule.getSessionEnd());
                ps.setString(4, schedule.getDayOfWeek());
                ps.setString(5, schedule.getDes());
                ps.setString(6, s.getCode());

                if (ps.executeUpdate() < 1) {
                    conn.rollback();
                    return false;
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(SubjectClass data) {
        return false;
    }

    public boolean updateNumber(String code, String action) {
    	int count = -1;
    	if(action.equals("add")) count = 1;
        String sql = "UPDATE subjectclass SET number = number + ? WHERE code = ? AND number < max_number";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, count);
            ps.setString(2, code);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "UPDATE subjectclass SET is_active = false WHERE code = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (ps.executeUpdate() < 1) {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
