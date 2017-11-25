package api._tools;

import api.model.TradeResult;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.util.Properties;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

class BitsoClient {

    private final Client client;
    private Properties props = new Properties();

    BitsoClient(Client client) throws IOException {
        this.client = client;
        props.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
    }

    TradeResult getTrades() {
        return client
          .target(format("%s?book=%s", props.getProperty("trade_url"), props.getProperty("default_book")))
          .request(APPLICATION_JSON_TYPE)
          .get(TradeResult.class);
    }
}
