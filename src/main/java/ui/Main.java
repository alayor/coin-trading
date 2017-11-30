package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.model.trades.Trade;
import service.trades.TradingService;
import service.trades._tools.rest_client.TradesRestApiClient;
import service.trades._tools.simulator.TradingSimulator;
import ui._tools.MockedHttpServer;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private TradingService tradingService;
    private static MockedHttpServer mockedServer = new MockedHttpServer();
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture<?> scheduledFuture;
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mockedServer.start();
        startSchedule();
        tradingService = TradingService.getInstance(new TradesRestApiClient("http://localhost:9999/singleTradeFixture.json"), new TradingSimulator(3, 3));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Coin Trading");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }

    private void startSchedule() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        scheduledFuture = scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> controller.getTrades(), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        scheduledFuture.cancel(true);
        scheduledThreadPoolExecutor.shutdown();
        tradingService.stop();
        mockedServer.stop();
    }

    public List<Trade> getTrades(int limit) {
        return tradingService.getLastTrades(limit);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
