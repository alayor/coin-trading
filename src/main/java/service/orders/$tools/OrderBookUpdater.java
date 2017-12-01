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
    private final OrderBookHolder orderBookHolder;
    private final DiffOrdersMessageHandler diffOrderMessageHandler;
    private final DiffOrdersWebSocketClient diffOrdersWebSocketClient;
    private final DiffOrderApplier diffOrderApplier;

    private OrderBookUpdater(
      DiffOrdersWebSocketClient diffOrdersWebSocketClient,
      OrderBookHolder orderBookHolder,
      DiffOrdersMessageHandler diffOrderMessageHandler,
      DiffOrderApplier diffOrderApplier) {
        this.diffOrdersWebSocketClient = diffOrdersWebSocketClient;
        this.orderBookHolder = orderBookHolder;
        this.diffOrderMessageHandler = diffOrderMessageHandler;
        this.diffOrderApplier = diffOrderApplier;
    }

    public static OrderBookUpdater getInstance() throws URISyntaxException {
        final DiffOrdersMessageHandler messageHandler = DiffOrdersMessageHandler.getInstance();
        final DiffOrdersEndpoint endpoint = new DiffOrdersEndpoint(messageHandler);
        final OrderBookRestApiClient orderBookApiClient = new OrderBookRestApiClient();
        final OrderBookHolder orderBookHolder = OrderBookHolder.getInstance(orderBookApiClient);
        final DiffOrdersWebSocketClient webSocketClient = DiffOrdersWebSocketClient.getInstance(endpoint);
        final DiffOrderApplier diffOrderApplier = DiffOrderApplier.getInstance();
        return getInstance(webSocketClient, orderBookHolder, messageHandler, diffOrderApplier);
    }

    public static OrderBookUpdater getInstance(OrderBookRestApiClient orderBookApiClient, URI uri) throws URISyntaxException {
        final DiffOrdersMessageHandler messageHandler = DiffOrdersMessageHandler.getInstance();
        final DiffOrdersEndpoint endpoint = new DiffOrdersEndpoint(messageHandler);
        final OrderBookHolder orderBookHolder = OrderBookHolder.getInstance(orderBookApiClient);
        final DiffOrdersWebSocketClient webSocketClient = DiffOrdersWebSocketClient.getInstance(uri, endpoint);
        final DiffOrderApplier diffOrderApplier = DiffOrderApplier.getInstance();
        return getInstance(webSocketClient, orderBookHolder, messageHandler, diffOrderApplier);
    }

    static OrderBookUpdater getInstance(
      DiffOrdersWebSocketClient webSocketClient,
      OrderBookHolder orderBookHolder,
      DiffOrdersMessageHandler diffOrderMessageHandler,
      DiffOrderApplier diffOrderApplier) {
        if (orderBookUpdater == null) {
            orderBookUpdater = new OrderBookUpdater(
              webSocketClient,
              orderBookHolder,
              diffOrderMessageHandler,
              diffOrderApplier);
        }
        return orderBookUpdater;
    }

    public static void clearInstance() {
        orderBookUpdater = null;
    }

    public void start() throws IOException, DeploymentException {
        diffOrdersWebSocketClient.connect();
        loadOrderBook();
        diffOrderApplier.start();
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
