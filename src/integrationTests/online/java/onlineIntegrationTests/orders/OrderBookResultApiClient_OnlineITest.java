package onlineIntegrationTests.orders;

import org.junit.Before;
import org.junit.Test;
import service.model.orders.OrderBookResult;
import service.orders._tools.rest_client.OrderBookRestApiClient;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class OrderBookResultApiClient_OnlineITest
{
    private OrderBookRestApiClient OrderBookRestApiClient;

    @Before
    public void setUp() throws Exception {
        OrderBookRestApiClient = new OrderBookRestApiClient(
          "https://api-dev.bitso.com/v3/order_book?aggregate=false&book=btc_mxn");
    }

    @Test
    public void shouldParseResultToOrderBook() throws URISyntaxException {
        // when
        OrderBookResult orderBookResult = OrderBookRestApiClient.getOrderBook();
        // then
        assertNotNull(orderBookResult);
        assertTrue(orderBookResult.isSuccess());
        assertNotNull(orderBookResult.getOrderBook());
        assertTrue(orderBookResult.getOrderBook().getSequence().length() > 0);
        assertTrue(orderBookResult.getOrderBook().getUpdatedAt().length() > 0);
        assertTrue(orderBookResult.getOrderBook().getAsks().size() > 0);
        assertEquals("btc_mxn", orderBookResult.getOrderBook().getAsks().get(0).getBook());
        assertTrue(orderBookResult.getOrderBook().getAsks().get(0).getOrderId().length() > 0);
        assertTrue(orderBookResult.getOrderBook().getAsks().get(0).getPrice().length() > 0);
        assertTrue(orderBookResult.getOrderBook().getAsks().get(0).getAmount().length() > 0);
        assertTrue(orderBookResult.getOrderBook().getBids().size() > 0);
        assertEquals("btc_mxn", orderBookResult.getOrderBook().getAsks().get(0).getBook());
        assertTrue(orderBookResult.getOrderBook().getBids().get(0).getOrderId().length() > 0);
        assertTrue(orderBookResult.getOrderBook().getBids().get(0).getPrice().length() > 0);
        assertTrue(orderBookResult.getOrderBook().getBids().get(0).getAmount().length() > 0);
    }
}
