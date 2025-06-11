
package controller;

import javafx.scene.Scene;
import model.Song;
import javafx.stage.Stage;
import java.util.*;
import view.*;

// AppController 類別，負責不同頁面的切換
public class AppController {

    // 切換到首頁
    public static void goHome(Stage stage, boolean isDarkMode) {
        // 建立首頁畫面物件
        HomeView home = new HomeView(stage, isDarkMode);
        // 建立場景並設定大小
        Scene scene = new Scene(home.getView(), 600, 500);
        // 若為深色模式則加入深色樣式
        if (isDarkMode) {
            scene.getStylesheets().add(AppController.class.getResource("../resources/style/dark.css").toExternalForm());
        }
        // 將場景設定到視窗
        stage.setScene(scene);
    }

    // 切換到上傳歌曲頁面
    public static void goUploadPage(Stage stage, boolean isDarkMode) {
        // 建立上傳歌曲畫面物件
        UploadView view = new UploadView(stage, isDarkMode);
        // 建立場景並設定大小
        Scene scene = new Scene(view.getView(), 400, 350);
        // 若為深色模式則加入深色樣式
        if (isDarkMode) {
            scene.getStylesheets().add(AppController.class.getResource("../resources/style/dark.css").toExternalForm());
        }
        // 將場景設定到視窗
        stage.setScene(scene);
    }

    // 切換到播放清單頁面
    public static void goPlaylistPage(Stage stage, boolean isDarkMode) {
        // 建立播放清單畫面物件
        PlaylistView view = new PlaylistView(stage, isDarkMode);
        // 建立場景並設定大小
        Scene scene = new Scene(view.getView(), 600, 400);
        // 若為深色模式則加入深色樣式
        if (isDarkMode) {
            scene.getStylesheets().add(AppController.class.getResource("../resources/style/dark.css").toExternalForm());
        }
        // 將場景設定到視窗
        stage.setScene(scene);
    }

    // 切換到編輯歌曲頁面
    public static void goEditSongsPage(Stage stage, boolean isDarkMode) {
        // 建立編輯歌曲畫面物件
        EditSongsView view = new EditSongsView(stage, isDarkMode);
        // 建立場景並設定大小
        Scene scene = new Scene(view.getView(), 700, 500);
        // 若為深色模式則加入深色樣式
        if (isDarkMode) {
            scene.getStylesheets().add(AppController.class.getResource("../resources/style/dark.css").toExternalForm());
        }
        // 將場景設定到視窗
        stage.setScene(scene);
    }

    // 切換到指定播放清單內容頁面
    public static void goChosenPlaylistPage(Stage stage, String playlist, boolean isDarkMode) {
        // 建立指定播放清單畫面物件
        ChosenPlaylistView view = new ChosenPlaylistView(stage, playlist, isDarkMode);
        // 建立場景並設定大小
        Scene scene = new Scene(view.getView(), 650, 400);
        // 若為深色模式則加入深色樣式
        if (isDarkMode) {
            scene.getStylesheets().add(AppController.class.getResource("../resources/style/dark.css").toExternalForm());
        }
        // 將場景設定到視窗
        stage.setScene(scene);
    }

    // 切換到編輯單一歌曲頁面
    public static void goEditSingleSongPage(Stage stage, Song song, boolean isDarkMode) {
        // 建立編輯單一歌曲畫面物件
        EditSingleSongView view = new EditSingleSongView(stage, song, isDarkMode);
        // 建立場景並設定大小
        Scene scene = new Scene(view.getView(), 600, 400);
        // 若為深色模式則加入深色樣式
        if (isDarkMode) {
            scene.getStylesheets().add(AppController.class.getResource("../resources/style/dark.css").toExternalForm());
        }
        // 將場景設定到視窗
        stage.setScene(scene);
    }

    // 切換到播放器頁面
    public static void goPlayerPage(Stage stage, Song song, List<Song> playlist, String cycleBtnstatus,
            String randomBtnstatus, String playlistname, boolean isDarkMode) {

        // 建立播放器畫面物件
        PlayerView view = new PlayerView(stage, song, playlist, cycleBtnstatus, randomBtnstatus, playlistname,
                isDarkMode);
        // 建立場景並設定大小
        Scene scene = new Scene(view.getView(), 600, 400);
        // 若為深色模式則加入深色樣式
        if (isDarkMode) {
            scene.getStylesheets().add(AppController.class.getResource("../resources/style/dark.css").toExternalForm());
        }
        // 將場景設定到視窗
        stage.setScene(scene);
        // 讓播放器畫面取得焦點
        view.getView().requestFocus();

    }

}
