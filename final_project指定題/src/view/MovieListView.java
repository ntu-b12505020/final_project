package view;

import controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Movie;
import model.MovieDAO;
import model.Showtime;
import model.ShowtimeDAO;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MovieListView 類別：使用者電影清單畫面
 */
public class MovieListView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化使用者電影清單畫面
     * 
     * @param stage 主視窗
     */
    public MovieListView(Stage stage) {
        Label title = new Label("Now Showing:");
        title.getStyleClass().add("title");
        VBox movieList = new VBox(15);
        movieList.setPadding(new Insets(10)); // 內部 padding

        // 取得所有上映中的電影
        List<Movie> movies = MovieDAO.getMoviesON();

        for (Movie movie : movies) {
            VBox movieBox = new VBox(5);
            Label name = new Label("🎬 " + movie.getName() + "  "
                    + (movie.getDuration() / 60 == 0 ? "" : movie.getDuration() / 60 + "h ") + movie.getDuration() % 60
                    + "m");
            Label desc = new Label("📝 " + movie.getDescription());
            Label age = new Label("🔞 Age Limit: " + movie.getAgeLimit() + "+");

            movieBox.getChildren().addAll(name, desc, age);

            // 取得該電影所有開放場次
            List<Showtime> showtimes = ShowtimeDAO.getOpenShowtimesbyMovie(movie);
            if (showtimes.isEmpty()) {
                movieBox.getChildren().add(new Label("❌ No showtimes available."));
            } else {
                for (Showtime s : showtimes) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String formattedTime = s.getTime().format(formatter);
                    Button sBtn = new Button(s.getHall() + " - " + formattedTime);
                    sBtn.setOnAction(e -> {
                        AppController.bookTicketWithPreset(stage, movie, s);
                    });
                    movieBox.getChildren().add(sBtn);
                }
            }

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
            AppController.goUserMenu(stage);
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
