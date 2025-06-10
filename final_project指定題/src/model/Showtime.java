package model;

import java.time.LocalDateTime;

/**
 * Showtime 類別：代表一個電影場次
 */
public class Showtime {
    private String id; // 場次ID
    private Movie movie; // 對應的電影物件
    private String hall; // 放映廳 (大廳/小廳)
    private LocalDateTime time; // 放映時間
    private String status = "closed"; // 場次狀態（如 open/closed）

    /**
     * 建構子，初始化場次資料
     * 
     * @param id     場次ID
     * @param movie  電影物件
     * @param hall   放映廳
     * @param time   放映時間
     * @param status 場次狀態
     */
    public Showtime(String id, Movie movie, String hall, LocalDateTime time, String status) {
        this.id = id;
        this.movie = movie;
        this.hall = hall;
        this.time = time;
        this.status = status;
    }

    /**
     * 取得場次ID
     */
    public String getId() {
        return id;
    }

    /**
     * 取得電影物件
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * 取得放映廳
     */
    public String getHall() {
        return hall;
    }

    /**
     * 取得場次狀態
     */
    public String getStatus() {
        return status;
    }

    /**
     * 取得放映時間
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * 設定放映廳
     */
    public void setHall(String hall) {
        this.hall = hall;
    }

    /**
     * 設定放映時間
     */
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /**
     * 設定場次狀態
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 轉為字串顯示（廳別 - 日期 時間）
     */
    @Override
    public String toString() {
        return hall + " - " + time.toLocalDate() + " " + time.toLocalTime().withNano(0);
    }
}
