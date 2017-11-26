package mockedIntegrationTests;

import api.model.TradeResult;
import api.tools.BitsoClient;
import mockedIntegrationTests.tools.MockedServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class BitsoClient_MockedIntegrationTest {
    private BitsoClient bitsoClient;
    private MockedServer mockedServer;

    @Before
    public void setUp() throws Exception {
        mockedServer = new MockedServer();
        mockedServer.start();
        bitsoClient = new BitsoClient("http://localhost:9999");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldParseResultToTradeResult() throws URISyntaxException {
        // when
        TradeResult tradeResult = bitsoClient.getTrades();
        // then
        assertNotNull(tradeResult);
    }
}
