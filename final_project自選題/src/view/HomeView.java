package view;

import controller.AppController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.PlaylistDAO;

import java.util.ArrayList;

// 首頁畫面類別
public class HomeView {
    // 主畫面容器
    private BorderPane view;
    // 播放清單名稱列表
    private ArrayList<String> playlistNames;
    // 播放清單圖片格線
    private GridPane styleGrid;
    // 主視窗
    private Stage stage;
    // 是否為深色模式
    private boolean isDarkMode;

    // 建構子，初始化首頁畫面
    public HomeView(Stage stage, boolean isDarkMode) {
        this.stage = stage;
        this.isDarkMode = isDarkMode;
        view = new BorderPane();
        view.setPadding(new Insets(20));

        // ========== Top (搜尋欄 + 搜尋按鈕) ============
        HBox topBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search songs, artists...");
        searchField.setPrefWidth(300);
        // 搜尋欄文字變動時即時過濾 playlist
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            ArrayList<String> filtered = filterPlaylists(playlistNames, newText);
            showPlaylists(filtered);
        });

        Button searchBtn = new Button("🔍");

        topBar.getChildren().addAll(searchField, searchBtn);
        topBar.setAlignment(Pos.CENTER);
        view.setTop(topBar);

        // ========== Center (播放清單圖片格線) ============
        styleGrid = new GridPane();
        styleGrid.setHgap(20);
        styleGrid.setVgap(20);
        styleGrid.setPadding(new Insets(20));
        styleGrid.setAlignment(Pos.CENTER);

        ScrollPane playlistPane = new ScrollPane(styleGrid);
        view.setCenter(playlistPane);
        BorderPane.setMargin(playlistPane, new Insets(10, 0, 10, 0));

        // 載入全部 playlist 名稱
        playlistNames = PlaylistDAO.getAllPlaylistNames();

        // 預設顯示全部 playlist
        showPlaylists(playlistNames);

        // 搜尋按鈕事件：根據關鍵字過濾 playlist 並更新顯示
        searchBtn.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                // 空字串，顯示全部
                showPlaylists(playlistNames);
            } else {
                ArrayList<String> filtered = filterPlaylists(playlistNames, keyword);
                showPlaylists(filtered);
            }
        });

        // ========== Bottom (上傳、編輯、模式切換) ============
        HBox bottomBar;

        Button uploadBtn = new Button("Upload");
        Button editBtn = new Button("Edit songs");
        Button modeBtn = new Button(isDarkMode ? "🌙" : "🔆");

        // 編輯歌曲按鈕事件
        editBtn.setOnAction(e -> {
            AppController.goEditSongsPage(stage, isDarkMode);
        });

        // 上傳歌曲按鈕事件
        uploadBtn.setOnAction(e -> {
            AppController.goUploadPage(stage, isDarkMode);
        });

        // 切換深色/亮色模式
        modeBtn.setOnAction(e -> {
            AppController.goHome(stage, !isDarkMode);
        });

        Region bSpacer = new Region();
        HBox.setHgrow(bSpacer, Priority.ALWAYS);
        bottomBar = new HBox(10, uploadBtn, editBtn, bSpacer, modeBtn);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setAlignment(Pos.CENTER);
        view.setBottom(bottomBar);
    }

    // 過濾 playlist 名稱的方法
    private ArrayList<String> filterPlaylists(ArrayList<String> allPlaylists, String keyword) {
        ArrayList<String> filtered = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (String name : allPlaylists) {
            if (name.toLowerCase().contains(lowerKeyword)) {
                filtered.add(name);
            }
        }
        return filtered;
    }

    // 根據 playlist 名稱列表顯示 UI
    private void showPlaylists(ArrayList<String> playlists) {
        styleGrid.getChildren().clear();
        // 預設 "All songs" 卡片
        String imgstr1 = "All songs.jpg";
        ImageView img1 = new ImageView(new Image("/resources/images/" + imgstr1));
        String defultname1 = "All songs";
        img1.setFitWidth(200);
        img1.setFitHeight(100);
        Button imageBtn1 = new Button("", img1);
        imageBtn1.setStyle("-fx-background-color: transparent; -fx-border-color: transpatent;");
        Label playlistname1 = new Label(defultname1);
        VBox playlistBox1 = new VBox(10, imageBtn1, playlistname1);
        styleGrid.add(playlistBox1, 0, 0);
        imageBtn1.setOnAction(e -> {
            AppController.goPlaylistPage(stage, isDarkMode);
        });

        // 依序顯示所有 playlist
        for (int i = 0; i < playlists.size(); i++) {
            String playlistName = playlists.get(i);
            Image img = new Image(getClass().getResourceAsStream("/resources/images/" + playlistName + ".jpg"));
            Canvas canvas = new Canvas(200, 100);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            // 從圖片中央裁出 2:1 區域
            double srcW = img.getWidth();
            double srcH = img.getHeight();
            double cropW = srcW;
            double cropH = srcW / 2;
            if (cropH > srcH) {
                cropH = srcH;
                cropW = srcH * 2;
            }
            double sx = (srcW - cropW) / 2;
            double sy = (srcH - cropH) / 2;
            gc.drawImage(img, sx, sy, cropW, cropH, 0, 0, 200, 100);

            Button imageBtn = new Button("", canvas);
            imageBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transpatent;");

            // 兩欄排版
            int col = (i % 2 == 0) ? 1 : 0;
            int row;
            if (i % 2 == 0) {
                row = i / 2;
            } else {
                row = i / 2 + 1;
            }

            Label playlistLabel = new Label(playlistName);
            VBox playlistBox = new VBox(10, imageBtn, playlistLabel);
            styleGrid.add(playlistBox, col, row);

            imageBtn.setOnAction(e -> {
                AppController.goChosenPlaylistPage(stage, playlistName, isDarkMode);
            });
        }
    }

    // 取得主畫面
    public BorderPane getView() {
        return view;
    }
}
