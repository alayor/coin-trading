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

    public BitsoApiRequester() {
        this.uri = properties.getProperty("trade_url");
    }

    public TradeResult getTrades(int limit) {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "limit", String.valueOf(limit));
        return getTradeResult(uri);
    }

    private TradeResult getTradeResult(URI uri) {
        uri = appender.appendArgument(uri, "book", properties.getProperty("default_book"));
        return client
          .target(uri)
          .request(APPLICATION_JSON_TYPE)
          .get(TradeResult.class);
    }

    public TradeResult getTradesSince(int id) {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "limit", "100");
        uri = appender.appendArgument(uri, "marker", String.valueOf(id));
        uri = appender.appendArgument(uri, "sort", "asc");
        return getTradeResult(uri);
    }

    void setClient(Client client) {
        this.client = client;
    }
}
