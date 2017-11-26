package service.tools;

import service.model.TradeResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class BitsoApiRequester {

    private final String uri;
    private Client client = ClientBuilder.newClient();
    private static UriArgumentAppender appender = new UriArgumentAppender();
    private static Properties properties = new Properties();
    static {
        try {
            properties.load(BitsoApiRequester.class.getClassLoader()
              .getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public BitsoApiRequester(String uri) throws IOException {
        this.uri = uri;
    }

    BitsoApiRequester() {
        this.uri = properties.getProperty("trade_url");
    }

    public TradeResult getTrades(int i) {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "limit", String.valueOf(i));
        return client
          .target(uri)
          .request(APPLICATION_JSON_TYPE)
          .get(TradeResult.class);
    }

    void setClient(Client client) {
        this.client = client;
    }
}
