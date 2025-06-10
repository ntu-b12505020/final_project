package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MovieDAO 類別：負責與資料庫進行電影相關的存取操作
 */
public class MovieDAO {
    // 資料庫連線字串
    private static final String URL = "jdbc:sqlite:cinema.db";
    // 快取所有電影資料（以ID為key）
    public static Map<String, Movie> movieMap = new HashMap<>();

    /**
     * 取得所有電影資料
     * 
     * @return 電影清單
     */
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT AtMoviesID, TitleZH, TitleEN, Summary, Length, Classification, Status FROM movies";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("AtMoviesID");
                String name = rs.getString("TitleZH") + "(" + rs.getString("TitleEN") + ")";
                String description = rs.getString("Summary");
                int duration = rs.getInt("Length");
                String status = rs.getString("Status");

                // Classification 是字串，要轉成 int
                String classificationStr = rs.getString("Classification").replace("+", "");
                int ageLimit = 0;
                try {
                    ageLimit = Integer.parseInt(classificationStr);
                } catch (NumberFormatException e) {
                    ageLimit = 0; // 如果轉失敗，預設為0
                }

                Movie movie = new Movie(id, name, description, duration, ageLimit, status);
                movies.add(movie);
                movieMap.put(id, movie); // 存入快取
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    /**
     * 取得所有狀態為 On 的電影
     * 
     * @return 上映中電影清單
     */
    public static List<Movie> getMoviesON() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT AtMoviesID, TitleZH, TitleEN, Summary, Length, Classification, Status FROM movies";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("AtMoviesID");
                String name = rs.getString("TitleZH") + "(" + rs.getString("TitleEN") + ")";
                String description = rs.getString("Summary");
                int duration = rs.getInt("Length");
                String status = rs.getString("Status");

                // Classification 是字串，要轉成 int
                String classificationStr = rs.getString("Classification").replace("+", "");
                int ageLimit = 0;
                try {
                    ageLimit = Integer.parseInt(classificationStr);
                } catch (NumberFormatException e) {
                    ageLimit = 0; // 如果轉失敗，預設為0
                }

                Movie movie = new Movie(id, name, description, duration, ageLimit, status);
                if (movie.getStatus().equals("On")) {
                    movies.add(movie);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    /**
     * 取得所有狀態為 On 且有開放場次的電影
     * 
     * @return 上映中且有開放場次的電影清單
     */
    public static List<Movie> getMoviesOnHaveOpenShowtime() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT AtMoviesID, TitleZH, TitleEN, Summary, Length, Classification, Status FROM movies";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("AtMoviesID");
                String name = rs.getString("TitleZH") + "(" + rs.getString("TitleEN") + ")";
                String description = rs.getString("Summary");
                int duration = rs.getInt("Length");
                String status = rs.getString("Status");

                // Classification 是字串，要轉成 int
                String classificationStr = rs.getString("Classification").replace("+", "");
                int ageLimit = 0;
                try {
                    ageLimit = Integer.parseInt(classificationStr);
                } catch (NumberFormatException e) {
                    ageLimit = 0; // 如果轉失敗，預設為0
                }

                Movie movie = new Movie(id, name, description, duration, ageLimit, status);
                // 狀態為On且有開放場次才加入
                if (movie.getStatus().equals("On") && ShowtimeDAO.getOpenShowtimesbyMovie(movie).size() != 0) {
                    movies.add(movie);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    /**
     * 取得所有有開放場次的電影
     * 
     * @return 有開放場次的電影清單
     */
    public static List<Movie> getMoviesHaveOpenShowtime() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT AtMoviesID, TitleZH, TitleEN, Summary, Length, Classification, Status FROM movies";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("AtMoviesID");
                String name = rs.getString("TitleZH") + "(" + rs.getString("TitleEN") + ")";
                String description = rs.getString("Summary");
                int duration = rs.getInt("Length");
                String status = rs.getString("Status");

                // Classification 是字串，要轉成 int
                String classificationStr = rs.getString("Classification").replace("+", "");
                int ageLimit = 0;
                try {
                    ageLimit = Integer.parseInt(classificationStr);
                } catch (NumberFormatException e) {
                    ageLimit = 0; // 如果轉失敗，預設為0
                }

                Movie movie = new Movie(id, name, description, duration, ageLimit, status);
                // 只要有開放場次就加入
                if (ShowtimeDAO.getOpenShowtimesbyMovie(movie).size() != 0) {
                    movies.add(movie);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    /**
     * 更新電影資料
     * 
     * @param movie 欲更新的電影物件
     */
    public static void update(Movie movie) {
        String sql = "UPDATE movies SET TitleZH = ?, TitleEN = ?, Summary = ?, Length = ?, Classification = ?, Status = ? WHERE AtMoviesID = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getNameZH());
            pstmt.setString(2, movie.getNameEN());
            pstmt.setString(3, movie.getDescription());
            pstmt.setInt(4, movie.getDuration());
            pstmt.setInt(5, movie.getAgeLimit());
            pstmt.setString(6, movie.getStatus());
            pstmt.setString(7, movie.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增一部電影到資料庫
     * 
     * @param movie 欲新增的電影物件
     * @return 新增成功回傳 true，否則 false
     */
    public static boolean addMovie(Movie movie) {
        String sql = "INSERT INTO movies (AtMoviesID, TitleZH, TitleEN, Classification, Summary, Length, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String TitleEN = movie.getName().substring(movie.getName().indexOf('(') + 1, movie.getName().indexOf(')'));
            String TitleZH = movie.getName().substring(0, movie.getName().indexOf('('));
            stmt.setString(1, movie.getId());
            stmt.setString(2, TitleZH);
            stmt.setString(3, TitleEN);
            stmt.setInt(4, movie.getAgeLimit());
            stmt.setString(5, movie.getDescription());
            stmt.setInt(6, movie.getDuration());
            stmt.setString(7, movie.getStatus());
            stmt.executeUpdate();
            System.out.println("success");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 刪除一部電影
     * 
     * @param movie 欲刪除的電影物件
     * @return 刪除成功回傳 true，否則 false
     */
    public static boolean deleteMovie(Movie movie) {
        String sql = "DELETE FROM movies WHERE AtMoviesID = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movie.getId());
            stmt.executeUpdate();
            System.out.println("success");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 依照電影ID查詢電影
     * 
     * @param id 電影ID
     * @return 查詢到的電影物件，查無則回傳 null
     */
    public static Movie getMovieById(String id) {
        String sql = "SELECT * FROM movies WHERE AtMoviesID = ?";
        Movie movie = null;
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("TitleZH") + "(" + rs.getString("TitleEN") + ")";
                    String description = rs.getString("Summary");
                    int duration = rs.getInt("Length");
                    String status = rs.getString("Status");

                    String classificationStr = rs.getString("Classification").replace("+", "");
                    int ageLimit = 0;
                    try {
                        ageLimit = Integer.parseInt(classificationStr);
                    } catch (NumberFormatException e) {
                        ageLimit = 0; // 如果轉失敗，預設為0
                    }
                    movie = new Movie(id, name, description, duration, ageLimit, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }

    /**
     * 依照電影名稱查詢電影
     * 
     * @param name 電影名稱（格式：中文名(英文名)）
     * @return 查詢到的電影物件，查無則回傳 null
     */
    public static Movie getMovieByName(String name) {
        String sql = "SELECT * FROM movies WHERE TitleZH || '(' || TitleEN || ')' = ?";
        Movie movie = null;
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("AtMoviesID");
                    String description = rs.getString("Summary");
                    int duration = rs.getInt("Length");
                    String status = rs.getString("Status");

                    String classificationStr = rs.getString("Classification").replace("+", "");
                    int ageLimit = 0;
                    try {
                        ageLimit = Integer.parseInt(classificationStr);
                    } catch (NumberFormatException e) {
                        ageLimit = 0;
                    }
                    movie = new Movie(id, name, description, duration, ageLimit, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movie;
    }
}
