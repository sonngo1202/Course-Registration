package com.example.Subject.dao;

import org.openapitools.model.Subject;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class SubjectDAO extends DAO<Subject, String> {

    public SubjectDAO() throws ClassNotFoundException, SQLException {
        super();
    }

    @Override
    public Iterable<Subject> findAll() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subject WHERE true";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Subject subject = resultSetToSubject(rs);
                subjects.add(subject);
                subject.setPrerequisites(findAllPrerequisites(subject.getCode()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return subjects;
    }

    @Override
    public Optional<Subject> findById(String id) {
        Subject subject = null;
        String sql = "SELECT * FROM subject WHERE code = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                subject = resultSetToSubject(rs);
                subject.setPrerequisites(findAllPrerequisites(subject.getCode()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(subject);
    }

    public List<Subject> findAllPrerequisites(String code) throws SQLException {
        String sql = "SELECT s.* FROM subject s " +
                "JOIN prerequisites ps ON ps.prerequisiteSubjectCode = s.code " +
                "WHERE ps.Subjectcode = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, code);

        List<Subject> prerequisites = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            prerequisites.add(resultSetToSubject(rs));
        }
        return prerequisites;
    }

    private Subject resultSetToSubject(ResultSet rs) throws SQLException {
        return Subject.builder()
                .code(rs.getString("code"))
                .name(rs.getString("name"))
                .credits(rs.getInt("credits"))
                .prerequisites(new ArrayList<>())
                .isActive(rs.getBoolean("is_active"))
                .des(rs.getString("des")).build();
    }

    @Override
    public boolean save(Subject s) {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO subject(code, name, credits, is_active, des) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getCode());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getCredits());
            ps.setBoolean(4, s.getIsActive());
            ps.setString(5, s.getDes());
            ps.executeUpdate();

            sql = "INSERT INTO prerequisites(prerequisiteSubjectCode, Subjectcode) VALUES (?, ?)";
            ps = conn.prepareStatement(sql);
            for (Subject prerequisite : s.getPrerequisites()) {
                ps.setString(1, prerequisite.getCode());
                ps.setString(2, s.getCode());
                ps.execute();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Subject data) {
        String sql = "UPDATE subject SET name = ?, credits = ?, is_active = ?, des= ? WHERE code = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, data.getName());
            ps.setInt(2, data.getCredits());
            ps.setBoolean(3, data.getIsActive());
            ps.setString(4, data.getDes());
            ps.setString(5, data.getCode());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "UPDATE subject SET is_active = false WHERE code = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
