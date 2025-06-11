package model;

// 匯入資料庫相關類別
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// PlaylistDAO 類別，負責與資料庫進行播放清單相關操作
public class PlaylistDAO {
    // 資料庫連線字串
    private static final String DB_URL = "jdbc:sqlite:MusicPlayer.db";

    // 取得所有播放清單名稱
    public static ArrayList<String> getAllPlaylistNames() {
        ArrayList<String> playlistNames = new ArrayList<>();

        // SQL 查詢語句，取得所有播放清單名稱
        String sql = "SELECT name FROM playlist"; // 假設欄位叫 name

        // 使用 try-with-resources 自動關閉資源
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            // 逐筆讀取結果並加入清單
            while (rs.next()) {
                String name = rs.getString("name");
                playlistNames.add(name);
            }
        } catch (SQLException e) {
            // 發生例外時印出錯誤訊息
            e.printStackTrace();
        }

        // 回傳所有播放清單名稱
        return playlistNames;
    }

    // 編輯播放清單名稱
    public static void editPlaylistName(String oldName, String newName) {
        // SQL 更新語句
        String sql = "UPDATE playlist SET name = ? WHERE name = ?";

        // 使用 try-with-resources 自動關閉資源
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 設定參數
            pstmt.setString(1, newName);
            pstmt.setString(2, oldName);
            // 執行更新
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // 發生例外時印出錯誤訊息
            e.printStackTrace();
        }
    }
}
