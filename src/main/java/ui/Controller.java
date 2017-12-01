package ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import ui.data.Ask;
import ui.data.Bid;
import ui.data.Trade;

public class Controller
{
    @FXML
    private TableView<Trade> tradesTableView;
    @FXML
    private TableView<Bid> bidsTableView;
    @FXML
    private TableView<Ask> asksTableView;
    private Main mainApp;

    @FXML
    public void initialize() {
        tradesTableView.setRowFactory(tv -> new TableRow<Trade>() {
            @Override
            public void updateItem(Trade item, boolean empty) {
                super.updateItem(item, empty) ;
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
        for (service.model.trades.Trade lastTrade : mainApp.getTrades(25))
        {
            data.add(new Trade(
                    lastTrade.getCreatedAt(),
                    lastTrade.getAmount(),
                    lastTrade.getMakerSide(),
                    lastTrade.getPrice(),
                    lastTrade.getTid(),
                    Boolean.toString(lastTrade.isSimulated())
            ));
        }
    }

    public void getBids() {
        ObservableList<Bid> data = bidsTableView.getItems();
        data.clear();
        data.add(new Bid(
                "1",
                "101",
                "23"
        ));
    }

    public void getAsks() {
        ObservableList<Ask> data = asksTableView.getItems();
        data.clear();
        data.add(new Ask(
                "1",
                "101",
                "23"
        ));
    }
}
