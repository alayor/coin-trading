package offlineIntegrationTests.orders;

import offlineIntegrationTests.tools.MockedHttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import service.model.orders.OrderBookResult;
import service.orders._tools.rest_client.OrderBookRestApiClient;

import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderBookRestApiClient_OfflineITest
{
    private OrderBookRestApiClient OrderBookRestApiClient;
    private static MockedHttpServer mockedServer = new MockedHttpServer();

    @BeforeClass
    public static void setUp() {
        mockedServer.start();
    }

    @AfterClass
    public static void tearDown() {
      mockedServer.stop();
    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException, IOException {
        // given
        OrderBookRestApiClient = new OrderBookRestApiClient("http://localhost:9999/orders/singleOrderBookFixture.json");
        // when
        OrderBookResult orderBookResult = OrderBookRestApiClient.getOrderBook();
        // then
        assertTrue(orderBookResult.isSuccess());
        assertEquals("2017-11-29T15:58:10+00:00", orderBookResult.getOrderBook().getUpdatedAt());
        assertEquals("43883574", orderBookResult.getOrderBook().getSequence());
        assertEquals(1, orderBookResult.getOrderBook().getBids().size());
        assertEquals("btc_mxn", orderBookResult.getOrderBook().getBids().get(0).getBook());
        assertEquals("500.00", orderBookResult.getOrderBook().getBids().get(0).getPrice());
        assertEquals("0.21160000", orderBookResult.getOrderBook().getBids().get(0).getAmount());
        assertEquals("DvYoSsVhR6EioyFC", orderBookResult.getOrderBook().getBids().get(0).getOrderId());
        assertEquals(1, orderBookResult.getOrderBook().getAsks().size());
        assertEquals("btc_mxn", orderBookResult.getOrderBook().getAsks().get(0).getBook());
        assertEquals("197300.00", orderBookResult.getOrderBook().getAsks().get(0).getPrice());
        assertEquals("0.01171780", orderBookResult.getOrderBook().getAsks().get(0).getAmount());
        assertEquals("po5JlwTy1kQWrH9N", orderBookResult.getOrderBook().getAsks().get(0).getOrderId());
    }

    @Test
    public void shouldParseEmptyFailedOrderBookResult() throws URISyntaxException, IOException {
        // given
        OrderBookRestApiClient = new OrderBookRestApiClient("http://localhost:9999/orders/singleFailedOrderBookFixture.json");
        // when
        OrderBookResult orderBookResult = OrderBookRestApiClient.getOrderBook();
        // then
        assertFalse(orderBookResult.isSuccess());
    }
}
