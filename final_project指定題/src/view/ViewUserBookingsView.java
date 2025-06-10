package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.OrderDAO;
import model.Ticket;
import controller.AppController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import model.Order;

/**
 * ViewUserBookingsView 類別：管理員查詢所有使用者訂票紀錄畫面
 */
public class ViewUserBookingsView {
    private VBox view; // 主畫面 VBox

    /**
     * 建構子，初始化管理員查詢所有使用者訂票紀錄畫面
     * 
     * @param stage 主視窗
     */
    public ViewUserBookingsView(Stage stage) {
        Label title = new Label("📋 All User Booking Records:");
        title.getStyleClass().addAll("title");

        Accordion userAccordion = new Accordion();

        // 取得所有使用者的訂單（依使用者分組）
        Map<String, List<Order>> ordersByUser = OrderDAO.getOrdersGroupedByUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (String userEmail : ordersByUser.keySet()) {
            List<Order> orders = ordersByUser.get(userEmail);
            Accordion orderAccordion = new Accordion();

            for (Order order : orders) {
                ListView<String> ticketList = new ListView<>();

                for (Ticket ticket : order.getTickets()) {
                    String ticketInfo = "🎬 " + ticket.getShowtime().getMovie().getName() +
                            " | 🕒 " + ticket.getShowtime().getTime().format(formatter) +
                            " | 💺 " + ticket.getSeat().getSeatId();

                    if (order.getPopcorn() > 0)
                        ticketInfo += " | Popcorn";
                    if (order.getCola() > 0)
                        ticketInfo += " | Cola";

                    ticketList.getItems().add(ticketInfo);
                }

                String orderTitle = "Order ID: " + order.getOrderId()
                        + " | 🕒 " + order.getOrderTime().format(formatter)
                        + " | Popcorn: " + order.getPopcorn()
                        + " | Cola: " + order.getCola()
                        + " | 💰 Total: $" + order.getMoney();

                TitledPane orderPane = new TitledPane(orderTitle, ticketList);
                orderAccordion.getPanes().add(orderPane);
            }

            TitledPane userPane = new TitledPane("👤 " + userEmail + " (" + orders.size() + " orders)", orderAccordion);
            userAccordion.getPanes().add(userPane);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            AppController.goAdminMenu(stage);
        });

        view = new VBox(10, title, userAccordion, backButton);
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
