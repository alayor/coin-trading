package api.tools;

import api.model.TradeResult;
import api.tools.context.Environment;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class BitsoClient {

    private final Client client;
    private final Environment environment;
    private Properties props = new Properties();

    public BitsoClient(Client client) throws IOException {
        this(client, Environment.PROD);
    }

    public BitsoClient(Client client, Environment environment) throws IOException {
        this.client = client;
        this.environment = environment;
        props.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
    }

    public TradeResult getTrades() {
        return client
          .target(URI.create(format("%s?book=%s", getTradeUrl(), props.getProperty("default_book"))))
          .request(APPLICATION_JSON_TYPE)
          .get(TradeResult.class);
    }

    private String getTradeUrl() {
        if (environment == Environment.DEV) {
            return props.getProperty("dev.trade_url");
        }
        return props.getProperty("trade_url");
    }
}
