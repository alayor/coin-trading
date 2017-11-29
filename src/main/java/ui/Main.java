package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.trades.TradingService;
import service.trades.TradingSimulator;
import service.trades.tools.TradesApiClient;
import ui.tools.MockedHttpServer;

public class Main extends Application {

    private TradingService tradingService;
    private static MockedHttpServer mockedServer = new MockedHttpServer();

    @Override
    public void start(Stage primaryStage) throws Exception{
        mockedServer.start();
        tradingService = new TradingService(new TradesApiClient("http://localhost:9999/singleTradeFixture.json"), new TradingSimulator(3, 3));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Coin Trading");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        tradingService.stop();
        mockedServer.stop();
    }

    public TradingService getTradingService()
    {
        return tradingService;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
