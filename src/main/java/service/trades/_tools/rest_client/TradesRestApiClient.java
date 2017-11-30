package service.trades._tools.rest_client;

import service.$tools.UriArgumentAppender;
import service.model.trades.TradeResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static service.$tools.AppProperties.getProperty;

public class TradesRestApiClient {

    private final String uri;
    private Client client = ClientBuilder.newClient();
    private static UriArgumentAppender appender = new UriArgumentAppender();

    public TradesRestApiClient(String uri) throws IOException {
        this.uri = uri;
    }

    public TradesRestApiClient() {
        this.uri = getProperty("trade_url");
    }

    public TradeResult getTrades(int limit) {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "limit", String.valueOf(limit));
        return getTradeResult(uri);
    }

    private TradeResult getTradeResult(URI uri) {
        uri = appender.appendArgument(uri, "book", getProperty("default_book"));
        return client
          .target(uri)
          .request(APPLICATION_JSON_TYPE)
          .get(TradeResult.class);
    }

    public TradeResult getTradesSince(String id) {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "limit", "100");
        uri = appender.appendArgument(uri, "marker", String.valueOf(id));
        uri = appender.appendArgument(uri, "sort", "asc");
        return getTradeResult(uri);
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
