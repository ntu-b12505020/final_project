// ========== view/HomeView.java ==============
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
 * HomeView 類別：首頁登入畫面與邏輯
 */
public class HomeView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化首頁登入畫面
     * 
     * @param stage 主視窗
     */
    public HomeView(Stage stage) {
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

        // 顯示密碼 CheckBox
        CheckBox showPassword = new CheckBox("Show Password");

        // 登入按鈕
        Button loginButton = new Button("Login");
        // 註冊超連結
        Hyperlink registerLink = new Hyperlink("Register?");

        // 登入按鈕事件
        loginButton.setOnAction(e -> AppController.login(stage, emailField.getText(),
                showPassword.isSelected() ? showPasswordField.getText() : passwordField.getText()));

        // 註冊超連結事件
        registerLink.setOnAction(e -> AppController.register(stage));

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
        view = new VBox(10, emailLabel, emailField, passwordLabel, passwordBox, loginButton, registerLink);
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
