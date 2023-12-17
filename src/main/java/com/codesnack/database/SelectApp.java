package com.codesnack.database;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.codesnack.commands.Methods;
import com.codesnack.telegram.TelegramBot;

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

    public void selectAll() {
        String sql = "SELECT id, userId, username, bio, photo, faculty FROM warehouses";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
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

    public void selectProfile(Long userId) {
        String sql = "SELECT bio, photo, faculty "
                + "FROM warehouses WHERE userId = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("bio") + "\t" +
                        rs.getString("photo") + "\t" +
                        rs.getString("faculty"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String sendProfile(Long userId) throws SQLException {
        String sql = "SELECT bio, faculty "
                + "FROM warehouses WHERE userId = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("bio") + "\n" +
                        rs.getString("faculty");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String sendUsername(Long userId) throws SQLException {
        String sql = "SELECT username "
                + "FROM warehouses WHERE userId = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private final Map<Long, Set<Long>> shownProfiles = new HashMap<>();
    private final Map<Long, Integer> amountOfShownProfiles = new HashMap<>();

    public int count() {
        String url = "jdbc:sqlite:C://sqlite/db/test.db";
        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) FROM warehouses";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                int rowCount = resultSet.getInt(1);
                return rowCount;
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getRandomProfile(Long userId) throws SQLException {
        if (!shownProfiles.containsKey(userId)) {
            shownProfiles.put(userId, new HashSet<>());
            amountOfShownProfiles.put(userId, 0);
        }
        String sql = "SELECT bio, faculty, userId FROM warehouses WHERE userId <> ? ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            Long randomUserId = rs.getLong("userId");
            TelegramBot.savingId(userId, randomUserId);
            if (!shownProfiles.get(userId).contains(randomUserId)) {
                shownProfiles.get(userId).add(randomUserId);
                amountOfShownProfiles.put(userId, amountOfShownProfiles.get(userId) + 1);
                return rs.getString("bio") + "\n" +
                        rs.getString("faculty");
            } else if (amountOfShownProfiles.get(userId) == count() - 1) {
                return "Анкет больше нет";
            }
        }
        return getRandomProfile(userId);
    }
}