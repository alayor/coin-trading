package service.orders._tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.orders.OrderBookResult;
import service.orders._tools.holders.OrderBookHolder;
import service.orders._tools.rest_client.OrderBookRestApiClient;
import service.orders._tools.web_socket.DiffOrdersWebSocketClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static service.orders._tools.OrderBookUpdater.getInstance;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookUpdaterTest {

    @Mock
    private DiffOrdersWebSocketClient webSocketClient;
    @Mock
    private OrderBookRestApiClient orderBookApiClient;
    @Mock
    private OrderBookHolder orderBookHolder;
    @Mock
    private OrderBookResult orderBookResult;

    @Before
    public void setUp() throws Exception {
        OrderBookUpdater.stop();
    }

    @Test
    public void shouldReturnOrderBookUpdaterInstance() throws Exception {
        // when
        OrderBookUpdater orderBookUpdater = getInstance();
        // then
        assertNotNull(orderBookUpdater);
    }

    @Test
    public void shouldBeSameOrdersServicePreviouslyCreated() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater1 = getInstance();
        // when
        OrderBookUpdater orderBookUpdater2 = getInstance();
        // then
        assertEquals(orderBookUpdater1, orderBookUpdater2);
    }

    @Test
    public void shouldConnectToWebSocketWhenStarting() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater = getInstance(webSocketClient, orderBookApiClient, orderBookHolder);
        // when
        orderBookUpdater.start();
        // then
        verify(webSocketClient).connect();
    }

    @Test
    public void shouldGetOrderBookResult() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookApiClient, orderBookHolder);
        // when
        orderBookUpdater.start();
        // then
        InOrder inOrder = Mockito.inOrder(webSocketClient, orderBookApiClient);
        inOrder.verify(webSocketClient).connect();
        inOrder.verify(orderBookApiClient).getOrderBook();
    }

    @Test
    public void shouldLoadBookOrderInHolder() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookApiClient, orderBookHolder);
        given(orderBookApiClient.getOrderBook()).willReturn(orderBookResult);
        // when
        orderBookUpdater.start();
        // then
        verify(orderBookHolder).loadOrderBook(orderBookResult);
    }
}