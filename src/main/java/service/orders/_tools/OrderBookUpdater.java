package service.orders._tools;

import service.orders._tools.rest_client.OrderBookRestApiClient;
import service.orders._tools.web_socket.DiffOrdersEndpoint;
import service.orders._tools.web_socket.DiffOrdersMessageHandler;
import service.orders._tools.web_socket.DiffOrdersWebSocketClient;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public class OrderBookUpdater {
    private static OrderBookUpdater orderBookUpdater;
    private final OrderBookRestApiClient orderBookApiClient;
    private DiffOrdersWebSocketClient diffOrdersWebSocketClient;

    private OrderBookUpdater(
      DiffOrdersWebSocketClient diffOrdersWebSocketClient,
      OrderBookRestApiClient orderBookApiClient) {
      this.diffOrdersWebSocketClient = diffOrdersWebSocketClient;
      this.orderBookApiClient = orderBookApiClient;
    }

    public static OrderBookUpdater getInstance() throws URISyntaxException {
        return getInstance(DiffOrdersWebSocketClient.getInstance(
          new DiffOrdersEndpoint(
            new DiffOrdersMessageHandler()
          )
        ), new OrderBookRestApiClient());
    }

    public static OrderBookUpdater getInstance(
      DiffOrdersWebSocketClient webSocketClient,
      OrderBookRestApiClient orderBookApiClient) {
        if (orderBookUpdater == null) {
            orderBookUpdater = new OrderBookUpdater(webSocketClient, orderBookApiClient);
        }
        return orderBookUpdater;
    }

    public static void stop() {
        orderBookUpdater = null;
    }

    public void start() throws IOException, DeploymentException {
      diffOrdersWebSocketClient.connect();
      orderBookApiClient.getOrderBook();
    }
}
