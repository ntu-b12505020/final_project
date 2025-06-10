package view;

import controller.AppController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Database;
import model.Movie;
import model.Showtime;
import model.ShowtimeDAO;

import java.time.*;
import java.time.format.*;

/**
 * AddShowtimeView 類別：新增場次的畫面與邏輯
 */
public class AddShowtimeView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化新增場次畫面
     * 
     * @param stage 主視窗
     * @param movie 欲新增場次的電影物件
     */
    public AddShowtimeView(Stage stage, Movie movie) {
        // 標題
        Label title = new Label("Add New Showtime");
        // 顯示電影名稱
        Label moviename = new Label("Movie: " + movie.getName());

        // 放映廳選擇
        ComboBox<String> hallCombo = new ComboBox<>(FXCollections.observableArrayList("Big Hall", "Small Hall"));

        // 放映時間輸入欄位
        TextField dateTimeField = new TextField();
        dateTimeField.setPromptText("Format: YYYY-MM-DD HH:mm");

        // 狀態選擇
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("open", "closed"));

        // 新增與返回按鈕
        Button addButton = new Button("Add");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: gray;");

        // 新增按鈕事件
        addButton.setOnAction(e -> {
            Movie selectedMovie = movie;
            String selectedHall = hallCombo.getSelectionModel().getSelectedItem();
            String inputTime = dateTimeField.getText();
            String selectedStatus = statusCombo.getSelectionModel().getSelectedItem();

            // 檢查欄位是否皆有填寫
            if (selectedHall == null || inputTime.isEmpty() || selectedStatus == null) {
                showWarning("Please fill in all fields.");
                return;
            }

            try {
                // 解析輸入的時間字串
                LocalDateTime time = LocalDateTime.parse(inputTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                // 驗證時間是否合法
                if (time.isBefore(LocalDateTime.now()) || !isValidDate(inputTime.substring(0, 10))
                        || Integer.parseInt(inputTime.substring(11, 13)) >= 24) {
                    showWarning("❗ Not a valid showtime");
                } else {
                    // 產生新的場次ID
                    String showtimeId = "S" + (ShowtimeDAO.getMaxId() + 3);
                    // 建立新場次物件並加入資料庫
                    Showtime newShowtime = new Showtime(showtimeId, selectedMovie, selectedHall, time, selectedStatus);
                    Database.addShowtime(newShowtime);

                    // 新增後刷新畫面
                    AppController.goAddShowtime(stage, selectedMovie);
                }
            } catch (Exception ex) {
                showWarning("❗ Time format error! Please use YYYY-MM-DD HH:mm");
            }
        });

        // 返回按鈕事件
        backButton.setOnAction(e -> {
            AppController.goEditShowtimeList(stage, movie);
        });

        // 按鈕區塊
        HBox doneBox = new HBox(10, addButton, backButton);

        // 主畫面 VBox 組合
        view = new VBox(10, title, moviename, hallCombo, dateTimeField, statusCombo, doneBox);
        view.setPadding(new Insets(20));
    }

    /**
     * 取得畫面 VBox
     * 
     * @return VBox 物件
     */
    public VBox getView() {
        return view;
    }

    /**
     * 驗證日期格式是否合法
     * 
     * @param aDate 日期字串（yyyy-MM-dd）
     * @return 合法則回傳 true，否則 false
     */
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

            return true;

        } catch (DateTimeParseException e) {
            // 如果日期格式錯誤，則返回 false
            return false;
        }
    }

    /**
     * 檢查該月份是否有這一天
     * 
     * @param month 月份
     * @param day   日
     * @param year  年
     * @return 合法則回傳 true，否則 false
     */
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

    /**
     * 判斷是否為閏年
     * 
     * @param year 年份
     * @return 閏年回傳 true，否則 false
     */
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }

    /**
     * 顯示警告訊息視窗
     * 
     * @param message 訊息內容
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(this.getClass().getResource("/caution.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }
}
