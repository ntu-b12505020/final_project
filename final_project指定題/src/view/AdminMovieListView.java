package view;

import controller.AppController;
import controller.MovieController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Movie;
import model.MovieDAO;
import model.OrderDAO;
import model.Showtime;
import model.ShowtimeDAO;

import java.util.List;
import java.util.Optional;

/**
 * AdminMovieListView 類別：管理員電影清單畫面
 */
public class AdminMovieListView {
    private VBox view; // 主畫面 VBox
    private MovieController movieController; // 電影控制器

    /**
     * 建構子，初始化管理員電影清單畫面
     * 
     * @param stage 主視窗
     */
    public AdminMovieListView(Stage stage) {
        movieController = new MovieController();
        Label title = new Label("All Movies:");
        title.getStyleClass().add("title");

        VBox movieList = new VBox(15);
        movieList.setPadding(new Insets(10)); // 內部 padding

        // 取得所有電影
        List<Movie> movies = movieController.getAllMovies();

        for (Movie movie : movies) {
            VBox movieBox = new VBox(5);
            Label name = new Label(movie.getName());
            name.getStyleClass().add("title");
            Label desc = new Label(movie.getDescription());
            desc.setWrapText(true);
            Label age = new Label("Age Limit: " + movie.getAgeLimit() + "+");
            Label status = new Label("Status: " + movie.getStatus());

            movieBox.getChildren().addAll(name, desc, age, status);

            // 刪除、編輯、管理場次按鈕
            Button dBtn = new Button("Delete");
            Button eBtn = new Button("Edit");
            Button sBtn = new Button("Manage showtime");

            // 刪除按鈕事件
            dBtn.setOnAction(e -> {
                ButtonType alertconfirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                ButtonType alertcancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert c = new Alert(Alert.AlertType.CONFIRMATION, "", alertconfirmButton, alertcancelButton);
                c.setTitle("Confirm cancel?");
                c.setHeaderText(null);
                c.setContentText("Movie: " + movie.getName());
                ImageView icon = new ImageView(String.valueOf(this.getClass().getResource("/question.png")));
                icon.setFitHeight(45);
                icon.setFitWidth(45);
                c.getDialogPane().setGraphic(icon);
                Optional<ButtonType> result = c.showAndWait();
                if (result.isPresent() && result.get() == alertconfirmButton) {
                    VBox parentVBox = (VBox) dBtn.getParent().getParent();
                    MovieDAO.deleteMovie(movie); // 刪除電影
                    movieList.getChildren().remove(parentVBox); // 從畫面移除
                    List<Showtime> allshowtime = ShowtimeDAO.getShowtimesbyMovie(movie);
                    for (Showtime showtime : allshowtime) {
                        OrderDAO.deleteOrdersByShowtime(showtime); // 刪除該電影所有場次的訂單
                    }
                    ShowtimeDAO.deleteShowtimeByMovie(movie); // 刪除該電影所有場次
                }
            });

            // 編輯按鈕事件
            eBtn.setOnAction(e -> {
                AppController.goEditMovie(stage, movie);
            });

            // 管理場次按鈕事件
            sBtn.setOnAction(e -> {
                AppController.goEditShowtimeList(stage, movie);
            });

            HBox manageBox = new HBox(10, eBtn, dBtn, sBtn);
            movieBox.getChildren().add(manageBox);

            movieBox.setStyle("-fx-border-color: #8f3f61; -fx-padding: 10;");
            movieList.getChildren().add(movieBox);
        }

        // 新增 ScrollPane 包住 movieList
        ScrollPane scrollPane = new ScrollPane(movieList);
        scrollPane.setFitToWidth(true); // 自動延展寬度
        scrollPane.setPrefHeight(400); // 設定可視高度

        // 返回按鈕
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            AppController.goAdminMenu(stage);
        });

        // 主畫面 VBox 組合
        view = new VBox(15, title, scrollPane, backButton);
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
