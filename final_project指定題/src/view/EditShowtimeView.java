package view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import controller.AppController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Database;
import model.SeatDAO;
import model.Showtime;

/**
 * EditShowtimeView 類別：編輯場次資訊的畫面與邏輯
 */
public class EditShowtimeView {
    private VBox view; // 主畫面 VBox
    private ScrollPane scrollPane; // 捲軸容器

    /**
     * 建構子，初始化編輯場次畫面
     * 
     * @param stage    主視窗
     * @param showtime 欲編輯的場次物件
     */
    public EditShowtimeView(Stage stage, Showtime showtime) {
        Label nameLabel = new Label("Movie Name:" + showtime.getMovie().getName());
        Label hallLabel = new Label("Hall: ");
        ComboBox<String> hallCombo = new ComboBox<>(FXCollections.observableArrayList("Big Hall", "Small Hall"));
        hallCombo.setValue(showtime.getHall());
        Label statusLabel = new Label("Status: ");
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("open", "closed"));
        statusCombo.setValue(showtime.getStatus());
        Label timeLabel = new Label("Time: ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timestr = showtime.getTime().format(formatter);
        TextField timeField = new TextField(timestr);
        timeField.setPrefWidth(300);

        Button doneButton = new Button("Done");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: gray;");

        // 完成按鈕事件
        doneButton.setOnAction(e -> {
            String hall = hallCombo.getSelectionModel().getSelectedItem();
            String time = timeField.getText().trim();
            String status = statusCombo.getSelectionModel().getSelectedItem();

            ButtonType alertconfirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType alertcancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert c = new Alert(Alert.AlertType.CONFIRMATION, "", alertconfirmButton, alertcancelButton);
            c.setTitle("Confirm edit?");
            c.setHeaderText(null);
            c.setContentText("Movie: " + showtime.getMovie().getName() + "\nHall: " + hall + "\nTime: " + time);
            ImageView icon = new ImageView(String.valueOf(this.getClass().getResource("/question.png")));
            icon.setFitHeight(45);
            icon.setFitWidth(45);
            c.getDialogPane().setGraphic(icon);
            Optional<ButtonType> result = c.showAndWait();
            if (result.isPresent() && result.get() == alertconfirmButton) {
                if (hall.isEmpty() || time.isEmpty()) {
                    showWarning("All fields must be filled.");
                    return;
                }

                LocalDateTime timeL = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                if (!hall.equals(showtime.getHall()) || !timeL.equals(showtime.getTime())) {
                    // 若場次時間或廳別有異動，則刪除原本所有座位資料
                    SeatDAO.deleteAllSeatsByShowtime(showtime);
                }
                // 更新原本的 showtime
                showtime.setHall(hall);
                showtime.setTime(timeL);
                showtime.setStatus(status);
                Database.updateShowtime(showtime); // 同步資料庫

                showSuccess("Showtime updated successfully!");
                AppController.goEditShowtimeList(stage, showtime.getMovie());
            }
        });

        // 返回按鈕事件
        backButton.setOnAction(e -> {
            AppController.goEditShowtimeList(stage, showtime.getMovie());
        });

        // 按鈕區塊
        HBox doneBox = new HBox(10, doneButton, backButton);

        // 主畫面 VBox 組合
        view = new VBox(10, nameLabel, hallLabel, hallCombo, timeLabel, timeField, statusLabel, statusCombo, doneBox);

        // 捲軸設定
        scrollPane = new ScrollPane(view);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPadding(new Insets(20));
    }

    /**
     * 取得畫面 ScrollPane
     * 
     * @return ScrollPane 物件
     */
    public ScrollPane getView() {
        return scrollPane;
    }

    /**
     * 顯示成功訊息視窗
     * 
     * @param message 訊息內容
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(this.getClass().getResource("/check.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
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
