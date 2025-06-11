package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.SongDAO;
import java.io.File;
import javafx.scene.media.*;
import model.Song;
import javafx.util.*;

import controller.AppController;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// 上傳歌曲畫面類別
public class UploadView {
    // 主畫面容器
    private VBox view;

    // 建構子，初始化畫面
    public UploadView(Stage stage, boolean isDarkMode) {

        // 返回首頁按鈕
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> AppController.goHome(stage, isDarkMode));

        // 歌曲名稱欄位
        Label nameLabel = new Label("Song name : ");
        TextField nameField = new TextField();

        // 歌手名稱欄位
        Label artistLabel = new Label("Artist name : ");
        TextField artistField = new TextField();

        // 上傳區塊標籤
        Label uploadLabel = new Label("Upload music (.mp3)");

        // 上傳圖示
        ImageView img = new ImageView(
                new Image(isDarkMode ? "/resources/images/uploadwhite.png" : "/resources/images/upload.png"));
        img.setFitWidth(100);
        img.setFitHeight(100);

        // 上傳按鈕區塊
        Button imageBtn = new Button("", img);
        imageBtn.setStyle("-fx-background-color: transparent;");
        VBox uploadBox = new VBox(10, img, uploadLabel);
        uploadBox.setAlignment(Pos.CENTER);
        uploadBox.setStyle(
                "-fx-border-style: dashed;" + (isDarkMode ? "-fx-border-color: white;" : " -fx-border-color: black;"));

        // 用於存放選擇的音樂檔案與 Media
        final Media[] media = new Media[1];
        final File[] file = new File[1];

        // 點擊上傳區塊選擇檔案
        uploadBox.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("choose music .mp3");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                String filePath = selectedFile.toURI().toString();
                uploadLabel.setText(selectedFile.getName());
                Media selectedMedia = new Media(filePath);
                media[0] = selectedMedia;
                file[0] = selectedFile;
            }
        });

        // 新增歌曲按鈕
        Button addBtn = new Button("Add");
        addBtn.setOnAction(e -> {
            if (media[0] == null || file[0] == null) {
                showWarning("請先選擇音樂檔案");
                return;
            }

            MediaPlayer mediaPlayer = new MediaPlayer(media[0]);

            mediaPlayer.setOnReady(() -> {
                Duration duration = media[0].getDuration();

                // 確保名稱欄位已填寫
                String name = nameField.getText().trim();
                String artist = artistField.getText().trim();
                if (name.isEmpty() || artist.isEmpty()) {
                    showWarning("請填寫歌曲名稱與演出者");
                    return;
                }

                // 建立 Song 並儲存
                Song newsong = new Song(name, formatDuration(duration), artist, file[0].toURI().toString());
                SongDAO.addSong(newsong);
                AppController.goHome(stage, isDarkMode);
            });

            // 啟動 MediaPlayer，否則不會觸發 setOnReady
            mediaPlayer.play();
            mediaPlayer.pause(); // 立刻暫停，不讓它發聲
        });

        // 按鈕區塊
        HBox btn = new HBox(10, addBtn, backBtn);
        view = new VBox(10, nameLabel, nameField, artistLabel, artistField, uploadBox, btn);
        view.setPadding(new Insets(20));
    }

    // 將 Duration 轉為 mm:ss 格式
    public static String formatDuration(Duration duaration) {
        int minutes = (int) duaration.toMinutes();
        int seconds = (int) (duaration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    // 顯示警告訊息
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(this.getClass().getResource("/resources/images/caution.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    // 取得主畫面
    public VBox getView() {
        return view;
    }
}
