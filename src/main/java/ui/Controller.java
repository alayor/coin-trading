package ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    public int currentOrdersAndTrades = 25;
    public int currentUpticks = 3;
    public int currentDownticks = 3;
    @FXML
    private TableView<Trade> tradesTableView;
    @FXML
    private TableView<Bid> bidsTableView;
    @FXML
    private TableView<Ask> asksTableView;
    private Main mainApp;

    @FXML
    public void initialize() {
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
        ObservableList<Bid> bidItems = bidsTableView.getItems();
        bidItems.clear();
        try {
            currentOrdersAndTrades = parseInt(ordersAndTrades.getText());
        } catch (NumberFormatException e) {
            // Intentionally
        }
        for (service.model.orders.Bid bestBid : mainApp.getBestBids(currentOrdersAndTrades)) {
            bidItems.add(new Bid(
              bestBid.getOrderId(),
              bestBid.getPrice(),
              bestBid.getAmount()
            ));
        }

        List<service.model.orders.Ask> bestAsks = mainApp.getBestAsks(currentOrdersAndTrades);
        ObservableList<Ask> askItems = asksTableView.getItems();
        askItems.clear();
        for (service.model.orders.Ask bestAsk : bestAsks) {
            askItems.add(new Ask(
              bestAsk.getOrderId(),
              bestAsk.getPrice(),
              bestAsk.getAmount()
            ));
        }
    }
}
