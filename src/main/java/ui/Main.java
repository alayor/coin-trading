package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.model.orders.Ask;
import service.model.orders.Bid;
import service.model.trades.Trade;
import service.orders.OrdersService;
import service.trades.TradingService;
import service.trades._tools.simulator.TradingSimulator;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private TradingService tradingService;
    private OrdersService ordersService;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture<?> tradesSchedule;
    private ScheduledFuture<?> ordersSchedule;
    private Controller controller;
    private TradingSimulator tradingSimulator;

    @Override
    public void start(Stage primaryStage) throws Exception{
        startServices();
        loadUI(primaryStage);
    }

    private void loadUI(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Coin Trading");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    private void startServices() throws URISyntaxException, IOException, DeploymentException {
        startSchedule();
        tradingSimulator = new TradingSimulator(3, 3);
        tradingService = TradingService.getInstance(tradingSimulator);
        tradingService.start();
        ordersService = OrdersService.getInstance();
        ordersService.start();
    }

    private void startSchedule() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        tradesSchedule = scheduledThreadPoolExecutor
          .scheduleWithFixedDelay(() -> controller.getTrades(), 5, 5, TimeUnit.SECONDS);
        ordersSchedule = scheduledThreadPoolExecutor.
          scheduleWithFixedDelay(() -> controller.getOrders(), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        tradesSchedule.cancel(true);
        ordersSchedule.cancel(true);
        scheduledThreadPoolExecutor.shutdown();
        tradingService.stop();
        ordersService.stop();
    }

    public List<Trade> getTrades(int limit) {
        return tradingService.getLastTrades(limit);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public List<Bid> getBestBids(int limit) {
        return ordersService.getBestBids(limit);
    }

    public List<Ask> getBestAsks(int limit) {
        return ordersService.getBestAsks(limit);
    }

    void setUpticksToSell(int num) {
        tradingSimulator.setUpticksToSell(num);
    }

    void setDownticksToBuy(int num) {
        tradingSimulator.setDownticksToBuy(num);
    }
}
