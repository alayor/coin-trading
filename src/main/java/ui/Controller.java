package ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ui.data.Ask;
import ui.data.Bid;
import ui.data.Trade;

import java.util.List;

import static java.lang.Integer.parseInt;

public class Controller {
    public TableColumn tidColumn;
    public TextField ordersAndTrades;
    public TextField upticks;
    public TextField downticks;

    private int currentOrdersAndTrades = 25;
    private int currentUpticks = 3;
    private int currentDownticks = 3;
    public GridPane gridPane;
    @FXML
    private TableView<Trade> tradesTableView;
    @FXML
    private TableView<Bid> bidsTableView;
    @FXML
    private TableView<Ask> asksTableView;
    private Main mainApp;

    @FXML
    public void initialize() {
        gridPane.setMinSize(0, 0);
        tradesTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bidsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        asksTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tradesTableView.setRowFactory(tv -> new TableRow<Trade>() {
            @Override
            public void updateItem(Trade item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getSimulated().equals("true")) {
                    setStyle("-fx-background-color: lightgreen;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void getTrades() {
        ObservableList<Trade> data = tradesTableView.getItems();
        data.clear();
        try {
            currentOrdersAndTrades = parseInt(ordersAndTrades.getText());
        } catch (NumberFormatException e) {
            // Intentionally
        }
        List<service.model.trades.Trade> trades = mainApp.getTrades(currentOrdersAndTrades);
        for (int i = 0; i < trades.size(); i++) {
            data.add(new Trade(
              String.valueOf(i + 1),
              trades.get(i).getCreatedAt(),
              trades.get(i).getAmount(),
              trades.get(i).getMakerSide(),
              trades.get(i).getPrice(),
              trades.get(i).getTid(),
              Boolean.toString(trades.get(i).isSimulated())
            ));
        }
        try {
            currentUpticks = parseInt(upticks.getText());
        } catch (NumberFormatException e) {
            // Intentionally
        }
        try {
            currentDownticks = parseInt(downticks.getText());
        } catch (NumberFormatException e) {
            // Intentionally
        }
        mainApp.setUpticksToSell(currentUpticks);
        mainApp.setDownticksToBuy(currentDownticks);
    }

    void getOrders() {
        try {
            currentOrdersAndTrades = parseInt(ordersAndTrades.getText());
        } catch (NumberFormatException e) {
            // Intentionally
        }
        ObservableList<Bid> bidItems = bidsTableView.getItems();
        List<service.model.orders.Bid> bestBids = mainApp.getBestBids(currentOrdersAndTrades);
        bidItems.clear();
        for (int i = 0; i < bestBids.size(); i++) {
            bidItems.add(new Bid(
              String.valueOf(i + 1),
              bestBids.get(i).getOrderId(),
              bestBids.get(i).getPrice(),
              bestBids.get(i).getAmount()
            ));
        }

        ObservableList<Ask> askItems = asksTableView.getItems();
        List<service.model.orders.Ask> bestAsks = mainApp.getBestAsks(currentOrdersAndTrades);
        askItems.clear();
        for (int i =0; i < bestAsks.size(); i++) {
            askItems.add(new Ask(
              String.valueOf(i + 1),
              bestAsks.get(i).getOrderId(),
              bestAsks.get(i).getPrice(),
              bestAsks.get(i).getAmount()
            ));
        }
    }
}
