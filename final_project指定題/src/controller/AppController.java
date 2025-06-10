// ========== controller/AppController.java ==============
package controller;

import view.TicketRecordView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Database;
import model.Movie;
import model.Showtime;
import model.User;
import model.UserDAO;
import view.AdminMenuView;
import view.TicketBookingChosenView;
import view.UserMenuView;
import view.RegisterView;
import view.HomeView;
import view.TicketBookingView;
import view.MovieListView;

public class AppController {
    private static String currentUserEmail; // 記錄登入的人

    // 登入系統，依據user 跟admin 導向不同畫面
    public static void login(Stage stage, String email, String password) {
        String role = Database.checkLogin(email, password);
        if (role.equals("user")) {
            currentUserEmail = email; // ✅ 儲存目前使用者
            goUserMenu(stage); // ✅ 呼叫正確版本
        } else if (role.equals("admin")) {
            currentUserEmail = email; // ✅ 儲存管理員帳號也可以
            goAdminMenu(stage);
        } else {
            showWarning("Login Failed!");

        }
    }

    // 使用者取消票券畫面
    public static void goCancelTicket(Stage stage) {
        view.CancelTicketView cancelView = new view.CancelTicketView(stage, currentUserEmail);
        Scene scene = new Scene(cancelView.getView(), 500, 500);
        scene.getStylesheets().add(AppController.class.getResource("/style/recordStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 使用者主選單畫面
    public static void goUserMenu(Stage stage) {
        UserMenuView userMenu = new UserMenuView(stage, currentUserEmail); // ✅ 傳入帳號
        Scene scene = new Scene(userMenu.getView(), 400, 300);
        scene.getStylesheets().add(AppController.class.getResource("/style/signMenuStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員主選單畫面
    public static void goAdminMenu(Stage stage) {
        AdminMenuView adminMenu = new AdminMenuView(stage);
        Scene scene = new Scene(adminMenu.getView(), 400, 300);
        scene.getStylesheets().add(AppController.class.getResource("/style/signMenuStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 使用者訂票畫面（無預選電影場次）
    public static void bookTicket(Stage stage) {
        TicketBookingView bookingView = new TicketBookingView(stage, currentUserEmail); // ✅ 用目前登入者 email
        Scene scene = new Scene(bookingView.getView(), 800, 450);
        scene.getStylesheets().add(AppController.class.getResource("/style/bookingStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 使用者查看訂票紀錄
    public static void goTicketRecord(Stage stage) {
        TicketRecordView recordView = new TicketRecordView(stage, currentUserEmail);
        Scene scene = new Scene(recordView.getView(), 500, 500);
        scene.getStylesheets().add(AppController.class.getResource("/style/recordStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 顯示所有電影列表（使用者版）
    public static void goMovieList(Stage stage) {
        MovieListView movieListView = new MovieListView(stage);
        Scene scene = new Scene(movieListView.getView(), 700, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/listStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 設定目前登入的使用者 email
    public static void setCurrentUserEmail(String email) {
        currentUserEmail = email;
    }

    // 檢查使用者年齡是否達標
    public static boolean checkAge(String email, int ageLimit) {
        String birth = Database.getBirthday(email);
        if (birth == null || !birth.contains("-")) {
            showWarning("❗ Cannot find birthday for: " + email);
            return false;
        }

        try {
            int birthYear = Integer.parseInt(birth.split("-")[0]);
            int currentYear = java.time.LocalDate.now().getYear();
            int age = currentYear - birthYear;
            return age >= ageLimit;
        } catch (Exception e) {
            showWarning("❗ Birthday format error for: " + birth);
            return false;
        }
    }

    // 導向註冊畫面
    public static void register(Stage stage) {
        RegisterView registerView = new RegisterView(stage);
        Scene scene = new Scene(registerView.getView(), 400, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/signMenuStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 執行註冊邏輯與錯誤處理
    public static void performRegister(Stage stage, String email, String password, String birthdaystr) {
        String check = Database.addUser(email, password, birthdaystr);
        LocalDate birthday;
        try {
            birthday = LocalDate.parse(birthdaystr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            showWarning("Register failed! Invalid birthday format.");
            return;
        }
        User user = new User('U' + String.valueOf(UserDAO.getAllUsers().size()), email, password, birthday);
        switch (check) {
            case "true":
                UserDAO.addUser(user);
                showSuccess("Register success! Please login.");
                goHome(stage);
                break;
            case "emptyemail":
                showWarning("Register failed! Email can't be empty.");
                break;
            case "email":
                showWarning("Register failed! Email already exists.");
                break;
            case "password":
                showWarning("Register failed! Password should be more than 6 words.");
                break;
            case "birthday":
                showWarning("Register failed! Birthday is not valid.");
                break;
            case "emailno@":
                showWarning("Register failed! There should be a @ in email.");
            default:
                break;
        }
    }

    // 導向首頁（登入註冊選單）
    public static void goHome(Stage stage) {
        HomeView home = new HomeView(stage);
        Scene scene = new Scene(home.getView(), 400, 300);
        scene.getStylesheets().add(AppController.class.getResource("/style/signMenuStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員新增電影
    public static void goAddMovie(Stage stage) {
        view.AddMovieView addMovieView = new view.AddMovieView(stage);
        Scene scene = new Scene(addMovieView.getView(), 400, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/manageStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員編輯電影
    public static void goEditMovie(Stage stage, Movie movie) {
        view.EditMovieView editMovieView = new view.EditMovieView(stage, movie);
        Scene scene = new Scene(editMovieView.getView(), 400, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/manageStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員查看所有電影清單
    public static void goAdminMovieList(Stage stage) {
        view.AdminMovieListView adminMovieView = new view.AdminMovieListView(stage);
        Scene scene = new Scene(adminMovieView.getView(), 700, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/listStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員新增場次
    public static void goAddShowtime(Stage stage, Movie movie) {
        view.AddShowtimeView addShowtimeView = new view.AddShowtimeView(stage, movie);
        Scene scene = new Scene(addShowtimeView.getView(), 400, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/manageStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員查看某電影的場次列表
    public static void goEditShowtimeList(Stage stage, Movie movie) {
        view.EditShowtimeListView editShowtimeListView = new view.EditShowtimeListView(stage, movie);
        Scene scene = new Scene(editShowtimeListView.getView(), 400, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/listStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員編輯單一場次
    public static void goEditShowtime(Stage stage, Showtime showtime) {
        view.EditShowtimeView editShowtimeView = new view.EditShowtimeView(stage, showtime);
        Scene scene = new Scene(editShowtimeView.getView(), 400, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/manageStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 管理員查看所有使用者訂票資料
    public static void goViewUserBookings(Stage stage) {
        view.ViewUserBookingsView viewUserBookingsView = new view.ViewUserBookingsView(stage);
        Scene scene = new Scene(viewUserBookingsView.getView(), 650, 400);
        scene.getStylesheets().add(AppController.class.getResource("/style/recordStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 取得目前登入者 email
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    // 訂票時帶入已選電影與場次
    public static void bookTicketWithPreset(Stage stage, Movie movie, Showtime showtime) {
        TicketBookingChosenView bookingView = new TicketBookingChosenView(stage, currentUserEmail, movie, showtime);
        Scene scene = new Scene(bookingView.getView(), 800, 450);
        scene.getStylesheets().add(AppController.class.getResource("/style/bookingStyle.css").toExternalForm());
        stage.setScene(scene);
    }

    // 登出使用者並導回首頁
    public static void logout(Stage stage) {
        currentUserEmail = null; // ✅ 清除目前登入者
        goHome(stage); // 回首頁
    }

    // 顯示成功訊息視窗
    private static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(AppController.class.getResource("/check.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    // 顯示警告訊息視窗
    private static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(AppController.class.getResource("/caution.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

}
