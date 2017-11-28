package onlineIntegrationTests;

import org.junit.Before;
import org.junit.Test;
import service.TradingService;
import service.TradingSimulator;
import service.tools.BitsoApiRequester;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TradingService_OnlineITest
{
    private TradingService tradingService;
    private BitsoApiRequester bitsoApiRequester;
    private TradingSimulator tradingSimulator;

    @Before
    public void setUp() throws Exception {
        bitsoApiRequester = new BitsoApiRequester();
        tradingSimulator = new TradingSimulator(3, 3);
    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException {
        // when
        tradingService = new TradingService(bitsoApiRequester, tradingSimulator);
        // then
        assertNotNull(tradingService);
    }

    @Test
    public void shouldHaveInitialTrades() throws URISyntaxException {
        // when
        tradingService = new TradingService(bitsoApiRequester, tradingSimulator);
        // then
        assertEquals(100, tradingService.getLastTrades(100).size());
    }
}
