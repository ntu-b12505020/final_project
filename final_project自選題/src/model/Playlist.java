package model;

// Playlist 類別，代表一個播放清單
public class Playlist {
    // 播放清單名稱
    private String name;

    // 建構子，初始化播放清單名稱
    public Playlist(String name) {
        this.name = name;
    }

    // 取得播放清單名稱
    public String getName() {
        return name;
    }

    // 設定播放清單名稱
    public void setName(String name) {
        this.name = name;
    }

}
