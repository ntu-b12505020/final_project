package model;

/**
 * Seat 類別：代表一個座位
 */
public class Seat {
    private String seatId; // 座位編號，例如 A1, B2
    private boolean isBooked; // 是否已被預訂

    /**
     * 建構子，初始化座位編號，預設未被預訂
     * 
     * @param seatId 座位編號
     */
    public Seat(String seatId) {
        this.seatId = seatId;
        this.isBooked = false;
    }

    /**
     * 取得座位編號
     * 
     * @return 座位編號
     */
    public String getSeatId() {
        return seatId;
    }

    /**
     * 判斷此座位是否已被預訂
     * 
     * @return true 表示已被預訂，false 表示尚未預訂
     */
    public boolean isBooked() {
        return isBooked;
    }

    /**
     * 將此座位標記為已預訂
     */
    public void book() {
        isBooked = true;
    }

    /**
     * 將此座位標記為未預訂
     */
    public void unbook() {
        isBooked = false;
    }
}
