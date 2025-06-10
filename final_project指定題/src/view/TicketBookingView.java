package view;

import controller.AppController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TicketBookingView 類別：訂票畫面與邏輯
 */
public class TicketBookingView {
    private VBox view; // 主畫面 VBox
    private List<Seat> selectedSeats = new ArrayList<>(); // 已選座位
    private ComboBox<Movie> movieCombo; // 電影下拉選單
    private ComboBox<Showtime> showtimeCombo; // 場次下拉選單
    private int money = 0; // 總金額

    /**
     * 建構子，初始化訂票畫面
     * 
     * @param stage     主視窗
     * @param userEmail 使用者信箱
     */
    public TicketBookingView(Stage stage, String userEmail) {
        Label movieLabel = new Label("Select Movie:");
        movieCombo = new ComboBox<>(FXCollections.observableArrayList(MovieDAO.getMoviesOnHaveOpenShowtime()));

        Label showtimeLabel = new Label("Select Showtime:");
        showtimeCombo = new ComboBox<>();

        Label regularTicketLabel = new Label("Regular $320 ");
        Button minusrButton = new Button("-");
        Label numberrLabel = new Label("0");
        Button plusrButton = new Button("+");
        HBox regularBox = new HBox(10, regularTicketLabel, minusrButton, numberrLabel, plusrButton);

        Label concessionTicketLabel = new Label("Concession $280 ");
        Button minuscButton = new Button("-");
        Label numbercLabel = new Label("0");
        Button pluscButton = new Button("+");
        HBox concessionBox = new HBox(10, concessionTicketLabel, minuscButton, numbercLabel, pluscButton);

        Label seatLabel = new Label("Select Seats:");
        GridPane seatGrid = new GridPane();
        ScrollPane scrollPane = new ScrollPane(seatGrid);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        seatGrid.setHgap(5);
        seatGrid.setVgap(5);
        seatGrid.setPadding(new Insets(10));

        Button bookButton = new Button("Confirm Booking");
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: gray;");

        // 電影選擇事件
        movieCombo.setOnAction(e -> {
            Movie selectedMovie = movieCombo.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                List<Showtime> relatedShowtimes = ShowtimeDAO.getOpenShowtimesbyMovie(selectedMovie);
                showtimeCombo.setItems(FXCollections.observableArrayList(relatedShowtimes));
                showtimeCombo.getSelectionModel().clearSelection();
                seatGrid.getChildren().clear();
                selectedSeats.clear();
            }
        });

        // 場次選擇事件
        showtimeCombo.setOnAction(e -> {
            Showtime selectedShowtime = showtimeCombo.getSelectionModel().getSelectedItem();
            if (selectedShowtime != null) {
                seatGrid.getChildren().clear();
                selectedSeats.clear();
                List<Seat> seats = Database.getSeatsByShowtime(selectedShowtime);
                List<String> bookedSeat = SeatDAO.getBookedSeatByShowtime(selectedShowtime);

                int col = 0, row = 0;

                for (Seat seat : seats) {
                    Button seatButton = new Button(seat.getSeatId());
                    seatButton.setPrefWidth(55);

                    if (bookedSeat.contains(seat.getSeatId())) {
                        seatButton.setStyle("-fx-background-color: gray;");
                        seat.book();
                        seatButton.setDisable(true);
                    } else {
                        seatButton.setOnAction(ev -> {
                            if (selectedSeats.contains(seat)) {
                                selectedSeats.remove(seat);
                                seatButton.setStyle("");
                            } else {
                                if (selectedSeats.size() + 1 <= Integer.parseInt(numberrLabel.getText())
                                        + Integer.parseInt(numbercLabel.getText())) {
                                    selectedSeats.add(seat);
                                    seatButton.setStyle("-fx-background-color:rgb(127, 161, 207);");
                                }
                            }
                        });
                    }

                    seatGrid.add(seatButton, col, row);

                    if (selectedShowtime.getHall().equals("Small Hall")) {
                        if (++col == 16) {
                            col = 0;
                            row++;
                        }
                    } else {
                        switch (row) {
                            case 0:
                                if (col < 7 || col > 30) {
                                    seatButton.setOpacity(0);
                                    seatButton.setDisable(true);
                                }
                                break;
                            case 1:
                                if (col < 4 || col > 33) {
                                    seatButton.setOpacity(0);
                                    seatButton.setDisable(true);
                                }
                                break;
                            case 12:
                                if (col > 7 && col < 30) {
                                    seatButton.setOpacity(0);
                                    seatButton.setDisable(true);
                                }
                                break;
                            default:
                                break;
                        }
                        int endCol = (row == 11) ? 39 : 38;
                        if (++col == endCol) {
                            col = 0;
                            row++;
                        }
                    }
                }
            }
        });

        // 普通票減少
        minusrButton.setOnAction(e -> {
            if (Integer.parseInt(numberrLabel.getText()) > 0) {
                String newnum = Integer.toString(Integer.parseInt(numberrLabel.getText()) - 1);
                numberrLabel.setText(newnum);
                for (javafx.scene.Node node : seatGrid.getChildren()) {
                    if (node instanceof Button btn) {
                        if (btn.getStyle().contains("rgb(127, 161, 207)")) {
                            btn.setStyle("");
                        }
                    }
                }
                selectedSeats.clear();
                money = money - 320;
            }
        });

        // 普通票增加
        plusrButton.setOnAction(e -> {
            if (!(showtimeCombo.getSelectionModel().getSelectedItem() == null)) {
                if (Integer.parseInt(numberrLabel.getText()) + Integer.parseInt(numbercLabel.getText()) < Database
                        .getSeatNumByShowtime(showtimeCombo.getSelectionModel().getSelectedItem())) {
                    String newnum = Integer.toString(Integer.parseInt(numberrLabel.getText()) + 1);
                    numberrLabel.setText(newnum);
                    money = money + 320;
                }
            } else {
                showWarning("Please select showtime!");
            }
        });

        // 優待票減少
        minuscButton.setOnAction(e -> {
            if (Integer.parseInt(numbercLabel.getText()) > 0) {
                String newnum = Integer.toString(Integer.parseInt(numbercLabel.getText()) - 1);
                numbercLabel.setText(newnum);
                for (javafx.scene.Node node : seatGrid.getChildren()) {
                    if (node instanceof Button btn) {
                        if (btn.getStyle().contains("rgb(127, 161, 207)")) {
                            btn.setStyle("");
                        }
                    }
                }
                money = money - 280;
                selectedSeats.clear();
            }
        });

        // 優待票增加
        pluscButton.setOnAction(e -> {
            if (!(showtimeCombo.getSelectionModel().getSelectedItem() == null)) {
                if (Integer.parseInt(numberrLabel.getText()) + Integer.parseInt(numbercLabel.getText()) < Database
                        .getSeatNumByShowtime(showtimeCombo.getSelectionModel().getSelectedItem())) {
                    String newnum = Integer.toString(Integer.parseInt(numbercLabel.getText()) + 1);
                    numbercLabel.setText(newnum);
                    money = money + 280;
                }
            } else {
                showWarning("Please select showtime!");
            }
        });

        // 爆米花加購
        Label popcornLabel = new Label("Popcorn $120: ");
        Button pluspBtn = new Button("+");
        Label popnumLabel = new Label("0");
        Button minuspBtn = new Button("-");
        HBox popcornHBox = new HBox(10, popcornLabel, minuspBtn, popnumLabel, pluspBtn);

        pluspBtn.setOnAction(e -> {
            if ((!(showtimeCombo.getSelectionModel().getSelectedItem() == null))
                    && Integer.parseInt(numberrLabel.getText()) + Integer.parseInt(numbercLabel.getText()) != 0) {
                String newnum = Integer.toString(Integer.parseInt(popnumLabel.getText()) + 1);
                popnumLabel.setText(newnum);
                money = money + 120;
            } else {
                showWarning("Please select showtime or book at least 1 ticket!!");
            }

        });

        minuspBtn.setOnAction(e -> {
            if (!(showtimeCombo.getSelectionModel().getSelectedItem() == null)) {
                if (Integer.parseInt(popnumLabel.getText()) > 0) {
                    String newnum = Integer.toString(Integer.parseInt(popnumLabel.getText()) - 1);
                    popnumLabel.setText(newnum);
                    money = money - 120;
                }
            } else {
                showWarning("Please select showtime!");
            }

        });

        // 可樂加購
        Label colaLabel = new Label("Cola $80: ");
        Button pluscolaBtn = new Button("+");
        Label colanumLabel = new Label("0");
        Button minuscolaBtn = new Button("-");
        HBox colaHBox = new HBox(10, colaLabel, minuscolaBtn, colanumLabel, pluscolaBtn);

        pluscolaBtn.setOnAction(e -> {
            if ((!(showtimeCombo.getSelectionModel().getSelectedItem() == null)
                    && Integer.parseInt(numberrLabel.getText()) + Integer.parseInt(numbercLabel.getText()) != 0)) {
                String newnum = Integer.toString(Integer.parseInt(colanumLabel.getText()) + 1);
                colanumLabel.setText(newnum);
                money = money + 80;
            } else {
                showWarning("Please select showtime or book at least 1 ticket!");
            }
        });

        minuscolaBtn.setOnAction(e -> {
            if (!(showtimeCombo.getSelectionModel().getSelectedItem() == null)) {
                if (Integer.parseInt(colanumLabel.getText()) > 0) {
                    String newnum = Integer.toString(Integer.parseInt(colanumLabel.getText()) - 1);
                    colanumLabel.setText(newnum);
                    money = money - 80;
                }
            } else {
                showWarning("Please select showtime!");
            }

        });

        // 訂票確認按鈕事件
        bookButton.setOnAction(e -> {
            Movie selectedMovie = movieCombo.getSelectionModel().getSelectedItem();
            Showtime selectedShowtime = showtimeCombo.getSelectionModel().getSelectedItem();

            if (selectedMovie == null || selectedShowtime == null || selectedSeats
                    .size() != Integer.parseInt(numberrLabel.getText()) + Integer.parseInt(numbercLabel.getText())) {
                showWarning("Please select movie, showtime and seat!");
                return;
            }

            int ageLimit = selectedMovie.getAgeLimit();
            if (!AppController.checkAge(userEmail, ageLimit)) {
                showWarning("Age limit not satisfied!");
                return;
            }

            int orderId = OrderDAO.addOrder(userEmail, selectedShowtime, money, Integer.parseInt(popnumLabel.getText()),
                    Integer.parseInt(colanumLabel.getText()));

            for (Seat seat : selectedSeats) {
                OrderDAO.bookSeatWithOrderId(selectedShowtime, seat.getSeatId(), userEmail, orderId);
                Database.addTicket(new Ticket(selectedShowtime.getId() + "-" + seat.getSeatId(), selectedShowtime, seat,
                        userEmail, orderId));
                seat.book();
            }
            showSuccess("✅ Booking successful!");
            AppController.goUserMenu(stage);
            if (Database.getSeatNumByShowtime(selectedShowtime) == 0) {
                selectedShowtime.setStatus("sold out");
                ShowtimeDAO.update(selectedShowtime);
            }

        });

        // 返回按鈕事件
        backButton.setOnAction(e -> {
            AppController.goUserMenu(stage);
        });

        VBox ticketVBox = new VBox(10, regularBox, concessionBox);
        VBox foodVBox = new VBox(10, popcornHBox, colaHBox);

        HBox bookHBox = new HBox(10, ticketVBox, foodVBox);

        HBox bookBox = new HBox(10, bookButton, backButton);
        view = new VBox(10, movieLabel, movieCombo, showtimeLabel, showtimeCombo, bookHBox, seatLabel,
                scrollPane, bookBox);
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
