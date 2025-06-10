package model;

/**
 * Ticket 類別：代表一張票券
 */
public class Ticket {
    private String ticketId; // 票券ID
    private Showtime showtime; // 對應的場次
    private Seat seat; // 對應的座位
    private String userEmail; // 購票者信箱
    private int order_id; // 所屬訂單編號

    /**
     * 建構子，初始化票券資料
     * 
     * @param ticketId  票券ID
     * @param showtime  場次物件
     * @param seat      座位物件
     * @param userEmail 購票者信箱
     * @param order_id  所屬訂單編號
     */
    public Ticket(String ticketId, Showtime showtime, Seat seat, String userEmail, int order_id) {
        this.ticketId = ticketId;
        this.showtime = showtime;
        this.seat = seat;
        this.userEmail = userEmail;
        this.order_id = order_id;
    }

    /**
     * 取得票券ID
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * 取得場次物件
     */
    public Showtime getShowtime() {
        return showtime;
    }

    /**
     * 取得座位物件
     */
    public Seat getSeat() {
        return seat;
    }

    /**
     * 取得所屬訂單編號
     */
    public int getorderid() {
        return order_id;
    }

    /**
     * 取得購票者信箱
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * 票券資訊字串（顯示於 ListView）
     */
    @Override
    public String toString() {
        return String.format("%-12s | %s %s | Seat: %s",
                showtime.getMovie().getName(),
                showtime.getTime().toLocalDate(),
                showtime.getTime().toLocalTime().withNano(0),
                seat.getSeatId());
    }
}
