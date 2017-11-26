package mockedIntegrationTests;

import mockedIntegrationTests.tools.MockedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import service.model.TradeResult;
import service.tools.BitsoApiRequester;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class BitsoApiRequester_MockedIntegrationTest {
    private BitsoApiRequester bitsoApiRequester;
    private static MockedServer mockedServer = new MockedServer();

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
        bitsoApiRequester = new BitsoApiRequester("http://localhost:9999/singleTradeFixture.json");
        // when
        TradeResult tradeResult = bitsoApiRequester.getTrades();
        // then
        assertNotNull(tradeResult);
    }
}
