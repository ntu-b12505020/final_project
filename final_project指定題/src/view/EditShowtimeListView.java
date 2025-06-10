package view;

import controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Movie;
import model.OrderDAO;
import model.SeatDAO;
import model.Showtime;
import model.ShowtimeDAO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * EditShowtimeListView 類別：管理員編輯場次清單畫面
 */
public class EditShowtimeListView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化管理員編輯場次清單畫面
     * 
     * @param stage 主視窗
     * @param movie 欲編輯場次的電影物件
     */
    public EditShowtimeListView(Stage stage, Movie movie) {
        Label title = new Label("Movie: " + movie.getName());
        title.getStyleClass().add("title");
        VBox showtimeList = new VBox(15);
        showtimeList.setPadding(new Insets(10)); // 內部 padding

        // 取得該電影所有場次
        List<Showtime> showtimes = ShowtimeDAO.getShowtimesbyMovie(movie);

        // 新增場次按鈕
        Button aBtn = new Button("Add Showtime");
        aBtn.setOnAction(e -> {
            AppController.goAddShowtime(stage, movie);
        });

        // 每個場次一個 VBox
        for (Showtime showtime : showtimes) {
            VBox showtimeBox = new VBox(5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String timestr = showtime.getTime().format(formatter);
            Label time = new Label(timestr);
            Label hall = new Label(showtime.getHall());
            Label status = new Label("Status: " + showtime.getStatus());

            // 刪除、編輯按鈕
            Button dBtn = new Button("Delete");
            Button eBtn = new Button("Edit");
            HBox manageBox = new HBox(10, eBtn, dBtn);
            showtimeBox.getChildren().addAll(time, hall, status, manageBox);

            // 刪除按鈕事件
            dBtn.setOnAction(e -> {
                ButtonType alertconfirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                ButtonType alertcancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert c = new Alert(Alert.AlertType.CONFIRMATION, "", alertconfirmButton, alertcancelButton);
                c.setTitle("Confirm cancel?");
                c.setHeaderText(null);
                c.setContentText("Movie: " + movie.getName() + "\nShowtime: " + showtime.toString());
                ImageView icon = new ImageView(String.valueOf(this.getClass().getResource("/question.png")));
                icon.setFitHeight(45);
                icon.setFitWidth(45);
                c.getDialogPane().setGraphic(icon);
                Optional<ButtonType> result = c.showAndWait();
                if (result.isPresent() && result.get() == alertconfirmButton) {
                    VBox parentVBox = (VBox) dBtn.getParent().getParent();
                    ShowtimeDAO.deleteShowtime(showtime); // 刪除場次
                    showtimeList.getChildren().remove(parentVBox); // 從畫面移除
                    OrderDAO.deleteOrdersByShowtime(showtime); // 刪除該場次所有訂單
                    SeatDAO.deleteAllSeatsByShowtime(showtime); // 刪除該場次所有座位
                }
            });

            // 編輯按鈕事件
            eBtn.setOnAction(e -> {
                AppController.goEditShowtime(stage, showtime);
            });

            showtimeBox.setStyle("-fx-border-color: #8f3f61; -fx-padding: 10;");
            showtimeList.getChildren().add(showtimeBox);
        }

        // 新增 ScrollPane 包住 showtimeList
        ScrollPane scrollPane = new ScrollPane(showtimeList);
        scrollPane.setFitToWidth(true); // 自動延展寬度
        scrollPane.setPrefHeight(400); // 設定可視高度

        // 返回按鈕
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            AppController.goAdminMovieList(stage);
        });

        // 主畫面 VBox 組合
        view = new VBox(15, title, aBtn, scrollPane, backButton);
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
}
