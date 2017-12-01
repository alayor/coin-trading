package service.orders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.orders.Ask;
import service.model.orders.Bid;
import service.orders.$tools.OrderBookUpdater;
import service.orders.$tools.holders.OrderBookHolder;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

    private OrdersService ordersService;
    @Mock
    private OrderBookUpdater orderBookUpdater;
    @Mock
    private OrderBookHolder orderBookHolder;
    @Mock
    private Bid bid;
    @Mock
    private Ask ask;

    @Before
    public void setUp() throws Exception {
        OrdersService.clearInstance();
        ordersService = OrdersService.getInstance(orderBookUpdater, orderBookHolder);
    }

    @Test
    public void shouldReturnOrdersServiceInstance() throws Exception {
        // when
        OrdersService ordersService = OrdersService.getInstance(orderBookUpdater, orderBookHolder);
        // then
        assertNotNull(ordersService);
    }

    @Test
    public void shouldBeSameOrdersServicePreviouslyCreated() throws Exception {
        // given
        OrdersService ordersService1 = OrdersService.getInstance(orderBookUpdater, orderBookHolder);
        // when
        OrdersService ordersService2 = OrdersService.getInstance(orderBookUpdater, orderBookHolder);
        // then
        assertEquals(ordersService1, ordersService2);
    }

    @Test
    public void shouldStartOrderBookUpdater() throws Exception {
        // when
        ordersService.start();
        // then
        verify(orderBookUpdater).start();
    }

    @Test
    public void shouldReturnBestBids() throws Exception {
        // given
        given(orderBookHolder.getBestBids(5)).willReturn(singletonList(bid));
        // when
        List<Bid> bids = ordersService.getBestBids(5);
        // then
        assertEquals(1, bids.size());
        assertEquals(bid, bids.get(0));
    }

    @Test
    public void shouldReturnBestAsks() throws Exception {
        // given
        given(orderBookHolder.getBestAsks(5)).willReturn(singletonList(ask));
        // when
        List<Ask> asks = ordersService.getBestAsks(5);
        // then
        assertEquals(1, asks.size());
        assertEquals(ask, asks.get(0));
    }
}
