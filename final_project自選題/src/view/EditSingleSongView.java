package view;

// 匯入檔案與 URI 處理
import java.io.File;
import java.net.URI;
import java.util.Optional;

// 匯入控制器與 JavaFX 相關類別
import controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Song;
import model.SongDAO;

// 單一歌曲編輯畫面類別
public class EditSingleSongView {
    // 主畫面容器
    private VBox view;
    // 捲動容器
    private ScrollPane scrollPane;
    // 檔案名稱
    String fileName;

    // 建構子，初始化畫面
    public EditSingleSongView(Stage stage, Song song, boolean isDarkMode) {
        // 取得歌曲檔案路徑
        String filePath = song.getFilePath();
        fileName = "";

        // 取得檔案名稱
        if (filePath != null && !filePath.isEmpty()) {
            fileName = new File(URI.create(filePath)).getName(); // 取出檔名部分
        }
        // 顯示檔案名稱
        Label fileLabel = new Label("🎵" + fileName);

        // 歌曲名稱欄位
        Label nameLabel = new Label("Song Name:");
        TextField nameField = new TextField(song.getName());
        nameField.setPrefWidth(300);

        // 歌手名稱欄位
        Label artistLabel = new Label("Artist Name:");
        TextField artistField = new TextField(song.getArtist());
        artistField.setPrefWidth(300);

        // 完成與返回按鈕
        Button doneButton = new Button("Done");
        Button backButton = new Button("Back");

        // 完成按鈕事件
        doneButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String artisit = artistField.getText().trim();

            // 彈出確認視窗
            ButtonType alertconfirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType alertcancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert c = new Alert(Alert.AlertType.CONFIRMATION, "", alertconfirmButton, alertcancelButton);
            c.setTitle("Confirm edit?");
            c.setHeaderText(null);
            c.setContentText("Song: " + name + "\nArtist: " + artisit + "\nFile Name: "
                    + fileName);
            ImageView icon = new ImageView(
                    String.valueOf(this.getClass().getResource("/resources/images/question.png")));
            icon.setFitHeight(45);
            icon.setFitWidth(45);
            c.getDialogPane().setGraphic(icon);
            Optional<ButtonType> result = c.showAndWait();
            if (result.isPresent() && result.get() == alertconfirmButton) {
                // 欄位不得為空
                if (name.isEmpty() || artisit.isEmpty()) {
                    showWarning("All fields must be filled.");
                    return;
                }

                // 更新歌曲資訊
                song.setName(name);
                song.setArtist(artisit);

                SongDAO.updateSong(song.getId(), name, artisit); // 同步資料庫

                showSuccess("Movie updated successfully!");
                AppController.goEditSongsPage(stage, isDarkMode);

            }
        });

        // 返回按鈕事件
        backButton.setOnAction(e -> {
            AppController.goEditSongsPage(stage, isDarkMode);
        });

        // 按鈕區塊
        HBox endBox = new HBox(10, doneButton, backButton);

        // 主畫面組合
        view = new VBox(10, fileLabel, nameLabel, nameField, artistLabel, artistField, endBox);

        // 捲動容器設定
        scrollPane = new ScrollPane(view);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPadding(new Insets(20));
    }

    // 取得主畫面
    public ScrollPane getView() {
        return scrollPane;
    }

    // 顯示成功訊息
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(this.getClass().getResource("/resources/images/check.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
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
}
