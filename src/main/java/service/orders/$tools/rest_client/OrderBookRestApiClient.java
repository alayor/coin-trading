package service.orders.$tools.rest_client;

import service.$tools.UriArgumentAppender;
import service.model.orders.OrderBookResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Interacts with the Bitso Rest Api for Orders.
 * It retrieves the current Order Book from the service.
 */
public class OrderBookRestApiClient {
    private final String uri;
    private Client client = ClientBuilder.newClient();
    private static UriArgumentAppender appender = new UriArgumentAppender();

    /**
     * Creates a new instance using the bitso prod uri for order book retrieval.
     */
    public OrderBookRestApiClient() {
        this.uri = "https://api.bitso.com/v3/order_book/";
    }

    /**
     * Gets the current order book from the api service, parses it and returns it as POJO.
     * @return the current order book.
     */
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

    /**
     * It should be used only by tests.
     */
    public OrderBookRestApiClient(String uri) {
        this.uri = uri;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
