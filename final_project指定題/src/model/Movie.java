package model;

/**
 * Movie 類別：代表一部電影的資料
 */
public class Movie {
    private String id; // 電影唯一識別碼
    private String name; // 電影名稱（格式：中文名(英文名)）
    private String description; // 電影簡介
    private int duration; // 片長（分鐘）
    private int ageLimit; // 年齡分級（例如：13、18）
    private String nameEN; // 英文名稱
    private String nameZH; // 中文名稱
    private String status; // 狀態（如：上映中、下檔等）

    /**
     * 建構子，初始化電影資料
     */
    public Movie(String id, String name, String description, int duration, int ageLimit, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.ageLimit = ageLimit;
        this.status = status;
    }

    // 取得電影ID
    public String getId() {
        return id;
    }

    // 取得電影名稱（中英文合併）
    public String getName() {
        return name;
    }

    // 取得英文名稱（從name字串中解析）
    public String getNameEN() {
        return name.substring(name.indexOf("(") + 1, name.indexOf(")"));
    }

    // 取得中文名稱（從name字串中解析）
    public String getNameZH() {
        return name.substring(0, name.indexOf("("));
    }

    // 取得電影簡介
    public String getDescription() {
        return description;
    }

    // 取得片長
    public int getDuration() {
        return duration;
    }

    // 取得年齡分級
    public int getAgeLimit() {
        return ageLimit;
    }

    // 取得電影狀態
    public String getStatus() {
        return status;
    }

    // 物件轉字串（顯示名稱與分級）
    @Override
    public String toString() {
        return name + " (" + ageLimit + "+)";
    }

    // 設定中文名稱，並同步更新name
    public void setNameZH(String nameZH) {
        this.nameZH = nameZH;
        name = nameZH + "(" + nameEN + ")";
    }

    // 設定英文名稱，並同步更新name
    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
        name = nameZH + "(" + nameEN + ")";
    }

    // 設定電影簡介
    public void setDescription(String description) {
        this.description = description;
    }

    // 設定片長
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // 設定年齡分級
    public void setAgeLimit(int ageLimit) {
        this.ageLimit = ageLimit;
    }

    // 設定電影狀態
    public void setStatus(String status) {
        this.status = status;
    }
}
