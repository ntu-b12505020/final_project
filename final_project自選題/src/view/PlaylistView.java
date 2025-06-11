package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.SongDAO;
import model.Song;
import controller.AppController;

import java.util.List;

// PlaylistView 類別，顯示所有歌曲的播放清單頁面
public class PlaylistView {
    // 主畫面容器
    private VBox view;
    // 歌曲卡片容器
    private VBox cardContainer;
    // 主視窗
    private Stage stage;
    // 循環播放按鈕
    private Button cycleBtn;
    // 隨機播放按鈕
    private Button randomBtn;
    // 是否為深色模式
    private boolean isDarkMode;

    // 建構子，初始化畫面
    public PlaylistView(Stage stage, boolean isDarkMode) {
        this.stage = stage;
        this.isDarkMode = isDarkMode;
        // 載入預設圖片
        Image img = new Image("/resources/images/All songs.jpg");
        Canvas canvas = new Canvas(50, 50);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 從圖片中央裁出 1:1 區域
        double srcW = img.getWidth();
        double srcH = img.getHeight();
        double cropSize = Math.min(srcW, srcH); // 取最小邊長
        double sx = (srcW - cropSize) / 2;
        double sy = (srcH - cropSize) / 2;
        gc.drawImage(img, sx, sy, cropSize, cropSize, 0, 0, 50, 50);

        // 放大鏡圖示 Label
        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 10px; -fx-padding: 0 5 0 10;");

        // 搜尋框
        TextField searchField = new TextField();
        searchField.setPromptText("Search songs or artists...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-effect: None;");

        // 外層包裝，模擬輸入框
        HBox searchBox = new HBox(5, searchIcon, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(5));
        if (!isDarkMode) {
            searchBox.setStyle(
                    "-fx-border-color: #ccc; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-background-color: white;");
        } else {
            searchBox.setStyle("-fx-border-color: #666; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10; " +
                    "-fx-background-color: #3c3c3c;");
        }
        searchBox.setPrefWidth(300);
        searchBox.setPrefHeight(20);
        searchField.setPromptText("Search songs, artists...");
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            updateSongList(newText);
        });

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
        HBox topBar = new HBox(10, canvas, titleBox, spacer, searchBox, homeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        // 控制列
        Button playBtn = new Button("▶");
        randomBtn = new Button("🔀");
        cycleBtn = new Button("🔁");

        if (!isDarkMode) {
            randomBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: black; -fx-border-width: 0; -fx-border-color: transparent;");
            cycleBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: black; -fx-border-width: 0; -fx-border-color: transparent;");
        } else {
            randomBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-width: 0; -fx-border-color: transparent;");
            cycleBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: white; -fx-border-width: 0; -fx-border-color: transparent;");
        }

        randomBtn.setOnAction(e -> toggleButtonStyle(randomBtn));
        cycleBtn.setOnAction(e -> toggleButtonStyle(cycleBtn));

        playBtn.setOnAction(e -> {
            List<Song> songs = SongDAO.getAllSongs();
            if (!songs.isEmpty()) {
                AppController.goPlayerPage(stage, songs.get(0), songs,
                        cycleBtn.getStyle(), randomBtn.getStyle(), "All songs", isDarkMode);
            }
        });

        HBox controlBar = new HBox(10, playBtn, randomBtn, cycleBtn);
        controlBar.setAlignment(Pos.CENTER_LEFT);

        // 歌曲清單卡片容器
        cardContainer = new VBox(10);
        cardContainer.setPadding(new Insets(10));

        // 取得所有歌曲
        List<Song> songList = SongDAO.getAllSongs();
        for (Song song : songList) {
            VBox songCard = new VBox(5);
            songCard.setPadding(new Insets(10));
            if (!isDarkMode) {
                songCard.setStyle(
                        "-fx-border-radius: 10; " +
                                "-fx-background-radius: 10; " +
                                "-fx-background-color: #e0f7ff;");
            } else {
                songCard.setStyle(
                        "-fx-border-radius: 10; " +
                                "-fx-background-radius: 10; " +
                                "-fx-background-color: #465263;");
            }
            Label nameLabel = new Label("🎵 " + song.getName());
            Label artistLabel = new Label("👤 " + song.getArtist());
            Label timeLabel = new Label(song.getTime());

            Button singlePlayBtn = new Button("▶");
            singlePlayBtn.setOnAction(e -> AppController.goPlayerPage(stage, song, songList,
                    cycleBtn.getStyle(), randomBtn.getStyle(), "All songs", isDarkMode));

            nameLabel.setMinWidth(200);
            artistLabel.setMinWidth(150);
            timeLabel.setMinWidth(60);
            timeLabel.setStyle("-fx-font-family: 'Courier New';");

            GridPane row = new GridPane();
            row.setHgap(10);
            row.add(singlePlayBtn, 0, 0);
            row.add(nameLabel, 1, 0);
            row.add(artistLabel, 2, 0);
            row.add(timeLabel, 3, 0);

            songCard.getChildren().add(row);
            cardContainer.getChildren().add(songCard);
        }

        // 可捲動歌曲區塊
        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        if (!isDarkMode) {
            scrollPane.setStyle("-fx-background-color: #f0fbff;" +
                    "-fx-background-insets: 0;" +
                    "-fx-padding: 0;");
            cardContainer.setStyle("-fx-background-color: #f0fbff;");
        } else {
            scrollPane.setStyle(
                    "-fx-background-color: #202938;" +
                            "-fx-background-insets: 0;" +
                            "-fx-padding: 0;");
            cardContainer.setStyle("-fx-background-color: #202938;");
        }
        VBox playlistCard = new VBox(10, controlBar, scrollPane);
        playlistCard.setPadding(new Insets(15));
        if (!isDarkMode) {
            playlistCard.setStyle(
                    "-fx-border-color: #007acc; " +
                            "-fx-border-radius: 12; " +
                            "-fx-background-radius: 12; " +
                            "-fx-background-color: #f0fbff;");
        } else {
            playlistCard.setStyle("-fx-border-color: rgb(0, 43, 71); " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12; " +
                    "-fx-background-color: #202938;");

        }
        view = new VBox(20, topBar, playlistCard);
        view.setPadding(new Insets(20));
        if (!isDarkMode) {
            view.setStyle("-fx-background-color: #f9f9f9;");
        } else {
            view.setStyle("-fx-background-color: #222;");
        }
    }

    // 切換按鈕樣式
    private void toggleButtonStyle(Button btn) {
        String selected = "-fx-background-color: transparent; -fx-text-fill: rgb(221, 59, 154);";
        String unselected = "-fx-background-color: transparent; "
                + (isDarkMode ? "-fx-text-fill: white;" : "-fx-text-fill: black;");
        if (btn.getStyle().equals(unselected)) {
            btn.setStyle(selected);
        } else {
            btn.setStyle(unselected);
        }
    }

    // 依關鍵字即時更新歌曲清單
    private void updateSongList(String keyword) {
        cardContainer.getChildren().clear(); // 清空原來的列表

        List<Song> songList = SongDAO.getAllSongs();
        keyword = keyword == null ? "" : keyword.toLowerCase();

        for (Song song : songList) {
            String name = song.getName().toLowerCase();
            String artist = song.getArtist().toLowerCase();

            if (keyword.isBlank() || name.contains(keyword) || artist.contains(keyword)) {
                VBox songCard = new VBox(5);
                songCard.setPadding(new Insets(10));
                if (!isDarkMode) {
                    songCard.setStyle(
                            "-fx-border-radius: 10; " +
                                    "-fx-background-radius: 10; " +
                                    "-fx-background-color: #e0f7ff;");
                } else {
                    songCard.setStyle(
                            "-fx-border-radius: 10; " +
                                    "-fx-background-radius: 10; " +
                                    "-fx-background-color: #465263;");
                }
                Label nameLabel = new Label("🎵 " + song.getName());
                Label artistLabel = new Label("👤 " + song.getArtist());
                Label timeLabel = new Label(song.getTime());

                Button singlePlayBtn = new Button("▶");
                singlePlayBtn.setOnAction(e -> AppController.goPlayerPage(stage, song, songList,
                        cycleBtn.getStyle(), randomBtn.getStyle(), "All songs", isDarkMode));

                nameLabel.setMinWidth(200);
                artistLabel.setMinWidth(150);
                timeLabel.setMinWidth(60);
                timeLabel.setStyle("-fx-font-family: 'Courier New';");

                GridPane row = new GridPane();
                row.setHgap(10);
                row.add(singlePlayBtn, 0, 0);
                row.add(nameLabel, 1, 0);
                row.add(artistLabel, 2, 0);
                row.add(timeLabel, 3, 0);

                songCard.getChildren().add(row);
                cardContainer.getChildren().add(songCard);
            }
        }
    }

    // 取得主畫面
    public VBox getView() {
        return view;
    }
}
