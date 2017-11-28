package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.TradingService;
import service.TradingSimulator;
import service.tools.BitsoApiRequester;
import ui.tools.MockedHttpServer;

public class Main extends Application {

    private TradingService tradingService;
    private static MockedHttpServer mockedServer = new MockedHttpServer();

    @Override
    public void start(Stage primaryStage) throws Exception{
        mockedServer.start();
        tradingService = new TradingService(new BitsoApiRequester("http://localhost:9999/singleTradeFixture.json"), new TradingSimulator(3, 3));
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        tradingService.stop();
        mockedServer.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
