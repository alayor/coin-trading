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

public class Controller {
    public TableColumn tidColumn;
    public TextField ordersAndTrades;
    public TextField upticks;
    public TextField downticks;
    @FXML
    private TableView<Trade> tradesTableView;
    @FXML
    private TableView<Bid> bidsTableView;
    @FXML
    private TableView<Ask> asksTableView;
    private Main mainApp;

    @FXML
    public void initialize() {
        tradesTableView.setMinWidth(400);
        bidsTableView.setMinWidth(400);
        asksTableView.setMinWidth(400);

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
        int limit = Integer.parseInt(ordersAndTrades.getText());
        for (service.model.trades.Trade lastTrade : mainApp.getTrades(limit)) {
            data.add(new Trade(
              lastTrade.getCreatedAt(),
              lastTrade.getAmount(),
              lastTrade.getMakerSide(),
              lastTrade.getPrice(),
              lastTrade.getTid(),
              Boolean.toString(lastTrade.isSimulated())
            ));
        }
        mainApp.setUpticksToSell(Integer.parseInt(upticks.getText()));
        mainApp.setDownticksToBuy(Integer.parseInt(downticks.getText()));
    }

    void getOrders() {
        ObservableList<Bid> bidItems = bidsTableView.getItems();
        bidItems.clear();
        int limit = Integer.parseInt(ordersAndTrades.getText());
        for (service.model.orders.Bid bestBid : mainApp.getBestBids(limit)) {
            bidItems.add(new Bid(
              bestBid.getOrderId(),
              bestBid.getPrice(),
              bestBid.getAmount()
            ));
        }

        List<service.model.orders.Ask> bestAsks = mainApp.getBestAsks(limit);
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
