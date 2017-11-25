package api.tools.bitso_client;

import api.model.TradeResult;
import api.tools.BitsoClient;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class BitsoClient_MockedIntegrationTest {
    private BitsoClient bitsoClient;

    @Before
    public void setUp() throws Exception {
        bitsoClient = new BitsoClient("https://api-dev.bitso.com/v3/");
    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException {
        // when
        TradeResult tradeResult = bitsoClient.getTrades();
        // then
        assertNotNull(tradeResult);
    }
}
