package com.example.SubjectClass.dao;

import org.openapitools.model.Schedule;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class ScheduleDAO extends DAO<Schedule, Long> {
    public ScheduleDAO() throws ClassNotFoundException, SQLException {
        super();
    }

    @Override
    public Iterable<Schedule> findAll() {
        return null;
    }

    @Override
    public Optional<Schedule> findById(Long id) {
        return Optional.empty();
    }

    @Override
    Schedule resultSetToModel(ResultSet rs) throws SQLException {
        return Schedule.builder()
                .id(rs.getLong("id"))
                .weeks(
                        Arrays.stream(rs.getString("weeks").split(","))
                                .map(week -> Integer.parseInt(week.trim()))
                                .collect(Collectors.toList())
                )
                .dayOfWeek(rs.getString("dayofweek"))
                .sessionStart(rs.getInt("session_start"))
                .sessionEnd(rs.getInt("session_end"))
                .des(rs.getString("des"))
                .build();
    }

    public List<Schedule> findAllBySubjectClassCode(String code) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedule WHERE SubjectClasscode = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                schedules.add(resultSetToModel(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return schedules;
    }

    @Override
    public boolean save(Schedule s) {
        return false;
    }

    @Override
    public boolean update(Schedule data) {
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
