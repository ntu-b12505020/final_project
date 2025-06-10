package view;

import java.util.Optional;

import controller.AppController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Database;
import model.Movie;

/**
 * EditMovieView 類別：編輯電影資訊的畫面與邏輯
 */
public class EditMovieView {
    private VBox view; // 主畫面 VBox
    private ScrollPane scrollPane; // 捲軸容器

    /**
     * 建構子，初始化編輯電影畫面
     * 
     * @param stage 主視窗
     * @param movie 欲編輯的電影物件
     */
    public EditMovieView(Stage stage, Movie movie) {
        // 中文片名欄位
        Label nameLabelZH = new Label("Movie Name (中文):");
        TextField nameZHField = new TextField(movie.getNameZH());
        nameZHField.setPrefWidth(300);

        // 英文片名欄位
        Label nameLabelEN = new Label("Movie Name (English):");
        TextField nameENField = new TextField(movie.getNameEN());
        nameENField.setPrefWidth(300);

        // 電影簡介欄位
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionArea = new TextArea(movie.getDescription());
        descriptionArea.setPrefWidth(300);
        descriptionArea.setWrapText(true);

        // 片長欄位
        Label durationLabel = new Label("Duration (minutes):");
        TextField durationField = new TextField(String.valueOf(movie.getDuration()));
        durationField.setPrefWidth(300);

        // 年齡分級欄位
        Label ageLimitLabel = new Label("Age Limit:");
        TextField ageLimitField = new TextField(String.valueOf(movie.getAgeLimit()));
        ageLimitField.setPrefWidth(300);

        // 狀態欄位
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("On", "Off"));
        statusCombo.setValue(movie.getStatus());

        // 完成與返回按鈕
        Button doneButton = new Button("Done");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: gray;");

        // 完成按鈕事件
        doneButton.setOnAction(e -> {
            String nameZH = nameZHField.getText().trim();
            String nameEN = nameENField.getText().trim();
            String description = descriptionArea.getText().trim();
            String durationStr = durationField.getText().trim();
            String ageLimitStr = ageLimitField.getText().trim();
            String status = statusCombo.getSelectionModel().getSelectedItem();

            ButtonType alertconfirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType alertcancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert c = new Alert(Alert.AlertType.CONFIRMATION, "", alertconfirmButton, alertcancelButton);
            c.setTitle("Confirm edit?");
            c.setHeaderText(null);
            c.setContentText("Movie: " + nameZH + "(" + nameEN + ")" + "\ndescription: " + description + "\nduration: "
                    + durationStr + "\nageLimit: " + ageLimitStr + "\nstatus: " + status);
            ImageView icon = new ImageView(String.valueOf(this.getClass().getResource("/question.png")));
            icon.setFitHeight(45);
            icon.setFitWidth(45);
            c.getDialogPane().setGraphic(icon);
            Optional<ButtonType> result = c.showAndWait();
            if (result.isPresent() && result.get() == alertconfirmButton) {
                if (nameZH.isEmpty() || nameEN.isEmpty() || description.isEmpty() || durationStr.isEmpty()
                        || ageLimitStr.isEmpty() || status.isEmpty()) {
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

                    // 更新原本的 movie
                    movie.setNameZH(nameZH);
                    movie.setNameEN(nameEN);
                    movie.setDescription(description);
                    movie.setDuration(duration);
                    movie.setAgeLimit(ageLimit);
                    movie.setStatus(status);

                    Database.updateMovie(movie); // 同步資料庫

                    showSuccess("Movie updated successfully!");
                    AppController.goAdminMenu(stage);

                } catch (NumberFormatException ex) {
                    showWarning("Duration and Age Limit must be numbers.");
                }
            }
        });

        // 返回按鈕事件
        backButton.setOnAction(e -> {
            AppController.goAdminMovieList(stage);
        });

        // 按鈕區塊
        HBox endBox = new HBox(10, doneButton, backButton);

        // 主畫面 VBox 組合
        view = new VBox(10, nameLabelZH, nameZHField, nameLabelEN, nameENField, descriptionLabel, descriptionArea,
                durationLabel, durationField,
                ageLimitLabel, ageLimitField, statusLabel, statusCombo, endBox);

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
