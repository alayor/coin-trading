package service.orders.$tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import service.model.orders.OrderBookResult;
import service.orders.$tools.holders.OrderBookHolder;
import service.orders.$tools.rest_client.OrderBookRestApiClient;
import service.orders.$tools.web_socket.DiffOrdersMessageHandler;
import service.orders.$tools.web_socket.DiffOrdersWebSocketClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static service.orders.$tools.OrderBookUpdater.getInstance;

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
    @Mock
    private DiffOrdersMessageHandler diffOrderMessageHandler;

    @Before
    public void setUp() throws Exception {
        OrderBookUpdater.clearInstance();
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
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookApiClient, orderBookHolder, diffOrderMessageHandler);
        given(orderBookApiClient.getOrderBook()).willReturn(orderBookResult);
        given(diffOrderMessageHandler.firstDiffOfferHasBeenReceived()).willReturn(true);
        // when
        orderBookUpdater.start();
        // then
        verify(webSocketClient).connect();
    }

    @Test
    public void shouldGetOrderBookResult() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookApiClient, orderBookHolder, diffOrderMessageHandler);
        given(orderBookApiClient.getOrderBook()).willReturn(orderBookResult);
        given(diffOrderMessageHandler.firstDiffOfferHasBeenReceived()).willReturn(true);
        // when
        orderBookUpdater.start();
        // then
        InOrder inOrder = Mockito.inOrder(webSocketClient, orderBookApiClient);
        inOrder.verify(webSocketClient).connect();
        inOrder.verify(orderBookApiClient).getOrderBook();
    }

    @Test
    public void shouldLoadBookOrderInHolderIfFirstDiffOrderHasBeenReceived() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookApiClient, orderBookHolder, diffOrderMessageHandler);
        given(orderBookApiClient.getOrderBook()).willReturn(orderBookResult);
        given(diffOrderMessageHandler.firstDiffOfferHasBeenReceived()).willReturn(true);
        // when
        orderBookUpdater.start();
        // then
        verify(orderBookHolder).loadOrderBook(orderBookResult);
    }
}
