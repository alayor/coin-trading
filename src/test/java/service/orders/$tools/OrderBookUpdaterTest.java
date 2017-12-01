package service.orders.$tools;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import service.orders.$tools.holders.OrderBookHolder;
import service.orders.$tools.rest_client.OrderBookRestApiClient;
import service.orders.$tools.web_socket.DiffOrdersWebSocketClient;

import java.net.URI;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static service.orders.$tools.OrderBookUpdater.getInstance;

@RunWith(MockitoJUnitRunner.class)
public class OrderBookUpdaterTest {

    @Mock
    private DiffOrdersWebSocketClient webSocketClient;
    @Mock
    private OrderBookHolder orderBookHolder;
    @Mock
    private DiffOrderApplier diffOrderApplier;
    @Mock
    private OrderBookRestApiClient apiClient;

    @Before
    public void setUp() throws Exception {
        OrderBookUpdater.clearInstance();
    }

    @Test
    public void shouldGetInstanceUsingApiClientAndURI() throws Exception {
        OrderBookUpdater orderBookUpdater = OrderBookUpdater.getInstance(apiClient, new URI(""));
        assertNotNull(orderBookUpdater);
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
          getInstance(webSocketClient, orderBookHolder, diffOrderApplier);
        given(webSocketClient.firstDiffOfferHasBeenReceived()).willReturn(true);
        // when
        orderBookUpdater.start();
        // then
        verify(webSocketClient).connect();
    }


    @Test
    public void shouldLoadBookOrderAfterDiffOrdersAreStartedAndFirstDiffOrderHasBeenReceived() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookHolder, diffOrderApplier);
        given(webSocketClient.firstDiffOfferHasBeenReceived()).willReturn(true);
        // when
        orderBookUpdater.start();
        // then
        InOrder inOrder = inOrder(webSocketClient, orderBookHolder);
        inOrder.verify(webSocketClient).connect();
        inOrder.verify(orderBookHolder).loadOrderBook();
    }

    @Test
    public void shouldStartDiffOrderApplierAfterOrderBookIsLoaded() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookHolder, diffOrderApplier);
        given(webSocketClient.firstDiffOfferHasBeenReceived()).willReturn(true);
        // when
        orderBookUpdater.start();
        // then
        InOrder inOrder = inOrder(orderBookHolder, diffOrderApplier);
        inOrder.verify(orderBookHolder).loadOrderBook();
        inOrder.verify(diffOrderApplier).start();
    }

    @Test
    public void shouldStopApplier() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookHolder, diffOrderApplier);
        // when
        orderBookUpdater.stop();
        // then
        verify(diffOrderApplier).stop();
    }

    @Test
    public void shouldThrowExceptionIfFirstDiffOfferHasnThrowsInterruptedException() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookHolder, diffOrderApplier);
        given(webSocketClient.firstDiffOfferHasBeenReceived()).willAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        });
        try {
            // when
            orderBookUpdater.start();
        } catch (Exception e) {
            // then
            assertTrue(e.getMessage().contains("Order Book couldn't get loaded. No Diff Offers were received."));
            return;
        }
        throw new AssertionError("No expected exception was thrown");
    }

    @Test
    public void shouldThrowExceptionIfFirstDiffOfferHasnNotBeenReceived() throws Exception {
        // given
        OrderBookUpdater orderBookUpdater =
          getInstance(webSocketClient, orderBookHolder, diffOrderApplier);
        given(webSocketClient.firstDiffOfferHasBeenReceived()).willReturn(false);
        orderBookUpdater.setTryCountToOne();
        try {
            // when
            orderBookUpdater.start();
        } catch (Exception e) {
            // then
            assertTrue(e.getMessage().contains("Order Book couldn't get loaded. No Diff Offers were received."));
            return;
        }
        throw new AssertionError("No expected exception was thrown");
    }
}
