package view;

import controller.AppController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Ticket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.OrderDAO;
import model.ShowtimeDAO;
import model.Order;

/**
 * CancelTicketView 類別：訂單取消畫面
 */
public class CancelTicketView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化訂單取消畫面
     * 
     * @param stage     主視窗
     * @param userEmail 使用者信箱
     */
    public CancelTicketView(Stage stage, String userEmail) {
        Label title = new Label("Cancel Your Orders:");
        title.getStyleClass().addAll("title");

        Accordion accordion = new Accordion();

        // 取得該使用者所有訂單
        List<Order> orders = OrderDAO.getOrdersByUser(userEmail);

        // 建立可取消的訂單 UI（每個 Order 一個 TitledPane）
        for (Order order : orders) {
            List<Ticket> tickets = order.getTickets();

            ListView<Ticket> ticketList = new ListView<>(FXCollections.observableArrayList(tickets));
            ticketList.setPrefHeight(100);

            Button cancelButton = new Button("Cancel this Order");
            cancelButton.setOnAction(e -> handleCancelOrder(stage, accordion, order));

            VBox orderBox = new VBox(10, ticketList, cancelButton);

            TitledPane pane = new TitledPane(
                    "Order ID: " + order.getOrderId() + " (" + tickets.size() + " tickets, popcorn:  "
                            + order.getPopcorn()
                            + ", cola:  "
                            + order.getCola() + ") - Total: $" + order.getMoney(),
                    orderBox);
            accordion.getPanes().add(pane);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> AppController.goUserMenu(stage));

        view = new VBox(10, title, accordion, backButton);
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
     * 處理取消訂單的邏輯
     * 
     * @param stage         主視窗
     * @param accordion     Accordion 物件
     * @param selectedOrder 欲取消的訂單
     */
    private void handleCancelOrder(Stage stage, Accordion accordion, Order selectedOrder) {
        if (selectedOrder != null && !selectedOrder.getTickets().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime showTime = selectedOrder.getTickets().get(0).getShowtime().getTime();

            ButtonType confirm = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", confirm, cancel);
            alert.setTitle("Confirm Cancel");
            alert.setHeaderText(null);
            alert.setContentText("Order ID: " + selectedOrder.getOrderId() +
                    "\nMovie: " + selectedOrder.getTickets().get(0).getShowtime().getMovie() +
                    "\nShowtime: " + showTime.toLocalDate() + " " + showTime.toLocalTime().withNano(0) +
                    "\nSeats: " + selectedOrder.getTickets().size());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == confirm) {
                // 檢查是否已經超過開演前30分鐘
                if (now.isAfter(showTime.minusMinutes(30))) {
                    showWarning("❗ Cannot cancel: within 30 minutes of showtime.");
                } else {
                    boolean success = OrderDAO.deleteOrder(selectedOrder.getOrderId());
                    if (success) {
                        for (Ticket ticket : selectedOrder.getTickets()) {
                            ticket.getSeat().unbook();
                            // 如果場次原本是 sold out，則改回 open
                            if (ticket.getShowtime().getStatus().equals("sold out")) {
                                ticket.getShowtime().setStatus("open");
                                ShowtimeDAO.update(ticket.getShowtime());
                            }
                        }

                        showSuccess("Order cancelled successfully!");
                        AppController.goCancelTicket(stage);
                    } else {
                        showWarning("Order cancel failed!");
                    }
                }
            }
        }
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
        alert.showAndWait();
    }
}
