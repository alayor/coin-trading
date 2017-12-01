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
    private final DiffOrdersWebSocketClient diffOrdersWebSocketClient;
    private final DiffOrderApplier diffOrderApplier;

    private OrderBookUpdater(
      DiffOrdersWebSocketClient diffOrdersWebSocketClient,
      OrderBookHolder orderBookHolder,
      DiffOrderApplier diffOrderApplier) {
        this.diffOrdersWebSocketClient = diffOrdersWebSocketClient;
        this.orderBookHolder = orderBookHolder;
        this.diffOrderApplier = diffOrderApplier;
    }

    public static OrderBookUpdater getInstance() throws URISyntaxException {
        return getInstance(
          DiffOrdersWebSocketClient.getInstance(new DiffOrdersEndpoint(DiffOrdersMessageHandler.getInstance())),
          OrderBookHolder.getInstance(new OrderBookRestApiClient()),
          DiffOrderApplier.getInstance()
        );
    }

    public static OrderBookUpdater getInstance(OrderBookRestApiClient orderBookApiClient, URI uri) throws URISyntaxException {
        return getInstance(
          DiffOrdersWebSocketClient.getInstance(uri, new DiffOrdersEndpoint(DiffOrdersMessageHandler.getInstance())),
          OrderBookHolder.getInstance(orderBookApiClient),
          DiffOrderApplier.getInstance());
    }

    static OrderBookUpdater getInstance(
      DiffOrdersWebSocketClient webSocketClient,
      OrderBookHolder orderBookHolder,
      DiffOrderApplier diffOrderApplier) {
        if (orderBookUpdater == null) {
            orderBookUpdater = new OrderBookUpdater(webSocketClient, orderBookHolder, diffOrderApplier);
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
            if (diffOrdersWebSocketClient.firstDiffOfferHasBeenReceived()) {
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
