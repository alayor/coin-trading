package service.orders.$tools;

import service.orders.$tools.holders.OrderBookHolder;
import service.orders.$tools.rest_client.OrderBookRestApiClient;
import service.orders.$tools.web_socket.DiffOrdersEndpoint;
import service.orders.$tools.web_socket.DiffOrdersMessageHandler;
import service.orders.$tools.web_socket.DiffOrdersWebSocketClient;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.Thread.sleep;

public class OrderBookUpdater {
    private static OrderBookUpdater orderBookUpdater;
    private final OrderBookRestApiClient orderBookApiClient;
    private final OrderBookHolder orderBookHolder;
    private final DiffOrdersMessageHandler diffOrderMessageHandler;
    private final DiffOrdersWebSocketClient diffOrdersWebSocketClient;

    private OrderBookUpdater(
      DiffOrdersWebSocketClient diffOrdersWebSocketClient,
      OrderBookRestApiClient orderBookApiClient,
      OrderBookHolder orderBookHolder,
      DiffOrdersMessageHandler diffOrderMessageHandler) {
        this.diffOrdersWebSocketClient = diffOrdersWebSocketClient;
        this.orderBookApiClient = orderBookApiClient;
        this.orderBookHolder = orderBookHolder;
        this.diffOrderMessageHandler = diffOrderMessageHandler;
    }

    public static OrderBookUpdater getInstance() throws URISyntaxException {
        final DiffOrdersMessageHandler messageHandler = DiffOrdersMessageHandler.getInstance();
        final DiffOrdersEndpoint endpoint = new DiffOrdersEndpoint(messageHandler);
        final OrderBookRestApiClient orderBookApiClient = new OrderBookRestApiClient();
        final OrderBookHolder orderBookHolder = OrderBookHolder.getInstance(orderBookApiClient);
        final DiffOrdersWebSocketClient webSocketClient = DiffOrdersWebSocketClient.getInstance(endpoint);
        return getInstance(webSocketClient, orderBookApiClient, orderBookHolder, messageHandler);
    }

    public static OrderBookUpdater getInstance(OrderBookRestApiClient orderBookApiClient, URI uri) throws URISyntaxException {
        final DiffOrdersMessageHandler messageHandler = DiffOrdersMessageHandler.getInstance();
        final DiffOrdersEndpoint endpoint = new DiffOrdersEndpoint(messageHandler);
        final OrderBookHolder orderBookHolder = OrderBookHolder.getInstance(orderBookApiClient);
        final DiffOrdersWebSocketClient webSocketClient = DiffOrdersWebSocketClient.getInstance(uri, endpoint);
        return getInstance(webSocketClient, orderBookApiClient, orderBookHolder, messageHandler);
    }

    static OrderBookUpdater getInstance(
      DiffOrdersWebSocketClient webSocketClient,
      OrderBookRestApiClient orderBookApiClient,
      OrderBookHolder orderBookHolder,
      DiffOrdersMessageHandler diffOrderMessageHandler) {
        if (orderBookUpdater == null) {
            orderBookUpdater = new OrderBookUpdater(
              webSocketClient,
              orderBookApiClient,
              orderBookHolder,
              diffOrderMessageHandler);
        }
        return orderBookUpdater;
    }

    public static void clearInstance() {
        orderBookUpdater = null;
    }

    public void start() throws IOException, DeploymentException {
        diffOrdersWebSocketClient.connect();
        loadOrderBook();
    }

    private void loadOrderBook() {
        int count = 10;
        while (count-- > 0) {
            try {
                sleep(1000);
            if (diffOrderMessageHandler.firstDiffOfferHasBeenReceived()) {
                orderBookHolder.loadOrderBook();
                return;
            }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Order Book couldn't get loaded. No Diff Offers were received.");
    }
}
