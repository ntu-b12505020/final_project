package view;

import controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * RegisterView 類別：註冊畫面與邏輯
 */
public class RegisterView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化註冊畫面
     * 
     * @param stage 主視窗
     */
    public RegisterView(Stage stage) {
        // Email 輸入欄位
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        // 密碼輸入欄位
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        TextField showPasswordField = new TextField();
        showPasswordField.setVisible(false);
        showPasswordField.managedProperty().bind(showPasswordField.visibleProperty());
        CheckBox showPassword = new CheckBox("Show Password");

        // 生日輸入欄位
        Label birthdayLabel = new Label("Birthday (YYYY-MM-DD):");
        TextField birthdayField = new TextField();

        // 註冊按鈕
        Button registerButton = new Button("Register");
        // 登入超連結
        Hyperlink loginLink = new Hyperlink("Login?");

        // 註冊按鈕事件
        registerButton.setOnAction(e -> {
            AppController.performRegister(stage, emailField.getText(),
                    showPassword.isSelected() ? showPasswordField.getText() : passwordField.getText(),
                    birthdayField.getText());
        });

        // 登入超連結事件
        loginLink.setOnAction(e -> {
            AppController.goHome(stage);
        });

        // 顯示密碼切換事件
        showPassword.setOnAction(e -> {
            if (showPassword.isSelected()) {
                showPasswordField.setText(passwordField.getText());
                showPasswordField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                passwordField.setText(showPasswordField.getText());
                passwordField.setVisible(true);
                showPasswordField.setVisible(false);
            }
        });

        // 密碼欄位與顯示密碼欄位同時顯示
        HBox passwordBox = new HBox(10, passwordField, showPasswordField, showPassword);

        // 主畫面 VBox 組合
        view = new VBox(10, emailLabel, emailField, passwordLabel, passwordBox, birthdayLabel, birthdayField,
                registerButton, loginLink);
        view.setPadding(new Insets(20));
    }

    /**
     * 取得畫面 VBox
     * 
     * @return VBox 物件
     */
    public VBox getView() {
        return view;
    }
}
