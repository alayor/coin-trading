package service.orders.tools;

import service.orders.tools.web_socket.BitsoEndpoint;
import service.orders.tools.web_socket.BitsoWebSocketClient;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public class OrderBookUpdater {
    private static OrderBookUpdater orderBookUpdater;
    private BitsoWebSocketClient bitsoWebSocketClient;

    private OrderBookUpdater() {
    }

    private OrderBookUpdater(BitsoWebSocketClient bitsoWebSocketClient) {
      this.bitsoWebSocketClient = bitsoWebSocketClient;
    }

    public static OrderBookUpdater getInstance() throws URISyntaxException {
        return getInstance(BitsoWebSocketClient.getInstance(
          new BitsoEndpoint(
            new BitsoMessageHandler()
          )
        ));
    }

    public static OrderBookUpdater getInstance(BitsoWebSocketClient bitsoWebSocketClient) {
        if (orderBookUpdater == null) {
            orderBookUpdater = new OrderBookUpdater(bitsoWebSocketClient);
        }
        return orderBookUpdater;
    }

    public static void stop() {
        orderBookUpdater = null;
    }

    public void start() throws IOException, DeploymentException {
      bitsoWebSocketClient.connect();
    }
}
