package view;

import controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

/**
 * UserMenuView 類別：一般使用者主選單畫面
 */
public class UserMenuView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化一般使用者主選單畫面
     * 
     * @param stage     主視窗
     * @param userEmail 使用者信箱
     */
    public UserMenuView(Stage stage, String userEmail) {
        Label welcomeLabel = new Label("Welcome, " + userEmail + "!");
        welcomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Button searchMoviesButton = new Button("Search Movies"); // 查詢電影
        Button bookTicketButton = new Button("Book Ticket"); // 訂票
        Button viewBookingsButton = new Button("View Bookings"); // 查詢訂票紀錄
        Button cancelTicketButton = new Button("Cancel Ticket"); // 取消訂票
        Button logoutButton = new Button("Logout"); // 登出

        // 登出按鈕事件
        logoutButton.setOnAction(e -> {
            AppController.logout(stage); // ✅ 呼叫 logout
        });

        // 查詢電影按鈕事件
        searchMoviesButton.setOnAction(e -> {
            AppController.goMovieList(stage);
        });

        // 訂票按鈕事件
        bookTicketButton.setOnAction(e -> {
            AppController.bookTicket(stage);
        });

        // 查詢訂票紀錄按鈕事件
        viewBookingsButton.setOnAction(e -> {
            AppController.goTicketRecord(stage);
        });

        // 取消訂票按鈕事件
        cancelTicketButton.setOnAction(e -> {
            AppController.goCancelTicket(stage);
        });

        // 主畫面 VBox 組合
        view = new VBox(10, welcomeLabel, searchMoviesButton, bookTicketButton, viewBookingsButton, cancelTicketButton,
                logoutButton);
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
