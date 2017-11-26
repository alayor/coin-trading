package service.tools.bitso_client;

import org.junit.Before;
import org.junit.Test;
import service.model.TradeResult;
import service.tools.BitsoClient;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BitsoClient_OnlineIntegrationTest {
    private BitsoClient bitsoClient;

    @Before
    public void setUp() throws Exception {
        bitsoClient = new BitsoClient("https://api-dev.bitso.com/v3/trades?book=btc_mxn");
    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException {
        // when
        TradeResult tradeResult = bitsoClient.getTrades();
        // then
        assertNotNull(tradeResult);
    }

    @Test
    public void shouldReturnResultsAccordingToLimit() throws Exception {
        //given
        bitsoClient = new BitsoClient("https://api-dev.bitso.com/v3/trades?book=btc_mxn&limit=5");
        // when
        TradeResult tradeResult = bitsoClient.getTrades();
        // then
        assertEquals(5, tradeResult.getTradeList().size());
    }
}
