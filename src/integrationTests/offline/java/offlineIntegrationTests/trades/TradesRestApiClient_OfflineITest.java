package offlineIntegrationTests.trades;

import offlineIntegrationTests.tools.MockedHttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import service.model.trades.TradeResult;
import service.trades._tools.rest_client.TradesRestApiClient;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class TradesRestApiClient_OfflineITest
{
    private TradesRestApiClient tradesRestApiClient;
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
        tradesRestApiClient = new TradesRestApiClient("http://localhost:9999/trades/singleTradeFixture.json");
        // when
        TradeResult tradeResult = tradesRestApiClient.getTrades(25);
        // then
        assertTrue(tradeResult.isSuccess());
        assertEquals(1, tradeResult.getTradeList().size());
        assertEquals("btc_mxn", tradeResult.getTradeList().get(0).getBook());
        assertEquals("2017-11-25T23:32:30+0000", tradeResult.getTradeList().get(0).getCreatedAt());
        assertEquals("0.00863248", tradeResult.getTradeList().get(0).getAmount());
        assertEquals("buy", tradeResult.getTradeList().get(0).getMakerSide());
        assertEquals("163310.07", tradeResult.getTradeList().get(0).getPrice());
        assertEquals("2129343", tradeResult.getTradeList().get(0).getTid());
    }

    @Test
    public void shouldParseEmptyFailedTradeResult() throws URISyntaxException, IOException {
        // given
        tradesRestApiClient = new TradesRestApiClient("http://localhost:9999/trades/singleFailedTradeFixture.json");
        // when
        TradeResult tradeResult = tradesRestApiClient.getTrades(3);
        // then
        assertFalse(tradeResult.isSuccess());
        assertEquals(0, tradeResult.getTradeList().size());
    }

    @Test
    public void shouldGetTradesSince() throws Exception {
        // given
        tradesRestApiClient = new TradesRestApiClient("http://localhost:9999/trades/multipleTradesFixture.json");
        // when
        TradeResult tradeResult = tradesRestApiClient.getTradesSince("2128418");
        // then
        assertTrue(tradeResult.isSuccess());
        assertEquals(25, tradeResult.getTradeList().size());
    }
}
