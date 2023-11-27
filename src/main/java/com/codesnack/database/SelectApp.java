package com.codesnack.database;

import java.sql.*;

public class SelectApp {
    private Connection connect() {
        String url = "jdbc:sqlite:C://sqlite/db/test.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public void selectAll(){
        String sql = "SELECT id, userId, username, bio, photo, faculty FROM warehouses";
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getLong("userId") + "\t" +
                        rs.getString("username") + "\t" +
                        rs.getString("bio") + "\t" +
                        rs.getString("photo") + "\t" +
                        rs.getString("faculty"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void selectProfile(Long userId){
        String sql = "SELECT bio, photo, faculty "
                + "FROM warehouses WHERE userId = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setLong(1, userId);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("bio") +  "\t" +
                        rs.getString("photo") + "\t" +
                        rs.getString("faculty"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String sendProfile(Long userId) throws SQLException {
        String sql = "SELECT bio, photo, faculty "
                + "FROM warehouses WHERE userId = ?";
        Connection conn = this.connect();
        PreparedStatement pstmt  = conn.prepareStatement(sql);
        pstmt.setLong(1, userId);
        ResultSet rs  = pstmt.executeQuery();
        if (rs.next()) {
//            System.out.println(rs.getString("bio") +  "\n" +
//                    rs.getString("photo") + "\n" +
//                    rs.getString("faculty"));
            return rs.getString("bio") +  "\n" +
                    rs.getString("photo") + "\n" +
                    rs.getString("faculty");
        }
        return null; // или другое значение по умолчанию, если нет строк в результате запроса
    }
}