package service.trades._tools.rest_client;

import service.$tools.UriArgumentAppender;
import service.model.trades.TradeResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * It communicates to the Bitso REST Api trades service to get the current trades as
 * well as new trades from a specified trade.
 */
public class TradesRestApiClient {
    private final String uri;
    private Client client = ClientBuilder.newClient();
    private static UriArgumentAppender appender = new UriArgumentAppender();

    /**
     * This should be used only for integration testing.
     * Creates a new api client specifying the uri to connect to.
     * @param uri used to connect to.
     */
    public TradesRestApiClient(String uri) {
        this.uri = uri;
    }

    /**
     * Creates a new api client that will connect to Bitso(c) Rest Api production service.
     */
    public TradesRestApiClient() {
        this.uri = "https://api.bitso.com/v3/trades/";
    }

    /**
     * Retrieves the trades from Bitso Rest Api service specifying the maximum
     * number of trades to be returned.
     * @param limit is the maximum number of trades to be returned.
     * @return an object that contains a list of trades and a success flag.
     */
    public TradeResult getTrades(int limit) {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "limit", String.valueOf(limit));
        return getTradeResult(uri);
    }

    /**
     * Retrieves the latest trades starting from one after the trade related to the id.
     * @param tradeId is used as marker to retrieve the latest trades after the trade related to this id.
     * @return a result that content the latest trades as well as a success flag.
     */
    public TradeResult getTradesSince(String tradeId) {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "limit", "100");
        uri = appender.appendArgument(uri, "marker", String.valueOf(tradeId));
        uri = appender.appendArgument(uri, "sort", "asc");
        return getTradeResult(uri);
    }

    private TradeResult getTradeResult(URI uri) {
        uri = appender.appendArgument(uri, "book", "btc_mxn");
        return client
          .target(uri)
          .request(APPLICATION_JSON_TYPE)
          .get(TradeResult.class);
    }

    void setClient(Client client) {
        this.client = client;
    }
}
