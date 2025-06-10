package view;

import controller.AppController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Order;
import model.OrderDAO;
import model.Ticket;

import java.util.List;

/**
 * TicketRecordView 類別：顯示使用者訂票紀錄畫面
 */
public class TicketRecordView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化訂票紀錄畫面
     * 
     * @param stage     主視窗
     * @param userEmail 使用者信箱
     */
    public TicketRecordView(Stage stage, String userEmail) {
        Label title = new Label("Your Booking Records:");
        title.getStyleClass().addAll("title");

        Accordion accordion = new Accordion();

        // 從 OrderDAO 撈訂單，包含金額、票券資訊
        List<Order> orders = OrderDAO.getOrdersByUser(userEmail);

        for (Order order : orders) {
            List<Ticket> tickets = order.getTickets();

            ListView<Ticket> ticketList = new ListView<>(FXCollections.observableArrayList(tickets));
            ticketList.setPrefHeight(100);

            // 顯示訂單資訊與金額
            TitledPane pane = new TitledPane(
                    "Order ID: " + order.getOrderId() + " (" + tickets.size() + " tickets, popcorn :  "
                            + order.getPopcorn()
                            + ", cola:  "
                            + order.getCola() + ") - Total: $" + order.getMoney(),
                    ticketList);

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
}
