package view;

import controller.AppController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Database;
import model.Movie;

/**
 * AddMovieView 類別：新增電影的畫面與邏輯
 */
public class AddMovieView {
    private VBox view; // 主畫面 VBox
    private ScrollPane scrollPane; // 捲軸容器

    /**
     * 建構子，初始化新增電影畫面
     * 
     * @param stage 主視窗
     */
    public AddMovieView(Stage stage) {
        // 中文片名欄位
        Label nameLabelZH = new Label("Movie Name (中文):");
        TextField nameZHField = new TextField();
        nameZHField.setPrefWidth(300);

        // 英文片名欄位
        Label nameLabelEN = new Label("Movie Name (English):");
        TextField nameENField = new TextField();
        nameENField.setPrefWidth(300);

        // 電影簡介欄位
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefWidth(300);

        // 片長欄位
        Label durationLabel = new Label("Duration (minutes):");
        TextField durationField = new TextField();
        durationField.setPrefWidth(300);

        // 年齡分級欄位
        Label ageLimitLabel = new Label("Age Limit:");
        TextField ageLimitField = new TextField();
        ageLimitField.setPrefWidth(300);

        // 狀態欄位
        Label statusLabel = new Label("Status: ");
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("On", "Off"));

        // 新增與返回按鈕
        Button addButton = new Button("Add Movie");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: gray;");

        // 新增按鈕事件
        addButton.setOnAction(e -> {
            String name = nameZHField.getText().trim() + "(" + nameENField.getText().trim() + ")";
            String description = descriptionArea.getText().trim();
            String durationStr = durationField.getText().trim();
            String ageLimitStr = ageLimitField.getText().trim();
            String status = statusCombo.getSelectionModel().getSelectedItem();

            // 檢查欄位是否皆有填寫
            if (name.isEmpty() || description.isEmpty() || durationStr.isEmpty() || ageLimitStr.isEmpty()
                    || status == null) {
                showWarning("All fields must be filled.");
                return;
            }

            try {
                int duration = Integer.parseInt(durationStr);
                int ageLimit = Integer.parseInt(ageLimitStr);
                if (duration < 0 || ageLimit < 0) {
                    showWarning("Duration and Age limit can't be negative!");
                    return;
                }

                // 產生新的 Movie ID
                String newId = "M" + String.format("%03d", Database.getMovies().size() + 1);

                // 建立新電影物件並加入資料庫
                Movie newMovie = new Movie(newId, name, description, duration, ageLimit, status);
                Database.addMovie(newMovie);

                showSuccess("Movie added successfully!");
                AppController.goAdminMenu(stage);

            } catch (NumberFormatException ex) {
                showWarning("Duration and Age Limit must be numbers.");
            }
        });

        // 返回按鈕事件
        backButton.setOnAction(e -> {
            AppController.goAdminMenu(stage);
        });

        // 按鈕區塊
        HBox doneBox = new HBox(10, addButton, backButton);
        view = new VBox(12, nameLabelZH, nameZHField, nameLabelEN, nameENField, descriptionLabel, descriptionArea,
                durationLabel, durationField,
                ageLimitLabel, ageLimitField, statusLabel, statusCombo, doneBox);

        // 捲軸設定
        scrollPane = new ScrollPane(view);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPadding(new Insets(20));
    }

    /**
     * 取得畫面 ScrollPane
     * 
     * @return ScrollPane 物件
     */
    public ScrollPane getView() {
        return scrollPane;
    }

    /**
     * 顯示成功訊息視窗
     * 
     * @param message 訊息內容
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(this.getClass().getResource("/check.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    /**
     * 顯示警告訊息視窗
     * 
     * @param message 訊息內容
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType okbtn = new ButtonType("OK");
        alert.getButtonTypes().setAll(okbtn);
        ImageView icon = new ImageView(
                new Image(String.valueOf(this.getClass().getResource("/caution.png"))));
        icon.setFitHeight(45);
        icon.setFitWidth(45);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }
}
