package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// SongDAO 類別，負責與資料庫進行歌曲相關操作
public class SongDAO {
    // 資料庫連線字串
    private static final String DB_URL = "jdbc:sqlite:MusicPlayer.db";

    // 新增歌曲到資料庫
    public static void addSong(Song song) {
        String sql = "INSERT INTO songs (name, time, artist, filePath) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, song.getName());
            pstmt.setString(2, song.getTime());
            pstmt.setString(3, song.getArtist());
            pstmt.setString(4, song.getFilePath());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 取得所有歌曲
    public static List<Song> getAllSongs() {
        List<Song> list = new ArrayList<>();
        String sql = "SELECT * FROM songs";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Song song = new Song(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("time"),
                        rs.getString("artist"),
                        rs.getString("filePath"));
                list.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 取得所有歌曲數量
    public static int getAllSongsNum() {
        int num = 0;
        String sql = "SELECT * FROM songs";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                num++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    // 依播放清單名稱取得所有歌曲
    public static List<Song> getSongsByPlaylist(String playlistName) {
        List<Song> list = new ArrayList<>();
        String sql = "SELECT * FROM songs WHERE playlist LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + playlistName + "%"); // 模糊查詢
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String playliststr = rs.getString("playlist");
                List<String> playlist = new ArrayList<>(Arrays.asList(playliststr.split(",")));

                // 僅當這首歌實際包含該 playlistName 時才加入清單
                if (playlist.contains(playlistName)) {
                    Song song = new Song(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("time"),
                            rs.getString("artist"),
                            rs.getString("filePath"),
                            playlist);
                    list.add(song);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 依播放清單名稱取得歌曲數量
    public static int getSongsnumByPlaylist(String playlistName) {
        int num = 0;
        String sql = "SELECT * FROM songs WHERE playlist LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + playlistName + "%"); // 模糊查詢
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String playliststr = rs.getString("playlist");
                List<String> playlist = new ArrayList<>(Arrays.asList(playliststr.split(",")));

                // 僅當這首歌實際包含該 playlistName 時才計數
                if (playlist.contains(playlistName)) {
                    num++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return num;
    }

    // 將歌曲加入指定播放清單
    public static void addSongToPlaylist(int songId, String playlistName) {
        String getSql = "SELECT playlist FROM songs WHERE id = ?";
        String updateSql = "UPDATE songs SET playlist = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement getStmt = conn.prepareStatement(getSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            getStmt.setInt(1, songId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                String playlistStr = rs.getString("playlist");
                List<String> playlists = new ArrayList<>();

                if (playlistStr != null && !playlistStr.isEmpty()) {
                    playlists = new ArrayList<>(Arrays.asList(playlistStr.split(",")));
                }

                if (!playlists.contains(playlistName)) {
                    playlists.add(playlistName);
                    String updatedPlaylist = String.join(",", playlists);

                    updateStmt.setString(1, updatedPlaylist);
                    updateStmt.setInt(2, songId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 從指定播放清單移除歌曲
    public static void removeSongFromPlaylist(int songId, String playlistName) {
        String getSql = "SELECT playlist FROM songs WHERE id = ?";
        String updateSql = "UPDATE songs SET playlist = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement getStmt = conn.prepareStatement(getSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            getStmt.setInt(1, songId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                String playlistStr = rs.getString("playlist");
                if (playlistStr != null && !playlistStr.isEmpty()) {
                    List<String> playlists = new ArrayList<>(Arrays.asList(playlistStr.split(",")));
                    playlists.removeIf(p -> p.equals(playlistName)); // 移除指定名稱

                    String updatedPlaylist = String.join(",", playlists);

                    updateStmt.setString(1, updatedPlaylist);
                    updateStmt.setInt(2, songId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 從資料庫刪除歌曲
    public static void deleteSongFromAllSongs(int songId) {
        String sql = "DELETE FROM songs WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, songId);
            pstmt.executeUpdate();
            System.out.println("sucess");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 更新歌曲名稱與歌手
    public static void updateSong(int songId, String newName, String newArtist) {
        String sql = "UPDATE songs SET name = ?, artist = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newArtist);
            pstmt.setInt(3, songId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 重新命名播放清單（同步更新所有歌曲的 playlist 欄位）
    public static void renamePlaylist(String oldName, String newName) {
        String selectSql = "SELECT id, playlist FROM songs WHERE playlist LIKE ?";
        String updateSql = "UPDATE songs SET playlist = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            selectStmt.setString(1, "%" + oldName + "%"); // 模糊搜尋
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                int songId = rs.getInt("id");
                String playlistStr = rs.getString("playlist");

                if (playlistStr != null && !playlistStr.isEmpty()) {
                    List<String> playlists = new ArrayList<>(Arrays.asList(playlistStr.split(",")));
                    boolean updated = false;

                    for (int i = 0; i < playlists.size(); i++) {
                        if (playlists.get(i).equals(oldName)) {
                            playlists.set(i, newName);
                            updated = true;
                        }
                    }

                    if (updated) {
                        String updatedPlaylistStr = String.join(",", playlists);
                        updateStmt.setString(1, updatedPlaylistStr);
                        updateStmt.setInt(2, songId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // （註解範例）取得最大 id（未啟用）
    // public static int getMaxId() {
    //     String sql = "select * from songs order by id desc limit 1";
    //     try (Connection conn = DriverManager.getConnection(DB_URL);
    //             PreparedStatement stmt = conn.prepareStatement(sql);
    //             ResultSet rs = stmt.executeQuery()) {
    //         if (rs.next()) {
    //             String idstr = rs.getString("id");
    //             if (idstr.equals("")) {
    //                 return 0;
    //             }
    //             int id = Integer.parseInt(idstr.substring(1));
    //             return id;
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return -2;
    // }
}
