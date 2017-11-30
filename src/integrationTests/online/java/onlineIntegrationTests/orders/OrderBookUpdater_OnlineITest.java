package onlineIntegrationTests.orders;

import org.junit.Before;
import org.junit.Test;
import service.model.diff_orders.DiffOrderResult;
import service.orders.$tools.OrderBookUpdater;
import service.orders.$tools.holders.CurrentDiffOrdersHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderBookUpdater_OnlineITest {

    @Before
    public void setUp() throws Exception {
        OrderBookUpdater.clearInstance();
    }

    @Test
    public void shouldQueueDiffOrders() throws Exception {
        // given
        CurrentDiffOrdersHolder diffOrders = CurrentDiffOrdersHolder.getInstance();
        OrderBookUpdater orderBookUpdater = OrderBookUpdater.getInstance();
        // when
        orderBookUpdater.start();
        // then
        List<DiffOrderResult> list = new ArrayList<>();
        list.add(diffOrders.consume());
        list.add(diffOrders.consume());
        assertEquals(2, list.size());
    }
}
