package service.orders.$tools.rest_client;

import service.$tools.UriArgumentAppender;
import service.model.orders.OrderBookResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class OrderBookRestApiClient {
    private final String uri;
    private Client client = ClientBuilder.newClient();
    private static UriArgumentAppender appender = new UriArgumentAppender();

    public OrderBookRestApiClient(String uri) {
        this.uri = uri;
    }

    public OrderBookRestApiClient() {
        this.uri = "https://api.bitso.com/v3/order_book/";
    }

    public OrderBookResult getOrderBook() {
        URI uri = URI.create(this.uri);
        uri = appender.appendArgument(uri, "aggregate", "false");
        return getOrderBookResult(uri);
    }

    private OrderBookResult getOrderBookResult(URI uri) {
        uri = appender.appendArgument(uri, "book", "btc_mxn");
        return client
          .target(uri)
          .request(APPLICATION_JSON_TYPE)
          .get(OrderBookResult.class);
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
