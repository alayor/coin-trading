package ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import ui.data.Trade;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Controller {
    @FXML
    private TableView<Trade> tableView;
    private Main mainApp;

    @FXML
    public void initialize() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(this::getTrades, 5, 5, TimeUnit.SECONDS);
    }

    void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void getTrades() {
        List<service.model.trades.Trade> lastTrades = mainApp.getTradingService().getLastTrades(25);
        ObservableList<Trade> data = tableView.getItems();
        data.clear();
        for (service.model.trades.Trade lastTrade : lastTrades)
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
