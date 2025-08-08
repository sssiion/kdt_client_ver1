package org.example.kdt_bank_client2.UserBank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL DB 연결을 담당하는 유틸 클래스
 * 다른 Controller에서 DBConnection.getConnection()으로 호출 가능
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/NuriBank"; // DB 주소
    private static final String USER = "root";  // DB 사용자 계정
    private static final String PASSWORD = "root"; // DB 비밀번호 (환경에 맞게 변경)

    /**
     * DB 연결을 반환하는 메소드
     * @return Connection 객체
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}