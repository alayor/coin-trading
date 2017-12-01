package service.orders;

import service.model.orders.Ask;
import service.model.orders.Bid;
import service.orders.$tools.OrderBookUpdater;
import service.orders.$tools.holders.OrderBookHolder;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * It interacts with the OrderBookUpdater to retrieve OrderBook from Bitso Rest Api
 * and apply modifications from Diff Orders Web Socket service.
 * It also retrieves the best bids and best asks.
 */
public class OrdersService {
    private static OrdersService ordersService;
    private final OrderBookHolder orderBookHolder;
    private OrderBookUpdater orderBookUpdater;

    private OrdersService(
      OrderBookUpdater orderBookUpdater,
      OrderBookHolder orderBookHolder) throws IOException, DeploymentException {
        this.orderBookUpdater = orderBookUpdater;
        this.orderBookHolder = orderBookHolder;
    }

    /**
     * Returns an instance of this class that will use the Order Book Updater and Holder to
     * retrieve best bids and asks.
     * @return an instance of this class that will be used to retrieve best bids and asks.
     * @throws URISyntaxException if any uri is wrong specified.
     * @throws IOException if connection with rest service fails.
     * @throws DeploymentException if connection with web socket fails.
     */
    public static OrdersService getInstance() throws URISyntaxException, IOException, DeploymentException {
        return getInstance(OrderBookUpdater.getInstance(), OrderBookHolder.getInstance());
    }

    /**
     * Starts the booking updater that will retrieve order books from Bitso(c) Rest Api and
     * apply modifications from Diff Orders Web Socket.
     * @throws IOException if communication fails with the Rest Api.
     * @throws DeploymentException if communication fails with the Web Socket.
     */
    public void start() throws IOException, DeploymentException {
        orderBookUpdater.start();
    }

    /**
     * Stop the service that updates the book order.
     */
    public void stop() {
        orderBookUpdater.stop();
    }

    /**
     * Return the best bids. The best bids are the current highest price buy orders.
     * It returns them desc sorted by price.
     * @param limit is the maximum number of orders to be returned.
     * @return the current buy orders with the highest price order by price desc.
     */
    public List<Bid> getBestBids(int limit) {
        return orderBookHolder.getBestBids(limit);
    }

    /**
     * Return the best asks. The best asks are the current lowest price ask orders.
     * It returns them asc sorted by price.
     * @param limit is the maximum number of orders to be returned.
     * @return the current ask orders with the lowest price order by price asc.
     */
    public List<Ask> getBestAsks(int limit) {
        return orderBookHolder.getBestAsks(limit);
    }

    static void clearInstance() {
        ordersService = null;
    }

    /**
     * It should be only used for testing.
     */
    public static OrdersService getInstance(OrderBookUpdater bookUpdater, OrderBookHolder orderBookHolder) throws IOException, DeploymentException {
        if (ordersService == null) {
            ordersService = new OrdersService(bookUpdater, orderBookHolder);
        }
        return ordersService;
    }
}
