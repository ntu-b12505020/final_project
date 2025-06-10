package model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order 類別：代表一筆訂單資料
 */
public class Order {
    private int orderId; // 訂單編號
    private String userEmail; // 使用者信箱
    private LocalDateTime orderTime; // 訂單成立時間
    private List<Ticket> tickets; // 此訂單所包含的票券
    private String showtime_id; // 場次ID
    private int money; // 訂單金額
    private int popcorn; // 爆米花數量
    private int cola; // 可樂數量

    /**
     * 建構子，初始化訂單資料
     * 
     * @param orderId     訂單編號
     * @param userEmail   使用者信箱
     * @param orderTime   訂單成立時間
     * @param tickets     訂單票券清單
     * @param showtime_id 場次ID
     * @param money       訂單金額
     * @param popcorn     爆米花數量
     * @param cola        可樂數量
     */
    public Order(int orderId, String userEmail, LocalDateTime orderTime, List<Ticket> tickets, String showtime_id,
            int money, int popcorn, int cola) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.orderTime = orderTime;
        this.tickets = tickets;
        this.showtime_id = showtime_id;
        this.money = money;
        this.popcorn = popcorn;
        this.cola = cola;
    }

    /**
     * 取得訂單編號
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * 取得使用者信箱
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * 設定訂單金額
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * 取得訂單成立時間
     */
    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    /**
     * 取得訂單票券清單
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

    /**
     * 取得爆米花數量
     */
    public int getPopcorn() {
        return popcorn;
    }

    /**
     * 取得可樂數量
     */
    public int getCola() {
        return cola;
    }

    /**
     * 取得場次ID
     */
    public String getShowtimeid() {
        return showtime_id;
    }

    /**
     * 取得訂單金額
     */
    public int getMoney() {
        return money;
    }

    /**
     * 回傳訂單簡易描述字串
     */
    @Override
    public String toString() {
        return "Order #" + orderId + " (" + orderTime.toString() + ")";
    }
}
