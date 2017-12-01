package service.orders;

import service.model.orders.Ask;
import service.model.orders.Bid;
import service.orders.$tools.OrderBookUpdater;
import service.orders.$tools.holders.OrderBookHolder;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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

    public static OrdersService getInstance() throws URISyntaxException, IOException, DeploymentException {
        return getInstance(OrderBookUpdater.getInstance(), OrderBookHolder.getInstance());
    }

    public static OrdersService getInstance(OrderBookUpdater bookUpdater, OrderBookHolder orderBookHolder) throws IOException, DeploymentException {
        if (ordersService == null) {
            ordersService = new OrdersService(bookUpdater, orderBookHolder);
        }
        return ordersService;
    }

    public void start() throws IOException, DeploymentException {
        orderBookUpdater.start();
    }

    public List<Bid> getBestBids(int limit) {
        return orderBookHolder.getBestBids(limit);
    }

    public List<Ask> getBestAsks(int limit) {
        return orderBookHolder.getBestAsks(limit);
    }

    static void clearInstance() {
        ordersService = null;
    }
}
