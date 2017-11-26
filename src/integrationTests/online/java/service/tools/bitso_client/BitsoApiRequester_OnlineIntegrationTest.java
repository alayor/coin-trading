package service.tools.bitso_client;

import org.junit.Before;
import org.junit.Test;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BitsoApiRequester_OnlineIntegrationTest {
    private BitsoApiRequester bitsoApiRequester;

    @Before
    public void setUp() throws Exception {
        bitsoApiRequester = new BitsoApiRequester("https://api-dev.bitso.com/v3/trades?book=btc_mxn");
    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException {
        // when
        TradeResult tradeResult = bitsoApiRequester.getTrades(25);
        // then
        assertNotNull(tradeResult);
    }

    @Test
    public void shouldReturnResultsAccordingToLimit() throws Exception {
        //given
        bitsoApiRequester = new BitsoApiRequester("https://api-dev.bitso.com/v3/trades?book=btc_mxn");
        // when
        TradeResult tradeResult = bitsoApiRequester.getTrades(5);
        // then
        assertEquals(5, tradeResult.getTradeList().size());
    }
}
