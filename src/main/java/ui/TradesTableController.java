package ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ui.data.Trade;

import java.util.List;

public class TradesTableController
{
    @FXML
    private TableView<Trade> tableView;
    private Main mainApp;

    void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void getTrades() {
        ObservableList<Trade> data = tableView.getItems();
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
}
