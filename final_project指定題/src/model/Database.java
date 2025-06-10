package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Database {
    private static HashMap<String, String> users = new HashMap<>();
    private static List<Ticket> tickets = new ArrayList<>();
    private static HashMap<String, String> birthdays = new HashMap<>();
    private static MovieDAO movieDAO = new MovieDAO();
    private static List<Movie> movies = new ArrayList<>();
    private static List<Showtime> showtimes = ShowtimeDAO.getAllShowtimes();
    private static HashMap<String, List<Seat>> seatMap = new HashMap<>(); // key: showtimeId

    static {
        movies = movieDAO.getAllMovies();
        tickets = SeatDAO.getAllTickets();

        List<User> userList = UserDAO.getAllUsers();
        for (User u : userList) {
            users.put(u.getEmail(), u.getPassword());
            birthdays.put(u.getEmail(), u.getBirthday().toString());
        }

        // 預設帳號
        users.put("user@test.com", "user123");
        users.put("admin@test.com", "admin123");
        birthdays.put("user@test.com", "2000-01-01");
        birthdays.put("admin@test.com", "1990-01-01");
        UserDAO.defaultuser();

        // 預設座位
        showtimes = ShowtimeDAO.getAllShowtimes();
        for (Showtime s : showtimes) {
            List<Seat> seats = new ArrayList<>();
            if (s.getHall().equals("Big Hall")) {
                for (int row = 1; row <= 11; row++) {
                    for (int col = 1; col <= 38; col++) {
                        seats.add(new Seat((char) (row + 64) + String.valueOf(col)));
                    }
                }
                for (int col = 1; col <= 39; col++) {
                    seats.add(new Seat("L" + String.valueOf(col)));
                }
                for (int col = 1; col <= 38; col++) {
                    seats.add(new Seat("M" + String.valueOf(col)));
                }
            } else if (s.getHall().equals("Small Hall")) {
                for (int row = 1; row <= 9; row++) {
                    for (int col = 1; col <= 16; col++) {
                        seats.add(new Seat((char) (row + 64) + String.valueOf(col)));
                    }
                }
            }
            seatMap.put(s.getId(), seats);
        }

    }

    // 用 user_email 將該用戶的 ticket 組成群組
    public static Map<String, List<Ticket>> getTicketsGroupedByUser() {
        List<Ticket> allTickets = getAllTickets();
        Map<String, List<Ticket>> map = new HashMap<>();
        for (Ticket t : allTickets) {
            map.computeIfAbsent(t.getUserEmail(), k -> new ArrayList<>()).add(t);
        }
        return map;
    }

    // 新增一部電影
    public static void addMovie(Movie movie) {
        movies.add(movie);
        MovieDAO.addMovie(movie);
    }

    // 新增一張票
    public static void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    // 根據使用者 email 查詢訂票紀錄
    public static List<Ticket> getTicketsByUser(String email) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets) {
            if (t.getUserEmail().equals(email)) {
                result.add(t);
            }
        }
        return result;
    }

    // 根據 Ticket ID 找票
    public static Ticket findTicketById(String ticketId) {
        for (Ticket t : tickets) {
            if ((t.getShowtime().getId() + "-" + t.getSeat().getSeatId()).equals(ticketId)) {
                return t;
            }
        }
        return null;
    }

    // 退票（退座位 + 移除紀錄）
    public static boolean cancelTicket(String ticketId) {
        Ticket ticket = findTicketById(ticketId);
        if (ticket == null)
            return false;
        // 退座位
        List<Seat> seats = seatMap.get(ticket.getShowtime().getId());
        for (Seat seat : seats) {
            if (seat.getSeatId().equals(ticket.getSeat().getSeatId())) {
                seat.unbook();
                SeatDAO.cancelBookedSeat(seat, ticket.getShowtime());
            }
        }
        return tickets.remove(ticket);
    }

    // 確定登入的是 user 或 admin
    public static String checkLogin(String email, String password) {
        if (users.containsKey(email) && users.get(email).equals(password)) {
            if (email.startsWith("admin")) {
                return "admin";
            } else {
                return "user";
            }
        }
        return "none";
    }

    // 新增用戶
    public static String addUser(String email, String password, String birthday) {
        if (email.equals("")) {
            return "emptyemail";
        }
        if (users.containsKey(email)) {
            return "email";
        }
        if (password.length() < 6) {
            return "password";
        }
        if (!isValidDate(birthday)) {
            return "birthday";
        }
        if (!email.contains("@")) {
            return "emailno@";
        }

        users.put(email, password);
        birthdays.put(email, birthday);
        System.out.println("Current users: " + users); // 调试代码
        return "true";
    }

    public static List<Movie> getMovies() {
        return movieDAO.getAllMovies(); // 從 MySQL 撈
    }

    // 取得指定電影的所有場次
    public static List<Showtime> getShowtimesByMovie(Movie movie) {
        List<Showtime> result = new ArrayList<>();
        showtimes = ShowtimeDAO.getAllShowtimes();
        for (Showtime s : showtimes) {
            if (s.getMovie().getId().equals(movie.getId())) {
                result.add(s);
            }
        }
        return result;
    }

    // 取得指定場次的所有座位
    public static List<Seat> getSeatsByShowtime(Showtime showtime) {
        return seatMap.get(showtime.getId());
    }

    // 取得指定場次剩餘可訂座位數
    public static int getSeatNumByShowtime(Showtime showtime) {
        List<Seat> seats = getSeatsByShowtime(showtime);
        int seatNum = 0;
        for (Seat s : seats) {
            if (!s.isBooked()) {
                seatNum += 1;
            }
        }
        return seatNum;
    }

    // 取得指定 email 的生日
    public static String getBirthday(String email) {
        return birthdays.get(email);
    }

    public static void bookSeat(Showtime showtime, String seatId, String userEmail, int order_id) {
        List<Seat> seats = seatMap.get(showtime.getId());
        for (Seat seat : seats) {
            if (seat.getSeatId().equals(seatId)) {
                seat.book();
                SeatDAO.insertBookedSeat(seat, showtime, userEmail, order_id);
            }
        }

    }

    // 利用movie_id尋找movie
    public static Movie findMovieById(String movieId) {
        for (Movie m : movies) {
            if (m.getId().equals(movieId)) {
                return m;
            }
        }
        return null;
    }
    ////////////////////////
    /// 管理員功能 ///
    /// 新增電影、場次、座位 ///
    /// //////////////////////

    // 新增showtime
    public static void addShowtime(Showtime s) {
        // 檢查該廳在該時間是否已經有場次
        boolean used = false;
        showtimes = ShowtimeDAO.getAllShowtimes();
        for (Showtime sh : showtimes) {
            if (sh.getHall().equals(s.getHall()) && sh.getTime().equals(s.getTime())) {
                showWarning("The hall has been used."); // 已有場次則警告
                used = true;
                break;
            }
        }
        // 若該廳該時段沒被使用，則新增場次與座位
        if (!used) {
            showtimes.add(s); // 加入本地 showtimes
            ShowtimeDAO.addshowtime(s); // 寫入資料庫
            List<Seat> seats = new ArrayList<>();
            // 根據影廳類型產生座位
            if (s.getHall().equals("Big Hall")) {
                // 大廳：A1~K38、L1~L39、M1~M38
                for (int row = 1; row <= 11; row++) {
                    for (int col = 1; col <= 38; col++) {
                        seats.add(new Seat((char) (row + 64) + String.valueOf(col)));
                    }
                }
                for (int col = 1; col <= 39; col++) {
                    seats.add(new Seat("L" + String.valueOf(col)));
                }
                for (int col = 1; col <= 38; col++) {
                    seats.add(new Seat("M" + String.valueOf(col)));
                }
            } else if (s.getHall().equals("Small Hall")) {
                // 小廳：A1~I16
                for (int row = 1; row <= 9; row++) {
                    for (int col = 1; col <= 16; col++) {
                        seats.add(new Seat((char) (row + 64) + String.valueOf(col)));
                    }
                }
            }
            showtimes = ShowtimeDAO.getAllShowtimes(); // 重新取得所有場次
            seatMap.put(s.getId(), seats); // 存入座位對應表
            showSuccess("✅ Showtime added!"); // 顯示成功訊息
        }

    }

    // 取得所有showtime
    public static List<Showtime> getAllShowtimes() {
        showtimes = ShowtimeDAO.getAllShowtimes();
        return showtimes;
    }

    // 取得所有ticket
    public static List<Ticket> getAllTickets() {
        return SeatDAO.getAllTickets();
    }

    // 更新電影資訊
    public static void updateMovie(Movie updatedMovie) {
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            if (m.getId().equals(updatedMovie.getId())) {
                movies.set(i, updatedMovie);
                break;
            }
        }
        MovieDAO.update(updatedMovie);
    }

    // 更新showtime資訊
    public static void updateShowtime(Showtime updatedShowtime) {
        for (int i = 0; i < showtimes.size(); i++) {
            Showtime s = showtimes.get(i);
            if (s.getId().equals(updatedShowtime.getId())) {
                showtimes.set(i, updatedShowtime);
                break;
            }
        }
        ShowtimeDAO.update(updatedShowtime);
    }

    // 確定是否為有效日期
    private static boolean isValidDate(String aDate) {
        try {
            // 設定日期格式為 yyyy-MM-dd
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
            LocalDate date = LocalDate.parse(aDate, formatter); // 如果格式不對會拋出異常
            // 1. 檢查月份是否在 1 到 12 範圍內
            int month = date.getMonthValue();
            if (month < 1 || month > 12) {
                return false;
            }

            // 2. 檢查日期與月份的搭配
            int day = date.getDayOfMonth();
            if (!isValidDayForMonth(month, day, date.getYear())) {
                return false;
            }

            // 3. 檢查日期是否是未來日期
            if (date.isAfter(LocalDate.now())) {
                return false;
            }

            return true;

        } catch (DateTimeParseException e) {
            // 如果日期格式錯誤，則返回 false
            return false;
        }
    }

    // 檢查該月份是否有這一天
    private static boolean isValidDayForMonth(int month, int day, int year) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return day >= 1 && day <= 31; // 31天的月份
            case 4:
            case 6:
            case 9:
            case 11:
                return day >= 1 && day <= 30; // 30天的月份
            case 2:
                // 判斷是否為閏年，閏年2月有29天，平年2月只有28天
                if (isLeapYear(year)) {
                    return day >= 1 && day <= 29;
                } else {
                    return day >= 1 && day <= 28;
                }
            default:
                return false; // 如果月份不對，直接返回 false
        }
    }

    // 判斷是否為閏年
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }

    // 成功提示
    private static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(Database.class.getResource("/check.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    // 警告提示
    private static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(Database.class.getResource("/caution.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }
}
