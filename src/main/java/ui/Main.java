package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.TradingService;
import service.tools.BitsoApiRequester;

public class Main extends Application {

    private TradingService tradingService;
    @Override
    public void start(Stage primaryStage) throws Exception{
        tradingService = new TradingService(new BitsoApiRequester());
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        tradingService.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
