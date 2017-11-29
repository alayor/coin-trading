package onlineIntegrationTests;

import org.junit.Before;
import org.junit.Test;
import service.model.orders.OrderBookResult;
import service.orders.tools.OrderBookRestApiClient;

import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderBookResultApiClient_OnlineITest
{
    private OrderBookRestApiClient orderBookRestApiClient;

    @Before
    public void setUp() throws Exception {
        orderBookRestApiClient = new OrderBookRestApiClient(
          "https://api-dev.bitso.com/v3/order_book?aggregate=false&book=btc_mxn");
    }

    @Test
    public void shouldParseResultToOrderBook() throws URISyntaxException {
        // when
        OrderBookResult orderBookResult = orderBookRestApiClient.getOrderBook();
        // then
        assertNotNull(orderBookResult);
        assertTrue(orderBookResult.isSuccess());
    }

//    @Test
//    public void shouldReturnResultsAccordingToLimit() throws Exception {
//        //given
//        orderBookRestApiClient = new TradesRestApiClient("https://api-dev.bitso.com/v3/trades?book=btc_mxn");
//        // when
//        TradeResult tradeResult = orderBookRestApiClient.getTrades(5);
//        // then
//        assertEquals(5, tradeResult.getTradeList().size());
//    }
}
