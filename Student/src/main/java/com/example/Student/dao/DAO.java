package com.example.Student.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
	protected static Connection con;
	protected DAO() {
		if(con == null) {
			String jdbcURL = "jdbc:mysql://localhost:3306/hdv_registersubject";
			String jdbcUsername = "root";
			String jdbcPassword = "Heliossn1202";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
