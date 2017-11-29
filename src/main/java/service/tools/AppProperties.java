package service.tools;

import service.trades.tools.TradesRestApiClient;

import java.io.IOException;
import java.util.Properties;

public class AppProperties {
    private static Properties properties = new Properties();
    static {
        try {
            properties.load(TradesRestApiClient.class.getClassLoader()
              .getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }
}
