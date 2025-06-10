package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ShowtimeDAO 類別：負責與資料庫進行場次相關的存取操作
 */
public class ShowtimeDAO {
    // 資料庫連線字串
    private static final String URL = "jdbc:sqlite:cinema.db";
    // 快取所有場次資料（以ID為key）
    public static Map<String, Showtime> showtimeMap = new HashMap<>();

    /**
     * 取得所有場次
     * 
     * @return 場次清單
     */
    public static List<Showtime> getAllShowtimes() {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT showtime_uid, movie_id, hall, time, status FROM showtime";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("showtime_uid");
                String movieid = rs.getString("movie_id");
                String hall = rs.getString("hall");
                String timestr = rs.getString("time");
                String status = rs.getString("status");
                Movie movie = MovieDAO.movieMap.get(movieid);
                LocalDateTime time = LocalDateTime.parse(timestr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                Showtime showtime = new Showtime(id, movie, hall, time, status);
                showtimes.add(showtime);
                showtimeMap.put(id, showtime);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return showtimes;
    }

    /**
     * 取得指定電影的所有場次
     * 
     * @param movie 電影物件
     * @return 場次清單
     */
    public static List<Showtime> getShowtimesbyMovie(Movie movie) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT showtime_uid, movie_id, hall, time, status FROM showtime";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("showtime_uid");
                String movieid = rs.getString("movie_id");
                String hall = rs.getString("hall");
                String timestr = rs.getString("time");
                String status = rs.getString("status");
                Movie movie1 = MovieDAO.movieMap.get(movieid);
                LocalDateTime time = LocalDateTime.parse(timestr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                Showtime showtime = new Showtime(id, movie, hall, time, status);

                if (movie.getId().equals(movie1.getId())) {
                    showtimes.add(showtime);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return showtimes;
    }

    /**
     * 取得目前最大場次ID的數字部分
     * 
     * @return 最大ID數字，查無則回傳 -2
     */
    public static int getMaxId() {
        String sql = "select * from showtime order by showtime_uid desc limit 1";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String idstr = rs.getString("showtime_uid");
                if (idstr.equals("")) {
                    return 0;
                }
                int id = Integer.parseInt(idstr.substring(1));
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    /**
     * 取得指定電影的所有開放場次
     * 
     * @param movie 電影物件
     * @return 開放場次清單
     */
    public static List<Showtime> getOpenShowtimesbyMovie(Movie movie) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT showtime_uid, movie_id, hall, time, status FROM showtime";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("showtime_uid");
                String movieid = rs.getString("movie_id");
                String hall = rs.getString("hall");
                String timestr = rs.getString("time");
                String status = rs.getString("status");
                Movie movie1 = MovieDAO.movieMap.get(movieid);
                LocalDateTime time = LocalDateTime.parse(timestr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                Showtime showtime = new Showtime(id, movie, hall, time, status);

                if (movie.getId().equals(movie1.getId()) && showtime.getStatus().equals("open")) {
                    showtimes.add(showtime);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return showtimes;
    }

    /**
     * 更新場次資料
     * 
     * @param showtime 欲更新的場次物件
     */
    public static void update(Showtime showtime) {
        String sql = "UPDATE showtime SET showtime_uid = ?, movie_id = ?, hall = ?, time = ?, status = ? WHERE showtime_uid = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String timestr = showtime.getTime().format(formatter);
            pstmt.setString(1, showtime.getId());
            pstmt.setString(2, showtime.getMovie().getId());
            pstmt.setString(3, showtime.getHall());
            pstmt.setString(4, timestr);
            pstmt.setString(5, showtime.getStatus());
            pstmt.setString(6, showtime.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增一筆場次資料
     * 
     * @param showtime 欲新增的場次物件
     * @return 新增成功回傳 true，否則 false
     */
    public static boolean addshowtime(Showtime showtime) {
        String sql = "INSERT INTO showtime (showtime_uid, movie_id, hall, time, status) VALUES (?, ?, ?, ?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String timestr = showtime.getTime().format(formatter);
            stmt.setString(1, showtime.getId());
            stmt.setString(2, showtime.getMovie().getId());
            stmt.setString(3, showtime.getHall());
            stmt.setString(4, timestr);
            stmt.setString(5, showtime.getStatus());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 刪除指定場次
     * 
     * @param showtime 欲刪除的場次物件
     * @return 刪除成功回傳 true，否則 false
     */
    public static boolean deleteShowtime(Showtime showtime) {
        String sql = "DELETE FROM showtime WHERE showtime_uid = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, showtime.getId());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 檢查所有場次，若已過期則自動關閉
     */
    public static void checkShowtime() {
        LocalDateTime now = LocalDateTime.now();
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement("SELECT showtime_uid, time FROM showtime")) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("showtime_uid");
                    String timestr = rs.getString("time");
                    LocalDateTime time = LocalDateTime.parse(timestr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    if (time.isBefore(now)) {
                        PreparedStatement changestatus = conn
                                .prepareStatement("UPDATE showtime SET status = ? WHERE showtime_uid = ?");
                        changestatus.setString(1, "closed");
                        changestatus.setString(2, id);
                        changestatus.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 依場次ID查詢場次
     * 
     * @param id 場次ID
     * @return 查詢到的場次物件，查無則回傳 null
     */
    public static Showtime getShowtimeById(String id) {
        String sql = "SELECT * FROM showtime WHERE showtime_uid = ?";
        Showtime showtime = null;
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String movieId = rs.getString("movie_id");
                    Movie movie = MovieDAO.getMovieById(movieId);
                    String hall = rs.getString("hall");
                    String timestr = rs.getString("time");
                    LocalDateTime time = LocalDateTime.parse(timestr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    String status = rs.getString("status");
                    showtime = new Showtime(id, movie, hall, time, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtime;
    }

    /**
     * 刪除指定電影的所有場次（同時刪除相關座位資料）
     * 
     * @param movie 欲刪除場次的電影物件
     * @return 刪除成功回傳 true，否則 false
     */
    public static boolean deleteShowtimeByMovie(Movie movie) {
        String selectSql = "SELECT showtime_uid FROM showtime WHERE movie_id = ?";
        String deleteSql = "DELETE FROM showtime WHERE movie_id = ?";
        String sql = "DELETE FROM seats WHERE showtime_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setString(1, movie.getId());
            try (ResultSet rs = selectStmt.executeQuery()) {
                while (rs.next()) {
                    try (PreparedStatement Stmt = conn.prepareStatement(sql)) {
                        String showtimeId = rs.getString("showtime_uid");
                        Stmt.setString(1, showtimeId);
                        Stmt.executeUpdate();
                    }

                }
            }
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, movie.getId());
                deleteStmt.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
