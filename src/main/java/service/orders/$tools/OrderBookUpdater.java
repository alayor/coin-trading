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

/**
 * It first connects to the Web Socket to retrieve and queue the current diff orders.
 * Then retrieves the current Order Book from the Rest Api Service.
 * Then start the process to apply diff-orders to the Order Book every second.
 */
public class OrderBookUpdater {
    private static OrderBookUpdater orderBookUpdater;
    private final OrderBookHolder orderBookHolder;
    private final DiffOrdersWebSocketClient diffOrdersWebSocketClient;
    private final DiffOrderApplier diffOrderApplier;
    private int tryCount = 10;

    private OrderBookUpdater(
      DiffOrdersWebSocketClient diffOrdersWebSocketClient,
      OrderBookHolder orderBookHolder,
      DiffOrderApplier diffOrderApplier) {
        this.diffOrdersWebSocketClient = diffOrdersWebSocketClient;
        this.orderBookHolder = orderBookHolder;
        this.diffOrderApplier = diffOrderApplier;
    }

    /**
     * Creates an instance of this class using the default api and web socket clients that
     * connect to Bitso.
     * @return a new or the current instance of this class.
     * @throws URISyntaxException in case any uri is incorrect
     */
    public static OrderBookUpdater getInstance() throws URISyntaxException {
        return getInstance(
          DiffOrdersWebSocketClient.getInstance(new DiffOrdersEndpoint(DiffOrdersMessageHandler.getInstance())),
          OrderBookHolder.getInstance(new OrderBookRestApiClient()),
          DiffOrderApplier.getInstance()
        );
    }

    /**
     * Starts the process by first get diff-orders from the Web Socket and queue them internally.
     * Then retrieves the current order book when the first diff-order has been received.
     * Finally, it starts the process to get diff-orders from the internal queue and apply them
     * to the book order.
     * @throws IOException if connection fails with the Rest Api.
     * @throws DeploymentException if connection fails with the Web Socket.
     */
    public void start() throws IOException, DeploymentException {
        diffOrdersWebSocketClient.connect();
        loadOrderBook();
        diffOrderApplier.start();
    }

    /**
     * Stop the service that updates the book order.
     */
    public void stop() {
        diffOrderApplier.stop();
    }

    private void loadOrderBook() {

        while (tryCount-- > 0) {
            try {
                sleep(1000);
            if (diffOrdersWebSocketClient.firstDiffOfferHasBeenReceived()) {
                orderBookHolder.loadOrderBook();
                return;
            }
            } catch (InterruptedException e) {
                throw new RuntimeException("Order Book couldn't get loaded. No Diff Offers were received.");
            }
        }
        throw new RuntimeException("Order Book couldn't get loaded. No Diff Offers were received.");
    }

    void setTryCountToOne() {
        this.tryCount = 1;
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

    /**
     * It should only used by tests.
     */
    public static OrderBookUpdater getInstance(OrderBookRestApiClient orderBookApiClient, URI uri) throws URISyntaxException {
        return getInstance(
          DiffOrdersWebSocketClient.getInstance(uri, new DiffOrdersEndpoint(DiffOrdersMessageHandler.getInstance())),
          OrderBookHolder.getInstance(orderBookApiClient),
          DiffOrderApplier.getInstance());
    }

    /**
     * It should only used by tests.
     */
    public static void clearInstance() {
        orderBookUpdater = null;
    }
}
