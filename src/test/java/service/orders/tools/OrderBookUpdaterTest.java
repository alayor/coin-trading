package service.orders.tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.orders.tools.web_socket.DiffOrdersWebSocketClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookUpdaterTest {

    @Mock
    private DiffOrdersWebSocketClient diffOrdersWebSocketClient;

    @Before
    public void setUp() throws Exception {
        OrderBookUpdater.stop();
    }

    @Test
    public void shouldReturnOrderBookUpdaterInstance() throws Exception {
        // when
        OrderBookUpdater orderBookUpdater = OrderBookUpdater.getInstance();
        // then
        assertNotNull(orderBookUpdater);
    }

    @Test
    public void shouldBeSameOrdersServicePreviouslyCreated() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater1 = OrderBookUpdater.getInstance();
        // when
        OrderBookUpdater orderBookUpdater2 = OrderBookUpdater.getInstance();
        // then
        assertEquals(orderBookUpdater1, orderBookUpdater2);
    }

    @Test
    public void shouldConnectToWebSocketWhenStarting() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater = OrderBookUpdater.getInstance(diffOrdersWebSocketClient);
        // when
        orderBookUpdater.start();
        // then
        verify(diffOrdersWebSocketClient).connect();
    }

    @Test
    public void shouldGetOrderBookResult() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          OrderBookUpdater.getInstance(diffOrdersWebSocketClient);
    }
}