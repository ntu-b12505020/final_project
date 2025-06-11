package view;

// 匯入 JavaFX 版面與控制元件
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
// 匯入歌曲資料存取物件
import model.SongDAO;
import model.Song;
import controller.AppController;

import java.util.List;

// 編輯所有歌曲畫面類別
public class EditSongsView {
    // 主畫面容器
    private VBox view;
    // 歌曲卡片容器
    private VBox cardContainer;

    // 建構子，初始化畫面
    public EditSongsView(Stage stage, boolean isDarkMode) {

        // 標題
        Label titleLabel = new Label("All Songs");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 顯示歌曲數量
        Label subtitleLabel = new Label(SongDAO.getAllSongsNum() + " songs");
        subtitleLabel.setStyle("-fx-text-fill: #777777;");

        // 標題區塊
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // 空白區域（推開右側按鈕）
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 返回首頁按鈕
        Button homeBtn = new Button("🏠");
        homeBtn.setOnAction(e -> AppController.goHome(stage, isDarkMode));

        // 頁首區塊
        HBox topBar = new HBox(10, titleBox, spacer, homeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        // 歌曲清單卡片容器
        cardContainer = new VBox(10);
        cardContainer.setPadding(new Insets(10));

        // 取得所有歌曲
        List<Song> songList = SongDAO.getAllSongs();
        for (Song song : songList) {
            // 歌曲卡片
            VBox songCard = new VBox(5);
            songCard.setPadding(new Insets(10));
            songCard.setStyle(isDarkMode
                    ? "-fx-background-radius: 10; -fx-background-color: #465263;"
                    : "-fx-background-radius: 10; -fx-background-color: #e0f7ff;");

            // 歌曲資訊
            Label nameLabel = new Label("🎵 " + song.getName());
            Label artistLabel = new Label("👤 " + song.getArtist());
            Label timeLabel = new Label(song.getTime());

            // 編輯按鈕
            Button editBtn = new Button("Edit");
            editBtn.setOnAction(e -> {
                AppController.goEditSingleSongPage(stage, song, isDarkMode);
            });

            // 刪除按鈕
            Button deleteBtn = new Button("➖");
            deleteBtn.setOnAction(e -> {
                SongDAO.deleteSongFromAllSongs(song.getId());
                cardContainer.getChildren().remove(songCard);
            });

            // 按鈕區塊
            HBox BtnHbox = new HBox(10, editBtn, deleteBtn);

            // 設定欄寬
            nameLabel.setMinWidth(200);
            artistLabel.setMinWidth(150);
            timeLabel.setMinWidth(60);
            timeLabel.setStyle("-fx-font-family: 'Courier New';");

            // 歌曲資訊排版
            GridPane row = new GridPane();
            row.setHgap(10);
            row.add(BtnHbox, 0, 0);
            row.add(nameLabel, 1, 0);
            row.add(artistLabel, 2, 0);
            row.add(timeLabel, 3, 0);

            songCard.getChildren().add(row);
            cardContainer.getChildren().add(songCard);
        }

        // 可捲動歌曲區塊
        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        cardContainer
                .setStyle(isDarkMode ? "-fx-background-color: #202938;" : "-fx-background-color: #f0fbff;");

        // 外框卡片
        VBox playlistCard = new VBox(10, scrollPane);
        playlistCard.setPadding(new Insets(15));
        playlistCard.setStyle(isDarkMode
                ? "-fx-border-color:rgb(0, 43, 71); -fx-border-radius: 12; -fx-background-radius: 12; -fx-background-color: #202938;"
                : "-fx-border-color: #007acc; -fx-border-radius: 12; -fx-background-radius: 12; -fx-background-color: #f0fbff;");
        // 主畫面組合
        view = new VBox(20, topBar, playlistCard);
        view.setPadding(new Insets(20));
    }

    // 取得主畫面
    public VBox getView() {
        return view;
    }
}
