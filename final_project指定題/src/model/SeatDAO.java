package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SeatDAO 類別：負責與資料庫進行座位與票券相關的存取操作
 */
public class SeatDAO {
    // 資料庫連線字串
    private static final String URL = "jdbc:sqlite:cinema.db";

    /**
     * 新增一筆已訂座位資料
     * 
     * @param seat      座位物件
     * @param showtime  場次物件
     * @param userEmail 使用者信箱
     * @param order_id  訂單編號
     */
    public static void insertBookedSeat(Seat seat, Showtime showtime, String userEmail, int order_id) {
        String sql = "INSERT INTO seats (seat_id, showtime_id, user_email, order_id) VALUES(?, ?, ?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seat.getSeatId());
            stmt.setString(2, showtime.getId());
            stmt.setString(3, userEmail);
            stmt.setInt(4, order_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得指定場次已被訂走的座位編號清單
     * 
     * @param showtime 場次物件
     * @return 已被訂走的座位編號清單
     */
    public static List<String> getBookedSeatByShowtime(Showtime showtime) {
        String sql = "SELECT seat_id from seats WHERE showtime_id = ?";
        List<String> bookedSeatId = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, showtime.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookedSeatId.add(rs.getString("seat_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSeatId;
    }

    /**
     * 依訂單編號取得所有票券
     * 
     * @param orderId 訂單編號
     * @return 該訂單的票券清單
     */
    static List<Ticket> getTicketsByOrderId(int orderId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT seat_id, showtime_id, user_email, order_id FROM seats WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String seatId = rs.getString("seat_id");
                String showtimeId = rs.getString("showtime_id");
                String userEmail = rs.getString("user_email");
                int order_id = rs.getInt("order_id");

                Showtime showtime = ShowtimeDAO.getShowtimeById(showtimeId);
                Seat seat = new Seat(seatId); // 產生座位物件
                Ticket ticket = new Ticket(seatId + "-" + showtimeId, showtime, seat, userEmail, order_id);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * 刪除某訂單下的所有票券
     * 
     * @param orderId 訂單編號
     */
    public static void deleteSeatsByOrderId(int orderId) {
        String sql = "DELETE FROM seats WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得指定使用者的所有票券，並依訂單分群
     * 
     * @param userEmail 使用者信箱
     * @return Map<訂單編號, 該訂單的票券清單>
     */
    public static Map<Integer, List<Ticket>> getTicketsGroupedByOrderId(String userEmail) {
        Map<Integer, List<Ticket>> orderMap = new HashMap<>();
        String sql = "SELECT seat_id, showtime_id, user_email, order_id FROM seats WHERE user_email = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    String showtimeId = rs.getString("showtime_id");
                    Showtime showtime = ShowtimeDAO.getShowtimeById(showtimeId);
                    String seatId = rs.getString("seat_id");
                    Seat seat = new Seat(seatId);
                    seat.book();
                    Ticket ticket = new Ticket(showtimeId + "-" + seatId, showtime, seat, userEmail, orderId);

                    orderMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderMap;
    }

    /**
     * 取消指定座位的訂位
     * 
     * @param seat     座位物件
     * @param showtime 場次物件
     * @return 取消成功回傳 true，否則 false
     */
    public static boolean cancelBookedSeat(Seat seat, Showtime showtime) {
        String sql = "DELETE FROM seats WHERE seat_id = ? and showtime_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, seat.getSeatId());
            stmt.setString(2, showtime.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取得指定使用者的所有票券
     * 
     * @param userEmail 使用者信箱
     * @return 該使用者的所有票券清單
     */
    public static List<Ticket> getTicketsByUser(String userEmail) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT seat_id, showtime_id, user_email, order_id FROM seats WHERE user_email = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String showtimeId = rs.getString("showtime_id");
                    Showtime showtime = ShowtimeDAO.getShowtimeById(showtimeId);
                    String email = rs.getString("user_email");
                    String seatId = rs.getString("seat_id");
                    int orderId = rs.getInt("order_id");

                    Seat seat = new Seat(seatId);
                    seat.book();

                    tickets.add(new Ticket(orderId + "-" + showtimeId + "-" + seatId, showtime, seat, email, orderId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    /**
     * 取得所有票券
     * 
     * @return 全部票券清單
     */
    public static List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT seat_id, showtime_id, user_email, order_id FROM seats";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String showtimeId = rs.getString("showtime_id");
                Showtime showtime = ShowtimeDAO.getShowtimeById(showtimeId);
                String email = rs.getString("user_email");
                String seatId = rs.getString("seat_id");
                int order_id = rs.getInt("order_id");
                Seat seat = new Seat(seatId);
                seat.book();
                tickets.add(new Ticket(showtimeId + "-" + seatId, showtime, seat, email, order_id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    /**
     * 刪除指定場次的所有座位資料
     * 
     * @param showtime 場次物件
     * @return 刪除成功回傳 true，否則 false
     */
    public static boolean deleteAllSeatsByShowtime(Showtime showtime) {
        String sql = "DELETE FROM seats WHERE showtime_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, showtime.getId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}