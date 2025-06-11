package view;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Song;
import controller.AppController;

import java.util.*;


// PlayerView 類別，負責音樂播放器畫面與功能
public class PlayerView {
    // 原始播放清單（未打亂）
    private List<Song> originalPlaylist;
    // 目前播放清單（可能已打亂）
    private List<Song> playList;
    // 目前播放歌曲的索引
    private int currentIndex;
    // 媒體播放器
    private MediaPlayer mediaPlayer;
    // 主視窗
    private Stage stage;

    // 進度條
    private Slider progressSlider;
    // 目前播放時間標籤
    private Label timeLabel;
    // 總時長標籤
    private Label totalTimeLabel;
    // 是否正在拖曳進度條
    private BooleanProperty isSeeking = new SimpleBooleanProperty(false);
    // 歌曲名稱標籤
    private Label songNameLabel;
    // 歌手名稱標籤
    private Label artistLabel;
    // 是否顯示播放清單
    private BooleanProperty isPlaylistVisible = new SimpleBooleanProperty(false);
    // 播放清單容器
    private VBox playlistBox;
    // 播放/暫停按鈕
    private Button playBtn;
    // 循環播放按鈕
    private Button cycleBtn;
    // 播放清單 ListView
    private ListView<Song> playlistView;
    // 畫面根節點
    private StackPane root;
    // 播放清單名稱
    private String playlistname;
    // 是否為深色模式
    private boolean isDarkMode;

    // 睡眠定時選項
    private Map<String, Integer> sleepOptions = new LinkedHashMap<>() {
        {
            put("10s", 10);
            put("1min", 60);
            put("10min", 600);
            put("15min", 900);
            put("30min", 1800);
            put("60min", 3600);
            put("120min", 7200);
            put("The end of the track", -1);
        }
    };

    // 建構子，初始化播放器畫面
    public PlayerView(Stage stage, Song song, List<Song> playlist, String cycleBtnStatus, String randomBtnStatus,
            String playlistname, boolean isDarkMode) {
        this.stage = stage;
        this.originalPlaylist = new ArrayList<>(playlist);
        this.playlistname = playlistname;
        this.playList = new ArrayList<>(playlist);
        this.isDarkMode = isDarkMode;

        if (randomBtnStatus.contains("rgb(221, 59, 154)")) {

            List<Song> shuffledList = new ArrayList<>(this.playList);
            shuffledList.remove(song);

            Collections.shuffle(shuffledList);

            shuffledList.add(0, song);

            this.playList = shuffledList;
        } else {

            this.playList = new ArrayList<>(originalPlaylist);
        }

        this.currentIndex = this.playList.indexOf(song);
        if (this.currentIndex < 0) {
            this.currentIndex = 0;
        }
        this.playList.get(currentIndex).setIsPlaying(true);

        buildUI(song, cycleBtnStatus, randomBtnStatus);
        playCurrentSong();
    }

    // 建立播放器 UI
    private void buildUI(Song initialSong, String cycleBtnStatus, String randomBtnStatus) {
        songNameLabel = new Label(initialSong.getName());
        artistLabel = new Label(initialSong.getArtist());
        VBox songInfoBox = new VBox(10, songNameLabel, artistLabel);

        progressSlider = new Slider();
        timeLabel = new Label("00:00");
        totalTimeLabel = new Label(initialSong.getTime());
        HBox timeHBox = new HBox(10, timeLabel, new Region(), totalTimeLabel);
        HBox.setHgrow(timeHBox.getChildren().get(1), Priority.ALWAYS);

        playBtn = new Button("⏸");
        playBtn.setOnAction(e -> togglePlayPause());

        cycleBtn = new Button("🔁");
        cycleBtn.setStyle(cycleBtnStatus);
        cycleBtn.setOnAction(e -> {
            if (cycleBtn.getStyle().contains("black") || cycleBtn.getStyle().contains("white")) {
                cycleBtn.setText("🔂");
                cycleBtn.setStyle("-fx-background-color: transparent;-fx-text-fill: rgb(221, 59, 154)");
            } else if (cycleBtn.getText().contains("🔂")) {
                cycleBtn.setText("🔁");
            } else {
                if (!isDarkMode) {
                    cycleBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
                } else {
                    cycleBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
                }
            }
        });

        Button randomBtn = new Button("🔀");
        randomBtn.setStyle(randomBtnStatus);
        randomBtn.setOnAction(e -> toggleShuffle(randomBtn));

        Button prevBtn = new Button("⏮");
        prevBtn.setOnAction(e -> playPrevious());

        Button nextBtn = new Button("⏭");
        nextBtn.setOnAction(e -> playNext());

        Button homeBtn = new Button("🏠");
        homeBtn.setOnAction(e -> goHome());

        Button listBtn = new Button("📋");
        listBtn.setOnAction(e -> togglePlaylistVisibility());

        playlistView = createPlaylistView();
        playlistBox = new VBox(new Label("Playlist"), playlistView);
        playlistBox.setPadding(new Insets(10));
        if (!isDarkMode) {
            playlistBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        } else {
            playlistBox.setStyle("-fx-background-color: #222; -fx-border-color: #555;");
        }
        playlistBox.setVisible(false);
        playlistBox.setManaged(false);

        Slider voiceSlider = new Slider(0, 1, 0.5);
        voiceSlider.setPrefWidth(100);
        updateSliderStyle(voiceSlider, 0.5);
        voiceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null)
                mediaPlayer.setVolume(newVal.doubleValue());
            updateSliderStyle(voiceSlider, newVal.doubleValue());
        });

        Button highVoice = new Button("🔊");
        highVoice.setOnAction(e -> adjustVolume(voiceSlider, 0.1));

        Button lowVoice = new Button("🔈");
        lowVoice.setOnAction(e -> adjustVolume(voiceSlider, -0.1));

        HBox voiceBox = new HBox(10, lowVoice, voiceSlider, highVoice);
        voiceBox.setAlignment(Pos.CENTER);

        Button sleepBtn = new Button("⏱");
        ContextMenu sleepMenu = new ContextMenu();
        sleepOptions.forEach((label, secs) -> {
            MenuItem item = new MenuItem(label);
            item.setOnAction(e -> startSleepTimer(secs));
            sleepMenu.getItems().add(item);
        });
        sleepBtn.setOnAction(e -> sleepMenu.show(sleepBtn, Side.TOP, 0, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controls = new HBox(10, cycleBtn, prevBtn, playBtn, nextBtn, randomBtn);
        controls.setAlignment(Pos.CENTER);
        HBox sHbox = new HBox(10, spacer, sleepBtn);
        setupSliderEvents();

        VBox centerBox = new VBox(10, songInfoBox, progressSlider, timeHBox, controls, voiceBox, sHbox);
        HBox allHBox = new HBox(10, playlistBox, centerBox);
        HBox.setHgrow(centerBox, Priority.ALWAYS);

        HBox topBar = new HBox(listBtn, new Region(), homeBtn);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);

        VBox centerContent = new VBox(10, topBar, allHBox);
        centerContent.setPadding(new Insets(20));

        BorderPane rootPane = new BorderPane(centerContent);
        root = new StackPane(rootPane);

        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE -> {
                    togglePlayPause();
                    event.consume(); // 防止滾動等預設行為
                }
                case LEFT -> {
                    if (mediaPlayer != null) {
                        Duration newTime = mediaPlayer.getCurrentTime().subtract(Duration.seconds(5));
                        mediaPlayer.seek(newTime.lessThan(Duration.ZERO) ? Duration.ZERO : newTime);
                    }
                }
                case RIGHT -> {
                    if (mediaPlayer != null) {
                        Duration duration = mediaPlayer.getTotalDuration();
                        Duration newTime = mediaPlayer.getCurrentTime().add(Duration.seconds(5));
                        mediaPlayer.seek(newTime.greaterThan(duration) ? duration : newTime);
                    }
                }
                case UP -> {
                    if (mediaPlayer != null) {
                        adjustVolume(voiceSlider, 0.1);
                    }
                }
                case DOWN -> {
                    if (mediaPlayer != null) {
                        adjustVolume(voiceSlider, -0.1);
                    }
                }
            }
        });
        Platform.runLater(() -> root.requestFocus());
        root.setFocusTraversable(true);
        root.setOnMouseClicked(e -> root.requestFocus());

    }

    // 建立播放清單 ListView
    private ListView<Song> createPlaylistView() {
        ListView<Song> view = new ListView<>();
        view.setItems(FXCollections.observableArrayList(playList));
        view.setCellFactory(param -> new ListCell<>() {
            private final Button listPlayBtn = new Button();
            private final Label label = new Label();
            private final HBox box = new HBox(10, listPlayBtn, label);

            {
                listPlayBtn.setOnAction(e -> handlePlaylistPlay(getItem()));
            }

            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setGraphic(null);
                } else {
                    label.setText(song.getName() + " - " + song.getArtist());
                    listPlayBtn.setText(song.getIsPlaying() ? "⏸" : "▶");
                    setGraphic(box);
                }
            }
        });
        return view;
    }

    // 處理播放清單中歌曲播放
    private void handlePlaylistPlay(Song clickedSong) {
        if (clickedSong == null)
            return;
        Song nowPlaying = playList.get(currentIndex);
        if (clickedSong.equals(nowPlaying)) {
            togglePlayPause();
        } else {
            nowPlaying.setIsPlaying(false);
            clickedSong.setIsPlaying(true);
            currentIndex = playList.indexOf(clickedSong);
            playCurrentSong();
        }
        playlistView.refresh();
    }

    // 播放/暫停切換
    private void togglePlayPause() {
        Song currentSong = playList.get(currentIndex);
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            playBtn.setText("▶");
            currentSong.setIsPlaying(false);
            mediaPlayer.pause();
        } else {
            playBtn.setText("⏸");
            currentSong.setIsPlaying(true);
            mediaPlayer.play();
        }
        playlistView.refresh();
    }

    // 隨機播放切換
    private void toggleShuffle(Button randomBtn) {
        // 1. 記住目前播放的歌
        Song currentSong = playList.get(currentIndex);

        // 2. 切換歌單
        if (randomBtn.getStyle().contains("rgb(221, 59, 154)")) {
            // 隨機 -> 原始
            playList = new ArrayList<>(originalPlaylist);
        } else {
            // 原始 -> 隨機
            List<Song> shuffled = new ArrayList<>(originalPlaylist);
            Collections.shuffle(shuffled);

            // 確保目前播放的歌保持在前面
            shuffled.remove(currentSong);
            shuffled.add(0, currentSong);
            playList = shuffled;
        }

        // 3. 在新的歌單中找出目前播放歌曲的 index
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).equals(currentSong)) {
                currentIndex = i;
                break;
            }
        }

        // 4. 更新播放清單顯示
        playlistView.setItems(FXCollections.observableArrayList(playList));
        playlistView.refresh();

        // 5. 更新按鈕樣式
        toggleButtonStyle(randomBtn);
    }

    // 切換按鈕樣式
    private void toggleButtonStyle(Button btn) {
        String selected = "-fx-background-color: transparent;-fx-text-fill: rgb(221, 59, 154)";
        String unselected = isDarkMode ? "-fx-background-color: transparent; -fx-text-fill: white;"
                : "-fx-background-color: transparent; -fx-text-fill: black;";
        btn.setStyle(btn.getStyle().equals(unselected) ? selected : unselected);
    }

    // 顯示/隱藏播放清單
    private void togglePlaylistVisibility() {
        boolean show = !isPlaylistVisible.get();
        isPlaylistVisible.set(show);
        playlistBox.setVisible(show);
        playlistBox.setManaged(show);
    }

    // 播放上一首
    private void playPrevious() {
        if (progressSlider.getValue() <= 3) {
            if (cycleBtn.getStyle().contains("black") || cycleBtn.getStyle().contains("white")) {
                if (currentIndex == 0) {
                } else {
                    currentIndex--;
                }
            } else if (cycleBtn.getText().contains("🔂")) {
            } else {
                if (currentIndex == 0) {
                    currentIndex = playList.size() - 1;
                } else {
                    currentIndex--;
                }
            }
        }
        playCurrentSong();
    }

    // 播放下一首
    private void playNext() {
        if (cycleBtn.getStyle().contains("black") || cycleBtn.getStyle().contains("white")) {
            if (currentIndex == playList.size() - 1) {
                mediaPlayer.stop();
                if (playlistname == "All songs") {
                    AppController.goPlaylistPage(stage, isDarkMode);
                    return;
                } else {
                    AppController.goChosenPlaylistPage(stage, playlistname, isDarkMode);
                    return;
                }

            } else {
                currentIndex++;
            }
        } else if (cycleBtn.getText().contains("🔂")) {

        } else {
            if (currentIndex == playList.size() - 1) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }
        }
        playCurrentSong();
    }

    // 返回首頁
    private void goHome() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        playList.get(currentIndex).setIsPlaying(false);
        AppController.goHome(stage, isDarkMode);
    }

    // 調整音量
    private void adjustVolume(Slider slider, double delta) {
        if (mediaPlayer == null)
            return;

        double volume = Math.max(0.0, Math.min(1.0, mediaPlayer.getVolume() + delta));
        mediaPlayer.setVolume(volume);
        if (slider != null) {
            slider.setValue(volume);
        }
    }

    // 啟動睡眠定時
    private void startSleepTimer(int seconds) {
        if (seconds == -1) {
            mediaPlayer.setOnEndOfMedia(() -> {
                stopPlayback();
            });
        } else {
            new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(Duration.seconds(seconds), e -> stopPlayback())).play();
        }
    }

    // 停止播放
    private void stopPlayback() {
        playList.get(currentIndex).setIsPlaying(false);
        mediaPlayer.stop();
        playBtn.setText("▶");
        playlistView.refresh();
    }

    // 設定進度條事件
    private void setupSliderEvents() {
        progressSlider.setOnMousePressed(e -> isSeeking.set(true));
        progressSlider.setOnMouseReleased(e -> {
            mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            isSeeking.set(false);
        });
        progressSlider.setOnMouseDragged(e -> {
            double value = progressSlider.getValue();
            timeLabel.setText(format(Duration.seconds(value)));
            updateSliderStyle(progressSlider, value);
        });
    }

    // 更新進度條樣式
    private void updateSliderStyle(Slider slider, double value) {
        double percentage = (value / slider.getMax()) * 100;
        Platform.runLater(() -> {
            Region track = (Region) slider.lookup(".track");
            if (track != null) {
                track.setStyle(String.format(
                        "-fx-background-color: linear-gradient(to right, rgb(76,112,212) %.2f%%, #ccc %.2f%%);",
                        percentage, percentage));
            }
        });
    }

    // 時間格式化
    private String format(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // 播放目前歌曲
    public void playCurrentSong() {
        Song currentSong = playList.get(currentIndex);
        playList.forEach(s -> s.setIsPlaying(false));
        currentSong.setIsPlaying(true);
        playlistView.refresh();
        songNameLabel.setText(currentSong.getName());
        artistLabel.setText(currentSong.getArtist());

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        mediaPlayer = new MediaPlayer(new Media(currentSong.getFilePath()));
        mediaPlayer.setVolume(0.5);

        mediaPlayer.setOnReady(() -> {
            progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            totalTimeLabel.setText(format(mediaPlayer.getTotalDuration()));
            mediaPlayer.play();
        });

        mediaPlayer.setOnEndOfMedia(() -> playNext());

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!isSeeking.get()) {
                progressSlider.setValue(newTime.toSeconds());
                timeLabel.setText(format(newTime));
                updateSliderStyle(progressSlider, newTime.toSeconds());
            }
        });
    }

    // 取得播放器畫面
    public StackPane getView() {
        return root;
    }
}
