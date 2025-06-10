package view;

import controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

/**
 * AdminMenuView 類別：管理員主選單畫面
 */
public class AdminMenuView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化管理員主選單畫面
     * 
     * @param stage 主視窗
     */
    public AdminMenuView(Stage stage) {
        // 歡迎標題
        Label welcomeLabel = new Label("Welcome, Admin: " + AppController.getCurrentUserEmail());
        welcomeLabel.getStyleClass().add("title");

        // 功能按鈕
        Button manageMoviesButton = new Button("Add Movies"); // 新增電影
        Button EditMoviesButton = new Button("Edit Movies"); // 編輯電影
        Button viewUserBookingsButton = new Button("View User Bookings"); // 查詢訂票紀錄
        Button backButton = new Button("Logout"); // 登出

        // 按鈕綁定功能
        manageMoviesButton.setOnAction(e -> {
            AppController.goAddMovie(stage); // ➔ 進入新增電影畫面
        });

        EditMoviesButton.setOnAction(e -> {
            AppController.goAdminMovieList(stage); // ➔ 進入編輯電影畫面
        });

        viewUserBookingsButton.setOnAction(e -> {
            AppController.goViewUserBookings(stage); // ➔ 進入查詢訂票紀錄
        });

        backButton.setOnAction(e -> {
            AppController.goHome(stage); // ➔ 回到首頁（登出）
        });

        // 主畫面 VBox 組合
        view = new VBox(10, welcomeLabel, manageMoviesButton, EditMoviesButton,
                viewUserBookingsButton,
                backButton);
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
