package com.example.Subject.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public abstract class DAO<T, PK> {

    protected Connection conn;

    public DAO() throws ClassNotFoundException, SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/hdv_registersubject?autoReconnect=true&useSSL=false";
        String dbClass = "com.mysql.cj.jdbc.Driver";
        Class.forName(dbClass);
        conn = DriverManager.getConnection(dbUrl, "root", "Heliossn1202");
    }

    public abstract Iterable<T> findAll();

    public abstract Optional<T> findById(PK id);

    public abstract boolean save(T s);

    public abstract boolean update(T data);

    public abstract boolean deleteById(PK id);
}
