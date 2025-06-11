package model;

// 匯入 Java 工具類別
import java.util.*;

// Song 類別，代表一首歌曲
public class Song {
    // 歌曲編號
    private int id;
    // 歌曲名稱
    private String name;
    // 歌曲時長
    private String time;
    // 歌手名稱
    private String artist;
    // 歌曲檔案路徑
    private String filePath;
    // 歸屬的播放清單名稱列表
    private List<String> playlist = null;
    // 是否正在播放
    private Boolean isPlaying = false;

    // 建構子，初始化所有欄位
    public Song(int id, String name, String time, String artist, String filePath) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.artist = artist;
        this.filePath = filePath;
    }

    // 建構子，僅有 id、name、filePath
    public Song(int id, String name, String filePath) {
        this(id, name, filePath, null, null);
    }

    // 建構子，無 id
    public Song(String name, String time, String artist, String filePath) {
        this(-1, name, time, artist, filePath);
    }

    // 建構子，包含播放清單
    public Song(int id, String name, String time, String artist, String filePath,
            List<String> playlist) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.artist = artist;
        this.filePath = filePath;
        this.playlist = playlist;
    }

    // 取得歌曲編號
    public int getId() {
        return id;
    }

    // 取得歌曲名稱
    public String getName() {
        return name;
    }

    // 取得歌曲時長
    public String getTime() {
        return time;
    }

    // 取得歌手名稱
    public String getArtist() {
        return artist;
    }

    // 取得歌曲檔案路徑
    public String getFilePath() {
        return filePath;
    }

    // 取得歌曲所屬播放清單
    public List<String> getPlayListbySong() {
        return playlist;
    }

    // 取得是否正在播放
    public Boolean getIsPlaying() {
        return isPlaying;
    }

    // 設定歌曲所屬播放清單
    public void setPlayListbySong(List<String> plaList) {
        this.playlist = plaList;
    }

    // 設定歌曲編號
    public void setId(int id) {
        this.id = id;
    }

    // 設定歌曲名稱
    public void setName(String name) {
        this.name = name;
    }

    // 設定歌曲時長
    public void setTime(String time) {
        this.time = time;
    }

    // 設定歌手名稱
    public void setArtist(String artist) {
        this.artist = artist;
    }

    // 設定歌曲檔案路徑
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // 設定是否正在播放
    public void setIsPlaying(Boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    // 判斷兩首歌是否相等（根據名稱與歌手）
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Song other = (Song) obj;
        return Objects.equals(this.name, other.name) &&
                Objects.equals(this.artist, other.artist); // 或其他唯一值
    }

    // 轉為字串（回傳歌曲名稱）
    @Override
    public String toString() {
        return name;
    }
}
