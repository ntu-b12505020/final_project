package view;

// 匯入 JavaFX 動畫暫停類別
import javafx.animation.PauseTransition;
// 匯入 JavaFX 版面、控制元件與圖像相關類別
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
// 匯入歌曲與播放清單資料存取物件
import model.SongDAO;
import controller.AppController;
import java.util.List;
import java.io.File;
import model.PlaylistDAO;
import model.Song;
import javafx.util.Duration;
import javafx.scene.image.ImageView;

// ChosenPlaylistView 類別，顯示指定播放清單內容
public class ChosenPlaylistView {
    // 主畫面容器
    private VBox view;
    // 存放所有歌曲清單的容器
    private VBox songListContainer;
    // 控制新增歌曲區塊是否展開
    private boolean isAddSectionVisible = false;
    // 播放清單名稱
    private String playlistName;
    // 新增歌曲按鈕
    private Button addBtn;
    // 關閉新增歌曲區塊按鈕
    private Button downBtn;
    // 播放清單內容容器
    private VBox playlistContainer;
    // 主視窗
    private Stage stage;
    // 循環播放按鈕
    private Button cycleBtn;
    // 隨機播放按鈕
    private Button randomBtn;
    // 移除歌曲按鈕
    private Button minusBtn;
    // 移除歌曲區塊容器
    private VBox removeListContainer;
    // 控制移除歌曲區塊是否展開
    private boolean isRemoveSectionVisible = false;
    // 顯示歌曲數量的標籤
    private Label playlistnum;
    // 搜尋欄
    private TextField searchField;
    // 播放清單標題
    private Label header;
    // 播放清單名稱編輯欄
    private TextField playlistNameField;
    // 是否正在編輯名稱
    private boolean isEditing = false;
    // 編輯按鈕
    private Button editBtn;
    // 是否為深色模式
    private boolean isDarkMode;

    // 建構子，初始化畫面
    public ChosenPlaylistView(Stage stage, String defaultname, boolean isDarkMode) {
        this.playlistName = defaultname;
        this.stage = stage;
        this.isDarkMode = isDarkMode;

        // 初始化播放清單內容容器
        playlistContainer = new VBox(10);
        playlistContainer.setPadding(new Insets(10));
        playlistContainer.managedProperty().bind(playlistContainer.visibleProperty());

        // 初始化移除歌曲區塊
        removeListContainer = new VBox(10);
        removeListContainer.setVisible(false);
        removeListContainer.managedProperty().bind(removeListContainer.visibleProperty());

        // 載入播放清單歌曲
        refreshPlaylistSongs();

        // 載入播放清單圖片
        Image img = new Image("/resources/images/" + playlistName + ".jpg");
        Canvas canvas = new Canvas(50, 50);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // 從圖片中央裁切 1:1 區域
        double srcW = img.getWidth();
        double srcH = img.getHeight();
        double cropSize = Math.min(srcW, srcH);
        double sx = (srcW - cropSize) / 2;
        double sy = (srcH - cropSize) / 2;
        gc.drawImage(img, sx, sy, cropSize, cropSize, 0, 0, 50, 50);

        // 播放清單名稱標題
        header = new Label(playlistName);
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // 播放清單名稱編輯欄
        playlistNameField = new TextField(playlistName);
        playlistNameField.setVisible(false);
        playlistNameField.setManaged(false);
        playlistNameField.setPrefWidth(150);

        // 搜尋欄
        searchField = new TextField();
        searchField.setPromptText("Search songs, artists...");
        searchField.setPrefWidth(280);
        if (!isDarkMode) {
            searchField.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: transparent; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10;");
        } else {
            searchField.setStyle("-fx-border-color: transparent; -fx-effect: None;");
        }

        // 搜尋圖示
        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 10px; -fx-padding: 0 5 0 10;");

        // 編輯按鈕圖示
        Image editImage = new Image(getClass().getResourceAsStream("/resources/images/pen.png"));
        ImageView imageView = new ImageView(editImage);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);

        // 編輯按鈕
        editBtn = new Button();
        editBtn.setGraphic(imageView);
        editBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // 點擊進入編輯狀態
        editBtn.setOnAction(e -> {
            if (!isEditing) {
                isEditing = true;
                header.setVisible(false);
                header.setManaged(false);

                playlistNameField.setText(playlistName);
                playlistNameField.setVisible(true);
                playlistNameField.setManaged(true);

                playlistNameField.requestFocus();
                playlistNameField.selectAll();
            }
        });

        // 編輯欄按下 Enter 或失焦時完成編輯
        playlistNameField.setOnAction(e -> finishEditing());
        playlistNameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && isEditing) {
                finishEditing();
            }
        });

        // 搜尋欄與圖示包成 HBox
        HBox searchBox = new HBox(searchIcon, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(0));
        searchBox.setSpacing(5);
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
        searchField.setPrefWidth(200);
        searchBox.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            refreshPlaylistSongsFilter(newText);
        });

        // 顯示歌曲數量
        playlistnum = new Label(Integer.toString(SongDAO.getSongsnumByPlaylist(playlistName)) + " songs");

        // 播放清單標題區塊
        VBox msgVBox = new VBox(10, header, playlistNameField, playlistnum);
        Region aspace = new Region();
        HBox.setHgrow(aspace, Priority.ALWAYS);

        // 返回首頁按鈕
        Button backBtn = new Button("🏠");
        backBtn.setOnAction(e -> AppController.goHome(stage, isDarkMode));

        // 頁首區塊
        HBox toptopBox = new HBox(10, canvas, msgVBox, editBtn, aspace, searchBox, backBtn);

        // 歌曲清單區塊
        songListContainer = new VBox(10);
        songListContainer.setVisible(false);
        songListContainer.managedProperty().bind(songListContainer.visibleProperty());

        // 主畫面組合
        view = new VBox(10, toptopBox, playlistContainer, songListContainer, removeListContainer);
        view.setPadding(new Insets(20));
    }

    // 展開新增歌曲區塊
    private void toggleAddSongSection() {
        if (!isAddSectionVisible) {
            songListContainer.setVisible(true);
            isAddSectionVisible = true;
            refreshSongList();
            addBtn.setVisible(false);
            addBtn.managedProperty().bind(addBtn.visibleProperty());
            playlistContainer.setVisible(false);
        }
    }

    // 重新載入播放清單歌曲
    private void refreshPlaylistSongs() {
        playlistContainer.getChildren().clear();

        // 播放、隨機、循環、加歌、減歌按鈕
        Button playBtn = new Button("▶");
        randomBtn = new Button("🔀");
        cycleBtn = new Button("🔁");
        addBtn = new Button("+");
        addBtn.setOnAction(e -> toggleAddSongSection());
        minusBtn = new Button("-");
        minusBtn.setOnAction(e -> toggleRemoveSongSection());

        Region bspace = new Region();
        HBox.setHgrow(bspace, Priority.ALWAYS);

        // 按鈕樣式
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

        // 播放按鈕行為
        playBtn.setOnAction(e -> {
            List<Song> songs = SongDAO.getSongsByPlaylist(playlistName);
            if (!songs.isEmpty()) {
                AppController.goPlayerPage(stage, songs.get(0), songs,
                        cycleBtn.getStyle(), randomBtn.getStyle(), playlistName, isDarkMode);
            }
        });

        // 隨機與循環按鈕行為
        randomBtn.setOnAction(e -> toggleButtonStyle(randomBtn));
        cycleBtn.setOnAction(e -> toggleButtonStyle(cycleBtn));

        // 頁首按鈕列
        HBox headerRow = new HBox(10, playBtn, randomBtn, cycleBtn, bspace, addBtn, minusBtn);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // 歌曲清單區塊
        VBox listBox = new VBox(10);
        listBox.setPadding(new Insets(10));

        // 取得播放清單所有歌曲
        List<Song> songs = SongDAO.getSongsByPlaylist(playlistName);
        for (Song song : songs) {
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

            // 歌曲資訊
            Label nameLabel = new Label("🎵 " + song.getName());
            Label artistLabel = new Label("👤 " + song.getArtist());
            Label timeLabel = new Label(song.getTime());

            // 單曲播放按鈕
            Button singlePlayBtn = new Button("▶");
            singlePlayBtn.setOnAction(e -> {
                AppController.goPlayerPage(stage, song, SongDAO.getSongsByPlaylist(playlistName),
                        cycleBtn.getStyle(), randomBtn.getStyle(), playlistName, isDarkMode);
            });

            // 設定欄寬
            nameLabel.setMinWidth(200);
            artistLabel.setMinWidth(150);
            timeLabel.setMinWidth(60);
            timeLabel.setStyle("-fx-font-family: 'Courier New';");

            // 歌曲資訊排版
            GridPane row = new GridPane();
            row.setHgap(10);
            row.add(nameLabel, 1, 0);
            row.add(artistLabel, 2, 0);
            row.add(singlePlayBtn, 0, 0);
            row.add(timeLabel, 3, 0);

            songCard.getChildren().add(row);
            listBox.getChildren().add(songCard);
        }

        // 可捲動歌曲區塊
        ScrollPane scrollPane = new ScrollPane(listBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        if (!isDarkMode) {
            scrollPane.setStyle("-fx-background-color: #f0fbff;" +
                    "-fx-background-insets: 0;" +
                    "-fx-padding: 0;");
            listBox.setStyle("-fx-background-color: #f0fbff;");
        } else {
            scrollPane.setStyle(
                    "-fx-background-color: #202938;" +
                            "-fx-background-insets: 0;" +
                            "-fx-padding: 0;");
            listBox.setStyle("-fx-background-color: #202938;");
        }

        // 外框卡片
        VBox playlistCard = new VBox(10, headerRow, scrollPane);
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

        playlistContainer.getChildren().add(playlistCard);
    }

    // 重新載入所有歌曲（新增歌曲用）
    private void refreshSongList() {
        songListContainer.getChildren().clear();

        // 標題與收合按鈕
        Label titleLabel = new Label("All Songs");
        downBtn = new Button("▼");
        downBtn.setOnAction(e -> {
            songListContainer.setVisible(false);
            isAddSectionVisible = false;
            addBtn.setVisible(true);
            playlistContainer.setVisible(true);
        });
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topHBox = new HBox(10, titleLabel, spacer, downBtn);

        VBox songsBox = new VBox(10);
        songsBox.setPadding(new Insets(10));

        // 取得所有歌曲與已在播放清單的歌曲
        List<Song> allSongs = SongDAO.getAllSongs();
        List<Song> existedSongs = SongDAO.getSongsByPlaylist(playlistName);
        List<Integer> existedIds = existedSongs.stream().map(Song::getId).toList();

        // 只顯示未在播放清單的歌曲
        for (Song song : allSongs) {
            if (!existedIds.contains(song.getId())) {
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
                nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                Label artistLabel = new Label("👤 " + song.getArtist());

                // 加入播放清單按鈕
                Button plusBtn = new Button("+");
                plusBtn.setOnAction(e -> {
                    SongDAO.addSongToPlaylist(song.getId(), playlistName);
                    showAutoCloseAlert(Alert.AlertType.INFORMATION, "null", "added to " + playlistName, 1.5);
                    playlistnum.setText(Integer.toString(SongDAO.getSongsnumByPlaylist(playlistName)) + " songs");
                    refreshSongList();
                    refreshPlaylistSongs();
                });

                HBox bottomRow = new HBox(10, artistLabel, plusBtn);
                songCard.getChildren().addAll(nameLabel, bottomRow);

                songsBox.getChildren().add(songCard);
            }
        }

        // 可捲動歌曲區塊
        ScrollPane scrollPane = new ScrollPane(songsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        if (!isDarkMode) {
            scrollPane.setStyle("-fx-background-color: #f0fbff;" +
                    "-fx-background-insets: 0;" +
                    "-fx-padding: 0;");
        } else {
            scrollPane.setStyle(
                    "-fx-background-color: #202938;" +
                            "-fx-background-insets: 0;" +
                            "-fx-padding: 0;");
            songsBox.setStyle("-fx-background-color: #222;");
        }

        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        // 外框卡片
        VBox allSongsCard = new VBox(10, topHBox, scrollPane);
        allSongsCard.setPadding(new Insets(15));
        if (!isDarkMode) {
            allSongsCard.setStyle(
                    "-fx-border-color: #999; " +
                            "-fx-border-radius: 12; " +
                            "-fx-background-radius: 12; " +
                            "-fx-background-color: #f0f0f0;");
        } else {
            allSongsCard.setStyle("-fx-border-color: #555; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12; " +
                    "-fx-background-color: #222;");
        }

        songListContainer.getChildren().setAll(allSongsCard);
    }

    // 完成編輯播放清單名稱
    private void finishEditing() {
        String newName = playlistNameField.getText().trim();
        if (!newName.isEmpty()) {
            File oldFile = new File("src/resources/images/" + playlistName + ".jpg");
            File newFile = new File("src/resources/images/" + newName + ".jpg");
            oldFile.renameTo(newFile);

            PlaylistDAO.editPlaylistName(playlistName, newName);
            SongDAO.renamePlaylist(playlistName, newName);
            playlistName = newName;
            header.setText(playlistName);
            this.playlistName = newName;
            header.setText(newName);
        } else {
            showWarning("please enter playlistname");
            return;
        }

        playlistNameField.setVisible(false);
        playlistNameField.setManaged(false);

        header.setVisible(true);
        header.setManaged(true);

        isEditing = false;
    }

    // 切換按鈕樣式（選取/未選取）
    private void toggleButtonStyle(Button btn) {
        String selected = "-fx-background-color: transparent;-fx-text-fill: rgb(221, 59, 154)";
        String unselected = "-fx-background-color: transparent; -fx-text-fill: black;";
        if (btn.getStyle().equals(unselected)) {
            btn.setStyle(selected);
        } else {
            btn.setStyle(unselected);
        }
    }

    // 依關鍵字篩選播放清單歌曲
    private void refreshPlaylistSongsFilter(String keyword) {
        playlistContainer.getChildren().clear();

        // 按鈕建立與樣式設定
        Button playBtn = new Button("▶");
        randomBtn = new Button("🔀");
        cycleBtn = new Button("🔁");
        addBtn = new Button("+");
        addBtn.setOnAction(e -> toggleAddSongSection());
        minusBtn = new Button("-");
        minusBtn.setOnAction(e -> toggleRemoveSongSection());

        Region bspace = new Region();
        HBox.setHgrow(bspace, Priority.ALWAYS);

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

        playBtn.setOnAction(e -> {
            List<Song> songs = SongDAO.getSongsByPlaylist(playlistName);
            if (!songs.isEmpty()) {
                AppController.goPlayerPage(stage, songs.get(0), songs,
                        cycleBtn.getStyle(), randomBtn.getStyle(), playlistName, isDarkMode);
            }
        });

        randomBtn.setOnAction(e -> toggleButtonStyle(randomBtn));
        cycleBtn.setOnAction(e -> toggleButtonStyle(cycleBtn));

        HBox headerRow = new HBox(10, playBtn, randomBtn, cycleBtn, bspace, addBtn, minusBtn);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox listBox = new VBox(10);
        listBox.setPadding(new Insets(10));

        // 篩選歌曲
        String keywordLower = keyword.toLowerCase();

        List<Song> songs = SongDAO.getSongsByPlaylist(playlistName).stream()
                .filter(song -> song.getName().toLowerCase().contains(keywordLower)
                        || song.getArtist().toLowerCase().contains(keywordLower))
                .toList();

        for (Song song : songs) {
            VBox songCard = new VBox(5);
            songCard.setPadding(new Insets(10));
            songCard.setStyle(
                    "-fx-border-color: #aaa; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-background-color: #e0f7ff;");

            Label nameLabel = new Label("🎵 " + song.getName());
            Label artistLabel = new Label("👤 " + song.getArtist());
            Label timeLabel = new Label(song.getTime());

            Button singlePlayBtn = new Button("▶");
            singlePlayBtn.setOnAction(e -> {
                AppController.goPlayerPage(stage, song, SongDAO.getSongsByPlaylist(playlistName),
                        cycleBtn.getStyle(), randomBtn.getStyle(), playlistName, isDarkMode);
            });

            nameLabel.setMinWidth(200);
            artistLabel.setMinWidth(150);
            timeLabel.setMinWidth(60);
            timeLabel.setStyle("-fx-font-family: 'Courier New';");

            GridPane row = new GridPane();
            row.setHgap(10);
            row.add(nameLabel, 1, 0);
            row.add(artistLabel, 2, 0);
            row.add(singlePlayBtn, 0, 0);
            row.add(timeLabel, 3, 0);

            songCard.getChildren().add(row);
            listBox.getChildren().add(songCard);
        }

        ScrollPane scrollPane = new ScrollPane(listBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox playlistCard = new VBox(10, headerRow, scrollPane);
        playlistCard.setPadding(new Insets(15));
        playlistCard.setStyle(
                "-fx-border-color: #007acc; " +
                        "-fx-border-radius: 12; " +
                        "-fx-background-radius: 12; " +
                        "-fx-background-color: #f0fbff;");

        playlistContainer.getChildren().add(playlistCard);
    }

    // 展開移除歌曲區塊
    private void toggleRemoveSongSection() {
        if (!isRemoveSectionVisible) {
            removeListContainer.setVisible(true);
            isRemoveSectionVisible = true;
            refreshRemoveList();
            minusBtn.setVisible(false);
            minusBtn.managedProperty().bind(minusBtn.visibleProperty());
            playlistContainer.setVisible(false);
        }
    }

    // 重新載入移除歌曲清單
    private void refreshRemoveList() {
        removeListContainer.getChildren().clear();

        // 標題與收合按鈕
        Label titleLabel = new Label("Remove Songs");
        Button downBtn2 = new Button("▼");
        downBtn2.setOnAction(e -> {
            removeListContainer.setVisible(false);
            isRemoveSectionVisible = false;
            minusBtn.setVisible(true);
            playlistContainer.setVisible(true);
        });
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topHBox = new HBox(10, titleLabel, spacer, downBtn2);

        VBox songsBox = new VBox(10);
        songsBox.setPadding(new Insets(10));

        // 取得播放清單所有歌曲
        List<Song> songsInPlaylist = SongDAO.getSongsByPlaylist(playlistName);
        for (Song song : songsInPlaylist) {
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
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            Label artistLabel = new Label("👤 " + song.getArtist());

            // 移除按鈕
            Button removeBtn = new Button("❌");
            removeBtn.setOnAction(e -> {
                SongDAO.removeSongFromPlaylist(song.getId(), playlistName);
                playlistnum.setText(Integer.toString(SongDAO.getSongsnumByPlaylist(playlistName)) + " songs");
                refreshRemoveList();
                refreshPlaylistSongs();
                showAutoCloseAlert(Alert.AlertType.INFORMATION, null,
                        "Remove " + song.getName() + " from " + playlistName, 1.5);
            });

            HBox bottomRow = new HBox(10, artistLabel, removeBtn);
            songCard.getChildren().addAll(nameLabel, bottomRow);

            songsBox.getChildren().add(songCard);
        }

        // 可捲動歌曲區塊
        ScrollPane scrollPane = new ScrollPane(songsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        // 外框卡片
        VBox removeCard = new VBox(10, topHBox, scrollPane);
        removeCard.setPadding(new Insets(15));
        if (!isDarkMode) {
            removeCard.setStyle(
                    "-fx-border-color: #999; " +
                            "-fx-border-radius: 12; " +
                            "-fx-background-radius: 12; " +
                            "-fx-background-color: #f0f0f0;");
        } else {
            removeCard.setStyle("-fx-border-color: #555; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12; " +
                    "-fx-background-color: #222;");
        }
        removeListContainer.getChildren().setAll(removeCard);
    }

    // 顯示自動關閉提示訊息
    public static void showAutoCloseAlert(Alert.AlertType type, String title, String content, double seconds) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(e -> alert.close());
        delay.play();
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

    // 取得主畫面容器
    public VBox getView() {
        return view;
    }
}
