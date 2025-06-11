package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.HomeView;

// 主程式進入點，繼承 Application
public class Main extends Application {
    // 覆寫 start 方法，應用程式啟動時執行
    @Override
    public void start(Stage primaryStage) {
        // 建立首頁畫面物件，預設非深色模式
        HomeView home = new HomeView(primaryStage, false);
        // 建立場景並設定大小
        Scene scene = new Scene(home.getView(), 600, 500);
        // 將場景設定到主視窗
        primaryStage.setScene(scene);
        // 設定視窗標題
        primaryStage.setTitle("Music Player");
        // 顯示主視窗
        primaryStage.show();
    }

    // 主程式進入點
    public static void main(String[] args) {
        // 啟動 JavaFX 應用程式
        launch(args);
    }
}
