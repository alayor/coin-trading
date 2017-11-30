package service.orders;

import service.orders.$tools.OrderBookUpdater;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public class OrdersService {
    private static OrdersService ordersService;
    private OrderBookUpdater orderBookUpdater;

    private OrdersService() {
    }

    private OrdersService(OrderBookUpdater orderBookUpdater) throws IOException, DeploymentException {
        this.orderBookUpdater = orderBookUpdater;
        orderBookUpdater.start();
    }

    public static OrdersService getInstance() throws URISyntaxException, IOException, DeploymentException {
        return getInstance(OrderBookUpdater.getInstance());
    }

    public static OrdersService getInstance(OrderBookUpdater orderBookUpdater) throws IOException, DeploymentException {
        if (ordersService == null) {
            ordersService = new OrdersService(orderBookUpdater);
        }
        return ordersService;
    }
}
