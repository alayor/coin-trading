package service.orders.tools;

import service.orders.tools.web_socket.DiffOrdersEndpoint;
import service.orders.tools.web_socket.DiffOrdersWebSocketClient;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public class OrderBookUpdater {
    private static OrderBookUpdater orderBookUpdater;
    private DiffOrdersWebSocketClient diffOrdersWebSocketClient;

    private OrderBookUpdater(DiffOrdersWebSocketClient diffOrdersWebSocketClient) {
      this.diffOrdersWebSocketClient = diffOrdersWebSocketClient;
    }

    public static OrderBookUpdater getInstance() throws URISyntaxException {
        return getInstance(DiffOrdersWebSocketClient.getInstance(
          new DiffOrdersEndpoint(
            new DiffOrdersMessageHandler()
          )
        ));
    }

    public static OrderBookUpdater getInstance(DiffOrdersWebSocketClient diffOrdersWebSocketClient) {
        if (orderBookUpdater == null) {
            orderBookUpdater = new OrderBookUpdater(diffOrdersWebSocketClient);
        }
        return orderBookUpdater;
    }

    public static void stop() {
        orderBookUpdater = null;
    }

    public void start() throws IOException, DeploymentException {
      diffOrdersWebSocketClient.connect();
    }
}
