package onlineIntegrationTests;

import org.junit.Before;
import org.junit.Test;
import service.model.TradeResult;
import service.trades.TradesApiClient;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TradesApiClient_OnlineITest
{
    private TradesApiClient tradesApiClient;

    @Before
    public void setUp() throws Exception {
        tradesApiClient = new TradesApiClient("https://api-dev.bitso.com/v3/trades?book=btc_mxn");
    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException {
        // when
        TradeResult tradeResult = tradesApiClient.getTrades(25);
        // then
        assertNotNull(tradeResult);
    }

    @Test
    public void shouldReturnResultsAccordingToLimit() throws Exception {
        //given
        tradesApiClient = new TradesApiClient("https://api-dev.bitso.com/v3/trades?book=btc_mxn");
        // when
        TradeResult tradeResult = tradesApiClient.getTrades(5);
        // then
        assertEquals(5, tradeResult.getTradeList().size());
    }
}
