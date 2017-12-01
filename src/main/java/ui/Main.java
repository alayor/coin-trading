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
import ui.$tools.MockedHttpServer;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private TradingService tradingService;
    private OrdersService ordersService;
    private static MockedHttpServer mockedServer = new MockedHttpServer();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture<?> tradesSchedule;
    private ScheduledFuture<?> ordersSchedule;
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mockedServer.start();
        startSchedule();
        tradingService = TradingService.getInstance(new TradingSimulator(3, 3));
        ordersService = OrdersService.getInstance();
        ordersService.start();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Coin Trading");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    private void startSchedule() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        tradesSchedule = scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> controller.getTrades(), 5, 5, TimeUnit.SECONDS);
        ordersSchedule = scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> controller.getOrders(), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        tradesSchedule.cancel(true);
        ordersSchedule.cancel(true);
        scheduledThreadPoolExecutor.shutdown();
        tradingService.stop();
        ordersService.stop();
        mockedServer.stop();
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
}
