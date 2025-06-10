package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.ShowtimeDAO;
import view.HomeView;

/**
 * 主程式入口，啟動 JavaFX 應用程式。
 */
public class Main extends Application {
    /**
     * JavaFX 應用程式啟動後會執行的主方法。
     * 
     * @param primaryStage 主視窗 Stage
     */
    @Override
    public void start(Stage primaryStage) {
        // 建立一個 Timeline，設定每 10 秒執行一次 ShowtimeDAO.checkShowtime()
        // 用於檢查場次是否過期或需要更新
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> ShowtimeDAO.checkShowtime()));
        timeline.setCycleCount(Timeline.INDEFINITE);// 無限次重複
        timeline.play();// 啟動定時器
        // 建立首頁畫面物件，傳入主視窗 Stage
        HomeView home = new HomeView(primaryStage);
        // 建立 Scene 並設置尺寸
        Scene scene = new Scene(home.getView(), 400, 300);
        // 套用 CSS 樣式
        scene.getStylesheets().add(getClass().getResource("/style/signMenuStyle.css").toExternalForm());
        // 設定視窗標題與顯示內容
        primaryStage.setTitle("Ticket Booking System");
        primaryStage.setScene(scene);
        primaryStage.show();// 顯示主視窗

    }

    public static void main(String[] args) {
        launch(args);// 啟動 JavaFX 應用程式

    }

}
