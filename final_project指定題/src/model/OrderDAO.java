package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderDAO 類別：負責與資料庫進行訂單相關的存取操作
 */
public class OrderDAO {
    // 資料庫連線字串
    private static final String URL = "jdbc:sqlite:cinema.db";

    /**
     * 新增訂單
     * 
     * @param userEmail 使用者信箱
     * @param showtime  場次物件
     * @param money     訂單金額
     * @param popcorn   爆米花數量
     * @param cola      可樂數量
     * @return 新增訂單的 order_id，失敗回傳 -1
     */
    public static int addOrder(String userEmail, Showtime showtime, int money, int popcorn, int cola) {
        String sql = "INSERT INTO \"order\" (user_email, order_time, showtime_id, money, popcorn, cola) VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, userEmail);
            stmt.setString(2, showtime.getId());
            stmt.setInt(3, money);
            stmt.setInt(4, popcorn);
            stmt.setInt(5, cola);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // 回傳自動產生的訂單編號
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 依 showtime 刪除所有相關訂單
     * 
     * @param showtime 欲刪除的場次
     */
    public static void deleteOrdersByShowtime(Showtime showtime) {
        String sql = "DELETE FROM \"order\" WHERE order_id IN (SELECT order_id FROM seats WHERE showtime_id = ?)";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("刪除 order for showtime id: " + showtime.getId());
            stmt.setString(1, showtime.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得指定使用者的所有訂單
     * 
     * @param userEmail 使用者信箱
     * @return 該使用者的訂單清單
     */
    public static List<Order> getOrdersByUser(String userEmail) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT order_id, user_email, order_time, showtime_id, money, popcorn, cola FROM \"order\" WHERE user_email = ? ORDER BY order_time DESC";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String email = rs.getString("user_email");
                LocalDateTime orderTime = rs.getTimestamp("order_time").toLocalDateTime();
                String showtimeId = rs.getString("showtime_id");
                int money = rs.getInt("money");
                int popcorn = rs.getInt("popcorn");
                int cola = rs.getInt("cola");

                // 取得此訂單的票券列表
                List<Ticket> tickets = SeatDAO.getTicketsByOrderId(orderId);

                Order order = new Order(orderId, email, orderTime, tickets, showtimeId, money, popcorn, cola);
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * 取得所有訂單，並依使用者分群
     * 
     * @return Map<使用者信箱, 該使用者的訂單清單>
     */
    public static Map<String, List<Order>> getOrdersGroupedByUser() {
        Map<String, List<Order>> map = new HashMap<>();
        String sql = "SELECT order_id, user_email, order_time, showtime_id, money, popcorn, cola FROM \"order\" ORDER BY user_email, order_time DESC";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String email = rs.getString("user_email");
                LocalDateTime orderTime = rs.getTimestamp("order_time").toLocalDateTime();
                String showtimeId = rs.getString("showtime_id");
                int money = rs.getInt("money");
                int popcorn = rs.getInt("popcorn");
                int cola = rs.getInt("cola");

                // 取得此訂單的票券列表
                List<Ticket> tickets = SeatDAO.getTicketsByOrderId(orderId);

                Order order = new Order(orderId, email, orderTime, tickets, showtimeId, money, popcorn, cola);
                order.setMoney(money); // 設定訂單金額（如有需要）

                // 加入 map（依 email 分群）
                map.computeIfAbsent(email, k -> new ArrayList<>()).add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 訂座位並綁定訂單
     * 
     * @param showtime  場次
     * @param seatId    座位ID
     * @param userEmail 使用者信箱
     * @param orderId   訂單編號
     */
    public static void bookSeatWithOrderId(Showtime showtime, String seatId, String userEmail, int orderId) {
        String sql = "INSERT INTO seats (seat_id, showtime_id, user_email, order_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seatId);
            stmt.setString(2, showtime.getId());
            stmt.setString(3, userEmail);
            stmt.setInt(4, orderId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 刪除指定訂單（同時刪除相關票券）
     * 
     * @param orderId 欲刪除的訂單編號
     * @return 刪除成功回傳 true，否則 false
     */
    public static boolean deleteOrder(int orderId) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false); // 啟用交易
            try {
                // 先刪票券
                SeatDAO.deleteSeatsByOrderId(orderId);

                // 再刪訂單
                String sql = "DELETE FROM \"order\" WHERE order_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, orderId);
                    int affected = stmt.executeUpdate();
                    if (affected == 1) {
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
