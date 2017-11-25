package api._tools;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

class BitsoClient {

    private final Client client;
    private Properties prop = new Properties();

    BitsoClient(Client client) throws IOException {
        this.client = client;
        prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
    }

    void getTrades() {
        client
          .target(prop.getProperty("trade_url"))
          .request(APPLICATION_JSON_TYPE);
    }
}
