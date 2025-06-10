package model;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO 類別：負責與資料庫進行使用者相關的存取操作
 */
public class UserDAO {
    // 資料庫連線字串
    private static final String URL = "jdbc:sqlite:cinema.db";

    /**
     * 預設建立管理員與一般使用者帳號（若不存在時）
     */
    public static void defaultuser() {
        // 建立管理員帳號
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt1 = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)")) {
            stmt1.setString(1, "admin@test.com");

            try (ResultSet rs = stmt1.executeQuery()) {
                if (rs.next() && !rs.getBoolean(1)) { // 如果資料不存在
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO users (uid, email, password, birthday) VALUES (?, ?, ?, ?)")) {
                        insertStmt.setString(1, "A1");
                        insertStmt.setString(2, "admin@test.com");
                        insertStmt.setString(3, "admin123");
                        insertStmt.setString(4, "1990-01-01");
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 建立一般使用者帳號
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt2 = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)")) {
            stmt2.setString(1, "user@test.com");

            try (ResultSet rs = stmt2.executeQuery()) {
                if (rs.next() && !rs.getBoolean(1)) { // 如果資料不存在
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO users (uid, email, password, birthday) VALUES (?, ?, ?, ?)")) {
                        insertStmt.setString(1, "U1");
                        insertStmt.setString(2, "user@test.com");
                        insertStmt.setString(3, "user123");
                        insertStmt.setString(4, "2000-01-01");
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增使用者到資料庫
     * 
     * @param user 欲新增的使用者物件
     */
    public static void addUser(User user) {
        String sql = "INSERT INTO users (uid, email, password, birthday) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String birthdaystr = user.getBirthday().format(formatter);
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, birthdaystr);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得所有使用者
     * 
     * @return 使用者清單
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT uid, email, password, birthday FROM users";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("uid");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String birthdaystr = rs.getString("birthday");
                LocalDate birthday = LocalDate.parse(birthdaystr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                users.add(new User(id, email, password, birthday));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

}
