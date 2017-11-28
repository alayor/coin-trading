package ui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ui.data.Trade;

import java.util.List;

public class Controller {
    @FXML
    private TableView<Trade> tableView;
    private Main mainApp;

    void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void getTrades() {
        List<service.model.Trade> lastTrades = mainApp.getTradingService().getLastTrades(25);
        ObservableList<Trade> data = tableView.getItems();
        service.model.Trade trade = lastTrades.get(0);
        data.add(new Trade(
                trade.getCreatedAt(),
                trade.getAmount(),
                trade.getMakerSide(),
                trade.getPrice(),
                trade.getTid(),
                Boolean.toString(trade.isSimulated())
        ));
    }
}
